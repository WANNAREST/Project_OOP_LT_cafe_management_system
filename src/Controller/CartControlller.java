package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import obj.Cart;
import obj.CartItem;
import obj.Product;

import javax.naming.LimitExceededException;

public class CartControlller {

private Product product;
private Cart cart;

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
    private TableView<CartItem> tblProduct;


    @FXML
    private Button btnMinus;

    @FXML
    private Button btnPlus;

    @FXML
    private Button btnRemove;

    @FXML
    private Label numLabel;

    @FXML
    private Label discountLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Button btnCheckOut;

    @FXML
    private Button btnContinue;


    @FXML
    private HBox lolbar;

    @FXML
    private ToggleButton useCointbtnToggle;

    @FXML
    private Label totalLabel;
    private UserAppController parentController;
    private Label numCoinsLabel;


    public CartControlller(Cart cart) {
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

    @FXML
    void checkOutBtnPressed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user-checkout-view.fxml"));
            AnchorPane checkoutView = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setCart(cart);
            paymentController.setController(parentController);

            int discount = Integer.parseInt(discountLabel.getText().replace("VND","").trim());
            paymentController.setDiscount(discount);

            if(parentController != null){
                parentController.showCheckOut(checkoutView);
            }else{
                System.err.println("Error: Parent controller is not set.");
            }


        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading checkout view " + e.getMessage());
        }



    }
    public void setParentController(UserAppController parentController) {
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
        if(useCointbtnToggle.isSelected()) {
            if(coins > 0) {
                precoint = coins;
                int discountAmount = coins * 2000;
                discountLabel.setText(String.format("%d VND", discountAmount));
                numCoinsLabel.setText(String.valueOf(0));
                updatetotal(); // Gọi cập nhật tổng tiền
            }else{
                useCointbtnToggle.setSelected(false);
                showAlert("No Coins", "You don't have any coins to use!");
            }
        }else{
            discountLabel.setText(String.format("%d VND",0));
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
        CartItem item = tblProduct.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                numLabel.setText(Integer.toString(newQuantity));
                tblProduct.refresh();
                updateSubTotal();
                updatetotal();
            }
        }
    }

    @FXML
    void plusBtnPressed(ActionEvent event) {
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

    public void updateSubTotal() {
         int subtotal = 0;
         subtotal = cart.totalCost();
         subtotalLabel.setText(String.format("%d VND", subtotal));

    }

    public void updatetotal(){
        try {
            String subtotalText = subtotalLabel.getText().replaceAll("[^\\d]", "");
            int subtotal = subtotalText.isEmpty() ? 0 : Integer.parseInt(subtotalText);

            // Lấy discount và xóa ký tự không phải số
            String discountText = discountLabel.getText().replaceAll("[^\\d]", "");
            int discount = discountText.isEmpty() ? 0 : Integer.parseInt(discountText);
            int total = subtotal - discount;
            totalLabel.setText(String.format("%d VND", total));
        } catch (NumberFormatException e) {
            totalLabel.setText(subtotalLabel.getText());
        }
    }


    public void initialize() {
        colProductID.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("id"));

        colProductName.setCellValueFactory(new PropertyValueFactory<CartItem, String>("name"));

        colProductCategory.setCellValueFactory(new PropertyValueFactory<CartItem, String>("category"));

        colProductPrice.setCellValueFactory(new PropertyValueFactory<CartItem, Double>("price"));

        colProductQuantity.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("quantity"));

        if (cart != null){
            tblProduct.setItems(cart.getItemsOrdered());
        }else{
            System.err.println("Error: Cart is null. Make sure to set the cart before initializing the view.");
        }


        tblProduct.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        numLabel.setText(Integer.toString(newSelection.getQuantity()));
                        numLabel.setAlignment(Pos.CENTER);
                    } else {
                        numLabel.setText("0"); // or whatever default you want
                    }
                }
        );
        updateSubTotal();
        updatetotal();

        if (tblProduct.getItems().size() == 0) {
            btnCheckOut.setVisible(false);
            btnContinue.setVisible(true);
            useCointbtnToggle.setVisible(false);
            lolbar.setAlignment(Pos.CENTER);
            lolbar.getChildren().clear();
            lolbar.getChildren().add(btnContinue);

}


    }

    void updateButtonBar(Product product){
        if (product == null){
            btnRemove.setVisible(false);
        }
        else{
            btnRemove.setVisible(true);
        }

    }

    void clearCart(){
        if(cart != null){
            cart.clearCart();
            tblProduct.getItems().clear();
            updateSubTotal();
            updatetotal();


            btnCheckOut.setVisible(false);
            btnContinue.setVisible(true);
            useCointbtnToggle.setVisible(false);
            lolbar.setAlignment(Pos.CENTER);
            lolbar.getChildren().clear();
            lolbar.getChildren().add(btnContinue);
        }
    }

}
