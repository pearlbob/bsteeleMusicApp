/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.LyricSection;
import com.bsteele.bsteeleMusicApp.shared.songs.LyricsLine;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class PlayerViewImpl
        extends CommonPlayViewImpl
        implements PlayerPresenterWidget.MyView {

    @UiField
    Button playStopButton;

    @UiField
    SpanElement keyLabel;
    @UiField
    Button originalKeyButton;
    @UiField
    Button keyUpButton;
    @UiField
    Button keyDownButton;

    @UiField
    TextBox currentBpmEntry;

    @UiField
    SelectElement bpmSelect;

    @UiField
    SpanElement timeSignature;

    @UiField
    Anchor title;

    @UiField
    Anchor artist;

    @UiField
    Button nextSongButton;
    @UiField
    Button prevSongButton;

    @UiField
    CanvasElement audioBeatDisplayCanvas;

    @UiField
    ScrollPanel chordsScrollPanel;

    @UiField
    HTMLPanel player;

    @UiField
    SpanElement copyright;


    interface Binder extends UiBinder<Widget, PlayerViewImpl> {
    }

    @Inject
    PlayerViewImpl(final EventBus eventBus, Binder binder, SongPlayMaster songPlayMaster) {
        super(eventBus, songPlayMaster);
        initWidget(binder.createAndBindUi(this));

        audioBeatDisplay = new AudioBeatDisplay(audioBeatDisplayCanvas);
        labelPlayStop();

        playStopButton.addClickHandler((ClickEvent event) -> {
            if (song != null) {
                switch (songUpdate.getState()) {
                    case playing:
                        songPlayMaster.stopSong();
                        break;
                    case idle:
                        SongUpdate songUpdate = new SongUpdate();
                        songUpdate.setSong(song.copySong());
                        songUpdate.setCurrentBeatsPerMinute(Integer.parseInt(currentBpmEntry.getValue()));
                        songUpdate.setCurrentKey(currentKey);
                        songPlayMaster.playSongUpdate(songUpdate);
                        break;
                }
            }
        });

        originalKeyButton.addClickHandler((ClickEvent event) -> {
            setCurrentKey(songUpdate.getSong().getKey());
        });

        keyUpButton.addClickHandler((ClickEvent event) -> {
            stepCurrentKey(+1);
        });
        keyDownButton.addClickHandler((ClickEvent event) -> {
            stepCurrentKey(-1);
        });

        currentBpmEntry.addChangeHandler((event) -> {
            setCurrentBpm(currentBpmEntry.getValue());
        });

        Event.sinkEvents(bpmSelect, Event.ONCHANGE);
        Event.setEventListener(bpmSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                setCurrentBpm(bpmSelect.getValue());
            }
        });

        prevSongButton.addClickHandler((ClickEvent event) -> {
            eventBus.fireEvent(new NextSongEvent(false));
        });
        nextSongButton.addClickHandler((ClickEvent event) -> {
            eventBus.fireEvent(new NextSongEvent());
        });

        keyLabel.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        keyLabel.getStyle().setWidth(3, Style.Unit.EM);
    }

    @Override
    public void onSongUpdate(SongUpdate songUpdate) {

        if (songUpdate == null || songUpdate.getSong() == null)
            return;     //  defense

        this.songUpdate = songUpdate;

        labelPlayStop();

        if (lastRepeatElement != null) {
            lastRepeatElement.setInnerText("x" + lastRepeatTotal);
            lastRepeatElement = null;
        }

        if (songUpdate.getState() != lastState) {
            switch (lastState) {
                case idle:
                    resetScroll(chordsScrollPanel);
                    break;
            }
            lastState = songUpdate.getState();

            setEnables();
        }

        //  turn on highlights if required
        switch (songUpdate.getState()) {
            case idle:
                break;
            case playing:
                if (songUpdate.getRepeatTotal() > 0) {
                    final String id = prefix + Song.genChordId(songUpdate.getSectionVersion(),
                            songUpdate.getRepeatLastRow(), songUpdate.getRepeatLastCol());
                    Element re = player.getElementById(id);
                    if (re != null) {
                        re.setInnerText("x" + (songUpdate.getRepeatCurrent() + 1) + "/" + songUpdate.getRepeatTotal());
                        lastRepeatElement = re;
                        lastRepeatTotal = songUpdate.getRepeatTotal();
                    }
                }
                break;
        }

//        if (song != null && !song.equals(songUpdate.getSong())) {
//            scheduler.scheduleDeferred(new Scheduler.ScheduledCommand() {
//                @Override
//                public void execute() {
//                    resetScroll(chordsScrollPanel);
//                }
//            });
//        }
        song = songUpdate.getSong();

        scheduler.scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                resetScroll(chordsScrollPanel);
            }
        });

        //  load new data even if the identity has not changed
        setAnchors(title, artist);
        copyright.setInnerHTML(song.getCopyright());

        timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

        syncCurrentKey(songUpdate.getCurrentKey());
        syncCurrentBpm(songUpdate.getCurrentBeatsPerMinute());

        syncKey(songUpdate.getCurrentKey());

        chordsFontSize = 0;     //    will never match, forces the fontSize set
        chordsDirty = true;   //  done by syncKey()
    }

    private void setEnables() {
        boolean enable = (songUpdate.getState() == SongUpdate.State.idle);

        originalKeyButton.setEnabled(enable);
        keyUpButton.setEnabled(enable);
        keyDownButton.setEnabled(enable);
        currentBpmEntry.setEnabled(enable);
        bpmSelect.setDisabled(!enable);
    }

    private void syncCurrentKey(Key key) {
        keyLabel.setInnerHTML(key.toString());
    }

    private void syncCurrentBpm(int bpm) {
        currentBpmEntry.setValue(Integer.toString(bpm));
        bpmSelect.setSelectedIndex(0);
    }

    private void labelPlayStop() {
        switch (songUpdate.getState()) {
            case playing:
                playStopButton.setText("Stop");
                audioBeatDisplayCanvas.getStyle().setDisplay(Style.Display.INLINE);
                break;
            case idle:
                playStopButton.setText("Play");
                audioBeatDisplayCanvas.getStyle().setDisplay(Style.Display.NONE);
                break;
        }
    }

    @Override
    public void onMusicAnimationEvent(MusicAnimationEvent event) {
        if (song == null)
            return;

        audioBeatDisplay.update(event.getT(), songUpdate.getEventTime(),
                songUpdate.getCurrentBeatsPerMinute(), false, song.getBeatsPerBar());

        {
            Widget parent = player.getParent();
            double parentWidth = parent.getOffsetWidth();
            double parentHeight = parent.getOffsetHeight();
            if (parentWidth != chordsParentWidth) {
                chordsParentWidth = parentWidth;
                chordsDirty = true;
            }
            if (parentHeight != chordsParentHeight) {
                chordsParentHeight = parentHeight;
                chordsDirty = true;
            }
        }

        if (event.getMeasureNumber() != lastMeasureNumber) {
            chordsDirty = true;
            lastMeasureNumber = event.getMeasureNumber();
        }

        if (chordsDirty) {

            //  turn off all highlights
            if (lastChordElement != null) {
                lastChordElement.getStyle().clearBackgroundColor();
                lastChordElement = null;
            }
            if (lastLyricsElement != null) {
                lastLyricsElement.getStyle().clearBackgroundColor();
                lastLyricsElement = null;
            }

            //  high light chord and lyrics
            switch (songUpdate.getState()) {
                case playing:
                    //  add highlights
                    if (songUpdate.getMeasure() >= 0) {
                        String chordCellId = prefix + songUpdate.getSectionNumber() + Song.genChordId(songUpdate
                                        .getSectionVersion(),
                                songUpdate.getChordSectionRow(), songUpdate.getChordSectionColumn());
                        //GWT.log(chordCellId );
                        Element ce = player.getElementById(chordCellId);
                        if (ce != null) {
                            ce.getStyle().setBackgroundColor(highlightColor);
                            lastChordElement = ce;
                        }
                        String lyricsCellId = prefix + Song.genLyricsId(songUpdate.getSectionNumber());
                        Element le = player.getElementById(lyricsCellId);
                        if (le != null) {
                            le.getStyle().setBackgroundColor(highlightColor);
                            lastLyricsElement = le;
                        }
                    }
                    break;
            }

            chordsDirty = false;
        }

        //  auto scroll
        autoScroll(chordsScrollPanel, player);

    }

    private void syncKey(Key key) {
        int tran = key.getHalfStep() - songUpdate.getSong().getKey().getHalfStep();
        syncKey(tran);
    }

    private void syncKey(int tran) {

            currentKey = Key.getKeyByHalfStep(song.getKey().getHalfStep() + tran);
            keyLabel.setInnerHTML(currentKey.toString() + " " + currentKey.sharpsFlatsToString());

            player.clear();

            ArrayList<LyricSection> lyricSections = song.getLyricSections();
            int sectionIndex = 0;
            {
                FlexTable flexTable = new FlexTable();
                FlexTable.FlexCellFormatter formatter = flexTable.getFlexCellFormatter();
                final int chordCol = 0;
                final int lyricsCol = 1;
                for (LyricSection lyricSection : lyricSections) {

                    int firstRow = flexTable.getRowCount();
                    FlexTable sectionTable = new FlexTable();
                    song.transpose(song.getChordSection(lyricSection.getSectionVersion()),
                            sectionTable, tran, lyricsDefaultFontSize, false);
                    flexTable.setWidget(firstRow, chordCol, sectionTable);

                    StringBuilder sb = new StringBuilder();
                    for (LyricsLine lyricsLine : lyricSection.getLyricsLines())
                        sb.append(lyricsLine.getLyrics()).append("\n");

                    flexTable.setHTML(firstRow, lyricsCol, sb.toString());
                    formatter.setStyleName(firstRow, lyricsCol, CssConstants.style + "lyrics" + lyricSection
                            .getSectionVersion().getSection().getAbbreviation() + "Class");
                    formatter.setRowSpan(firstRow, lyricsCol, flexTable.getRowCount() - firstRow);
                }
                player.add(flexTable);
            }
    }

    private AudioBeatDisplay audioBeatDisplay;

    private boolean chordsDirty = true;
    private double chordsParentWidth;
    private double chordsParentHeight;
    private Element lastRepeatElement;
    private int lastRepeatTotal;
    private int lastMeasureNumber;


    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private int chordsFontSize = chordsMaxFontSize;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private static final int lyricsDefaultFontSize = lyricsMaxFontSize;
    private static final String prefix = "player";
    private static final Scheduler scheduler = Scheduler.get();
    private static final Logger logger = Logger.getLogger(PlayerViewImpl.class.getName());

}
