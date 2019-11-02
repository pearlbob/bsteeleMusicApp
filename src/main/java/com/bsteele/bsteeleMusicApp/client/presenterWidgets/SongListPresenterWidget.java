/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.AllSongWriteEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.AllSongWriteEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongReadEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongReadEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongRemoveEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongRemoveEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.application.home.AppTab;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.util.ClientFileIO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class SongListPresenterWidget extends PresenterWidget<SongListPresenterWidget.MyView>
        implements
        SongUpdateEventHandler,
        SongReadEventHandler,
        SongRemoveEventHandler,
        NextSongEventHandler,
        AllSongWriteEventHandler,
        HomeTabEventHandler {


    public interface MyView extends View {

        HandlerRegistration addSongUpdateEventHandler(SongUpdateEventHandler handler);

        HandlerRegistration addSongReadEventHandler(SongReadEventHandler handler);

        boolean addToSongList(Song song, boolean force);

        void removeAll(ArrayList<Song> songs);

        void displaySongList();

        void selectAllSearch();

        void saveSongAs(String filename, String data);

        void saveAllSongsAs(String fileName);

        void nextSong(boolean forward, boolean force);

        Song getSelectedSong();
    }

    @Inject
    SongListPresenterWidget(final EventBus eventBus,
                            final MyView view) {
        super(eventBus, view);

        this.eventBus = eventBus;
        this.view = view;

        String url = (GWT.getHostPageBaseURL()+ "allSongs.songlyrics").replace("/beta","");
        if (url.matches("^http://127.0.0.1.*") || url.matches("^http://local.host.*")) {
            //  expedite debugging
            defaultAllSongs();
        } else {
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
            try {
                requestBuilder.sendRequest(null, new RequestCallback() {
                    public void onError(Request request, Throwable exception) {
                        logger.info("failed file reading: " + exception.getMessage());
                        sendStatus("failed reading", exception.getMessage());
                        defaultAllSongs();
                    }

                    public void onResponseReceived(Request request, Response response) {
                        //GWT.log("response.getStatusCode(): "+ response.getStatusCode());
                        if (response.getStatusCode() == 200) {
                            logger.info("read songs from: " + url);

                            //  all songs from server
                            try {
                                addJsonToSongList(response.getText());
                            } catch (ParseException pex) {
                                logger.info("unexpected parse error from server: " + pex.getMessage());
                            }

                            sendStatus("read", url);
                        } else {

                            logger.info("failed reading: " + url);
                            logger.info("Used internal copy instead.");
                            sendStatus("failed reading", url);
                            defaultAllSongs();
                        }
                    }
                });
            } catch (Exception e) {
                logger.info("RequestException for " + url + ": " + e.getMessage());
                sendStatus("Exception", e.getMessage());
            }
        }

    }

    private void defaultAllSongs() {
        //  default all songs
        try {
            addJsonToSongList(AppResources.INSTANCE.allSongsAsJsonString().getText());
            logger.info("Used internal copy allSongs.songlyrics");
        } catch (ParseException pex) {
            logger.info("unexpected parse error internal allSongs: " + pex.getMessage());
        }
    }

    @Override
    public void onBind() {
        view.addSongUpdateEventHandler(this);
        view.addSongReadEventHandler(this);

        eventBus.addHandler(AllSongWriteEvent.TYPE, this);
        eventBus.addHandler(NextSongEvent.TYPE, this);
        eventBus.addHandler(SongRemoveEvent.TYPE, this);
        eventBus.addHandler(HomeTabEvent.TYPE, this);
    }

    @Override
    public void onSongUpdate(SongUpdateEvent event) {
        eventBus.fireEvent(event);
    }

    @Override
    public void onSongRead(SongReadEvent event) {
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
    public void onAllSongWrite(AllSongWriteEvent event) {
        view.saveAllSongsAs("allSongs.songlyrics");
    }


    @Override
    public void onSongRemove(SongRemoveEvent event) {
        ArrayList<Song> songs = event.getSongs();
        if (!songs.isEmpty()) {
            if (view.getSelectedSong() != null && Song.containsSongTitleAndArtist(songs, view.getSelectedSong()))
                view.nextSong(true, true);   //  fixme: not always good enough?

            view.removeAll(songs);
            view.displaySongList();
        }
    }


    @Override
    public void onHomeTab(HomeTabEvent event) {
        if (event.getTab() == AppTab.songs) {
            view.displaySongList();
            view.selectAllSearch();
        }
    }


    private void addJsonToSongList(String jsonString) throws ParseException {
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
    public void onNextSong(NextSongEvent event) {
        getView().nextSong(event.isForward(), false);
    }

    private void sendStatus(String name, String value) {
        eventBus.fireEvent(new StatusEvent(name, value));
    }

    /**
     * Native function to write the song as JSON.
     *
     * @param filename the file to be written to
     * @param data     the json data to write
     */
    private void saveSongAs(String filename, String data) {
        ClientFileIO.saveDataAs(filename, data);
    }

    private final EventBus eventBus;
    private final MyView view;

    private static final Logger logger = Logger.getLogger(SongListPresenterWidget.class.getName());
}
