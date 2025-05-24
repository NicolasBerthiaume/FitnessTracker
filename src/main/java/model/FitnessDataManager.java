package model;

import util.CSVUtil;

import java.time.LocalDate;
import java.util.*;

public class FitnessDataManager {
    private final Map<LocalDate, FitnessEntry> fitnessData = new TreeMap<>();
    private final LocalDate startDate;

    public FitnessDataManager(LocalDate startDate) {
        this.startDate = startDate;
        loadDataFromCSV();
    }

    public void addWeight(LocalDate date, double weight) {
        FitnessEntry current = fitnessData.getOrDefault(date, new FitnessEntry(date, null, null, null, null));
        FitnessEntry updated = new FitnessEntry(date, weight, current.getBreakfastCalories(), current.getLunchCalories(), current.getDinnerCalories());
        fitnessData.put(date, updated);
        CSVUtil.addWeightEntry(date, weight);
        loadDataFromCSV();
    }

    public void addBreakfastCalories(LocalDate date, int calories) {
        FitnessEntry current = fitnessData.getOrDefault(date, new FitnessEntry(date, null, null, null, null));
        FitnessEntry updated = new FitnessEntry(date, current.getWeight(), calories, current.getLunchCalories(), current.getDinnerCalories());
        fitnessData.put(date, updated);
        CSVUtil.addCaloriesEntry(date, updated);
        loadDataFromCSV();
    }

    public void addLunchCalories(LocalDate date, int calories) {
        FitnessEntry current = fitnessData.getOrDefault(date, new FitnessEntry(date, null, null, null, null));
        FitnessEntry updated = new FitnessEntry(date, current.getWeight(), current.getBreakfastCalories(), calories, current.getDinnerCalories());
        fitnessData.put(date, updated);
        CSVUtil.addCaloriesEntry(date, updated);
        loadDataFromCSV();
    }

    public void addDinnerCalories(LocalDate date, int calories) {
        FitnessEntry current = fitnessData.getOrDefault(date, new FitnessEntry(date, null, null, null, null));
        FitnessEntry updated = new FitnessEntry(date, current.getWeight(), current.getBreakfastCalories(), current.getLunchCalories(), calories);
        fitnessData.put(date, updated);
        CSVUtil.addCaloriesEntry(date, updated);
        loadDataFromCSV();
    }

    public Map<LocalDate, FitnessEntry> getAllFitnessData() {
        return Collections.unmodifiableMap(fitnessData);
    }

    public Map<LocalDate, Double> getAllWeightData() {
        Map<LocalDate, Double> weightData = new TreeMap<>();
        for (Map.Entry<LocalDate, FitnessEntry> entry : fitnessData.entrySet()) {
            if (entry.getValue().getWeight() != null) {
                weightData.put(entry.getKey(), entry.getValue().getWeight());
            }
        }
        return weightData;
    }

    public Map<LocalDate, Integer> getAllCaloriesData() {
        Map<LocalDate, Integer> caloriesData = new TreeMap<>();
        for (Map.Entry<LocalDate, FitnessEntry> entry : fitnessData.entrySet()) {
            Integer total = entry.getValue().getTotalCalories();
            if (total != null) {
                caloriesData.put(entry.getKey(), total);
            }
        }
        return caloriesData;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return fitnessData.keySet().stream().max(LocalDate::compareTo).orElse(startDate);
    }

    private void loadDataFromCSV() {
        List<FitnessEntry> entries = CSVUtil.getAllFitnessEntries();
        for (FitnessEntry entry : entries) {
            fitnessData.put(entry.getDate(), entry);
        }
    }
}
