module com.example.diplom2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.diplom2 to javafx.fxml;
    exports com.example.diplom2;
}