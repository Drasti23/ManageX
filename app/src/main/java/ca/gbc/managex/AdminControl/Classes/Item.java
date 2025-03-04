package ca.gbc.managex.AdminControl.Classes;


import java.util.ArrayList;

public class Item {
    private String name;
    private ArrayList<ItemSize> itemSizeList;

    public Item(String name, ArrayList<ItemSize> itemSizeList) {
        this.name = name;
        this.itemSizeList = itemSizeList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ItemSize> getItemSizeList() {
        return itemSizeList;
    }

    public void setItemSizeList(ArrayList<ItemSize> itemSizeList) {
        this.itemSizeList = itemSizeList;
    }
    public Item(){

    }
}
