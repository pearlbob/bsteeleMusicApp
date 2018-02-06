/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Style.Unit;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 */
@JsType
public class AudioBeatDisplay {

    public AudioBeatDisplay() {
    }

//    public void initialize(AudioElement audioElement, CanvasElement element, CanvasElement element2) {
//        this.audioElement = audioElement;
//        canvas = element;
//        canvas2 = element2;
//        ctx = canvas.getContext2d();
//        ctx2 = canvas2.getContext2d();
//        
//        //  works on relative position
//        ctx.translate(0.5, 0.5);
//        ctx2.translate(0.5, 0.5);
//        
//        t = audioElement.getCurrentTime();
//
//        ctx.setFillStyle("#ffffff");
//        ctx.fillRect(0, 0, element.getWidth(), element.getHeight());
//    }
//
//    public void update(final int bpm, final boolean doSubBeat, final int reqestedBeatsPerBar) {
//        int w = canvas.getWidth();
//        int h = canvas.getHeight();
//        int beatsPerBar = (reqestedBeatsPerBar == 0 ? 4 : reqestedBeatsPerBar);
//        int sPerBar = beatsPerBar * 60 / bpm;
//        t = audioElement.getCurrentTime();
//        double barT = t % sPerBar;
//        int beat = (int) Math.floor(beatsPerBar * barT / sPerBar);
//        //GWT.log( "beat: "+beat+", barT: "+barT);
//
//        if (beat != lastBeat)
//        {
//            //   background
//            ctx.setFillStyle("#ffffff");
//            ctx.fillRect(0, 0, w, h);
//
//            //  beat indicator
//            String beatColor = (beat == 0 ? "#dc8aff" : "#a7ffba");
//            ctx.setFillStyle(beatColor);
//            ctx.fillRect(beat * w / beatsPerBar, 0, w / beatsPerBar, h);
//
//            ctx.setFillStyle("#000000");
//            ctx.setFont("25px monospace");
//            for (int b = 0; b < beatsPerBar; b++) {
//                ctx.fillText(Integer.toString(b + 1), Math.round((b + 0.5) * w / beatsPerBar), 60);
//            }
//
//            lastBeat = beat;
//        }
//        
//        if (doSubBeat) {
//            ctx2.clearRect(0, 0, canvas2.getWidth(), canvas2.getHeight());
//            
//            //  draw new line
//            ctx2.setStrokeStyle(CssColor.make(255, 0, 0));
//            ctx2.setLineWidth(2);
//            ctx2.beginPath();
//            int x = (int) Math.round(barT / sPerBar * w);
//            ctx2.moveTo(x, 0);
//            ctx2.lineTo(x, h);
//            ctx2.stroke();
//        }
//    }
//
//    private AudioElement audioElement;
//    private CanvasElement canvas;
//    private CanvasElement canvas2;
//    private Context2d ctx;
//    private Context2d ctx2;
//    private double t;
//    private int lastBeat = -1;

    public void initialize(AudioElement audioElement, CanvasElement element) {
        this.audioElement = audioElement;
        canvas = Canvas.wrap(element);
        ctx = canvas.getContext2d();
        t = audioElement.getCurrentTime(); //  temp
        

        ctx.setFillStyle("#ffffff");
        ctx.fillRect(0, 0, element.getWidth(), element.getHeight());
    }

    public void update(final int bpm, final boolean doSubBeat, final int reqestedBeatsPerBar) {
        int w = canvas.getCanvasElement().getWidth();
        int h = canvas.getCanvasElement().getHeight();
        int beatsPerBar = (reqestedBeatsPerBar == 0 ? 4 : reqestedBeatsPerBar);
        int sPerBar = beatsPerBar * 60 / bpm;
        t = audioElement.getCurrentTime();
        double barT = t % sPerBar;
        int beat = (int) Math.floor(beatsPerBar * barT / sPerBar);
        //GWT.log( "beat: "+beat+", barT: "+barT);

        if (beat != lastBeat ||doSubBeat ) 
        {
            //   background
            ctx.setFillStyle("#ffffff");
            ctx.fillRect(0, 0, w, h);

            //  beat indicator
            String beatColor = (beat == 0 ? "#dc8aff" : "#a7ffba");
            ctx.setFillStyle(beatColor);
            ctx.fillRect(beat * w / beatsPerBar, 0, w / beatsPerBar, h);

            ctx.setFillStyle("#000000");
            ctx.setFont("bold 40px sans-serif");
            for (int b = 0; b < beatsPerBar; b++) {
                //ctx.fillRect(Math.round((b + 0.5) * w / beatsPerBar), 0, 15, h);
                ctx.fillText(Integer.toString(b + 1), Math.round((b + 0.5) * w / beatsPerBar), h*3/4+2);
            }

            if (doSubBeat) {
                //  draw new line
                ctx.setStrokeStyle(CssColor.make(255, 0, 0));
                ctx.setLineWidth(2);
                ctx.beginPath();
                int x = (int) Math.round(barT / sPerBar * w);
                GWT.log(Integer.toString(x));
                ctx.moveTo(x, 0);
                ctx.lineTo(x, h);
                ctx.stroke();
            }
            lastBeat = beat;
        }
    }

    private AudioElement audioElement;
    private Canvas canvas;
    private Context2d ctx;
    private double t;
    private int lastBeat = -1;
}
