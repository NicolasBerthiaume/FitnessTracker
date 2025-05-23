package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import model.FitnessDataManager;

import java.time.LocalDate;

public class DashboardView extends BorderPane {
    private final FitnessDataManager manager;
    private int currentDaysRange = 30;

    public DashboardView() {
        this.manager = new FitnessDataManager(LocalDate.now().minusDays(30));

        // Range dropdown
        ComboBox<String> dateRangeDropdown = new ComboBox<>();
        dateRangeDropdown.getItems().addAll("Last 7 days", "Last 30 days");
        dateRangeDropdown.setValue("Last 30 days");

        // Charts
        WeightChartView weightChart = new WeightChartView(manager);
        CaloriesChartView caloriesChart = new CaloriesChartView(manager);
        weightChart.updateWeightChart(manager, currentDaysRange);
        caloriesChart.updateCaloriesChart(manager, currentDaysRange);

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

        // Date range dropdown event
        dateRangeDropdown.setOnAction(e -> {
            currentDaysRange = dateRangeDropdown.getValue().equals("Last 7 days") ? 7 : 30;
            weightChart.updateWeightChart(manager, currentDaysRange);
            caloriesChart.updateCaloriesChart(manager, currentDaysRange);
        });

        // Weight button event
        weightButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightInput.getText());
                LocalDate selectedDate = weightDatePicker.getValue();
                manager.addWeight(selectedDate, weight);
                weightChart.updateWeightChart(manager, currentDaysRange);
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

                caloriesChart.updateCaloriesChart(manager, currentDaysRange);
                caloriesInput.clear();
            } catch (NumberFormatException ex) {
                caloriesInput.setText("Invalid input");
            }
        });

        // UI setup
        HBox topBar = new HBox(10, new Label("Date range:"), dateRangeDropdown);
        topBar.setPadding(new Insets(10));

        HBox weightControls = new HBox(10, new Label("Weight:"), weightInput, weightDatePicker, weightButton);
        HBox caloriesControls = new HBox(10,
                new Label("Calories:"), caloriesInput,
                new Label("for"), mealTypeDropdown,
                caloriesDatePicker, caloriesButton);

        VBox inputBox = new VBox(10, weightControls, caloriesControls);
        inputBox.setPadding(new Insets(10));

        VBox charts = new VBox(20, weightChart, caloriesChart);
        charts.setPadding(new Insets(10));

        this.setTop(topBar);
        this.setCenter(charts);
        this.setBottom(inputBox);
    }
}