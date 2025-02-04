package ca.gbc.managex.ManagerControl.Payroll;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import ca.gbc.managex.R;

public class ManagePayrollFragment extends Fragment {
    private Spinner spinnerTimePeriod, spinnerEmployee;
    private RecyclerView recyclerWorkHours;

    private List<WorkHour> workHoursList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private WorkHoursAdapter workHoursAdapter;
    private String selectedEmployeeId, selectedTimePeriod;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private String userUid = user.getUid();  // Replace with actual user UID

    private List<String> employeeNames = new ArrayList<>();
    private List<String> employeeIds = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_payroll,container,false);

        spinnerTimePeriod = view.findViewById(R.id.spinner_time_period);
        spinnerEmployee = view.findViewById(R.id.spinner_employee);
        recyclerWorkHours = view.findViewById(R.id.recycler_work_hours);

        recyclerWorkHours.setLayoutManager(new LinearLayoutManager(getContext()));
        workHoursAdapter = new WorkHoursAdapter(workHoursList);
        recyclerWorkHours.setAdapter(workHoursAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUid);

        loadTimePeriods();
        loadEmployees();

        spinnerEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEmployeeId = employeeIds.get(position);
                fetchWorkHours();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimePeriod = spinnerTimePeriod.getSelectedItem().toString();
                fetchWorkHours();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        return view;
    }
    private void loadTimePeriods() {
        databaseReference.child("EmployeeWorksheet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TreeSet<String> uniqueWeeks = new TreeSet<>(Collections.reverseOrder()); // Store unique week ranges

                SimpleDateFormat firebaseFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String dateStr = dateSnapshot.getKey(); // Fetch stored date (e.g., "03 Feb 2024")
                    try {
                        Date date = firebaseFormat.parse(dateStr);
                        if (date == null) continue;

                        calendar.setTime(date);

                        // Move to Monday of the same week
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        String weekStart = outputFormat.format(calendar.getTime());

                        // Move to Sunday of the same week
                        calendar.add(Calendar.DAY_OF_WEEK, 6);
                        String weekEnd = outputFormat.format(calendar.getTime());

                        // Store the formatted week range
                        uniqueWeeks.add(weekStart + " to " + weekEnd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                List<String> timePeriods = new ArrayList<>(uniqueWeeks);

                if (timePeriods.isEmpty()) {
                    timePeriods.add("No Data Available");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, timePeriods);
                spinnerTimePeriod.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load time periods", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadEmployees() {
        databaseReference.child("employeeInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeNames.clear();
                employeeIds.clear();
                for (DataSnapshot empSnapshot : snapshot.getChildren()) {
                    String empId = empSnapshot.getKey();
                    String empName = empSnapshot.child("firstName").getValue(String.class) + empSnapshot.child("lastName").getValue(String.class);
                    employeeNames.add(empName);
                    employeeIds.add(empId);
                }
                spinnerEmployee.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, employeeNames));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load employees", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWorkHours() {
        if (selectedEmployeeId == null || selectedTimePeriod == null) return;

        databaseReference.child("EmployeeWorksheet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workHoursList.clear();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();

                    if (dateSnapshot.child(selectedEmployeeId).exists()) {
                        DataSnapshot empData = dateSnapshot.child(selectedEmployeeId);
                        String clockIn = empData.child("clockInTime").getValue(String.class);
                        String clockOut = empData.child("clockOutTime").getValue(String.class);
                        String duration = empData.child("duration").getValue(String.class);

                        workHoursList.add(new WorkHour(date, clockIn, clockOut, duration));
                    }
                }

                workHoursAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch work hours", Toast.LENGTH_SHORT).show();
            }
        });
    }

}