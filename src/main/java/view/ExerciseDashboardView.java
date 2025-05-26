package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
        TextField nameInput = new TextField();
        nameInput.setPromptText("Exercise");

        Spinner<Integer> setSpinner = new Spinner<>(1, 100, 1);
        Spinner<Integer> repsSpinner = new Spinner<>(1, 100, 10);
        Spinner<Double> weightSpinner = new Spinner<>(0.0, 1000.0, 50.0, 5.0);
        weightSpinner.setEditable(true);

        Button addButton = new Button("Add Exercise");

        // Add button handler
        addButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            String name = nameInput.getText();
            int set = setSpinner.getValue();
            int reps = repsSpinner.getValue();
            double weight = weightSpinner.getValue();

            ExerciseEntry entry = new ExerciseEntry(date, name, set, reps, weight);
            manager.addExerciseEntry(entry);
            tableView.refreshData();
            nameInput.clear();
        });

        // Layout
        HBox inputBox = new HBox(10, new Label("Date:"), datePicker,
                new Label("Exercise:"), nameInput,
                new Label("Set:"), setSpinner,
                new Label("Reps:"), repsSpinner,
                new Label("Weight:"), weightSpinner,
                addButton);
        inputBox.setPadding(new Insets(10));

        VBox mainLayout = new VBox(10, inputBox, tableView);
        mainLayout.setPadding(new Insets(10));

        this.setCenter(mainLayout);
    }
}

