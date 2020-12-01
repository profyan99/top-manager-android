package com.topmngr.game.Ui;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.topmngr.game.Network.Network;
import com.topmngr.game.Utils.Assets;

/**
 * Created by Profyan on 02.04.2017.
 */
public class ItemServersList extends ListRow {

    public ItemServersList(String nametxt, int players, int maxplayers, int period, int maxperiods, boolean pass,
                           Network.GameState state) {

        setSize(getWidth(),60);
        Label name = new Label(nametxt, Assets.instance.mainStyle);
        Label other = new Label(players+"/"+maxplayers + "            "+period+"/"+maxperiods, Assets.instance.mainStyle);

        if(pass) {
            add(name).left().padLeft(30);
            Image imgLock = new Image(Assets.instance.bglock);
            add(imgLock).padLeft(20).left().expand();
        }
        else {
            add(name).left().padLeft(30).expand();
        }
        add(other).padRight(30);
        setTouchable(Touchable.enabled);
        if(state == Network.GameState.WAITING) //TODO сделать нормальный бэк для списка
            background(new Image(Assets.instance.bgTopbar).getDrawable());
        else
            background(new Image(Assets.instance.bgTopbar).getDrawable());
        //setDebug(true);
    }

}
