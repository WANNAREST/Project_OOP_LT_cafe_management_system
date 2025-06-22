package Controller.customer;

import Controller.CartController;
import Controller.customer.UserInformationController;
import Controller.db.DatabaseConnection;
import com.sun.jdi.IntegerValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import obj.Customer;
import obj.Cart;
import obj.Product;
import obj.Store;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserAppController implements Initializable {

    public Store store;
    public Cart cart;
    public Customer customer;


    private Parent cartView;
    private CartController cartController;


    @FXML
    private ImageView avt;

    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane anchorPane;


    @FXML
    private TextField searchField;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label nameLabel;

    @FXML
    private Label numCoinLabel;

    public UserAppController(Store store) {

        this(store, new Cart());  // Call the other constructor with new Cart
    }

    public UserAppController(Store store, Cart cart) {
        this.store = store;
        this.cart = cart == null ? new Cart() : cart;
    }

    public UserAppController(Customer customer) {
        this.customer = customer;
    }

    public UserAppController(Store store, Cart cart, Customer customer) {
        this.store = store;
        this.cart = cart == null ? new Cart() : cart;
        this.customer = customer;
    }

    public void updatePointDisplay() {
        if (customer != null && numCoinLabel != null) {
            // Refresh customer data from database to get latest points
            refreshCustomerPointsFromDatabase();
            numCoinLabel.setText(Integer.toString(customer.getpoint()));
            System.out.println("🔄 UI UPDATE: Customer coins display updated to: " + customer.getpoint());
        }
    }

    private void refreshCustomerPointsFromDatabase() {
        if (customer == null || customer.getPhone() == null || customer.getPhone().isEmpty()) {
            return;
        }

        String query = "SELECT c.bonus_point FROM Customers c " +
                "JOIN Users u ON c.customer_id = u.user_id " +
                "WHERE u.phone = ? AND u.role = 'customer'";

        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getPhone());
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int dbPoints = rs.getInt("bonus_point");
                customer.setpoint(dbPoints);
                System.out.println("🔄 REFRESH: Updated customer points from database: " + dbPoints);
            }

        } catch (java.sql.SQLException e) {
            System.err.println("❌ Error refreshing customer points: " + e.getMessage());
        }
    }



    public void onOrderSuccess() {
        if (customer != null) {
            customer.rewardOrderPoints(); // Thưởng 20 point
            updatePointDisplay(); // Cập nhật hiển thị

            // Có thể save vào database ở đây
            // saveCustomerToDatabase(customer);
        }
    }

    public boolean useCustomerPoints(int pointsToUse) {
        if (customer != null && customer.usePoints(pointsToUse)) {
            updatePointDisplay();
            return true;
        }
        return false;
    }


    public void showCheckOut(AnchorPane checkoutView) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(checkoutView);

        AnchorPane.setTopAnchor(checkoutView, 0.0);
        AnchorPane.setBottomAnchor(checkoutView, 0.0);
        AnchorPane.setLeftAnchor(checkoutView, 0.0);
        AnchorPane.setRightAnchor(checkoutView, 0.0);

        // Update coin display when showing checkout (in case coins were used)
        updatePointDisplay();
    }

    @FXML
    void showCart(ActionEvent event) {
        contentPane.getChildren().clear();
        updatePointDisplay(); // Refresh coin display when opening cart
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/user-cart-view.fxml"));
            Controller.CartController controller = new Controller.CartController(cart);
            controller.setParentController(this);
            controller.setCoinLabel(numCoinLabel);
            controller.setCustomer(customer);
            fxmlLoader.setController(controller);
            HBox root = fxmlLoader.load();
            contentPane.getChildren().add(root);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void showChatBot(ActionEvent event) {
        contentPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/chatbot-view.fxml"));
            ChatBotController controller = new ChatBotController(store);
            loader.setController(controller);
            HBox root = loader.load();
            contentPane.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load chatbot view: " + e.getMessage());
        }


    }

    @FXML
    void showFavouriteList(ActionEvent event) {

    }

    @FXML
    public void showMenu(ActionEvent event) {
        refreshProductGrid();
        updatePointDisplay(); // Refresh coin display when returning to menu
    }

    private void refreshProductGrid() {
        if (contentPane == null) {
            System.err.println("Error: contentPane is null");
            return;
        }

        contentPane.getChildren().clear();

        // Recreate the grid with products
        createProductGrid();
    }

    private void createProductGrid() {
        if(store == null){
            System.err.println("Error: Store is null. Make sure to set the store before initializing the view.");
            return;
        }

        // Create new grid components
        ScrollPane newScrollPane = new ScrollPane();
        GridPane newGridPane = new GridPane();

        // Configure grid
        newGridPane.getChildren().clear();
        newGridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        newGridPane.setHgap(20);
        newGridPane.setVgap(20);
        newGridPane.setPadding(new Insets(20));
        newGridPane.getColumnConstraints().clear();

        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(33.33);
            newGridPane.getColumnConstraints().add(column);
        }

        int col = 0;
        int row = 0;

        for(int i=0; i<store.getItemsInStore().size(); i++){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/product-view.fxml"));

                AnchorPane productView = fxmlLoader.load();
                productView.setPrefWidth(Region.USE_COMPUTED_SIZE);
                productView.setPrefHeight(Region.USE_COMPUTED_SIZE);

                ProductController controller = fxmlLoader.getController();

                // IMPORTANT: Use the SAME cart instance
                controller.setData(store.getItemsInStore().get(i));
                controller.setParentContainer(contentPane);
                controller.setCart(this.cart); // Use THIS.cart

                newGridPane.add(productView, col, row);
                GridPane.setMargin(productView, new Insets(10));

                col++;
                if(col == 3){
                    col = 0;
                    row++;
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading product view: " + e.getMessage());
            }
        }

        newGridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
        newGridPane.setMinWidth(Region.USE_COMPUTED_SIZE);

        newScrollPane.setFitToWidth(true);
        newScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        newScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        newScrollPane.setPannable(true);
        newScrollPane.setContent(newGridPane);

        contentPane.getChildren().add(newScrollPane);

        AnchorPane.setTopAnchor(newScrollPane, 0.0);
        AnchorPane.setBottomAnchor(newScrollPane, 0.0);
        AnchorPane.setLeftAnchor(newScrollPane, 0.0);
        AnchorPane.setRightAnchor(newScrollPane, 0.0);
    }


    @FXML
    private void handleSearchAction(ActionEvent event) {
        performSearch();
    }

    // This method will be called by both button click and Enter key press
    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            refreshProductGrid(); // Show all products if search is empty
            return;
        }

        filterProduct(searchText);
    }

    @FXML
    void showSearch(ActionEvent event) {
        performSearch();

    }

    private void filterProduct(String searchText) {
        if(contentPane == null || store == null){
            System.err.println("Error: contentPane or store is null");
        }

        contentPane.getChildren().clear();
        ScrollPane newScrollPane = new ScrollPane();
        GridPane newGridPane = new GridPane();

        // Configure grid
        newGridPane.getChildren().clear();
        newGridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        newGridPane.setHgap(20);
        newGridPane.setVgap(20);
        newGridPane.setPadding(new Insets(20));
        newGridPane.getColumnConstraints().clear();

        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(33.33);
            newGridPane.getColumnConstraints().add(column);
        }

        int col = 0;
        int row = 0;

        for (Product product : store.getItemsInStore()) {
            // Check if product name or description contains search text
            if (product.getName().toLowerCase().contains(searchText)) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/product-view.fxml"));
                    AnchorPane productView = fxmlLoader.load();
                    productView.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    productView.setPrefHeight(Region.USE_COMPUTED_SIZE);

                    ProductController controller = fxmlLoader.getController();
                    controller.setData(product);
                    controller.setParentContainer(contentPane);
                    controller.setCart(this.cart);

                    newGridPane.add(productView, col, row);
                    GridPane.setMargin(productView, new Insets(10));

                    col++;
                    if (col == 3) {
                        col = 0;
                        row++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error loading product view: " + e.getMessage());
                }
            }
        }

        newGridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
        newGridPane.setMinWidth(Region.USE_COMPUTED_SIZE);

        newScrollPane.setFitToWidth(true);
        newScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        newScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        newScrollPane.setPannable(true);
        newScrollPane.setContent(newGridPane);

        contentPane.getChildren().add(newScrollPane);

        AnchorPane.setTopAnchor(newScrollPane, 0.0);
        AnchorPane.setBottomAnchor(newScrollPane, 0.0);
        AnchorPane.setLeftAnchor(newScrollPane, 0.0);
        AnchorPane.setRightAnchor(newScrollPane, 0.0);
    }


    @FXML
    void showUserDetail(ActionEvent event) {
        contentPane.getChildren().clear();

        try {
            // Use getResource with a proper path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-information.fxml"));
            HBox root = loader.load();

            // Get the controller and pass the customer data and parent controller reference
            UserInformationController controller = loader.getController();
            if (controller != null && customer != null) {
                controller.setCustomer(customer);
                controller.setParentController(this); // Pass parent controller reference
            }

            contentPane.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
            // Add user-friendly error handling
            System.err.println("Could not load user detail view: " + e.getMessage());
        }
    }

    @FXML
    void signOut(ActionEvent event) {
        try {
            // Load the launch app (login selection screen)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LaunchApp.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            javafx.stage.Stage stage = (javafx.stage.Stage) nameLabel.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.setTitle("Cafe Shop Management System");
            stage.centerOnScreen();

            System.out.println("✅ User signed out successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error during sign out: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the product grid
        createProductGrid();

        // Update user display
        updatePointDisplay();
        updateUserDisplay();

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                performSearch();
            }
        });
    }

    public void updateUserDisplay() {
        if (customer != null && nameLabel != null) {
            nameLabel.setText(customer.getFullName());
        }
    }



}



