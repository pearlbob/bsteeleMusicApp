package com.bsteele.bsteeleMusicApp.client.application;

import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AppOptionsTest {

    @Test
    public void toJson() {
        AppOptions appOptions = AppOptions.getInstance();
        int lastHash = 0;
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
                            int hash = appOptions.hashCode();
                            assertNotEquals(hash, lastHash);
                            lastHash = hash;

                            String json = appOptions.toJson();
                            appOptions.fromJson(appOptions.toJson());
                            assertEquals(hash, appOptions.hashCode());
                            String json2 = appOptions.toJson();
                            assertEquals(json, json2);
                            assertEquals(hash, appOptions.hashCode());
                        }
                    }
                }
            }
        }


    }


    private static final Logger logger = Logger.getLogger(AppOptionsTest.class.getName());
}