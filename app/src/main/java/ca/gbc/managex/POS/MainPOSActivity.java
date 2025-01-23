package ca.gbc.managex.POS;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.gbc.managex.R;
import ca.gbc.managex.databinding.ActivityMainPosactivityBinding;
import ca.gbc.managex.user.LoginActivity;

public class MainPOSActivity extends AppCompatActivity {

   ActivityMainPosactivityBinding binding;
   FirebaseDatabase database = FirebaseDatabase.getInstance();
   DatabaseReference reference = database.getReference();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String storeName;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_posactivity);
        binding = ActivityMainPosactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.ActivityPOS);
        getStoreName();
        back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getStoreName(){

        reference.child("Users").child(user.getUid()).child("credentials").child("restaurantName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    storeName = snapshot.getValue(String.class);
                    binding.storeNamePOS.setText(storeName);
                }
                else{
                    Toast.makeText(MainPOSActivity.this,"Restaurant Name not found.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainPOSActivity.this, "Failed to fetch store name.", Toast.LENGTH_SHORT).show();
                storeName="NotFound";
                binding.storeNamePOS.setText(storeName);
            }
        });
    }
}