package com.topmngr.game.Ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created by PROFYAN on 03.04.2017.
 */
public class ServerList<T extends ListRow> extends Table
{

    private Array<T> items = new Array<T>();
    private int selectedIndex;
    private boolean selectable = true;
    private int space = 0;

    public ServerList()
    {
        this(null);
    }

    @Override
    public void clear() {
        super.clear();
        items.clear();
        selectedIndex = -1;
    }

    public ServerList(T[] items)
    {
        top();
        //setDebug(true);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
        this.setTouchable(Touchable.enabled);

        defaults().expandX().fillX();
        if(items != null) {
            for (T item : items) {
                addItem(item);
            }
        }
    }

    /** Sets whether this List's items are selectable. If not selectable, touch events will not be consumed. */
    public void setSelectable(boolean selectable)
    {
        this.selectable = selectable;
    }

    /** Sets space between items in list */
    public void setSpace(int space) {
        this.space = space;
        this.defaults().padBottom(space);
    }

    /** @return distance between items in list */
    public int getSpace() {
        return space;
    }

    /** @return True if items are selectable. */
    public boolean isSelectable()
    {
        return selectable;
    }

    /** @return The index of the currently selected item. The top item has an index of 0. Nothing selected has an index of -1. */
    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    /** @return The ListRow of the currently selected item, or null if the list is empty or nothing is selected. */
    public T getSelection()
    {
        if (items.size == 0 || selectedIndex == -1)
            return null;

        return items.get(selectedIndex);
    }

    public Array<T> getItems()
    {
        return items;
    }

    public void addItem(final T item)
    {
        item.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(selectedIndex != -1)
                    items.get(selectedIndex).setIsSelected(false);

                selectedIndex = items.indexOf(item, false);
                item.setIsSelected(true);
                //System.out.println("CLICKED FUCK: "+selectedIndex);
            }

        });

        items.add(item);
        add(item);
        row().padTop(5);
    }

    public void removeItem(T item)
    {
        item.remove();
        int _index = items.indexOf(item, false);
        items.removeValue(item, false);
        items.get(_index).setIsSelected(true);
    }


    /**
     * Remove the selected row
     */
    public void removeSelected()
    {
        if (selectedIndex < 0 || selectedIndex >= items.size)
            return;

        removeItem(items.get(selectedIndex));
    }

}
