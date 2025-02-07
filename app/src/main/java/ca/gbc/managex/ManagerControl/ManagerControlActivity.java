package ca.gbc.managex.ManagerControl;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import ca.gbc.managex.ManagerControl.EditAvailability.Fragment.EditAvailabilityFragment;
import ca.gbc.managex.ManagerControl.Payroll.ManagePayrollFragment;
import ca.gbc.managex.ManagerControl.Schedule.ManageScheduleFragment;
import ca.gbc.managex.R;

public class ManagerControlActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    ImageView back,menuButton;
    ManagePayrollFragment firstFragment = new ManagePayrollFragment();
    ManageScheduleFragment secondFragment = new ManageScheduleFragment();

    EditAvailabilityFragment thirdFragment = new EditAvailabilityFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_control);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.payroll);
        back = findViewById(R.id.backButton);
        menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(this::showPopupMenu);
        back.setOnClickListener(v->finish());

    }
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.manager_portal_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.taxes) {

                return true;
            } else if (item.getItemId() == R.id.availability) {

                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.payroll){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, firstFragment)
                    .commit();
            return true;
        }
        else if(item.getItemId() == R.id.edit_availability){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, thirdFragment)
                    .commit();
            return true;
        }
        else if(item.getItemId() == R.id.schedule){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, secondFragment)
                    .commit();
            return true;
        }
        return false;
    }
}