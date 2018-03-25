/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class LyricsAndChordsView
        extends ViewImpl
        implements LyricsAndChordsPresenterWidget.MyView {

    @UiField
    Button playStopButton;

    @UiField
    SelectElement keySelect;

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
    SplitLayoutPanel split;

    @UiField
    CanvasElement audioBeatDisplayCanvas;

    @UiField
    HTMLPanel chords;

    @UiField
    HTMLPanel lyrics;

    @UiField
    SpanElement copyright;


    interface Binder extends UiBinder<Widget, LyricsAndChordsView> {
    }

    @Inject
    LyricsAndChordsView(final EventBus eventBus, Binder binder, SongPlayMaster songPlayMaster) {
        this.eventBus = eventBus;
        initWidget(binder.createAndBindUi(this));

        this.songPlayMaster = songPlayMaster;
        audioBeatDisplay = new AudioBeatDisplay(audioBeatDisplayCanvas);
        labelPlayStop();

        playStopButton.addClickHandler((ClickEvent event) -> {
            if (song != null) {
                switch (songUpdate.getState()) {
                    case playing:
                        songPlayMaster.stopSong();
                        break;
                    case idle:
                        Song songToPlay = song.copySong();
                        songToPlay.setBeatsPerMinute(Integer.parseInt(currentBpmEntry.getValue()));
                        songPlayMaster.playSong(songToPlay);
                        break;
                }
            }
        });

        Event.sinkEvents(keySelect, Event.ONCHANGE);
        Event.setEventListener(keySelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                transpose(Integer.parseInt(keySelect.getValue()));
            }
        });

        currentBpmEntry.addChangeHandler((event) -> {
            GWT.log("currentBpmEntry change: "
                    + currentBpmEntry.getText()
            );
        });

        Event.sinkEvents(bpmSelect, Event.ONCHANGE);
        Event.setEventListener(bpmSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                int bpm = Integer.parseInt(bpmSelect.getValue());
                currentBpmEntry.setValue(Integer.toString(bpm));
                GWT.log("bpm select: " + bpm);
            }
        });
    }

    @Override
    public void onSongUpdate(SongUpdate songUpdate) {

        this.songUpdate = songUpdate;

        labelPlayStop();

        if ( lastRepeatElement != null ) {
            lastRepeatElement.setInnerText("x"+lastRepeatTotal);
            lastRepeatElement = null;
        }

        //  turn on highlights if required
        switch (songUpdate.getState()) {
            case idle:
                break;
            case playing:
                if (songUpdate.getRepeatTotal() > 0) {
                    final String id = Song.genChordId(songUpdate.getSectionVersion(),
                            songUpdate.getRepeatLastRow(), songUpdate.getRepeatLastCol());
                    Element re = lyrics.getElementById(id);
                    if (re != null) {
                        re.setInnerText("x" + (songUpdate.getRepeatCurrent()+1) + "/" + songUpdate.getRepeatTotal());
                        lastRepeatElement = re;
                        lastRepeatTotal = songUpdate.getRepeatTotal();
                    }
                }
                break;
        }

        chordsDirty = true;
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
    public void onMusicAnimationEvent(double t) {
        if (song != null
            //&& songUpdate.getState() == SongUpdate.State.playing
                )
            audioBeatDisplay.update(t, songUpdate.getEventTime(),
                    songUpdate.getBeatsPerMinute(), false, song.getBeatsPerBar());

        if (chords.getOffsetWidth() != chordsWidth) {
            chordsWidth = chords.getOffsetWidth();
            chordsDirty = true;
        }
        if (chords.getOffsetHeight() != chordsHeight) {
            chordsHeight = chords.getOffsetHeight();
            chordsDirty = true;
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

            //  add highlights
            if (songUpdate.getMeasure() >= 0) {
                String chordCellId = Song.genChordId(songUpdate.getSectionVersion(),
                        songUpdate.getChordSectionRow(), songUpdate.getChordSectionColumn());

                Element ce = chords.getElementById(chordCellId);
                if (ce != null) {
                    ce.getStyle().setBackgroundColor(highlightColor);
                    lastChordElement = ce;
                }
                String lyricsCellId = Song.genLyicsId(songUpdate.getSectionNumber());
                Element le = lyrics.getElementById(lyricsCellId);
                if (le != null) {
                    le.getStyle().setBackgroundColor(highlightColor);
                    lastLyricsElement = le;
                }
            }

            switch (songUpdate.getState()) {
                case idle:
                    resizeChords();
                    break;
            }
        }
    }


    @Override
    public void setSong(Song song) {
        boolean keepKey = (this.song != null
                && this.song.equals(song));  //  identiry only

        this.song = song;

        //  load new data even if the identity has not changed
        title.setInnerHTML(song.getTitle());
        artist.setInnerHTML(song.getArtist());
        copyright.setInnerHTML(song.getCopyright());

        timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

        currentBpmEntry.setValue(Integer.toString(song.getBeatsPerMinute()));

        String keyAsString = "0";
        int keyAsInt = 0;
        if (keepKey) {
            keyAsString = keySelect.getValue();
            keyAsInt = Integer.parseInt(keyAsString);
        }
        transpose(keyAsInt);

        { //  set the transposition selection to original key
            NodeList<OptionElement> options = keySelect.getOptions();
            for (int i = 0; i < options.getLength(); i++) {
                OptionElement e = options.getItem(i);
                if (e.getValue().equals(keyAsString)) {
                    keySelect.setSelectedIndex(i);
                }
            }
        }

        lyrics.clear();
        lyrics.add(new HTML(song.generateHtmlLyricsTable()));

        chordsDirty = true;
    }

    private void transpose(int tran) {
        chords.clear();
        chords.add(new HTML(song.transpose(tran)));

        resizeChords();
    }

    private void resizeChords() {
        //  adjust fontSize so the table fits but is still minimally, if possible
        if (chords != null && chords.getOffsetWidth() > 0 && chords.getOffsetHeight() > 0) {
            {
                Widget parent = chords.getParent();
                double parentWidth = parent.getOffsetWidth();
                double parentHeight = parent.getOffsetHeight();
                NodeList<Element> list = chords.getElement().getElementsByTagName("table");
                for (int i = 0; i < list.getLength(); i++) {
                    //  html table
                    Element e = list.getItem(i);
                    if (e.getId().equals("chordTable")) {
                        int tableWidth = e.getClientWidth();
                        int tableHeight = e.getClientHeight();

                        //GWT.log//
                        logger.fine("chords panel: (" + parentWidth + ","
                                + parentHeight + ") for (" + tableWidth + ","
                                + tableHeight + ")");

                        double maxFontSizeUsed = 0;
                        if (parentWidth < tableWidth
                                || parentHeight < tableHeight
                                || parentWidth > 1.2 * tableWidth) {
                            double ratio = Math.min(parentWidth / tableWidth,
                                    parentHeight / tableHeight);
                            logger.fine("wratio: " + (parentWidth / tableWidth)
                                    + ", hratio: " + (parentHeight / tableHeight));
                            NodeList<Element> cells = e.getElementsByTagName("td");
                            for (int c = 0; c < cells.getLength(); c++) {
                                Style cellStyle = cells.getItem(c).getStyle();

                                double currentSize = chordsMaxFontSize;     //  fixme: demands all chord fonts be the same size
                                MatchResult mr = fontSizeRegexp.exec(cellStyle.getFontSize());
                                if (mr != null)
                                    currentSize = Double.parseDouble(mr.getGroup(1));
                                double size = Math.min(Math.max(chordsMinFontSize, ratio * currentSize), chordsMaxFontSize);
                                maxFontSizeUsed = Math.max(maxFontSizeUsed, size);
                                if (currentSize != size) {
                                    cellStyle.setFontSize(size, Style.Unit.PX);
                                    cellStyle.setPaddingTop(size / 6, Style.Unit.PX);
                                    cellStyle.setPaddingBottom(size / 6, Style.Unit.PX);
                                    cellStyle.setPaddingLeft(size / 6, Style.Unit.PX);
                                    cellStyle.setPaddingRight(size / 3, Style.Unit.PX);
                                }
                            }
                            chordsDirty = !((ratio >= 1 && ratio <= 1.1)
                                    || maxFontSizeUsed == chordsMinFontSize
                                    || maxFontSizeUsed == chordsMaxFontSize);
                        } else
                            chordsDirty = false;

                        //  move the splitter closer
//                    if (!chordsDirty && chords.getOffsetWidth() - tableWidth > 5) {
////                        fixme:      move the splitter closer to the chords
// split.getLayoutData();
//                    }

                        break;
                    }
                }
            }

            //  optimize the lyrics fontsize
            if (!chordsDirty) {
                //  last pass
                NodeList<Element> list = lyrics.getElement().getElementsByTagName("table");
                if (list.getLength() > 0) {
                    Element e = list.getItem(0);
                    int tableWidth = e.getClientWidth();
                    int tableHeight = e.getClientHeight();

                    logger.fine("lyrics panel: (" + lyrics.getOffsetWidth() + ","
                            + lyrics.getOffsetHeight() + ") for (" + tableWidth + ","
                            + tableHeight + ")  = " + ((double) lyrics.getOffsetWidth() / tableWidth));

                    if (lyrics.getOffsetWidth() < 1.05 * tableWidth
                            || lyrics.getOffsetWidth() > 1.1 * tableWidth)          //  try to use the extra width
                    {
                        double ratio = 0.95 * (double) lyrics.getOffsetWidth() / tableWidth;
                        logger.fine("lyrics ratio: " + ((double) lyrics.getOffsetWidth() / tableWidth));
                        NodeList<Element> cells = e.getElementsByTagName("td");
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

                audioBeatDisplayCanvas.setWidth(chordsWidth);
            }
        }
    }


    private AudioBeatDisplay audioBeatDisplay;
    private Song song;
    private SongUpdate songUpdate = new SongUpdate();
    private SongPlayMaster songPlayMaster;
    private Element lastChordElement;
    private Element lastLyricsElement;
    private boolean chordsDirty = true;
    private int chordsWidth;
    private int chordsHeight;
    private Element lastRepeatElement;
    private int lastRepeatTotal;

    public static final String highlightColor = "#e4c9ff";
    private static final RegExp fontSizeRegexp = RegExp.compile("^([\\d.]+)px$");
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private final EventBus eventBus;
    private static final Logger logger = Logger.getLogger(LyricsAndChordsView.class.getName());

}
