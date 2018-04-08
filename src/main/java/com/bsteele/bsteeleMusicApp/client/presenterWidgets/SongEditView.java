/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * @author bob
 */
public class SongEditView
        extends ViewImpl
        implements SongEditPresenterWidget.MyView,
        HasHandlers {

    @UiField
    ButtonElement songEnter;

    @UiField
    ButtonElement songEntryClear;

    @UiField
    TextBox titleEntry;

    @UiField
    TextBox artistEntry;

    @UiField
    TextBox copyrightEntry;

    @UiField
    SelectElement keySelection;

    @UiField
    ButtonElement keyGuess;

    @UiField
    TextBox bpmEntry;

    @UiField
    SelectElement timeSignatureEntry;

    @UiField
    SelectElement sectionSelection;

    @UiField
    TextArea chordsTextEntry;

    @UiField
    Button chordsI;
    @UiField
    Button chordsIV;
    @UiField
    Button chordsIV7;
    @UiField
    Button chordsV;
    @UiField
    Button chordsV7;
    @UiField
    Button chordsii;
    @UiField
    Button chordsiii;
    @UiField
    Button chordsvi;
    @UiField
    Button chordsvii;
    @UiField
    SelectElement scaleNoteSelection;
    @UiField
    SelectElement chordSelection;

    @UiField
    Button recent0;
    @UiField
    Button recent1;
    @UiField
    Button recent2;
    @UiField
    Button recent3;
    @UiField
    Button recent4;
    @UiField
    Button recent5;
    @UiField
    Button common0;
    @UiField
    Button common1;
    @UiField
    Button common2;
    @UiField
    Button common3;
    @UiField
    Button common4;
    @UiField
    Button common5;

    @UiField
    Button chordsUndo;
    @UiField
    Button chordsRedo;

    @UiField
    TextAreaElement lyricsEntry;

    interface Binder extends UiBinder<Widget, SongEditView> {
    }

    @Inject
    SongEditView(Binder binder) {
        initWidget(binder.createAndBindUi(this));

        handlerManager = new HandlerManager(this);

        //  sectionSelection
        SelectElement sectionSelectElement = SelectElement.as(sectionSelection);
        for (Section section : Section.values()) {
            OptionElement optionElement = document.createOptionElement();
            String name = section.name();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            optionElement.setLabel(section.getAbbreviation() + ": " + name);
            optionElement.setValue(section.name());
            sectionSelectElement.add(optionElement, null);
        }
        Event.sinkEvents(sectionSelection, Event.ONCLICK);
        Event.setEventListener(sectionSelection, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                chordsTextAdd("\n" + Section.valueOf(sectionSelection.getValue()).getAbbreviation() + ":");
            }
        });

        //  key selection
        Event.sinkEvents(keySelection, Event.ONCLICK);
        Event.setEventListener(keySelection, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                setKey(Key.valueOf(keySelection.getValue()));
            }
        });


        Event.sinkEvents(scaleNoteSelection, Event.ONCLICK);
        Event.setEventListener(scaleNoteSelection, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                titleChordSelections();
            }
        });
        Event.sinkEvents(chordSelection, Event.ONCLICK);
        Event.setEventListener(chordSelection, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                enterChord(chordSelection.getValue());
            }
        });

        chordsTextEntry.setFocus(true);

        //  chord entry
        chordsI.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsIV.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsIV7.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsV.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsV7.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsiii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsvi.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsvii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });

        recent0.setVisible(false);   //  hide them all initially
        recent0.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent1.setVisible(false);   //  hide them all initially
        recent1.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent2.setVisible(false);   //  hide them all initially
        recent2.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent3.setVisible(false);   //  hide them all initially
        recent3.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent4.setVisible(false);   //  hide them all initially
        recent4.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent5.setVisible(false);   //  hide them all initially
        recent5.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common0.setVisible(false);   //  hide them all initially
        common0.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common1.setVisible(false);   //  hide them all initially
        common1.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common2.setVisible(false);   //  hide them all initially
        common2.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common3.setVisible(false);   //  hide them all initially
        common3.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common4.setVisible(false);   //  hide them all initially
        common4.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common5.setVisible(false);   //  hide them all initially
        common5.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });

        chordsUndo.setEnabled(false);
        chordsUndo.addClickHandler((ClickEvent event) -> {
        });

        chordsRedo.setEnabled(false);
        chordsRedo.addClickHandler((ClickEvent event) -> {
        });


        Event.sinkEvents(songEnter, Event.ONCLICK);
        Event.setEventListener(songEnter, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                enterSong();
            }
        });

        Event.sinkEvents(keyGuess, Event.ONCLICK);
        Event.setEventListener(keyGuess, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                guessTheKey();
            }
        });

        Event.sinkEvents(songEntryClear, Event.ONCLICK);
        Event.setEventListener(songEntryClear, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                titleEntry.setText("");
                artistEntry.setText("");
                copyrightEntry.setText("");
                bpmEntry.setText("106");
                timeSignatureEntry.setValue("4/4");
                chordsTextEntry.setValue("");
                lyricsEntry.setValue("");
            }
        });

