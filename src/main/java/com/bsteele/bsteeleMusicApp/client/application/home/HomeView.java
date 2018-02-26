package com.bsteele.bsteeleMusicApp.client.application.home;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import javax.inject.Inject;

public class HomeView extends ViewImpl implements HomePresenter.MyView {

  interface Binder extends UiBinder<Widget, HomeView> {
  }

  @UiField
  SimplePanel songList;

  @Inject
  HomeView(Binder uiBinder) {
    initWidget(uiBinder.createAndBindUi(this));

    bindSlot(HomePresenter.SLOT_SONGLIST_CONTENT, songList);
  }
}
