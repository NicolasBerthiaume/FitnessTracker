package model;

import util.ExerciseCSVUtil;

import java.time.LocalDate;
import java.util.*;

public class ExerciseDataManager {
    private final Map<LocalDate, List<ExerciseEntry>> exerciseData = new TreeMap<>();
    private final LocalDate startDate;

    public ExerciseDataManager(LocalDate startDate) {
        this.startDate = startDate;
        loadDataFromCSV();
    }

    public void addExerciseEntry(ExerciseEntry entry) {
        exerciseData.computeIfAbsent(entry.getDate(), k -> new ArrayList<>()).add(entry);
        ExerciseCSVUtil.addExerciseEntry(entry);
        loadDataFromCSV(); // Refresh internal state
    }

    public List<ExerciseEntry> getEntriesForDate(LocalDate date) {
        return exerciseData.getOrDefault(date, Collections.emptyList());
    }

    public Map<LocalDate, List<ExerciseEntry>> getAllExerciseData() {
        return Collections.unmodifiableMap(exerciseData);
    }

    public List<ExerciseEntry> getAllExerciseEntries() {
        return exerciseData.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return exerciseData.keySet().stream().max(LocalDate::compareTo).orElse(startDate);
    }

    public List<ExerciseEntry> getFilteredEntries(LocalDate date, String searchQuery) {
        List<ExerciseEntry> entriesForDate = exerciseData.getOrDefault(date, new ArrayList<>());

        if (searchQuery == null || searchQuery.isEmpty()) {
            return entriesForDate;
        }

        String lowerQuery = searchQuery.toLowerCase();
        return entriesForDate.stream()
                .filter(entry -> entry.getExerciseName().toLowerCase().contains(lowerQuery))
                .toList();
    }

    private void loadDataFromCSV() {
        List<ExerciseEntry> entries = ExerciseCSVUtil.getAllExerciseEntries();
        exerciseData.clear();
        for (ExerciseEntry entry : entries) {
            exerciseData.computeIfAbsent(entry.getDate(), k -> new ArrayList<>()).add(entry);
        }
    }
}

