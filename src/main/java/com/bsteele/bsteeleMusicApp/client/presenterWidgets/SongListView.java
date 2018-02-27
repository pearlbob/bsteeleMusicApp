/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.songs.SongSelectionEvent;
import com.bsteele.bsteeleMusicApp.client.application.songs.SongSelectionEventHandler;
import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;

/**
 *
 * @author bob
 */
public class SongListView
        extends ViewImpl
        implements SongListPresenterWidget.MyView,
        HasHandlers {

  @Override
  public void setSongList(Set<Song> songs) {
    allSongs.clear();
    allSongs.addAll(songs);
    searchSongs(songSearch.getValue());
  }

  interface Binder extends UiBinder<Widget, SongListView> {
  }

  @UiField
  TextBox songSearch;

  @UiField
  Button clearSearch;

  @UiField
  Grid songGrid;

  @Inject
  SongListView(Binder binder) {
    initWidget(binder.createAndBindUi(this));

    handlerManager = new HandlerManager(this);

    songGrid.addClickHandler(event -> {
      if (filteredSongs != null) {
        Song selectedSong = filteredSongs.get(songGrid.getCellForEvent(event).getRowIndex());
        SongSelectionEvent songSelectionEvent = new SongSelectionEvent(selectedSong);
        fireEvent(songSelectionEvent);
      }
    });

    songSearch.addKeyUpHandler((event) -> {
      searchSongs(songSearch.getValue());
    });

    songSearch.setFocus(true);
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    handlerManager.fireEvent(event);
  }

  @Override
  public HandlerRegistration addSongSelectionEventHandler(
          SongSelectionEventHandler handler) {
    return handlerManager.addHandler(SongSelectionEvent.TYPE, handler);
  }

  private void searchSongs(String search) {
    if (search == null) {
      search = "";
    }
    search = search.replaceAll("[^\\w\\s']+", "");
    search = search.toLowerCase();
    {
      TreeSet<Song> sortedSongs = new TreeSet<>();
      for (Song song : allSongs) {
        if (search.length() == 0
                || song.getTitle().toLowerCase().contains(search)
                || song.getArtist().toLowerCase().contains(search)) {
          sortedSongs.add(song);
        }
      }
      filteredSongs.clear();
      filteredSongs.addAll(sortedSongs);
    }
    displaySongList(filteredSongs);
  }

  /**
   *
   * @param filteredSongs
   */
  public void displaySongList(ArrayList<Song> filteredSongs) {

    this.filteredSongs = filteredSongs;
    songGrid.resize(filteredSongs.size(), columns);
    {
      int r = 0;
      for (Song song : filteredSongs) {
        songGrid.setHTML(r, 0, song.getTitle());
        songGrid.setHTML(r, 1, song.getArtist());
        r++;
      }
    }
  }

  private HandlerManager handlerManager;

  private static final int columns = 2;
  private ArrayList<Song> filteredSongs = new ArrayList<>();
  private final TreeSet<Song> allSongs = new TreeSet<>();
}
