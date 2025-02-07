package ca.gbc.managex.ManageRestaurantProfile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.gbc.managex.R;

public class ManageRestaurantProfile extends AppCompatActivity {
    private EditText etRestaurantName, etRestaurantEmail, etContactNumber, etRestaurantCuisine, etRestaurantAddress;
    private Button btnSaveProfile, btnCancel, btnEditProfile;
    private DatabaseReference restaurantRef;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_restaurant_profile);

        etRestaurantName = findViewById(R.id.editRestaurantName);
        etContactNumber = findViewById(R.id.editContactNumber);
        etRestaurantEmail = findViewById(R.id.editRestaurantEmail);
        etRestaurantCuisine = findViewById(R.id.editRestaurantCuisine);
        etRestaurantAddress = findViewById(R.id.editRestaurantAddress);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnCancel = findViewById(R.id.btnCancelProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        backButton = findViewById(R.id.backButton);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        restaurantRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(auth.getCurrentUser().getUid()).child("credentials");

        fetchRestaurantProfile();

        backButton.setOnClickListener(view -> finish());
        btnEditProfile.setOnClickListener(v -> enableEditing(true));
        btnSaveProfile.setOnClickListener(v -> saveProfileUpdates());
        btnCancel.setOnClickListener(v -> fetchRestaurantProfile());
    }

    private void fetchRestaurantProfile() {
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etRestaurantName.setText(snapshot.child("restaurantName").getValue(String.class));
                    etRestaurantEmail.setText(snapshot.child("restaurantEmail").getValue(String.class));
                    etContactNumber.setText(snapshot.child("contactNumber").getValue(String.class));
                    etRestaurantCuisine.setText(snapshot.child("restaurantCuisine").getValue(String.class));
                    etRestaurantAddress.setText(snapshot.child("restaurantAddress").getValue(String.class));
                    enableEditing(false);
                } else {
                    Toast.makeText(ManageRestaurantProfile.this, "Profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageRestaurantProfile.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEditing(boolean isEditable) {
        etRestaurantName.setEnabled(isEditable);
        etRestaurantEmail.setEnabled(isEditable);
        etContactNumber.setEnabled(isEditable);
        etRestaurantCuisine.setEnabled(isEditable);
        etRestaurantAddress.setEnabled(isEditable);
    }

    private void saveProfileUpdates() {
        restaurantRef.child("restaurantName").setValue(etRestaurantName.getText().toString().trim());
        restaurantRef.child("restaurantEmail").setValue(etRestaurantEmail.getText().toString().trim());
        restaurantRef.child("contactNumber").setValue(etContactNumber.getText().toString().trim());
        restaurantRef.child("restaurantCuisine").setValue(etRestaurantCuisine.getText().toString().trim());
        restaurantRef.child("restaurantAddress").setValue(etRestaurantAddress.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        enableEditing(false);
                    } else {
                        Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
