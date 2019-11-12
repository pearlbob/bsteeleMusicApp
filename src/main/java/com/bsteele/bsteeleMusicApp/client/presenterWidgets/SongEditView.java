/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.GwtLocalStorage;
import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongRemoveEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongRemoveEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.shared.GridCoordinate;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordComponent;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordDescriptor;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordSection;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordSectionLocation;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.Measure;
import com.bsteele.bsteeleMusicApp.shared.songs.MeasureEditType;
import com.bsteele.bsteeleMusicApp.shared.songs.MeasureNode;
import com.bsteele.bsteeleMusicApp.shared.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.shared.songs.Phrase;
import com.bsteele.bsteeleMusicApp.shared.songs.ScaleChord;
import com.bsteele.bsteeleMusicApp.shared.songs.ScaleNote;
import com.bsteele.bsteeleMusicApp.shared.songs.Section;
import com.bsteele.bsteeleMusicApp.shared.songs.SectionVersion;
import com.bsteele.bsteeleMusicApp.shared.songs.SongBase;
import com.bsteele.bsteeleMusicApp.shared.songs.SongMoment;
import com.bsteele.bsteeleMusicApp.shared.util.UndoStack;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author bob
 */
public class SongEditView
        extends ViewImpl
        implements SongEditPresenterWidget.MyView,
        HasHandlers {

    @UiField
    ButtonElement songEnter;

    @UiField
    ButtonElement songEntryClear;

    @UiField
    ButtonElement songEntryRemove;

    @UiField
    Button nextSongButton;
    @UiField
    Button prevSongButton;

    @UiField
    Label errorLabel;
    @UiField
    Label debugLabel;

    @UiField
    TextBox titleEntry;

    @UiField
    TextBox artistEntry;

    @UiField
    TextBox copyrightEntry;

    @UiField
    SelectElement keySelection;

    @UiField
    ButtonElement keyGuess;

    @UiField
    TextBox bpmEntry;

    @UiField
    SelectElement timeSignatureEntry;

    @UiField
    Label userLabel;

    @UiField
    Button sectionI;
    @UiField
    Button sectionV;
    @UiField
    Button sectionPC;
    @UiField
    Button sectionC;
    @UiField
    Button sectionBr;
    @UiField
    Button sectionO;
    @UiField
    Button sectionA;
    @UiField
    Button sectionB;
    @UiField
    Button sectionCo;
    @UiField
    Button sectionT;

    @UiField
    SelectElement sectionVersionSelect;

    @UiField
    TextBox measureEntry;

    @UiField
    Label measureEntryCorrection;

    @UiField
    RadioButton editInsert;

    @UiField
    RadioButton editReplace;

    @UiField
    Button editDelete;

    @UiField
    RadioButton editAppend;

    @UiField
    FlexTable chordsFlexTable;

    @UiField
    Button chordsI;
    @UiField
    Button chordsIV;
    @UiField
    Button chordsIV7;
    @UiField
    Button chordsV;
    @UiField
    Button chordsV7;
    @UiField
    Button chordsii;
    @UiField
    Button chordsiii;
    @UiField
    Button chordsvi;
    @UiField
    Button chordsvii;
    @UiField
    SelectElement scaleNoteSelection;

    @UiField
    SelectElement scaleNoteOtherSelection;

    @UiField
    Button recent0;
    @UiField
    Button recent1;
    @UiField
    Button recent2;
    @UiField
    Button recent3;
    @UiField
    Button recent4;
    @UiField
    Button recent5;
    @UiField
    Button common0;
    @UiField
    Button common1;
    @UiField
    Button common2;
    @UiField
    Button common3;
    @UiField
    Button common4;
    @UiField
    Button common5;

    @UiField
    Button noRepeat;
    @UiField
    Button repeat2;
    @UiField
    Button repeat3;
    @UiField
    Button repeat4;
    @UiField
    Button noChord;

    @UiField
    Button major;
    @UiField
    Button minor;
    @UiField
    Button dominant7;

    @UiField
    Button undo;
    @UiField
    Button redo;
    @UiField
    Button showHints;
    @UiField
    DivElement editHints;

    @UiField
    TextArea lyricsTextEntry;

    interface Binder extends UiBinder<Widget, SongEditView> {
    }

    @Inject
    SongEditView(@Nonnull final EventBus eventBus, Binder binder) {
        this.eventBus = eventBus;
        initWidget(binder.createAndBindUi(this));

        handlerManager = new HandlerManager(this);

        chordDescriptorMap.put(ChordDescriptor.major, major);
        chordDescriptorMap.put(ChordDescriptor.minor, minor);
        chordDescriptorMap.put(ChordDescriptor.dominant7, dominant7);

        //  key selection
        Event.sinkEvents(keySelection, Event.ONCHANGE);
        Event.setEventListener(keySelection, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                setKey(Key.parse(keySelection.getValue()));
                checkSong();
            }
        });


        Event.sinkEvents(scaleNoteSelection, Event.ONCHANGE);
        Event.setEventListener(scaleNoteSelection, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                titleChordSelections();
                checkSong();
            }
        });

        Event.sinkEvents(scaleNoteOtherSelection, Event.ONCHANGE);
        Event.setEventListener(scaleNoteOtherSelection, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                if (scaleNoteOtherSelection.getSelectedIndex() > 0)
                    enterChord(scaleNoteOtherSelection.getValue());
                scaleNoteOtherSelection.setSelectedIndex(0);    //  hack: clear the selection to get a hit on change
            }
        });

        measureEntry.addValueChangeHandler((ValueChangeEvent<String> event) -> {
            logger.fine("measure change: \"" + event.getValue() + "\"");
            processMeasureEntry();
        });
        measureEntry.addKeyUpHandler((KeyUpEvent event) -> {
            event.stopPropagation();

            if (event.isControlKeyDown())
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_DOWN:
                        logger.fine("measure KeyUp: \"" + event.getNativeKeyCode() + "\"");
                        selectChordCell(lastGridCoordinate.getRow() + 1, lastGridCoordinate.getCol());
                        measureFocus();
                        return;
                    case KeyCodes.KEY_RIGHT:
                        logger.fine("measure KeyUp: \"" + event.getNativeKeyCode() + "\"");
                        selectChordCell(lastGridCoordinate.getRow(), lastGridCoordinate.getCol() + 1);
                        measureFocus();
                        return;
                    case KeyCodes.KEY_UP:
                        logger.fine("measure KeyUp: \"" + event.getNativeKeyCode() + "\"");
                        selectChordCell(lastGridCoordinate.getRow() - 1, lastGridCoordinate.getCol());
                        measureFocus();
                        return;
                    case KeyCodes.KEY_LEFT:
                        logger.fine("measure KeyUp: \"" + event.getNativeKeyCode() + "\"");
                        selectChordCell(lastGridCoordinate.getRow(), lastGridCoordinate.getCol() - 1);
                        measureFocus();
                        return;
//                case KeyCodes.KEY_Z:  //  fixme:  only works if measureEntry is focused but conflicts with normal text processing there
//                    if (event.isControlKeyDown()) {
//                        if (event.isShiftKeyDown()) {
//                            if (undoStack.canRedo())
//                                setSong(undoStack.redo());
//                        } else {
//                            if (undoStack.canUndo())
//                                setSong(undoStack.undo());
//                        }
//                        return;
//                    }
//                    break;
                    default:
                        break;
                }

            String entry = measureEntry.getValue();
            if (entry.isEmpty())
                return;

            switch (event.getNativeKeyCode()) {
                case KeyCodes.KEY_ENTER:
                    processMeasureEntry();
                    break;
                default:
                    preProcessMeasureEntry();
                    break;
            }
        });

        editAppend.setValue(true);
        editInsert.addClickHandler((ClickEvent e) -> {
            setCurrentMeasureEditType(MeasureEditType.insert);
        });
        editReplace.addClickHandler((ClickEvent e) -> {
            setCurrentMeasureEditType(MeasureEditType.replace);
        });
        editDelete.setEnabled(false);
        editDelete.addClickHandler((ClickEvent e) -> {
            deleteCurrentChordSectionLocation();
        });

        editAppend.addClickHandler((ClickEvent e) -> {
            setCurrentMeasureEditType(MeasureEditType.append);
        });

        chordsFlexTable.addDragStartHandler(dragStartEvent -> {
            dragStartEvent.stopPropagation();
            logger.fine("dragStartEvent: " + dragStartEvent.toDebugString());
        });
        chordsFlexTable.addDragEndHandler(dragEndEvent -> {
            dragEndEvent.stopPropagation();
            logger.fine("dragEndEvent: " + dragEndEvent.toDebugString());
        });
        chordsFlexTable.addClickHandler(clickEvent -> {
            logger.fine("singleClick");
            measureFocus();

            HTMLTable.Cell cell = chordsFlexTable.getCellForEvent(clickEvent);
            setCurrentMeasureEditType(MeasureEditType.append);
            if (cell != null && song != null)
                selectChordCell(cell.getRowIndex(), cell.getCellIndex());
            updateCurrentChordEditLocation();
        });
        chordsFlexTable.addDoubleClickHandler(doubleClickEvent -> {
            logger.fine("doubleClick");
            measureFocus();

            Element td = getEventTargetCell(Event.as(doubleClickEvent.getNativeEvent()));
            if (td == null) {
                return;
            }
            int row = TableRowElement.as(td.getParentElement()).getSectionRowIndex();
            int column = TableCellElement.as(td).getCellIndex();

            setCurrentMeasureEditType(MeasureEditType.replace);
            updateCurrentChordEditLocation(row, column);
        });

        lyricsTextEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });

        sectionI.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.intro);
        });
        sectionV.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.verse);
        });
        sectionPC.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.preChorus);
        });
        sectionC.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.chorus);
        });
        sectionBr.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.bridge);
        });
        sectionO.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.outro);
        });
        sectionA.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.a);
        });
        sectionB.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.b);
        });
        sectionCo.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.coda);
        });
        sectionT.addClickHandler((ClickEvent event) -> {
            processSectionEntry(Section.tag);
        });
        Event.sinkEvents(sectionVersionSelect, Event.ONCHANGE);

        //  chord entry
        chordsI.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsIV.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsIV7.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsV.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsV7.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsiii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsvi.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        chordsvii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });

        recent0.setVisible(false);   //  hide them all initially
        recent0.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        recent1.setVisible(false);   //  hide them all initially
        recent1.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent2.setVisible(false);   //  hide them all initially
        recent2.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent3.setVisible(false);   //  hide them all initially
        recent3.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent4.setVisible(false);   //  hide them all initially
        recent4.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        recent5.setVisible(false);   //  hide them all initially
        recent5.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common0.setVisible(false);   //  hide them all initially
        common0.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common1.setVisible(false);   //  hide them all initially
        common1.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        common2.setVisible(false);   //  hide them all initially
        common2.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common3.setVisible(false);   //  hide them all initially
        common3.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common4.setVisible(false);   //  hide them all initially
        common4.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });
        common5.setVisible(false);   //  hide them all initially
        common5.addClickHandler((ClickEvent event) -> {
            enterChord(event);
        });

        noRepeat.addClickHandler((ClickEvent event) -> {
            setCurrentMeasureEditType(MeasureEditType.replace);
            processChordEntry("x1");
        });
        repeat2.addClickHandler((ClickEvent event) -> {
            setCurrentMeasureEditType(MeasureEditType.replace);
            processChordEntry("x2");
        });
        repeat3.addClickHandler((ClickEvent event) -> {
            setCurrentMeasureEditType(MeasureEditType.replace);
            processChordEntry("x3");
        });
        repeat4.addClickHandler((ClickEvent event) -> {
            setCurrentMeasureEditType(MeasureEditType.replace);
            processChordEntry("x4");
        });
        noChord.addClickHandler((ClickEvent event) -> {
            processChordEntry("X");
        });

        undo.setEnabled(false);
        undo.addClickHandler((ClickEvent event) -> {
            if (undoStack.canUndo()) {
                setSong(undoStack.undo());
                measureFocus();
            }
        });

        redo.setEnabled(false);
        redo.addClickHandler((ClickEvent event) -> {
            if (undoStack.canRedo()) {
                setSong(undoStack.redo());
                measureFocus();
            }
        });

        editHints.getStyle().setDisplay(Style.Display.NONE);
        showHints.addClickHandler((ClickEvent event) -> {
            areHintsHidden = !areHintsHidden;
            if (areHintsHidden)
                editHints.getStyle().setDisplay(Style.Display.NONE);
            else
                editHints.getStyle().clearDisplay();
        });

        Event.sinkEvents(songEnter, Event.ONCLICK);
        Event.setEventListener(songEnter, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                enterSong();
            }
        });

        Event.sinkEvents(keyGuess, Event.ONCLICK);
        Event.setEventListener(keyGuess, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                song.guessTheKey();
                checkSong();
            }
        });

        Event.sinkEvents(songEntryClear, Event.ONCLICK);
        Event.setEventListener(songEntryClear, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                clearSong();
            }
        });

        prevSongButton.addClickHandler((ClickEvent event) -> {
            eventBus.fireEvent(new NextSongEvent(false));
        });
        nextSongButton.addClickHandler((ClickEvent event) -> {
            eventBus.fireEvent(new NextSongEvent());
        });

        Event.sinkEvents(songEntryRemove, Event.ONCLICK);
        Event.setEventListener(songEntryRemove, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                if (song != null) {
                    fireEvent(new SongRemoveEvent(song));
                    clearSong();
                    eventBus.fireEvent(new NextSongEvent());
                }
            }
        });

        for (ChordDescriptor cd : chordDescriptorMap.keySet()) {
            Button b = chordDescriptorMap.get(cd);
            b.addClickHandler((ClickEvent event) -> {
                enterChord(event);
            });
        }

        titleEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });
        titleEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });
        artistEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });
        copyrightEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });
        bpmEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });

        Event.sinkEvents(timeSignatureEntry, Event.ONCHANGE);
        Event.setEventListener(timeSignatureEntry, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                checkSong();
            }
        });

        setKey(Key.getDefault());

        logger.info("songEditview info");
        logger.fine("songEditview fine");
        logger.finer("songEditview finer");
        logger.finest("songEditview finest");

    }

    private String preProcessMeasureEntry() {
        String entry = measureEntry.getValue();
        if (entry.isEmpty()) {
            measureEntryCorrection.getElement().setInnerHTML(nbsp);
            return "";
        }

        if (song == null)
            song = Song.createEmptySong();

        //  speed entry enhancement
        logger.fine("entryToUppercase: " + SongBase.entryToUppercase(entry));
        logger.fine("parseChordEntry: " + song.parseChordEntry(SongBase.entryToUppercase(entry)));
        String upperEntry = MeasureNode.concatMarkup(song.parseChordEntry(SongBase.entryToUppercase(entry)));
        if (upperEntry.equals(entry))
            measureEntryCorrection.getElement().setInnerHTML(nbsp);
        else
            measureEntryCorrection.setText(upperEntry);
        return upperEntry;
    }

    private void processMeasureEntry() {

        String entry = preProcessMeasureEntry();
        if (entry.isEmpty())
            return;

        processChordEntry(entry);
    }

    private boolean processSectionEntry(Section section) {
        if (section == null || song == null)
            return false;

        SectionVersion sectionVersion = new SectionVersion(section, sectionVersionSelect.getSelectedIndex());
        ChordSection chordSection = song.getChordSection(sectionVersion);
        if (chordSection != null)
            measureEntry.setValue(chordSection.toMarkup());
        else
            measureEntry.setValue(sectionVersion.toString());
        measureFocus();
        return true;
    }

    private boolean processChordEntry(String input) {
        errorLabel.setText(null);

        if (song == null)
            song = Song.createEmptySong();

        if (input.isEmpty())
            return false;

        ArrayList<MeasureNode> measureNodes = song.parseChordEntry(input);
        for (MeasureNode measureNode : measureNodes) {
            if (song.edit(measureNode)) {
                addRecentMeasureNode(measureNode);
                undoStackPushSong();
                findMostCommonScaleChords();
                updateCurrentChordEditLocation();
            }
        }

        checkSong();
        measureEntry.setValue("");
        preProcessMeasureEntry();
        measureFocus();
        return true;
    }

    private final void deleteCurrentChordSectionLocation() {
        errorLabel.setText(null);

        if (song == null)
            return;

        if (song.deleteCurrentSelection()) {
            undoStackPushSong();
            updateCurrentChordEditLocation();
        }
        checkSong();
        measureFocus();
    }

    private void setCurrentMeasureEditType(MeasureEditType type) {
        if (song == null)
            return;

        song.setCurrentMeasureEditType(type);
        updateCurrentChordEditLocation();
    }

    private void updateCurrentChordEditLocation() {
        measureFocus();
        if (song == null)
            return;

        switch (song.getCurrentMeasureEditType()) {
            default:
            case append:
                editAppend.setValue(true);
                editDelete.setEnabled(false);
                break;
            case insert:
                editInsert.setValue(true);
                editDelete.setEnabled(false);
                break;
            case replace:
                editReplace.setValue(true);
                editDelete.setEnabled(true);
                break;
            case delete:
                editReplace.setValue(true);
                editDelete.setEnabled(true);
                break;
        }
        selectChordCell(song.getCurrentChordSectionLocation());
    }

    private void updateCurrentChordEditLocation(int row, int col) {
        if (song == null)
            return;
        updateCurrentChordEditLocation(song.getChordSectionLocation(new GridCoordinate(row, col)));
    }

    private void updateCurrentChordEditLocation(ChordSectionLocation chordSectionLocation) {
        if (song == null)
            return;
        song.setCurrentChordSectionLocation(chordSectionLocation);
        updateCurrentChordEditLocation();
    }

    private final void clearSong() {
        onSongUpdate(Song.createEmptySong());
        displaySong();
        checkSong();
    }

    @Override
    public void onSongUpdate(Song newSong) {
        if (newSong == null)
            return;

        setSong(newSong);
        undoStackPushSong();
    }

    private final void setSong(Song newSong) {
        if (newSong == null)
            return;

        this.song = newSong.copySong();
        if (!isActive)
            return;

        titleEntry.setText(song.getTitle());
        artistEntry.setText(song.getArtist());
        copyrightEntry.setText(song.getCopyright());
        setKey(song.getKey());
        bpmEntry.setText(Integer.toString(song.getDefaultBpm()));
        timeSignatureEntry.setValue(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());
        userLabel.setText(song.getUser());
        lyricsTextEntry.setValue(song.getLyricsAsString());
        findMostCommonScaleChords();
        displaySong();
        updateCurrentChordEditLocation();

        ChordSectionLocation chordSectionLocation = song.getCurrentChordSectionLocation();

        if (chordSectionLocation != null)
            selectChordCell(chordSectionLocation);
        else
            selectLastChordsCell();
        sectionVersionSelect.setSelectedIndex(0);

        checkSong();

        setUndoRedoEnables();

        measureEntry.setValue("");

        logger.fine(song.toMarkup());
    }

    private void displaySong() {
        song.transpose(chordsFlexTable, 0, fontsize);
        logger.finer(song.toMarkup());
    }

    private final void undoStackPushSong() {
        if (song == null)
            return;
        logger.finest(song.toMarkup());
        logger.finest(song.copySong().toMarkup());
        undoStack.push(song.copySong());

        setUndoRedoEnables();
    }

    private final void setUndoRedoEnables() {
        undo.setEnabled(undoStack.canUndo());
        redo.setEnabled(undoStack.canRedo());
    }

    /**
     * Terrible workaround for missing  FlexTable.getCellForEvent(doubleClickEvent);
     *
     * @param event the event to look into
     * @return the element clicked
     */
    protected com.google.gwt.dom.client.Element getEventTargetCell(Event event) {
        for (com.google.gwt.dom.client.Element td = DOM.eventGetTarget(event); td != null; td = DOM.getParent(td)) {
            if (td.getPropertyString("tagName").equalsIgnoreCase("td")) {
//                Element tr = DOM.getParent(td);
//                Element body = DOM.getParent(tr);
//                if (body == this.bodyElem) {
                return DOM.asOld(td);
//                }
            }

//            if (td == this.bodyElem) {
//                return null;
//            }
        }

        return null;
    }

    private void selectLastChordsCell() {
        if (chordsFlexTable == null)
            return;
        int rows = chordsFlexTable.getRowCount();
        if (rows <= 0)
            return;
        int cols = chordsFlexTable.getCellCount(rows - 1);
        if (cols <= 0)
            return;
        selectChordCell(rows - 1, cols - 1);
    }


    private void selectChordCell() {
        if (song == null)
            return;
        selectChordCell(song.getCurrentChordSectionLocation());
    }

    /**
     * Select the selected chords cell
     *
     * @param row the row
     * @param col the column
     */
    private void selectChordCell(int row, int col) {
        selectChordCell(new GridCoordinate(row, col));
    }

    private void selectChordCell(GridCoordinate gridCoordinate) {

        if (chordsFlexTable == null || song == null || gridCoordinate == null)
            return;

        ChordSectionLocation chordSectionLocation = song.getChordSectionLocation(gridCoordinate);

        if (chordSectionLocation == null)
            return;

        selectChordCell(chordSectionLocation, gridCoordinate);
    }

    private void selectChordCell(ChordSectionLocation chordSectionLocation) {
        if (chordsFlexTable == null || song == null || chordSectionLocation == null)
            return;
        selectChordCell(chordSectionLocation, song.getGridCoordinate(chordSectionLocation));
    }

    private void selectChordCell(ChordSectionLocation chordSectionLocation, GridCoordinate gridCoordinate) {
        if (chordsFlexTable == null || song == null || chordSectionLocation == null || gridCoordinate == null)
            return;

        logger.fine("selectChordCell: " + chordSectionLocation.toString()
                + " at " + gridCoordinate.toString()
                + "  " + song.getCurrentChordSectionLocationMeasureNode().toMarkup());

        song.setCurrentChordSectionLocation(chordSectionLocation);

        FlexTable.FlexCellFormatter formatter = chordsFlexTable.getFlexCellFormatter();

        //  clear the last selection
        if (lastGridCoordinate != null) {
            try {
                Element le = formatter.getElement(lastGridCoordinate.getRow(), lastGridCoordinate.getCol());
                if (le != null) {
                    le.setAttribute("editSelect", "none");
                    le.getStyle().setBackgroundColor("");
                }
            } catch (IndexOutOfBoundsException ioob) {
            }
        }
        lastGridCoordinate = song.getGridCoordinate(chordSectionLocation);

        if (chordSectionLocation.isSection()) {
            switch (song.getCurrentMeasureEditType()) {
                case replace:
                case delete:
                    break;
                default:
                    setCurrentMeasureEditType(MeasureEditType.replace); //  fixme: shouldn't be here?
                    break;
            }
        }

        //  indicate the current selection
        try {
            logger.fine("rowCount: " + chordsFlexTable.getRowCount());
            Element e = formatter.getElement(gridCoordinate.getRow(), gridCoordinate.getCol());
            //  MeasureEditType editLocation = MeasureEditType.append;
            switch (song.getCurrentMeasureEditType()) {
                default:
                case append:
                    e.setAttribute("editSelect", "append");
                    e.getStyle().setBackgroundColor(null);
                    break;
                case insert:
                    e.setAttribute("editSelect", "insert");
                    e.getStyle().setBackgroundColor(null);
                    break;
                case replace:
                case delete:
                    e.setAttribute("editSelect", "replace");
                    e.getStyle().setBackgroundColor(selectedBorderColorValueString);
                    measureEntry.setText(song.toMarkup(chordSectionLocation));
                    break;
            }
        } catch (IndexOutOfBoundsException ioob) {
            //  can happen
            logger.fine(ioob.getMessage() + " at (" + gridCoordinate.getRow() + ", " + gridCoordinate.getCol() + ")");
            logger.fine(song.logGrid());
        }

        measureEntry.selectAll();
    }

    /**
     * Delete the last selected chords cell.
     * This will delete the entire chord section if the section header is selected.
     */
    private void deleteChordsCell() {

        if (song.chordSectionLocationDelete(song.getCurrentChordSectionLocation())) {
            undoStackPushSong();
            displaySong();
            editAppend.setValue(true);     // delete after delete?
            selectChordCell();

            measureFocus();
            checkSong();
        }
    }


    private static final String selectedBorderColorValueString = "#f88";

    public Song checkSong() {
        //  fixme: move checkSong() functionality to SongBase
        Key newKey = Key.parse(keySelection.getValue());
        if (newKey == null)
            newKey = Key.C;  //  punt an error
        if (key != newKey)   // avoid unnecessary changes
            setKey(newKey);

        String beatsPerBar = "";
        String unitsPerMeasure = "";
        final RegExp timeSignatureExp = RegExp.compile("^(\\d{1,2})\\/(\\d)$");
        MatchResult mr = timeSignatureExp.exec(timeSignatureEntry.getValue());
        if (mr != null) {
            // parse
            beatsPerBar = mr.getGroup(1);
            unitsPerMeasure = mr.getGroup(2);
        }

        Song newSong;
        errorLabel.setText(null);
        try {
            newSong = Song.checkSong(titleEntry.getText(), artistEntry.getText(),
                    copyrightEntry.getText(),
                    newKey, bpmEntry.getText(), beatsPerBar, unitsPerMeasure,
                    GwtLocalStorage.instance().getUserName(),
                    song.toMarkup(), lyricsTextEntry.getValue());
            warn(newSong.getMessage());
            //  worry about the location spec changing after parsing above
            newSong.setCurrentChordSectionLocation(newSong.getChordSectionLocation(
                    song.getGridCoordinate(song.getCurrentChordSectionLocation())));
            newSong.setCurrentMeasureEditType(song.getCurrentMeasureEditType());
        } catch (ParseException pe) {
            error(pe.getMessage());
            displaySong();
            selectChordCell();
            songEnter.setDisabled(true);
            return null;
        }

        song = newSong;
        displaySong();
        selectChordCell();
        songEnter.setDisabled(false);

        debug("current: " + song.getCurrentChordSectionLocation() + " " + song.getCurrentMeasureEditType());

        return newSong;
    }

    private void warn(String message) {
        errorLabel.setText(message);
        errorLabel.getElement().getStyle().setColor("green");
    }

    protected void error(String message) {
        errorLabel.setText(message);
        errorLabel.getElement().getStyle().setColor("red");
    }

    private void debug(String message) {
        if (logger.isLoggable(Level.FINE)) {
            debugLabel.setText(message);
            debugLabel.getElement().getStyle().setColor("gray");
        }
    }

    public void enterSong() {
        Song newSong = checkSong();

        if (newSong != null) {
            newSong.setUser(GwtLocalStorage.instance().getUserName());
            logger.fine(newSong.toJson());
            fireSongSubmission(newSong);
        }
    }

    private void setKey(Key key) {
        this.key = key;

        titleChordButtons();

        String keyValue = key.name();
        NodeList<OptionElement> list = keySelection.getOptions();
        for (int i = 0; i < list.getLength(); i++) {
            OptionElement oe = list.getItem(i);
            oe.setSelected(oe.getValue().equals(keyValue));
        }
    }

    private void enterChord(ClickEvent event) {
        String s = event.getRelativeElement().getInnerText();
        if (s.length() > 0)
            enterChord(s);
        measureFocus();
    }

    private void enterChord(String name) {
        processChordEntry(name);
    }

    private void addRecentMeasureNode(MeasureNode measureNode) {
        if (measureNode == null)
            return;

        switch (measureNode.getMeasureNodeType()) {
            case comment:
            case decoration:
                break;
            case measure:
                addRecentMeasure((Measure) measureNode);
                break;
            case repeat:
            case phrase:
                for (Measure measure : ((Phrase) measureNode).getMeasures())
                    addRecentMeasure(measure);
                break;
            case section:
                for (Phrase phrase : ((ChordSection) measureNode).getPhrases())
                    for (Measure measure : phrase.getMeasures())
                        addRecentMeasure(measure);
                break;
        }
    }

    private void addRecentMeasure(Measure measure) {
        if (measure == null || recentMeasures.contains(measure))
            return; //  leave well enough alone

        switch (measure.getMeasureNodeType()) {
            case decoration:
            case comment:
                return;
        }

        //recentScaleChords.remove(scaleChord);
        final Button recents[] = {
                recent0,
                recent1,
                recent2,
                recent3,
                recent4,
                recent5
        };

        final int recentScaleChordsMaxSize = recents.length;
        recentMeasures.add(0, measure);
        while (recentMeasures.size() > recentScaleChordsMaxSize)
            recentMeasures.remove(recentMeasures.size() - 1);

        int i = 0;
        for (Measure m : recentMeasures) {
            Button recent = recents[i++];
            recent.setHTML(m.toString());
            recent.setVisible(true);
        }
    }

    private class MeasureCountItem implements Comparable<MeasureCountItem> {
        MeasureCountItem(Measure measure, int count) {
            this.measure = measure;
            this.count = count;
        }

        public Measure getScaleChord() {
            return measure;
        }

        public int getCount() {
            return count;
        }

        @Override
        public int compareTo(MeasureCountItem o) {
            if (count != o.count)
                return count < o.count ? 1 : -1;   //  note reverse order
            return measure.compareTo(o.measure);
        }

        private final Measure measure;
        private final int count;
    }

    private void findMostCommonScaleChords() {
        final Button commons[] = {
                common0,
                common1,
                common2,
                common3,
                common4,
                common5
        };

        HashMap<Measure, Integer> measureMap = new HashMap<>();
        {
            Measure measure;
            for (int momentNumber = 0; momentNumber < song.getSongMomentsSize(); momentNumber++) {
                SongMoment songMoment = song.getSongMoment(momentNumber);
                if (songMoment == null) break;
                MeasureNode measureNode = song.findMeasureNode(songMoment.getChordSectionLocation());
                if (measureNode == null)
                    continue;
                switch (measureNode.getMeasureNodeType()) {
                    case measure:
                        measure = (Measure) measureNode;
                        logger.finer(measure.toMarkup());
                        measureMap.put(measure, (measureMap.containsKey(measure) ? measureMap.get(measure).intValue() : 0) + 1);
                        break;
                }
            }
        }

        TreeSet<MeasureCountItem> MeasureCountItems = new TreeSet<>();
        for (Measure measure : measureMap.keySet()) {
            MeasureCountItems.add(new MeasureCountItem(measure, measureMap.get(measure)));
        }
        int i = 0;
        for (MeasureCountItem item : MeasureCountItems) {
            if (i >= commons.length)
                break;
            Button common = commons[i++];
            common.setHTML(item.getScaleChord().toString());
            common.setVisible(true);
        }
        while (i < commons.length) {
            //  hide
            Button common = commons[i++];
            common.setVisible(false);
        }
    }

    private void fireSongSubmission(Song song) {
        fireEvent(new SongSubmissionEvent(song));
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public HandlerRegistration SongUpdateEventHandler(SongUpdateEventHandler handler) {
        return handlerManager.addHandler(SongUpdateEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration SongSubmissionEventHandler(SongSubmissionEventHandler handler) {
        return handlerManager.addHandler(SongSubmissionEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration SongRemoveEventHandler(SongRemoveEventHandler handler) {
        return handlerManager.addHandler(SongRemoveEvent.TYPE, handler);
    }

    private void titleChordSelections() {

        ScaleNote scaleNote = ScaleNote.valueOf(scaleNoteSelection.getValue());

        for (ChordDescriptor cd : chordDescriptorMap.keySet()) {
            Button b = chordDescriptorMap.get(cd);
            ScaleChord sc = new ScaleChord(scaleNote, cd);
            b.setText(sc.toString());
        }

        {    // other chords
            scaleNoteOtherSelection.clear();
            OptionElement optionElement = document.createOptionElement();
            optionElement.setLabel("Other chords");
            optionElement.setValue("");
            scaleNoteOtherSelection.add(optionElement, null);
            for (ChordDescriptor cd : ChordDescriptor.getOtherChordDescriptorsOrdered()) {
                optionElement = document.createOptionElement();
                ScaleChord sc = new ScaleChord(scaleNote, cd);
                optionElement.setLabel(sc.toString());
                optionElement.setValue(sc.toString());
                scaleNoteOtherSelection.add(optionElement, null);
            }
        }
    }

    private void titleChordButtons() {
        ScaleChord keyScaleChord = key.getMajorDiatonicByDegree(1 - 1);
        chordsI.setHTML(keyScaleChord.toString());
        ScaleChord iv = key.getMajorDiatonicByDegree(4 - 1);
        chordsIV.setHTML(iv.toString());
        chordsIV7.setHTML(new ScaleChord(iv.getScaleNote(), ChordDescriptor.dominant7).toString());
        ScaleChord v7 = key.getMajorDiatonicByDegree(5 - 1);
        chordsV7.setHTML(v7.toString());
        ScaleChord v = new ScaleChord(v7.getScaleNote(), ChordDescriptor.major);
        chordsV.setHTML(v.toString());

        chordsii.setHTML(key.getMajorDiatonicByDegree(2 - 1).toString());
        chordsiii.setHTML(key.getMajorDiatonicByDegree(3 - 1).toString());
        chordsvi.setHTML(key.getMajorDiatonicByDegree(6 - 1).toString());
        chordsvii.setHTML(key.getMajorDiatonicByDegree(7 - 1).toString());

        //  list all the scale notes with the key scale notes first
        scaleNoteSelection.clear();     //  fixme: document garbage collection?
        ArrayList<ScaleNote> scaleNotes = new ArrayList<>();
        for (int i = 0; i < MusicConstant.notesPerScale; i++)
            scaleNotes.add(key.getMajorScaleByNote(i));
        for (int i = 0; i < MusicConstant.halfStepsPerOctave; i++) {
            ScaleNote scaleNote = key.getScaleNoteByHalfStep(i);
            if (!scaleNotes.contains(scaleNote))
                scaleNotes.add(scaleNote);
        }
        for (ScaleNote scaleNote : scaleNotes) {
            OptionElement optionElement = document.createOptionElement();
            String s = scaleNote.toString();
            optionElement.setLabel(s
                    + (s.length() < 2 ? "   " : " ")
                    + ChordComponent.getByHalfStep(scaleNote.getHalfStep() - key.getHalfStep()).getShortName());
            optionElement.setValue(scaleNote.name());
            scaleNoteSelection.add(optionElement, null);
        }
        titleChordSelections();
    }

    @Override
    public void measureFocus() {
        logger.fine("measureFocus()");
        Scheduler.get().scheduleFinally(() -> measureEntry.setFocus(true));
    }

    @Override
    public void setActive(boolean isActive) {
        this.isActive = isActive;
        if (isActive) {
            setSong(song);
            measureFocus();
        }
    }

    private boolean areHintsHidden = true;
    private ArrayList<Measure> recentMeasures = new ArrayList<>();
    private HashMap<ChordDescriptor, Button> chordDescriptorMap = new HashMap<>();
    private UndoStack<Song> undoStack = new UndoStack<>(50);
    private Key key = Key.getDefault();
    private Song song;
    private GridCoordinate lastGridCoordinate = new GridCoordinate(0, 0);
    private boolean isActive = false;
    private static final int fontsize = 32;
    private final HandlerManager handlerManager;
    private static final Document document = Document.get();
    private static final String nbsp = "&nbsp;";
    private final EventBus eventBus;    //  is actually used

    private static final Logger logger = Logger.getLogger(SongEditView.class.getName());
//    static {
//        logger.setLevel(Level.FINE);
//    }
}
