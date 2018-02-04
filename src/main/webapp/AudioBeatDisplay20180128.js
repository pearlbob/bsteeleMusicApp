
/* 
 * Copyright 2018 Robert Steele at bsteele.com
 */

class AudioBeatDisplay {

    constructor(audioContext, canvas) {
        this.audioContext = audioContext;
        this.canvas = canvas;
        this.ctx = this.canvas.getContext("2d");
        this.t = Date.now() / 1000; //  temp

        this.ctx.fillStyle = "#ffffff";
        this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
    }

    _log(s) {
        window.console.log(s);
    }

    update(bpm, doSubBeat, reqestedBeatsPerBar) {
        let ctx = this.ctx;
        let w = this.ctx.canvas.width;
        let beatsPerBar = (reqestedBeatsPerBar === undefined ? 4 : reqestedBeatsPerBar);
        let sPerBar = beatsPerBar * 60 / bpm;
        let t = Date.now() / 1000;
        let barT = t % sPerBar;
        let beat = Math.trunc(beatsPerBar * barT / sPerBar);

        //   background
        ctx.fillStyle = "#ffffff";
        ctx.fillRect(0, 0, w, this.ctx.canvas.height);

        //  beat indicator
        let beatColor = (beat === 0 ? "#dc8aff" : "#a7ffba");
        ctx.fillStyle = beatColor;
        ctx.fillRect(beat * w / beatsPerBar, 0, w / beatsPerBar, this.ctx.canvas.height);

        ctx.fillStyle = "#000000";
        ctx.font = '25px monospace';
        for (let b = 0; b < beatsPerBar; b++) {
            ctx.fillText(b + 1, Math.round((b + 0.5) * w / beatsPerBar), 25);
        }


        if (doSubBeat) {
            //  draw new line
            ctx.strokeStyle = "#ff0000";
            ctx.lineWidth = 2;
            ctx.beginPath();
            let x = Math.round(barT / sPerBar * this.ctx.canvas.width);
            ctx.moveTo(x, 0);
            ctx.lineTo(x, this.ctx.canvas.height);
            ctx.stroke();
        }
    }

}

/*
 * myEntity.offscreenCanvas = document.createElement('canvas');
 myEntity.offscreenCanvas.width = myEntity.width;
 myEntity.offscreenCanvas.height = myEntity.height;
 myEntity.offscreenContext = myEntity.offscreenCanvas.getContext('2d');
 
 myEntity.render(myEntity.offscreenContext);
 
 //grab the context from your destination canvas
 var destCtx = destinationCanvas.getContext('2d');
 
 //call its drawImage() function passing it the source canvas directly
 destCtx.drawImage(sourceCanvas, 0, 0);
 
 ctx.drawImage(image, sx, sy, sWidth, sHeight, dx, dy, dWidth, dHeight);
 */
