package ca.gbc.managex.AdminControl.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.gbc.managex.AdminControl.Adapter.EmployeeCardAdapter;
import ca.gbc.managex.AdminControl.AdminControlActivity;
import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.AdminControl.Dialogs.AddEmployeeDialog;
import ca.gbc.managex.R;
import ca.gbc.managex.databinding.FragmentManageEmployeeBinding;

public class ManageEmployeeFragment extends Fragment {

    FragmentManageEmployeeBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("Users").child(user.getUid());
    FloatingActionButton fab;
    ArrayList<Employee> employeeList = new ArrayList<Employee>();
    private EmployeeCardAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manage_employee,container,false);
        binding = FragmentManageEmployeeBinding.inflate(inflater,container,false);
        recyclerView = view.findViewById(R.id.employeeRecyclerView);
        adapter = new EmployeeCardAdapter(employeeList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getEmpDataFromFirebase();
        

        fab = view.findViewById(R.id.addEmpFab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEmployeeDialog.display(getParentFragmentManager());

                Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT).show();
                sampleEmployeeAdded();
            }
        });

    return view;
    }

    public void addEmployeeToDatabase(Employee employee){
        reference.child("employeeInfo").child(String.valueOf(employee.getId())).setValue(employee);
    }

    public void sampleEmployeeAdded(){

        Employee employee = new Employee(1,"First","Last","email.com","+12345678","12-2-2","chef",5050,5050);
        addEmployeeToDatabase(employee);

    }

    public void getEmpDataFromFirebase(){
      reference.child("employeeInfo").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              Iterable<DataSnapshot> children = snapshot.getChildren();
              for (DataSnapshot child: children) {
                  Employee employee = child.getValue(Employee.class);
                  employeeList.add(employee);
              }
              adapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }
}
