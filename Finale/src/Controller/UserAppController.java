package Controller;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import obj.Cart;
import obj.Product;
import obj.Store;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserAppController implements Initializable {

    public Store store;
    private Cart cart;
    
    @FXML private Circle avt;
    @FXML private GridPane gridPane;
    @FXML private AnchorPane anchorPane;
    @FXML private AnchorPane contentPane;
    @FXML private ScrollPane scrollPane;
    @FXML private Label nameLabel;

    public UserAppController(Store store) {
        this.store = store;
        this.cart = new Cart();
    }

    public UserAppController() {
        this.store = new Store();
        this.cart = new Cart();
    }

    @FXML
    void showOrderPending(ActionEvent event) {
        System.out.println("showOrderPending called");
        contentPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-cart-view.fxml"));
            HBox root = loader.load();
            
            // ✅ THÊM - Setup OrderController với database mới
            OrderController orderController = loader.getController();
           
            
            contentPane.getChildren().add(root);
            
            root.prefWidthProperty().bind(contentPane.widthProperty());
            root.prefHeightProperty().bind(contentPane.heightProperty());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load order pending view: " + e.getMessage());
        }
    }

    @FXML
    void showChatBot(ActionEvent event) {
        showAlert("Coming Soon", "ChatBot feature đang được phát triển!", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    void showSalaryRecord(ActionEvent event) {
        System.out.println("showSalaryRecord called");
        contentPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SalaryRecord.fxml"));
            BorderPane root = loader.load();
            contentPane.getChildren().add(root);
            
            root.prefWidthProperty().bind(contentPane.widthProperty());
            root.prefHeightProperty().bind(contentPane.heightProperty());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load salary record view: " + e.getMessage());
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🚀 UserAppController initializing...");
        // ✅ HIỂN THỊ DASHBOARD LÀM TRANG MẶC ĐỊNH
        loadDashboard();
    }
    private void loadDashboard() {
        contentPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
            ScrollPane dashboardRoot = loader.load();
      
            contentPane.getChildren().add(dashboardRoot);
            
            // Bind size
            dashboardRoot.prefWidthProperty().bind(contentPane.widthProperty());
            dashboardRoot.prefHeightProperty().bind(contentPane.heightProperty());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Could not load dashboard: " + e.getMessage());
            
            // ✅ FALLBACK - Hiển thị thông báo chào mừng thay vì product grid
            showWelcomeMessage();
        }
    }
    private void showWelcomeMessage() {
        try {
            // Tạo welcome message đơn giản
            Label welcomeLabel = new Label("🏪 Chào mừng đến với Hệ thống Quản lý Coffee Shop!");
            welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            Label subLabel = new Label("Hãy chọn một chức năng từ menu bên trái để bắt đầu.");
            subLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            
            // Center the labels
            AnchorPane welcomePane = new AnchorPane();
            welcomePane.getChildren().addAll(welcomeLabel, subLabel);
            
            // Position labels
            AnchorPane.setTopAnchor(welcomeLabel, 250.0);
            AnchorPane.setLeftAnchor(welcomeLabel, 300.0);
            AnchorPane.setTopAnchor(subLabel, 300.0);
            AnchorPane.setLeftAnchor(subLabel, 350.0);
            
            contentPane.getChildren().add(welcomePane);
            
            welcomePane.prefWidthProperty().bind(contentPane.widthProperty());
            welcomePane.prefHeightProperty().bind(contentPane.heightProperty());
            
            System.out.println("📄 Welcome message displayed as fallback");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Could not show welcome message: " + e.getMessage());
        }
    }
    @FXML
    void showDashboard(ActionEvent event) {
        System.out.println("showDashboard called");
        contentPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
            ScrollPane dashboardRoot = loader.load();
            
            // ✅ THÊM - Setup DashboardController
            DashboardController dashboardController = loader.getController();
            
            contentPane.getChildren().add(dashboardRoot);
            
            dashboardRoot.prefWidthProperty().bind(contentPane.widthProperty());
            dashboardRoot.prefHeightProperty().bind(contentPane.heightProperty());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load dashboard view: " + e.getMessage());
            
            
        }
    }

    @FXML
    void showSearch(ActionEvent event) {
        showAlert("Coming Soon", "Search feature đang được phát triển!", Alert.AlertType.INFORMATION);
    }

    @FXML
    void showUserDetail(ActionEvent event) {
        System.out.println("showUserDetail called");
        contentPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Employee.fxml"));
            HBox root = loader.load();
            contentPane.getChildren().add(root);
            
            root.prefWidthProperty().bind(contentPane.widthProperty());
            root.prefHeightProperty().bind(contentPane.heightProperty());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load user detail view: " + e.getMessage());
        }
    }

    @FXML
    void showInventory(ActionEvent event) {
        System.out.println("showInventory called");
        contentPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Inventory.fxml"));
            HBox root = loader.load();
            InventoryController inventoryController = loader.getController();
            inventoryController.setStore(store);
            contentPane.getChildren().add(root);
            
            root.prefWidthProperty().bind(contentPane.widthProperty());
            root.prefHeightProperty().bind(contentPane.heightProperty());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load inventory view: " + e.getMessage());
        }
    }

    @FXML
    void showShift(ActionEvent event) {
        System.out.println("showShift called");
        contentPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Shift.fxml"));
            BorderPane root = loader.load();
            contentPane.getChildren().add(root);
            
            root.prefWidthProperty().bind(contentPane.widthProperty());
            root.prefHeightProperty().bind(contentPane.heightProperty());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load shift view: " + e.getMessage());
        }
    }

    @FXML
    void showCart(ActionEvent event) {
        System.out.println("showCart called");
        contentPane.getChildren().clear();
        try {
            // ✅ SỬA - Đổi từ "/view/cart.fxml" thành "/view/user-cart-view.fxml"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-cart-view.fxml"));
            
            // ✅ SỬA - Load BorderPane thay vì AnchorPane vì user-cart-view.fxml là BorderPane
            BorderPane cartRoot = loader.load();
            
            // ✅ SỬA - Lấy OrderController thay vì CartController vì user-cart-view.fxml dùng OrderController
            OrderController orderController = loader.getController();
            
            
            contentPane.getChildren().add(cartRoot);
            
            cartRoot.prefWidthProperty().bind(contentPane.widthProperty());
            cartRoot.prefHeightProperty().bind(contentPane.heightProperty());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load cart view: " + e.getMessage());
        }
    }

    @FXML
    void signOut(ActionEvent event) {
        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận đăng xuất");
            confirmAlert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
            confirmAlert.setContentText("Tất cả dữ liệu chưa lưu sẽ bị mất.");
            
            ButtonType btnYes = new ButtonType("Đăng xuất", ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType("Hủy", ButtonBar.ButtonData.NO);
            confirmAlert.getButtonTypes().setAll(btnYes, btnNo);
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            
            if (result.isPresent() && result.get() == btnYes) {
                clearUserSession();
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LaunchApp.fxml"));
                Parent launchRoot = loader.load();
                Scene launchScene = new Scene(launchRoot);
                
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.setScene(launchScene);
                currentStage.setTitle("OOP Coffee - Welcome");
                currentStage.centerOnScreen();
                
                showAlert("Thành công", "Đăng xuất thành công!", Alert.AlertType.INFORMATION);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể đăng xuất: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearUserSession() {
        try {
            if (store != null) {
                System.out.println("Clearing user session data...");
            }
            if (cart != null) {
                cart.emptyCart();
            }
            System.out.println("User session cleared successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void initialize(URL location, ResourceBundle resources) {
//        if(store == null){
//            System.err.println("Error: Store is null. Make sure to set the store before initializing the view.");
//            return;
//        }
//        store.getItemsInStore().addListener((ListChangeListener<Product>) change -> {
//            refreshProductGrid();
//        });
//        refreshProductGrid();
//    }

//    private void refreshProductGrid() {
//        gridPane.getChildren().clear();
//        
//        int col = 0;
//        int row = 0;
//        
//        System.out.println("Có tất cả " + store.getItemsInStore().size() + " sản phẩm trong cửa hàng");
//        for(int i = 0; i < store.getItemsInStore().size(); i++){
//            try {
//                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/product-view.fxml"));
//                AnchorPane productView = fxmlLoader.load();
//                productView.setPrefWidth(Region.USE_COMPUTED_SIZE);
//                productView.setPrefHeight(Region.USE_COMPUTED_SIZE);
//                
//                @SuppressWarnings("rawtypes")
//                ProductController controller = fxmlLoader.getController();
//                controller.setData(store.getItemsInStore().get(i));
//                controller.setCart(cart);
//
//                gridPane.add(productView, col, row);
//                GridPane.setMargin(productView, new Insets(10));
//
//                col++;
//                if (col == 3) {
//                    col = 0;
//                    row++;
//                }
//                
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.err.println("Error loading product view: " + e.getMessage());
//            }
//        }
//    }

    // Utility methods
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getters & Setters
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
}



