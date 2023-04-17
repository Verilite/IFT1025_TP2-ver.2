module com.example.gui_client {
    requires javafx.controls;
    requires javafx.fxml;


    opens server.models to javafx.fxml;
    exports server.models;
    exports com.example.gui_client;
    opens com.example.gui_client to javafx.fxml;
}