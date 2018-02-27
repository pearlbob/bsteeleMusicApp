/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

  @Override
  public void setSong(Song song) {
    title.setText(song.getTitle());
  }

  interface Binder extends UiBinder<Widget, LyricsAndChordsView> {
  }

  @Inject
  LyricsAndChordsView(Binder binder) {
    initWidget(binder.createAndBindUi(this));
  }

  @UiField
  Label title;

}
