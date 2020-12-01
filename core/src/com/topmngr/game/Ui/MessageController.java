package com.topmngr.game.Ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashSet;
import java.util.Set;

public class MessageController {
    private Stage stage;
    private long lastStart = 0;
    private float delay = 0;
    private final Color DEFAULT_COLOR = Colors.get("cPureWhite");
    private final float DEFAULT_DURATION = 3f;

    public enum messageType  {
        VALUES_SUCCESS,
        LIMIT_COST,
        LIMIT_GAME_VALUES,
        WRONG_AMOUNT,
        FILL_FIELD,
        LIMIT_FREQUENCY,
        TIME_IS_OVER,
        NONE

    };
    final private String messConst[] = {
            "Значения приняты!",
            "Цена не может быть ниже 15 и больше 200!",
            "Значения не могут быть отрицательными и больше 50 000!",
            "Неверное кол-во продукции!",
            "Заполните поля!",
            "Сообщение раз в 2 секунды!",
            "Время на подачу решений вышло!",
    };
    private Set<messageType> messageSet;

    public MessageController(Stage stage) {
        this.stage = stage;
        messageSet = new HashSet<messageType>();
    }
    public void showMsg(String text) {
        show(text, DEFAULT_COLOR, DEFAULT_DURATION, messageType.NONE);
    }
    public void showMsg(messageType type) {
        if(messageSet.contains(type))
            return;

        messageSet.add(type);
        show(messConst[type.ordinal()], DEFAULT_COLOR, DEFAULT_DURATION, type);
    }
    public void showMsg(String text, Color color) {
        show(text, color, DEFAULT_DURATION, messageType.NONE);
    }
    public void showMsg(String text, float duration) {
        show(text, DEFAULT_COLOR, duration, messageType.NONE);
    }
    public void showMsg(String text, Color color, float duration) {
        show(text,color,duration, messageType.NONE);
    }
    private void show(String text, Color color, float duration, messageType type) {
        long nowTime = System.currentTimeMillis();
        float nDelay = 0.5f + delay - (System.currentTimeMillis()-lastStart)/1000f;
        if(nDelay < 0)
            nDelay = 0;
        new TempMessage(text,
                nDelay,
                duration,
                stage,
                color,
                this,
                type);
        lastStart = nowTime+(long)(nDelay*1000);
        delay = duration;
    }
    void reset(messageType type) {
        messageSet.remove(type);
    }
}
