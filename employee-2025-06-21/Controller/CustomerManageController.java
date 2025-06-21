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
import obj.Products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DATABASE.DatabaseConnection;

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
		orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderTime"));
		orderTotalColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%,.0f VND", cellData.getValue().getTotalAmount())));

		// Cột cho Order Detail Table
		productNameColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
				String.format("%,.0f VND", cellData.getValue().getPrice())));
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
			if (selectedOrder != null) {
				loadOrderDetails(selectedOrder.getOrderId());
			}
		});
	}

	private void loadCustomerOrders(String customerId) {
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
			}

			orderTableView.setItems(customerOrders);
		} catch (SQLException e) {
			System.err.println("Lỗi khi tải đơn hàng:");
			e.printStackTrace();
		}
	}

	private void loadOrderDetails(int orderId) {
		orderDetails.clear();
		double calculatedTotal = 0; // Biến để tính tổng từ CartItem

		String query = "SELECT p.product_name, p.price, ol.quantity FROM Orderlines ol JOIN Products p ON ol.product_id = p.product_id WHERE ol.order_id = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, orderId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Products product = new Products(rs.getString("product_name"), rs.getFloat("price"), "", "", 0);
				CartItem item = new CartItem(product, rs.getInt("quantity"));
				orderDetails.add(item);
				calculatedTotal += item.getPrice() * item.getQuantity();
			}

			orderDetailTableView.setItems(orderDetails);
			System.out.printf("Tổng từ CartItem: %,.0f VND | Tổng từ Orders: %,.0f VND%n", calculatedTotal,
					orderTableView.getSelectionModel().getSelectedItem().getTotalAmount());
		} catch (SQLException e) {
			System.err.println("Lỗi khi tải chi tiết đơn hàng:");
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