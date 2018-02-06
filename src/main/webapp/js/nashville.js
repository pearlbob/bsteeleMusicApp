/* 
 * Copyright 2015 Robert Steele, bsteele.com
 * All rights reserved.
 */


var nashville = function () {
    var nashvileEdit;
    var rhythmEditCtx;
    var rhythmNotes = new Array(0);
    var gap = 4;
    var edgeHandleSize = 8;
    var screenDirty = true;

    var dragIndex;
    var dragging;
    var mouseX;
    var dragHoldX;


    var beats = 4;
    var divisionsPerBeat = 2;
    var unit = 1 / divisionsPerBeat;

    function onLoad() {
        nashvileEdit = document.getElementById("nashvilleEdit");
        rhythmEditCtx = nashvileEdit.getContext("2d");

        var rw = rhythmEditCtx.canvas.width - gap;
        for (var b = 0; b < 4; b++) {
            var n = {start: b * divisionsPerBeat, dur: unit};
            var x = xModBeat(rw / beats * b);
            var w = xModBeat(rw / beats * n.dur) - gap - gap;
            n.x = x + gap;
            n.w = w;
            rhythmNotes.push(n);
        }

        nashvileEdit.addEventListener("mousemove", mouseMove, false);
        nashvileEdit.addEventListener("mousedown", mouseDownListener, false);
    }

    function frameUpdate() {
        
        if (!screenDirty)
            return;
        screenDirty = false;

        var ctx = rhythmEditCtx;
        var rw = ctx.canvas.width - gap;
        var rh = ctx.canvas.height;

        //  clear
        ctx.fillStyle = "#e0e0e0";
        ctx.fillRect(0, 0, rw, rh);

        //  text
        ctx.font = "20px Arial";
        ctx.textAlign = "center";
        ctx.fillStyle = 'black';
        for (var b = 1; b <= beats; b++) {
            ctx.fillText(b, gap + xModBeat(b * rw / beats) - 3 * rw / (4 * beats), 20);
            ctx.fillText('a', gap + xModBeat(b * rw / beats) - rw / (4 * beats), 20);
        }

        //  blocks
        ctx.fillStyle = 'blue';
        var y = rh / 2;
        var h = rh / 3;
        for (var b = 0; b < rhythmNotes.length; b++) {
            var n = rhythmNotes[b];
            ctx.fillRect(n.x, y, n.w, h);
        }

//        {   //  diagnostic
//            var diag = "custom: ";
//            for (var b = 0; b < rhythmNotes.length; b++) {
//                var n = rhythmNotes[b];
//                diag += ", " + n.start + ":" + n.dur
//            }
//            window.console.log(diag);
//        }
    }

    function mouseMove(event) {
        if (dragging)
            return;

        var note = null;
        mouseX = event.clientX - nashvileEdit.offsetLeft;
        //window.console.log(mouseX);
        for (var i = 0; i < rhythmNotes.length; i++) {
            if (hitTest(rhythmNotes[i], mouseX)) {
                note = rhythmNotes[i];
                break;
            }
        }
        if (note !== null) {
            if (mouseX < note.x + edgeHandleSize)
                nashvileEdit.style.cursor = "w-resize";
            else if (mouseX > note.x + note.w - edgeHandleSize)
                nashvileEdit.style.cursor = "e-resize";
            else
                nashvileEdit.style.cursor = "pointer";
        } else
            nashvileEdit.style.cursor = "auto";
    }

    function mouseDownListener(event) {
        var i;
        //We are going to pay attention to the layering order of the objects so that if a mouse down occurs over more than object,
        //only the topmost one will be dragged.
        var highestIndex = -1;

        //getting mouse position correctly, being mindful of resizing that may have occured in the browser:
        mouseX = event.clientX - nashvileEdit.offsetLeft;

        //find which shape was clicked
        var note = null;
        for (i = 0; i < rhythmNotes.length; i++) {
            if (hitTest(rhythmNotes[i], mouseX)) {
                dragging = true;
                if (i > highestIndex) {
                    //We will pay attention to the point on the object where the mouse is "holding" the object:
                    note = rhythmNotes[i];
                    dragHoldX = mouseX - note.x;
                    highestIndex = i;
                    dragIndex = i;
                }
            }
        }
        nashvileEdit.style.cursor = "pointer";
        if (note !== null && mouseX < note.x + edgeHandleSize)
            nashvileEdit.style.cursor = "w-resize";
        else if (note !== null && mouseX > note.x + note.w - edgeHandleSize)
            nashvileEdit.style.cursor = "e-resize";

        if (dragging) {
            window.addEventListener("mousemove", mouseMoveListener, false);
        }
        nashvileEdit.removeEventListener("mousedown", mouseDownListener, false);
        window.addEventListener("mouseup", mouseUpListener, false);

        //code below prevents the mouse down from having an effect on the main browser window:
        event.preventDefault();

        return false;
    }

    function mouseUpListener(evt) {
        nashvileEdit.addEventListener("mousedown", mouseDownListener, false);
        window.removeEventListener("mouseup", mouseUpListener, false);
        if (dragging) {
            dragging = false;
            window.removeEventListener("mousemove", mouseMoveListener, false);
        }
        nashvileEdit.style.cursor = "auto";
    }

    function mouseMoveListener(event) {
        var posX;
        var n = rhythmNotes[dragIndex];
        var minX = gap;
        var maxX = nashvileEdit.width;
        if (dragIndex > 0)
            minX = rhythmNotes[dragIndex - 1].x + rhythmNotes[dragIndex - 1].w + gap;
        if (dragIndex < rhythmNotes.length - 1)
            maxX = rhythmNotes[dragIndex + 1].x - gap;

        //getting mouse position correctly 
        var bRect = nashvileEdit.getBoundingClientRect();
        mouseX = event.clientX - nashvileEdit.offsetLeft;

        //clamp x and y positions to prevent object from dragging outside of canvas
        posX = mouseX - dragHoldX;
        posX = (posX < minX) ? minX : ((posX > maxX - n.w) ? maxX - n.w : posX);

        var rw = rhythmEditCtx.canvas.width - gap;
        switch (nashvileEdit.style.cursor) {
            case "w-resize":
                var end = n.x + n.w;
                n.x = (mouseX > end - rw / (beats * divisionsPerBeat) ? end - rw / (beats * divisionsPerBeat) : (mouseX < minX ? minX : mouseX));
                n.x = xModBeat(n.x) + gap;
                n.start = computeBeatFromX(n.x);
                n.w = xModBeat(end - n.x) - 2 * gap;
                n.dur = computeBeatFromX(n.w) * unit;
                break;
            case "e-resize":
                //window.console.log((n.x + 2 * edgeHandleSize) + ", " + mouseX + ", " + (maxX - gap));
                n.w = (mouseX > maxX ? maxX : (mouseX < n.x + 2 * edgeHandleSize ? n.x + 2 * edgeHandleSize : mouseX)) - n.x;
                n.w = xModBeat(n.w) - 2 * gap;
                n.dur = computeBeatFromX(n.w) * unit;
                break;
            default:
                n.x = xModBeat(posX) + gap;
                n.start = computeBeatFromX(n.x);
                break;
        }

        screenDirty = true;
    }

    function xModBeat(x) {
        var rw = rhythmEditCtx.canvas.width - gap;
        return Math.round(beats * divisionsPerBeat * x / rw) * rw / (beats * divisionsPerBeat);
    }
    function computeBeatFromX(x) {
        var rw = rhythmEditCtx.canvas.width - gap;
        return Math.round(beats * divisionsPerBeat * x / rw);
    }

    function hitTest(note, mx) {
        return (mx >= note.x && mx <= note.x + note.w);
    }

    function onPlus() {
        var n = rhythmNotes[rhythmNotes.length - 1];
        var rw = rhythmEditCtx.canvas.width - gap;
        var lastX = xModBeat((beats * divisionsPerBeat - 1) / (beats * divisionsPerBeat) * rw);
        if (n.x + n.w <= lastX) {
            n = {dur: unit};
            n.x = lastX + gap;
            n.w = xModBeat(rw / (beats * divisionsPerBeat)) - 2 * gap;
            n.start = computeBeatFromX(n.x);
            n.dur = computeBeatFromX(n.w) * unit;
            rhythmNotes.push(n);
        }
        screenDirty = true;
    }

    function onMinus() {
        if (rhythmNotes.length > 1)
            rhythmNotes = rhythmNotes.slice(0, rhythmNotes.length - 1);
        screenDirty = true;
    }

    function getBeatLocation(note) {
        if (note < 0 || note >= rhythmNotes.length)
            return undefined;
        var n = rhythmNotes[note];
        return n.start;
    }
    function  getBeatDuration(note) {
        if (note < 0 || note >= rhythmNotes.length)
            return undefined;
        var n = rhythmNotes[note];
        return n.dur;
    }

    return {
        onLoad: function () {
            onLoad();
        },
        frameUpdate: function () {
            frameUpdate();
        },
        onPlus: function () {
            onPlus();
        },
        onMinus: function () {
            onMinus();
        },
        getBeatLocation: function (note) {
            return getBeatLocation(note);
        },
        getBeatDuration: function (note) {
            return getBeatDuration(note);
        }
    };
}();