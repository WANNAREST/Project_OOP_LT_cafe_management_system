package Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import obj.CartItem;
import obj.Customer;
import obj.Order;
import obj.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import Controller.DatabaseConnection;

public class CustomerManageController {

	// Các thành phần cho Customer Table
	@FXML
	private TableView<Customer> customerTableView;
	@FXML
	private TableColumn<Customer, String> customerIdColumn;
	@FXML
	private TableColumn<Customer, String> customerNameColumn;
	@FXML
	private TableColumn<Customer, String> customerPhoneColumn;
	@FXML
	private TableColumn<Customer, String> bonusPointColumn;

	// Các thành phần cho Order Table
	@FXML
	private TableView<Order> orderTableView;
	@FXML
	private TableColumn<Order, String> orderIdColumn;
	@FXML
	private TableColumn<Order, String> orderDateColumn;
	@FXML
	private TableColumn<Order, String> orderTotalColumn;

	// Các thành phần cho Order Detail Table (sử dụng CartItem)
	@FXML
	private TableView<CartItem> orderDetailTableView;
	@FXML
	private TableColumn<CartItem, String> productNameColumn;
	@FXML
	private TableColumn<CartItem, Integer> quantityColumn;
	@FXML
	private TableColumn<CartItem, String> priceColumn;

	@FXML
	private TextField searchField;

	private ObservableList<Customer> masterCustomerList = FXCollections.observableArrayList();
	private FilteredList<Customer> filteredCustomers;
	private ObservableList<Order> customerOrders = FXCollections.observableArrayList();
	private ObservableList<CartItem> orderDetails = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		// 1. Thiết lập các cột TableView
		setupTableColumns();

		// 2. Khởi tạo FilteredList
		filteredCustomers = new FilteredList<>(masterCustomerList, p -> true);
		customerTableView.setItems(filteredCustomers);

		// 3. Thiết lập sự kiện chọn khách hàng
		setupCustomerSelectionListener();

		// 4. Thiết lập sự kiện chọn đơn hàng
		setupOrderSelectionListener();

		// 5. Thiết lập bộ lọc tìm kiếm
		setupSearchFilter();

