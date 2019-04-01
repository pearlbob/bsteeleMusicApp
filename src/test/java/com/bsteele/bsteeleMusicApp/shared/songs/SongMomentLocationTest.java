package com.bsteele.bsteeleMusicApp.shared.songs;

import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SongMomentLocationTest {

    @Test
    public void parse() {
        try {
            SongMomentLocation loc;
            {


                loc = SongMomentLocation.parse(null);

                assertNull(loc);
                loc = SongMomentLocation.parse(new StringBuffer("V2:"));
                assertNull(loc);
                loc = SongMomentLocation.parse(new StringBuffer("V2:0"));
                assertNull(loc);
                loc = SongMomentLocation.parse(new StringBuffer("V2:0:1"));
                assertNull(loc);
                loc = SongMomentLocation.parse(new StringBuffer("V2:0:1"));
                assertNull(loc);
                loc = SongMomentLocation.parse(new StringBuffer("V2:0:1#0"));
                assertNull(loc);
                loc = SongMomentLocation.parse(new StringBuffer("V2:2:1#3"));
                SongMomentLocation locExpected = new SongMomentLocation(new ChordSectionLocation(
                        new SectionVersion(Section.verse, 2), 2, 1), 3);
                //System.out.println(locExpected);
                assertEquals(locExpected, loc);
            }

            for (Section section : Section.values())
                for (int version = 0; version < 10; version++)
                    for (int phraseIndex = 0; phraseIndex <= 3; phraseIndex++)
                        for (int index = 1; index < 8; index++) {
                            for (int instance = 1; instance < 4; instance++) {
                                SongMomentLocation locExpected = new SongMomentLocation(new ChordSectionLocation(
                                        new SectionVersion(section, version), phraseIndex, index), instance);
                                StringBuffer sb = new StringBuffer(section.toString() + (version > 0 ? version : "") + ":" + phraseIndex + ":" + index + "#" + instance);

                                //System.out.println(sb.toString());
                                loc = SongMomentLocation.parse(sb);

                                assertNotNull(loc);
                                assertEquals(locExpected, loc);
                            }
                        }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}