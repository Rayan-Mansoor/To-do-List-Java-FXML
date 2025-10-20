module com.mycompany.todolistgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.mail;
    requires io.github.cdimascio.dotenv.java;
    
    opens com.mycompany.todolistgui to javafx.fxml;
    exports com.mycompany.todolistgui;
}
