package com.topmngr.game.Screen;

import com.topmngr.game.Network.Network;
import com.topmngr.game.Game.Player;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class ClientScreen {
    public  void onDisconnect(String reason) {

    }
    public void onServerSendMessage(String message) {

    }
    public void onPlayerSendMessage(String txt, String name, String color) {

    }
    public void onChangePlayersAmount(int amount) {

    }
    public void onPlayerUpdate(Player player) {

    }
    public void onChangePlayerState(Network.ChangePlayerState changePlayerState) {

    }
    public void onChangeGameState(Network.GameState state) {

    }
    public void onUpdateRating(Network.RatingStruct[] struct) {

    }
    public void onTimerSolutionUpdate(int time) {

    }
    public void onUpdateRooms(Network.RoomsStruct[] struct) {

    }
    public synchronized void onEndDiscoverHosts(final InetAddress address, boolean mode) {

    }
}
