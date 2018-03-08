package com.bsteele.bsteeleMusicApp;

import com.bsteele.bsteeleMusicApp.client.BSteeleMusicAppTest;
import com.bsteele.bsteeleMusicApp.shared.SongUpdateTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BSteeleMusicAppSuite extends GWTTestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for BSteeleMusicApp");
    suite.addTestSuite(BSteeleMusicAppTest.class);
    suite.addTestSuite(SongUpdateTest.class);
    return suite;
  }
}
