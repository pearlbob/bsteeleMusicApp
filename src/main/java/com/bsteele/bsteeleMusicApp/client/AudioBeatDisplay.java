/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;

/**
 * @author bob
 */
public class AudioBeatDisplay {

    private AudioBeatDisplay() {
    }

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

    public void update(final double currentTime, final double startTime,
                       final int bpm, final boolean doSubBeat, final int requestedBeatsPerBar) {

        int beatsPerBar = (requestedBeatsPerBar <= 0 ? 4 : requestedBeatsPerBar);
        double sPerBeat =                  60.0 / bpm;
        double sPerBar = beatsPerBar * sPerBeat;
        double barT = Util.mod(currentTime, sPerBar);
        int beat = (int) Math.floor(beatsPerBar * barT / sPerBar);
        double beatT = barT % (sPerBar / beatsPerBar);
        boolean beatFlash = (beatT < sPerBeat/2);

        //  fixme: optimize audioBeatDisplay to update canvas on beat changes only

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        
        if (beat != lastBeat || beatFlash != lastBeatFlash) {
            //  background
            String startColor = backgroundColor;
            {
                int barsFromStart = (int) Math.floor((currentTime - startTime) / sPerBar);

                    //GWT.log("b: "+beat+", "+lastBeatFlash);

                    switch (barsFromStart) {
                        case -2:
                            startColor = orange;
                            break;
                        case -1:
                            startColor = red;
                            break;
                        case 0:
                            startColor = lawnGreen;
                            break;
                    }
            }
            String beatColor = ((beat == 0 && beatFlash) ? beat1FlashColor : startColor);

            ctx.setFillStyle(beatColor);
            double padding = w * 0.15;
            ctx.fillRect(padding, 0, w-2*padding, h);

            //  text
            ctx.setFillStyle("#000000");
            ctx.setFont("bold 40px sans-serif");
            ctx.fillText(Integer.toString(beat + 1), w / 2, h * 3 / 4 + 2);

            lastBeat = beat;
            lastBeatFlash = beatFlash;

            //GWT.log("t: "+(currentTime-lastT)+", b: "+beat+", "+beatColor);
            //lastT=  currentTime;
        }
    }

    private static final String beat1FlashColor = "#dc8aff";
    private static final String orange = "#FFA500";
    private static final String red = "#FF0000";
    private static final String lawnGreen = "#7CFC00";
    private static final String backgroundColor = "#FFFDF6";
    private CanvasElement canvas;
    private Context2d ctx;
    private int lastBeat = -1;
    private boolean lastBeatFlash;
}
