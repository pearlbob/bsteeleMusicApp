package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import org.junit.Test;

import java.text.ParseException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SongMomentLocationTest {

    @Test
    public void parse() {
        try {
            SongMomentLocation loc;
            {


                loc = SongMomentLocation.parse((String)null);

                assertNull(loc);
                loc = SongMomentLocation.parse("V2:");
                assertNull(loc);
                loc = SongMomentLocation.parse("V2:0");
                assertNull(loc);
                loc = SongMomentLocation.parse("V2:0:1");
                assertNull(loc);
                loc = SongMomentLocation.parse("V2:0:1");
                assertNull(loc);
                loc = SongMomentLocation.parse("V2:0:1#0");
                assertNull(loc);
                loc = SongMomentLocation.parse("V2:2:1#3");
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
                                MarkedString markedString = new MarkedString(
                                        section.toString() + (version > 0 ? version : "")
                                                + ":" + phraseIndex + ":" + index + "#" + instance);

                               logger.fine(markedString.toString());
                                loc = SongMomentLocation.parse(markedString);

                                assertNotNull(loc);
                                assertEquals(locExpected, loc);
                            }
                        }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static Logger logger = Logger.getLogger(SongMomentLocationTest.class.getName());
}