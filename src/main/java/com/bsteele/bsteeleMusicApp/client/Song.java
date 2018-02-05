/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import static jsinterop.annotations.JsPackage.GLOBAL;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType(namespace = GLOBAL)
public class Song {
   

    public static String transpose(String chords, int halfSteps) {
        //GWT.log("Song.transpose()  here: " + halfSteps + " to: " + chords);

        int chordNumber = 0;
        String sout = "";
        int state = 0;
        String chordLines[] = chords.split("\n");
        for (int li = 0; li < chordLines.length; li++) {
            String chordLine = chordLines[li];
            state = 0;
            
            for (int ci = 0; ci < chordLine.length(); ci++) {
                char c = chordLine.charAt(ci);
                switch (state) {
                    case 1:	//  chord symbol modifier, one character only
                        state = 0;
                        if (c == 'b' || c == js_flat) {
                            chordNumber -= 1;
                            sout += chordNumberToLetter(chordNumber + halfSteps);
                            break;
                        }
                        if (c == '#' || c == js_sharp) {
                            chordNumber += 1;
                            sout += chordNumberToLetter(chordNumber + halfSteps);
                            break;
                        }
                        if (c == js_natural) {
                            sout += chordNumberToLetter(chordNumber + halfSteps);
                            break;
                        }
                        sout += chordNumberToLetter(chordNumber + halfSteps);
                    //	fall through
                    default:
                    case 0:
                        if (c == '(') {
                            sout += c;
                            state = 11;
                            break;
                        }

                        //  don't transpose the section identifiers that happen to look like notes
                        String toMatch = chordLine.substring(ci, Math.min(chordLine.length() - ci, Section.maxLength));
                        int matchLength = Section.matchLength(toMatch);
                        if (matchLength > 0 && chordLine.charAt(ci + matchLength) == ':') {
                            String sub = chordLine.substring(ci, matchLength);
                            sout += sub;
                            ci += matchLength - 1; //  skip the end of the section id
                            break;
                        }

                        if (c >= 'A' && c <= 'G') {
                            chordNumber = chordLetterToNumber(c);
                            state = 1;
                            break;
                        }
                        if (toMatch.startsWith("maj")) {
                            sout += "maj";
                            ci += 2;
                        } else if (toMatch.startsWith("sus")) {
                            sout += "sus";
                            ci += 2;
                        } else if ((c >= '0' && c <= '9')
                                || c == 'm'
                                || c == ' ' || c == '-' || c == '|' || c == '/'
                                || c == '\n'
                                || c == js_delta) {
                            sout += c;
                        } else {	//  don't parse the rest
                            sout += c;
                            state = 10;
                        }
                        break;

                    case 10: //	wait for newline
                        sout += c;
                        break;

                    case 11: //	wait for newline or closing paren
                        sout += c;
                        if (c == ')') {
                            state = 0;
                        }
                        break;
                }
            }
            sout += '\n';
        }

        //GWT.log(sout);
        return sout;
    }

    private static int chordLetterToNumber(char letter) {
        int i = letter - 'A';
        //                            a  a# b  c  c# d  d# e  f f#  g  g#
        //                            0  1  2  3  4  5  6  7  8  9  10 11

        return chordLetterToNumber[i];
    }
    private static final int chordLetterToNumber[] = new int[]{0, 2, 3, 5, 7, 8, 10};

    private static String chordNumberToLetter(int n) {

        n = n % 12;
        if (n < 0) {
            n += 12;
        }
        //                            a     a#    b    c    c#    d    d#    e    f    f#    g    g#
        //                            0     1     2    3    4     5    6     7    8    9     10   11

        return chordNumberToLetter[n];
    }

    private static final String chordNumberToLetter[] = new String[]{"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private static final char js_flat = '\u266D';
    private static final char js_natural = '\u266E';
    private static final char js_sharp = '\u266F';
    private static final char js_delta = '\u0394';

}
