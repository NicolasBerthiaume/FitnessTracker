package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import model.FitnessDataManager;

import java.time.LocalDate;

public class DashboardView extends BorderPane {
    private final FitnessDataManager manager;

    public DashboardView() {
        this.manager = new FitnessDataManager(LocalDate.now().minusDays(30));

        WeightChartView weightChart = new WeightChartView(manager);
        CaloriesChartView caloriesChart = new CaloriesChartView(manager);

        TextField weightInput = new TextField();
        weightInput.setPromptText("Enter weight");

        DatePicker weightDatePicker = new DatePicker(LocalDate.now());
        Button weightButton = new Button("Add weight");

        TextField caloriesInput = new TextField();
        caloriesInput.setPromptText("Enter calories");

        DatePicker caloriesDatePicker = new DatePicker(LocalDate.now());
        Button caloriesButton = new Button("Add calories");

        weightButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightInput.getText());
                LocalDate selectedDate = weightDatePicker.getValue();
                manager.addWeight(selectedDate, weight);
                weightChart.updateWeightChart(manager);
                weightInput.clear();
            } catch (NumberFormatException ex) {
                weightInput.setText("Invalid input");
            }
        });

        caloriesButton.setOnAction(e -> {
            try {
                int cals = Integer.parseInt(caloriesInput.getText());
                LocalDate selectedDate = caloriesDatePicker.getValue();
                manager.addCalories(selectedDate, cals);
                caloriesChart.updateCaloriesChart(manager);
                caloriesInput.clear();
            } catch (NumberFormatException ex) {
                caloriesInput.setText("Invalid input");
            }
        });

        HBox weightControls = new HBox(10, new Label("Weight:"), weightInput, weightDatePicker, weightButton);
        HBox caloriesControls = new HBox(10, new Label("Calories:"), caloriesInput, caloriesDatePicker, caloriesButton);

        VBox inputBox = new VBox(10, weightControls, caloriesControls);
        inputBox.setPadding(new Insets(10));

        VBox charts = new VBox(20, weightChart, caloriesChart);
        charts.setPadding(new Insets(10));

        this.setCenter(charts);
        this.setBottom(inputBox);
    }
}
