package com.topmngr.game.Ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by PROFYAN on 10.04.2017.
 */
public class AdvanceStage extends Stage {
    private boolean isKeyboardShow = false;
    private float cameraSmesh = 0f;

    public AdvanceStage(Viewport viewport) {
        super(viewport);
    }

    public AdvanceStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    private void raiseKeyboard() {
        isKeyboardShow = false;
        Vector3 pos = getCamera().position;
        getCamera().position.set(pos.x, pos.y + cameraSmesh,pos.z);
        getCamera().update();
        cameraSmesh = 0;
    }

    void setKeyboardOpen(float smesh) {
        isKeyboardShow = true;
        cameraSmesh = smesh;
    }
    boolean isKeyboardShow() {
        return isKeyboardShow;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Actor actor = hit(screenX,screenY,true);
        if((actor == null || !(actor instanceof TextField)) && isKeyboardShow) {
            super.unfocusAll();
            Gdx.input.setOnscreenKeyboardVisible(false);
            raiseKeyboard();
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
