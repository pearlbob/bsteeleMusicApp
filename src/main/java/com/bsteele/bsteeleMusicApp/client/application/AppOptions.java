package com.bsteele.bsteeleMusicApp.client.application;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;

import java.util.logging.Logger;

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

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        sb.append("\"countIn\": \"" + countIn + "\", ");
        sb.append("\"dashAllMeasureRepetitions\": \"" + dashAllMeasureRepetitions + "\", ");
        sb.append("\"playWithLineIndicator\": \"" + playWithLineIndicator + "\", ");
        sb.append("\"playWithMeasureIndicator\": \"" + playWithMeasureIndicator + "\", ");
        sb.append("\"debug\": \"" + debug + "\"");      //  no comma at end
        sb.append(" ]");
        return sb.toString();
    }

    public void fromJson(String json) {
        final RegExp jsonArrayExp = RegExp.compile("^\\w*\\[(.*)\\]\\w*$");
        MatchResult mr = jsonArrayExp.exec(json);
        if (mr != null) {
            // parse
            String dataString = mr.getGroup(1);
            logger.fine(dataString);
            final RegExp commaExp = RegExp.compile(",");    //  fixme: will match commas in data!
            final RegExp jsonNameValueExp = RegExp.compile("\\s*\"(\\w+)\"\\:\\s*\"(\\w+)\"\\s*");
            SplitResult splitResult = commaExp.split(dataString, 10);       //  worry about the limit here
            for (int i = 0; i < splitResult.length(); i++) {
                logger.fine(splitResult.get(i));

                mr = jsonNameValueExp.exec(splitResult.get(i));
                if (mr != null) {
                    String name = mr.getGroup(1);
                    boolean b;
                    switch (name) {
                        case "countIn":
                            b = Boolean.parseBoolean(mr.getGroup(2));
                            logger.fine("countin: " + b);
                            setCountIn(b);
                            break;
                        case "dashAllMeasureRepetitions":
                            setDashAllMeasureRepetitions(Boolean.parseBoolean(mr.getGroup(2)));
                            break;
                        case "playWithLineIndicator":
                            setPlayWithLineIndicator(Boolean.parseBoolean(mr.getGroup(2)));
                            break;
                        case "playWithMeasureIndicator":
                            setPlayWithMeasureIndicator(Boolean.parseBoolean(mr.getGroup(2)));
                            break;
                        case "debug":
                            setDebug(Boolean.parseBoolean(mr.getGroup(2)));
                            break;
                    }
                }
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (83 * hash + Boolean.valueOf(countIn).hashCode()) % (1 << 31);
        hash = (83 * hash + Boolean.valueOf(dashAllMeasureRepetitions).hashCode()) % (1 << 31);
        hash = (83 * hash + Boolean.valueOf(playWithLineIndicator).hashCode()) % (1 << 31);
        hash = (83 * hash + Boolean.valueOf(playWithMeasureIndicator).hashCode()) % (1 << 31);
        hash = (83 * hash + Boolean.valueOf(debug).hashCode()) % (1 << 31);
        return hash;
    }

    private static AppOptions instance = new AppOptions();

    private boolean countIn = true;
    private boolean dashAllMeasureRepetitions = true;
    private boolean playWithLineIndicator = true;
    private boolean playWithMeasureIndicator = true;
    private boolean debug = false;

    private static final Logger logger = Logger.getLogger(AppOptions.class.getName());
}
