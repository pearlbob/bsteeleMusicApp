/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.NextSongEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.ChordComponent;
import com.bsteele.bsteeleMusicApp.client.songs.ChordDescriptor;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.Measure;
import com.bsteele.bsteeleMusicApp.client.songs.MeasureNode;
import com.bsteele.bsteeleMusicApp.client.songs.MeasureSequenceItem;
import com.bsteele.bsteeleMusicApp.client.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.client.songs.ScaleChord;
import com.bsteele.bsteeleMusicApp.client.songs.ScaleNote;
import com.bsteele.bsteeleMusicApp.client.songs.Section;
import com.bsteele.bsteeleMusicApp.client.songs.SectionVersion;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongMoment;
import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ClickEvent;
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

/**
 * @author bob
 */
public class SongEditView
        extends ViewImpl
        implements SongEditPresenterWidget.MyView,
        HasHandlers
{

    @UiField
    ButtonElement songEnter;

    @UiField
    ButtonElement songEntryClear;

    @UiField
    Button nextSongButton;
    @UiField
    Button prevSongButton;

    @UiField
    Label errorLabel;

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
    //    @UiField
//    Button sectionA;
//    @UiField
//    Button sectionB;
//    @UiField
//    Button sectionCo;
//    @UiField
//    Button sectionT;
    @UiField
    SelectElement sectionOtherSelection;


    @UiField
    TextBox measureEntry;

    @UiField
    RadioButton editInsert;

    @UiField
    RadioButton editReplace;

    @UiField
    RadioButton editAppend;

    @UiField
    TextArea chordsTextEntry;
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
    Button major;
    @UiField
    Button minor;
    @UiField
    Button dominant7;

    @UiField
    Button chordsUndo;
    @UiField
    Button chordsRedo;

    @UiField
    TextArea lyricsTextEntry;

    interface Binder extends UiBinder<Widget, SongEditView>
    {
    }

    @Inject
    SongEditView(@Nonnull final EventBus eventBus, Binder binder)
    {
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
                setKey(Key.valueOf(keySelection.getValue()));
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
            GWT.log("measure change: \"" + event.getValue() + "\"");
            processMeasureEntry();
        });
        measureEntry.addKeyUpHandler((KeyUpEvent event) -> {
            GWT.log("measure KeyUp: \"" + measureEntry.getValue() + "\"");
            String entry = measureEntry.getValue();
            if (entry.isEmpty())
                return;

            char lastChar = entry.charAt(entry.length() - 1);
            switch (lastChar) {
                case ' ':
                case '\n':
                case '\r':
                    processMeasureEntry();
                    break;
                default:
                    preProcessMeasureEntry();
                    break;
            }

        });
        editAppend.setValue(true);
        editInsert.addClickHandler((ClickEvent e) -> {
            selectChordsCell(lastChordSelection);
        });
        editReplace.addClickHandler((ClickEvent e) -> {
            selectChordsCell(lastChordSelection);
        });
        editAppend.addClickHandler((ClickEvent e) -> {
            selectChordsCell(lastChordSelection);
        });

        lyricsTextEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });

        sectionI.addClickHandler((ClickEvent event) -> {
            processEntry(Section.intro.getAbbreviation() + ":");
            checkSong();
        });
        sectionV.addClickHandler((ClickEvent event) -> {
            processEntry(Section.verse.getAbbreviation() + ":");
            checkSong();
        });
        sectionPC.addClickHandler((ClickEvent event) -> {
            processEntry(Section.preChorus.getAbbreviation() + ":");
            checkSong();
        });
        sectionC.addClickHandler((ClickEvent event) -> {
            processEntry(Section.chorus.getAbbreviation() + ":");
            checkSong();
        });
        sectionBr.addClickHandler((ClickEvent event) -> {
            processEntry(Section.bridge.getAbbreviation() + ":");
            checkSong();
        });

        sectionO.addClickHandler((ClickEvent event) -> {
            processEntry(Section.outro.getAbbreviation() + ":");
            checkSong();
        });
//        sectionA.addClickHandler((ClickEvent event) -> {
//            chordsTextAdd("\n" + Section.a.getAbbreviation() + ":");
        //       checkSong();
//        });
//        sectionB.addClickHandler((ClickEvent event) -> {
//            chordsTextAdd("\n" + Section.b.getAbbreviation() + ":");
        //       checkSong();
//        });
//        sectionCo.addClickHandler((ClickEvent event) -> {
//            chordsTextAdd("\n" + Section.coda.getAbbreviation() + ":");
//        checkSong();
//        });
//        sectionT.addClickHandler((ClickEvent event) -> {
//            chordsTextAdd("\n" + Section.tag.getAbbreviation() + ":");
        //  checkSong();
