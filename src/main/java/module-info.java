module sample.cafeshopmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;

    opens sample.cafeshopmanagement to javafx.fxml;
    opens sample.cafeshopmanagement.Design to javafx.fxml; // Mở quyền cho thư mục Design
    exports sample.cafeshopmanagement;
    exports sample.cafeshopmanagement.controllers;
    opens sample.cafeshopmanagement.controllers to javafx.fxml;
}