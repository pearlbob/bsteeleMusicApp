package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.shared.songs.SongId;
import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class SongHashTest extends GWTTestCase {

    @Test
    public void testFromJson() {
        logger.fine("SongHashTest:");
        String allSongs = AppResources.INSTANCE.allSongsAsJsonString().getText();
        logger.fine("allSongs: "+allSongs.length());

        ArrayList<SongHash> songList = SongHash.fromJson(allSongs);
        int listSize = songList.size();
        logger.fine("songList: "+listSize);

        //  see that the hashes are unique
        {
            HashMap<String, SongHash> map = new HashMap<>();
            for ( SongHash songHash : songList){
                map.put(songHash.getHash(), songHash);
            }
            assertEquals(listSize, map.keySet().size());
        }

        //  see that the songid's are unique
        {
            HashMap<SongId, SongHash> map = new HashMap<>();
            for ( SongHash songHash : songList){
                map.put(songHash.getSongId(), songHash);
            }
            assertEquals(listSize, map.keySet().size());
        }

        assertEquals(963, songList.size());     //  fixme: bad style, list size will change!

        {
            SongHash songHash = songList.get(0);
            logger.info("songHash[0]: "+songHash.toString());
            logger.info("songHash[0].songId: "+songHash.getSongId());
        }
    }

    @Override
    public String getModuleName() {
        return "com.bsteele.bsteeleMusicApp.BSteeleMusicAppJUnit";
    }

    private static Logger logger = Logger.getLogger(SongHashTest.class.getName());
}