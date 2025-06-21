package obj;

import DATABASE.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {
	
	public Employee getEmployeeByPhone(String phone) throws SQLException {
	    String query = "SELECT u.user_id, u.full_name, u.phone FROM Users u " +
	                   "JOIN Employees e ON u.user_id = e.employee_id " +
	                   "WHERE u.phone = ?";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, phone);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            return new Employee(
	                rs.getInt("user_id"),
	                rs.getString("full_name"),
	                rs.getString("phone")
	            );
	        }
	    }
	    throw new SQLException("Không tìm thấy nhân viên với số điện thoại: " + phone);
	}
    
    // Lấy thông tin nhân viên bằng ID
	public Employee getEmployeeById(int employeeId) throws SQLException {
	    // Bỏ base_salary nếu không cần thiết
	    String query = "SELECT u.user_id, u.full_name, u.dob, u.phone, u.email " +
	                   "FROM Users u JOIN Employees e ON u.user_id = e.employee_id " +
	                   "WHERE u.user_id = ?";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setInt(1, employeeId);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            LocalDate dob = LocalDate.parse(rs.getString("dob"), formatter);
	            
	            return new Employee(
	                rs.getInt("user_id"),
	                rs.getString("full_name"),
	                dob,
	                rs.getString("phone"),
	                rs.getString("email"),
	                "/images/employees/" + rs.getInt("user_id") + ".jpg"
	            );
	        }
	    }
	    throw new SQLException("Employee not found with ID: " + employeeId);
	}

    // Lấy lịch làm việc của nhân viên trong một tháng cụ thể
	public Map<LocalDate, List<ShiftInfo>> getEmployeeSchedule(int employeeId, int month, int year) throws SQLException {
	    Map<LocalDate, List<ShiftInfo>> scheduleMap = new HashMap<>();
	    
	    String query = "SELECT s.date, s.time, sd.status " +
	                  "FROM Shifts s " +
	                  "JOIN Shift_Details sd ON s.shift_id = sd.shift_id " +
	                  "WHERE sd.employee_id = ? " +
	                  "AND MONTH(s.date) = ? " +
	                  "AND YEAR(s.date) = ? " +
	                  "ORDER BY s.date, s.time";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setInt(1, employeeId);
	        stmt.setInt(2, month);
	        stmt.setInt(3, year);
	        
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            LocalDate date = rs.getDate("date").toLocalDate();
	            String time = rs.getString("time");
	            String status = rs.getString("status");
	            
	            scheduleMap.computeIfAbsent(date, k -> new ArrayList<>())
	                      .add(new ShiftInfo(time, status));
	        }
	    }
	    return scheduleMap;
	}

    // Kiểm tra xem nhân viên có làm ca cụ thể không
    public boolean hasShiftOnDate(int employeeId, LocalDate date, String shiftType) throws SQLException {
        String query = "SELECT 1 FROM Shifts s " +
                       "JOIN Shift_Details sd ON s.shift_id = sd.shift_id " +
                       "WHERE sd.employee_id = ? " +
                       "AND s.date = ? " +
                       "AND s.time = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, shiftType);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    
    public int getEmployeeBaseSalary(int employeeId, int month, int year) throws SQLException {
        String query = "SELECT base_salary FROM Salary " +
                     "WHERE employee_id = ? " +
                     "AND month = ? " +
                     "AND year = ? " +
                     "LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("base_salary");
            }
        }
        // Nếu không tìm thấy bản ghi lương cho tháng đó
        return 200000; // Giá trị mặc định
    }
    
    public Map<String, Double> getEmployeeSalary(int employeeId, int month, int year) throws SQLException {
        Map<String, Double> result = new HashMap<>();
        String query = "SELECT base_salary, bonus, total FROM Salary WHERE employee_id = ? AND month = ? AND year = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result.put("salary", rs.getDouble("total")); // Hiển thị tổng lương
                result.put("bonus", rs.getDouble("bonus"));
            }
        }
        return result.isEmpty() ? null : result;
    }
    
    public int getCompletedShiftsCount(int employeeId, int month, int year) throws SQLException {
        String query = "SELECT COUNT(*) as completed_count FROM Shift_Details sd " +
                       "JOIN Shifts s ON sd.shift_id = s.shift_id " +
                       "WHERE sd.employee_id = ? " +
                       "AND MONTH(s.date) = ? " +
                       "AND YEAR(s.date) = ? " +
                       "AND (sd.status = 'completed'OR sd.status = 'late')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("completed_count");
            }
        }
        return 0;
    }
}