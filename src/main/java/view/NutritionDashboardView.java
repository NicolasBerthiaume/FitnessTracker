package view;

import javafx.geometry.Insets;
import javafx.scene.layout.*;

import model.NutritionDataManager;

import java.time.LocalDate;

public class NutritionDashboardView extends BorderPane {
    public NutritionDashboardView() {
        var manager = new NutritionDataManager(LocalDate.now().minusDays(30));

        var weightChart = new WeightChartView(manager);
        var caloriesChart = new CaloriesChartView(manager);
        weightChart.updateWeightChart();
        caloriesChart.updateCaloriesChart();

        var weightInputPanel = new WeightInputPanel(manager, weightChart::updateWeightChart);
        var caloriesInputPanel = new CaloriesInputPanel(manager, caloriesChart::updateCaloriesChart);

        var weightSection = new VBox(10, weightChart, weightInputPanel);
        weightSection.setPadding(new Insets(10, 10, 20, 10));
        var caloriesSection = new VBox(10, caloriesChart, caloriesInputPanel);
        caloriesSection.setPadding(new Insets(20, 10, 10, 10));

        var nutritionSection = new VBox(10, weightSection, caloriesSection);
        nutritionSection.setSpacing(10);
        nutritionSection.setPadding(new Insets(10, 10, 10, 10));
        this.setCenter(nutritionSection);
    }
}