package com.bsteele.bsteeleMusicApp.server;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MetaDataExloration {
    @Test
    public void testMusicBrainz() {
        System.out.println(musicBrainzQuery("All Along the Watchtower", "Jimi Hendrix"));
    }

    public String musicBrainzQuery(String title, String artist) {
        try {
            title = URLEncoder.encode(title, enc);
            artist = URLEncoder.encode(artist, enc);
        } catch (UnsupportedEncodingException uee) {
            return null;    //  won't happen
        }
        String jimiHendrixMbid = "06fb1c8b-566e-4cb2-985b-b467c90781d4";
/*
        https://musicbrainz.org/ws/2/artist/06fb1c8b-566e-4cb2-985b-b467c90781d4?inc=aliases

        989013d7-af14-4c78-925a-bd8679f31397

        Name:	All Along the Watchtower (alternate mix)
MBID:	147be6c5-735b-4ff2-a3f1-ca55544e3ee4

 search:   /<ENTITY>?query=<QUERY>&limit=<LIMIT>&offset=<OFFSET>
 area, artist, event, instrument, label, place, recording, release, release-group, series, work, url
         */

        return "http://musicbrainz.org/ws/2/recording?query="
                + "recording:" + title
                + "+AND+"
                + "artist:" + artist
                + "&limit=200";
    }

    private static final String enc = "UTF-8";
}
