package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.util.ClientFileIO;
import com.bsteele.bsteeleMusicApp.shared.songs.Bar;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.bsteele.bsteeleMusicApp.shared.songs.Chord;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordComponent;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordDescriptor;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.Measure;
import com.bsteele.bsteeleMusicApp.shared.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.shared.songs.Note;
import com.bsteele.bsteeleMusicApp.shared.songs.NoteDuration;
import com.bsteele.bsteeleMusicApp.shared.songs.Pitch;
import com.bsteele.bsteeleMusicApp.shared.songs.ScaleChord;
import com.bsteele.bsteeleMusicApp.shared.songs.ScaleNote;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class BassFile {

    public void writeBassFile(Song aSong) {
        //  this.song = song;

        this.song = Song.createSong("A", "bob", "bsteele.com", Key.G,
                100, 4, 4, "v: A B C D", "v: bob, bob, bob berand");

        int beatsPerBar = song.getBeatsPerBar();

        StringBuilder sb = new StringBuilder();
        //  file format
        sb.append("{\n")
                .append(warning)
                .append(",\n")
                .append(version)
                .append(",\n\"keyN\": ")
                .append(Key.getDefault().getKeyValue())   //    default key
                .append(",\n\"beatsPerBar\": ")
                .append(song.getBeatsPerBar())
                .append(",\n\"notesPerBar\": ")
                .append(song.getUnitsPerMeasure())
                .append(",\n\"bpm\": ")
                .append(song.getBeatsPerMinute())
                .append(",\n\"isSwing8\":false,\"hiHatRhythm\":\"X\",\"swingType\":2")
                .append(",\n\"sheetNotes\":[\n");

        //  fake some notes, diatonic triads
        boolean first = true;
        Key key = song.getKey();
        for (int keyCount = 0; keyCount < MusicConstant.halfStepsPerOctave; keyCount++) {
//            for (int tonicNumber = 0; tonicNumber < MusicConstant.MajorDiatonic.values().length; tonicNumber++) {
//                sb.append("\n");
//
//                ScaleChord scaleChord = key.getMajorDiatonicByDegree(tonicNumber);
//
//                ArrayList<Chord> chords = new ArrayList<>();
//                chords.add(new Chord(scaleChord));
//                Measure measure = new Measure(beatsPerBar, chords);
//                Bar bar = new Bar();
//                bar.setMeasure(measure);
//                {
//                    ArrayList<Note> notes = new ArrayList<>();
//                    Pitch root = Pitch.findPitch(scaleChord.getScaleNote(), Pitch.E1);
//                    TreeSet<ChordComponent> chordComponents = scaleChord.getChordComponents();
//                    ChordComponent chordComponent = chordComponents.first();
//                    int count = 1;
//                    for (; count <= 4 && chordComponent != null; count++) {
//                        int halfSteps = chordComponent.getHalfSteps();
//                        notes.add(new Note(root.offsetByHalfSteps(halfSteps
//                                + (count == 4 && halfSteps <= g3ScaleNoteHalfStep - MusicConstant.halfStepsPerOctave
//                                ? MusicConstant.halfStepsPerOctave : 0)
//                        ),
//                                NoteDuration.whole));
//                        chordComponent = chordComponents.higher(chordComponent);
//                    }
//                    while (count <= 4) {
//                        notes.add(new Note(root.offsetByHalfSteps((count == 4 ? MusicConstant.halfStepsPerOctave : 0)),
//                                NoteDuration.whole));
//                        count++;
//                    }
//                    bar.setNotes(notes);
//                }
//
//                //  output
//                double beatCount = 0;
//                String lyrics = key.toString() + " " + MusicConstant.MajorDiatonic.values()[tonicNumber].name();
//                for (Note note : bar.getNotes()) {
//                    if (first)
//                        first = false;
//                    else
//                        sb.append(",\n");
//                    sb.append(noteToString(bar.getChordAtBeat(beatCount), note, lyrics));
//                    beatCount += note.getDuration();
//                    lyrics = "";
//                }
//            }

            //  step the key
            key = key.nextKeyByFifth();
        }
        sb.append("\n]\n}\n");
        ClientFileIO.saveDataAs("bassFileTestFile.bsst", sb.toString());
    }

    private String noteToString(Chord chord, Note note, String lyrics) {
        int bassString = bassString(note.getPitch(), 0);
        int bassFret = bassFret(note.getPitch(), bassString);
        ScaleChord scaleChord = chord.getScaleChord();
        ScaleNote rootNote = scaleChord.getScaleNote();
        int chordN = Util.mod(rootNote.getHalfStep() - e1ScaleNoteHalfStep,
                MusicConstant.halfStepsPerOctave);
        ChordComponent chordComponent = ChordComponent.getByHalfStep(note.getScaleNote().getHalfStep() - rootNote.getHalfStep());
        int scaleN = 0;
        if (chordComponent != null && scaleChord.contains(chordComponent))
            scaleN = chordComponent.getScaleNumber();
        ChordDescriptor chordDescriptor = scaleChord.getChordDescriptor();

        return "\t{\"isNote\":true,\"string\":"
                + bassString + ",\"fret\":" + bassFret + ",\"noteDuration\":"
                + durationToDurationIndex(note.getDuration(), song.getBeatsPerBar())
                + ", \"chordN\":"
                + chordN
                + ",\"chordModifier\":\""
                + chordDescriptor.getShortName()
                + "\",\"minorMajor\":\""
                + majorMinorMap(chordDescriptor)
                + "\","
                + "\"minorMajorSelectIndex\":0,\"scaleN\":"
                + (scaleN < 0 ? "" : scaleN)
                + ",\"lyrics\":\""
                + lyrics
                + "\","
                + "\"tied\":false}";
    }

    private String majorMinorMap(ChordDescriptor chordDescriptor) {
        switch (chordDescriptor) {
            case major:
            case major6:
            case major7:
            default:
                return "major";
            case minor:
            case minor7:
                return "minor";
            case dominant7:
            case dominant9:
            case dominant11:
            case dominant13:
                return "dominant";
        }
    }

    private int bassString(Pitch pitch, int position) {
        //  fixme: handle position not used in bass string selection
        for (int i = 1; i < bassStringPitches.length; i++) {
            Pitch bassPitch = bassStringPitches[i];
            if (pitch.getNumber() < bassPitch.getNumber())
                return i - 1;
        }
        return 3;
    }

    private int bassFret(Pitch pitch, int string) {
        Pitch bassPitch = bassStringPitches[string];
        return pitch.getNumber() - bassPitch.getNumber();
    }

    int durationToDurationIndex(double duration, double beats) {
        if (duration >= beats)
            return 0;
        if (duration >= beats / 2)
            return 1;
        if (duration >= beats / 4)
            return 2;
        if (duration >= beats / 8)
            return 3;
        return 4;
    }

    public static final String warning = "\"warning\":\"File generated by Robert Steele's Bass Study Tool.  Any modifications by hand are likely to be wrong.\"";
    public static final String version = "\"version\":\"0.0\"";
    public static final Pitch bassStringPitches[] = {
            Pitch.E1,
            Pitch.A1,
            Pitch.D2,
            Pitch.G2
    };
    public static final int e1ScaleNoteHalfStep = Pitch.E1.getScaleNote().getHalfStep();
    public static final int g3ScaleNoteHalfStep = Pitch.G3.getScaleNote().getHalfStep();
    private Song song;

          /*
    {"warning":"File generated by Robert Steele's Bass Study Tool.  Any modifications by hand are likely to be wrong.","version":"0.0","keyN":0,"beatsPerBar":3,"notesPerBar":4,"bpm":95,"isSwing8":false,"hiHatRhythm":"X","swingType":2,"sheetNotes":[
        {"isNote":true,"string":0,"fret":3,"noteDuration":1,"chordN":3,"chordModifier":"","minorMajor":"major","minorMajorSelectIndex":0,"scaleN":1,"lyrics":"Intro","tied":false},
        {"isNote":true,"string":0,"fret":3,"noteDuration":2,"chordN":3,"chordModifier":"","minorMajor":"major","minorMajorSelectIndex":0,"scaleN":1,"lyrics":"","tied":false},
        {"isNote":true,"string":1,"fret":5,"noteDuration":1,"chordN":10,"chordModifier":"","minorMajor":"major","minorMajorSelectIndex":0,"scaleN":1,"lyrics":"","tied":false},
        {"isNote":true,"string":1,"fret":5,"noteDuration":2,"chordN":10,"chordModifier":"","minorMajor":"major","minorMajorSelectIndex":0,"scaleN":1,"lyrics":"","tied":false}
        ]}

        {"warning":"File generated by Robert Steele's Bass Study Tool.  Any modifications by hand are likely to be wrong.","version":"0.0","keyN":0,"beatsPerBar":4,"notesPerBar":4,"bpm":100,"isSwing8":false,"hiHatRhythm":"","swingType":3,"sheetNotes":[
{"isNote":true,"string":0,"fret":3,"noteDuration":2,"chordN":3,"chordModifier":"maj7","minorMajor":"mdi","minorMajorSelectIndex":21,"scaleN":1,"lyrics":"","tied":false},
{"isNote":true,"string":0,"fret":5,"noteDuration":2,"chordN":3,"chordModifier":"m7","minorMajor":"mdii","minorMajorSelectIndex":22,"scaleN":1,"lyrics":"","tied":false},
{"isNote":true,"string":0,"fret":7,"noteDuration":2,"chordN":3,"chordModifier":"m7","minorMajor":"mdiii","minorMajorSelectIndex":23,"scaleN":1,"lyrics":"","tied":false},
{"isNote":true,"string":0,"fret":8,"noteDuration":2,"chordN":3,"chordModifier":"maj7","minorMajor":"mdiv","minorMajorSelectIndex":24,"scaleN":1,"lyrics":"","tied":false},
{"isNote":true,"string":0,"fret":10,"noteDuration":2,"chordN":3,"chordModifier":"7","minorMajor":"mdv","minorMajorSelectIndex":25,"scaleN":1,"lyrics":"","tied":false},
{"isNote":true,"string":0,"fret":12,"noteDuration":2,"chordN":3,"chordModifier":"m7","minorMajor":"mdvi","minorMajorSelectIndex":26,"scaleN":1,"lyrics":"","tied":false},
{"isNote":true,"string":0,"fret":2,"noteDuration":2,"chordN":3,"chordModifier":"m7b5","minorMajor":"mdvii","minorMajorSelectIndex":27,"scaleN":1,"lyrics":"","tied":false},
{"isNote":false,"noteDuration":2}]}
        */
}
