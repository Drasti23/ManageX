package ca.gbc.managex.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ca.gbc.managex.R;

public class ForgetActivity extends AppCompatActivity {
    EditText etResetPassword;
    Button resetPassword;
    FirebaseAuth auth;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forget), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        etResetPassword = findViewById(R.id.etResetPassword);
        resetPassword = findViewById(R.id.btnResetPassword);
        back = findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etResetPassword.getText().toString().trim();
                if(email.isEmpty()){
                    Toast.makeText(ForgetActivity.this,"Please fill the required field!", Toast.LENGTH_SHORT).show();
                }
                resetPassword(email);
            }
        });
    }

    public void resetPassword(String email){
auth.sendPasswordResetEmail(email)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgetActivity.this,"Reset password email sent!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ForgetActivity.this,"Failed to send reset email!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}