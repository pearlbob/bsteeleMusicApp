/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative=true, namespace=JsPackage.GLOBAL)
public class WebSocket
{
	
	@JsProperty
	public Function onclose;
	
	@JsProperty
	public Function onerror;
	
	@JsProperty
	public Function onmessage;
	
	@JsProperty
	public Function onopen;
	
	@JsProperty
	public String url;
	
	@JsConstructor
	public WebSocket(String url){
	}
	
	@JsMethod
	public native void send(String data);
	
	@JsMethod
	public native void close();
}