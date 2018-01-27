/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
    
    private void initialize()
    {
        addClickHandler(new MyHandler());
        setEnabled(true);
    }


    private class MyHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            System.out.println("BpmTapButton clicked");
        }

    }

}
