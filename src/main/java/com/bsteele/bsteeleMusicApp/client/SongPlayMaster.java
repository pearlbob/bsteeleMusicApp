/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.application.BSteeleMusicIO;
import com.bsteele.bsteeleMusicApp.client.songs.Song;

/**
 * @author bob
 */
public interface SongPlayMaster {

    public void onMessage(double systemT, String data);

    public void stopSong();

    public void play();

    public void playSong(Song song);

    public void continueSong();

    public void setSelection(int first, int last);

    public void setBSteeleMusicIO(BSteeleMusicIO bSteeleMusicIO);
}
