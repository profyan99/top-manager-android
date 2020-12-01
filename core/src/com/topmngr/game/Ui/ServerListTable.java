package com.topmngr.game.Ui;

import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.topmngr.game.Utils.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.topmngr.game.Screen.TopManager.screenY;
import static com.topmngr.game.Screen.TopManager.viewportWidth;

/**
 * Created by PROFYAN on 17.04.2017.
 */
public class ServerListTable extends AdvanceTable {

    private ServerList serversList;

    public interface Listener{
        void hide();
        void connectRoom();
        void createRoom();
    }
    public ServerListTable(Listener listener, ServerList serversList){
        super();
        this.serversList = serversList;
        init(listener);
    }
    private void init(final Listener listener)  {

        ScrollPane scrollPane = new ScrollPane(serversList);
        scrollPane.setScrollingDisabled(true,false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlingTime(0);
        scrollPane.getStyle().background  = new Image(Assets.instance.bgKontent).getDrawable();
        scrollPane.setFlickScroll(true);
        scrollPane.setOverscroll(false,true);
        scrollPane.setDebug(true);
        scrollPane.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.connectRoom();
                super.clicked(event, x, y);
            }
        });

        Label title = new Label("Список комнат", Assets.instance.mainStyle);
        title.setFontScale(1.3f);
        Table tTitle = new Table();
        {
            Label lName = new Label("Название", Assets.instance.mainStyle);
            Label lPlayers = new Label("Игроки", Assets.instance.mainStyle);
            Label lPeriods = new Label("Период", Assets.instance.mainStyle);
            tTitle.add(lName).padLeft(50+50).expandX().left();
            tTitle.add(lPlayers);
            tTitle.add(lPeriods).padRight(50+50).padLeft(50);
        }

        AdvanceTextButton buttonCreate = new AdvanceTextButton("Создать", Assets.instance.mainButtonStyle);
        buttonCreate.getLabel().setFontScale(1.3f);
        buttonCreate.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                listener.createRoom();
                return true;
            }
        });

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
        root.add(title).center().expandX();
        root.add(buttonCreate).width(150).height(75).left().padRight(50);
        root.row().padTop(30);
        root.add(tTitle).colspan(3).width(viewportWidth);
        root.row();
        root
                .add(scrollPane)
                .center()
                .width(this.getWidth() - 100)
                .height(this.getHeight() - 210)
                .padTop(10)
                .padLeft(50)
                .padRight(50)
                .padBottom(50)
                .colspan(3);
        root.pack();
        root.setOrigin(Align.center);
        this.add(root).expand();
        this.setVisible(false);
    }
}
