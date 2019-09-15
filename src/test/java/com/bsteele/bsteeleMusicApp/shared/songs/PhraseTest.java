package com.bsteele.bsteeleMusicApp.shared.songs;

import junit.framework.TestCase;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PhraseTest extends TestCase {

    @Test
    public void testParsing() {

        int phraseIndex = 0;
        int beatsPerBar = 4;
        String s;
        Phrase phrase;

        try {
            s = "G F E D x2";
            phrase = Phrase.parse(s, phraseIndex, beatsPerBar, null);
            fail();     //  a repeat is not a phrase
        } catch (ParseException pex) {
        }

        try {
            s = "A B C D, G F E D x2";
            phrase = Phrase.parse(s, phraseIndex, beatsPerBar, null);
            assertEquals(4, phrase.size());  //  phrase should not include the repeat
        } catch (ParseException pex) {
            pex.printStackTrace();
        }

        try {
            s = "   A ";
            phrase = Phrase.parse(s, phraseIndex, beatsPerBar, null);
            assertEquals(1, phrase.size());
        } catch (ParseException pex) {
            pex.printStackTrace();
        }
        try {
            s = "A B C D";
            phrase = Phrase.parse(s, phraseIndex, beatsPerBar, null);
            assertEquals(4, phrase.size());
        } catch (ParseException pex) {
            pex.printStackTrace();
        }

        try {
            s = "A B C D A B C D A B C D";
            phrase = Phrase.parse(s, phraseIndex, beatsPerBar, null);
            assertEquals(12, phrase.size());
        } catch (ParseException pex) {
            pex.printStackTrace();
        }

        try {
            s = "A B C, D A ,B C D ,A, B C D";
            phrase = Phrase.parse(s, phraseIndex, beatsPerBar, null);
            assertEquals(12, phrase.size());
        } catch (ParseException pex) {
            pex.printStackTrace();
        }

        try {
            s = "A B C, D A ,B C D ,A, B C D, G G x2";
            phrase = Phrase.parse(s, phraseIndex, beatsPerBar, null);
            assertEquals(12, phrase.size());
        } catch (ParseException pex) {
            pex.printStackTrace();
        }
    }

    private static Logger logger = Logger.getLogger(PhraseTest.class.getName());
}
