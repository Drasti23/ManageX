package ca.gbc.managex.AdminControl.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import ca.gbc.managex.R;

public class AddSectionDialog extends DialogFragment {
private Toolbar toolbar;
private Button saveSection;
private String sectionName;
TextInputEditText etSection;
FirebaseDatabase database = FirebaseDatabase.getInstance();
FirebaseAuth auth = FirebaseAuth.getInstance();
FirebaseUser user = auth.getCurrentUser();
DatabaseReference reference = database.getReference().child("Users").child(user.getUid()).child("Menu");
public static final String TAG = "addSectionDialog";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public static AddSectionDialog display(FragmentManager fragmentManager){
        AddSectionDialog dialog = new AddSectionDialog();
        dialog.show(fragmentManager,TAG);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.add_section_dialog,container,false);
        saveSection = view.findViewById(R.id.btnSaveSection);
        etSection = view.findViewById(R.id.etAddSectionName);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.dialog_menu_add_emp);
        toolbar.setNavigationIcon(R.drawable.baseline_close_24);
        toolbar.setNavigationOnClickListener(v-> dismiss());
        saveSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sectionName = etSection.getText().toString();
                if(sectionName.isEmpty()){
                    Toast.makeText(getContext(),"Enter a section name",Toast.LENGTH_SHORT).show();
                }
                else{
                    saveSectionNameToDatabase(sectionName);
                    dismiss();
                }
            }
        });
    }
    private void saveSectionNameToDatabase(String sectionName) {
        reference.child(sectionName).setValue(sectionName);
    }
}
