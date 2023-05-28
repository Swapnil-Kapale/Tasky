module com.example.tasky {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires MaterialFX;


    opens com.example.tasky to javafx.fxml;
    exports com.example.tasky;
}