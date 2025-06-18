package Controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import Cart_CoffeeShop.Order_CoffeeShop;
import Store.Store_CoffeeShop;

public class OrderHistoryController {

    private StoreController parentController;
    private ObservableList<Order_CoffeeShop> orders;
    
    @FXML
    private TableView<Order_CoffeeShop> orderTable;

    @FXML
    private TableColumn<Order_CoffeeShop, String> colCustomerName;

    @FXML
    private TableColumn<Order_CoffeeShop, String> colPhoneNumber;

    @FXML
    private TableColumn<Order_CoffeeShop, String> colOrderType;

    @FXML
    private TableColumn<Order_CoffeeShop, String> colDeliveryAddress;

    @FXML
    private TableColumn<Order_CoffeeShop, String> colOrderTime;

    @FXML
    private TableColumn<Order_CoffeeShop, String> colStatus;

    @FXML
    private TableColumn<Order_CoffeeShop, Double> colTotalAmount;

    public OrderHistoryController(ObservableList<Order_CoffeeShop> orders) {
        this.orders = orders;
    }

    public void setParentController(StoreController parentController) {
        this.parentController = parentController;
    }

    @FXML
    void backToMenu(ActionEvent event) {
        if (parentController != null) {
            parentController.showMenu(event);
        } else {
            System.err.println("Error: Parent controller is not set.");
        }
    }

    public void initialize() {
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colOrderType.setCellValueFactory(new PropertyValueFactory<>("orderType"));
        colDeliveryAddress.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        colOrderTime.setCellValueFactory(new PropertyValueFactory<>("orderTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        if (orders != null) {
            orderTable.setItems(orders);
        } else {
            System.err.println("Error: Orders list is null.");
        }
    }
}