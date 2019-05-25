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

    public void initialize();

    public void onMessage(double systemT, String data);

    public void stopSong();

    public void playSongUpdate(SongUpdate songUpdate);

    public void issueSongUpdate(SongUpdate songUpdate);

    public void play( Song song );

    public void continueSong();

    public void playSlideSongToMomentNumber( int momentNumber );

    public void setBSteeleMusicIO(BSteeleMusicIO bSteeleMusicIO);
}
