package view;

import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import javafx.application.Platform;
import javafx.scene.Node;

import model.FitnessDataManager;
import model.FitnessEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CaloriesChartView extends VBox {
    private final BarChart<String, Number> caloriesChart;
    private final XYChart.Series<String, Number> caloriesSeries;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

    public CaloriesChartView(FitnessDataManager fitnessDataManager) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Calories");

        caloriesChart = new BarChart<>(xAxis, yAxis);
        caloriesChart.setTitle("Daily Calories Intake");
        caloriesSeries = new XYChart.Series<>();
        caloriesSeries.setName("Calories");
        caloriesChart.getData().add(caloriesSeries);

        this.getChildren().add(caloriesChart);
        updateCaloriesChart(fitnessDataManager);
    }

    public void updateCaloriesChart(FitnessDataManager fitnessDataManager) {
        caloriesSeries.getData().clear();
        Map<LocalDate, FitnessEntry> data = fitnessDataManager.getAllFitnessData();

        for (LocalDate date : data.keySet()) {
            FitnessEntry entry = data.get(date);
            if (entry.getCalories() != null) {
                String label = date.format(dateFormatter);
                caloriesSeries.getData().add(new XYChart.Data<>(label, entry.getCalories()));
            }
        }

        //this shows the specific entries when hovering over them
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> dataPoint : caloriesSeries.getData()) {
                Node node = dataPoint.getNode();
                if (node != null) {
                    String tooltipText = dataPoint.getXValue() + ": " + dataPoint.getYValue() + " cal";
                    Tooltip tooltip = new Tooltip(tooltipText);
                    Tooltip.install(node, tooltip);
                }
            }
        });
    }
}
