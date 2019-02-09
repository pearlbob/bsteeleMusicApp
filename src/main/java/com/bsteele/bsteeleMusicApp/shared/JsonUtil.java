/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * @author bob
 */
public class JsonUtil {

    public static final int toInt(JSONValue jv) {
        JSONNumber jn = jv.isNumber();
        if (jn != null) {
            return (int) jn.doubleValue();
        }
        return Integer.parseInt(jv.isString().stringValue());
    }

    /** no longs!
     *
     * @param jv
     * @return
     */
//    public static final long toLong(JSONValue jv) {
//        if (jv == null)
//            return 0;
//        return Long.parseLong(jv.toString());
//    }

    public static final double toDouble(JSONValue jv) {
        JSONNumber jn = jv.isNumber();
        if (jn != null) {
            return jn.doubleValue();
        }
        return Double.NaN;
    }

    /**
     * Encode a given string in JSON.
     *
     * @param s
     * @return
     */
    public static final String encode(String s) {
        if (s == null || s.length() == 0)
            return "";
        JSONString jsonString = new JSONString(s);
        return jsonString.toString();
    }

}
