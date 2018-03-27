package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class KeyTest {

    @Test
    public void getKeyByValue() {
//        for (int i = -6; i <= 6; i++) {
//            Key key = Key.getKeyByValue(i);
//            assertEquals(i, key.getKeyValue());
//            System.out.println(i + " " + key.name()
//                    + " toString: " + key.toString()
//                    + " html: " + key.toHtml()
//            );
//            System.out.print("\t");
//            for (int j = 0; j < 7; j++) {
//                ScaleNote sn = key.getMajorScaleByNote(j);
//                String s = sn.toString();
//                System.out.print(s);
//                if (s.toString().length() < 2)
//                    System.out.print(" ");
//                System.out.print(" ");
//            }
//            System.out.println();
//
//            System.out.print("\t");
//            for (int j = 0; j < 7; j++) {
//                ScaleNote sn = key.getDiatonicByDegree(j);
//                String s = sn.toString();
//                System.out.print(s);
//                if (s.toString().length() < 2)
//                    System.out.print(" ");
//                System.out.print(" ");
//            }
//            System.out.println();
//
//            System.out.print("\t");
//            for (int j = 0; j < 12; j++) {
//                ScaleNote sn = key.getScaleNoteByHalfStep(j);
//                String s = sn.toString();
//                System.out.print(s);
//                if (s.toString().length() < 2)
//                    System.out.print(" ");
//                System.out.print(" ");
//            }
//            System.out.println();
//        }

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

        assertEquals("G♭ D♭ A♭ E♭ B♭ F  C", diatonicByDegree(Key.Gb));
        assertEquals("D♭ A♭ E♭ B♭ F  C  G", diatonicByDegree(Key.Db));
        assertEquals("A♭ E♭ B♭ F  C  G  D", diatonicByDegree(Key.Ab));
        assertEquals("E♭ B♭ F  C  G  D  A", diatonicByDegree(Key.Eb));
        assertEquals("B♭ F  C  G  D  A  E", diatonicByDegree(Key.Bb));
        assertEquals("F  C  G  D  A  E  B", diatonicByDegree(Key.F));
        assertEquals("C  G  D  A  E  B  F♯", diatonicByDegree(Key.C));
        assertEquals("G  D  A  E  B  F♯ C♯", diatonicByDegree(Key.G));
        assertEquals("D  A  E  B  F♯ C♯ G♯", diatonicByDegree(Key.D));
        assertEquals("E  B  F♯ C♯ G♯ D♯ A♯", diatonicByDegree(Key.E));
        assertEquals("B  F♯ C♯ G♯ D♯ A♯ F", diatonicByDegree(Key.B));
        assertEquals("F♯ C♯ G♯ D♯ A♯ E♯ C", diatonicByDegree(Key.Fs));


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
            ScaleNote sn = key.getDiatonicByDegree(j);
            String s = sn.toString();
            sb.append(s);
            if (s.toString().length() < 2)
                sb.append(" ");
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    @Test
    public void guessKey() {
        Key key;

        ArrayList<ScaleNote> scaleNotes = new ArrayList<>();
        scaleNotes.add(ScaleNote.D);
        scaleNotes.add(ScaleNote.G);
        scaleNotes.add(ScaleNote.A);
        scaleNotes.add(ScaleNote.C);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.D, key.getKeyScaleNote());

        scaleNotes.clear();
        scaleNotes.add(ScaleNote.Cs);
        scaleNotes.add(ScaleNote.Fs);
        scaleNotes.add(ScaleNote.Gs);

        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.Fs, key.getKeyScaleNote());
        scaleNotes.add(ScaleNote.B);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.Fs, key.getKeyScaleNote());

        scaleNotes.clear();
        scaleNotes.add(ScaleNote.C);
        scaleNotes.add(ScaleNote.F);
        scaleNotes.add(ScaleNote.G);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.C, key.getKeyScaleNote());
        scaleNotes.add(ScaleNote.As);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.C, key.getKeyScaleNote());

        scaleNotes.clear();
        scaleNotes.add(ScaleNote.Cs);
        scaleNotes.add(ScaleNote.Gs);
        scaleNotes.add(ScaleNote.Fs);
        scaleNotes.add(ScaleNote.B);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.Fs, key.getKeyScaleNote());

        scaleNotes.clear();
        scaleNotes.add(ScaleNote.C);
        scaleNotes.add(ScaleNote.G);
        scaleNotes.add(ScaleNote.F);
        scaleNotes.add(ScaleNote.As);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.C, key.getKeyScaleNote());

        scaleNotes.clear();
        scaleNotes.add(ScaleNote.B);
        scaleNotes.add(ScaleNote.E);
        scaleNotes.add(ScaleNote.Fs);
        scaleNotes.add(ScaleNote.A);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.B, key.getKeyScaleNote());

        scaleNotes.clear();
        scaleNotes.add(ScaleNote.As);
        scaleNotes.add(ScaleNote.Ds);
        scaleNotes.add(ScaleNote.Gs);
        scaleNotes.add(ScaleNote.F);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.C, key.getKeyScaleNote());

        scaleNotes.clear();
        scaleNotes.add(ScaleNote.Bb);
        scaleNotes.add(ScaleNote.Eb);
        scaleNotes.add(ScaleNote.Ab);
        scaleNotes.add(ScaleNote.F);
        key = Key.guessKey(scaleNotes);
        assertEquals(ScaleNote.Bb, key.getKeyScaleNote());
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
}





























