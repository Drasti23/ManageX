package ca.gbc.managex.AdminControl.Dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.R;
import ca.gbc.managex.databinding.AddEmployeeDialogBinding;
import lombok.extern.slf4j.Slf4j;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddEmployeeDialog extends DialogFragment implements AdapterView

        .OnItemSelectedListener {
    private Toolbar toolbar;
    private Spinner spinner;

    private Button datePicker,saveEmployee;
    private String selectedDate,selectedPosition,firstName,lastName,contactNumber,email,empType;
    private double payRate;
    public TextInputEditText etFName,etLName,etContactNumber,etEmail,etPass,etCode,etPayRate,etEmpType;
    private int code,pass;
    private LocalDate localDate = LocalDate.now();
    private AddEmployeeDialogBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("Users").child(user.getUid());

    public static final String TAG = "addEmployeeDialog";
    public static AddEmployeeDialog display(FragmentManager fragmentManager){
        AddEmployeeDialog dialog = new AddEmployeeDialog();
        dialog.show(fragmentManager,TAG);
        return dialog;
    }
    public interface EmployeeCountCallback {
        void onCountRetrieved(int count);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.add_employee_dialog,container,false);
        binding = AddEmployeeDialogBinding.inflate(inflater,container,false);
        toolbar = view.findViewById(R.id.toolbar);
        spinner = view.findViewById(R.id.spinner);
        saveEmployee = view.findViewById(R.id.btnSaveEmpData);
        datePicker = (Button) view.findViewById(R.id.datePicker);
        etFName = view.findViewById(R.id.etEmpFName);
        etLName = view.findViewById(R.id.etEmpLName);
        etContactNumber = view.findViewById(R.id.etEmpPhone);
        etCode = view.findViewById(R.id.etEmpCode);
        etPass = view.findViewById(R.id.etEmpPass);
        etEmail = view.findViewById(R.id.etEmpEmail);
        etPayRate = view.findViewById(R.id.etPayRate);
        etEmpType = view.findViewById(R.id.etEmpType);
        spinner.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.inflateMenu(R.menu.dialog_menu_add_emp);
        toolbar.setNavigationIcon(R.drawable.baseline_close_24);
        toolbar.setNavigationOnClickListener(v-> dismiss());
        datePicker.setOnClickListener(v->{
            datePicker.setText(openDatePickerDialog());
        });

        //list of positions
        List<String> positions = new ArrayList<String>();
        positions.add("Crew");
        positions.add("Manager");
        positions.add("Cleaner");
        positions.add("Partner");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,positions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        //error checking
        checkErrorInEditText(etContactNumber,10);
        checkErrorInEditText(etPass,4);
        checkErrorInEditText(etCode,4);
        saveEmployee.setOnClickListener(v -> {
            firstName = etFName.getText().toString();
            lastName = etLName.getText().toString();
            contactNumber = etContactNumber.getText().toString();
            email = etEmail.getText().toString();
            empType = etEmpType.getText().toString();
            String inputCode = etCode.getText().toString();
            String inputPass = etPass.getText().toString();
            String inputPayRate = etPayRate.getText().toString();


            if (!(inputCode.isEmpty() || inputPass.isEmpty() || inputPayRate.isEmpty())) {
                code = Integer.parseInt(inputCode);
                pass = Integer.parseInt(inputPass);
                payRate = Double.parseDouble(inputPayRate);

                if (firstName.isEmpty() || lastName.isEmpty() || contactNumber.isEmpty() || email.isEmpty() || selectedDate == null || selectedPosition == null || empType.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                } else {
                    getNumberOfEmployee(count -> {
                        int newId = count + 1; // Ensure ID is updated asynchronously
                        Employee employee = new Employee(newId, firstName, lastName, email, contactNumber, selectedDate, selectedPosition,empType, code, pass,payRate);
                        //Employee saved yo
                        reference.child("employeeInfo").child(String.valueOf(newId)).setValue(employee);
                        dismiss();
                    });
                }
            } else {
                Toast.makeText(getContext(), "Please fill required fields", Toast.LENGTH_SHORT).show();
            }
        });



    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedPosition = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private String openDatePickerDialog(){
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                selectedDate = i + "/" + i1 + "/"+ i2;
                Toast.makeText(getContext(),"Date selected : " + selectedDate,Toast.LENGTH_SHORT).show();
            }
        },localDate.getYear(),localDate.getMonthValue(),localDate.getDayOfMonth());
        dialog.show();
        datePicker.setText(selectedDate);
        return selectedDate;
    }
    public void checkErrorInEditText(TextInputEditText et, int limit){
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.length() < limit){
                et.setError("required digit : " + limit);
            }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width,height);
        }
    }

    public void getNumberOfEmployee(EmployeeCountCallback callback) {
        reference.child("employeeInfo").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot emp : snapshot.getChildren()) {
                        Employee employee = emp.getValue(Employee.class);
                        if (employee != null) {
                            callback.onCountRetrieved(employee.getId());
                        }
                    }
                } else {
                    callback.onCountRetrieved(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
