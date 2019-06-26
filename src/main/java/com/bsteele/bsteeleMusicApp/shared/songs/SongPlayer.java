package com.bsteele.bsteeleMusicApp.shared.songs;


import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class SongPlayer {
    public SongPlayer(@Nonnull SongBase songBase) {
        this.songBase = songBase;
    }

    /**
     * Move the update indicators to the given measureNumber.
     * Should only be used to reposition the moment number.
     * Normally use nextMeasureNumber().
     *
     * @param m the measureNumber to move to
     */
    public final void setMomentNumber(double t, int m) {

        logger.fine("setMomentNumber() from " + momentNumber + " to " + m);

        t0 = t; //  default only
        if (m == 0) {
            //    we're done
            momentNumber = 0;

        } else if (m > 0) {
            if (songBase == null || songBase.getSongMomentsSize() == 0) {
                momentNumber = 0;
            } else if (m >= songBase.getSongMomentsSize()) {
                momentNumber = Integer.MAX_VALUE;
            } else {
                //  walk forward to the correct moment
                SongMoment songMoment = songBase.getSongMoment(m);
                if (songMoment != null) {
                    momentNumber = m;
                    t0 = t - songMoment.getBeatNumber() * songBase.getSecondsPerBeat();
                } else {
                    momentNumber = Integer.MAX_VALUE;
                    t0 = 0;
                }
            }
        } else {
            //  leave negative measures as they are
            momentNumber = m;
            t0 = t + Math.abs(momentNumber) * songBase.getDefaultTimePerBar();
        }
    }


    public final int getMomentNumber() {
        return momentNumber;
    }

    public boolean isDone() {
        return momentNumber == Integer.MAX_VALUE;
    }

    /**
     * get moment number at time in seconds
     *
     * @param t time in seconds
     * @return the moment number
     */
    public final int getMomentNumberAt(double t) {
        t -= t0;
        if (t < 0)
            return (int) Math.floor(t / songBase.getDefaultTimePerBar());
        momentNumber = songBase.getSongMomentNumberAtTime(t);
        return momentNumber;
    }

    /**
     * Assumes moment number has already been determined.
     *
     * @param t time in seconds
     * @return the beat number within the current moment
     */
    public final int getBeat(double t) {
        if (momentNumber >= songBase.getSongMomentsSize())
            return 0;

        int beatsPerBar = songBase.getBeatsPerBar();
        if (beatsPerBar == 0)
            return 0;
        final double beatDuration = songBase.getDefaultTimePerBar() / beatsPerBar;

        t -= t0;
        int ret = (int) Math.floor(t / beatDuration);

        SongMoment songMoment = songBase.getSongMoment(momentNumber);
        if (songMoment != null) {
            ret -= songMoment.getBeatNumber();
            ret %= songMoment.getMeasure().getBeatCount();
        } else
            ret %= songBase.getBeatsPerBar();

        if (ret < 0)
            ret += songBase.getBeatsPerBar();
        return ret;
    }

    /**
     * Assumes moment number has already been determined.
     *
     * @param t time in seconds
     * @return the beat number within the current moment
     */
    public final double getBeatFraction(double t) {
        if (momentNumber >= songBase.getSongMomentsSize())
            return 0;

        int beatsPerBar = songBase.getBeatsPerBar();
        if (beatsPerBar == 0)
            return 0;
        final double beatDuration = songBase.getDefaultTimePerBar() / beatsPerBar;

        t -= t0;
        double ret = t / beatDuration;

        SongMoment songMoment = songBase.getSongMoment(momentNumber);
        if (songMoment != null) {
            ret -= songMoment.getBeatNumber();
            ret %= songMoment.getMeasure().getBeatCount();
        } else
            ret %= songBase.getBeatsPerBar();

        if (ret < 0)
            ret += songBase.getBeatsPerBar();
        return ret;
    }

    public final double getT0() {
        return t0;
    }

    private int momentNumber;
    private double t0;
    private SongBase songBase;

    private static final Logger logger = Logger.getLogger(SongPlayer.class.getName());
}
