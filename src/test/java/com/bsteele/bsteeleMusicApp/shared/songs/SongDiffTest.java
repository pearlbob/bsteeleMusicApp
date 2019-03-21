package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.client.songs.Song;
import junit.framework.TestCase;
import org.junit.Test;

import static com.bsteele.bsteeleMusicApp.shared.songs.SongBaseTest.createSongBase;

public class SongDiffTest
        extends TestCase {

    @Test
    public void testDiff() {
        SongBase a = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
        SongBase b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                100, 4, 4, "v: A B D D", "v: bob, bob, Barbara Ann");

        SongBase.diff( a , b );
    }
}