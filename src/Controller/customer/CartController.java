package Controller.customer;

import Controller.customer.PaymentController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import obj.Cart;
import obj.CartItem;
import obj.Customer;
import obj.Order;
import obj.Product;
import Controller.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.LimitExceededException;

public class CartController {

    private Product product;
    private Cart cart;
    private Customer customer;

    // Support both parent controller types
    private Controller.StoreController storeParentController;
    private Controller.UserAppController userParentController;
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

    // Additional fields for customer version
    @FXML
    private Button btnCheckOut;

    @FXML
    private Button btnContinue;

    @FXML
    private HBox lolbar;

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

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Support both parent controller types
    public void setParentController(Controller.StoreController parentController) {
        this.storeParentController = parentController;
    }

    public void setParentController(Controller.UserAppController parentController) {
        this.userParentController = parentController;
    }

    public void setCoinLabel(Label label) {
        this.numCoinsLabel = label;
    }

    @FXML
    void checkOutBtnPressed(ActionEvent event) {
        try {
            // Get discount amount for bonus points logic
            int discountAmount = Integer.parseInt(discountLabel.getText().replace("VND","").replaceAll("[^0-9]", ""));

            // Check if customer is using coins and deduct them permanently
            if (customer != null && useCointbtnToggle.isSelected() && precoint > 0) {
                System.out.println("🎯 CHECKOUT: Customer using " + precoint + " coins");
                System.out.println("🎯 CHECKOUT: Customer ID: " + customer.getId() + ", Phone: " + customer.getPhone());
                System.out.println("🎯 CHECKOUT: Before deduction - Customer points: " + customer.getpoint());

                // Deduct coins from customer permanently
                boolean success = customer.usePoints(precoint);
                if (success) {
                    System.out.println("🎯 CHECKOUT: After deduction - Customer points: " + customer.getpoint());

                    // Update the customer's points in database
                    updateCustomerPointsInDatabase(customer.getId(), customer.getpoint());

                    // Verify the database update
                    verifyCustomerPointsInDatabase(customer.getId(), customer.getPhone());

                    // Update parent controller's coin display
                    if (userParentController != null) {
                        userParentController.updatePointDisplay();
                    }

                    System.out.println("✅ Deducted " + precoint + " coins from customer. Remaining: " + customer.getpoint());
                } else {
                    System.err.println("❌ Failed to deduct coins from customer object");
                }
            }

            // Add bonus points if no discount was used (for customer interface)
            if (customer != null && discountAmount == 0) {
                String phoneNumber = customer.getPhone();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    System.out.println("🎁 CHECKOUT: No discount used, adding 20 bonus points");
                    updateBonusPoints(phoneNumber, 20);
                } else {
                    System.out.println("🎁 CHECKOUT: Cannot add bonus points - no phone number");
                }
            } else if (discountAmount > 0) {
                System.out.println("🎁 CHECKOUT: Discount used (" + discountAmount + " VND), no bonus points added");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-checkout-view.fxml"));
            AnchorPane checkoutView = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setCart(cart);
            if (userParentController != null) {
                paymentController.setController(userParentController);
            }
            paymentController.setCustomer(customer);

            paymentController.setDiscount(discountAmount);

            if(userParentController != null){
                userParentController.showCheckOut(checkoutView);
            }else{
                System.err.println("Error: User parent controller is not set.");
            }

        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading checkout view " + e.getMessage());
        }
    }

    @FXML
    void continueShoppingBtnPressed(ActionEvent event) {
        if (storeParentController != null) {
            storeParentController.showMenu(event);
        } else if (userParentController != null) {
            userParentController.showMenu(event);
        } else {
            System.err.println("Error: No parent controller is set.");
        }
    }

