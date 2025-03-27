package ca.gbc.managex.AdminControl.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import ca.gbc.managex.AdminControl.EmployeeInfoActivity;
import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.R;

public class EmployeeCardAdapter extends RecyclerView.Adapter<EmployeeCardAdapter.ItemViewHolder> {

    private ArrayList<Employee> employeeList;
    private Context context;

    public EmployeeCardAdapter(Context context, ArrayList<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        String name = employee.getFirstName() + " " + employee.getLastName();
        holder.name.setText(name);
        holder.code.setText(employee.getEmpCode() + "");

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmployeeInfoActivity.class);
            intent.putExtra("emp_name", name);
            intent.putExtra("emp_fname",employee.getFirstName());
            intent.putExtra("emp_lname",employee.getLastName());
            intent.putExtra("emp_id",String.valueOf(employee.getId()));
            intent.putExtra("emp_code", String.valueOf(employee.getEmpCode()));
            intent.putExtra("emp_password", String.valueOf(employee.getEmpPass()));
            intent.putExtra("emp_position", employee.getPosition());
            intent.putExtra("emp_contact",employee.getContactNumber());
            intent.putExtra("emp_email", employee.getEmail());
            intent.putExtra("emp_joining_date",employee.getJoiningDate());
            intent.putExtra("emp_type",employee.getEmpType());
            intent.putExtra("emp_rate",String.valueOf(employee.getPayRate()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, code;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvEmpNameCard);
            code = itemView.findViewById(R.id.tvEmpCode);
        }
    }
}
