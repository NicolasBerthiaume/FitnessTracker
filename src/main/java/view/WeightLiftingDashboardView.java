package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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
        progressChartView.setPadding(new Insets(20, 10, 10, 10));

        // Controls for date and exercise
        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<String> exerciseDropdown = new ComboBox<>();
        exerciseDropdown.setPromptText("Select exercise");
        exerciseDropdown.getItems().addAll(manager.getUniqueExerciseNames());

        Button createExerciseButton = new Button("New Exercise");
        createExerciseButton.setOnAction(ev -> {
            new NewExerciseDialog(newExercise -> {
                if (!exerciseDropdown.getItems().contains(newExercise)) {
                    exerciseDropdown.getItems().add(newExercise);
                }
                exerciseDropdown.setValue(newExercise);
                progressChartView.refreshExerciseNames();
            }).show();
        });

        // Input pane for weight lifting entries
        WeightLiftingInputPanel weightLiftingInputPanel = new WeightLiftingInputPanel();

        // Add exercise button
        Button addButton = new Button("Add Exercise");
        addButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            String exerciseName = exerciseDropdown.getValue();
            ExerciseEntry entry = weightLiftingInputPanel.getEntry(date, exerciseName);
            if (entry != null) {
                manager.addExerciseEntry(entry);
                tableView.refreshData();
                progressChartView.refreshChart();
                weightLiftingInputPanel.clearInputs();
            } else {
                showAlert("Please complete all fields before adding.");
            }
        });

        // Bulk add button
        Button bulkAddButton = new Button("Bulk Add");
        bulkAddButton.setOnAction(e -> {
            new BulkAddExerciseDialog(manager, progressChartView, tableView, () -> {
                tableView.refreshData();
                progressChartView.refreshChart();
            }).show();
        });

        // Layout
        HBox topRow = new HBox(10,
                new Label("Date:"), datePicker,
                new Label("Exercise:"), exerciseDropdown,
                createExerciseButton
        );
        topRow.setPadding(new Insets(5));

        HBox bottomRow = new HBox(10, addButton, bulkAddButton);
        bottomRow.setPadding(new Insets(5));

        VBox inputBox = new VBox(10, topRow, weightLiftingInputPanel, bottomRow);
        inputBox.setPadding(new Insets(10));

        VBox weightLiftingTableAndInput = new VBox(10, tableView, inputBox);
        weightLiftingTableAndInput.setPadding(new Insets(10, 10, 20, 10));

        VBox mainLayout = new VBox(10, weightLiftingTableAndInput, progressChartView);
        mainLayout.setPadding(new Insets(10));

        this.setCenter(mainLayout);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}