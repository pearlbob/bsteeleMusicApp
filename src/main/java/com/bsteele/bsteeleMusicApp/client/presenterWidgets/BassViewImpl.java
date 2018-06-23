/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.BassFile;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongMoment;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class BassViewImpl
        extends CommonPlayViewImpl
        implements BassPresenterWidget.MyView
{

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
    CanvasElement audioBeatDisplayCanvas;

    @UiField
    ScrollPanel chordsScrollPanel;

    @UiField
    HTMLPanel bass;

    @UiField
    SpanElement copyright;

    @UiField
    Button saveButton;


    interface Binder extends UiBinder<Widget, BassViewImpl>
    {
    }

    @Inject
    BassViewImpl(final EventBus eventBus, Binder binder, SongPlayMaster songPlayMaster)
    {
        super(eventBus, songPlayMaster);
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
            setCurrentKey(songUpdate.getSong().getKey());
        });

        keyUpButton.addClickHandler((ClickEvent event) -> {
            stepCurrentKey(+1);
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

        saveButton.addClickHandler((ClickEvent event) -> {
            BassFile bassFile = new BassFile();
            bassFile.writeBassFile(song);
        });

        keyLabel.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        keyLabel.getStyle().setWidth(3, Style.Unit.EM);
    }

    @Override
    public void onSongUpdate(SongUpdate songUpdate)
    {

        if (songUpdate == null || songUpdate.getSong() == null)
            return;     //  defense

        this.songUpdate = songUpdate;

        labelPlayStop();

        if (lastRepeatElement != null) {
            lastRepeatElement.setInnerText("x" + lastRepeatTotal);
            lastRepeatElement = null;
        }

        if (songUpdate.getState() != lastState) {
            switch (lastState) {
                case idle:
                    resetScroll(chordsScrollPanel);
                    break;
            }
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
                    Element re = bass.getElementById(id);
                    if (re != null) {
                        re.setInnerText("x" + (songUpdate.getRepeatCurrent() + 1) + "/" + songUpdate.getRepeatTotal());
                        lastRepeatElement = re;
                        lastRepeatTotal = songUpdate.getRepeatTotal();
                    }
                }
                break;
        }

        if (song != null && !song.equals(songUpdate.getSong())) {
            resetScroll(chordsScrollPanel);
        }
        song = songUpdate.getSong();

        //  load new data even if the identity has not changed
        title.setInnerHTML(song.getTitle());
        artist.setInnerHTML(song.getArtist());
        copyright.setInnerHTML(song.getCopyright());

        timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

        syncCurrentKey(songUpdate.getCurrentKey());
        syncCurrentBpm(songUpdate.getCurrentBeatsPerMinute());

        syncKey(songUpdate.getCurrentKey());

        chordsDirty = true;

        chordsFontSize = 0;     //    will never match, forces the fontSize set
        chordsDirty = true;   //  done by syncKey()
    }

    private void setEnables()
    {
        boolean enable = (songUpdate.getState() == SongUpdate.State.idle);

        originalKeyButton.setEnabled(enable);
        keyUpButton.setEnabled(enable);
        keyDownButton.setEnabled(enable);
        currentBpmEntry.setEnabled(enable);
        bpmSelect.setDisabled(!enable);
    }

    private void syncCurrentKey(Key key)
    {
        keyLabel.setInnerHTML(key.toString());
    }

    private void syncCurrentBpm(int bpm)
    {
        currentBpmEntry.setValue(Integer.toString(bpm));
        bpmSelect.setSelectedIndex(0);
    }

    private void labelPlayStop()
    {
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
    public void onMusicAnimationEvent(MusicAnimationEvent event)
    {
        if (song == null)
            return;

        audioBeatDisplay.update(event.getT(), songUpdate.getEventTime(),
                songUpdate.getCurrentBeatsPerMinute(), false, song.getBeatsPerBar());

        {
            Widget parent = bass.getParent();
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
                        String chordCellId = prefix + songUpdate.getSectionNumber() + Song.genChordId(songUpdate
                                        .getSectionVersion(),
                                songUpdate.getChordSectionRow(), songUpdate.getChordSectionColumn());
                        //GWT.log(chordCellId );
                        Element ce = bass.getElementById(chordCellId);
                        if (ce != null) {
                            ce.getStyle().setBackgroundColor(highlightColor);
                            lastChordElement = ce;
                        }
                        String lyricsCellId = prefix + Song.genLyricsId(songUpdate.getSectionNumber());
                        Element le = bass.getElementById(lyricsCellId);
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
        autoScroll(chordsScrollPanel, bass);

    }

    private void syncKey(Key key)
    {
        int tran = key.getHalfStep() - songUpdate.getSong().getKey().getHalfStep();
        syncKey(tran);
    }

    private void syncKey(int tran)
    {
        currentKey = Key.getKeyByHalfStep(song.getKey().getHalfStep() + tran);
        keyLabel.setInnerHTML(currentKey.toString() + " " + currentKey.sharpsFlatsToString());

        bass.clear();

        bass.add(new HTMLPanel(song.measureNodesToHtml(prefix + "Table", currentKey, tran)));

        NodeList<Element> nodeList = bass.getElement().getElementsByTagName("canvas");
        int limit = nodeList.getLength();
        for (int i = 0; i < limit; i++) {
            canvasElement = (CanvasElement) nodeList.getItem(i);
            scoreCtx = canvasElement.getContext2d();

            final RegExp timeSignatureExp = RegExp.compile("bassLine(\\d{1,4})-(\\d{1,4})$");
            MatchResult mr = timeSignatureExp.exec(canvasElement.getId());
            if (mr == null) {
                continue;
            }
            int firstMoment = Integer.parseInt(mr.getGroup(1));
            int lastMoment = Integer.parseInt(mr.getGroup(2));

//            gridCanvas.setCoordinateSpaceWidth(3000);
//            gridCanvas.setCoordinateSpaceHeight(2000);

            int w = canvasElement.getWidth();
            int h = canvasElement.getHeight();

            //  fill with white
            scoreCtx.setFillStyle(background);
            scoreCtx.fillRect(0, 0, w, h);

            //  write the score lines
            double scoreTop = 3 * scoreLineHeight;
            scoreLines(scoreTop);

            //   write the bar lines
            scoreCtx.setStrokeStyle(black);
            scoreCtx.setLineWidth(1);
            scoreCtx.beginPath();
            scoreCtx.moveTo(scoreMargin, scoreTop);
            scoreCtx.lineTo(scoreMargin, scoreTop + 4 * scoreLineHeight);
            scoreCtx.stroke();
            for (int barCount = 1; barCount <= bars; barCount++) {
                scoreCtx.beginPath();
                double x = barStart + barCount * barWidth;
                scoreCtx.moveTo(x, scoreTop);
                scoreCtx.lineTo(x, scoreTop + 4 * scoreLineHeight);
                scoreCtx.stroke();
            }

            //  write the bass clef
            {
                scoreCtx.setFillStyle("#000000");
                scoreCtx.setFont("84px sans-serif");
                    scoreCtx.fillText(MusicConstant.bassClef, 1.5 * hSpace, scoreTop + 4.2 * scoreLineHeight);
            }

            //  write the measure count
            {
                scoreCtx.setFillStyle("#000000");
                scoreCtx.setFont("12px sans-serif");
                for (int m = firstMoment; m <= lastMoment; m++) {
                    SongMoment songMoment = song.getSongMoments().get(m);
                    scoreCtx.fillText(Integer.toString(songMoment.getSequenceNumber()+1),
                            barStart + (m - firstMoment) * barWidth+hSpace, scoreTop-hSpace/2 );
                }
            }

            //  write the measure chords
            //  fixme: preliminary measure chords
            scoreCtx.setFillStyle("#000000");
            scoreCtx.setFont("bold 15px sans-serif");
            for (int m = firstMoment; m <= lastMoment; m++) {
                SongMoment songMoment = song.getSongMoments().get(m);
                scoreCtx.fillText(songMoment.getMeasure().toString(), barStart + (m - firstMoment) * barWidth,
                        scoreLineHeight);
            }
        }
    }

    private double scoreLines(double y)
    {
        scoreCtx.setStrokeStyle(black);
        scoreCtx.setLineWidth(1);
        double scoreLength = barStart + bars * barWidth;
        for (int line = 0; line < 5; line++) {
            scoreCtx.beginPath();
            scoreCtx.moveTo(scoreMargin, y);
            scoreCtx.lineTo(scoreLength, y);
            scoreCtx.stroke();
            y += scoreLineHeight;
        }
        return y;
    }

    private static final CssColor background = CssColor.make("white");
    private static final CssColor black = CssColor.make("black");
    private static final double scoreLineHeight = 15;
    private static final int bars = 4;
    private static final double barWidth = 200;
    private static final double hSpace = 5;
    private static final double scoreMargin = hSpace*2;

    private double barStart = 100 - scoreMargin;
    private CanvasElement canvasElement;
    private Context2d scoreCtx;

    private AudioBeatDisplay audioBeatDisplay;

    private boolean chordsDirty = true;
    private double chordsParentWidth;
    private double chordsParentHeight;
    private Element lastRepeatElement;
    private int lastRepeatTotal;
    private int lastMeasureNumber;


    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private int chordsFontSize = chordsMaxFontSize;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private static final String prefix = "bass";
    private static final Logger logger = Logger.getLogger(BassViewImpl.class.getName());

}
