package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.shared.songs.Key;
import com.bsteele.bsteeleMusicApp.shared.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.shared.util.Util;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
class CommonPlayViewImpl
        extends ViewImpl {
    protected CommonPlayViewImpl(
            @Nonnull final EventBus eventBus,
            @Nonnull final SongPlayMaster songPlayMaster) {
        this.eventBus = eventBus;
        this.songPlayMaster = songPlayMaster;
    }

    protected void playStop() {
        if (song != null) {
            switch (songUpdate.getState()) {
                case playing:
                    songPlayMaster.stopSong();
                    break;
                case idle:
                    if (songPlayMaster.isLeader()) {
                        SongUpdate update = new SongUpdate();
                        update.setSong(song.copySong());
                        update.setCurrentBeatsPerMinute(currentBpm);
                        update.setCurrentKey(currentKey);
                        songPlayMaster.playSongUpdate(update);
                    }
                    break;
            }
        }
    }

    protected void setCurrentKey(Key key) {
        if (currentKey == null || !currentKey.equals(key)) {
            currentKey = key;
            issueSongUpdate();
        }
    }

    protected void stepCurrentKey(int halfStep) {
        halfStep = Util.mod(halfStep, MusicConstant.halfStepsPerOctave);
        if (halfStep == 0)
            return;

        Key key = songUpdate.getCurrentKey();
        if (key == null) {
            if (songUpdate == null || songUpdate.getSong() == null)
                return;
            key = songUpdate.getSong().getKey();
        }
        if (key == null)
            return;

        setCurrentKey(Key.getKeyByHalfStep(key.getHalfStep() + halfStep));
    }


    protected void stepCurrentBpm(int step) {
        if (step == 0)
            return;
        int bpm = getCurrentBpm() + step;
        setCurrentBpm(bpm < MusicConstant.minBpm
                ? MusicConstant.minBpm
                : (bpm > MusicConstant.maxBpm ? MusicConstant.maxBpm : bpm));
        issueSongUpdate();
    }

    protected void setCurrentBpm(String bpm) throws NumberFormatException {
        if (bpm == null)
            throw new NumberFormatException("BPM count missing");
        setCurrentBpm(Integer.parseInt(bpm.trim()));
        issueSongUpdate();
    }

    protected void setCurrentBpm(int bpm) {
        currentBpm = bpm;
    }

    protected int getCurrentBpm() {
        return currentBpm;
    }

    private void issueSongUpdate() {
        SongUpdate newSongUpdate = SongUpdate.createSongUpdate(song);
        newSongUpdate.setCurrentBeatsPerMinute(currentBpm);
        newSongUpdate.setCurrentKey(currentKey);
        songPlayMaster.issueSongUpdate(newSongUpdate);
    }


    protected void resetScroll(ScrollPanel scrollPanel) {
        scrollPosition = 0;
        scrollPanel.setVerticalScrollPosition(0);
        scrollDelay = 0;
    }

    /**
     * set the anchors for title and artist
     *
     * @param title  the child viewImpl's title anchor
     * @param artist the child viewImpl's artist anchor
     */
    protected void setAnchors(Anchor title, Anchor artist) {
        title.setText(song.getTitle()
                + (song.getFileVersionNumber() > 0
                ? " (" + Integer.toString(song.getFileVersionNumber()) + ")"
                : ""));
        title.setHref(anchorUrlStart + URL.encode(song.getTitle() + " " + song.getArtist()));
        artist.setText(song.getArtist());
        artist.setHref(anchorUrlStart + URL.encode(song.getArtist()));
    }

    protected Element lastChordElement;
    protected Element lastLyricsElement;
    protected Song song;

    protected Key currentKey;
    protected Key lastKey;
    protected int halfStepOffset = 0;

    protected SongUpdate songUpdate = new SongUpdate();
    protected SongUpdate.State lastState = SongUpdate.State.none;
    protected final EventBus eventBus;
    protected final SongPlayMaster songPlayMaster;
    protected static final String anchorUrlStart = "https://www.youtube.com/results?search_query=";

    protected double smoothedBPM = (MusicConstant.minBpm + MusicConstant.maxBpm) / 2;
    private static int currentBpm = (MusicConstant.minBpm + MusicConstant.maxBpm) / 2;
    protected final double smoothedBPMPass = 0.15;
    protected int tapCount = 0;
    protected double lastTap = 0;

    private int lastScrollPosition = 0;
    private double scrollPosition = 0;
    private int scrollDelay = 0;

    private static Logger logger = Logger.getLogger(CommonPlayViewImpl.class.getName());
}
