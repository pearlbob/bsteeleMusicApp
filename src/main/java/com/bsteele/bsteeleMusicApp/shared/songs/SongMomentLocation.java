package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.util.MarkedString;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import javax.annotation.Nonnull;
import java.text.ParseException;

public class SongMomentLocation {

    SongMomentLocation(@Nonnull ChordSectionLocation chordSectionLocation, int index) {
        this.chordSectionLocation = chordSectionLocation;
        this.index = index;
    }

    public static final SongMomentLocation parse(String s) throws ParseException  {
        return parse(new MarkedString(s));
    }

    /**
     * @param markedString string buffer
     * @return the song moment location parsed
     */
    public static final SongMomentLocation parse(MarkedString markedString) throws ParseException  {

        ChordSectionLocation chordSectionLocation = ChordSectionLocation.parse(markedString);

        if (markedString.available() < 2)
            return null;

        final RegExp numberRegexp = RegExp.compile("^"+separator+"(\\d+)");  //  workaround for RegExp is not serializable.
        MatchResult mr = numberRegexp.exec(markedString.remainingStringLimited(5));
        if (mr != null) {
            try {
                int index = Integer.parseInt(mr.getGroup(1));
                if ( index <=0 )
                    return null;
                markedString.consume(mr.getGroup(0).length());
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
                && index == o.index
                ;
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
