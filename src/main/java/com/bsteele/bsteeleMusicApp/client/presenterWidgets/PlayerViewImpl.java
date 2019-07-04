/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.application.GWTAppOptions;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.LyricSection;
import com.bsteele.bsteeleMusicApp.shared.songs.LyricsLine;
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
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
    Anchor title;

    @UiField
    Anchor artist;

    @UiField
    Button nextSongButton;
    @UiField
    Button prevSongButton;

    @UiField
    Label statusLabel;

//    @UiField
//    CanvasElement audioBeatDisplayCanvas;

    @UiField
    ScrollPanel chordsScrollPanel;

    @UiField
    HTMLPanel player;

    @UiField(provided = true)
    Canvas playerBackgroundCanvas;

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
        playerTopCover = Canvas.createIfSupported();

        initWidget(binder.createAndBindUi(this));


        // audioBeatDisplay = new AudioBeatDisplay(audioBeatDisplayCanvas);
        labelPlayStop();

        playStopButton.addClickHandler((ClickEvent event) -> {
            togglePlayStop();
        });

        originalKeyButton.addClickHandler((ClickEvent event) -> setCurrentKey(songUpdate.getSong().getKey()));

        keyUpButton.addClickHandler((ClickEvent event) -> stepCurrentKey(+1));
        keyDownButton.addClickHandler((ClickEvent event) -> stepCurrentKey(-1));

        currentBpmEntry.addChangeHandler((event) -> setCurrentBpm(currentBpmEntry.getValue()));

        Event.sinkEvents(bpmSelect, Event.ONCHANGE);
        Event.setEventListener(bpmSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                setCurrentBpm(bpmSelect.getValue());
            }
        });

        prevSongButton.addClickHandler((ClickEvent event) -> eventBus.fireEvent(new NextSongEvent(false)));
        nextSongButton.addClickHandler((ClickEvent event) -> eventBus.fireEvent(new NextSongEvent()));

        keyLabel.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        keyLabel.getStyle().setWidth(3, Style.Unit.EM);

        statusLabel.setVisible(false);

        playerTopCover.addKeyDownHandler(handler -> {
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
                            logger.finer("space");
                            togglePlayStop();
                            break;
                        default:
                            logger.info("playerTopCover.addKeyDownHandler: " + Integer.toString(handler.getNativeKeyCode()));
                            break;
                    }
                    break;
                case idle:
                    switch (handler.getNativeKeyCode()) {
                        case KeyCodes.KEY_SPACE:
                            logger.info("space");
                            togglePlayStop();
                            break;
                    }
                    break;
            }
        });
        playerTopCover.addMouseWheelHandler(handler -> {
            if (songUpdate == null || song == null)
                return;     //  defense

            int d = (handler.getDeltaY() < 0 ? -1 : 1);
            songPlayMaster.playSongOffsetRowNumber(d);

            logger.finer("playerTopCover.addMouseWheelHandler: " + handler.getDeltaY());
        });
    }

    private void togglePlayStop() {
        if (song != null) {
            switch (songUpdate.getState()) {
                case playing:
                    songPlayMaster.stopSong();
                    break;
                case idle:
                    SongUpdate songUpdateCopy = new SongUpdate();
                    songUpdateCopy.setSong(song.copySong());
                    songUpdateCopy.setCurrentBeatsPerMinute(Integer.parseInt(currentBpmEntry.getValue()));
                    songUpdateCopy.setCurrentKey(currentKey);
                    verticalScrollPositionOffset = 0;
                    songPlayMaster.playSongUpdate(songUpdateCopy);
                    playerTopCover.setFocus(true);
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

            chordsFontSize = 0;     //    will never match, forces the fontSize set
            chordsDirty = true;   //  done by syncKey()

            if (isSongDiff)
                scheduler.scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        resetScrollForLineAt(0);
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
        bpmSelect.setDisabled(!enable);

        nextSongButton.setEnabled(enable);
        prevSongButton.setEnabled(enable);
    }

    private void syncCurrentKey(Key key) {
        keyLabel.setInnerHTML(key.toString());
    }

    private void syncCurrentBpm(int bpm) {
        currentBpmEntry.setValue(Integer.toString(bpm));
        bpmSelect.setSelectedIndex(0);
    }

    private void labelPlayStop() // fixme: rename
    {
        switch (songUpdate.getState()) {
            case playing:
                playStopButton.setText("Stop");
                //audioBeatDisplayCanvas.getStyle().setDisplay(Style.Display.INLINE);
                statusLabel.setVisible(true);
                playerTopCover.getCanvasElement().getStyle().setZIndex(1);
                break;
            case idle:
                playStopButton.setText("Play");
                //audioBeatDisplayCanvas.getStyle().setDisplay(Style.Display.NONE);
                statusLabel.setVisible(false);
                scrollForLineAt(0); //  hide
                playerTopCover.getCanvasElement().getStyle().setZIndex(-2);
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
//        audioBeatDisplay.update(event.getT(), songUpdate.getEventTime(),
//                songUpdate.getCurrentBeatsPerMinute(), false, song.getBeatsPerBar());

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

//            //  turn off all highlights
//            if (lastLyricsElement != null) {
//                lastLyricsElement.getStyle().clearBackgroundColor();
//                lastLyricsElement = null;
//            }

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
                    String status = song.songMomentStatus(event.getBeatNumber(), songUpdate.getMomentNumber());
                    if (appOptions.isDebug()) {
                        status = status + " " + chordsScrollPanel.getVerticalScrollPosition();
                    }
                    if (!status.equals(lastStatus)) {
                        statusLabel.setText(status);
                        logger.finer(status);
                        lastStatus = status;
                    }
                    break;
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
                        flexTable, tran, lyricsDefaultFontSize, true);
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
            player.add(flexTable);

            playerFlexTable.addClickHandler(clickEvent -> {
                logger.fine("singleClick");

                HTMLTable.Cell cell = playerFlexTable.getCellForEvent(clickEvent);

                logger.info("click: (" + cell.getCellIndex() + ", " + cell.getRowIndex() + ")");
                Element e = cell.getElement();
                renderHorizontalLineAt((e.getAbsoluteTop() + e.getAbsoluteBottom()) / 2 - playerBackgroundCanvas.getAbsoluteTop());
            });
        }
    }

    private final void renderHorizontalLineAt(double y) {
        if (y == lastHorizontalLineY
                || !appOptions.isPlayWithLineIndicator())
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
        lastVerticalScrollPosition = (int) y;
    }

    private final void scrollForLineAt(double y) {
        scrollForLineY = y;
    }

    private final void scrollForLineAnimation() {
        double d = scrollForLineY - lastScrollLineY;
        final double minDelta = 0.05;
        final double maxDelta = 1.5;
        final double delta = (d >= 0 ? 1 : -1) * Math.min((Math.abs(d) / 4 * minDelta), maxDelta);

        if (Math.abs(d) <= minDelta)
            lastScrollLineY = scrollForLineY;
        else {
            lastScrollLineY += delta;
        }
        double y = lastScrollLineY;


        //  scroll if required
        double h = playerBackgroundCanvas.getOffsetHeight();
        int maxH = chordsScrollPanel.getOffsetHeight();
        int midH = maxH / 2;
        //  fixme: y += verticalScrollPositionOffset;
        if (y < midH)
            y = 0;
        else if (y > h - midH)
            y = (h - midH);
        else
            y = (y - midH);

        //  output if different
        int vp = (int) Math.round(y);
        if (vp != lastVerticalScrollPosition) {
            chordsScrollPanel.setVerticalScrollPosition(lastVerticalScrollPosition);
            lastVerticalScrollPosition = vp;
            logger.finer("lastScrollLineY: " + lastScrollLineY + " to " + lastVerticalScrollPosition);
        }
    }

//    protected final void onSongRender() {
//        if (playerFlexTable != null) {
//            FlexTable.FlexCellFormatter formatter = playerFlexTable.getFlexCellFormatter();
//            logger.fine("player table rows: " + playerFlexTable.getRowCount());
//            for (int r = 0; r < playerFlexTable.getRowCount(); r++) {
//                int cols = playerFlexTable.getCellCount(r);
//                for (int c = 0; c < cols; c++) {
//
//                    Element element = formatter.getElement(r, c);
//                    if (element == null)
//                        continue;
//
//                    logger.finer("  measure( " + c + ", " + r + "): top "
//                            + element.getAbsoluteBottom() + "-" + element.getAbsoluteTop() + " = "
//                            + (element.getAbsoluteBottom() - element.getAbsoluteTop()));
//                }
//            }
//        }
//        renderHorizontalLineAt(0);
//    }

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
    private int verticalScrollPositionOffset;
    private String lastStatus;


    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private int chordsFontSize = chordsMaxFontSize;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private static final int lyricsDefaultFontSize = lyricsMaxFontSize;
    private boolean isActive = false;
    private static final Scheduler scheduler = Scheduler.get();
    private static final GWTAppOptions appOptions = GWTAppOptions.getInstance();


    private static final Logger logger = Logger.getLogger(PlayerViewImpl.class.getName());

    static {
        //  logger.setLevel(Level.FINE);
    }

}
