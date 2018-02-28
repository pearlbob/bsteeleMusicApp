/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import javax.inject.Inject;

/**
 *
 * @author bob
 */
public class LyricsAndChordsView extends ViewImpl
        implements LyricsAndChordsPresenterWidget.MyView {

  @UiField
  SpanElement title;
  
  @UiField
  SpanElement artist;

  @UiField
  HTMLPanel chords;

  @UiField
  HTMLPanel lyrics;

  @UiField
  SpanElement copyright;

  @Override
  public void setSong(Song song) {
    title.setInnerHTML(song.getTitle());
    artist.setInnerHTML(song.getArtist());
    copyright.setInnerHTML(song.getCopyright());

    chords.clear();
    chords.add(new HTML(song.generateHtmlChordTable()));
    lyrics.clear();
    lyrics.add(new HTML(song.generateHtmlLyricsTable()));
  }

  interface Binder extends UiBinder<Widget, LyricsAndChordsView> {
  }

  @Inject
  LyricsAndChordsView(Binder binder) {
    initWidget(binder.createAndBindUi(this));
  }

}
