package com.bsteele.bsteeleMusicApp.shared.songs;


import java.util.ArrayList;
import java.util.logging.Logger;

public class SongPlayer {
    SongPlayer(SongBase songBase) {
        this.songBase = songBase;
    }

    /**
     * Move the update indicators to the given measureNumber.
     * Should only be used to reposition the moment number.
     * Normally use nextMeasureNumber().
     *
     * @param m the measureNumber to move to
     */
    public void setMomentNumber(int m) {
        momentNumber = 0;

        logger.fine("setMomentNumber() from " + momentNumber + " to " + m);

        if (m == 0) {
            //    we're done from the above
            momentNumber = 0;
        } else if (m > 0) {
            if (songBase == null || songBase.getSongMoments() == null || songBase.getSongMoments().isEmpty())
                momentNumber = 0;
            else {
                //  walk forward to correct moment
                momentNumber = Math.max(0, Math.min(m, songBase.getSongMoments().size() - 1));
            }
        } else {
            //  leave negative measures as they are
            momentNumber = m;
        }
    }

    /**
     * Walk to the next measureNumber.
     *
     * @return true if the measureNumber exists
     */
    public final boolean nextMomentNumber() {
        logger.fine("nextMomentNumber() from " + momentNumber);
        if (momentNumber < 0) {
            momentNumber++;
            if (momentNumber == 0)
                setMomentNumber(0);
            return true;
        }

        ArrayList<SongMoment> songMoments = songBase.getSongMoments();
        if (songMoments == null)
            return false;

        if (momentNumber >= songMoments.size() - 1)
            return false;

        //  increment to the next moment
        momentNumber++;

        return (momentNumber < songMoments.size());
    }

    private int momentNumber;

    private SongBase songBase;

    private static final Logger logger = Logger.getLogger(SongPlayer.class.getName());
}
