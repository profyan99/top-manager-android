package com.topmngr.game.Screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.topmngr.game.Ui.*;
import com.topmngr.game.Network.GameClient;
import com.topmngr.game.Network.GameServer;
import com.topmngr.game.Network.Network;

import com.topmngr.game.Game.Player;
import com.topmngr.game.Ui.AdvanceTextField;
import com.topmngr.game.Utils.Assets;
import com.topmngr.game.Utils.SoundManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Comparator;

import static com.topmngr.game.Screen.TopManager.screenX;
import static com.topmngr.game.Screen.TopManager.screenY;
import static com.topmngr.game.Screen.TopManager.VIEWPORT_LEFT;
import static com.topmngr.game.Screen.TopManager.VIEWPORT_RIGHT;
import static com.topmngr.game.Screen.TopManager.viewportWidth;
import static com.topmngr.game.Network.Network.PlayersState;

class GameScreen extends ClientScreen implements Screen {
    private AdvanceStage stage;

    private Viewport viewport;

    private MessageController msgController;

    private Player
            player,
            playerOld;

    private Network.GameState gameState;

    private GameClient client;

    private GameServer server;

    private Network.RatingStruct[] playerStructs;

    private AdvanceImageButton activeButton;

    private Table
            tKontent,
            tChat,
            tManage,
            tSummary,
            tStorage,
            tBank,
            tProds,
            tRating,
            topBar,
            tRif,
            ratingKontentTable;

    private ChatList2
            chatList;

    private ScrollPane
            scrollPane;

    private Label
            lTitle,
            lPlayers,
            lPeriod,
            lTime,
            lSummaryVal,
            lStorageVal,
            lBankVal,
            lProdsVal,
            lInfoIndustryVal,
            lFPS;

    private Actor
            activeActor;

    private AdvanceTextField
            tCost,
            tProd,
            tMark,
            tInv,
            tNIR;
    private Dialog
            exitDialog,
            endTournamentDialog,
            endDialog;



    private Array<String> chatMsg;
    private int
            amountPeriods = 1,
            maxPeriods = 8,
            secondsToEnd = 0,
            amountPlayers = 0,
            amountMessages = 0;

    private long
            lastMessageTime = 0;

    private final int PERIOD_MESSAGES_MILLS = 2 * 1000;

    private boolean
            back = false,
            isSolution = false,
            isServer = false,
            isInRoom = false,
            isOnPause = false;

    private float possX, possY;

    /**
     * 0 - mainKontent
     * -1 - chat
     * 1 - rating
     */
    private int stateWindow = 0;


