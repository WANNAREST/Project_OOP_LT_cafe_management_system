package Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.ComboBoxTableCell;
import obj.Order;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
public class OrderController implements Initializable {
	 // Filter controls
    @FXML private DatePicker dpFromDate;
    @FXML private DatePicker dpToDate;
    @FXML private ComboBox<String> cbStatus;
    @FXML private ComboBox<String> cbPaymentStatus;
    @FXML private TextField tfOrderId;
    @FXML private Button btnFilter;
    @FXML private Button btnResetFilter;

    // Action controls
    @FXML private TextField tfSelectedOrderId;
    @FXML private ComboBox<String> cbNewStatus;
    @FXML private ComboBox<String> cbNewPaymentStatus;
    @FXML private Button btnUpdateOrder;
    @FXML private Button btnViewDetails;
    @FXML private Button btnDeleteOrder;

    // Table and labels
    @FXML private TableView<Order> orderTable;
    @FXML private Label lblOrderCount;
    @FXML private Label lblTotalRevenue;

    // Table columns theo schema mới
    @FXML private TableColumn<Order, String> colOrderId;
    @FXML private TableColumn<Order, String> colDate;
    @FXML private TableColumn<Order, String> colCustomerId;
    @FXML private TableColumn<Order, String> colCustomerName;
    @FXML private TableColumn<Order, String> colEmployeeId;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colPaymentMethod;
    @FXML private TableColumn<Order, String> colPaymentStatus;
    @FXML private TableColumn<Order, String> colTotal;
    @FXML private TableColumn<Order, String> colDeliveryAddress;
    @FXML private TableColumn<Order, String> colNote;
    @Override
    
    public void initialize(URL location, ResourceBundle resources) {
        setupDatePickers();
        setupComboBoxes();
        setupTable();
        setupTableSelection();
        loadOrderData();
    }

    private void setupDatePickers() {
        dpToDate.setValue(LocalDate.now());
        dpFromDate.setValue(LocalDate.now().minusDays(30));
    }

    private void setupComboBoxes() {
        // Order status
        cbStatus.getItems().addAll(
            "Tất cả",
            "Chờ xác nhận",
            "Đang chuẩn bị",
            "Đang giao hàng",
            "Đã hoàn thành",
            "Đã hủy"
        );
        cbStatus.setValue("Tất cả");

        // Payment status
        cbPaymentStatus.getItems().addAll(
            "Tất cả",
            "Chưa thanh toán",
            "Đã thanh toán",
            "Đã hoàn tiền"
        );
        cbPaymentStatus.setValue("Tất cả");

        // New status for updates
        cbNewStatus.getItems().addAll(
            "Chờ xác nhận",
            "Đang chuẩn bị",
            "Đang giao hàng",
            "Đã hoàn thành",
            "Đã hủy"
        );

        cbNewPaymentStatus.getItems().addAll(
            "Chưa thanh toán",
            "Đã thanh toán",
            "Đã hoàn tiền"
        );
    }

    private void setupTable() {
        // Setup columns theo schema mới
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colPaymentStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("formattedTotal"));
        colDeliveryAddress.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        // Enable editing
        orderTable.setEditable(true);
        
        // Status column editable
        colStatus.setCellFactory(ComboBoxTableCell.forTableColumn("Chờ xác nhận", "Đang chuẩn bị", "Đang giao hàng", "Đã hoàn thành", "Đã hủy"));
        colStatus.setOnEditCommit(event -> {
            Order order = event.getRowValue();
            order.setStatus(event.getNewValue());
            updateOrderStatusInDB(order.getOrderId(), "status", event.getNewValue());
        });

        // Payment status column editable
        colPaymentStatus.setCellFactory(ComboBoxTableCell.forTableColumn("Chưa thanh toán", "Đã thanh toán", "Đã hoàn tiền"));
        colPaymentStatus.setOnEditCommit(event -> {
            Order order = event.getRowValue();
            order.setPaymentStatus(event.getNewValue());
            updateOrderStatusInDB(order.getOrderId(), "payment_status", event.getNewValue());
        });

