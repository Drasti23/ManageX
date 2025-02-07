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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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
    private TextView tvgrossPay,tvtotalHours, tvpayRate,tvreceivingPay,tvbonus,tvstatHol,tvtaxRate;
    private double payRate,grossPay,totalHours,bonus,statHol,taxRate,overTimePayRate,netPay;
    private String iPayRate,iStatHol,iBonus;

    private Salary salaryObject;
    private List<WorkHour> workHoursList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private WorkHoursAdapter workHoursAdapter;
    private String selectedEmployeeId, selectedTimePeriod;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private String userUid = user.getUid();  // Replace with actual user UID
    private Button generatePdf;
    private List<String> employeeNames = new ArrayList<>();
    private List<String> employeeIds = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_payroll,container,false);

        generatePdf = view.findViewById(R.id.generatePdf);
        spinnerTimePeriod = view.findViewById(R.id.spinner_time_period);
        spinnerEmployee = view.findViewById(R.id.spinner_employee);
        recyclerWorkHours = view.findViewById(R.id.recycler_work_hours);
        tvgrossPay = view.findViewById(R.id.tvGrossPay);
        tvtotalHours = view.findViewById(R.id.tvTotalHoursWorked);
        tvpayRate = view.findViewById(R.id.tvPayRate);
        tvbonus = view.findViewById(R.id.tvBonus);
        tvreceivingPay = view.findViewById(R.id.tvReceivingPay);
        tvstatHol = view.findViewById(R.id.tvStatHol);
        tvtaxRate = view.findViewById(R.id.tvTaxes);
        overTimePayRate = 25.00;
        bonus=00.00;
        taxRate=13.00;
        totalHours = 0;



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
                databaseReference.child("employeeInfo").child(selectedEmployeeId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            payRate = snapshot.child("payRate").getValue(Double.class);
                            fetchWorkHours();  // Ensure `payRate` is loaded before fetching work hours
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load pay rate", Toast.LENGTH_SHORT).show();
                    }
                });
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

        generatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PDFGenerator pdfGenerator = new PDFGenerator(getContext());
                pdfGenerator.generatePayrollPDF(
                        spinnerEmployee.getSelectedItem().toString(),
                        grossPay, totalHours, netPay, bonus, statHol, taxRate,selectedTimePeriod,payRate
                );
            }
        });
        return view;


    }

    private void calculatePayCheckInfo(){
        salaryObject = new Salary(payRate,totalHours,taxRate,bonus,overTimePayRate);
        grossPay = salaryObject.calculateGrossPay(payRate,totalHours,bonus);
        netPay = salaryObject.calculateReceivingPay(taxRate,grossPay);


        tvtotalHours.setText(totalHours+"");
        tvpayRate.setText(payRate+"");
        tvreceivingPay.setText(netPay+"");
        tvgrossPay.setText(grossPay+"");
        tvbonus.setText(bonus+"");
        tvtaxRate.setText(taxRate+"");
        tvstatHol.setText(statHol+"");

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
                    payRate = empSnapshot.child("payRate").getValue(Double.class);
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

        totalHours = 0; // Reset total hours to 0 when selecting a new employee

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
                        double hourWorked = parseDurationToHours(duration);
                        totalHours += hourWorked;

                        workHoursList.add(new WorkHour(date, clockIn, clockOut, duration));
                    }
                }

                workHoursAdapter.notifyDataSetChanged();
                tvtotalHours.setText(String.format(Locale.getDefault(), "%.2f", totalHours));
                calculatePayCheckInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch work hours", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double parseDurationToHours(String duration) {
        try {
            String[] parts = duration.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours + (minutes / 60.0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0; // Default to 0 if parsing fails
        }
    }
    private void enableEditing(boolean edit){
       // tvstatHol.setE
    }


}