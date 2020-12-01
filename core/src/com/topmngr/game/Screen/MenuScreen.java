package com.topmngr.game.Screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import com.topmngr.game.Ui.*;
import com.topmngr.game.Network.GameClient;
import com.topmngr.game.Network.Network;
import com.topmngr.game.Ui.AdvanceTextField;
import com.topmngr.game.Utils.Assets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.topmngr.game.Screen.TopManager.*;
import static com.topmngr.game.Utils.Assets.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

class MenuScreen extends ClientScreen implements Screen {

    private AdvanceStage stage;
    private Window windowServer, windowClient;
    private Label lInfo;
    private Table windowDiscover, menuTable;
    private Image loadingImg;
    private AdvanceTextField tfNameClient, tfNameServer;
    private ServerList<ItemServersList> serversList;
    private GameClient client;
    private ArrayList<Boolean> passRooms;
    private Dialog roomCreateDialog;

    private SettingsTable settingsTable;
    private ErrorTable errorTable;
    private RulesTable rulesTable;
    private ServerListTable serverListTable;


    private int amDev = 0;
    private MessageController controller;
    private boolean isInRoom = false;
    private AdvanceTable activeTable;

    MenuScreen(GameClient client) {
        this.client = client;
        isInRoom = true;
    }
    MenuScreen() {

    }

