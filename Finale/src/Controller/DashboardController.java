package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import obj.database;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // ===== FXML LABELS - THỐNG KÊ TỔNG QUAN =====
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblTodayRevenue;
    @FXML private Label lblTodayOrders;
    @FXML private Label lblMonthRevenue;
    @FXML private Label lblYearRevenue;

    // ===== FXML CHARTS =====
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private BarChart<String, Number> orderChart;
    @FXML private PieChart productCategoryChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboardData();
        loadCharts();
    }

    // ✅ TẢI DỮ LIỆU DASHBOARD
    private void loadDashboardData() {
        try (Connection conn = database.connectDB()) {
            
            // 📊 THỐNG KÊ TỔNG QUAN
            loadOverallStatistics(conn);
            
            // 📅 THỐNG KÊ THEO NGÀY
            loadDailyStatistics(conn);
            
            // 📆 THỐNG KÊ THEO THÁNG/NĂM
            loadMonthlyYearlyStatistics(conn);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }
    }

    // 📊 THỐNG KÊ TỔNG QUAN
    private void loadOverallStatistics(Connection conn) throws Exception {
        
        // Tổng doanh thu (từ đơn hàng đã thanh toán)
        String revenueSql = "SELECT COALESCE(SUM(total), 0) as total_revenue FROM Orders WHERE payment_status = 'paid'";
        try (PreparedStatement stmt = conn.prepareStatement(revenueSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                long totalRevenue = rs.getLong("total_revenue");
                lblTotalRevenue.setText(String.format("%,d VND", totalRevenue));
            }
        }

        // Tổng số đơn hàng
        String ordersSql = "SELECT COUNT(*) as total_orders FROM Orders";
        try (PreparedStatement stmt = conn.prepareStatement(ordersSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int totalOrders = rs.getInt("total_orders");
                lblTotalOrders.setText(String.valueOf(totalOrders));
            }
        }

        // Tổng số sản phẩm
        String productsSql = "SELECT COUNT(*) as total_products FROM Products";
        try (PreparedStatement stmt = conn.prepareStatement(productsSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int totalProducts = rs.getInt("total_products");
                lblTotalProducts.setText(String.valueOf(totalProducts));
            }
        }

        // Tổng số khách hàng
        String customersSql = "SELECT COUNT(*) as total_customers FROM Customers";
        try (PreparedStatement stmt = conn.prepareStatement(customersSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int totalCustomers = rs.getInt("total_customers");
                lblTotalCustomers.setText(String.valueOf(totalCustomers));
            }
        }
    }

    // 📅 THỐNG KÊ THEO NGÀY
    private void loadDailyStatistics(Connection conn) throws Exception {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Doanh thu hôm nay
        String todayRevenueSql = "SELECT COALESCE(SUM(total), 0) as today_revenue FROM Orders WHERE DATE(date) = ? AND payment_status = 'paid'";
        try (PreparedStatement stmt = conn.prepareStatement(todayRevenueSql)) {
            stmt.setString(1, todayStr);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long todayRevenue = rs.getLong("today_revenue");
                    lblTodayRevenue.setText(String.format("%,d VND", todayRevenue));
                }
            }
        }

        // Số đơn hàng hôm nay
        String todayOrdersSql = "SELECT COUNT(*) as today_orders FROM Orders WHERE DATE(date) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(todayOrdersSql)) {
            stmt.setString(1, todayStr);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int todayOrders = rs.getInt("today_orders");
                    lblTodayOrders.setText(String.valueOf(todayOrders));
                }
            }
        }
    }

    // 📆 THỐNG KÊ THEO THÁNG/NĂM
    private void loadMonthlyYearlyStatistics(Connection conn) throws Exception {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // Doanh thu tháng này
        String monthRevenueSql = "SELECT COALESCE(SUM(total), 0) as month_revenue FROM Orders " +
                               "WHERE MONTH(date) = ? AND YEAR(date) = ? AND payment_status = 'paid'";
        try (PreparedStatement stmt = conn.prepareStatement(monthRevenueSql)) {
            stmt.setInt(1, currentMonth);
            stmt.setInt(2, currentYear);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long monthRevenue = rs.getLong("month_revenue");
                    lblMonthRevenue.setText(String.format("%,d VND", monthRevenue));
                }
            }
        }

        // Doanh thu năm này
        String yearRevenueSql = "SELECT COALESCE(SUM(total), 0) as year_revenue FROM Orders " +
                              "WHERE YEAR(date) = ? AND payment_status = 'paid'";
        try (PreparedStatement stmt = conn.prepareStatement(yearRevenueSql)) {
            stmt.setInt(1, currentYear);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long yearRevenue = rs.getLong("year_revenue");
                    lblYearRevenue.setText(String.format("%,d VND", yearRevenue));
                }
            }
        }
    }

    // 📈 TẢI BIỂU ĐỒ
    private void loadCharts() {
        loadRevenueChart();
        loadOrderChart();
        loadProductCategoryChart();
    }

    // 📈 BIỂU ĐỒ DOANH THU 7 NGÀY QUA
    private void loadRevenueChart() {
        try (Connection conn = database.connectDB()) {
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Doanh thu");

            String sql = "SELECT DATE(date) as order_date, COALESCE(SUM(total), 0) as daily_revenue " +
                        "FROM Orders WHERE DATE(date) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                        "AND payment_status = 'paid' " +
                        "GROUP BY DATE(date) ORDER BY DATE(date)";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String date = rs.getString("order_date");
                    long revenue = rs.getLong("daily_revenue");
                    
                    // Format ngày để hiển thị đẹp hơn
                    LocalDate localDate = LocalDate.parse(date);
                    String formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd/MM"));
                    
                    series.getData().add(new XYChart.Data<>(formattedDate, revenue));
                }
            }

            revenueChart.getData().clear();
            revenueChart.getData().add(series);
            revenueChart.setTitle("Doanh thu 7 ngày qua");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📊 BIỂU ĐỒ SỐ LƯỢNG ĐẠI HÀNG THEO TRẠNG THÁI
    private void loadOrderChart() {
        try (Connection conn = database.connectDB()) {
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Số lượng đơn hàng");

            String sql = "SELECT status, COUNT(*) as order_count FROM Orders GROUP BY status";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("order_count");
                    
                    // Chuyển đổi status sang tiếng Việt
                    String vietnameseStatus = convertStatusToVietnamese(status);
                    series.getData().add(new XYChart.Data<>(vietnameseStatus, count));
                }
            }

            orderChart.getData().clear();
            orderChart.getData().add(series);
            orderChart.setTitle("Đơn hàng theo trạng thái");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🥧 BIỂU ĐỒ TRÒN - SẢN PHẨM THEO CATEGORY
    private void loadProductCategoryChart() {
        try (Connection conn = database.connectDB()) {
            
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            String sql = "SELECT category, COUNT(*) as product_count FROM Products GROUP BY category";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String category = rs.getString("category");
                    int count = rs.getInt("product_count");
                    
                    pieChartData.add(new PieChart.Data(category + " (" + count + ")", count));
                }
            }

            productCategoryChart.setData(pieChartData);
            productCategoryChart.setTitle("Phân loại sản phẩm");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ CHUYỂN ĐỔI STATUS SANG TIẾNG VIỆT
    private String convertStatusToVietnamese(String status) {
        switch (status.toLowerCase()) {
            case "offline": return "Offline";
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }

    // ✅ REFRESH DASHBOARD
    @FXML
    private void refreshDashboard() {
        loadDashboardData();
        loadCharts();
    }
}
