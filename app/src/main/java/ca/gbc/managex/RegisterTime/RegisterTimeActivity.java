package ca.gbc.managex.RegisterTime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ca.gbc.managex.R;

public class RegisterTimeActivity extends AppCompatActivity {

    // Firebase instances
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("Users").child(user.getUid());

    // Views
    private TextView textViewGreeting, textViewStatus,tvLastTime,tvDuration,tvTotalTime;
    private Button buttonClockIn, buttonClockOut;
    private ImageView backButton;
    private String employeeId;
    private boolean isClockedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_time);

        // Initialize views
        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewStatus = findViewById(R.id.textViewStatus);
        buttonClockIn = findViewById(R.id.buttonClockIn);
        buttonClockOut = findViewById(R.id.buttonClockOut);
        backButton = findViewById(R.id.backButton);
        tvDuration = findViewById(R.id.tvDuration);
        tvLastTime = findViewById(R.id.tvLastTime);
        tvTotalTime = findViewById(R.id.tvTotalTimeWorked);
        Intent i = getIntent();
        String name = i.getStringExtra("employeeName");
        employeeId = i.getStringExtra("employeeId");
        textViewGreeting.setText("Hello "+ name);
        // Fetch user details
        //fetchUserDetails();
fetchLastActivityStatus();
        // Set back button action
        backButton.setOnClickListener(view -> finish());

        // Set Clock In button action
        buttonClockIn.setOnClickListener(view -> clockInUser());

        // Set Clock Out button action
        buttonClockOut.setOnClickListener(view -> clockOutUser());
    }

    // Fetch the last activity (Clock In / Clock Out) from Firebase
    private void fetchLastActivityStatus() {
        String currentDate = getCurrentDate();

        reference.child("EmployeeWorksheet").child(currentDate).child(employeeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String lastActivity = snapshot.child("lastActivity").getValue(String.class);
                            String lastTime = "";
                            long duration = 0;

                            if ("Clocked In".equals(lastActivity) && snapshot.child("clockInTime").exists()) {
                                lastTime = snapshot.child("clockInTime").getValue(String.class);
                            } else if ("Clocked Out".equals(lastActivity) && snapshot.child("clockOutTime").exists()) {
                                lastTime = snapshot.child("clockOutTime").getValue(String.class);
                            }

                            if (!lastTime.isEmpty()) {
                                duration = calculateDuration(lastTime, formatDate(System.currentTimeMillis()));
                                tvLastTime.setText("Last Time: " + lastTime);
                                tvDuration.setText("Duration: " + formatDuration(duration));
                            } else {
                                tvLastTime.setText("Last Time: None");
                                tvDuration.setText("Duration: 00:00");
                            }

                            textViewStatus.setText("Last Activity: " + (lastActivity != null ? lastActivity : "None"));
                            isClockedIn = "Clocked In".equals(lastActivity);
                            updateButtonsState();
                        } else {
                            textViewStatus.setText("Last Activity: None");
                            tvLastTime.setText("Last Time: None");
                            tvDuration.setText("Duration: 00:00");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegisterTimeActivity.this, "Failed to fetch last activity.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Update buttons based on the current clock-in status
    private void updateButtonsState() {
        buttonClockIn.setEnabled(!isClockedIn);
        buttonClockOut.setEnabled(isClockedIn);
    }

    // Clock In user and update Firebase
    private void clockInUser() {
        long clockInTime = System.currentTimeMillis(); // Get current time in milliseconds
        String formattedClockInTime = formatDate(clockInTime); // Format timestamp
        String currentDate = getCurrentDate();


        Map<String, Object> clockInData = new HashMap<>();
        clockInData.put("clockInTime", formattedClockInTime);
        clockInData.put("lastActivity", "Clocked In");

        reference.child("EmployeeWorksheet").child(currentDate).child(employeeId)
                .updateChildren(clockInData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        textViewStatus.setText("Last Activity: Clocked In");
                        isClockedIn = true;
                        updateButtonsState();
                        Toast.makeText(RegisterTimeActivity.this, "Clocked In Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
        fetchLastActivityStatus();
    }

    // Clock Out user and update Firebase
    private void clockOutUser() {
        long clockOutTime = System.currentTimeMillis(); // Get current time in milliseconds
        String formattedClockOutTime = formatDate(clockOutTime); // Format timestamp
        String currentDate = getCurrentDate();

        reference.child("EmployeeWorksheet").child(currentDate).child(employeeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.child("clockInTime").exists()) {
                            String clockInTime = snapshot.child("clockInTime").getValue(String.class);
                            long duration = calculateDuration(clockInTime, formattedClockOutTime);

                            Map<String, Object> clockOutData = new HashMap<>();
                            clockOutData.put("clockOutTime", formattedClockOutTime);
                            clockOutData.put("duration", formatDuration(duration));
                            clockOutData.put("lastActivity", "Clocked Out");

                            reference.child("EmployeeWorksheet").child(currentDate).child(employeeId)
                                    .updateChildren(clockOutData)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            textViewStatus.setText("Last Activity: Clocked Out");
                                            isClockedIn = false;
                                            updateButtonsState();
                                            tvTotalTime.setText("Total time worked : "+formatDuration(duration));
                                            Toast.makeText(RegisterTimeActivity.this, "Clocked Out Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterTimeActivity.this, "Clock-in time not found!", Toast.LENGTH_SHORT).show();
                        }
                        fetchLastActivityStatus();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegisterTimeActivity.this, "Failed to fetch clock-in time.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Helper to calculate duration between two times
    private long calculateDuration(String clockInTime, String clockOutTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.getDefault());
            Date clockInDate = sdf.parse(clockInTime);
            Date clockOutDate = sdf.parse(clockOutTime);

            return clockOutDate.getTime() - clockInDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Helper to format duration in HH:mm
    private String formatDuration(long durationInMillis) {
        long hours = (durationInMillis / (1000 * 60 * 60)) % 24;
        long minutes = (durationInMillis / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    // Helper to format timestamp into a human-readable date string
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Helper to get the current date in a readable format
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}