//    titleEntry.addChangeHandler((event) -> {
//      GWT.log("titleEntry: " + titleEntry.getValue());
//    });
//    artistEntry.addChangeHandler((event) -> {
//      GWT.log("artistEntry: " + artistEntry.getValue());
//    });
//    copyrightEntry.addChangeHandler((event) -> {
//      GWT.log("copyrightEntry: " + copyrightEntry.getValue());
//    });
//    bpmEntry.addChangeHandler((event) -> {
//      GWT.log("bpmEntry: " + bpmEntry.getValue());
//    });
//
//    Event.sinkEvents(timeSignatureEntry, Event.ONCHANGE);
//    Event.setEventListener(timeSignatureEntry, (Event event) -> {
//      if (Event.ONCHANGE == event.getTypeInt()) {
//        GWT.log("timeSignatureEntry(): " + timeSignatureEntry.getValue());
//      }
//    });

        setKey(Key.getDefault());

    }


    @Override
    public void setSongEdit(Song song) {
        titleEntry.setText(song.getTitle());
        artistEntry.setText(song.getArtist());
        copyrightEntry.setText(song.getCopyright());
        setKey(song.getKey());
        bpmEntry.setText(Integer.toString(song.getDefaultBpm()));
        timeSignatureEntry.setValue(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());
        chordsTextEntry.setValue(song.getChordsAsString());
        chordsTextEntry.setCursorPos(0);
        lyricsEntry.setValue(song.getLyricsAsString());
    }

    public void enterSong() {
        if (titleEntry.getText().length() <= 0) {
            Window.alert("no song title given!");
            return;
        }

        if (artistEntry.getText().length() <= 0) {
            Window.alert("no artist given!");
            return;
        }

        if (copyrightEntry.getText().length() <= 0) {
            Window.alert("no copyright given!");
            return;
        }

        Key key = Key.valueOf(keySelection.getValue());
        if (key == null)
            key = Key.C;  //  punt an error
        setKey(key);

        if (bpmEntry.getText().length() <= 0) {
            Window.alert("no BPM given!");
            return;
        }

        String bpmText = bpmEntry.getText();
        final RegExp twoOrThreeDigitsRegexp = RegExp.compile("^\\d{2,3}$");
        if (!twoOrThreeDigitsRegexp.test(bpmText)) {
            Window.alert("BPM has to be a number from " + minBpm + " to " + maxBpm);
            return;
        }

        int bpm = Integer.parseInt(bpmText);
        if (bpm < minBpm || bpm > maxBpm) {
            Window.alert("BPM has to be a number from " + minBpm + " to " + maxBpm);
            return;
        }

        if (chordsTextEntry.getValue().length() <= 0) {
            Window.alert("no chords given!");
            return;
        }
        if (lyricsEntry.getValue().length() <= 0) {
            Window.alert("no lyrics given!");
            return;
        }

        int beatsPerBar = 4;
        int unitsPerMeasure = 4;
        final RegExp timeSignatureExp = RegExp.compile("^(\\d{1,2})\\/(\\d)$");
        MatchResult mr = timeSignatureExp.exec(timeSignatureEntry.getValue());
        if (mr != null) {
            // match
            beatsPerBar = Integer.parseInt(mr.getGroup(1));
            unitsPerMeasure = Integer.parseInt(mr.getGroup(2));
        }

        Song song = Song.createSong(titleEntry.getText(), artistEntry.getText(),
                copyrightEntry.getText(), key, bpm, beatsPerBar, unitsPerMeasure,
                chordsTextEntry.getValue(), lyricsEntry.getValue());
        //GWT.log(song.toJson());

        fireSongSubmission(song);
    }

    private void setKey(Key key) {
        this.key = key;

        titleChordButtons();

        String keyValue = key.name();
        NodeList<OptionElement> list = keySelection.getOptions();
        for (int i = 0; i < list.getLength(); i++) {
            OptionElement oe = list.getItem(i);
            oe.setSelected(oe.getValue().equals(keyValue));
        }
    }

    private void enterChord(ClickEvent event) {
        enterChord(event.getRelativeElement().getInnerText());
    }

    private void enterChord(String name) {
        ScaleChord scaleChord = ScaleChord.parse(name);
        addRecentScaleChord(scaleChord);
        chordsTextAdd(scaleChord.toString());
        findMostCommonScaleChords();
    }

    private void addRecentScaleChord(ScaleChord scaleChord) {
        if (recentScaleChords.contains(scaleChord))
            return; //  leave well enough alone

        //recentScaleChords.remove(scaleChord);
        final Button recents[] = {
                recent0,
                recent1,
                recent2,
                recent3,
                recent4,
                recent5
        };

        final int recentScaleChordsMaxSize = recents.length;
        recentScaleChords.add(0, scaleChord);
        while (recentScaleChords.size() > recentScaleChordsMaxSize)
            recentScaleChords.remove(recentScaleChords.size() - 1);

        int i = 0;
        for (ScaleChord recentScaleChord : recentScaleChords) {
            Button recent = recents[i++];
            recent.setHTML(recentScaleChord.toString());
            recent.setVisible(true);
        }
    }

    private class CommonScaleChordItem implements Comparable<CommonScaleChordItem> {
        CommonScaleChordItem(ScaleChord scaleChord, int count) {
            this.scaleChord = scaleChord;
            this.count = count;
        }

        public ScaleChord getScaleChord() {
            return scaleChord;
        }

        public int getCount() {
            return count;
        }

        @Override
        public int compareTo(CommonScaleChordItem o) {
            if (count != o.count)
                return count < o.count ? 1 : -1;   //  note reverse order
            return scaleChord.compareTo(o.scaleChord);
        }

        private final ScaleChord scaleChord;
        private final int count;
    }

    private void findMostCommonScaleChords() {
        final Button commons[] = {
                common0,
                common1,
                common2,
                common3,
                common4,
                common5
        };

        HashMap<ScaleChord, Integer> scaleChordMap = new HashMap<>();
        {
            String s = chordsTextEntry.getValue();
            int pos = 0;
            while (pos < s.length()) {
                ScaleChord scaleChord = ScaleChord.parse(s.substring(pos));
                if (scaleChord != null) {
                    //GWT.log("parse: " + scaleChord.toString());
                    if (!scaleChordMap.containsKey(scaleChord))
                        scaleChordMap.put(scaleChord, 1);
                    else
                        scaleChordMap.put(scaleChord, scaleChordMap.get(scaleChord) + 1);
                    pos += scaleChord.getLength();
                } else
                    pos++;
            }
        }

        TreeSet<CommonScaleChordItem> commonScaleChordItems = new TreeSet<>();
        for (ScaleChord key : scaleChordMap.keySet()) {
            commonScaleChordItems.add(new CommonScaleChordItem(key, scaleChordMap.get(key)));
        }
        int i = 0;
        for (CommonScaleChordItem item : commonScaleChordItems) {
            if (i >= commons.length)
                break;
            Button common = commons[i++];
            common.setHTML(item.getScaleChord().toString());
            common.setVisible(true);
            //GWT.log((i - 1) + ": " + item.getCount() + " " + item.getScaleChord());
        }
        while (i < commons.length) {
            //GWT.log(i + ": hide");
            Button common = commons[i++];
            common.setVisible(false);
        }
    }

    private void chordsTextAdd(String addition) {
        if (addition == null || addition.length() == 0)
            return;
        addition += " ";

        int position = chordsTextEntry.getCursorPos();
        String text = chordsTextEntry.getValue();
        text = text.substring(0, position) + addition + text.substring(position);
        chordsTextEntry.setText(text);
        chordsTextEntry.setCursorPos(position + addition.length());
        chordsTextEntry.setFocus(true);
    }


    private void fireSongSubmission(Song song) {
        fireEvent(new SongSubmissionEvent(song));
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }


    @Override
    public HandlerRegistration SongSubmissionEventHandler(
            SongSubmissionEventHandler handler) {
        return handlerManager.addHandler(SongSubmissionEvent.TYPE, handler);
    }

    private void guessTheKey() {
        final RegExp scaleNoteExp = RegExp.compile("(" + ScaleChord.getRegExp() + ")", "g");
        MatchResult mr;
        ArrayList<ScaleChord> scaleChords = new ArrayList<>();
        while ((mr = scaleNoteExp.exec(chordsTextEntry.getValue())) != null) {
            if (mr.getGroupCount() >= 1) {
                String s = mr.getGroup(1);
                ScaleChord scaleChord = ScaleChord.parse(s);
                if (scaleChord != null)
                    scaleChords.add(scaleChord);
            }
        }

        setKey(Key.guessKey(scaleChords));
    }

    private void titleChordSelections() {
        chordSelection.clear();
        ScaleNote scaleNote = ScaleNote.valueOf(scaleNoteSelection.getValue());

        for (ChordDescriptor cd : ChordDescriptor.getAllChordDescriptorsOrdered()) {
            OptionElement optionElement = document.createOptionElement();
            ScaleChord sc = new ScaleChord(scaleNote, cd);
            optionElement.setLabel(sc.toString());
            optionElement.setValue(sc.toString());
            chordSelection.add(optionElement, null);
        }
    }

    private void titleChordButtons() {
        ScaleChord keyScaleChord = key.getDiatonicByDegree(1 - 1);
        chordsI.setHTML(keyScaleChord.toString());
        ScaleChord iv = key.getDiatonicByDegree(4 - 1);
        chordsIV.setHTML(iv.toString());
        chordsIV7.setHTML(new ScaleChord(iv.getScaleNote(), ChordDescriptor.dominant7).toString());
        ScaleChord v7 = key.getDiatonicByDegree(5 - 1);
        chordsV7.setHTML(v7.toString());
        ScaleChord v = new ScaleChord(v7.getScaleNote(), ChordDescriptor.major);
        chordsV.setHTML(v.toString());

        chordsii.setHTML(key.getDiatonicByDegree(2 - 1).toString());
        chordsiii.setHTML(key.getDiatonicByDegree(3 - 1).toString());
        chordsvi.setHTML(key.getDiatonicByDegree(6 - 1).toString());
        chordsvii.setHTML(key.getDiatonicByDegree(7 - 1).toString());

        //  list all the scale notes with the key scale notes first
        scaleNoteSelection.clear();     //  fixme: document garbage collection?
        ArrayList<ScaleNote> scaleNotes = new ArrayList<>();
        for (int i = 0; i < MusicConstant.notesPerScale; i++)
            scaleNotes.add(key.getMajorScaleByNote(i));
        for (int i = 0; i < MusicConstant.halfStepsPerOctave; i++) {
            ScaleNote scaleNote = key.getScaleNoteByHalfStep(i);
            if (!scaleNotes.contains(scaleNote))
                scaleNotes.add(scaleNote);
        }
        for (ScaleNote scaleNote : scaleNotes) {
            OptionElement optionElement = document.createOptionElement();
            String s = scaleNote.toString();
            optionElement.setLabel(s
                    + (s.length() < 2 ? "   " : " ")
                    + ChordComponent.getByHalfStep(scaleNote.getHalfStep() - key.getHalfStep()).getShortName());
            optionElement.setValue(scaleNote.name());
            scaleNoteSelection.add(optionElement, null);
        }
        titleChordSelections();
    }

    private ArrayList<ScaleChord> recentScaleChords = new ArrayList<>();
    private ArrayList<ScaleChord> commonScaleChords = new ArrayList<>();
    private Key key = Key.getDefault();
    private static final int minBpm = 50;
    private static final int maxBpm = 400;
    private final HandlerManager handlerManager;
    private static final Document document = Document.get();
}
