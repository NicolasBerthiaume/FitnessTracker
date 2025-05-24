package view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.FitnessDataManager;
import model.FitnessEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CaloriesChartView extends VBox {
    private final BarChart<String, Number> caloriesChart;
    private final CategoryAxis xAxis;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private final ToggleGroup viewToggleGroup;
    private boolean showTotalCalories = true;

    public CaloriesChartView(FitnessDataManager fitnessDataManager) {
        xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Calories");

        caloriesChart = new BarChart<>(xAxis, yAxis);
        caloriesChart.setAnimated(false);
        caloriesChart.setTitle("Daily Calorie Intake");

        // UI toggle between meal breakdown and total calories
        viewToggleGroup = new ToggleGroup();
        RadioButton totalToggle = new RadioButton("Total Calories");
        RadioButton breakdownToggle = new RadioButton("Meal Breakdown");
        totalToggle.setToggleGroup(viewToggleGroup);
        breakdownToggle.setToggleGroup(viewToggleGroup);
        totalToggle.setSelected(true);

        HBox toggleBox = new HBox(10, new Label("View:"), totalToggle, breakdownToggle);
        toggleBox.setPadding(new Insets(10));

        viewToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            showTotalCalories = totalToggle.isSelected();
            updateCaloriesChart(fitnessDataManager, 30);
        });

        this.getChildren().addAll(toggleBox, caloriesChart);
        updateCaloriesChart(fitnessDataManager, 30);
    }

    public void updateCaloriesChart(FitnessDataManager fitnessDataManager, int daysToShow) {
        LocalDate startDate = LocalDate.now().minusDays(daysToShow);
        Map<LocalDate, FitnessEntry> data = fitnessDataManager.getAllFitnessData();

        caloriesChart.getData().clear();
        xAxis.setCategories(FXCollections.observableArrayList());

        if (showTotalCalories) {
            XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
            totalSeries.setName("Total Calories");

            for (LocalDate date : data.keySet()) {
                if (date.isBefore(startDate)) continue;
                FitnessEntry entry = data.get(date);
                String label = date.format(dateFormatter);
                xAxis.getCategories().add(label);
                totalSeries.getData().add(new XYChart.Data<>(label, entry.getTotalCalories()));
            }

            caloriesChart.getData().add(totalSeries);
            installTooltips(totalSeries);
        } else {
            XYChart.Series<String, Number> breakfastSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> lunchSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> dinnerSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> snackSeries = new XYChart.Series<>();

            breakfastSeries.setName("Breakfast");
            lunchSeries.setName("Lunch");
            dinnerSeries.setName("Dinner");
            snackSeries.setName("Snack");

            for (LocalDate date : data.keySet()) {
                if (date.isBefore(startDate)) continue;
                FitnessEntry entry = data.get(date);
                String label = date.format(dateFormatter);
                xAxis.getCategories().add(label);

                if (entry.getBreakfastCalories() != null)
                    breakfastSeries.getData().add(new XYChart.Data<>(label, entry.getBreakfastCalories()));
                if (entry.getLunchCalories() != null)
                    lunchSeries.getData().add(new XYChart.Data<>(label, entry.getLunchCalories()));
                if (entry.getDinnerCalories() != null)
                    dinnerSeries.getData().add(new XYChart.Data<>(label, entry.getDinnerCalories()));
                if (entry.getSnackCalories() != null)
                    snackSeries.getData().add(new XYChart.Data<>(label, entry.getSnackCalories()));
            }

            caloriesChart.getData().addAll(breakfastSeries, lunchSeries, dinnerSeries, snackSeries);
            installTooltips(breakfastSeries);
            installTooltips(lunchSeries);
            installTooltips(dinnerSeries);
            installTooltips(snackSeries);
        }
    }

    //good implementation, could be copied for WeightChartView
    //especially if we want to compare multiple data?
    private void installTooltips(XYChart.Series<String, Number> series) {
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                Node node = dataPoint.getNode();
                if (node != null) {
                    String tooltipText = series.getName() + " on " + dataPoint.getXValue() + ": " + dataPoint.getYValue() + " cal";
                    Tooltip tooltip = new Tooltip(tooltipText);
                    Tooltip.install(node, tooltip);
                }
            }
        });
    }
}