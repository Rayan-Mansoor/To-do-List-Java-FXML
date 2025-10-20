package com.mycompany.todolistgui;

import java.io.*;
import java.util.ArrayList;

public final class ToDoRepository {

    private final File dbFile;

    public ToDoRepository() {
        this(new File("database.txt"));
    }

    public ToDoRepository(File file) {
        this.dbFile = file;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ToDoList> load() {
        if (!dbFile.isFile())
            return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dbFile))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList) {
                return (ArrayList<ToDoList>) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void save(ArrayList<ToDoList> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dbFile))) {
            oos.writeObject(items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
