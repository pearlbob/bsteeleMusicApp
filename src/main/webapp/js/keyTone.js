/* 
 * Copyright 2015 Robert Steele, bsteele.com
 * All rights reserved.
 */

/**
 *
 * @author bob
 */
var keyTone = (function ()
{

    //
    function isPitchSharpenedOnScale(keyTone, pitch)
    {
        if (keyTone <= 0)
            return false;

        var ret = false;
        pitch = modPitch(pitch - 1);
        for (var i = 0; i < keyTone; i++)
            if (semi_sharps[i] === pitch)
            {
                ret = true;
                break;
            }

        return ret;
    }

    function isPitchNaturalOnScale(keyTone, pitch)
    {
        if (keyTone === 0)
            return false;

        var ret = (keyTone > 0
                ? isPitchSharpenedOnScale(keyTone, pitch + 1)
                : isPitchFlattedOnScale(keyTone, pitch - 1));

        return ret;
    }

    function isPitchFlattedOnScale(keyTone, pitch)
    {
        if (keyTone >= 0)
            return false;

        pitch = modPitch(pitch + 1);
        for (var i = 0; i < - keyTone; i++)
            if (semi_sharps[semi_sharps.length - 1 - i] === pitch)
                return true;

        return false;
    }

    function modPitch(pitch)
    {
        pitch = pitch % 12;
        if (pitch < 0)
            pitch += 12;
        return pitch;
    }

//    public static final boolean isScaleNote(int keyTone, int pitch)
//    {
//	pitch = modPitch(pitch);
//	return semi_majorScale[(pitch + 12 + (5 * keyTone) % 12) % 12];
//    }
//
//    public static final String getKeyName(int keyTone)
//    {
//	String names[] = (keyTone >= 0 ? sharpPitchNames : flatPitchNames);
//	return names[(8 + keyToneOffset(keyTone)) % 12];  //	origin at C, not E
//    }
//
//    private static int keyToneOffset(int keyTone)
//    {
//	return (12 + (7 * keyTone) % 12) % 12;
//    }
//
//    public enum NoteModifier
//    {
//
//	sharp, natural, flat;
//    };
//
//    public static final int mapNoteToPitch(int keyTone, int note)
//    {
//	return mapNoteToPitch(keyTone, note, null);
//    }
//
//    public static final int mapNoteToPitch(int keyTone, int note, NoteModifier modifier)
//    {
//	if (note < 0)
//	    return 0;
//
//	int pitch = 12 * (note / 7);
//	note = note % 7;
//
//	int j = 0;
//	int offset = (12 + (5 * keyTone) % 12) % 12;
//	for (int i = 0; i < 12; i++)
//	{
//	    if (!semi_majorScale[(i + offset) % 12])
//		continue;
//
//	    if (j == note)
//	    {
//		pitch += i;
//		break;
//	    }
//	    j = (j + 1) % 7;
//	}
//
//	return pitch;
//    }
//
//    public static final String getNoteName(int keyTone, int note)
//    {
//	return getScaleName(keyTone, mapNoteToPitch(keyTone, note, null));
//    }
//
    function getScaleName(keyTone, pitch) {
        pitch = modPitch(pitch);

        var s;

        if (isPitchSharpenedOnScale(keyTone, pitch))
            s = rawPitchNames[modPitch(pitch - 1)];
        else if (isPitchNaturalOnScale(keyTone, pitch))
            s = rawPitchNames[pitch] + naturalSign;
        else if (isPitchFlattedOnScale(keyTone, pitch))
            s = rawPitchNames[modPitch(pitch + 1)];
        else
        {
            s = rawPitchNames[pitch];
            if (keyTone >= 0 && pitchSharpFlats[pitch])
                s += sharpSign;
            else if (keyTone < 0 && pitchSharpFlats[pitch])
                s += flatSign;
        }

        //	deal with exceptions
        if (keyTone >= 6)
        {
            if (pitch === 1)
                s = "E" + sharpSign;
            else if (keyTone >= 7 && pitch === 8)
                s = "B";
        }
        else if (keyTone <= -6)
        {
            if (pitch === 7)
                s = "C";
            else if (keyTone <= -7 && pitch === 1)
                s = "F" + naturalSign;
        }
        return s;
    }

    function getAbsoluteScaleName(keyTone, pitch) {
        pitch = modPitch(pitch);

        let s;

        if (keyTone >= 0) {
            s = rawPitchNames[pitch];
        } else {
            s = flatPitchNames[pitch];
        }
        s += getAbsoluteScaleModifier(keyTone, pitch);
        return s;
    }
    
    function getAbsoluteScaleModifier(keyTone, pitch) {
        pitch = modPitch(pitch);

        let s = "";

        if (keyTone >= 0) {
            if (pitchSharpFlats[pitch])
                s = sharpSign;
        } else {
            if (pitchSharpFlats[pitch])
                s = flatSign;
        }
        return s;
    }



    function mapPitchToNote(keyTone, pitch) {

        var mPitch = modPitch(pitch);
        var ret = Math.floor(pitch / 12) * 7 + scaleNotes[pitch % 12]
                + (isPitchFlattedOnScale(keyTone, pitch)
                        || (keyTone < 0 && pitchSharpFlats[mPitch])
                        ? 1 : 0)
                //- (isPitchSharpenedOnScale(keyTone, pitch) ? 1 : 0)   //  why is this a fix to comment it out?
                ;
        return ret;
    }

    function sheetNotePrefix(keyTone, pitch)
    {
        pitch = modPitch(pitch);

        var s = undefined;

        if (isPitchSharpenedOnScale(keyTone, pitch))
            s = undefined;
        else if (isPitchNaturalOnScale(keyTone, pitch))
            s = naturalSign;
        else if (isPitchFlattedOnScale(keyTone, pitch))
            s = undefined;
        else
        {
            if (keyTone >= 0 && pitchSharpFlats[pitch])
                s = sharpSign;
            else if (keyTone < 0 && pitchSharpFlats[pitch])
                s = flatSign;
        }

        //	deal with exceptions
        if (keyTone >= 6)
        {
            if (pitch === 1)
                s = sharpSign;
            else if (keyTone >= 7 && pitch === 8)
                s = naturalSign;
        }
        else if (keyTone <= -6)
        {
//            if (pitch === 7)
//                ;
//            else 
            if (keyTone <= -7 && pitch === 1)
                s = naturalSign;
        }
        return s;
    }

    var flatSign = "\u266d";
    var naturalSign = "\u266e";
    var sharpSign = "\u266f";


    //	sharps: F♯, C♯, G♯, D♯, A♯, E♯, B♯
    //  flats:  B♭, E♭, A♭, D♭, G♭, C♭, F♭
    var semi_sharps =
            [// f  c  g  d   a  e  b
                1, 8, 3, 10, 5, 0, 7
            ];
    var rawPitchNames =
            [//  0    1   2     3    4    5    6    7    8    9    10   11
                "E", "F", "F", "G", "G", "A", "A", "B", "C", "C", "D", "D"
            ];
    var flatPitchNames =
            [//  0    1   2     3    4    5    6    7    8    9    10   11
                "E", "F", "G", "G", "A", "A", "B", "B", "C", "D", "D", "E"
            ];

    var pitchSharpFlats =
            [//  0e      1f    2fg   3g     4ga    5a    6ab   7b      8c    9cd   10d    11de
                false, false, true, false, true, false, true, false, false, true, false, true
            ];

    var scaleNotes = /* */[0, 1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6];    //  from E == 0, major scale


    return {
        getScaleName: function (keyTone, pitch) {
            return getScaleName(keyTone, pitch);
        },
        getAbsoluteScaleName: function (keyTone, pitch) {
            return getAbsoluteScaleName(keyTone, pitch);
        },
        getAbsoluteScaleModifier: function (keyTone, pitch) {
            return getAbsoluteScaleModifier(keyTone, pitch);
        },
        sheetNotePrefix: function (keyTone, pitch) {
            return sheetNotePrefix(keyTone, pitch);
        },
        isPitchSharpenedOnScale: function (keyTone, pitch) {
            return isPitchSharpenedOnScale(keyTone, pitch);
        },
        isPitchNaturalOnScale: function (keyTone, pitch) {
            return isPitchNaturalOnScale(keyTone, pitch);
        },
        isPitchFlattedOnScale: function (keyTone, pitch) {
            return isPitchFlattedOnScale(keyTone, pitch);
        },
        mapPitchToNote: function (keyTone, pitch) {
            return mapPitchToNote(keyTone, pitch);
        }
    };
})();
