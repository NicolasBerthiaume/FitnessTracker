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

public class WeightChartView extends VBox {
    private final LineChart<String, Number> weightChart;
    private final XYChart.Series<String, Number> weightSeries;
    private final CategoryAxis xAxis;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

    public WeightChartView(FitnessDataManager fitnessDataManager) {
        xAxis = new CategoryAxis();

        //sets the maximum of the y-axis to the highest recorded entry +1 (fallback 100)
        //and the mininum of the y-axis to the lower recorded entry -1 (fallback 60)
        NumberAxis yAxis;
        double minWeight = fitnessDataManager.getAllFitnessData().values().stream()
                .filter(e -> e.getWeight() != null)
                .mapToDouble(FitnessEntry::getWeight)
                .min().orElse(60); // fallback min

        double maxWeight = fitnessDataManager.getAllFitnessData().values().stream()
                .filter(e -> e.getWeight() != null)
                .mapToDouble(FitnessEntry::getWeight)
                .max().orElse(100); // fallback max

        yAxis = new NumberAxis(minWeight - 1, maxWeight + 1, 1);
        yAxis.setAutoRanging(false);

        xAxis.setLabel("Day");
        yAxis.setLabel("Weight (kg)");

        weightChart = new LineChart<>(xAxis, yAxis);
        weightChart.setAnimated(false);
        weightChart.setTitle("Daily Weight-ins");
        weightSeries = new XYChart.Series<>();
        weightSeries.setName("Weight");
        weightChart.getData().add(weightSeries);

        this.getChildren().add(weightChart);
        updateWeightChart(fitnessDataManager, 30);
    }

    public void updateWeightChart(FitnessDataManager fitnessDataManager, int daysToShow) {
        LocalDate startDate = LocalDate.now().minusDays(daysToShow);
        Map<LocalDate, FitnessEntry> data = fitnessDataManager.getAllFitnessData();

        weightSeries.getData().clear();

        // this line and the one in the loop ensures that
        // the date labels get updated correctly when adding new data in the app
        xAxis.setCategories(FXCollections.observableArrayList());

        for (LocalDate date : data.keySet()) {
            if (date.isBefore(startDate)) { continue; }
            FitnessEntry entry = data.get(date);
            if (entry.getWeight() != null) {
                String label = date.format(dateFormatter);
                xAxis.getCategories().add(label);
                weightSeries.getData().add(new XYChart.Data<>(label, entry.getWeight()));
            }
        }

        //this shows the specific entries when hovering over them
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> dataPoint : weightSeries.getData()) {
                Node node = dataPoint.getNode();
                if (node != null) {
                    String tooltipText = dataPoint.getXValue() + ": " + dataPoint.getYValue() + " kg";
                    Tooltip tooltip = new Tooltip(tooltipText);
                    Tooltip.install(node, tooltip);
                }
            }
        });
    }
}
