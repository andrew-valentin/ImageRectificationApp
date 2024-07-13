module com.example.camscanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.camscanner to javafx.fxml;
    exports com.example.camscanner;
}