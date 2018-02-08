/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 *
 * @author bob
 */
public class SectionColorMap {

    public static CssColor sectionColor(Section s) {
        switch (s) {
            case a:
                return verseColor;
            case b:
                return chorusColor;
            case bridge:
                return bridgeColor;
            case chorus:
                return chorusColor;
            case coda:
            case intro:
            case outro:
                return introColor;
            case tag:
                return tagColor;
            case preChorus:
                return tagColor;
            case verse:
                return preChorusColor;
            default:
                return chorusColor;
        }
    }

    private static final CssColor verseColor = CssColor.make(0xf5, 0xf2, 0xe9);
    private static final CssColor chorusColor = CssColor.make(0xff, 0xff, 0xff);
    private static final CssColor bridgeColor = CssColor.make(0xf6, 0xed, 0xff);
    private static final CssColor introColor = CssColor.make(0xEC, 0xff, 0xF9);
    private static final CssColor preChorusColor = CssColor.make(0xf7, 0xf0, 0xff);
    private static final CssColor tagColor = CssColor.make(0xedf6, 0xf6, 0xff);
}
