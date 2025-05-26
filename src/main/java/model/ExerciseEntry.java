package model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class ExerciseEntry {
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty exerciseName = new SimpleStringProperty();
    private final IntegerProperty setNumber = new SimpleIntegerProperty();
    private final IntegerProperty reps = new SimpleIntegerProperty();
    private final DoubleProperty weightLoad = new SimpleDoubleProperty();
    private final DoubleProperty totalVolumeLbs = new SimpleDoubleProperty();

    public ExerciseEntry(LocalDate date, String exerciseName, int setNumber, int reps, double weightLoad) {
        this.date.set(date);
        this.exerciseName.set(exerciseName);
        this.setNumber.set(setNumber);
        this.reps.set(reps);
        this.weightLoad.set(weightLoad);
        recalculateTotalVolume();

        // Update total volume automatically if reps or weightLoad changes
        this.reps.addListener((obs, oldVal, newVal) -> recalculateTotalVolume());
        this.weightLoad.addListener((obs, oldVal, newVal) -> recalculateTotalVolume());
    }

    // Properties
    // JavaFX properties are necessary for TableView (and other controls) to observe and reflect data.
    // Without them, you’d have to manually refresh the UI every time data changes.
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty exerciseNameProperty() { return exerciseName; }
    public IntegerProperty setNumberProperty() { return setNumber; }
    public IntegerProperty repsProperty() { return reps; }
    public DoubleProperty weightLoadProperty() { return weightLoad; }
    public DoubleProperty totalVolumeProperty() { return totalVolumeLbs; }

    // Getters
    public LocalDate getDate() { return date.get(); }
    public String getExerciseName() { return exerciseName.get(); }
    public int getSetNumber() { return setNumber.get(); }
    public int getReps() { return reps.get(); }
    public double getWeightLoad() { return weightLoad.get(); }
    public double getTotalVolumeLbs() { return totalVolumeLbs.get(); }

    // Setters
    public void setDate(LocalDate date) { this.date.set(date); }
    public void setExerciseName(String exerciseName) { this.exerciseName.set(exerciseName); }
    public void setSetNumber(int setNumber) { this.setNumber.set(setNumber); }
    public void setReps(int reps) { this.reps.set(reps); }
    public void setWeightLoad(double weightLoad) { this.weightLoad.set(weightLoad); }

    private void recalculateTotalVolume() {
        this.totalVolumeLbs.set(this.reps.get() * this.weightLoad.get());
    }
}