/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import javax.inject.Inject;

/**
 *
 * @author bob
 */
public class SongListView
        extends ViewImpl
        implements SongListPresenterWidget.MyView {

  interface Binder extends UiBinder<Widget, SongListView> {
  }

  @UiField
  SpanElement username;

  @UiField
  TextBox songSearch;

  @UiField
  Grid songGrid;

  @Inject
  SongListView(Binder binder) {
    initWidget(binder.createAndBindUi(this));
  }

  @Override
  public void displayCurrentUserName(String username) {
    this.username.setInnerHTML(username);
  }

  @Override
  public Grid getSongGrid() {
    return songGrid;
  }

  @Override
  public TextBox getSongSearch() {
    return songSearch;
  }
}
