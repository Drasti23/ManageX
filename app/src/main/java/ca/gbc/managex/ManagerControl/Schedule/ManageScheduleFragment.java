package ca.gbc.managex.ManagerControl.Schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import ca.gbc.managex.AdminControl.Classes.Employee;
import android.Manifest;
import ca.gbc.managex.R;

import android.graphics.pdf.PdfDocument;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManageScheduleFragment extends Fragment {

    private TableLayout tableSchedule;
    private TextView tvSelectedWeek, tvStartTime, tvEndTime, tvDuration;
    private CalendarView calendarView;
    private Button btnCreateSlot, btnCancelSlot, btnPublish;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private List<Employee> employeeList;
    private String selectedDate = "", selectedWeek = "";
    private Employee selectedEmployee = null;
    private Map<String, TableRow> employeeRowMap = new HashMap<>();
    private Spinner spinnerEmployee;  // Add this


    public ManageScheduleFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_schedule, container, false);

        // 🏗️ Initializing UI Elements
        tableSchedule = view.findViewById(R.id.tableSchedule);
        tvSelectedWeek = view.findViewById(R.id.tvSelectedWeek);
        calendarView = view.findViewById(R.id.calendarView);
        tvStartTime = view.findViewById(R.id.tvStartTime);
        tvEndTime = view.findViewById(R.id.tvEndTime);
        tvDuration = view.findViewById(R.id.tvDuration);
        btnCreateSlot = view.findViewById(R.id.btnCreateSlot);
        btnCancelSlot = view.findViewById(R.id.btnCancelSlot);
        btnPublish = view.findViewById(R.id.btnPublish);
        spinnerEmployee = view.findViewById(R.id.spinnerEmployee);


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());
        employeeList = new ArrayList<>();
        selectedWeek = getCurrentWeek();

        // 📅 Set up Click Listeners
        tvSelectedWeek.setOnClickListener(v -> showWeekPicker());
        tvStartTime.setOnClickListener(v -> showTimePicker(tvStartTime));
        tvEndTime.setOnClickListener(v -> showTimePicker(tvEndTime));
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d", dayOfMonth, month + 1); // Format: dd/MM
            loadScheduleFromFirebase();
        });

        btnCreateSlot.setOnClickListener(v -> createSlot());
        btnCancelSlot.setOnClickListener(v -> clearFields());
        btnPublish.setOnClickListener(v -> publishSchedule());

        // 🔥 Load Data
        loadEmployees();
        loadScheduleFromFirebase();

        return view;
    }

    private void showWeekPicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-'W'ww", Locale.getDefault());
                    selectedWeek = sdf.format(calendar.getTime());
                    tvSelectedWeek.setText("Week: " + selectedWeek);
                    loadScheduleFromFirebase();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadScheduleFromFirebase() {
        Log.d("FirebaseDebug", "Fetching data for Week: " + selectedWeek);

        if (selectedWeek == null || selectedWeek.isEmpty()) {
            Log.e("FirebaseError", "Selected Week is null or empty");
            return;
        }

        databaseReference.child("employeeSchedule").child(selectedWeek).child("Select Week")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Log.d("FirebaseData", "No schedule data found for week: " + selectedWeek);
                            return;
                        }

                        tableSchedule.removeAllViews();
                        employeeRowMap.clear();
                        addTableHeaders();

                        for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                            String day = daySnapshot.getKey();
                            Log.d("FirebaseData", "Processing Day: " + day);

                            for (DataSnapshot monthSnapshot : daySnapshot.getChildren()) {
                                for (DataSnapshot scheduleSnapshot : monthSnapshot.getChildren()) {
                                    Schedule schedule = scheduleSnapshot.getValue(Schedule.class);
                                    if (schedule != null && schedule.getEmpName() != null) {
                                        Log.d("FirebaseData", "Adding Employee: " + schedule.getEmpName() + " | Shift: " + schedule.getStartTime() + " - " + schedule.getEndTime());
                                        updateScheduleTable(schedule);
                                    } else {
                                        Log.e("FirebaseError", "Skipping schedule with null values");
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Failed to load schedule: " + error.getMessage());
                    }
                });
    }


    private void updateScheduleTable(Schedule schedule) {
        TableRow row;

        if (!employeeRowMap.containsKey(schedule.getEmpName())) {
            row = new TableRow(getContext());

            TextView employeeCell = new TextView(getContext());
            employeeCell.setText(schedule.getEmpName());
            employeeCell.setPadding(16, 8, 16, 8);
            employeeCell.setTextSize(14);
            employeeCell.setTextColor(getResources().getColor(android.R.color.black));
            employeeCell.setBackgroundColor(getResources().getColor(android.R.color.white));
            row.addView(employeeCell);

            for (int i = 0; i < 7; i++) {
                TextView cell = new TextView(getContext());
                cell.setText("");
                cell.setPadding(16, 8, 16, 8);
                cell.setTextSize(14);
                cell.setTextColor(getResources().getColor(android.R.color.black));
                cell.setBackgroundColor(getResources().getColor(android.R.color.white));
                row.addView(cell);
            }

            tableSchedule.addView(row);
            employeeRowMap.put(schedule.getEmpName(), row);
        } else {
            row = employeeRowMap.get(schedule.getEmpName());
        }

        int dayIndex = getDayIndex(schedule.getDay());
        if (dayIndex == -1) {
            Log.e("UIError", "Invalid Day: " + schedule.getDay());
            return;
        }

        TextView dayCell = (TextView) row.getChildAt(dayIndex + 1);

        String existingText = dayCell.getText().toString().trim();
        if (!existingText.isEmpty()) {
            existingText += "\n";
        }

        dayCell.setText(existingText + schedule.getStartTime() + " - " + schedule.getEndTime());
        dayCell.setBackgroundColor(getResources().getColor(R.color.app_button));

        Log.d("UIUpdate", "Updated UI for: " + schedule.getEmpName() + " on " + schedule.getDay());
    }





    private void createSlot() {
        if (selectedEmployee == null) {
            Toast.makeText(getContext(), "Select an employee", Toast.LENGTH_SHORT).show();
            return;
        }

        String week = selectedWeek;
        String startTime = tvStartTime.getText().toString();
        String endTime = tvEndTime.getText().toString();
        String duration = tvDuration.getText().toString();

        if (selectedDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select a date from the calendar!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract day and month correctly
        String[] dateParts = selectedDate.split("/");
        String dayOnly = dateParts[0];
        String monthOnly = dateParts[1];

        String scheduleId = databaseReference.child("employeeSchedule").push().getKey();
        Schedule schedule = new Schedule(scheduleId, selectedEmployee.getId(), selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName(), week, selectedDate, startTime, endTime, duration);

        // Fix path to match Firebase structure
        databaseReference.child("employeeSchedule").child(week).child("Select Week").child(dayOnly).child(monthOnly).child(scheduleId)
                .setValue(schedule)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Schedule Created!", Toast.LENGTH_SHORT).show();
                        loadScheduleFromFirebase();
                    } else {
                        Toast.makeText(getContext(), "Failed to save", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void publishSchedule() {
        generatePrintableSchedule();
    }

    private void generatePrintableSchedule() {
        databaseReference.child("employeeSchedule").child(selectedWeek).child("Select Week")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(getContext(), "No schedule found for this week!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 🧠 Collect all schedule data
                        Map<String, List<Schedule>> scheduleByDate = new TreeMap<>();
                        for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                            String day = daySnapshot.getKey();

                            for (DataSnapshot monthSnapshot : daySnapshot.getChildren()) {
                                for (DataSnapshot scheduleSnapshot : monthSnapshot.getChildren()) {
                                    Schedule schedule = scheduleSnapshot.getValue(Schedule.class);
                                    if (schedule != null) {
                                        scheduleByDate
                                                .computeIfAbsent(day, k -> new ArrayList<>())
                                                .add(schedule);
                                    }
                                }
                            }
                        }

                        // Pass to PDF generator
                        printStructuredSchedule(scheduleByDate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Failed to load schedule: " + error.getMessage());
                    }
                });
    }
    private void printStructuredSchedule(Map<String, List<Schedule>> scheduleMap) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        paint.setTextSize(14);
        paint.setAntiAlias(true);

        int pageNum = 1;

        // 📆 Format week title
        List<String> weekDates = generateWeekDates(selectedWeek);
        String weekTitle = weekDates.get(0) + " - " + weekDates.get(6); // first - last
        String year = selectedWeek.split("-W")[0];

        for (Map.Entry<String, List<Schedule>> entry : scheduleMap.entrySet()) {
            String day = entry.getKey();
            List<Schedule> dailySchedules = entry.getValue();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, pageNum++).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            int y = 40;

            // 🏷 Header
            paint.setFakeBoldText(true);
            paint.setTextSize(16);
            canvas.drawText("📅 Week Schedule: " + weekTitle, 20, y, paint);
            canvas.drawText("Year: " + year, 450, y, paint);
            y += 40;

            // 📌 Day Heading
            paint.setTextSize(15);
            canvas.drawText("🗓 Date: " + day, 20, y, paint);
            y += 30;

            paint.setTextSize(14);
            paint.setFakeBoldText(false);

            // 🧾 Sort by start time
            Collections.sort(dailySchedules, Comparator.comparing(Schedule::getStartTime));

            for (Schedule schedule : dailySchedules) {
                if (y > 750) { // page break logic
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(600, 800, pageNum++).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = 40;
                }

                String timeFrame = "⏰ " + schedule.getStartTime() + " - " + schedule.getEndTime();
                String empName = "👤 " + schedule.getEmpName();
                String duration = "⏳ " + schedule.getDuration();

                canvas.drawText(timeFrame + "  ➝  " + empName + "  (" + duration + ")", 30, y, paint);
                y += 25;
            }

            pdfDocument.finishPage(page);
        }

        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Published_Schedule.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            Toast.makeText(getContext(), "📂 PDF Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPDF(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "⚠ Error saving PDF!", Toast.LENGTH_SHORT).show();
        }
    }




    private void showPublishDialog(String scheduleText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Published Schedule")
                .setMessage(scheduleText)
                .setPositiveButton("📄 Save as PDF", (dialog, which) -> printSchedule(scheduleText))
                .setNegativeButton("❌ Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void printSchedule(String scheduleText) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
                return;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10-12
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }


        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(14);
        int yPosition = 50;
        String[] lines = scheduleText.split("\n");

        for (String line : lines) {
            canvas.drawText(line, 20, yPosition, paint);
            yPosition += 25;
        }

        pdfDocument.finishPage(page);

        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Published_Schedule.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            Toast.makeText(getContext(), "📂 PDF Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPDF(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "⚠ Error saving PDF!", Toast.LENGTH_SHORT).show();
        }
    }


    private void openPDF(File file) {
        Uri uri = FileProvider.getUriForFile(getContext(), "ca.gbc.managex.provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent chooser = Intent.createChooser(intent, "Open PDF");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            Toast.makeText(getContext(), "No PDF viewer installed!", Toast.LENGTH_SHORT).show();
        }
    }



    private void clearFields() {
        tvStartTime.setText("Select Start Time");
        tvEndTime.setText("Select End Time");
        tvDuration.setText("Duration: 0 hrs");
    }
    private String getCurrentWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-'W'ww", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private void showTimePicker(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    textView.setText(time);
                    calculateDuration();
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void loadEmployees() {
        databaseReference.child("employeeInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeList.clear();
                List<String> employeeNames = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    int id = child.child("id").getValue(Integer.class);
                    String firstName = child.child("firstName").getValue(String.class);
                    String lastName = child.child("lastName").getValue(String.class);
                    String position = child.child("position").getValue(String.class);
                    String empType = child.child("empType").getValue(String.class);

                    empType = empType != null && empType.equalsIgnoreCase("Part Time") ? "P" : "F";

                    Employee employee = new Employee(id, firstName, lastName, "", "", "", position, empType, 0, 0, 0.0);
                    employeeList.add(employee);
                    employeeNames.add(firstName + " " + lastName + " - " + position + " (" + empType + ")");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, employeeNames);
                spinnerEmployee.setAdapter(adapter);
                spinnerEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedEmployee = employeeList.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedEmployee = null;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load employees: " + error.getMessage());
            }
        });
    }



    private int getDayIndex(String day) {
        if (day == null || day.trim().isEmpty()) {
            Log.e("UIError", "Invalid day received: " + day);
            return -1;
        }

        if (day.contains("/")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                String fullDate = day + "/" + calendar.get(Calendar.YEAR);  // Append the current year

                Date date = sdf.parse(fullDate);
                if (date != null) {
                    calendar.setTime(date);
                    return calendar.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
                }
            } catch (Exception e) {
                Log.e("UIError", "Failed to parse date format: " + day);
                return -1;
            }
        }
        return -1;
    }





    private void calculateDuration() {
        String start = tvStartTime.getText().toString();
        String end = tvEndTime.getText().toString();

        if (!start.equals("Select Start Time") && !end.equals("Select End Time")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date startTime = sdf.parse(start);
                Date endTime = sdf.parse(end);

                if (startTime != null && endTime != null) {
                    long difference = endTime.getTime() - startTime.getTime();
                    if (difference < 0) {
                        tvDuration.setText("Invalid Time Range");
                    } else {
                        long hours = (difference / (1000 * 60 * 60));
                        long minutes = (difference / (1000 * 60)) % 60;
                        tvDuration.setText(String.format(Locale.getDefault(), "Duration: %d hrs %d min", hours, minutes));
                    }
                }
            } catch (Exception e) {
                tvDuration.setText("Error in Time Format");
            }
        }
    }
    private List<String> generateWeekDates(String selectedWeek) {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/MM", Locale.getDefault()); // Format: "Mon 11/03"
        Calendar calendar = Calendar.getInstance();

        try {
            int weekNumber = Integer.parseInt(selectedWeek.split("-W")[1]); // Extract week number
            calendar.set(Calendar.WEEK_OF_YEAR, weekNumber);
            calendar.set(Calendar.YEAR, Integer.parseInt(selectedWeek.split("-W")[0])); // Extract year
        } catch (Exception e) {
            Log.e("DateError", "Error parsing week format: " + selectedWeek);
            return Arrays.asList("Error", "Error", "Error", "Error", "Error", "Error", "Error");
        }

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Start from Sunday

        for (int i = 0; i < 7; i++) {
            dates.add(sdf.format(calendar.getTime())); // Generates the correct week dates
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dates;
    }


    private void addTableHeaders() {
        if (tableSchedule.getChildCount() > 0) {
            tableSchedule.removeViewAt(0); // Remove old headers if they exist
        }

        TableRow headerRow = new TableRow(getContext());

        // Add Employee Header
        TextView employeeHeader = new TextView(getContext());
        employeeHeader.setText("Employee");
        employeeHeader.setPadding(16, 8, 16, 8);
        employeeHeader.setTextSize(16);
        employeeHeader.setTextColor(getResources().getColor(android.R.color.white));
        employeeHeader.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        employeeHeader.setGravity(Gravity.CENTER);
        headerRow.addView(employeeHeader);

        // Generate dynamic dates for the selected week
        List<String> weekDates = generateWeekDates(selectedWeek);

        for (String date : weekDates) {
            TextView headerCell = new TextView(getContext());
            headerCell.setText(date);  // Now dynamically displays days & dates
            headerCell.setPadding(16, 8, 16, 8);
            headerCell.setTextSize(16);
            headerCell.setTextColor(getResources().getColor(android.R.color.white));
            headerCell.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            headerCell.setGravity(Gravity.CENTER);
            headerRow.addView(headerCell);
        }

        // Add the header row to the table
        tableSchedule.addView(headerRow, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission Granted! Try again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Storage permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

