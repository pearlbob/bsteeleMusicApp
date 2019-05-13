/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.home.AppTab;
import com.bsteele.bsteeleMusicApp.shared.Grid;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordSectionLocation;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.MeasureNode;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class LyricsAndChordsViewImpl
        extends CommonPlayViewImpl
        implements LyricsAndChordsPresenterWidget.MyView
        , KeyPressHandler {

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
    SplitLayoutPanel split;

    @UiField
    CanvasElement audioBeatDisplayCanvas;

    @UiField
    HTMLPanel chordsContainer;
    @UiField
    FocusPanel chordsFocus;

    @UiField
    FlexTable chordsFlexTable;

    @UiField
    FocusPanel lyricsFocus;
    @UiField
    ScrollPanel lyricsScrollPanel;
    @UiField
    HTMLPanel lyrics;

    @UiField
    SpanElement copyright;

    interface Binder extends UiBinder<Widget, LyricsAndChordsViewImpl> {
    }

    @Inject
    LyricsAndChordsViewImpl(@Nonnull final EventBus eventBus,
                            @Nonnull Binder binder,
                            @Nonnull final SongPlayMaster songPlayMaster) {
        super(eventBus, songPlayMaster);

        initWidget(binder.createAndBindUi(this));

        audioBeatDisplay = new AudioBeatDisplay(audioBeatDisplayCanvas);
        labelPlayStop();

        playStopButton.addClickHandler((ClickEvent event) -> {
            playStop();
        });

        originalKeyButton.addClickHandler((ClickEvent event) -> {
            if (song != null) {
                setCurrentKey(song.getKey());
            }
        });

        keyUpButton.addClickHandler((ClickEvent event) -> {
            stepCurrentKey(1);
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

        chordsFocus.addDomHandler(this, KeyPressEvent.getType());
        lyricsFocus.addDomHandler(this, KeyPressEvent.getType());

        keyLabel.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        keyLabel.getStyle().setWidth(3, Style.Unit.EM);
    }

    @Override
    public void onKeyPress(KeyPressEvent keyPressEvent) {
        GWT.log("onKeyPress: " + keyPressEvent.toDebugString());
        int keyCode = keyPressEvent.getNativeEvent().getKeyCode();
        if (keyCode == KeyCodes.KEY_SPACE) {
            playStop();
        }
    }


    @Override
    public void onSongUpdate(SongUpdate songUpdate) {

        if (songUpdate == null || songUpdate.getSong() == null)
            return;     //  defense

        this.songUpdate = songUpdate;

        if (!isActive)
            return;

        labelPlayStop();

        if (lastRepeatElement != null) {
            lastRepeatElement.setInnerText("x" + lastRepeatTotal);
            lastRepeatElement = null;
        }

        if (songUpdate.getState() != lastState) {
            lastState = songUpdate.getState();
            setEnables();
        }

        //  turn on highlights if required
        switch (songUpdate.getState()) {
            case idle:
                break;
            case playing:
                if (songUpdate.getRepeatTotal() > 0) {
//                    final String id = prefix + Song.genChordId(songUpdate.getSectionVersion(),
//                            songUpdate.getRepeatLastRow(), songUpdate.getRepeatLastCol());
//                    Element re = lyrics.getElementById(id);
//                    if (re != null) {
//                        re.setInnerText("x" + (songUpdate.getRepeatCurrent() + 1) + "/" + songUpdate.getRepeatTotal());
//                        lastRepeatElement = re;
//                        lastRepeatTotal = songUpdate.getRepeatTotal();
//                    }
                }
                break;
        }

        song = songUpdate.getSong();

        scheduler.scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                resetScroll(lyricsScrollPanel);
            }
        });

        updateCount++;

        //  load new data even if the identity has not changed
        setAnchors(title, artist);
        copyright.setInnerHTML(song.getCopyright());

        timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

        syncCurrentKey(songUpdate.getCurrentKey());
        syncCurrentBpm(songUpdate.getCurrentBeatsPerMinute());

        lyrics.clear();
        lyrics.add(new HTML(song.generateHtmlLyricsTable(prefix)));

        forceChordsFontSize = true;  //    force the fontSize set
        chordsDirty = true;
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
        halfStepOffset = key.getHalfStep() - songUpdate.getSong().getKey().getHalfStep();
        transposeToOffset();
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
//        if (song == null || !isActive)
//            return;
//
//        audioBeatDisplay.update(event.getT(), songUpdate.getEventTime(),
//                songUpdate.getCurrentBeatsPerMinute(), false, song.getBeatsPerBar());
//
//        {
//            Widget parent = chordsFlexTable.getParent();
//            double parentWidth = parent.getOffsetWidth();
//            double parentHeight = parent.getOffsetHeight();
//            if (parentWidth != chordsParentWidth) {
//                chordsParentWidth = parentWidth;
//                chordsDirty = true;
//            }
//            if (parentHeight != chordsParentHeight) {
//                chordsParentHeight = parentHeight;
//                chordsDirty = true;
//            }
//        }
//
//        if (chordsDirty) {
//
//            //  turn off all highlights
//            if (lastChordElement != null) {
//                lastChordElement.getStyle().clearBackgroundColor();
//                lastChordElement = null;
//            }
//            if (lastLyricsElement != null) {
//                lastLyricsElement.getStyle().clearBackgroundColor();
//                lastLyricsElement = null;
//            }
//            if (event.getMeasureNumber() != lastMeasureNumber) {
//                chordsDirty = true;
//                lastMeasureNumber = event.getMeasureNumber();
//            }
//
//            switch (songUpdate.getState()) {
//                case playing:
//                    //  add highlights
////                    if (songUpdate.getMeasure() >= 0) {
////                        SectionVersion v = song.getChordSectionVersion(songUpdate.getSectionVersion());
////                        String chordCellId = prefix + Song.genChordId(v,
////                                songUpdate.getChordSectionRow(), songUpdate.getChordSectionColumn());
////
////                        Element ce = chordsFlexTable.getElementById(chordCellId);
////                        if (ce != null) {
////                            ce.getStyle().setBackgroundColor(highlightColor);
////                            lastChordElement = ce;
////            }
////                        String lyricsCellId = prefix + Song.genLyricsId(songUpdate.getMomentNumber());
////                        Element le = lyrics.getElementById(lyricsCellId);
////                        if (le != null) {
////                            le.getStyle().setBackgroundColor(highlightColor);
////                            lastLyricsElement = le;
////            }
////            }
//                    break;
//            }
//
//            resizeChords();
//        }
    }

    @Override
    public void setActive(boolean isActive) {
        this.isActive = isActive;
        if (isActive)
            onSongUpdate(songUpdate);
    }

    private void transposeToOffset() {
        currentKey = Key.getKeyByHalfStep(song.getKey().getHalfStep() + halfStepOffset);
        keyLabel.setInnerHTML(currentKey.toString() + " " + currentKey.sharpsFlatsToString());

        if (song == null)
            return;

        song.transpose(chordsFlexTable, halfStepOffset, chordsFontSize);

        resizeChords();
    }

    private void resizeChords() {

        //  adjust fontSize so the table fits but is still minimally, if possible
        if (chordsFlexTable != null
                && chordsFlexTable.getOffsetWidth() > 0
                && chordsFlexTable.getOffsetHeight() > 0) {
            Widget parent = chordsFocus;
            double parentWidth = parent.getOffsetWidth();
            double parentHeight = parent.getOffsetHeight();

            int tableWidth = chordsFlexTable.getOffsetWidth();
            int tableHeight = chordsFlexTable.getOffsetHeight();
            FlexTable.FlexCellFormatter formatter = chordsFlexTable.getFlexCellFormatter();

            if (forceChordsFontSize
                    || parentWidth < tableWidth
                    || parentHeight < tableHeight
                    || parentWidth > (1 + 8.0 / chordsFontSize) * tableWidth) {
                double ratio = Math.min(parentWidth / tableWidth,
                        parentHeight / tableHeight);
                logger.finer("wratio: " + (parentWidth / tableWidth)
                        + ", hratio: " + (parentHeight / tableHeight));

                int size = (int) Math.floor(Math.max(chordsMinFontSize, Math.min(chordsFontSize * ratio,
                        chordsMaxFontSize)));
                Grid<ChordSectionLocation> grid = song.getChordSectionLocationGrid();
                int rLimit = grid.getRowCount();
                if (forceChordsFontSize || chordsFontSize != size) {
                    //  fixme: demands all chord fonts be the same size
                    song.transpose(chordsFlexTable, halfStepOffset, chordsFontSize);

                    for (int r = 0; r < rLimit; r++) {
                        ArrayList<ChordSectionLocation> row = grid.getRow(r);
                        int colLimit = row.size();
                        for (int c = 0; c < colLimit; c++) {
                            Element e = formatter.getElement(r, c);
                            Style cellStyle = e.getStyle();
                            //cellStyle.setFontSize(size, Style.Unit.PX);
                            cellStyle.setPaddingTop(size / 6, Style.Unit.PX);
                            cellStyle.setPaddingBottom(size / 6, Style.Unit.PX);
                            cellStyle.setPaddingLeft(size / 6, Style.Unit.PX);
                            cellStyle.setPaddingRight(size / 3, Style.Unit.PX);
                        }
                    }
                    chordsFontSize = size;
                }
                //sendStatus("chords ratio", Double.toString(ratio) + ", size: " + Double.toString(size));

                chordsDirty = !((ratio >= 1 && ratio <= (1 + 20.0 / chordsFontSize))
                        || chordsFontSize == chordsMinFontSize
                        || chordsFontSize == chordsMaxFontSize)
                        || forceChordsFontSize;
                forceChordsFontSize = false;
                //sendStatus("chordsDirty", Boolean.toString(chordsDirty));
            } else
                chordsDirty = false;
        }


        //  optimize the lyrics fontSize
        if (!chordsDirty) {
            //  last pass

            int tableWidth = chordsFlexTable.getOffsetWidth();
            int tableHeight = chordsFlexTable.getOffsetHeight();

            logger.finer("lyrics panel: (" + lyrics.getOffsetWidth() + ","
                    + lyrics.getOffsetHeight() + ") for (" + tableWidth + ","
                    + tableHeight + ")  = " + ((double) lyrics.getOffsetWidth() / tableWidth));

            if (lyrics.getOffsetWidth() < 1.05 * tableWidth
                    || lyrics.getOffsetWidth() > 1.1 * tableWidth)          //  try to use the extra width
            {
                final double ratio = 0.95 * (double) lyrics.getOffsetWidth() / tableWidth;
                //sendStatus("lyrics ratio: ", Double.toString((double) lyrics.getOffsetWidth() / tableWidth));
                final RegExp fontSizeRegexp = RegExp.compile("^([\\d.]+)px$");
                FlexTable.FlexCellFormatter formatter = chordsFlexTable.getFlexCellFormatter();

                Grid<ChordSectionLocation> grid = song.getChordSectionLocationGrid();
                int rLimit = grid.getRowCount();
                for (int r = 0; r < rLimit; r++) {
                    ArrayList<ChordSectionLocation> row = grid.getRow(r);
                    int colLimit = row.size();
                    for (int c = 0; c < colLimit; c++) {
                        Element e = formatter.getElement(r, c);
                        Style cellStyle = e.getStyle();

                        double currentSize = lyricsMaxFontSize;     //  fixme: demands all lyrics fonts be the
                        // same size
                        MatchResult mr = fontSizeRegexp.exec(cellStyle.getFontSize());
                        if (mr != null)
                            currentSize = Double.parseDouble(mr.getGroup(1));
                        double size = Math.min(Math.max(lyricsMinFontSize, ratio * currentSize),
                                lyricsMaxFontSize);
                        if (currentSize != size)
                            cellStyle.setFontSize(size, Style.Unit.PX);
                    }
                }
            }

            audioBeatDisplayCanvas.setWidth((int) Math.floor(chordsParentWidth));
        }
    }

    private void sendStatus(String name, String value) {
        eventBus.fireEvent(new StatusEvent(name, value));
    }

    private AudioBeatDisplay audioBeatDisplay;

    private Element lastChordElement;
    private Element lastLyricsElement;
    private boolean chordsDirty = true;
    private double chordsParentWidth;
    private double chordsParentHeight;
    private Element lastRepeatElement;
    private int lastRepeatTotal;
    private int lastMeasureNumber;
    private int updateCount;
    private boolean isActive = false;

    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 80;
    private int chordsFontSize = chordsMaxFontSize;
    private boolean forceChordsFontSize = true;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private static String prefix = "lyAndCh";
    private static final Scheduler scheduler = Scheduler.get();
    private static final Logger logger = Logger.getLogger(LyricsAndChordsViewImpl.class.getName());

}
