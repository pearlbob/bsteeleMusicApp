/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.*;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.core.client.GWT;
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
 * @author bob
 */
public class SongListPresenterWidget extends PresenterWidget<SongListPresenterWidget.MyView>
        implements
        SongUpdateEventHandler,
        SongSubmissionEventHandler,
        SongReadEventHandler {



    public interface MyView extends View {

        HandlerRegistration addSongUpdateEventHandler(
                SongUpdateEventHandler handler);

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

        addJsonToSongList(AppResources.INSTANCE.legacySongsAsJsonString().getText());
        addJsonToSongList(AppResources.INSTANCE.allSongsAsJsonString().getText());
    }

    @Override
    public void onBind() {
        view.addSongUpdateEventHandler(this);
        view.addSongReadEventHandler(this);

        eventBus.addHandler(SongSubmissionEvent.TYPE, this);
    }

    @Override
    public void onSongUpdate(SongUpdateEvent event) {
        eventBus.fireEvent(event);
    }

    @Override
    public void onSongSubmission(SongSubmissionEvent event) {
        Song song = event.getSong();
        String filename = song.getTitle() + ".songlyrics";

        saveSongAs(filename, song.toJson());

        addToSongList(song);
        view.setSongList(allSongs);
        fireEvent(new SongUpdateEvent(song));
    }

    @Override
    public void onSongRead(SongReadEvent event) {
        Song song = event.getSong();
        addToSongList(song);
        view.setSongList(allSongs);
        fireEvent(new SongUpdateEvent(song));
    }

    private void addJsonToSongList(String jsonString) {
        if (jsonString != null && jsonString.length() > 0) {
            JSONValue jv = JSONParser.parseStrict(jsonString);
            if (jv != null) {
                JSONArray ja = jv.isArray();
                if (ja != null) {
                    int jaLimit = ja.size();
                    //  order by oldest first
                    TreeSet<Song> sortedSet = new TreeSet<>(Song.getComparatorByType(Song.ComparatorType.lastModifiedDateLast));
                    for (int i = 0; i < jaLimit; i++) {
                        sortedSet.add(Song.fromJsonObject(ja.get(i).isObject()));
                    }
                    //  add to all songs
                    for (Song song : sortedSet) {
                        addToSongList(song);
                    }
                }
            }
        }
        view.setSongList(allSongs);
    }

    private void addToSongList(Song song) {
        if (song != null) {
            if (allSongs.contains(song)) {
                GWT.log("Dup: " + song.getTitle());
                allSongs.remove(song);  //  remove any prior version
            }
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
