package com.bsteele.bsteeleMusicApp.client.legacy;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

import java.util.ArrayList;

/**
 * Definition of drum section for one or more measures
 * to be used either as the song's default drums or
 * the special drums for a given section.
 */
@Deprecated
public class LegacyDrumSection {

    /**
     * Get the section's drum measures
     *
     * @return the drum measure
     */
    public ArrayList<LegacyDrumMeasure> getDrumMeasures() {
        return drumMeasures;
    }

    /**
     * Set the section's drum measures in bulk
     *
     * @param drumMeasures the drum measures
     */
    public void setDrumMeasures(ArrayList<LegacyDrumMeasure> drumMeasures) {
        this.drumMeasures = drumMeasures;
    }

    private ArrayList<LegacyDrumMeasure> drumMeasures;
}
