/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;

public interface AppResources extends ClientBundle {

    AppResources INSTANCE = GWT.create(AppResources.class);

    interface Style extends CssResource {

        String sectionAClass();

        String sectionBClass();

        String sectionCClass();

        String lyricsCClass();

        String sectionVClass();

        String lyricsVClass();

        String sectionBrClass();

        String lyricsBrClass();

        String sectionPCClass();

        String lyricsPCClass();

        String sectionIClass();

        String lyricsIClass();

        String sectionOClass();

        String lyricsOClass();

        String sectionCoClass();

        String lyricsCoClass();

        String sectionTClass();

        String lyricsTClass();

        String tooltiptext();

        String dialogVPanel();

        String serverResponseLabelError();

        String tooltip();

        String audioBeatDisplayCanvasStyle();

        String sendButton();

        String songTitle();

        String songListItem();

        String songListItemData();

        String chordTable();

        String lyricsTable();

        String sectionLabel();

        String chords();

        String chordsTextEntry();

        String measureTextEntry();

        String lyricsTextEntry();

        String songList();

        String entryLine();

        String smallIntEntry();

        String inline();

        String longEntryLine();

        String lyricsAClass();

        String lyricsBClass();

        String rightJustified();

        String rightJustifiedClass();

        String label();

        String playMeasureLabel();

        String playNextMeasureLabel();

        String errorLabel();

        String textCenter();

        String sectionCommentClass();

        String playChordsBackground();

        String jumpBackground();

        String playChordsForeground();

        String playTopCover();

        String focusPanel();

        String verticalPanel();

        @ClassName("gwt-DialogBox")
        String gwtDialogBox();

        @ClassName("gwt-TabLayoutPanelTabs")
        String gwtTabLayoutPanelTabs();

        @ClassName("gwt-SplitLayoutPanel")
        String gwtSplitLayoutPanel();

        @ClassName("gwt-FileUpload")
        String gwtFileUpload();

        @ClassName("gwt-TabLayoutPanel")
        String gwtTabLayoutPanel();

        @ClassName("gwt-H2")
        String gwtH2();

        @ClassName("gwt-H1")
        String gwtH1();
    }

    @Source("css/bsteeleMusicApp.gss")
    Style style();

    @Source("allSongs.songlyrics")
    TextResource allSongsAsJsonString();

    @Source("buildId.txt")
    TextResource buildId();

    //  @Source("config.xml")
    //  public TextResource initialConfiguration();
    //
    //  @Source("manual.pdf")
    //  public DataResource ownersManual();
}
