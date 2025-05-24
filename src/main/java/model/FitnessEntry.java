package model;

import java.time.LocalDate;

public class FitnessEntry {
    private final LocalDate date;
    private final Double weight;
    private final Integer breakfastCalories;
    private final Integer lunchCalories;
    private final Integer dinnerCalories;

    public FitnessEntry(LocalDate date,
                        Double weight,
                        Integer breakfastCalories,
                        Integer lunchCalories,
                        Integer dinnerCalories) {
        this.date = date;
        this.weight = weight;
        this.breakfastCalories = breakfastCalories;
        this.lunchCalories = lunchCalories;
        this.dinnerCalories = dinnerCalories;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getBreakfastCalories() {
        return breakfastCalories;
    }

    public Integer getLunchCalories() {
        return lunchCalories;
    }

    public Integer getDinnerCalories() {
        return dinnerCalories;
    }

    public int getTotalCalories() {
        return (breakfastCalories != null? breakfastCalories : 0)
                + (lunchCalories != null? lunchCalories : 0)
                + (dinnerCalories != null? dinnerCalories : 0);
    }
}
