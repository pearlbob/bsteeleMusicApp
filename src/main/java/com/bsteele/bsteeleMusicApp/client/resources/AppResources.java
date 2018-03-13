/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;

public interface AppResources extends ClientBundle {

    public static final AppResources INSTANCE = GWT.create(AppResources.class);

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

        String sectionLabel();

        String chords();

        String songList();

        String entryLine();

        String smallIntEntry();

        String lyricsAClass();

        String lyricsBClass();

        String label();
    }

    @Source("css/bsteeleMusicApp.gss")
    Style style();

    @Source("allSongs.songlyrics")
    TextResource allSongsAsJsonString();

    @Source("legacySongs.songlyrics")
    TextResource legacySongsAsJsonString();

    @Source("buildId.txt")
    TextResource buildId();

    //  @Source("config.xml")
    //  public TextResource initialConfiguration();
    //
    //  @Source("manual.pdf")
    //  public DataResource ownersManual();
}
