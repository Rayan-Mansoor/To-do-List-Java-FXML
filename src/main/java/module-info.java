module com.mycompany.todolistgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.mail;

    opens com.mycompany.todolistgui to javafx.fxml;
    exports com.mycompany.todolistgui;
}
