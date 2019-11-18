package com.bsteele.bsteeleMusicApp.shared.util;

import java.util.logging.Logger;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class Md5Test {

    @Test
    public void Md5StringTest() {
        String data = "{\n" +
                "\"title\": \"25 or 6 to 4\",\n" +
                "\"artist\": \"Chicago\",\n" +
                "\"user\": \"Unknown\",\n" +
                "\"lastModifiedDate\": 1548296878134,\n" +
                "\"copyright\": \" ℗ 1975 Rhino Entertainment Company, a Warner Music Group Company. Marketed by Rhino Entertainment Company. © 1975 Rhino Entertainment Company, a Warner Music Group Company.\",\n" +
                "\"key\": \"C\",\n" +
                "\"defaultBpm\": 136,\n" +
                "\"timeSignature\": \"4/4\",\n" +
                "\"chords\": \n" +
                "    [\n" +
                "\t\"I:\",\n" +
                "\t\"Am Am/G Am/F♯ FE x4\",\n" +
                "\t\"I2:\",\n" +
                "\t\"Am Am/G Am/F♯ FE x2\",\n" +
                "\t\"V:\",\n" +
                "\t\"Am Am/G Am/F♯ FE x4\",\n" +
                "\t\"C:\",\n" +
                "\t\"F F C C\",\n" +
                "\t\"G G F F\",\n" +
                "\t\"O:\",\n" +
                "\t\"Dm C B B♭\",\n" +
                "\t\"A\"\n" +
                "    ],\n" +
                "\"lyrics\": \n" +
                "    [\n" +
                "\t\"i:\",\n" +
                "\t\"v:\",\n" +
                "\t\"Waiting for the break of day\",\n" +
                "\t\"Searching for something to say\",\n" +
                "\t\"Flashing lights against the sky\",\n" +
                "\t\"Giving up I close my eyes\",\n" +
                "\t\"ch:\",\n" +
                "\t\"Sitting cross-legged on the floor\",\n" +
                "\t\"25 or 6 to 4\",\n" +
                "\t\"i2: \",\n" +
                "\t\"v:\",\n" +
                "\t\"Staring blindly into space\",\n" +
                "\t\"Getting up to splash my face\",\n" +
                "\t\"Wanting just to stay awake\",\n" +
                "\t\"Wondering how much I can take\",\n" +
                "\t\"ch:\",\n" +
                "\t\"Should I try to do some more\",\n" +
                "\t\"25 or 6 to 4\",\n" +
                "\t\"i: (lead breaks)\",\n" +
                "\t\"v:\",\n" +
                "\t\"Feeling like I ought to sleep\",\n" +
                "\t\"Spinning room is sinking deep\",\n" +
                "\t\"Searching for something to say\",\n" +
                "\t\"Waiting for the break of day\",\n" +
                "\t\"ch:\",\n" +
                "\t\"25 or 6 to 4\",\n" +
                "\t\"25 or 6 to 4\",\n" +
                "\t\"i:\",\n" +
                "\t\"o:\"\n" +
                "    ]\n" +
                "}\n";

        String hash = Md5.getMd5Digest(data);
        logger.fine( "hash: "+hash);
        assertEquals("3876B4A9EF15C182630040AB91A6A0B4", hash);
    }

    private static Logger logger = Logger.getLogger(Md5Test.class.getName());
}
