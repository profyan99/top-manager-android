package com.topmngr.game.Ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.topmngr.game.Utils.Assets;
import com.topmngr.game.Utils.SoundManager;

/**
 * Created by PROFYAN on 17.04.2017.
 */
public class AdvanceTextButton extends TextButton {
    public AdvanceTextButton(String text, TextButtonStyle style) {
        super(text, style);
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.playSound(Assets.instance.sounds.get(Assets.Sounds.CLICK));
                super.clicked(event, x, y);
            }
        });
    }
}
