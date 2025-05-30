package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;

import javafx.util.StringConverter;
import model.ExerciseDataManager;
import model.ExerciseEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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


        // Progress section
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Date");
        xAxis.setTickLabelRotation(45);
        xAxis.setAutoRanging(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Weight (lbs)");

        LineChart<Number, Number> progressChart = new LineChart<>(xAxis, yAxis);
        progressChart.setTitle("Progress Over Time");
        progressChart.setPrefHeight(500);
        progressChart.setMinWidth(600);
        VBox.setVgrow(progressChart, Priority.ALWAYS);

        ComboBox<String> progressDropdown = new ComboBox<>();
        progressDropdown.getItems().addAll(manager.getUniqueExerciseNames());
        progressDropdown.setPromptText("Select exercise for progress");

        // Set the first exercise as default if available
        if (!progressDropdown.getItems().isEmpty()) {
            progressDropdown.setValue(progressDropdown.getItems().get(0));
        }

        Label pbLabel = new Label("PB: -");

        progressDropdown.setOnAction(ev -> {
            String selectedExercise = progressDropdown.getValue();
            if (selectedExercise == null) return;

            List<ExerciseEntry> heaviestSets = manager.getHeaviestSetPerDay(selectedExercise);

            // Create a mutable copy and sort by date to ensure chronological order
            List<ExerciseEntry> sortedSets = new ArrayList<>(heaviestSets);
            sortedSets.sort((a, b) -> a.getDate().compareTo(b.getDate()));

            // Clear existing data
            progressChart.getData().clear();

            // Create series
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(selectedExercise);

            double minWeight = Double.MAX_VALUE;
            double maxWeight = Double.MIN_VALUE;

            // Convert dates to numbers (days since epoch)
            LocalDate baseDate = LocalDate.of(2020, 1, 1); // Reference date
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;

            for (ExerciseEntry entry : sortedSets) {
                long daysSinceBase = ChronoUnit.DAYS.between(baseDate, entry.getDate());
                double weight = entry.getWeightLoad();

                series.getData().add(new XYChart.Data<>(daysSinceBase, weight));

                if (weight < minWeight) minWeight = weight;
                if (weight > maxWeight) maxWeight = weight;
                if (daysSinceBase < minX) minX = daysSinceBase;
                if (daysSinceBase > maxX) maxX = daysSinceBase;
            }

            if (minWeight == Double.MAX_VALUE || maxWeight == Double.MIN_VALUE) {
                minWeight = 0;
                maxWeight = 100;
            }
            yAxis.setLowerBound(minWeight - 5);
            yAxis.setUpperBound(maxWeight + 5);

            xAxis.setLowerBound(minX - 1);
            xAxis.setUpperBound(maxX + 1);

            Set<Double> uniqueXValues = new TreeSet<>();
            for (XYChart.Data<Number, Number> dataPoint : series.getData()) {
                uniqueXValues.add(dataPoint.getXValue().doubleValue());
            }

            // Calculate tick unit to show every data point
            if (uniqueXValues.size() > 1) {
                List<Double> sortedXValues = new ArrayList<>(uniqueXValues);

                // Find the minimum spacing between consecutive points
                double minSpacing = Double.MAX_VALUE;
                for (int i = 1; i < sortedXValues.size(); i++) {
                    double spacing = sortedXValues.get(i) - sortedXValues.get(i-1);
                    if (spacing < minSpacing) {
                        minSpacing = spacing;
                    }
                }

                // Set tick unit to the minimum spacing (or 1, whichever is smaller)
                xAxis.setTickUnit(Math.min(minSpacing, 1.0));
            } else {
                xAxis.setTickUnit(1.0);
            }


            xAxis.setAutoRanging(false);
            xAxis.setMinorTickVisible(false);


            if (uniqueXValues.size() > 8) {
                progressChart.setMinWidth(Math.max(600, uniqueXValues.size() * 80));
            }

            // Custom tick formatting for dates
            xAxis.setTickLabelFormatter(new StringConverter<Number>() {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

                @Override
                public String toString(Number number) {
                    LocalDate date = baseDate.plusDays(number.longValue());
                    return date.format(formatter);
                }

                @Override
                public Number fromString(String string) {
                    return 0; // Not used for display
                }
            });

            // Add series to chart
            progressChart.getData().add(series);

            // Update PB label
            double pb = sortedSets.stream()
                    .mapToDouble(ExerciseEntry::getWeightLoad)
                    .max()
                    .orElse(0);
            pbLabel.setText(selectedExercise + " PB: " + pb + " kg");
        });

        // Trigger the initial load if there's a default selection
        if (progressDropdown.getValue() != null) {
            progressDropdown.fireEvent(new ActionEvent());
        }

        HBox progressControls = new HBox(10, new Label("Progress:"), progressDropdown, pbLabel);
        progressControls.setPadding(new Insets(10));

        VBox progressSection = new VBox(10, progressControls, progressChart);
        progressSection.setPadding(new Insets(10));


        //put it all together
        VBox mainLayout = new VBox(10, inputBox, tableView, progressSection);

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
        TextField notesField = new TextField();
        notesField.setPromptText("Notes");
        weightSpinner.setEditable(true);
        repsSpinner.setEditable(true);

        row.getChildren().addAll(setLabel, new Label("Reps:"), repsSpinner, new Label("Weight (kg):"), weightSpinner, new Label("Notes:"), notesField);

        if (!isFirstSet) {
            Button removeButton = new Button("❌ Remove");
            removeButton.setOnAction(e -> {
                container.getChildren().remove(row);
                if (onRemoveCallback != null) onRemoveCallback.run();
            });
            row.getChildren().add(removeButton);
        }

        // Store only relevant components when saving
        row.setUserData(new Object[]{setNumber, repsSpinner, weightSpinner, notesField});

        container.getChildren().add(row);
    }

    private void updateProgressChart(String exerciseName, LineChart<String, Number> progressChart, Label pbLabel) {
        List<ExerciseEntry> heaviestSets = manager.getHeaviestSetPerDay(exerciseName);

        // Create a mutable copy and sort by date to ensure chronological order
        List<ExerciseEntry> sortedSets = new ArrayList<>(heaviestSets);
        sortedSets.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        // Clear existing data first
        progressChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(exerciseName);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        List<String> categoriesList = new ArrayList<>();
        double minWeight = Double.MAX_VALUE;
        double maxWeight = Double.MIN_VALUE;

        for (ExerciseEntry entry : sortedSets) {
            String formattedDate = entry.getDate().format(dateFormatter);
            categoriesList.add(formattedDate);

            double weight = entry.getWeightLoad();
            series.getData().add(new XYChart.Data<>(formattedDate, weight));

            if (weight < minWeight) minWeight = weight;
            if (weight > maxWeight) maxWeight = weight;
        }

        // Set categories before adding data
        CategoryAxis xAxis = (CategoryAxis) progressChart.getXAxis();
        ObservableList<String> categories = FXCollections.observableArrayList(categoriesList);
        xAxis.setCategories(categories);

        // Set Y-axis bounds
        if (minWeight == Double.MAX_VALUE || maxWeight == Double.MIN_VALUE) {
            minWeight = 0;
            maxWeight = 100;
        }
        NumberAxis yAxis = (NumberAxis) progressChart.getYAxis();
        yAxis.setLowerBound(minWeight - 5);
        yAxis.setUpperBound(maxWeight + 5);

        progressChart.getData().add(series);

        // Update PB label
        double pb = sortedSets.stream()
                .mapToDouble(ExerciseEntry::getWeightLoad)
                .max()
                .orElse(0);
        pbLabel.setText(exerciseName + " PB: " + pb + " kg");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

