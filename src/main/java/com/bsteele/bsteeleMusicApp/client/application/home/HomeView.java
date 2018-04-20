package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.application.events.StatusEvent;
import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.GenerateSongHtml;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.TreeSet;

public class HomeView extends ViewImpl implements HomePresenter.MyView {

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
    Button showStatus;
    @UiField
    HTML allStatus;

    @UiField
    Label buildId;

    @Inject
    HomeView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));

        bindSlot(HomePresenter.SLOT_SONGLIST_CONTENT, songList);
        bindSlot(HomePresenter.SLOT_LYRICSANDCHORDS_CONTENT, lyricsAndChords);
        bindSlot(HomePresenter.SLOT_PLAYER_CONTENT, player);
        bindSlot(HomePresenter.SLOT_SONGEDIT_CONTENT, songEdit);
        bindSlot(HomePresenter.SLOT_DRUMOPTIONS_CONTENT, drumOptions);

        allScales.setVisible(false);
        showAllScales.addClickHandler((ClickEvent event) -> {
            allScales.setVisible(!allScales.isVisible());
            if (allScales.isVisible()) {
                GenerateSongHtml generateAllChordHtml = new GenerateSongHtml();
                allScales.setHTML(generateAllChordHtml.generateAllScalesHtml());
            }
        });
        allChords.setVisible(false);
        showAllChords.addClickHandler((ClickEvent event) -> {
            allChords.setVisible(!allChords.isVisible());
            if (allChords.isVisible()) {
                GenerateSongHtml generateAllChordHtml = new GenerateSongHtml();
                allChords.setHTML(generateAllChordHtml.generateAllChordHtml());
            }
        });

        allStatus.setVisible(false);
        showStatus.addClickHandler((ClickEvent event) -> {
            allStatus.setVisible(!allStatus.isVisible());
            if (allStatus.isVisible()) {
                allStatus.setHTML(generateStatusHtml());
            }
        });

        buildId.setText(AppResources.INSTANCE.buildId().getText());
    }

    @Override
    public void selectTab(String tabName) {
        homeTabs.selectTab(1);    //  fixme
    }

    @Override
    public void onStatusEvent(StatusEvent event) {
        statusMap.put(event.getName(), event.getValue());
        if (allStatus.isVisible()) {
            allStatus.setHTML(generateStatusHtml());
        }
        GWT.log(event.getName()+": "+ event.getValue());
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

    private HashMap<String, String> statusMap = new HashMap<>();
}
