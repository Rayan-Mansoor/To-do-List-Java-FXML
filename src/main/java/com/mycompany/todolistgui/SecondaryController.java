package com.mycompany.todolistgui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SecondaryController implements Initializable {
    File file = new File("database.txt"); 
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
    ArrayList<ToDoList> list = new ArrayList<>();
    ListIterator iterator = null;
    
    @FXML
    TextField recipient;
    
    @FXML
    Label EmailSuccess;
    
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
        }
        recipient.setFocusTraversable(false);
    }
    
    
    public void EMAIL(){
        System.out.println(list);
        String text = "";
        iterator = list.listIterator();
        while(iterator.hasNext()){
            text += iterator.next(); 
        }
        String subject = "ToDo List By Group 14";
        String msg = text;
        String from = "zrgm.group14@gmail.com";
        String to = recipient.getText();
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");
        Session session = Session.getInstance(properties, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication("zrgm.group14@gmail.com","hxcueirrgipvbzlx");
            } 
        });
        session.setDebug(true);
        MimeMessage mm = new MimeMessage(session);
        try {
            mm.setFrom(from);
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mm.setSubject(subject);
            mm.setText(msg);
            Transport.send(mm);
            EmailSuccess.setStyle(" -fx-text-fill:green; ");
            EmailSuccess.setText("Email Sent Successfully");
            } catch (Exception ex) {
                EmailSuccess.setStyle(" -fx-text-fill:red; ");
                EmailSuccess.setText("Failed To Send Email ");
            }
    }
}