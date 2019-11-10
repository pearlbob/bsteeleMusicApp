/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.application.BSteeleMusicIO;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;

/**
 * @author bob
 */
public interface SongPlayMaster {

    void initialize();

    void onMessage(double systemT, String data);

    void stopSong();

    void playSongUpdate(SongUpdate songUpdate);

    void issueSongUpdate(SongUpdate songUpdate);

    void play(Song song);

    void continueSong();

    void playSongOffsetRowNumber(int offset);

    void playSongSetRowNumber(int row);

    Integer jumpSectionToFirstSongMomentInSection(int momentNumber);

    int getMomentNumber();

    Integer getSkipToNumber();

    void setBSteeleMusicIO(BSteeleMusicIO bSteeleMusicIO);
}
