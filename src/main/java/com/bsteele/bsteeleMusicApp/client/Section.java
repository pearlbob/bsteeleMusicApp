/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

/**
 *
 * @author bob
 */
public enum Section {
    intro("i"),
    verse("v"),
    preChorus("pc"),
    chorus("c"),
    bridge("br"),
    coda("co"),
    tag("t"),
    outro("o");

    Section(String abrev) {

    }

    private String abreviation;
}
