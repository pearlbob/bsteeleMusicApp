package com.bsteele.bsteeleMusicApp.client.legacy;

import org.junit.Test;

import static org.junit.Assert.*;

public class LegacyDrumMeasureTest {

    @Test
    public void isSilentTest() {
        LegacyDrumMeasure drumMeasure = new LegacyDrumMeasure();

        assertTrue(drumMeasure.isSilent());
        drumMeasure.setHighHat("x___");
        assertFalse(drumMeasure.isSilent());
    }
}