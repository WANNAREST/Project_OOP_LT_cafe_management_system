package Controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import obj.Cart;
import obj.CartItem;
import obj.Order;
import obj.Products;
import DATABASE.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.LimitExceededException;

public class CartController {

	private Products product;
	private Cart cart;

	private StoreController parentController;
	private Label numCoinsLabel;

	@FXML
	private Button btnMinus;

	@FXML
	private Button btnPlus;

	@FXML
	private Button btnRemove;

	@FXML
	private TableColumn<CartItem, String> colProductCategory;

	@FXML
	private TableColumn<CartItem, Integer> colProductID;

	@FXML
	private TableColumn<CartItem, String> colProductName;

	@FXML
	private TableColumn<CartItem, Double> colProductPrice;

	@FXML
	private TableColumn<CartItem, Integer> colProductQuantity;

	@FXML
	private Label discountLabel;

	@FXML
	private Label numLabel;

	@FXML
	private Label subtotalLabel;

	@FXML
	private TableView<CartItem> tblProduct;

	@FXML
	private Label totalLabel;

	@FXML
	private ToggleButton useCointbtnToggle;

	@FXML
	private TextField customerNameField;

	@FXML
	private TextField phoneNumberField;
	
	@FXML
	private TextField bonusPointsField;

	public CartController(Cart cart) {
		this.cart = cart;
		if (cart == null) {
			System.err.println("Error: Cart is null. Make sure to set the cart before initializing the view.");
			return;
		}
		if (tblProduct == null) {
			tblProduct = new TableView<>();
			System.out.println("Created new TableView as it was null");
		}

	}

	public void setParentController(StoreController parentController) {
		this.parentController = parentController;
	}

	public void setCoinLabel(Label label) {
		this.numCoinsLabel = label;
	}

	@FXML
	void continueShoppingBtnPressed(ActionEvent event) {
		if (parentController != null) {
			// Simply call the showMenu method on the parent controller
			parentController.showMenu(event);
		} else {
			System.err.println("Error: Parent controller is not set.");
		}
	}

	@FXML
	void removeBtnPressed(ActionEvent event) throws LimitExceededException {
		CartItem item = tblProduct.getSelectionModel().getSelectedItem();
		if (item != null) {
			cart.removeProduct(item.getProduct());
		}
		updateSubTotal();
		updatetotal();

	}

	private int precoint = 0;

	@FXML
	void useCoinbtnPressed(ActionEvent event) {
		int coins = Integer.parseInt(numCoinsLabel.getText());
		if (useCointbtnToggle.isSelected()) {
			if (coins > 0) {
				precoint = coins;
				discountLabel.setText(String.valueOf(coins * 2000));
				numCoinsLabel.setText(String.valueOf(0));
				updatetotal();
			} else {
				useCointbtnToggle.setSelected(false);
				showAlert("No Coins", "You don't have any coins to use!");
			}
		} else {
			discountLabel.setText(String.valueOf(0));
			numCoinsLabel.setText(String.valueOf(precoint));
			updatetotal();
		}

	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	void minusBtnPressed(ActionEvent event) {
		try {
			CartItem item = tblProduct.getSelectionModel().getSelectedItem();
			if (item != null) {
				int newQuantity = item.getQuantity() - 1;
				if (newQuantity > 0) {
					cart.updateProductQuantity(item.getProduct(), newQuantity);
					// Cập nhật lại TableView
					numLabel.setText(Integer.toString(newQuantity));
					tblProduct.refresh();
					updateSubTotal();
					updatetotal();
				} else {
					cart.removeProduct(item.getProduct());
					tblProduct.refresh();
					updateSubTotal();
					updatetotal();
				}
			}
		} catch (LimitExceededException e) {
			showAlert("Error", e.getMessage());
		}
	}

	@FXML
	void plusBtnPressed(ActionEvent event) {
		try {
			CartItem item = tblProduct.getSelectionModel().getSelectedItem();
			if (item != null) {
				int newQuantity = item.getQuantity() + 1;
				cart.updateProductQuantity(item.getProduct(), newQuantity);
				// Cập nhật lại TableView
				numLabel.setText(Integer.toString(newQuantity));
				tblProduct.refresh();
				updateSubTotal();
				updatetotal();
			}
		} catch (LimitExceededException e) {
			showAlert("Error", e.getMessage());
		}
	}

	public void updateSubTotal() {
	    int subtotal = 0;
	    for (CartItem item : cart.getItemsOrdered()) {
	        subtotal += item.getTotalPrice();
	    }
	    subtotalLabel.setText(String.format("%,d VND", subtotal));
	}

	public void updatetotal() {
	    try {
	        // Xử lý chuỗi tiền tệ (loại bỏ " VND" và dấu ",")
	        int subtotal = Integer.parseInt(subtotalLabel.getText().replaceAll("[^0-9]", ""));
	        int discount = Integer.parseInt(discountLabel.getText().replaceAll("[^0-9]", ""));
	        
	        // Tính toán và định dạng lại tổng tiền
	        totalLabel.setText(String.format("%,d VND", subtotal - discount));
	    } catch (NumberFormatException e) {
	        totalLabel.setText("0 VND");
	    }
	}

	public void initialize() {
		colProductID.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("id"));

		colProductName.setCellValueFactory(new PropertyValueFactory<CartItem, String>("name"));

		colProductCategory.setCellValueFactory(new PropertyValueFactory<CartItem, String>("category"));

		colProductPrice.setCellValueFactory(new PropertyValueFactory<CartItem, Double>("price"));

		colProductQuantity.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("quantity"));

