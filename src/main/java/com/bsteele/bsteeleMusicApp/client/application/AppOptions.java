package com.bsteele.bsteeleMusicApp.client.application;

public class AppOptions {

    //make the constructor private so that this class cannot be instantiated externally
    private AppOptions() {
    }

    //Get the only object available
    public static AppOptions getInstance() {
        return instance;
    }


    public boolean isCountIn() {
        return countIn;
    }

    public void setCountIn(boolean countIn) {
        this.countIn = countIn;
    }

    public boolean isDashAllMeasureRepetitions() {
        return dashAllMeasureRepetitions;
    }

    public void setDashAllMeasureRepetitions(boolean dashAllMeasureRepetitions) {
        this.dashAllMeasureRepetitions = dashAllMeasureRepetitions;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    public boolean isPlayWithLineIndicator() {
        return playWithLineIndicator;
    }

    public void setPlayWithLineIndicator(boolean playWithLineIndicator) {
        this.playWithLineIndicator = playWithLineIndicator;
    }

    public boolean isPlayWithMeasureIndicator() {
        return playWithMeasureIndicator;
    }

    public void setPlayWithMeasureIndicator(boolean playWithMeasureIndicator) {
        this.playWithMeasureIndicator = playWithMeasureIndicator;
    }

    private static AppOptions instance = new AppOptions();

    private boolean countIn = true;
    private boolean dashAllMeasureRepetitions = true;
    private boolean debug = false;
    private boolean playWithLineIndicator = true;
    private boolean playWithMeasureIndicator = true;


}
