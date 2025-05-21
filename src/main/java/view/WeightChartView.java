package view;

import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class WeightChartView extends BorderPane {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd");

    private Double weightUpperLimit = 85.0;
    private Double weightLowerLimit = 75.0;
    private Double tickUnit = 1.0;
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis(weightLowerLimit, weightUpperLimit, tickUnit);
    private final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private final Map<Integer, String> dataLabels = new HashMap<>();


}
