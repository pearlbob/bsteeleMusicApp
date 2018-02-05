/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.CanvasElement;
import static jsinterop.annotations.JsPackage.GLOBAL;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType(namespace = GLOBAL)
public class AudioBeatDisplay {

    AudioBeatDisplay(Object audioContext, CanvasElement element) {
        this.audioContext = audioContext;
        canvas = Canvas.wrap(element);
        ctx = canvas.getContext2d();
        t = System.currentTimeMillis() / 1000; //  temp

        ctx.setFillStyle("#ffffff");
        ctx.fillRect(0, 0, element.getWidth(), element.getHeight());
    }

    public void update(final int bpm, final boolean doSubBeat, final int reqestedBeatsPerBar) {
        int w = canvas.getCanvasElement().getWidth();
        int h = canvas.getCanvasElement().getHeight();
        int beatsPerBar = (reqestedBeatsPerBar == 0 ? 4 : reqestedBeatsPerBar);
        int sPerBar = beatsPerBar * 60 / bpm;
         t = System.currentTimeMillis() / 1000;
        long barT = t % sPerBar;
        int beat = (int) Math.floorDiv(beatsPerBar * barT, sPerBar);

        //   background
        ctx.setFillStyle("#ffffff");
        ctx.fillRect(0, 0, w, h);

        //  beat indicator
        String beatColor = (beat == 0 ? "#dc8aff" : "#a7ffba");
        ctx.setFillStyle(beatColor);
        ctx.fillRect(beat * w / beatsPerBar, 0, w / beatsPerBar, h);

        ctx.setFillStyle("#000000");
        ctx.setFont(  "25px monospace");
        for (int b = 0; b < beatsPerBar; b++) {
            ctx.fillText(Integer.toString(b + 1), Math.round((b + 0.5) * w / beatsPerBar), 25);
        }

        if (doSubBeat) {
            //  draw new line
            ctx.setStrokeStyle( CssColor.make(255,0,0));
            ctx.setLineWidth(2);
            ctx.beginPath();
            int x = Math.round(barT / sPerBar * w);
            ctx.moveTo(x, 0);
            ctx.lineTo(x, h);
            ctx.stroke();
        }
    }

    private final Object audioContext;
    private final Canvas canvas;
    private final Context2d ctx;
    private long t;

}
