package com.bsteele.bsteeleMusicApp.shared.songs;

import org.junit.Test;

import static org.junit.Assert.*;

public class SectionVersionTest {

    @Test
    public void parse() {
        for (Section section : Section.values())
            for (int i = 0; i < 10; i++) {
                SectionVersion sectionVersionExpected = new SectionVersion(section);
                SectionVersion sectionVersion = SectionVersion.parse(new StringBuffer(section.toString() + ":"));
                assertEquals(sectionVersionExpected, sectionVersion);
                sectionVersion = SectionVersion.parse(new StringBuffer(section.getAbbreviation() + ":  "));
                assertEquals(sectionVersionExpected, sectionVersion);
                sectionVersion = SectionVersion.parse(new StringBuffer(section.getFormalName() + ": A B C"));
                assertEquals(sectionVersionExpected, sectionVersion);
                sectionVersion = SectionVersion.parse(new StringBuffer(section.getFormalName() + "asdf"));
                assertNull( sectionVersion);
            }
    }

    @Test
    public void parseInContext() {
        for (Section section : Section.values())
            for (int i = 0; i < 10; i++) {
                String chords = " D C G G ";
                StringBuffer sb = new StringBuffer(section.toString() + (i > 0 ? i : "") + ":" + chords);
                System.out.println(sb.toString());
                SectionVersion sectionVersion = SectionVersion.parse(sb);
                assertEquals(chords, sb.toString());
                assertNotNull(sectionVersion);
                assertEquals(section, sectionVersion.getSection());
                assertEquals(i, sectionVersion.getVersion());

                chords = chords.trim();
                sb = new StringBuffer(section.toString() + (i > 0 ? i : "") + ":" + chords);
                sectionVersion = SectionVersion.parse(sb);
                assertEquals(chords, sb.toString());
                assertNotNull(sectionVersion);
                assertEquals(section, sectionVersion.getSection());
                assertEquals(i, sectionVersion.getVersion());
            }
    }
}