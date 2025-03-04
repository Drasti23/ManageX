package ca.gbc.managex.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ca.gbc.managex.AdminControl.AdminControlActivity;
import ca.gbc.managex.ManageRestaurantProfile.ManageRestaurantProfile;
import ca.gbc.managex.ManagerControl.ManagerControlActivity;
import ca.gbc.managex.R;
import ca.gbc.managex.databinding.ActivitySettingBinding;
import ca.gbc.managex.user.LoginActivity;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    ImageView back;

    private static final String OWNER_PASSWORD = "2000";
    private static final String MANAGER_PASSWORD = "1000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.main);

        back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptForPassword("Logout");
            }
        });

        binding.cvOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptForPassword("Owner");
            }
        });

        binding.cvManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptForPassword("Manager");
            }
        });

        binding.cvResProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptForPassword("ChangeRestaurant");
            }
        });
    }

    private void promptForPassword(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredPassword = input.getText().toString();
                if (enteredPassword.equals(OWNER_PASSWORD) || enteredPassword.equals(MANAGER_PASSWORD)) {
                    if (action.equals("Owner")) {
                        Intent intent = new Intent(SettingActivity.this, AdminControlActivity.class);
                        startActivity(intent);
                    } else if (action.equals("Manager")) {
                         Intent i = new Intent(SettingActivity.this, ManagerControlActivity.class);
                          startActivity(i);

                    } else if (action.equals("Logout")) {
                        logout();
                    } else if (action.equals("ChangeRestaurant")) {
                        // Implement Change Restaurant logic
                        Intent i = new Intent(SettingActivity.this, ManageRestaurantProfile.class);
                        startActivity(i);
                        Toast.makeText(SettingActivity.this, "Change Restaurant Access Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}