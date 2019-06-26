package com.bsteele.bsteeleMusicApp.shared.songs;

import org.junit.Test;

import java.util.logging.Logger;

import static com.bsteele.bsteeleMusicApp.shared.songs.SongBaseTest.createSongBase;
import static org.junit.Assert.*;

public class SongPlayerTest {

    @Test
    public void testSetMomentNumber() {
        int beatsPerBar = 4;
        double fractions[] = new double[]{
                0.25, 0.66, 0.9
        };

        final int clocksPerBeat = 10;

        SongBase a;

        for (double fraction : fractions)
            for (int bpm = 60; bpm <= 120; bpm++) {
                int expectedBeat = 0;
                logger.fine("fraction: " + fraction + ", bpm: " + bpm);
                a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                        bpm, beatsPerBar, 4,
                        "i: A A# B C verse: A B C D prechorus: D E F F# chorus: G D C G x3 o: D C G G",
                        "i:\nv: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro");

                SongPlayer songPlayer = new SongPlayer(a);

                int lastMomentNumber = -2;
                double secondsPerBeat = 60.0 / bpm;
                double t = 200;
                songPlayer.setMomentNumber(t, lastMomentNumber);

                //
                int shortLimit = (int) (
                        (Math.abs(lastMomentNumber * beatsPerBar) +
                                a.getTotalBeats() * 0.5
                        )
                                * clocksPerBeat
                );
                int limit = shortLimit + (int) (
                        //  extra stuff to get past the end
                        (
                                a.getTotalBeats() * (1.0 - fraction)
                                        + 2 * beatsPerBar + 1
                        )
                                * clocksPerBeat
                );
                logger.fine("t: " + t + " = " + (t / (secondsPerBeat * beatsPerBar)) + " bars" + " = " + (t / secondsPerBeat) + " beats");
                double dt = 60.0 / (clocksPerBeat * bpm);
                logger.finest("dt: " + dt + " s");
                logger.fine("limit: " + limit + " = " + (limit * dt) + " s"
                        + " = " + (limit * dt / (secondsPerBeat * beatsPerBar)) + " bars"
                        + " = " + (limit * dt / secondsPerBeat) + " beats");
                logger.fine("TotalBeats: " + a.getTotalBeats());

                //  play song for a while
                int i = 0;
                int lastBeat = -1;
                for (; i < shortLimit; i++) {
                    t += dt;
                    int momentNumber = songPlayer.getMomentNumberAt(t);
                    if (lastMomentNumber == Integer.MAX_VALUE)
                        fail();
                    else if (momentNumber >= 0) {
                        if (momentNumber != lastMomentNumber) {
                            logger.fine("t: " + t + ", m: " + momentNumber + " " + a.getSongMoment(momentNumber));
                            if (momentNumber != Integer.MAX_VALUE)
                                //  should be one more than last moment or at end
                                assertEquals(lastMomentNumber + 1, momentNumber);
                        }
                    }
                    lastMomentNumber = momentNumber;
                    SongMoment songMoment = a.getSongMoment(momentNumber);
                    logger.finest("t: " + t + ", m: " + momentNumber + " " + songMoment);

                    int beat = songPlayer.getBeat(t);
                    if (beat != lastBeat) {
                        assertEquals(expectedBeat, beat);
                        expectedBeat = (expectedBeat + 1) % beatsPerBar;
                        logger.fine("t: " + t + ", m: " + (songMoment != null ? songMoment.getMeasure() : null) + ", beat: " + beat);
                        lastBeat = beat;
                    }
                }
                logger.fine("short done");

                //  reset the current moment
                int newMomentNumber = (int) (a.getSongMomentsSize() * fraction);
                lastMomentNumber = newMomentNumber - 1;
                songPlayer.setMomentNumber(t, newMomentNumber);
                for (; i < limit; i++) {
                    t += dt;
                    int momentNumber = songPlayer.getMomentNumberAt(t);
                    if (lastMomentNumber == Integer.MAX_VALUE)
                        //  once at end, stay at end
                        assertEquals(Integer.MAX_VALUE, momentNumber);
                    else if (momentNumber >= 0) {
                        if (momentNumber != lastMomentNumber) {
                            logger.fine("t: " + t + ", m: " + momentNumber + " " + a.getSongMoment(momentNumber));
                            if (momentNumber != Integer.MAX_VALUE)
                                //  should be one more than last moment or at end
                                assertEquals(lastMomentNumber + 1, momentNumber);
                        }
                        lastMomentNumber = momentNumber;
                    }

                    logger.finest("t: " + t + ", m: " + momentNumber + " " + a.getSongMoment(momentNumber));

                    SongMoment songMoment = a.getSongMoment(momentNumber);
                    logger.finest("t: " + t + ", m: " + momentNumber + " " + songMoment);
                    int beat = songPlayer.getBeat(t);
                    if (beat != lastBeat) {
                        assertEquals(expectedBeat, beat);
                        assertEquals(expectedBeat, songPlayer.getBeatFraction(t), dt + 1.0 / clocksPerBeat + 0.0001);
                        expectedBeat = (expectedBeat + 1) % beatsPerBar;
                        logger.fine("t: " + t + ", m: " + (songMoment != null ? songMoment.getMeasure() : null) + ", beat: " + beat);
                        lastBeat = beat;
                    }
                    logger.finer("t: " + t + ", m: " + (songMoment != null ? songMoment.getMeasure() : null) + ", beat: " + beat + ", beatFraction: " + songPlayer.getBeatFraction(t));
                }

                logger.fine("t: " + t);
                logger.fine("done");
            }
    }

    @Test
    public void testNextMomentNumber() {
        int beatsPerBar = 4;

        final int clocksPerBeat = 10;
        SongBase a;

        for (int bpm = 60; bpm <= 120; bpm++) {
            logger.fine("bpm: " + bpm);
            a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    bpm, beatsPerBar, 4,
                    "i: A A# B C verse: A B C D prechorus: D E F F# chorus: G D C G x3 o: D C G D",
                    "i:\n"
                            + "v: bob, bob, bob berand\npc: nope\nc: sing chorus here \no: last line of outro"
            );

            SongPlayer songPlayer = new SongPlayer(a);

            int lastMomentNumber = -2;
            double secondsPerBeat = 60.0 / bpm;
            double t = 100; // lastMomentNumber * beatsPerBar * secondsPerBeat;
            songPlayer.setMomentNumber(t, lastMomentNumber);

            int limit = (int) (
                    (Math.abs(lastMomentNumber * beatsPerBar) +
                            a.getTotalBeats()
                            + 2 * beatsPerBar + 1
                    )
                            * clocksPerBeat
            );
            logger.fine("t: " + t + " = " + (t / (secondsPerBeat * beatsPerBar)) + " bars" + " = " + (t / secondsPerBeat) + " beats");
            double dt = 60.0 / (clocksPerBeat * bpm);
            logger.fine("dt: " + dt + " s");
            logger.fine("limit: " + limit + " = " + (limit * dt) + " s"
                    + " = " + (limit * dt / (secondsPerBeat * beatsPerBar)) + " bars"
                    + " = " + (limit * dt / secondsPerBeat) + " beats");
            logger.fine("TotalBeats: " + a.getTotalBeats());
            for (int i = 0; i < limit; i++) {
                t += dt;
                int momentNumber = songPlayer.getMomentNumberAt(t);
                if (lastMomentNumber == Integer.MAX_VALUE)
                    //  once at end, stay at end
                    assertEquals(Integer.MAX_VALUE, momentNumber);
                else if (momentNumber >= 0) {
                    if (momentNumber != lastMomentNumber) {
                        logger.fine("t: " + songPlayer.getT0() + "  " + t + ", m: " + momentNumber + " " + a.getSongMoment(momentNumber));
                        if (momentNumber != Integer.MAX_VALUE)
                            //  should be one more than last moment or at end
                            assertEquals(lastMomentNumber + 1, momentNumber);
                    }
                }
                lastMomentNumber = momentNumber;
                logger.finest("t: " + songPlayer.getT0() + "  " + t + ", m: " + momentNumber + " " + a.getSongMoment(momentNumber));
            }
            logger.fine("t: " + t);
        }
    }

    private static Logger logger = Logger.getLogger(SongPlayerTest.class.getName());
}