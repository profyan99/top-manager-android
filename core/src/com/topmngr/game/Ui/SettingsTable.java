package com.topmngr.game.Ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.topmngr.game.Utils.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.topmngr.game.Screen.TopManager.*;
import static com.topmngr.game.Screen.TopManager.screenY;

/**
 * Created by PROFYAN on 17.04.2017.
 */
public class SettingsTable extends AdvanceTable {


    public interface Listener{
        void applyChanges(); // TODO сделать настройки и подтверждение изменения настроек
        void hide();
    }
    public SettingsTable(Listener listener){
        super();
        init(listener);
    }
    private void init(final Listener listener)  {

        Label title = new Label("Настройки", Assets.instance.mainStyle);
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
        AdvanceTextButton buttonStats = new AdvanceTextButton("Статистика", Assets.instance.mainButtonStyle);
        buttonStats.getLabel().setFontScale(1.3f);
        buttonStats.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                showStats();
                return true;
            }
        });

        root.top();
        root.padTop(30);
        root.add(buttonBack).width(150).height(75).left().padLeft(50);
        root.add(title).center();
        root.add(buttonStats).width(200).height(75).right().padRight(50);
        root.pack();
        root.setOrigin(Align.center);
        this.add(root).expand();
        this.setVisible(false);
    }

    private void showStats() {
        final Dialog exitDialog = new Dialog("Статистика",Assets.instance.mainWindowStyle);
        exitDialog.setPosition(screenX/2, screenY/2);
        exitDialog.setSize(400,300);

        Table dialogTable = new Table();
        dialogTable.setSize(400,250);

        AdvanceTextButton okBtn = new AdvanceTextButton("Ок",Assets.instance.mainButtonStyle);
        okBtn.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                exitDialog.hide();
                exitDialog.remove();
                return false;
            }
        });

        Label msg = new Label(Assets.PlayerData.getStats(), Assets.instance.mainStyle);
        msg.setWrap(true);

        dialogTable.add(msg).center().width(400).height(200);
        dialogTable.row().padTop(50);
        dialogTable.add(okBtn).width(100).height(50).center();

        exitDialog.add(dialogTable).expand();
        exitDialog.setMovable(false);

        exitDialog.show(getStage());
    }
}
