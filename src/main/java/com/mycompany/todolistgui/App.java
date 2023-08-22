package com.mycompany.todolistgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.stage.Modality;


public class App extends Application {

    private static Scene scene,scene2;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 762, 511);
        scene2 = new Scene(loadFXML("email"), 300, 200);
        stage.setTitle("Todo list by Group 14");
        stage.setScene(scene);
        stage.setResizable(false);
        
        stage.show();
    }
    
    void SwitchStage(){
        
        Stage EmailStage = new Stage();
        EmailStage.setTitle("Email The ToDo List");
        EmailStage.setScene(scene2);
        EmailStage.initModality(Modality.APPLICATION_MODAL);
        EmailStage.show();
    }
    

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}