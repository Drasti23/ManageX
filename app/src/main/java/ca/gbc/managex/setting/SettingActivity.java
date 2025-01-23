package ca.gbc.managex.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ca.gbc.managex.AdminControl.AdminControlActivity;
import ca.gbc.managex.R;
import ca.gbc.managex.databinding.ActivitySettingBinding;
import ca.gbc.managex.user.LoginActivity;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.main);

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

    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}