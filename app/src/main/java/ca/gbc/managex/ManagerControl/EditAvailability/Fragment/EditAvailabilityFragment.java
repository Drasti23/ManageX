package ca.gbc.managex.ManagerControl.EditAvailability.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.ManagerControl.EditAvailability.Adapter.EmployeeAdapter;
import ca.gbc.managex.ManagerControl.EditAvailability.Availability;
import ca.gbc.managex.R;
import ca.gbc.managex.databinding.FragmentEditAvailabilityBinding;


public class EditAvailabilityFragment extends Fragment implements EmployeeAdapter.OnEmployeeClickListener {
    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> employeeList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Availability currentAvailabilityObj = null;
    FragmentEditAvailabilityBinding binding;
    Map<String,String> availability = new HashMap<>();
    String empName="";
    int empId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditAvailabilityBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.availability_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(employeeList,getContext(),this);
        recyclerView.setAdapter(employeeAdapter);
        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());
        getEmpDataFromFirebase();

        if(currentAvailabilityObj ==null){
        setDefaultAvailability();
        }
        binding.etSearchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEmployees(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        binding.btnSaveAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                availability.put("SundayStart", binding.tvSundayStartTime.getText().toString());
                availability.put("SundayEnd", binding.tvSundayEndTime.getText().toString());

                availability.put("MondayStart", binding.tvMondayStartTime.getText().toString());
                availability.put("MondayEnd", binding.tvMondayEndTime.getText().toString());

                availability.put("TuesdayStart", binding.tvTuesdayStartTime.getText().toString());
                availability.put("TuesdayEnd", binding.tvTuesdayEndTime.getText().toString());

                availability.put("WednesdayStart", binding.tvWednesdayStartTime.getText().toString());
                availability.put("WednesdayEnd", binding.tvWednesdayEndTime.getText().toString());

                availability.put("ThursdayStart", binding.tvThursdayStartTime.getText().toString());
                availability.put("ThursdayEnd", binding.tvThursdayEndTime.getText().toString());

                availability.put("FridayStart", binding.tvFridayStartTime.getText().toString());
                availability.put("FridayEnd", binding.tvFridayEndTime.getText().toString());

                availability.put("SaturdayStart", binding.tvSaturdayStartTime.getText().toString());
                availability.put("SaturdayEnd", binding.tvSaturdayEndTime.getText().toString());
                currentAvailabilityObj = new Availability(empId,empName,availability);
                saveAvailabilityToFirebase(currentAvailabilityObj);
            }
        });



        return view;
    }

    public void saveAvailabilityToFirebase(Availability saveObj){
        databaseReference.child("employeeAvailability").child(empId+"").setValue(saveObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   Toast.makeText(getContext(), "Availability saved successfully!", Toast.LENGTH_SHORT).show();
               }
               else{
                   Toast.makeText(getContext(), "Failed to save availability", Toast.LENGTH_SHORT).show();

               }
            }
        });
    }


    public void getEmpDataFromFirebase() {
        employeeList.clear();
        databaseReference.child("employeeInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeList.clear(); // Clear the list before adding new items
                for (DataSnapshot child : snapshot.getChildren()) {
                    Employee employee = child.getValue(Employee.class);
                    employeeList.add(employee);
                }
                employeeAdapter.notifyDataSetChanged(); // Update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load employees", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEmployeeClick(Availability empAvailability, int id, String name) {
        currentAvailabilityObj = empAvailability;
        empId = id;
        empName = name;

        if (currentAvailabilityObj != null) {
            binding.tvSundayStartTime.setText(currentAvailabilityObj.getAvailability().get("SundayStart"));
            binding.tvSundayEndTime.setText(currentAvailabilityObj.getAvailability().get("SundayEnd"));
            binding.tvMondayStartTime.setText(currentAvailabilityObj.getAvailability().get("MondayStart"));
            binding.tvMondayEndTime.setText(currentAvailabilityObj.getAvailability().get("MondayEnd"));
            binding.tvTuesdayStartTime.setText(currentAvailabilityObj.getAvailability().get("TuesdayStart"));
            binding.tvTuesdayEndTime.setText(currentAvailabilityObj.getAvailability().get("TuesdayEnd"));
            binding.tvWednesdayStartTime.setText(currentAvailabilityObj.getAvailability().get("WednesdayStart"));
            binding.tvWednesdayEndTime.setText(currentAvailabilityObj.getAvailability().get("WednesdayEnd"));
            binding.tvThursdayStartTime.setText(currentAvailabilityObj.getAvailability().get("ThursdayStart"));
            binding.tvThursdayEndTime.setText(currentAvailabilityObj.getAvailability().get("ThursdayEnd"));
            binding.tvFridayStartTime.setText(currentAvailabilityObj.getAvailability().get("FridayStart"));
            binding.tvFridayEndTime.setText(currentAvailabilityObj.getAvailability().get("FridayEnd"));
            binding.tvSaturdayStartTime.setText(currentAvailabilityObj.getAvailability().get("SaturdayStart"));
            binding.tvSaturdayEndTime.setText(currentAvailabilityObj.getAvailability().get("SaturdayEnd"));
        } else {
            setDefaultAvailability();
        }
    }

    private void setDefaultAvailability() {
        binding.tvSundayStartTime.setText("closed");
        binding.tvSundayEndTime.setText("closed");
        binding.tvMondayStartTime.setText("closed");
        binding.tvMondayEndTime.setText("closed");
        binding.tvTuesdayStartTime.setText("closed");
        binding.tvTuesdayEndTime.setText("closed");
        binding.tvWednesdayStartTime.setText("closed");
        binding.tvWednesdayEndTime.setText("closed");
        binding.tvThursdayStartTime.setText("closed");
        binding.tvThursdayEndTime.setText("closed");
        binding.tvFridayStartTime.setText("closed");
        binding.tvFridayEndTime.setText("closed");
        binding.tvSaturdayStartTime.setText("closed");
        binding.tvSaturdayEndTime.setText("closed");
    }
    private void filterEmployees(String query) {
        List<Employee> filteredList = new ArrayList<>();
        for (Employee emp : employeeList) {
            if (emp.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    String.valueOf(emp.getEmpCode()).contains(query) || emp.getLastName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(emp);
            }
        }
        employeeAdapter.updateList(filteredList);
    }



}