//        });
        {
            // other sections
            Event.sinkEvents(sectionOtherSelection, Event.ONCHANGE);
            Event.setEventListener(sectionOtherSelection, (Event event) -> {
                if (Event.ONCHANGE == event.getTypeInt()) {
                    if (sectionOtherSelection.getSelectedIndex() > 0)
                        processEntry(sectionOtherSelection.getValue() + ":");
                    sectionOtherSelection.setSelectedIndex(0);    //  hack: clear the selection to get a hit on change
                    checkSong();
                }
            });

            sectionOtherSelection.clear();
            OptionElement optionElement = document.createOptionElement();
            optionElement.setLabel("Other section");
            optionElement.setValue("");
            sectionOtherSelection.add(optionElement, null);

            Section otherSections[] = new Section[]{Section.a, Section.b, Section.coda, Section.tag};
            for (Section section : otherSections) {
                optionElement = document.createOptionElement();
                optionElement.setLabel(section.toString());
                optionElement.setValue(section.getAbbreviation());
                sectionOtherSelection.add(optionElement, null);
            }
        }


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

        chordsUndo.setEnabled(false);
        chordsUndo.addClickHandler((ClickEvent event) -> {
            checkSong();
        });

        chordsRedo.setEnabled(false);
        chordsRedo.addClickHandler((ClickEvent event) -> {
            checkSong();
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
                guessTheKey();
                checkSong();
            }
        });

        Event.sinkEvents(songEntryClear, Event.ONCLICK);
        Event.setEventListener(songEntryClear, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                titleEntry.setText("");
                artistEntry.setText("");
                copyrightEntry.setText("");
                bpmEntry.setText("106");
                timeSignatureEntry.setValue("4/4");
                chordsTextEntry.setValue("");
                chordsFlexTable.removeAllRows();
                editAppend.setValue(true);
                lyricsTextEntry.setValue("");
                checkSong();
            }
        });

        prevSongButton.addClickHandler((ClickEvent event) -> {
            eventBus.fireEvent(new NextSongEvent(false));
        });
        nextSongButton.addClickHandler((ClickEvent event) -> {
            eventBus.fireEvent(new NextSongEvent());
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

    }

    private void preProcessMeasureEntry()
    {
        String entry = measureEntry.getValue();
        if (entry.isEmpty())
            return;

        //  speed entry enhancement: first chord char is always upper case
        if (entry.length() >= 1) {
            char c = entry.charAt(0);
            if (c >= 'a' && c <= 'g') {
                entry = Util.firstToUpper(entry);
                measureEntry.setValue(entry);
            }
        }
    }

    private void processMeasureEntry()
    {
        String entry = measureEntry.getValue();
        if (entry.isEmpty())
            return;

        preProcessMeasureEntry();
        if (processEntry(entry))
            measureEntry.setValue("");
    }

    private boolean processEntry(String entry)
    {
        SectionVersion sectionVersion = Section.parse(entry);
        if (sectionVersion != null) {
            GWT.log("new SectionVersion: \"" + sectionVersion.toString() + "\"");
            entry = entry.substring(sectionVersion.getParseLength());
        }
        Measure measure = Measure.parse(entry, song.getBeatsPerBar());
        if (measure != null) {
            GWT.log("new measure: \"" + measure.toString() + "\"");
            // entry = entry.substring(measure.getParseLength());
        }

        if (sectionVersion != null) { //  fixme
        }

        if (measure != null && lastChordSelection != null) {
            MeasureSequenceItem.EditLocation editLocation = MeasureSequenceItem.EditLocation.append;
            if (editInsert.getValue()) {
                editLocation = MeasureSequenceItem.EditLocation.insert;
            } else if (editReplace.getValue()) {
                editLocation = MeasureSequenceItem.EditLocation.replace;
            }

            if (song.measureEdit(song.getStructuralMeasureNode(lastChordSelection.row, lastChordSelection.col),
                    editLocation, measure))
            {
                editAppend.setValue(true);      //  select append
                song.transpose(prefix, chordsFlexTable, 0, fontsize);
                return true;
            }
        }
        return false;
    }


    @Override
    public void setSongEdit(Song song)
    {
        if (song == null)
            return;

        this.song = song;

        titleEntry.setText(song.getTitle());
        artistEntry.setText(song.getArtist());
        copyrightEntry.setText(song.getCopyright());
        setKey(song.getKey());
        bpmEntry.setText(Integer.toString(song.getDefaultBpm()));
        timeSignatureEntry.setValue(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());
        chordsTextEntry.setValue(song.getStructuralGridAsText());
        chordsTextEntry.setCursorPos(0);
        lyricsTextEntry.setValue(song.getLyricsAsString());
        findMostCommonScaleChords();

        song.transpose(prefix, chordsFlexTable, 0, fontsize);
        selectLastChordsCell();

        lastChordSelection = null;
        chordsFlexTable.addClickHandler(clickEvent -> {
            HTMLTable.Cell cell = chordsFlexTable.getCellForEvent(clickEvent);
            selectChordsCell(new ChordSelection(cell));
            measureEntry.setFocus(true);
        });

        checkSong();
    }

    private void selectLastChordsCell()
    {
        int rows = chordsFlexTable.getRowCount();
        if (rows <= 0)
            return;
        int cols = chordsFlexTable.getCellCount(rows - 1);
        if (cols <= 0)
            return;
        FlexTable.FlexCellFormatter formatter = chordsFlexTable.getFlexCellFormatter();
        selectChordsCell(new ChordSelection(formatter.getElement(rows - 1, cols - 1),
                rows - 1, cols - 1));
    }

    private class ChordSelection
    {
        ChordSelection(Element e, int row, int col)
        {
            this.element = e;
            this.row = row;
            this.col = col;
        }

        ChordSelection(HTMLTable.Cell cell)
        {
            this.element = cell.getElement();
            this.row = cell.getRowIndex();
            this.col = cell.getCellIndex();
        }

        public Element getElement()
        {
            return element;
        }

        public int getRow()
        {
            return row;
        }

        public int getCol()
        {
            return col;
        }

        private Element element;
        private int row;
        private int col;

    }

    private void selectChordsCell(ChordSelection chordSelection)
    {
        if (chordSelection == null)
            return;

        String text = chordSelection.element.getInnerText();

        if (text != null && text.length() > 0) {

            //  clear the last selection
            if (lastChordSelection != null) {
                lastChordSelection.element.setAttribute("editSelect", "none");
                lastChordSelection.element.getStyle().setBackgroundColor("");
            }

            //  indicate the current selection
            MeasureSequenceItem.EditLocation editLocation = MeasureSequenceItem.EditLocation.append;
            if (editInsert.getValue()) {
                chordSelection.element.setAttribute("editSelect", "insert");
                editLocation = MeasureSequenceItem.EditLocation.insert;
            } else if (editAppend.getValue()) {
                chordSelection.element.setAttribute("editSelect", "append");
            } else {
                chordSelection.element.setAttribute("editSelect", "replace");
                chordSelection.element.getStyle().setBackgroundColor(selectedBorderColorValueString);
                editLocation = MeasureSequenceItem.EditLocation.replace;
            }

            if (song != null) {
                MeasureNode measureNode = song.getStructuralMeasureNode(chordSelection.row, chordSelection.col);
                GWT.log("chordSelection: (" + chordSelection.row + "," + chordSelection.col + ") "
                        + measureNode.toString()
                        + " " + editLocation.name());
            }

            lastChordSelection = chordSelection;
        }
    }

    private static final String selectedBorderColorValueString = "#f88";

    public Song checkSong()
    {
        Key key = Key.valueOf(keySelection.getValue());
        if (key == null)
            key = Key.C;  //  punt an error
        setKey(key);

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
        try {
            newSong = Song.checkSong(titleEntry.getText(), artistEntry.getText(),
                    copyrightEntry.getText(),
                    key, bpmEntry.getText(), beatsPerBar, unitsPerMeasure,
                    song.getStructuralGridAsText(), lyricsTextEntry.getValue());
        } catch (ParseException pe) {
            errorLabel.setText(pe.getMessage());
            songEnter.setDisabled(true);
            return null;
        }

        song = newSong;
        song.transpose(prefix, chordsFlexTable, 0, fontsize);
        selectLastChordsCell();      // default

        errorLabel.setText("");
        songEnter.setDisabled(false);

        return newSong;
    }

    public void enterSong()
    {
        Song newSong = checkSong();
        if (newSong != null)
            fireSongSubmission(newSong);
    }

    private void setKey(Key key)
    {
        this.key = key;

        titleChordButtons();

        String keyValue = key.name();
        NodeList<OptionElement> list = keySelection.getOptions();
        for (int i = 0; i < list.getLength(); i++) {
            OptionElement oe = list.getItem(i);
            oe.setSelected(oe.getValue().equals(keyValue));
        }
    }

    private void enterChord(ClickEvent event)
    {
        String s = event.getRelativeElement().getInnerText();
        if (s.length() > 0)
            enterChord(s);
    }

    private void enterChord(String name)
    {
        ScaleChord scaleChord = ScaleChord.parse(name);
        if (scaleChord == null)
            return; //  fixme: shouldn't happen

        if (processEntry(scaleChord.toString() + " ")) {
            addRecentScaleChord(scaleChord);
            findMostCommonScaleChords();
            checkSong();
        }
    }

    private void addRecentScaleChord(ScaleChord scaleChord)
    {
        if (recentScaleChords.contains(scaleChord))
            return; //  leave well enough alone

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
        recentScaleChords.add(0, scaleChord);
        while (recentScaleChords.size() > recentScaleChordsMaxSize)
            recentScaleChords.remove(recentScaleChords.size() - 1);

        int i = 0;
        for (ScaleChord recentScaleChord : recentScaleChords) {
            Button recent = recents[i++];
            recent.setHTML(recentScaleChord.toString());
            recent.setVisible(true);
        }
    }

    private class MeasureCountItem implements Comparable<MeasureCountItem>
    {
        MeasureCountItem(Measure measure, int count)
        {
            this.measure = measure;
            this.count = count;
        }

        public Measure getScaleChord()
        {
            return measure;
        }

        public int getCount()
        {
            return count;
        }

        @Override
        public int compareTo(MeasureCountItem o)
        {
            if (count != o.count)
                return count < o.count ? 1 : -1;   //  note reverse order
            return measure.compareTo(o.measure);
        }

        private final Measure measure;
        private final int count;
    }

    private void findMostCommonScaleChords()
    {
        final Button commons[] = {
                common0,
                common1,
                common2,
                common3,
                common4,
                common5
        };

        HashMap<Measure, Integer> measureMap = new HashMap<>();
        for (SongMoment songMoment : song.getSongMoments()) {
            Measure m = songMoment.getMeasure();
            measureMap.put(m, (measureMap.containsKey(m) ? measureMap.get(m).intValue() : 0) + 1);
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

    private void chordsAdd(ScaleChord scaleChord)
    {
        int row = 0;
        int col = 0;

        if (lastChordSelection == null)
            selectLastChordsCell();

        FlexTable.FlexCellFormatter formatter = chordsFlexTable.getFlexCellFormatter();
        if (lastChordSelection != null) {
            int rowCount = chordsFlexTable.getRowCount();
            search:
            for (row = 0; row < rowCount; row++) {
                int colCount = chordsFlexTable.getCellCount(row);
                for (col = 0; col < colCount; col++) {
                    if (lastChordSelection.element == formatter.getElement(row, col)) {
                        break search;
                    }
                }
            }
        }

        if (editInsert.getValue()) {
            GWT.log("insert: (" + row + ", " + col + "): " + scaleChord.toString());
        } else if (editAppend.getValue()) {
            GWT.log("append: (" + row + ", " + col + "): " + scaleChord.toString());
        } else {
            //   replace
            GWT.log("replace: (" + row + ", " + col + "): " + scaleChord.toString());
        }
    }

    private void fireSongSubmission(Song song)
    {
        fireEvent(new SongSubmissionEvent(song));
    }

    @Override
    public void fireEvent(GwtEvent<?> event)
    {
        handlerManager.fireEvent(event);
    }

    @Override
    public HandlerRegistration SongUpdateEventHandler(SongUpdateEventHandler handler)
    {
        return handlerManager.addHandler(SongUpdateEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration SongSubmissionEventHandler(SongSubmissionEventHandler handler)
    {
        return handlerManager.addHandler(SongSubmissionEvent.TYPE, handler);
    }


    private void guessTheKey()
    {
        HashMap<ScaleChord, Integer> scaleChordMap = ScaleChord.findScaleChordsUsed(chordsTextEntry.getValue());
        TreeSet<ScaleChord> treeSet = new TreeSet<>();
        treeSet.addAll(scaleChordMap.keySet());
        setKey(Key.guessKey(treeSet));
    }


    private void titleChordSelections()
    {

        ScaleNote scaleNote = ScaleNote.valueOf(scaleNoteSelection.getValue());

        for (ChordDescriptor cd : chordDescriptorMap.keySet()) {
            Button b = chordDescriptorMap.get(cd);
            ScaleChord sc = new ScaleChord(scaleNote, cd);
            b.setText(sc.toString());
        }

        {    // other chords
            scaleNoteOtherSelection.clear();
            OptionElement optionElement = document.createOptionElement();
            optionElement.setLabel("Other chord");
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

    private void titleChordButtons()
    {
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

    private ArrayList<ScaleChord> recentScaleChords = new ArrayList<>();
    //private ArrayList<ScaleChord> commonScaleChords = new ArrayList<>();
    HashMap<ChordDescriptor, Button> chordDescriptorMap = new HashMap<>();
    private Key key = Key.getDefault();
    private Song song;
    private ChordSelection lastChordSelection;
    private static final int minBpm = 50;
    private static final int maxBpm = 400;
    private static final int fontsize = 32;
    private final HandlerManager handlerManager;
    private static final Document document = Document.get();
    private final EventBus eventBus;
    private static String prefix = "songEdit";
}