		// 6. Tải dữ liệu khách hàng
		loadCustomers();
	}

	private void setupTableColumns() {
		// Cột cho Customer Table
		customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		bonusPointColumn.setCellValueFactory(cellData -> 
	    	new SimpleStringProperty(String.valueOf(cellData.getValue().getBonusPoint())));

		// Cột cho Order Table
		orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
		orderDateColumn.setCellValueFactory(cellData -> {
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		    String formattedDate = cellData.getValue().getOrderTime().format(formatter);
		    return new SimpleStringProperty(formattedDate);
		});
		orderTotalColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%,.0f VND", cellData.getValue().getTotalAmount())));

		// Cột cho Order Detail Table with debugging
		productNameColumn.setCellValueFactory(cellData -> {
		    try {
		        String productName = cellData.getValue().getProduct().getName();
		        System.out.println("🔍 CUSTOMER CELL VALUE: Product name: " + productName);
		        return new SimpleStringProperty(productName);
		    } catch (Exception e) {
		        System.err.println("❌ CUSTOMER CELL VALUE ERROR: Product name: " + e.getMessage());
		        return new SimpleStringProperty("Error");
		    }
		});
		
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		
		priceColumn.setCellValueFactory(cellData -> {
		    try {
		        int price = cellData.getValue().getPrice();
		        String formattedPrice = String.format("%,.0f VND", (double)price);
		        System.out.println("🔍 CUSTOMER CELL VALUE: Price: " + price + " -> " + formattedPrice);
		        return new SimpleStringProperty(formattedPrice);
		    } catch (Exception e) {
		        System.err.println("❌ CUSTOMER CELL VALUE ERROR: Price: " + e.getMessage());
		        return new SimpleStringProperty("Error");
		    }
		});
	}

	private void setupCustomerSelectionListener() {
		customerTableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, selectedCustomer) -> {
					if (selectedCustomer != null) {
						loadCustomerOrders(selectedCustomer.getCustomerId());
					}
				});
	}

	private void setupOrderSelectionListener() {
		orderTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedOrder) -> {
			System.out.println("🖱️ CUSTOMER MANAGE: Order selection changed");
			System.out.println("🖱️ CUSTOMER MANAGE: Old selection: " + (oldValue != null ? oldValue.getOrderId() : "null"));
			System.out.println("🖱️ CUSTOMER MANAGE: New selection: " + (selectedOrder != null ? selectedOrder.getOrderId() : "null"));
			
			if (selectedOrder != null) {
				loadOrderDetails(selectedOrder.getOrderId());
			} else {
				System.out.println("🖱️ CUSTOMER MANAGE: No order selected, clearing order details");
				orderDetails.clear();
				orderDetailTableView.setItems(orderDetails);
			}
		});
	}

	private void loadCustomerOrders(String customerId) {
		System.out.println("🔍 CUSTOMER MANAGE: Loading orders for customer ID: " + customerId);
		customerOrders.clear();
		String query = "SELECT order_id, date, total FROM Orders WHERE customer_id = ? ORDER BY date DESC";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, customerId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Order order = new Order(); // Giả sử có constructor mặc định
				order.setOrderId(rs.getInt("order_id"));
				order.setOrderTime(rs.getTimestamp("date").toLocalDateTime());
				order.setTotalAmount(rs.getDouble("total")); // Gán total từ database
				customerOrders.add(order);
				
				System.out.println("🔍 CUSTOMER MANAGE: Added order: ID=" + rs.getInt("order_id") + 
				                 ", Date=" + rs.getTimestamp("date") + ", Total=" + rs.getDouble("total"));
			}

			System.out.println("🔍 CUSTOMER MANAGE: Total orders loaded for customer: " + customerOrders.size());
			orderTableView.setItems(customerOrders);
			
			// Clear order details when customer changes
			orderDetails.clear();
			orderDetailTableView.setItems(orderDetails);
			
		} catch (SQLException e) {
			System.err.println("❌ CUSTOMER MANAGE: Lỗi khi tải đơn hàng:");
			e.printStackTrace();
		}
	}

	private void loadOrderDetails(int orderId) {
		System.out.println("🔍 CUSTOMER MANAGE: Loading order details for order ID: " + orderId);
		orderDetails.clear();
		double calculatedTotal = 0; // Biến để tính tổng từ CartItem

		// Use historical price from orderlines, not current product price
		String query = "SELECT p.product_name, p.product_id, ol.price, ol.quantity " +
		              "FROM Orderlines ol " +
		              "JOIN Products p ON ol.product_id = p.product_id " +
		              "WHERE ol.order_id = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, orderId);
			ResultSet rs = stmt.executeQuery();

			System.out.println("🔍 CUSTOMER MANAGE: Executing query: " + query);

			while (rs.next()) {
				// Create product with historical price from orderlines
				Product product = new Product();
				product.setId(rs.getInt("product_id"));
				product.setName(rs.getString("product_name"));
				product.setPrice(rs.getInt("price")); // This is ol.price (historical price)
				
				CartItem item = new CartItem(product, rs.getInt("quantity"));
				orderDetails.add(item);
				calculatedTotal += item.getPrice() * item.getQuantity();
				
				System.out.println("🔍 CUSTOMER MANAGE: Added item: " + rs.getString("product_name") + 
				                 " x" + rs.getInt("quantity") + " @ " + rs.getInt("price") + " VND");
			}

			System.out.println("🔍 CUSTOMER MANAGE: Total order details loaded: " + orderDetails.size());
			System.out.println("🔍 CUSTOMER MANAGE: Setting items to orderDetailTableView...");
			System.out.println("🔍 CUSTOMER MANAGE: orderDetails observable list size before: " + orderDetails.size());
			
			orderDetailTableView.setItems(orderDetails);
			System.out.println("🔍 CUSTOMER MANAGE: Set items to table, table items count: " + orderDetailTableView.getItems().size());
			
			// Force table refresh
			orderDetailTableView.refresh();
			System.out.println("🔍 CUSTOMER MANAGE: Table refreshed");
			
			// Test cell value factories
			if (!orderDetails.isEmpty()) {
			    CartItem firstItem = orderDetails.get(0);
			    System.out.println("🔍 CUSTOMER MANAGE: Testing first item values:");
			    System.out.println("  - Product name: " + firstItem.getProduct().getName());
			    System.out.println("  - Quantity: " + firstItem.getQuantity());
			    System.out.println("  - Price: " + firstItem.getPrice());
			}
			System.out.printf("🔍 CUSTOMER MANAGE: Tổng từ CartItem: %,.0f VND | Tổng từ Orders: %,.0f VND%n", calculatedTotal,
					orderTableView.getSelectionModel().getSelectedItem().getTotalAmount());
		} catch (SQLException e) {
			System.err.println("❌ CUSTOMER MANAGE: Lỗi khi tải chi tiết đơn hàng:");
			e.printStackTrace();
		}
	}

	private void setupSearchFilter() {
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredCustomers.setPredicate(customer -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase().trim();
				return customer.getName().toLowerCase().contains(lowerCaseFilter);
			});
		});
	}

	private void loadCustomers() {
	    masterCustomerList.clear();

	    String query = "SELECT u.user_id, u.full_name, u.phone, c.bonus_point "
	            + "FROM Users u JOIN Customers c ON u.user_id = c.customer_id";

	    try (Connection conn = DatabaseConnection.getConnection();
	            PreparedStatement stmt = conn.prepareStatement(query);
	            ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            String id = String.valueOf(rs.getInt("user_id"));
	            String name = rs.getString("full_name");
	            String phone = rs.getString("phone");
	            int bonusPoint = rs.getInt("bonus_point");

	            Customer customer = new Customer(name, phone, "");
	            customer.setCustomerId(id);
	            customer.setBonusPoint(bonusPoint); // Thêm điểm thưởng
	            masterCustomerList.add(customer);
	        }

	    } catch (SQLException e) {
	        System.err.println("Lỗi khi tải danh sách khách hàng:");
	        e.printStackTrace();
	    }
	}
}