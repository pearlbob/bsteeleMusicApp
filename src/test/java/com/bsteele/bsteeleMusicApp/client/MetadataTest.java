package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.util.JsonUtil;
import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;

import java.util.logging.Logger;

public class MetadataTest
        extends GWTTestCase {

    @Test
    public void testToJson() {

        assertEquals("\"abc\"", JsonUtil.encode("abc"));
        assertEquals("\" \"", JsonUtil.encode(" "));
        assertEquals("\" \"", JsonUtil.encode(" "));    //  tab maps to space
        assertEquals("\"     \"", JsonUtil.encode("     "));    //  2 tabs map to 4 spaces

        assertEquals("\"\"", JsonUtil.encode(null));
        assertEquals("\"\"", JsonUtil.encode(""));

    }

    @Test
    public void testCompareTo() {
    }


    @Override
    public String getModuleName() {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }

    private static Logger logger = Logger.getLogger(MetadataTest.class.getName());
}