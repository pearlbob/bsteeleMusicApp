package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob.
 */
public class DrumMeasure {


    public String getHighHat() {
        return highHat;
    }

    public void setHighHat(String highHat) {
        this.highHat = highHat;
    }

    public String getSnare() {
        return snare;
    }

    public void setSnare(String snare) {
        this.snare = snare;
    }

    public String getKick() {
        return kick;
    }

    public void setKick(String kick) {
        this.kick = kick;
    }

    private String highHat;
    private String snare;
    private String kick;
}
