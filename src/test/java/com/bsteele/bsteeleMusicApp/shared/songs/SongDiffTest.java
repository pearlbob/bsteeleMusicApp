package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.client.util.StringTripleToHtmlTable;
import com.bsteele.bsteeleMusicApp.shared.util.StringTriple;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Logger;

import static com.bsteele.bsteeleMusicApp.shared.songs.SongBaseTest.createSongBase;

public class SongDiffTest
        extends TestCase {

    @Test
    public void testDiff() {
        {
            SongBase a = createSongBase("A", "bobby", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i1: D D D D v: A B C D", "v: bob, bob, bob berand");
            SongBase b = createSongBase("A", "bobby", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "i1: D D D D D D D D v: A B C D", "v: bob, bob, bob berand");

            ArrayList<StringTriple> diffs = SongBase.diff(a, b);
            logger.info(diffs.toString());
            logger.info(StringTripleToHtmlTable.toHtmlTable(new StringTriple("fields", "a", "b"), diffs));
            diffs = SongBase.diff( b,a);
            logger.info(diffs.toString());
            logger.info(StringTripleToHtmlTable.toHtmlTable(new StringTriple("fields", "b", "a"), diffs));
//            assertEquals("[(artist:: \"bobby\", \"bob\")," +
//                            " (chords:: \"V: A B C D \", \"V: A B D D \")," +
//                            " (chords missing:: \"\", \"O: D \")," +
//                            " (lyrics V::: \"bob, bob, bob berand\", \"bob, bob, Barbara Ann\")]"
//                    , diffs.toString());
        }
        {
            SongBase a = createSongBase("A", "bobby", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");
            SongBase b = createSongBase("A", "bob", "bsteele.com", Key.getDefault(),
                    100, 4, 4, "v: A B D D O: D", "v: bob, bob, Barbara Ann");

            ArrayList<StringTriple> diffs = SongBase.diff(a, b);
            logger.fine(diffs.toString());
            logger.info(StringTripleToHtmlTable.toHtmlTable(new StringTriple("fields", "a", "b"), diffs));
            assertEquals("[(artist:: \"bobby\", \"bob\")," +
                            " (chords:: \"V: A B C D \", \"V: A B D D \")," +
                            " (chords missing:: \"\", \"O: D \")," +
                            " (lyrics V:: \"bob, bob, bob berand\", \"bob, bob, Barbara Ann\")]"
                    , diffs.toString());
        }

    }

    private static Logger logger = Logger.getLogger(SongDiffTest.class.getName());
}