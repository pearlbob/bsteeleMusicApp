/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;

/**
 * @author bob
 */
public class SongUpdateTest extends GWTTestCase {

    public SongUpdateTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getEventTime method, of class SongUpdate.
     */
    @Test
    public void testGetEventTime() {
        System.out.println("getEventTime");
        SongUpdate instance = new SongUpdate();
        long expResult = System.currentTimeMillis();
        instance.setEventTime(expResult);
        double result = instance.getEventTime();
        assertEquals(expResult, result, 0.0);
    }


    /**
     * Test of getSectionCount method, of class SongUpdate.
     */
    @Test
    public void testGetSectionCount() {
        System.out.println("getSectionCount");
        SongUpdate instance = new SongUpdate();
        int expResult = 0;
        int result = instance.getSectionCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChordSectionRow method, of class SongUpdate.
     */
    @Test
    public void testGetChordSectionRow() {
        System.out.println("getChordSectionRow");
        SongUpdate instance = new SongUpdate();
        int expResult = 0;
        int result = instance.getChordSectionRow();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRepeatCurrent method, of class SongUpdate.
     */
    @Test
    public void testGetChordSectionCurrentRepeat() {
        System.out.println("getRepeatCurrent");
        SongUpdate instance = new SongUpdate();
        int expResult = 0;
        int result = instance.getRepeatCurrent();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMeasure method, of class SongUpdate.
     */
    @Test
    public void testGetMeasure() {
        System.out.println("getMeasure");
        SongUpdate instance = new SongUpdate();
        int expResult = 0;
        int result = instance.getMeasure();
        assertEquals(expResult, result);
    }

    /**
     * Test of getBeat method, of class SongUpdate.
     */
    @Test
    public void testGetBeat() {
        System.out.println("getBeat");
        SongUpdate instance = new SongUpdate();
        int expResult = 0;
        int result = instance.getBeat();
        assertEquals(expResult, result);
    }

    /**
     * Test of getBeatsPerMeasure method, of class SongUpdate.
     */
    @Test
    public void testGetBeatsPerMeasure() {
        System.out.println("getBeatsPerMeasure");
        SongUpdate instance = new SongUpdate();
        int expResult = 0;
        int result = instance.getBeatsPerMeasure();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentBeatsPerMinute method, of class SongUpdate.
     */
    @Test
    public void testGetBeatsPerMinute() {
        System.out.println("getCurrentBeatsPerMinute");
        SongUpdate instance = new SongUpdate();
        int expResult = 100; // fixme: bad default
        int result = instance.getCurrentBeatsPerMinute();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEventTime method, of class SongUpdate.
     */
    @Test
    public void testSetEventTime() {
        System.out.println("setEventTime");
        long eventTime = 0;
        SongUpdate instance = new SongUpdate();
        instance.setEventTime(eventTime);
    }

  /*
  todo: test setSong(), getSong()
   */

    /**
     * Test of setSectionCount method, of class SongUpdate.
     */
    @Test
    public void testSetSectionCount() {
        System.out.println("setSectionCount");
        int sectionCount = 0;
        SongUpdate instance = new SongUpdate();
        instance.setSectionCount(sectionCount);
    }

    /**
     * Test of setChordSectionRow method, of class SongUpdate.
     */
    @Test
    public void testSetChordSectionRow() {
        System.out.println("setChordSectionRow");
        int chordSectionRow = 0;
        SongUpdate instance = new SongUpdate();
        instance.setChordSectionRow(chordSectionRow);
    }

    /**
     * Test of setRepeatCurrent method, of class SongUpdate.
     */
    @Test
    public void testSetChordSectionCurrentRepeat() {
        System.out.println("setRepeatCurrent");
        int chordSectionCurrentRepeat = 0;
        SongUpdate instance = new SongUpdate();
        instance.setRepeatCurrent(chordSectionCurrentRepeat);
    }

    /**
     * Test of setMeasure method, of class SongUpdate.
     */
    @Test
    public void testSetMeasure() {
        System.out.println("setMeasure");
        int measure = 0;
        SongUpdate instance = new SongUpdate();
        instance.setMeasure(measure);
    }

    /**
     * Test of setBeat method, of class SongUpdate.
     */
    @Test
    public void testSetBeat() {
        System.out.println("setBeat");
        int beat = 0;
        SongUpdate instance = new SongUpdate();
        instance.setBeat(beat);
    }

    /**
     * Test of setBeatsPerBar method, of class SongUpdate.
     */
    @Test
    public void testSetBeatsPerBar() {
        System.out.println("setBeatsPerBar");
        int beatsPerMeasure = 0;
        SongUpdate instance = new SongUpdate();
        instance.setBeatsPerBar(beatsPerMeasure);
    }

    /**
     * Test of setCurrentBeatsPerMinute method, of class SongUpdate.
     */
    @Test
    public void testSetBeatsPerMinute() {
        System.out.println("setCurrentBeatsPerMinute");
        int beatsPerMinute = 0;
        SongUpdate instance = new SongUpdate();
        instance.setCurrentBeatsPerMinute(beatsPerMinute);
    }

    /**
     * Test of fromJson method, of class SongUpdate.
     */
    @Test
    public void testFromJson() {
        System.out.println("fromJson");
        String jsonString = "";
        SongUpdate expResult = null;
        try {
            SongUpdate result = SongUpdate.fromJson(jsonString);
            assertEquals(expResult, result);
        } catch (ParseException pex){
            fail(pex.getMessage());
        }
    }

    /**
     * Test of fromJsonObject method, of class SongUpdate.
     */
    @Test
    public void testFromJsonObject() {
        System.out.println("fromJsonObject");
        JSONObject jo = null;
        SongUpdate expResult = null;
        SongUpdate result = null;
        try {
            result = SongUpdate.fromJsonObject(jo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(expResult, result);
    }

    /**
     * Test of toJson method, of class SongUpdate.
     */
    @Test
    public void testToJson() {
        System.out.println("toJson");
//    SongUpdate instance = new SongUpdate();
//    String expResult = "";
//    String result = instance.toJson();
//    assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class SongUpdate.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        SongUpdate instance = new SongUpdate();
        assertTrue(instance.hashCode() == instance.hashCode());
        SongUpdate instance2 = new SongUpdate();
        assertTrue(instance.hashCode() == instance2.hashCode());
        instance.setBeat(14);
    assertTrue(instance.hashCode() != instance2.hashCode());
    instance2.setBeat(14);
    assertTrue(instance.hashCode() == instance2.hashCode());
    }

    /**
     * Test of equals method, of class SongUpdate.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        SongUpdate instance = new SongUpdate();
        assertEquals(instance.equals(instance), true);
        assertEquals(instance.equals(null), false);
        assertEquals(instance.equals(this), false);
        SongUpdate instance2 = new SongUpdate();
        assertEquals(instance.equals(instance2), true);
        instance.setBeat(14);
        assertEquals(instance.equals(instance2), false);
        instance2.setBeat(14);
        assertEquals(instance.equals(instance2), true);
    }

    @Override
    public String getModuleName() {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }

}
