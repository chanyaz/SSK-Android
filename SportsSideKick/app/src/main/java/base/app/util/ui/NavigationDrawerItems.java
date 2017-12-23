package base.app.util.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar Marinkovic on 15-Mar-17.
 */

public class NavigationDrawerItems {

    private List<Boolean> itemSelector;
    private int position = 0;

    public List<Boolean> getItemSelector() {
        return itemSelector;
    }

    public void setItemSelector(List<Boolean> itemSelector) {
        this.itemSelector = itemSelector;
    }

    public void generateList(int size) {
        itemSelector = new ArrayList<>();
        for (int i = 0; i < size; i++) {

            itemSelector.add(new Boolean(false));
        }
        itemSelector.set(0, true);

    }

    public boolean getItemById(int id) {
        return itemSelector.get(id).booleanValue();
    }


    public void setByPosition(int position) {
        this.position = position;
        for (int i = 0; i < itemSelector.size(); i++) {
            if (i == position) {
                itemSelector.set(i, true);
            } else {
                itemSelector.set(i, false);
            }

        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private static NavigationDrawerItems instance;

    private NavigationDrawerItems() {

    }

    public static NavigationDrawerItems getInstance() {
        if (instance == null) {
            instance = new NavigationDrawerItems();
        }
        return instance;
    }
}
