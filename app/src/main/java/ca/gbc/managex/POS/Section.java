package ca.gbc.managex.POS;

import java.util.ArrayList;

import ca.gbc.managex.AdminControl.Classes.Item;

public class Section {
    private String name;
    private ArrayList<Item> items;

    public Section(String name, ArrayList<Item> items) {
        this.name = name;
        this.items = items;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
