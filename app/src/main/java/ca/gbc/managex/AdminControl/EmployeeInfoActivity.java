package ca.gbc.managex.AdminControl;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.gbc.managex.R;

public class EmployeeInfoActivity extends AppCompatActivity {
    TextView id,name,email,contact,joiningData,pass,code,position;
    Button delete,attach,edit;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = findViewById(R.id.tvEmpId);
        name = findViewById(R.id.tvEmpName);
        email = findViewById(R.id.tvEmpEmail);
        contact = findViewById(R.id.tvEmpPhoneNum);
        joiningData = findViewById(R.id.tvEmpJoiningDate);
        pass = findViewById(R.id.tvEmppass);
        code = findViewById(R.id.tvEmpCode);
        position = findViewById(R.id.tvEmpPosition);
        attach = findViewById(R.id.btnEmpAttach);
        delete = findViewById(R.id.btnEmpDelete);
        edit = findViewById(R.id.btnEmpEdit);
        back = findViewById(R.id.backButton);

        String emp_name = getIntent().getStringExtra("emp_name");
        String emp_code = getIntent().getStringExtra("emp_code");
        String emp_position = getIntent().getStringExtra("emp_position");
        String emp_email = getIntent().getStringExtra("emp_email");
        String emp_id = getIntent().getStringExtra("emp_id");
        String emp_pass = getIntent().getStringExtra("emp_password");
        String emp_joining_date = getIntent().getStringExtra("emp_joining_date");
        String emp_contact = getIntent().getStringExtra("emp_contact");

        id.setText(emp_id);
        name.setText(emp_name);
        email.setText(emp_email);
        contact.setText(emp_contact);
        joiningData.setText(emp_joining_date);
        pass.setText(emp_pass);
        code.setText(emp_code);
        position.setText(emp_position);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EmployeeInfoActivity.this, AdminControlActivity.class);
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeInfoActivity.this);
                builder.setTitle("Delete Employee")
                        .setMessage("All the employee data will be deleted.").
                        setPositiveButton("delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                findEmployeeToDelete(emp_id);
                                finish();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
    public void findEmployeeToDelete(String empId){


        DatabaseReference empReference = reference.child("Users").child(auth.getUid()).child("employeeInfo").child(empId);
                empReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EmployeeInfoActivity.this,"Employee deleted",Toast.LENGTH_SHORT).show();
                    }
                });
    }
   }