package com.mycompany.todolistgui;

import java.io.Serializable;

public class ToDoList implements Serializable {
    private static final long serialVersionUID = 1L;

    // database.txt
    String subject;
    String description;
    String location;
    String date;
    String time;

    ToDoList(String subject, String description, String location, String date, String time) {
        this.subject = subject;
        this.description = description;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Subject: " + subject
                + "\nDescription: " + description
                + "\nLocation: " + location
                + "\nDate (Deadline): " + date
                + "\nTime (Deadline): " + time
                + "\n\n";
    }
}