    GameScreen(Object o) {
        if(o instanceof Integer) {
            try {
                server = new GameServer((Integer)o);
                client = new GameClient(this, InetAddress.getLocalHost());
                isServer = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(o instanceof InetAddress) {
            this.client = new GameClient(this,(InetAddress)o);
        }
        else {
            this.client = (GameClient)o;
            isInRoom = true;
        }

    }

    @Override
    public void show() {
        OrthographicCamera camera = new OrthographicCamera(screenX, screenY);
        camera.position.set(new Vector3(screenX/2,screenY/2,0));
        viewport = new FillViewport(screenX,screenY, camera);
        stage = new AdvanceStage(viewport);
        msgController = new MessageController(stage);
        chatMsg = new Array<String>();
        player = new Player();


        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                String error = "ERROR: \n\n\t" + e.getLocalizedMessage() + "\n\t"
                        + e.getCause() + "\n\n" + "Message: \n\n\t" + e.getMessage();

                //Assets.putError(error);

                System.err.println("Critical Failure: " + error);
                e.printStackTrace();
                FileHandle file = Gdx.files.local("errors.txt");
                file.writeString(error, true);


            }
        });
        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        new Runnable() {
                            @Override public void run() {
                                if(client != null)
                                    client.stop();
                                if(server != null)
                                    server.stop();
                            }
                        }
                ));


        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new GestureDetector(new GestureDetector.GestureListener() {

            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                return false;
            }

            @Override
            public boolean longPress(float x, float y) {
                return false;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                if(velocityY <= -30)
                    changeWindows(true);
                else if(velocityY >= 30)
                    changeWindows(false);
                return false;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                return false;
            }

            @Override
            public boolean panStop(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
                return false;
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                return false;
            }

            @Override
            public void pinchStop() {

            }
        }));

        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCatchBackKey(true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        createUI();
        initializeGame();

    }

    private void createUI() {
        tKontent = new Table();
        tChat = new Table();

        topBar = new Table();
        topBar.setSize(TopManager.viewportWidth,60);
        topBar.setPosition(VIEWPORT_LEFT,screenY - topBar.getHeight());
        topBar.setBackground(new Image(Assets.instance.bgTopbar).getDrawable());
        {
            Table
                    sPlayers = new Table(),
                    sPeriod = new Table(),
                    sTime = new Table();

            lPlayers = new Label("", Assets.instance.mainStyle);
            lPlayers.setFontScale(0.7f);
            lPlayers.setColor(Colors.get("cPureWhite"));

            sPlayers.background(new Image(Assets.instance.bgTopbarLabel).getDrawable());
            sPlayers.add(lPlayers).expand();

            lPeriod = new Label("", Assets.instance.mainStyle);
            lPeriod.setFontScale(0.7f);
            lPeriod.setColor(Colors.get("cPureWhite"));

            sPeriod.background(new Image(Assets.instance.bgTopbarLabel).getDrawable());
            sPeriod.add(lPeriod).expand();

            lTime = new Label("", Assets.instance.mainStyle);
            lTime.setFontScale(0.7f);
            lTime.setColor(Colors.get("cPureWhite"));

            sTime.background(new Image(Assets.instance.bgTopbarLabel).getDrawable());
            sTime.add(lTime).expand();


            topBar.add(sPlayers).center().expandX().height(40);
            topBar.add(sPeriod).center().expand().height(40);
            topBar.add(sTime).center().expand().height(40);
        }
        {
            tKontent.setSize(TopManager.viewportWidth, 580);
            tKontent.setPosition(VIEWPORT_LEFT,screenY-tKontent.getHeight()-topBar.getHeight());
            tKontent.top();

            Table kontentTable = new Table();
            kontentTable.left();
            kontentTable.setSize(TopManager.viewportWidth,tKontent.getHeight() - 20);
            {
                Table leftMenu = new Table();
                leftMenu.top();
                leftMenu.setBackground(new Image(Assets.instance.bgleftBar).getDrawable());
                leftMenu.setSize(100,kontentTable.getHeight());
                {
                    final AdvanceImageButton
                            summary,
                            warehouse,
                            bank,
                            production,
                            industry,
                            manage;

                    summary = new AdvanceImageButton(new Image(Assets.instance.btnSummary[0]).getDrawable(),
                            new Image(Assets.instance.btnSummary[1]).getDrawable(), new Image(Assets.instance.btnSummary[2]).getDrawable());
                    summary.addListener(new InputListener() {
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            changeContentTable(tSummary);
                            if(activeButton != summary)
                                activeButton.setChecked(false);
                            else
                                activeButton.setChecked(true);

                            activeButton = summary;
                            return false;
                        }
                    });

                    warehouse = new AdvanceImageButton(new Image(Assets.instance.btnStorage[0]).getDrawable(),
                            new Image(Assets.instance.btnStorage[1]).getDrawable(), new Image(Assets.instance.btnStorage[2]).getDrawable());
                    warehouse.addListener(new InputListener() {
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            changeContentTable(tStorage);
                            if(activeButton != warehouse)
                                activeButton.setChecked(false);
                            else
                                activeButton.setChecked(true);

                            activeButton = warehouse;
                            return false;
                        }
                    });
                    bank = new AdvanceImageButton(new Image(Assets.instance.btnBank[0]).getDrawable(),
                            new Image(Assets.instance.btnBank[1]).getDrawable(), new Image(Assets.instance.btnBank[2]).getDrawable());
                    bank.addListener(new InputListener() {
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            changeContentTable(tBank);
                            if(activeButton != bank)
                                activeButton.setChecked(false);
                            else
                                activeButton.setChecked(true);

                            activeButton = bank;
                            return false;
                        }
                    });
                    production = new AdvanceImageButton(new Image(Assets.instance.btnProd[0]).getDrawable(),
                            new Image(Assets.instance.btnProd[1]).getDrawable(), new Image(Assets.instance.btnProd[2]).getDrawable());
                    production.addListener(new InputListener() {
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            changeContentTable(tProds);
                            if(activeButton != production)
                                activeButton.setChecked(false);
                            else
                                activeButton.setChecked(true);

                            activeButton = production;
                            return false;
                        }
                    });
                    industry = new AdvanceImageButton(new Image(Assets.instance.btnIndustry[0]).getDrawable(),
                            new Image(Assets.instance.btnIndustry[1]).getDrawable(), new Image(Assets.instance.btnIndustry[2]).getDrawable());
                    industry.addListener(new InputListener() {
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            changeContentTable(tRating);
                            if(activeButton != industry)
                                activeButton.setChecked(false);
                            else
                                activeButton.setChecked(true);

                            activeButton = industry;
                            return false;
                        }
                    });
                    manage = new AdvanceImageButton(new Image(Assets.instance.btnManage[0]).getDrawable(),
                            new Image(Assets.instance.btnManage[1]).getDrawable(), new Image(Assets.instance.btnManage[2]).getDrawable());
                    manage.addListener(new InputListener() {
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            changeContentTable(tManage);
                            if(activeButton != manage)
                                activeButton.setChecked(false);
                            else
                                activeButton.setChecked(true);

                            activeButton = manage;
                            return false;
                        }
                    });
                    leftMenu.setWidth(100);
                    leftMenu.defaults().width(leftMenu.getHeight() / 6).height(leftMenu.getHeight() / 6);
                    leftMenu.add(summary).expand();
                    leftMenu.row();
                    leftMenu.add(warehouse).expand();
                    leftMenu.row();
                    leftMenu.add(bank).expand();
                    leftMenu.row();
                    leftMenu.add(production).expand();
                    leftMenu.row();
                    leftMenu.add(industry).expand();
                    leftMenu.row();
                    leftMenu.add(manage).expand();

                    activeButton = summary;
                }

                Table mainKontent = new Table();
                mainKontent.setSize(kontentTable.getWidth() - leftMenu.getWidth() - 10,kontentTable.getHeight());
                mainKontent.top();
                mainKontent.setBackground(new Image(Assets.instance.bgKontent).getDrawable());
                {
                    float padSize = leftMenu.getWidth() + 30;
                    possX = padSize+VIEWPORT_LEFT;
                    possY = screenY-tKontent.getHeight()-topBar.getHeight();
                    String s;

                    tSummary = new Table();
                    {
                        Label lTitle = new Label("Отчет о прибыли",Assets.instance.mainStyle);
                        lTitle.setFontScale(1.2f);
                        lTitle.setColor(Colors.get("cDarkBlue"));
                        lTitle.setAlignment(Align.center, Align.center);

                        Label lSummary;
                        s =
                                "Выручка:\n" +
                                "СППТ:\n" +
                                "\n" +
                                "Валовый доход:\n" +
                                "Маркетинг:\n" +
                                "НИОКР:\n" +
                                "Амортизация:\n" +
                                "Стоимость хранения:\n" +
                                "Банковский процент:\n" +
                                "\n" +
                                "Прибыль до налога:\n" +
                                "Налог:\n" +
                                "Чистая прибыль:\n";

                        lSummary = new Label(s,Assets.instance.mainStyle);
                        lSummary.setWrap(true);
                        lSummary.setColor(Colors.get("cDarkBlue"));

                        s =
                                "$ " + player.gRevenue + "\n" +
                                "$ " + player.gSPPT + "\n" +
                                "_____________"  + "\n" +
                                "$ " + player.gGrossIncome + "\n" +
                                "$ " + player.gPlayerMarketing + "\n" +
                                "$ " + player.gPlayerResAndDev + "\n" +
                                "$ " + player.gDepreciation + "\n" +
                                "$ " + player.gCostStorage + "\n" +
                                "$ " + player.gBankInterest + "\n" +
                                "_____________" + "\n" +
                                "$ " + player.gProfitTax + "\n" +
                                "$ " + player.gTax + "\n" +
                                "$ " + player.gNetProfit + "\n";

                        lSummaryVal = new Label(s,Assets.instance.mainStyle);
                        lSummaryVal.setWrap(true);

                        tSummary.setSize(viewportWidth - padSize,mainKontent.getHeight());
                        tSummary.setPosition(possX,possY);
                        tSummary.add(lTitle).colspan(2).align(Align.center).fillX();
                        tSummary.row().padTop(40);
                        tSummary.add(lSummary).left();
                        tSummary.add(lSummaryVal).expandX();
                    }

                    tStorage = new Table();
                    {
                        Label lTitle = new Label("Склад",Assets.instance.mainStyle);
                        lTitle.setFontScale(1.2f);
                        lTitle.setColor(Colors.get("cDarkBlue"));
                        lTitle.setAlignment(Align.center, Align.center);

                        Label lStorage;
                        s =
                                "Получено заказов:\n" +
                                "Продано:\n" +
                                "\n" +
                                "Невыполненных зак.:\n" +
                                "На складе:";

                        lStorage = new Label(s,Assets.instance.mainStyle);
                        lStorage.setWrap(true);
                        lStorage.setColor(Colors.get("cDarkBlue"));

                        s =
                                player.gReceivedOrders + "\n" +
                                player.gSell + "\n" +
                                "_____________"  + "\n" +
                                player.gBackLog + "\n" +
                                + player.gStorage;

                        lStorageVal = new Label(s,Assets.instance.mainStyle);
                        lStorageVal.setWrap(true);

                        tStorage.setSize(viewportWidth - padSize,mainKontent.getHeight());
                        tStorage.setPosition(possX,possY);
                        tStorage.add(lTitle).colspan(2).align(Align.center).fillX();
                        tStorage.row().padTop(40);
                        tStorage.add(lStorage).left();
                        tStorage.add(lStorageVal).expandX();
                    }

                    tBank = new Table();
                    {
                        Label lTitle = new Label("Банк",Assets.instance.mainStyle);
                        lTitle.setFontScale(1.2f);
                        lTitle.setColor(Colors.get("cDarkBlue"));
                        lTitle.setAlignment(Align.center, Align.center);

                        Label lBank;
                        s =
                                "Цена:\n" +
                                "Производство:\n" +
                                "Маркетинг:\n" +
                                "Инвестиции:\n" +
                                "НИОКР:\n" +
                                "\n" +
                                "Наличные средства:\n" +
                                "На складе:\n" +
                                "Капвложения:\n" +
                                "\n"+
                                "Суммарный актив:\n"+
                                "\n"+
                                "Займы:\n" +
                                "Нак. прибыль:\n";

                        lBank = new Label(s,Assets.instance.mainStyle);
                        lBank.setWrap(true);
                        lBank.setColor(Colors.get("cDarkBlue"));

                        s =
                                "$ " + player.gPlayerCost + "\n" +
                                "$ " + player.gPlayerProduction + "\n" +
                                "$ " + player.gPlayerMarketing + "\n" +
                                "$ " + player.gPlayerInvestments + "\n" +
                                "$ " + player.gPlayerResAndDev + "\n" +
                                "_____________\n" +
                                "$ " + player.gCash + "\n" +
                                "$ " + player.gActiveStorage + "\n" +
                                "$ " + player.gKapInvests + "\n" +
                                "_____________\n" +
                                "$ " + player.gSumActive + "\n" +
                                "_____________\n" +
                                "$ " + player.gLoans + "\n" +
                                "$ " + player.gAccumulatedProfit + "\n";

                        lBankVal = new Label(s,Assets.instance.mainStyle);
                        lBankVal.setWrap(true);

                        tBank.setSize(viewportWidth - padSize,mainKontent.getHeight());
                        tBank.setPosition(possX,possY);
                        tBank.add(lTitle).colspan(2).align(Align.center).fillX();
                        tBank.row().padTop(40);
                        tBank.add(lBank).left();
                        tBank.add(lBankVal).expandX();
                    }

                    tProds = new Table();
                    {
                        Label lTitle = new Label("Производство",Assets.instance.mainStyle);
                        lTitle.setFontScale(1.2f);
                        lTitle.setColor(Colors.get("cDarkBlue"));
                        lTitle.setAlignment(Align.center, Align.center);

                        Label lProds;
                        s =
                                "Полная мощность:\n" +
                                "Доп. вложения:\n" +
                                "\n" +
                                "Мощность след.пер:\n" +
                                "\n" +
                                "Стоимость ед. прод.:\n";

                        lProds = new Label(s,Assets.instance.mainStyle);
                        lProds.setWrap(true);
                        lProds.setColor(Colors.get("cDarkBlue"));

                        s =
                                player.gFullPower + "\n" +
                                "$ " + player.gAdditionalValues + "\n" +
                                "_____________\n" +
                                player.gFuturePower + "\n" +
                                "_____________\n" +
                                player.gCostMakeProduct + "\n";

                        lProdsVal = new Label(s,Assets.instance.mainStyle);
                        lProdsVal.setWrap(true);

                        tProds.setSize(viewportWidth - padSize,mainKontent.getHeight());
                        tProds.setPosition(possX,possY);
                        tProds.add(lTitle).colspan(2).align(Align.center).fillX();
                        tProds.row().padTop(40);
                        tProds.add(lProds).left();
                        tProds.add(lProdsVal).expandX();
                    }

                    tRating = new Table();
                    {
                        Label lTitle = new Label("Индустрия",Assets.instance.mainStyle);
                        lTitle.setFontScale(1.2f);
                        lTitle.setColor(Colors.get("cDarkBlue"));
                        lTitle.setAlignment(Align.center, Align.center);

                        Label lInfoIndustry;
                        s =
                                "Всего заказов:\n" +
                                "Всего произв.:\n" +
                                "Всего продано:\n" +
                                "Общая мощность:\n" +
                                "Складировано:\n" +
                                "\n" +
                                "Выручка по инд.:\n" +
                                "Средняя цена:\n" +
                                "Ср. ст-сть пр-ва:\n" +
                                "\n"+
                                "Ставка нал. на приб.:\n"+
                                "Ставка по крдт.:\n"+
                                "Ставка по крдт. экстр.:\n"+
                                "Ставка по депозиту:\n"+
                                "\n"+
                                "Капвложения:\n" +
                                "Исп-е мощности:\n";

                        lInfoIndustry = new Label(s,Assets.instance.mainStyle);
                        lInfoIndustry.setWrap(true);
                        lInfoIndustry.setColor(Colors.get("cDarkBlue"));

                        s =
                                player.allReceivedOrders + "\n" +
                                player.allProd + "\n" +
                                player.allSell + "\n" +
                                player.allSumPower + "\n" +
                                player.allStorage + "\n" +
                                "_____________" + "\n" +
                                "$ " + player.allRevenue + "\n" +
                                "$ " + player.allAvCost + "\n" +
                                "$ " + player.allAvCostMakeProd + "\n" +
                                "_____________" + "\n" +
                                Network.PROFIT_TAX * 100 +"%" + "\n" +
                                Network.BANK_RATE * 100 +"%" + "\n" +
                                Network.EXTR_BANK_RATE * 100 +"%" + "\n" +
                                Network.BANK_RATE * 100 +"%" + "\n" +
                                "_____________" + "\n" +
                                "$ " + player.allKapInvests + "\n" +
                                player.allAvUsingPower + "\n";

                        lInfoIndustryVal = new Label(s,Assets.instance.mainStyle);
                        lInfoIndustryVal.setWrap(true);

                        tRating.setSize(viewportWidth - padSize,mainKontent.getHeight());
                        tRating.setPosition(possX,possY);
                        tRating.add(lTitle).colspan(2).align(Align.center).fillX();
                        tRating.row().padTop(20);
                        tRating.add(lInfoIndustry).left();
                        tRating.add(lInfoIndustryVal).expandX();
                    }

                    tManage = new Table();
                    {
                        Label lTitle = new Label("Управление фирмой",Assets.instance.mainStyle);
                        lTitle.setFontScale(1.2f);
                        lTitle.setColor(Colors.get("cDarkBlue"));
                        lTitle.setAlignment(Align.center, Align.center);

                        AdvanceTextButton bSend;

                        tCost = new AdvanceTextField("",Assets.instance.skin);
                        tCost.getStyle().font = Assets.instance.mainFontSmall;
                        tCost.getStyle().messageFontColor = Color.WHITE;
                        tCost.getStyle().messageFont  = Assets.instance.mainFontSmall;
                        tCost.setMessageText("Цена");
                        tCost.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
                        tCost.setMaxLength(3);
                        tCost.setFocusTraversal(true);

                        tProd = new AdvanceTextField("",Assets.instance.skin);
                        tProd.getStyle().font = Assets.instance.mainFontSmall;
                        tProd.getStyle().messageFontColor = Color.WHITE;
                        tProd.getStyle().messageFont  = Assets.instance.mainFontSmall;
                        tProd.setMessageText("Производство");
                        tProd.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
                        tProd.setMaxLength(4);
                        tProd.setFocusTraversal(true);

                        tMark = new AdvanceTextField("",Assets.instance.skin);
                        tMark.getStyle().font = Assets.instance.mainFontSmall;
                        tMark.getStyle().messageFontColor = Color.WHITE;
                        tMark.getStyle().messageFont  = Assets.instance.mainFontSmall;
                        tMark.setMessageText("Маркетинг");
                        tMark.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
                        tMark.setMaxLength(5);
                        tMark.setFocusTraversal(true);

                        tInv = new AdvanceTextField("",Assets.instance.skin);
                        tInv.getStyle().font = Assets.instance.mainFontSmall;
                        tInv.getStyle().messageFontColor = Color.WHITE;
                        tInv.getStyle().messageFont  = Assets.instance.mainFontSmall;
                        tInv.setMessageText("Инвестиции");
                        tInv.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
                        tInv.setMaxLength(5);
                        tInv.setFocusTraversal(true);

                        tNIR = new AdvanceTextField("",Assets.instance.skin);
                        tNIR.getStyle().font = Assets.instance.mainFontSmall;
                        tNIR.getStyle().messageFontColor = Color.WHITE;
                        tNIR.getStyle().messageFont  = Assets.instance.mainFontSmall;
                        tNIR.setMessageText("НИОКР");
                        tNIR.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
                        tNIR.setMaxLength(5);
                        tNIR.setFocusTraversal(true);

                        bSend = new AdvanceTextButton("Отправить",Assets.instance.mainButtonStyle);
                        bSend.addListener(new InputListener() {

                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                sendSolutions();
                                return false;
                            }
                        });

                        tManage.setSize(viewportWidth - padSize,mainKontent.getHeight());
                        tManage.setPosition(possX,possY);
                        tManage.defaults().size((mainKontent.getWidth() - 5 * 20) / 5f,50);

                        tManage.padTop(40);
                        tManage.add(lTitle).colspan(5).align(Align.center).fillX();
                        tManage.row().padTop(60);
                        tManage.add(tCost).center().pad(10);
                        tManage.add(tProd).center().pad(10);
                        tManage.add(tMark).center().pad(10);
                        tManage.add(tInv).center().pad(10);
                        tManage.add(tNIR).center().pad(10);
                        tManage.row().padTop(100);
                        tManage.add(bSend).colspan(5).center().width(135).height(55);
                    }

                }
                kontentTable.add(leftMenu).align(Align.left).width(100);
                kontentTable.add(mainKontent).width(kontentTable.getWidth() - 110).align(Align.left).height(kontentTable.getHeight()).padLeft(10);
            }

            tKontent.row().padTop(20);
            tKontent.add(kontentTable).height(tKontent.getHeight() - 20);
        }
        {
            tChat.setSize(TopManager.viewportWidth, 520);
            tChat.setPosition(VIEWPORT_LEFT,screenY-tKontent.getHeight()-tChat.getHeight()-topBar.getHeight());
            tChat.top();
            {
                Table titleChat = new Table();
                titleChat.setBackground(new Image(Assets.instance.bgButton).getDrawable());
                lTitle = new Label("[cDarkBlue]ЧАТ", Assets.instance.mainStyle);
                titleChat.add(lTitle).expand();

                Table chatTable = new Table();
                chatTable.setWidth(tChat.getWidth() - 40 * 2);
                chatTable.top();
                {
                    chatList = new ChatList2(chatMsg);
                    scrollPane = new ScrollPane(chatList);
                    scrollPane.setScrollingDisabled(true,false);
                    scrollPane.setFadeScrollBars(false);
                    scrollPane.setFlingTime(0);
                    scrollPane.getStyle().background  = new Image(Assets.instance.bgKontent).getDrawable();
                    scrollPane.setFlickScroll(true);
                    scrollPane.setOverscroll(false,true);
                    scrollPane.setDebug(true);

                    final AdvanceTextField askField = new AdvanceTextField("",Assets.instance.skin);
                    askField.getStyle().font = Assets.instance.mainFontSmall;
                    askField.getStyle().background = new Image(Assets.instance.bgTextFieldChat).getDrawable();
                    askField.getStyle().messageFontColor = Color.WHITE;
                    askField.getStyle().messageFont  = Assets.instance.mainFontSmall;
                    askField.setMessageText("Сообщение...");
                    askField.setMaxLength(100);


                    AdvanceTextButton ask = new AdvanceTextButton("Отправить",Assets.instance.mainButtonStyle);
                    ask.addListener(new InputListener() {
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            if(askField.getText().isEmpty())
                                return false;
                            if(System.currentTimeMillis() < lastMessageTime) {
                                msgController.showMsg(MessageController.messageType.LIMIT_FREQUENCY);
                                return false;
                            }

                            client.sendMessage(askField.getText());
                            askField.setText("");
                            lastMessageTime = System.currentTimeMillis() + PERIOD_MESSAGES_MILLS;
                            return false;
                        }
                    });

                    chatTable.add(scrollPane).height(tChat.getHeight() - 150).width(chatTable.getWidth()).colspan(2);
                    chatTable.row().padTop(10);
                    chatTable.add(askField).width(viewportWidth*0.75f).left();
                    chatTable.add(ask).left().padLeft(20).height(50).width(130);
                }
                tChat.add(titleChat).width(150).height(50).center();
                tChat.row().padTop(10);
                tChat.add(chatTable).width(tChat.getWidth() - 40 * 2).height(tChat.getHeight()-60).center();
            }
        }
        tRif = new Table();
        tRif.setSize(viewportWidth, 250);
        tRif.setBackground(new Image(Assets.instance.bgTopbar).getDrawable());
        tRif.setPosition(VIEWPORT_LEFT,screenY - topBar.getHeight());
        {
            ratingKontentTable = new Table();
            Label title = new Label(
                    "Имя\n\nЦена\nВыруч.\nЧ.Приб.\nН.Приб.\nДоля Р.\nРИФ",
                    Assets.instance.mainStyle);
            title.setWrap(true);
            title.setFontScale(0.75f);
            title.setColor(Colors.get("cDarkBlue"));

            ScrollPane pane = new ScrollPane(ratingKontentTable);
            pane.getStyle().background = new Image(Assets.instance.bgRatePlayers).getDrawable();
            pane.setFlickScroll(true);
            pane.setOverscroll(false,false);
            pane.setScrollingDisabled(false,true);

            ratingKontentTable.defaults().width(viewportWidth  / 6f).height(tRif.getHeight());
            Label l[] = new Label[8];
            for(int i = 0; i < 8; i++) {
                l[i] = new Label("",Assets.instance.mainStyle);
                l[i].setFontScale(0.75f);
                l[i].setWrap(true);
                l[i].setColor(Colors.get("cPureWhite"));
                ratingKontentTable.add(l[i]).pad(10);
            }
            tRif.add(title).width(110).height(tRif.getHeight()).padLeft(20);
            tRif.add(pane).height(tRif.getHeight() - 20).width(tRif.getWidth() - 150).center();
        }
        exitDialog = new Dialog("Выход из игры",Assets.instance.mainWindowStyle);
        {
            exitDialog.setSize(400, 200);
            exitDialog.setPosition(screenX / 2 - 200, screenY / 2 - 100);
            Table dialogTable = new Table();
            dialogTable.setSize(400, 150);
            AdvanceTextButton yesBtn = new AdvanceTextButton("Да", Assets.instance.mainButtonStyle);
            yesBtn.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    exitDialog.hide();
                    if (gameState == Network.GameState.GAME) {
                        Assets.PlayerData.addExitedGamesAmount();
                    }
                    if (isInRoom) {
                        client.disconnectRoom();
                        ScreenManager.getInstance().show(ScreenEnum.MENU,client);
                        client.setClientScreen((ClientScreen) ScreenManager.getInstance().getCurrentScreen());
                    } else {
                        if (client != null)
                            client.stop();
                        if (server != null)
                            server.stop();
                        ScreenManager.getInstance().show(ScreenEnum.MENU);
                    }
                    return false;
                }
            });
            AdvanceTextButton noBtn = new AdvanceTextButton("Нет", Assets.instance.mainButtonStyle);
            noBtn.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    exitDialog.hide();
                    back = false;
                    return false;
                }
            });

            Label msg = new Label("Вы хотите выйти из игры?", Assets.instance.mainStyle);

            dialogTable.add(msg).center().expand().colspan(2);
            dialogTable.row().padTop(50);
            dialogTable.add(yesBtn).width(100).height(50).center().padRight(30);
            dialogTable.add(noBtn).width(100).height(50).center().padLeft(30);

            exitDialog.add(dialogTable).expand();
            exitDialog.setMovable(false);
        }

        lFPS = new Label("",Assets.instance.mainStyle);
        lFPS.setFontScale(1.1f);
        lFPS.setColor(Color.GREEN);
        lFPS.setPosition(VIEWPORT_LEFT+50,screenY-50);

        stage.addActor(tKontent);
        stage.addActor(tChat);
        stage.addActor(tManage);
        stage.addActor(tSummary);
        stage.addActor(tStorage);
        stage.addActor(tBank);
        stage.addActor(tProds);
        stage.addActor(tRating);
        stage.addActor(tRif);
        stage.addActor(topBar);
        stage.addActor(lFPS);
        tStorage.addAction(Actions.sequence(Actions.fadeOut(0),Actions.hide()));
        tBank.addAction(Actions.sequence(Actions.fadeOut(0),Actions.hide()));
        tProds.addAction(Actions.sequence(Actions.fadeOut(0),Actions.hide()));
        tManage.addAction(Actions.sequence(Actions.fadeOut(0),Actions.hide()));
        tRating.addAction(Actions.sequence(Actions.fadeOut(0),Actions.hide()));
        activeActor = tSummary;
    }

    private int getDifferentInPercents(float second, float first) {
        if(first == 0)
            return 0;
        return (int) (((second - first)/first) * 100);
    }

    private String getPercentsColor(int diff) {
        String col = "[cDarkBlue]";
        if(diff > 0)
            col = "[cGreen]";
        else if(diff < 0)
            col = "[cRed]";
        return col;
    }

    private void updateInformation() {
        int diff = getDifferentInPercents(player.gRevenue,playerOld.gRevenue);
        String s;
        {
            s =
                    "[cDarkBlue]$ " + (int)player.gRevenue + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue] ) " + "\n" + //player.gRevenue
                            "$ " + (int)player.gSPPT + "\n" +
                            "_____________" + "\n" +
                            "$ " + (int)player.gGrossIncome + "\n" +
                            "$ " + (int)player.gPlayerMarketing + "\n" +
                            "$ " + (int)player.gPlayerResAndDev + "\n" +
                            "$ " + (int)player.gDepreciation + "\n" +
                            "$ " + (int)player.gCostStorage + "\n" +
                            "$ " + (int)player.gBankInterest + "\n";

            diff = getDifferentInPercents(player.gProfitTax, playerOld.gProfitTax);
            s += "_____________" + "\n" +
                    "$ " + (int)player.gProfitTax + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n" +
                    "$ " + (int)player.gTax + "\n";
            diff = getDifferentInPercents(player.gProfitTax, playerOld.gProfitTax);
            s += "$ " + (int)player.gNetProfit + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";
            lSummaryVal.setText(s);

            diff = getDifferentInPercents(player.gReceivedOrders, playerOld.gReceivedOrders);
            s =
                  "[cDarkBlue]" +   (int)player.gReceivedOrders + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.gSell, playerOld.gSell);
            s +=
                    (int)player.gSell + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n" +
                            "_____________" + "\n" +
                            (int)player.gBackLog + "\n" +
                            +(int)player.gStorage;
        }
        lStorageVal.setText(s);

        {
            s = "[cDarkBlue]" + "$ " + (int)player.gPlayerCost + "\n" +
                    (int)player.gPlayerProduction + "\n" +
                    "$ " + (int)player.gPlayerMarketing + "\n" +
                    "$ " + (int)player.gPlayerInvestments + "\n" +
                    "$ " + (int)player.gPlayerResAndDev + "\n" +
                    "_____________\n";

            diff = getDifferentInPercents(player.gCash, playerOld.gCash);
            s +=
                    "$ " + (int)player.gCash + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n" +
                            "$ " + (int)player.gActiveStorage + "\n" +
                            "$ " + (int)player.gKapInvests + "\n" +
                            "_____________\n" +
                            "$ " + (int)player.gSumActive + "\n" +
                            "_____________\n";

            diff = getDifferentInPercents(player.gLoans, playerOld.gLoans);
            s +=
                    "$ " + (int)player.gLoans + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.gLoans, playerOld.gLoans);
            s +=
                    "$ " + (int)player.gAccumulatedProfit + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";
        }
        lBankVal.setText(s);

        {
            diff = getDifferentInPercents(player.gFullPower, playerOld.gFullPower);
            s =
                    "[cDarkBlue]" + (int)player.gFullPower + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n" +
                            "$ " + (int)player.gAdditionalValues + "\n" +
                            "_____________\n";

            diff = getDifferentInPercents(player.gFuturePower, playerOld.gFuturePower);
            s +=
                    (int)player.gFuturePower + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            s += "_____________\n";
            s += String.format("%.2f",playerOld.gCostMakeProduct) + "\n";
        }
        lProdsVal.setText(s);

        {
            diff = getDifferentInPercents(player.allReceivedOrders, playerOld.allReceivedOrders);
            s = "[cDarkBlue]" + (int)player.allReceivedOrders + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.allProd, playerOld.allProd);
            s += (int)player.allProd + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.allSell, playerOld.allSell);
            s += (int)player.allSell + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.allSumPower, playerOld.allSumPower);
            s += (int)player.allSumPower + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.allStorage, playerOld.allStorage);
            s += (int)player.allStorage + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n" +
                    "_____________" + "\n";

            diff = getDifferentInPercents(player.allRevenue, playerOld.allRevenue);
            s += "$ " + (int)player.allRevenue + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.allAvCost, playerOld.allAvCost);
            s += "$ " + (int)player.allAvCost + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.allAvCostMakeProd, playerOld.allAvCostMakeProd);
            s += "$ " + (int)player.allAvCostMakeProd + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n" +
                    "_____________" + "\n";

            s += Network.PROFIT_TAX * 100 +"%" + "\n" +
                    Network.BANK_RATE * 100 +"%" + "\n" +
                    Network.EXTR_BANK_RATE * 100 +"%" + "\n" +
                    Network.BANK_RATE * 100 +"%" + "\n" +
                    "_____________" + "\n";

            diff = getDifferentInPercents(player.allKapInvests, playerOld.allKapInvests);
            s += "$ " + (int)player.allKapInvests + " ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";

            diff = getDifferentInPercents(player.allAvUsingPower, playerOld.allAvUsingPower);
            s += (int)player.allAvUsingPower + "% ( " + getPercentsColor(diff) + diff + "% [cDarkBlue]) " + "\n";
        }
        lInfoIndustryVal.setText(s);

    }

    private void sendSolutions() {

        if(!isSolution)
            return;

        if(tCost.getText().isEmpty()
                || tProd.getText().isEmpty()
                || tMark.getText().isEmpty()
                || tInv.getText().isEmpty()
                || tNIR.getText().isEmpty()) {

            msgController.showMsg(MessageController.messageType.FILL_FIELD);
            return;
        }
        int
                tmpCost = Integer.parseInt(tCost.getText()),
                tmpProd = Integer.parseInt(tProd.getText()),
                tmpMarketing = Integer.parseInt(tMark.getText()),
                tmpInvest = Integer.parseInt(tInv.getText()),
                tmpNir = Integer.parseInt(tNIR.getText());


        if(tmpProd > player.gFuturePower)
        {
            msgController.showMsg(MessageController.messageType.WRONG_AMOUNT);
            return;
        }
        if(tmpProd < 0 || tmpMarketing < 0 || tmpInvest < 0 || tmpNir < 0 ||
                tmpProd > 50000 || tmpMarketing > 50000 || tmpInvest > 50000 || tmpNir > 50000)
        {
            msgController.showMsg(MessageController.messageType.LIMIT_GAME_VALUES);
            return;
        }
        if(15 > tmpCost || tmpCost > 200)
        {
            msgController.showMsg(MessageController.messageType.LIMIT_COST);
            return;
        }
        client.sendPlayerSolution(tmpCost, tmpProd, tmpMarketing, tmpInvest, tmpNir);
        msgController.showMsg(MessageController.messageType.VALUES_SUCCESS);
        lTime.setText("");
        isSolution = false;
    }

    private void changeContentTable(Actor show) {
        if(show == null || show == activeActor)
            return;

        if(activeActor != null)
            activeActor.addAction(Actions.sequence(Actions.fadeOut(0.5f,Interpolation.pow4),Actions.hide()));

        show.addAction(Actions.sequence(Actions.fadeIn(0.5f,Interpolation.pow4), Actions.show()));
        activeActor = show;
    }

    private void changeWindows(boolean chat) {
        if(chat) {
            if(stateWindow == -1)
                return;

            if(stateWindow == 1) { // переключаемся на контент
                tRif.addAction(Actions.moveTo(VIEWPORT_LEFT, screenY, 0.5f, Interpolation.fade));
                tKontent.addAction(Actions.moveTo(VIEWPORT_LEFT,screenY-tKontent.getHeight()- topBar.getHeight(),0.5f, Interpolation.fade));
                tChat.addAction(Actions.moveTo(VIEWPORT_LEFT,screenY-tKontent.getHeight()-tChat.getHeight() - topBar.getHeight(),0.5f, Interpolation.fade));
                if (activeActor != null) {
                    activeActor.addAction(Actions.moveTo(possX, possY, 0.5f, Interpolation.fade));
                    activeActor.setTouchable(Touchable.enabled);
                }
                tKontent.setTouchable(Touchable.enabled);
            }
            else if(stateWindow == 0) { // переключаемся на чат
                tChat.addAction(Actions.moveTo(VIEWPORT_LEFT, 0, 0.5f, Interpolation.fade));
                tKontent.addAction(Actions.moveTo(VIEWPORT_LEFT, tChat.getHeight(), 0.5f, Interpolation.fade));
                if (activeActor != null) {
                    activeActor.addAction(Actions.moveTo(possX, tChat.getHeight(), 0.5f, Interpolation.fade));
                    activeActor.setTouchable(Touchable.disabled);
                }
                tKontent.setTouchable(Touchable.disabled);

                amountMessages = 0;
                lTitle.setText("[cDarkBlue]ЧАТ");
            }
            stateWindow--;
        }
        else {
            if(stateWindow == 1)
                return;

            if(stateWindow == -1) { // переключаемся на контент
                tChat.addAction(Actions.moveTo(VIEWPORT_LEFT,screenY-tKontent.getHeight()-tChat.getHeight() - topBar.getHeight(),0.5f, Interpolation.fade));
                tKontent.addAction(Actions.moveTo(VIEWPORT_LEFT,screenY-tKontent.getHeight()- topBar.getHeight(),0.5f, Interpolation.fade));
                if(activeActor != null) {
                    activeActor.addAction(Actions.moveTo(possX,possY,0.5f, Interpolation.fade));
                    activeActor.setTouchable(Touchable.enabled);
                }
                tKontent.setTouchable(Touchable.enabled);
            }
            else if(stateWindow == 0) { // переключаемся на рейтинг
                tRif.addAction(Actions.moveTo(VIEWPORT_LEFT, screenY - tRif.getHeight() - topBar.getHeight(), 0.5f, Interpolation.fade));
                tKontent.addAction(Actions.moveTo(VIEWPORT_LEFT,screenY-tKontent.getHeight()- topBar.getHeight() - tRif.getHeight(),0.5f, Interpolation.fade));
                tChat.addAction(Actions.moveTo(VIEWPORT_LEFT,screenY-tKontent.getHeight()-tChat.getHeight() - topBar.getHeight() - tRif.getHeight() ,0.5f, Interpolation.fade));
                if (activeActor != null) {
                    activeActor.addAction(Actions.moveTo(possX, screenY-tKontent.getHeight()- topBar.getHeight() - tRif.getHeight(), 0.5f, Interpolation.fade));
                    activeActor.setTouchable(Touchable.disabled);
                }
                tKontent.setTouchable(Touchable.disabled);
            }
            stateWindow++;
        }
    }

    private void updatePlayersList(String name, PlayersState state ) {

        final String colors[] = {
                "[cRed]", "[cGreen]", "[cGray]"
        };
        int i = 0;
        for(; i < playerStructs.length; i++) {
            if(playerStructs[i].name.equals(name))
                break;
        }
        String s =
                colors[state.ordinal()] + playerStructs[i].name + "\n\n[cPureWhite]" +
                        "$ " + playerStructs[i].cost + "\n" +
                        "$ " + playerStructs[i].revenue + "\n" +
                        "$ " + playerStructs[i].netProfit + "\n" +
                        "$ " + playerStructs[i].accProfit + "\n" +
                        String.format("%.1f",playerStructs[i].markPart) + "%\n" +
                        playerStructs[i].rif;
        ((Label)ratingKontentTable.getChildren().get(i)).setText(s);
    }

    public void onDisconnect(String reason) {
        final Dialog exitDialog = new Dialog("Ошибка соединения!",Assets.instance.mainWindowStyle);
        exitDialog.setPosition(screenX/2, screenY/2);
        Table dialogTable = new Table();
        dialogTable.setSize(400,150);

        AdvanceTextButton okBtn = new AdvanceTextButton("Ок",Assets.instance.mainButtonStyle);
        okBtn.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                exitDialog.hide();
                if(isInRoom) {
                    client.disconnectRoom();
                    ScreenManager.getInstance().show(ScreenEnum.MENU,client);
                    client.setClientScreen((ClientScreen) ScreenManager.getInstance().getCurrentScreen());
                }
                else {
                    if(client != null)
                        client.stop();
                    if(server != null)
                        server.stop();
                    ScreenManager.getInstance().show(ScreenEnum.MENU);
                }
                return false;
            }
        });

        Label msg = new Label("Отключен от сервера.\nПричина: "+reason, Assets.instance.mainStyle);

        dialogTable.add(msg).center().expand();
        dialogTable.row().padTop(50);
        dialogTable.add(okBtn).width(100).height(50).center();

        exitDialog.add(dialogTable).expand();
        exitDialog.setMovable(false);
        exitDialog.show(stage);
    }

    public void onServerSendMessage(String text) {
        msgController.showMsg(text);
    }

    public void onChangePlayerState(Network.ChangePlayerState state) {
        updatePlayersList(state.player.name, state.player.state);
    }

    public void onChangePlayersAmount(int amount) {
        amountPlayers = amount;
        lPlayers.setText("Игроки: "+ amountPlayers);
    }

    public void onPlayerSendMessage(String txt, String name, String color) {
        String str = color+name+"[BLACK]: "+txt;
        if(chatMsg.size >= 200)
            chatMsg.removeIndex(0);

        chatMsg.add(str);
        chatList.setItems(chatMsg);
        scrollPane.setScrollY(scrollPane.getMaxY());
        if(stateWindow != -1) {
            amountMessages++;
            lTitle.setText("[cDarkBlue]ЧАТ ([cPureWhite]+"+ amountMessages +"[cDarkBlue])");
        }

    }

    @Override
    public void onPlayerUpdate(Player p) {

        if(gameState == Network.GameState.WAITING) {
            amountPeriods = p.currPeriod;
            maxPeriods = p.maxPeriods;
            playerOld = p;
            if(amountPeriods != 0)
                isSolution = true;
        }
        else if(gameState == Network.GameState.GAME) {
            amountPeriods = p.currPeriod;
            if(amountPeriods != maxPeriods) {
                if (p.isBankrupt) {
                    final Dialog exitDialog = new Dialog("Конец игры", Assets.instance.mainWindowStyle);
                    exitDialog.setPosition(screenX / 2, screenY / 2);
                    exitDialog.setSize(400, 250);

                    Table dialogTable = new Table();
                    dialogTable.setSize(400, 200);

                    AdvanceTextButton okBtn = new AdvanceTextButton("Ок", Assets.instance.mainButtonStyle);
                    okBtn.addListener(new InputListener() {

                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            exitDialog.hide();
                            return false;
                        }
                    });

                    Label msg = new Label(
                            "Вы не смогли расплатиться с долгами!\nВаша фирма закрыта по причине банкротства" +
                                    "\nВы можете остаться и наблюдать или покинуть игру.", Assets.instance.mainStyle);

                    dialogTable.add(msg).center().expand();
                    dialogTable.row().padTop(50);
                    dialogTable.add(okBtn).width(100).height(50).center();
                    exitDialog.add(dialogTable).expand();
                    exitDialog.setMovable(false);
                    exitDialog.show(stage);

                    Assets.PlayerData.addBankruptAmount();


                } else {
                    secondsToEnd = Network.COUNT_REPEAT_TIMER_SOLUTIONS;
                    isSolution = true;
                    playerOld = player;

                }
                Gdx.input.vibrate(500);
                SoundManager.playSound(Assets.instance.sounds.get(Assets.Sounds.ALERT));
            }
            else {
                playerOld = player;
                Gdx.input.vibrate(500);

            }
        }
        player = p;
        lPeriod.setText("Период: "+ amountPeriods +" / "+ maxPeriods);
        updateInformation();
        if(Gdx.app.getType() == Application.ApplicationType.Android && isOnPause) {
            TopManager.notificationHandler.showNotification("Начался новый период!","Зайди и прими решения!");
        }
    }

    private void initializeGame() {
        amountPlayers = 0;
        lPlayers.setText("Игроки: "+ amountPlayers);
        gameState = Network.GameState.WAITING;
        amountPeriods = 0;
        lPeriod.setText("Период: "+ amountPeriods +" / "+ maxPeriods);
    }

    public void onChangeGameState(Network.GameState gState) {
        gameState = gState;
        if(gState == Network.GameState.WAITING) {
            initializeGame();
        }
        else if(gState == Network.GameState.GAME) {
            secondsToEnd = Network.COUNT_REPEAT_TIMER_SOLUTIONS;
            isSolution = true;
        }
        else if(gState == Network.GameState.END) {
            final Dialog exitDialog = new Dialog("Конец игры",Assets.instance.mainWindowStyle);
            exitDialog.setPosition(screenX/2, screenY/2);
            exitDialog.setSize(400,350);

            Table dialogTable = new Table();
            dialogTable.setSize(400,300);
            AdvanceTextButton okBtn = new AdvanceTextButton("Играть", Assets.instance.mainButtonStyle);
            okBtn.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(isServer || isInRoom)
                    {
                        System.out.println("NEW GAME");
                        initializeGame();
                        client.newGame();
                    }
                    exitDialog.hide();
                    return false;
                }
            });

            AdvanceTextButton exBtn = new AdvanceTextButton("Отчет",Assets.instance.mainButtonStyle);
            exBtn.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    exitDialog.hide();
                    if(!isInRoom){
                        if(client != null)
                            client.stop();
                        if(server != null)
                            server.stop();
                        ScreenManager.getInstance().show(ScreenEnum.MENU);
                    }
                    return false;
                }
            });
            String s1, s2, s;
            s = "";
            s1 = "";
            s2 = "";
            for(int i = 0; i < playerStructs.length; i++) {

                if(playerStructs[i].name.equals(player.name)) {
                    s += "[cDarkBlue]"+(i+1)+"\n";
                    s1 += "[cDarkBlue]"+playerStructs[i].name+"\n";
                    s2 += "[cDarkBlue]"+playerStructs[i].rif+"\n";
                }
                else {
                    s += "[cPureWhite]"+(i+1)+"\n";
                    s1 += "[cPureWhite]"+playerStructs[i].name+"\n";
                    s2 += "[cPureWhite]"+playerStructs[i].rif+"\n";
                }

            }
            Label zagl  = new Label("[cPureWhite]Итог по РИФ:", Assets.instance.mainStyle);
            Label num  = new Label(s, Assets.instance.mainStyle);
            Label msg = new Label(s1, Assets.instance.mainStyle);
            Label rif =  new Label(s2, Assets.instance.mainStyle);

            dialogTable.add(zagl).colspan(3).center();
            dialogTable.row().padTop(25);
            dialogTable.add(num).center().expand().padRight(10);
            dialogTable.add(msg).center().expand().padRight(50);
            dialogTable.add(rif).center().expand();
            dialogTable.row().padTop(50);
            Table btnTable = new Table();
            btnTable.add(okBtn).width(100).height(50).center().padRight(50);
            btnTable.add(exBtn).width(100).height(50).center().padLeft(50);
            dialogTable.add(btnTable).height(50).expandX().colspan(3);

            exitDialog.add(dialogTable).expand();
            exitDialog.setMovable(false);
            exitDialog.show(stage);

            if(playerStructs[0].name.equals(player.name))
                Assets.PlayerData.addWinAmount();

            if(player.gRif > Assets.PlayerData.getBiggestRIF())
                Assets.PlayerData.setBiggestRIF((int) player.gRif);

            if(player.gAccumulatedProfit > Assets.PlayerData.getBiggestAccumulatedNetProfit())
                Assets.PlayerData.setBiggestAccumulatedNetProfit((int) player.gAccumulatedProfit/1000);

            Assets.PlayerData.addGamesAmount();
        }
        else if(gState == Network.GameState.END_TOURNAMENT) {
            final Dialog exitDialog = new Dialog("Конец игры",Assets.instance.mainWindowStyle);
            exitDialog.setPosition(screenX/2, screenY/2);
            exitDialog.setSize(400,350);

            Table dialogTable = new Table();
            dialogTable.setSize(400,300);

            Label label = new Label("Результаты будут объявлены позже.\nСледите за новостями в группе!",Assets.instance.mainStyle);
            //label.setWrap(true);

            AdvanceTextButton exBtn = new AdvanceTextButton("Выход",Assets.instance.mainButtonStyle);
            exBtn.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    exitDialog.hide();
                    client.disconnectRoom();
                    ScreenManager.getInstance().show(ScreenEnum.MENU,client);
                    client.setClientScreen((ClientScreen) ScreenManager.getInstance().getCurrentScreen());
                    return false;
                }
            });

            dialogTable.add(label).expandX().left();
            dialogTable.row().padTop(50);
            dialogTable.add(exBtn).width(100).height(50).center();
            exitDialog.add(dialogTable).expand();
            exitDialog.setMovable(false);
            exitDialog.show(stage);
        }
    }

    public void onUpdateRating(Network.RatingStruct[] struct) {
        for(int i = 0; i < 8; i++) {
            ((Label)ratingKontentTable.getChildren().get(i)).setText("");
        }
        playerStructs = struct;
        Arrays.sort(playerStructs, new Comparator<Network.RatingStruct>() {
            @Override
            public int compare(Network.RatingStruct o1, Network.RatingStruct o2) {
                if(o1.rif == null || o2.rif == null)
                    return 0;
                if(o1.rif.equals("Банкрот"))
                    return -1;
                else if(o2.rif.equals("Банкрот"))
                    return 1;
                return -(Integer.parseInt(o1.rif) - Integer.parseInt(o2.rif));
            }
        });
        String s;
        for(int i = 0; i < struct.length; i++) {
            s =
                            "[cGreen]" + struct[i].name + "\n\n[cPureWhite]" +
                            "$ " + struct[i].cost + "\n" +
                            "$ " + struct[i].revenue + "\n" +
                            "$ " + struct[i].netProfit + "\n" +
                            "$ " + struct[i].accProfit + "\n" +
                            String.format("%.1f",struct[i].markPart) + "%\n" +
                            struct[i].rif;

            ((Label)ratingKontentTable.getChildren().get(i)).setText(s);
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(44f/256f, 76f/256f, 86f/256f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(Assets.instance.isDeveloper) {
            lFPS.setText(""+Gdx.graphics.getFramesPerSecond());
        }
        stage.act(delta);
        if((Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) && !back) {
            exitDialog.show(stage, Actions.fadeIn(0.5f));
        }
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;
        viewportWidth = 720 * aspectRatio;

        VIEWPORT_LEFT = (screenX - viewportWidth) / 2f;
        VIEWPORT_RIGHT = VIEWPORT_LEFT + viewportWidth;
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        isOnPause = true;
    }

    @Override
    public void resume() {
        isOnPause = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        if(client != null)
            client.stop();
        if(server != null)
            server.stop();
        stage.dispose();
    }

    public void onTimerSolutionUpdate(int amount) {

        secondsToEnd = amount;
        if(isSolution)
            lTime.setText(String.format("[cRed]%02d:%02d", secondsToEnd / 60, secondsToEnd % 60));
        else
            lTime.setText(String.format("[cGreen]%02d:%02d", secondsToEnd / 60, secondsToEnd % 60));

        if(secondsToEnd == 30 && isSolution && Gdx.app.getType() == Application.ApplicationType.Android && isOnPause) {
            TopManager.notificationHandler.showNotification("Прими решения!","Осталось меньше 30 секунд!");
        }
        if(secondsToEnd == 0) {
            lTime.setText("[cGreen]00:00");
            msgController.showMsg(MessageController.messageType.TIME_IS_OVER);
            isSolution = false;
        }

    }
}
