package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.ChordSection;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordSectionLocation;
import com.bsteele.bsteeleMusicApp.shared.songs.Section;
import com.bsteele.bsteeleMusicApp.shared.songs.SectionVersion;
import org.junit.Test;

public class ChordSectionLocationTest {

    @Test
    public void parseLocation() {
        for (Section section : Section.values()) {
            for (int v = 1; v <= 4; v++) {
                SectionVersion sectionVersion = new SectionVersion(section, v);
                ChordSection chordSection = new ChordSection(sectionVersion);
                for (int index = 1; index <= 4; index++) {
                    ChordSectionLocation chordSectionLocation = new ChordSectionLocation(chordSection, index);
                    System.out.println(chordSectionLocation);
                }
            }
        }
    }
}