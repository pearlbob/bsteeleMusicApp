/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.AudioBeatDisplay;
import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.application.AppOptions;
import com.bsteele.bsteeleMusicApp.client.application.events.MusicAnimationEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.LyricSection;
import com.bsteele.bsteeleMusicApp.shared.songs.LyricsLine;
import com.bsteele.bsteeleMusicApp.shared.songs.SectionVersion;
import com.bsteele.bsteeleMusicApp.shared.songs.SongMoment;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
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

    @UiField
    CanvasElement playerBackgroundElement;

    @UiField
    SpanElement copyright;


    interface Binder extends UiBinder<Widget, PlayerViewImpl> {
    }

    @Inject
    PlayerViewImpl(final EventBus eventBus, Binder binder, SongPlayMaster songPlayMaster) {
        super(eventBus, songPlayMaster);
        initWidget(binder.createAndBindUi(this));

        // audioBeatDisplay = new AudioBeatDisplay(audioBeatDisplayCanvas);
        labelPlayStop();

        playStopButton.addClickHandler((ClickEvent event) -> {
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
                        songPlayMaster.playSongUpdate(songUpdateCopy);
                        break;
                }
            }
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
            switch (lastState) {
                case idle:
                    lastScrollLineY = 0;
                    resetScroll(chordsScrollPanel);
                    break;
            }
            lastState = songUpdate.getState();

            setEnables();
        }

        //  turn on highlights if required
