package ca.gbc.managex.AdminControl;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import ca.gbc.managex.AdminControl.Dialogs.EditEmployeeDialog;
import ca.gbc.managex.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmployeeInfoActivity extends AppCompatActivity {
    TextView id,name,email,contact,joiningData,pass,code,position,type,payRate;
    Button delete,attach,edit;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView back;
    private static final int PICK_FILE_REQUEST = 101;
    private Uri fileUri;

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
        payRate = findViewById(R.id.tvEmpPayRate);
        type = findViewById(R.id.tvEmpType);

        String emp_name = getIntent().getStringExtra("emp_name");
        String emp_fname = getIntent().getStringExtra("emp_fname");
        String emp_lname = getIntent().getStringExtra("emp_lname");
        String emp_code = getIntent().getStringExtra("emp_code");
        String emp_position = getIntent().getStringExtra("emp_position");
        String emp_email = getIntent().getStringExtra("emp_email");
        String emp_id = getIntent().getStringExtra("emp_id");
        String emp_pass = getIntent().getStringExtra("emp_password");
        String emp_joining_date = getIntent().getStringExtra("emp_joining_date");
        String emp_contact = getIntent().getStringExtra("emp_contact");
        String emp_type = getIntent().getStringExtra("emp_type");
        String emp_rate = getIntent().getStringExtra("emp_rate");

        id.setText(emp_id);
        name.setText(emp_name);
        email.setText(emp_email);
        contact.setText(emp_contact);
        joiningData.setText(emp_joining_date);
        pass.setText(emp_pass);
        code.setText(emp_code);
        payRate.setText(emp_rate);
        type.setText(emp_type);
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

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditEmployeeDialog dialog = EditEmployeeDialog.newInstance(
                        auth.getUid(),
                        emp_id,
                        emp_fname,emp_lname,
                        emp_email,
                        emp_contact,
                        emp_code,
                        emp_position,
                        emp_joining_date,
                        emp_pass,emp_rate,emp_type
                );
                dialog.show(getSupportFragmentManager(), "EditEmployeeDialog");
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // any file type
                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedUri = data.getData();
            String empId = id.getText().toString(); // Get employee ID
            uploadToSupabaseStorage(selectedUri, empId);
        }
    }

    private void uploadToSupabaseStorage(Uri fileUri, String empId) {
        try {
            // Read file into bytes
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            // Your Supabase config
            String supabaseUrl = "https://uufqxrlnildvmptfkgmf.supabase.co";
            String anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV1ZnF4cmxuaWxkdm1wdGZrZ21mIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDMwNTA1NzIsImV4cCI6MjA1ODYyNjU3Mn0.XhFcimOfuekaBNEACzgjBqLTRCX-tGryGVg9iAId780";
            String bucketName = "employeedocs";

            // File path format: employeeId/timestamp_filename
            String fileName = System.currentTimeMillis() + "_file.pdf";
            String filePath = auth.getUid() + "/" + empId + "/" + fileName ;

            String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + filePath;

            RequestBody body = RequestBody.create(fileBytes, MediaType.parse(getContentResolver().getType(fileUri)));

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + anonKey)
                    .header("Content-Type", getContentResolver().getType(fileUri))
                    .put(body)
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(EmployeeInfoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(EmployeeInfoActivity.this, "Upload error: " + response.message(), Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(EmployeeInfoActivity.this, "File uploaded to Supabase!", Toast.LENGTH_SHORT).show();
                            Log.d("Supabase", "File Path: " + filePath);
                            // Optionally: save 'filePath' to Firebase Realtime DB under employee node
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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