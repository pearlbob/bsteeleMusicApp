/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.GWTAppOptions;
import com.bsteele.bsteeleMusicApp.client.application.events.SongReadEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongReadEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongFile;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.util.ClientFileIO;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * @author bob
 */
public class SongListView
        extends ViewImpl
        implements SongListPresenterWidget.MyView,
        SongSubmissionEventHandler,
        SongReadPopup.SongReadResponse,
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
    SelectElement filterByComplexity;
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

        //this.eventBus = eventBus;

        eventBus.addHandler(SongSubmissionEvent.TYPE, this);

        songGrid.addClickHandler(event -> {
            if (filteredSongs != null && songGrid != null) {
                HTMLTable.Cell cell = songGrid.getCellForEvent(event);
                if (cell != null) {
                    selectedSong = filteredSongs.get(cell.getRowIndex());
                    fireEvent(new SongUpdateEvent(selectedSong));
                }
            }
        });

        songSearch.addKeyUpHandler((event) -> searchSongs());

        /*
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

        Event.sinkEvents(filterByComplexity, Event.ONCHANGE);
        Event.setEventListener(filterByComplexity, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                setComplexityFilter(filterByComplexity.getValue());
            }
        });

        /*
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
            for (SongFile songFile : reducedSongFiles) {
                asyncReadSongFile(songFile.getFile());
            }
            clearFiles(event.getNativeEvent()); //  clear files for a new "change"
        });

        //  work around GWT to allow multiple files in a selection
        readSongFiles.getElement().setPropertyString("multiple", "multiple");

        testParse.addClickHandler((event) -> {
            TreeSet<Song> sortedSongs = new TreeSet<>(songComparator);
            for (Song song : allSongs) {
                try {
                    //Song newSong =
                    song.checkSong();

//  do not            //  include commented songs
//                    if (newSong.getMessage() != null)
//                        sortedSongs.add(song);

                } catch (ParseException pe) {
                    //  parse error
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

        popup = new SongReadPopup(this);

        scheduler.scheduleFinally(() -> songSearch.setFocus(true));
    }

    private void setComplexityFilter(String value) {
        try {
            complexityFilter = ComplexityFilter.valueOf(value);
        } catch (IllegalArgumentException ex) {
            complexityFilter = ComplexityFilter.all;
        }
        displaySongList();
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
        Date now = new Date();
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyyMMdd_HHmmss");

        String data = Song.toJson(allSongs);
        saveSongAs("allSongs_" + fmt.format(now) + ".songlyrics", data);
    }

    /**
     * Native function to write the song as JSON.
     *
     * @param filename the file name to write
     * @param data     the json data to write
     */
    @Override
    public void saveSongAs(String filename, String data) {
        ClientFileIO.saveDataAs(filename, data);
    }


    @Override
    public boolean addToSongList(Song song, boolean force) {
        if (song == null)
            return false;

        if (allSongs.contains(song)) {
            String message;
            Song oldSong = allSongs.floor(song);
            if (oldSong == null)
                return allSongs.add(song);      //  how would this happen?

            if (oldSong.equals(song))
                return false;

            if (force) {
                allSongs.remove(oldSong);  //  remove any prior version
                return allSongs.add(song);
            }

            if (appOptions.isAlwaysUseTheNewestSongOnRead()) {
                if (song.getLastModifiedTime() < oldSong.getLastModifiedTime())
                    return false;
                else {
                    allSongs.remove(oldSong);  //  remove any prior version
                    return allSongs.add(song);
                }
            }
//            if (!force && Song.compareByVersionNumber(oldSong, song) > 0) {
//                message = "song parse: \"" + song.toString() + "\" cannot replace: \"" + oldSong.toString() + "\"";
//                popupOver(message, oldSong, song);
//                logger.info(message);
//                return false;
//            }


            message = "Should: \"" + song.toString() + "\" replace: \"" + oldSong.toString() + "\" ?";
            popupOver(message, oldSong, song);
            logger.info(message);
            return false;
        }
        return allSongs.add(song);
    }

    @Override
    public void onSongSubmission(SongSubmissionEvent event) {
        Song song = event.getSong();

        logger.info("onSongSubmission: " + song.getTitle()
                + ": " +
                song.getRawLyrics().substring(0, 40)
        );

        if (event.isWriteSong()) {
            String filename = song.getTitle() + ".songlyrics";

            saveSongAs(filename, song.toJson());
        }

        addToSongList(song, true);
        displaySongList();
        fireEvent(new SongUpdateEvent(song));
    }

    private void popupOver(String message, Song oldSong, Song newSong) {
        if (songReadList.isEmpty()) {
            songReadList.add(new SongData(message, oldSong, newSong));
            processSongReadList();
        } else
            songReadList.add(new SongData(message, oldSong, newSong));
    }

    private void processSongReadList() {
        if (!songReadList.isEmpty()) {
            SongData songData = songReadList.get(0);
            popup.setPopupPositionAndShow((offsetWidth, offsetHeight) -> {
                int left = (Window.getClientWidth() - offsetWidth) / 3;
                popup.setPopupPosition(left, 0);
            });
            popup.processSong(songData.message, songData.oldSong, songData.newSong);
        }
    }

    @Override
    public void songReadResponse(SongReadPopup.SongReadResponseEnum value) {
        switch (value) {
            default:
            case keepTheOld:
                //  do nothing
                songReadList.remove(0);
                processSongReadList();
                break;
            case useThenew:
                Song song = songReadList.remove(0).newSong;
                addToSongList(song, true);
                displaySongList();
                if (songReadList.isEmpty())
                    fireEvent(new SongUpdateEvent(song));
                else
                    processSongReadList();
                break;
            case cancel:
                songReadList.clear();
                break;
        }
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

        //  apply complexity filster
        TreeSet<Song> allSongsFiltered = allSongs;
        if (complexityFilter != ComplexityFilter.all) {
            TreeSet<Song> sortedSongs = new TreeSet<>(Song.getComparatorByType(Song.ComparatorType.complexity));
            sortedSongs.addAll(allSongs);
            double factor = 1.0;
            switch (complexityFilter) {
                case veryEasy:
                    factor = 1.0 / 4;
                    break;
                case easy:
                    factor = 2.0 / 4;
                    break;
                case moderate:
                    factor = 3.0 / 4;
                    break;
            }
            int limit = (int) (factor * sortedSongs.size());
            Song[] allSongsFilteredList = sortedSongs.toArray(new Song[0]);
            allSongsFiltered = new TreeSet<>(Arrays.asList(allSongsFilteredList).subList(0, limit));
        }

        //  apply search filter
        {
            TreeSet<Song> sortedSongs = new TreeSet<>(songComparator);
            for (Song song : allSongsFiltered) {
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

        if ("title".equals(listBySelect.getValue()) && "".equals(search)) {

            //  fixme: takes too long to execute... according to chrome
            //  fixme: try caching the grid based on search term, complexityFilter.  clear on song add/remove
            scheduler.scheduleDeferred(() -> {

                //  collect the full height
                {
                    int h = songGrid.getOffsetHeight();
                    if (h > 0)
                        gridHeight = h;
                }

                //  roll the songs on an empty title select
                if (gridHeight > 0) {
                    songListScrollOffset = songListScrollOffset + gridHeight / 20;
                    if (songListScrollOffset > gridHeight)
                        songListScrollOffset = 0;
                } else
                    songListScrollOffset = 0;

                songListScrollPanel.setVerticalScrollPosition(songListScrollOffset);// in pixels
                logger.finest("songListScrollOffset: " + songListScrollOffset);
            });
        }
    }

    /**
     * @param filteredSongs the songs to display
     */
    public void displaySongList(ArrayList<Song> filteredSongs) {

        this.filteredSongs = filteredSongs;
        JsDate jsDate = JsDate.create();

        songGrid.resize(filteredSongs.size(), columns);
        {
            int r = 0;
            for (Song song : filteredSongs) {
                songGrid.setHTML(r, 0,
                        "<div class=\"" + CssConstants.style + "songListItem\">"
                                + song.getTitle()
                                + (song.getFileVersionNumber() > 0
                                ? " (" + song.getFileVersionNumber() + ")"
                                : "")
                                + "</div>");
                songGrid.setHTML(r, 1,
                        "<div class=\"" + CssConstants.style + "songListItemData\">"
                                + song.getArtist() + "</div>");
                String timeAsString = "unloved since 2017";
                if (song.getLastModifiedTime() > 0) {
                    jsDate.setTime(song.getLastModifiedTime());
                    timeAsString = jsDate.toDateString();
                }
                songGrid.setHTML(r, 2,
                        "<div class=\"" + CssConstants.style + "songListItemData\">"
                                + timeAsString + "</div>");
                r++;
            }
        }
        listCount.setText("Count: " + filteredSongs.size() + " / " + allSongs.size());

        songSearchFocus();
    }

    private void songSearchFocus() {
        scheduler.scheduleFinally(() -> songSearch.setFocus(true));
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

//    private void sendStatus(String name, String value) {
//        eventBus.fireEvent(new StatusEvent(name, value));
//    }

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
     * @param event the event to hack
     */
    private native void clearFiles(NativeEvent event)/*-{
        event.srcElement.value = null;
    }-*/;

    private void asyncReadSongFile(Object entry) {

        File file = (File) entry;

        FileReader reader = new FileReader();
        reader.addLoadEndHandler((LoadEndEvent event) -> {
            ArrayList<Song> songs = Song.fromJson(reader.getStringResult());
            if (songs == null)
                logger.warning("file parse failure on: " + file.getName());
            else if (songs.size() == 1) {
                Song song = songs.get(0);
                song.setLastModifiedTime(file.getLastModifiedDate().getTime());
                song.setFileName(file.getName());
                fireEvent(new SongReadEvent(song));
            } else
                fireEvent(new SongReadEvent(songs));
        });
        reader.readAsText(file);
    }

    private enum ComplexityFilter {
        all,
        veryEasy,
        easy,
        moderate
    }

    private ComplexityFilter complexityFilter = ComplexityFilter.all;

    private static class SongData {
        SongData(String message, Song oldSong, Song newSong) {
            this.message = message;
            this.oldSong = oldSong;
            this.newSong = newSong;
        }

        final String message;
        final Song oldSong;
        final Song newSong;
    }

    private final HandlerManager handlerManager;
    //private final EventBus eventBus;

    private static final int columns = 3;
    private ArrayList<Song> filteredSongs = new ArrayList<>();
    private final TreeSet<Song> allSongs = new TreeSet<>();
    private Song selectedSong;
    private Comparator<Song> songComparator = Song.getComparatorByType(Song.ComparatorType.title);
    private int songListScrollOffset = 0;
    private int gridHeight;
    private static final AppOptions appOptions = GWTAppOptions.getInstance();
    private static final Scheduler scheduler = Scheduler.get();
    private SongReadPopup popup;
    private ArrayList<SongData> songReadList = new ArrayList<>();

    private static final Logger logger = getLogger(SongListView.class.getName());
}
