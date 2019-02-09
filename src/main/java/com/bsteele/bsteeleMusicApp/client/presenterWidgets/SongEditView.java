/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.DomInputEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.DomInputEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongRemoveEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongRemoveEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.ChordComponent;
import com.bsteele.bsteeleMusicApp.client.songs.ChordDescriptor;
import com.bsteele.bsteeleMusicApp.client.songs.ChordSection;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.LyricSection;
import com.bsteele.bsteeleMusicApp.client.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.client.songs.ScaleChord;
import com.bsteele.bsteeleMusicApp.client.songs.ScaleNote;
import com.bsteele.bsteeleMusicApp.client.songs.Section;
import com.bsteele.bsteeleMusicApp.client.songs.SectionVersion;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

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
        HasHandlers {

    @UiField
    ButtonElement songEnter;

    @UiField
    ButtonElement songEntryClear;

    @UiField
    ButtonElement songEntryRemove;

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
    TextArea chordsTextEntry;

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

    interface Binder extends UiBinder<Widget, SongEditView> {
    }

    @Inject
    SongEditView(Binder binder) {
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
                checkSong();
            }
        });


        chordsTextEntry.setFocus(true);
        chordsTextEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });

        lyricsTextEntry.addKeyUpHandler((KeyUpHandler) -> {
            checkSong();
        });

        sectionI.addClickHandler((ClickEvent event) -> {
            chordsTextAdd("\n" + Section.intro.getAbbreviation() + ":");
            checkSong();
        });
        sectionV.addClickHandler((ClickEvent event) -> {
            chordsTextAdd("\n" + Section.verse.getAbbreviation() + ":");
            checkSong();
        });
        sectionPC.addClickHandler((ClickEvent event) -> {
            chordsTextAdd("\n" + Section.preChorus.getAbbreviation() + ":");
            checkSong();
        });
        sectionC.addClickHandler((ClickEvent event) -> {
            chordsTextAdd("\n" + Section.chorus.getAbbreviation() + ":");
            checkSong();
        });
        sectionBr.addClickHandler((ClickEvent event) -> {
            chordsTextAdd("\n" + Section.bridge.getAbbreviation() + ":");
            checkSong();
        });

        sectionO.addClickHandler((ClickEvent event) -> {
            chordsTextAdd("\n" + Section.outro.getAbbreviation() + ":");
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
                        chordsTextAdd("\n" + sectionOtherSelection.getValue() + ":");
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
            checkSong();
        });
        chordsIV.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        chordsIV7.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        chordsV.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        chordsV7.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        chordsii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        chordsiii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        chordsvi.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        chordsvii.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });

        recent0.setVisible(false);   //  hide them all initially
        recent0.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        recent1.setVisible(false);   //  hide them all initially
        recent1.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        recent2.setVisible(false);   //  hide them all initially
        recent2.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        recent3.setVisible(false);   //  hide them all initially
        recent3.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        recent4.setVisible(false);   //  hide them all initially
        recent4.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        recent5.setVisible(false);   //  hide them all initially
        recent5.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        common0.setVisible(false);   //  hide them all initially
        common0.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        common1.setVisible(false);   //  hide them all initially
        common1.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        common2.setVisible(false);   //  hide them all initially
        common2.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        common3.setVisible(false);   //  hide them all initially
        common3.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        common4.setVisible(false);   //  hide them all initially
        common4.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
        });
        common5.setVisible(false);   //  hide them all initially
        common5.addClickHandler((ClickEvent event) -> {
            enterChord(event);
            checkSong();
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
                clearSong();
            }
        });

        Event.sinkEvents(songEntryRemove, Event.ONCLICK);
        Event.setEventListener(songEntryRemove, (Event event) -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                if (song != null) {
                    fireEvent(new SongRemoveEvent(song));
                    clearSong();
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

    }


    private final void clearSong() {
        titleEntry.setText("");
        artistEntry.setText("");
        copyrightEntry.setText("");
        bpmEntry.setText("106");
        timeSignatureEntry.setValue("4/4");
        chordsTextEntry.setValue("");
        lyricsTextEntry.setValue("");
        checkSong();
    }

    @Override
    public void setSongEdit(Song song) {
        if (song == null)
            return;

        this.song = song;

        titleEntry.setText(song.getTitle());
        artistEntry.setText(song.getArtist());
        copyrightEntry.setText(song.getCopyright());
        setKey(song.getKey());
        bpmEntry.setText(Integer.toString(song.getDefaultBpm()));
        timeSignatureEntry.setValue(song.getBeatsPerBar() + "/" + song.getUnitsPerMeasure());
        chordsTextEntry.setValue(song.getChordsAsString());
        chordsTextEntry.setCursorPos(0);
        lyricsTextEntry.setValue(song.getLyricsAsString());
        findMostCommonScaleChords();

        checkSong();
    }

    public Song checkSong() {
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
                    chordsTextEntry.getValue(), lyricsTextEntry.getValue());
        } catch (ParseException pe) {
            errorLabel.setText(pe.getMessage());
            songEnter.setDisabled(true);
            return null;
        }

        errorLabel.setText("");
        songEnter.setDisabled(false);

        return newSong;
    }

    public void enterSong() {
        Song newSong = checkSong();
        if (newSong != null)
            fireSongSubmission(newSong);
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
    }

    private void enterChord(String name) {
        ScaleChord scaleChord = ScaleChord.parse(name);
        if (scaleChord == null)
            return; //  fixme: shouldn't happen
        addRecentScaleChord(scaleChord);
        chordsTextAdd(scaleChord.toString());
        findMostCommonScaleChords();
        checkSong();
    }

    private void addRecentScaleChord(ScaleChord scaleChord) {
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

    private class CommonScaleChordItem implements Comparable<CommonScaleChordItem> {
        CommonScaleChordItem(ScaleChord scaleChord, int count) {
            this.scaleChord = scaleChord;
            this.count = count;
        }

        public ScaleChord getScaleChord() {
            return scaleChord;
        }

        public int getCount() {
            return count;
        }

        @Override
        public int compareTo(CommonScaleChordItem o) {
            if (count != o.count)
                return count < o.count ? 1 : -1;   //  note reverse order
            return scaleChord.compareTo(o.scaleChord);
        }

        private final ScaleChord scaleChord;
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

        HashMap<ScaleChord, Integer> scaleChordMap = ScaleChord.findScaleChordsUsed(chordsTextEntry.getValue());

        TreeSet<CommonScaleChordItem> commonScaleChordItems = new TreeSet<>();
        for (ScaleChord key : scaleChordMap.keySet()) {
            commonScaleChordItems.add(new CommonScaleChordItem(key, scaleChordMap.get(key)));
        }
        int i = 0;
        for (CommonScaleChordItem item : commonScaleChordItems) {
            if (i >= commons.length)
                break;
            Button common = commons[i++];
            common.setHTML(item.getScaleChord().toString());
            common.setVisible(true);
            //GWT.log((i - 1) + ": " + item.getCount() + " " + item.getScaleChord());
        }
        while (i < commons.length) {
            //GWT.log(i + ": hide");
            Button common = commons[i++];
            common.setVisible(false);
        }
    }

    private void chordsTextAdd(String addition) {
        if (addition == null || addition.length() == 0)
            return;
        addition += " ";

        int position = chordsTextEntry.getCursorPos();
        String text = chordsTextEntry.getValue();
        text = text.substring(0, position) + addition + text.substring(position);
        chordsTextEntry.setText(text);
        chordsTextEntry.setCursorPos(position + addition.length());
        chordsTextEntry.setFocus(true);
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


    private void guessTheKey() {
        HashMap<ScaleChord, Integer> scaleChordMap = ScaleChord.findScaleChordsUsed(chordsTextEntry.getValue());
        TreeSet<ScaleChord> treeSet = new TreeSet<>();
        treeSet.addAll(scaleChordMap.keySet());
        setKey(Key.guessKey(treeSet));
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

    private ArrayList<ScaleChord> recentScaleChords = new ArrayList<>();
    private ArrayList<ScaleChord> commonScaleChords = new ArrayList<>();
    HashMap<ChordDescriptor, Button> chordDescriptorMap = new HashMap<>();
    private Key key = Key.getDefault();
    private Song song;
    private static final int minBpm = 50;
    private static final int maxBpm = 400;
    private final HandlerManager handlerManager;
    private static final Document document = Document.get();
}
