/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
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
  Label title;

  @UiField
  HTMLPanel chords;

  @UiField
  HTMLPanel lyrics;

  @Override
  public void setSong(Song song) {
    title.setText(song.getTitle());
    GWT.log(song.generateHtmlChordTable());
    
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
