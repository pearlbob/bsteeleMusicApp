package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.client.songs.Key;
import com.bsteele.bsteeleMusicApp.client.songs.MusicConstant;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
class CommonPlayViewImpl
        extends ViewImpl
{
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
                    SongUpdate update = new SongUpdate();
                    update.setSong(song.copySong());
                    update.setCurrentBeatsPerMinute(currentBpm);
                    update.setCurrentKey(currentKey);
                    songPlayMaster.playSongUpdate(update);
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

    protected void setCurrentBpm(String bpm) {
        setCurrentBpm(Integer.parseInt(bpm));
    }

    protected void setCurrentBpm(int bpm) {
        if (currentBpm != bpm) {
            currentBpm = bpm;
            issueSongUpdate();
        }
    }

    private void issueSongUpdate() {
        songUpdate.setCurrentBeatsPerMinute(currentBpm);
        songUpdate.setCurrentKey(currentKey);
        songUpdate.setSong(song);
        songPlayMaster.issueSongUpdate(songUpdate);
    }

    protected void autoScroll(ScrollPanel scrollPanel, HTMLPanel htmlPanel) {
        switch (songUpdate.getState()) {
            case playing:
                if (song.getTotalBeats() == 0 || htmlPanel.getOffsetHeight() == 0)
                    break;

                //  auto scroll
                int max = scrollPanel.getMaximumVerticalScrollPosition();
                int h = scrollPanel.getElement().getScrollHeight() - max;

                scrollPosition += (max + 1.25 * h) / (60.0 * song.getTotalBeats() * 60 / songUpdate.getCurrentBeatsPerMinute());
                scrollPosition = Math.min(max + h / 2, scrollPosition);
                //GWT.log("scroll: " + Double.toString(scrollPosition) + "  m: " + Integer.toString(max) + "  h: " + Integer.toString(h));
                int position = (int) Math.rint(scrollPosition);
                position = Math.max(0, Math.min(position - h / 2, max));
                int currentPosition = scrollPanel.getVerticalScrollPosition();
                if (Math.abs(currentPosition - position) > 8) {
                    scrollPosition = currentPosition;    // let the human override the scroll
                    lastScrollPosition = (int) Math.rint(scrollPosition);
                } else if (position != lastScrollPosition && scrollDelay > 1) {
                    lastScrollPosition = position;
                    scrollPanel.setVerticalScrollPosition(position);
                    scrollDelay = 0;
                    // GWT.log("bass scroll: " + Double.toString(scrollPosition) + "  " + Integer.toString(max));
                }
                scrollDelay++;
                break;
        }
    }

    protected void resetScroll(ScrollPanel scrollPanel) {
        scrollPosition = 0;
        scrollPanel.setVerticalScrollPosition(0);
        scrollDelay = 0;
    }

    protected Element lastChordElement;
    protected Element lastLyricsElement;
    protected Song song;

    protected static Key currentKey;
    protected int halfStepOffset = 0;
    private static int currentBpm;
    protected SongUpdate songUpdate = new SongUpdate();
    protected SongUpdate.State lastState = SongUpdate.State.idle;
    protected final EventBus eventBus;
    protected final SongPlayMaster songPlayMaster;

    private int lastScrollPosition = 0;
    private double scrollPosition = 0;
    private int scrollDelay = 0;
}
