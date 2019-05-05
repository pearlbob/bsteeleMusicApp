package com.bsteele.bsteeleMusicApp.client.application;

public class AppOptions {

    //make the constructor private so that this class cannot be instantiated externally
    private AppOptions(){}

    //Get the only object available
    public static AppOptions getInstance(){
        return instance;
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

    private static AppOptions instance = new AppOptions();

    private boolean dashAllMeasureRepetitions = true;
    private boolean debug=false;
}
