package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;

public class MetaDataTest extends GWTTestCase {

    @Test
    public void testMusicBrainz() {
        GWT.log(musicBrainzQuery("All Along the Watchtower", "Jimi Hendrix"));
    }

    public String musicBrainzQuery(String title, String artist) {
        return "https://musicbrainz.org/search?query=" + URL.encode(title)
                + "&type=recording" +
                "&limit=25";
    }

    @Override
    public String getModuleName() {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }
}
