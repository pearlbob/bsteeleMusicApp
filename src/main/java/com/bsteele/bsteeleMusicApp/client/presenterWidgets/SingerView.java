/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class SingerView
        extends ViewImpl
        implements SingerPresenterWidget.MyView {

    @UiField
    SpanElement keyLabel;
    @UiField
    SpanElement currentBpm;


    @UiField
    SpanElement timeSignature;

    @UiField
    SpanElement title;

    @UiField
    SpanElement artist;

    @UiField
    CanvasElement audioBeatDisplayCanvas;

    @UiField
    HTMLPanel chordsContainer;

    @UiField
    HTMLPanel singer;

    @UiField
    SpanElement copyright;


    interface Binder extends UiBinder<Widget, SingerView> {
    }

    @Inject
    SingerView(final EventBus eventBus, Binder binder, SongPlayMaster songPlayMaster) {
        this.eventBus = eventBus;
        initWidget(binder.createAndBindUi(this));

        audioBeatDisplay = new AudioBeatDisplay(audioBeatDisplayCanvas);
        labelPlayStop();

    }

    @Override
    public void onSongUpdate(SongUpdate songUpdate) {

        if (songUpdate == null || songUpdate.getSong() == null)
            return;     //  defense

        this.songUpdate = songUpdate;

        labelPlayStop();

        //  set the song prior to play selection overrides
        setSong(songUpdate.getSong(), songUpdate.getCurrentKey());

        setCurrentKey(songUpdate.getCurrentKey());
        setBpm(songUpdate.getCurrentBeatsPerMinute());
    }

    private void setCurrentKey(Key key) {
        keyLabel.setInnerHTML(key.toString());
    }

    private void setBpm(int bpm) {
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
        if (song != null)
            audioBeatDisplay.update(event.getT(), songUpdate.getEventTime(),
                    songUpdate.getCurrentBeatsPerMinute(), false, song.getBeatsPerBar());
    }


    @Override
    public void setSong(Song song) {
        setSong(song, null);
    }

    private void setSong(Song song, Key key) {
        this.song = song;

        //  load new data even if the identity has not changed
        title.setInnerHTML(song.getTitle());
        artist.setInnerHTML(song.getArtist());
        copyright.setInnerHTML(song.getCopyright());

        timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

        setCurrentKey(song.getKey());
        setBpm(song.getBeatsPerMinute());

        singer.clear();
        singer.add(new HTML(song.generateHtmlLyricsTable()));
    }

    private AudioBeatDisplay audioBeatDisplay;
    private Song song;
    private SongUpdate songUpdate = new SongUpdate();
    private Element lastLyricsElement;
    private int lastMeasureNumber;

    public static final String highlightColor = "#e4c9ff";
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private final EventBus eventBus;
    private static final Document document = Document.get();
    private static final Logger logger = Logger.getLogger(SingerView.class.getName());

}
