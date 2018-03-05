package com.bsteele.bsteeleMusicApp.client;

import com.bsteele.bsteeleMusicApp.client.resources.AppResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
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

    AppResources.INSTANCE.style().ensureInjected();

    RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();

    DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);

    dockLayoutPanel.addNorth(new HTML("<h1>\n"
            + "<img src=\"images/runningsmall.ico\" alt=\"Bob Steele jpg\" border=\"0\"\n"
            + "height=\"28\" width=\"28\"/>\n"
            + "bsteele Music App</h1>"), 35);
    {

      // Create a tab panel
      TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
      tabPanel.setAnimationDuration(150);
      tabPanel.getElement().getStyle().setMarginBottom(10.0, Unit.PX);

//      HTML homeText = new HTML("Songs go here");
//      tabPanel.add(homeText, "Songs");
//    // Add a tab with an image
//    SimplePanel imageContainer = new SimplePanel();
//    imageContainer.setWidget(new Image(Showcase.images.gwtLogo()));
//    tabPanel.add(imageContainer, tabTitles[1]);
      // Add a chord and Lyrics tab
      {
        SplitLayoutPanel p = new SplitLayoutPanel();
        p.addNorth(new HTML("header"), 100);
        //p.addSouth(new HTML("footer fsdfg"), 50);
        p.addWest(new HTML("chords"), 400);
        p.add(new HTML("lyrics"));
        tabPanel.add(p, "Chords and lyrics");
      }

//      {
//        FlowPanel fp = new FlowPanel();
//        fp.add(new HTML("<p><a href=\"./lyrics.html\">Lyrics</a></p>"));
//        fp.add(new HTML("<p><a href=\"./bassStudyTool.html\">Bob's Bass Study Tool</a></p>"));
//        fp.add(new HTML("<table>\n"
//                + "<tr> <td><button type=\"button\" id=\"tapButton\" onclick=\"onNativeTap(event);\" >Tap for BPM</button></td> <td id=\"bpmTally\">106</td> </tr>\n"
//                + "<tr> <td>websocket response:</td> <td id=\"websocketResponse\"></td>  </tr>\n"
//                + "</table>\n"));
      String url = getWebSocketURL();
      if (!jettyRegex.test(url)) {
        socket = new WebSocket(url);
        socket.onmessage = new SocketReceiveFunction();

//          // Use Widget API to Create a <paper-button>
//          PaperButton button = new PaperButton("Test Websocket");
//          button.setRaised(true);
//          if (socket != null) {
//            button.addClickHandler((ClickEvent event) -> {
//              String msg = "hello bob";
//              socket.send("hello bob");
//              GWT.log("message sent:" + msg);
//            });
//          }
//          fp.add(button);
//
      }
//  else {
//          //  no websocket!
//          //websocketMessagePanel.clear();
//          // websocketMessagePanel.add(new HTMLPanel("no websocket on jetty that i know of"));
//        }
//        tabPanel.add(fp, "Options");
//      }

      // Return the content
      tabPanel.selectTab(0);
      tabPanel.ensureDebugId("mainTabPanel");

      dockLayoutPanel.add(tabPanel);
      // dockLayoutPanel.setHeight("0%");

      rootLayoutPanel.add(dockLayoutPanel);
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
      GWT.log("message recv: " + me.data);
//      websocketMessagePanel.clear();
//      websocketMessagePanel.add(new HTMLPanel(me.data));
      return event;
    }
  }

  @JsType(isNative = true, name = "Object", namespace = GLOBAL)
  static class OnMessageEevent {

    public String data;
  }

  public static boolean sendMessage(String message) {
    if (socket == null) {
      return false;
    }

    socket.send(message);
    return true;
  }
  
  private static final RegExp jettyRegex = RegExp.compile("8888");
  private static WebSocket socket;
}
