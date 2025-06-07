package application;

public class WorkSchedule {
    private String date;
    private String shift;
    private String startTime;
    private String endTime;
    private String status;
    private String note;

    public WorkSchedule(String date, String shift, String startTime, String endTime, String status, String note) {
        this.date = date;
        this.shift = shift;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.note = note;
    }

    public String getDate() { return date; }
    public String getShift() { return shift; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
}
