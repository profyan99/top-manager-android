package com.topmngr.game.Ui;

import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.topmngr.game.Utils.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.topmngr.game.Screen.TopManager.screenY;
import static com.topmngr.game.Screen.TopManager.viewportWidth;

/**
 * Created by PROFYAN on 17.04.2017.
 */
public class ErrorTable extends AdvanceTable {

    private Label lErorr;

    @Override
    public void show() {
        super.show();
        lErorr.setText(Assets.instance.getError());
    }

    public ErrorTable(){
        super();
        init();
    }
    private void init()  {

        lErorr = new Label("",Assets.instance.mainStyle);
        lErorr.getStyle().fontColor = Colors.get("cPureWhite");
        lErorr.setWrap(true);

        ScrollPane scrollPane = new ScrollPane(lErorr);
        scrollPane.setScrollingDisabled(true,false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlingTime(0);
        scrollPane.getStyle().background  = new Image(Assets.instance.bgKontent).getDrawable();
        scrollPane.setFlickScroll(true);
        scrollPane.setOverscroll(false,true);
        scrollPane.setDebug(true);

        root.top();
        root.add(scrollPane).pad(15).align(Align.left).width(viewportWidth-30).height(screenY-30);
        root.pack();
        root.setOrigin(Align.center);
        this.add(root).expand();
        this.setVisible(false);
    }
}
