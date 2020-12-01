package com.topmngr.game.Network;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.kryonet.*;
import com.esotericsoftware.minlog.Log;
import com.topmngr.game.Game.ClientStruct;
import com.topmngr.game.Game.Player;
import com.topmngr.game.Game.PlayersStruct;
import com.topmngr.game.Utils.Assets;
import com.topmngr.game.Network.Network.GameState;


import java.io.IOException;
import java.util.*;

public class GameServer {
    private Server server;
    private final int playersAmount;
    private int
                connectedPlayers,
                amountSendPlayers,
                countPeriods;
    private ArrayList<ClientStruct> players;

    private GameState state;

    private double
            summM = 8400f,
            summP = 0f,
            allBuys = 0f,
            summNiokrAll = 3360f,
            summMarketingAll = 8400f,
            summProductionAll = 3360f,
            allBuysOld = 3360f,
            summLastSales = 3360f;

    private Timer timerSolution;
    private boolean isSolutionAvaliable = false;

    public GameServer(final int playersServer) throws IOException { // TODO обновить сервер

        players = new ArrayList<ClientStruct>();
        playersAmount = playersServer;

        server = new Server() {
            @Override
            protected Connection newConnection() {
                return new GameConnection();
            }
        };
        Log.set(Log.LEVEL_INFO);
        Network.register(server);


        server.addListener(new Listener() {

            public void received (Connection connection, Object object) {
                //Gdx.app.log("SERVER","void received. Connection: "+connection.toString()+" Object: "+object.toString());
                GameConnection gameConnection = (GameConnection)connection;
                Player player = gameConnection.data.player;
                if(object instanceof Network.Register) {
                    String tmpName = ((Network.Register)object).name;
                    for (ClientStruct c: players) {

                        if(c.player.name.equals(tmpName)) {
                            if(state == GameState.GAME) {
                                c.timeExit = 0;
                                gameConnection.data = c;

                                Network.ServerMessage message = new Network.ServerMessage();
                                message.message = gameConnection.data.player.name+" подключился к игре";
                                gameConnection.sendTCP(new Network.Connected());
                                sendToOtherPlayers(message, gameConnection);

                                Network.PlayersAmount playersAmount = new Network.PlayersAmount();
                                playersAmount.amount = connectedPlayers;
                                gameConnection.sendTCP(playersAmount);

                                Network.UpdatePlayer updatePlayer = new Network.UpdatePlayer();
                                updatePlayer.player = c.player;
                                gameConnection.sendTCP(updatePlayer);

                                Network.RatingStruct structRating[] = new Network.RatingStruct[connectedPlayers];
                                for(int o = 0; o < structRating.length; o++) {
                                    structRating[o] = new Network.RatingStruct();
                                    structRating[o].name = players.get(o).player.name;
                                    structRating[o].cost = (int) players.get(o).player.gPlayerCost;
                                    structRating[o].revenue = (int) players.get(o).player.gRevenue;
                                    structRating[o].netProfit = (int) players.get(o).player.gNetProfit;
                                    structRating[o].accProfit = (int) players.get(o).player.gAccumulatedProfit;
                                    structRating[o].markPart = (int)players.get(o).player.gMarketShare;
                                    structRating[o].rif = "" + (int) players.get(o).player.gRif;
                                }

                                Network.UpdateRating rate = new Network.UpdateRating();
                                rate.structs = structRating;
                                gameConnection.sendTCP(rate);

                                Network.ChangeGameState stateS = new Network.ChangeGameState();
                                stateS.state = state;
                                gameConnection.sendTCP(stateS);

                                for(ClientStruct cs:players) {
                                    Network.ChangePlayerState st = new Network.ChangePlayerState();
                                    st.player = new PlayersStruct();
                                    st.player.name = cs.player.name;
                                    st.player.state = cs.player.gState;
                                    connection.sendTCP(st);
                                }

                                return;
                            }
                            else {
                                Network.Disconnected message = new Network.Disconnected();
                                message.reason = "Игрок с таким именем уже существует!";
                                gameConnection.sendTCP(message);
                                gameConnection.close();
                                return;
                            }
                        }
                    }
                    //**************** INITIALIZE PLAYER ****************
                    initializePlayer(gameConnection, tmpName);


                    if(connectedPlayers == playersAmount) {
                        state = GameState.GAME;

                        Network.ChangeGameState stateS = new Network.ChangeGameState();
                        stateS.state = state;
                        server.sendToAllTCP(stateS);

                        openSolutions();
                        Gdx.app.log("srv", "Start new game");
                    }
                    return;
                }
                if(object instanceof Network.NewGame) {
                    initializeGame();
                    return;
                }
                if(object instanceof Network.PlayerSolutions) {

                    if(players.get(gameConnection.ID).player.isSend ||
                            !isSolutionAvaliable ||
                            players.get(gameConnection.ID).player.isBankrupt)
                        return;

                    Network.PlayerSolutions solution = (Network.PlayerSolutions) object;

                    int
                            availableMoney =
                            (int) (50000 - players.get(gameConnection.ID).player.gLoans + players.get(gameConnection.ID).player.gCash),
                            needMoney = (int) (solution.gPlayerMarketing+solution.gPlayerInvestments
                                    +solution.gPlayerResAndDev+player.gCostMakeProduct * solution.gPlayerProduction),
                            step = 0;

                    while(needMoney > availableMoney) {
                        switch (step) {
                            case 0: {
                                needMoney -= solution.gPlayerMarketing;
                                solution.gPlayerMarketing = 0;
                                step++;
                                break;
                            }
                            case 1: {
                                needMoney -= solution.gPlayerResAndDev;
                                solution.gPlayerResAndDev = 0;
                                step++;
                                break;
                            }
                            case 2: {
                                needMoney -= player.gCostMakeProduct * player.gPlayerProduction;
                                solution.gPlayerProduction = 0;
                                step++;
                                break;
                            }
                            case 3: {
                                needMoney -= solution.gPlayerInvestments;
                                solution.gPlayerInvestments = 0;
                                step++;
                                break;
                            }
                            case 4: {
                                // БАНКРОТ
                                needMoney = availableMoney;
                                players.get(gameConnection.ID).player.isBankrupt = true;
                                connectedPlayers --;
                            }

                        }

                    }
                    players.get(gameConnection.ID).player.gPlayerCost = solution.gPlayerCost;
                    players.get(gameConnection.ID).player.gPlayerProduction = solution.gPlayerProduction;
                    players.get(gameConnection.ID).player.gPlayerMarketing = solution.gPlayerMarketing;
                    players.get(gameConnection.ID).player.gPlayerInvestments = solution.gPlayerInvestments;
                    players.get(gameConnection.ID).player.gPlayerResAndDev = solution.gPlayerResAndDev;
                    players.get(gameConnection.ID).player.isSend = true;

                    Network.ChangePlayerState state = new Network.ChangePlayerState();
                    state.player = new PlayersStruct();
                    state.player.name = player.name;
                    players.get(gameConnection.ID).player.gState = Network.PlayersState.WAIT;
                    state.player.state = players.get(gameConnection.ID).player.gState;
                    server.sendToAllTCP(state);

                    if(!players.get(gameConnection.ID).player.isBankrupt)
                        amountSendPlayers++;

                    if(amountSendPlayers >= connectedPlayers)
                    {
                        isSolutionAvaliable = false;
                        amountSendPlayers = 0;
                        timerSolution.clear();
                        for(ClientStruct c:players) {
                            c.player.isSend = false;
                        }
                        calculateAll();
                    }
                    return;
                }
                if (object instanceof Network.PlayerMessage) {
                    ((Network.PlayerMessage)object).color = player.color;
                    server.sendToAllTCP(object);
                    return;
                }
            }

            @Override
            public void connected(Connection connection) {
                GameConnection gameConnection = (GameConnection)connection;

                if(state == GameState.GAME) {
                    for(ClientStruct cs: players) {
                        if(cs.ip.equals(connection.getRemoteAddressTCP().getAddress()) && cs.timeExit != 0) {
                            return;
                        }
                    }
                    Network.Disconnected message = new Network.Disconnected();
                    message.reason = "Игра уже началась!";
                    gameConnection.sendTCP(message);
                    gameConnection.close();
                    return;
                }
                if(connectedPlayers == playersAmount) {
                    Network.Disconnected message = new Network.Disconnected();
                    message.reason = "Сервер заполнен!";
                    gameConnection.sendTCP(message);
                    gameConnection.close();
                    return;
                }
            }

            public void disconnected (Connection connection) {
                GameConnection gameConnection = (GameConnection)connection;
                if(gameConnection.data.player == null)
                        return;

                Network.ServerMessage message = new Network.ServerMessage();
                message.message = gameConnection.data.player.name+" покинул игру!";
                server.sendToAllTCP(message);
                if(state == GameState.GAME) {
                    players.get(gameConnection.ID).timeExit = System.currentTimeMillis() + Network.TIME_RELOAD_MILLS;
                }
                else {
                    players.remove(gameConnection.data);
                    connectedPlayers--;
                    Network.PlayersAmount playersAmount = new Network.PlayersAmount();
                    playersAmount.amount = connectedPlayers;
                    server.sendToAllTCP(playersAmount);
                    Gdx.app.log("srv", "Delete player: "+gameConnection.data.player.name);
                }
                Gdx.app.log("srv", "Disconnected player: "+gameConnection.data.player.name);
            }

        });

        server.bind(Network.SERVER_PORT,Network.SERVER_PORT);
        server.start();
        initializeGame();
    }

