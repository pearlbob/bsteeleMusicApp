package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.util.StringTripleToHtmlTable;
import com.bsteele.bsteeleMusicApp.shared.util.StringTriple;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.logging.Logger;

public class SongReadPopup extends DialogBox {
    public enum SongReadResponseEnum{
        keepTheOld,
        useThenew,
        cancel
    }
    public interface SongReadResponse{
        void songReadResponse(SongReadResponseEnum value);
    }

    public SongReadPopup(SongReadResponse songReadResponse) {
        // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
        // If this is set, the panel closes itself automatically when the user
        // clicks outside of it.
        super(false);
        setModal(true);
        this.songReadResponse = songReadResponse;
    }

    public void processSong(String message, Song oldSong, Song newSong) {
        // PopupPanel is a SimplePanel, so you have to set it's widget property to
        // whatever you want its contents to be.

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Style.Unit.PX);
        dockLayoutPanel.addNorth(new Label(message), 50);
        setWidget(dockLayoutPanel);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(new Label(message));
        vPanel.add(new Label("\n"));
        HTMLPanel htmlPanel = new HTMLPanel(StringTripleToHtmlTable.toHtmlTable(
                new StringTriple("Value", "Existing: " + oldSong.getFileName(), "Read: " + newSong.getFileName()),
                Song.diff(oldSong, newSong)));
        vPanel.add(htmlPanel);

        {
            HorizontalPanel horizontalPanel = new HorizontalPanel();
            horizontalPanel.add(new Button("Keep existing version", (ClickHandler) event -> {
                logger.info("Keep existing: " + newSong.getTitle());
                hide();
                songReadResponse.songReadResponse(SongReadResponseEnum.keepTheOld);
            }));
            horizontalPanel.add(new Button("Use the newly read version", (ClickHandler) event -> {
                try {
                    logger.info("fired: " + newSong.getTitle());
                    hide();
                    songReadResponse.songReadResponse(SongReadResponseEnum.useThenew);
                } catch (Exception ex) {
                    logger.info("Exception on song submission: " + ex.getMessage());
                    hide();
                }
            }));
            horizontalPanel.add(new Button("Cancel", (ClickHandler) event -> {
                logger.info("Cancel: " + newSong.getTitle());
                hide();
                songReadResponse.songReadResponse(SongReadResponseEnum.cancel);
            }));
            vPanel.add(horizontalPanel);
        }
        vPanel.setWidth("1000px");
        setWidget(vPanel);
        show();
    }

    private final SongReadResponse songReadResponse;
    private static final Logger logger = Logger.getLogger(SongReadPopup.class.getName());
}
