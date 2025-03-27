package ca.gbc.managex.AdminControl.Classes;

public class ItemSize {
    private String size;
    private double price;

    public ItemSize(String size, double price) {
        this.size = size;
        this.price = price;
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String info(){
        return "Size: " + size + " | Price: "+price;
    }
    public ItemSize(){

    }
}
