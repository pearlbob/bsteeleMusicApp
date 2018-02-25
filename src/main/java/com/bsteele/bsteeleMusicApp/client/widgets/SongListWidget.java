/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.widgets;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 *
 * @author bob
 */
public class SongListWidget extends Composite {

  interface MyUiBinder extends UiBinder<Widget, SongListWidget> {
  }
  private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField
  TextBox songSearch;

  @UiField
  Grid songGrid;

  public SongListWidget() {
    initWidget(uiBinder.createAndBindUi(this));

    {
      String jsonString = AppResources.INSTANCE.allSongsAsJsonString().getText();
      if (jsonString != null && jsonString.length() > 0) {
        JSONValue jv = JSONParser.parseStrict(jsonString);
        if (jv != null) {
          JSONArray ja = jv.isArray();
          if (ja != null) {
            int jaLimit = ja.size();
            for (int i = 0; i < jaLimit; i++) {
              Song song = Song.songFromJsonObject(ja.get(i).isObject());
              if (song != null) {
                songs.add(song);
              }
            }
          }
        }
      }
    }

    searchSongs(null);

    songSearch.addKeyUpHandler((event) -> {
      searchSongs(songSearch.getValue());
    });

    songSearch.setFocus(true);
  }

  @Override
  protected void onLoad() {
    songSearch.setFocus(true);
  }

  private void searchSongs(String search) {
    if (search == null) {
      search = "";
    }
    search = search.replaceAll("[^\\w\\s']+", "");
    search = search.toLowerCase();
    TreeSet<Song> sortedSongs = new TreeSet<>();
    for (Song song : songs) {
      if (search.length() == 0
              || song.getTitle().toLowerCase().contains(search)
              || song.getArtist().toLowerCase().contains(search)) {
        sortedSongs.add(song);
      }
    }
    songGrid.resize(sortedSongs.size(), columns);
    {
      int r = 0;
      for (Song song : sortedSongs) {
        songGrid.setHTML(r, 0, song.getTitle());
        songGrid.setHTML(r, 1, song.getArtist());
        r++;
      }
    }
  }

  private static final int columns = 2;
  private final ArrayList<Song> songs = new ArrayList<>();

}
