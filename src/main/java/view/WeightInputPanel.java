package view;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.NutritionDataManager;

import java.time.LocalDate;

public class WeightInputPanel extends HBox {
    public WeightInputPanel(NutritionDataManager manager, Runnable updateChartCallback) {
        setSpacing(10);

        Label label = new Label("Weight:");
        TextField weightInput = new TextField();
        weightInput.setPromptText("Enter weight");

        DatePicker weightDatePicker = new DatePicker(LocalDate.now());
        Button addButton = new Button("Add weight");

        addButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightInput.getText());
                LocalDate date = weightDatePicker.getValue();
                manager.addWeight(date, weight);
                updateChartCallback.run();
                weightInput.clear();
            } catch (NumberFormatException ex) {
                weightInput.setText("Invalid input");
            }
        });

        getChildren().addAll(label, weightInput, weightDatePicker, addButton);
    }
}

