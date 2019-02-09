package com.bsteele.bsteeleMusicApp.shared.songs;

import org.junit.Test;

import static org.junit.Assert.*;

public class SectionTest {

    @Test
    public void parse() {
        for (Section section : Section.values())
            for (int i = 0; i < 10; i++) {
                String chords = " D C G G ";
                StringBuffer sb = new StringBuffer(section.toString() + (i > 0 ? i : "") + ":" + chords);
                System.out.println(sb.toString());
                SectionVersion sectionVersion = Section.parse(sb);
                assertEquals(chords, sb.toString());
                assertNotNull(sectionVersion);
                assertEquals(section, sectionVersion.getSection());
                assertEquals(i, sectionVersion.getVersion());

                chords = chords.trim();
                sb = new StringBuffer(section.toString() + (i > 0 ? i : "") + ":" + chords);
                sectionVersion = Section.parse(sb);
                assertEquals(chords, sb.toString());
                assertNotNull(sectionVersion);
                assertEquals(section, sectionVersion.getSection());
                assertEquals(i, sectionVersion.getVersion());
            }
    }

    @Test
    public void getDescription() {
    }
}