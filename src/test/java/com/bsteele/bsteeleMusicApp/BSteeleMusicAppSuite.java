package com.bsteele.bsteeleMusicApp;

import com.bsteele.bsteeleMusicApp.client.songs.SongUpdateTest;
import com.bsteele.bsteeleMusicApp.client.songs.*;
import com.bsteele.bsteeleMusicApp.client.util.UndoStackTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BSteeleMusicAppSuite extends GWTTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Tests for BSteeleMusicApp");
        suite.addTestSuite(ChordComponentTest.class);
        suite.addTestSuite(ChordDescriptorTest.class);
        suite.addTestSuite(ChordSectionTest.class);
        suite.addTestSuite(ChordTest.class);
        suite.addTestSuite(KeyTest.class);
        suite.addTestSuite(MeasureCommentTest.class);
        suite.addTestSuite(MeasureTest.class);
        suite.addTestSuite(PitchTest.class);
        suite.addTestSuite(ScaleChordTest.class);
        suite.addTestSuite(ScaleNoteTest.class);
        suite.addTestSuite(SongBaseTest.class);
        suite.addTestSuite(SongTest.class);
        suite.addTestSuite(SongUpdateTest.class);

      //  suite.addTestSuite(UndoStackTest.class);

        return suite;
    }
}
