package ca.gbc.managex.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
        binding.cvResProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingActivity.this, ManageRestaurantProfile.class);
                startActivity(i);

            }
        });

        binding.cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        binding.cvOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingActivity.this, AdminControlActivity.class);
                startActivity(i);

            }
        });
        binding.cvManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingActivity.this, ManagerControlActivity.class);
                startActivity(i);
            }
        });

    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}