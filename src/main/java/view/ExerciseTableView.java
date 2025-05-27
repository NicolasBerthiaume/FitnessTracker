package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.ExerciseEntry;
import model.ExerciseDataManager;

import java.time.LocalDate;
import java.util.List;

public class ExerciseTableView extends BorderPane {
    private final TableView<ExerciseEntry> tableView = new TableView<>();
    private final ObservableList<ExerciseEntry> exerciseData = FXCollections.observableArrayList();
    private final ExerciseDataManager exerciseDataManager;

    public ExerciseTableView(ExerciseDataManager manager) {
        this.exerciseDataManager = manager;
        this.exerciseData.addAll(manager.getAllExerciseEntries());

        setPadding(new Insets(10));
        createTable();
        //filter functionality still needs tweaking
        setTop(createToolbar());
        setCenter(tableView);
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

        //actions for each entry
        //just supports delete atm
        TableColumn<ExerciseEntry, Void> actions = new TableColumn<>("Actions");
        actions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(e -> {
                    ExerciseEntry entry = getTableView().getItems().get(getIndex());

                    //confirmation pop-up
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
                    setGraphic(deleteButton);
                }
            }
        });

        tableView.setItems(exerciseData);
        tableView.getColumns().addAll(dateCol, nameCol, setCol, repsCol, weightCol, volumeCol, actions);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private HBox createToolbar() {
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select date");
        ComboBox<String> exerciseDropdown = new ComboBox<>();
        exerciseDropdown.setPromptText("Select exercise");
        exerciseDropdown.getItems().addAll(exerciseDataManager.getUniqueExerciseNames());

        Button filterButton = new Button("Filter");
        Button clearButton = new Button("Clear Filters");

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
        box.setPadding(new Insets(10));
        return box;
    }

    public void refreshData() {
        exerciseData.setAll(exerciseDataManager.getAllExerciseEntries());
    }
}

