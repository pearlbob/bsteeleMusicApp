package com.bsteele.bsteeleMusicApp.client.legacy;

import com.bsteele.bsteeleMusicApp.shared.songs.DrumType;

import java.util.EnumMap;
import java.util.Map;

/**
 * CopyRight 2018 bsteele.com
 * User: bob.
 */

/**
 * Descriptor of the drums to be played for the given measure and
 * likely subsequent measures.
 */
@Deprecated
public class LegacyDrumMeasure {


    /**
     * Descriptor of a single drum in the measure.
     */
    public class Part {

        /**
         * The drum type for the part described
         *
         * @return the drum type
         */
        public DrumType getDrumType() {
            return drumType;
        }

        /**
         * The drum type for the part described
         *
         * @param drumType the drum type
         */
        public void setDrumType(DrumType drumType) {
            this.drumType = drumType;
        }

        /**
         * Get the divisions per beat, i.e. the drum part resolution
         *
         * @return divisions per beat
         */
        public int getDivisionsPerBeat() {
            return divisionsPerBeat;
        }

        /**
         * Set the divisions per beat, i.e. the drum part resolution
         *
         * @param divisionsPerBeat divisions per beat
         */
        public void setDivisionsPerBeat(int divisionsPerBeat) {
            this.divisionsPerBeat = divisionsPerBeat;
        }

        /**
         * Get the description in the form of a string where drum hits
         * are non-white space and silence are spaces.  Resolution of the drum
         * description is determined by the divisions per beat.  When the length
         * of the description is less than the divisions per beat times the beats per measure,
         * the balance of the measure will be silent.
         *
         * @return the string encoded drum description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Set the drum part description
         *
         * @param description the string encoded drum description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        private DrumType drumType;
        private int divisionsPerBeat;
        private String description;

    }

    /**
     * Get all parts as a map.
     *
     * @return all parts as a map
     */
    public Map<DrumType, Part> getParts() {
        return parts;
    }

    /**
     * Get an individual drum's part.
     *
     * @param drumType the selected part's type
     * @return the drum's part
     */
    public Part getPart(DrumType drumType) {
        return parts.get(drumType);
    }

    /**
     * Set an individual drum's part.
     *
     * @param drumType the selected part's type
     * @param part     the part descriptor
     */
    public void setPart(DrumType drumType, Part part) {
        parts.put(drumType, part);
    }

    private EnumMap<DrumType, Part> parts;

    //  legacy stuff
    //
    public String getHighHat() {
        return highHat;
    }

    public void setHighHat(String highHat) {
        this.highHat = (highHat == null ? "" : highHat);
        isSilent = null;
    }

    public String getSnare() {
        return snare;
    }

    public void setSnare(String snare) {
        this.snare = (snare == null ? "" : snare);
        isSilent = null;
    }

    public String getKick() {
        return kick;
    }

    public void setKick(String kick) {
        this.kick = (kick == null ? "" : kick);
        isSilent = null;
    }

    public boolean isSilent() {
        if (isSilent == null)
            isSilent = !(highHat.matches(".*[xX].*")
                    || snare.matches(".*[xX].*")
                    || kick.matches(".*[xX].*"));
        return isSilent;
    }

    @Override
    public String toString() {
        return "{" + highHat + ", " + snare + ", " + kick + '}';
    }

    private String highHat = "";
    private String snare = "";
    private String kick = "";
    private Boolean isSilent;
}
