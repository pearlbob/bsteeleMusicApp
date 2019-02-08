/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.SongReadEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongReadEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongFile;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.util.ClientFileIO;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewImpl;
import org.vectomatic.file.File;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.impl.FileListImpl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * @author bob
 */
public class SongListView
        extends ViewImpl
        implements SongListPresenterWidget.MyView,
        AttachEvent.Handler,
        HasHandlers {


    interface Binder extends UiBinder<Widget, SongListView> {
    }

    @UiField
    DockLayoutPanel dockLayoutPanel;

    @UiField
    TextBox songSearch;

    @UiField
    Button clearSearch;

    @UiField
    SelectElement listBySelect;

    @UiField
    FileUpload readSongFiles;
    // multiple accept=".songlyrics"

    @UiField
    Grid songGrid;

    @UiField
    ScrollPanel songListScrollPanel;

    @UiField
    Label listCount;
    @UiField
    Button testParse;
    @UiField
    Button removeAll;

    @Inject
    SongListView(@Nonnull final EventBus eventBus, @Nonnull Binder binder) {
        initWidget(binder.createAndBindUi(this));

        handlerManager = new HandlerManager(this);

        songSearch.addAttachHandler(this);

        this.eventBus = eventBus;

        songGrid.addClickHandler(event -> {
            if (filteredSongs != null && songGrid != null) {
                HTMLTable.Cell cell = songGrid.getCellForEvent(event);
                if (cell != null) {
                    selectedSong = filteredSongs.get(cell.getRowIndex());
                    fireEvent(new SongUpdateEvent(selectedSong));
                }
            }
        });

        songSearch.addKeyUpHandler((event) -> {
            searchSongs();
        });

        /**
         * The X button
         */
        clearSearch.addClickHandler((event) -> {
            songSearch.setText("");
            searchSongs();
        });

        //  list by selection
        Event.sinkEvents(listBySelect, Event.ONCHANGE);
        Event.setEventListener(listBySelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                songComparator = Song.getComparatorByType(Song.ComparatorType.valueOf(listBySelect.getValue()));
                searchSongs();
            }
        });

        /**
         * Song file read
         */
        readSongFiles.addChangeHandler((event) -> {
            FileList files = new FileList(getFiles(event.getNativeEvent()));

            int limit = files.getLength();
            TreeSet<SongFile> reducedSongFiles = new TreeSet<>();
            for (int i = 0; i < limit; i++) {
                SongFile f = new SongFile(files.getItem(i));
                reducedSongFiles.add(f);
            }
//            SongFile lastSongFile = null;
            for (SongFile songFile : reducedSongFiles) {
//                if (lastSongFile != null && lastSongFile.getSongTitle().equals(songFile.getSongTitle()))
//                    eventBus.fireEvent(new StatusEvent("song read skip",
//                            "\"" + songFile.getFile().getName() +
//                                    "\", used \"" + lastSongFile.getFile().getName() + "\""));
//                else
                asyncReadSongFile(songFile.getFile());
//                lastSongFile = songFile;
            }
            clearFiles(event.getNativeEvent()); //  clear files for a new "change"
        });

        //  work around GWT to allow multiple files in a selection
        readSongFiles.getElement().setPropertyString("multiple", "multiple");

        testParse.addClickHandler((event) -> {
            TreeSet<Song> sortedSongs = new TreeSet<>(songComparator);
            for (Song song : allSongs) {
                try {
                    song.checkSong();
                } catch (ParseException pe) {
                    sortedSongs.add(song);
                }
            }
            filteredSongs.clear();
            filteredSongs.addAll(sortedSongs);
            displaySongList(filteredSongs);
        });

        removeAll.addClickHandler((event) -> {
            allSongs.clear();
            searchSongs();
        });
    }

    @Override
    public void onAttachOrDetach(AttachEvent event) {
        songSearch.setFocus(true);
        songSearch.selectAll();
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }


    @Override
    public HandlerRegistration addSongUpdateEventHandler(SongUpdateEventHandler handler) {
        return handlerManager.addHandler(SongUpdateEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addSongReadEventHandler(SongReadEventHandler handler) {
        return handlerManager.addHandler(SongReadEvent.TYPE, handler);
    }

    @Override
    public void saveAllSongsAs(String fileName) {
        String data = Song.toJson(allSongs);
        saveSongAs(fileName, data);
    }


    /**
     * Native function to write the song as JSON.
     *
     * @param filename
     * @param data
     */
    @Override
    public void saveSongAs(String filename, String data) {
        ClientFileIO.saveDataAs(filename, data);
    }


    @Override
    public boolean addToSongList(Song song, boolean force) {
        if (song != null) {
            if (allSongs.contains(song)) {
                Song oldSong = allSongs.floor(song);
                if (!force && Song.compareByVersionNumber(oldSong, song) > 0) {
                    GWT.log("\"" + song.toString() + "\" cannot replace: \"" + oldSong.toString() + "\"");
                    return false;
                }
                allSongs.remove(oldSong);  //  remove any prior version
                GWT.log("\"" + song.toString() + "\" replaces: \"" + oldSong.toString() + "\"");
            }
            return allSongs.add(song);
        }
        return false;
    }

    @Override
    public void removeAll(ArrayList<Song> songs) {
        allSongs.removeAll(songs);
    }

    @Override
    public void displaySongList() {
        searchSongs();
    }

    @Override
    public void selectAllSearch() {
        songSearch.selectAll();
        songSearch.setFocus(true);
    }

    private void searchSongs() {
        String search = songSearch.getValue();
        if (search == null) {
            search = "";
        }
        search = search.replaceAll("[^\\w\\s']+", "");
        search = search.toLowerCase();
        {
            TreeSet<Song> sortedSongs = new TreeSet<>(songComparator);
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

        {
            //  roll the songs on an empty select
            int scrollPosition = 0;
            if (search == "" && songGrid.getOffsetHeight() > 0) {
                songListScrollOffset = songListScrollOffset + songGrid.getOffsetHeight() / 20;
                if (songListScrollOffset > songGrid.getOffsetHeight())
                    songListScrollOffset = 0;
                scrollPosition = songListScrollOffset;
            }
            songListScrollPanel.setVerticalScrollPosition(scrollPosition);// in pixels
        }
    }

    /**
     * @param filteredSongs
     */
    public void displaySongList(ArrayList<Song> filteredSongs) {

        this.filteredSongs = filteredSongs;
        songGrid.resize(filteredSongs.size(), columns);
        {
            int r = 0;
            for (Song song : filteredSongs) {
                songGrid.setHTML(r, 0,
                        "<div class=\"" + CssConstants.style + "songListItem\">"
                                + song.getTitle() + "</div>");
                songGrid.setHTML(r, 1,
                        "<div class=\"" + CssConstants.style + "songListItemData\">"
                                + song.getArtist() + "</div>");
                songGrid.setHTML(r, 2,
                        "<div class=\"" + CssConstants.style + "songListItemData\">"
                                + (song.getLastModifiedDate() == null ? "unloved since 2017" : song
                                .getLastModifiedDate().toDateString()) + "</div>");
                r++;
            }
        }
        listCount.setText("Count: " + Integer.toString(filteredSongs.size()) + " / " + Integer.toString(allSongs.size()));

        songSearchFocus();
    }

    private void songSearchFocus() {

        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                songSearch.setFocus(true);
            }
        });
    }

    @Override
    public void nextSong(boolean forward, boolean force) {
        //  nothing to move to
        if (filteredSongs.size() < 2) {
            if (force)
                fireEvent(new SongUpdateEvent(new SongUpdate()));  //   empty song
            return;
        }

        if (selectedSong == null || !Song.containsSongTitleAndArtist(filteredSongs, selectedSong)) {
            selectedSong = filteredSongs.get(0);
            fireEvent(new SongUpdateEvent(selectedSong));   //  arbitrary choice of the first
        } else
            for (int i = 0; i < filteredSongs.size(); i++)
                if (selectedSong.compareTo(filteredSongs.get(i)) == 0) // title and artist only
                {
                    selectedSong = filteredSongs.get(Util.mod(i + (forward ? 1 : -1), filteredSongs.size()));
                    fireEvent(new SongUpdateEvent(selectedSong));
                    break;
                }
    }

    @Override
    public Song getSelectedSong() {
        return selectedSong;
    }

    private native FileListImpl getFiles(NativeEvent event)/*-{
        var ret = event.target.files;
        if (ret.length <= 0)
            return null;

        return ret;
    }-*/;

    /**
     * JS hack to allow the same file or files to be reloaded
     * if selected again.  Otherwise there is no change event.
     *
     * @param event
     */
    private native void clearFiles(NativeEvent event)/*-{
        event.srcElement.value = null;
    }-*/;

    private void asyncReadSongFile(Object entry) {

        File file = (File) entry;

        FileReader reader = new FileReader();
        reader.addLoadEndHandler((LoadEndEvent event) -> {
            ArrayList<Song> songs = Song.fromJson(reader.getStringResult());
            if (songs.size() == 1) {
                Song song = songs.get(0);
                song.setLastModifiedDate(file.getLastModifiedDate());
                song.setFileName(file.getName());
                fireEvent(new SongReadEvent(song));
            } else
                fireEvent(new SongReadEvent(songs));
        });
        reader.readAsText(file);
    }

    private final HandlerManager handlerManager;
    private final EventBus eventBus;

    private static final int columns = 3;
    private ArrayList<Song> filteredSongs = new ArrayList<>();
    private final TreeSet<Song> allSongs = new TreeSet<>();
    private Song selectedSong;
    private Comparator<Song> songComparator = Song.getComparatorByType(Song.ComparatorType.title);
    private int songListScrollOffset = 0;
}
