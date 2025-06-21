package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import obj.Order;
import obj.Store;
import obj.database;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String> colCustomerName;
    @FXML private TableColumn<Order, String> colDate;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colPaymentMethod;
    @FXML private TableColumn<Order, String> colPaymentStatus;
    @FXML private TableColumn<Order, Long> colTotal;

    // ✅ THÊM - Các FXML controls từ user-cart-view.fxml
    @FXML private DatePicker dpFromDate;
    @FXML private DatePicker dpToDate;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private ComboBox<String> cbPaymentStatus;
    @FXML private TextField tfOrderId;
    @FXML private Button btnFilter;
    @FXML private Button btnResetFilter;

    @FXML private TextField tfSelectedOrderId;
    @FXML private ComboBox<String> cbNewStatus;
    @FXML private ComboBox<String> cbNewPaymentStatus;
    @FXML private Button btnUpdateOrder;
    @FXML private Button btnViewDetails;
    @FXML private Button btnDeleteOrder;

    @FXML private Label lblOrderCount;
    @FXML private Label lblTotalRevenue;

    // Additional columns from FXML
    @FXML private TableColumn<Order, Integer> colCustomerId;
    @FXML private TableColumn<Order, Integer> colEmployeeId;
    @FXML private TableColumn<Order, String> colDeliveryAddress;
    @FXML private TableColumn<Order, String> colNote;

    private Store store;

    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupComboBoxes();
        setupTableSelection();
        loadOrderData();
    }

    private void setupTable() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colPaymentStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // ✅ THÊM - Setup additional columns if they exist
        if (colCustomerId != null) {
            colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        }
        if (colEmployeeId != null) {
            colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        }
        if (colDeliveryAddress != null) {
            colDeliveryAddress.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        }
        if (colNote != null) {
            colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        }

        // Format total column
        colTotal.setCellFactory(col -> new TableCell<Order, Long>() {
            @Override
            protected void updateItem(Long total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d VND", total));
                }
            }
        });

        // Make table editable
        orderTable.setEditable(true);

        // Status column editable
        colStatus.setCellFactory(ComboBoxTableCell.forTableColumn(
            "Offline", "Chờ xác nhận", "Đã xác nhận", "Đã hủy"
        ));
        colStatus.setOnEditCommit(event -> {
            Order order = event.getRowValue();
            String newStatus = convertStatusToDb(event.getNewValue());
            order.setStatus(newStatus);
            updateOrderStatusInDB(order.getOrderId(), "status", newStatus);
        });

        // Payment status column editable
        colPaymentStatus.setCellFactory(ComboBoxTableCell.forTableColumn(
            "Đã thanh toán", "Chưa thanh toán", "Đang xử lý"
        ));
        colPaymentStatus.setOnEditCommit(event -> {
            Order order = event.getRowValue();
            String newPaymentStatus = convertPaymentStatusToDb(event.getNewValue());
            order.setPaymentStatus(newPaymentStatus);
            updateOrderStatusInDB(order.getOrderId(), "payment_status", newPaymentStatus);
        });
    }

    private void setupComboBoxes() {
        // Status filter
        if (cbStatus != null) {
            cbStatus.getItems().addAll(
                "Tất cả",
                "Offline",
                "Chờ xác nhận", 
                "Đã xác nhận",
                "Đã hủy"
            );
            cbStatus.setValue("Tất cả");
        }

        // Payment status filter
        if (cbPaymentStatus != null) {
            cbPaymentStatus.getItems().addAll(
                "Tất cả",
                "Đã thanh toán",
                "Chưa thanh toán",
                "Đang xử lý"
            );
            cbPaymentStatus.setValue("Tất cả");
        }

        // Action status combo
        if (cbNewStatus != null) {
            cbNewStatus.getItems().addAll(
                "offline", "pending", "confirmed", "cancelled"
            );
        }

        // Action payment status combo
        if (cbNewPaymentStatus != null) {
            cbNewPaymentStatus.getItems().addAll(
                "paid", "unpaid", "pending"
            );
        }
    }

    private void setupTableSelection() {
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && tfSelectedOrderId != null) {
                tfSelectedOrderId.setText(String.valueOf(newSelection.getOrderId()));
            }
        });
    }

    // ✅ THÊM - Method handleResetFilter
    @FXML
    private void handleResetFilter() {
        // Reset all filter controls
        if (cbStatus != null) cbStatus.setValue("Tất cả");
        if (cbPaymentStatus != null) cbPaymentStatus.setValue("Tất cả");
        if (dpFromDate != null) dpFromDate.setValue(null);
        if (dpToDate != null) dpToDate.setValue(null);
        if (dpStartDate != null) dpStartDate.setValue(null);
        if (dpEndDate != null) dpEndDate.setValue(null);
        if (tfOrderId != null) tfOrderId.clear();
        
        // Reload data
        handleFilter();
        
        showAlert("Đã reset bộ lọc!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleFilter() {
        ObservableList<Order> filteredOrders = getFilteredOrderData();
        orderTable.setItems(filteredOrders);
        updateStatistics(filteredOrders);
    }

    @FXML
    private void handleClearFilter() {
        handleResetFilter(); // Sử dụng lại logic reset
    }

    @FXML
    private void handleRefresh() {
        handleFilter();
        showAlert("Đã làm mới dữ liệu!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleUpdateOrder() {
        if (tfSelectedOrderId == null) {
            showAlert("UI không đầy đủ!", Alert.AlertType.ERROR);
            return;
        }
        
        String orderIdText = tfSelectedOrderId.getText().trim();
        if (orderIdText.isEmpty()) {
            showAlert("Vui lòng chọn đơn hàng!", Alert.AlertType.WARNING);
            return;
        }

        String newStatus = cbNewStatus != null ? cbNewStatus.getValue() : null;
        String newPaymentStatus = cbNewPaymentStatus != null ? cbNewPaymentStatus.getValue() : null;

        if (newStatus == null || newPaymentStatus == null) {
            showAlert("Vui lòng chọn trạng thái mới!", Alert.AlertType.WARNING);
            return;
        }

        int orderId = Integer.parseInt(orderIdText);
        updateOrderInDB(orderId, newStatus, newPaymentStatus);
        handleFilter();
    }

    @FXML
    private void handleViewDetails() {
        if (tfSelectedOrderId == null) {
            showAlert("UI không đầy đủ!", Alert.AlertType.ERROR);
            return;
        }
        
        String orderIdText = tfSelectedOrderId.getText().trim();
        if (orderIdText.isEmpty()) {
            showAlert("Vui lòng chọn đơn hàng!", Alert.AlertType.WARNING);
            return;
        }

        showOrderDetails(Integer.parseInt(orderIdText));
    }

    @FXML
    private void handleDeleteOrder() {
        if (tfSelectedOrderId == null) {
            showAlert("UI không đầy đủ!", Alert.AlertType.ERROR);
            return;
        }
        
        String orderIdText = tfSelectedOrderId.getText().trim();
        if (orderIdText.isEmpty()) {
            showAlert("Vui lòng chọn đơn hàng!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa đơn hàng");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa đơn hàng này?");
        confirmAlert.setContentText("Đơn hàng: " + orderIdText + "\n⚠️ Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteOrderFromDB(Integer.parseInt(orderIdText));
        }
    }

    // Database operations
    private void updateOrderInDB(int orderId, String newStatus, String newPaymentStatus) {
        String sql = "UPDATE Orders SET status = ?, payment_status = ? WHERE order_id = ?";
        
        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(sql)) {
            
            prepare.setString(1, newStatus);
            prepare.setString(2, newPaymentStatus);
            prepare.setInt(3, orderId);
            
            int rowsAffected = prepare.executeUpdate();
            
            if (rowsAffected > 0) {
                showAlert("Cập nhật đơn hàng thành công!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Không tìm thấy đơn hàng để cập nhật!", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi cập nhật: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateOrderStatusInDB(int orderId, String column, String newValue) {
        String sql = "UPDATE Orders SET " + column + " = ? WHERE order_id = ?";
        
        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(sql)) {
            
            prepare.setString(1, newValue);
            prepare.setInt(2, orderId);
            prepare.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi cập nhật trạng thái: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteOrderFromDB(int orderId) {
        String sql = "DELETE FROM Orders WHERE order_id = ?";
        
        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(sql)) {
            
            prepare.setInt(1, orderId);
            int rowsAffected = prepare.executeUpdate();
            
            if (rowsAffected > 0) {
                showAlert("Xóa đơn hàng thành công!", Alert.AlertType.INFORMATION);
                handleFilter(); // Refresh data
                if (tfSelectedOrderId != null) {
                    tfSelectedOrderId.clear();
                }
            } else {
                showAlert("Không tìm thấy đơn hàng để xóa!", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi xóa đơn hàng: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private ObservableList<Order> getAllOrderData() {
        ObservableList<Order> orders = FXCollections.observableArrayList();

        String sql = "SELECT o.*, u.full_name as customer_name " +
                    "FROM Orders o " +
                    "LEFT JOIN Customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN Users u ON c.customer_id = u.user_id " +
                    "ORDER BY o.order_id DESC";

        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(sql);
             ResultSet result = prepare.executeQuery()) {

            while (result.next()) {
                Timestamp timestamp = result.getTimestamp("date");
                LocalDateTime dateTime = timestamp != null ? timestamp.toLocalDateTime() : null;
                
                Order order = new Order(
                    result.getInt("order_id"),
                    result.getInt("customer_id"),
                    result.getInt("employee_id"),
                    dateTime,
                    result.getString("status"),
                    result.getString("payment_method"),
                    result.getString("payment_status"),
                    result.getString("note") != null ? result.getString("note") : "",
                    result.getString("delivery_address") != null ? result.getString("delivery_address") : "",
                    result.getLong("total")
                );
                
                order.setCustomerName(result.getString("customer_name") != null ? 
                    result.getString("customer_name") : "Khách vãng lai");
                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        return orders;
    }

    private ObservableList<Order> getFilteredOrderData() {
        ObservableList<Order> orders = FXCollections.observableArrayList();

        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT o.*, u.full_name as customer_name " +
            "FROM Orders o " +
            "LEFT JOIN Customers c ON o.customer_id = c.customer_id " +
            "LEFT JOIN Users u ON c.customer_id = u.user_id " +
            "WHERE 1=1 "
        );

        // Add filters - check for null to handle different FXML layouts
        if (cbStatus != null && cbStatus.getValue() != null && !"Tất cả".equals(cbStatus.getValue())) {
            sqlBuilder.append("AND o.status = ? ");
        }
        
        if (cbPaymentStatus != null && cbPaymentStatus.getValue() != null && !"Tất cả".equals(cbPaymentStatus.getValue())) {
            sqlBuilder.append("AND o.payment_status = ? ");
        }
        
        // Handle both dpFromDate and dpStartDate (different FXML files may use different names)
        if ((dpFromDate != null && dpFromDate.getValue() != null) || 
            (dpStartDate != null && dpStartDate.getValue() != null)) {
            sqlBuilder.append("AND DATE(o.date) >= ? ");
        }
        
        if ((dpToDate != null && dpToDate.getValue() != null) || 
            (dpEndDate != null && dpEndDate.getValue() != null)) {
            sqlBuilder.append("AND DATE(o.date) <= ? ");
        }

        // Search by order ID
        if (tfOrderId != null && !tfOrderId.getText().trim().isEmpty()) {
            sqlBuilder.append("AND o.order_id = ? ");
        }

        sqlBuilder.append("ORDER BY o.order_id DESC");

        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(sqlBuilder.toString())) {

            int paramIndex = 1;

            if (cbStatus != null && cbStatus.getValue() != null && !"Tất cả".equals(cbStatus.getValue())) {
                prepare.setString(paramIndex++, convertStatusToDb(cbStatus.getValue()));
            }
            
            if (cbPaymentStatus != null && cbPaymentStatus.getValue() != null && !"Tất cả".equals(cbPaymentStatus.getValue())) {
                prepare.setString(paramIndex++, convertPaymentStatusToDb(cbPaymentStatus.getValue()));
            }
            
            if ((dpFromDate != null && dpFromDate.getValue() != null) || 
                (dpStartDate != null && dpStartDate.getValue() != null)) {
                java.time.LocalDate startDate = dpFromDate != null ? dpFromDate.getValue() : dpStartDate.getValue();
                prepare.setDate(paramIndex++, Date.valueOf(startDate));
            }
            
            if ((dpToDate != null && dpToDate.getValue() != null) || 
                (dpEndDate != null && dpEndDate.getValue() != null)) {
                java.time.LocalDate endDate = dpToDate != null ? dpToDate.getValue() : dpEndDate.getValue();
                prepare.setDate(paramIndex++, Date.valueOf(endDate));
            }

            if (tfOrderId != null && !tfOrderId.getText().trim().isEmpty()) {
                prepare.setInt(paramIndex++, Integer.parseInt(tfOrderId.getText().trim()));
            }

            try (ResultSet result = prepare.executeQuery()) {
                while (result.next()) {
                    Timestamp timestamp = result.getTimestamp("date");
                    LocalDateTime dateTime = timestamp != null ? timestamp.toLocalDateTime() : null;
                    
                    Order order = new Order(
                        result.getInt("order_id"),
                        result.getInt("customer_id"),
                        result.getInt("employee_id"),
                        dateTime,
                        result.getString("status"),
                        result.getString("payment_method"),
                        result.getString("payment_status"),
                        result.getString("note") != null ? result.getString("note") : "",
                        result.getString("delivery_address") != null ? result.getString("delivery_address") : "",
                        result.getLong("total")
                    );
                    
                    order.setCustomerName(result.getString("customer_name") != null ? 
                        result.getString("customer_name") : "Khách vãng lai");
                    orders.add(order);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi lọc dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        return orders;
    }

    private void showOrderDetails(int orderId) {
        StringBuilder details = new StringBuilder();
        
        String orderSql = "SELECT o.*, " +
                         "u.full_name as customer_name, " +
                         "emp_u.full_name as employee_name " +
                         "FROM Orders o " +
                         "LEFT JOIN Customers c ON o.customer_id = c.customer_id " +
                         "LEFT JOIN Users u ON c.customer_id = u.user_id " +
                         "LEFT JOIN Employees e ON o.employee_id = e.employee_id " +
                         "LEFT JOIN Users emp_u ON e.employee_id = emp_u.user_id " +
                         "WHERE o.order_id = ?";

        try (Connection connect = database.connectDB();
             PreparedStatement prepare = connect.prepareStatement(orderSql)) {
            
            prepare.setInt(1, orderId);
            
            try (ResultSet result = prepare.executeQuery()) {
                if (result.next()) {
                    details.append("🧾 CHI TIẾT ĐƠN HÀNG #").append(orderId).append("\n");
                    details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                    
                    details.append("👤 THÔNG TIN KHÁCH HÀNG:\n");
                    details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    details.append("📝 Tên: ").append(result.getString("customer_name") != null ? 
                        result.getString("customer_name") : "Khách vãng lai").append("\n");
                    details.append("🕒 Ngày đặt: ").append(result.getTimestamp("date")).append("\n");
                    details.append("👨‍💼 Nhân viên: ").append(result.getString("employee_name") != null ? 
                        result.getString("employee_name") : "N/A").append("\n\n");
                    
                    details.append("📊 TRẠNG THÁI ĐƠN HÀNG:\n");
                    details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    details.append("📋 Trạng thái: ").append(result.getString("status")).append("\n");
                    details.append("💳 Thanh toán: ").append(result.getString("payment_method")).append("\n");
                    details.append("✅ TT thanh toán: ").append(result.getString("payment_status")).append("\n");
                    if (result.getString("delivery_address") != null) {
                        details.append("🏠 Địa chỉ giao: ").append(result.getString("delivery_address")).append("\n");
                    }
                    if (result.getString("note") != null) {
                        details.append("📝 Ghi chú: ").append(result.getString("note")).append("\n");
                    }
                    details.append("\n");
                    
                    details.append("💵 THÔNG TIN THANH TOÁN:\n");
                    details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    details.append("✅ Tổng tiền: ").append(String.format("%,d VND", result.getLong("total"))).append("\n");
                }
            }

            // Lấy chi tiết sản phẩm
            details.append("\n📦 CHI TIẾT SẢN PHẨM:\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            String orderlinesSql = "SELECT ol.*, p.product_name " +
                                  "FROM Orderlines ol " +
                                  "JOIN Products p ON ol.product_id = p.product_id " +
                                  "WHERE ol.order_id = ?";
            
            try (PreparedStatement olPrepare = connect.prepareStatement(orderlinesSql)) {
                olPrepare.setInt(1, orderId);
                
                try (ResultSet olResult = olPrepare.executeQuery()) {
                    int itemNum = 1;
                    while (olResult.next()) {
                        details.append(String.format("%d. %s\n", itemNum++, olResult.getString("product_name")));
                        details.append(String.format("   • Số lượng: %d\n", olResult.getInt("quantity")));
                        details.append(String.format("   • Đơn giá: %,d VND\n", olResult.getInt("price")));
                        details.append(String.format("   • Thành tiền: %,d VND\n\n", 
                            olResult.getInt("quantity") * olResult.getInt("price")));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            details.append("Lỗi tải chi tiết: ").append(e.getMessage());
        }

        // Hiển thị dialog
        Alert detailAlert = new Alert(Alert.AlertType.INFORMATION);
        detailAlert.setTitle("Chi tiết đơn hàng");
        detailAlert.setHeaderText("Đơn hàng #" + orderId);
        detailAlert.setContentText(details.toString());
        
        detailAlert.setResizable(true);
        detailAlert.getDialogPane().setPrefSize(600, 500);
        
        detailAlert.showAndWait();
    }

    private void loadOrderData() {
        handleFilter();
    }

    private void updateStatistics(ObservableList<Order> orders) {
        int orderCount = orders.size();
        long totalRevenue = orders.stream()
            .filter(order -> !"cancelled".equals(order.getStatus()))
            .mapToLong(Order::getTotal)
            .sum();

        if (lblOrderCount != null) {
            lblOrderCount.setText("Tổng số đơn hàng: " + orderCount);
        }
        if (lblTotalRevenue != null) {
            lblTotalRevenue.setText("Doanh thu: " + String.format("%,d VND", totalRevenue));
        }
    }

    // Helper methods
    private String convertStatusToDb(String displayStatus) {
        switch (displayStatus) {
            case "Offline": return "offline";
            case "Chờ xác nhận": return "pending";
            case "Đã xác nhận": return "confirmed";
            case "Đã hủy": return "cancelled";
            default: return displayStatus;
        }
    }

    private String convertPaymentStatusToDb(String displayStatus) {
        switch (displayStatus) {
            case "Đã thanh toán": return "paid";
            case "Chưa thanh toán": return "unpaid";
            case "Đang xử lý": return "pending";
            default: return displayStatus;
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

