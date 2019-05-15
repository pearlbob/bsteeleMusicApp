package com.bsteele.bsteeleMusicApp.client.application;

import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class AppOptionsTest {

    @Test
    public void toJson() {
        AppOptions appOptions = AppOptions.getInstance();
        ArrayList<Boolean> booleanValues = new ArrayList<>();
        booleanValues.add(true);
        booleanValues.add(false);

        for (boolean countIn : booleanValues) {
            for (boolean dashAllMeasureRepetitions : booleanValues) {
                for (boolean playWithLineIndicator : booleanValues) {
                    for (boolean playWithMeasureIndicator : booleanValues) {
                        for (boolean debug : booleanValues) {
                            appOptions.setCountIn(countIn);
                            appOptions.setDashAllMeasureRepetitions(dashAllMeasureRepetitions);
                            appOptions.setPlayWithLineIndicator(playWithLineIndicator);
                            appOptions.setPlayWithMeasureIndicator(playWithMeasureIndicator);
                            appOptions.setDebug(debug);

                            String json = appOptions.toJson();
                            appOptions.fromJson(appOptions.toJson());
                            String json2 = appOptions.toJson();
                            assertEquals(json, json2);
                        }
                    }
                }
            }
        }


    }


    private static final Logger logger = Logger.getLogger(AppOptionsTest.class.getName());
}