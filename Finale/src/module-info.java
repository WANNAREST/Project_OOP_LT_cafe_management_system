module Model {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.naming;
	requires javafx.graphics;
	requires java.sql;
	requires java.base;
	requires javafx.base;
	opens Controller to javafx.fxml, javafx.base;
	opens obj to javafx.fxml, javafx.base;
	opens main to javafx.graphics, javafx.fxml, javafx.base;
	exports main;
	
}
