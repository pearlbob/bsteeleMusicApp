/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.songs.*;
import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class PlayerView
        extends ViewImpl
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
    SpanElement title;

    @UiField
    SpanElement artist;

    @UiField
    CanvasElement audioBeatDisplayCanvas;

    @UiField
    ScrollPanel chordsScrollPanel;

    @UiField
    HTMLPanel player;

    @UiField
    SpanElement copyright;


    interface Binder extends UiBinder<Widget, PlayerView> {
    }

    @Inject
    PlayerView(final EventBus eventBus, Binder binder, SongPlayMaster songPlayMaster) {
        this.eventBus = eventBus;
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
            if (song != null) {
                transpose(0);
            }
        });

        keyUpButton.addClickHandler((ClickEvent event) -> {
            if (song != null) {
                transpose(currentKeyTransposition + 1);
            }
        });
        keyDownButton.addClickHandler((ClickEvent event) -> {
            if (song != null) {
                transpose(currentKeyTransposition - 1);
            }
        });

        currentBpmEntry.addChangeHandler((event) -> {
            setBpm(currentBpmEntry.getValue());
        });

        Event.sinkEvents(bpmSelect, Event.ONCHANGE);
        Event.setEventListener(bpmSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                setBpm(bpmSelect.getValue());
                bpmSelect.setSelectedIndex(0);
            }
        });
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
            switch (lastState) {
                case playing:
                    scrollPosition = 0;
                    chordsScrollPanel.setVerticalScrollPosition(0);
                    break;
            }
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

        //  set the song prior to play selection overrides
        if (song == null || !song.equals(songUpdate.getSong()))
            setSong(songUpdate.getSong(), songUpdate.getCurrentKey());

        setCurrentKey(songUpdate.getCurrentKey());
        setBpm(songUpdate.getCurrentBeatsPerMinute());
    }

    private void setCurrentKey(Key key) {
        this.currentKey = key;
        currentKeyTransposition = key.getHalfStep() - song.getKey().getHalfStep();
        keyLabel.setInnerHTML(currentKey.toString());
    }

    private void setBpm(String bpm) {
        setBpm(Integer.parseInt(bpm));
    }

    private void setBpm(int bpm) {
        currentBpmEntry.setValue(Integer.toString(bpm));
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
                        String chordCellId = prefix + songUpdate.getSectionNumber() + Song.genChordId(songUpdate.getSectionVersion(),
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
        switch (songUpdate.getState()) {
            case playing:
                if (song.getTotalBeats() == 0 || player.getOffsetHeight() == 0)
                    break;

                //  auto scroll
                int max = chordsScrollPanel.getMaximumVerticalScrollPosition();
                int h = chordsScrollPanel.getOffsetHeight();

                scrollPosition += (max+1.5*h)/ (60.0 * song.getTotalBeats() * 60 / songUpdate.getCurrentBeatsPerMinute());
                scrollPosition = Math.min(max+h/2, scrollPosition);
                //GWT.log("scroll: " + Double.toString(scrollPosition)+"  m: "+Integer.toString(max)+"  h: "+Integer.toString(h));
                int position = (int) Math.rint(scrollPosition);
                position = Math.max(0, Math.min(position - h / 2, max));
                int currentPosition = chordsScrollPanel.getVerticalScrollPosition();
//                if (Math.abs(currentPosition - position) > 4) {
//                    scrollPosition = currentPosition;    // let the human override the scroll
//                    lastScrollPosition = (int) Math.rint(scrollPosition);
//                } else
                if (position != lastScrollPosition && scrollDelay > 1) {
                    lastScrollPosition = position;
                    chordsScrollPanel.setVerticalScrollPosition(position);
                    scrollDelay = 0;
                   // GWT.log("player scroll: " + Double.toString(scrollPosition) + "  " + Integer.toString(max));
                }
                scrollDelay++;
                break;
        }
    }


    @Override
    public void setSong(Song song) {
        setSong(song, null);
    }

    private void setSong(Song song, Key key) {

        if (key == null) {
            boolean keepKey = (this.song != null && song != null
                    && this.song.getSongId().equals(song.getSongId()));  //  identity only
            if (keepKey)
                ;   //  keep the current key transposition unchanged from our recent use of this song
            else
                currentKeyTransposition = 0;
        } else
            currentKeyTransposition = key.getHalfStep() - song.getKey().getHalfStep();

        this.song = song;
        originalKey = song.getKey();

        //  load new data even if the identity has not changed
        title.setInnerHTML(song.getTitle());
        artist.setInnerHTML(song.getArtist());
        copyright.setInnerHTML(song.getCopyright());

        timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

        setBpm(song.getBeatsPerMinute());
        transpose(currentKeyTransposition);

//        lyrics.clear();
//        lyrics.add(new HTML(song.generateHtmlLyricsTable()));


        scrollPosition = 0;
        chordsScrollPanel.setVerticalScrollPosition(0);
        chordsFontSize = 0;     //    will never match, forces the fontSize set
        chordsDirty = true;   //  done by transpose()
    }

    private void transpose(int tran) {
        currentKeyTransposition = Util.mod(tran, MusicConstant.halfStepsPerOctave);

        currentKey = Key.getKeyByHalfStep(originalKey.getHalfStep() + currentKeyTransposition);
        keyLabel.setInnerHTML(currentKey.toString());

        player.clear();

        final String style = "com-bsteele-bsteeleMusicApp-client-resources-AppResources-Style-";
        ArrayList<LyricSection> lyricSections = song.parseLyrics();
        int sectionIndex = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        song.getChordSectionMap();
        for (LyricSection lyricSection : lyricSections) {
            sb.append("<tr>");
            sb.append("<td>")
                    .append(song.generateHtmlChordTable(lyricSection.getSectionVersion(), prefix + sectionIndex));
            sb.append("<td class=\"")
                    .append(style)
                    .append("sectionLabel \">")
                    .append(lyricSection.getSectionVersion().toString())
                    .append(":</td>");
            sb.append("<td")
                    .append(" class=\"")
                    .append(style)
                    .append("lyrics")
                    .append(lyricSection.getSectionVersion().getSection().getAbbreviation())
                    .append("Class\"")
                    .append(" id=\"" + prefix)
                    .append(Song.genLyricsId(sectionIndex)).append("\">");
            for (LyricsLine lyricsLine : lyricSection.getLyricsLines())
                sb.append(lyricsLine.getLyrics()).append("\n");
            sb.append("</td>");
            sb.append("</tr>\n");
            sectionIndex++;
        }
        sb.append("</table>");
        player.add(new HTMLPanel(sb.toString()));
    }

    private AudioBeatDisplay audioBeatDisplay;
    private Song song;
    private Key originalKey;
    private SongUpdate songUpdate = new SongUpdate();
    private int currentKeyTransposition = 0;
    private Key currentKey = Key.getDefault();
    private Element lastChordElement;
    private Element lastLyricsElement;
    private boolean chordsDirty = true;
    private double chordsParentWidth;
    private double chordsParentHeight;
    private Element lastRepeatElement;
    private int lastRepeatTotal;
    private int lastMeasureNumber;
    private int lastScrollPosition = 0;
    private double scrollPosition = 0;
    private int scrollDelay = 0;
    private SongUpdate.State lastState = SongUpdate.State.idle;

    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private int chordsFontSize = chordsMaxFontSize;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private final EventBus eventBus;
    private static final String prefix = "player";
    private static final Logger logger = Logger.getLogger(PlayerView.class.getName());

}
