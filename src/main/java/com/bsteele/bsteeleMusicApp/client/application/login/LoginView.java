
package com.bsteele.bsteeleMusicApp.client.application.login;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import javax.inject.Inject;

public class LoginView extends ViewWithUiHandlers<LoginUiHandlers> implements LoginPresenter.MyView,
        AttachEvent.Handler {


    interface Binder extends UiBinder<Widget, LoginView> {
    }

    @UiField
    Button confirm;

    @UiField
    TextBox username;
    @UiField
    TextBox password;
    @UiField
    Label errorLabel;

    @Inject
    LoginView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));

        username.addAttachHandler(this);

        Storage localStorage = Storage.getLocalStorageIfSupported();

        //localStorage.removeItem("username");  //  testing only

        String name = localStorage.getItem("username");
        if (name != null)
            username.setText(name);
        else {
            username.setText(defaultUsername);
        }
    }

    @Override
    public void onAttachOrDetach(AttachEvent event) {
        username.setFocus(true);
        username.selectAll();
    }

    @UiHandler("confirm")
    void onConfirm(ClickEvent event) {
        if (username.getText() != null
                && username.getText().length() > 0
                && !defaultUsername.equals(username.getText())
                && loginRegExp.test(username.getText()))
            getUiHandlers().confirm(username.getText(), null);//username.getText(), password.getText());
        else {
            username.setFocus(true);
            username.selectAll();
            errorLabel.setText("We need a login name to identify your contributions to the song list.  You can only use letters, numbers and underscores.");
        }
    }

    RegExp loginRegExp = RegExp.compile("^[\\w]+$");
    private static final String defaultUsername = "Your app name here";
}