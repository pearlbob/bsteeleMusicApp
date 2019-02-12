package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.ChordSection;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordSectionLocation;
import com.bsteele.bsteeleMusicApp.shared.songs.Section;
import com.bsteele.bsteeleMusicApp.shared.songs.SectionVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChordSectionLocationTest {

    @Test
    public void parse() {
        for (Section section : Section.values()) {
            for (int v = 1; v <= 4; v++) {
                SectionVersion sectionVersion = new SectionVersion(section, v);
                ChordSection chordSection = new ChordSection(sectionVersion);
                for (int index = 1; index <= 40; index++) {
                    ChordSectionLocation chordSectionLocationExpected = new ChordSectionLocation(chordSection, index);
                    StringBuffer sb = new StringBuffer(sectionVersion.getId() + ":" + index);
                    ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse(sb);
                    //System.out.println(chordSectionLocationExpected);
                    assertEquals(chordSectionLocationExpected,chordSectionLocation);
                }
            }
        }
    }
}