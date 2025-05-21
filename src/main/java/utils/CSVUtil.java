package utils;

import model.FitnessEntry;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class CSVUtil {
    private static final String CSV_FILE_PATH = "fitness_data.csv";

    public static void addWeightEntry(LocalDate date, double weight) {
        Map<LocalDate, FitnessEntry> dataMap = loadDataMap();

        FitnessEntry existingEntry = dataMap.getOrDefault(date, new FitnessEntry(date, null, null));
        dataMap.put(date, new FitnessEntry(date, weight, existingEntry.getCalories()));

        saveDataMap(dataMap);
    }

    public static void addCaloriesEntry(LocalDate date, int calories) {
        Map<LocalDate, FitnessEntry> dataMap = loadDataMap();

        FitnessEntry existingEntry = dataMap.getOrDefault(date, new FitnessEntry(date, null, null));
        dataMap.put(date, new FitnessEntry(date, existingEntry.getWeight(), calories));

        saveDataMap(dataMap);
    }

    private static Map<LocalDate, FitnessEntry> loadDataMap() {
        Map <LocalDate, FitnessEntry> dataMap = new TreeMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens.length >= 3) {
                    LocalDate date = LocalDate.parse(tokens[0]);
                    Double weight = tokens[1].isEmpty() ? null : Double.parseDouble(tokens[1]);
                    Integer calories = tokens[2].isEmpty() ? null : Integer.parseInt(tokens[2]);
                    dataMap.put(date, new FitnessEntry(date, weight, calories));
                }
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataMap;
    }

    private static void saveDataMap(Map<LocalDate, FitnessEntry> dataMap) {
        try (BufferedWriter writer = new BufferedWriter((new FileWriter(CSV_FILE_PATH)))) {
            writer.write("Date,Weight,Calories\n");
            for (FitnessEntry entry : dataMap.values()) {
                writer.write(entry.getDate() + "," +
                        (entry.getWeight() != null ? entry.getWeight() : "") + "," +
                        (entry.getCalories() != null ? entry.getCalories() : "") + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<FitnessEntry> getAllFitnessEntries() {
        return new ArrayList<>(loadDataMap().values());
    }
}
