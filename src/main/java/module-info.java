module com.example.iclab03 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.iclab03 to javafx.fxml;
    exports com.example.iclab03;
}