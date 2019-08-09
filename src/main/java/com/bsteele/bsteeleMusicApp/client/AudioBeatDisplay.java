/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.songs.SongUpdate;
import com.bsteele.bsteeleMusicApp.shared.songs.SongMoment;
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

    public final void update(final SongUpdate songUpdate, final int beatNumber, final double beatFraction) {

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        //  background
        String startColor = gray;
        {
            //GWT.log("b: "+beatNumber);

            switch (songUpdate.getMomentNumber()) {
                case -2:
                    startColor = orange;
                    break;
                case -1:
                    startColor = red;
                    break;
                case 0:
                    startColor = lawnGreen;
                    break;
                default:
                    startColor = "#000000";
                    break;
            }
        }
        logger.finest("b: " + beatNumber + " " + beatFraction);

        ctx.setFillStyle(backgroundColor);
        final int radius = 7;
        double padding = w * 0.15;
        int bounceH = h - 2 * radius - radius;
        ctx.fillRect(padding, 0, w, h);

        //  text
//        ctx.setFillStyle("#000000");
//        ctx.setFont("bold 40px sans-serif");
//        ctx.fillText(Double.toString(beatNumber + 1 + beatFraction), w / 2, h * 3 / 4 + 2);

        ctx.setFillStyle(startColor);
        ctx.beginPath();

        double x = beatFraction - 0.5;
        ctx.arc(padding + radius + bounceH * (beatNumber + beatFraction), h - radius - (-4 * x * x + 1) * bounceH
                , radius, 0, Math.PI * 2);
        ctx.fill();
    }

    private static final String gray = "#808080";
    private static final String orange = "#FFA500";
    private static final String red = "#FF0000";
    private static final String lawnGreen = "#7CFC00";
    private static final String backgroundColor = "#FFFDF6";
    private CanvasElement canvas;
    private Context2d ctx;
    private static final CssColor ballColor = CssColor.make(255, 0, 0);

    private static Logger logger = Logger.getLogger(AudioBeatDisplay.class.getName());
}
