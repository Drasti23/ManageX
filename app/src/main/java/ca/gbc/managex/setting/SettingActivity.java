package ca.gbc.managex.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import ca.gbc.managex.AdminControl.AdminControlActivity;
import ca.gbc.managex.ManageRestaurantProfile.ManageRestaurantProfile;
import ca.gbc.managex.ManagerControl.ManagerControlActivity;
import ca.gbc.managex.R;
import ca.gbc.managex.databinding.ActivitySettingBinding;
import ca.gbc.managex.user.LoginActivity;

public class SettingActivity extends AppCompatActivity {
    private static final String DEFAULT_ACCESS = "1234";
    ActivitySettingBinding binding;
    ImageView back;

    private String managerCode = DEFAULT_ACCESS;
    private String adminCode = DEFAULT_ACCESS;

    DatabaseReference credentialsRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(FirebaseAuth.getInstance().getUid()).child("credentials");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.main);

        back = findViewById(R.id.backButton);
        back.setOnClickListener(view -> finish());

        fetchCodesFromDatabase();

        // Require code to unlock settings access
        promptForAccessBeforeUnlockingSettings();
    }

    private void fetchCodesFromDatabase() {
        credentialsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mgr = snapshot.child("managerCode").getValue(String.class);
                String adm = snapshot.child("adminCode").getValue(String.class);

                if (mgr != null) managerCode = mgr;
                if (adm != null) adminCode = adm;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingActivity.this, "Error loading credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void promptForAccessBeforeUnlockingSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_password_prompt, null);
        EditText input = view.findViewById(R.id.etPasswordInput);
        builder.setView(view);
        builder.setCancelable(false);

        builder.setTitle("Access Settings");
        builder.setMessage("Enter admin or manager access code");

        builder.setPositiveButton("Enter", (dialog, which) -> {
            String enteredPassword = input.getText().toString().trim();
            if (enteredPassword.equals(managerCode) || enteredPassword.equals(adminCode)) {
                initSettings(); // 🟢 Access granted
            } else {
                Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
                finish(); // 🚫 Block access
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> finish());
        builder.show();
    }

    private void initSettings() {
        binding.cvLogout.setOnClickListener(view -> promptForPassword("Logout"));
        binding.cvOwner.setOnClickListener(view -> promptForPassword("Owner"));
        binding.cvManager.setOnClickListener(view -> promptForPassword("Manager"));
        binding.cvResProfile.setOnClickListener(view -> promptForPassword("ChangeRestaurant"));
    }

    private void promptForPassword(String role) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_password_prompt, null);
        EditText input = view.findViewById(R.id.etPasswordInput);
        builder.setView(view);
        builder.setCancelable(false);

        builder.setTitle("Enter Code");

        builder.setPositiveButton("OK", (dialog, which) -> {
            String enteredPassword = input.getText().toString().trim();

            if ((role.equals("Owner") && enteredPassword.equals(adminCode)) ||
                    (role.equals("Manager") && enteredPassword.equals(managerCode))) {

                if (role.equals("Owner")) {
                    startActivity(new Intent(this, AdminControlActivity.class));
                } else if (role.equals("Manager")) {
                    startActivity(new Intent(this, ManagerControlActivity.class));
                }

            } else if (role.equals("Logout")) {
                logout();

            } else if (role.equals("ChangeRestaurant")) {
                startActivity(new Intent(this, ManageRestaurantProfile.class));
                Toast.makeText(this, "Access granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Incorrect code", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
