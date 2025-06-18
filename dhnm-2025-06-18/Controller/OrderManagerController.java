package Controller;

import Cart_CoffeeShop.CartItem;
import Cart_CoffeeShop.Order_CoffeeShop;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderManagerController {
	@FXML
	private TableView<Order_CoffeeShop> orderTable;
	@FXML
	private TableColumn<Order_CoffeeShop, Integer> orderIdColumn;
	@FXML
	private TableColumn<Order_CoffeeShop, String> customerNameColumn;
	@FXML
	private TableColumn<Order_CoffeeShop, String> phoneColumn;
	@FXML
	private TableColumn<Order_CoffeeShop, String> orderTimeColumn;
	@FXML
	private TableColumn<Order_CoffeeShop, String> statusColumn;

	@FXML
	private TableView<CartItem> itemsTable;
	@FXML
	private TableColumn<CartItem, String> productNameColumn;
	@FXML
	private TableColumn<CartItem, Integer> quantityColumn;
	@FXML
	private TableColumn<CartItem, String> priceColumn;

	@FXML
	private TextField customerNameField;
	@FXML
	private TextField phoneField;
	@FXML
	private TextField totalField;

	private ObservableList<Order_CoffeeShop> pendingOrders = FXCollections.observableArrayList();
	private ObservableList<CartItem> orderItems = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		// Thiết lập cột cho bảng đơn hàng
		orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
		customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		orderTimeColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getOrderTime().toString()));
		statusColumn.setCellValueFactory(cellData -> {
		    String status = cellData.getValue().getStatus();
		    String vietnameseStatus = "";
		    switch(status) {
		        case "pending":
		            vietnameseStatus = "Đang chờ";
		            break;
		        case "confirmed":
		            vietnameseStatus = "Xác nhận";
		            break;
		        case "cancelled":
		            vietnameseStatus = "Hủy";
		            break;
		        default:
		            vietnameseStatus = status;
		    }
		    return new SimpleStringProperty(vietnameseStatus);
		});

		// Thiết lập cột cho bảng sản phẩm
		productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%,.0f VND", cellData.getValue().getPrice())));

		// Load dữ liệu
		loadAllOrders();

		// Xử lý sự kiện chọn đơn hàng
		orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				showOrderDetails(newSelection);
			}
		});
	}

	private void loadAllOrders() {
	    try {
	        List<Order_CoffeeShop> allOrders = new ArrayList<>();
	        allOrders.addAll(Order_CoffeeShop.getOnlineOrdersByStatus("pending"));
	        allOrders.addAll(Order_CoffeeShop.getOnlineOrdersByStatus("confirmed"));
	        allOrders.addAll(Order_CoffeeShop.getOnlineOrdersByStatus("cancelled"));
	        
	        pendingOrders.setAll(allOrders);
	        orderTable.setItems(pendingOrders);
	    } catch (SQLException e) {
	        showAlert("Lỗi", "Không thể tải danh sách đơn hàng", e.getMessage(), Alert.AlertType.ERROR);
	    }
	}

	private void showOrderDetails(Order_CoffeeShop order) {
	    try {
	        customerNameField.setText(order.getCustomerName());
	        phoneField.setText(order.getPhoneNumber());
	        
	        // Load chi tiết sản phẩm trước khi tính tổng
	        List<CartItem> items = order.getOrderItems();
	        orderItems.setAll(items);
	        itemsTable.setItems(orderItems);
	        
	        // Tính tổng sau khi có items
	        double total = items.stream().mapToDouble(item -> 
	            item.getPrice() * item.getQuantity()).sum();
	        totalField.setText(String.format("%,.0f VND", total));
	    } catch (SQLException e) {
	        showAlert("Lỗi", "Không thể tải chi tiết đơn hàng", e.getMessage(), Alert.AlertType.ERROR);
	    }
	}

	@FXML
	private void handleConfirmPayment() {
	    Order_CoffeeShop selectedOrder = orderTable.getSelectionModel().getSelectedItem();
	    if (selectedOrder == null) {
	        showAlert("Cảnh báo", "Không có đơn hàng được chọn", 
	                 "Vui lòng chọn một đơn hàng để xác nhận", Alert.AlertType.WARNING);
	        return;
	    }
	    
	    // Kiểm tra nếu đơn hàng không phải pending thì không cho xác nhận
	    if (!"pending".equals(selectedOrder.getStatus())) {
	        showAlert("Cảnh báo", "Không thể xác nhận", 
	                 "Chỉ có thể xác nhận đơn hàng đang ở trạng thái chờ", Alert.AlertType.WARNING);
	        return;
	    }
	    
	 // Lấy employeeId từ session hiện tại (ví dụ: 3 là nhân viên đang đăng nhập)
	    int currentEmployeeId = 3; // Thay bằng ID nhân viên thực tế
	    
	    try {
	    	boolean success = Order_CoffeeShop.confirmOnlineOrder(selectedOrder.getOrderId(), currentEmployeeId);
	        if (success) {
	            showAlert("Thành công", "Xác nhận đơn hàng", 
	                     "Đơn hàng đã được xác nhận thành công", Alert.AlertType.INFORMATION);
	            selectedOrder.setStatus("confirmed");
	            orderTable.refresh();
	        } else {
	            showAlert("Lỗi", "Xác nhận đơn hàng", 
	                     "Không thể xác nhận đơn hàng này", Alert.AlertType.ERROR);
	        }
	    } catch (SQLException e) {
	        showAlert("Lỗi", "Xác nhận đơn hàng", 
	                 "Lỗi khi xác nhận đơn hàng: " + e.getMessage(), Alert.AlertType.ERROR);
	    }
	}

	@FXML
	private void handleCancelOrder() {
	    Order_CoffeeShop selectedOrder = orderTable.getSelectionModel().getSelectedItem();
	    if (selectedOrder == null) {
	        showAlert("Cảnh báo", "Không có đơn hàng được chọn", 
	                 "Vui lòng chọn một đơn hàng để hủy", Alert.AlertType.WARNING);
	        return;
	    }
	    
	    // Kiểm tra nếu đơn hàng không phải pending thì không cho hủy
	    if (!"pending".equals(selectedOrder.getStatus())) {
	        showAlert("Cảnh báo", "Không thể hủy", 
	                 "Chỉ có thể hủy đơn hàng đang ở trạng thái chờ", Alert.AlertType.WARNING);
	        return;
	    }
	    
	    int currentEmployeeId = 3; // Thay bằng ID nhân viên thực tế
	    
	    TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("Hủy đơn hàng");
	    dialog.setHeaderText("Nhập lý do hủy đơn hàng #" + selectedOrder.getOrderId());
	    dialog.setContentText("Lý do:");
	    
	    dialog.showAndWait().ifPresent(reason -> {
	        try {
	            boolean success = Order_CoffeeShop.cancelOnlineOrder(selectedOrder.getOrderId(), reason, currentEmployeeId);
	            if (success) {
	                showAlert("Thành công", "Hủy đơn hàng", 
	                         "Đơn hàng đã được hủy thành công", Alert.AlertType.INFORMATION);
	                // Cập nhật trạng thái ngay trên giao diện
	                selectedOrder.setStatus("cancelled");
	                orderTable.refresh();
	            } else {
	                showAlert("Lỗi", "Hủy đơn hàng", 
	                         "Không thể hủy đơn hàng này", Alert.AlertType.ERROR);
	            }
	        } catch (SQLException e) {
	            showAlert("Lỗi", "Hủy đơn hàng", 
	                     "Lỗi khi hủy đơn hàng: " + e.getMessage(), Alert.AlertType.ERROR);
	        }
	    });
	}

	private void clearFields() {
		customerNameField.clear();
		phoneField.clear();
		totalField.clear();
		orderItems.clear();
	}

	private void showAlert(String title, String header, String content, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}