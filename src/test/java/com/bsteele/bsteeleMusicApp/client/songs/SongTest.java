package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongTest extends GWTTestCase {

    @Test
    public void testFromJson() {
        int songCount=0;
       String jsonString = AppResources.INSTANCE.allSongsAsJsonString().getText();
        JSONValue jv = JSONParser.parseStrict(jsonString);
        TreeSet<ChordDescriptor> chordDescriptors = new TreeSet<>();
        TreeSet<ChordTension> chordTensions = new TreeSet<>();
        if (jv != null) {
            JSONArray ja = jv.isArray();
            if (ja != null) {
                int jaLimit = ja.size();
                for (int i = 0; i < jaLimit; i++) {
                    songCount++;
                    Song song = Song.fromJsonObject(ja.get(i).isObject());
                    final RegExp scaleNoteExp = RegExp.compile("(" + ScaleChord.getRegExp() + ")", "g");
                    MatchResult mr;

                    while ((mr = scaleNoteExp.exec(song.getChordsAsString())) != null) {
                        if (mr.getGroupCount() >= 1) {
                            String s = mr.getGroup(1);
                            ScaleChord scaleChord = ScaleChord.parse(s);
                            if (scaleChord != null) {
                                chordDescriptors.add(scaleChord.getChordDescriptor());
                                chordTensions.add(scaleChord.getChordTension());
                            }
                        }
                    }
                }
            }
        }
        logger.info("chords: "+chordDescriptors.toString());
        logger.info("tensions: "+chordTensions.toString());
        logger.info("count: "+songCount);
    }

    @Override
    public String getModuleName() {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }

    private static Logger logger = Logger.getLogger("");
}