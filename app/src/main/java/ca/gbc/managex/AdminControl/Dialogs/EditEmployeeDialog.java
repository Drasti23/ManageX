package ca.gbc.managex.AdminControl.Dialogs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.R;

public class EditEmployeeDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private String userId, empId, selectedPosition,joiningDate,emp_pass,emp_rate,emp_type;
    private TextInputEditText etEmpFName, etEmpLName, etEmpEmail, etEmpPhone, etEmpCode,etEmpType,etEmpRate;
    private Spinner spinnerPosition;
    private Button btnUpdateEmpData;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    public static EditEmployeeDialog newInstance(String userId, String empId, String fname, String lname, String email, String phone, String code, String position,String joiningDate,String emp_pass,String emp_rate, String emp_type) {
        EditEmployeeDialog fragment = new EditEmployeeDialog();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("empId", empId);
        args.putString("fname", fname);
        args.putString("lname", lname);
        args.putString("email", email);
        args.putString("phone", phone);
        args.putString("code", code);
        args.putString("position", position);
        args.putString("joiningDate", joiningDate);
        args.putString("emp_pass", emp_pass);
        args.putString("emp_rate",emp_rate);
        args.putString("emp_type",emp_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            empId = getArguments().getString("empId");
            databaseReference = firebaseDatabase.getReference().child("Users").child(userId);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_employee, container, false);

        etEmpFName = view.findViewById(R.id.etEmpFName);
        etEmpLName = view.findViewById(R.id.etEmpLName);
        etEmpEmail = view.findViewById(R.id.etEmpEmail);
        etEmpPhone = view.findViewById(R.id.etEmpPhone);
        etEmpCode = view.findViewById(R.id.etEmpCode);
        etEmpType = view.findViewById(R.id.etEmployeeEmpType);
        etEmpRate = view.findViewById(R.id.etEmployeePayRate);
        spinnerPosition = view.findViewById(R.id.spinner);
        btnUpdateEmpData = view.findViewById(R.id.btnUpdateEmpData);
        List<String> positions = new ArrayList<>();
        positions.add("Crew");
        positions.add("Manager");
        positions.add("Cleaner");
        positions.add("Partner");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,positions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(dataAdapter);
        if (getArguments() != null) {
            String name = getArguments().getString("name", "");
            etEmpFName.setText(getArguments().getString("fname", ""));
            etEmpLName.setText(getArguments().getString("lname", ""));
            etEmpEmail.setText(getArguments().getString("email", ""));
            etEmpPhone.setText(getArguments().getString("phone", ""));
            etEmpCode.setText(getArguments().getString("code", ""));
            joiningDate = getArguments().getString("joiningDate", "");
            emp_rate =getArguments().getString("emp_rate","");
            emp_type = getArguments().getString("emp_type","");
            emp_pass = getArguments().getString("emp_pass", "");
            etEmpRate.setText(emp_rate);
            etEmpType.setText(emp_type);
            setSpinnerValue(spinnerPosition, getArguments().getString("position", ""));
        }

        btnUpdateEmpData.setOnClickListener(v -> updateEmployeeData());

        return view;
    }

    private void fetchEmployeeData() {
        // Fetch employee data from Firebase and populate fields
    }

    private void updateEmployeeData() {
        String firstName = etEmpFName.getText().toString().trim();
        String lastName = etEmpLName.getText().toString().trim();
        String email = etEmpEmail.getText().toString().trim();
        String phone = etEmpPhone.getText().toString().trim();
        String empCode = etEmpCode.getText().toString().trim();
        String position = spinnerPosition.getSelectedItem().toString();
        String type = etEmpType.getText().toString().trim();
        String rate = etEmpRate.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || empCode.isEmpty() || emp_pass.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int empCodeInt = Integer.parseInt(empCode);
            int empPassInt = Integer.parseInt(emp_pass);
            double empRateDouble = Double.parseDouble(rate);

            Employee employee = new Employee(Integer.parseInt(empId), firstName, lastName, email, phone, joiningDate, position, type,empCodeInt, empPassInt,empRateDouble);
            databaseReference.child("employeeInfo").child(String.valueOf(empId)).setValue(employee);
            dismiss();

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedPosition = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}