package com.mycompany.todolistgui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class PrimaryController implements Initializable {
    
    File file = new File("database.txt"); 
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
    ArrayList<ToDoList> list = new ArrayList<>();
    ListIterator iterator = null;
    SpinnerValueFactory<Integer> SVFH = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12);
    
    SpinnerValueFactory<Integer> SVFM = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
    
    

    @FXML
    TextField subject, discription, location, enter, recipient;
    
    @FXML
    Spinner<Integer> hr,mins;
    
    @FXML
    ToggleGroup AMPM;
    
    @FXML
    Label info, info1, info2, info3, update_prompt, EmailSuccess;
    
    @FXML
    RadioButton am,pm;
    
    @FXML
    ListView LV,show;
    
    @FXML
    DatePicker date;
    
    @FXML
    Button add;
    
    String toUpdate;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        if(file.isFile()){
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                list = (ArrayList<ToDoList>) ois.readObject();
                ois.close();
                
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            LV.getItems().addAll(list);
        }
            if(LV.getItems().isEmpty()){
                info2.setText("Your ToDo List Appears Here");
            }
        hr.setValueFactory(SVFH);
        mins.setValueFactory(SVFM);
        hr.getValueFactory().setValue(10);
        mins.getValueFactory().setValue(10);
        
    }
    
    public void ADD(){
        update_prompt.setText("");
        info.setText("");
        if(subject.getText().equals("") || discription.getText().equals("") || location.getText().equals("") || date.getValue()==null || AMPM.getSelectedToggle()==null){
            info.setText("Fill Out the remaining fields");
            return;
        }
        info.setText("");
        info2.setText("");
        String Sub = subject.getText();
        subject.setDisable(false);
        String Disc = discription.getText();
        String Loc = location.getText();
        LocalDate LocDate = date.getValue();
        String Date = LocDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        String Hours = Integer.toString(hr.getValue());
        String Minutes = Integer.toString(mins.getValue());
        RadioButton SelectedToggle = (RadioButton)AMPM.getSelectedToggle();
        String Time = Hours+":"+Minutes+" "+SelectedToggle.getText();
        if(subject.getText().equals(toUpdate)){
            add.setText("Add");
            info1.setText("Task Updated Successfully");
            iterator = list.listIterator();
            while(iterator.hasNext()){
                ToDoList obj0 = (ToDoList)iterator.next();
                if(obj0.subject.equals(toUpdate)){
                    
                    iterator.set(new ToDoList(Sub,Disc,Loc,Date,Time));
                }
            } 
        }
        else{
            add.setText("Add");
            info1.setText("Task Added & Saved Successfully");
            list.add(new ToDoList(Sub,Disc,Loc,Date,Time));
        }
        subject.setPromptText("");
        discription.setPromptText("");
        location.setPromptText("");
        subject.clear();
        discription.clear();
        location.clear();
        date.setValue(null);
        hr.getValueFactory().setValue(10);
        mins.getValueFactory().setValue(10);
        SelectedToggle.setSelected(false);
        

        try{
        oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(list);
        oos.close();
        }catch(Exception e){}

        LV.getItems().clear();
        LV.getItems().addAll(list);
        if(LV.getItems().isEmpty()){
            info2.setText("Your ToDo List Appears Here");
        }
    
    }
    
    public void SEARCH(){
        
        boolean isPresent = false;
        String toSearch = enter.getText();
        enter.setText("");
        iterator = list.listIterator();
        while(iterator.hasNext()){
            ToDoList obj = (ToDoList)iterator.next();
            if(obj.subject.toLowerCase().equals(toSearch.toLowerCase())){
                info.setText("");
                info1.setText("Task Found Successfully");
                info3.setText("");
                isPresent = true;
                show.getItems().clear();
                show.getItems().add(obj);
            }                          
        }
        if(isPresent==false){
            info1.setText("");
            info.setText("Task not Found");
            show.getItems().clear();
        }
    }
    
    public void UPDATE(){
        
        info.setText("");
        info1.setText("");
        boolean isPresent = false;
        toUpdate = enter.getText();
        enter.setText("");
        iterator = list.listIterator();
        while(iterator.hasNext()){
            ToDoList obj = (ToDoList)iterator.next();
            if(obj.subject.toLowerCase().equals(toUpdate.toLowerCase())){
                isPresent = true;
                info1.setText("");
                subject.clear();
                discription.clear();
                location.clear();
                date.setValue(null);
                hr.getValueFactory().setValue(12);
                mins.getValueFactory().setValue(30);
                RadioButton SelectedToggle = (RadioButton)AMPM.getSelectedToggle();
                if(SelectedToggle!=null){
                    SelectedToggle.setSelected(false);
                }
                show.getItems().clear();
                update_prompt.setText("Update the Task below");
                
                subject.setText(obj.subject);
                discription.setText(obj.discription);
                location.setText(obj.location);
                date.setValue(LocalDate.parse(obj.date,DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
                String hour = null, minute = null , apm = null;
                if(obj.time.charAt(1)==':' && obj.time.charAt(3)==' '){
                    hour = obj.time.substring(0,1);
                    minute = obj.time.substring(2, 3);
                    apm = obj.time.substring(4, 6);
                }
                else if (obj.time.charAt(1)==':' && obj.time.charAt(4)==' '){
                    hour = obj.time.substring(0,1);
                    minute = obj.time.substring(2, 4);
                    apm = obj.time.substring(5, 7);
                }
                else if (obj.time.charAt(2)==':' && obj.time.charAt(4)==' '){
                    hour = obj.time.substring(0,2);
                    minute = obj.time.substring(3, 4);
                    apm = obj.time.substring(5, 7);
                }
                else if (obj.time.charAt(2)==':' && obj.time.charAt(5)==' '){
                    hour = obj.time.substring(0,2);
                    minute = obj.time.substring(3, 5);
                    apm = obj.time.substring(6, 8);
                }
                hr.getValueFactory().setValue(Integer.parseInt(hour));
                mins.getValueFactory().setValue(Integer.parseInt(minute));
                String STAM = am.getText();
                String STPM = pm.getText();
                
                if(apm.equals(STAM)){
                    am.setSelected(true);
                }
                else if (apm.equals(STPM)){
                    pm.setSelected(true);
                }
                subject.setDisable(true);
                add.setText("Update the task");
                
            }                          
        }   
        if(isPresent == false){
            info1.setText("");
            info.setText("Task not Found");
        }
    }
    
    public void DELETE(){
        
        boolean isPresent = false;
        info1.setText("");
        String toDelete = enter.getText();
        enter.setText("");
        iterator = list.listIterator();
        while(iterator.hasNext()){
            ToDoList obj = (ToDoList) iterator.next();
            if(obj.subject.toLowerCase().equals(toDelete.toLowerCase())){
                show.getItems().clear();
                isPresent = true;
                iterator.remove();
                info.setText("Task Deleted Successfully");
            }
        }
        if(isPresent == false){
            info1.setText("");
            info.setText("Task not Found");
        }
        try{
        oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(list);
        oos.close();
        }catch(Exception e){}

        LV.getItems().clear();
        LV.getItems().addAll(list);
        if(LV.getItems().isEmpty()){
            info2.setText("Your ToDo List Appears Here");
        }
        
    }
    public void SWITCHSTAGE(){
        App obj = new App();
        obj.SwitchStage();
    }
}