		if (cart != null) {
			tblProduct.setItems((ObservableList<CartItem>) cart.getItemsOrdered());
		} else {
			System.err.println("Error: Cart is null. Make sure to set the cart before initializing the view.");
		}

		tblProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				numLabel.setText(Integer.toString(newSelection.getQuantity()));
				numLabel.setAlignment(Pos.CENTER);
			} else {
				numLabel.setText("0"); // or whatever default you want
			}
		});
		
		phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
	        loadBonusPoints(newValue.trim());
	    });
		
		updateSubTotal();
		updatetotal();

	}

	void updateButtonBar(Products product) {
		if (product == null) {
			btnRemove.setVisible(false);
		} else {
			btnRemove.setVisible(true);
		}

	}

	@FXML
	void placeOrderBtnPressed(ActionEvent event) {
	    try {
	        if (cart.getItemsOrdered().isEmpty()) {
	            showAlert("Giỏ hàng trống", "Vui lòng thêm sản phẩm vào giỏ hàng.");
	            return;
	        }

	        String phoneNumber = phoneNumberField.getText().trim();
	        if (!phoneNumber.isEmpty() && !phoneNumber.matches("\\d{10,15}")) {
	            showAlert("Lỗi", "Số điện thoại phải có 10-15 chữ số.");
	            return;
	        }

	        // Lấy discount từ giao diện (đã loại bỏ " VND" và dấu ",")
	        int discount = Integer.parseInt(discountLabel.getText().replaceAll("[^0-9]", ""));

	     // Tính tổng tiền sau discount
	        double subtotal = cart.getItemsOrdered().stream()
	                           .mapToDouble(item -> item.getTotalPrice())
	                           .sum();
	        double totalAfterDiscount = Math.max(0, subtotal - discount);
	        
	        // Tạo đơn hàng với discount (total sẽ được tính bên trong Order)
	        Order order = new Order(
	            "", 
	            phoneNumber,
	            "offline",
	            "Store pickup",
	            new ArrayList<>(cart.getItemsOrdered()),
	            discount // Truyền discount vào
	        );
	        
	        // Set tổng tiền sau discount trước khi lưu
	        order.setTotalAmount(totalAfterDiscount);

	        // Lưu đơn hàng
	        order.placeCompleteOrder(parentController.getCurrentUserId()); // Truyền employeeId từ StoreController

	        // Cộng điểm nếu không dùng discount
	        if (!phoneNumber.isEmpty() && discount == 0) {
	            updateBonusPoints(phoneNumber, 20);
	            loadBonusPoints(phoneNumber);
	        }

	        showSuccessAlert("Thành công", "Đơn hàng đã được đặt!");
	        cart.emptyCart();
	        discountLabel.setText("0 VND");
	        bonusPointsField.setText("0");
	        updateSubTotal();
	        updatetotal();
	    } catch (Exception e) {
	        showAlert("Lỗi", e.getMessage());
	    }
	}

	// Hàm cập nhật điểm thưởng
	private void updateBonusPoints(String phoneNumber, int pointsToAdd) {
	    Connection conn = null;
	    try {
	        conn = DatabaseConnection.getConnection();
	        conn.setAutoCommit(false); // Bắt đầu transaction
	        
	        // 1. Kiểm tra số điện thoại có tồn tại và là customer không
	        String checkSql = "SELECT c.customer_id FROM Customers c " +
	                         "JOIN Users u ON c.customer_id = u.user_id " +
	                         "WHERE u.phone = ? AND u.role = 'customer'";
	        
	        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
	            checkStmt.setString(1, phoneNumber);
	            ResultSet rs = checkStmt.executeQuery();

	            if (rs.next()) {
	                int customerId = rs.getInt("customer_id");
	                
	                // 2. Cập nhật điểm thưởng
	                String updateSql = "UPDATE Customers SET bonus_point = bonus_point + ? " +
	                                 "WHERE customer_id = ?";
	                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
	                    updateStmt.setInt(1, pointsToAdd);
	                    updateStmt.setInt(2, customerId);
	                    int updated = updateStmt.executeUpdate();
	                    
	                    if (updated > 0) {
	                        conn.commit(); // Commit transaction
	                        showAlert("Tích điểm thành công", 
	                                "Khách hàng đã được cộng " + pointsToAdd + " điểm!");
	                    }
	                }
	            } else {
	                showAlert("Không tìm thấy khách hàng", 
	                        "Số điện thoại này chưa đăng ký hoặc không phải khách hàng");
	            }
	        }
	    } catch (SQLException e) {
	        try {
	            if (conn != null) conn.rollback(); // Rollback nếu có lỗi
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        System.err.println("Lỗi khi cập nhật điểm: " + e.getMessage());
	    } finally {
	        try {
	            if (conn != null) {
	                conn.setAutoCommit(true);
	                conn.close();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

	private void loadBonusPoints(String phoneNumber) {
	    // Kiểm tra số điện thoại hợp lệ trước khi query
	    if (phoneNumber == null || phoneNumber.trim().isEmpty() || !phoneNumber.matches("\\d{10,15}")) {
	        bonusPointsField.setText("0");
	        return;
	    }

	    String query = "SELECT c.bonus_point FROM Customers c " +
	                   "JOIN Users u ON c.customer_id = u.user_id " +
	                   "WHERE u.phone = ?";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setString(1, phoneNumber);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            bonusPointsField.setText(String.valueOf(rs.getInt("bonus_point")));
	        } else {
	            bonusPointsField.setText("0");
	        }
	    } catch (SQLException e) {
	        System.err.println("Lỗi khi tải điểm thưởng: " + e.getMessage());
	        bonusPointsField.setText("0");
	    }
	}
	
	// Phương thức xử lý sử dụng điểm thưởng
	@FXML
	void useBonusBtnPressed(ActionEvent event) {
	    try {
	        String phoneNumber = phoneNumberField.getText().trim();
	        if (phoneNumber.isEmpty() || !phoneNumber.matches("\\d{10,15}")) {
	            showAlert("Lỗi", "Vui lòng nhập số điện thoại hợp lệ (10-15 số)");
	            return;
	        }

	        int bonusPoints = Integer.parseInt(bonusPointsField.getText());
	        if (bonusPoints < 20) {
	            showAlert("Không đủ điểm", "Cần tối thiểu 20 điểm để giảm giá");
	            return;
	        }

	        // Tính toán giảm giá (20 điểm = 200 đồng)
	        int discountAmount = (bonusPoints / 20) * 200;
	        int subtotal = Integer.parseInt(subtotalLabel.getText().replaceAll("[^0-9]", ""));
	        
	        // Đảm bảo không giảm quá tổng hóa đơn
	        discountAmount = Math.min(discountAmount, subtotal);
	        
	        // Cập nhật giao diện
	        discountLabel.setText(String.format("%,d VND", discountAmount));
	        bonusPointsField.setText("0");
	        updatetotal();

	        // Cập nhật điểm trong database
	        updateBonusPoints(phoneNumber, -bonusPoints);

	    } catch (NumberFormatException e) {
	        showAlert("Lỗi", "Dữ liệu điểm không hợp lệ");
	    }
	}
	
	
	
	@FXML
	void clearCurrentCart(ActionEvent event) {
	    cart.emptyCart();
	    bonusPointsField.setText("0"); // Thêm dòng này
	    discountLabel.setText("0 VND");
	    updateSubTotal();
	    updatetotal();
	}

	private void showSuccessAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	void handleTableClick(MouseEvent event) {
		tblProduct.refresh();
		updateSubTotal();
		updatetotal();
	}

}