package model;

import util.NutritionCSVUtil;

import java.time.LocalDate;
import java.util.*;

public class NutritionDataManager {
    private final Map<LocalDate, NutritionEntry> fitnessData = new TreeMap<>();
    private final LocalDate startDate;

    public NutritionDataManager(LocalDate startDate) {
        this.startDate = startDate;
        loadDataFromCSV();
    }

    public void addWeight(LocalDate date, double weight) {
        NutritionEntry current = fitnessData.getOrDefault(date, new NutritionEntry(date, null, null, null, null, null));
        NutritionEntry updated = new NutritionEntry(date, weight,
                current.getBreakfastCalories(),
                current.getLunchCalories(),
                current.getDinnerCalories(),
                current.getSnackCalories());
        fitnessData.put(date, updated);
        NutritionCSVUtil.addWeightEntry(date, weight);
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

    private void updateCaloriesForMeal(LocalDate date, int caloriesToAdd, String mealType) {
        NutritionEntry currentEntry = fitnessData.getOrDefault(date, new NutritionEntry(date, null, 0, 0, 0, 0));

        Integer breakfast = currentEntry.getBreakfastCalories() == null ? 0 : currentEntry.getBreakfastCalories();
        Integer lunch = currentEntry.getLunchCalories() == null ? 0 : currentEntry.getLunchCalories();
        Integer dinner = currentEntry.getDinnerCalories() == null ? 0 : currentEntry.getDinnerCalories();
        Integer snack = currentEntry.getSnackCalories() == null ? 0 : currentEntry.getSnackCalories();

        switch (mealType) {
            case "breakfast" -> breakfast = caloriesToAdd;
            case "lunch"     -> lunch = caloriesToAdd;
            case "dinner"    -> dinner = caloriesToAdd;
            case "snack"     -> snack = caloriesToAdd;
        }

        NutritionEntry updatedEntry = new NutritionEntry(date, currentEntry.getWeight(), breakfast, lunch, dinner, snack);
        fitnessData.put(date, updatedEntry);
        NutritionCSVUtil.addCaloriesEntry(date, updatedEntry);
        loadDataFromCSV();
    }

    public Map<LocalDate, NutritionEntry> getAllFitnessData() {
        return Collections.unmodifiableMap(fitnessData);
    }

    public Map<LocalDate, Double> getAllWeightData() {
        Map<LocalDate, Double> weightData = new TreeMap<>();
        for (Map.Entry<LocalDate, NutritionEntry> entry : fitnessData.entrySet()) {
            if (entry.getValue().getWeight() != null) {
                weightData.put(entry.getKey(), entry.getValue().getWeight());
            }
        }
        return weightData;
    }

    public Map<LocalDate, Integer> getAllCaloriesData() {
        Map<LocalDate, Integer> caloriesData = new TreeMap<>();
        for (Map.Entry<LocalDate, NutritionEntry> entry : fitnessData.entrySet()) {
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
        List<NutritionEntry> entries = NutritionCSVUtil.getAllFitnessEntries();
        for (NutritionEntry entry : entries) {
            fitnessData.put(entry.getDate(), entry);
        }
    }
}
