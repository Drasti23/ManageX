package ca.gbc.managex.POS;

public class OrderBill {
    private String orderId;
    private int orderNumberOfTheDay;
    private String orderTimeAndDate;
    private boolean paymentStatus;
    private boolean dinInOrder;
    private int tableNumber;
    private PaymentInfo paymentInfo;
    private boolean open;
    private String paymentTimeAndDate;
    private String paymentMethod;
    private String orderTaker;
    private String date;

    public OrderBill(String orderId, int orderNumberOfTheDay, String orderTimeAndDate, boolean paymentStatus,PaymentInfo paymentInfo,String date) {
        this.orderId = orderId;
        this.orderNumberOfTheDay = orderNumberOfTheDay;
        this.orderTimeAndDate = orderTimeAndDate;
        this.paymentStatus = paymentStatus;
        dinInOrder = false;
        tableNumber=0;
        this.paymentInfo = paymentInfo;
        open = true;
        paymentTimeAndDate="";
        paymentMethod="";
        orderTaker="";
        this.date = date;
    }
    public OrderBill(){

    }

    public OrderBill(String orderId, int orderNumberOfTheDay, String orderTimeAndDate, boolean paymentStatus,boolean dinInOrder,int tableNumber,PaymentInfo paymentInfo,String date) {
        this.orderId = orderId;
        this.orderNumberOfTheDay = orderNumberOfTheDay;
        this.orderTimeAndDate = orderTimeAndDate;
        this.paymentStatus = paymentStatus;
        this.dinInOrder = dinInOrder;
        this.tableNumber = tableNumber;
        this.paymentInfo = paymentInfo;
        this.open = open;
        paymentTimeAndDate="";
        paymentMethod="";
        orderTaker="";
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getOrderTaker() {
        return orderTaker;
    }

    public void setOrderTaker(String orderTaker) {
        this.orderTaker = orderTaker;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentTimeAndDate() {
        return paymentTimeAndDate;
    }

    public void setPaymentTimeAndDate(String paymentTimeAndDate) {
        this.paymentTimeAndDate = paymentTimeAndDate;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderNumberOfTheDay() {
        return orderNumberOfTheDay;
    }

    public void setOrderNumberOfTheDay(int orderNumberOfTheDay) {
        this.orderNumberOfTheDay = orderNumberOfTheDay;
    }

    public String getOrderTimeAndDate() {
        return orderTimeAndDate;
    }

    public void setOrderTimeAndDate(String orderTimeAndDate) {
        this.orderTimeAndDate = orderTimeAndDate;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isDinInOrder() {
        return dinInOrder;
    }

    public void setDinInOrder(boolean dinInOrder) {
        this.dinInOrder = dinInOrder;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
}