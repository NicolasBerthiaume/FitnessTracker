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

        tableView.setItems(exerciseData);
        tableView.getColumns().addAll(dateCol, nameCol, setCol, repsCol, weightCol, volumeCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private HBox createToolbar() {
        DatePicker datePicker = new DatePicker();
        TextField searchField = new TextField();
        searchField.setPromptText("Filter by exercise");

        Button filterButton = new Button("Filter");
        filterButton.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            String searchQuery = searchField.getText().toLowerCase();
            List<ExerciseEntry> filtered = exerciseDataManager.getFilteredEntries(selectedDate, searchQuery);
            exerciseData.setAll(filtered);
        });

        HBox box = new HBox(10, new Label("Date:"), datePicker, searchField, filterButton);
        box.setPadding(new Insets(10));
        return box;
    }

    public void refreshData() {
        exerciseData.setAll(exerciseDataManager.getAllExerciseEntries());
    }
}

