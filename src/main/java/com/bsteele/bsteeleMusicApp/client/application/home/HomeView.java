package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.SongPlayMaster;
import com.bsteele.bsteeleMusicApp.client.application.GWTAppOptions;
import com.bsteele.bsteeleMusicApp.client.application.events.AllSongWriteEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.AllSongWriteEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.GenerateSongHtml;
import com.bsteele.bsteeleMusicApp.shared.songs.AppOptions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.dom.client.Document;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

public class HomeView extends ViewImpl implements HomePresenter.MyView,
        HasHandlers {


    interface Binder extends UiBinder<Widget, HomeView> {
    }

    @UiField
    TabLayoutPanel homeTabs;

    @UiField
    SimpleLayoutPanel songList;

    @UiField
    SplitLayoutPanel lyricsAndChords;

    @UiField
    DockLayoutPanel player;


    @UiField
    SimplePanel songEdit;

//    @UiField
//    DockLayoutPanel bass;
//
//    @UiField
//    DockLayoutPanel singer;
//
//
//    @UiField
//    SimplePanel drumOptions;

    //    @UiField
//    CheckBox countIn;
    @UiField
    CheckBox dashAllMeasureRepetitions;
//    @UiField
//    CheckBox playWithLineIndicator;
//    @UiField
//    CheckBox playWithMeasureIndicator;
//    @UiField
//    CheckBox playWithBouncingBall;
//    @UiField
//    CheckBox playWithMeasureLabel;

    @UiField
    SelectElement keyOffsetSelect;

    @UiField
    CheckBox alwaysUseTheNewestSongOnReadLabel;

    @UiField
    CheckBox debug;

    @UiField
    Button showAllScales;
    @UiField
    HTML allScales;
    @UiField
    Button showAllChords;
    @UiField
    HTML allChords;
    @UiField
    Button showAllTonics;
    @UiField
    HTML allTonics;
    @UiField
    Button showFileFormat;
    @UiField
    HTML fileFormat;

    @UiField
    Button showStatus;
    @UiField
    HTML allStatus;

    @UiField
    Label buildId;

    @UiField
    Button writeAllSongs;

    @UiField
    Button claimRemoteLeadership;

    @UiField
    Button fullscreen;

    @Inject
    HomeView(Binder uiBinder, SongPlayMaster songPlayMaster) {
        initWidget(uiBinder.createAndBindUi(this));

        this.songPlayMaster = songPlayMaster;

        bindSlot(HomePresenter.SLOT_SONGLIST_CONTENT, songList);
        bindSlot(HomePresenter.SLOT_LYRICS_AND_CHORDS_CONTENT, lyricsAndChords);
        bindSlot(HomePresenter.SLOT_PLAYER_CONTENT, player);
        bindSlot(HomePresenter.SLOT_SONG_EDIT_CONTENT, songEdit);
//        bindSlot(HomePresenter.SLOT_BASS_CONTENT, bass);
//        bindSlot(HomePresenter.SLOT_SINGER_CONTENT, singer);
//        bindSlot(HomePresenter.SLOT_DRUM_OPTIONS_CONTENT, drumOptions);

        handlerManager = new HandlerManager(this);

        allScales.setVisible(false);
        showAllScales.addClickHandler((ClickEvent event) -> {
            allScales.setVisible(!allScales.isVisible());
            if (allScales.isVisible()) {
                GenerateSongHtml generateSongHtml = new GenerateSongHtml();
                allScales.setHTML(generateSongHtml.generateAllScalesHtml());
            }
        });
        allChords.setVisible(false);
        showAllChords.addClickHandler((ClickEvent event) -> {
            allChords.setVisible(!allChords.isVisible());
            if (allChords.isVisible()) {
                GenerateSongHtml generateSongHtml = new GenerateSongHtml();
                allChords.setHTML(generateSongHtml.generateAllChordHtml());
            }
        });

        allTonics.setVisible(false);
        showAllTonics.addClickHandler((ClickEvent event) -> {
            allTonics.setVisible(!allTonics.isVisible());
            if (allTonics.isVisible()) {
                GenerateSongHtml generateSongHtml = new GenerateSongHtml();
                allTonics.setHTML(generateSongHtml.generateAllTonicHtml());
            }
        });

        fileFormat.setVisible(false);
        showFileFormat.addClickHandler((ClickEvent event) -> {
            fileFormat.setVisible(!fileFormat.isVisible());
            if (fileFormat.isVisible()) {
                GenerateSongHtml generateSongHtml = new GenerateSongHtml();
                fileFormat.setHTML(generateSongHtml.generateFileFormat());
            }
        });

        allStatus.setVisible(false);
        showStatus.addClickHandler((ClickEvent event) -> {
            allStatus.setVisible(!allStatus.isVisible());
            if (allStatus.isVisible()) {
                allStatus.setHTML(generateStatusHtml());
            }
        });

        homeTabs.addSelectionHandler(selectionEvent -> {
            AppTab tab = AppTab.values()[homeTabs.getSelectedIndex()];
            switch (tab) {           // fixme: very weak tab selection mechanism!
                case lyricsAndChords:
                case player:     //  fixme: edit tab for now
//                case bass:
//                case singer:
                    lastPlayTab = tab;
                    break;
            }
            fireEvent(new HomeTabEvent(tab));
        });

//        // Listen for keyboard events
//        homeTabs.addKeyDownHandler(new KeyDownHandler() {
//            public void onKeyDown(KeyDownEvent event) {
//                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
//                    addStock();
//                }
//            }
//        });

        writeAllSongs.addClickHandler((ClickEvent event) -> {
            fireEvent(new AllSongWriteEvent());
        });

        claimRemoteLeadership.addClickHandler((ClickEvent event) -> {
            if (songPlayMaster.isConnectedWithServer()) {
                songPlayMaster.setLeader(!songPlayMaster.isLeader());
            }
            claimRemoteLeadershipRefresh();
        });

        fullscreen.addClickHandler((ClickEvent event) -> {
            requestFullscreen();
        });

        buildId.setText(AppResources.INSTANCE.buildId().getText());

        {
            final Map<String, List<String>> parameterMap = Window.Location.getParameterMap();
            if (parameterMap != null) {
                List<String> list = parameterMap.get("open");
                if (list != null && list.size() > 0)
                    try {
                        AppTab appTab = AppTab.valueOf(list.get(0));
                        switch (appTab) {
                            case player:
                            case lyricsAndChords:
//                            case bass:
//                            case singer:
                                homeTabs.selectTab(appTab.ordinal()); // fixme: very weak tab selection mechanism!
                                break;
                        }
                    } catch (Exception ex) {
                    }
            }
        }

//        countIn.setValue(appOptions.isCountIn());
//        countIn.addClickHandler((ClickEvent e) -> {
//            appOptions.setCountIn(!appOptions.isCountIn());
//            countIn.setValue(appOptions.isCountIn());
//        });
//        dashAllMeasureRepetitions.setValue(appOptions.isDashAllMeasureRepetitions());
//        dashAllMeasureRepetitions.addClickHandler((ClickEvent e) -> {
//            appOptions.setDashAllMeasureRepetitions(!appOptions.isDashAllMeasureRepetitions());
//            dashAllMeasureRepetitions.setValue(appOptions.isDashAllMeasureRepetitions());
//        });
//        playWithLineIndicator.setValue(appOptions.isPlayWithLineIndicator());
//        playWithLineIndicator.addClickHandler((ClickEvent e) -> {
//            appOptions.setPlayWithLineIndicator(!appOptions.isPlayWithLineIndicator());
//            playWithLineIndicator.setValue(appOptions.isPlayWithLineIndicator());
//        });
//        playWithMeasureIndicator.setValue(appOptions.isPlayWithMeasureIndicator());
//        playWithMeasureIndicator.addClickHandler((ClickEvent e) -> {
//            appOptions.setPlayWithMeasureIndicator(!appOptions.isPlayWithMeasureIndicator());
//            playWithMeasureIndicator.setValue(appOptions.isPlayWithMeasureIndicator());
//        });
//        playWithBouncingBall.setValue(appOptions.isPlayWithBouncingBall());
//        playWithBouncingBall.addClickHandler((ClickEvent e) -> {
//            appOptions.setPlayWithBouncingBall(!appOptions.isPlayWithBouncingBall());
//            playWithBouncingBall.setValue(appOptions.isPlayWithBouncingBall());
//        });
//        playWithMeasureLabel.setValue(appOptions.isPlayWithMeasureLabel());
//        playWithMeasureLabel.addClickHandler((ClickEvent e) -> {
//            appOptions.setPlayWithMeasureLabel(!appOptions.isPlayWithMeasureLabel());
//            playWithMeasureLabel.setValue(appOptions.isPlayWithMeasureLabel());
//        });

        Event.sinkEvents(keyOffsetSelect, Event.ONCHANGE);
        Event.setEventListener(keyOffsetSelect, (Event event) -> {
            if (Event.ONCHANGE == event.getTypeInt()) {
                logger.info("keyOffset: " + keyOffsetSelect.getValue());
                appOptions.setKeyOffeset(Integer.parseInt(keyOffsetSelect.getValue()));
            }
        });

        alwaysUseTheNewestSongOnReadLabel.setValue(appOptions.isAlwaysUseTheNewestSongOnRead());
        alwaysUseTheNewestSongOnReadLabel.addClickHandler((ClickEvent e) -> {
            appOptions.setAlwaysUseTheNewestSongOnRead(!appOptions.isAlwaysUseTheNewestSongOnRead());
            alwaysUseTheNewestSongOnReadLabel.setValue(appOptions.isAlwaysUseTheNewestSongOnRead());
        });
        debug.setValue(appOptions.isDebug());
        debug.addClickHandler((ClickEvent e) -> {
            GWTAppOptions.getInstance().setDebug(!appOptions.isDebug());
            debug.setValue(appOptions.isDebug());
        });

        //  hide debug if not local
        if (GWT.getHostPageBaseURL().matches(".*127.0.0.1:8888"))
            debug.setVisible(false);
    }

    private native void requestFullscreen() /*-{
        var element = $doc.documentElement;

        if (element.requestFullscreen) {
            element.requestFullscreen();
        } else if (element.mozRequestFullScreen) {
            element.mozRequestFullScreen();
        } else if (element.webkitRequestFullscreen) {
            element.webkitRequestFullscreen();
        } else if (element.msRequestFullscreen) {
            element.msRequestFullscreen();
        }
    }-*/;

    private void claimRemoteLeadershipRefresh() {
        if (songPlayMaster.isConnectedWithServer()) {
            claimRemoteLeadership.setText(songPlayMaster.isLeader() ? "Abdicate my leadership" : "Make me the leader");
        } else {
            claimRemoteLeadership.setText("Server not found");
        }
    }

    @Override
    public void selectLastPlayTab() {
        AppTab tab = AppTab.values()[homeTabs.getSelectedIndex()];
        switch (tab) {
            case edit:  //  stay on edit if already there
                break;
            default:
                if (tab != lastPlayTab)
                    homeTabs.selectTab(lastPlayTab.ordinal());
                break;
        }

    }

    @Override
    public void onStatusEvent(StatusEvent event) {
        statusMap.put(event.getName(), event.getValue());
        if (allStatus.isVisible()) {
            allStatus.setHTML(generateStatusHtml());
        }
        //GWT.log(event.toString());
    }

    @Override
    public HandlerRegistration addSongReadEventHandler(AllSongWriteEventHandler handler) {
        return handlerManager.addHandler(AllSongWriteEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addHomeTabEventHandler(HomeTabEventHandler handler) {
        return handlerManager.addHandler(HomeTabEvent.TYPE, handler);
    }

    private String generateStatusHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table><tr><th>Name</th><th>Value</th></tr>");
        TreeSet<String> sortedKeys = new TreeSet<>();
        sortedKeys.addAll(statusMap.keySet());
        for (String name : sortedKeys) {
            sb.append("<tr><td>").append(name).append(":</td><td>").append(statusMap.get(name)).append("</td>\n");
        }
        sb.append("</table>");
        return sb.toString();
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    private final HandlerManager handlerManager;
    protected final SongPlayMaster songPlayMaster;

    private HashMap<String, String> statusMap = new HashMap<>();
    private AppTab lastPlayTab = AppTab.player;
    private static final AppOptions appOptions = GWTAppOptions.getInstance();
    private static final Logger logger = Logger.getLogger(HomeView.class.getName());
}
