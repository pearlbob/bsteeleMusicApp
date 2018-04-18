/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.SongReadEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongReadEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSelectionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSelectionEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.gwtplatform.mvp.client.ViewImpl;
import org.vectomatic.file.File;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.impl.FileListImpl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
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
    SelectElement listBySelect;

    @UiField
    FileUpload readSongFiles;
    // multiple accept=".songlyrics"

    @UiField
    Grid songGrid;
    @UiField
    Label listCount;

    @Inject
    SongListView(Binder binder) {
        initWidget(binder.createAndBindUi(this));

        handlerManager = new HandlerManager(this);

        songGrid.addClickHandler(event -> {
            if (filteredSongs != null && songGrid != null) {
                HTMLTable.Cell cell = songGrid.getCellForEvent(event);
                if (cell != null) {
                    Song selectedSong = filteredSongs.get(cell.getRowIndex());
                    fireEvent(new SongSelectionEvent(selectedSong));
                }
            }
        });

        songSearch.addKeyUpHandler((event) -> {
            searchSongs(songSearch.getValue());
        });

        /**
         * The X button
         */
        clearSearch.addClickHandler((event) -> {
            songSearch.setText("");
            searchSongs(songSearch.getValue());
            songSearch.setFocus(true);
        });

        //  list by selection
        Event.sinkEvents(listBySelect, Event.ONCHANGE);
        Event.setEventListener(listBySelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                songComparator = Song.getComparatorByType(Song.ComparatorType.valueOf(listBySelect.getValue()));
                searchSongs(songSearch.getValue());
            }
        });

        /**
         * Song file read
         */
        readSongFiles.addChangeHandler((event) -> {
            FileList files = new FileList(getFiles(event.getNativeEvent()));

            int limit = files.getLength();
            for (int i = 0; i < limit; i++) {
                asyncReadSongFile(files.getItem(i));
            }
            clearFiles(event.getNativeEvent()); //  clear files for a new "change"
        });

        songSearch.setFocus(true);

        //  work around GWT to allow multiple files in a selection
        readSongFiles.getElement().setPropertyString("multiple", "multiple");
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

    @Override
    public HandlerRegistration addSongReadEventHandler(SongReadEventHandler handler) {
        return handlerManager.addHandler(SongReadEvent.TYPE, handler);
    }

    private void searchSongs(String search) {
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
                        "<div class=\"com-bsteele-bsteeleMusicApp-client-resources-AppResources-Style-songListItem\">"
                                + song.getTitle() + "</div>");
                songGrid.setHTML(r, 1,
                        "<div class=\"com-bsteele-bsteeleMusicApp-client-resources-AppResources-Style-songListItemData\">"
                                + song.getArtist() + "</div>");
                songGrid.setHTML(r, 2,
                        "<div class=\"com-bsteele-bsteeleMusicApp-client-resources-AppResources-Style-songListItemData\">"
                                + (song.getLastModifiedDate() == null ? "unloved since 2017" : song.getLastModifiedDate().toDateString()) + "</div>");
                r++;
            }
        }
        listCount.setText("Count: "+Integer.toString(filteredSongs.size()));
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
            Song song = Song.fromJson(reader.getStringResult());
            song.setLastModifiedDate(file.getLastModifiedDate());
            fireEvent(new SongReadEvent(song));
        });
        reader.readAsText(file);
    }

    private final HandlerManager handlerManager;

    private static final int columns = 3;
    private ArrayList<Song> filteredSongs = new ArrayList<>();
    private final TreeSet<Song> allSongs = new TreeSet<>();
    private Comparator<Song> songComparator = Song.getComparatorByType(Song.ComparatorType.title);
}
