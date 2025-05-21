package view;

import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import model.FitnessDataManager;
import model.FitnessEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CaloriesChartView extends VBox {
    private final LineChart<Number, Number> caloriesChart;
    private final XYChart.Series<Number, Number> caloriesSeries;

    public CaloriesChartView(FitnessDataManager fitnessDataManager) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Calories");

        caloriesChart = new LineChart<>(xAxis, yAxis);
        caloriesChart.setTitle("Daily Calories");
        caloriesSeries = new XYChart.Series<>();
        caloriesSeries.setName("Calories");
        caloriesChart.getData().add(caloriesSeries);

        this.getChildren().add(caloriesChart);
        updateCaloriesChart(fitnessDataManager);
    }

    public void updateCaloriesChart(FitnessDataManager fitnessDataManager) {
        caloriesSeries.getData().clear();
        Map<LocalDate, FitnessEntry> data = fitnessDataManager.getAllFitnessData();
        int index = 0;
        for (LocalDate date : data.keySet()) {
            FitnessEntry entry = data.get(date);
            if (entry.getCalories() != null) {
                caloriesSeries.getData().add(new XYChart.Data<>(index++, entry.getCalories()));
            }
        }
    }
}
