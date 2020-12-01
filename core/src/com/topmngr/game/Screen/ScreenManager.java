
package com.topmngr.game.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;


final class ScreenManager {
    private static ScreenManager instance;
    private Game game;

    private ScreenManager() {

    }

    public static ScreenManager getInstance() {
        if (null == instance) {
            instance = new ScreenManager();
        }
        return instance;
    }

    void initialize(Game game) {
        this.game = game;

    }
    Screen getCurrentScreen() {
        return game.getScreen();
    }
    void show(ScreenEnum screen, Object... params) {
        if (null == game) return;


        if(params.length == 0) {
            game.setScreen(screen.getScreenInstance());
        }
        else {
            game.setScreen(screen.getScreenInstance(params));
        }
    }

}