    @Override
    public void show() {
        OrthographicCamera camera = new OrthographicCamera(screenX, screenY);
        camera.position.set(new Vector3(screenX/2,screenY/2,0));
        Viewport viewport = new FillViewport(screenX, screenY, camera);
        passRooms = new ArrayList<Boolean>();

        stage = new AdvanceStage(viewport);
        controller = new MessageController(stage);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (screenX < TopManager.viewportWidth / 4 && screenY < 100) {
                    if(!Assets.instance.isDeveloper) {
                        amDev++;
                        if(amDev == 10) {
                            Assets.instance.isDeveloper = true;
                            System.out.println("You are developer!");
                            controller.showMsg("You are developer!");
                            lInfo.addAction(Actions.sequence(Actions.show(),Actions.fadeIn(0.5f)));
                        }
                        else {
                            System.out.println("+1 to developer. Lost: "+(10 - amDev));
                        }
                    }
                    else {
                        amDev++;
                        if(amDev == 13) {
                            if(activeTable == serverListTable) {
                                client.deleteAllRooms();
                                controller.showMsg("Rooms successfully deleted!");
                            }
                            else if(activeTable == errorTable) {
                                Assets.instance.cleanErrors();
                                controller.showMsg("Errors successfully deleted!");
                            }
                            else {
                                amDev = 0;
                                controller.showMsg("You are not a developer!");
                                lInfo.addAction(Actions.sequence(Actions.fadeOut(0.5f),Actions.hide()));
                                Assets.instance.isDeveloper = false;
                            }
                            amDev = 10;
                        }
                    }
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCatchBackKey(true);


        AdvanceTextButton serverButton = new AdvanceTextButton("Создать", Assets.instance.mainButtonStyle);
        serverButton.getLabel().setStyle(Assets.instance.mainStyle);
        serverButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                windowServer.addAction(Actions.sequence(Actions.show(), Actions.fadeIn(0.5f)));
                menuTable.setTouchable(Touchable.disabled);
                return true;
            }
        });

        final AdvanceTextButton clientButton = new AdvanceTextButton("Подключиться", Assets.instance.mainButtonStyle);
        clientButton.getLabel().setStyle(Assets.instance.mainStyle);
        clientButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                windowClient.addAction(Actions.sequence(Actions.show(), Actions.fadeIn(0.5f)));
                menuTable.setTouchable(Touchable.disabled);
                return true;
            }
        });

        final AdvanceTextButton settingsButton = new AdvanceTextButton("Настройки", Assets.instance.mainButtonStyle);
        settingsButton.getLabel().setStyle(Assets.instance.mainStyle);
        settingsButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                showTable(settingsTable);
                return true;
            }
        });

        AdvanceTextButton serverListButton = new AdvanceTextButton("Играть онлайн", Assets.instance.mainButtonStyle);
        serverListButton.getLabel().setStyle(Assets.instance.mainStyle);
        serverListButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                showTable(serverListTable);
                try {
                    client = new GameClient(MenuScreen.this,InetAddress.getByName(Network.SERVER_IP));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                return true;
            }
        });

        final AdvanceTextButton rulesButton = new AdvanceTextButton("Правила", Assets.instance.mainButtonStyle);
        rulesButton.getLabel().setStyle(Assets.instance.mainStyle);
        rulesButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                showTable(rulesTable);
                return true;
            }
        });

        AdvanceTextButton exitButton = new AdvanceTextButton("Выход", Assets.instance.mainButtonStyle);
        exitButton.getLabel().setStyle(Assets.instance.mainStyle);
        exitButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });

        menuTable = new Table();
        menuTable.top();
        menuTable.setSize(viewportWidth,screenY);
        menuTable.setPosition(VIEWPORT_LEFT,0);
        menuTable.background(new Image(Assets.instance.bgMainMenu).getDrawable());

        menuTable.add(serverListButton).width(250).height(80).center().padTop(60);
        menuTable.row().padTop(20);
        menuTable.add(clientButton).width(250).height(80).center();
        menuTable.row().padTop(20);
        menuTable.add(serverButton).width(250).height(80).center();
        menuTable.row().padTop(20);
        menuTable.add(settingsButton).width(250).height(80).center();
        menuTable.row().padTop(20);
        menuTable.add(rulesButton).width(250).height(80).center();
        menuTable.row().padTop(20);
        menuTable.add(exitButton).width(250).height(80).center();

        createWindowServer();
        createWindowClient();
        createWindowDiscover();
        createWindowErrors();
        createWindowList();
        createWindowSettings();
        createWindowRules();

        lInfo = new Label("Показать ошибки",Assets.instance.mainStyle);
        lInfo.setPosition(screenX / 2,30, Align.center);
        lInfo.addAction(Actions.sequence(alpha(0), Actions.hide()));
        lInfo.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(Assets.instance.isDeveloper)
                    showTable(errorTable);
                return true;
            }
        });

        stage.addActor(menuTable);
        stage.addActor(windowServer);
        stage.addActor(windowClient);
        stage.addActor(lInfo);
        stage.addActor(errorTable);
        stage.addActor(windowDiscover);
        stage.addActor(serverListTable);
        stage.addActor(settingsTable);
        stage.addActor(rulesTable);

    }

    private void createWindowServer() {
        windowServer = new Window("Запуск сервера", Assets.instance.mainWindowStyle);
        windowServer.setBounds(screenX/2-200, screenY/2-175, 400,350);
        windowServer.setMovable(false);
        windowServer.top();
        {
            final AdvanceTextField tfPlayers;
            Label lPlayers,lName;
            AdvanceTextButton bAgree,bDisagree;
            {
                tfPlayers = new AdvanceTextField("2", Assets.instance.mainTextFieldStyle);
                tfPlayers.setMaxLength(1);

                tfNameServer = new AdvanceTextField(PlayerData.getName(), Assets.instance.mainTextFieldStyle);
                tfNameServer.setMaxLength(8);

                lName = new Label("Никнейм: ",Assets.instance.mainStyle);

                lPlayers = new Label("Игроков: ",Assets.instance.mainStyle);

                bAgree = new AdvanceTextButton("Запуск", Assets.instance.mainButtonStyle);

                bDisagree = new AdvanceTextButton("Отмена", Assets.instance.mainButtonStyle);
            }
            windowServer.add(lPlayers).right();
            windowServer.add(tfPlayers).padLeft(20).height(50).width(150);
            windowServer.row().padTop(30);
            windowServer.add(lName).right();
            windowServer.add(tfNameServer).padLeft(20).height(50).width(150);
            windowServer.row();

            Table btnTable = new Table();
            btnTable.add(bAgree).width(100).height(50).center().padRight(50);
            btnTable.add(bDisagree).width(100).height(50).center().padLeft(50);

            windowServer.add(btnTable).colspan(2).expand().padTop(50);


            bAgree.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    if(tfPlayers.getText().isEmpty())
                        return true;

                    int pl = Integer.parseInt(tfPlayers.getText());

                    if(!(2 <= pl && pl<= 8))
                        return true;

                    PlayerData.setName(tfNameServer.getText());
                    tfNameClient.setText(tfNameServer.getText());
                    ScreenManager.getInstance().show(ScreenEnum.GAME,pl);
                    return true;
                }
            });

            bDisagree.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    windowServer.addAction(Actions.sequence(Actions.fadeOut(0.5f),alpha(0), Actions.hide()));
                    menuTable.setTouchable(Touchable.enabled);
                    return true;
                }
            });
        }
        windowServer.addAction(Actions.sequence(alpha(0), Actions.hide()));
    }

    private void createWindowErrors() {
        errorTable = new ErrorTable();
    }

    private void createWindowClient() {
        windowClient = new Window("Подключение к серверу", Assets.instance.mainWindowStyle);
        windowClient.setBounds(screenX/2-200, screenY/2-125, 400,250);
        windowClient.setMovable(false);
        windowClient.top();
        {

            final Label lName;
            AdvanceTextButton bAgree,bDisagree;
            {
                tfNameClient = new AdvanceTextField(PlayerData.getName(), Assets.instance.mainTextFieldStyle);
                tfNameClient.setMaxLength(8);

                lName = new Label("Никнейм: ",Assets.instance.mainStyle);

                bAgree = new AdvanceTextButton("Запуск", Assets.instance.mainButtonStyle);

                bDisagree = new AdvanceTextButton("Отмена", Assets.instance.mainButtonStyle);
            }
            windowClient.add(lName).right();
            windowClient.add(tfNameClient).padLeft(20).height(50).width(150);
            windowClient.row();

            Table btnTable = new Table();
            btnTable.add(bAgree).width(100).height(50).center().padRight(50);
            btnTable.add(bDisagree).width(100).height(50).center().padLeft(50);

            windowClient.add(btnTable).colspan(2).expand().padTop(50);

            bAgree.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    if(tfNameClient.getText().isEmpty())
                        return true;

                    windowClient.addAction(Actions.sequence(Actions.fadeOut(0.5f),alpha(0), Actions.hide()));
                    windowDiscover.addAction(Actions.sequence(Actions.show(),fadeIn(0.5f)));
                    loadingImg.addAction(forever(rotateBy(-90,0.5f)));
                    PlayerData.setName(tfNameClient.getText());
                    tfNameServer.setText(tfNameClient.getText());

                    new AvailableServer(MenuScreen.this);

                    return true;
                }
            });
            bDisagree.addListener(new InputListener() {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    windowClient.addAction(Actions.sequence(Actions.fadeOut(0.5f),alpha(0), Actions.hide()));
                    menuTable.setTouchable(Touchable.enabled);
                    return true;
                }
            });

        }
        windowClient.addAction(Actions.sequence(alpha(0), Actions.hide()));
    }

    private void createWindowDiscover() {
        windowDiscover = new Table();
        windowDiscover.setFillParent(true);
        windowDiscover.setBackground(new Image(Assets.instance.bgDiscover).getDrawable());
        loadingImg = new Image(Assets.instance.bgloading);
        loadingImg.setOrigin(64,64);

        Label lSearch = new Label("Поиск доступных серверов...",Assets.instance.mainStyle);
        lSearch.getStyle().fontColor = Colors.get("cPureWhite");

        windowDiscover.add(loadingImg).width(128).height(128).center().padTop(50);
        windowDiscover.row().padTop(50);
        windowDiscover.add(lSearch).center().expandX();
        windowDiscover.addAction(Actions.sequence(alpha(0),Actions.hide()));

    }

    public synchronized void onEndDiscoverHosts(final InetAddress available, final boolean mode) {
        if(available != null) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    ScreenManager.getInstance().show(ScreenEnum.GAME,available);
                }
            });
        }
        else {
            loadingImg.clearActions();
            windowDiscover.addAction(Actions.sequence(Actions.fadeOut(0.5f),alpha(0), Actions.hide()));
            menuTable.setTouchable(Touchable.enabled);


            final Dialog exitDialog = new Dialog("Ошибка подключения!",Assets.instance.mainWindowStyle);
            exitDialog.setPosition(screenX/2, screenY/2);
            exitDialog.setSize(400,200);

            Table dialogTable = new Table();
            dialogTable.setSize(400,150);

            AdvanceTextButton okBtn = new AdvanceTextButton("Ок",Assets.instance.mainButtonStyle);
            okBtn.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    exitDialog.hide();
                    exitDialog.remove();
                    if(mode) {
                        serverListTable.hide();
                        if(client != null)
                            client.stop();
                    }
                    return false;
                }
            });

            Label msg;
            if(!mode) {
                msg = new Label("Нет доступных серверов!", Assets.instance.mainStyle);
            }
            else {
                msg = new Label("Ошибка подключения к серверу!", Assets.instance.mainStyle);
            }

            dialogTable.add(msg).center().expand();
            dialogTable.row().padTop(50);
            dialogTable.add(okBtn).width(100).height(50).center();

            exitDialog.add(dialogTable).expand();
            exitDialog.setMovable(false);

            exitDialog.show(stage);
        }
    }

    private void createWindowRules() {
        rulesTable = new RulesTable(new RulesTable.Listener() {
            @Override
            public void hide() {
                resetMenu();
            }
        });
    }

    private void createWindowSettings() {
        settingsTable = new SettingsTable(new SettingsTable.Listener() {
            @Override
            public void applyChanges() {

            }

            @Override
            public void hide() {
                resetMenu();
            }
        });
    }

    private void resetMenu() {
        menuTable.setTouchable(Touchable.enabled);
        activeTable = null;
    }

    private void showTable(AdvanceTable table) {
        table.show();
        activeTable = table;
        menuTable.setTouchable(Touchable.disabled);
    }

    private void createWindowList() {

        serversList = new ServerList<ItemServersList>();
        serverListTable = new ServerListTable(new ServerListTable.Listener() {
            @Override
            public void hide() {
                resetMenu();
                deleteClient();
            }

            @Override
            public void connectRoom() {
                onConnectRoom();
            }

            @Override
            public void createRoom() {
                onCreateRoom();
            }
        }, serversList);


        if(isInRoom) {
            showTable(serverListTable);
            client.needUpdateRooms();
        }
    }

    private void deleteClient() {
        client.stop();
        client = null;
    }

    private void onCreateRoom() {
        roomCreateDialog = new Dialog("Создание комнаты",Assets.instance.mainWindowStyle);
        roomCreateDialog.setPosition(screenX/2, screenY/2);
        roomCreateDialog.setSize(400,500);
        roomCreateDialog.setKeepWithinStage(false);
        //exitDialog.setModal(false);

        Table dialogTable = new Table();
        dialogTable.setSize(400,450);


        final AdvanceTextField tfPlayers, tfPeriods, tfName, tfPass;
        Label lPlayers,lName, lPeriods, lPass;
        AdvanceTextButton bAgree,bDisagree;
        {
            tfPlayers = new AdvanceTextField("2", Assets.instance.mainTextFieldStyle);
            tfPlayers.setMaxLength(1);

            tfName = new AdvanceTextField("Room", Assets.instance.mainTextFieldStyle);
            tfName.setMaxLength(16);

            tfPeriods = new AdvanceTextField("8", Assets.instance.mainTextFieldStyle);
            tfPeriods.setMaxLength(1);

            tfPass = new AdvanceTextField("", Assets.instance.mainTextFieldStyle);
            tfPass.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
            tfPass.setMaxLength(4);

            lName = new Label("Название: ",Assets.instance.mainStyle);
            lPlayers = new Label("Игроков: ",Assets.instance.mainStyle);
            lPeriods = new Label("Периодов: ",Assets.instance.mainStyle);
            lPass = new Label("Пароль: ",Assets.instance.mainStyle);

            bAgree = new AdvanceTextButton("Создать", Assets.instance.mainButtonStyle);
            bDisagree = new AdvanceTextButton("Отмена", Assets.instance.mainButtonStyle);
        }
        dialogTable.add(lName).right();
        dialogTable.add(tfName).padLeft(20).height(50).width(150);
        dialogTable.row().padTop(30);
        dialogTable.add(lPlayers).right();
        dialogTable.add(tfPlayers).padLeft(20).height(50).width(150);
        dialogTable.row().padTop(30);
        dialogTable.add(lPeriods).right();
        dialogTable.add(tfPeriods).padLeft(20).height(50).width(150);
        dialogTable.row().padTop(30);
        dialogTable.add(lPass).right();
        dialogTable.add(tfPass).padLeft(20).height(50).width(150);
        dialogTable.row();

        Table btnTable = new Table();
        btnTable.add(bAgree).width(100).height(50).center().padRight(50);
        btnTable.add(bDisagree).width(100).height(50).center().padLeft(50);

        dialogTable.add(btnTable).colspan(2).expand().padTop(50);

        roomCreateDialog.add(dialogTable).expand();
        roomCreateDialog.setMovable(false);

        roomCreateDialog.show(stage);

        bAgree.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(tfPlayers.getText().isEmpty() || tfPeriods.getText().isEmpty() ||
                        tfName.getText().isEmpty() || tfName.getText().length() < 4)
                    return true;

                int pl = Integer.parseInt(tfPlayers.getText());
                int per = Integer.parseInt(tfPeriods.getText());

                if(!(2 <= pl && pl<= 8) || !(2 <= per && per <= Network.MAX_PERIODS))
                    return true;

                client.createRoom(tfName.getText(),pl, per, (tfPass.getText().length() == 0) ? (null) : (tfPass.getText()),
                        Assets.instance.isDeveloper);
                roomCreateDialog.hide();
                roomCreateDialog.remove();
                return true;
            }
        });

        bDisagree.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                roomCreateDialog.hide();
                roomCreateDialog.remove();
                return true;
            }
        });
    }

    private void onConnectRoom() {
        if(serversList.getSelectedIndex() != -1) {

            final Dialog exitDialog = new Dialog("Подключение к комнате",Assets.instance.mainWindowStyle);
            AdvanceTextField tfPass = null;
            exitDialog.setPosition(screenX/2, screenY/2);
            exitDialog.setKeepWithinStage(false);

            if(passRooms.get(serversList.getSelectedIndex()))
                exitDialog.setSize(400,250);
            else
                exitDialog.setSize(400,350);

            Table dialogTable = new Table();
            dialogTable.setSize(400,exitDialog.getHeight() - 50);

            final AdvanceTextField tfName = new AdvanceTextField(PlayerData.getName(), Assets.instance.mainTextFieldStyle);
            tfName.setMaxLength(8);

            Label lName = new Label("Никнейм: ",Assets.instance.mainStyle);

            AdvanceTextButton okBtn = new AdvanceTextButton("Ок",Assets.instance.mainButtonStyle);

            AdvanceTextButton noBtn = new AdvanceTextButton("Отмена",Assets.instance.mainButtonStyle);
            noBtn.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    exitDialog.hide();
                    exitDialog.remove();
                    return false;
                }
            });

            dialogTable.add(lName).right();
            dialogTable.add(tfName).padLeft(20).height(50).width(150);
            if(passRooms.get(serversList.getSelectedIndex()))
            {
                tfPass = new AdvanceTextField("", Assets.instance.mainTextFieldStyle);
                tfPass.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
                tfPass.setMaxLength(4);

                Label lPass = new Label("Пароль: ",Assets.instance.mainStyle);
                dialogTable.row().padTop(50);
                dialogTable.add(lPass).right();
                dialogTable.add(tfPass).padLeft(20).height(50).width(150);
            }
            dialogTable.row();
            Table btnTable = new Table();
            btnTable.add(okBtn).width(100).height(50).center().padRight(50);
            btnTable.add(noBtn).width(100).height(50).center().padLeft(50);

            dialogTable.add(btnTable).colspan(2).expand().padTop(50);

            exitDialog.add(dialogTable).expand();
            exitDialog.setMovable(false);

            exitDialog.show(stage);
            final AdvanceTextField finalTfPass = tfPass;
            okBtn.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(serversList.getSelectedIndex() != -1) {
                        if (tfName.getText().isEmpty() || (finalTfPass != null && finalTfPass.getText().length() == 0))
                            return true;

                        PlayerData.setName(tfName.getText());
                        tfNameServer.setText(tfName.getText());
                        tfNameClient.setText(tfName.getText());
                        ScreenManager.getInstance().show(ScreenEnum.GAME, client);
                        client.setClientScreen((ClientScreen) ScreenManager.getInstance().getCurrentScreen());


                        exitDialog.hide();
                        exitDialog.remove();
                        client.connectRoom(Assets.PlayerData.getName(), serversList.getSelectedIndex(),
                                (finalTfPass != null) ? (finalTfPass.getText()) : (null));
                    }
                    return false;
                }
            });

        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(44f/256f, 76f/256f, 86f/256f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);

        stage.draw();
        if((Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) && activeTable != null) {
            activeTable.hide();
            if(activeTable == serverListTable) {
                deleteClient();
            }
            resetMenu();
        }
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        viewportWidth = 720 * aspectRatio;

        VIEWPORT_LEFT = (screenX - viewportWidth) / 2f;
        VIEWPORT_RIGHT = VIEWPORT_LEFT + viewportWidth;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void onUpdateRooms(Network.RoomsStruct[] struct) {
        serversList.clear();
        passRooms.clear();
        for (Network.RoomsStruct aStruct : struct) {
            passRooms.add(aStruct.hasPass);
            serversList.addItem(new ItemServersList(aStruct.name,
                    aStruct.players, aStruct.maxPlayers, aStruct.periods, aStruct.maxPeriods, aStruct.hasPass,
                    aStruct.state));
        }
    }

    private class AvailableServer implements Runnable {
        private MenuScreen menuScreen;
        private Client cl;

        AvailableServer(MenuScreen menuScreen) {
            this.menuScreen = menuScreen;
            new Thread(this, "DiscoverServers").start();
        }
        @Override
        public void run() {
            cl = new Client();
            cl.start();
            InetAddress address = cl.discoverHost(Network.SERVER_PORT,5000);
            if(address == null)
                menuScreen.onEndDiscoverHosts(null, false);
            else
                menuScreen.onEndDiscoverHosts(address, false);
            cl.close();
            try {
                cl.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
