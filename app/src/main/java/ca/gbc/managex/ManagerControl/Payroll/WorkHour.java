package ca.gbc.managex.ManagerControl.Payroll;

public class WorkHour {
    private String Date,clockInTime,clockOutTime,duration;

    public WorkHour(String date, String clockInTime, String clockOutTime, String duration) {
        Date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.duration = duration;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(String clockInTime) {
        this.clockInTime = clockInTime;
    }

    public String getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(String clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
