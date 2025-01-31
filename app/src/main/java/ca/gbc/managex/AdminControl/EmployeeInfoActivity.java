package ca.gbc.managex.AdminControl;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import ca.gbc.managex.R;

public class EmployeeInfoActivity extends AppCompatActivity {
    TextView tvEmpName, tvEmpId, tvEmpEmail, tvEmpPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_info);

        tvEmpName = findViewById(R.id.tvEmpName);
        tvEmpId = findViewById(R.id.tvEmpId);
        tvEmpEmail = findViewById(R.id.tvEmail);
        tvEmpPhone = findViewById(R.id.tvPhoneNum);

        // Retrieve data from intent
        String empName = getIntent().getStringExtra("EMP_NAME");
        int empId = getIntent().getIntExtra("EMP_ID", 0);
        String empEmail = getIntent().getStringExtra("EMP_EMAIL");
        String empPhone = getIntent().getStringExtra("EMP_PHONE");

        // Set data to TextViews
        tvEmpName.setText(empName);
        tvEmpId.setText(String.valueOf(empId));
        tvEmpEmail.setText(empEmail);
        tvEmpPhone.setText(empPhone);
    }
}
