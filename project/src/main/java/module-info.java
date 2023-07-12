module com.abhijeet {
	requires javafx.controls;
	requires javafx.fxml;
	requires com.jfoenix;
	opens com.abhijeet to javafx.fxml;
	exports com.abhijeet;
}
