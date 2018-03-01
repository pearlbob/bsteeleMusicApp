/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.songs.SongSelectionEvent;
import com.bsteele.bsteeleMusicApp.client.application.songs.SongSelectionEventHandler;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.shared.Song;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author bob
 */
public class SongListPresenterWidget extends PresenterWidget<SongListPresenterWidget.MyView>
        implements SongSelectionEventHandler {

  public interface MyView extends View
  {

    HandlerRegistration addSongSelectionEventHandler(
            SongSelectionEventHandler handler);

    void setSongList(Set<Song> songs);
  }

  @Inject
  SongListPresenterWidget(final EventBus eventBus,
          final MyView view
  ) {
    super(eventBus, view);

    this.eventBus = eventBus;
    this.view = view;

    addSonglist(AppResources.INSTANCE.legacySongsAsJsonString().getText());
    addSonglist(AppResources.INSTANCE.allSongsAsJsonString().getText());
  }

  @Override
  public void onBind() {
    view.addSongSelectionEventHandler(this);
  }

  @Override
  public void onSongSelection(SongSelectionEvent event) {
    eventBus.fireEvent(event);
  }

  private void addSonglist(String jsonString) {
    if (jsonString != null && jsonString.length() > 0) {
      JSONValue jv = JSONParser.parseStrict(jsonString);
      if (jv != null) {
        JSONArray ja = jv.isArray();
        if (ja != null) {
          int jaLimit = ja.size();
          for (int i = 0; i < jaLimit; i++) {
            Song song = Song.songFromJsonObject(ja.get(i).isObject());
            if (song != null) {
              allSongs.remove(song);  //  remove prior versions
              allSongs.add(song);
            }
          }
        }
      }
    }
    getView().setSongList(allSongs);
  }

  private final TreeSet<Song> allSongs = new TreeSet<>();
  private final EventBus eventBus;
  private final MyView view;
}
