package com.bsteele.bsteeleMusicApp.client.application;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.proxy.Proxy;

public class ApplicationPresenter
        extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy> {

  interface MyView extends View {
  }

  @ProxyStandard
  @UseGatekeeper(LoggedInGatekeeper.class)
  interface MyProxy extends Proxy<ApplicationPresenter> {
  }

  public static final NestedSlot SLOT_MAIN = new NestedSlot();
  
  @Inject
  ApplicationPresenter(
          EventBus eventBus,
          MyView view,
          MyProxy proxy) {
    super(eventBus, view, proxy, RevealType.Root);
  }
}

/*
todo: activate websockets

todo: event plot
todo: audio call out of chord changes
todo: audio call out of section changes
todo: audio count in, missing last beat or two
todo: mp3 recording and playback (including start offsets, non-integer bpm)
todo: align tabs to enum
todo: tooltips, many tooltips
todo: metronome clicks

todo: pop the colors on beat transitions
todo: fix ctl-key on mac

todo: control s to save when editing a song

todo: fix multiple section id's on a single chord section with vertical span

todo: add lastModified to song metadata on file read, add size?
    file location as well
todo: markup language aids (buttons)
todo: search for text in song

todo: select start by sections
todo: loop by section
todo: to dynamically change font size for chords to fit
todo: set an indicator for added songs, font color change?

todo: improve leadin count down

todo: comments in chords?

todo: optionChoicesDiv with a css class


todo: accelerated sub-beat indicator
todo: hash by song name not list index

todo: section alterations imply: ch1 ch2 etc
todo: Beat vs measure resolution on a section basis

*/