package com.mycompany.todolistgui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SecondaryController implements Initializable {

    private ArrayList<ToDoList> items = new ArrayList<>();
    private EmailService emailService; // lazy

    @FXML private TextField recipient;
    @FXML private Label EmailSuccess;
    @FXML private Button emailBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recipient.setFocusTraversable(false);
    }

    /** Inject the current tasks from the primary controller. */
    public void setItems(ArrayList<ToDoList> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    /** Send the email (called by the button in secondary.fxml). */
    public void sendEmail() {
        String to = trim(recipient.getText());
        if (to.isEmpty()) {
            setStatus("Recipient email required", "red");
            return;
        }
        try {
            new InternetAddress(to, true); // validate
        } catch (AddressException e) {
            setStatus("Invalid email address", "red");
            return;
        }

        String subject = "Your To-Do List";
        StringBuilder body = new StringBuilder();
        for (ToDoList t : items) body.append(t);

        if (emailService == null) {
            try {
                emailService = new EmailService();
            } catch (IllegalStateException envMissing) {
                setStatus("Missing SMTP env vars", "red");
                return;
            }
        }

        emailBtn.setDisable(true);
        setStatus("Sending...", "grey");

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                emailService.sendTextEmail(to, subject, body.toString());
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            emailBtn.setDisable(false);
            setStatus("Email sent successfully", "green");
        });
        task.setOnFailed(e -> {
            emailBtn.setDisable(false);
            Throwable ex = task.getException();
            setStatus((ex instanceof MessagingException) ? "Failed to send email" : "Unexpected error", "red");
        });
        new Thread(task, "email-sender").start();
    }

    private void setStatus(String text, String colorLower) {
        EmailSuccess.setText(text);
        EmailSuccess.setStyle("-fx-text-fill: " + colorLower + ";");
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}