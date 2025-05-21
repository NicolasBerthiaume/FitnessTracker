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
        FitnessEntry currentEntry = fitnessData.getOrDefault(date, new FitnessEntry(date, null, null));
        FitnessEntry updatedEntry = new FitnessEntry(date, weight, currentEntry.getCalories());
        fitnessData.put(date, updatedEntry);
        CSVUtil.addWeightEntry(date, weight);
    }

    public void addCalories(LocalDate date, int calories) {
        FitnessEntry currentEntry = fitnessData.getOrDefault(date, new FitnessEntry(date, null, null));
        FitnessEntry updatedEntry = new FitnessEntry(date, currentEntry.getWeight(), calories);
        fitnessData.put(date, updatedEntry);
        CSVUtil.addCaloriesEntry(date, calories);
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

    public Map<LocalDate, Double> getAllCaloriesData() {
        Map<LocalDate, Double> caloriesData = new TreeMap<>();
        for (Map.Entry<LocalDate, FitnessEntry> entry : fitnessData.entrySet()) {
            if (entry.getValue().getCalories() != null) {
                caloriesData.put(entry.getKey(), entry.getValue().getWeight());
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
