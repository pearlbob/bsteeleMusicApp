/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.shared.Util;
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
                        re.setInnerText("x" + (songUpdate.getRepeatCurrent() + 1) + "/" + songUpdate.getRepeatTotal());
                        lastRepeatElement = re;
                        lastRepeatTotal = songUpdate.getRepeatTotal();
                    }
                }
                break;
        }

        //  set the song prior to play selection overrides
        setSong(songUpdate.getSong(), songUpdate.getCurrentKey());

        setCurrentKey(songUpdate.getCurrentKey());
        setBpm(songUpdate.getCurrentBeatsPerMinute());

        chordsFontSize =0;//    will never match, forces the fontSize set
        chordsDirty = true;

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
        if (song != null)
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
                        String chordCellId = Song.genChordId(songUpdate.getSectionVersion(),
                                songUpdate.getChordSectionRow(), songUpdate.getChordSectionColumn());

                        Element ce = chords.getElementById(chordCellId);
                        if (ce != null) {
                            ce.getStyle().setBackgroundColor(highlightColor);
                            lastChordElement = ce;
                        }
                        String lyricsCellId = Song.genLyricsId(songUpdate.getSectionNumber());
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

        lyrics.clear();
        lyrics.add(new HTML(song.generateHtmlLyricsTable()));

        //chordsDirty = true;   //  done by transpose()
    }

    private void transpose(int tran) {
        currentKeyTransposition = Util.mod(tran, MusicConstant.halfStepsPerOctave);

        currentKey = Key.getKeyByHalfStep(originalKey.getHalfStep() + currentKeyTransposition);
        keyLabel.setInnerHTML(currentKey.toString());

        chords.clear();
        chords.add(new HTML(song.transpose(currentKeyTransposition)));

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
                        
                        if (parentWidth < tableWidth
                                || parentHeight < tableHeight
                                || parentWidth > (1+1.0/chordsFontSize) * tableWidth) {
                            double ratio = Math.min(parentWidth / tableWidth,
                                    parentHeight / tableHeight);
                            logger.fine("wratio: " + (parentWidth / tableWidth)
                                    + ", hratio: " + (parentHeight / tableHeight));
                            NodeList<Element> cells = e.getElementsByTagName("td");

                            int size = (int) Math.floor(Math.max(chordsMinFontSize,Math.min(chordsFontSize*ratio,chordsMaxFontSize)));
                            if (chordsFontSize != size) {
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
                            sendStatus("chords ratio", Double.toString(ratio) + ", size: " + Double.toString(size));

                            chordsDirty = !((ratio >= 1 && ratio <= (1+2.0/chordsFontSize))
                                    || chordsFontSize == chordsMinFontSize
                                    || chordsFontSize == chordsMaxFontSize);
                            sendStatus("chordsDirty", Boolean.toString(chordsDirty));
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

    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private   int chordsFontSize = chordsMaxFontSize;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private final EventBus eventBus;
    private static final Logger logger = Logger.getLogger(LyricsAndChordsView.class.getName());

}
