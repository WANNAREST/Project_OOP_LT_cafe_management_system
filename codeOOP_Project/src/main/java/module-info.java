module hust.soict.hedspi.market.codeoop_project {
    requires javafx.controls;
    requires javafx.fxml;

    opens application to javafx.fxml;
    exports application;
}
