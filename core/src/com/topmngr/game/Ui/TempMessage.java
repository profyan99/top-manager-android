package com.topmngr.game.Ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.topmngr.game.Screen.TopManager;
import com.topmngr.game.Utils.Assets;



class TempMessage extends Actor {

    TempMessage(final String message, float delay, float duration, final Stage stage,
                final Color color, final MessageController controller, final MessageController.messageType type) {
        final Table table = new Table();
        table.setPosition(TopManager.screenX/2,-50, Align.center);
        table.setBackground(new Image(Assets.instance.bgTopbar).getDrawable());


        final Label label = new Label(message,Assets.instance.mainStyle);
        label.setFontScale(0.8f);
        label.setColor(color);

        table.add(label).expand();
        table.setSize(message.length() * 20, label.getHeight() * 2);
        table.setPosition(TopManager.screenX/2 - table.getWidth()/2,-70);
        stage.addActor(table);

        table.addAction(
                Actions.sequence(
                        Actions.delay(delay),
                        Actions.parallel(
                                Actions.fadeIn(0.5f),
                                Actions.moveBy(0,200,0.5f)),
                        Actions.delay(duration),
                        Actions.parallel(
                                Actions.fadeOut(0.5f),
                                Actions.moveBy(0,-200,0.5f)),
                        new Action() {
                            public boolean act (float delta) {
                                if(type != MessageController.messageType.NONE)
                                    controller.reset(type);
                                table.remove();
                            return true;
                    }}));

    }

}
