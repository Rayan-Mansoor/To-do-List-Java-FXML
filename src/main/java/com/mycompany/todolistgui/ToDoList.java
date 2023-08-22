
package com.mycompany.todolistgui;


import java.io.Serializable;

public class ToDoList implements Serializable{
    
    String subject;
    String discription;
    String location;
    String date;
    String time;
    ToDoList(String subject, String discription, String location,String date, String time){
        this.subject=subject;
        this.discription=discription;
        this.location=location;
        this.date=date;
        this.time=time;
        
    }
    public String toString(){
        return "Subject : "+subject + "\nDiscription: " + discription + "\nLocation: " + location + "\nDate (Deadline): " + date + "\nTime (Deadline): " + time+"\n\n"; 
    }
}
