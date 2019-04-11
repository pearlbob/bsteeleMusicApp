package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import org.junit.Test;

import java.text.ParseException;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class SectionVersionTest {

    @Test
    public void parse() {
        try {
            for (Section section : Section.values())
                for (int i = 0; i < 10; i++) {
                    SectionVersion sectionVersionExpected = new SectionVersion(section);
                    SectionVersion sectionVersion = null;

                    sectionVersion = SectionVersion.parse(section.toString() + ":");

                    assertEquals(sectionVersionExpected, sectionVersion);
                    sectionVersion = SectionVersion.parse(section.getAbbreviation() + ":  ");
                    assertEquals(sectionVersionExpected, sectionVersion);
                    sectionVersion = SectionVersion.parse(section.getFormalName() + ": A B C");
                    assertEquals(sectionVersionExpected, sectionVersion);
                    try {
                        sectionVersion = SectionVersion.parse(section.getFormalName() + "asdf");
                    } catch (ParseException e) {
                        //  expected
                    }
                }
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void parseInContext() {
        try {
            for (Section section : Section.values())
                for (int i = 0; i < 10; i++) {
                    String chords = " D C G G ";
                    MarkedString markedString = new MarkedString(section.toString() + (i > 0 ? i : "") + ":" + chords);
                    logger.fine(markedString.toString());
                    SectionVersion sectionVersion = null;

                    sectionVersion = SectionVersion.parse(markedString);

                    assertEquals(chords, markedString.toString());
                    assertNotNull(sectionVersion);
                    assertEquals(section, sectionVersion.getSection());
                    assertEquals(i, sectionVersion.getVersion());

                    chords = chords.trim();
                    markedString = new MarkedString(section.toString() + (i > 0 ? i : "") + ":" + chords);
                    sectionVersion = SectionVersion.parse(markedString);
                    assertEquals(chords, markedString.toString());
                    assertNotNull(sectionVersion);
                    assertEquals(section, sectionVersion.getSection());
                    assertEquals(i, sectionVersion.getVersion());
                }
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    private static Logger logger = Logger.getLogger(SectionVersionTest.class.getName());
}