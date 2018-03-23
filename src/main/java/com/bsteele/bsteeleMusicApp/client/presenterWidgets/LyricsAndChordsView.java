/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.gargoylesoftware.htmlunit.javascript.host.event.AnimationEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
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
    ButtonElement playButton;

    @UiField
    ButtonElement stopButton;

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

        Event.sinkEvents(playButton, Event.ONCLICK);
        Event.setEventListener(playButton, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                if (song != null) {
                    Song songToPlay = song.copySong();
                    songToPlay.setBeatsPerMinute(Integer.parseInt(currentBpmEntry.getValue()));
                    songPlayMaster.playSong(songToPlay);
                }
            }
        });

        Event.sinkEvents(stopButton, Event.ONCLICK);
        Event.setEventListener(stopButton, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                songPlayMaster.stopSong();
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
        String chordCellId = Song.genChordId(songUpdate.getSectionVersion(),
                songUpdate.getChordSectionRow(), songUpdate.getChordSectionColumn());

        //  turn off all highlights
        if (lastChordElement != null) {
            lastChordElement.getStyle().clearBackgroundColor();
            lastChordElement = null;
        }
        if (lastLyricsElement != null) {
            lastLyricsElement.getStyle().clearBackgroundColor();
            lastLyricsElement = null;
        }


        //  turn on highlights if required
        switch (songUpdate.getState()) {
            case idle:
                break;
            case playing:
                if (songUpdate.getMeasure() >= 0) {
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
                break;
        }
        chordsDirty = true;
    }

    @Override
    public void onMusicAnimationEvent(double t) {
        if (chords.getOffsetWidth() != chordsWidth) {
            chordsWidth = chords.getOffsetWidth();
            chordsDirty = true;
        }
        if (chords.getOffsetHeight() != chordsHeight) {
            chordsHeight = chords.getOffsetHeight();
            chordsDirty = true;
        }
        if (chordsDirty) {
            resizeChords();
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
        //  adjust fontsize so the table fits but is still minimally, if possible
        if (chords != null && chords.getOffsetWidth() > 0 && chords.getOffsetHeight() > 0) {
            NodeList<Element> list = chords.getElement().getElementsByTagName("table");
            for (int i = 0; i < list.getLength(); i++) {
                //  html table
                Element e = list.getItem(i);
                if (e.getId().equals("chordTable")) {
                    int tableWidth = e.getClientWidth();
                    int tableHeight = e.getClientHeight();

                    logger.fine("chords panel: (" + chords.getOffsetWidth() + ","
                            + chords.getOffsetHeight() + " for (" + tableWidth + ","
                            + tableHeight + ")");

                    double maxFontSizeUsed = 0;
                    if (chords.getOffsetWidth() < tableWidth
                            || chords.getOffsetWidth() > 1.1 * tableWidth) {
                        double ratio = (double) chords.getOffsetWidth() / tableWidth;
                        NodeList<Element> cells = e.getElementsByTagName("td");
                        for (int c = 0; c < cells.getLength(); c++) {
                            Style cellStyle = cells.getItem(c).getStyle();

                            double currentSize = chordsMaxFontsize;     //  fixme: demands all chord fonts be the same size
                            MatchResult mr = fontSizeRegexp.exec(cellStyle.getFontSize());
                            if (mr != null)
                                currentSize = Double.parseDouble(mr.getGroup(1));
                            double size = Math.min(Math.max(chordsMinFontsize, ratio * currentSize), chordsMaxFontsize);
                            maxFontSizeUsed = Math.max(maxFontSizeUsed, size);
                            if (currentSize != size)
                                cellStyle.setFontSize(size, Style.Unit.PX);
                        }
                    }
                    chordsDirty = !(chords.getOffsetWidth() >= tableWidth
                            || maxFontSizeUsed == chordsMinFontsize);

                    break;
                }
            }
        }
    }

    private Song song;
    private SongPlayMaster songPlayMaster;
    private Element lastChordElement;
    private Element lastLyricsElement;
    private boolean chordsDirty = true;
    private int chordsWidth;
    private int chordsHeight;

    public static final String highlightColor = "#e4c9ff";
    private static final RegExp fontSizeRegexp = RegExp.compile("^([\\d.]+)px$");
    private static final int chordsMinFontsize = 10;
    private static final int chordsMaxFontsize = 48;
    private final EventBus eventBus;
    private static final Logger logger = Logger.getLogger(LyricsAndChordsView.class.getName());

}
