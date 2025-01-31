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
import java.util.List;

import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.AdminControl.EmployeeInfoActivity;
import ca.gbc.managex.R;

public class EmployeeCardAdapter extends RecyclerView.Adapter<EmployeeCardAdapter.ViewHolder> {
    private Context context;
    private List<Employee> employeeList;

    // Constructor to pass context and list of employees
    public EmployeeCardAdapter(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.nameTextView.setText(employee.getName());
        holder.roleTextView.setText(employee.getRole());

        // Handle the item click to open the employee detail activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmployeeInfoActivity.class);
            intent.putExtra("EMPLOYEE_ID", employee.getId());
            intent.putExtra("EMPLOYEE_NAME", employee.getName());
            intent.putExtra("EMPLOYEE_ROLE", employee.getRole());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    // ViewHolder class to bind views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, roleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.employee_name);
            roleTextView = itemView.findViewById(R.id.employee_role);
        }
    }
}

