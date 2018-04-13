package com.bsteele.bsteeleMusicApp.client.application.home;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.bsteele.bsteeleMusicApp.client.songs.GenerateSongHtml;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.gwtplatform.mvp.client.ViewImpl;

import javax.inject.Inject;

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
    SimplePanel songEdit;

    @UiField
    SimplePanel drumOptions;

    @UiField Button showAllScales;
    @UiField HTML allScales;
    @UiField Button showAllChords;
    @UiField HTML allChords;

    @UiField
    Label buildId;

    @Inject
    HomeView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));

        bindSlot(HomePresenter.SLOT_SONGLIST_CONTENT, songList);
        bindSlot(HomePresenter.SLOT_LYRICSANDCHORDS_CONTENT, lyricsAndChords);
        bindSlot(HomePresenter.SLOT_SONGEDIT_CONTENT, songEdit);
        bindSlot(HomePresenter.SLOT_DRUMOPTIONS_CONTENT, drumOptions);

        allScales.setVisible(false);
        showAllScales.addClickHandler((ClickEvent event) -> {
            allScales.setVisible(!allScales.isVisible());
                 if ( allScales.isVisible()){
                     GenerateSongHtml generateAllChordHtml = new GenerateSongHtml();
                     allScales.setHTML(generateAllChordHtml.generateAllScalesHtml());
                 }
        });
        allChords.setVisible(false);
        showAllChords.addClickHandler((ClickEvent event) -> {
            allChords.setVisible(!allChords.isVisible());
            if ( allChords.isVisible()){
                GenerateSongHtml generateAllChordHtml = new GenerateSongHtml();
                allChords.setHTML(generateAllChordHtml.generateAllChordHtml());
            }
        });

        buildId.setText(AppResources.INSTANCE.buildId().getText());
    }

    @Override
    public void selectTab(String tabName) {
        homeTabs.selectTab(1);    //  fixme
    }
}
