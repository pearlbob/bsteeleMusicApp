/* 
 * Copyright 2015 Robert Steele, bsteele.com
 * All rights reserved.
 * 
 * todo:
 * 6b, 7b, 6#, 7#
 
 * scrolling score better?
 * double bar colon repeats
 */


/* global keyTone */
var keyTone;
/* global nashville */
var nashville;

var myBass = function () {

    let bassFretX = 63;
    let bassFretY = 10;
    let bassFretHeight = 200;
    let bassScale = 2000;
    let scoreLineHeight = 12;
    let bassSteps;
    let bpm = 120;
    let beatsPerBar = 4;
    let fretboardClickCount = 0;
    let myCanvas;
    let ctx;
    let keysDown = [];
    let myScoreLayers;
    let myScore;
    let scoreCtx;
    let myScoreBackground;
    let scoreBackgroundCtx;
    let scoreBackgroundDirty = true;
    let pressR = 20;
    let xInitial = 6;
    let xSpace = 4;
    let selectedStep;
    let sharpSign = "\u266F";
    let naturalSign = "\u266E";
    let flatSign = "\u266D";
    let bassClefImg = new Image();
    let wholeNoteImg = new Image();
    let halfNoteImg = new Image();
    let quarterNoteImg = new Image();
    let eighthNoteImg = new Image();
    let sixteenthNoteImg = new Image();
    let wholeRestImg = new Image();
    let halfRestImg = new Image();
    let quarterRestImg = new Image();
    let eighthRestImg = new Image();
    let sixteenthRestImg = new Image();
    let noteDurations;
    let noteRestDurations;
    let isFinished = false;
    let chordN = 0;
    let minorMajor = "major";
    let minorMajorSelectIndex = 0;
    let chordArray;
    let chords = {};
    let chordModifier;
    let diaton = null;
    let noteDurationSelect;
    let showScaleNotes;
    let showAllScaleNotes;
    let showHighNotes = false;
    let lastFileNameRead;
    let scoreRowHeight;
    let canvasScoreRowMaximum;
    let firstScoreLine = 0;
    let maxLineCount = 0;
    let ySpace = 2 * scoreLineHeight;
    let chordFontSize = 14;
    let chordYGap = 6;
    let scoreExtras;
    let scaleExtra;
    let scaleExtraSelect;
    let chordsOnScore;
    let chordsOnScoreSelect;
    let lastRightFinger = 0;
    let lastString = 10; //  force start on index
    let measureNumber = 1;
    let scaleNotesSelect;
    let allScaleNotesSelect;
    let showLyrics;
    let lyricsSelect;
    let lyricEntryText;
    let lyricsDirty = false;
    let undoSheetNotesStack = new Array(0);
    let undoSheetNotesStackPointer = 0;
    let undoSheetNotesStackCount = 0;
    let lastWrittenUndoSheetNotesStackCount = 0;
    let undoLimit = 20;
    let sheetNotes = new Array(0);
    let sheetNoteSelectedStart = undefined;
    let sheetNoteSelectedEnd = undefined;
    let sheetNoteCopyBuffer = new Array(0);
    let wheelDelta = 0;
    let playRememberedSheetNoteStart;
    let playRememberedSheetNoteEnd;
    let nonRestLastFullMeasureIndex;
    let sheetNoteInsert = false;
    let sheetNotesInQueue = new Array(0);
    let scoreBackgroundLastIndex = undefined;
    let scoreBackgroundLastBeat = 0;
    let keySelect;
    let chordSelect;
    let minorMajorSelect;
    let durationSelections;
    let isDottedSelect;
    let isTiedSelect;
    let bpmSelect;
    let beatsPerBarSelect;
    let bluesSelect;
    let beatsPerBluesMeasureSelect;
    let customBeatDiv;
    let bluesPatternSelect;
    let leadingNoteSelect;
    let manualBluesPatternSelect;
    let isSwing8 = false;
    let isSwing8Select;
    let swingType = 3;  //	1: mild, 2: medium, 3:strict
    let swingTypeSelect = 2;
    //	audio
    let audioContext;
    let stepMax = 40;
    let bassMp3s = {}; //  an extra or two
    let guitarMp3s = {};
    let audioStartDelay = 0.25;
    let amp;
    let beatDuration;
    let doLoop = false;
    let loopCount = 0;
    let loopStart;
    let loopEnd;
    let limitedLoopN;
    let myTimeout = undefined;
    let isPlaying = false;
    let playStartTime = 0;
    let outputDiv;
    let masterGain;
    let bassSelect;
    let drumSelect;
    let chordsSelect;
    let countSelect;
    let countAndSelect;
    let playModeSelect;
    let drumGain;
    let countGain;
    let chordGain;
    let mp3Sources = new Array(0);
    let doBass = false;
    let doDrums = false;
    let doChords = false;
    let doCount = false;
    let doCountAnd = false;
    let doPlayMode = false;
    let snare1SoundBuffer = {};
    let snare2SoundBuffer = {};
    let kickSoundBuffer = {};
    let hiHatSelect;
    let hiHatRhythm;
    let hiHat3SoundBuffer = {};
    let hiHat1SoundBuffer = {};
    let count1SoundBuffer = {};
    let count2SoundBuffer = {};
    let count3SoundBuffer = {};
    let count4SoundBuffer = {};
    let countAndSoundBuffer = {};
    let title = "Unknown";
    let fileInput;
    let textFile = null;
    let downloadlink = null;
    let volume = 10;
    let volumeSelect;
    //                          0    1     2         3     4         5     6         7    8    9         10     11
    let sharpPitchNames = /**/["E", "F", "F\u266f", "G", "G\u266f", "A", "A\u266f", "B", "C", "C\u266f", "D", "D\u266f"];
    let flatPitchNames = /* */["E", "F", "G\u266D", "G", "A\u266D", "A", "B\u266D", "B", "C", "D\u266D", "D", "E\u266D"];
    let pitchNames = sharpPitchNames;
    //                     0  1  2  3  4  5  6  7  8  9  10 11
    let scaleSharps = /**/[0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1]; //  from E == 0, major scale
    let romanNumerial = [" ", "I", "ii", "iii", "IV", "V", "vi", "vii"];
    let keyN = 0; //  locaction in circle of fiths, -6 <= keyN <= 6, C == 0
    let keyScaleNotesElement;
    let circleOfFifths;

    //
    //
    function onFretClick(x, y) {
        for (let s = 0; s < 4; s++)
            for (let fret = 0; fret < 13; fret++) {
                let bassStep = bassSteps[s][fret];
                let nextStep = bassSteps[s][fret + 1];
                if ((Math.abs(bassStep.fretX - x) <= pressR
                        || (x > bassStep.fretX && x < bassStep.fretX + pressR + (nextStep.fretX - bassStep.fretX) / 4))
                        && Math.abs(bassStep.fretY - y) <= pressR)
                {
                    insertBassStepAsSheetNote(bassStep);
                    doBass = bassSelect.checked;
                    playSelectedStep();   //  too much?
                    bassChordUpdate();
                    //scoreComputeLinesAndUpdate();
                    //updateNoteSelectOptions(sheetNoteSelectedStart);
                    break;
                }
            }

        //  hide the annoying tooltip once the fretboard has been used a bit
        fretboardClickCount++;
        if (fretboardClickCount >= 3)
            myCanvas.title = '';
    }

    function insertBassStepAsSheetNote(bassStep) {
        selectedStep = bassStep;
        let sheetNote = {
            isNote: true,
            string: bassStep.s,
            fret: bassStep.fret,
            noteDuration: noteDurationSelect % 5 + (isDottedSelect.checked ? 5 : 0),
            chordN: chordN,
            chordModifier: chordModifier,
            minorMajor: minorMajor,
            minorMajorSelectIndex: minorMajorSelectIndex,
            scaleN: chordArray[(bassStep.stepN + 12 - effectiveChordN(chordN, minorMajor)) % 12],
            lyrics: lyricEntryText.valueOf().value,
            tied: isTiedSelect.checked,
            line: 0,
            m: 0
        };
        insertSheetNote(sheetNote);
    }

    function refreshNoteModifiers() {
        if (sheetNoteSelectedStart === undefined)
            return;
        if (sheetNoteInsert)
            return;
        if (sheetNoteSelectedEnd !== undefined && sheetNoteSelectedStart !== sheetNoteSelectedEnd)
            return;
        let sn = sheetNotes[sheetNoteSelectedStart];
        if (!sn.isNote)
            return;
        sn.noteDuration = noteDurationSelect % 5 + (isDottedSelect.checked ? 5 : 0);
        sn.chordN = chordN;
        sn.chordModifier = chordModifier;
        sn.minorMajor = minorMajor;
        sn.minorMajorSelectIndex = minorMajorSelectIndex;
        //sn.scaleN= chordArray[(bassStep.stepN + 12 - chordN) % 12];
        sn.lyrics = lyricEntryText.valueOf().value;
        sn.tied = isTiedSelect.checked;
        pushUndoStack(); //  assume there are changes!!

        scoreComputeLinesAndUpdate();
    }

    function onMyRestClick(restN) {
        let rest = {isNote: false, noteDuration: restN};
        insertSheetNote(rest);
        scoreComputeLinesAndUpdate();
    }

    function onLyricEntry() {
        if (sheetNoteSelectedStart !== undefined && sheetNoteInsert === false) {
            let sheetNoteSelected = sheetNotes[sheetNoteSelectedStart];
            sheetNoteSelected.lyrics = lyricEntryText.valueOf().value;
            scoreComputeLinesAndUpdate();
        }
    }

    function onRepeatStart() {
        let n = {isNote: false, noteDuration: -1, lyrics: "&#x1d106;"};
        insertSheetNote(n);
        scoreComputeLinesAndUpdate();
    }

    function onRepeatEnd() {
        let n = {isNote: false, noteDuration: -1, lyrics: "&#x1d107;"};
        insertSheetNote(n);
        scoreComputeLinesAndUpdate();
    }

    function onBlankEntry() {
        let sheetNote = {
            isNote: false,
            string: 0,
            fret: 0,
            noteDuration: -1, // indicated noteless chord entry
            chordN: chordN,
            chordModifier: chordModifier,
            minorMajor: minorMajor,
            minorMajorSelectIndex: minorMajorSelectIndex,
            scaleN: 0,
            lyrics: lyricEntryText.valueOf().value,
            tied: false,
            line: 0,
            m: 0
        };
        insertSheetNote(sheetNote);
    }

    function deepCopy(o) {
        if (o === undefined)
            return o;
        if (Array.isArray(o)) {
            let newArr = [];
            for (let i = o.length; i-- > 0; ) {
                newArr[i] = deepCopy(o[i]);
            }
            return newArr;
        }
        if (typeof o === 'object') {
            let copy = Object.prototype.toString.call(o) === '[object Array]' ? [] : {};
            let k;
            for (k in o) {
                copy[k] = deepCopy(o[k]);
            }
            return copy;
        }
        if (typeof o === 'string') {
            return String(o);
        }
        return o;
    }

    function insertSheetNote(sheetNote) {
        if (sheetNotes.length > 0)
        {
            let i = sheetNoteSelectedStart === undefined ? sheetNotes.length - 1 : sheetNoteSelectedStart;
            let e = sheetNotes[i];
            sheetNote.line = e.line;    //  fixme: only approximate!
            sheetNote.m = e.m;    //  fixme: only approximate!
        }

        if (sheetNoteSelectedStart === undefined) {
            //  at the end
            sheetNotes.push(sheetNote);
            scoreComputeLinesAndUpdate();
            setSheetNoteSelectedIndex(undefined);   //  stay at the end
        } else {
            //  at the beginning or the middle
            let inc = (sheetNoteInsert === true ? 0 : 1);
            sheetNotes.splice(sheetNoteSelectedStart, inc, sheetNote);
            scoreComputeLinesAndUpdate();
            setSheetNoteSelectedIndex(sheetNoteSelectedStart + 1);
            if (sheetNoteSelectedStart >= sheetNotes.length)
                sheetNoteSelectedStart = undefined;
            //sheetNoteInsert = false;       //  leave in same mode
        }
        pushUndoStack();
        updateFromDom();
    }

    function pushUndoStack() {
        //  cut the old undo's off the stack
        if (undoSheetNotesStackPointer < undoSheetNotesStack.length - 1)
            undoSheetNotesStack = undoSheetNotesStack.slice(0, undoSheetNotesStackPointer + 1);
        //  store the sheet notes in the undo stack
        undoSheetNotesStack = undoSheetNotesStack.slice(-undoLimit);
        undoSheetNotesStack.push(
                {
                    sheetNotes: deepCopy(sheetNotes),
                    sheetNoteSelectedStart: sheetNoteSelectedStart,
                    sheetNoteSelectedEnd: sheetNoteSelectedEnd,
                    sheetNoteInsert: sheetNoteInsert
                });
        undoSheetNotesStackPointer = undoSheetNotesStack.length - 1;
        //window.console.log("insert undo: " + undoSheetNotesStackPointer+" / "+undoSheetNotesStack.length);
        undoSheetNotesStackCount++;
        lyricsDirty = false;
    }

    function onMyUndo() {
        if (undoSheetNotesStackPointer <= 0)
            return;
        undoSheetNotesStackPointer--;
        undoSheetNotesStackCount++;
        expressUndoSheetNotesStackPointer();
    }

    function onMyRedo() {
        if (undoSheetNotesStackPointer >= undoSheetNotesStack.length - 1)
            return;
        undoSheetNotesStackPointer++;
        undoSheetNotesStackCount++;
        expressUndoSheetNotesStackPointer();
    }

    function expressUndoSheetNotesStackPointer() {
        //window.console.log("express undo: " + undoSheetNotesStackPointer+" / "+undoSheetNotesStack.length);
        let undo = undoSheetNotesStack[undoSheetNotesStackPointer];
        sheetNotes = deepCopy(undo.sheetNotes);
        setSheetNoteSelectedIndex(undo.sheetNoteSelectedStart);
        sheetNoteInsert = undo.sheetNoteInsert;
        scoreComputeLinesAndUpdate();
    }

    function fretLoc(n) {
        if (n < 0)
            n = 0;
        return bassFretX + bassScale - Math.round((bassScale / Math.pow(2, n / 12)));
    }

    function getSheetNoteStep(sheetNote) {
        if (sheetNote.isNote === false)
            return undefined;
        return bassSteps[sheetNote.string][sheetNote.fret];
    }
    function getSheetNoteIndexStep(sheetNoteIndex) {
        return getSheetNoteStep(sheetNotes[sheetNoteIndex]);
    }

    function fretboardUpdate() {

        //  clear
        ctx.fillStyle = "#FFFFFF";
        ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
        //  frets
        ctx.strokeStyle = '#808080';
        ctx.lineWidth = 2;
        for (let fret = 0; fret <= 12 + 1; fret++) {
            let x = fretLoc(fret);
            ctx.beginPath();
            ctx.moveTo(x, bassFretY);
            ctx.lineTo(x, bassFretY + bassFretHeight);
            ctx.stroke();
        }

        //  strings
        ctx.strokeStyle = 'lightgray';
        for (let s = 0; s < 4; s++) {
            ctx.lineWidth = (4 - s) * 2;
            let y = bassFretY + bassFretHeight - bassFretHeight * s / 4 - bassFretHeight / 8;
            ctx.beginPath();
            ctx.moveTo(bassFretX, y);
            ctx.lineTo(bassFretX + bassScale * 0.6, y);
            ctx.stroke();
        }

        //  markers
        ctx.strokeStyle = 'blue';
        ctx.lineWidth = 3;
        for (let i = 0; i < 4; i++) {
            ctx.beginPath();
            ctx.arc((fretLoc(2 + 2 * i) + fretLoc(2 + 2 * i + 1)) / 2,
                    bassFretY + bassFretHeight / 2, 10, 0, 2 * Math.PI);
            ctx.stroke();
        }
        ctx.beginPath();
        ctx.arc((fretLoc(11) + fretLoc(12)) / 2, bassFretY + bassFretHeight / 4, 10, 0, 2 * Math.PI);
        ctx.stroke();
        ctx.beginPath();
        ctx.arc((fretLoc(11) + fretLoc(12)) / 2, bassFretY + bassFretHeight * 3 / 4, 10, 0, 2 * Math.PI);
        ctx.stroke();
    }

    //       R  1  2  3  4  5  6  7  8  9  10 11   semitones
    //  negative numbers are scale notes not in the chord
    let semi_major = //
            [1, 0, -2, 0, 3, -4, 0, 5, 0, -6, 0, -7];
    let semi_majorScale = //
            [1, 0, 2, 0, 3, 4, 0, 5, 0, 6, 0, 7];
    let semi_majorPentatonic = //
            [1, 0, 2, 0, 3, -4, 0, 5, 0, 6, 0, -7];
    let semi_major7 = //
            [1, 0, -2, 0, 3, -4, 0, 5, 0, -6, 0, 7];
    let semi_minor = //
            [1, 0, -2, 3, 0, -4, 0, 5, -6, 0, -7, 0];
    let semi_minorScale = //
            [1, 0, 2, 3, 0, 4, 0, 5, 6, 0, 7, 0];
    let semi_minor7 = //
            [1, 0, -2, 3, 0, -4, 0, 5, -6, 0, 7, 0];
    let semi_m7b5 = //
            [1, 0, -2, 3, 0, -4, 5, 0, -6, 0, 7, 0];
    let semi_minorPentatonic = //
            [1, 0, 0, 3, 0, 4, 0, 5, 0, 0, 7, 0];
    let semi_blues = //
            [1, 0, 0, 3, 0, 4, 5, 6, 0, 0, 7, 0];
    let semi_dominant7 = //
            [1, 0, -2, 0, 3, -4, 0, 5, 0, -6, 7, 0];
    let semi_power5 = //
            [1, 0, -2, 0, -3, -4, 0, 5, 0, -6, -7, 0];
    let semi_dominant7Scale = //
            [1, 0, 2, 0, 3, 4, 0, 5, 0, 6, 7, 0];
    let semi_augmented = //
            [1, 0, 0, 0, 3, 0, 0, 0, 5, 0, 0, 0];
    let semi_augmented7 = //
            [1, 0, 0, 0, 3, 0, 0, 0, 5, 0, 7, 0];
    let semi_mixolydianScale = //
            [1, 0, 2, 0, 3, 4, 0, 5, 0, 6, 7, 0];
    let semi_diminished = //
            [1, 0, 0, 3, 0, 0, 5, 0, 0, 0, 0, 0];
    let semi_diminished7 = //
            [1, 0, 0, 3, 0, 0, 5, 0, 0, 7, 0, 0];
    let semi_jazz7 = //
            [1, 0, 0, 3, 0, 0, 0, 5, 0, 0, 0, 7];
    let semi_boogiewoogie = //
            [1, 0, 0, 0, 3, 0, 0, 5, 0, 6, 7, 0];
    let semi_r5 = //
            [1, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0];
    let semi_r56 = //
            [1, 0, 0, 0, 0, 0, 0, 5, 0, 6, 0, 0];
    let semi_r5b7 = //
            [1, 0, 0, 0, 0, 0, 0, 5, 0, 0, 7, 0];
    /*
     * 
     */
    function chordModifierUpdate() {
        chordArray = semi_major;    //  default only
        chordModifier = '';
        diaton = null;
        minorMajor = minorMajorSelect[minorMajorSelectIndex].valueOf().value;
        if (minorMajor === "major") {
            //chordModifier = "maj";
        } else if (minorMajor === "majorScale") {
            //chordModifier = "maj";
            chordArray = semi_majorScale;
        } else if (minorMajor === "pentatonic") {
            chordModifier = "7";
            chordArray = semi_majorPentatonic;
        } else if (minorMajor === "major7") {
            chordModifier = "maj7";
            chordArray = semi_major7;
        } else if (minorMajor === "minor") {
            chordArray = semi_minor;
            chordModifier = "m";
        } else if (minorMajor === "minorScale") {
            chordArray = semi_minorScale;
            chordModifier = "m";
        } else if (minorMajor === "minor7") {
            chordModifier = "m7";
            chordArray = semi_minor7;
        } else if (minorMajor === "minorPentatonic") {
            chordModifier = "m7";
            chordArray = semi_minorPentatonic;
        } else if (minorMajor === "dominant") {
            chordModifier = "7";
            chordArray = semi_dominant7;
        } else if (minorMajor === "power5") {
            chordModifier = "5";
            chordArray = semi_power5;
        } else if (minorMajor === "augmented") {
            chordModifier = "#5";
            chordArray = semi_augmented;
        } else if (minorMajor === "augmented7") {
            chordModifier = "7#5";
            chordArray = semi_augmented7;
        } else if (minorMajor === "mixolydianScale") {
            chordModifier = "7";
            chordArray = semi_mixolydianScale;
        } else if (minorMajor === "diminished") {
            chordArray = semi_diminished;
            chordModifier = "dim";
        } else if (minorMajor === "diminished7") {
            chordModifier = "dim7";
            chordArray = semi_diminished7;
        } else if (minorMajor === "jazz7") {
            chordModifier = "jazz7";
            chordArray = semi_jazz7;
        } else if (minorMajor === "blues") {
            chordModifier = "blues";
            chordArray = semi_blues;
        } else if (minorMajor === "boogiewoogie") {
            chordArray = semi_boogiewoogie;
        } else if (minorMajor === "r5") {
            chordArray = semi_r5;
        } else if (minorMajor === "r56") {
            chordArray = semi_r56;
        } else if (minorMajor === "r5b7") {
            chordArray = semi_r5b7;
        } else if (minorMajor === "mdi") {
            diaton = 0;
            chordModifier = "maj7";
            chordArray = semi_major7;
        } else if (minorMajor === "mdii") {
            diaton = 2;
            chordModifier = "m7";
            chordArray = semi_minor7;
        } else if (minorMajor === "mdiii") {
            diaton = 4;
            chordModifier = "m7";
            chordArray = semi_minor7;
        } else if (minorMajor === "mdiv") {
            diaton = 5;
            chordModifier = "maj7";
            chordArray = semi_major7;
        } else if (minorMajor === "mdv") {
            diaton = 7;
            chordModifier = "7";
            chordArray = semi_dominant7;
        } else if (minorMajor === "mdvi") {
            diaton = 9;
            chordModifier = "m7";
            chordArray = semi_minor7;
        } else if (minorMajor === "mdvii") {
            diaton = 11;
            chordModifier = "m7b5";
            chordArray = semi_m7b5;
        }
    }
    function bassChordUpdate() {
        chordModifierUpdate();

        let rootFretMin = 0;
        let rootFretMax = 0;
        //  find the diatonic min/max frets
        if (diaton !== null) {
            for (let s = 0; s < 2; s++)  //  will find it on first two strings
                for (let fret = 2; fret <= 7; fret++) {
                    if (s === 0 && fret > 5)
                        continue;
                    let n = (s * 5 + fret + 12 - chordN);
                    let modN = n % 12;
                    let note = chordArray[modN];
                    if (note === 1) {
                        rootFretMin = fret - 1;
                        rootFretMax = fret + 2;
                        break;
                    }
                    if (rootFretMin > 0)
                        break;
                }
            //chordN = (chordN + diaton) % 12; //  adjust to chord root, not the diatonic root
        }

        fretboardUpdate();


        {    //  chord notes
            let alphaMax = (isPlaying ? 0.3 : 1);
            let rootSeen = (diaton !== null); // true if diatonic, false until seen otherwise
            let rootN = 0;
            for (let s = 0; s < 4; s++)
                for (let fret = 0; fret < 13; fret++) {
                    let n = (s * 5 + fret + 12 - effectiveChordN(chordN, minorMajor));
                    let modN = n % 12;
                    let note = chordArray[modN];
                    let emphasis = false;
                    if (note > 0) {
                        if (diaton === null) {
                            //  emphasize the first full cycle
                            if (!rootSeen && fret > 0 && (fret <= 5 || s > 0) && note === 1) {
                                rootSeen = true;
                                rootN = n;
                                rootFretMin = fret - 1;
                                rootFretMax = fret + 3;
                            }
                            emphasis = (rootSeen && n <= rootN + 12 && fret >= rootFretMin && fret <= rootFretMax);
                        } else {     //  diatonic
                            emphasis = fret >= rootFretMin && fret <= rootFretMax;
                        }
                    }

                    //  indicate the press
                    let isHighlight = false;
                    if (sheetNoteSelectedStart !== undefined && sheetNoteSelectedStart < sheetNotes.length) {
                        let sn = sheetNotes[sheetNoteSelectedStart];
                        isHighlight = sn.isNote
                                && sn.string === s && sn.fret === fret
                                //&& bassStep.stepN === getSheetNoteIndexStep(sheetNoteSelectedStart).stepN
                                ;
                    }

                    if (note > 0 || isHighlight || (showAllScaleNotes && note < 0)) {
                        let bassStep = bassSteps[s][fret];
                        ctx.globalAlpha = emphasis ? alphaMax : 0.3;
                        displayBassPress(bassStep, note, isHighlight);
                    }
                }
            ctx.globalAlpha = 1;
        }

        if (isPlaying || doPlayMode) {
            //  only show the playing press
            ctx.globalAlpha = 1;
            if (sheetNoteSelectedStart !== undefined)
            {
                //  go back to the last note
                for (let i = sheetNoteSelectedStart; i >= 0; i--) {
                    let sn = sheetNotes[i];
                    if (sn.isNote) {
                        let bassStep = bassSteps[sn.string][sn.fret];
                        //  indicate the press
                        displayBassPress(bassStep, sn.scaleN, true);
                        //  go forward to the next note
                        for (let j = sheetNoteSelectedStart + 1; j < sheetNotes.length; j++)
                        {
                            let sn2 = sheetNotes[j];
                            if (sn2.isNote) {
                                ctx.globalAlpha = 0.45;
                                drawArrowFromTo(sn, sn2);
                                ctx.globalAlpha = 1;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            ctx.globalAlpha = 1;
        }

    }

    function diatonicStep(aMinorMajor) {
        let aDiaton = 0;
        switch (aMinorMajor) {
            case 'mdi':
                aDiaton = 0;
                break;
            case 'mdii':
                aDiaton = 2;
                break;
            case 'mdiii':
                aDiaton = 4;
                break;
            case 'mdiv':
                aDiaton = 5;
                break;
            case 'mdv':
                aDiaton = 7;
                break;
            case 'mdvi':
                aDiaton = 9;
                break;
            case 'mdvii':
                aDiaton = 11;
                break;
        }
        return aDiaton;
    }

    function effectiveChordN(aChordN, aMinorMajor) {
        return (aChordN + diatonicStep(aMinorMajor)) % 12;
    }


    function displayBassPress(bassStep, note, isHighlight) {

        //  count strings internally from 0!  worse yet, E to G

        let fretX = bassStep.fretX;
        if (bassStep.fret > 0 && showScaleNotes === true && scaleExtra === 1)
            fretX += pressR / 2;

        ctx.strokeStyle = 'black';
        ctx.lineWidth = 2;
        if (isHighlight)
            ctx.strokeRect(fretX - pressR, bassStep.fretY - pressR, 2 * pressR, 2 * pressR);
        else {
            ctx.beginPath();
            ctx.arc(fretX, bassStep.fretY, pressR, 0, 2 * Math.PI);
            ctx.stroke();
        }

        if (note === 1)
            ctx.fillStyle = 'red';
        else if (note === 3)
            ctx.fillStyle = '#FFB28F';
        else if (note === 5)
            ctx.fillStyle = 'orange';
        else if (note === 7)
            ctx.fillStyle = 'yellow';
        else
            ctx.fillStyle = 'lightyellow';
        if (isHighlight)
            ctx.fillRect(fretX - pressR, bassStep.fretY - pressR, 2 * pressR, 2 * pressR);
        else
            ctx.fill();
        ctx.font = "30px Arial";
        ctx.textAlign = "center";
        let numberString = (note === 1 ? 'R' : (note === 0 ? "" : Math.abs(note)));
        if (showScaleNotes === true) {
            if (scaleExtra === 1) {
                ctx.fillRect(fretX - 2.3 * pressR, bassStep.fretY - pressR + 4, 1.2 * pressR, 1.5 * pressR);
                ctx.fillStyle = 'black';
                ctx.fillText(numberString, fretX - 2 * pressR + pressR / 3, bassStep.fretY + pressR / 2);
            }
            ctx.fillStyle = 'black';
            let name = keyTone.getAbsoluteScaleName(keyN, bassStep.stepN);
            if (name !== undefined)
                ctx.fillText(name, fretX, bassStep.fretY + pressR - 8);
        } else if (scaleExtra === 1) {
            ctx.fillStyle = 'black';
            ctx.fillText(numberString, fretX, bassStep.fretY + pressR - 8);
        }
    }

    function drawArrowFromTo(sn1, sn2) {
        let bs1 = bassSteps[sn1.string][sn1.fret];
        let bs2 = bassSteps[sn2.string][sn2.fret];
        let sn1Xoff = 0;
        let sn1Yoff = 0;
        let sn2Xoff = 0;
        let sn2Yoff = 0;
        if (sn1.string === sn2.string) {
            if (sn1.fret === sn2.fret) {
                ctx.strokeStyle = 'black';
                ctx.lineWidth = 1;
                ctx.beginPath();
                ctx.arc(bs1.fretX + pressR, bs1.fretY - pressR, pressR, 1 / 2 * Math.PI, 2 / 2 * Math.PI, true);
                ctx.stroke();
                return;
            } else if (sn1.fret > sn2.fret) {
                sn1Xoff = -pressR;
                //sn2Xoff = pressR;
            } else if (sn1.fret < sn2.fret) {
                sn1Xoff = pressR;
                //sn2Xoff = -pressR;
            }
        } else if (sn1.string < sn2.string) {
            sn1Yoff = -pressR;
            //sn2Yoff = pressR;
        } else if (sn1.string > sn2.string) {
            sn1Yoff = pressR;
            //sn2Yoff = -pressR;
        }
        arrow([bs1.fretX + sn1Xoff, bs1.fretY + sn1Yoff], [bs2.fretX + sn2Xoff, bs2.fretY + sn2Yoff]);
    }

    function arrow(p1, p2) {
        ctx.save();
        let dist = Math.sqrt((p2[0] - p1[0]) * (p2[0] - p1[0]) + (p2[1] - p1[1]) * (p2[1] - p1[1]));
        ctx.beginPath();
        ctx.lineWidth = 1;
        ctx.strokeStyle = 'black';
        ctx.moveTo(p1[0], p1[1]);
        ctx.lineTo(p2[0], p2[1]);
        ctx.stroke();
        let angle = Math.acos((p2[1] - p1[1]) / dist);
        if (p2[0] < p1[0])
            angle = 2 * Math.PI - angle;
        let size = 15;
        ctx.beginPath();
        ctx.translate(p2[0], p2[1]);
        ctx.rotate(-angle);
        ctx.fillStyle = 'black';
        ctx.lineWidth = 2;
        ctx.strokeStyle = 'black';
        ctx.moveTo(0, -size);
        ctx.lineTo(-size / 5, -size);
        ctx.lineTo(0, 0);
        ctx.lineTo(size / 5, -size);
        ctx.lineTo(0, -size);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();
        ctx.restore();
    }



    function scoreLines(y) {
        scoreCtx.strokeStyle = 'black';
        scoreCtx.lineWidth = 1;
        for (let line = 0; line < 5; line++) {
            scoreCtx.beginPath();
            scoreCtx.moveTo(0, y);
            scoreCtx.lineTo(scoreCtx.canvas.width, y);
            scoreCtx.stroke();
            y += scoreLineHeight;
        }
        return y;
    }

    //	used when the notes have changed and new rendering locations are likely required 
    function scoreComputeLinesAndUpdate() {
        scoreUpdateAllLines(false); //	compute note line sizes after note changes

        //  keep selected lines in view
        //  now that the note lines are correct, we can use them!

        //  render
        scoreUpdate();
    }

    //	used when a simple refresh is required to render score
    function scoreUpdate() {
        scoreUpdateAllLines(true);
    }

    function scoreUpdateAllLines(doRender) {
        {
            let topNote = 0;
            for (let i = 0; i < sheetNotes.length; i++) {
                let sn = sheetNotes[i];
                if (sn.isNote) {
                    let stepN = sn.string * 5 + sn.fret;
                    if (stepN > topNote)
                        topNote = stepN;
                }
            }
            showHighNotes = topNote > 18;
        }
        scoreExtras = (showScaleNotes ? 1 : 0)
                + (chordsOnScore > 0 ? 1 : 0)
                + (scaleExtra > 0 ? 1 : 0)
                + (showHighNotes ? 1 : 0)
                + (showLyrics > 0 ? 1 : 0);
        scoreRowHeight =
                9 * scoreLineHeight
                + scoreExtras * ySpace
                + 1
                ;

        //  clear
        measureNumber = 1;
        scoreCtx.clearRect(0, 0, scoreCtx.canvas.width, scoreCtx.canvas.height);
        if (sheetNotes.length === 0) {
            scoreUpdateLine(0, 0, true);
        } else {
            //  update from the top line down
            let lastIndex = sheetNotes.length;
            let index = 0;
            //  global to be retained across score lines
            lastRightFinger = 0;
            lastString = 10; //  force start on index

            let line = 0;
            for (; line < 250/*safety only*/ && index < lastIndex; line++)
            {
                index = scoreUpdateLine(line, index, doRender);
            }
            maxLineCount = line;
        }

        canvasScoreRowMaximum = Math.max(Math.floor(scoreCtx.canvas.height / scoreRowHeight), 1);
        //window.console.log("canvasScoreRowMaximum = " + canvasScoreRowMaximum);
    }


    //  flats:                      B♭, E♭, A♭, D♭, G♭, C♭, F♭
    let keyFlatLocations = /* */[0, 3.5, 2, 4, 2.5, 4.5, 3, 5];
    //	sharps:                     F♯, C♯, G♯, D♯, A♯,E♯, B♯
    let keySharpLocations = /**/[0, 1.5, 3, 1, 2.5, 4, 2, 3.5];

    //  main score rendering, should only be used by scoreUpdateAllLines()
    function scoreUpdateLine(line, sheetNoteN,
            doRender //  used to compute space used by prior un-rendered lines
            )
    {
        //  skip rendering lines prior to the top
        if (doRender && line < firstScoreLine) {
            for (; sheetNoteN < sheetNotes.length; sheetNoteN++) {
                let sheetNote = sheetNotes[sheetNoteN];
                if (sheetNote.line > line)
                    break;
            }
            return sheetNoteN;
        }

        let lineTop = (line - firstScoreLine) * scoreRowHeight;
        scoreCtx.clearRect(0, lineTop, scoreCtx.canvas.width, scoreRowHeight);
        let lastX = xInitial;
        let y = lineTop + (1 + (showHighNotes ? 1 : 0)) * scoreLineHeight + scoreExtras * ySpace;
        let clefTopY = y;
        let selectedTop = lineTop + 1;
        let selectedBottom = clefTopY + 6 * scoreLineHeight;
        let clefBottomY = y + 5 * scoreLineHeight;
        if (doRender) {
            // bass lines
            scoreLines(y);

            //  left bar
            scoreCtx.strokeStyle = 'black';
            scoreCtx.lineWidth = 1;
            scoreCtx.beginPath();
            scoreCtx.moveTo(0, clefTopY);
            scoreCtx.lineTo(0, clefBottomY - scoreLineHeight);
            scoreCtx.stroke();
            //  bass clef
            scoreCtx.font = "60px Arial";
            scoreCtx.drawImage(bassClefImg, xInitial, clefTopY);
        }
        lastX += bassClefImg.width + 2 * xSpace;
        //log( "lastX after clef: "+lastX);


        //  key
        if (keyN !== 0) {
            scoreCtx.font = 2 * scoreLineHeight + "px Arial";
            let locs = (keyN > 0 ? keySharpLocations : keyFlatLocations);
            let keySign = (keyN > 0 ? sharpSign : flatSign);
            let Yoff = (keyN > 0 ? 3 : 0);
            for (let i = 1; i <= Math.abs(keyN); i++) {
                //  compute height of sharp/flat from note
                let keyY = clefTopY + locs[i] * scoreLineHeight + Yoff;
                if (doRender)
                    scoreCtx.fillText(keySign, lastX, keyY);
                lastX += scoreCtx.measureText(keySign).width / 2;
            }
            if (keyN !== 0)
                lastX += scoreCtx.measureText(keySign).width / 2;
        }

        //  time stamp
        if (line === 0) {
            scoreCtx.font = "30px Arial";
            let timeY = 0;
            let timeChar = "C";
            if (beatsPerBar === 4) {
                timeY = 3 * scoreLineHeight - 2;
            } else {
                timeChar = beatsPerBar;
                timeY = 2 * scoreLineHeight - 2;
                if (doRender)
                    scoreCtx.fillText("4", lastX, clefTopY + 4 * scoreLineHeight - 2);
            }
            if (doRender)
                scoreCtx.fillText(timeChar, lastX, clefTopY + timeY + 1);
            lastX += scoreCtx.measureText(timeChar).width + 2 * xSpace;
            if (isSwing8)
            {
                scoreCtx.font = "14px Arial";
                scoreCtx.fillText("Swing", 0, clefTopY - 1.5 * scoreLineHeight);
            }
        }
        lastX += 2 * xSpace;

        if (doRender) {
            //  initial measure number
            if (sheetNotes.length > 0) {
                let sheetNote = sheetNotes[sheetNoteN];
                measureNumber = sheetNote.m;
            }
            scoreCtx.font = scoreLineHeight + "px Arial";
            scoreCtx.fillText(measureNumber, lastX, clefTopY - scoreLineHeight / 2);
        }

        let lineXStart = lastX;
        let maxWidthPerBar = 0;
        let maxMeasureW = 0;
        let lastMaxMeasureW = maxMeasureW;
        let measuresInTheLine = 0;

        let lastChordName = undefined;
        let measureCount = 0;
        let beatCount = 0;
        let lastBeat = -1;       //  beats count from 0 internally
        let lastChordX = 0;
        let index = sheetNoteN;
        let eighthsPlayedIndex = undefined;
        let lastModifierAtPitch = [];
        let lastSheetNoteOnLineIndex = -1;
        let lastMeasureNumberOnLine = measureNumber;
        let measureLyricsWidth = 0;
        let lyricsX = lastX;
        let firstBeamIndex = 0;
        let lastBeamIndex = -1;
        let beamIsUpright = true;
        let tieTheta = 0.14 * Math.PI;

        for (; index < sheetNotes.length; index++) {
            let sheetNote = sheetNotes[index];

            if (doRender) {
                //  don't render lines if you aren't suppose to
                if (sheetNote.line !== line)
                    break;
                //  remember where we're at
                lastSheetNoteOnLineIndex = index;
            }

            //  compute the required resources for display
            let noteImg = undefined;
            let noteIsDotted = false;
            let noteIsTied = false;
            let noteModifier = undefined;
            let chordName = undefined;
            let scaleName = undefined;
            let scaleNumber = undefined;
            let lyrics = undefined;
            let duration = 0;
            let step = undefined;

            if (sheetNote.isNote === false) {
                if (sheetNote.noteDuration < 0) {
                    //  blank chord or lyric
                    //	chord
                    chordName = keyTone.getAbsoluteScaleName(keyN, sheetNote.chordN) + sheetNote.chordModifier;
                } else {
                    //  rest
                    let noteRestDuration = noteRestDurations[sheetNote.noteDuration];
                    noteImg = noteRestDuration.noteImg;
                    duration = noteRestDuration.duration;
                }
            } else {
                //  a note
                step = getSheetNoteStep(sheetNote);
                if (step !== undefined) {
                    //	chord
                    chordName = keyTone.getAbsoluteScaleName(keyN, effectiveChordN(sheetNote.chordN, sheetNote.minorMajor)) + sheetNote.chordModifier;
                    //	pitch name
                    if (showScaleNotes) {
                        scaleName = keyTone.getAbsoluteScaleName(keyN, step.stepN);
                    }

                    // scale number
                    switch (scaleExtra) {
                        case 1:  //  scale number
                            if (sheetNote.scaleN !== 0)
                                scaleNumber = sheetNote.scaleN === 1 ? 'R' : Math.abs(sheetNote.scaleN);
                            break;
                        case 2:      //  right hand
                            if (lastString <= sheetNote.string)
                                lastRightFinger = (lastRightFinger + 1) % 2;
                            lastString = sheetNote.string;
                            scaleNumber = (lastRightFinger === 0 ? 'I' : 'M');
                            sheetNote.rh = lastRightFinger;
                            break;
                        case 3:      //  string
                            scaleNumber = 10 * (4 - sheetNote.string) //  invert the order for display!
                                    + (sheetNote.fret % 10);
                            break;
                    }

                    //	modifiers if required
                    noteModifier = keyTone.sheetNotePrefix(keyN, step.stepN);

                    //	note
                    if (sheetNote.noteDuration !== undefined) {
                        let dur = noteDurations[sheetNote.noteDuration];
                        duration = dur.duration;
                        noteImg = dur.noteImg;
                        noteIsDotted = dur.dotted;
                    }
                    noteIsTied = sheetNote.tied;
                }
            }

            sheetNote.lw = 0;
            if (showLyrics > 0 && sheetNote.lyrics !== undefined)
                lyrics = sheetNote.lyrics;

            //  compute x location if not rendering
            let firstX = (doRender ? sheetNote.x : lastX);
            switch (chordsOnScore) {
                case 2:
                default:
                    if (chordName !== undefined) {
                        if (lastChordName === undefined)
                            lastChordName = chordName;
                        else if (lastChordName !== chordName) {
                            lastChordName = chordName;
                            firstX = Math.max(firstX, lastChordX); //  slide past the prior chord name
                        } else {
                            chordName = undefined; //  no need to display
                        }
                    }
                    break;
                case 0:     //  none
                    chordName = undefined;
                    break;
                case 3:     //  max
                    break;
            }


            //  display the parts
            scoreCtx.fillStyle = "black";
            let scoreLineY = lineTop + chordFontSize;
            if (sheetNote.isNote === false && sheetNote.noteDuration >= 0) {
                //  rest
                let w = Math.round(noteImg.width * 40 / noteImg.height);
                if (doRender)
                    scoreCtx.drawImage(noteImg, sheetNote.x, clefTopY + 1, w, 40);
                lastX = Math.max(lastX, firstX + w) + xSpace;
            } else {
                //  a note or blank

                //	chord
                // let cName = keyTone.getAbsoluteScaleName(keyN, sheetNote.chordN) + sheetNote.chordModifier;
                if (chordName !== undefined) {
                    scoreCtx.font = chordFontSize + "px Arial";
                    if (doRender)
                        scoreCtx.fillText(chordName, sheetNote.x, scoreLineY);
                    lastChordX = firstX + scoreCtx.measureText(chordName).width + xSpace;
                    lastX = Math.max(lastX, lastChordX);
                }
                scoreLineY += chordFontSize + chordYGap;

                //  lyrics
                if (lyrics !== undefined && lyrics.length > 0) {
                    lyrics = lyrics.trim() + ' ';
                    scoreCtx.font = 3 / 2 * scoreLineHeight + "px Arial";
                    let clipLength = Math.max(10 * xSpace, lastX - firstX);
                    if (doRender) {
                        if (showLyrics === 1) {
                            scoreCtx.save();
                            scoreCtx.rect(sheetNote.lx, scoreLineY - 2 * scoreLineHeight, clipLength, 3 * scoreLineHeight); /**/
                            scoreCtx.clip();
                        }
                        scoreCtx.fillText(lyrics, sheetNote.lx, scoreLineY);
                        if (showLyrics === 1)
                            scoreCtx.restore();
                    }
                    let mt = scoreCtx.measureText(lyrics).width;
                    let lw = showLyrics === 1 ? clipLength : mt;
                    measureLyricsWidth += lw + xSpace;
                    sheetNote.lw = lw;
                    lyricsX += lw + xSpace;
                    //lastX = Math.max(lastX, firstX + (showLyrics === 1 ? Math.min(clipLength, mt) : mt) + xSpace);
                }
                if (showLyrics > 0)
                    scoreLineY += 2 * scoreLineHeight;
                if (sheetNote.isNote) {
                    // a note

                    //	pitch name
                    if (showScaleNotes) {
                        scoreCtx.fillStyle = scoreColorByPitch(step.stepN);
                        scoreCtx.font = 3 / 2 * scoreLineHeight + "px Arial";
                        if (doRender)
                            scoreCtx.fillText(scaleName, sheetNote.x, scoreLineY);
                        lastX = Math.max(lastX, firstX + scoreCtx.measureText(scaleName).width);
                        scoreLineY += 2 * scoreLineHeight + 2;
                        scoreCtx.fillStyle = "black";
                    }

                    // scale number
                    if (scaleNumber !== undefined) {
                        scoreCtx.font = 3 / 2 * scoreLineHeight + "px Arial";
                        if (doRender)
                            scoreCtx.fillText(scaleNumber, sheetNote.x, scoreLineY);
                        lastX = Math.max(lastX, firstX + scoreCtx.measureText(scaleNumber).width);
                    }
                    if (scaleExtra > 0)
                        scoreLineY += 2 * scoreLineHeight + 2;
                    let scaleN = keyTone.mapPitchToNote(keyN, step.stepN);
                    let offset = 5;
                    y = clefBottomY
                            - scaleN * scoreLineHeight / 2
                            + offset;

                    //	modifier if required
                    {
                        let x = firstX;
                        let modW = 0;
//                        {    //  center if other elements are long in x
//                            let w = Math.ceil(lastX - firstX);
//                            if (w > 3 * scoreLineHeight) {
//                                x = (lastX + 2 * firstX) / 3;
//                            }
//                        }

                        //  show modifier if different from last at the given note
                        if (noteModifier !== lastModifierAtPitch[scaleN]) {
                            lastModifierAtPitch[scaleN] = noteModifier;
                            //x += xSpace;
                            let m = keyTone.getAbsoluteScaleModifier(keyN, step.stepN);
                            if (m.length === 0)
                                m = naturalSign;
                            scoreCtx.font = 2 * scoreLineHeight + "px Arial";

                            if (doRender)
                                scoreCtx.fillText(m, sheetNote.x, y);
                            modW = scoreCtx.measureText(m).width;
                            x += modW;
                        }
                        sheetNote.modW = modW;

                        //	note
                        let width = 2 * scoreLineHeight;
                        let dotWidth = 11;          //  fixme!!!!!!!!!!!!!!!!!!!!!!
                        if (noteIsDotted) {
                            width += xSpace + dotWidth;
                        }

                        //  beam notes in the same beat
                        let isUpright = scaleN < 6;       //  default
                        if (doRender
                                //&& !noteIsTied
                                && duration < 0.25)
                        {
                            let b = sheetNote.b;
                            if (lastBeamIndex < index) {
                                firstBeamIndex = lastBeamIndex = index;
                                beamIsUpright = isUpright;
                                {
                                    let durationSum = 0;
                                    for (let i = index + 1; i < sheetNotes.length; i++) {
                                        let nextNote = sheetNotes[i];
                                        let nextDur = noteDurations[nextNote.noteDuration];
                                        durationSum += nextDur.duration;

                                        if (!nextNote.isNote
                                                || durationSum >= 0.25 - 0.0001
                                                // || nextNote.tied
                                                || nextDur.duration > 0.125
                                                || b !== nextNote.b
                                                || Math.abs(step.stepN - getSheetNoteStep(nextNote).stepN) > 9
                                                )
                                            break;
                                        lastBeamIndex = i;
                                    }
                                }

                                if (lastBeamIndex > firstBeamIndex)
                                {
                                    //  beam some notes
                                    let nextNote = sheetNotes[lastBeamIndex];
                                    let beamYoff = scaleN < 6 ? -32 : 24;
                                    let beamXoff = (scaleN < 6 ? 13.5 : 5);
                                    let beamX = sheetNote.x + sheetNote.modW + beamXoff;
                                    let beamY = y + beamYoff - 2;
                                    let beamLastY = clefBottomY + nextNote.y + beamYoff - 2;

                                    //  compute the line the beam is on
                                    let slope = -(nextNote.y - sheetNote.y) / (nextNote.x + nextNote.modW - (sheetNote.x + sheetNote.modW));

                                    //  elevate the beam if any interim notes are too close
                                    for (let i = firstBeamIndex + 1; i < lastBeamIndex; i++) {
                                        let biNote = sheetNotes[i];
                                        let by = beamY - slope * (biNote.x - sheetNote.x);
                                        let dy = (clefBottomY + beamYoff + biNote.y - 2) - (beamY - slope * (biNote.x - sheetNote.x));
                                        dy = isUpright ? Math.min(0, dy) : Math.max(0, dy);
                                        //log( "interim: "+i+": by:"+by+", notey:"+(clefBottomY + beamYoff + biNote.y-2)+",
                                        // beamY: "+beamY+", beamLastY: "+beamLastY+", dy: "+dy);
                                        beamY += dy;
                                        beamLastY += dy;
                                    }

                                    //  draw the top beam
                                    scoreCtx.strokeStyle = 'black';
                                    scoreCtx.lineWidth = 4;
                                    scoreCtx.beginPath();
                                    scoreCtx.moveTo(beamX - 1, beamY);
                                    scoreCtx.lineTo(nextNote.x + nextNote.width - (scaleN < 6 ? 10 : 18), beamLastY);
                                    scoreCtx.stroke();

                                    //  double beam 1/16th notes
                                    let lastNoteDuration = 0;
                                    for (let i = firstBeamIndex; i <= lastBeamIndex; i++) {
                                        let bNote = sheetNotes[i];
                                        if (bNote.noteDuration !== 4) {
                                            lastNoteDuration = bNote.noteDuration;
                                            continue;
                                        }
                                        let bEndX = 0;
                                        if (i < lastBeamIndex) {
                                            let nNote = sheetNotes[i + 1];
                                            if (nNote.noteDuration !== 4 && lastNoteDuration === 4) {
                                                //  next note is not a 1/16
                                                continue;
                                            }
                                            if (nNote.noteDuration === 4)
                                                bEndX = nNote.x + nNote.modW;
                                            else if (lastNoteDuration === 4)
                                                bEndX = bNote.x + bNote.modW;
                                            else
                                                bEndX = bNote.x + bNote.modW + bNote.width / 2;

                                        } else {
                                            let pNote = sheetNotes[i - 1];
                                            bEndX = (pNote.noteDuration === 4
                                                    ? pNote.x + pNote.modW
                                                    : bNote.x + bNote.modW - bNote.width / 2
                                                    );
                                        }

                                        scoreCtx.beginPath();
                                        scoreCtx.moveTo(bNote.x + bNote.modW + beamXoff,
                                                beamY - slope * (bNote.x + bNote.modW - (sheetNote.x + sheetNote.modW))
                                                + (isUpright ? 8 : -8));
                                        scoreCtx.lineTo(bEndX + beamXoff, beamY - slope * (bEndX - (sheetNote.x + sheetNote.modW))
                                                + (isUpright ? 8 : -8));
                                        scoreCtx.stroke();

                                        lastNoteDuration = bNote.noteDuration;
                                    }

                                    //  extend staffs
                                    scoreCtx.lineWidth = 1;
                                    for (let i = firstBeamIndex; i <= lastBeamIndex; i++) {
                                        let bNote = sheetNotes[i];
                                        scoreCtx.beginPath();
                                        let bx = bNote.x + bNote.modW + beamXoff;
                                        let by = clefBottomY + beamYoff + bNote.y + (isUpright ? 2 : -2);
                                        scoreCtx.moveTo(bx, by);
                                        scoreCtx.lineTo(bx, beamY - slope * (bNote.x - sheetNote.x));
                                        scoreCtx.stroke();
                                    }
                                }
                            }
                            if (lastBeamIndex > firstBeamIndex && lastBeamIndex >= index) {
                                noteImg = noteDurations[2].noteImg;     //  quarter note
                                isUpright = beamIsUpright;
                            }
                        }

                        if (doRender && noteImg !== undefined)
                        {
                            if (isUpright) { //  draw note upright
                                scoreCtx.drawImage(noteImg, x, y - 3 * scoreLineHeight,
                                        2 * scoreLineHeight, 3 * scoreLineHeight);
                                if (noteIsDotted) {
                                    scoreCtx.font = 1.5 * scoreLineHeight + "px Arial";
                                    scoreCtx.fillText("\u25cf", x + width - 2 * xSpace - dotWidth, y);
                                }
                                //  draw tie
                                if (noteIsTied && index < sheetNotes.length) {
                                    let nextNote = sheetNotes[index + 1];
                                    let tieStart = sheetNote.x + sheetNote.modW + (sheetNote.width - sheetNote.modW) / 2;
                                    let tieEnd = (sheetNote.line === nextNote.line
                                            ? nextNote.x + nextNote.modW + (nextNote.width - nextNote.modW) / 4     //  fixme!!!!!!!!!!!!
                                            : scoreCtx.canvas.width + (scoreCtx.canvas.width - tieStart));
                                    let r = (tieEnd - tieStart) / (2 * Math.sin(tieTheta));
                                    scoreCtx.strokeStyle = 'black';
                                    scoreCtx.lineWidth = 1.5;
                                    scoreCtx.beginPath();
                                    scoreCtx.arc((tieStart + tieEnd) / 2,
                                            y - r * Math.cos(tieTheta) + 4,
                                            r, 0.5 * Math.PI - tieTheta, 0.5 * Math.PI + tieTheta);
                                    scoreCtx.stroke();
                                }
                            } else
                            {	 //	invert some notes that are too high, i.e. draw them upside down
                                scoreCtx.save();
                                scoreCtx.translate(x, y);
                                scoreCtx.rotate(-Math.PI);
                                scoreCtx.textAlign = "right";
                                scoreCtx.drawImage(noteImg,
                                        -3 / 2 * scoreLineHeight,
                                        -3 * scoreLineHeight + (scoreLineHeight + offset) / 2 + 1, 2 * scoreLineHeight,
                                        3 * scoreLineHeight);
                                scoreCtx.restore();
                                if (noteIsDotted) {
                                    scoreCtx.fillText("\u25cf", x + width - 2 * xSpace - dotWidth, y);
                                }
                                //  draw tie
                                if (noteIsTied && index < sheetNotes.length) {
                                    let nextNote = sheetNotes[index + 1];
                                    let tieStart = sheetNote.x + sheetNote.modW + (sheetNote.width - sheetNote.modW) / 2;
                                    let tieEnd = (sheetNote.line === nextNote.line
                                            ? nextNote.x + nextNote.modW + (nextNote.width - nextNote.modW) / 4     //  fixme!!!!!!!!!!!!
                                            : scoreCtx.canvas.width + (scoreCtx.canvas.width - tieStart));
                                    let r = (tieEnd - tieStart) / (2 * Math.sin(tieTheta));
                                    scoreCtx.strokeStyle = 'black';
                                    scoreCtx.lineWidth = 1.5;
                                    scoreCtx.beginPath();
                                    scoreCtx.arc((tieStart + tieEnd) / 2,
                                            y + r * Math.cos(tieTheta) - 4 - 8,
                                            r, -0.5 * Math.PI - tieTheta, -0.5 * Math.PI + tieTheta);
                                    scoreCtx.stroke();
                                }
                            }


                            //	render lines above/below staff
                            scoreCtx.strokeStyle = 'black';
                            scoreCtx.lineWidth = 1;
                            if (y > clefBottomY) {
                                scoreHLine(x, x + width, clefBottomY);
                            } else if (y <= clefTopY - scoreLineHeight / 2) {
                                for (let yLine = clefTopY - 3 * scoreLineHeight; yLine < clefTopY; yLine += scoreLineHeight)
                                    if (y <= yLine + scoreLineHeight) {
                                        scoreHLine(x, x + width, yLine);
                                    }
                            }
                        }
                        lastX = Math.max(lastX, x + width);
                    }
                }
            }

            if (!doRender)
            {
                sheetNote.mc = measureCount;

                //  find the max x per beat in the measure
                let b = Math.floor((measureCount + 0.001) * beatsPerBar);
                b = b % beatsPerBar;

                if (b !== lastBeat) {
                    lastBeat = b;
                }

                sheetNote.x = Math.floor(firstX);   //  could be corrected below
                sheetNote.y = y - clefBottomY;      //  y is stored relative so other lines can be selected
                sheetNote.width = Math.max(Math.ceil(lastX - firstX), xSpace);
                sheetNote.lastX = Math.max(sheetNote.x + sheetNote.width, lyricsX);  //  house keeping, can change
                sheetNote.line = line;              //  could be corrected in subsequent call
                sheetNote.m = measureNumber;
                sheetNote.b = b;

                if (duration > 0)
                    maxWidthPerBar = Math.max(maxWidthPerBar, sheetNote.width / duration);

                //log("\tsn: " + index + ", line: " + sheetNote.line + ", m: " + sheetNote.m + " = b" + sheetNote.b + ", x= " + sheetNote.x + ", w= " + sheetNote.width);
            }

            measureCount += duration;
            beatCount += duration;

            //  measure bar
            if (measureCount >= 0.999 * (beatsPerBar / 4)) {

                if (!doRender)
                {
                    //  spread the measure in x to fit the x width of the widest beat
                    let i = index;
                    while (i >= 0 && sheetNotes[i].m === measureNumber)
                        i--;
                    i++;
                    let mx = sheetNotes[i].x;       //  measure's first x
                    let barW = Math.max(maxWidthPerBar, measureLyricsWidth);
                    measureLyricsWidth = 0;

                    lastX = mx + barW;
                    sheetNote.lastX = lastX;    //  likely adjusted later

                    maxMeasureW = Math.max(maxMeasureW, barW);  //  max measure currently on this line
                }

                measureCount = 0;
                maxWidthPerBar = 0;
                measureNumber++;
                measuresInTheLine++;
                lastX = Math.max(lastX, lastChordX); //  chords don't cross bars
                lastModifierAtPitch = [];

                lastX = Math.ceil(lastX); //  move to integer location
                lyricsX = sheetNote.lastX;

                if (doRender) {
                    //  render measure bars

                    let x = sheetNote.lastX - xSpace;
                    //log("bar line: "+line+" sn: "+index+" : lastX: "+x);
                    scoreCtx.strokeStyle = 'black';
                    scoreCtx.lineWidth = 1;
                    scoreCtx.beginPath();
                    scoreCtx.moveTo(x, clefTopY);
                    scoreCtx.lineTo(x, clefBottomY - scoreLineHeight);
                    scoreCtx.stroke();
                    if (showScaleNotes) {
                        scoreCtx.beginPath();
                        scoreCtx.moveTo(x, lineTop + 3 * scoreLineHeight);
                        scoreCtx.lineTo(x, lineTop + 5 * scoreLineHeight);
                        scoreCtx.stroke();
                    }

                    //  subsequent measure number
                    scoreCtx.font = scoreLineHeight + "px Arial";
                    scoreCtx.fillText(measureNumber, x + xSpace, clefTopY - scoreLineHeight / 2);
                    //  no x calculation, assume measure number width is always less than the measure
                } else {
                    //  !doRender
                    lastX += xSpace;

                }
                eighthsPlayedIndex = undefined;


                switch (chordsOnScore) {
                    case 2:
                    default:
                        lastChordName = undefined; //  force a chord at every bar for max chords
                        break;
                    case 0:
                    case 1:
                        break;
                }

                //  see if we're out of x space for this line
                if (!doRender && Math.max(lastX, lineXStart + maxMeasureW * measuresInTheLine) >= myScore.width - xSpace) {
                    measuresInTheLine--;    //  went too far
                    break;
                }
                lastSheetNoteOnLineIndex = index;
                lastMeasureNumberOnLine = measureNumber - 1;    //  measure number is one ahead at this point
                lastMaxMeasureW = maxMeasureW;
            }

            //  see if we're out of x space for this line
            if (!doRender && lastX >= myScore.width - xSpace) {
                break;
            }
        }

//        if (lastSheetNoteOnLineIndex !== undefined)
//        {
//            let sn = sheetNotes[lastSheetNoteOnLineIndex];
//            log("lastSheetNoteOnLineIndex: " + lastSheetNoteOnLineIndex + ", m: " + sn.m + " b: " + sn.b+", lastX: "+sn.lastX+"/"+myScore.width);
//        }

        //  spread the bars to fill the line
        if (!doRender && sheetNotes.length > 0) {
            if (lastSheetNoteOnLineIndex >= 0)
            {
                //  spread the measure in x to fit the x width of the widest beat
                let i = sheetNoteN;
                let m = sheetNotes[i].m;
                let mx = lineXStart;            //  first measure's first x
                let barW = lastMaxMeasureW;     //  use the old one, prior to the space failure
                let lxBudget = undefined;
                let lastLyricsX = mx;

                lastX = mx;

                //  deal with open ended measures
                if (index >= sheetNotes.length
                        && lineXStart + maxMeasureW * (measuresInTheLine + 1) <= myScore.width - xSpace)
                    lastSheetNoteOnLineIndex = sheetNotes.length - 1;
                else if (measuresInTheLine > 0)
                    barW = (myScore.width - xSpace - lineXStart) / measuresInTheLine;


                while (i <= lastSheetNoteOnLineIndex) {

                    //  compute the x space lyrics can use in this measure
                    if (lxBudget === undefined) {
                        let w = 0;
                        for (let li = i; li <= lastSheetNoteOnLineIndex && m === sheetNotes[li].m; li++)
                            w += sheetNotes[li].lw;
                        lxBudget = Math.max(barW - w - xSpace, 0);
                    }

                    //  reposition notes to represent their duration better
                    let sheetNote = sheetNotes[i];
                    let dur = calulateSheetNoteDuration(sheetNote);
                    let bx = mx + (sheetNote.mc + dur / 2) * 4 / beatsPerBar * barW - sheetNote.width / 2;
                    bx = Math.max(bx, lastX);
                    sheetNote.x = bx;
                    lastX = Math.max(mx + (sheetNote.mc + dur) * 4 / beatsPerBar * barW, bx + sheetNote.width);
                    sheetNote.lastX = lastX;

                    //  figure where the lyrics should go... center over note but stay on budget for the measure
                    let lx = (bx + sheetNote.width / 2 - sheetNote.lw / 2);   //  desired first lyrics x
                    let lxBudgetUsed = lx - lastLyricsX;
                    if (lxBudgetUsed > 0 && lxBudget > 0) {
                        lxBudgetUsed = Math.min(lxBudgetUsed, lxBudget); //  don't over spend
                        sheetNote.lx = lastLyricsX + lxBudgetUsed;
                        lxBudget -= lxBudgetUsed;
                    } else
                        sheetNote.lx = lastLyricsX;  //  forced fit
                    lastLyricsX = sheetNote.lx + sheetNote.lw;

                    //log("line: "+line+" sn: "+i+" : mx: "+mx+" bx: "+bx);
                    i++;
                    if (i < sheetNotes.length && m !== sheetNotes[i].m) {
                        mx += barW;
                        lastLyricsX = mx;
                        m = sheetNotes[i].m;
                        lxBudget = undefined;
                    }
                }
            } else
            {
                //  deal with open measure on new line
                lastSheetNoteOnLineIndex = sheetNotes.length - 1;
            }
        }


        if (doRender) {
            //  deal with open ended measures
            if (sheetNotes.length > 0 && lastSheetNoteOnLineIndex >= 0 && index >= sheetNotes.length
                    && line === sheetNotes[sheetNotes.length - 1].line)
                lastSheetNoteOnLineIndex = sheetNotes.length - 1;

            if (sheetNotes.length > 0 && lastSheetNoteOnLineIndex < sheetNotes.length || isFinished) {
                //  clear partial measure written
                let lastSheetNote = sheetNotes[lastSheetNoteOnLineIndex];
                if (lastSheetNote !== undefined) {
                    let x = lastSheetNote.lastX;
                    x = Math.ceil(x) + 1 - xSpace;
                    scoreCtx.clearRect(x, lineTop, scoreCtx.canvas.width - x, scoreRowHeight);

                    //  backup the right hand finger calculation
                    for (let i = lastSheetNoteOnLineIndex; i > 0; i--) {
                        let sn = sheetNotes[i];
                        if (sn.isNote) {
                            lastRightFinger = sn.rh;
                            lastString = sn.string;
                            break;
                        }
                    }
                }
            }

            //  indicated selected
            if (sheetNoteSelectedStart !== undefined)
            {
                let snStart = sheetNotes[sheetNoteSelectedStart];
                let snEnd = sheetNotes[sheetNoteSelectedEnd];
                if (doRender && snStart.line <= line && snEnd.line >= line && !isPlaying) {
                    scoreCtx.lineWidth = "1px";
                    scoreCtx.beginPath();
                    if (sheetNoteInsert === true) {
                        scoreCtx.strokeStyle = "red";
                        let x = snStart.x;
                        if (sheetNoteSelectedStart === 0)
                            x = lineXStart;
                        else {
                            let prior = sheetNotes[sheetNoteSelectedStart - 1];
                            if (prior.line === line)
                                x = (prior.x + prior.width + snStart.x) / 2 - xSpace / 2;
                            else
                                x = lineXStart;
                        }
                        scoreCtx.rect(x, selectedTop, xSpace, selectedBottom - selectedTop);
                    } else {
                        scoreCtx.strokeStyle = "blue";
                        scoreCtx.rect(
                                snStart.line === line ? snStart.x : 0,
                                selectedTop,
                                line === snEnd.line ? snEnd.x - (snStart.line === line ? snStart.x : 0) + snEnd.width : scoreCtx.canvas.width,
                                selectedBottom - selectedTop);
                    }
                    scoreCtx.stroke();
                }
            }

            //  indicate edit position
            if (sheetNoteSelectedStart === undefined
                    && index === sheetNotes.length
                    && lastX < myScore.width
                    && !isFinished
                    ) {
                scoreCtx.strokeStyle = "red";
                scoreCtx.beginPath();
                if (sheetNotes.length === 0) {
                    scoreCtx.rect(xInitial + bassClefImg.width, selectedTop, xSpace, selectedBottom - selectedTop);
                } else {
                    let sheetNote = sheetNotes[sheetNotes.length - 1];
                    scoreCtx.rect(sheetNote.x + sheetNote.width + xSpace, selectedTop, xSpace, selectedBottom - selectedTop);
                }
                scoreCtx.stroke();
            }
        }

        measureNumber = lastMeasureNumberOnLine + 1;
        return lastSheetNoteOnLineIndex + 1;
    }

    function scoreColorByPitch(stepN)
    {
        scoreCtx.fillStyle = "black";
        if (keyTone.isPitchSharpenedOnScale(keyN, stepN))
            return "red";
        else if (keyTone.isPitchFlattedOnScale(keyN, stepN))
            return "blue";
        else if (keyTone.isPitchNaturalOnScale(keyN, stepN))
            return "green";
        return "black";
    }



    function scoreBackgroundUpdate() {
        cleanPlayedNotes();	//  fixme: too much?
        let currentIndex = undefined;
        let beat = undefined;
        let dt = undefined;
        let currentTime = audioContext.currentTime;
        if (isPlaying && sheetNotesInQueue.length > 0) {
            for (let i = 0; i < sheetNotesInQueue.length; i++)
            {
                let sq = sheetNotesInQueue[i];
                if (sq.index >= 0 && sq.time <= currentTime && currentTime <= sq.end) {
                    currentIndex = sq.index;
                }
            }

            dt = currentTime - playStartTime;
            beat = (beatsPerBar + Math.floor(dt / beatDuration) % beatsPerBar) % beatsPerBar + 1;
        }

        //  only change when required
        if (currentIndex === scoreBackgroundLastIndex && scoreBackgroundLastBeat === beat && !scoreBackgroundDirty)
            return;

        scoreBackgroundDirty = false;
        scoreBackgroundLastIndex = currentIndex;
        scoreBackgroundLastBeat = beat;

        //  fill the background with the nominal color
        scoreBackgroundCtx.fillStyle = "white";
        scoreBackgroundCtx.fillRect(0, 0, scoreBackgroundCtx.canvas.width, scoreBackgroundCtx.canvas.height);
        //  show the beat
        if (beat !== undefined) {
            if (dt >= 0)
                scoreBackgroundCtx.fillStyle = "white";
            else if (dt < -beatDuration * beatsPerBar)
                scoreBackgroundCtx.fillStyle = "orange";
            else
                scoreBackgroundCtx.fillStyle = "red";
            scoreBackgroundCtx.fillRect(0, 0, 30, 30);
            beat = (beatsPerBar + Math.floor(dt / beatDuration) % beatsPerBar) % beatsPerBar + 1;
            scoreBackgroundCtx.fillStyle = 'black';
            scoreBackgroundCtx.font = "30px Arial";
            scoreBackgroundCtx.fillText(beat, 7, 24);
        }

        //  show the loop count
        if (doLoop) {
            scoreBackgroundCtx.fillStyle = 'black';
            scoreBackgroundCtx.font = "20px Arial";
            scoreBackgroundCtx.fillText(loopCount, 37, 24);
        }

        if (currentIndex === undefined || currentIndex >= sheetNotes.length) {
            return;
        }

        let sn = sheetNotes[currentIndex];
        if (firstScoreLine > sn.line || (isPlaying && !doLoop && sn.line < maxLineCount - (canvasScoreRowMaximum - 1))) {
            firstScoreLine = sn.line;
            scoreUpdate();
        } else if (firstScoreLine < sn.line - (canvasScoreRowMaximum - 1)) {
            firstScoreLine = sn.line - (canvasScoreRowMaximum - 1);
            scoreUpdate();
        }

        //window.console.log("currentIndex: " + currentIndex + ", line: " + sn.line);
        scoreBackgroundCtx.fillStyle = "#80FF80"; //  bright green
        let y = (sn.line - firstScoreLine) * scoreRowHeight + scoreExtras * ySpace
                + (1 + (showHighNotes ? 1 : 0)) * scoreLineHeight;
        {
            let width = sn.width;
            //	mark all of the tied notes as current
            if (sn.tied !== undefined && sn.tied
                    && currentIndex < sheetNotes.length - 1)
                if (sn.line === sheetNotes[currentIndex + 1].line)
                    width = sheetNotes[currentIndex + 1].x + sheetNotes[currentIndex + 1].width - sn.x;
                else
                    width = scoreBackgroundCtx.canvas.width - sn.x;

            //	show the highlight
            scoreBackgroundCtx.fillRect(sn.x - xSpace, y, width, 4 * scoreLineHeight);
        }

        //  indicate the playing note
        if (sn.isNote) {
            chordN = sn.chordN;
            minorMajorSelectIndex = sn.minorMajorSelectIndex;
        }
        setSheetNoteSelectedIndex(currentIndex);
        bassChordUpdate();
    }

    function scoreHLine(x, x2, y) {
        scoreCtx.beginPath();
        scoreCtx.moveTo(x, y);
        scoreCtx.lineTo(x2, y);
        scoreCtx.stroke();
    }

    function keyUpdate() {
        let notes = "";
        let first = false;
        chordN = (7 * (12 - 4 + keyN)) % 12;
        chordSelect.options[(12 - 5 + chordN) % 12].selected = true;
        pitchNames = (keyN >= 0 ? sharpPitchNames : flatPitchNames);
        //  update the chord selection to follow the sharp or flat selection
        for (let i = 0; i < 12; i++) {
            chordSelect.options[i].label = pitchNames[(12 - 7 + i) % 12]; //  start with A
        }
        for (let i = 0; i <= 12; i++) {
            //  show the scale in text form
            let n = semi_majorScale[i % 12];
            if (n > 0) {
                if (first === false)
                    first = true;
                else
                    notes += ", ";
                notes += pitchNames[(7 * (12 - 4 + keyN) + i) % 12];
            }
        }
        keyScaleNotesElement.innerText = notes;
        bassChordUpdate();
        scoreComputeLinesAndUpdate();
    }

    function moveNoteSelectedBy(off) {
        stop();
        if (off === 0 || sheetNotes.length <= 0)
            return;
        if (lyricsDirty) {
            lyricsDirty = false;
            pushUndoStack();
        }
        let s = sheetNoteSelectedStart;
        if (s === undefined) {
            sheetNoteInsert = true;
            s = sheetNotes.length - 1;
            off++;
        }
        s += off;
        if (s < 0)
            s = 0;
        else if (s > sheetNotes.length - 1)
            s = undefined;
        if (sheetNoteSelectedStart !== s) {
            setSheetNoteSelectedIndex(s);
            //            sheetNoteSelectedStart = s;
            //            updateNoteSelectOptions(sheetNoteSelectedStart !== undefined
            //                    ? sheetNotes[sheetNoteSelectedStart]
            //                    : undefined);

            playSelectedStep();
            bassChordUpdate();
            updateFromDom();
        }
    }

    function decodeKeyCode(e) {
        let ctl = keysDown[17]   //  control
                || keysDown[91];    //  command

        switch (e.keyCode) {
            case 8:      //  backspace
            case 127:    //  delete
                onMyDelete();
                break;
            case 37:     //  Left
                moveNoteSelectedBy(-1);
                break;
            case 39:     //  Right
                moveNoteSelectedBy(1);
                break;
            case 13:     //  enter
                refreshNoteModifiers();
                moveNoteSelectedBy(1);
                break;
            case 38:     //  up arrow
                firstScoreLine = (firstScoreLine > 0 ? firstScoreLine - 1 : 0);
                scoreUpdate();
                break;
            case 40:     //  down arrow
                if (sheetNotes.length > 0 && firstScoreLine < sheetNotes[sheetNotes.length - 1].line)
                {
                    firstScoreLine++;
                    scoreUpdate();
                }
                break;
            case 67:    //   control C
                if (ctl && sheetNoteSelectedStart !== undefined)
                {
                    sheetNoteCopyBuffer = deepCopy(sheetNotes.slice(sheetNoteSelectedStart, sheetNoteSelectedEnd + 1));
                }
                break;
            case 86:    //   control V
                if (ctl)
                    pasteSheetNoteCopyBuffer();
                break;
            case 88:    //   control x
                if (ctl)
                {
                    if (sheetNoteSelectedStart !== undefined)
                        sheetNoteCopyBuffer = deepCopy(sheetNotes.slice(sheetNoteSelectedStart, sheetNoteSelectedEnd + 1));
                    onMyDelete();
                }
                break;
            case 90:    //   control Z
                if (ctl)
                    if (keysDown[16])
                        onMyRedo();
                    else
                        onMyUndo();
                break;
            case 32:        //  space
                //log("space: " + isPlaying);
                e.preventDefault();
                if (isPlaying)
                    stop();
                else
                    play(true);
                break;
            default:
                if (e.keyCode < 16 || e.keyCode >= 127)    //  latin only!
                    output("unsupported character: " + e.keyCode);
                return false;
        }
        return true;
    }

    function pasteSheetNoteCopyBuffer() {
        //output("paste: " + (sheetNoteSelectedEnd - sheetNoteSelectedStart + 1));
        if (sheetNoteCopyBuffer === undefined || sheetNoteCopyBuffer.length <= 0)
            return;

        if (sheetNoteSelectedStart === undefined) {
            //  add at end
            sheetNotes = deepCopy(sheetNotes.concat(sheetNoteCopyBuffer));
            sheetNoteSelectedEnd = undefined;	//  stay at end
        } else if (sheetNoteSelectedStart === 0 && sheetNoteInsert) {
            //  insert at beginning
            sheetNotes = deepCopy(sheetNoteCopyBuffer.concat(sheetNotes));
            sheetNoteSelectedEnd = sheetNoteCopyBuffer.length - 1;
        } else if (sheetNoteInsert) {
            //  insert at selected location
            sheetNotes = deepCopy(sheetNotes.slice(0, sheetNoteSelectedStart)
                    .concat(sheetNoteCopyBuffer, sheetNotes.slice(sheetNoteSelectedStart)));
            sheetNoteSelectedStart += sheetNoteCopyBuffer.length;
            sheetNoteSelectedEnd = sheetNoteSelectedStart;
        } else {
            sheetNotes = deepCopy(sheetNotes.slice(0, sheetNoteSelectedStart)
                    .concat(sheetNoteCopyBuffer, sheetNotes.slice(sheetNoteSelectedEnd + 1)));
            sheetNoteSelectedEnd = sheetNoteSelectedStart + sheetNoteCopyBuffer.length - 1;
        }

        pushUndoStack();

        scoreComputeLinesAndUpdate();
    }

    function scoreMouseWheelEvent(e) {
        wheelDelta += e.wheelDelta ? e.wheelDelta : -e.detail;
        if (wheelDelta > 25) {
            moveNoteSelectedBy(-1);
            wheelDelta = 0;
        } else if (wheelDelta < -25) {
            moveNoteSelectedBy(1);
            wheelDelta = 0;
        }
        if (sheetNoteSelectedStart !== undefined
                && sheetNoteSelectedStart > 0) {
            e.preventDefault();
            e.stopPropagation();
        }
        // window.console.log("sheetNoteSelected: " + sheetNoteSelectedStart + "/" + sheetNoteSelectedEnd);
    }

    function scoreTouchMoveEvent(e) {
        let delta = 0;
    }

    function resizeScoreLayers() {
        let w = Math.max(1100, myScoreLayers.scrollWidth);
        let h = myScoreLayers.scrollHeight;//	can vanish!!!!!!!!!!!!!!!!!!!!!!!!!!

        if (isFinished) {
            w = 1100;
            //	recompute max line count if required
            if (scoreCtx.canvas.width !== w) {
                scoreCtx.canvas.width = w;
                scoreCtx.canvas.height = h;
                scoreComputeLinesAndUpdate();
            }
            h = maxLineCount * scoreRowHeight;
//	    let sh = h + 'px';
//	    if (myScoreLayers.style.height !== h) {
//		myScoreLayers.style.height = h;
//		scoreBackgroundDirty = true;
//	    }
        }

//	{   //	force style height to 2 lines if not finished, whole song if so
//	    let h = (isFinished ? maxLineCount : 2) * scoreRowHeight;
//	    if (scoreCtx.canvas.height !== h)
//		scoreCtx.canvas.height = h;
//	    if (scoreBackgroundCtx.canvas.height !== h)
//		scoreBackgroundCtx.canvas.height = h;
//	    h += 'px';
//	    if (myScoreLayers.style.height !== h) {
//		myScoreLayers.style.height = h;
//		scoreBackgroundDirty = true;
//	    }
//	}

        scoreBackgroundCtx.canvas.width = w;
        scoreBackgroundCtx.canvas.height = h;
        scoreCtx.canvas.width = w;
        scoreCtx.canvas.height = h;
        scoreCtx.clearRect(0, 0, w, h);
        scoreComputeLinesAndUpdate();
    }

    function initializeBass() {
        myCanvas = document.getElementById("myCanvas");
        ctx = myCanvas.getContext("2d");
        myScoreLayers = document.getElementById("myScoreLayers");
        myScore = document.getElementById("myScore");
        scoreCtx = myScore.getContext("2d");
        scoreCtx.translate(0.5, 0.5);
        myScoreBackground = document.getElementById("myScoreBackground");
        scoreBackgroundCtx = myScoreBackground.getContext("2d");
        scoreBackgroundCtx.translate(0.5, 0.5);
        chordSelect = document.getElementById("chord");
        minorMajorSelect = document.getElementById("minorMajor");
        keySelect = document.getElementById("key");
        keyScaleNotesElement = document.getElementById("keyScaleNotes");
        outputDiv = document.getElementById("output");
        bassSelect = document.getElementById("bass");
        drumSelect = document.getElementById("drum");
        chordsSelect = document.getElementById("chords");
        countSelect = document.getElementById("count");
        countAndSelect = document.getElementById("countAnd");
        playModeSelect = document.getElementById("playMode");
        volumeSelect = document.getElementById("volume");
        bpmSelect = document.getElementById("bpm");
        beatsPerBarSelect = document.getElementById("timeSignature");
        isSwing8Select = document.getElementById("isSwing8");
        fileInput = document.getElementById('fileInput');
        lyricsSelect = document.getElementById("lyrics");
        lyricEntryText = document.getElementById("lyricEntry");
        scaleExtraSelect = document.getElementById("scaleExtra");
        chordsOnScoreSelect = document.getElementById("chordsOnScore");
        scaleNotesSelect = document.getElementById("scaleNotes");
        allScaleNotesSelect = document.getElementById("allScaleNotes");
        durationSelections = new Array(0);
        for (let i = 0; i <= 4; i++) {
            durationSelections[i] = document.getElementById("radio" + i);
        }
        isDottedSelect = document.getElementById("isDotted");
        isTiedSelect = document.getElementById("isTied");
        bluesSelect = document.getElementById("blues");
        beatsPerBluesMeasureSelect = document.getElementById("beatsPerBluesMeasure");
        customBeatDiv = document.getElementById("customBeat");
        bluesPatternSelect = document.getElementById("bluesPattern");
        leadingNoteSelect = document.getElementById("leadingNote");
        manualBluesPatternSelect = document.getElementById("manualBluesPattern");
        hiHatSelect = document.getElementById("hihat");
        swingTypeSelect = document.getElementById("swingType");


        onHiHat(hiHatSelect.value);
        onSwingType(parseInt(swingTypeSelect.value));
        onBPM();

        audioStartDelay = 0.25;

        //  circle of fifths
        circleOfFifths = new Array(0);
        for (let i = -7; i <= 7; i++) {
            let major = {};
            let sharpFlats = i;
            major.sharpFlats = sharpFlats;
            major.fifthName = (sharpFlats === 0 ? naturalSign
                    : (sharpFlats > 0 ? sharpFlats + sharpSign
                            : -sharpFlats + flatSign));
            major.pitch = 7 * (i + 24 - 4) % 12;
            if (i >= 0)
                major.name = sharpPitchNames[major.pitch];
            else
                major.name = flatPitchNames[major.pitch];
            circleOfFifths[i] = major;
            //window.console.log(major.name + " " + major.sharpFlats + " " + major.fifthName);
        }

        //  add sharps/flats decoration to key selection
        for (let i = 0; i <= 14; i++) {
            let key = circleOfFifths[i - 7];
            keySelect[i].label = key.name + " " + key.fifthName;
        }

        //  hash chords
        chords.major = semi_major;
        chords.majorScale = semi_majorScale;
        chords.pentatonic = semi_majorPentatonic;
        chords.major7 = semi_major7;
        chords.minor = semi_minor;
        chords.minorScale = semi_minor7;
        chords.minor7 = semi_major7;
        chords.minorPentatonic = semi_minorPentatonic;
        chords.dominant = semi_dominant7;
        chords.power5 = semi_power5;
        chords.augmented = semi_augmented;
        chords.augmented7 = semi_augmented7;
        chords.mixolydianScale = semi_mixolydianScale;
        chords.diminished = semi_diminished;
        chords.diminished7 = semi_diminished7;
        chords.jazz7 = semi_jazz7;
        chords.blues = semi_blues;
        chords.boogiewoogie = semi_boogiewoogie;
        chords.r5 = semi_r5;
        chords.r56 = semi_r56;
        chords.r5b7 = semi_r5b7;
        chords.mdi = semi_major7;
        chords.mdii = semi_minor7;
        chords.mdiii = semi_minor7;
        chords.mdiv = semi_major7;
        chords.mdv = semi_dominant7;
        chords.mdvi = semi_minor7;
        chords.mdvii = semi_m7b5;


        //  setup audio output
        window.AudioContext = new (window.AudioContext || window.webkitAudioContext)();
        audioContext = window.AudioContext;
        masterGain = audioContext.createGain();
        masterGain.connect(audioContext.destination);
        masterGain.gain.linearRampToValueAtTime(1, audioContext.currentTime + 0.01);
        amp = audioContext.createGain();
        amp.gain.linearRampToValueAtTime(0, audioContext.currentTime + 0.004);
        // Create the low pass filter.

        let filter = new BiquadFilterNode(audioContext, {"type": "lowpass", "frequency": 500});
        amp.connect(filter);
        filter.connect(masterGain);
        amp.gain.linearRampToValueAtTime(volume / 10, audioContext.currentTime + 0.01);
        bassSteps = new Array(0);
        for (let s = 0; s < 4; s++) {
            bassSteps.push(new Array(0));
            for (let fret = 0; fret < 13 + 4; fret++) {
                let n = s * 5 + fret;
                let bassStep = {
                    s: s,
                    fret: fret,
                    stepN: n,
                    scaleSharp: scaleSharps[n % 12], //  C scale only?
                    f: 55 * Math.pow(2, (n - 5) / 12), //  A2 is 55 hertz
                    fretX: (fretLoc(fret) + fretLoc(fret - 1)) / 2 - (fret === 0 ? pressR : 0),
                    fretY: bassFretY + bassFretHeight - s * bassFretHeight / 4 - bassFretHeight / 8
                };
                bassSteps[s].push(bassStep);
            }
        }
        bassClefImg.src = "images/bassClef.png";
        wholeNoteImg.src = "images/wholeNote.png";
        halfNoteImg.src = "images/halfNote.png";
        quarterNoteImg.src = "images/quarterNote.png";
        eighthNoteImg.src = "images/eighthNote.png";
        sixteenthNoteImg.src = "images/sixteenthNote.png";
        noteDurations = [
            //  un-dotted
            {noteImg: wholeNoteImg, duration: 1, beats: 4, dotted: false},
            {noteImg: halfNoteImg, duration: 1 / 2, beats: 2, dotted: false},
            {noteImg: quarterNoteImg, duration: 1 / 4, beats: 1, dotted: false},
            {noteImg: eighthNoteImg, duration: 1 / 8, beats: 1 / 2, dotted: false},
            {noteImg: sixteenthNoteImg, duration: 1 / 16, beats: 1 / 4, dotted: false},
            //  dotted
            {noteImg: wholeNoteImg, duration: 1, beats: 4, dotted: true}, //  placeholder
            {noteImg: halfNoteImg, duration: 3 / 4, beats: 3, dotted: true},
            {noteImg: quarterNoteImg, duration: 3 / 8, beats: 3 / 2, dotted: true},
            {noteImg: eighthNoteImg, duration: 3 / 16, beats: 3 / 4, dotted: true},
            {noteImg: sixteenthNoteImg, duration: 3 / 32, beats: 3 / 8, dotted: true}
        ];
        wholeRestImg.src = "images/wholeRest.png";
        halfRestImg.src = "images/halfRest.png";
        quarterRestImg.src = "images/quarterRest.png";
        eighthRestImg.src = "images/eighthRest.png";
        sixteenthRestImg.src = "images/sixteenthRest.png";
        noteRestDurations = [
            {noteImg: wholeRestImg, duration: 1},
            {noteImg: halfRestImg, duration: 1 / 2},
            {noteImg: quarterRestImg, duration: 1 / 4},
            {noteImg: eighthRestImg, duration: 1 / 8},
            {noteImg: sixteenthRestImg, duration: 1 / 16}];
        //  initial update
        updateFromDom();
        //  respond to window size changes
        window.addEventListener('resize', function (e) {
            resizeScoreLayers();
        }, false);
        //  initialize click responses from fretboard
        myCanvas.addEventListener('click', function (e) {
            e.stopPropagation();
            onFretClick(e.pageX - myCanvas.offsetLeft, e.pageY - myCanvas.offsetTop);
        }, false);
        //  initialize click responses from score
        myScore.addEventListener('click', function (e) {
            e.stopPropagation();
            onScoreClick(e.pageX - myScoreLayers.offsetLeft, e.pageY - myScoreLayers.offsetTop);
        }, false);
        //  force the delete key to delete the current sheet note instead of backing up into the previous webpage,
        //  losing all your work.
        window.addEventListener('keydown', function (e) {
            keysDown[e.keyCode] = e.keyCode;
            //window.console.log("window keydown: "+e.keyCode);
            if (e.keyIdentifier === 'U+0008' || e.keyIdentifier === 'Backspace') {
                if (e.target !== lyricEntryText && e.target !== manualBluesPatternSelect)
                    e.preventDefault();
            } else if (e.keyIdentifier === 'U+0009')         //  tab
                e.preventDefault();
        }, true);
        window.addEventListener('keyup',
                function (e) {
                    keysDown[e.keyCode] = false;
                },
                false);
        document.addEventListener('keydown', function (e) {
            //window.console.log("document keydown: "+e.keyCode);
            if (e.target !== lyricEntryText && e.target !== manualBluesPatternSelect)
                if (decodeKeyCode(e))
                    e.stopPropagation();
        }, true);

//        leadingNoteSelect.addEventListener('click', function (e) {
//            log('leadingNoteSelect: '+leadingNoteSelect[leadingNoteSelect.selectedIndex].valueOf().value);
//        }, true);

        myScore.addEventListener('mousewheel', scoreMouseWheelEvent, false);
        myScore.addEventListener("touchmove", scoreTouchMoveEvent, false);
        //window.addEventListener("keydown",
        //    function(e){
        //        keys[e.keyCode] = e.keyCode;
        //        let keysArray = getNumberArray(keys);
        //        document.body.innerHTML = "Keys currently pressed:" + keysArray;
        //        if(keysArray.toString() == "17,65"){
        //            document.body.innerHTML += " Select all!"
        //        }
        //    },
        //false);
        //
        //

        //  assure completion of initial score
        bassClefImg.onload = function () {
            scoreComputeLinesAndUpdate();
        };
        chordGain = audioContext.createGain();
        chordGain.gain.setTargetAtTime(1, 0, 1);
        chordGain.connect(masterGain);
        drumGain = audioContext.createGain();
        drumGain.gain.setTargetAtTime(1, 0, 1);
        drumGain.connect(masterGain);
        countGain = audioContext.createGain();
        countGain.gain.setTargetAtTime(0.20, 0, 1);
        countGain.connect(masterGain);

        //  read the drum sounds
        fetchAudioData('images/snare_4405.mp3', snare1SoundBuffer);
        fetchAudioData('images/snare_4406.mp3', snare2SoundBuffer);
        fetchAudioData('images/kick_4516.mp3', kickSoundBuffer);
        fetchAudioData('images/hihat3.mp3', hiHat3SoundBuffer);
        fetchAudioData('images/hihat1.mp3', hiHat1SoundBuffer);

        fetchAudioData('images/count1.mp3', count1SoundBuffer);
        fetchAudioData('images/count2.mp3', count2SoundBuffer);
        fetchAudioData('images/count3.mp3', count3SoundBuffer);
        fetchAudioData('images/count4.mp3', count4SoundBuffer);
        fetchAudioData('images/countAnd.mp3', countAndSoundBuffer);



        /*
         *  15 16 17 18 19 20 20 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39
         *  (0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24)
         *  (g g#  a a#  b  c c#  d d#  e  f f#  g g#  a a#  b  c c# d  d#  e  f f#  g
         *  10 11 12 13 14
         *   5  6  7  8  9
         *   0  1  2  3  4
         */
        for (let i = 0; i < stepMax; i++) {
            loadMp3AudioData(i.toString());
        }
        for (let i = 0; i <= 30; i++) {
            loadGuitarMp3AudioData(i.toString());
        }

        //    file reading
        fileInput.addEventListener('change', function (e) {
            let file = fileInput.files[0];
            title = file.name;
            let textType = /text.*/;
            if (file.type.match(textType)
                    || file.name.match(/\.bsst$/))
            {
                let reader = new FileReader();
                reader.onload = function (e) {
                    parseFile(reader.result);
                    setSheetNoteSelectedIndex(0);
                    fileUpdate();
                    pushUndoStack();
                    sheetNoteInsert = false;
                    lastFileNameRead = file.name;
                    document.title = "Bass" + (lastFileNameRead === undefined ? "" : ": " + lastFileNameRead.replace(".txt", "").replace(".bsst", ""));
                };
                reader.readAsText(file);
            }
        });

        //  initial update
        onBeatsSelect();
        keyUpdate();
        chordSelect.options[10].selected = true; //  force the initial chord to be G
        updateFromDom();
        resizeScoreLayers();
    }

    function fetchAudioData(path, obj) {
        return fetch(path).then(function (response) {
            return response.arrayBuffer();
        }).then(function (buffer) {
            audioContext.decodeAudioData(buffer, function (decodedData) {
                obj.audioData = decodedData;
            });
        });
    }


    function onUnLoad() {
        return (
                lastWrittenUndoSheetNotesStackCount === undoSheetNotesStackCount
                ? undefined
                : "There are " + (undoSheetNotesStackCount - lastWrittenUndoSheetNotesStackCount) + " un-saved changes to your song!"
                );
    }

    let loadMp3AudioData = function (index) {
        // Async
        loadMp3("bass_", index, function (buffer) {
            bassMp3s[index] = buffer;
        });
    };
    let loadGuitarMp3AudioData = function (index) {
        // Async
        loadMp3("guitar_", index, function (buffer) {
            guitarMp3s[index] = buffer;
        });
    };
    function loadMp3(prefix, index, cb) {
        let req = new XMLHttpRequest();
        let url = "images/" + prefix + index + ".mp3";
        req.open('GET', url, true);
        // XHR2
        req.responseType = 'arraybuffer';
        req.onload = function () {
            audioContext.decodeAudioData(req.response, cb);
        };
        req.send();
    }

    function stringifyFile() {
        //  strip the data that will be re-calculated on load
        let fileNotes = new Array(0);
        for (let i = 0; i < sheetNotes.length; i++) {
            let note = sheetNotes[i];
            delete note.line;
            delete note.m;
            delete note.mc;
            delete note.x;
            delete note.width;
            delete note.lastX;
            delete note.b;
            delete note.lw;
            delete note.lx;
            delete note.modW;
            delete note.y;
            fileNotes.push(note);
        }
        let all = {
            warning: "File generated by Robert Steele's Bass Study Tool.  Any modifications by hand are likely to be wrong.",
            version: "0.0",
            keyN: keyN,
            beatsPerBar: beatsPerBar,
            bpm: bpm,
            isSwing8: isSwing8,
            hiHatRhythm: hiHatRhythm,
            swingType: swingType,
            sheetNotes: fileNotes
        };
        return JSON.stringify(all).replace(/{/g, "\n{").replace(/],/g, "],\n");
    }

    function parseFile(result) {
        try {
            let all = JSON.parse(result);
            keyN = all.keyN;
            beatsPerBar = all.beatsPerBar;
            sheetNotes = all.sheetNotes;
            isSwing8 = (all.isSwing8 === undefined ? false : all.isSwing8);
            isSwing8Select.checked = isSwing8;

            if (all.bpm !== undefined && all.bpm > 40 && all.bpm <= 200) {
                bpm = all.bpm;
                for (let i = 0; i < bpmSelect.length; i++) {
                    if (parseInt(bpmSelect[i].valueOf().value) === bpm) {
                        bpmSelect[i].selected = true;
                        break;
                    }
                }
            }
            if (all.hiHatRhythm !== undefined && all.hiHatRhythm.length > 0) {
                hiHatRhythm = all.hiHatRhythm;
                for (let i = 0; i < hiHatSelect.length; i++) {
                    if (hiHatSelect[i].valueOf().value === hiHatRhythm) {
                        hiHatSelect[i].selected = true;
                        break;
                    }
                }
            } else {
                hiHatRhythm = "";
                hiHatSelect[0].selected = true;
            }
            if (all.swingType !== undefined && all.swingType >= 1 && all.swingType <= 3) {
                swingType = all.swingType;
                for (let i = 0; i < swingTypeSelect.length; i++) {
                    if (parseInt(bpmSelect[i].valueOf().value) === swingType) {
                        swingTypeSelect[i].selected = true;
                        break;
                    }
                }
            } else {
                swingType = 2;
                swingTypeSelect[1].selected = true;
            }

            //  adjust for old file formats
            for (let i = 0; i < sheetNotes.length; i++) {
                let sn = sheetNotes[i];
                if (sn.isNote && sn.minorMajor === undefined && sn.minorMajorSelectIndex !== undefined)
                    sn.minorMajor = minorMajorSelect.item(sn.minorMajorSelectIndex).valueOf().value;
            }
            output("parsed: " + result.length + " bytes");
        } catch (err) {
            output("parse error: " + err.message);
        }

    }

    function fileUpdate()
    {
        if (keyN < -7)
            keyN = -7;
        else if (keyN > 7)
            keyN = 7;
        keySelect.options[keyN - -7].selected = true;
        switch (beatsPerBar) {
            default:
                beatsPerBar = 4;
            case 2:
            case 3:
            case 4:
                beatsPerBarSelect.options[beatsPerBar - 2].selected = true;
                break;
        }
        isSwing8Select.checked = isSwing8;
        keyUpdate();
    }

    //  translate from dom objects to javascript variables
    function onChord() {
        if (sheetNoteSelectedStart < sheetNoteSelectedEnd) {
            //  set chord select on multiple notes
            chordN = parseInt(chordSelect.item(chordSelect.selectedIndex).valueOf().value);
            minorMajorSelectIndex = minorMajorSelect.selectedIndex;
            chordModifierUpdate();
            for (let i = sheetNoteSelectedStart; i <= sheetNoteSelectedEnd; i++) {
                //log("onChord: " + i);
                sheetNotes[i].chordN = chordN;
                sheetNotes[i].chordModifier = chordModifier;
            }
        }
        updateFromDom();
    }
    function updateFromDom() {
        chordN = parseInt(chordSelect.item(chordSelect.selectedIndex).valueOf().value);
        minorMajorSelectIndex = minorMajorSelect.selectedIndex;
        minorMajor = minorMajorSelect.item(minorMajorSelectIndex).valueOf().value;
        doPlayMode = playModeSelect.checked;
        //  read the note duration selected from the GUI
        let lastNoteDurationSelect = noteDurationSelect % 5;
        {
            noteDurationSelect = 0;
            let x = document.getElementsByName("durationSelection");
            for (let i = 0; i < x.length; i++) {
                if (x[i].checked) {
                    noteDurationSelect = i;
                    break;
                }
            }
        }
        if (lastNoteDurationSelect !== noteDurationSelect)
            isDottedSelect.checked = false;
        else if (isDottedSelect.checked)
            noteDurationSelect += 5;
        //isTiedSelect.checked = false;     //  fixme better
        showLyrics = parseInt(lyricsSelect.valueOf().value);
        scaleExtra = parseInt(scaleExtraSelect.valueOf().value);
        chordsOnScore = parseInt(chordsOnScoreSelect.valueOf().value);
        showScaleNotes = scaleNotesSelect.checked;

        //  show the chord notes on the fretboard
        bassChordUpdate();
        scoreComputeLinesAndUpdate();
    }

    function onAllScaleNotes()
    {
        showAllScaleNotes = allScaleNotesSelect.checked;

        //  show the chord notes on the fretboard
        bassChordUpdate();
    }

    function onHiHat(rhythm)
    {
        hiHatRhythm = rhythm;
    }
    function onSwingType(type)
    {
        swingType = type;
    }


    function onScoreClick(x, y) {
        wheelDelta = 0;
        let isShifted = keysDown[16];

        //  find the selected sheet note
        let foundSheetNote = undefined;
        let line = firstScoreLine + Math.floor(y / scoreRowHeight);

        for (let i = 0; i < sheetNotes.length; i++) {
            let sheetNote = sheetNotes[i];
            //  reject the wrong line
            if (sheetNote.line === line)
            {
                //  compute a minimum insert space
                let xInsertSpace = xSpace;

                //  look at the note and it's surrouning notes
                let qMin = sheetNote.x - xInsertSpace;
                if (i > 0) {
                    let prior = sheetNotes[i - 1];
                    if (prior.line === line) {
                        let minX = sheetNote.x + sheetNote.width / 2 - (prior.x + prior.width / 2);
                        qMin = sheetNote.x + sheetNote.width / 2 - (minX / 3);
                    }
                }
                let qMax = sheetNote.x + sheetNote.width + xInsertSpace;
                if (i < sheetNotes.length - 1) {
                    let next = sheetNotes[i + 1];
                    if (next.line === line) {
                        let maxX = next.x + next.width / 2 - (sheetNote.x + sheetNote.width / 2);
                        qMax = sheetNote.x + sheetNote.width / 2 + (maxX / 3);
                    }
                }
                //  figure out if in between or on the note
                if (x < qMin) {
                    if (isPlaying) {
                        playRememberedSheetNoteStart = i;
                        playRememberedSheetNoteEnd = i;
                    }
                    sheetNoteInsert = !isShifted;
                    foundSheetNote = i;
                    break;
                }
                if (x >= qMin && x <= qMax) {
                    if (isPlaying) {
                        playRememberedSheetNoteStart = i;
                        playRememberedSheetNoteEnd = i;
                    }
                    sheetNoteInsert = false; //  figure out if in between or on the note
                    foundSheetNote = i;
                    break;
                }
            }
        }

        if (isShifted && sheetNoteSelectedStart !== undefined && foundSheetNote > sheetNoteSelectedStart)
        {
            sheetNoteSelectedEnd = foundSheetNote;
        } else if (isShifted && sheetNoteSelectedEnd !== undefined && foundSheetNote < sheetNoteSelectedEnd)
        {
            sheetNoteSelectedStart = foundSheetNote;
        } else {
            sheetNoteSelectedStart = foundSheetNote;
            sheetNoteSelectedEnd = foundSheetNote;
        }
        //window.console.log("sheetNoteSelected: " + sheetNoteSelectedStart + "/" + sheetNoteSelectedEnd);

        updateNoteSelectOptions(foundSheetNote);
        updateFromDom();

        //  restart limited loop if looping
        if (isPlaying && loopStart !== undefined)
            onLimitedLoop(limitedLoopN);
    }

    function updateNoteSelectOptions(sheetNo) {
        //  push chord and note selection back into the UI
        //  note that the last will be the on clicked
        //  or the last note prior to the rest selected
        //  or the last of the sheet notes
        if (sheetNo === undefined) {
            selectedStep = undefined;
            lyricEntryText.value = null;
            return;
        }

        //  find the chord and chord modifiers here or earlier
        if (sheetNoteInsert && sheetNo > 0)
            sheetNo--;  //  on insert, use the prior note, not the current one
        let sheetNote;
        for (let sn = sheetNo; sn >= 0; sn--) {
            sheetNote = sheetNotes[sn];
            if (sheetNote.isNote || sheetNote.noteDuration < 0) {
                chordN = sheetNote.chordN;
                chordSelect.options[(chordN + 12 - 5) % 12].selected = true; //  origin at A
                for (let i = 0; i < minorMajorSelect.options.length; i++) {
                    let option = minorMajorSelect.options[i];
                    if (option.valueOf().value === sheetNote.minorMajor) {
                        option.selected = true;
                        break;
                    }
                }
                //minorMajorSelect.options[sheetNote.minorMajorSelectIndex].selected = true;
                break;
            }
        }
        sheetNote = sheetNotes[sheetNo];
        lyricEntryText.value = (sheetNote.lyrics !== undefined && sheetNoteInsert === false
                ? sheetNote.lyrics
                : null);
        lyricsDirty = false;
        if (!sheetNote.isNote && sheetNote.noteDuration >= 0) {
            selectedStep = undefined;
            return;
        }

        selectedStep = getSheetNoteStep(sheetNote);
        if (sheetNote.noteDuration >= 0) {
            noteDurationSelect = sheetNote.noteDuration;
            durationSelections[noteDurationSelect % 5].checked = true;
            isDottedSelect.checked = (sheetNote.noteDuration >= 5);
            isTiedSelect.checked = sheetNote.tied;
        }
        if (!isPlaying && sheetNote.isNote)
            playSelectedStep();
    }

    function addSemiTones(offset) {
        chordSelect.selectedIndex = (chordSelect.selectedIndex + 12 + offset % 12) % 12;
        updateFromDom();
    }

    function onKey() {
        keyN = parseInt(keySelect[keySelect.selectedIndex].valueOf().value);
        keyUpdate();
    }

    function onHalfStep(halfSteps) {
        halfSteps %= 12;
        for (let index = 0; index < sheetNotes.length; index++) {
            let sn = sheetNotes[index];
            if (!sn.isNote)
                continue;
            sn.fret += halfSteps;
            if (sn.fret < 0) {
                sn.string--;
                if (sn.string < 0) {
                    sn.string = 0;
                    sn.fret += 12; //  have to move up an octive
                } else
                    sn.fret += 5;
            }
            if (sn.string < 3 && sn.fret >= 5) {
                sn.string++;
                sn.fret -= 5;
            }
            sn.scaleN = Math.abs(chordArray[(sn.scaleN + 12 + halfSteps) % 12]);
            sn.chordN = (12 + halfSteps + sn.chordN) % 12;
        }
        chordN = (12 + halfSteps + chordN) % 12;
        keyUpdate();
        scoreComputeLinesAndUpdate();
    }

    function onPrint() {
        let tmpSheetNoteSelectedStart = sheetNoteSelectedStart;
        let tmpSheetNoteInsert = sheetNoteInsert;
        firstScoreLine = 0;
        isFinished = true;
        sheetNoteSelectedStart = undefined;
        sheetNoteInsert = true;
        resizeScoreLayers();
        let win = window.open();
        let page = "<html><head><title>" + title + "</title></head>\n";
        page += "<body><h1>" + title.replace(/_/g, ' ').replace(/\.(txt|bsst)$/, '') + "</h1>\n";
        page += "<img src='" + myScore.toDataURL() + "'/>\n";
        page += 'from bob steele\'s bass study tool: <a href="http://bsteele.com/bass">bsteele.com/bass</a>\n';
        page += '<img src="http://bsteele.com/images/runningsmall.ico" alt="bob\'s running man icon">\n';
        page += '</body></html>\n';
        //console.log( page );
        win.document.write(page);
        //win.print();
        //win.location.reload();
        isFinished = false;
        sheetNoteSelectedStart = tmpSheetNoteSelectedStart;
        sheetNoteInsert = tmpSheetNoteInsert;
        resizeScoreLayers();
        bassChordUpdate();
    }

    function onMyDelete() {
        if (sheetNotes.length === 0)
            return;
        if (sheetNoteSelectedStart === undefined)
            sheetNoteSelectedStart = sheetNotes.length - 1;

        if (sheetNoteSelectedStart !== undefined) {
            //  delete the notes
            if (sheetNoteSelectedEnd === undefined)
                sheetNotes = deepCopy(sheetNotes.slice(0, sheetNoteSelectedStart));
            else if (sheetNoteSelectedStart === 0)
                sheetNotes = deepCopy(sheetNotes.slice(sheetNoteSelectedEnd + 1));
            else
                sheetNotes = deepCopy(sheetNotes.slice(0, sheetNoteSelectedStart).concat(sheetNotes.slice(sheetNoteSelectedEnd + 1)));

            //  re-adjust the selection
            if (sheetNotes.length === 0)
                sheetNoteSelectedStart = undefined;
            else if (sheetNoteSelectedStart >= sheetNotes.length)
                sheetNoteSelectedStart = undefined;
            sheetNoteSelectedEnd = sheetNoteSelectedStart;

            //  update the text entry
            lyricEntryText.value = (sheetNoteSelectedStart === undefined ? "" : sheetNotes[sheetNoteSelectedStart].lyrics);
        } else
        {
            sheetNoteInsert = true;
            return;
        }

        pushUndoStack();
        updateFromDom();
    }


    function myDeleteAll() {
        if (sheetNotes.length === 0)
            return;
        sheetNotes = new Array(0);
        sheetNoteSelectedStart = undefined;
        sheetNoteInsert = true;
        isSwing8 = false;
        pushUndoStack();
        updateFromDom();
    }

    function calulateSheetNoteDuration(sheetNote) {
        if (sheetNote === undefined || sheetNote.noteDuration < 0)
            return 0;
        return noteDurations[sheetNote.noteDuration].duration;
    }

    function calculateNoteDurationToS(noteDuration) {
        if (noteDuration < 0)
            return 0;
        let dur = noteDurations[noteDuration].beats;
        if (dur > beatsPerBar)
            dur = beatsPerBar;
        dur *= 60 / bpm;
        return dur;
    }

    function playSelectedStep() {
//        //  too much?
//        if (selectedStep === undefined || isPlaying)
//            return;
//        let dur = calculateNoteDurationToS(noteDurationSelect);
//        let t = audioContext.currentTime + audioStartDelay;
//        let source = playStep(selectedStep.stepN, t + audioStartDelay, dur, true);
//        if (source !== undefined)
//            sheetNotesInQueue.push({index: -1, time: t, end: t + dur, source: source});
    }

    function playStep(n, t, duration, output) {
        return playBufferStep(bassMp3s, n, t, duration, output, 1);
    }
    function playGuitarStep(n, t, duration, output) {
        return playBufferStep(guitarMp3s, n, t, duration, output, 1 / 6);
    }
    function playBufferStep(buffers, n, t, duration, output, amplitude) {
        if (n === undefined)
            return undefined;
        let f = 55 * Math.pow(2, (n - 5) / 12); //  A2 is 55 hertz
        let rampDuration = Math.min(2 / f, duration / 10);
        //  a throwaway object!  by api design
        let source = audioContext.createBufferSource();
        let ramp = audioContext.createGain();
        source.onended = function () {
            ramp.disconnect();
            ramp = undefined;
        };
        source.buffer = buffers[n];
        source.connect(ramp);
        ramp.connect(amp);

        //  don't ramp it up if not playing, but go through the motion to get the timing for the display
        if (output) {
            ramp.gain.linearRampToValueAtTime(amplitude, t + 0.0005); //  rely on a smooth recording start
            ramp.gain.linearRampToValueAtTime(amplitude, t + duration - rampDuration);
            ramp.gain.linearRampToValueAtTime(0, t + duration);
        } else
            ramp.gain.linearRampToValueAtTime(0, t + 0.0005);
        source.start(t);
        source.stop(t + duration + 0.0005);
        //window.console.log(t + "\t" + step.stepN);

        return source;
    }

    function playDrumBuffer(buffer, t) {
        //  a throwaway object!  by api design
        let source = audioContext.createBufferSource();
        source.buffer = buffer;
        source.connect(drumGain);
        source.start(t);
        mp3Sources.push(source);
    }

    function playCountBuffer(buffer, t) {
        //  a throwaway object!  by api design
        let source = audioContext.createBufferSource();
        source.buffer = buffer;
        source.connect(countGain);
        source.start(t);
        //source.stop(t + 60/(bpm * 2));
        mp3Sources.push(source);
    }

    function prePlay() {
        stop();
        doBass = bassSelect.checked;
        doDrums = drumSelect.checked;
        doChords = chordsSelect.checked;
        doCount = countSelect.checked;
        doCountAnd = countAndSelect.checked;
        doPlayMode = playModeSelect.checked;
        isSwing8 = isSwing8Select.checked;
        playRememberedSheetNoteStart = sheetNoteSelectedStart;
        playRememberedSheetNoteEnd = sheetNoteSelectedEnd;
        findLastNonRestFullMeasureIndex();
        let t = audioContext.currentTime;
        amp.gain.linearRampToValueAtTime(volume / 10, t + audioStartDelay / 2);
        isPlaying = true;
        return t + audioStartDelay; //  return the start of the playback
    }

    function onLimitedLoop(loopN) {
        if (sheetNotes === undefined || sheetNotes.length <= sheetNoteSelectedStart)
            return;
        limitedLoopN = loopN;
        adjustLoopStartStopIndexes(sheetNoteSelectedStart, loopN);
        playStartEnd(true, loopStart, loopEnd);
    }

    function onBlues() {

        myDeleteAll();
        let blues = bluesSelect[bluesSelect.selectedIndex].valueOf().value;
        let encodedBeatsPerBluesMeasure = beatsPerBluesMeasureSelect[beatsPerBluesMeasureSelect.selectedIndex].valueOf().value;
        let bluesPattern = bluesPatternSelect[bluesPatternSelect.selectedIndex].valueOf().value;
        let leadingDirection = leadingNoteSelect[leadingNoteSelect.selectedIndex].valueOf().value;
        let leadingStyle = Math.floor(leadingDirection % 100);
        leadingDirection = Math.floor(leadingDirection / 100);

        {
            let v = manualBluesPatternSelect.valueOf().value;
            if (v !== undefined && v.length > 0) {
                v = v.trim();
                v = v.replace(/\s/g, '');
                v = v.replace(/[^0-9-]/g, '1');
                bluesPattern = v;
            }
        }

        //  parse the negative scale note notation
        let bluesNotes = [];
        let bluesDirection = [];
        {
            let j = 0;
            for (let i = 0; i < bluesPattern.length; j++, i++) {
                if (bluesPattern[i] === '-' && i < bluesPattern.length - 1) {
                    bluesDirection[j] = -1;
                    i++;
                    //   fixme -- ?
                    //  fixme other junk?
                } else
                    bluesDirection[j] = 1;
                bluesNotes[j] = bluesPattern[i];
            }
        }


        isSwing8 = false;
        let noteDuration = 2;
        let beatsPerBluesMeasure = 4;
        switch (encodedBeatsPerBluesMeasure) {
            case "1":
                noteDuration = 0;
                beatsPerBluesMeasure = 1;
                break;
            case "2":
                noteDuration = 1;
                beatsPerBluesMeasure = 2;
                break;
            case "4":
                noteDuration = 2;
                beatsPerBluesMeasure = 4;
                break;
            case "8":
                noteDuration = 3;
                beatsPerBluesMeasure = 8;
                break;
            case "custom":
                noteDuration = 3; //temp
                beatsPerBluesMeasure = 8; //temp
                break;
            default:
            case "8s":
                noteDuration = 3;
                beatsPerBluesMeasure = 8;
                isSwing8 = true;
                break;
        }
        isSwing8Select.checked = isSwing8;
        updateFromDom();
        let root = chordN % 12;
        for (let m = 0; m < blues.length; m++) {
            let bluesMeasure = blues[m];
            let stepN = 0;
            switch (bluesMeasure) {
                case "1":
                    break;
                case "4":
                    stepN += 5; //  half steps
                    break;
                case "5":
                    stepN += 7; //  half steps
                    break;
            }
            let bluesChordN = (root + stepN) % 12;
            let limit = beatsPerBluesMeasure;
            //  allow for boogie woogie 8 note pattern
            if (bluesNotes.length > beatsPerBluesMeasure && m < blues.length - 1 && blues[m] === blues[m + 1])
            {
                limit *= 2;
                m++;
            }

            //  write a measure (or 2)
            if (encodedBeatsPerBluesMeasure !== "custom")
            {
                //  NOT custom
                for (let noteNo = 0; noteNo < limit; noteNo++) {
                    //  chose a note
                    let n = 0;
                    let bluesIndex = 0;
                    if (limit === bluesNotes.length)      //  1/1, 2/2, 4/4, or 8/8
                        bluesIndex = noteNo;
                    else if (limit === 2 * bluesNotes.length)      //  2/1, 4/2, 8/4, or 16/8
                        bluesIndex = Math.floor(noteNo / 2);
                    else
                        bluesIndex = Math.floor(limit > 4 ? noteNo / (limit / 4) : noteNo) % bluesNotes.length;
                    n = parseInt(bluesNotes[bluesIndex]);
                    if (n >= 8) {
                        n = 12;
                    } else {
                        let n2 = undefined;
                        for (let i = 0; i < chordArray.length; i++) {
                            if (Math.abs(chordArray[i]) === n)
                            {
                                n2 = i;
                                break;
                            }
                        }
                        if (n2 === undefined) {
                            for (let i = 0; i < semi_dominant7Scale.length; i++) {
                                if (semi_dominant7Scale[i] === n)
                                {
                                    n2 = i;
                                    break;
                                }
                            }
                        }

                        //  use the note below if asked and if possible
                        if (bluesDirection[bluesIndex] < 0 && n2 + bluesChordN >= 12)
                            n2 -= 12; //  scale note below

                        n = n2;
                    }
                    if (n === undefined)
                        n = bluesChordN; //  safety only
                    else
                        n = bluesChordN + n;
                    //  chose a string
                    let s = Math.floor(n / 5);
                    if (s > 0 && n % 5 === 0)     //  don't use open strings if possible
                        s--;
                    if (s > 3)
                        s = 3;
                    n -= s * 5;
                    let bassStep = bassSteps[s][n];
                    let sheetNote = {
                        isNote: true,
                        string: bassStep.s,
                        fret: bassStep.fret,
                        noteDuration: noteDuration,
                        chordN: bluesChordN,
                        chordModifier: chordModifier,
                        minorMajor: minorMajor,
                        minorMajorSelectIndex: minorMajorSelectIndex,
                        scaleN: chordArray[(bassStep.stepN + 12 - bluesChordN) % 12],
                        lyrics: (noteNo === 0 ? romanNumerial[bluesMeasure] : undefined),
                        line: Math.floor(m / 4)
                    };
                    sheetNotes.push(sheetNote);
                }
            } else {
                //  custom

                let noteNo = 0;
                for (let beat = 0; beat < beatsPerBluesMeasure; ) {

                    //  rest until it's time for the note
                    let loc = nashville.getBeatLocation(noteNo);
                    if (loc === undefined)
                        loc = beatsPerBluesMeasure; //  past the end

                    //  fill in with rests when required
                    if (loc > beat) {
                        let r = loc - beat;
                        if (r >= 4) {
                            insertSheetNote({isNote: false, noteDuration: 1});
                            r -= 4;
                        }
                        if (r >= 2) {
                            insertSheetNote({isNote: false, noteDuration: 2});
                            r -= 2;
                        }
                        if (r >= 1) {
                            insertSheetNote({isNote: false, noteDuration: 3});
                            r -= 1;
                        }
                        beat = loc;
                        continue;
                    }

                    //  chose the note in the blues pattern
                    let n = parseInt(bluesNotes[noteNo % bluesNotes.length]);
                    //  find the duration
                    let dotted = false;
                    {
                        let d = 2 * nashville.getBeatDuration(noteNo);
                        let dMap = [0, 3, 2, 4 + 3, 1, 1];
                        if (d >= dMap.length)
                            d = dMap.length - 1; //  can't do it!
                        noteDuration = dMap[d % dMap.length];
                    }


                    if (n >= 8) {
                        n = 12; //  octive in half-steps
                    } else {
                        //  try to find the note in the selected chord
                        let n2 = undefined;
                        for (let i = 0; i < chordArray.length; i++) {
                            if (Math.abs(chordArray[i]) === n)
                            {
                                n2 = i;
                                break;
                            }
                        }
                        //  find at least some note
                        if (n2 === undefined) {
                            for (let i = 0; i < semi_dominant7Scale.length; i++) {
                                if (semi_dominant7Scale[i] === n)
                                {
                                    n2 = i;
                                    break;
                                }
                            }
                        }

                        //  use the note below if asked and if possible
                        if (bluesDirection[noteNo % bluesDirection.length] < 0 && n2 + bluesChordN >= 12)
                            n2 -= 12; //  scale note below

                        n = n2;
                    }
                    if (n === undefined)
                        n = bluesChordN; //  safety only,  the root
                    else
                        n = bluesChordN + n;
                    //  chose a string
                    let s = Math.floor(n / 5);
                    if (s > 0 && n % 5 === 0)     //  don't use open strings if possible
                        s--;
                    if (s > 3)
                        s = 3;
                    n -= s * 5;
                    let bassStep = bassSteps[s][n];
                    let sheetNote = {
                        isNote: true,
                        string: bassStep.s,
                        fret: bassStep.fret,
                        noteDuration: noteDuration,
                        dotted: dotted,
                        chordN: bluesChordN,
                        chordModifier: chordModifier,
                        minorMajor: minorMajor,
                        minorMajorSelectIndex: minorMajorSelectIndex,
                        scaleN: chordArray[(bassStep.stepN + 12 - bluesChordN) % 12],
                        lyrics: (noteNo === 0 ? romanNumerial[bluesMeasure] : undefined),
                        line: Math.floor(m / 4)
                    };
                    sheetNotes.push(sheetNote);
                    beat += 2 * nashville.getBeatDuration(noteNo);
                    noteNo++;
                }
            }
        }


        //  apply the leading notes
        scoreUpdateAllLines(false); //	compute note measure count, duration, etc.
        if (sheetNotes.length > 2)
        {
            let priorI = 0;

            for (let i = 1; i < sheetNotes.length; i++) {
                let sheetNote = sheetNotes[i];
                if (!sheetNote.isNote)
                    continue;       //  a rest or otherwise not a note
                //
                //  find the next note
                let j = (i + 1 < sheetNotes.length ? i + 1 : 0);
                let next = sheetNotes[j];
                while (!next.isNote) {
                    j = (j + 1 < sheetNotes.length ? j + 1 : 0);
                    next = sheetNotes[j];
                }

                //  adjust the last note in the measure
                if (sheetNote.m !== next.m) {
                    while (!computeLeadingNote(priorI, i, j, leadingStyle, leadingDirection))
                    {
                        switch (leadingStyle) {
                            default:
                                break;
                            case 5:
                                leadingStyle = 2;
                                continue;
                            case 2:
                                leadingStyle = 1;
                                continue;
                        }
                        break;
                    }
                }
                priorI = i;
            }
        }

        firstScoreLine = 0; //  fixme
        scoreComputeLinesAndUpdate();
        setSheetNoteSelectedIndex(0);
        pushUndoStack();
        window.location.href = "#top";
    }

    function computeLeadingNote(priorI, i, j, style, direction) {
        let prior = sheetNotes[priorI];
        let sheetNote = sheetNotes[i];
        let next = sheetNotes[j];
//        log("lead: " + i + ":  m:" + sheetNote.m + ":  mc:" + sheetNote.mc
//                + ", next: " + j + ":  m:" + next.m + ":  mc:" + next.mc);
        let string = next.string;
        let fret = next.fret;
        //let n = string * 5 + fret;
        let pn = prior.string * 5 + prior.fret;
        let nn = next.string * 5 + next.fret;
        let ld;
        let nChordArray = chords[next.minorMajor];
        let mod = false;

        switch (style)
        {
            default:
            case 0:
                break;
            case 1: //  Chromatic
                ld = direction;
                switch (ld)
                {
                    case 2:     //  closest
                        ld = (pn < nn ? 0 : 1);
                        break;
                }
                switch (ld)
                {
                    default:
                        break;
                    case 0:     //  lower
                        if (fret > 0)
                            fret--;
                        else if (string > 0) {
                            string--;
                            fret += 5 - 1;
                        } else {    //  0,0  ie low E
                            //  octave up
                            string = 2;
                            fret = 5 - 1;
                        }
                        break;
                    case 1:     //  upper
                        if (fret < 7)
                            fret++;
                        else if (string < 3) {
                            string++;
                            fret += -5 + 1;
                        } else {    //  3,7  high D
                            //  octave down
                            string -= 2;
                            fret += -5 + 1;
                        }
                        mod = true;
                        break;
                }
                break;
            case 2: //  scale
                ld = direction;
                switch (ld)
                {
                    case 2:     //  closest
                        ld = (pn < nn ? 0 : 1);
                        break;
                }
                switch (ld)
                {
                    case 0:     //  lower
                    case 1:     //  upper
                        fret += stepsToScaleNote(nChordArray, (ld === 0 ? -1 : 1));
                        if (fret < 0) {
                            if (string === 0) {
                                //  octave up
                                string += 2;
                                fret += 2;
                            } else {
                                string--;
                                fret += 5;
                            }
                        } else if (fret > 7 && string < 3) {
                            string++;
                            fret -= 5;
                        }
                        mod = true;
                        break;
                }
                break;
            case 5: //  dominant
                ld = direction;
                switch (ld)
                {
                    case 2:     //  closest
                        ld = (pn < nn ? 0 : 1);
                        break;
                }
                switch (ld)
                {
                    case 0:     //  lower
                    case 1:     //  upper
                        let steps = stepsToScaleNoteN(chordArray, 5);
                        fret += (ld === 0 ? -(12 - steps) : steps);
                        while (fret < 0) {
                            if (string === 0) {
                                //  octave up
                                string += 2;
                                fret += 2;
                            } else {
                                string--;
                                fret += 5;
                            }
                        }
                        while (fret > 7 && string < 3) {
                            string++;
                            fret -= 5;
                        }
                        mod = true;
                        break;
                }
                break;
        }
        let n = string * 5 + fret;
        if (mod
                && n !== pn
                && n !== nn
                )
        {
            sheetNote.string = string;
            sheetNote.fret = fret;
            let bassStep = bassSteps[string][fret];
            sheetNote.scaleN = chordArray[(bassStep.stepN + 12 - sheetNote.chordN) % 12];
            return true;
        }
        return false;
    }

    function stepsToScaleNote(chordArray, scaleSteps) {
        if (chordArray === undefined || scaleSteps === undefined || scaleSteps === 0)
            return 0;
        if (scaleSteps > 0)
            for (let i = 1; i < chordArray.length; i++) {
                if (chordArray[i] !== 0) {
                    scaleSteps--;
                    if (scaleSteps === 0)
                        return i;   //  half steps to nth scale note
                }
            }
        else
            for (let i = 1; i < chordArray.length; i++) {
                if (chordArray[chordArray.length - i] !== 0) {
                    scaleSteps++;
                    if (scaleSteps === 0)
                        return -i;   //  half steps to nth scale note
                }
            }
        return 0;
    }

    function stepsToScaleNoteN(chordArray, n) {
        if (chordArray === undefined || n === undefined || n === 0)
            return 0;

        for (let i = 1; i < chordArray.length; i++) {
            if (Math.abs(chordArray[i]) === n) {
                return i;   //  half steps to nth scale note
            }
        }

        return 0;
    }

    function onBeatsSelect() {
        let encodedBeatsPerBluesMeasure = beatsPerBluesMeasureSelect[beatsPerBluesMeasureSelect.selectedIndex].valueOf().value;
        switch (encodedBeatsPerBluesMeasure) {
            case "custom":
                customBeatDiv.style.display = "block";
                break;
            default:
                customBeatDiv.style.display = "none";
                break;
        }
    }

    function onVolume() {
        let v = parseInt(volumeSelect.item(volumeSelect.selectedIndex).valueOf().value);
        if (v < 0)
            v = 0;
        if (v > 10)
            v = 10;
        volume = v;
        masterGain.gain.linearRampToValueAtTime(volume / 10, audioContext.currentTime + audioStartDelay / 2);
    }

    function play(loop) {
        playStartEnd(loop, undefined, undefined);
    }

    function playStartEnd(loop, newLoopStart, newLoopEnd) {
        let t = prePlay();
        loopStart = newLoopStart;
        loopEnd = newLoopEnd;
        loopCount = 0;
        if (loop)
            doLoop = true;
        //  initial play
        let dur = playOneLoop(t);
        let internalCallback = function ()
        {
            if (!isPlaying)
                return;
            t += dur;
            let tn = audioContext.currentTime;
            if (doLoop === true) {
                dur = playOneLoop(t);

                //  compensate for the previous slip so it doesn't accumulate
                let slip = tn - (t - audioStartDelay);

                //log("slip: " + slip + ", q: " + sheetNotesInQueue.length + ", tn: " + tn);
                myTimeout = window.setTimeout(internalCallback, (dur - slip) * 1000);
            } else {
                //  clean up the end of a play
                cleanPlayedNotes();
                if (sheetNotesInQueue.length > 0) {
                    //  wait for the last note to finish on a single play
                    myTimeout = window.setTimeout(internalCallback,
                            (sheetNotesInQueue[sheetNotesInQueue.length - 1].end - tn + audioStartDelay) * 1000);
                } else {
                    isPlaying = false;
                }
            }
        };
        let dur0 = dur - audioStartDelay;
        if (dur0 < audioStartDelay)
            dur0 = audioStartDelay;
        myTimeout = window.setTimeout(internalCallback, (t + dur0 - audioContext.currentTime) * 1000);
        //log("test: " + sheetNotesInQueue.length);
    }

    function findLastNonRestFullMeasureIndex() {
        let measureCount = 0;
        nonRestLastFullMeasureIndex = undefined;
        //  join rests at the beginning with rests at the end when looping
        if (sheetNotes.length > 0
                && sheetNotes[0].isNote === false
                && sheetNotes[0].noteDuration > 0)
            for (let index = 0; index < sheetNotes.length; index++) {
                let sn = sheetNotes[index];
                let duration = 0;
                if (sn.isNote === false) {
                    if (sn.noteDuration < 0) {
                        //  blank chord or lyric
                    } else {
                        //  rest
                        let noteRestDuration = noteRestDurations[sn.noteDuration];
                        duration = noteRestDuration.duration;
                    }
                } else {
                    //	note
                    if (sn.noteDuration !== undefined) {
                        let dur = noteDurations[sn.noteDuration];
                        duration = dur.duration;
                    }
                }

                measureCount += duration;
                //  measure bar
                if (measureCount >= 0.999 * (beatsPerBar / 4)) {
                    measureCount = 0;
                    if (sn.isNote)
                        nonRestLastFullMeasureIndex = index;
                }
            }
    }

    function adjustLoopStartStopIndexes(newStart, loopN) {
        let measureCount = 0;
        let start = 0;
        let end = undefined;
        let barCount = 0;
        //  join rests at the beginning with rests at the end when looping
        if (sheetNotes.length > 0)
            for (let index = 0; index < sheetNotes.length; index++) {
                let sn = sheetNotes[index];
                let duration = 0;
                if (sn.isNote === false) {
                    if (sn.noteDuration < 0) {
                        //  blank chord or lyric
                    } else {
                        //  rest
                        let noteRestDuration = noteRestDurations[sn.noteDuration];
                        duration = noteRestDuration.duration;
                    }
                } else {
                    //	note
                    if (sn.noteDuration !== undefined) {
                        let dur = noteDurations[sn.noteDuration];
                        duration = dur.duration;
                    }
                }

                measureCount += duration;
                //  measure bar
                if (measureCount >= 0.999 * (beatsPerBar / 4)) {
                    measureCount = 0;
                    if (index < newStart && index < sheetNotes.length - 1)
                        start = index + 1;
                    if (index >= newStart && end === undefined) {
                        barCount++;
                        if (barCount === loopN) {
                            end = index;
                        }
                    }

                }
            }

        loopStart = start; //  can be undefined
        loopEnd = end; //  can be undefined
    }

    function playAtT(t0) {
        //window.console.log(t0 + "\tplayAtT(  "  + loopCount);
        beatDuration = 60.0 / bpm;
        let t = t0;
        let lastT = t;
        let index = 0;
        if (loopStart !== undefined) {
            index = loopStart;
        } else if (doPlayMode && playRememberedSheetNoteStart !== undefined)
        {
            index = playRememberedSheetNoteStart;
        }

        setSheetNoteSelectedIndex(index);
        //  drum lead in
        if (loopCount === 0 && loopStart === undefined)
            t += 2 * beatsPerBar * beatDuration;
        playStartTime = t;
        //  setup the sheet note plays
        let eighthsPlayed = 1;
        let lastMeasure = 0;
        let lastMinorMajor = undefined;
        let lastChordN = undefined;
        let lastChordModifier = undefined;
        let beatDur = 60 / bpm;
        let m = -1;
        let lastNoteWasTied = false;
        for (; index < sheetNotes.length; index++) {
            if (loopEnd !== undefined && index > loopEnd)
                break;
            if (lastNoteWasTied) {  //	skip the second tied note   //	fixme: only one tie?
                lastNoteWasTied = false;
                continue;
            }
            let sheetNote = sheetNotes[index];
            let step = getSheetNoteStep(sheetNote);
            let dur = calculateNoteDurationToS(sheetNote.noteDuration);
            if (sheetNote.tied === true
                    && index < sheetNotes.length - 1)
            {
                dur += calculateNoteDurationToS(sheetNotes[index + 1].noteDuration);
                lastNoteWasTied = true;
            }
            if (sheetNote.m !== m) {
                m = sheetNote.m;
                eighthsPlayed = 0;
            }

            //  swing if required
            if (
                    isSwing8At(index)       //  test for first note of two 8th's
                    )
            {
                let swingT = t;
                let swingDur = (6 + swingType) / 6 * dur;
                if (eighthsPlayed & 1)
                {
                    swingDur = (6 - swingType) / 6 * dur;
                    swingT += swingType / 6 * dur;
                }

                if (step !== undefined) {
                    let source = playStep(step.stepN, swingT, swingDur, doBass);
                    if (source !== undefined)
                        sheetNotesInQueue.push({index: index, time: swingT, end: swingT + swingDur, source: source});
                }
                //if (m === 1)  window.console.log("swing: " + m + ", " + swingDur);
                eighthsPlayed++;
            } else {
                //  play it straight
                if (step !== undefined) {
                    let source = playStep(step.stepN, t, dur, doBass);
                    if (source !== undefined)
                        sheetNotesInQueue.push({index: index, time: t, end: t + dur, source: source});
                } else if (!sheetNote.isNote) {
                    //  queue filler for display only
                    sheetNotesInQueue.push({index: index, time: t, end: t + dur, source: undefined});
                }
                //window.console.log("not swing: " + m);1
            }

            //  add chord notes
            if (doChords && (sheetNote.m !== lastMeasure || (sheetNote.isNote
                    && (sheetNote.minorMajor !== lastMinorMajor
                            || sheetNote.chordN !== lastChordN
                            || sheetNote.chordModifier !== lastChordModifier
                            )
                    ))) {
                lastMeasure = sheetNote.m;
                lastMinorMajor = sheetNote.minorMajor;
                lastChordN = sheetNote.chordN;
                lastChordModifier = sheetNote.chordModifier;

                //  add chord playback
                if (step !== undefined)
                {
                    //  convert from bass to guitar offsets
                    let root = step.stepN;
                    if (root + 12 + 12 < stepMax)
                        root += 12;

                    //  play the chord
                    let chordArray = chords[sheetNote.minorMajor];
                    if (chordArray !== undefined) {
//                        let noteCount = 0;
                        for (let i = 0; i < chordArray.length; i++) {
                            if (chordArray[i] > 0) {
                                //log("root: " + root + ", i: " + i + " = " + (root + i));
                                let source = playGuitarStep(root + i, t, beatsPerBar * beatDur, true);
                                if (source !== undefined)
                                    sheetNotesInQueue.push({index: index, time: t, end: t + beatsPerBar * beatDur, source: source});
//                                if ( ++noteCount >= 2 )
//                                break;//temp!!!!!!!!!!!!!!!!!!!!!!!!!
                            }
                        }
                    }
                }
            }

            t += dur;
            //   partial ends are added to the beginning on a loop
            if (!doLoop || nonRestLastFullMeasureIndex === undefined || index <= nonRestLastFullMeasureIndex)
                lastT = t;
        }

        return lastT - t0; //  play duration
    }

    function isSwing8At(index) {
        if (!isSwing8)
            return false;
        if (index === undefined || index < 0 || index >= sheetNotes.length)
            return false;
        let sheetNote = sheetNotes[index];
        if (!sheetNote.isNote)
            return false;
        if (sheetNote.noteDuration !== 3)
            return false;
        //  first note found is an 8th
        if (index + 1 >= sheetNotes.length
                || (sheetNote.m !== undefined && sheetNotes[index + 1].m !== undefined && sheetNote.m !== sheetNotes[index + 1].m))
            index--;	//  look at the previous note at the end of a measure
        else
            index++;
        let sheetNote2 = sheetNotes[index];
        if (!sheetNote2.isNote)
            return false;
        if (sheetNote2.noteDuration !== 3)
            return false;

        //  allow swing across different eighth notes
//	if (sheetNote.string !== sheetNote2.string)
//	    return false;
//	if (sheetNote.fret !== sheetNote2.fret)
//	    return false;
        return true;
    }

    function cleanPlayedNotes() {
        //  remove used notes from the play queue
        let currentTime = audioContext.currentTime;
        let spliceIndex = undefined;
        for (let i = 0; i < sheetNotesInQueue.length; i++) {
            let sq = sheetNotesInQueue[i];
            if (sq.end + audioStartDelay < currentTime) {
                if (sq.source !== undefined) {
                    //sq.source.stop();
                    sq.source.disconnect();
                }
                spliceIndex = i;
            } else
                break; //  wait for the right time
        }
        if (spliceIndex !== undefined)
            sheetNotesInQueue.splice(0, spliceIndex + 1); // remove notes from queue
    }

    function playOneLoop(t0) {
        //  clear any ending notes or beats
        for (let i = 0; i < mp3Sources.length; i++) {
            mp3Sources[i].stop(t0 + audioStartDelay); //  stop the prior loop iteration
        }
        mp3Sources.length = 0; //  clear the array of sources

        cleanPlayedNotes();
        let dur = playAtT(t0);
        loopCount++;
        {
            let t = t0;
            let limit = t0 + dur - 1e-4;
            while (t < limit) {
                switch (beatsPerBar) {
                    case 4:
                        //            for (b = 0; b < 2; b++)
                        //                playDrumBuffer(snare1SoundBuffer.audioData, t + b * beatDuration / 2);
                        if (doDrums) {
                            playDrumBuffer(kickSoundBuffer.audioData, t + 0 * beatDuration);
                            playDrumBuffer(snare2SoundBuffer.audioData, t + 1 * beatDuration);
                            playDrumBuffer(kickSoundBuffer.audioData, t + 2 * beatDuration);
                            playDrumBuffer(snare1SoundBuffer.audioData, t + 3 * beatDuration);
                        }

                        if (doCount) {
                            playCountBuffer(count1SoundBuffer.audioData, t + 0 * beatDuration);
                            playCountBuffer(count2SoundBuffer.audioData, t + 1 * beatDuration);
                            playCountBuffer(count3SoundBuffer.audioData, t + 2 * beatDuration);
                            playCountBuffer(count4SoundBuffer.audioData, t + 3 * beatDuration);
                        }
                        if (bpm <= 110 && doCountAnd)
                        {
                            playCountBuffer(countAndSoundBuffer.audioData, t + beatDuration / 2);
                            playCountBuffer(countAndSoundBuffer.audioData, t + 1 * beatDuration + beatDuration / 2);
                            playCountBuffer(countAndSoundBuffer.audioData, t + 2 * beatDuration + beatDuration / 2);
                            playCountBuffer(countAndSoundBuffer.audioData, t + 3 * beatDuration + beatDuration / 2);
                        }
                        if (hiHatRhythm !== undefined)
                            for (let i = 0; i < hiHatRhythm.length && i < 24; i++) {
                                let j = i;
                                if (isSwing8) {
                                    switch (i % 4)
                                    {
                                        case 0:
                                            //	fixme: duration lengthen
                                            break;
                                        case 4:
                                            //	1: mild, 2: medium, 3:strict
                                            j -= (3 - swingType) / 3;
                                            break;
                                    }
                                }

                                if (hiHatRhythm[i] === 'X')
                                    playDrumBuffer(hiHat3SoundBuffer.audioData, t + j * beatDuration / 6);
                                else if (hiHatRhythm[i] === 'x')
                                    playDrumBuffer(hiHat1SoundBuffer.audioData, t + j * beatDuration / 6);
                            }
                        break;
                    case 3:
                        if (doCount) {
                            playCountBuffer(count1SoundBuffer.audioData, t + 0 * beatDuration);
                            playCountBuffer(count2SoundBuffer.audioData, t + 1 * beatDuration);
                            playCountBuffer(count3SoundBuffer.audioData, t + 2 * beatDuration);
                        }
                        if (bpm <= 110 && doCountAnd)
                        {
                            playCountBuffer(countAndSoundBuffer.audioData, t + beatDuration / 2);
                            playCountBuffer(countAndSoundBuffer.audioData, t + 1 * beatDuration + beatDuration / 2);
                            playCountBuffer(countAndSoundBuffer.audioData, t + 2 * beatDuration + beatDuration / 2);
                        }
                        if (doDrums) {
                            playDrumBuffer(kickSoundBuffer.audioData, t + 0 * beatDuration);
                            playDrumBuffer(snare1SoundBuffer.audioData, t + 1 * beatDuration);
                            playDrumBuffer(snare1SoundBuffer.audioData, t + 2 * beatDuration);
                        }
                        break;
                    case 2:
                        if (doCount) {
                            playCountBuffer(count1SoundBuffer.audioData, t + 0 * beatDuration);
                            playCountBuffer(count2SoundBuffer.audioData, t + 1 * beatDuration);
                        }
                        if (bpm <= 110 && doCountAnd)
                        {
                            playCountBuffer(countAndSoundBuffer, t + beatDuration / 2);
                            playCountBuffer(countAndSoundBuffer, t + 1 * beatDuration + beatDuration / 2);
                        }
                        if (doDrums) {
                            playDrumBuffer(kickSoundBuffer.audioData, t + 0 * beatDuration);
                            playDrumBuffer(snare1SoundBuffer.audioData, t + 1 * beatDuration);
                        }
                        break;
                }

                t += beatsPerBar * beatDuration;
            }
        }
        return dur;
    }

    function stop() {
        doLoop = false;
        loopStart = undefined;
        loopEnd = undefined;
        if (isPlaying) {
            isPlaying = false; //  fixme race condition!!!!!!!
            setSheetNoteSelectedIndex(playRememberedSheetNoteStart);
        }
        if (myTimeout !== undefined) {
            clearTimeout(myTimeout);
            myTimeout = undefined;
        }
        stopTheAudio();
    }

    function stopTheAudio() {
        //amp.gain.cancelScheduledValues(0);
        //amp.gain.setValueAtTime(0, audioContext.currentTime);
        //  disconnect the queue sources and empty the queue
        if (sheetNotesInQueue.length > 0)
            for (let i = 0; i < sheetNotesInQueue.length; i++) {
                let sq = sheetNotesInQueue[i];
                let source = sq.source;
                if (source !== undefined) {
                    //source.stop();        //  safari is unhappy with this!
                    source.disconnect();
                    sq.source = undefined;
                }
            }
        sheetNotesInQueue.length = 0;
        if (mp3Sources.length > 0)
            for (let i = 0; i < mp3Sources.length; i++)
                mp3Sources[i].stop();
        mp3Sources.length = 0;
        //amp.gain.linearRampToValueAtTime(volume / 10, audioContext.currentTime + audioStartDelay);
    }

    function output(text) {
        outputDiv.innerHTML = text;
    }

    function myReadFileStart() {
        //  cheap fix:  null the file value so the "change" event will occur even if the same file is chosen.
        fileInput.value = null;
    }

    function mySaveAs() {
        let data = new Blob([stringifyFile()], {type: 'text/plain'});
        // If we are replacing a previously generated file we need to
        // manually revoke the object URL to avoid memory leaks.
        if (textFile !== null) {
            window.URL.revokeObjectURL(textFile);
        }

        textFile = window.URL.createObjectURL(data);
        if (downloadlink === null) {
            downloadlink = document.createElement("a");
            downloadlink.style = "display:none";
            downloadlink.download = (lastFileNameRead === undefined ? 'song.bsst' : lastFileNameRead.replace(".txt", ".bsst"));
            ;
        }
        downloadlink.href = textFile;
        downloadlink.click();
        lastWrittenUndoSheetNotesStackCount = undoSheetNotesStackCount;
    }


    function myOnTimeSignature(bpb) {
        switch (bpb) {
            case 2:
            case 3:
            case 4:
                beatsPerBar = bpb;
                stop();
                scoreComputeLinesAndUpdate();
                break;
        }
    }

    function setSheetNoteSelectedIndex(index) {
        if (index < 0 || sheetNotes.length === 0 || index >= sheetNotes.length)
            index = undefined;
        //  we don't know why the selection was made so update everything
        sheetNoteSelectedStart = index;
        sheetNoteSelectedEnd = index;
        if (sheetNotes.length > 0) {
            updateNoteSelectOptions(sheetNoteSelectedStart);
            let i = index === undefined ? sheetNotes.length - 1 : index;
            let line = sheetNotes[i].line;
            if (firstScoreLine > line || (isPlaying && !doLoop && line < maxLineCount - (canvasScoreRowMaximum - 1)))
                firstScoreLine = line;
            else if (firstScoreLine < line - (canvasScoreRowMaximum - 1))   //  keep the selected line in view
                firstScoreLine = line - (canvasScoreRowMaximum - 1);
            //window.console.log("firstScoreLine = " + firstScoreLine + ", line=" + line + ", m=" + canvasScoreRowMaximum);
        }
        scoreUpdate();
    }


    function onBPM() {
        bpm = parseInt(bpmSelect.item(bpmSelect.selectedIndex).valueOf().value);
    }

    function log(text) {
        window.console.log(text);
    }



    return {
        onLoad: function () {
            initializeBass();
        },
        onUnLoad: function () {
            return onUnLoad();
        },
        onChord: function () {
            onChord();
        },
        onRestClick: function (restN) {
            onMyRestClick(restN);
        },
        onDelete: function () {
            onMyDelete();
        },
        onUndo: function () {
            onMyUndo();
        },
        onRedo: function () {
            onMyRedo();
        },
        onPlay: function () {
            play(false);
        },
        onLoop: function () {
            play(true);
        },
        onStop: function () {
            stop();
        },
        onBPM: function () {
            onBPM();
        },
        onTimeSignature: function () {
            myOnTimeSignature(parseInt(beatsPerBarSelect.item(beatsPerBarSelect.selectedIndex).valueOf().value));
        },
        onReadFileStart: function () {
            myReadFileStart();
        },
        onSaveAs: function () {
            mySaveAs();
        },
        onPlus5th: function () {
            addSemiTones(7);
        },
        onMinus4th: function () {
            addSemiTones(-7);
        },
        onDeleteAll: function () {
            myDeleteAll();
        },
        onKey: function () {
            onKey();
        },
        onPrint: function () {
            onPrint();
        },
        onLyricEntry: function () {
            onLyricEntry();
        },
        onBlankEntry: function () {
            onBlankEntry();
        },
        onHalfStep: function (stepN) {
            onHalfStep(stepN);
        },
        onLineUp: function () {
            firstScoreLine = (firstScoreLine > 0 ? firstScoreLine - 1 : 0);
            scoreUpdate();
        },
        onLineDown: function () {
            if (sheetNotes.length > 0 && firstScoreLine < sheetNotes[sheetNotes.length - 1].line)
            {
                firstScoreLine++;
                scoreUpdate();
            }
        },
        onLimitedLoop: function (n) {
            onLimitedLoop(n);
        },
        onBlues: function () {
            onBlues();
        },
        frameUpdate: function () {
            scoreBackgroundUpdate();
        },
        onBeatsSelect: function () {
            onBeatsSelect();
        },
        onVolume: function () {
            onVolume();
        },
        onRepeatStart: function () {
            onRepeatStart();
        },
        onRepeatEnd: function () {
            onRepeatEnd();
        },
        onAllScaleNotes: function () {
            onAllScaleNotes();
        },
        onHiHat: function () {
            onHiHat(hiHatSelect.value);
        },
        onIsSwing8: function (selected) {
            isSwing8 = isSwing8Select.checked;
            scoreComputeLinesAndUpdate();
        },
        onSwingType: function () {
            onSwingType(parseInt(swingTypeSelect.value));
        },
        onDisplayResize: function () {
            resizeScoreLayers();
        }

    };
};
