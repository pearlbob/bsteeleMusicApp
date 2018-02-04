/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.core.shared.GWT;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType
public class Song {
    
    public String transcribe(String chords, int halfSteps) {
        GWT.log("Song.transcribe()  here: " + halfSteps + " to: " + chords);

        int chordNumber = 0;
        String sout = "";
        int state = 0;
        for (int ci = 0; ci < chords.length(); ci++) {
            char c = chords.charAt(ci);
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

                    //  don't transcribe the section identifiers that happen to look like notes
//                    String sub = chords.substring(ci, 5);
//                    if ( / ^ Coda:/i.test(sub)
//                     
//                     
//                        ) {
//                                sout += sub;
//                        ci += sub.length - 1; //  skip the end of the section id
//                        break;
//                    }
//                    sub = chords.substr(ci, 4);
//                    if ( / ^ Out:/i.test(sub)
//                     
//                     
//                        ) {
//                                sout += sub;
//                        ci += sub.length - 1; //  skip the end of the section id
//                        break;
//                    }
//                    sub = chords.substr(ci, 3);
//                    if ( / ^ Ch:/i.test(sub) ||  / ^ Co
//                    :/i.test(sub) ||  / ^ C
//                    :/i.test(sub) ||  / ^ Br
//                    :/i.test(sub) ||  / ^ Pc
//                    :/i.test(sub)
//                     
//                     
//                        ) {
//                                sout += sub;
//                        ci += sub.length - 1; //  skip the end of the section id
//                        break;
//                    }
//                    sub = sub.substr(ci, 2);
//                    if ( / ^ C:/i.test(sub)
//                     
//                     
//                        ) {
//                                sout += sub;
//                        ci += sub.length - 1; //  skip the end of the section id
//                        break;
//                    }
//
//                    if (c >= 'A' && c <= 'G') {
//                        chordNumber = chordLetterToNumber(c);
//                        state = 1;
//                        break;
//                    }
//
//                    if (chords.startsWith('maj'
//                    , ci
//                     
//                     
//                        )) {
//                                sout += 'maj'
//                        ;
//                                ci += 2;
//                    }
//                    else if (chords.startsWith(
//                    
//                    'sus'
//                    , ci
//                     
//                     
//                        )) {
//                                sout += 'sus'
//                        ;
//                                ci += 2;
//                    }
//                    else 
                        if ((c >= '0' && c <= '9')
                            || c == 'm'
                            || c == ' ' || c == '-' || c == '|' || c == '/'
                            || c == '\n'
                            || c == js_delta) {
                                sout += c;
                            } 
                                else {	//  don't parse the rest
                                sout += c;
                                state = 10;
                            }
                    break;

                case 10: //	wait for newline
                    sout += c;
                    if (c == '\n') {
                        state = 0;
                    }
                    break;

                case 11: //	wait for newline or closing paren
                    sout += c;
                    if (c == '\n' || c == ')') {
                        state = 0;
                    }
                    break;
            }
        }
        
        GWT.log(sout);
        return sout;
    }

    private final String chordNumberToLetter(int n) {

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
