package ca.gbc.managex.AdminControl.Classes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
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
