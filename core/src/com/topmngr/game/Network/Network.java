package com.topmngr.game.Network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.topmngr.game.Game.Player;
import com.topmngr.game.Game.PlayersStruct;

public class Network {
    public final static int SERVER_PORT = 54555;
    public final static int MAX_PERIODS = 16;
    final static int TIME_RELOAD_MILLS = 1000*60;
    public static  final int COUNT_REPEAT_TIMER_SOLUTIONS = (5 * 60);
    public final static int LIMIT_LOAN = 50000;
    public final static int EXTR_LIMIT_LOAN = 50000;
    public final static float BANK_RATE = 0.1f;
    public final static float EXTR_BANK_RATE = 0.4f;
    public final static float PROFIT_TAX = 0.25f;
    public static final String SERVER_IP = "194.87.146.204";

    public enum PlayersState {
        THINK, WAIT, BANKRUPT
    }

    static void register(EndPoint point) {
        Kryo kryo = point.getKryo();
        kryo.register(ServerMessage.class);
        kryo.register(String[].class);
        kryo.register(PlayerMessage.class);
        kryo.register(Disconnected.class);
        kryo.register(PlayersAmount.class);
        kryo.register(UpdatePlayer.class);
        kryo.register(PlayerSolutions.class);
        kryo.register(Register.class);
        kryo.register(Connected.class);
        kryo.register(Player.class);
        kryo.register(ChangePlayerState.class);
        kryo.register(PlayersStruct.class);
        kryo.register(PlayersState.class);
        kryo.register(GameState.class);
        kryo.register(ChangeGameState.class);
        kryo.register(UpdateRating.class);
        kryo.register(RatingStruct.class);
        kryo.register(RatingStruct[].class);
        kryo.register(NewGame.class);
        kryo.register(UpdateTimerSolution.class);
        kryo.register(ConnectRoom.class);
        kryo.register(RoomsStruct.class);
        kryo.register(RoomsStruct[].class);
        kryo.register(UpdateRooms.class);
        kryo.register(DisconnectRoom.class);
        kryo.register(CreateRoom.class);
        kryo.register(DeleteAllRooms.class);
        kryo.register(NeedUpdateRooms.class);
    }

    public enum GameState
    {
        WAITING,GAME, END, END_TOURNAMENT
    }

    static class Register {
        public String name;
    }

    static class ServerMessage {
        String message;
    }

    static class PlayerMessage {
        public String message;
        public String name;
        public String color;
    }

    static class Disconnected {
        String reason;
    }

    static class Connected {

    }

    static class PlayersAmount {
        int amount;
    }

    static public class ChangePlayerState {
        public PlayersStruct player;
    }

    static class ChangeGameState {
        GameState state;
    }

    static class UpdatePlayer {
        Player player;
    }

    static class NewGame {

    }

    static class UpdateTimerSolution {
        int time;
    }

    public static class RatingStruct {
        public String name;
        public String rif;
        public double markPart;
        public int
                cost,
                revenue,
                netProfit,
                accProfit;
    }

    static class DisconnectRoom {

    }

    static class UpdateRating {
        RatingStruct structs[];
    }

    public static class RoomsStruct {
        public String name;
        public boolean hasPass;
        public int
                maxPlayers,
                players,
                maxPeriods,
                periods;
        public GameState state;
    }

    static class UpdateRooms {
        RoomsStruct structs[];
    }

    static class CreateRoom {
        int players,periods;
        String name, password;
        boolean isTournament;
    }

    static class ConnectRoom {
        int room;
        String name, password;
    }

    static class DeleteAllRooms {

    }

    static class NeedUpdateRooms {

    }

    static class PlayerSolutions {
         int
                gPlayerCost, // цена изм.
                gPlayerProduction, // Производство изм.
                gPlayerMarketing, // Маркетинг изм.
                gPlayerInvestments, // Инвестиции изм.
                gPlayerResAndDev; // НИОКР изм.
    }
}
