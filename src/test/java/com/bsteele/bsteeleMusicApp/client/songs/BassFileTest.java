package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class BassFileTest extends TestCase {

    @Test
    public void testBassFile() {
        Song song = Song.createSong("testSong", "bob", "none",
                Key.getDefault(), 106, 4, 4,
                "bob", "v: A B C D", "v: bob bob bob");
        BassFile bassFile = new BassFile();
        assertEquals(0, bassFile.durationToDurationIndex(4.0, 4));
        assertEquals(1, bassFile.durationToDurationIndex(2.0, 4));
        assertEquals(2, bassFile.durationToDurationIndex(1.0, 4));
        assertEquals(3, bassFile.durationToDurationIndex(0.5, 4));
        assertEquals(4, bassFile.durationToDurationIndex(0.25, 4));

        assertEquals(0, bassFile.durationToDurationIndex(4.0, 3));
        assertEquals(1, bassFile.durationToDurationIndex(2.0, 3));
        assertEquals(2, bassFile.durationToDurationIndex(1.0, 3));
        assertEquals(3, bassFile.durationToDurationIndex(0.5, 3));
        assertEquals(4, bassFile.durationToDurationIndex(0.25, 3));


        assertEquals(0, bassFile.durationToDurationIndex(4.0, 2));
        assertEquals(0, bassFile.durationToDurationIndex(2.0, 2));
        assertEquals(1, bassFile.durationToDurationIndex(1.0, 2));
        assertEquals(2, bassFile.durationToDurationIndex(0.5, 2));
        assertEquals(3, bassFile.durationToDurationIndex(0.25, 2));

        song = Song.createSong("testSong", "bob", "none",
                Key.getDefault(), 106, 3, 4,
                "bob", "v: A B C D", "v: bob bob bob");
        bassFile = new BassFile();
    }
}