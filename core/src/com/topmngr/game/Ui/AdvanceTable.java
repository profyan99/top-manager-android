package com.topmngr.game.Ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.topmngr.game.Utils.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.topmngr.game.Screen.TopManager.VIEWPORT_LEFT;
import static com.topmngr.game.Screen.TopManager.screenY;
import static com.topmngr.game.Screen.TopManager.viewportWidth;


public class AdvanceTable extends Table {
    protected Table root;

    public AdvanceTable() {
        this.setBackground(new Image(Assets.instance.bgDiscover).getDrawable());
        this.setTouchable(Touchable.enabled);
        this.setSize(viewportWidth,screenY);
        this.setPosition(VIEWPORT_LEFT,0);
        this.setVisible(false);

        root = new Table();
        root.setTransform(true);
        root.setSize(viewportWidth,screenY);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        super.setScale(scaleX, scaleY);
        root.setScale(scaleX, scaleY);
    }
    public void show(){
        this.addAction(Actions.sequence(alpha(0),Actions.show(), Actions.fadeIn(0.5f)));
    }
    public void hide(){
        this.addAction(Actions.sequence(Actions.fadeOut(0.5f), alpha(0), Actions.hide()));
    }
}
