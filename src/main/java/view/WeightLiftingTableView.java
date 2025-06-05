package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ExerciseEntry;
import model.ExerciseDataManager;

import java.time.LocalDate;
import java.util.List;

public class WeightLiftingTableView extends BorderPane {
    private final TableView<ExerciseEntry> tableView = new TableView<>();
    private final ObservableList<ExerciseEntry> exerciseData = FXCollections.observableArrayList();
    private final ExerciseDataManager exerciseDataManager;

    public WeightLiftingTableView(ExerciseDataManager manager) {
        this.exerciseDataManager = manager;
        this.exerciseData.addAll(manager.getAllExerciseEntries());

        setPadding(new Insets(10));
        createTable();
        //filter functionality still needs tweaking
        setTop(createToolbar());

        VBox tableContainer = new VBox(tableView);
        tableContainer.setPadding(new Insets(15));
        tableContainer.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );

        setCenter(tableContainer);
    }

    private void createTable() {
        TableColumn<ExerciseEntry, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        TableColumn<ExerciseEntry, String> nameCol = new TableColumn<>("Exercise");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().exerciseNameProperty());

        TableColumn<ExerciseEntry, Integer> setCol = new TableColumn<>("Set #");
        setCol.setCellValueFactory(cellData -> cellData.getValue().setNumberProperty().asObject());

        TableColumn<ExerciseEntry, Integer> repsCol = new TableColumn<>("Reps");
        repsCol.setCellValueFactory(cellData -> cellData.getValue().repsProperty().asObject());

        TableColumn<ExerciseEntry, Double> weightCol = new TableColumn<>("Weight (lbs)");
        weightCol.setCellValueFactory(cellData -> cellData.getValue().weightLoadProperty().asObject());

        TableColumn<ExerciseEntry, Double> volumeCol = new TableColumn<>("Volume (lbs)");
        volumeCol.setCellValueFactory(cellData -> cellData.getValue().totalVolumeProperty().asObject());

        TableColumn<ExerciseEntry, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(cellData -> cellData.getValue().notesProperty());

        //actions for each entry
        //supports edit and delete
        TableColumn<ExerciseEntry, Void> actions = new TableColumn<>("Actions");
        actions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button editButton = new Button("Edit");
            private final HBox actionButtons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(e -> {
                    ExerciseEntry entry = getTableView().getItems().get(getIndex());
                    showEditDialog(entry);
                });

                deleteButton.setOnAction(e -> {
                    ExerciseEntry entry = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Deletion");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Delete this entry?");
                    confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            exerciseDataManager.deleteExerciseEntry(entry);
                            exerciseData.remove(entry);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });

        tableView.setItems(exerciseData);
        tableView.getColumns().addAll(dateCol, nameCol, setCol, repsCol, weightCol, volumeCol, notesCol, actions);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private HBox createToolbar() {
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select date");
        ComboBox<String> exerciseDropdown = new ComboBox<>();
        exerciseDropdown.setPromptText("Select exercise");
        exerciseDropdown.getItems().addAll(exerciseDataManager.getUniqueExerciseNames());

        Button filterButton = new Button("Filter");
        Button clearButton = new Button("Clear");

        filterButton.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            String selectedExercise = exerciseDropdown.getValue();

            List<ExerciseEntry> filtered = exerciseDataManager.getFilteredEntries(selectedDate, selectedExercise);
            exerciseData.setAll(filtered);
        });

        clearButton.setOnAction(e -> {
            datePicker.setValue(null);
            exerciseDropdown.setValue(null);
            exerciseData.setAll(exerciseDataManager.getAllExerciseEntries());
        });

        HBox box = new HBox(10,
                new Label("Date:"), datePicker,
                new Label("Exercise:"), exerciseDropdown,
                filterButton, clearButton
        );
        box.setPadding(new Insets(10, 10, 20, 10));
        return box;
    }

    public void refreshData() {
        exerciseData.setAll(exerciseDataManager.getAllExerciseEntries());
    }

    private void showEditDialog(ExerciseEntry entry) {
        Stage dialog = new Stage();
        dialog.setWidth(1000);
        dialog.setTitle("Edit Exercise Entry");

        DatePicker datePicker = new DatePicker(entry.getDate());

        ComboBox<String> exerciseDropdown = new ComboBox<>();
        exerciseDropdown.getItems().addAll(exerciseDataManager.getUniqueExerciseNames());
        exerciseDropdown.setValue(entry.getExerciseName());

        Spinner<Integer> setSpinner = new Spinner<>(1, 100, entry.getSetNumber());
        Spinner<Integer> repsSpinner = new Spinner<>(1, 100, entry.getReps());
        Spinner<Double> weightSpinner = new Spinner<>(0.0, 1000.0, entry.getWeightLoad(), 5.0);
        weightSpinner.getValueFactory().setValue(entry.getWeightLoad());
        weightSpinner.setEditable(true);
        TextArea notesArea = new TextArea(entry.getNotes());
        notesArea.setPromptText("Enter notes...");
        notesArea.setPrefRowCount(3);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(ev -> {
            entry.setDate(datePicker.getValue());
            entry.setExerciseName(exerciseDropdown.getValue());
            entry.setSetNumber(setSpinner.getValue());
            entry.setReps(repsSpinner.getValue());
            entry.setWeightLoad(weightSpinner.getValue());
            entry.setNotes(notesArea.getText());

            exerciseDataManager.updateExerciseEntry(entry);
            refreshData();
            dialog.close();
        });

        HBox dateAndExerciseBox = new HBox(10,
                new VBox(new Label("Date:"), datePicker),
                new VBox(new Label("Exercise:"), exerciseDropdown)
        );

        HBox setRepsWeightBox = new HBox(10,
                new VBox(new Label("Set:"), setSpinner),
                new VBox(new Label("Reps:"), repsSpinner),
                new VBox(new Label("Weight:"), weightSpinner)
        );

        VBox inputFieldsBox = new VBox(10,
                dateAndExerciseBox,
                setRepsWeightBox
        );
        inputFieldsBox.setPadding(new Insets(10));

        VBox notesSectionBox = new VBox(10, new Label("Notes:"), notesArea);

        HBox formContentBox = new HBox(10, inputFieldsBox, notesSectionBox);
        formContentBox.setPadding(new Insets(10));

        HBox saveButtonBox = new HBox(saveButton);
        saveButtonBox.setAlignment(Pos.CENTER_LEFT);
        saveButtonBox.setPadding(new Insets(0, 10, 10, 20));

        VBox dialogLayout = new VBox(10, formContentBox, saveButtonBox);
        dialogLayout.setPadding(new Insets(10));

        dialog.setScene(new Scene(dialogLayout));
        dialog.show();
    }
}

