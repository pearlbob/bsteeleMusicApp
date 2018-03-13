/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.Song;
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
public class LyricsAndChordsView extends ViewImpl
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
    SelectElement currentTimeSignatureSelect;

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

    interface Binder extends UiBinder<Widget, LyricsAndChordsView> {
    }

    @Inject
    LyricsAndChordsView(Binder binder) {
        initWidget(binder.createAndBindUi(this));

        Event.sinkEvents(playButton, Event.ONCLICK);
        Event.setEventListener(playButton, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                GWT.log("play()");
                if (song != null)
                    SongPlayMaster.getSongPlayMaster().playSong(song );
            }
        });

        Event.sinkEvents(stopButton, Event.ONCLICK);
        Event.setEventListener(stopButton, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                GWT.log("stop()");
                SongPlayMaster.getSongPlayMaster().stopSong();
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

        Event.sinkEvents(currentTimeSignatureSelect, Event.ONCHANGE);
        Event.setEventListener(currentTimeSignatureSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                GWT.log("current bpm select: " + currentTimeSignatureSelect.getValue());
            }
        });
    }

    private void transpose(int tran) {
        chords.clear();
        chords.add(new HTML(song.transpose(tran)));
        //GWT.log("  chords.getStyleName(): " + chords.getStyleName());
    }

    private Song song;
}
