package ca.gbc.managex.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.gbc.managex.MainActivity;
import ca.gbc.managex.R;

public class RegistrationActivity extends AppCompatActivity {
    EditText registerEmail,registerPassword,confirmPassword;
    Button registerButton;
    ImageView registerWithGoogleIV,back;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;


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

        registerWithGoogleIV = findViewById(R.id.registerWithGoogle);
        registerEmail = findViewById(R.id.etregisterEmail);
        registerPassword = findViewById(R.id.etregisterPassword);
        confirmPassword = findViewById(R.id.etregisterConfirmPassword);
        registerButton = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        back = findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

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
                                    databaseReference.child("Users").child(auth.getUid()).child("credentials").child("restaurantEmail").setValue(email);
                                    databaseReference.child("Users").child(auth.getUid()).child("credentials").child("restaurantPassword").setValue(password);
                                }
                                else{
                                    Toast.makeText(RegistrationActivity.this,"There is a problem.",Toast.LENGTH_LONG).show();
                                }
                                Intent i = new Intent(RegistrationActivity.this, Registration2.class);
                                i.putExtra("store_eid",email);
                                startActivity(i);
                                finish();

                            }
                        });

            }

        });
        registerWithGoogleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerWithGoogle();
            }
        });

    }
    public void registerWithGoogle(){
Intent signInIntent = googleSignInClient.getSignInIntent();
startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-in failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(RegistrationActivity.this);
                            if(account != null){
                                String email = account.getEmail();

                                databaseReference.child("Users").child(auth.getUid()).child("credentials").child("restaurantEmail").setValue(email);
                                Intent i = new Intent(RegistrationActivity.this, Registration2.class);
                                //i.putExtra("store_eid",email);
                                startActivity(i);
                                finish();

                            }
                            else{
                                Toast.makeText(RegistrationActivity.this,"Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}