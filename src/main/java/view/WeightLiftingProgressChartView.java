package view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.ExerciseDataManager;
import model.ExerciseEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class WeightLiftingProgressChartView extends VBox {
    private final ExerciseDataManager manager;
    private final LineChart<Number, Number> progressChart;
    private final ComboBox<String> progressDropdown;
    private final Label pbLabel;
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;

    public WeightLiftingProgressChartView(ExerciseDataManager manager) {
        this.manager = manager;

        // Initialize chart axes
        xAxis = new NumberAxis();
        xAxis.setLabel("Date");
        xAxis.setTickLabelRotation(45);
        xAxis.setAutoRanging(false);

        yAxis = new NumberAxis();
        yAxis.setLabel("Weight (lbs)");

        // Initialize chart
        progressChart = new LineChart<>(xAxis, yAxis);
        progressChart.setTitle("Progress Over Time");
        progressChart.setPrefHeight(500);
        progressChart.setMinWidth(600);
        VBox.setVgrow(progressChart, Priority.ALWAYS);

        // Initialize dropdown
        progressDropdown = new ComboBox<>();
        progressDropdown.getItems().addAll(manager.getUniqueExerciseNames());
        progressDropdown.setPromptText("Select exercise for progress");

        // Set the first exercise as default if available
        if (!progressDropdown.getItems().isEmpty()) {
            progressDropdown.setValue(progressDropdown.getItems().get(0));
        }

        // Initialize PB label
        pbLabel = new Label("PB: -");

        // Set up event handler
        progressDropdown.setOnAction(this::handleExerciseSelection);

        // Layout controls
        HBox progressControls = new HBox(10, new Label("Progress:"), progressDropdown, pbLabel);
        progressControls.setPadding(new Insets(10));

        // Add components to this VBox
        VBox chartContainer = new VBox(progressChart);
        chartContainer.setPadding(new Insets(15));
        chartContainer.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );

        this.getChildren().addAll(progressControls, chartContainer);
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        // Trigger the initial load if there's a default selection
        if (progressDropdown.getValue() != null) {
            progressDropdown.fireEvent(new ActionEvent());
        }
    }

    private void handleExerciseSelection(ActionEvent event) {
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
    }

    public void refreshExerciseNames() {
        String currentSelection = progressDropdown.getValue();
        progressDropdown.getItems().clear();
        progressDropdown.getItems().addAll(manager.getUniqueExerciseNames());

        // Try to restore previous selection if it still exists
        if (currentSelection != null && progressDropdown.getItems().contains(currentSelection)) {
            progressDropdown.setValue(currentSelection);
        } else if (!progressDropdown.getItems().isEmpty()) {
            progressDropdown.setValue(progressDropdown.getItems().get(0));
            progressDropdown.fireEvent(new ActionEvent());
        }
    }

    public void refreshChart() {
        if (progressDropdown.getValue() != null) {
            handleExerciseSelection(new ActionEvent());
        }
    }
}