package com.topmngr.game.Ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.topmngr.game.Screen.TopManager;

/**
 * Created by PROFYAN on 04.04.2017.
 */
public class AdvanceTextField extends com.badlogic.gdx.scenes.scene2d.ui.TextField {
    public AdvanceTextField(String text, Skin skin) {
        super(text, skin);
        setFocusListener();
    }

    public AdvanceTextField(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
        setFocusListener();
    }

    public AdvanceTextField(String text, TextFieldStyle style) {
        super(text, style);
        setFocusListener();
    }
    private void setFocusListener() {
        addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                super.keyboardFocusChanged(event, actor, focused);
                if(Gdx.app.getType() == Application.ApplicationType.Desktop)
                    return;
                if (focused && !(((AdvanceStage)getStage()).isKeyboardShow())) {
                    Vector3 pos = getStage().getCamera().position;
                    float smesh = Math.max(0,TopManager.screenY/2 + 50 - getY());
                    getStage().getCamera().position.set(
                            pos.x, pos.y - smesh,pos.z);
                    getStage().getCamera().update();
                    ((AdvanceStage)getStage()).setKeyboardOpen(smesh);
                }
            }
        });
        setTextFieldListener(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener() {
            @Override
            public void keyTyped(com.badlogic.gdx.scenes.scene2d.ui.TextField textField, char c) {
                if ((c == '\r' || c == '\n')) {
                    textField.next(true);
                }
            }
        });
    }
}
