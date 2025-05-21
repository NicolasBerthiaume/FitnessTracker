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
        weightInput.setPromptText("Enter today's weight");
        Button weightButton = new Button("Add weight");

        TextField caloriesInput = new TextField();
        caloriesInput.setPromptText("Enter today's calories");
        Button caloriesButton = new Button("Add calories");

        weightButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightInput.getText());
                manager.addWeight(LocalDate.now(), weight);
                weightChart.updateWeightChart(manager);
                weightInput.clear();
            } catch (NumberFormatException ex) {
                weightInput.setText("Invalid input");
            }
        });

        caloriesButton.setOnAction(e -> {
            try {
                int cals = Integer.parseInt(caloriesInput.getText());
                manager.addCalories(LocalDate.now(), cals);
                caloriesChart.updateCaloriesChart(manager);
                caloriesInput.clear();
            } catch (NumberFormatException ex) {
                caloriesInput.setText("Invalid input");
            }
        });

        HBox weightControls = new HBox(10, new Label("Weight:"), weightInput, weightButton);
        HBox caloriesControls = new HBox(10, new Label("Calories:"), caloriesInput, caloriesButton);

        VBox inputBox = new VBox(10, weightControls, caloriesControls);
        inputBox.setPadding(new Insets(10));

        VBox charts = new VBox(20, weightChart, caloriesChart);
        charts.setPadding(new Insets(10));

        this.setCenter(charts);
        this.setBottom(inputBox);
    }
}
