/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONValue;

/**
 *
 * @author bob
 */
public class JsonUtil {

  public static int toInt(JSONValue jv) {
    JSONNumber jn = jv.isNumber();
    if (jn != null) {
      return (int) jn.doubleValue();
    }
    return Integer.parseInt(jv.isString().stringValue());
  }

  public static long toLong(JSONValue jv) {
    if ( jv == null )
      return 0;
    return Long.parseLong(jv.toString());
  }

  public static double toDouble(JSONValue jv) {
    JSONNumber jn = jv.isNumber();
    if (jn != null) {
      return jn.doubleValue();
    }
    return Double.NaN;
  }

  public static String encode(String s) {
    return s.replaceAll("\\\\", "\\\\\\\\")
            .replaceAll("\\\r", "")
            .replaceAll("\\\t", "\\\\t")
            .replaceAll("\\\"", "\\\\\"");
  }

}
