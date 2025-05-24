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
        FitnessEntry current = fitnessData.getOrDefault(date, new FitnessEntry(date, null, null, null, null, null));
        FitnessEntry updated = new FitnessEntry(date, weight,
                current.getBreakfastCalories(),
                current.getLunchCalories(),
                current.getDinnerCalories(),
                current.getSnackCalories());
        fitnessData.put(date, updated);
        CSVUtil.addWeightEntry(date, weight);
        loadDataFromCSV();
    }

    public void addBreakfastCalories(LocalDate date, int calories) {
        updateCaloriesForMeal(date, calories, "breakfast");
    }

    public void addLunchCalories(LocalDate date, int calories) {
        updateCaloriesForMeal(date, calories, "lunch");
    }

    public void addDinnerCalories(LocalDate date, int calories) {
        updateCaloriesForMeal(date, calories, "dinner");
    }

    public void addSnackCalories(LocalDate date, int calories) {
        updateCaloriesForMeal(date, calories, "snack");
    }

    private void updateCaloriesForMeal(LocalDate date, int calories, String mealType) {
        FitnessEntry currentEntry = fitnessData.getOrDefault(date, new FitnessEntry(date, null, 0, 0, 0, 0));

        int breakfast = currentEntry.getBreakfastCalories();
        int lunch = currentEntry.getLunchCalories();
        int dinner = currentEntry.getDinnerCalories();
        int snack = currentEntry.getSnackCalories();

        switch (mealType) {
            case "breakfast" -> breakfast = calories;
            case "lunch"     -> lunch = calories;
            case "dinner"    -> dinner = calories;
            case "snack"     -> snack = calories;
        }

        FitnessEntry updatedEntry = new FitnessEntry(date, currentEntry.getWeight(), breakfast, lunch, dinner, snack);
        fitnessData.put(date, updatedEntry);
        CSVUtil.addCaloriesEntry(date, updatedEntry);
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
