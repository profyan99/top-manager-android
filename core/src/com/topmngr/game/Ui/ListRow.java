package com.topmngr.game.Ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by PROFYAN on 03.04.2017.
 */
public abstract class ListRow extends Table
{

    private boolean isSelected;
    private ListRowStyle style;

    public void setStyle(ListRowStyle style)
    {
        this.style = style;
    }

    public void setIsSelected(boolean isSelected)
    {
        this.isSelected = isSelected;

        if(style == null)
            return;
    }

    public boolean isSelected()
    {
        return isSelected;
    }

    public static class ListRowStyle
    {
        public ListRowStyle()
        {

        }
    }
}