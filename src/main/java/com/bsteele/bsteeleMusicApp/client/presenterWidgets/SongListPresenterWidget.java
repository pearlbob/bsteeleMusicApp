/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 *
 * @author bob
 */
public class SongListPresenterWidget extends PresenterWidget<SongListPresenterWidget.MyView> {

  public interface MyView extends View //implements DisplayMessageEvent.DisplayMessageHandler
  {

    void displayCurrentUserName(String username);

    Grid getSongGrid();

    TextBox getSongSearch();
  }

  //private final CurrentUserService currentUserService;
  @Inject
  SongListPresenterWidget(final EventBus eventBus,
          final MyView view
  //, CurrentUserService currentUserService
  ) {
    super(eventBus, view);

    //this.currentUserService = currentUserService;
  }

//    @Override
//    public void onDisplayMessage(DisplayMessageEvent event) {
//        getView().addMessage(event.getMessage());
//    }
  @Override
  public void onBind() {
    //addRegisteredHandler(DisplayMessageEvent.getType(), this);
    getView().displayCurrentUserName("bob"//currentUserService.getCurrentUsername()
    );

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

    TextBox songSearch = getView().getSongSearch();
    songSearch.addKeyUpHandler((event) -> {
      searchSongs(songSearch.getValue());
    });

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
    Grid songGrid = getView().getSongGrid();
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
