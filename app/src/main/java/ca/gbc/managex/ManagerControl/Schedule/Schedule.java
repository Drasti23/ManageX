package ca.gbc.managex.ManagerControl.Schedule;

public class Schedule {
    private String scheduleId;
    private int empId;
    private String empName;
    private String week;
    private String day;
    private String startTime;
    private String endTime;
    private String duration;

    public Schedule() {
        // Default constructor required for Firebase
    }

    public Schedule(String scheduleId, int empId, String empName, String week, String day, String startTime, String endTime, String duration) {
        this.scheduleId = scheduleId;
        this.empId = empId;
        this.empName = empName;
        this.week = week;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}