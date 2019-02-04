package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import static org.junit.Assert.*;

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