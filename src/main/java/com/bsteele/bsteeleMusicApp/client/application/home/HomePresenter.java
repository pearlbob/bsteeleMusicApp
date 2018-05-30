package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.application.ApplicationPresenter;
import com.bsteele.bsteeleMusicApp.client.application.BSteeleMusicIO;
import com.bsteele.bsteeleMusicApp.client.application.LoggedInGatekeeper;
import com.bsteele.bsteeleMusicApp.client.application.events.AllSongWriteEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.AllSongWriteEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongUpdateEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEventHandler;
import com.bsteele.bsteeleMusicApp.client.place.NameTokens;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.BassPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.DrumOptionsPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.LyricsAndChordsPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.PlayerPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SingerPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SongEditPresenterWidget;
import com.bsteele.bsteeleMusicApp.client.presenterWidgets.SongListPresenterWidget;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.presenter.slots.SingleSlot;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

/**
 * @author bob
 */
public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy>
        implements SongUpdateEventHandler, SongSubmissionEventHandler, AllSongWriteEventHandler,
        StatusEventHandler {

    interface MyView extends View {

        void selectLastPlayTab();

        void onStatusEvent(StatusEvent event);

        HandlerRegistration addSongReadEventHandler(AllSongWriteEventHandler handler);
    }

    public static final SingleSlot<SongListPresenterWidget> SLOT_SONGLIST_CONTENT = new SingleSlot<>();
    public static final SingleSlot<LyricsAndChordsPresenterWidget> SLOT_LYRICS_AND_CHORDS_CONTENT = new SingleSlot<>();
    public static final SingleSlot<PlayerPresenterWidget> SLOT_PLAYER_CONTENT = new SingleSlot<>();
    public static final SingleSlot<BassPresenterWidget> SLOT_BASS_CONTENT = new SingleSlot<>();
    public static final SingleSlot<SingerPresenterWidget> SLOT_SINGER_CONTENT = new SingleSlot<>();
    public static final SingleSlot<SongEditPresenterWidget> SLOT_SONG_EDIT_CONTENT = new SingleSlot<>();
    public static final SingleSlot<DrumOptionsPresenterWidget> SLOT_DRUM_OPTIONS_CONTENT = new SingleSlot<>();


    private final SongListPresenterWidget songListPresenterWidget;
    private final LyricsAndChordsPresenterWidget lyricsAndChordsPresenterWidget;
    private final PlayerPresenterWidget playerPresenterWidget;
    private final BassPresenterWidget bassPresenterWidget;
    private final SingerPresenterWidget singerPresenterWidget;
    private final SongEditPresenterWidget songEditPresenterWidget;
    private final DrumOptionsPresenterWidget drumOptionsPresenterWidget;

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
            PlayerPresenterWidget playerPresenterWidget,
            BassPresenterWidget bassPresenterWidget,
            SingerPresenterWidget singerPresenterWidget,
            SongEditPresenterWidget songEditPresenterWidget,
            DrumOptionsPresenterWidget drumOptionsPresenterWidget,
            BSteeleMusicIO bSteeleMusicIO
    ) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

        this.eventBus = eventBus;
        this.songListPresenterWidget = songListPresenterWidget;
        this.lyricsAndChordsPresenterWidget = lyricsAndChordsPresenterWidget;
        this.playerPresenterWidget = playerPresenterWidget;
        this.bassPresenterWidget = bassPresenterWidget;
        this.singerPresenterWidget = singerPresenterWidget;
        this.songEditPresenterWidget = songEditPresenterWidget;
        this.drumOptionsPresenterWidget = drumOptionsPresenterWidget;


        view.addSongReadEventHandler(this);

        this.bSteeleMusicIO = bSteeleMusicIO; //  fixme: do this better: force the presence of the singleton
    }

    @Override
    protected void onBind() {
        setInSlot(SLOT_SONGLIST_CONTENT, songListPresenterWidget);
        setInSlot(SLOT_LYRICS_AND_CHORDS_CONTENT, lyricsAndChordsPresenterWidget);
        setInSlot(SLOT_PLAYER_CONTENT, playerPresenterWidget);
        setInSlot(SLOT_BASS_CONTENT, bassPresenterWidget);
        setInSlot(SLOT_SINGER_CONTENT, singerPresenterWidget);
        setInSlot(SLOT_SONG_EDIT_CONTENT, songEditPresenterWidget);
        setInSlot(SLOT_DRUM_OPTIONS_CONTENT, drumOptionsPresenterWidget);

        eventBus.addHandler(SongUpdateEvent.TYPE, this);
        eventBus.addHandler(SongSubmissionEvent.TYPE, this);
        eventBus.addHandler(StatusEvent.TYPE, this);
    }


    @Override
    public void onSongUpdate(SongUpdateEvent event) {
        getView().selectLastPlayTab();
    }


    @Override
    public void onSongSubmission(SongSubmissionEvent event) {
        getView().selectLastPlayTab();
    }


    @Override
    public void onStatusEvent(StatusEvent event) {
        getView().onStatusEvent(event);
    }

    @Override
    public void onAllSongWrite(AllSongWriteEvent event) {
        eventBus.fireEvent(event);
    }

    private final EventBus eventBus;
    private final BSteeleMusicIO bSteeleMusicIO;
}
