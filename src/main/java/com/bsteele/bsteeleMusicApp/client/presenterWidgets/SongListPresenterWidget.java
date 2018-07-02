/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.*;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.util.ClientFileIO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author bob
 */
public class SongListPresenterWidget extends PresenterWidget<SongListPresenterWidget.MyView>
        implements
        SongUpdateEventHandler,
        SongSubmissionEventHandler,
        SongReadEventHandler,
        NextSongEventHandler,
        AllSongWriteEventHandler {


    public interface MyView extends View {

        HandlerRegistration addSongUpdateEventHandler(SongUpdateEventHandler handler);

        HandlerRegistration addSongReadEventHandler(SongReadEventHandler handler);

        void setSongList(Set<Song> songs);

        void nextSong(boolean forward);
    }

    @Inject
    SongListPresenterWidget(final EventBus eventBus,
                            final MyView view
    ) {
        super(eventBus, view);

        this.eventBus = eventBus;
        this.view = view;

        //addJsonToSongList(AppResources.INSTANCE.legacySongsAsJsonString().getText());
        addJsonToSongList(AppResources.INSTANCE.allSongsAsJsonString().getText());
    }

    @Override
    public void onBind() {
        view.addSongUpdateEventHandler(this);
        view.addSongReadEventHandler(this);

        eventBus.addHandler(SongSubmissionEvent.TYPE, this);
        eventBus.addHandler(AllSongWriteEvent.TYPE, this);
        eventBus.addHandler(NextSongEvent.TYPE, this);
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
        ArrayList<Song> songs = event.getSongs();
        if (!songs.isEmpty()) {
            for (Song song : songs)
                addToSongList(song);
            view.setSongList(allSongs);
            Song song = songs.get(songs.size() - 1);
            fireEvent(new SongUpdateEvent(song));
        }
    }

    @Override
    public void onAllSongWrite(AllSongWriteEvent event) {
        Date now = new Date();
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyyMMdd_HHmmss");

        String data = Song.toJson(allSongs);
        saveSongAs("allSongs_" + fmt.format(now) + ".songlyrics", data);
        //allSongs.clear(); view.setSongList(allSongs);// testParse only
    }


    private void addJsonToSongList(String jsonString) {
        if (jsonString != null && jsonString.length() > 0) {
            JSONValue jv = JSONParser.parseStrict(jsonString);
            if (jv != null) {
                JSONArray ja = jv.isArray();
                if (ja != null) {
                    int jaLimit = ja.size();
                    //  order by oldest first
                    TreeSet<Song> sortedSet = new TreeSet<>(Song.getComparatorByType(Song.ComparatorType.versionNumber));
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
                allSongs.remove(song);  //  remove any prior version
            }
            allSongs.add(song);
        }
    }

    @Override
    public void onNextSong(NextSongEvent event) {
        getView().nextSong(event.isForward());
    }


    /**
     * Native function to write the song as JSON.
     *
     * @param filename
     * @param data
     */
    private void saveSongAs(String filename, String data) {
        ClientFileIO.saveDataAs(filename, data);
    }

    private final TreeSet<Song> allSongs = new TreeSet<>();
    private final EventBus eventBus;
    private final MyView view;
}
