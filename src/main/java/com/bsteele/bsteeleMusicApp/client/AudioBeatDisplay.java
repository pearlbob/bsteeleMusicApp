/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.CanvasElement;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType
public class AudioBeatDisplay {

    public AudioBeatDisplay() {
    }

    public void initialize(AudioElement audioElement, CanvasElement canvasElement) {
        this.audioElement = audioElement;
        canvas = Canvas.wrap(canvasElement);
        ctx = canvas.getContext2d();

        backgroundColor = "#FFFDF6";
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

    public void update(final double currentTime, final int bpm, final boolean doSubBeat, final int reqestedBeatsPerBar) {
        int w = canvas.getCanvasElement().getWidth();
        int h = canvas.getCanvasElement().getHeight();
        int beatsPerBar = (reqestedBeatsPerBar == 0 ? 4 : reqestedBeatsPerBar);
        double sPerBar = beatsPerBar * 60.0 / bpm;
        double barT = currentTime % sPerBar;
        int beat = (int) Math.floor(beatsPerBar * barT / sPerBar);
        double beatT = barT % (sPerBar / beatsPerBar);

        //GWT.log("beat: " + beat + ", barT: " + barT + ", beatT: " + beatT);
        switch (1) {
            default:
                if (beat != lastBeat || doSubBeat) {
                    //   background
                    ctx.setFillStyle(backgroundColor);
                    ctx.fillRect(0, 0, w, h);

                    //  beat indicator
                    String beatColor = (beat == 0 ? "#dc8aff" : "#a7ffba");
                    ctx.setFillStyle(beatColor);
                    ctx.fillRect(beat * w / beatsPerBar, 0, w / beatsPerBar, h);

                    ctx.setFillStyle("#000000");
                    ctx.setFont("bold 40px sans-serif");
                    for (int b = 0; b < beatsPerBar; b++) {
                        ctx.fillText(Integer.toString(b + 1), Math.round((b + 0.5) * w / beatsPerBar), h * 3 / 4 + 2);
                    }

                    if (doSubBeat) {
                        //  draw new line
                        ctx.setStrokeStyle(CssColor.make(255, 0, 0));
                        ctx.setLineWidth(2);
                        ctx.beginPath();
                        int x = (int) Math.round(barT / sPerBar * w);
                        //GWT.log(Integer.toString(x));
                        ctx.moveTo(x, 0);
                        ctx.lineTo(x, h);
                        ctx.stroke();
                    }
                    lastBeat = beat;
                }
                break;
            case 1:
                if (beat != lastBeat || beatT > flashDurationS) {
                    //  background
                    String beatColor = (beat == 0 && beatT < flashDurationS ? "#dc8aff" : backgroundColor);
                    ctx.setFillStyle(beatColor);
                    ctx.fillRect(0, 0, w, h);
                    //GWT.log(Double.toString(beatFraction));

                    //  text
                    ctx.setFillStyle("#000000");
                    ctx.setFont("bold 40px sans-serif");
                    ctx.fillText(Integer.toString(beat + 1), w / 2, h * 3 / 4 + 2);
                }
                break;

        }
    }

    private AudioElement audioElement;
    private Canvas canvas;
    private Context2d ctx;
    private int lastBeat = -1;
    private String backgroundColor;
    private static final double flashDurationS = 0.05;
}
