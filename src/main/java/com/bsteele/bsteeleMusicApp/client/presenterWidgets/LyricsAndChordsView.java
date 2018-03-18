/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;

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
    SpanElement title;

    @UiField
    SpanElement artist;

    @UiField
    HTMLPanel chords;

    @UiField
    HTMLPanel lyrics;

    @UiField
    SpanElement copyright;

    @Override
    public void setSong(Song song) {
        boolean keepKey = (this.song != null
                && this.song.equals(song));  //  identiry only

        this.song = song;

        //  load new data even if the identity has not changed
        title.setInnerHTML(song.getTitle());
        artist.setInnerHTML(song.getArtist());
        copyright.setInnerHTML(song.getCopyright());

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
                break;
        }
    }

    interface Binder extends UiBinder<Widget, LyricsAndChordsView> {
    }

    @Inject
    LyricsAndChordsView(Binder binder, SongPlayMaster songPlayMaster) {
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

    private void transpose(int tran) {
        chords.clear();
        chords.add(new HTML(song.transpose(tran)));
    }

    private Song song;
    private SongPlayMaster songPlayMaster;
    private Element lastChordElement;
    private Element lastLyricsElement;
    public static final  String highlightColor = "#e4c9ff";
}
