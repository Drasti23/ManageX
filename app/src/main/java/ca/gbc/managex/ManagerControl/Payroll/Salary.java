package ca.gbc.managex.ManagerControl.Payroll;

public class Salary {
    private double payRate;
    private double hours;
    private double taxRate;
    private double bonus;
    private double overTimePayRate;
    private double grossPay;
    private double netPay;
    public Salary(){

    }

    public Salary(double payRate, double hours, double taxRate, double bonus, double overTimePayRate) {
        this.payRate = payRate;
        this.hours = hours;
        this.taxRate = taxRate;
        this.bonus = bonus;
        this.grossPay = 0;
        this.netPay = 0;
        this.overTimePayRate = overTimePayRate;
    }


    public double calculateGrossPay(double payRate, double hours, double bonus){
        if(hours>40){
            double remainingHours = hours-40;
            double overTimePay = remainingHours*payRate;
            grossPay = payRate*40;
            grossPay = overTimePay+grossPay;
        }
        else{
            grossPay = (payRate * hours) + bonus;
        }
       return grossPay;
    }
    public double calculateReceivingPay(double taxRate, double grossPay){
        double moneyToDeduct = (grossPay * taxRate)/100;
        netPay = (grossPay-moneyToDeduct);
        return netPay;
    }

    public double getPayRate() {
        return payRate;
    }

    public void setPayRate(double payRate) {
        this.payRate = payRate;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public double getOverTimePayRate() {
        return overTimePayRate;
    }

    public void setOverTimePayRate(double overTimePayRate) {
        this.overTimePayRate = overTimePayRate;
    }

    public double getGrossPay() {
        return grossPay;
    }

    public void setGrossPay(double grossPay) {
        this.grossPay = grossPay;
    }

    public double getNetPay() {
        return netPay;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }
}
