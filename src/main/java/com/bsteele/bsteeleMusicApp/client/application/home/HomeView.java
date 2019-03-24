package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.application.events.AllSongWriteEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.AllSongWriteEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.HomeTabEventHandler;
import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.GenerateSongHtml;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
    DockLayoutPanel bass;

    @UiField
    DockLayoutPanel singer;

    @UiField
    SimplePanel songEdit;

    @UiField
    SimplePanel drumOptions;

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
    Label allSongId;

    @UiField
    Button writeAllSongs;

    @Inject
    HomeView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));

        bindSlot(HomePresenter.SLOT_SONGLIST_CONTENT, songList);
        bindSlot(HomePresenter.SLOT_LYRICS_AND_CHORDS_CONTENT, lyricsAndChords);
        bindSlot(HomePresenter.SLOT_PLAYER_CONTENT, player);
        bindSlot(HomePresenter.SLOT_BASS_CONTENT, bass);
        bindSlot(HomePresenter.SLOT_SINGER_CONTENT, singer);
        bindSlot(HomePresenter.SLOT_SONG_EDIT_CONTENT, songEdit);
        bindSlot(HomePresenter.SLOT_DRUM_OPTIONS_CONTENT, drumOptions);

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
                case bass:
                case singer:
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
                            case bass:
                            case singer:
                                homeTabs.selectTab(appTab.ordinal()); // fixme: very weak tab selection mechanism!
                                break;
                        }
                    } catch (Exception ex) {
                    }
            }
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

    private HashMap<String, String> statusMap = new HashMap<>();
    private AppTab lastPlayTab = AppTab.player;
}
