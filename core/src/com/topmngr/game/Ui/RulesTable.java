package com.topmngr.game.Ui;

import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.topmngr.game.Utils.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.topmngr.game.Screen.TopManager.screenY;
import static com.topmngr.game.Screen.TopManager.viewportWidth;

/**
 * Created by PROFYAN on 17.04.2017.
 */
public class RulesTable extends AdvanceTable {

    public interface Listener{
        void hide();
    }
    public RulesTable(Listener listener){
        super();
        init(listener);
    }
    private void init(final Listener listener)  {

        Label lRules = new Label(Assets.instance.rulesText,Assets.instance.mainStyle);
        lRules.setColor(Colors.get("cDarkBlue"));
        lRules.setWrap(true);

        ScrollPane scrollPane = new ScrollPane(lRules);
        scrollPane.setScrollingDisabled(true,false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlingTime(0);
        scrollPane.getStyle().background  = new Image(Assets.instance.bgKontent).getDrawable();
        scrollPane.setFlickScroll(true);
        scrollPane.setOverscroll(false,true);
        scrollPane.setDebug(true);

        Label title = new Label("Правила игры", Assets.instance.mainStyle);
        title.setFontScale(1.3f);
        title.setAlignment(Align.center, Align.center);

        AdvanceTextButton buttonBack = new AdvanceTextButton("Назад", Assets.instance.mainButtonStyle);
        buttonBack.getLabel().setFontScale(1.3f);
        buttonBack.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                listener.hide();
                return true;
            }
        });

        root.top();
        root.padTop(30);
        root.add(buttonBack).width(150).height(75).left().padLeft(50);
        root.add(title).center().expandX().padRight(150);
        root.row();
        root.add(scrollPane).pad(15).align(Align.left).width(viewportWidth-30).height(screenY - 30 - 75 - 30).colspan(2);
        root.pack();
        root.setOrigin(Align.center);
        this.add(root).expand();
        this.setVisible(false);
    }
}
