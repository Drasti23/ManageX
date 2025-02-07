package ca.gbc.managex.ManagerControl.Payroll;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ca.gbc.managex.ManagerControl.Payroll.WorkHour;
import ca.gbc.managex.R;

public class WorkHoursAdapter extends RecyclerView.Adapter<WorkHoursAdapter.ViewHolder> {

    private List<WorkHour> workHoursList;

    public WorkHoursAdapter(List<WorkHour> workHoursList) {
        this.workHoursList = workHoursList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_hour_card_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkHour workHour = workHoursList.get(position);
        holder.textDate.setText("Date: "+workHour.getDate());
        holder.textClockIn.setText("Clock in time:" +workHour.getClockInTime());
        holder.textClockOut.setText(" Clock out time:"+workHour.getClockOutTime());
        holder.textDuration.setText("Duration: "+workHour.getDuration());
    }

    @Override
    public int getItemCount() {
        return workHoursList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textClockIn, textClockOut, textDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textClockIn = itemView.findViewById(R.id.text_clock_in);
            textClockOut = itemView.findViewById(R.id.text_clock_out);
            textDuration = itemView.findViewById(R.id.text_duration);
        }
    }
}
