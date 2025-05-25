package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import obj.Product;

public class ProductController{

        @FXML
        private Button detailbtnPressed;

        @FXML
        private Label lblCost;

        @FXML
        private Label lblTitle;


        public ProductController() {

        }

        public void setData(Product product) {
            if (lblCost != null) {
                lblCost.setText(String.valueOf(product.getPrice()) + "vnd");
            }
            if(lblTitle != null) {
                lblTitle.setText(product.getName());
            }
            if (detailbtnPressed != null) {
                detailbtnPressed.setVisible(true);
            }



        }

    }


