/**
 * CopyRight 2018 bsteele.com
 * User: bob
 * <p>
 * Package level java doc description here?
 * <p>
 * Package level java doc description here?
 */

/**
 * Package level java doc description here?
 */
package com.bsteele.bsteeleMusicApp;

/*
fixme: elminate com.google.gwt.json.client.JSON*
fixme: multiple sections on a single MeasureSequenceItem
fixme: bass view is damaged and old design
todo: complete chord addressing
todo: do measure addressing
todo: implement local storage songs... and their removal
todo: write allSongs.songlyrics sanity check for download from Bodhi
fixme: eliminate date on allSongs.songlyrics
fixme: redo key guess
todo: add minor keys
todo: song complexity index
todo: eliminate dots if they follow the default pattern eg. "E.A." in 4/4 becomes "EA"
todo: user select of 2,3,4,5 or 6 measures per line?
todo: x2 and x3 exploded
todo: dashes automatically enforced?
fixme: africa display
fixme: Measure.parse() behavior on errors
todo: comments in chords!  make them more obvious, horizontal spacing

todo: use webstorage for user name
____fixme: double delete => "  ) (  "
fixme: how to delete a repeat marker?... and a repeat?
fixme: chords shouldn't stretch vertically to match lyrics
todo: edit view, click to the right of chord table should select last in row
fixme: player view on born to run
fixme: layla sharps and flats
todo: Song diff/merge display for versions of same file, a write that guarantees biggest version number
fixme: always move to top of song on song change
todo: cursor off screen when in play, all mouse wheels go to scroll
todo: keep key after edit
todo: select display key when editing
fixme: last line of lyrics don't display unless ended with new line
____fixme: call checkSong after edit change delete
____fixme: E..A enters as E.A.
____fixme: dots should not be lost on key change
todo: even the horizontal spacing of mesure display widths... if there is room
todo: arrow keys should work on chord edit
todo: drag selection in chord edit
fixme: cursor should be remembered in undos
fixme: chord cursor should not go to end of song at section delete
fixme: click to the right of the chord edit table should select end of row
todo: go to top of song on song change
todo: https://www.youtube.com/results?search_query=Aint+No+Sunshine+by+Bill+Withers
todo: focus on search on songs tab select
todo: add a newline to chords for things like repetition in 5 measures: africa


____fixme: can't edit numbered sections, eg. V2, Ch3, etc.
____fixme: notes not always in the correct key signature (i.e. # instead of b, vise versa)
todo: colorize the lyrics section in edit
todo: chords: 69 maug

todo: guitar chord fingerings with/without capo, bar chords?  with bass notes for chords
todo: piano chords (different from guitar)
todo: bass finger board for each chord
todo: option: hide chorus lyrics on subesequent chorus's

fixme: newline needed in lyrics section to register the section
fixme: songs not saved in directory with same song other versions, will not have a proper version number on it
fixme: first read song can't be shown removed even though it is.
fixme: fill last line of repeat when not a multiple of 4
fixme: lyrics and chords sizing
fixme: Black Velvet, Alannah Myles key of B, line 1 of c: DG♯♯ (was EbBb in key of C, became: D#A#),  AA of line 2 doesn't transpose either (was BbA)

fixme: songs with delta (maj7) don't parse in json?
fixme: better error messages at parsing
fixme: broken measure   X X X 
fixme: written files should update their modification date
fixme: fix chaos at song.parseLyrics() and song.parse(beatsPerBar, song.chords);
todo: remember the last selected key for a song
todo: Bodhi question:  arrangement presentation
todo: Bodhi question:  arrangement change of key with notes... after 12 offsets?
todo: Bodhi question:  arrangement after song change
todo: Bodhi question:  AB/G     assume G under A and B?  B only?

fixme: Ddim/G transposes to Cdim/G and Adim/G as well.
fixme: in play, beat display out of sync with measure update
fixme: section progress indicator
fixme: training options
fixme: sync audio context clocks across multiple machines

fixme: add exercise to bass tab: R 8 5 10, etc
fixme: validate song before entry: all lyrics sections should have matching chord sections
fixme: bass clef, notes above top line don't have partial lines above
fixme:  chords and lyrics can jiggle chords at some sizes
fixme: x1/3 doesn't work, allow proper space for it
fixme: chords jiggle in play on chordsAndLyrics
fixme: in play, no chord indicator on multiple sections of same chords on chordsAndLyrics

____: fix last chord not shown if no ending newline for the file
todo: select a key that's a minor key
____: fix staging script: it removes beta on a test failure
fixme: The minor scale starts with the assumption of the major scale note names, and then flats the third and seventh, keeping those note name positions accordingly.

todo: mouse over a chord displayed to get a fingering chart.  Dropdown at top has options for guitar, ukulele, bass)
todo: basically need a look-up table that displays image-file associated with appropriately-displayed chord.
  I can help find files to fill table with appropriate chords if you can create
   the structure to hold the table and display as mouse-over.

todo: study tool: prescribed (#,b,natural) notes affects all octaves for the duration of the bar
todo: arrangement display in song display as comment
todo: arrangements can be part of song, e.g. drums
todo: option list on/off
todo: a default, catch all, section
todo: finish transposition
todo: add drums to the edit page, store by song
todo: move the lyrics highlight color to the section id
todo: keep current lyrics section in view: i.e. autoscroll
fixme: map sections from multiple sections to a single findable section id for play indicators
fixme: change colors for  multiple sections during play

todo: arrangement window in song display
todo: add log on server side
todo: add "fixit" memo to server side log
todo: display: vocal, Bb, melody?, theme riff?
todo: worry about legal copyright notice of file sharing


todo: edit should offer BPM & Key from lyrics & chords
todo: chord colors should always parse lyrics in play
todo: delta shape == major7
todo: play youtube video
fixme: WebSocket connection to 'wss://fit-union-164517.appspot.com/bsteeleMusicApp/bsteeleMusic' failed: Error during WebSocket handshake: Unexpected response code: 400
fixme: standard google app engine deployment includes client .class files

• Chord Tone - the pitches in a chord that determine its basic sound quality. Chord tones are the following:
a. root, 3rd and 5th of a triad (major, minor, augmented or diminished).
b. root, 4th and 5th of a triad (sus 4 triad).
c. root, 3rd, 5th and 7th of seventh chords.
d. root, 4th, 5th and flat 7th of a dominant 7th sus 4 chord.
e. root, 3rd, 5th and 6th of a sixth chord (major, minor).

fixme: drum   sound from remotely started song
todo: drums: open TH, closed TH,  snare, kick
todo: improve leadin count down
todo: write song.comparatorByTitleAndArtist()
todo: write song.equals (hashcode, etc) for all fields
todo: broadcast song selection: conflicts?

todo: metronome clicks
todo: pop the colors on beat transitions
todo: fix ctl-key on mac

todo: event plot
todo: audio call out of chord changes
todo: audio call out of upcoming section changes
todo: audio count in, missing last beat or two
todo: mp3 recording and playback (including start offsets, non-integer bpm)
todo: align tabs to enum
todo: tooltips, many tooltips

todo: control s to legacy when editing a song

todo: fix multiple section id's on a single chord section with vertical span
todo: fix multiple section id's on a single chord section with color selection

todo: add lastModified to song metadata on file read, add size?
    file location as well
todo: markup language aids (buttons)
todo: search for text in song

todo: select start by sections
todo: loop by section
todo: to dynamically change font size for chords to fit
todo: set an indicator for added events, font color change?


todo: comments in chords?

todo: optionChoicesDiv with a css class

todo: screen control screen
todo: logging screen
todo: accelerated sub-beat indicator
todo: hash by song name not list index

todo: section alterations imply: ch1 ch2 etc
todo: Beat vs measure resolution on a section basis


todo: datagrid for events
todo: get focus on songlist search, keep it there
todo: visual screen-warning that we're about to change sections
todo: think about fullscreen use, particularly when playing



Lyrics:
Song
	title
	artist
	copyright
	key
	BPM
	time signature
	chords
	lyrics
	default drums
	metadata (genre,album,date,album cover, etc)
chords
	section definitions
lyrics
	lyric sections
lyric section
	sectionId: type (i,v,pc,c,br,a,b,o,t), version (1,2,3...)
	lyric measure
lyric measure
	lyrics
section
	sectionId
	repeats
	measures
	parts
measure
	beat count
	chords / no chords
chord
	scale note
	beats
	chord modifier (major/minor/major7/minor7/dominant7/sus/...etc)
	tensions: 7, b7, 9, b9, #9, 11, #11, 13, b13 (nonharmonic tones)
	anticipation/delayed attack (push/pull) -8th, -16th, -1/3triplet, +8th, +16th, +1/3triplet
	slash chord (bass)
key
	name
	number (of sharps/flats, aka wheel of fifth's position)
scaleNote
	key
	offset	(in the key scale)
	name  ( A, A#, Bb, B, etc)

Scores:
part
	type (drum, guitar, bass)
	bars
bar
	measure
	notes
note
	pitch
	duration (in beats)
	scaleNote
	scaleNumber
	isDotted
	isTied
	isBeamed (maintain invisible bar)
	isSwing 1,2
	isTriplet 1,2,3
	isDrum
	isRest
	isPreNotationRequired (#,b, natural)
	trebleClefPosition
	bassClefPosition
pitch
	absolute pitch name (A0->)
	frequency (hertz)




 */