    @FXML
    void removeBtnPressed(ActionEvent event) {
        CartItem item = tblProduct.getSelectionModel().getSelectedItem();
        if (item != null) {
            try {
                cart.removeProduct(item.getProduct());
            } catch (Exception e) {
                System.err.println("Error removing product: " + e.getMessage());
            }
        }
        updateSubTotal();
        updatetotal();
    }

    private int precoint = 0;

    @FXML
    void useCoinbtnPressed(ActionEvent event) {
        // For customer version with Customer object
        if (customer != null) {
            if(useCointbtnToggle.isSelected()) {
                // Toggle ON: Use coins
                int coins = customer.getpoint();
                if(coins > 0) {
                    precoint = coins;
                    // Temporarily deduct coins for display (don't modify customer permanently)
                    int discountAmount = customer.calculateDiscountFromPoints(precoint);
                    discountLabel.setText(String.format("%d VND", discountAmount));
                    if (numCoinsLabel != null) {
                        numCoinsLabel.setText(String.valueOf(0));
                    }
                    updatetotal();
                }else{
                    useCointbtnToggle.setSelected(false);
                    showAlert("No Coins", "You don't have any coins to use!");
                }
            }else{
                // Toggle OFF: Remove discount and restore display
                discountLabel.setText(String.format("%d VND",0));
                if (numCoinsLabel != null) {
                    numCoinsLabel.setText(String.valueOf(precoint));
                }
                updatetotal();
            }
        } else {
            // For store version with numCoinsLabel
            if (numCoinsLabel != null) {
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
                    // Use the cart's method if available, otherwise update directly
                    if (cart.getClass().getMethod("updateProductQuantity", Product.class, int.class) != null) {
                        cart.updateProductQuantity(item.getProduct(), newQuantity);
                    } else {
                        item.setQuantity(newQuantity);
                    }
                    numLabel.setText(Integer.toString(newQuantity));
                    tblProduct.refresh();
                    updateSubTotal();
                    updatetotal();
                } else {
                    try {
                        cart.removeProduct(item.getProduct());
                    } catch (Exception ex) {
                        System.err.println("Error removing product: " + ex.getMessage());
                    }
                    tblProduct.refresh();
                    updateSubTotal();
                    updatetotal();
                }
            }
        } catch (Exception e) {
            // Fallback to direct item manipulation
            CartItem item = tblProduct.getSelectionModel().getSelectedItem();
            if (item != null) {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity > 0) {
                    item.setQuantity(newQuantity);
                    numLabel.setText(Integer.toString(newQuantity));
                    tblProduct.refresh();
                    updateSubTotal();
                    updatetotal();
                } else {
                    try {
                        cart.removeProduct(item.getProduct());
                    } catch (Exception ex) {
                        System.err.println("Error removing product: " + ex.getMessage());
                    }
                    tblProduct.refresh();
                    updateSubTotal();
                    updatetotal();
                }
            }
        }
    }

    @FXML
    void plusBtnPressed(ActionEvent event) {
        try {
            CartItem item = tblProduct.getSelectionModel().getSelectedItem();
            if (item != null) {
                int newQuantity = item.getQuantity() + 1;
                // Use the cart's method if available, otherwise update directly
                if (cart.getClass().getMethod("updateProductQuantity", Product.class, int.class) != null) {
                    cart.updateProductQuantity(item.getProduct(), newQuantity);
                } else {
                    item.setQuantity(newQuantity);
                }
                numLabel.setText(Integer.toString(newQuantity));
                tblProduct.refresh();
                updateSubTotal();
                updatetotal();
            }
        } catch (Exception e) {
            // Fallback to direct item manipulation
            CartItem item = tblProduct.getSelectionModel().getSelectedItem();
            if (item != null) {
                int newQuantity = item.getQuantity() + 1;
                item.setQuantity(newQuantity);
                numLabel.setText(Integer.toString(newQuantity));
                tblProduct.refresh();
                updateSubTotal();
                updatetotal();
            }
        }
    }

    public void updateSubTotal() {
        int subtotal = 0;
        try {
            // Try using cart's totalCost method first
            subtotal = cart.totalCost();
        } catch (Exception e) {
            // Fallback to manual calculation
            for (CartItem item : cart.getItemsOrdered()) {
                subtotal += item.getTotalPrice();
            }
        }
        subtotalLabel.setText(String.format("%,d VND", subtotal));
    }

    public void updatetotal() {
        try {
            // Remove non-numeric characters from labels
            String subtotalText = subtotalLabel.getText().replaceAll("[^0-9]", "");
            String discountText = discountLabel.getText().replaceAll("[^0-9]", "");

            int subtotal = subtotalText.isEmpty() ? 0 : Integer.parseInt(subtotalText);
            int discount = discountText.isEmpty() ? 0 : Integer.parseInt(discountText);

            int total = Math.max(0, subtotal - discount);
            totalLabel.setText(String.format("%,d VND", total));
        } catch (NumberFormatException e) {
            totalLabel.setText("0 VND");
        }
    }

    public void initialize() {
        // Set up table columns
        if (colProductID != null) {
            colProductID.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("id"));
        }

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
                numLabel.setText("0");
            }
        });

        // Add phone number listener for bonus points (store version)
        if (phoneNumberField != null) {
            phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
                loadBonusPoints(newValue.trim());
            });
        }

        updateSubTotal();
        updatetotal();

        // Handle empty cart UI (customer version)
        if (btnCheckOut != null && btnContinue != null && lolbar != null) {
            if (tblProduct.getItems().size() == 0) {
                btnCheckOut.setVisible(false);
                btnContinue.setVisible(true);
                useCointbtnToggle.setVisible(false);
                lolbar.setAlignment(Pos.CENTER);
                lolbar.getChildren().clear();
                lolbar.getChildren().add(btnContinue);
            }
        }
    }

    void updateButtonBar(Product product) {
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

            String phoneNumber = phoneNumberField != null ? phoneNumberField.getText().trim() : "";
            if (!phoneNumber.isEmpty() && !phoneNumber.matches("\\d{10,15}")) {
                showAlert("Lỗi", "Số điện thoại phải có 10-15 chữ số.");
                return;
            }

            // Get discount from UI
            int discount = Integer.parseInt(discountLabel.getText().replaceAll("[^0-9]", ""));

            // Calculate total after discount
            double subtotal = cart.getItemsOrdered().stream()
                    .mapToDouble(item -> item.getTotalPrice())
                    .sum();
            double totalAfterDiscount = Math.max(0, subtotal - discount);

            // Create order with discount
            Order order = new Order(
                    "",
                    phoneNumber,
                    "offline",
                    "Store pickup",
                    new ArrayList<>(cart.getItemsOrdered()),
                    discount
            );

            // Set total amount after discount before saving
            if (hasMethod(order, "setTotalAmount")) {
                order.setTotalAmount(totalAfterDiscount);
            }

            // Save order
            System.out.println("📝 CART: Checking if order has placeCompleteOrder method: " + hasMethod(order, "placeCompleteOrder"));
            System.out.println("📝 CART: Store parent controller is not null: " + (storeParentController != null));

            if (hasMethod(order, "placeCompleteOrder") && storeParentController != null) {
                System.out.println("📝 CART: Calling placeCompleteOrder with employee ID: " + storeParentController.getCurrentUserId());
                boolean success = order.placeCompleteOrder(storeParentController.getCurrentUserId());
                System.out.println("📝 CART: Order placement result: " + success);
            } else {
                System.err.println("❌ CART: Cannot save order - missing method or parent controller");
                if (!hasMethod(order, "placeCompleteOrder")) {
                    System.err.println("❌ CART: placeCompleteOrder method not found");
                }
                if (storeParentController == null) {
                    System.err.println("❌ CART: storeParentController is null");
                }
            }

            // Check if customer coins were used via toggle (for customer object)
            if (customer != null && useCointbtnToggle.isSelected() && precoint > 0) {
                System.out.println("🎯 PLACE ORDER: Customer using " + precoint + " coins");
                System.out.println("🎯 PLACE ORDER: Customer ID: " + customer.getId() + ", Phone: " + customer.getPhone());
                System.out.println("🎯 PLACE ORDER: Before deduction - Customer points: " + customer.getpoint());

                // Deduct coins from customer permanently
                boolean success = customer.usePoints(precoint);
                if (success) {
                    System.out.println("🎯 PLACE ORDER: After deduction - Customer points: " + customer.getpoint());

                    // Update the customer's points in database
                    updateCustomerPointsInDatabase(customer.getId(), customer.getpoint());

                    // Verify the database update
                    verifyCustomerPointsInDatabase(customer.getId(), customer.getPhone());

                    System.out.println("✅ Deducted " + precoint + " coins from customer. Remaining: " + customer.getpoint());
                } else {
                    System.err.println("❌ Failed to deduct coins from customer object");
                }
            }

            // Add bonus points if not using discount
            if (!phoneNumber.isEmpty() && discount == 0) {
                System.out.println("🎁 PLACE ORDER: No discount used, adding 20 bonus points to " + phoneNumber);
                updateBonusPoints(phoneNumber, 20);
                loadBonusPoints(phoneNumber);
            } else if (!phoneNumber.isEmpty() && discount > 0) {
                System.out.println("🎁 PLACE ORDER: Discount used (" + discount + " VND), no bonus points added");
            } else if (phoneNumber.isEmpty()) {
                System.out.println("🎁 PLACE ORDER: No phone number provided, cannot add bonus points");
            }

            showSuccessAlert("Thành công", "Đơn hàng đã được đặt!");
            clearCurrentCart(event);
        } catch (Exception e) {
            showAlert("Lỗi", e.getMessage());
        }
    }

    // Helper method to check if a method exists
    private boolean hasMethod(Object obj, String methodName) {
        try {
            if (methodName.equals("placeCompleteOrder")) {
                obj.getClass().getMethod(methodName, int.class);
            } else {
                obj.getClass().getMethod(methodName);
            }
            return true;
        } catch (NoSuchMethodException e) {
            System.err.println("❌ METHOD CHECK: " + methodName + " method not found in " + obj.getClass().getSimpleName());
            return false;
        }
    }

    // Bonus points methods (for store version)
    private void updateBonusPoints(String phoneNumber, int pointsToAdd) {
        System.out.println("🎁 BONUS POINTS: Attempting to add " + pointsToAdd + " points to phone: " + phoneNumber);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT c.customer_id, c.bonus_point FROM Customers c " +
                    "JOIN Users u ON c.customer_id = u.user_id " +
                    "WHERE u.phone = ? AND u.role = 'customer'";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, phoneNumber);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int customerId = rs.getInt("customer_id");
                    int currentPoints = rs.getInt("bonus_point");
                    System.out.println("🎁 BONUS POINTS: Found customer ID " + customerId + " with " + currentPoints + " current points");

                    String updateSql = "UPDATE Customers SET bonus_point = bonus_point + ? " +
                            "WHERE customer_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, pointsToAdd);
                        updateStmt.setInt(2, customerId);
                        int updated = updateStmt.executeUpdate();

                        if (updated > 0) {
                            conn.commit();
                            System.out.println("✅ BONUS POINTS: Successfully added " + pointsToAdd + " points to database");

                            // Verify the update
                            verifyBonusPointsUpdate(phoneNumber, currentPoints + pointsToAdd);

                            showSuccessAlert("Tích điểm thành công",
                                    "Khách hàng đã được cộng " + pointsToAdd + " điểm!");
                        } else {
                            System.err.println("❌ BONUS POINTS: No rows updated in database");
                            conn.rollback();
                        }
                    }
                } else {
                    System.err.println("❌ BONUS POINTS: No customer found with phone: " + phoneNumber);
                    showAlert("Không tìm thấy khách hàng",
                            "Số điện thoại này chưa đăng ký hoặc không phải khách hàng");
                }
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ BONUS POINTS: Database error: " + e.getMessage());
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

    // Helper method to verify bonus points were actually added to database
    private void verifyBonusPointsUpdate(String phoneNumber, int expectedPoints) {
        String query = "SELECT c.bonus_point FROM Customers c " +
                "JOIN Users u ON c.customer_id = u.user_id " +
                "WHERE u.phone = ? AND u.role = 'customer'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int actualPoints = rs.getInt("bonus_point");
                if (actualPoints == expectedPoints) {
                    System.out.println("✅ VERIFICATION: Database correctly shows " + actualPoints + " points");
                } else {
                    System.err.println("❌ VERIFICATION: Expected " + expectedPoints + " but database shows " + actualPoints);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ VERIFICATION ERROR: " + e.getMessage());
        }
    }

    private void loadBonusPoints(String phoneNumber) {
        if (bonusPointsField == null) return;

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
            System.err.println("Error loading bonus points: " + e.getMessage());
            bonusPointsField.setText("0");
        }
    }

    @FXML
    void useBonusBtnPressed(ActionEvent event) {
        // Get phone number from the field
        String phoneNumber = phoneNumberField != null ? phoneNumberField.getText().trim() : "";

        if (phoneNumber.isEmpty() || !phoneNumber.matches("\\d{10,15}")) {
            showAlert("Lỗi", "Vui lòng nhập số điện thoại hợp lệ để sử dụng điểm thưởng!");
            return;
        }

        // Get current bonus points from database
        int currentBonusPoints = getCurrentBonusPoints(phoneNumber);

        if (currentBonusPoints <= 0) {
            showAlert("Không có điểm", "Khách hàng không có điểm thưởng để sử dụng!");
            return;
        }

        // Calculate discount: 20 điểm = 200 VND, so 1 điểm = 10 VND
        int discountAmount = currentBonusPoints * 10;

        // Apply discount and set points to 0
        discountLabel.setText(String.format("%,d VND", discountAmount));
        bonusPointsField.setText("0");

        // Update database - use all points (set to 0)
        updateBonusPointsToZero(phoneNumber);

        // Update total
        updatetotal();

        showSuccessAlert("Sử dụng điểm thành công",
                String.format("Đã sử dụng %d điểm thưởng, giảm %,d VND", currentBonusPoints, discountAmount));
    }

    // Helper method to get current bonus points
    private int getCurrentBonusPoints(String phoneNumber) {
        String query = "SELECT c.bonus_point FROM Customers c " +
                "JOIN Users u ON c.customer_id = u.user_id " +
                "WHERE u.phone = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("bonus_point");
            }
        } catch (SQLException e) {
            System.err.println("Error getting bonus points: " + e.getMessage());
        }

        return 0;
    }

    // Helper method to set bonus points to 0 after using them
    private void updateBonusPointsToZero(String phoneNumber) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT c.customer_id FROM Customers c " +
                    "JOIN Users u ON c.customer_id = u.user_id " +
                    "WHERE u.phone = ? AND u.role = 'customer'";

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, phoneNumber);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int customerId = rs.getInt("customer_id");

                    String updateSql = "UPDATE Customers SET bonus_point = 0 WHERE customer_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, customerId);
                        int updated = updateStmt.executeUpdate();

                        if (updated > 0) {
                            conn.commit();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi khi sử dụng điểm: " + e.getMessage());
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

    @FXML
    void clearCurrentCart(ActionEvent event) {
        if (cart != null) {
            try {
                if (hasMethod(cart, "emptyCart")) {
                    cart.emptyCart();
                } else if (hasMethod(cart, "clearCart")) {
                    cart.clearCart();
                }
            } catch (Exception e) {
                // Manual clear
                cart.getItemsOrdered().clear();
            }

            if (discountLabel != null) discountLabel.setText("0 VND");
            if (bonusPointsField != null) bonusPointsField.setText("0");

            // Reset coin usage state
            if (useCointbtnToggle != null) {
                useCointbtnToggle.setSelected(false);
            }
            precoint = 0;

            updateSubTotal();
            updatetotal();

            // Handle customer version UI
            if (btnCheckOut != null && btnContinue != null && lolbar != null) {
                btnCheckOut.setVisible(false);
                btnContinue.setVisible(true);
                useCointbtnToggle.setVisible(false);
                lolbar.setAlignment(Pos.CENTER);
                lolbar.getChildren().clear();
                lolbar.getChildren().add(btnContinue);
            }
        }
    }

    void clearCart() {
        clearCurrentCart(null);
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateCustomerPointsInDatabase(int customerId, int newPointBalance) {
        System.out.println("🔄 Attempting to update customer ID: " + customerId + " to " + newPointBalance + " points");

        // Method 1: Try updating by customer_id
        String updateSql = "UPDATE Customers SET bonus_point = ? WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setInt(1, newPointBalance);
            stmt.setInt(2, customerId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Customer points updated in database: " + newPointBalance);
                return; // Success, exit method
            } else {
                System.err.println("❌ No rows updated - customer ID might not exist: " + customerId);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating customer points by ID: " + e.getMessage());
        }

        // Method 2: Fallback - try updating by phone number if customer object has phone
        if (customer != null && customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            System.out.println("🔄 Fallback: Attempting to update by phone number: " + customer.getPhone());
            updateCustomerPointsByPhone(customer.getPhone(), newPointBalance);
        }
    }

    private void updateCustomerPointsByPhone(String phoneNumber, int newPointBalance) {
        String updateSql = "UPDATE Customers c " +
                "JOIN Users u ON c.customer_id = u.user_id " +
                "SET c.bonus_point = ? " +
                "WHERE u.phone = ? AND u.role = 'customer'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setInt(1, newPointBalance);
            stmt.setString(2, phoneNumber);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Customer points updated by phone in database: " + newPointBalance);
            } else {
                System.err.println("❌ Failed to update customer points by phone: " + phoneNumber);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating customer points by phone: " + e.getMessage());
        }
    }

    @FXML
    void handleTableClick(MouseEvent event) {
        // Handle table click events if needed
    }

    // Debug method to verify database state
    private void verifyCustomerPointsInDatabase(int customerId, String phoneNumber) {
        System.out.println("🔍 VERIFICATION: Checking database for customer ID: " + customerId + ", Phone: " + phoneNumber);

        // Check by customer ID
        String query1 = "SELECT bonus_point FROM Customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query1)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int dbPoints = rs.getInt("bonus_point");
                System.out.println("🔍 VERIFICATION: Database shows " + dbPoints + " points for customer ID " + customerId);
            } else {
                System.out.println("🔍 VERIFICATION: No customer found with ID " + customerId);
            }

        } catch (SQLException e) {
            System.err.println("🔍 VERIFICATION ERROR: " + e.getMessage());
        }

        // Check by phone number
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String query2 = "SELECT c.bonus_point FROM Customers c " +
                    "JOIN Users u ON c.customer_id = u.user_id " +
                    "WHERE u.phone = ? AND u.role = 'customer'";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query2)) {

                stmt.setString(1, phoneNumber);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int dbPoints = rs.getInt("bonus_point");
                    System.out.println("🔍 VERIFICATION: Database shows " + dbPoints + " points for phone " + phoneNumber);
                } else {
                    System.out.println("🔍 VERIFICATION: No customer found with phone " + phoneNumber);
                }

            } catch (SQLException e) {
                System.err.println("🔍 VERIFICATION ERROR: " + e.getMessage());
            }
        }
    }
}