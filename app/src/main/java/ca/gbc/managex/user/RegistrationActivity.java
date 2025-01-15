package ca.gbc.managex.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.gbc.managex.MainActivity;
import ca.gbc.managex.R;

public class RegistrationActivity extends AppCompatActivity {
    EditText registerEmail,registerPassword,confirmPassword;
    Button registerButton;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerEmail = findViewById(R.id.etregisterEmail);
        registerPassword = findViewById(R.id.etregisterPassword);
        confirmPassword = findViewById(R.id.etregisterConfirmPassword);
        registerButton = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                String confPassword = confirmPassword.getText().toString();
                if(!(email.isEmpty() || password.isEmpty() || confPassword.isEmpty())){
                    if(!confPassword.equals(password)){
                        Toast.makeText(RegistrationActivity.this,"Password didn't match.",Toast.LENGTH_LONG).show();
                    }
                    else{
                        registerUser(email,password);
                    }
                }
                else{
                    Toast.makeText(RegistrationActivity.this,"Fill all the required fields.",Toast.LENGTH_LONG).show();

                }
            }

            public void registerUser(String email,String password){

                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    databaseReference.child("Users").child(auth.getUid()).child("restaurantEmail").setValue(email);
                                    databaseReference.child("Users").child(auth.getUid()).child("restaurantPassword").setValue(password);
                                }
                                else{
                                    Toast.makeText(RegistrationActivity.this,"There is a problem.",Toast.LENGTH_LONG).show();
                                }
                                Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                                i.putExtra("store_eid",email);
                                startActivity(i);
                                finish();

                            }
                        });

            }

        });
    }
}