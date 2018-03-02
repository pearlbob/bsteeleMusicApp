/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.shared.HandlerManager;
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
 *
 * @author bob
 */
public class SongEditView
        extends ViewImpl
        implements SongEditPresenterWidget.MyView {

  @UiField
  ButtonElement songEnter;

  @UiField
  ButtonElement songCancel;

  @UiField
  TextBox titleEntry;

  @UiField
  TextBox artistEntry;

  @UiField
  TextBox copyrightEntry;

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
        GWT.log("songEnter()");
        enterSong();
      }
    });

    Event.sinkEvents(songCancel, Event.ONCLICK);
    Event.setEventListener(songCancel, (Event event) -> {
      if (Event.ONCLICK == event.getTypeInt()) {
        GWT.log("songCancel()");
      }
    });

    titleEntry.addChangeHandler((event) -> {
      GWT.log("titleEntry: " + titleEntry.getValue());
    });
    artistEntry.addChangeHandler((event) -> {
      GWT.log("artistEntry: " + artistEntry.getValue());
    });
    copyrightEntry.addChangeHandler((event) -> {
      GWT.log("copyrightEntry: " + copyrightEntry.getValue());
    });
    bpmEntry.addChangeHandler((event) -> {
      GWT.log("bpmEntry: " + bpmEntry.getValue());
    });

    Event.sinkEvents(timeSignatureEntry, Event.ONCHANGE);
    Event.setEventListener(timeSignatureEntry, (Event event) -> {
      if (Event.ONCHANGE == event.getTypeInt()) {
        GWT.log("timeSignatureEntry(): " + timeSignatureEntry.getValue());
      }
    });

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
    isEdit = true;
  }

  public void enterSong() {
    if (titleEntry.getText().length() <= 0) {
      Window.alert("no song title given!");
      return;
    }
//    if (!isEdit) {
//      for (var i = 0; i < songs.length; i++) {
//        var song = songs[i];
//        if (titleEntry.value == = song.name) {
//          Window.alert("song with that title already exists!");
//          return;
//        }
//      }
//    }

    if (artistEntry.getText().length() <= 0) {
      Window.alert("no artist given!");
      return;
    }

    if (copyrightEntry.getText().length() <= 0) {
      Window.alert("no copyright given!");
      return;
    }

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
            copyrightEntry.getText(), bpm, beatsPerBar, unitsPerMeasure,
            chordsEntry.getValue(), lyricsEntry.getValue());
    GWT.log(song.toJson());

//    saveSongAs(titleEntry.getText() + ".songlyrics", songHtml);
//    addSongHtml(song);
//    songEntryClear();
//    songEntryElement.hidden = true;
//    addSongButton.hidden = false;
//    optionChoicesDiv.hidden = false;
//    let s = document.getElementById("Song" + (songIdCount - 1) + "Toc");
//    if (s != null) {
//      s.scrollIntoView();
//    }
  }

  private boolean isEdit = false;
  private final HandlerManager handlerManager;
  private static final RegExp twoOrThreeDigitsRegexp = RegExp.compile("^\\d{2,3}$");
  private static final int minBpm = 50;
  private static final int maxBpm = 400;
  private static final RegExp timeSignatureExp = RegExp.compile("^(\\d{1,2})\\/(\\d)$");
}
