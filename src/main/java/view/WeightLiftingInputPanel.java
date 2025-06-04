package view;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import model.ExerciseEntry;

import java.time.LocalDate;

public class WeightLiftingInputPanel extends HBox {
    private final Spinner<Integer> repsSpinner;
    private final Spinner<Double> weightSpinner;
    private final TextField notesField;

    public WeightLiftingInputPanel() {
        this(1, 100, 10, 0, 500, 50.0, 2.5);
    }

    public WeightLiftingInputPanel(int minReps, int maxReps, int defaultReps, double minWeight, double maxWeight, double defaultWeight, double step) {
        setSpacing(10);

        repsSpinner = new Spinner<>(minReps, maxReps, defaultReps);
        repsSpinner.setEditable(true);

        weightSpinner = new Spinner<>(minWeight, maxWeight, defaultWeight, step);
        weightSpinner.setEditable(true);

        notesField = new TextField();
        notesField.setPromptText("Notes");

        getChildren().addAll(
                new Label("Reps:"), repsSpinner,
                new Label("Weight:"), weightSpinner,
                new Label("Notes:"), notesField
        );
        HBox.setHgrow(notesField, Priority.ALWAYS);
    }

    public int getReps() {
        return repsSpinner.getValue();
    }

    public double getWeight() {
        return weightSpinner.getValue();
    }

    public String getNotes() {
        return notesField.getText().trim();
    }

    public void setReps(int reps) {
        repsSpinner.getValueFactory().setValue(reps);
    }

    public void setWeight(double weight) {
        weightSpinner.getValueFactory().setValue(weight);
    }

    public void setNotes(String notes) {
        notesField.setText(notes);
    }

    public ExerciseEntry getEntry(LocalDate date, String exerciseName) {
        if (date == null || exerciseName == null || exerciseName.isEmpty()) {
            return null;
        }

        int reps = getReps();
        double weight = getWeight();
        String notes = getNotes();

        // Default to set #1 when adding via single-entry panel
        return new ExerciseEntry(date, exerciseName, 1, reps, weight, notes);
    }

    public void clearInputs() {
        repsSpinner.getValueFactory().setValue(10);     // Default reps
        weightSpinner.getValueFactory().setValue(50.0); // Default weight
        notesField.clear();
    }
}