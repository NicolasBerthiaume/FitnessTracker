package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
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

        var inputBox = new VBox(10, weightInputPanel, caloriesInputPanel);
        inputBox.setPadding(new Insets(10));

        var charts = new VBox(20, weightChart, caloriesChart);
        charts.setPadding(new Insets(10));

        this.setCenter(charts);
        this.setBottom(inputBox);
    }
}
