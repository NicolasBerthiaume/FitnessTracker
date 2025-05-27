package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

        // Controls for date input
        DatePicker datePicker = new DatePicker(LocalDate.now());

        //exercise dropdown
        ComboBox<String> exerciseNameDropdown = new ComboBox<>();
        exerciseNameDropdown.setPromptText("Select exercise");
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

        //add single exercise
        Button addButton = new Button("Add Exercise");

        //button to add multiple sets at a time
        Button bulkAddButton = new Button("Bulk Add");
        bulkAddButton.setOnAction(e -> showBulkAddDialog(tableView));

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
        HBox topRow = new HBox(10,
                new Label("Date:"), datePicker,
                new Label("Exercise:"), exerciseNameDropdown, createExerciseButton
        );

        HBox bottomRow = new HBox(10,
                new Label("Set:"), setSpinner,
                new Label("Reps:"), repsSpinner,
                new Label("Weight:"), weightSpinner,
                addButton,
                bulkAddButton
        );

        topRow.setPadding(new Insets(5));
        bottomRow.setPadding(new Insets(5));

        VBox inputBox = new VBox(10, topRow, bottomRow);
        inputBox.setPadding(new Insets(10));

        VBox mainLayout = new VBox(10, inputBox, tableView);
        mainLayout.setPadding(new Insets(10));

        this.setCenter(mainLayout);
    }

    private void showBulkAddDialog(ExerciseTableView tableView) {
        Stage dialog = new Stage();
        dialog.setWidth(700);
        dialog.setHeight(700);
        dialog.setTitle("Bulk Add Exercise Sets");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<String> exerciseDropdown = new ComboBox<>();
        exerciseDropdown.getItems().addAll(manager.getUniqueExerciseNames());
        exerciseDropdown.setPromptText("Select exercise");

        VBox setsBox = new VBox(10);  // This will hold all the sets
        int[] setCounter = {1}; // mutable container to track next set number

        // Add first set
        addSetRow(setsBox, setCounter[0]++, true, null, null); // No remove button, no callback needed

        Button addSetButton = new Button("Add Set");
        addSetButton.setOnAction(e -> {
            addSetRow(setsBox, setCounter[0]++, false, exerciseDropdown.getValue(), () -> {
                if (setsBox.getChildren().isEmpty()) {
                    exerciseDropdown.setDisable(false);
                    setCounter[0] = 1; // Reset counter
                }
            });
        });

        Button saveButton = new Button("Save All");
        saveButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            String exerciseName = exerciseDropdown.getValue();
            if (exerciseName == null || date == null) {
                showAlert("Please select a date and exercise name.");
                return;
            }

            for (Node node : setsBox.getChildren()) {
                if (node instanceof HBox row) {
                    Object[] data = (Object[]) row.getUserData();
                    int set = (int) data[0];
                    Spinner<Integer> repsSpinner = (Spinner<Integer>) data[1];
                    Spinner<Double> weightSpinner = (Spinner<Double>) data[2];

                    ExerciseEntry entry = new ExerciseEntry(
                            date,
                            exerciseName,
                            set,
                            repsSpinner.getValue(),
                            weightSpinner.getValue()
                    );

                    manager.addExerciseEntry(entry);
                }
            }

            tableView.refreshData();
            dialog.close(); // close the pop-up
        });

        layout.getChildren().addAll(new Label("Date:"), datePicker,
                new Label("Exercise:"), exerciseDropdown, setsBox,
                addSetButton, saveButton);

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.show();
    }

    private void addSetRow(VBox container, int setNumber, boolean isFirstSet, String exerciseName, Runnable onRemoveCallback) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label setLabel = new Label("Set #" + setNumber);
        Spinner<Integer> repsSpinner = new Spinner<>(1, 100, 10);
        Spinner<Double> weightSpinner = new Spinner<>(0, 500, 50, 2.5);
        weightSpinner.setEditable(true);
        repsSpinner.setEditable(true);

        row.getChildren().addAll(setLabel, new Label("Reps:"), repsSpinner, new Label("Weight (kg):"), weightSpinner);

        if (!isFirstSet) {
            Button removeButton = new Button("❌ Remove");
            removeButton.setOnAction(e -> {
                container.getChildren().remove(row);
                if (onRemoveCallback != null) onRemoveCallback.run();
            });
            row.getChildren().add(removeButton);
        }

        // Store only relevant components when saving
        row.setUserData(new Object[]{setNumber, repsSpinner, weightSpinner});

        container.getChildren().add(row);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

