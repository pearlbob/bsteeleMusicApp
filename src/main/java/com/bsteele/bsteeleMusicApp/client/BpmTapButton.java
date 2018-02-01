/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Button;

/**
 *
 * @author bob
 */
public class BpmTapButton extends Button {

    public BpmTapButton() {
        super("BPM Tap");
        initialize();
    }

    private void initialize() {
        addClickHandler(new MyHandler());
        setEnabled(true);
    }

    private class MyHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            long t = System.currentTimeMillis();
            dt = t - lastMillisT;
            lastMillisT = t;
            double bpm = 60 * 1e3 / dt;
            setText(numberFormat.format(bpm));
            //GWT.log("BpmTapButton clicked: dt: "+dt+" hz: "+(1e3/dt));
        }

    }

    public long getDt() {
        return dt;
    }

    private long dt = 1;
    private NumberFormat numberFormat = NumberFormat.getFormat("#####0.000000");
    private long lastMillisT = System.currentTimeMillis();
}
