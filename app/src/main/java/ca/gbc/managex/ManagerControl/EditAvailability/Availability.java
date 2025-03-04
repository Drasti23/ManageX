package ca.gbc.managex.ManagerControl.EditAvailability;

import java.util.HashMap;
import java.util.Map;

public class Availability {
    private int empId;
    private String empName;
    private Map<String,String> availability;

    public Availability(){

    }

    // constructor for getting availability from database
    public Availability(int empId, String empName){
        this.empId = empId;
        this.empName = empName;
        availability = new HashMap<>();
    }

    // constructor for setting availability to database
    public Availability(int empId, String empName, Map<String, String> availability) {
        this.empId = empId;
        this.empName = empName;
        this.availability = availability;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Map<String, String> getAvailability() {
        return availability;
    }

    public void setAvailability(Map<String, String> availability) {
        this.availability = availability;
    }
}
