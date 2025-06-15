package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import obj.Store;

import java.io.IOException;

public class CartControlller {

    @FXML
    private Label discountLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label totalLabel;
    private UserAppController parentController;

    @FXML
    void checkOutBtnPressed(ActionEvent event) {

    }
    
    public void setParentController(UserAppController parentController) {
        this.parentController = parentController;
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
    void removeBtnPressed(ActionEvent event) {

    }

}
