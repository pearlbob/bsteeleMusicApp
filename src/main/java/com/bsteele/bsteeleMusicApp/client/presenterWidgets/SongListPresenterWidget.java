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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
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
        SongRemoveEventHandler,
        NextSongEventHandler,
        AllSongWriteEventHandler,
        HomeTabEventHandler
{


    public interface MyView extends View
    {

        HandlerRegistration addSongUpdateEventHandler(SongUpdateEventHandler handler);

        HandlerRegistration addSongReadEventHandler(SongReadEventHandler handler);

        boolean addToSongList(Song song, boolean force);

        void removeAll(ArrayList<Song> songs);

        void displaySongList();

        void saveSongAs(String filename, String data);

        void saveAllSongsAs(String fileName);

        void nextSong(boolean forward, boolean force);

        Song getSelectedSong();
    }

    @Inject
    SongListPresenterWidget(final EventBus eventBus,
                            final MyView view)
    {
        super(eventBus, view);

        this.eventBus = eventBus;
        this.view = view;

        String url = GWT.getHostPageBaseURL() + "allSongs.songlyrics";
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback()
            {
                public void onError(Request request, Throwable exception)
                {
                    GWT.log("failed file reading", exception);
                    //  default all songs
                    addJsonToSongList(AppResources.INSTANCE.allSongsAsJsonString().getText());
                }

                public void onResponseReceived(Request request, Response response)
                {
                    //GWT.log("response.getStatusCode(): "+ response.getStatusCode());
                    if (response.getStatusCode() == 200) {
                        addJsonToSongList(response.getText());
                        GWT.log("read songs from: " + url);
                    } else {
                        addJsonToSongList(AppResources.INSTANCE.allSongsAsJsonString().getText());
                        GWT.log("failed reading: " + url);
                    }
                }
            });
        } catch (Exception e) {
            GWT.log("RequestException for " + url + ": ", e);
        }
    }

    @Override
    public void onBind()
    {
        view.addSongUpdateEventHandler(this);
        view.addSongReadEventHandler(this);

        eventBus.addHandler(SongSubmissionEvent.TYPE, this);
        eventBus.addHandler(AllSongWriteEvent.TYPE, this);
        eventBus.addHandler(NextSongEvent.TYPE, this);
        eventBus.addHandler(SongRemoveEvent.TYPE, this);
        eventBus.addHandler(HomeTabEvent.TYPE, this);
    }

    @Override
    public void onSongUpdate(SongUpdateEvent event)
    {
        eventBus.fireEvent(event);
    }

    @Override
    public void onSongSubmission(SongSubmissionEvent event)
    {
        Song song = event.getSong();
        String filename = song.getTitle() + ".songlyrics";

        view.saveSongAs(filename, song.toJson());

        view.addToSongList(song, true);
        view.displaySongList();
        fireEvent(new SongUpdateEvent(song));
    }

    @Override
    public void onSongRead(SongReadEvent event)
    {
        ArrayList<Song> songs = event.getSongs();
        if (!songs.isEmpty()) {
            //  order by oldest first
            TreeSet<Song> sortedSet = new TreeSet<>(Song.getComparatorByType(
                    Song.ComparatorType.versionNumber));
            sortedSet.addAll(songs);
            Song lastSong = null;
            for (Song song : sortedSet)
                if (view.addToSongList(song, false))
                    lastSong = song;
            view.displaySongList();
            if (lastSong != null)
                fireEvent(new SongUpdateEvent(lastSong));
        }
    }

    @Override
    public void onAllSongWrite(AllSongWriteEvent event)
    {
        Date now = new Date();
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyyMMdd_HHmmss");

        view.saveAllSongsAs("allSongs_" + fmt.format(now) + ".songlyrics");
    }


    @Override
    public void onSongRemove(SongRemoveEvent event)
    {
        ArrayList<Song> songs = event.getSongs();
        if (!songs.isEmpty()) {
            if (view.getSelectedSong() != null && Song.containsSongTitleAndArtist(songs, view.getSelectedSong()))
                view.nextSong(true, true);   //  fixme: not always good enough?

            view.removeAll(songs);
            view.displaySongList();
        }
    }


    @Override
    public void onHomeTab(HomeTabEvent event)
    {
        view.displaySongList();
    }


    private void addJsonToSongList(String jsonString)
    {
        if (jsonString != null && jsonString.length() > 0) {
            JSONValue jv = JSONParser.parseStrict(jsonString);
            if (jv != null) {
                JSONArray ja = jv.isArray();
                if (ja != null) {
                    int jaLimit = ja.size();
                    //  order by oldest first
                    TreeSet<Song> sortedSet = new TreeSet<>(Song.getComparatorByType(
                            Song.ComparatorType.versionNumber));
                    for (int i = 0; i < jaLimit; i++) {
                        sortedSet.add(Song.fromJsonObject(ja.get(i).isObject()));
                    }
                    //  add to all songs
                    for (Song song : sortedSet) {
                        //  note: the highest number song will be added last
                        //  replacing any previous from the list
                        view.addToSongList(song, false);
                    }
                    view.displaySongList();
                }
            }
        }
    }


    @Override
    public void onNextSong(NextSongEvent event)
    {
        getView().nextSong(event.isForward(), false);
    }

    private final EventBus eventBus;
    private final MyView view;
}
