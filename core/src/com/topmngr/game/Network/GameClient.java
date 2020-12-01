package com.topmngr.game.Network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.topmngr.game.Screen.*;
import com.topmngr.game.Utils.Assets;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by PROFYAN on 26.01.2017.
 */
public class GameClient {
    private Client client;
    private ClientScreen gameScreen;


    public GameClient(ClientScreen screen, final InetAddress address) {
        this.gameScreen = screen;
        client = new Client();
        client.start();
        Network.register(client);


        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void connected (Connection connection) {
                Network.Register register = new Network.Register();
                register.name = Assets.PlayerData.getName();
                client.sendTCP(register);
            }

            public void received (Connection connection, Object object) {
                if (object instanceof Network.Disconnected) {
                    gameScreen.onDisconnect(((Network.Disconnected)object).reason);
                    return;
                }
                if (object instanceof Network.Connected) {
                    gameScreen.onServerSendMessage("Подключен успешно!");
                    return;
                }
                if (object instanceof Network.PlayerMessage) {

                    gameScreen.onPlayerSendMessage(
                            ((Network.PlayerMessage)object).message,
                            ((Network.PlayerMessage)object).name,
                            ((Network.PlayerMessage)object).color);
                    return;
                }
                if (object instanceof Network.ServerMessage)
                {
                    gameScreen.onServerSendMessage(((Network.ServerMessage)object).message);
                    return;
                }
                if (object instanceof Network.PlayersAmount)
                {
                    gameScreen.onChangePlayersAmount(((Network.PlayersAmount) object).amount);
                    return;
                }
                if (object instanceof Network.UpdatePlayer)
                {
                    gameScreen.onPlayerUpdate(((Network.UpdatePlayer) object).player);
                    return;
                }
                if (object instanceof Network.ChangePlayerState)
                {
                    gameScreen.onChangePlayerState((Network.ChangePlayerState)object);
                    return;
                }
                if(object instanceof Network.ChangeGameState)
                {
                    gameScreen.onChangeGameState(((Network.ChangeGameState) object).state);
                    return;
                }
                if(object instanceof Network.UpdateRating)
                {
                    gameScreen.onUpdateRating(((Network.UpdateRating)object).structs);
                    return;
                }
                if(object instanceof Network.UpdateRooms) {
                    gameScreen.onUpdateRooms(((Network.UpdateRooms)object).structs);
                    return;
                }
                if(object instanceof Network.UpdateTimerSolution) {
                    gameScreen.onTimerSolutionUpdate(((Network.UpdateTimerSolution)object).time);
                    return;
                }
            }

            public void disconnected (Connection connection) {
                client.stop();
            }
        }));

        new Thread("Connect client") {
            public void run () {
                try {
                    sleep(500);
                    client.connect(5000,address, Network.SERVER_PORT,Network.SERVER_PORT);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    gameScreen.onEndDiscoverHosts(null, true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        client.setTimeout(10000);

    }

    public void sendMessage(String message) {
        Network.PlayerMessage mess = new Network.PlayerMessage();
        mess.message = message;
        mess.name = Assets.PlayerData.getName();
        client.sendTCP(mess);
    }
    public void sendPlayerSolution(int cost, int amoumt, int mark, int inv, int nir) {
        Network.PlayerSolutions solutions = new Network.PlayerSolutions();
        solutions.gPlayerCost = cost;
        solutions.gPlayerProduction = amoumt;
        solutions.gPlayerMarketing = mark;
        solutions.gPlayerInvestments = inv;
        solutions.gPlayerResAndDev = nir;
        client.sendTCP(solutions);
    }
    public void stop() {
        Gdx.app.log("client", "Stop client");
        client.stop();
    }
    public void newGame() {
        client.sendTCP(new Network.NewGame());
    }
    public void createRoom(String name, int maxPlayers, int maxPeriods, String password, boolean isTournament) {
        Network.CreateRoom room = new Network.CreateRoom();
        room.name = name;
        room.periods = maxPeriods;
        room.players = maxPlayers;
        room.password = password;
        room.isTournament = isTournament;
        client.sendTCP(room);
    }
    public void connectRoom(String name, int roomID, String password) {
        Network.ConnectRoom connectRoom = new Network.ConnectRoom();
        connectRoom.name = name;
        connectRoom.room = roomID;
        connectRoom.password = password;
        client.sendTCP(connectRoom);
    }
    public void disconnectRoom() {
        client.sendTCP(new Network.DisconnectRoom());
    }
    public void setClientScreen(ClientScreen screen) {
        gameScreen = screen;
    }
    public void deleteAllRooms() {
        client.sendTCP(new Network.DeleteAllRooms());
    }
    public void needUpdateRooms() {
        client.sendTCP(new Network.NeedUpdateRooms());
    }
}
