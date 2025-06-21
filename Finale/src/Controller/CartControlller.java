package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import obj.Cart;
import obj.Product;
import obj.Store;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CartControlller implements Initializable {

    @FXML private Label discountLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label totalLabel;
    @FXML private Label itemCountLabel;
    
    // ✅ THÊM - TableView để hiển thị cart
    @FXML private TableView<Product> cartTableView;
    @FXML private TableColumn<Product, String> colProductName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Integer> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, Long> colTotal;
    @FXML private TableColumn<Product, Void> colAction;
    
    private UserAppController parentController;
    private Cart cart;
    private Store store;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        updateCartDisplay();
    }

    // ✅ THÊM - Setup table columns
    private void setupTableColumns() {
        if (cartTableView != null) {
            colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            colTotal.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getTotalPrice()).asObject());
            
            // Setup action column with buttons
            setupActionColumn();
            
            // Format price columns
            colPrice.setCellFactory(col -> new TableCell<Product, Integer>() {
                @Override
                protected void updateItem(Integer price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText(String.format("%,d", price));
                    }
                }
            });
            
            colTotal.setCellFactory(col -> new TableCell<Product, Long>() {
                @Override
                protected void updateItem(Long total, boolean empty) {
                    super.updateItem(total, empty);
                    if (empty || total == null) {
                        setText(null);
                    } else {
                        setText(String.format("%,d", total));
                    }
                }
            });
        }
    }

    // ✅ THÊM - Setup action column với buttons +, -, remove
    private void setupActionColumn() {
        if (colAction != null) {
            colAction.setCellFactory(col -> new TableCell<Product, Void>() {
                private final Button btnIncrease = new Button("+");
                private final Button btnDecrease = new Button("-");
                private final Button btnRemove = new Button("❌");
                private final HBox hbox = new HBox(5, btnDecrease, btnIncrease, btnRemove);

                {
                    btnIncrease.setOnAction(e -> {
                        Product product = getTableView().getItems().get(getIndex());
                        increaseQuantity(product);
                    });
                    
                    btnDecrease.setOnAction(e -> {
                        Product product = getTableView().getItems().get(getIndex());
                        decreaseQuantity(product);
                    });
                    
                    btnRemove.setOnAction(e -> {
                        Product product = getTableView().getItems().get(getIndex());
                        removeProduct(product);
                    });
                    
                    btnIncrease.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    btnDecrease.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
                    btnRemove.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(hbox);
                    }
                }
            });
        }
    }

    // ✅ THÊM - Cart operations
    private void increaseQuantity(Product product) {
        if (cart != null) {
            cart.increaseProductQuantity(product.getProductId());
            updateCartDisplay();
        }
    }

    private void decreaseQuantity(Product product) {
        if (cart != null) {
            cart.decreaseProductQuantity(product.getProductId());
            updateCartDisplay();
        }
    }

    private void removeProduct(Product product) {
        if (cart != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận xóa");
            confirmAlert.setHeaderText("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?");
            confirmAlert.setContentText(product.getProductName());
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                cart.removeProduct(product);
                updateCartDisplay();
            }
        }
    }

    @FXML  // ✅ THÊM
    void continueShoppingBtnPressed(ActionEvent event) {
        if (parentController != null) {
            parentController.showDashboard(event);
        } else {
            System.err.println("Error: Parent controller is not set.");
        }
    }

    @FXML  // ✅ THÊM  
    void checkOutBtnPressed(ActionEvent event) {
        if (cart == null || cart.isEmpty()) {
            showAlert("Giỏ hàng trống!", "Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận thanh toán");
        confirmAlert.setHeaderText("Bạn có chắc muốn thanh toán?");
        confirmAlert.setContentText(
            "Tổng tiền: " + cart.getFormattedTotalCost() + "\n" +
            "Số sản phẩm: " + cart.getTotalItems()
        );
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            processCheckout();
        }
    }

    // ✅ THÊM - Process checkout
    private void processCheckout() {
        try {
            // TODO: Implement checkout logic
            // - Tạo order trong database
            // - Cập nhật stock
            // - Clear cart
            
            showAlert("Thanh toán thành công!", 
                     "Cảm ơn bạn đã mua hàng!\nTổng tiền: " + cart.getFormattedTotalCost(), 
                     Alert.AlertType.INFORMATION);
            
            // Clear cart sau khi thanh toán
            cart.emptyCart();
            updateCartDisplay();
            
            // Quay về dashboard
            if (parentController != null) {
                parentController.showDashboard(null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi thanh toán!", "Đã xảy ra lỗi trong quá trình thanh toán: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML  // ✅ THÊM
    void removeBtnPressed(ActionEvent event) {
        Product selectedProduct = cartTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            removeProduct(selectedProduct);
        } else {
            showAlert("Chưa chọn sản phẩm!", "Vui lòng chọn sản phẩm cần xóa.", Alert.AlertType.WARNING);
        }
    }

    // ✅ THÊM - Update cart display
    public void updateCartDisplay() {
        if (cart != null) {
            // Update table
            if (cartTableView != null) {
                cartTableView.setItems(cart.getItemsOrdered());
            }
            
            // Update labels
            long subtotal = cart.totalCost();
            double discount = 0; // TODO: Implement discount logic
            long total = subtotal - (long) discount;
            
            if (subtotalLabel != null) subtotalLabel.setText(String.format("%,d VND", subtotal));
            if (discountLabel != null) discountLabel.setText(String.format("%,.0f VND", discount));
            if (totalLabel != null) totalLabel.setText(String.format("%,d VND", total));
            if (itemCountLabel != null) itemCountLabel.setText(String.valueOf(cart.getTotalItems()));
        } else {
            // Clear display if no cart
            if (cartTableView != null) cartTableView.setItems(FXCollections.observableArrayList());
            if (subtotalLabel != null) subtotalLabel.setText("0 VND");
            if (discountLabel != null) discountLabel.setText("0 VND");
            if (totalLabel != null) totalLabel.setText("0 VND");
            if (itemCountLabel != null) itemCountLabel.setText("0");
        }
    }

    // ✅ SETTERS
    public void setParentController(UserAppController parentController) {
        this.parentController = parentController;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
        updateCartDisplay();
    }

    public void setStore(Store store) {
        this.store = store;
    }

    // ✅ UTILITY
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
