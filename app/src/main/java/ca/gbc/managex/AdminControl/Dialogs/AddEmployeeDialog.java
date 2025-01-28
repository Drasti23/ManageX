package ca.gbc.managex.AdminControl.Dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.gbc.managex.R;
import ca.gbc.managex.databinding.AddEmployeeDialogBinding;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddEmployeeDialog extends DialogFragment implements AdapterView

        .OnItemSelectedListener {
    private Toolbar toolbar;
    private Spinner spinner;

    private Button datePicker;
    private String selectedDate;
    private LocalDate localDate = LocalDate.now();
    private AddEmployeeDialogBinding binding;

    public static final String TAG = "addEmployeeDialog";
    public static AddEmployeeDialog display(FragmentManager fragmentManager){
        AddEmployeeDialog dialog = new AddEmployeeDialog();
        dialog.show(fragmentManager,TAG);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.add_employee_dialog,container,false);
        binding = AddEmployeeDialogBinding.inflate(inflater,container,false);
        toolbar = view.findViewById(R.id.toolbar);
        spinner = view.findViewById(R.id.spinner);
        datePicker = (Button) view.findViewById(R.id.datePicker);

        spinner.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v-> dismiss());
        toolbar.inflateMenu(R.menu.dialog_menu_add_emp);
        toolbar.setNavigationIcon(R.drawable.baseline_close_24);
        toolbar.setOnMenuItemClickListener(item->{
            dismiss();
            return  true;
        });
        datePicker.setOnClickListener(v->{
            datePicker.setText(openDialog());
        });
        List<String> positions = new ArrayList<String>();
        positions.add("Crew");
        positions.add("Manager");
        positions.add("Cleaner");
        positions.add("Partner");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,positions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        binding.datePicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width,height);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private String openDialog(){
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                selectedDate = i + "/" + i1 + "/"+ i2;
            }
        },localDate.getYear(),localDate.getMonthValue(),localDate.getDayOfMonth());
        dialog.show();
        datePicker.setText(selectedDate);
        return selectedDate;
    }
    public void checkErrorInTextView(){

    }
}
