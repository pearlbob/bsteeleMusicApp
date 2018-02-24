/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author bob
 */
public class AppTitleWidget extends Composite {
  interface MyUiBinder extends UiBinder<Widget, AppTitleWidget> {}
  private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  public AppTitleWidget() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
}
