/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.application.GWTAppOptions;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.LyricSection;
import com.bsteele.bsteeleMusicApp.shared.songs.LyricsLine;
import com.bsteele.bsteeleMusicApp.shared.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.shared.songs.SongMoment;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class PlayerViewImpl
        extends CommonPlayViewImpl
        implements AttachEvent.Handler,
        PlayerPresenterWidget.MyView {

    @UiField
    FocusPanel playerFocusPanel;

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
    Button bpmUpButton;
    @UiField
    Button bpmDownButton;

    @UiField
    SpanElement timeSignature;

    @UiField
    Anchor title;

    @UiField
    Anchor artist;

    @UiField
    Button nextSongButton;
    @UiField
    Button prevSongButton;

    @UiField
    Label playMeasureLabel;

    @UiField
    Label playNextMeasureLabel;

    @UiField
    Label playStatusLabel;

    @UiField
    CanvasElement audioBeatDisplayCanvas;

    @UiField
    ScrollPanel chordsScrollPanel;

    @UiField
    HTMLPanel player;

    @UiField(provided = true)
    Canvas playerBackgroundCanvas;

    @UiField(provided = true)
    Canvas jumpBackgroundCanvas;

    /**
     * An HTML trick:  a transparent canvas used to capture the scroll panel input when in play auto-scroll
     */
    @UiField(provided = true)
    Canvas playerTopCover;

    @UiField
    SpanElement copyright;


    interface Binder extends UiBinder<Widget, PlayerViewImpl> {
    }

    @Inject
    PlayerViewImpl(final EventBus eventBus, Binder binder, SongPlayMaster songPlayMaster) {
        super(eventBus, songPlayMaster);

        playerBackgroundCanvas = Canvas.createIfSupported();
        jumpBackgroundCanvas = Canvas.createIfSupported();
        playerTopCover = Canvas.createIfSupported();

        initWidget(binder.createAndBindUi(this));


        audioBeatDisplay = new AudioBeatDisplay(audioBeatDisplayCanvas);
        labelPlayStop();

        playStopButton.addClickHandler((ClickEvent event) -> {
            togglePlayStop();
        });

        originalKeyButton.addClickHandler((ClickEvent event) -> setCurrentKey(songUpdate.getSong().getKey()));

        keyUpButton.addClickHandler((ClickEvent event) -> stepCurrentKey(+1));
        keyDownButton.addClickHandler((ClickEvent event) -> stepCurrentKey(-1));
        bpmUpButton.addClickHandler((ClickEvent event) -> {
            stepCurrentBpm(+1);
            syncCurrentBpm(getCurrentBpm());
            updateCountIn();
            playerFocusPanel.setFocus(true);
        });
        bpmDownButton.addClickHandler((ClickEvent event) -> {
            stepCurrentBpm(-1);
            syncCurrentBpm(getCurrentBpm());
            updateCountIn();
            playerFocusPanel.setFocus(true);
        });


        //currentBpmEntry.setTitle("Hint: Click the text entry then tap the space bar at the desired rate.");
        currentBpmEntry.addChangeHandler((event) -> {
            try {
                setCurrentBpm(currentBpmEntry.getValue());
            } catch (NumberFormatException nfe) {
                logger.info("bad BPM: <" + currentBpmEntry.getValue() + ">");
                playerFocusPanel.setFocus(true);
            }
        });
        currentBpmEntry.addKeyPressHandler(handler -> {
            switch (handler.getCharCode()) {
                default:
                    logger.fine("bpm key: " + handler.getCharCode());
                    break;
                case KeyCodes.KEY_UP:
                case KeyCodes.KEY_RIGHT:
                    try {
                        setCurrentBpm(Integer.parseInt(currentBpmEntry.getValue()) + 1);
                        handler.preventDefault();
                    } catch (NumberFormatException nfe) {
                        logger.info("bad BPM: <" + currentBpmEntry.getValue() + ">");
                    }
                    break;
                case KeyCodes.KEY_DOWN:
                case KeyCodes.KEY_LEFT:
                    try {
                        setCurrentBpm(Integer.parseInt(currentBpmEntry.getValue()) - 1);
                        handler.preventDefault();
                    } catch (NumberFormatException nfe) {
                        logger.info("bad BPM: <" + currentBpmEntry.getValue() + ">");
                    }
                    break;
            }
        });

        prevSongButton.addClickHandler((ClickEvent event) -> eventBus.fireEvent(new NextSongEvent(false)));
        nextSongButton.addClickHandler((ClickEvent event) -> eventBus.fireEvent(new NextSongEvent()));

        keyLabel.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        keyLabel.getStyle().setWidth(3, Style.Unit.EM);

        playStatusLabel.setVisible(false);

        playerFocusPanel.addKeyDownHandler(handler -> {
            if (songUpdate == null || song == null)
                return;     //  defense

            switch (songUpdate.getState()) {
                case playing:
                    switch (handler.getNativeKeyCode()) {
                        case KeyCodes.KEY_DOWN:
                            logger.finer("down");
                            songPlayMaster.playSongOffsetRowNumber(1);
                            break;
                        case KeyCodes.KEY_UP:
                            logger.finer("up");
                            songPlayMaster.playSongOffsetRowNumber(-1);
                            break;
                        case KeyCodes.KEY_RIGHT:
                            logger.finer("right");
                            songPlayMaster.playSongOffsetRowNumber(1);
                            break;
                        case KeyCodes.KEY_LEFT:
                            logger.finer("left");
                            songPlayMaster.playSongOffsetRowNumber(-1);
                            break;
                        case KeyCodes.KEY_SPACE:
                            processSpaceKey(handler.isControlKeyDown());
                            break;
                        default:
                            logger.info("playerFocusPanel.addKeyDownHandler: " + Integer.toString(handler.getNativeKeyCode()));
                            break;
                    }
                    break;
                case idle:      //  fixme: not active in idle!
                    switch (handler.getNativeKeyCode()) {
                        case KeyCodes.KEY_SPACE:
                            processSpaceKey(handler.isControlKeyDown());
                            break;
                    }
                    break;
            }
        });
        playerTopCover.addMouseWheelHandler(handler -> {
            if (songUpdate == null || song == null || playerFlexTable == null || songPlayMaster == null)
                return;     //  defense

            switch (songUpdate.getState()) {
                case playing:
                    break;
                case idle:
                    playScrollAccumulator = 0;
                    return;
            }

            int d = handler.getDeltaY();
            if ((playScrollAccumulator < 0 && d > 0) || (playScrollAccumulator > 0 && d < 0))
                playScrollAccumulator = 0;
            playScrollAccumulator += d * 6 /*  gain    */;

            GridCoordinate gridCoordinate = song.getMomentGridCoordinate(songPlayMaster.getMomentNumber());
            if (gridCoordinate == null)
                return;

            FlexTable.FlexCellFormatter formatter = playerFlexTable.getFlexCellFormatter();
            Element e = formatter.getElement(gridCoordinate.getRow(), gridCoordinate.getCol());
            if (e == null)
                return;

            int h = e.getOffsetHeight();
            if (playScrollAccumulator < -h) {
                playScrollAccumulator += h;
                songPlayMaster.playSongOffsetRowNumber(-1);
                logger.fine("play scroll: bump -1");
            } else if (playScrollAccumulator > h) {
                playScrollAccumulator -= h;
                songPlayMaster.playSongOffsetRowNumber(1);
                logger.fine("play scroll: bump +1");
            }

            logger.finest("play scroll: " + handler.getDeltaY()
                    + ", h: " + h
                    + ", acc: " + playScrollAccumulator
            );
        });
        playerTopCover.addClickHandler((clickEvent) -> {
            if (song == null || playerFlexTable == null)
                return;

            playerFocusPanel.setFocus(true);

            int y = clickEvent.getY() + playerTopCover.getAbsoluteTop();

            FlexTable.FlexCellFormatter flexCellFormatter = playerFlexTable.getFlexCellFormatter();
            int rowLimit = playerFlexTable.getRowCount();
            int foundRow = rowLimit - 1;  //  worst case
            for (int r = 0; r < rowLimit; r++) {
                Element e = flexCellFormatter.getElement(r, 0);
                //logger.info("playerTopCover y: " + y +": "+ r + ": " + e.getAbsoluteTop() + " to " + e.getAbsoluteBottom());
                if (y <= e.getAbsoluteBottom()) {
                    foundRow = r;
                    break;
                }
            }
            logger.info("foundRow: " + foundRow);

            SongMoment songMoment = song.getSongMomentAtRow(foundRow);
            if (songMoment != null) {
                songMoment = song.getFirstSongMomentInSection(songMoment.getMomentNumber());
                if (songMoment != null) {
                    if (songPlayMaster != null) {
                        Element element;
                        GridCoordinate coordinate;

                        //  from location
                        SongMoment fromMoment = song.getLastSongMomentInSection(songPlayMaster.getMomentNumber());
                        if (fromMoment == null)
                            jumpSectionFromY = 0;
                        else {
                            int fromMomentNumber = (fromMoment != null ? fromMoment.getMomentNumber() : 0);
                            coordinate = song.getMomentGridCoordinate(fromMomentNumber);
                            element = flexCellFormatter.getElement(coordinate.getRow(), coordinate.getCol());
                            jumpSectionFromY = element.getAbsoluteBottom() - playerBackgroundCanvas.getAbsoluteTop();
                        }

                        //  to location
                        coordinate = song.getMomentGridCoordinate(songMoment.getMomentNumber());
                        element = flexCellFormatter.getElement(coordinate.getRow(), coordinate.getCol());
                        jumpSectionToY = element.getAbsoluteTop() - playerBackgroundCanvas.getAbsoluteTop();

                        //  width
                        element = flexCellFormatter.getElement(coordinate.getRow(),
                                playerFlexTable.getCellCount(coordinate.getRow()) - 1);
                        jumpSectionMaxX = element.getAbsoluteLeft();

                        logger.fine("jumpSectionToY: " + jumpSectionToY);

                        jumpMomentNumber = songPlayMaster.jumpSectionToFirstSongMomentInSection(songMoment.getMomentNumber());
                    }
                }
            }

        });
    }

    @Override
    public void onAttachOrDetach(AttachEvent event) {
        playerFocusPanel.setFocus(true);
    }

    private void processSpaceKey(boolean isControlKeyDown) {
        logger.finer("space");
        if (isControlKeyDown)
            tapToTempo();
        else
            togglePlayStop();
    }

    private void tapToTempo() {
        double tap = System.currentTimeMillis();
        double delta = tap - lastTap;

        if (delta > 0) {
            double rawBPM = 60 * 1000 / delta;
            double filteredBPM = Math.max(MusicConstant.minBpm, Math.min(MusicConstant.maxBpm, rawBPM));
            if (filteredBPM != rawBPM)
                tapCount = 0;
            else
                tapCount++;
            smoothedBPM = smoothedBPM * (1 - smoothedBPMPass) + filteredBPM * smoothedBPMPass;

            logger.fine("tapToTempo(): " + delta / 1000 + " s = " + smoothedBPM + " bpm  (" + filteredBPM + "), count: " + tapCount);
            if (tapCount >= 3)
                currentBpmEntry.setValue(Integer.toString((int) smoothedBPM));
        }
        lastTap = tap;
    }

    private void togglePlayStop() {
        if (song != null) {
            playScrollAccumulator = 0;

            switch (songUpdate.getState()) {
                case playing:
                    //  get set for a stop
                    songPlayMaster.stopSong();
                    playMeasureLabel.setText("");
                    playNextMeasureLabel.setText("");
                    playStatusLabel.setText("");
                    break;
                case idle:
                    //  ask for a play
                    SongUpdate songUpdateCopy = new SongUpdate();
                    songUpdateCopy.setSong(song.copySong());
                    songUpdateCopy.setCurrentBeatsPerMinute(Integer.parseInt(currentBpmEntry.getValue()));
                    songUpdateCopy.setCurrentKey(currentKey);
                    songPlayMaster.playSongUpdate(songUpdateCopy);
                    break;
            }
        }
    }

    @Override
    public void onSongUpdate(SongUpdate songUpdate) {

        if (songUpdate == null || songUpdate.getSong() == null)
            return;     //  defense

        this.songUpdate = songUpdate;

        if (!isActive)
            return;

        playerFocusPanel.setFocus(true);

        labelPlayStop();

        if (lastRepeatElement != null) {
            lastRepeatElement.setInnerText("x" + lastRepeatTotal);
            lastRepeatElement = null;
        }

        //  reset display at a stop
        if (songUpdate.getState() != lastState) {
            switch (lastState) {
                case idle:
                    renderHorizontalLineAt(0);
                    resetScrollForLineAt(0);
                    break;
                case playing:
                    renderHorizontalLineAt(0);
                    break;
            }
            lastState = songUpdate.getState();

            switch (lastState) {    //  the new one now
                case playing:
                    resetScrollForLineAt(0);
                    break;
                case idle:
                    //  turn off the prior highlight
                    if (lastChordElement != null) {
                        lastChordElement.getStyle().clearBackgroundColor();
                        lastChordElement = null;
                    }
                    break;
            }
            setEnables();
        }

        boolean isSongDiff = (song == null || !song.equals(songUpdate.getSong()));
        if (isSongDiff
                || !songUpdate.getCurrentKey().equals(lastKey)
        ) {
            song = songUpdate.getSong();

            //  load new data even if the identity has not changed
            setAnchors(title, artist);
            copyright.setInnerHTML(song.getCopyright());

            timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

            syncCurrentKey(songUpdate.getCurrentKey());
            syncCurrentBpm(songUpdate.getCurrentBeatsPerMinute());

            if (isSongDiff)
                lastKey = null; //  force update on song change
            syncKey(songUpdate.getCurrentKey());

            //chordsFontSize = 0;     //    will never match, forces the fontSize set
            chordsDirty = true;   //  done by syncKey()

            if (isSongDiff)
                scheduler.scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        resetScrollForLineAt(0);
                        playerFocusPanel.setFocus(true);
                    }
                });
        }

        if (song != null && playerFlexTable != null)
            switch (songUpdate.getState()) {
                case playing:
                    if (songUpdate.getMomentNumber() < 0) {
                        renderHorizontalLineAt(0);      //  typically off screen
                        scrollForLineAt(0);     //  to the top
                    } else {
                        SongMoment songMoment = song.getSongMoment(songUpdate.getMomentNumber());
                        if (songMoment == null)
                            break;
                        GridCoordinate gridCoordinate = song.getMomentGridCoordinate(songMoment);
                        FlexTable.FlexCellFormatter formatter = playerFlexTable.getFlexCellFormatter();

                        if (appOptions.isPlayWithLineIndicator()) {
                            int r = gridCoordinate.getRow();
                            if (r < playerFlexTable.getRowCount()) {
                                int c = gridCoordinate.getCol();
                                if (c < playerFlexTable.getCellCount(r)) {
                                    Element e = formatter.getElement(r, c);
                                    if (e != null) {
                                        int sectionBeats = song.getChordSectionBeats(songMoment.getChordSectionLocation());
                                        double ratio = (sectionBeats == 0 ? 0.5 : (double) songMoment.getSectionBeatNumber() / sectionBeats);
                                        renderHorizontalLineAt(
                                                e.getAbsoluteTop() + ratio * (e.getAbsoluteBottom() - e.getAbsoluteTop())
                                                        - playerBackgroundCanvas.getAbsoluteTop()
                                                        + 3);
                                    }
                                }
                            }
                        }

                        //  scroll the row into view
                        int r = gridCoordinate.getRow();
                        if (r < playerFlexTable.getRowCount()) {
                            int limit = playerFlexTable.getCellCount(r);
                            for (int c = 0; c < limit; c++) {
                                Element e = formatter.getElement(r, c);
                                if (e != null) {
                                    scrollForLineAt(e.getAbsoluteBottom() - playerBackgroundCanvas.getAbsoluteTop());
                                    break;
                                }
                            }
                        }
                    }
            }
    }

    private void setEnables() {
        boolean enable = (songUpdate.getState() == SongUpdate.State.idle);

        originalKeyButton.setEnabled(enable);
        keyUpButton.setEnabled(enable);
        keyDownButton.setEnabled(enable);
        currentBpmEntry.setEnabled(enable);

        nextSongButton.setEnabled(enable);
        prevSongButton.setEnabled(enable);

        if (enable) {
            bpmUpButton.setEnabled(enable);
            bpmDownButton.setEnabled(enable);
        }
    }

    private void syncCurrentKey(Key key) {
        keyLabel.setInnerHTML(key.toString());
    }

    private void syncCurrentBpm(int bpm) {
        setCurrentBpm(bpm);
        currentBpmEntry.setValue(Integer.toString(bpm));
    }

    private void labelPlayStop() // fixme: rename
    {
        switch (songUpdate.getState()) {
            case playing:
                playStopButton.setText("Stop");
                audioBeatDisplayCanvas.getStyle().setDisplay(
                        appOptions.isPlayWithBouncingBall()
                                ? Style.Display.INLINE_BLOCK
                                : Style.Display.NONE);
                playStatusLabel.setVisible(true);
                playerTopCover.getCanvasElement().getStyle().setZIndex(1);
                break;
            case idle:
                playStopButton.setText("Play");
                audioBeatDisplayCanvas.getStyle().setDisplay(Style.Display.NONE);
                playStatusLabel.setVisible(false);
                scrollForLineAt(0); //  hide
                playerTopCover.getCanvasElement().getStyle().setZIndex(-10);
                jumpMomentNumber = null;
                jumpSectionToY = Integer.MIN_VALUE; //  force clearing of jump indicator
                break;
        }
    }

    @Override
    public void onMusicAnimationEvent(MusicAnimationEvent event) {
        if (song == null)
            return;

        if (!isActive)
            return;

        try {
            if (appOptions.isPlayWithBouncingBall())
                audioBeatDisplay.update(songUpdate, event.getBeatCount(), event.getBeatNumber(), event.getBeatFraction());

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
                if (lastLyricsElement != null) {
                    lastLyricsElement.getStyle().clearBackgroundColor();
                    lastLyricsElement = null;
                }

                //  high light chord and lyrics
                switch (songUpdate.getState()) {
                    case playing:
                        //  turn off the prior highlight
                        if (lastChordElement != null) {
                            lastChordElement.getStyle().clearBackgroundColor();
                            lastChordElement = null;
                        }

                        //  add highlights
                        if (appOptions.isPlayWithMeasureIndicator()
                                && songUpdate.getMomentNumber() >= 0
                                && songUpdate.getMomentNumber() < song.getSongMomentsSize()
                        ) {
                            GridCoordinate gridCoordinate = song.getMomentGridCoordinate(songUpdate.getMomentNumber());
                            FlexTable.FlexCellFormatter formatter = playerFlexTable.getFlexCellFormatter();
                            Element e = formatter.getElement(gridCoordinate.getRow(), gridCoordinate.getCol());

                            if (e != null) {
                                e.getStyle().setBackgroundColor(highlightColor);
                                lastChordElement = e;
                            }
//                        String lyricsCellId = prefix + Song.genLyricsId(songUpdate.getMomentNumber());
//                        Element le = player.getElementById(lyricsCellId);
//                        if (le != null) {
//                            le.getStyle().setBackgroundColor(highlightColor);
//                            lastLyricsElement = le;
                        }
                        break;
                }

                chordsDirty = false;
            }

            //  auto scroll
            switch (songUpdate.getState()) {
                case playing:
                    scrollForLineAnimation();
                    break;
            }

            //  status
            switch (songUpdate.getState()) {
                case playing:
                    if (appOptions.isPlayWithMeasureLabel()) {
                        int tran = currentKey.getHalfStep() - songUpdate.getSong().getKey().getHalfStep();
                        playMeasureLabel.setText(song.songMomentMeasure(songUpdate.getMomentNumber(),
                                currentKey, tran));
                        playNextMeasureLabel.setText(song.songNextMomentMeasure(songUpdate.getMomentNumber(),
                                currentKey, tran));
                    }

                    String status = song.songMomentStatus(event.getBeatNumber(), songUpdate.getMomentNumber());
                    if (!status.equals(lastStatus)) {
                        playStatusLabel.setText(status);
                        logger.finer(status);
                        lastStatus = status;
                    }

                    if (songPlayMaster.getMomentNumber() == 0) {
                        bpmUpButton.setEnabled(false);
                        bpmDownButton.setEnabled(false);
                    }
                    break;
            }

            if (songPlayMaster == null
                    || songPlayMaster.getSkipToNumber() == null)
                jumpMomentNumber = null;

            //  clear the jump indicator if necessary
            if (jumpMomentNumber != null || jumpSectionToY != lastJumpSectionToY) {
                Context2d ctx = jumpBackgroundCanvas.getContext2d();
                CanvasElement canvasElement = ctx.getCanvas();
                double w = canvasElement.getClientWidth();
                double h = canvasElement.getClientHeight();
                if (w != canvasElement.getWidth() || h != canvasElement.getHeight()) {
                    canvasElement.setWidth((int) w);
                    canvasElement.setHeight((int) h);
                }
                ctx.clearRect(0.0, 0.0, w, h);

                //  section jump indicator
                if (jumpMomentNumber != null) {
                    //  draw the indicator
                    ctx.setFillStyle("lightGray");
                    ctx.beginPath();
                    ctx.moveTo(0.0, jumpSectionFromY);
                    ctx.lineTo(jumpSectionMaxX, jumpSectionFromY);
                    ctx.lineTo(0.0, jumpSectionToY);
                    ctx.closePath();
                    ctx.fill();
                    lastJumpSectionToY = -1;
                } else if (jumpSectionToY != lastJumpSectionToY) {
                    lastJumpSectionToY = jumpSectionToY;
                }
            }
        } catch (
                Exception ex) {
            //  this is bad
            logger.severe(ex.getMessage());
        }
    }

    @Override
    public void setActive(boolean isActive) {
        this.isActive = isActive;
        if (isActive) {
            lastKey = null;    //  force update
            onSongUpdate(songUpdate);
            playerFocusPanel.setFocus(true);
        }
    }

    private void syncKey(Key key) {
        int tran = key.getHalfStep() - songUpdate.getSong().getKey().getHalfStep();
        syncKey(tran);
    }

    private void syncKey(int tran) {

        currentKey = Key.getKeyByHalfStep(song.getKey().getHalfStep() + tran);
        keyLabel.setInnerHTML(currentKey.toString() + " " + currentKey.sharpsFlatsToString());

        if (currentKey == lastKey) {
            return;
        }
        lastKey = currentKey;

        player.clear();

        ArrayList<LyricSection> lyricSections = song.getLyricSections();
        {
            FlexTable flexTable = new FlexTable();
            FlexTable.FlexCellFormatter formatter = flexTable.getFlexCellFormatter();
            final int lyricsCol = song.getChordSectionLocationGridMaxColCount() + 1;

            for (LyricSection lyricSection : lyricSections) {

                int firstRow = flexTable.getRowCount();
                logger.finer("lyricSection: " + lyricSection.getSectionVersion().toString());
                logger.finer("chordSection: " + song.getChordSection(lyricSection.getSectionVersion()).toString());
                logger.finest("pre  tran: " + flexTable.getRowCount()
                        + (firstRow > 0 ? " last row col: " + flexTable.getCellCount(flexTable.getRowCount() - 1) : ""));
                song.transpose(song.getChordSection(lyricSection.getSectionVersion()),
                        flexTable, tran, chordsFontSize, true);
                logger.finest("post tran: " + flexTable.getRowCount()
                        + (flexTable.getRowCount() > 0 ? " last row col: " + flexTable.getCellCount(flexTable.getRowCount() - 1) : ""));
                StringBuilder sb = new StringBuilder();
                for (LyricsLine lyricsLine : lyricSection.getLyricsLines())
                    sb.append(lyricsLine.getLyrics()).append("\n");

                //  fill the blanks in the table
                int lastRow = flexTable.getRowCount() - 1;
                for (int r = firstRow; r <= lastRow; r++)
                    for (int c = flexTable.getCellCount(r); c < lyricsCol; c++)
                        flexTable.setHTML(r, c, "");

                flexTable.setHTML(firstRow, lyricsCol, sb.toString());
                formatter.setStyleName(firstRow, lyricsCol, CssConstants.style + "lyrics" + lyricSection
                        .getSectionVersion().getSection().getAbbreviation() + "Class");
                formatter.setRowSpan(firstRow, lyricsCol, flexTable.getRowCount() - firstRow);
                logger.finest("post lyrics: " + firstRow + " to " + flexTable.getRowCount()
                        + (flexTable.getRowCount() > 0 ? " last row col: " + flexTable.getCellCount(flexTable.getRowCount() - 1) : ""));
            }
            if (playerFlexTable != null)
                playerFlexTable.removeFromParent();
            playerFlexTable = flexTable;
            playerFlexTable.setStyleName(AppResources.INSTANCE.style().chordTable());
            player.add(flexTable);
        }
    }

    private final void renderHorizontalLineAt(double y) {
        if (y == lastHorizontalLineY)
            return;

        logger.finest("y: " + y);
        Context2d ctx = playerBackgroundCanvas.getContext2d();
        CanvasElement canvasElement = ctx.getCanvas();
        double w = canvasElement.getClientWidth();
        double h = canvasElement.getClientHeight();
        if (w != canvasElement.getWidth() || h != canvasElement.getHeight()) {
            canvasElement.setWidth((int) w);
            canvasElement.setHeight((int) h);
        }

        final int lineWidth = 5;
        ctx.clearRect(0.0, lastHorizontalLineY - lineWidth / 2 - 1, w, lineWidth + 2);

        if (!appOptions.isPlayWithLineIndicator())
            return;     //  got cleared one time

        if (y > 0) {
            ctx.setStrokeStyle("darkGray");
            ctx.setLineWidth(lineWidth);
            ctx.beginPath();
            ctx.moveTo(0.0, y);
            ctx.lineTo(w, y);
            ctx.stroke();
        }

        lastHorizontalLineY = y;
    }

    private final void resetScrollForLineAt(double y) {
        scrollForLineY = lastScrollLineY = y;
        chordsScrollPanel.setVerticalScrollPosition((int) y);
        lastVerticalScrollPosition = Integer.MIN_VALUE;
    }

    private final void scrollForLineAt(double y) {
        scrollForLineY = y;
    }

    private final void scrollForLineAnimation() {
        final double d = scrollForLineY - lastScrollLineY;
        final double minDelta = 0.05;
        final double maxDelta = 1.5;
        final double delta = (d >= 0 ? 1 : -1) * Math.min((Math.abs(d) / 4 * minDelta), maxDelta);
        final double h = playerBackgroundCanvas.getOffsetHeight();
        final int maxH = chordsScrollPanel.getOffsetHeight();
        final int midH = maxH / 2;

        if (Math.abs(d) <= minDelta     //  too small to scroll
                || Math.abs(d) > midH  //  too large to scroll
        )
            lastScrollLineY = scrollForLineY;
        else
            lastScrollLineY += delta;

        double y = lastScrollLineY;

        //  scroll if required
        if (y < midH)
            y = 0;      //  at the top
        else if (y > h - midH)
            y = (h - midH);     //  at the bottom
        else
            y = (y - midH);

        //  output if different
        int vp = (int) Math.round(y);
        if (vp != lastVerticalScrollPosition) {
            chordsScrollPanel.setVerticalScrollPosition(vp);
            lastVerticalScrollPosition = vp;
            logger.finer("lastScrollLineY: " + lastScrollLineY + " to " + lastVerticalScrollPosition);
        }
    }

    private void updateCountIn() {
        switch (songUpdate.getState()) {
            case playing:
                if (songPlayMaster.getMomentNumber() < 0) {
                    SongUpdate songUpdateCopy = new SongUpdate();
                    songUpdateCopy.setSong(song.copySong());
                    songUpdateCopy.setCurrentBeatsPerMinute(Integer.parseInt(currentBpmEntry.getValue()));
                    songUpdateCopy.setCurrentKey(currentKey);
                    songPlayMaster.playSongUpdate(songUpdateCopy);
                }
                break;
        }
    }


    private AudioBeatDisplay audioBeatDisplay;

    private boolean chordsDirty = true;
    private double chordsParentWidth;
    private double chordsParentHeight;
    private Element lastRepeatElement;
    private int lastRepeatTotal;
    private int lastMeasureNumber;
    private FlexTable playerFlexTable;
    private double lastHorizontalLineY;
    private double scrollForLineY;
    private double lastScrollLineY;
    private int lastVerticalScrollPosition = 0;
    private String lastStatus;
    private Integer jumpMomentNumber = null;
    private int jumpSectionFromY = 0;
    private int jumpSectionToY = 0;
    private int lastJumpSectionToY = -1;
    private int jumpSectionMaxX = 10;
    private int playScrollAccumulator = 0;


    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private static int chordsFontSize = 42;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private static final int lyricsDefaultFontSize = lyricsMaxFontSize;
    private boolean isActive = false;
    private static final Scheduler scheduler = Scheduler.get();
    private static final AppOptions appOptions = GWTAppOptions.getInstance();


    private static final Logger logger = Logger.getLogger(PlayerViewImpl.class.getName());

    static {
        //  logger.setLevel(Level.FINE);
    }

}
