package com.bsteele.bsteeleMusicApp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.vaadin.polymer.paper.widget.PaperButton;
import static jsinterop.annotations.JsPackage.GLOBAL;
import jsinterop.annotations.JsType;

/**
 *
 */
public class BSteeleMusicApp implements EntryPoint {

  /**
   *
   */
  @Override
  public void onModuleLoad() {

    RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();

    DockLayoutPanel outerdockLayoutPanel = new DockLayoutPanel(Unit.PX );

    outerdockLayoutPanel.addNorth(new HTML("bsteele Music App"), 50);
    {

      // Create a tab panel
      TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
      tabPanel.setAnimationDuration(150);
      tabPanel.getElement().getStyle().setMarginBottom(10.0, Unit.PX);

      HTML homeText = new HTML("Songs go here");
      tabPanel.add(homeText, "Songs");

//    // Add a tab with an image
//    SimplePanel imageContainer = new SimplePanel();
//    imageContainer.setWidget(new Image(Showcase.images.gwtLogo()));
//    tabPanel.add(imageContainer, tabTitles[1]);
      // Add a chord and Lyrics tab
      {
        SplitLayoutPanel p = new SplitLayoutPanel();
        p.addNorth(new HTML("header"), 100);
        p.addSouth(new HTML("footer fsdfg"), 50);
        p.addWest(new HTML("chords"), 400);
        p.add(new HTML("lyrics"));
        tabPanel.add(p, "Chords and lyrics");
      }

      {
        FlowPanel fp = new FlowPanel();
        fp.add(new HTML("<p><a href=\"./lyrics.html\">Lyrics</a></p>"));
      fp.add(new HTML("<p><a href=\"./bassStudyTool.html\">Bob's Bass Study Tool</a></p>"));
        fp.add(new HTML("<table>\n"
                + "<tr> <td><button type=\"button\" id=\"tapButton\" onclick=\"onNativeTap(event);\" >Tap for BPM</button></td> <td id=\"bpmTally\">106</td> </tr>\n"
                + "<tr> <td>websocket response:</td> <td id=\"websocketResponse\"></td>  </tr>\n"
                + "</table>\n"));
        String url = getWebSocketURL();
        if (!jettyRegex.test(url)) {
          socket = new WebSocket(url);
          socket.onmessage = new SocketReceiveFunction();

          // Use Widget API to Create a <paper-button>
          PaperButton button = new PaperButton("Test Websocket");
          button.setRaised(true);
          if (socket != null) {
            button.addClickHandler((ClickEvent event) -> {
              String msg = "hello bob";
              socket.send("hello bob");
              GWT.log("message sent:" + msg);
            });
          }
          fp.add(button);
          tabPanel.add(fp, "Options");
        } else {
          //  no websocket!
          //websocketMessagePanel.clear();
          // websocketMessagePanel.add(new HTMLPanel("no websocket on jetty that i know of"));
        }

      }

      // Return the content
      tabPanel.selectTab(0);
      tabPanel.ensureDebugId("mainTabPanel");

      outerdockLayoutPanel.add(tabPanel);
     // dockLayoutPanel.setHeight("0%");

      rootLayoutPanel.add(outerdockLayoutPanel);
      rootLayoutPanel.forceLayout();

    }
    //websocketMessagePanel = RootPanel.get("websocketResponse");
  }

  private String getWebSocketURL() {
    final String moduleBaseURL = GWT.getHostPageBaseURL();
    return moduleBaseURL.replaceFirst("^http\\:", "ws:") + "bsteeleMusic";
  }

  private final class SocketReceiveFunction implements Function {

    @Override
    public Object call(Object event) {

      OnMessageEevent me = (OnMessageEevent) event;
      GWT.log("message recv:");
      websocketMessagePanel.clear();
      websocketMessagePanel.add(new HTMLPanel(me.data));
      return event;
    }
  }

  @JsType(isNative = true, name = "Object", namespace = GLOBAL)
  static class OnMessageEevent {

    public String data;
  }

  private static final RegExp jettyRegex = RegExp.compile("8888");
  private WebSocket socket;

  private RootPanel websocketMessagePanel;
}
