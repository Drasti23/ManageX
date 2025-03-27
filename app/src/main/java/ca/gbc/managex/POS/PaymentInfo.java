package ca.gbc.managex.POS;

import java.util.ArrayList;

public class PaymentInfo {
    ArrayList<OrderItem> orderItemList;
    private int numberOfItemModified;
    private double subTotal;
    private double tax;
    private double totalAmountToPay;
    private int numberOfItems;
    private double amountPayed;
    private double changeGiven;

    public PaymentInfo(){

    }



    public PaymentInfo(ArrayList<OrderItem> orderItemList, int itemModified, double subTotal, double tax, double totalAmountToPay, int numberOfItems) {
        this.orderItemList = orderItemList;
        this.numberOfItemModified = itemModified;
        this.subTotal = subTotal;
        this.tax = tax;
        this.totalAmountToPay = totalAmountToPay;
        this.numberOfItems = numberOfItems;
    }

    public ArrayList<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(ArrayList<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public double getAmountPayed() {
        return amountPayed;
    }

    public void setAmountPayed(double amountPayed) {
        this.amountPayed = amountPayed;
    }

    public double getChangeGiven() {
        return changeGiven;
    }

    public void setChangeGiven(double changeGiven) {
        this.changeGiven = changeGiven;
    }

    public double getNumberOfItemModified() {
        return numberOfItemModified;
    }

    public void setNumberOfItemModified(int numberOfItemModified) {
        this.numberOfItemModified = numberOfItemModified;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotalAmountToPay() {
        return totalAmountToPay;
    }

    public void setTotalAmountToPay(double totalAmountToPay) {
        this.totalAmountToPay = totalAmountToPay;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

}
