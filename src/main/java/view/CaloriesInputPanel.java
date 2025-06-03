package view;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.NutritionDataManager;

import java.time.LocalDate;

public class CaloriesInputPanel extends HBox {
    public CaloriesInputPanel(NutritionDataManager manager, Runnable updateChartCallback) {
        setSpacing(10);

        Label label = new Label("Calories:");
        TextField caloriesInput = new TextField();
        caloriesInput.setPromptText("Enter calories");

        Label forLabel = new Label("for");

        ComboBox<String> mealTypeDropdown = new ComboBox<>();
        mealTypeDropdown.getItems().addAll("Breakfast", "Lunch", "Dinner", "Snack");
        mealTypeDropdown.setValue("Breakfast");

        DatePicker caloriesDatePicker = new DatePicker(LocalDate.now());
        Button addButton = new Button("Add calories");

        addButton.setOnAction(e -> {
            try {
                int calories = Integer.parseInt(caloriesInput.getText());
                LocalDate date = caloriesDatePicker.getValue();
                String mealType = mealTypeDropdown.getValue();

                switch (mealType) {
                    case "Breakfast" -> manager.addBreakfastCalories(date, calories);
                    case "Lunch"     -> manager.addLunchCalories(date, calories);
                    case "Dinner"    -> manager.addDinnerCalories(date, calories);
                    case "Snack"     -> manager.addSnackCalories(date, calories);
                }

                updateChartCallback.run();
                caloriesInput.clear();
            } catch (NumberFormatException ex) {
                caloriesInput.setText("Invalid input");
            }
        });

        getChildren().addAll(label, caloriesInput, forLabel, mealTypeDropdown, caloriesDatePicker, addButton);
    }
}
