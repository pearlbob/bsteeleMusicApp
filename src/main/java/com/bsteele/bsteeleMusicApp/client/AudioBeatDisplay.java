/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.CanvasElement;

import java.util.logging.Logger;

/**
 * @author bob
 */
public class AudioBeatDisplay {

    public AudioBeatDisplay(CanvasElement canvasElement) {
        initialize(canvasElement);
    }

    private void initialize(CanvasElement canvasElement) {
        canvas = canvasElement;
        ctx = canvas.getContext2d();

        //  fixme: there should be a way to get the color!
//        for (Element e = canvasElement; e != null; e = e.getParentElement()) {
//            String c = e.getStyle().getBackgroundColor();
//            if (c != null && c.length() > 0) {
//                backgroundColor = c;
//                break;
//            }
//        }

        ctx.setFillStyle(backgroundColor);
        ctx.fillRect(0, 0, canvasElement.getWidth(), canvasElement.getHeight());
    }

    public final void update(final SongUpdate songUpdate, final int beatCount, final int beatNumber, final double beatFraction) {

        //  fixme:  only can on an as needed basis

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        //  background
        String startColor = gray;

        {
            int n = songUpdate.getMomentNumber();
            switch (n) {
                case -2:
                    startColor = orange;
                    break;
                case -1:
                    startColor = red;
                    break;
                default:
                    if (n >= 0)
                        //  normal
                        startColor = black;
                    break;
            }
        }
        //logger.finest("b: " + beatNumber + " " + beatFraction);

        ctx.setFillStyle(backgroundColor);
        final int radius = 7;
        double padding = w * 0.25;
        int bounceH = h - 2 * radius - radius;
        ctx.fillRect(0, 0, w, h);

        //  text
        ctx.setFillStyle("#000000");
        ctx.setFont("bold 40px sans-serif");
        ctx.fillText(Integer.toString(beatNumber + 1), 4, h * 3 / 4 + 2);

        ctx.setFillStyle(startColor);
        ctx.beginPath();

        double x = beatFraction - 0.5;
        double dx = (beatNumber == beatCount - 1
                ? (beatCount - 1) * (1 - beatFraction)
                : beatNumber % beatCount + beatFraction);
        //  logger.info( "dx: "+dx+", n: "+beatNumber);
        ctx.arc(padding + radius + bounceH / 2 * dx
                , h - radius - (-4 * x * x + 1) * bounceH
                , radius, 0, Math.PI * 2);
        ctx.fill();
    }

    private static final String black = "#000000";
    private static final String gray = "#808080";
    private static final String orange = "#FFA500";
    private static final String red = "#FF0000";
    //private static final String lawnGreen = "#7CFC00";
    private static final String backgroundColor = "#FFFDF6";
    private CanvasElement canvas;
    private Context2d ctx;
    private static final CssColor ballColor = CssColor.make(255, 0, 0);

    private static Logger logger = Logger.getLogger(AudioBeatDisplay.class.getName());
}
