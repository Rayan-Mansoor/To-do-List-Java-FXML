package com.mycompany.todolistgui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PrimaryController implements Initializable {

    private final ToDoRepository repository = new ToDoRepository();

    private ArrayList<ToDoList> items = new ArrayList<>();
    private ListIterator<ToDoList> iterator;

    private final SpinnerValueFactory<Integer> hoursFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12);
    private final SpinnerValueFactory<Integer> minutesFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
            59);

    // Note: keeping fx:id names the same as your FXML (subject/description/etc.) to
    // avoid breaking bindings.
    @FXML
    private TextField subject, description, location, enter;
    @FXML
    private Spinner<Integer> hr, mins;
    @FXML
    private ToggleGroup AMPM;
    @FXML
    private Label info, info1, info2, info3, update_prompt;
    @FXML
    private RadioButton am, pm;
    @FXML
    private ListView<ToDoList> LV, show;
    @FXML
    private DatePicker date;
    @FXML
    private Button add;

    private String pendingUpdateKey; // subject of the item being edited

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load saved items
        items = repository.load();
        LV.getItems().setAll(items);
        if (LV.getItems().isEmpty()) {
            info2.setText("Your To-Do List appears here");
        }

        // Spinners
        hr.setValueFactory(hoursFactory);
        mins.setValueFactory(minutesFactory);
        hr.getValueFactory().setValue(10);
        mins.getValueFactory().setValue(10);
    }

    /** Save (add or update) the task based on pendingUpdateKey state. */
    public void addTask() {
        update_prompt.setText("");
        info.setText("");

        if (subject.getText().isEmpty()
                || description.getText().isEmpty()
                || location.getText().isEmpty()
                || date.getValue() == null
                || AMPM.getSelectedToggle() == null) {
            info.setText("Fill out the remaining fields");
            return;
        }

        info.setText("");
        info2.setText("");

        String sub = subject.getText();
        String desc = description.getText();
        String loc = location.getText();
        LocalDate picked = date.getValue();

        String dateStr = picked.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        String hours = String.valueOf(hr.getValue());
        String minutes = String.valueOf(mins.getValue());
        RadioButton selectedToggle = (RadioButton) AMPM.getSelectedToggle();
        String timeStr = hours + ":" + minutes + " " + selectedToggle.getText();

        if (sub.equals(pendingUpdateKey)) {
            // Update existing
            add.setText("Add");
            info1.setText("Task updated");
            iterator = items.listIterator();
            while (iterator.hasNext()) {
                ToDoList current = iterator.next();
                if (current.subject.equals(pendingUpdateKey)) {
                    iterator.set(new ToDoList(sub, desc, loc, dateStr, timeStr));
                }
            }
            pendingUpdateKey = null;
        } else {
            // Add new
            add.setText("Add");
            info1.setText("Task added & saved");
            items.add(new ToDoList(sub, desc, loc, dateStr, timeStr));
        }

        clearEditor(selectedToggle);

        repository.save(items);
        LV.getItems().setAll(items);
        if (LV.getItems().isEmpty()) {
            info2.setText("Your To-Do List appears here");
        }
    }

    /** Search by subject (case-insensitive) and show the single match if found. */
    public void searchTask() {
        boolean found = false;
        String query = enter.getText();
        enter.setText("");

        iterator = items.listIterator();
        while (iterator.hasNext()) {
            ToDoList item = iterator.next();
            if (item.subject.equalsIgnoreCase(query)) {
                info.setText("");
                info1.setText("Task found");
                info3.setText("");
                found = true;
                show.getItems().setAll(item);
            }
        }
        if (!found) {
            info1.setText("");
            info.setText("Task not found");
            show.getItems().clear();
        }
    }

    /**
     * Populate fields to edit the task with the given subject from the search box.
     */
    public void prepareUpdateTask() {
        info.setText("");
        info1.setText("");

        boolean found = false;
        String key = enter.getText();
        enter.setText("");

        iterator = items.listIterator();
        while (iterator.hasNext()) {
            ToDoList item = iterator.next();
            if (item.subject.equalsIgnoreCase(key)) {
                found = true;
                info1.setText("");

                subject.clear();
                description.clear();
                location.clear();
                date.setValue(null);
                hr.getValueFactory().setValue(12);
                mins.getValueFactory().setValue(30);

                RadioButton selected = (RadioButton) AMPM.getSelectedToggle();
                if (selected != null)
                    selected.setSelected(false);

                show.getItems().clear();
                update_prompt.setText("Update the task below");

                // Populate editor
                subject.setText(item.subject);
                description.setText(item.description);
                location.setText(item.location);
                date.setValue(LocalDate.parse(item.date, DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

                // Parse time "H:MM AM" or "HH:M AM" etc.
                String hour, minute, apm;
                if (item.time.charAt(1) == ':' && item.time.charAt(3) == ' ') {
                    hour = item.time.substring(0, 1);
                    minute = item.time.substring(2, 3);
                    apm = item.time.substring(4, 6);
                } else if (item.time.charAt(1) == ':' && item.time.charAt(4) == ' ') {
                    hour = item.time.substring(0, 1);
                    minute = item.time.substring(2, 4);
                    apm = item.time.substring(5, 7);
                } else if (item.time.charAt(2) == ':' && item.time.charAt(4) == ' ') {
                    hour = item.time.substring(0, 2);
                    minute = item.time.substring(3, 4);
                    apm = item.time.substring(5, 7);
                } else {
                    hour = item.time.substring(0, 2);
                    minute = item.time.substring(3, 5);
                    apm = item.time.substring(6, 8);
                }

                hr.getValueFactory().setValue(Integer.parseInt(hour));
                mins.getValueFactory().setValue(Integer.parseInt(minute));

                if (apm.equals(am.getText()))
                    am.setSelected(true);
                else if (apm.equals(pm.getText()))
                    pm.setSelected(true);

                subject.setDisable(true);
                add.setText("Update task");
                pendingUpdateKey = item.subject;
            }
        }
        if (!found) {
            info1.setText("");
            info.setText("Task not found");
        }
    }

    /** Delete by subject from the search box. */
    public void deleteTask() {
        boolean found = false;
        info1.setText("");
        String key = enter.getText();
        enter.setText("");

        iterator = items.listIterator();
        while (iterator.hasNext()) {
            ToDoList item = iterator.next();
            if (item.subject.equalsIgnoreCase(key)) {
                show.getItems().clear();
                found = true;
                iterator.remove();
                info.setText("Task deleted");
            }
        }
        if (!found) {
            info1.setText("");
            info.setText("Task not found");
        }

        repository.save(items);
        LV.getItems().setAll(items);
        if (LV.getItems().isEmpty()) {
            info2.setText("Your To-Do List appears here");
        }
    }

    /** Open the email dialog and pass a snapshot of the current items. */
    public void openEmailDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
            Parent root = loader.load();

            SecondaryController controller = loader.getController();
            controller.setItems(new ArrayList<>(items)); // inject live snapshot

            Stage emailStage = new Stage();
            emailStage.setTitle("Email To-Do List");
            emailStage.initOwner(add.getScene().getWindow());
            emailStage.initModality(Modality.WINDOW_MODAL);
            emailStage.setScene(new Scene(root, 300, 200));
            emailStage.setResizable(false);
            emailStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helpers
    private void clearEditor(RadioButton selectedToggle) {
        subject.setPromptText("");
        description.setPromptText("");
        location.setPromptText("");

        subject.clear();
        description.clear();
        location.clear();

        date.setValue(null);
        hr.getValueFactory().setValue(10);
        mins.getValueFactory().setValue(10);
        if (selectedToggle != null)
            selectedToggle.setSelected(false);
        subject.setDisable(false);
    }
}