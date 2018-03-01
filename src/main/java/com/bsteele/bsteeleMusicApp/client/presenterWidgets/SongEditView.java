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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
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
        GWT.log("chords: ");
        GWT.log(chordsEntry.getValue());
        GWT.log("lyricsEntry: ");
        GWT.log(lyricsEntry.getValue());
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
    timeSignatureEntry.setValue(song.getBeatsPerBar() + "/4");
    chordsEntry.setValue(song.getChords());
    lyricsEntry.setValue(song.getRawLyrics());
  }

  private HandlerManager handlerManager;
}
