package ca.gbc.managex.ManagerControl.EditAvailability.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.ManagerControl.EditAvailability.Adapter.EmployeeAdapter;
import ca.gbc.managex.R;


public class EditAvailabilityFragment extends Fragment {
    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> employeeList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_availability, container, false);

        recyclerView = view.findViewById(R.id.availability_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(employeeList);
        recyclerView.setAdapter(employeeAdapter);


        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());

        getEmpDataFromFirebase();

        return view;
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
}