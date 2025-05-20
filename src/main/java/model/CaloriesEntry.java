package model;

import java.time.LocalDate;

public class CaloriesEntry {
    private final LocalDate date;
    private final int calories;

    public CaloriesEntry(LocalDate date, int calories) {
        this.date = date;
        this.calories = calories;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getCalories() {
        return calories;
    }
}
