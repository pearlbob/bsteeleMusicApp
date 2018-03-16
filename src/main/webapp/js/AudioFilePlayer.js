/* 
 * Copyright 2018 Robert Steele at bsteele.com
 */

"use strict";

function AudioFilePlayer() {
    this.fileMap = new Map();

    //  setup audio output
    window.AudioContext = window.AudioContext || window.webkitAudioContext;
    this.audioContext = new window.AudioContext();
    this.mp3Sources = new Array(0);

    this.gain = this.audioContext.createGain();
    this.gain.gain.linearRampToValueAtTime(1, this.audioContext.currentTime + 0.01);
    this.gain.connect(this.audioContext.destination);


    this.bufferFile = function (filePath) {
        let buffer = this.fileMap.get(filePath);
        if (buffer === undefined) {
            //  fixme: should likely be on a webworker
            // Async
            let req = new XMLHttpRequest();
            req.parent = this;
            req.filePath = filePath;
            req.cb = function (buffer) {
                req.parent.fileMap.set(req.filePath, buffer);
            };
            req.open('GET', filePath, true);
            // XHR2
            req.responseType = 'arraybuffer';
            req.onload = function () {
                this.parent.audioContext.decodeAudioData(req.response, req.cb);
            };
            req.send();
            return true;
        }
        return false;
    };

    this.play = function (filePath, when, duration) {
        let buffer = this.fileMap.get(filePath);
        if (buffer === undefined) {
            return false;
        }

        //  a throwaway source object!  by api design
        let source = this.audioContext.createBufferSource();
        source.buffer = buffer;
        source.connect(this.gain);
        source.start(when);
        this.mp3Sources.push(source);
        return true;
    };
}
;