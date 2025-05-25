package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import obj.Store;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserAppController implements Initializable {

    public Store store;
    @FXML
    private Circle avt;

    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label nameLabel;

    public UserAppController(Store store) {
        this.store = store;
    }


    @FXML
    void showCart(ActionEvent event) {
        contentPane.getChildren().clear();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/user-cart-view.fxml"));
            CartControlller controller = new CartControlller();
            controller.setParentController(this);
            fxmlLoader.setController(controller);
            HBox root = fxmlLoader.load();
            contentPane.getChildren().add(root);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void showChatBot(ActionEvent event) {


    }

    @FXML
    void showFavouriteList(ActionEvent event) {

    }

    @FXML
    void showMenu(ActionEvent event) {
        anchorPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-main-app.fxml"));
            loader.setController(new UserAppController(store)); // Ensure the correct controller is set
            AnchorPane root = loader.load();
            anchorPane.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading user-main-app view: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    void showSearch(ActionEvent event) {

    }

    @FXML
    void showUserDetail(ActionEvent event) {
        contentPane.getChildren().clear();

        try {
            // Use getResource with a proper path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-information.fxml"));
            HBox root = loader.load();
            contentPane.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
            // Add user-friendly error handling
            System.err.println("Could not load user detail view: " + e.getMessage());
        }
    }

    @FXML
    void signOut(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(store == null){
            System.err.println("Error: Store is null. Make sure to set the store before initializing the view.");
            return;
        }
        if (scrollPane == null) {
            scrollPane = new ScrollPane();
            System.out.println("Created new ScrollPane as it was null");
        }

        // Ensure gridPane exists
        if (gridPane == null) {
            gridPane = new GridPane();
            System.out.println("Created new GridPane as it was null");
        }

        // Clear existing items in grid
        gridPane.getChildren().clear();
        gridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20));
        gridPane.getColumnConstraints().clear();

        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(33.33);
            gridPane.getColumnConstraints().add(column);
        }



        int col = 0;
        int row = 0;

        for(int i=0; i<store.getItemsInStore().size(); i++){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/product-view.fxml"));

                // First load the view
                AnchorPane productView = fxmlLoader.load();
                productView.setPrefWidth(Region.USE_COMPUTED_SIZE);
                productView.setPrefHeight(Region.USE_COMPUTED_SIZE);

                // Then get the controller AFTER loading
                ProductController controller = fxmlLoader.getController();

                // Set data to the controller
                controller.setData(store.getItemsInStore().get(i));

                // Add the product view to the gridPane
                gridPane.add(productView, col, row);
                GridPane.setMargin(productView, new Insets(10));

                // Increment column count
                col++;

                // Check if we need to move to the next row
                if(col == 3){
                    col = 0;
                    row++;
                }

                System.out.println("Added product " + i + " at position (" + col + "," + row + ")");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading product view: " + e.getMessage());
            }
        }

        gridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
        gridPane.setMinWidth(Region.USE_COMPUTED_SIZE);

        // Make sure gridPane is in the contentPane
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true); //

        scrollPane.setContent(gridPane);

        // Make sure contentPane exists before using it
        if (contentPane != null) {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(scrollPane);

            AnchorPane.setTopAnchor(scrollPane, 0.0);
            AnchorPane.setBottomAnchor(scrollPane, 0.0);
            AnchorPane.setLeftAnchor(scrollPane, 0.0);
            AnchorPane.setRightAnchor(scrollPane, 0.0);

        } else {
            System.err.println("Error: contentPane is null. Make sure it is properly initialized.");
        }

    }



}



