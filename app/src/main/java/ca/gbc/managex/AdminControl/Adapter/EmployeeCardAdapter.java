package ca.gbc.managex.AdminControl.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.R;

public class EmployeeCardAdapter extends RecyclerView.Adapter<EmployeeCardAdapter.ItemViewHolder>{


    private ArrayList<Employee> employeeList;

    public EmployeeCardAdapter(ArrayList<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_card,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
    Employee employee = employeeList.get(position);
    String name = employee.getFirstName().concat(" "+ employee.getLastName());
    holder.name.setText(name);
    holder.code.setText(employee.getEmpCode()+"");
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView name,code;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvEmpNameCard);
            code = itemView.findViewById(R.id.tvEmpCodeCard);
        }
    }
}