    private void initializeGame() {
        state = GameState.WAITING;
        Connection[] connections = server.getConnections();
        amountSendPlayers = 0;
        countPeriods = 0;
        players.clear();
        connectedPlayers = 0;
        summM = 8400f;
        summP = 0f;
        allBuys = 0f;
        summNiokrAll = 3360f;
        summMarketingAll = 8400f;
        summProductionAll = 3360f;
        allBuysOld = 3360f;
        summLastSales = 3360f;
        isSolutionAvaliable = false;
        timerSolution = new Timer();

        Network.ChangeGameState stateS = new Network.ChangeGameState();
        stateS.state = state;
        server.sendToAllTCP(stateS);

        for (Connection connection : connections) {
            GameConnection cc = (GameConnection) connection;
            initializePlayer(cc, cc.data.player.name);
        }

        if(connectedPlayers == playersAmount) {
            state = GameState.GAME;

            stateS = new Network.ChangeGameState();
            stateS.state = state;
            server.sendToAllTCP(stateS);

            openSolutions();
            Gdx.app.log("srv", "Start new game");
        }

    }

    private void initializePlayer(GameConnection gameConnection, String tmpName) {
        Player player;
        player = new Player();
        player.gReceivedOrders = 3360/playersAmount;
        player.gReceivedOrdersOld = player.gReceivedOrders;
        player.gMachineTools = 4200/playersAmount;
        player.gFuturePower = player.gMachineTools;
        player.gPlayerInvestments = (4200/playersAmount * 2);
        player.gPlayerCost = 30;
        player.gPlayerProduction = (3360/playersAmount);
        player.gPlayerMarketing = (8400/playersAmount);
        player.gPlayerResAndDev = (3360/playersAmount);
        player.gRif = (100);
        player.gMarketShare = (int)(100f/playersAmount);
        player.gCash = 14000/playersAmount;
        player.gLoans = 7280;
        calculatePlayer(player);
        player.gAccumulatedProfitZero = player.gAccumulatedProfit;
        player.gFullPower = player.gMachineTools;
        player.gReceivedOrdersOld = player.gReceivedOrders;
        player.name = tmpName;
        player.color = Assets.instance.colors[connectedPlayers];
        player.allProd = 3360;
        player.allSell = 3360;
        player.allStorage = 0;
        player.allRevenue = 3360*player.gPlayerCost;
        player.allAvCost = player.gPlayerCost;
        player.allAvCostMakeProd = player.gCostMakeProduct;
        player.allAvUsingPower = 80;
        player.allReceivedOrders = 3360;
        player.allKapInvests = player.gKapInvests*playersAmount;
        player.allSumPower = 3360;
        player.isSend = false;
        player.isBankrupt = false;

        gameConnection.data.player = player;
        gameConnection.data.ip = gameConnection.getRemoteAddressTCP().getAddress();
        //****************************************************

        Network.ServerMessage message = new Network.ServerMessage();
        message.message = gameConnection.data.player.name+" подключился к игре";
        gameConnection.sendTCP(new Network.Connected());
        sendToOtherPlayers(message, gameConnection);

        connectedPlayers++;
        synchronizationPlayers(gameConnection);

        Network.UpdatePlayer updatePlayer = new Network.UpdatePlayer();
        updatePlayer.player = player;
        gameConnection.sendTCP(updatePlayer);

        Network.RatingStruct structRating[] = new Network.RatingStruct[connectedPlayers];
        for(int o = 0; o < structRating.length; o++) {
            structRating[o] = new Network.RatingStruct();
            structRating[o].name = players.get(o).player.name;
            structRating[o].cost = (int) player.gPlayerCost;
            structRating[o].revenue = (int) player.gRevenue;
            structRating[o].netProfit = (int) player.gNetProfit;
            structRating[o].accProfit = (int) player.gAccumulatedProfit;
            structRating[o].markPart = 100 / playersAmount;
            structRating[o].rif = "" + (int) player.gRif;
        }

        Network.UpdateRating rate = new Network.UpdateRating();
        rate.structs = structRating;
        server.sendToAllTCP(rate);
    }

