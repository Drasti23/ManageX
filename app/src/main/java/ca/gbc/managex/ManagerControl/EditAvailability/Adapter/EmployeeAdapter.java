package ca.gbc.managex.ManagerControl.EditAvailability.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.gbc.managex.AdminControl.Classes.Employee;

import java.util.List;


import ca.gbc.managex.ManagerControl.EditAvailability.Availability;
import ca.gbc.managex.R;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {
    private Availability currentAvailability;

    private List<Employee> employeeList;
    private Context context;
    private int selectedPosition = 0;

    private OnEmployeeClickListener listener;

    public interface OnEmployeeClickListener{
        void onEmployeeClick(Availability empAvailability,int empId,String empName);
    }


    public EmployeeAdapter(List<Employee> employeeList,Context context1, OnEmployeeClickListener listener) {
        this.employeeList = employeeList;
        currentAvailability = null;
        context = context1;
        this.listener = listener;

    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_cardview, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Employee employee = employeeList.get(position);
        holder.tvEmployeeName.setText(employee.getFirstName() + " " + employee.getLastName());
        holder.tvEmployeePosition.setText(employee.getPosition());
        holder.tvEmployeeCode.setText(employee.getEmpCode()+"");
        holder.tvEmployeeId.setText(employee.getId()+"");
        if(selectedPosition == position){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white)); // Selected item color
        }
        else{
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.app_button));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousPosition = selectedPosition;
                selectedPosition = position; // Update selected position

                // Notify changes
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

                if (listener != null) {
                    getAvailabilityFromDatabase(employee.getId(), new OnAvailabilityFetchedListener() {
                        @Override
                        public void onAvailabilityFetched(Availability availability) {
                            listener.onEmployeeClick(availability, employee.getId(), employee.getFirstName() + " " + employee.getLastName());
                        }
                    });
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvEmployeePosition,tvEmployeeId,tvEmployeeCode;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeePosition = itemView.findViewById(R.id.tvEmployeePosition);
            tvEmployeeId = itemView.findViewById(R.id.tvEmployeeId);
            tvEmployeeCode = itemView.findViewById(R.id.tvEmployeeCode);
        }
    }
    public void updateList(List<Employee> filteredList) {
        this.employeeList = filteredList;
        notifyDataSetChanged();
    }
    public void getAvailabilityFromDatabase(int id, OnAvailabilityFetchedListener callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        reference.child("Users").child(auth.getUid()).child("employeeAvailability").child(id + "")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Availability availability = snapshot.getValue(Availability.class);
                            callback.onAvailabilityFetched(availability);
                        } else {
                            callback.onAvailabilityFetched(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onAvailabilityFetched(null);
                    }
                });
    }

    public interface OnAvailabilityFetchedListener {
        void onAvailabilityFetched(Availability availability);
    }

}
