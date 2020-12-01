package com.topmngr.game.Ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.topmngr.game.Utils.Assets;

import static com.topmngr.game.Screen.TopManager.screenX;
import static com.topmngr.game.Screen.TopManager.screenY;

class AdvanceDialog extends Dialog {

    private Table dialogTable;
    private Label msg;

    private AdvanceDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
        setPosition(screenX/2, screenY/2);
        setSize(400,200);

        dialogTable = new Table();
        dialogTable.setSize(400,150);
        msg = new Label(Assets.PlayerData.getStats(), Assets.instance.mainStyle);

        dialogTable.add(msg).center().expand();
        dialogTable.row().padTop(50);
        add(dialogTable).expand();
        setMovable(false);

    }
    private void setText(String text) {
        msg.setText(text);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        dialogTable.setSize(width, height - 50);
    }
    private void setButton(Button button) {
        dialogTable.add(button).width(100).height(50).center();
    }
    public static Builder newBuilder(String title, WindowStyle windowStyle) {
        return new AdvanceDialog(title, windowStyle).new Builder();
    }


    public class Builder {
        private Builder() {
            // private constructor
        }

        public Builder size(int width, int height) {
            AdvanceDialog.this.setSize(width,height);
            return this;
        }
        public Builder text(String text) {
            AdvanceDialog.this.setText(text);
            return this;
        }
        public Builder button(Button button) {
            AdvanceDialog.this.setButton(button);
            return this;
        }


        public AdvanceDialog build() {
            return AdvanceDialog.this;
        }
    }
}
