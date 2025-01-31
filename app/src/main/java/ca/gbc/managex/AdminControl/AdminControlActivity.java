package ca.gbc.managex.AdminControl;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import ca.gbc.managex.AdminControl.Fragments.ManageEmployeeFragment;
import ca.gbc.managex.AdminControl.Fragments.ManagePOSFragment;
import ca.gbc.managex.R;

public class AdminControlActivity extends AppCompatActivity  implements BottomNavigationView
        .OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.person);
        back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    ManageEmployeeFragment firstFragment = new ManageEmployeeFragment();
    ManagePOSFragment secondFragment = new ManagePOSFragment();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        if(item.getItemId() == R.id.person){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, firstFragment)
                    .commit();
            return true;
        }
        else if(item.getItemId() == R.id.pos){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, secondFragment)
                    .commit();
            return true;
        }
        return false;

    }
}