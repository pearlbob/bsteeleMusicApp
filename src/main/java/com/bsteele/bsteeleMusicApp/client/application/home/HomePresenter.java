package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.application.ApplicationPresenter;
import com.bsteele.bsteeleMusicApp.client.application.LoggedInGatekeeper;
import com.bsteele.bsteeleMusicApp.client.application.songs.SongSelectionEvent;
import com.bsteele.bsteeleMusicApp.client.application.songs.SongSelectionEventHandler;
import com.bsteele.bsteeleMusicApp.client.place.NameTokens;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.LyricsAndChordsPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SongEditPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SongListPresenterWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

/*  todo
datagrid for songs
get focus on songlist search, keep it there

 */
/**
 *
 * @author bob
 */
public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy>
        implements SongSelectionEventHandler {

  interface MyView extends View {

    void selectTab(String tabName);
  }

  public static final SingleSlot SLOT_SONGLIST_CONTENT = new SingleSlot();
  public static final SingleSlot SLOT_LYRICSANDCHORDS_CONTENT = new SingleSlot();
  public static final SingleSlot SLOT_SONGEDIT_CONTENT = new SingleSlot();

  private final SongListPresenterWidget songListPresenterWidget;
  private final LyricsAndChordsPresenterWidget lyricsAndChordsPresenterWidget;
  private final SongEditPresenterWidget songEditPresenterWidget;

  @ProxyStandard
  @NameToken(NameTokens.HOME)
  @UseGatekeeper(LoggedInGatekeeper.class)
  interface MyProxy extends ProxyPlace<HomePresenter> {
  }

  @Inject
  HomePresenter(
          EventBus eventBus,
          MyView view,
          MyProxy proxy,
          SongListPresenterWidget songListPresenterWidget,
          LyricsAndChordsPresenterWidget lyricsAndChordsPresenterWidget,
          SongEditPresenterWidget songEditPresenterWidget
          ) {
    super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

    this.eventBus = eventBus;
    this.songListPresenterWidget = songListPresenterWidget;
    this.lyricsAndChordsPresenterWidget = lyricsAndChordsPresenterWidget;
    this.songEditPresenterWidget = songEditPresenterWidget;
  }

  @Override
  protected void onBind() {
    setInSlot(SLOT_SONGLIST_CONTENT, songListPresenterWidget);
    setInSlot(SLOT_LYRICSANDCHORDS_CONTENT, lyricsAndChordsPresenterWidget);
    setInSlot(SLOT_SONGEDIT_CONTENT, songEditPresenterWidget);

    eventBus.addHandler(SongSelectionEvent.TYPE, this);
  }

  @Override
  public void onSongSelection(SongSelectionEvent event) {
    getView().selectTab("lyricsAndChordsTab");
  }

  private final EventBus eventBus;

}
