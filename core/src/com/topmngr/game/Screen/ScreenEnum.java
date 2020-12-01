package com.topmngr.game.Screen;

import com.badlogic.gdx.Screen;
import com.topmngr.game.Network.GameClient;


enum ScreenEnum {

    MENU {

        @Override
        protected Screen getScreenInstance(Object... params) {
            return new MenuScreen((GameClient)params[0]);
        }
        @Override
        protected Screen getScreenInstance() {
            return new MenuScreen();
        }

    },
    INTRO {
        @Override
        protected Screen getScreenInstance() {
            return new IntroScreen();
        }
    },
    GAME {
        @Override
        protected Screen getScreenInstance(Object... params) {
            return new GameScreen(params[0]);
        }

    };

    protected Screen getScreenInstance(Object... params) {
        return null;
    }
    protected Screen getScreenInstance() {

        return null;
    }
}
