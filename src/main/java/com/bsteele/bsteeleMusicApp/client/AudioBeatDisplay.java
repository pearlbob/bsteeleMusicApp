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


        //logger.finest("b: " + beatNumber + " " + beatFraction);

        ctx.setFillStyle(backgroundColor);
        final int radius = 9;
        final double padding = w * 0.05;
        final double dw = (w - 2 * padding) / 6;
        int bounceH = h - 2 * radius - radius;
        ctx.fillRect(0, 0, w, h);

        //  text
        if ( false) {
            ctx.setFillStyle("#000000");
            ctx.setFont("bold 40px sans-serif");
            ctx.fillText(Integer.toString(beatNumber + 1), 4, h * 3 / 4 + 2);
        }

        ctx.setFillStyle(lightGray);
        ctx.fillRect(padding + radius, 0, dw, h);   //  first one is always there
        switch (beatCount) {
            default:
            case 2:
                break;
            case 3:
                ctx.fillRect(padding + radius + 2 * dw, 0, dw/3, h);
                break;
            case 4:
                ctx.fillRect(padding + radius + 2 * dw, 0, dw, h);
                break;
            case 6:
                ctx.fillRect(padding + radius + 2 * dw, 0, dw, h);
                ctx.fillRect(padding + radius + 4 * dw, 0, dw, h);
                break;
        }

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
        ctx.setFillStyle(startColor);
        double x = beatFraction - 0.5;
        double dx = (beatNumber == beatCount - 1
                ? (beatCount - 1) * (1 - beatFraction)
                : beatNumber % beatCount + beatFraction);
        //  logger.info( "dx: "+dx+", n: "+beatNumber);
        ctx.beginPath();
        ctx.arc(padding + radius + dw * dx
                , h - radius - (-4 * x * x + 1) * bounceH
                , radius, 0, Math.PI * 2);
        ctx.closePath();
        ctx.fill();
    }

    private static final String black = "#000000";
    private static final String gray = "#808080";
    private static final String lightGray = "#e8e8e8";
    private static final String orange = "#FFA500";
    private static final String red = "#FF0000";
    //private static final String lawnGreen = "#7CFC00";
    private static final String backgroundColor = "#f5f0e1";
    private CanvasElement canvas;
    private Context2d ctx;
    private static final CssColor ballColor = CssColor.make(255, 0, 0);

    private static Logger logger = Logger.getLogger(AudioBeatDisplay.class.getName());
}
