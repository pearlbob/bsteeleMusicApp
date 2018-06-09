/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.SectionVersion;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.inject.Inject;
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
    SpanElement title;

    @UiField
    SpanElement artist;

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
    HTMLPanel chords;
    @UiField
    FocusPanel lyricsFocus;
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
                    final String id = prefix + Song.genChordId(songUpdate.getSectionVersion(),
                            songUpdate.getRepeatLastRow(), songUpdate.getRepeatLastCol());
                    Element re = lyrics.getElementById(id);
                    if (re != null) {
                        re.setInnerText("x" + (songUpdate.getRepeatCurrent() + 1) + "/" + songUpdate.getRepeatTotal());
                        lastRepeatElement = re;
                        lastRepeatTotal = songUpdate.getRepeatTotal();
                    }
                }
                break;
        }

        song = songUpdate.getSong();

        updateCount++;

        //  load new data even if the identity has not changed
        title.setInnerHTML(song.getTitle());
        artist.setInnerHTML(song.getArtist());
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
        transpose(key.getHalfStep() - songUpdate.getSong().getKey().getHalfStep());
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
            Widget parent = chords.getParent();
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
            if (event.getMeasureNumber() != lastMeasureNumber) {
                chordsDirty = true;
                lastMeasureNumber = event.getMeasureNumber();
            }

            switch (songUpdate.getState()) {
                case playing:
                    //  add highlights
                    if (songUpdate.getMeasure() >= 0) {
                        SectionVersion v = song.getChordSectionVersion(songUpdate.getSectionVersion());
                        String chordCellId = prefix + Song.genChordId(v,
                                songUpdate.getChordSectionRow(), songUpdate.getChordSectionColumn());

                        Element ce = chords.getElementById(chordCellId);
                        if (ce != null) {
                            ce.getStyle().setBackgroundColor(highlightColor);
                            lastChordElement = ce;
                        }
                        String lyricsCellId = prefix + Song.genLyricsId(songUpdate.getSectionNumber());
                        Element le = lyrics.getElementById(lyricsCellId);
                        if (le != null) {
                            le.getStyle().setBackgroundColor(highlightColor);
                            lastLyricsElement = le;
                        }
                    }
                    break;
            }

            resizeChords();
        }
    }

    private void transpose(int tran) {
        currentKey = Key.getKeyByHalfStep(song.getKey().getHalfStep() + tran);
        keyLabel.setInnerHTML(currentKey.toString());

        if (song == null)
            return;

        chords.clear();
        chords.add(new HTML(song.transpose(tran, prefix)));

        resizeChords();
    }

    private void resizeChords() {
        //  adjust fontSize so the table fits but is still minimally, if possible
        if (chords != null && chords.getOffsetWidth() > 0 && chords.getOffsetHeight() > 0) {
            {
                Widget parent = chordsFocus;
                double parentWidth = parent.getOffsetWidth();
                double parentHeight = parent.getOffsetHeight();
                NodeList<Element> list = chords.getElement().getElementsByTagName("table");
                for (int i = 0; i < list.getLength(); i++) {
                    //  html table
                    Element e = list.getItem(i);
                    if (e.getId().equals(prefix + "ChordTable")) {
                        int tableWidth = e.getClientWidth();
                        int tableHeight = e.getClientHeight();

                        //  GWT.log
//                        logger.fine
//                                ("L&C chords panel: (" + parentWidth + ","
//                                        + parentHeight + ") for (" + tableWidth + ","
//                                        + tableHeight + ") "
//                                        + ((1 + 1.0 / chordsFontSize) * tableWidth));

                        if (forceChordsFontSize
                                || parentWidth < tableWidth
                                || parentHeight < tableHeight
                                || parentWidth > (1 + 4.0 / chordsFontSize) * tableWidth) {
                            double ratio = Math.min(parentWidth / tableWidth,
                                    parentHeight / tableHeight);
                            logger.fine("wratio: " + (parentWidth / tableWidth)
                                    + ", hratio: " + (parentHeight / tableHeight));
                            NodeList<Element> cells = e.getElementsByTagName("td");

                            int size = (int) Math.floor(Math.max(chordsMinFontSize, Math.min(chordsFontSize * ratio, chordsMaxFontSize)));
                            if (forceChordsFontSize || chordsFontSize != size) {
                                //  fixme: demands all chord fonts be the same size
                                for (int c = 0; c < cells.getLength(); c++) {
                                    Style cellStyle = cells.getItem(c).getStyle();
                                    cellStyle.setFontSize(size, Style.Unit.PX);
                                    cellStyle.setPaddingTop(size / 6, Style.Unit.PX);
                                    cellStyle.setPaddingBottom(size / 6, Style.Unit.PX);
                                    cellStyle.setPaddingLeft(size / 6, Style.Unit.PX);
                                    cellStyle.setPaddingRight(size / 3, Style.Unit.PX);
                                }
                                chordsFontSize = size;
                            }
                            //sendStatus("chords ratio", Double.toString(ratio) + ", size: " + Double.toString(size));

                            chordsDirty = !((ratio >= 1 && ratio <= (1 + 8.0 / chordsFontSize))
                                    || chordsFontSize == chordsMinFontSize
                                    || chordsFontSize == chordsMaxFontSize)
                                    || forceChordsFontSize;
                            forceChordsFontSize = false;
                            //sendStatus("chordsDirty", Boolean.toString(chordsDirty));
                        } else
                            chordsDirty = false;
                        break;
                    }
                }
            }

            //  optimize the lyrics fontSize
            if (!chordsDirty) {
                //  last pass
                NodeList<Element> list = lyrics.getElement().getElementsByTagName("table");
                if (list.getLength() > 0) {
                    Element e = list.getItem(0);
                    final int tableWidth = e.getClientWidth();
                    final int tableHeight = e.getClientHeight();

                    logger.fine("lyrics panel: (" + lyrics.getOffsetWidth() + ","
                            + lyrics.getOffsetHeight() + ") for (" + tableWidth + ","
                            + tableHeight + ")  = " + ((double) lyrics.getOffsetWidth() / tableWidth));

                    if (lyrics.getOffsetWidth() < 1.05 * tableWidth
                            || lyrics.getOffsetWidth() > 1.1 * tableWidth)          //  try to use the extra width
                    {
                        final double ratio = 0.95 * (double) lyrics.getOffsetWidth() / tableWidth;
                        //sendStatus("lyrics ratio: ", Double.toString((double) lyrics.getOffsetWidth() / tableWidth));
                        final NodeList<Element> cells = e.getElementsByTagName("td");
                        final RegExp fontSizeRegexp = RegExp.compile("^([\\d.]+)px$");
                        for (int c = 0; c < cells.getLength(); c++) {
                            Style cellStyle = cells.getItem(c).getStyle();

                            double currentSize = lyricsMaxFontSize;     //  fixme: demands all lyrics fonts be the same size
                            MatchResult mr = fontSizeRegexp.exec(cellStyle.getFontSize());
                            if (mr != null)
                                currentSize = Double.parseDouble(mr.getGroup(1));
                            double size = Math.min(Math.max(lyricsMinFontSize, ratio * currentSize), lyricsMaxFontSize);
                            if (currentSize != size)
                                cellStyle.setFontSize(size, Style.Unit.PX);
                        }
                    }
                }

                audioBeatDisplayCanvas.setWidth((int) Math.floor(chordsParentWidth));
            }
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

    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private int chordsFontSize = chordsMaxFontSize;
    private boolean forceChordsFontSize = true;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private static String prefix = "lyAndCh";
    private static final Logger logger = Logger.getLogger(LyricsAndChordsViewImpl.class.getName());

}
