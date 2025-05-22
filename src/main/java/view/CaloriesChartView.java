package view;

import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import model.FitnessDataManager;
import model.FitnessEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CaloriesChartView extends VBox {
    private final LineChart<String, Number> caloriesChart;
    private final XYChart.Series<String, Number> caloriesSeries;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

    public CaloriesChartView(FitnessDataManager fitnessDataManager) {
        CategoryAxis xAxis = new CategoryAxis();
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
                String label = date.format(dateFormatter);
                caloriesSeries.getData().add(new XYChart.Data<>(label, entry.getCalories()));
            }
        }
    }
}
