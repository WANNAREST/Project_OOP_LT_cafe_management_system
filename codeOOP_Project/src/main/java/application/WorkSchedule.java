package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkSchedule {
    private int shiftId;
    private LocalDate date;
    private String time; // "morning", "afternoon", "evening"
    private List<ShiftDetail> shiftDetails = new ArrayList<>();

    public WorkSchedule(int shiftId, LocalDate date, String time) {
        this.shiftId = shiftId;
        this.date = date;
        this.time = time;
    }

    // Getter - Setter
    public int getShiftId() { return shiftId; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }

    public void addShiftDetail(ShiftDetail detail) {
        shiftDetails.add(detail);
    }

    public List<ShiftDetail> getShiftDetails() {
        return shiftDetails;
    }

    public String getFormattedTime() {
        return switch (time.toLowerCase()) {
            case "morning" -> "Ca sáng (7h-12h)";
            case "afternoon" -> "Ca chiều (13h-18h)";
            case "evening" -> "Ca tối (19h-22h)";
            default -> time;
        };
    }
}
