package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import model.NutritionDataManager;

import java.time.LocalDate;

public class NutritionDashboardView extends BorderPane {
    private final NutritionDataManager manager;
    private int currentDaysRange = 30;

    public NutritionDashboardView() {
        this.manager = new NutritionDataManager(LocalDate.now().minusDays(30));

        // Charts
        WeightChartView weightChart = new WeightChartView(manager);
        CaloriesChartView caloriesChart = new CaloriesChartView(manager);
        weightChart.updateWeightChart();
        caloriesChart.updateCaloriesChart();

        // Weight controls
        TextField weightInput = new TextField();
        weightInput.setPromptText("Enter weight");
        DatePicker weightDatePicker = new DatePicker(LocalDate.now());
        Button weightButton = new Button("Add weight");

        // Calories controls
        TextField caloriesInput = new TextField();
        caloriesInput.setPromptText("Enter calories");
        DatePicker caloriesDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> mealTypeDropdown = new ComboBox<>();
        mealTypeDropdown.getItems().addAll("Breakfast", "Lunch", "Dinner", "Snack");
        mealTypeDropdown.setValue("Breakfast");
        Button caloriesButton = new Button("Add calories");

        // Weight button event
        weightButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightInput.getText());
                LocalDate selectedDate = weightDatePicker.getValue();
                manager.addWeight(selectedDate, weight);
                weightChart.updateWeightChart();
                weightInput.clear();
            } catch (NumberFormatException ex) {
                weightInput.setText("Invalid input");
            }
        });

        // Calories button event
        caloriesButton.setOnAction(e -> {
            try {
                int cals = Integer.parseInt(caloriesInput.getText());
                LocalDate selectedDate = caloriesDatePicker.getValue();
                String mealType = mealTypeDropdown.getValue();

                switch (mealType) {
                    case "Breakfast" -> manager.addBreakfastCalories(selectedDate, cals);
                    case "Lunch"     -> manager.addLunchCalories(selectedDate, cals);
                    case "Dinner"    -> manager.addDinnerCalories(selectedDate, cals);
                    case "Snack"     -> manager.addSnackCalories(selectedDate, cals);
                }

                caloriesChart.updateCaloriesChart();
                caloriesInput.clear();
            } catch (NumberFormatException ex) {
                caloriesInput.setText("Invalid input");
            }
        });

        // UI setup
        HBox weightControls = new HBox(10, new Label("Weight:"), weightInput, weightDatePicker, weightButton);
        HBox caloriesControls = new HBox(10,
                new Label("Calories:"), caloriesInput,
                new Label("for"), mealTypeDropdown,
                caloriesDatePicker, caloriesButton);

        VBox inputBox = new VBox(10, weightControls, caloriesControls);
        inputBox.setPadding(new Insets(10));

        VBox charts = new VBox(20, weightChart, caloriesChart);
        charts.setPadding(new Insets(10));


        this.setCenter(charts);
        this.setBottom(inputBox);
    }
}