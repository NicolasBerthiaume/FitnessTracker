package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class NewExerciseDialog {
    private final Stage dialog;
    private final Consumer<String> onExerciseCreated;

    public NewExerciseDialog(Consumer<String> onExerciseCreated) {
        this.onExerciseCreated = onExerciseCreated;
        this.dialog = new Stage();
        setupUI();
    }

    private void setupUI() {
        dialog.setTitle("New Exercise");

        TextField newExerciseField = new TextField();
        Button saveButton = new Button("Save");

        saveButton.setOnAction(ev -> {
            String newExercise = newExerciseField.getText().trim();
            if (!newExercise.isEmpty()) {
                onExerciseCreated.accept(newExercise);
                dialog.close();
            }
        });

        VBox dialogLayout = new VBox(10,
                new Label("Exercise Name:"),
                newExerciseField,
                saveButton
        );
        dialogLayout.setPadding(new Insets(10));
        dialog.setScene(new Scene(dialogLayout));
    }

    public void show() {
        dialog.show();
    }
}