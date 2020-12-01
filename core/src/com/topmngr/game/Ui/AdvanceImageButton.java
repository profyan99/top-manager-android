package com.topmngr.game.Ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.topmngr.game.Utils.Assets;
import com.topmngr.game.Utils.SoundManager;

/**
 * Created by PROFYAN on 17.04.2017.
 */
public class AdvanceImageButton extends ImageButton {
    public AdvanceImageButton(Skin skin) {
        super(skin);
        addSound();
    }

    public AdvanceImageButton(Skin skin, String styleName) {
        super(skin, styleName);
        addSound();
    }

    public AdvanceImageButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        super(imageUp, imageDown, imageChecked);
        addSound();
    }

    private void addSound() {
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.playSound(Assets.instance.sounds.get(Assets.Sounds.CLICK));
                super.clicked(event, x, y);
            }
        });
    }
}
