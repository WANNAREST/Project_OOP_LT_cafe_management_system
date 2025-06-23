package Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import obj.CartItem;
import obj.Order;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderManagerController {
	@FXML
	private TableView<Order> orderTable;
	@FXML
	private TableColumn<Order, Integer> orderIdColumn;
	@FXML
	private TableColumn<Order, String> customerNameColumn;
	@FXML
	private TableColumn<Order, String> phoneColumn;
	@FXML
	private TableColumn<Order, String> orderTimeColumn;
	@FXML
	private TableColumn<Order, String> statusColumn;

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
	
	private int currentEmployeeId;

	private ObservableList<Order> pendingOrders = FXCollections.observableArrayList();
	private ObservableList<CartItem> orderItems = FXCollections.observableArrayList();

	public void setCurrentEmployeeId(int employeeId) {
	    this.currentEmployeeId = employeeId;
	}
	
	@FXML
	public void initialize() {
		// Thiết lập cột cho bảng đơn hàng
		orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
		customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		orderTimeColumn.setCellValueFactory(cellData -> {
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		    String formattedDate = cellData.getValue().getOrderTime().format(formatter);
		    return new SimpleStringProperty(formattedDate);
		});
		statusColumn.setCellValueFactory(cellData -> {
		    Order.OrderStatus status = cellData.getValue().getStatus();
		    return new SimpleStringProperty(status.getDescription());
		});

		// Thiết lập cột cho bảng sản phẩm
		productNameColumn.setCellValueFactory(cellData -> {
		    try {
		        String productName = cellData.getValue().getProduct().getName();
		        System.out.println(" CELL VALUE: Product name: " + productName);
		        return new SimpleStringProperty(productName);
		    } catch (Exception e) {
		        System.err.println(" CELL VALUE ERROR: Product name: " + e.getMessage());
		        return new SimpleStringProperty("Error");
		    }
		});
		
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		
		priceColumn.setCellValueFactory(cellData -> {
		    try {
		        int price = cellData.getValue().getPrice();
		        String formattedPrice = String.format("%,.0f VND", (double)price);
		        System.out.println(" CELL VALUE: Price: " + price + " -> " + formattedPrice);
		        return new SimpleStringProperty(formattedPrice);
		    } catch (Exception e) {
		        System.err.println(" CELL VALUE ERROR: Price: " + e.getMessage());
		        return new SimpleStringProperty("Error");
		    }
		});

		// Load dữ liệu
		loadAllOrders();

		// Xử lý sự kiện chọn đơn hàng
		orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			System.out.println("🖱 SELECTION: Order selection changed");
			System.out.println("🖱 SELECTION: Old selection: " + (oldSelection != null ? oldSelection.getOrderId() : "null"));
			System.out.println("🖱 SELECTION: New selection: " + (newSelection != null ? newSelection.getOrderId() : "null"));
			
			if (newSelection != null) {
				showOrderDetails(newSelection);
			} else {
				System.out.println("🖱 SELECTION: No order selected, clearing details");
				clearFields();
			}
		});
	}

	private void loadAllOrders() {
	    System.out.println("📋 LOAD ORDERS: Loading all orders from database...");
	    List<Order> allOrders = new ArrayList<>();
	    
	    // Test database connection first
	    try {
	        java.sql.Connection conn = DatabaseConnection.getConnection();
	        System.out.println("📋 LOAD ORDERS: Database connection successful");
	        
	        // Check if Orders table has data
	        java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as order_count FROM Orders");
	        java.sql.ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            int orderCount = rs.getInt("order_count");
	            System.out.println("📋 LOAD ORDERS: Total orders in database: " + orderCount);
	        }
	        rs.close();
	        stmt.close();
	        
	        // Check if Orderlines table has data
	        stmt = conn.prepareStatement("SELECT COUNT(*) as orderline_count FROM Orderlines");
	        rs = stmt.executeQuery();
	        if (rs.next()) {
	            int orderlineCount = rs.getInt("orderline_count");
	            System.out.println("📋 LOAD ORDERS: Total orderlines in database: " + orderlineCount);
	        }
	        rs.close();
	        stmt.close();
	        
	    } catch (Exception e) {
	        System.err.println("📋 LOAD ORDERS: Database connection failed: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    List<Order> pendingOrdersList = Order.getOnlineOrdersByStatus("pending");
	    List<Order> confirmedOrdersList = Order.getOnlineOrdersByStatus("confirmed");
	    List<Order> cancelledOrdersList = Order.getOnlineOrdersByStatus("cancelled");
	    List<Order> offlineOrdersList = Order.getOnlineOrdersByStatus("offline");
	    
	    System.out.println("📋 LOAD ORDERS: Pending orders: " + pendingOrdersList.size());
	    System.out.println("📋 LOAD ORDERS: Confirmed orders: " + confirmedOrdersList.size());
	    System.out.println("📋 LOAD ORDERS: Cancelled orders: " + cancelledOrdersList.size());
	    System.out.println("📋 LOAD ORDERS: Offline orders: " + offlineOrdersList.size());
	    
	    allOrders.addAll(pendingOrdersList);
	    allOrders.addAll(confirmedOrdersList);
	    allOrders.addAll(cancelledOrdersList);
	    allOrders.addAll(offlineOrdersList);
	    
	    System.out.println("📋 LOAD ORDERS: Total orders loaded: " + allOrders.size());
	    
	    for (Order order : allOrders) {
	        System.out.println("📋 LOAD ORDERS: Order ID " + order.getOrderId() + 
	                         ", Customer: " + order.getCustomerName() + 
	                         ", Status: " + order.getStatus() +
	                         ", Items: " + (order.getOrderItems() != null ? order.getOrderItems().size() : 0));
	    }
	    
	    pendingOrders.setAll(allOrders);
	    orderTable.setItems(pendingOrders);
	}

	private void showOrderDetails(Order order) {
	    System.out.println(" ORDER DETAILS: Showing details for order ID: " + order.getOrderId());
	    System.out.println(" ORDER DETAILS: Customer: " + order.getCustomerName());
	    System.out.println(" ORDER DETAILS: Phone: " + order.getPhoneNumber());
	    
	    customerNameField.setText(order.getCustomerName() != null ? order.getCustomerName() : "N/A");
	    phoneField.setText(order.getPhoneNumber() != null ? order.getPhoneNumber() : "N/A");
	    
	    // Load chi tiết sản phẩm trước khi tính tổng
	    List<CartItem> items = order.getOrderItems();
	    System.out.println(" ORDER DETAILS: Order items count: " + (items != null ? items.size() : 0));
	    
	    if (items != null && !items.isEmpty()) {
	        for (int i = 0; i < items.size(); i++) {
	            CartItem item = items.get(i);
	            System.out.println(" ORDER DETAILS: Item " + (i+1) + ": " + 
	                             item.getProduct().getName() + " x" + item.getQuantity() + 
	                             " @ " + item.getPrice() + " VND");
	        }
	        
	        System.out.println(" UI UPDATE: Setting items to table...");
	        System.out.println(" UI UPDATE: orderItems observable list size before: " + orderItems.size());
	        
	        orderItems.setAll(items);
	        System.out.println(" UI UPDATE: orderItems observable list size after setAll: " + orderItems.size());
	        
	        itemsTable.setItems(orderItems);
	        System.out.println(" UI UPDATE: Set items to table, table items count: " + itemsTable.getItems().size());
	        
	        // Force table refresh
	        itemsTable.refresh();
	        System.out.println(" UI UPDATE: Table refreshed");
	        
	        // Test cell value factories
	        if (!orderItems.isEmpty()) {
	            CartItem firstItem = orderItems.get(0);
	            System.out.println(" UI UPDATE: Testing first item values:");
	            System.out.println("  - Product name: " + firstItem.getProduct().getName());
	            System.out.println("  - Quantity: " + firstItem.getQuantity());
	            System.out.println("  - Price: " + firstItem.getPrice());
	        }
	        
	        // Tính tổng sau khi có items
	        double total = items.stream().mapToDouble(item -> 
	            item.getPrice() * item.getQuantity()).sum();
	        totalField.setText(String.format("%,.0f VND", total));
	        System.out.println(" ORDER DETAILS: Total calculated: " + total + " VND");
	    } else {
	        System.out.println(" ORDER DETAILS: No items found for this order");
	        orderItems.clear();
	        itemsTable.setItems(orderItems);
	        totalField.setText("0 VND");
	    }
	}

	@FXML
	private void handleConfirmPayment() {
	    Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
	    if (selectedOrder == null) {
	        showAlert("Cảnh báo", "Không có đơn hàng được chọn", 
	                 "Vui lòng chọn một đơn hàng để xác nhận", Alert.AlertType.WARNING);
	        return;
	    }
	    
	    // Kiểm tra nếu đơn hàng không phải pending thì không cho xác nhận
	    if (!Order.OrderStatus.PENDING.equals(selectedOrder.getStatus())) {
	        showAlert("Cảnh báo", "Không thể xác nhận", 
	                 "Chỉ có thể xác nhận đơn hàng đang ở trạng thái chờ", Alert.AlertType.WARNING);
	        return;
	    }
	    
	 // Lấy employeeId từ session hiện tại (ví dụ: 3 là nhân viên đang đăng nhập)
	    //int currentEmployeeId = 3; // Thay bằng ID nhân viên thực tế
	    
	    boolean success = Order.confirmOnlineOrder(selectedOrder.getOrderId(), currentEmployeeId);
	    if (success) {
	        showAlert("Thành công", "Xác nhận đơn hàng", 
	                 "Đơn hàng đã được xác nhận thành công", Alert.AlertType.INFORMATION);
	        selectedOrder.setStatus(Order.OrderStatus.CONFIRMED);
	        orderTable.refresh();
	    } else {
	        showAlert("Lỗi", "Xác nhận đơn hàng", 
	                 "Không thể xác nhận đơn hàng này", Alert.AlertType.ERROR);
	    }
	}

	@FXML
	private void handleCancelOrder() {
	    Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
	    if (selectedOrder == null) {
	        showAlert("Cảnh báo", "Không có đơn hàng được chọn", 
	                 "Vui lòng chọn một đơn hàng để hủy", Alert.AlertType.WARNING);
	        return;
	    }
	    
	    // Kiểm tra nếu đơn hàng không phải pending thì không cho hủy
	    if (!Order.OrderStatus.PENDING.equals(selectedOrder.getStatus())) {
	        showAlert("Cảnh báo", "Không thể hủy", 
	                 "Chỉ có thể hủy đơn hàng đang ở trạng thái chờ", Alert.AlertType.WARNING);
	        return;
	    }
	    
	    //int currentEmployeeId = 3; // Thay bằng ID nhân viên thực tế
	    
	    TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("Hủy đơn hàng");
	    dialog.setHeaderText("Nhập lý do hủy đơn hàng #" + selectedOrder.getOrderId());
	    dialog.setContentText("Lý do:");
	    
	    dialog.showAndWait().ifPresent(reason -> {
	        boolean success = Order.cancelOnlineOrder(selectedOrder.getOrderId(), reason, currentEmployeeId);
	        if (success) {
	            showAlert("Thành công", "Hủy đơn hàng", 
	                     "Đơn hàng đã được hủy thành công", Alert.AlertType.INFORMATION);
	            // Cập nhật trạng thái ngay trên giao diện
	            selectedOrder.setStatus(Order.OrderStatus.CANCELLED);
	            orderTable.refresh();
	        } else {
	            showAlert("Lỗi", "Hủy đơn hàng", 
	                     "Không thể hủy đơn hàng này", Alert.AlertType.ERROR);
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