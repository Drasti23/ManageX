package ca.gbc.managex.AdminControl.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.gbc.managex.R;
import ca.gbc.managex.RegisterTime.RegisterTimeActivity;

public class LoginDialog {

    private static final String TAG = "LoginDialog";
    Class c;


    public void showDialog(Activity activity,Class c) {
        this.c = c;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_login_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextInputEditText code = dialog.findViewById(R.id.editTextCode);
        TextInputEditText password = dialog.findViewById(R.id.editTextPass);
        Button dialogBtn_Enter = dialog.findViewById(R.id.buttonEnter);

        dialogBtn_Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eCode = code.getText().toString().trim();
                String ePass = password.getText().toString().trim();

                if (eCode.isEmpty() || ePass.isEmpty()) {
                    Toast.makeText(activity, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyCredentials(activity, dialog, eCode, ePass);
            }
        });

        dialog.show();
    }

    private void verifyCredentials(Activity activity, Dialog dialog, String eCode, String ePass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(activity, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String restaurantId = user.getUid();
        DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(restaurantId)
                .child("employeeInfo");

        employeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot empSnapshot : snapshot.getChildren()) {
                    String storedEmpCode = getStringValue(empSnapshot, "empCode");
                    String storedEmpPass = getStringValue(empSnapshot, "empPass");

                    if (storedEmpCode != null && storedEmpPass != null &&
                            storedEmpCode.equals(eCode) && storedEmpPass.equals(ePass)) {
                        String employeeName = getStringValue(empSnapshot,"firstName")  + " "+   getStringValue(empSnapshot,"lastName");
                        String employeeId = getStringValue(empSnapshot,"id");

                        // Successful login
                        Intent intent = new Intent(activity,c);
                        intent.putExtra("employeeName",employeeName);
                        intent.putExtra("employeeId",employeeId);
                        activity.startActivity(intent);
                        dialog.dismiss();
                        return;
                    }
                }
                // If no match is found
                Toast.makeText(activity, "Invalid credentials. Try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: " + error.getMessage());
                dialog.dismiss();
            }
        });
    }


    private String getStringValue(DataSnapshot snapshot, String key) {
        Object value = snapshot.child(key).getValue();
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Long) {
            return String.valueOf(value);
        }
        return null;
    }
}