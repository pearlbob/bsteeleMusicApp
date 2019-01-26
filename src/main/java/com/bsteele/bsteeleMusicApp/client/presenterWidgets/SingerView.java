/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class SingerView
        extends CommonPlayViewImpl
        implements SingerPresenterWidget.MyView {

    @UiField
    SpanElement keyLabel;
    @UiField
    SpanElement currentBpm;


    @UiField
    SpanElement timeSignature;

    @UiField
    Anchor title;

    @UiField
    Anchor  artist;

    @UiField
    Button nextSongButton;
    @UiField
    Button prevSongButton;

    @UiField
    ScrollPanel lyricsScrollPanel;

    @UiField
    CanvasElement audioBeatDisplayCanvas;

    @UiField
    HTMLPanel lyricsContainer;

    @UiField
    HTMLPanel singer;

    @UiField
    SpanElement copyright;


    interface Binder extends UiBinder<Widget, SingerView> {
    }

    @Inject
    SingerView(final EventBus eventBus, Binder binder, SongPlayMaster songPlayMaster) {
        super(eventBus, songPlayMaster);
        initWidget(binder.createAndBindUi(this));

        audioBeatDisplay = new AudioBeatDisplay(audioBeatDisplayCanvas);
        labelPlayStop();

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

        if (songUpdate.getState() != lastState) {
            switch (lastState) {
                case idle:
                    resetScroll(lyricsScrollPanel);
                    break;
            }
            lastState = songUpdate.getState();
        }


        syncCurrentKey(songUpdate.getCurrentKey());
        syncCurrentBpm(songUpdate.getCurrentBeatsPerMinute());
        
        if (song == null || !song.equals(songUpdate.getSong())) {
            resetScroll(lyricsScrollPanel);

            song = songUpdate.getSong();

            setAnchors(title,artist);
            copyright.setInnerHTML(song.getCopyright());

            timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

            singer.clear();
            singer.add(new HTML(song.generateHtmlLyricsTable(prefix)));
        }
    }

    private void syncCurrentKey(Key key) {
        keyLabel.setInnerHTML(key.toString());
    }

    private void syncCurrentBpm(int bpm) {
        currentBpm.setInnerText(Integer.toString(bpm));
    }

    private void labelPlayStop() {
        switch (songUpdate.getState()) {
            case playing:
                audioBeatDisplayCanvas.getStyle().setDisplay(Style.Display.INLINE);
                break;
            case idle:
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

        //  turn off all highlights
        if (lastLyricsElement != null) {
            lastLyricsElement.getStyle().clearBackgroundColor();
            lastLyricsElement = null;
        }

        //  high light chord and lyrics
        switch (songUpdate.getState()) {
            case playing:
                //  add highlights
                if (songUpdate.getMeasure() >= 0) {
                    String lyricsCellId = prefix + Song.genLyricsId(songUpdate.getSectionNumber());
                    Element le = singer.getElementById(lyricsCellId);
                    if (le != null) {
                        le.getStyle().setBackgroundColor(highlightColor);
                        lastLyricsElement = le;
                    }
                }
                break;
        }

        //  auto scroll
        autoScroll(lyricsScrollPanel, singer);
    }

    private AudioBeatDisplay audioBeatDisplay;

    public static final String highlightColor = "#e4c9ff";
    private static String prefix = "singer";
    private static final Document document = Document.get();
    private static final Logger logger = Logger.getLogger(SingerView.class.getName());
}
