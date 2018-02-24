package com.bsteele.bsteeleMusicApp.client.application.songs;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import javax.inject.Inject;


public class SongsView extends ViewImpl implements SongsPresenter.MyView {

  interface Binder extends UiBinder<Widget, SongsView> {
  }

  @Inject
  SongsView(Binder uiBinder) {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
