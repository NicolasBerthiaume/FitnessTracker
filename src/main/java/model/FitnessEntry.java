package model;

import java.time.LocalDate;

public class FitnessEntry {
    private final LocalDate date;
    private final Double weight;
    private final Integer calories;

    public FitnessEntry(LocalDate date, Double weight, Integer calories) {
        this.date = date;
        this.weight = weight;
        this.calories = calories;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getCalories() {
        return calories;
    }
}
