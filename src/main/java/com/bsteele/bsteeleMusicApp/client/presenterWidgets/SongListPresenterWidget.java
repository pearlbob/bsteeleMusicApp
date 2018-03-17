/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.*;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
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
        implements SongSelectionEventHandler,
        SongSubmissionEventHandler,
        SongReadEventHandler {

  public interface MyView extends View {

    HandlerRegistration addSongSelectionEventHandler(
            SongSelectionEventHandler handler);

    HandlerRegistration addSongReadEventHandler(
            SongReadEventHandler handler);

    void setSongList(Set<Song> songs);
  }

  @Inject
  SongListPresenterWidget(final EventBus eventBus,
          final MyView view
  ) {
    super(eventBus, view);

    this.eventBus = eventBus;
    this.view = view;

    addJsonToSonglist(AppResources.INSTANCE.legacySongsAsJsonString().getText());
    addJsonToSonglist(AppResources.INSTANCE.allSongsAsJsonString().getText());
  }

  @Override
  public void onBind() {
    view.addSongSelectionEventHandler(this);
    view.addSongReadEventHandler(this);

    eventBus.addHandler(SongSubmissionEvent.TYPE, this);
  }

  @Override
  public void onSongSelection(SongSelectionEvent event) {
    eventBus.fireEvent(event);
  }

  @Override
  public void onSongSubmission(SongSubmissionEvent event) {
    Song song = event.getSong();
    String filename = song.getTitle() + ".songlyrics";

    saveSongAs(filename, song.toJson());

    addToSonglist(song);
    view.setSongList(allSongs);
    fireEvent(new SongSelectionEvent(song));
  }

  @Override
  public void onSongRead(SongReadEvent event) {
    Song song = event.getSong();
    addToSonglist(song);
    view.setSongList(allSongs);
    fireEvent(new SongSelectionEvent(song));
  }

  private void addJsonToSonglist(String jsonString) {
    if (jsonString != null && jsonString.length() > 0) {
      JSONValue jv = JSONParser.parseStrict(jsonString);
      if (jv != null) {
        JSONArray ja = jv.isArray();
        if (ja != null) {
          int jaLimit = ja.size();
          for (int i = 0; i < jaLimit; i++) {
            addToSonglist(Song.fromJsonObject(ja.get(i).isObject()));
          }
        }
      }
    }
    view.setSongList(allSongs);
  }

  private void addToSonglist(Song song) {
    if (song != null) {
      allSongs.remove(song);  //  remove any prior version
      allSongs.add(song);
    }
  }

  /**
   * Native function to write the song as JSON.
   *
   * @param filename
   * @param data
   */
  private native void saveSongAs(String filename, String data) /*-{
    var data = new Blob([data], {type: 'text/plain'});
    // If we are replacing a previously generated file we need to
    // manually revoke the object URL to avoid memory leaks.
    //if (textFile !== null) {
    //    window.URL.revokeObjectURL(textFile);
    //}

    var textFile = window.URL.createObjectURL(data);
//    if (downloadlink === null) {
//        downloadlink = document.createElement("a");
//        downloadlink.style = "display:none";
//    }
    var downloadlink = document.createElement("a");
    downloadlink.style = "display:none";
    downloadlink.download = filename;
    downloadlink.href = textFile;
    downloadlink.click();
}-*/;

  private final TreeSet<Song> allSongs = new TreeSet<>();
  private final EventBus eventBus;
  private final MyView view;
}
