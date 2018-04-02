package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class KeyTest {

    @Test
    public void getKeyByValue() {

        //  print the table of values
        for (int i = -6; i <= 6; i++) {
            Key key = Key.getKeyByValue(i);
            assertEquals(i, key.getKeyValue());
            System.out.print(
                    (i >= 0 ? " " : "")
                            + i + " " + key.name()
                            //+ " toString: "
                            + " (" + key.toString() + ")\t"
                    //+ " html: " + key.toHtml()
            );

            System.out.print("\tscale: ");
            for (int j = 0; j < 7; j++) {
                ScaleNote sn = key.getMajorScaleByNote(j);
                String s = sn.toString();
                System.out.print(s);
                if (s.toString().length() < 2)
                    System.out.print(" ");
                System.out.print(" ");
            }
            //System.out.println("\t");

            System.out.print("\tdiatonics: ");
            for (int j = 0; j < 7; j++) {
                ScaleChord sc = key.getDiatonicByDegree(j);
                String s = sc.toString();
                System.out.print(s);
                int len = s.length();
                while ( len < 4) {
                    len++;
                    System.out.print(" ");
                }
                System.out.print(" ");
            }
            //System.out.println();

            System.out.print("\tall notes: ");
            for (int j = 0; j < 12; j++) {
                ScaleNote sn = key.getScaleNoteByHalfStep(j);
                String s = sn.toString();
                System.out.print(s);
                if (s.toString().length() < 2)
                    System.out.print(" ");
                System.out.print(" ");
            }
            System.out.println();
        }

        assertEquals("G♭ A♭ B♭ C♭ D♭ E♭ F", majorScale(Key.Gb));
        assertEquals("D♭ E♭ F  G♭ A♭ B♭ C", majorScale(Key.Db));
        assertEquals("A♭ B♭ C  D♭ E♭ F  G", majorScale(Key.Ab));
        assertEquals("E♭ F  G  A♭ B♭ C  D", majorScale(Key.Eb));
        assertEquals("B♭ C  D  E♭ F  G  A", majorScale(Key.Bb));
        assertEquals("F  G  A  B♭ C  D  E", majorScale(Key.F));
        assertEquals("C  D  E  F  G  A  B", majorScale(Key.C));
        assertEquals("G  A  B  C  D  E  F♯", majorScale(Key.G));
        assertEquals("D  E  F♯ G  A  B  C♯", majorScale(Key.D));
        assertEquals("E  F♯ G♯ A  B  C♯ D♯", majorScale(Key.E));
        assertEquals("B  C♯ D♯ E  F♯ G♯ A♯", majorScale(Key.B));
        assertEquals("F♯ G♯ A♯ B  C♯ D♯ E♯", majorScale(Key.Fs));

        assertEquals("G♭   A♭m  B♭m  C♭   D♭7  E♭m  Fm7b5", diatonicByDegree(Key.Gb));
        assertEquals("D♭   E♭m  Fm   G♭   A♭7  B♭m  Cm7b5", diatonicByDegree(Key.Db));
        assertEquals("A♭   B♭m  Cm   D♭   E♭7  Fm   Gm7b5", diatonicByDegree(Key.Ab));
        assertEquals("E♭   Fm   Gm   A♭   B♭7  Cm   Dm7b5", diatonicByDegree(Key.Eb));
        assertEquals("B♭   Cm   Dm   E♭   F7   Gm   Am7b5", diatonicByDegree(Key.Bb));
        assertEquals("F    Gm   Am   B♭   C7   Dm   Em7b5", diatonicByDegree(Key.F));
        assertEquals("C    Dm   Em   F    G7   Am   Bm7b5", diatonicByDegree(Key.C));
        assertEquals("G    Am   Bm   C    D7   Em   F♯m7b5", diatonicByDegree(Key.G));
        assertEquals("D    Em   F♯m  G    A7   Bm   C♯m7b5", diatonicByDegree(Key.D));
        assertEquals("A    Bm   C♯m  D    E7   F♯m  G♯m7b5", diatonicByDegree(Key.A));
        assertEquals("E    F♯m  G♯m  A    B7   C♯m  D♯m7b5", diatonicByDegree(Key.E));
        assertEquals("B    C♯m  D♯m  E    F♯7  G♯m  A♯m7b5", diatonicByDegree(Key.B));
        assertEquals("F♯   G♯m  A♯m  B    C♯7  D♯m  E♯m7b5", diatonicByDegree(Key.Fs));


//        -6 Gb toString: G♭ html: G&#9837;
//        G♭ A♭ B♭ C♭ D♭ E♭ F
//        G♭ D♭ A♭ E♭ B♭ F  C
//        G♭ G  A♭ A  B♭ C♭ C  D♭ D  E♭ E  F
//        -5 Db toString: D♭ html: D&#9837;
//        D♭ E♭ F  G♭ A♭ B♭ C
//        D♭ A♭ E♭ B♭ F  C  G
//        D♭ D  E♭ E  F  G♭ G  A♭ A  B♭ B  C
//        -4 Ab toString: A♭ html: A&#9837;
//        A♭ B♭ C  D♭ E♭ F  G
//        A♭ E♭ B♭ F  C  G  D
//        A♭ A  B♭ B  C  D♭ D  E♭ E  F  G♭ G
//                -3 Eb toString: E♭ html: E&#9837;
//        E♭ F  G  A♭ B♭ C  D
//        E♭ B♭ F  C  G  D  A
//        E♭ E  F  G♭ G  A♭ A  B♭ B  C  D♭ D
//                -2 Bb toString: B♭ html: B&#9837;
//        B♭ C  D  E♭ F  G  A
//        B♭ F  C  G  D  A  E
//        B♭ B  C  D♭ D  E♭ E  F  G♭ G  A♭ A
//                -1 F toString: F html: F
//        F  G  A  B♭ C  D  E
//        F  C  G  D  A  E  B
//        F  G♭ G  A♭ A  B♭ B  C  D♭ D  E♭ E
//        0 C toString: C html: C
//        C  D  E  F  G  A  B
//        C  G  D  A  E  B  F♯
//        C  C♯ D  D♯ E  F  F♯ G  G♯ A  A♯ B
//        1 G toString: G html: G
//        G  A  B  C  D  E  F♯
//        G  D  A  E  B  F♯ C♯
//        G  G♯ A  A♯ B  C  C♯ D  D♯ E  F  F♯
//        2 D toString: D html: D
//        D  E  F♯ G  A  B  C♯
//        D  A  E  B  F♯ C♯ G♯
//        D  D♯ E  F  F♯ G  G♯ A  A♯ B  C  C♯
//        3 A toString: A html: A
//        A  B  C♯ D  E  F♯ G♯
//        A  E  B  F♯ C♯ G♯ D♯
//        A  A♯ B  C  C♯ D  D♯ E  F  F♯ G  G♯
//        4 E toString: E html: E
//        E  F♯ G♯ A  B  C♯ D♯
//        E  B  F♯ C♯ G♯ D♯ A♯
//        E  F  F♯ G  G♯ A  A♯ B  C  C♯ D  D♯
//        5 B toString: B html: B
//        B  C♯ D♯ E  F♯ G♯ A♯
//        B  F♯ C♯ G♯ D♯ A♯ F
//        B  C  C♯ D  D♯ E  F  F♯ G  G♯ A  A♯
//        6 Fs toString: F♯ html: F&#9839;
//        F♯ G♯ A♯ B  C♯ D♯ E♯
//        F♯ C♯ G♯ D♯ A♯ E♯ C
//        F♯ G  G♯ A  A♯ B  C  C♯ D  D♯ E  E♯
    }

    private String majorScale(Key key) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 7; j++) {
            ScaleNote sn = key.getMajorScaleByNote(j);
            String s = sn.toString();
            sb.append(s);
            if (s.toString().length() < 2)
                sb.append(" ");
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private String diatonicByDegree(Key key) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 7; j++) {
            ScaleChord sc = key.getDiatonicByDegree(j);
            String s = sc.toString();
            sb.append(s);
            int i = s.length();
            while ( i < 4) {
                i++;
                sb.append(" ");
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    @Test
    public void isDiatonic(){
               for ( Key key: Key.values()) {
                   for (int j = 0; j < MusicConstant.notesPerScale; j++) {
                       ScaleChord sc = key.getDiatonicByDegree(j);
                       assertTrue(key.isDiatonic(sc));
                      // fixme: add more tests
                   }
               }
    }

    @Test
    public void guessKey() {
        Key key;

        ArrayList<ScaleChord> scaleChords = new ArrayList<>();
        scaleChords.add(new ScaleChord(ScaleNote.Gb));
        scaleChords.add(new ScaleChord(ScaleNote.Cb));
        scaleChords.add(new ScaleChord(ScaleNote.Db,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Gb, key.getKeyScaleNote());

        scaleChords.clear();
        scaleChords.add( ScaleChord.parse("Gb") );
        scaleChords.add( ScaleChord.parse("Cb") );
        scaleChords.add( ScaleChord.parse("Db7") );
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Gb, key.getKeyScaleNote());

        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.C));
        scaleChords.add(new ScaleChord(ScaleNote.F));
        scaleChords.add(new ScaleChord(ScaleNote.G,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.C, key.getKeyScaleNote());

        //  1     2   3    4    5    6     7
        //  D♭   E♭m  Fm   G♭   A♭7  B♭m  Cm7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.Db));
        scaleChords.add(new ScaleChord(ScaleNote.Gb));
        scaleChords.add(new ScaleChord(ScaleNote.Ab,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Db, key.getKeyScaleNote());

        //  1     2   3    4    5    6     7
        //  D♭   E♭m  Fm   G♭   A♭7  B♭m  Cm7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.Cs));
        scaleChords.add(ScaleChord.parse("F#"));
        scaleChords.add(new ScaleChord(ScaleNote.Gs,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Db, key.getKeyScaleNote());
        scaleChords.add(new ScaleChord(ScaleNote.Bb,ChordDescriptor.minor));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Db, key.getKeyScaleNote());


        //  1     2   3    4    5    6     7
        //  A    Bm   C♯m  D    E7   F♯m  G♯m7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.A));
        scaleChords.add(new ScaleChord(ScaleNote.Fs,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.B,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.E,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.A, key.getKeyScaleNote());

        //  1     2   3    4    5    6     7
        //  E♭   Fm   Gm   A♭   B♭7  Cm   Dm7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.Ab));
        scaleChords.add(new ScaleChord(ScaleNote.F,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.G,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.Bb,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Eb, key.getKeyScaleNote());

        //  1     2   3    4    5    6     7
        //  C    Dm   Em   F    G7   Am   Bm7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.A,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.D,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.E,ChordDescriptor.minor));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.C, key.getKeyScaleNote());
        scaleChords.add(new ScaleChord(ScaleNote.G,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.C, key.getKeyScaleNote());


        //  1     2   3    4    5    6     7
        //  B    C♯m  D♯m  E    F♯7  G♯m  A♯m7b5
        //  E    F♯m  G♯m  A    B7   C♯m  D♯m7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.Gs,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.Ds,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.E));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.E, key.getKeyScaleNote());
        scaleChords.add(new ScaleChord(ScaleNote.Fs,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.B, key.getKeyScaleNote());


        //  1     2   3    4    5    6     7
        //  A♭   B♭m  Cm   D♭   E♭7  Fm   Gm7b5
        //  D♭   E♭m  Fm   G♭   A♭7  B♭m  Cm7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.Bb,ChordDescriptor.minor));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Ab, key.getKeyScaleNote());
        scaleChords.add(new ScaleChord(ScaleNote.Ab,ChordDescriptor.dominant7));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Db, key.getKeyScaleNote());

        //  1     2   3    4    5    6     7
        //  A♭   B♭m  Cm   D♭   E♭7  Fm   Gm7b5
        //  D♭   E♭m  Fm   G♭   A♭7  B♭m  Cm7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.Bb,ChordDescriptor.minor));
        scaleChords.add(new ScaleChord(ScaleNote.Db));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Db, key.getKeyScaleNote());
        scaleChords.add(new ScaleChord(ScaleNote.Ab));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.Ab, key.getKeyScaleNote());

        //  1     2   3    4    5    6     7
        //  G    Am   Bm   C    D7   Em   F♯m7b5
        //  D    Em   F♯m  G    A7   Bm   C♯m7b5
        scaleChords.clear();
        scaleChords.add(new ScaleChord(ScaleNote.D));
        scaleChords.add(new ScaleChord(ScaleNote.G));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.D, key.getKeyScaleNote());
        scaleChords.add(new ScaleChord(ScaleNote.C));
        key = Key.guessKey(scaleChords);
        assertEquals(ScaleNote.G, key.getKeyScaleNote());
    }

    @Test
    public void transpose() {
        for (int k = -6; k <= 6; k++) {
            Key key = Key.getKeyByValue(k);
//            System.out.println(key.toString() + ":");

            for (int i = 0; i < MusicConstant.halfStepsPerOctave; i++) {
                ScaleNote fsn = ScaleNote.getFlatByHalfStep(i);
                ScaleNote ssn = ScaleNote.getSharpByHalfStep(i);
                assertEquals(fsn.getHalfStep(), ssn.getHalfStep());
//                System.out.print(" " + i + ":");
//                if ( i < 10)
//                    System.out.print(" ");
                for (int j = 0; j <= MusicConstant.halfStepsPerOctave; j++) {
                    ScaleNote fTranSn = key.transpose(fsn, j);
                    ScaleNote sTranSn = key.transpose(ssn, j);
                    assertEquals(fTranSn.getHalfStep(), sTranSn.getHalfStep());
//                    System.out.print(" ");
//                    ScaleNote sn =  key.getScaleNoteByHalfStep(fTranSn.getHalfStep());
//                    String s = sn.toString();
//                    System.out.print(s);
//                    if ( s.length() < 2)
//                        System.out.print(" ");

                }
                //System.out.println();
            }

        }
    }

    @Test
    public void generateKeySelection() {
//        StringBuilder sb = new StringBuilder();
//        for (Key key : Key.values()) {
//            sb.append("<option value=\"").append(key.name() + "\"");
//            if (key.getKeyValue() == 0)
//                sb.append(" selected=\"selected\"");
//            sb.append(">");
//            sb.append(key.toString());
//            if (key.getKeyValue() != 0)
//                sb.append(" ").append(Math.abs(key.getKeyValue()))
//                        .append(key.getKeyValue() < 0 ? MusicConstant.flatChar : MusicConstant.sharpChar);
//            sb.append("</option>\n");
//        }
//        System.out.println(sb);


    }

    @Test
    public void keysByHalfStep() {
        Key key = Key.A;
        Key lastKey = key.previousKeyByHalfStep();
        TreeSet<Key> set = new TreeSet<>();
        for (int i = 0; i < MusicConstant.halfStepsPerOctave; i++) {
            Key nextKey = key.nextKeyByHalfStep();
            assertNotEquals(key, lastKey);
            assertNotEquals(key, nextKey);
            assertEquals(key, lastKey.nextKeyByHalfStep());
            assertEquals(key, nextKey.previousKeyByHalfStep());
            assertFalse(set.contains(key));
            set.add(key);

            //  increment
            lastKey = key;
            key = nextKey;
        }
        assertEquals(key, Key.A);
        assertEquals(MusicConstant.halfStepsPerOctave, set.size());
    }
}





























