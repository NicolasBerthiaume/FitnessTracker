package view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.NutritionDataManager;
import model.NutritionEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CaloriesChartView extends VBox {
    private final BarChart<String, Number> caloriesChart;
    private final CategoryAxis xAxis;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
    private final ToggleGroup viewToggleGroup;
    private boolean showTotalCalories = true;
    private final ComboBox<String> dateRangeDropdown;
    private final NutritionDataManager fitnessDataManager;

    public CaloriesChartView(NutritionDataManager fitnessDataManager) {
        this.fitnessDataManager = fitnessDataManager;

        xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Calories");

        caloriesChart = new BarChart<>(xAxis, yAxis);
        caloriesChart.setAnimated(false);
        caloriesChart.setTitle("Daily Calorie Intake");

        //date range setup
        dateRangeDropdown = new ComboBox<>();
        dateRangeDropdown.getItems().addAll("Last 7 days", "Last 30 days");
        dateRangeDropdown.setValue("Last 30 days");
        dateRangeDropdown.setOnAction(e -> updateCaloriesChart());

        // UI toggle between meal breakdown and total calories setup
        viewToggleGroup = new ToggleGroup();
        RadioButton totalToggle = new RadioButton("Total Calories");
        RadioButton breakdownToggle = new RadioButton("Meal Breakdown");
        totalToggle.setToggleGroup(viewToggleGroup);
        breakdownToggle.setToggleGroup(viewToggleGroup);
        totalToggle.setSelected(true);

        //set date range UI
        HBox filterBox = new HBox(10, new Label("Show:"), dateRangeDropdown);
        filterBox.setPadding(new Insets(10));

        //set calories toggle UI
        HBox toggleBox = new HBox(10, new Label("View:"), totalToggle, breakdownToggle);
        toggleBox.setPadding(new Insets(10));

        viewToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            showTotalCalories = totalToggle.isSelected();
            updateCaloriesChart();
        });

        VBox chartContainer = new VBox(caloriesChart);
        chartContainer.setPadding(new Insets(15));
        chartContainer.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );

        this.getChildren().addAll(filterBox, toggleBox, chartContainer);
        updateCaloriesChart();
    }

    public void updateCaloriesChart() {
        int daysToShow = dateRangeDropdown.getValue().equals("Last 7 days") ? 7 : 30;
        LocalDate startDate = LocalDate.now().minusDays(daysToShow);
        Map<LocalDate, NutritionEntry> data = fitnessDataManager.getAllFitnessData();

        caloriesChart.getData().clear();
        xAxis.setCategories(FXCollections.observableArrayList());

        if (showTotalCalories) {
            XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
            totalSeries.setName("Total Calories");

            for (LocalDate date : data.keySet()) {
                if (date.isBefore(startDate)) continue;
                NutritionEntry entry = data.get(date);
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
                NutritionEntry entry = data.get(date);
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