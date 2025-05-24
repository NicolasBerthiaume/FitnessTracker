package view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import model.FitnessDataManager;
import model.FitnessEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CaloriesChartView extends VBox {
    private final BarChart<String, Number> caloriesChart;
    private final XYChart.Series<String, Number> breakfastSeries;
    private final XYChart.Series<String, Number> lunchSeries;
    private final XYChart.Series<String, Number> dinnerSeries;
    private final XYChart.Series<String, Number> totalSeries;
    private final CategoryAxis xAxis;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

    public CaloriesChartView(FitnessDataManager fitnessDataManager) {
        xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Calories");

        caloriesChart = new BarChart<>(xAxis, yAxis);
        caloriesChart.setAnimated(false);
        caloriesChart.setTitle("Daily Calorie Intake by Meal");

        breakfastSeries = new XYChart.Series<>();
        breakfastSeries.setName("Breakfast");

        lunchSeries = new XYChart.Series<>();
        lunchSeries.setName("Lunch");

        dinnerSeries = new XYChart.Series<>();
        dinnerSeries.setName("Dinner");

        totalSeries = new XYChart.Series<>();
        totalSeries.setName("Total");

        caloriesChart.getData().addAll(breakfastSeries, lunchSeries, dinnerSeries, totalSeries);
        this.getChildren().add(caloriesChart);

        updateCaloriesChart(fitnessDataManager, 30);
    }

    public void updateCaloriesChart(FitnessDataManager fitnessDataManager, int daysToShow) {
        LocalDate startDate = LocalDate.now().minusDays(daysToShow);
        Map<LocalDate, FitnessEntry> data = fitnessDataManager.getAllFitnessData();

        breakfastSeries.getData().clear();
        lunchSeries.getData().clear();
        dinnerSeries.getData().clear();
        totalSeries.getData().clear();
        xAxis.setCategories(FXCollections.observableArrayList());

        for (LocalDate date : data.keySet()) {
            if (date.isBefore(startDate)) continue;

            FitnessEntry entry = data.get(date);
            String label = date.format(dateFormatter);
            xAxis.getCategories().add(label);

            Integer b = entry.getBreakfastCalories();
            Integer l = entry.getLunchCalories();
            Integer d = entry.getDinnerCalories();

            int total = 0;
            if (b != null) {
                breakfastSeries.getData().add(new XYChart.Data<>(label, b));
                total += b;
            }
            if (l != null) {
                lunchSeries.getData().add(new XYChart.Data<>(label, l));
                total += l;
            }
            if (d != null) {
                dinnerSeries.getData().add(new XYChart.Data<>(label, d));
                total += d;
            }
            if (total > 0) {
                totalSeries.getData().add(new XYChart.Data<>(label, total));
            }
        }

        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : caloriesChart.getData()) {
                for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                    Node node = dataPoint.getNode();
                    if (node != null) {
                        String tooltipText = series.getName() + " on " + dataPoint.getXValue() + ": " + dataPoint.getYValue() + " cal";
                        Tooltip tooltip = new Tooltip(tooltipText);
                        Tooltip.install(node, tooltip);
                    }
                }
            }
        });
    }
}