    private void openSolutions() {
        for(ClientStruct c: players) {
            if(c.player.isBankrupt)
                continue;

            Network.ChangePlayerState st = new Network.ChangePlayerState();
            st.player = new PlayersStruct();
            st.player.name = c.player.name;
            players.get(players.indexOf(c)).player.gState = Network.PlayersState.THINK;
            st.player.state = players.get(players.indexOf(c)).player.gState;
            server.sendToAllTCP(st);
        }

        Network.ServerMessage msg = new Network.ServerMessage();
        msg.message = "Начался новый период!";
        server.sendToAllTCP(msg);

        timerSolution.scheduleTask(new TimerSolution(),0,1,Network.COUNT_REPEAT_TIMER_SOLUTIONS);
        isSolutionAvaliable = true;
    }

    public void stop() {
        Gdx.app.log("srv", "server stopped");
        Network.Disconnected diss = new Network.Disconnected();
        diss.reason = "Сервер выключен!";
        server.sendToAllTCP(diss);

        server.stop();
        try {
            server.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Synchronize logged player to other players in server
     */
    private void synchronizationPlayers(GameConnection c) {

        players.add(c.data);
        c.ID = players.indexOf(c.data);
        Network.PlayersAmount playersAmount = new Network.PlayersAmount();
        playersAmount.amount = connectedPlayers;
        server.sendToAllTCP(playersAmount);
    }

    private void sendToOtherPlayers(Object object, Connection iskl) {

        for(Connection c:server.getConnections()) {
            if(c.equals(iskl))
                continue;
            server.sendToTCP(c.getID(),object);
        }
    }

    private double sum(double mass[]) {
        double summa = 0;
        for (double mas : mass)
            summa += mas;
        return summa;
    }

    /**
     * Calculate buyers and RIF per player and send data to players
     */
    private void calculateAll() {

        double
                summMarketing = 0,
                avPrice = 0;

        double goods_max_sales[] = new double[8],
                goods_predicted[] = new double[8],
                share_effect_price[] = new double[8],
                share_effect_mk[] = new double[8],
                share_effect_rd[] = new double[8],
                share[] = new double[8],
                share_compressed[] = new double[8];

        int kof2 = 0, firmBuyers = 0, allBuyers = 0, allBuyers2 = 0;

        summM = 0;
        summP = 0;
        int c = 0;

        for (ClientStruct cs : players) {
            if (cs == null || cs.player.isBankrupt) {
                c++;
                continue;
            }

            summMarketing += cs.player.gPlayerMarketing;
            avPrice += cs.player.gPlayerCost;
            summNiokrAll += cs.player.gPlayerResAndDev;
            summM += Math.pow(cs.player.gPlayerMarketing / cs.player.gPlayerCost, 1.5);
            summP += Math.pow(1f / cs.player.gPlayerCost, 3.0);
            summMarketingAll += cs.player.gPlayerMarketing;
            summProductionAll += cs.player.gPlayerProduction;
            goods_predicted[c] = cs.player.gStorage + cs.player.gPlayerProduction;
            goods_max_sales[c] = cs.player.gPlayerCost * goods_predicted[c];
            c++;
        }
        {
            double sum_mk_compressed = Math.min(
                    0.25f * (summMarketing - 2100 * 2 * connectedPlayers)
                            + 2100* 2 * connectedPlayers,
                    summMarketing
            );


            double average_price_given = avPrice / connectedPlayers;
            double average_price_planned;
            if (sum(goods_predicted) == 0)
                average_price_planned = average_price_given;
            else
                average_price_planned = sum(goods_max_sales) / sum(goods_predicted);

            double average_price_mixed = average_price_planned;

            double demand_effect_mk = 5.3f * Math.pow(
                    sum_mk_compressed / 8400f,
                    0.5f
            ) / Math.pow(
                    average_price_mixed / 30f,
                    1
            );
            double demand_effect_rd = Math.pow(
                    summNiokrAll / (countPeriods + 1f)/ 3360f,
                    1
            );
            double orders_demand = 62.5f  * (
                    demand_effect_rd + demand_effect_mk
            );

            c = 0;
            for (ClientStruct cs : players) {
                if (cs == null || cs.player.isBankrupt) {
                    c++;
                    continue;
                }
                share_effect_price[c] = Math.pow(
                        average_price_mixed / cs.player.gPlayerCost,
                        3
                );
                share_effect_mk[c] = Math.pow(
                        cs.player.gPlayerMarketing / cs.player.gPlayerCost,
                        1.5f
                );
                share_effect_rd[c] = Math.pow(
                        cs.player.gAllResAndDev,
                        1f
                );
                c++;
            }

            double sum_share_effect_price = sum(share_effect_price);
            double sum_share_effect_mk = sum(share_effect_mk);
            double sum_share_effect_rd = sum(share_effect_rd);

            c = 0;
            for (ClientStruct cs : players) {
                if (cs == null || cs.player.isBankrupt) {
                    c++;
                    continue;
                }
                // orders

                share[c] = 0.7f * share_effect_price[c] / sum_share_effect_price
                        + 0.15 * share_effect_mk[c] / sum_share_effect_mk
                        + 0.15 * share_effect_rd[c] / sum_share_effect_rd;

                share_compressed[c] = Math.min(share[c] * 40 / cs.player.gPlayerCost, share[c]);
                cs.player.gReceivedOrders = (float) (orders_demand * share_compressed[c]);
                allBuyers += cs.player.gReceivedOrders;
                System.out.println("C: " + c + " ORDERS: " + cs.player.gReceivedOrders);
                cs.player.gReceivedOrders = 0;
                c++;
            }
            Gdx.app.log("srv_calculate", "1 | all buys: "+allBuyers + " orders_demand: "+orders_demand);
        }
        {
            avPrice /= connectedPlayers;
            summMarketing = (Math.sqrt(summMarketing) > 16800) ?
                    (Math.sqrt(summMarketing) / 4f + 12600) : (Math.sqrt(summMarketing));

            Gdx.app.log("srv_calculate","summMarketing: " + summMarketing + " avPrice: " + avPrice + " summNiokrAll: " + summNiokrAll +
                    " summM: " + summM + " summP: " + summP + " summMarketing: " + summMarketing);


            allBuys = Math.round((summMarketing / avPrice) * 936.2f + summNiokrAll / (6.72f * (countPeriods + 1)));


            if (summMarketing == 0)
                allBuys *= 0.85;

            if (avPrice > 40)
                allBuys *= 40f / avPrice;

            c = 0;
            for (ClientStruct cs : players) {
                if (cs == null || cs.player.isBankrupt) {
                    c++;
                    continue;
                }

                firmBuyers = getBuyers(cs.player, (int) allBuys);
                cs.player.gReceivedOrders = firmBuyers;
                if (cs.player.gPlayerCost > 40) {
                    cs.player.gReceivedOrders = firmBuyers * 40f / cs.player.gPlayerCost;
                    kof2 += (firmBuyers - firmBuyers * 40f / cs.player.gPlayerCost);
                }
                c++;

            }
            c = 0;
            if (kof2 != 0) {
                for (ClientStruct cs : players) {
                    if (cs == null || cs.player.isBankrupt) {
                        c++;
                        continue;
                    }
                    cs.player.gReceivedOrders += getBuyers(cs.player, kof2);
                    c++;
                }
            }

            c = 0;
            for (ClientStruct cs : players) {
                if (cs == null || cs.player.isBankrupt) {
                    c++;
                    continue;
                }
                allBuyers2 += cs.player.gReceivedOrders;
                Gdx.app.log("srv_calculate","2 | C: "+c+" ORDERS: "+cs.player.gReceivedOrders);
                c++;
            }
            Gdx.app.log("srv_calculate","2 | all buys: " + allBuys + " allBuyers: " + allBuyers2);
        }
        float
                summPower = 0,
                allProd = 0,
                allSell = 0,
                allStorage = 0,
                allRevenue = 0,
                allAvCost = 0,
                allAvCostMakeProd = 0,
                allAvUsingPower = 0,
                allKapInvests = 0,
                summSales = 0;

        for (ClientStruct cs : players) {
            if (cs == null || cs.player.isBankrupt)
                continue;
            calculatePlayer(cs.player);

            if(allBuyers2 != 0)
                cs.player.gMarketShare = cs.player.gReceivedOrders/allBuyers2 * 100;
            else
                cs.player.gMarketShare = 0;

            summPower += cs.player.gFullPower;
            allProd += cs.player.gPlayerProduction;
            allSell += cs.player.gSell;
            allStorage += cs.player.gStorage;
            allRevenue += cs.player.gRevenue;
            allAvCost += cs.player.gPlayerCost;
            allAvCostMakeProd += cs.player.gCostMakeProduct;
            allAvUsingPower += cs.player.gUsingPower;
            allKapInvests += cs.player.gKapInvests;
            summSales += cs.player.gSell;

        }

        allAvCost /= connectedPlayers;
        allAvCostMakeProd /= connectedPlayers;
        allAvUsingPower /= connectedPlayers;

        Network.RatingStruct structRating[] = new Network.RatingStruct[playersAmount];
        int o = 0;

        for (ClientStruct cs : players) {
            if (cs == null)
                continue;

            if(cs.player.isBankrupt) {
                structRating[o] = new Network.RatingStruct();
                structRating[o].name = cs.player.name;
                structRating[o].cost = 0;
                structRating[o].revenue = 0;
                structRating[o].netProfit = 0;
                structRating[o].accProfit = 0;
                structRating[o].markPart = 0;
                structRating[o].rif = "Банкрот";
            }
            else {
                Gdx.app.log("srv_calculate", "gAllMarketing: "+cs.player.gAllMarketing + "\ngAllResAndDev: "+cs.player.gAllResAndDev +
                        "\nsummMarketing: " + summMarketingAll + "\nsummNIR: " + summNiokrAll +
                        "\n"+( cs.player.gAllMarketing + cs.player.gAllResAndDev) / (summMarketingAll + summNiokrAll));
                Gdx.app.log("srv_calculate",
                        "RIF:\n\n  -Accumulated: "+ (50 * (cs.player.gAccumulatedProfit/(countPeriods + 1)/cs.player.gAccumulatedProfitZero/connectedPlayers)) +
                                "\n  -Spros: " + (( cs.player.gAllMarketing + cs.player.gAllResAndDev) / (summMarketingAll + summNiokrAll) * connectedPlayers * 10) +
                                "\n  -Predl: " + (( cs.player.gAllProduction / summProductionAll) * connectedPlayers * 10) +
                                "\n  -Power: " + ((80 - Math.abs(cs.player.gUsingPower - 80)) / 8) +
                                "\n  -Rinok: "+ (cs.player.gReceivedOrders / allBuyers2 * connectedPlayers * 10) +
                                "\n  -Rost: " + (Math.min((cs.player.gSell == 0) ? (0) : (10 * cs.player.gSell / cs.player.gSellOld / summSales * summLastSales),10)));
                cs.player.gRif =
                        Math.round((50 * (cs.player.gAccumulatedProfit/(countPeriods+1)/cs.player.gAccumulatedProfitZero/connectedPlayers)) +
                                (( cs.player.gAllMarketing + cs.player.gAllResAndDev) / (summMarketingAll + summNiokrAll) * connectedPlayers * 10) +
                                (( cs.player.gAllProduction / summProductionAll) * connectedPlayers * 10) +
                                ( (1f - Math.abs(cs.player.gUsingPower - 80)/100) * 10) +
                                ( cs.player.gReceivedOrders / allBuyers2 * connectedPlayers * 10) +
                                (Math.min((cs.player.gSell == 0) ? (0) : (10 * cs.player.gSell / cs.player.gSellOld / summSales * summLastSales),20)));

                structRating[o] = new Network.RatingStruct();
                structRating[o].name = cs.player.name;
                structRating[o].cost = (int) cs.player.gPlayerCost;
                structRating[o].revenue = (int) cs.player.gRevenue;
                structRating[o].netProfit = (int) cs.player.gNetProfit;
                structRating[o].accProfit = (int) cs.player.gAccumulatedProfit;
                structRating[o].markPart = (int) cs.player.gMarketShare;
                structRating[o].rif = "" +(int) cs.player.gRif;
            }

            cs.player.gReceivedOrdersOld = cs.player.gReceivedOrders;
            if(cs.player.gSell != 0)
                cs.player.gSellOld = cs.player.gSell;
            cs.player.allProd = allProd;
            cs.player.allSell = allSell;
            cs.player.allStorage = allStorage;
            cs.player.allRevenue = allRevenue;
            cs.player.allAvCost = allAvCost;
            cs.player.allAvCostMakeProd = allAvCostMakeProd;
            cs.player.allAvUsingPower = allAvUsingPower;
            cs.player.allReceivedOrders = allBuyers2;
            cs.player.allKapInvests = allKapInvests;
            cs.player.allSumPower = summPower;
            o++;
        }
        allBuysOld = allBuyers2;
        summLastSales = summSales;
        countPeriods++;

        for(ClientStruct cs:players) {
            if(cs.player.isBankrupt && !cs.player.isBankruptSend) {
                cs.player.isBankruptSend = true;
                Network.PlayerMessage messageChat = new Network.PlayerMessage();
                messageChat.message = "** " + cs.player.name + "обанкротился(ась)";
                messageChat.name = "Сервер";
                messageChat.color = "[cGray]";
                server.sendToAllTCP(messageChat);

                Network.ChangePlayerState st = new Network.ChangePlayerState();
                st.player = new PlayersStruct();
                st.player.name = cs.player.name;
                players.get(players.indexOf(c)).player.gState = Network.PlayersState.BANKRUPT;
                st.player.state = players.get(players.indexOf(c)).player.gState;
                server.sendToAllTCP(st);
            }
        }

        Connection[] connections = server.getConnections();
        for (int i = connections.length - 1; i >= 0; i--)  {
            GameConnection cc = (GameConnection) connections[i];
            Network.UpdatePlayer upd = new Network.UpdatePlayer();
            upd.player = players.get(cc.ID).player;
            cc.sendTCP(upd);
        }

        Network.UpdateRating rate = new Network.UpdateRating();
        rate.structs = structRating;
        server.sendToAllTCP(rate);

        if(countPeriods < Network.MAX_PERIODS)
            openSolutions();
        else {
            Network.ChangeGameState state = new Network.ChangeGameState();
            state.state = GameState.END;
            server.sendToAllTCP(state);
        }
    }

    /**
     * Calculate buyers for player based on the entered data
     */
    private int getBuyers(Player p, int K)
    {
        double nir = 0, mar = 0;
        if(summNiokrAll > 0)
            nir = K * 0.15f * p.gAllResAndDev/ summNiokrAll;
        if(summM > 0)
            mar = K * 0.15f * Math.pow(p.gPlayerMarketing/p.gPlayerCost,1.5)/summM;

        return (int)(mar +
                K * 0.7f * Math.pow(1f/p.gPlayerCost,3.0)/summP + nir);
    }

    /**
     * Calculate all player parameters
     */
    private void calculatePlayer(Player p)
    {

        p.gCostStorage = p.gStorage; // стоимость хранения = склад
        p.gFullPower = p.gFuturePower;
        if(p.gPlayerProduction + p.gStorage < p.gReceivedOrders) { // если спрос > предложения
            p.gSell = p.gPlayerProduction + p.gStorage; // продано = предложение
            p.gStorage = 0; // склад = 0
            p.gBackLog = p.gReceivedOrders - p.gSell; // невыполненные заказы
        }
        else { // иначе если спрос <= предложение
            p.gSell = p.gReceivedOrders; // продано = спрос
            float prod = p.gPlayerProduction;
            if(p.gReceivedOrders > prod) // если спрос > произведенной продукции
            {
                p.gStorage -= (p.gReceivedOrders - prod); // вычитаем из склада не проданные товары
                prod = 0;
            }
            else // иначе просто изх продукции отнимаем спрос
                prod -= p.gReceivedOrders;

            p.gStorage += prod; // прибавляем к складу оставшиеся товары
            p.gBackLog = 0;
        }
        p.gRevenue = p.gPlayerCost * p.gSell; // Выручка = Цена * продано
        // амортизация
        if(p.gPlayerInvestments < p.gMachineTools * 2) // если инвестиции < требуемой амортизации
        {
            p.gDepreciation = p.gPlayerInvestments; // амортизация = инвестиции
            float difDepr = p.gMachineTools * 2 - p.gPlayerInvestments;
            p.gMachineTools -= Math.round (difDepr/40f); // отнимаем станки
        }
        else
        {
            p.gDepreciation = p.gMachineTools * 2;
            p.gAdditionalValues = (p.gPlayerInvestments - p.gDepreciation); // Добавляем станки на оставшиеся инвестиции
        }

        if(p.gLoans <= 0)
            p.gBankInterest = Math.round (p.gLoans * 0.02f); // банковский процент = 2% от займов.
        else
            p.gBankInterest = Math.round (p.gLoans * (0.1)/4f); // банковский процент = 4 от займов.

        p.gFuturePower = (p.gAdditionalValues + p.gMachineTools * 40)/40; // мощность след.периода
        p.gMachineTools = p.gFuturePower; // станки
        p.gCostMakeProduct = Math.round  (((4200f / playersAmount) / p.gFuturePower) * 15f + 3); // стоимость производства ед. продукции
        p.gSPPT = p.gSell * p.gCostMakeProduct; // СППТ = продано * стоимость.ед.прод.
        p.gCostMakeProductAll = p.gCostMakeProduct * p.gPlayerProduction; // тайные отнимания
        p.gGrossIncome = p.gRevenue - p.gSPPT; // Валовый доход = Выручка - СППТ
        p.gProfitTax = p.gRevenue - (p.gCostStorage + p.gBankInterest + p.gSPPT //p.gCostMakeProductAll
                 + p.gDepreciation + p.gPlayerMarketing + p.gPlayerResAndDev);
        p.gCash -= (p.gCostMakeProductAll - p.gSPPT);

        if(p.gCash <  p.gAdditionalValues) {
            p.gLoans += p.gAdditionalValues - p.gCash;
            p.gCash = 0;
        }
        else
            p.gCash -= p.gAdditionalValues;
        // Прибыль до налога...
        if(p.gProfitTax > 0) { // если эта прибыль > 0
            p.gTax = Math.round  (p.gProfitTax * 0.25); // то налог = 25% от прибыли
            p.gNetProfit = p.gProfitTax - p.gTax; // чистая прибыль
        }
        else // иначе чистая прибыль равна прибыли до налога
        {
            p.gTax = 0;
            p.gNetProfit = p.gProfitTax;
        }
        if(p.gNetProfit < 0)
        {
            float difNet = p.gCash + p.gNetProfit;
            if(difNet > 0)
                p.gCash = difNet;
            else
            {
                p.gCash = 0;
                p.gLoans -= difNet;
            }
        }
        else
        {
            if(p.gLoans > 0)
            {
                p.gLoans -= p.gNetProfit*0.75;
                p.gCash += p.gNetProfit * 0.25;
            }
            else
            {
                p.gLoans -= p.gNetProfit*0.25;
                p.gCash += p.gNetProfit * 0.75;
            }
        }

        p.gAccumulatedProfit += p.gNetProfit; // увеличиваем накопленную прибыль
        p.gUsingPower = p.gPlayerProduction / p.gFuturePower * 100; // используемая мощность в %
        p.gActiveStorage = p.gStorage * p.gCostMakeProduct;
        p.gKapInvests = p.gFullPower * 40;
        p.gSumActive = p.gKapInvests + p.gActiveStorage + p.gCash;

        p.gAllMarketing += p.gPlayerMarketing;
        p.gAllResAndDev += p.gPlayerResAndDev;
        p.gAllProduction += p.gPlayerProduction;
    }

    private static class GameConnection extends Connection {
        ClientStruct data;
        int ID;
        GameConnection() {
            data = new ClientStruct();
        }
    }

    private class TimerSolution extends Timer.Task {
        private int count = 0;
        public TimerSolution() {
            count = 0;
        }
        @Override
        public void run() {
            count++;
            Network.UpdateTimerSolution time = new Network.UpdateTimerSolution();
            time.time = Network.COUNT_REPEAT_TIMER_SOLUTIONS-count;
            server.sendToAllTCP(time);
            if(count == Network.COUNT_REPEAT_TIMER_SOLUTIONS) {
                isSolutionAvaliable = false;
                for(ClientStruct cs:players) {
                    if(cs.timeExit != 0 && System.currentTimeMillis() > cs.timeExit) {
                        players.remove(cs);
                        connectedPlayers--;
                        Network.PlayersAmount playersAmount = new Network.PlayersAmount();
                        playersAmount.amount = connectedPlayers;
                        server.sendToAllTCP(playersAmount);
                        Gdx.app.log("srv", "Delete player: "+cs.player.name);
                        continue;
                    }
                    if(cs.player.isBankrupt)
                        continue;

                    if(!cs.player.isSend) {
                        if(50000 - cs.player.gLoans + cs.player.gCash < cs.player.gDepreciation)
                            cs.player.gPlayerInvestments = 50000 - cs.player.gLoans + cs.player.gCash;
                        else
                            cs.player.gPlayerInvestments = cs.player.gDepreciation;
                    }
                }
                for(ClientStruct cs:players) {
                    cs.player.isSend = false;
                }
                amountSendPlayers = 0;
                calculateAll();
                Gdx.app.log("srv","Timersolution do");
            }
        }
    }
}
