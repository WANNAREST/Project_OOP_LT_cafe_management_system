module EMPLOYEE_OOP_COFFEE_SHOP {
//	requires javafx.controls;
//	requires javafx.fxml;
//	requires javafx.graphics;
//	requires javafx.base;
//	requires java.naming;
//	
//	opens application to javafx.graphics, javafx.fxml;
	
	requires javafx.controls;
    requires javafx.fxml;
	requires java.naming;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql;

    opens Controller to javafx.fxml; // Cho phép JavaFX truy cập package Controller
    opens application to javafx.fxml; // Cho phép JavaFX truy cập package application (nếu cần)

    opens obj to javafx.base;

    exports application; // Xuất package application nếu cần chạy lớp Main
    exports Controller; // Xuất package Controller nếu các lớp khác cần truy cập
    exports obj;
}
