package com.bsteele.bsteeleMusicApp.shared.songs;

import javax.annotation.Nonnull;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeatExtension extends MeasureComment {

    public static final MeasureRepeatExtension get(ChordSectionLocation.Marker marker) {
        if (marker == null)
            return nullMeasureRepeatExtension;

        switch (marker) {
            case repeatUpperRight:
                return upperRightMeasureRepeatExtension;
            case repeatMiddleRight:
                return middleRightMeasureRepeatExtension;
            case repeatLowerRight:
                return lowerRightMeasureRepeatExtension;
            default:
            case none:
                return nullMeasureRepeatExtension;
        }
    }

    @Override
    public MeasureNodeType getMeasureNodeType() {
        return MeasureNodeType.decoration;
    }

    private MeasureRepeatExtension(String markerString) {
        this.markerString = markerString;
    }

    public String getHtmlBlockId() {
        return "RE";
    }


    @Override
    public String transpose(@Nonnull Key key, int halfSteps) {
        return toString();
    }


    @Override
    public String toMarkup() {
        return "";
    }

    @Override
    public boolean isRepeat() {
        return true;
    }

    @Override
    public String toString() {
        return markerString;
    }

    private static final String uppperRight = 	"\u23A4";
    private static final String lowerRight = "\u23A6";
    private static final String uppperLeft = "\u23A1";
    private static final String lowerLeft = "\u23A3";
    private static final String extension = "\u23A5";
    private static final MeasureRepeatExtension upperRightMeasureRepeatExtension = new MeasureRepeatExtension(uppperRight);
    private static final MeasureRepeatExtension middleRightMeasureRepeatExtension = new MeasureRepeatExtension(extension);
    private static final MeasureRepeatExtension lowerRightMeasureRepeatExtension = new MeasureRepeatExtension(lowerRight);
    private static final MeasureRepeatExtension nullMeasureRepeatExtension = new MeasureRepeatExtension("");

    private final String markerString;

}
