package com.topmngr.game.Ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.topmngr.game.Utils.Assets;


/**
 * Created by PROFYAN on 05.02.2017.
 */
public class ChatList2 extends Widget implements Cullable {
    private Array<String> items;
    float xX, yY;
    float itemHeight;
    float actorHeight;
    int abs;
    private Rectangle cullingArea;
    private float prefWidth, prefHeight;
    private final static int OTSTUP_SMALL = 10;
    private final static int OTSTUP_BIG = 70;

    public ChatList2(Array<String> array) {
        items = new Array<String>();
        items.addAll(array);


    }
    public void setItems(Array<String> array) {
        float oldPrefWidth = getPrefWidth(), oldPrefHeight = getPrefHeight();
        items.clear();
        items.addAll(array);
        invalidate();
        if (oldPrefWidth != getPrefWidth() || oldPrefHeight != getPrefHeight())
            invalidateHierarchy();
    }
    public void layout () {

        itemHeight =  Assets.instance.mainFontSmall.getLineHeight();
        int pref2 = 0;
        prefWidth = 0;
        Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
        GlyphLayout layout = layoutPool.obtain();
        for (int i = 0; i < items.size; i++) {
            layout.setText(Assets.instance.mainFontSmall, items.get(i));
            prefWidth = Math.max(layout.width, prefWidth);
            pref2 += itemHeight;
        }
        layoutPool.free(layout);
        prefHeight = pref2 + items.size*OTSTUP_SMALL + OTSTUP_BIG;
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        xX = getX();
        yY = getY();
        actorHeight = getHeight();
        abs = 0;
        for(int i = 0; i < items.size; i++) { //
            if (cullingArea == null || (actorHeight - abs <= cullingArea.y + cullingArea.height && actorHeight - abs >= cullingArea.y)) {
                Assets.instance.mainFontSmall.draw(batch, items.get(i), xX + 20, yY + actorHeight - abs);
            }
            else if (actorHeight - abs < cullingArea.y) {
                break;
            }
            abs += OTSTUP_SMALL + itemHeight;
        }
    }
    public float getPrefWidth () {
        validate();
        return prefWidth;
    }

    public float getPrefHeight () {
        validate();
        return prefHeight;
    }
    @Override
    public void setCullingArea(Rectangle cullingArea) {
        this.cullingArea = cullingArea;
    }
}
