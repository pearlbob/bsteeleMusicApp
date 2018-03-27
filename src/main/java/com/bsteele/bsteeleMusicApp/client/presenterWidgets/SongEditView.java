/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.ScaleNote;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.TextAreaElement;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;

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
    TextBox bpmEntry;

    @UiField
    SelectElement timeSignatureEntry;

    @UiField
    TextAreaElement chordsEntry;

    @UiField
    TextAreaElement lyricsEntry;

    interface Binder extends UiBinder<Widget, SongEditView> {
    }

    @Inject
    SongEditView(Binder binder) {
        initWidget(binder.createAndBindUi(this));

        handlerManager = new HandlerManager(this);

        Event.sinkEvents(songEnter, Event.ONCLICK);
        Event.setEventListener(songEnter, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                enterSong();
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
                chordsEntry.setValue("");
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

    }

    //  @Override
//  public void fireEvent(GwtEvent<?> event) {
//    handlerManager.fireEvent(event);
//  }
    @Override
    public void setSongEdit(Song song) {
        titleEntry.setText(song.getTitle());
        artistEntry.setText(song.getArtist());
        copyrightEntry.setText(song.getCopyright());
        bpmEntry.setText(Integer.toString(song.getBpm()));
        timeSignatureEntry.setValue(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());
        chordsEntry.setValue(song.getChords());
        lyricsEntry.setValue(song.getRawLyrics());
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

        ScaleNote keyScaleNote = ScaleNote.valueOf(keySelection.getValue());
        if (keyScaleNote == null)
            keyScaleNote = ScaleNote.C;  //  punt an error

        if (bpmEntry.getText().length() <= 0) {
            Window.alert("no BPM given!");
            return;
        }

        String bpmText = bpmEntry.getText();
        if (!twoOrThreeDigitsRegexp.test(bpmText)) {
            Window.alert("BPM has to be a number from " + minBpm + " to " + maxBpm);
            return;
        }

        int bpm = Integer.parseInt(bpmText);
        if (bpm < minBpm || bpm > maxBpm) {
            Window.alert("BPM has to be a number from " + minBpm + " to " + maxBpm);
            return;
        }

        if (chordsEntry.getValue().length() <= 0) {
            Window.alert("no chords given!");
            return;
        }
        if (lyricsEntry.getValue().length() <= 0) {
            Window.alert("no lyrics given!");
            return;
        }

        int beatsPerBar = 4;
        int unitsPerMeasure = 4;
        MatchResult mr = timeSignatureExp.exec(timeSignatureEntry.getValue());
        if (mr != null) {
            // match
            beatsPerBar = Integer.parseInt(mr.getGroup(1));
            unitsPerMeasure = Integer.parseInt(mr.getGroup(2));
        }

        Song song = Song.createSong(titleEntry.getText(), artistEntry.getText(),
                copyrightEntry.getText(), keyScaleNote, bpm, beatsPerBar, unitsPerMeasure,
                chordsEntry.getValue(), lyricsEntry.getValue());
        //GWT.log(song.toJson());

        fireSongSubmission(song);
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

    private final HandlerManager handlerManager;
    private static final RegExp twoOrThreeDigitsRegexp = RegExp.compile("^\\d{2,3}$");
    private static final int minBpm = 50;
    private static final int maxBpm = 400;
    private static final RegExp timeSignatureExp = RegExp.compile("^(\\d{1,2})\\/(\\d)$");
}
