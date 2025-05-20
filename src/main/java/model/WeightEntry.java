package model;

import java.time.LocalDate;

public class WeightEntry {
    private final LocalDate date;
    private final double weight;

    public WeightEntry(LocalDate date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getWeight() {
        return weight;
    }
}
