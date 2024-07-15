module com.example.camscanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.rectification to javafx.fxml;
    exports com.example.rectification;
}