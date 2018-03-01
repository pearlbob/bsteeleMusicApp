package com.bsteele.bsteeleMusicApp.client.application.home;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import javax.inject.Inject;

public class HomeView extends ViewImpl implements HomePresenter.MyView {

  interface Binder extends UiBinder<Widget, HomeView> {
  }

  @UiField
  TabLayoutPanel homeTabs;

  @UiField
  SimplePanel songList;

  @UiField
  SplitLayoutPanel lyricsAndChords;

  @UiField
  SimplePanel songEdit;

  @Inject
  HomeView(Binder uiBinder) {
    initWidget(uiBinder.createAndBindUi(this));

    bindSlot(HomePresenter.SLOT_SONGLIST_CONTENT, songList);
    bindSlot(HomePresenter.SLOT_LYRICSANDCHORDS_CONTENT, lyricsAndChords);
    bindSlot(HomePresenter.SLOT_SONGEDIT_CONTENT, songEdit);
  }

  @Override
  public void selectTab(String tabName) {
    homeTabs.selectTab(1);    //  fixme
  }
}
