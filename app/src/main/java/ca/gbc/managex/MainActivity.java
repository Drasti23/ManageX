package ca.gbc.managex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ca.gbc.managex.AdminControl.Dialogs.LoginDialog;
import ca.gbc.managex.POS.MainPOSActivity;
import ca.gbc.managex.RegisterTime.RegisterTimeActivity;
import ca.gbc.managex.databinding.ActivityMainBinding;
import ca.gbc.managex.setting.SettingActivity;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.main);
        binding.llPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginDialog alert = new LoginDialog();
                alert.showDialog(MainActivity.this, MainPOSActivity.class);

            }
        });
        binding.llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

Intent i = new Intent(MainActivity.this, SettingActivity.class);
startActivity(i);
            }
        });
        binding.llOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.llTimeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginDialog alert = new LoginDialog();
                alert.showDialog(MainActivity.this, RegisterTimeActivity.class);
            }
        });

    }
}
