package ca.gbc.managex.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.gbc.managex.MainActivity;
import ca.gbc.managex.R;

public class Registration2 extends AppCompatActivity {

    Button register;
    EditText resName , phoneNumber;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    String stringResName,stringPhoneNumber;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back = findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        register = findViewById(R.id.btnRegisterStore);
        resName = findViewById(R.id.etresName);
        phoneNumber = findViewById(R.id.etresPhoneNumber);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            stringResName = resName.getText().toString();
            stringPhoneNumber  = phoneNumber.getText().toString();
            if(stringResName.isEmpty() || stringPhoneNumber.isEmpty()){
                Toast.makeText(Registration2.this,"Please fill all the required field.",Toast.LENGTH_SHORT).show();
            }
            else{
                saveRestaurantInfoToFirebase(stringResName,stringPhoneNumber);
                Intent i = new Intent(Registration2.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            }
        });




    }

    public void saveRestaurantInfoToFirebase(String name, String number){
        reference.child("Users").child(auth.getUid()).child("credentials").child("restaurantName").setValue(name);
        reference.child("Users").child(auth.getUid()).child("credentials").child("contactNumber").setValue(number);

    }
}