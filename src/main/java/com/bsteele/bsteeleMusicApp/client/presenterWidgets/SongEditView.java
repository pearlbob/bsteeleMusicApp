/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.ScaleChord;
import com.bsteele.bsteeleMusicApp.client.songs.Section;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.dom.client.*;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;
import java.util.ArrayList;

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
    ButtonElement chordsMajor;
    @UiField
    ButtonElement chordsMinor;
    @UiField
    ButtonElement chordsDominant7;

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
                Section section = Section.valueOf(sectionSelection.getValue());
                String text = chordsTextEntry.getValue();
                int position = chordsTextEntry.getCursorPos();
                String add = "\n" + section.getAbbreviation() + ": ";
                text = text.substring(0, position) + add + text.substring(position);
                chordsTextEntry.setText(text);
                chordsTextEntry.setCursorPos(position + add.length());
                chordsTextEntry.setFocus(true);
            }
        });

        chordsTextEntry.setFocus(true);

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
//            keySelection.add();
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

    private void titleChordButtons() {
        ScaleChord keyScaleChord =  key.getDiatonicByDegree(0);
        chordsMajor.setTitle(keyScaleChord.toString());
        chordsMinor.setTitle(key.getDiatonicByDegree(3).toString());
        chordsDominant7.setTitle(key.getDiatonicByDegree(4).toString());
    }


    private Key key = Key.getDefault();
    private static final int minBpm = 50;
    private static final int maxBpm = 400;
    private final HandlerManager handlerManager;
    private static final Document document = Document.get();
}
