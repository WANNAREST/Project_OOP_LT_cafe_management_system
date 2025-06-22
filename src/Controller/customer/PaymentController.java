package Controller.customer;

import Controller.UserAppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import obj.Cart;
import obj.CartItem;
import obj.Customer;

import java.io.IOException;

public class PaymentController {
    public Cart cart;
    public Customer customer;
    private int discount =0;
    private UserAppController parentController;



    @FXML
    private RadioButton btnBank;

    @FXML
    private RadioButton btnCOD;

    @FXML
    private Label labelDiscount;

    @FXML
    private Label labelPayment;

    @FXML
    private Label labelPaymentMethod;

    @FXML
    private Label labelTotal;

    @FXML
    private ToggleGroup payment_method;

    @FXML
    private AnchorPane paymentpane;

    @FXML
    private TableColumn<?, ?> tblName;

    @FXML
    private TableView<CartItem> tblPayemnt;

    @FXML
    private TableColumn<?, ?> tblPrice;

    @FXML
    private TableColumn<?, ?> tblQuantity;

    @FXML
    private TableColumn<?, ?> tblTotal;

    @FXML
    private TextField txtDeliveryAddress;

    public void setCart(Cart cart) {
        this.cart = cart;
        initialize();
    }



    public void setController(UserAppController controller){
        this.parentController = controller;
    }

    @FXML
    void BankbtnPressed(ActionEvent event) {
        try {
            paymentpane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customer/user-bank-payment.fxml"));
            AnchorPane bankpane = loader.load();
            BankPaymentController bankPaymentController = loader.getController();
            bankPaymentController.setCart(cart);
            bankPaymentController.setCustomer(customer);
            bankPaymentController.setParentController(this);
            bankPaymentController.setDeliveryAddress(getDeliveryAddress());
            paymentpane.getChildren().add(bankpane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Failed to load bank payment view");
        }
    }

    @FXML
    void CODbtnPressed(ActionEvent event) {
        try{
            paymentpane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customer/user-cod-payment.fxml"));
            AnchorPane codpane = loader.load();
            CODPaymentController codPaymentController = loader.getController();
            codPaymentController.setCart(cart);
            codPaymentController.setCustomer(customer);
            codPaymentController.setParentController(this);
            codPaymentController.setDeliveryAddress(getDeliveryAddress());
            paymentpane.getChildren().add(codpane);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setDiscount(int discount) {
        this.discount = discount;
        labelDiscount.setText(String.format("%d VND", discount));
        labelTotal.setText(String.format("%d VND", cart.totalCost() - discount));
    }

    void initialize(){

        tblName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tblQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        if (cart != null) {
            tblPayemnt.setItems(cart.getItemsOrdered());
        }
    }

    public void orderSuccess() {

        tblPayemnt.getItems().clear();
        labelDiscount.setText("0 VND");
        labelTotal.setText("0 VND");

        updatePointDisplay();

        if(parentController != null){
            parentController.showMenu(null);
        } else {
            System.err.println("Parent controller is null in PaymentController");
        }

    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void updatePointDisplay() {
        if (parentController != null) {
            parentController.updatePointDisplay();
        }
    }

    public String getDeliveryAddress() {
        if (txtDeliveryAddress != null && !txtDeliveryAddress.getText().trim().isEmpty()) {
            return txtDeliveryAddress.getText().trim();
        }
        return customer != null ? customer.getAddress() : "Địa chỉ không có sẵn";
    }
}
