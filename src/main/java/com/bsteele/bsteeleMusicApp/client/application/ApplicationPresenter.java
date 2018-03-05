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

/*  todo
activate websockets

event plot
audio call out of chord changes
audio call out of section changes
audio count in, missing last beat or two
mp3 recording and playback (including start offsets, non-integer bpm)
align tabs to enum
tooltips, many tooltips
metronome clicks

pop the colors on beat transitions
fix ctl-key on mac

control s to save when editing a song

fix multiple section id's on a single chord section with vertical span

add lastModified to song metadata on file read, add size?
    file location as well
markup language aids (buttons)
search for text in song

select start by sections
loop by section
to dynamically change font size for chords to fit
set an indicator for added songs, font color change? 

improve leadin count down

comments in chords?

fix: optionChoicesDiv with a css class


accelerated sub-beat indicator
hash by song name not list index



section alterations imply: ch1 ch2 etc
Beat vs measure resolution on a section basis 

*/