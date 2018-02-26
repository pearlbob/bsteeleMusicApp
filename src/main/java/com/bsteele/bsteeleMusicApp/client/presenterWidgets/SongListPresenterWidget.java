/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client.presenterWidgets;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 *
 * @author bob
 */
public class SongListPresenterWidget extends PresenterWidget<SongListPresenterWidget.MyView> {
    public interface MyView extends View 
            //implements DisplayMessageEvent.DisplayMessageHandler
    {
        void displayCurrentUserName(String username);
    }

    //private final CurrentUserService currentUserService;

    @Inject
    SongListPresenterWidget(final EventBus eventBus,
                    final MyView view
            //, CurrentUserService currentUserService
    ) {
        super(eventBus, view);

        //this.currentUserService = currentUserService;
    }
    
//    @Override
//    public void onDisplayMessage(DisplayMessageEvent event) {
//        getView().addMessage(event.getMessage());
//    }

    @Override
    public void onBind() {
      //addRegisteredHandler(DisplayMessageEvent.getType(), this);
        getView().displayCurrentUserName("bob"//currentUserService.getCurrentUsername()
        );
    }
}
