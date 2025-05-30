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

public class WeightLiftingDashboardView extends BorderPane {
    private final ExerciseDataManager manager;
    private final WeightLiftingProgressChartView progressChartView;

    public WeightLiftingDashboardView() {
        this.manager = new ExerciseDataManager(LocalDate.now().minusDays(30));

        // Table view for displaying entries
        WeightLiftingTableView tableView = new WeightLiftingTableView(manager);

        // Progress chart view
        progressChartView = new WeightLiftingProgressChartView(manager);

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
                    // Update progress chart dropdown as well
                    progressChartView.refreshExerciseNames();
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

        TextField notesField = new TextField();
        notesField.setPromptText("Notes (optional)");

        // Add button handler
        addButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            String name = exerciseNameDropdown.getValue();
            int set = setSpinner.getValue();
            int reps = repsSpinner.getValue();
            double weight = weightSpinner.getValue();

            String notes = notesField.getText().trim();
            ExerciseEntry entry = new ExerciseEntry(date, name, set, reps, weight, notes);
            manager.addExerciseEntry(entry);
            tableView.refreshData();
            progressChartView.refreshChart(); // Refresh progress chart when new data is added
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

        VBox inputBox = new VBox(10, topRow, bottomRow, new Label("Notes:"), notesField);
        inputBox.setPadding(new Insets(10));

        //put it all together
        VBox mainLayout = new VBox(10, inputBox, tableView, progressChartView);

        mainLayout.setPadding(new Insets(10));

        this.setCenter(mainLayout);
    }

    private void showBulkAddDialog(WeightLiftingTableView tableView) {
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

        // Add first set
        addSetRow(setsBox, true); // No remove button for first set

        Button addSetButton = new Button("Add Set");
        addSetButton.setOnAction(e -> {
            addSetRow(setsBox, false); // Add set with remove button
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
                    TextField notesField = (TextField) data[3];

                    ExerciseEntry entry = new ExerciseEntry(
                            date,
                            exerciseName,
                            set,
                            repsSpinner.getValue(),
                            weightSpinner.getValue(),
                            notesField.getText().trim()
                    );

                    manager.addExerciseEntry(entry);
                }
            }

            tableView.refreshData();
            progressChartView.refreshChart(); // Refresh progress chart when bulk data is added
            dialog.close(); // close the pop-up
        });

        layout.getChildren().addAll(new Label("Date:"), datePicker,
                new Label("Exercise:"), exerciseDropdown, setsBox,
                addSetButton, saveButton);

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.show();
    }

    private void addSetRow(VBox container, boolean isFirstSet) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        // Set number will be determined by renumberSets()
        Label setLabel = new Label("Set #1");
        Spinner<Integer> repsSpinner = new Spinner<>(1, 100, 10);
        Spinner<Double> weightSpinner = new Spinner<>(0, 500, 50, 2.5);
        TextField notesField = new TextField();
        notesField.setPromptText("Notes");
        weightSpinner.setEditable(true);
        repsSpinner.setEditable(true);

        row.getChildren().addAll(setLabel, new Label("Reps:"), repsSpinner, new Label("Weight (kg):"), weightSpinner, new Label("Notes:"), notesField);

        if (!isFirstSet) {
            Button removeButton = new Button("❌ Remove");
            removeButton.setOnAction(e -> {
                container.getChildren().remove(row);
                renumberSets(container); // Renumber after removal
            });
            row.getChildren().add(removeButton);
        }

        // Store components for saving - set number will be updated by renumberSets
        row.setUserData(new Object[]{1, repsSpinner, weightSpinner, notesField});

        container.getChildren().add(row);
        renumberSets(container); // Renumber after addition
    }

    private void renumberSets(VBox container) {
        int setNumber = 1;
        for (Node node : container.getChildren()) {
            if (node instanceof HBox row) {
                // Update the label
                Label setLabel = (Label) row.getChildren().get(0);
                setLabel.setText("Set #" + setNumber);

                // Update the stored data
                Object[] data = (Object[]) row.getUserData();
                data[0] = setNumber; // Update set number
                row.setUserData(data);

                setNumber++;
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}