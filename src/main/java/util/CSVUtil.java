package util;

import model.FitnessEntry;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class CSVUtil {
    private static final String CSV_FILE_PATH = "fitness_data.csv";

    public static void addWeightEntry(LocalDate date, double weight) {
        Map<LocalDate, FitnessEntry> dataMap = loadDataMap();
        FitnessEntry existing = dataMap.getOrDefault(date, new FitnessEntry(date, null, null, null, null));
        dataMap.put(date, new FitnessEntry(date, weight, existing.getBreakfastCalories(), existing.getLunchCalories(), existing.getDinnerCalories()));
        saveDataMap(dataMap);
    }

    public static void addCaloriesEntry(LocalDate date, FitnessEntry updatedEntry) {
        Map<LocalDate, FitnessEntry> dataMap = loadDataMap();
        dataMap.put(date, updatedEntry);
        saveDataMap(dataMap);
    }

    private static Map<LocalDate, FitnessEntry> loadDataMap() {
        Map<LocalDate, FitnessEntry> dataMap = new TreeMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens.length >= 5) {
                    LocalDate date = LocalDate.parse(tokens[0]);
                    Double weight = tokens[1].isEmpty() ? null : Double.parseDouble(tokens[1]);
                    Integer breakfast = tokens[2].isEmpty() ? null : Integer.parseInt(tokens[2]);
                    Integer lunch = tokens[3].isEmpty() ? null : Integer.parseInt(tokens[3]);
                    Integer dinner = tokens[4].isEmpty() ? null : Integer.parseInt(tokens[4]);
                    dataMap.put(date, new FitnessEntry(date, weight, breakfast, lunch, dinner));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataMap;
    }

    private static void saveDataMap(Map<LocalDate, FitnessEntry> dataMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
            writer.write("Date,Weight,Breakfast,Lunch,Dinner\n");
            for (FitnessEntry entry : dataMap.values()) {
                writer.write(entry.getDate() + "," +
                        (entry.getWeight() != null ? entry.getWeight() : "") + "," +
                        (entry.getBreakfastCalories() != null ? entry.getBreakfastCalories() : "") + "," +
                        (entry.getLunchCalories() != null ? entry.getLunchCalories() : "") + "," +
                        (entry.getDinnerCalories() != null ? entry.getDinnerCalories() : "") + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<FitnessEntry> getAllFitnessEntries() {
        return new ArrayList<>(loadDataMap().values());
    }
}
