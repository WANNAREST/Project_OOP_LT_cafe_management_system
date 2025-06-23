package obj;

import Controller.DatabaseConnection;

import java.time.LocalDate;
import java.util.*;

public class WorkSchedule {
	private String employeeId;
	private Map<LocalDate, List<String>> shifts; // Thay đổi từ String sang List<String>

    public WorkSchedule(String employeeId) {
        this.employeeId = employeeId;
        this.shifts = new HashMap<>();
    }

    public void addShift(LocalDate date, String shiftTime) {
        shifts.computeIfAbsent(date, k -> new ArrayList<>()).add(shiftTime);
    }

    public List<String> getShiftsOnDate(LocalDate date) {
        return shifts.getOrDefault(date, Collections.emptyList());
    }

    public Map<LocalDate, List<String>> getAllShifts() {
        return shifts;
    }

    public void loadFromDatabase(int employeeId, int month, int year) throws Exception {
        String query = "SELECT s.date, s.time FROM Shifts s " +
                      "JOIN Shift_Details sd ON s.shift_id = sd.shift_id " +
                      "WHERE sd.employee_id = ? " +
                      "AND MONTH(s.date) = ? " +
                      "AND YEAR(s.date) = ?";

        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            
            var rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate date = rs.getDate("date").toLocalDate();
                String shift = rs.getString("time");
                addShift(date, shift);
            }
        }
    }
}