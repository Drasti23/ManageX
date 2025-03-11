package ca.gbc.managex.POS;

import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.AdminControl.Classes.ItemSize;

public class OrderItem {
    private Item item;
    private ItemSize size;
    private int quantity;
    private String note;
    private boolean customized;

    public OrderItem(Item item, ItemSize size) {
        this.item = item;
        this.size = size;
        this.quantity = 1; // Default quantity is 1
        this.note = "";
        this.customized = false;
    }


    public void setCustomized(boolean customized){
        this.customized = customized;
    }
    public boolean getCustomized(){
        return this.customized;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Item getItem() {
        return item;
    }

    public ItemSize getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        this.quantity++; // Increase quantity when the same item is selected again
    }
    public void decreaseQuantity(){
            this.quantity--;

    }
}
