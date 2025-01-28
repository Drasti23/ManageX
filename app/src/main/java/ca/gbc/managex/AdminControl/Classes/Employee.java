package ca.gbc.managex.AdminControl.Classes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Employee {
    public Employee(int id, String firstName, String lastName, String email, String contactNumber, String joiningDate, String position, int empCode, int empPass) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.joiningDate = joiningDate;
        this.position = position;
        this.empCode = empCode;
        this.empPass = empPass;
    }

    public Employee(){

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getEmpCode() {
        return empCode;
    }

    public void setEmpCode(int empCode) {
        this.empCode = empCode;
    }

    public int getEmpPass() {
        return empPass;
    }

    public void setEmpPass(int empPass) {
        this.empPass = empPass;
    }

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String joiningDate;
    private String position;
    private int empCode;
    private int empPass;

}