//        switch (songUpdate.getState()) {
//            case idle:
//                break;
//            case playing:
//                if (songUpdate.getRepeatTotal() > 0) {
//                    final String id = prefix + Song.genChordId(songUpdate.getSectionVersion(),
//                            songUpdate.getRepeatLastRow(), songUpdate.getRepeatLastCol());
//                    Element re = player.getElementById(id);
//                    if (re != null) {
//                        re.setInnerText("x" + (songUpdate.getRepeatCurrent() + 1) + "/" + songUpdate.getRepeatTotal());
//                        lastRepeatElement = re;
//                        lastRepeatTotal = songUpdate.getRepeatTotal();
//                    }
//                }
//                break;
//        }

        if (song == null || !song.equals(songUpdate.getSong())) {
            song = songUpdate.getSong();

            switch (songUpdate.getState()) {
                default:
                case idle:
                    lastScrollLineY = 0;
                    scheduler.scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            resetScroll(chordsScrollPanel);
                        }
                    });
                    break;
                case playing:
                    break;
            }
        }

        //  load new data even if the identity has not changed
        setAnchors(title, artist);
        copyright.setInnerHTML(song.getCopyright());

        timeSignature.setInnerHTML(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());

        syncCurrentKey(songUpdate.getCurrentKey());
        syncCurrentBpm(songUpdate.getCurrentBeatsPerMinute());

        syncKey(songUpdate.getCurrentKey());

        chordsFontSize = 0;     //    will never match, forces the fontSize set
        chordsDirty = true;   //  done by syncKey()
        lastHorizontalLineY = 0;

        if (song != null && playerFlexTable != null) {
            SongMoment songMoment = song.getSongMoments().get(songUpdate.getMomentNumber());
            GridCoordinate gridCoordinate = song.getMomentGridCoordinate(songMoment);

            FlexTable.FlexCellFormatter formatter = playerFlexTable.getFlexCellFormatter();
            int r = gridCoordinate.getRow();
            if (r < playerFlexTable.getRowCount()) {
                int c = gridCoordinate.getCol();
                if (c < playerFlexTable.getCellCount(r)) {
                    Element e = formatter.getElement(r, c);
                    if (e != null) {
                        renderHorizontalLineAt((e.getAbsoluteTop() + e.getAbsoluteBottom()) / 2
                                - playerBackgroundElement.getAbsoluteTop());
                    }
                }
            }

            //  scroll into view
            r = gridCoordinate.getRow();
            if (r < playerFlexTable.getRowCount()) {
                int c = 0;
                if (c < playerFlexTable.getCellCount(r)) {
                    Element e = formatter.getElement(r, c);
                    if (e != null) {
                        scrollForLineAt(e.getAbsoluteBottom() - playerBackgroundElement.getAbsoluteTop());
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
    }

    private void syncCurrentKey(Key key) {
        keyLabel.setInnerHTML(key.toString());
    }

    private void syncCurrentBpm(int bpm) {
        currentBpmEntry.setValue(Integer.toString(bpm));
        bpmSelect.setSelectedIndex(0);
    }

    private void labelPlayStop() {
        switch (songUpdate.getState()) {
            case playing:
                playStopButton.setText("Stop");
                //audioBeatDisplayCanvas.getStyle().setDisplay(Style.Display.INLINE);
                statusLabel.setVisible(true);
                break;
            case idle:
                playStopButton.setText("Play");
                //audioBeatDisplayCanvas.getStyle().setDisplay(Style.Display.NONE);
                statusLabel.setVisible(false);
                scrollForLineAt(0); //  hide
                break;
        }
    }

    @Override
    public void onMusicAnimationEvent(MusicAnimationEvent event) {
        if (song == null)
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
//            if (lastChordElement != null) {
//                lastChordElement.getStyle().clearBackgroundColor();
//                lastChordElement = null;
//            }
//            if (lastLyricsElement != null) {
//                lastLyricsElement.getStyle().clearBackgroundColor();
//                lastLyricsElement = null;
//            }
//
//            //  high light chord and lyrics
//            switch (songUpdate.getState()) {
//                case playing:
//                    //  add highlights
//                    if (songUpdate.getMeasure() >= 0) {
//                        String chordCellId = prefix + songUpdate.getMomentNumber() + Song.genChordId(songUpdate
//                                        .getSectionVersion(),
//                                songUpdate.getChordSectionRow(), songUpdate.getChordSectionColumn());
//                        //GWT.log(chordCellId );
//                        Element ce = player.getElementById(chordCellId);
//                        if (ce != null) {
//                            ce.getStyle().setBackgroundColor(highlightColor);
//                            lastChordElement = ce;
//                        }
//                        String lyricsCellId = prefix + Song.genLyricsId(songUpdate.getMomentNumber());
//                        Element le = player.getElementById(lyricsCellId);
//                        if (le != null) {
//                            le.getStyle().setBackgroundColor(highlightColor);
//                            lastLyricsElement = le;
//                        }
//                    }
//                    break;
//            }

                chordsDirty = false;
            }

            //  auto scroll

            //  status
            statusLabel.setText(song.songMomentStatus(songUpdate.getMomentNumber())
                    + " " + chordsScrollPanel.getVerticalScrollPosition()
            );
        } catch (Exception ex) {
            //  this is bad
            logger.severe(ex.getMessage());
        }
    }

    private void syncKey(Key key) {
        int tran = key.getHalfStep() - songUpdate.getSong().getKey().getHalfStep();
        syncKey(tran);
    }

    private void syncKey(int tran) {

        currentKey = Key.getKeyByHalfStep(song.getKey().getHalfStep() + tran);
        keyLabel.setInnerHTML(currentKey.toString() + " " + currentKey.sharpsFlatsToString());

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
                renderHorizontalLineAt((e.getAbsoluteTop() + e.getAbsoluteBottom()) / 2 - playerBackgroundElement.getAbsoluteTop());
            });
        }
    }

    private final void renderHorizontalLineAt(double y) {
        if (y == lastHorizontalLineY)
            return;
        lastHorizontalLineY = y;

        logger.finest("y: " + y);

        Context2d ctx = playerBackgroundElement.getContext2d();
        CanvasElement canvasElement = ctx.getCanvas();
        double w = canvasElement.getClientWidth();
        double h = canvasElement.getClientHeight();
        canvasElement.setWidth((int) w);
        canvasElement.setHeight((int) h);

        ctx.setFillStyle("#f5f0e1");    //  fixme: one location for background constant
        ctx.fillRect(0.0, 0.0, w, h);
        ctx.setStrokeStyle("black");
        ctx.setLineWidth(1.5);
        ctx.beginPath();
        ctx.moveTo(0.0, y);
        ctx.lineTo(w, y);
        ctx.stroke();
    }

    private final void scrollForLineAt(double y) {
        if (y <= lastScrollLineY)
            return;
        lastScrollLineY = y;

        //  scroll if required
        double h = playerBackgroundElement.getClientHeight();
        int maxH = chordsScrollPanel.getOffsetHeight();
        int midH = maxH / 2;
        if (y < midH)
            chordsScrollPanel.setVerticalScrollPosition(0);
        else if (y > h - midH)
            chordsScrollPanel.setVerticalScrollPosition((int) (h - midH));
        else
            chordsScrollPanel.setVerticalScrollPosition((int) (y - midH));
    }

    protected final void onSongRender() {
        if (playerFlexTable != null) {
            FlexTable.FlexCellFormatter formatter = playerFlexTable.getFlexCellFormatter();
            logger.fine("player table rows: " + playerFlexTable.getRowCount());
            for (int r = 0; r < playerFlexTable.getRowCount(); r++) {
                int cols = playerFlexTable.getCellCount(r);
                for (int c = 0; c < cols; c++) {

                    Element element = formatter.getElement(r, c);
                    if (element == null)
                        continue;

                    logger.finer("  measure( " + c + ", " + r + "): top "
                            + element.getAbsoluteBottom() + "-" + element.getAbsoluteTop() + " = "
                            + (element.getAbsoluteBottom() - element.getAbsoluteTop()));
                }
            }
        }
        renderHorizontalLineAt(0);
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
    private double lastScrollLineY;


    public static final String highlightColor = "#e4c9ff";
    private static final int chordsMinFontSize = 8;
    private static final int chordsMaxFontSize = 52;
    private int chordsFontSize = chordsMaxFontSize;
    private static final int lyricsMinFontSize = 8;
    private static final int lyricsMaxFontSize = 28;
    private static final int lyricsDefaultFontSize = lyricsMaxFontSize;
    private static final String prefix = "player";
    private static final Scheduler scheduler = Scheduler.get();
    private static final AppOptions appOptions = AppOptions.getInstance();


    private static final Logger logger = Logger.getLogger(PlayerViewImpl.class.getName());

    static {
        // logger.setLevel(Level.FINE);
    }

}