        // Style cho status columns
        colStatus.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Chờ xác nhận":
                            setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case "Đang chuẩn bị":
                            setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case "Đang giao hàng":
                            setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case "Đã hoàn thành":
                            setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case "Đã hủy":
                            setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        colPaymentStatus.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String paymentStatus, boolean empty) {
                super.updateItem(paymentStatus, empty);
                if (empty || paymentStatus == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(paymentStatus);
                    switch (paymentStatus) {
                        case "Chưa thanh toán":
                            setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case "Đã thanh toán":
                            setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case "Đã hoàn tiền":
                            setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }

    private void setupTableSelection() {
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tfSelectedOrderId.setText(newSelection.getOrderId());
                cbNewStatus.setValue(newSelection.getStatus());
                cbNewPaymentStatus.setValue(newSelection.getPaymentStatus());
                btnUpdateOrder.setDisable(false);
                btnViewDetails.setDisable(false);
                btnDeleteOrder.setDisable(false);
            } else {
                tfSelectedOrderId.clear();
                cbNewStatus.setValue(null);
                cbNewPaymentStatus.setValue(null);
                btnUpdateOrder.setDisable(true);
                btnViewDetails.setDisable(true);
                btnDeleteOrder.setDisable(true);
            }
        });

        // Disable buttons initially
        btnUpdateOrder.setDisable(true);
        btnViewDetails.setDisable(true);
        btnDeleteOrder.setDisable(true);
    }

    @FXML
    private void handleFilter() {
        ObservableList<Order> filteredOrders = getFilteredOrderData();
        orderTable.setItems(filteredOrders);
        updateStatistics(filteredOrders);
    }

    @FXML
    private void handleResetFilter() {
        dpFromDate.setValue(LocalDate.now().minusDays(30));
        dpToDate.setValue(LocalDate.now());
        cbStatus.setValue("Tất cả");
        cbPaymentStatus.setValue("Tất cả");
        tfOrderId.clear();
        loadOrderData();
    }

    @FXML
    private void handleUpdateOrder() {
        String orderId = tfSelectedOrderId.getText().trim();
        String newStatus = cbNewStatus.getValue();
        String newPaymentStatus = cbNewPaymentStatus.getValue();

        if (orderId.isEmpty()) {
            showAlert("Vui lòng chọn đơn hàng!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận cập nhật");
        confirmAlert.setHeaderText("Bạn có chắc muốn cập nhật đơn hàng?");
        confirmAlert.setContentText("Đơn hàng: " + orderId + 
                                   "\nTrạng thái: " + newStatus +
                                   "\nThanh toán: " + newPaymentStatus);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateOrderInDB(orderId, newStatus, newPaymentStatus);
            handleFilter();
            showAlert("Cập nhật đơn hàng thành công!", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleViewDetails() {
        String orderId = tfSelectedOrderId.getText().trim();
        if (orderId.isEmpty()) {
            showAlert("Vui lòng chọn đơn hàng!", Alert.AlertType.WARNING);
            return;
        }

        showOrderDetails(orderId);
    }

    @FXML
    private void handleDeleteOrder() {
        String orderId = tfSelectedOrderId.getText().trim();
        if (orderId.isEmpty()) {
            showAlert("Vui lòng chọn đơn hàng!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa đơn hàng");
        confirmAlert.setHeaderText("Bạn có chắc muốn xóa đơn hàng này?");
        confirmAlert.setContentText("Đơn hàng: " + orderId + "\n⚠️ Hành động này không thể hoàn tác!");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteOrderFromDB(orderId);
        }
    }

    // Database methods theo schema mới
    private void loadOrderData() {
        ObservableList<Order> orders = getAllOrderData();
        orderTable.setItems(orders);
        updateStatistics(orders);
    }

    // Sửa getAllOrderData() - thay đổi ORDER BY
    private ObservableList<Order> getAllOrderData() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        
        // ✅ SỬA - thay o.date thành o.order_date (hoặc tên cột thực tế)
        String sql = "SELECT o.*, " +
                    "COALESCE(c.customer_name, CONCAT('Khách hàng ', o.customer_id)) as customer_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "ORDER BY o.order_date DESC"; // ✅ ĐỔI TỪ o.date THÀNH o.order_date

        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                Order order = new Order(
                    result.getString("order_id"),
                    result.getString("customer_id"),
                    result.getString("employee_id"),
                    // ✅ SỬA - thay "date" thành "order_date" 
                    result.getTimestamp("order_date").toLocalDateTime(),
                    result.getString("status"),
                    result.getString("payment_method"),
                    result.getString("payment_status"),
                    result.getString("note") != null ? result.getString("note") : "",
                    result.getString("delivery_address") != null ? result.getString("delivery_address") : "",
                    result.getDouble("total")
                );
                
                order.setCustomerName(result.getString("customer_name"));
                // ✅ KIỂM TRA column tồn tại trước khi get
                try {
                    order.setBonusUsed(result.getInt("bonus_used"));
                    order.setOriginalTotal(result.getDouble("original_total"));
                } catch (SQLException e) {
                    // Nếu column không tồn tại, set default
                    order.setBonusUsed(0);
                    order.setOriginalTotal(result.getDouble("total"));
                }
                
                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            closeConnection(result, prepare, connect);
        }

        return orders;
    }

    // ✅ SỬA getFilteredOrderData() tương tự
    private ObservableList<Order> getFilteredOrderData() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("SELECT o.*, " +
            "COALESCE(c.customer_name, CONCAT('Khách hàng ', o.customer_id)) as customer_name " +
            "FROM orders o " +
            "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
            "WHERE 1=1");

        boolean hasFromDate = dpFromDate.getValue() != null;
        boolean hasToDate = dpToDate.getValue() != null;
        boolean hasStatus = cbStatus.getValue() != null && !cbStatus.getValue().equals("Tất cả");
        boolean hasPaymentStatus = cbPaymentStatus.getValue() != null && !cbPaymentStatus.getValue().equals("Tất cả");
        boolean hasOrderId = !tfOrderId.getText().trim().isEmpty();

        // ✅ SỬA - thay date thành order_date
        if (hasFromDate) sql.append(" AND DATE(o.order_date) >= ?");
        if (hasToDate) sql.append(" AND DATE(o.order_date) <= ?");
        if (hasStatus) sql.append(" AND o.status = ?");
        if (hasPaymentStatus) sql.append(" AND o.payment_status = ?");
        if (hasOrderId) sql.append(" AND o.order_id LIKE ?");

        // ✅ SỬA ORDER BY
        sql.append(" ORDER BY o.order_date DESC");

        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (hasFromDate) prepare.setDate(paramIndex++, Date.valueOf(dpFromDate.getValue()));
            if (hasToDate) prepare.setDate(paramIndex++, Date.valueOf(dpToDate.getValue()));
            if (hasStatus) prepare.setString(paramIndex++, cbStatus.getValue());
            if (hasPaymentStatus) prepare.setString(paramIndex++, cbPaymentStatus.getValue());
            if (hasOrderId) prepare.setString(paramIndex++, "%" + tfOrderId.getText().trim() + "%");

            result = prepare.executeQuery();

            while (result.next()) {
                Order order = new Order(
                    result.getString("order_id"),
                    result.getString("customer_id"),
                    result.getString("employee_id"),
                    // ✅ SỬA tên cột
                    result.getTimestamp("order_date").toLocalDateTime(),
                    result.getString("status"),
                    result.getString("payment_method"),
                    result.getString("payment_status"),
                    result.getString("note") != null ? result.getString("note") : "",
                    result.getString("delivery_address") != null ? result.getString("delivery_address") : "",
                    result.getDouble("total")
                );
                
                order.setCustomerName(result.getString("customer_name"));
                try {
                    order.setBonusUsed(result.getInt("bonus_used"));
                    order.setOriginalTotal(result.getDouble("original_total"));
                } catch (SQLException e) {
                    order.setBonusUsed(0);
                    order.setOriginalTotal(result.getDouble("total"));
                }
                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi lọc dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            closeConnection(result, prepare, connect);
        }

        return orders;
    }

    private void updateOrderStatusInDB(String orderId, String columnName, String newValue) {
        String sql = "UPDATE orders SET " + columnName + " = ? WHERE order_id = ?";

        Connection connect = null;
        PreparedStatement prepare = null;

        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, newValue);
            prepare.setString(2, orderId);

            prepare.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi cập nhật: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            closeConnection(null, prepare, connect);
        }
    }

    private void updateOrderInDB(String orderId, String newStatus, String newPaymentStatus) {
        String sql = "UPDATE orders SET status = ?, payment_status = ? WHERE order_id = ?";

        Connection connect = null;
        PreparedStatement prepare = null;

        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, newStatus);
            prepare.setString(2, newPaymentStatus);
            prepare.setString(3, orderId);

            prepare.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi cập nhật: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            closeConnection(null, prepare, connect);
        }
    }

    private void deleteOrderFromDB(String orderId) {
        Connection connect = null;
        PreparedStatement prepare = null;

        try {
            connect = database.connectDB();
            
            // Nếu có bảng order_items thì xóa trước
            // prepare = connect.prepareStatement("DELETE FROM order_items WHERE order_id = ?");
            // prepare.setString(1, orderId);
            // prepare.executeUpdate();
            // prepare.close();

            // Xóa order
            prepare = connect.prepareStatement("DELETE FROM orders WHERE order_id = ?");
            prepare.setString(1, orderId);
            int rowsAffected = prepare.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Xóa đơn hàng thành công!", Alert.AlertType.INFORMATION);
                handleFilter();
                orderTable.getSelectionModel().clearSelection();
            } else {
                showAlert("Không tìm thấy đơn hàng để xóa!", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi xóa đơn hàng: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            closeConnection(null, prepare, connect);
        }
    }

    // Sửa method showOrderDetails() - bỏ membership_level
    private void showOrderDetails(String orderId) {
        Alert detailAlert = new Alert(Alert.AlertType.INFORMATION);
        detailAlert.setTitle("Chi tiết đơn hàng");
        detailAlert.setHeaderText("Đơn hàng: " + orderId);

        StringBuilder details = new StringBuilder();
        String sql = "SELECT o.*, c.customer_name, c.customer_phone, c.bonus_point " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "WHERE o.order_id = ?";

        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, orderId);
            result = prepare.executeQuery();

            if (result.next()) {
                // ✅ SỬA tên cột
                details.append("📅 Ngày đặt: ").append(result.getTimestamp("order_date")).append("\n");
                details.append("👤 Khách hàng: ").append(result.getString("customer_name")).append(" (").append(result.getString("customer_id")).append(")\n");
                details.append("📞 Số điện thoại: ").append(result.getString("customer_phone")).append("\n");
                details.append("🎁 Điểm tích lũy hiện tại: ").append(result.getInt("bonus_point")).append(" điểm\n");
                details.append("👨‍💼 Nhân viên: ").append(result.getString("employee_id")).append("\n");
                details.append("🏷️ Trạng thái: ").append(result.getString("status")).append("\n");
                details.append("💳 Thanh toán: ").append(result.getString("payment_method")).append("\n");
                details.append("💰 TT Thanh toán: ").append(result.getString("payment_status")).append("\n");
                details.append("🏠 Địa chỉ giao: ").append(result.getString("delivery_address")).append("\n");
                details.append("📝 Ghi chú: ").append(result.getString("note")).append("\n\n");
                
                details.append("💵 THÔNG TIN THANH TOÁN:\n");
                details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                
                // ✅ KIỂM TRA column tồn tại
                try {
                    details.append("💰 Tổng tiền gốc: ").append(String.format("%,.0f VND", result.getDouble("original_total"))).append("\n");
                    details.append("🎁 Điểm đã sử dụng: ").append(result.getInt("bonus_used")).append(" điểm\n");
                    details.append("💸 Giảm giá: -").append(String.format("%,.0f VND", result.getInt("bonus_used") * 10.0)).append("\n");
                } catch (SQLException e) {
                    details.append("💰 Tổng tiền gốc: ").append(String.format("%,.0f VND", result.getDouble("total"))).append("\n");
                    details.append("🎁 Điểm đã sử dụng: 0 điểm\n");
                    details.append("💸 Giảm giá: -0 VND\n");
                }
                
                details.append("✅ Thành tiền: ").append(String.format("%,.0f VND", result.getDouble("total"))).append("\n\n");
                details.append("ℹ️ Quy đổi: 20 điểm = 200 VND (1 điểm = 10 VND)\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            details.append("Lỗi tải chi tiết: ").append(e.getMessage());
        } finally {
            closeConnection(result, prepare, connect);
        }

        detailAlert.setContentText(details.toString());
        detailAlert.showAndWait();
    }

    private void updateStatistics(ObservableList<Order> orders) {
        int orderCount = orders.size();
        double totalRevenue = orders.stream()
            .filter(order -> !"Đã hủy".equals(order.getStatus()))
            .mapToDouble(Order::getTotal)
            .sum();

        lblOrderCount.setText("Tổng số đơn hàng: " + orderCount);
        lblTotalRevenue.setText("Doanh thu: " + String.format("%,.0f VND", totalRevenue));
    }

    private void closeConnection(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
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

