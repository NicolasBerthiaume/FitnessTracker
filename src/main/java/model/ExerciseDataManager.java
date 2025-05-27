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
        return exerciseData.values().stream()
                .flatMap(List::stream)
                .filter(entry -> date == null || entry.getDate().equals(date))
                .filter(entry -> searchQuery == null || searchQuery.isEmpty() || entry.getExerciseName().toLowerCase().contains(searchQuery.toLowerCase()))
                .toList();
    }

    private void loadDataFromCSV() {
        List<ExerciseEntry> entries = ExerciseCSVUtil.getAllExerciseEntries();
        exerciseData.clear();
        for (ExerciseEntry entry : entries) {
            exerciseData.computeIfAbsent(entry.getDate(), k -> new ArrayList<>()).add(entry);
        }
    }

    public void deleteExerciseEntry(ExerciseEntry entry) {
        LocalDate date = entry.getDate();
        List<ExerciseEntry> entries = exerciseData.get(date);
        if (entries != null) {
            entries.remove(entry);
            if (entries.isEmpty()) {
                exerciseData.remove(date);
            }
            ExerciseCSVUtil.saveExerciseEntries(getAllExerciseEntries());
        }
    }

    public List<String> getUniqueExerciseNames() {
        return exerciseData.values().stream()
                .flatMap(List::stream)
                .map(ExerciseEntry::getExerciseName)
                .distinct()
                .sorted()
                .toList();
    }
}

