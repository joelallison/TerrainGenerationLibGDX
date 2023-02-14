package com.joelallison.screens.userinterface;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.joelallison.screens.AppScreen;

import static com.joelallison.screens.LoginScreen.login;
import static com.joelallison.screens.LoginScreen.register;


public class LoginInterface extends UserInterface {

    Label usernameLabel = new Label("Username", chosenSkin);
    static TextField usernameField = new TextField("", chosenSkin);
    Label passwordLabel = new Label("Password", chosenSkin);
    static TextField passwordField = new TextField("", chosenSkin);
    TextButton loginButton = new TextButton("Login", chosenSkin);
    TextButton registerButton = new TextButton("Register", chosenSkin);
    TextButton skipButton = new TextButton("Go to app", chosenSkin);
    public static Label feedbackLabel = new Label("", chosenSkin);
    Table loginTable = new Table();

    public void genUI(final Stage stage) { //stage is made final here so that it can be accessed within inner classes
        // TextFields don't lose focus by default when you click out, so...
        stage.getRoot().addCaptureListener(new InputListener() {
            // this code was gratefully found on a GitHub forum --> https://github.com/libgdx/libgdx/issues/2173
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) stage.setKeyboardFocus(null);
                return false;
            }
        });

        createLoginTable();

        stage.addActor(loginTable);
    }

    public void createLoginTable() {
        loginTable.setDebug(true);
        loginTable.setFillParent(true);
        loginTable.center();

        loginTable.defaults().align(Align.left).space(4);

        loginTable.add(usernameLabel);

        loginTable.row();
        loginTable.add(usernameField).colspan(2);

        loginTable.row();
        loginTable.add(passwordLabel);

        loginTable.row();
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        loginTable.add(passwordField).colspan(2);

        loginTable.row();
        registerButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                register();
                return true;
            }
        });
        loginTable.add(registerButton);

        loginButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                login();
                return true;
            }
        });

        loginTable.add(loginButton);
        loginTable.row();

        skipButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                ((Game) Gdx.app.getApplicationListener()).setScreen(new AppScreen());
                return true;
            }
        });
        loginTable.add(skipButton);
        loginTable.row();
        loginTable.add(feedbackLabel).colspan(2);
    }

    public static String getUsernameField() {
        return usernameField.getText();
    }

    public static String getPasswordField() {
        return passwordField.getText();
    }
}

