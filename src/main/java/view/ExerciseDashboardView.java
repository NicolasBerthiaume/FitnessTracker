package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ExerciseDataManager;
import model.ExerciseEntry;

import java.time.LocalDate;

public class ExerciseDashboardView extends BorderPane {
    private final ExerciseDataManager manager;

    public ExerciseDashboardView() {
        this.manager = new ExerciseDataManager(LocalDate.now().minusDays(30));

        // Table view for displaying entries
        ExerciseTableView tableView = new ExerciseTableView(manager);

        // Controls for input
        DatePicker datePicker = new DatePicker(LocalDate.now());

        //exercise dropdown
        ComboBox<String> exerciseNameDropdown = new ComboBox<>();
        exerciseNameDropdown.getItems().addAll(manager.getUniqueExerciseNames());
        exerciseNameDropdown.setEditable(false);

        Button createExerciseButton = new Button("New Exercise");

        //button for creating new exercise names
        createExerciseButton.setOnAction(ev -> {
            Stage dialog = new Stage();
            dialog.setTitle("New Exercise");

            TextField newExerciseField = new TextField();
            Button saveButton = new Button("Save");

            saveButton.setOnAction(ev2 -> {
                String newExercise = newExerciseField.getText().trim();
                if (!newExercise.isEmpty() && !exerciseNameDropdown.getItems().contains(newExercise)) {
                    exerciseNameDropdown.getItems().add(newExercise);
                    exerciseNameDropdown.setValue(newExercise);
                }
                dialog.close();
            });

            VBox dialogLayout = new VBox(10, new Label("Exercise Name:"), newExerciseField, saveButton);
            dialogLayout.setPadding(new Insets(10));
            dialog.setScene(new Scene(dialogLayout));
            dialog.show();
        });

        Spinner<Integer> setSpinner = new Spinner<>(1, 100, 1);
        Spinner<Integer> repsSpinner = new Spinner<>(1, 100, 10);
        Spinner<Double> weightSpinner = new Spinner<>(0.0, 1000.0, 50.0, 5.0);
        weightSpinner.setEditable(true);

        Button addButton = new Button("Add Exercise");

        // Add button handler
        addButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            String name = exerciseNameDropdown.getValue();
            int set = setSpinner.getValue();
            int reps = repsSpinner.getValue();
            double weight = weightSpinner.getValue();

            ExerciseEntry entry = new ExerciseEntry(date, name, set, reps, weight);
            manager.addExerciseEntry(entry);
            tableView.refreshData();
        });

        // Layout
        HBox inputBox = new HBox(10, new Label("Date:"), datePicker,
                new Label("Exercise:"), exerciseNameDropdown, createExerciseButton,
                new Label("Set:"), setSpinner,
                new Label("Reps:"), repsSpinner,
                new Label("Weight:"), weightSpinner,
                addButton);

        VBox mainLayout = new VBox(10, inputBox, tableView);
        mainLayout.setPadding(new Insets(10));

        this.setCenter(mainLayout);
    }
}

