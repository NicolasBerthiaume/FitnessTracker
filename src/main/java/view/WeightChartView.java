package view;

import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import model.FitnessDataManager;
import model.FitnessEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class WeightChartView extends VBox {
    private final LineChart<Number, Number> weightChart;
    private final XYChart.Series<Number, Number> weightSeries;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

    public WeightChartView(FitnessDataManager fitnessDataManager) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Weight (kg)");

        weightChart = new LineChart<>(xAxis, yAxis);
        weightChart.setTitle("Daily Weight");
        weightSeries = new XYChart.Series<>();
        weightSeries.setName("Weight");
        weightChart.getData().add(weightSeries);

        this.getChildren().add(weightChart);
        updateWeightChart(fitnessDataManager);
    }

    public void updateWeightChart(FitnessDataManager fitnessDataManager) {
        weightSeries.getData().clear();
        Map<LocalDate, FitnessEntry> data = fitnessDataManager.getAllFitnessData();
        int index = 0;
        for (LocalDate date : data.keySet()) {
            FitnessEntry entry = data.get(date);
            if (entry.getWeight() != null) {
                weightSeries.getData().add(new XYChart.Data<>(index++, entry.getWeight()));
            }
        }
    }
}
