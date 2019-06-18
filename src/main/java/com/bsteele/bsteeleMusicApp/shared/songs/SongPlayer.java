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
            if (songBase == null || songBase.getSongMoments() == null || songBase.getSongMoments().isEmpty()) {
                momentNumber = 0;
            } else if (m >= songBase.getSongMoments().size()) {
                momentNumber = Integer.MAX_VALUE;
            } else {
                //  walk forward to the correct moment
                momentNumber = m;
                t0 = t - songBase.getSongMoment(momentNumber).getBeatNumber() * songBase.getSecondsPerBeat();
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
        return momentNumber== Integer.MAX_VALUE;
    }

    /**
     * get moment number at time in seconds
     *
     * @param t time in seconds
     * @return the moment number
     */
    public final int getMomentNumberAt(double t) {
        if (t < 0)
            return (int) Math.floor(t / songBase.getDefaultTimePerBar());
        momentNumber = songBase.getSongMomentNumberAtTime(t - t0);
        return momentNumber;
    }

    public final double getT0() {
        return t0;
    }

    private int momentNumber;
    private double t0;
    private SongBase songBase;

    private static final Logger logger = Logger.getLogger(SongPlayer.class.getName());
}
