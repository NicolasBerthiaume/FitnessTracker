package view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.NutritionDataManager;
import model.NutritionEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class WeightChartView extends VBox {
    private final LineChart<String, Number> weightChart;
    private final XYChart.Series<String, Number> weightSeries;
    private final CategoryAxis xAxis;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private final ComboBox<String> dateRangeDropdown;
    private final NutritionDataManager fitnessDataManager;

    public WeightChartView(NutritionDataManager fitnessDataManager) {
        this.fitnessDataManager = fitnessDataManager;

        xAxis = new CategoryAxis();

        //sets the maximum of the y-axis to the highest recorded entry +1 (fallback 100)
        //and the mininum of the y-axis to the lower recorded entry -1 (fallback 60)
        NumberAxis yAxis;
        double minWeight = fitnessDataManager.getAllFitnessData().values().stream()
                .filter(e -> e.getWeight() != null)
                .mapToDouble(NutritionEntry::getWeight)
                .min().orElse(60); // fallback min

        double maxWeight = fitnessDataManager.getAllFitnessData().values().stream()
                .filter(e -> e.getWeight() != null)
                .mapToDouble(NutritionEntry::getWeight)
                .max().orElse(100); // fallback max

        yAxis = new NumberAxis(minWeight - 1, maxWeight + 1, 1);
        yAxis.setAutoRanging(false);

        xAxis.setLabel("Day");
        yAxis.setLabel("Weight (kg)");

        weightChart = new LineChart<>(xAxis, yAxis);
        weightChart.setAnimated(false);
        weightChart.setTitle("Daily Weight-ins");
        weightSeries = new XYChart.Series<>();
        weightSeries.setName("Weight");
        weightChart.getData().add(weightSeries);

        //date range setup
        dateRangeDropdown = new ComboBox<>();
        dateRangeDropdown.getItems().addAll("Last 7 days", "Last 30 days");
        dateRangeDropdown.setValue("Last 30 days");
        dateRangeDropdown.setOnAction(e -> updateWeightChart());

        //set date range UI
        HBox filterBox = new HBox(10, new Label("Show:"), dateRangeDropdown);
        filterBox.setPadding(new Insets(10));

        VBox chartContainer = new VBox(weightChart);
        chartContainer.setPadding(new Insets(15));
        chartContainer.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );

        this.getChildren().addAll(filterBox, chartContainer);
        updateWeightChart();
    }

    public void updateWeightChart() {
        int daysToShow = dateRangeDropdown.getValue().equals("Last 7 days") ? 7 : 30;
        LocalDate startDate = LocalDate.now().minusDays(daysToShow);
        Map<LocalDate, NutritionEntry> data = fitnessDataManager.getAllFitnessData();

        weightSeries.getData().clear();

        // this line and the one in the loop ensures that
        // the date labels get updated correctly when adding new data in the app
        xAxis.setCategories(FXCollections.observableArrayList());

        double minWeight = Double.MAX_VALUE;
        double maxWeight = Double.MIN_VALUE;

        for (LocalDate date : data.keySet()) {
            if (date.isBefore(startDate)) { continue; }
            NutritionEntry entry = data.get(date);
            if (entry.getWeight() != null) {
                double weight = entry.getWeight();
                String label = date.format(dateFormatter);
                xAxis.getCategories().add(label);
                weightSeries.getData().add(new XYChart.Data<>(label, entry.getWeight()));

                //tracks if new min/max weight has changed
                if (weight < minWeight) minWeight = weight;
                if (weight > maxWeight) maxWeight = weight;
            }
        }

        //fallback in case no data
        if (minWeight == Double.MAX_VALUE || maxWeight == Double.MIN_VALUE) {
            minWeight = 60;
            maxWeight = 100;
        }

        //update new min/max weight values if needed
        ((NumberAxis) weightChart.getYAxis()).setLowerBound(minWeight - 1);
        ((NumberAxis) weightChart.getYAxis()).setUpperBound(maxWeight + 1);

        //this shows the specific entries when hovering over them
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> dataPoint : weightSeries.getData()) {
                Node node = dataPoint.getNode();
                if (node != null) {
                    String tooltipText = weightSeries.getName() + " on " + dataPoint.getXValue() + ": " + dataPoint.getYValue() + " kg";
                    Tooltip tooltip = new Tooltip(tooltipText);
                    Tooltip.install(node, tooltip);
                }
            }
        });
    }
}
