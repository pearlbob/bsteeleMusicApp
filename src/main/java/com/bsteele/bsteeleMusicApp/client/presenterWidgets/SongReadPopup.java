package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEvent;
import com.bsteele.bsteeleMusicApp.client.application.events.SongSubmissionEventHandler;
import com.bsteele.bsteeleMusicApp.client.songs.Song;
import com.bsteele.bsteeleMusicApp.client.util.StringTripleToHtmlTable;
import com.bsteele.bsteeleMusicApp.shared.util.StringTriple;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.logging.Logger;

public class SongReadPopup extends DialogBox
{

    public SongReadPopup(String message, Song oldSong, Song newSong) {
        // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
        // If this is set, the panel closes itself automatically when the user
        // clicks outside of it.
        super(true);

        // PopupPanel is a SimplePanel, so you have to set it's widget property to
        // whatever you want its contents to be.

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Style.Unit.PX);
        dockLayoutPanel.addNorth(new Label(message),50);
        setWidget(dockLayoutPanel);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(new Label(message));
        vPanel.add(new Label("\n"));
        HTMLPanel htmlPanel = new HTMLPanel(StringTripleToHtmlTable.toHtmlTable(
                new StringTriple("Value", "Existing: "+oldSong.getFileName(), "Read: "+newSong.getFileName()),
                Song.diff(oldSong,newSong)));
        vPanel.add(htmlPanel);

        {
            HorizontalPanel horizontalPanel = new HorizontalPanel();
            horizontalPanel.add(new Button("Keep existing version", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    hide();
                }
            }));
            horizontalPanel.add(new Button("Use the newly read version", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    handlerManager.fireEvent(new SongSubmissionEvent(newSong, false));
                    hide();
                }
            }));
            horizontalPanel.add(new Button("Cancel", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    hide();
                }
            }));
            vPanel.add(horizontalPanel);
        }
        vPanel.setWidth("1000px");
        setWidget(vPanel);
        show();
    }

    public HandlerRegistration SongSubmissionEventHandler(SongSubmissionEventHandler handler) {
      logger.info( "pre handlerManager.getHandlerCount(): "+ handlerManager.getHandlerCount(SongSubmissionEvent.TYPE));
        HandlerRegistration handlerRegistration =  handlerManager.addHandler(SongSubmissionEvent.TYPE, handler);
        logger.info( "post handlerManager.getHandlerCount(): "+ handlerManager.getHandlerCount(SongSubmissionEvent.TYPE));
        return handlerRegistration;
    }

    private final HandlerManager handlerManager = new HandlerManager(this);
    private static final Logger logger = Logger.getLogger(LyricsAndChordsViewImpl.class.getName());
}
