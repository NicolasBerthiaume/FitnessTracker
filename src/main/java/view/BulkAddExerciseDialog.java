package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ExerciseDataManager;
import model.ExerciseEntry;

import java.time.LocalDate;

public class BulkAddExerciseDialog extends Stage {
    private final VBox setsBox = new VBox(10);
    private final ExerciseDataManager manager;
    private final Runnable refreshCallback;

    public BulkAddExerciseDialog(ExerciseDataManager manager, WeightLiftingProgressChartView progressChartView, WeightLiftingTableView tableView, Runnable refreshCallback) {
        this.manager = manager;
        this.refreshCallback = refreshCallback;

        setTitle("Bulk Add Exercise Sets");
        setWidth(800);
        setHeight(600);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<String> exerciseDropdown = new ComboBox<>();
        exerciseDropdown.getItems().addAll(manager.getUniqueExerciseNames());
        exerciseDropdown.setPromptText("Select exercise");

        addSetRow(true);

        Button addSetButton = new Button("Add Set");
        addSetButton.setOnAction(e -> addSetRow(false));

        Button saveAllButton = new Button("Save All");
        saveAllButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            String exercise = exerciseDropdown.getValue();
            if (date == null || exercise == null || exercise.isEmpty()) {
                showAlert("Please select a date and exercise name.");
                return;
            }

            for (int i = 0; i < setsBox.getChildren().size(); i++) {
                HBox row = (HBox) setsBox.getChildren().get(i);
                WeightLiftingInputPanel inputPane = (WeightLiftingInputPanel) row.getChildren().get(1);
                int setNumber = i + 1;
                ExerciseEntry entry = new ExerciseEntry(
                        date,
                        exercise,
                        setNumber,
                        inputPane.getReps(),
                        inputPane.getWeight(),
                        inputPane.getNotes()
                );
                manager.addExerciseEntry(entry);
            }

            refreshCallback.run();
            close();
        });

        layout.getChildren().addAll(new Label("Date:"), datePicker, new Label("Exercise:"), exerciseDropdown, setsBox, addSetButton, saveAllButton);
        setScene(new Scene(layout));
    }

    private void addSetRow(boolean isFirst) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label setLabel = new Label("Set #" + (setsBox.getChildren().size() + 1));
        WeightLiftingInputPanel inputPane = new WeightLiftingInputPanel();

        row.getChildren().addAll(setLabel, inputPane);

        if (!isFirst) {
            Button removeBtn = new Button("❌");
            removeBtn.setOnAction(e -> {
                setsBox.getChildren().remove(row);
                renumberSets();
            });
            row.getChildren().add(removeBtn);
        }

        setsBox.getChildren().add(row);
    }

    private void renumberSets() {
        for (int i = 0; i < setsBox.getChildren().size(); i++) {
            HBox row = (HBox) setsBox.getChildren().get(i);
            Label setLabel = (Label) row.getChildren().get(0);
            setLabel.setText("Set #" + (i + 1));
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}