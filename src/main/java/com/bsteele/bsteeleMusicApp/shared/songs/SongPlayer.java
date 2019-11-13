package com.bsteele.bsteeleMusicApp.shared.songs;


import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class SongPlayer {
    public SongPlayer(@Nonnull SongBase songBase) {
        this.songBase = songBase;
        songBase.computeSongMoments();  //  compute song moments off of realtime constraints
    }

    /**
     * Move the update indicators to the given measureNumber.
     * Should only be used to reposition the moment number in time.
     * Normally use nextMeasureNumber().
     *
     * @param t the time to match the given moment number
     * @param m the measureNumber to move to
     */
    public final void setMomentNumber(double t, int m) {

        //logger.info("setMomentNumber() from " + momentNumber + " to " + m+" at "+t);

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
        //logger.info("setMomentNumber() t0: " + t0);
    }

    /**
     * Getter
     *
     * @return the current moment number
     */
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
        double tn = t - t0;
        if (tn < 0)
            return (int) Math.floor(tn / songBase.getDefaultTimePerBar());
        momentNumber = songBase.getSongMomentNumberAtSongTime(tn);

        if (skipFromNumber != null && momentNumber == skipFromNumber) {
            skipFromNumber = null;
            setMomentNumber(t, skipToNumber);
            skipToNumber = null;
        }

        return momentNumber;
    }

    public final int peekMomentNumberAt(double t) {
        double tn = t - t0;
        if (tn < 0)
            return (int) Math.floor(tn / songBase.getDefaultTimePerBar());
        int mn = songBase.getSongMomentNumberAtSongTime(tn);

        if (skipFromNumber != null && mn == skipFromNumber) {
            mn = skipToNumber;
        }
        return mn;
    }

    public final double getPeekMomentT0(int mn) {
        if (skipToNumber == null || mn != skipToNumber)
            return t0;

        int beatsToEnd = (skipFromNumber != Integer.MAX_VALUE
                ? songBase.getSongMoment(skipFromNumber).getBeatNumber()
                //  special case for last measure of song
                : songBase.getTotalBeats());
        return t0 + (beatsToEnd - songBase.getSongMoment(skipToNumber).getBeatNumber()) * songBase.getSecondsPerBeat();
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

    public final int jumpSectionToFirstSongMomentInSection(int to) {
        //  figure out where we're skipping from
        SongMoment songMoment = songBase.getLastSongMomentInSection(momentNumber);
        skipFromNumber = (songMoment == null ? 0 : songMoment.getMomentNumber() + 1);

        //  deal with repeat of last section
        if (skipFromNumber >= songBase.getSongMomentsSize())
            skipFromNumber = Integer.MAX_VALUE;

        songMoment = songBase.getFirstSongMomentInSection(to);
        if (songMoment != null)
            skipToNumber = songMoment.getMomentNumber();
        else
            skipToNumber = null;
        return skipToNumber;
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
        double ret = t % beatDuration;
        if (ret < 0)
            ret += beatDuration;

        return ret / beatDuration;
    }

    public final int getBeatCount() {
        SongMoment songMoment = songBase.getSongMoment(momentNumber);
        return songMoment != null ? songMoment.getMeasure().getBeatCount() : songBase.getBeatsPerBar();
    }

    public Integer getSkipToNumber() {
        return skipToNumber;
    }

    public final double getT0() {
        return t0;
    }

    private int momentNumber;
    private Integer skipFromNumber = null;
    private Integer skipToNumber = null;
    private double t0;
    private SongBase songBase;

    private static final Logger logger = Logger.getLogger(SongPlayer.class.getName());

}
