package com.bsteele.bsteeleMusicApp.shared.songs;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SongPlayer {
    SongPlayer(@Nonnull SongBase songBase) {
        this.songBase = songBase;
    }

    /**
     * Move the update indicators to the given measureNumber.
     * Should only be used to reposition the moment number.
     * Normally use nextMeasureNumber().
     *
     * @param m the measureNumber to move to
     */
    public void setMomentNumber(double t, int m) {

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
                //  walk forward to correct moment
                momentNumber = Math.max(0, Math.min(m, songBase.getSongMoments().size() - 1));
                t0 = t - songBase.getSongMoment(momentNumber).getBeatNumber() * songBase.getSecondsPerBeat();
            }
        } else {
            //  leave negative measures as they are
            momentNumber = m;
            t0 = t + Math.abs(momentNumber) * songBase.getDefaultTimePerBar();
        }
    }


    public int getMomentNumber() {
        return momentNumber;
    }

    /**
     * get moment number at time in seconds
     *
     * @param t time in seconds
     * @return the moment number
     */
    public int getMomentNumberAt(double t) {
        if (t < 0)
            return (int) Math.floor(t / songBase.getDefaultTimePerBar());
        return songBase.getSongMomentNumberAtTime(t - t0);
    }

    public double getT0() {
        return t0;
    }

    /**
     * Walk to the next measureNumber.
     *
     * @return true if the measureNumber exists
     */
//    public final boolean nextMomentNumber() {
//        logger.fine("nextMomentNumber() from " + momentNumber);
//        if (momentNumber < 0) {
//            momentNumber++;
//            if (momentNumber == 0)
//                setMomentNumber(0);
//            return true;
//        }
//
//        ArrayList<SongMoment> songMoments = songBase.getSongMoments();
//        if (songMoments == null)
//            return false;
//
//        if (momentNumber >= songMoments.size() - 1)
//            return false;
//
//        //  increment to the next moment
//        momentNumber++;
//
//        return (momentNumber < songMoments.size());
//    }

    private int momentNumber;
    private double t0;
    private SongBase songBase;

    private static final Logger logger = Logger.getLogger(SongPlayer.class.getName());

}
