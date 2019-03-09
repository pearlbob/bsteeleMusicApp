package com.bsteele.bsteeleMusicApp.shared.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;

public class SongMomentLocation {

    SongMomentLocation(@Nonnull ChordSectionLocation chordSectionLocation, int index) {
        this.chordSectionLocation = chordSectionLocation;
        this.index = index;
    }

    /**
     * @param sb string buffer
     * @return the song moment location parsed
     */
    public static final SongMomentLocation parse(StringBuffer sb) {

        ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse(sb);
        if (chordSectionLocation == null)
            return null;

        if (sb.length() < 2)
            return null;

        final RegExp numberRegexp = RegExp.compile("^"+separator+"(\\d+)");  //  workaround for RegExp is not serializable.
        MatchResult mr = numberRegexp.exec(sb.substring(0, Math.min(sb.length(), 5)));
        if (mr != null) {
            try {
                int index = Integer.parseInt(mr.getGroup(1));
                if ( index <=0 )
                    return null;
                sb.delete(0, mr.getGroup(0).length());
                return new SongMomentLocation(chordSectionLocation, index);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SongMomentLocation))
            return false;
        SongMomentLocation o = (SongMomentLocation) obj;
        return chordSectionLocation.equals(o.chordSectionLocation)
                && index == o.index;
    }
    public String getId() {
        return chordSectionLocation.getId() + separator + index;
    }

    public int getIndex() {
        return index;
    }

    private final ChordSectionLocation chordSectionLocation;
    private final int index;
    private static final String separator = "#";

}
