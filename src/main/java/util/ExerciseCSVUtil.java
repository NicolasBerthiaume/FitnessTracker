package util;

import model.ExerciseEntry;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ExerciseCSVUtil {
    private static final String EXERCISE_CSV_FILE_PATH = "exercise_data.csv";

    public static void addExerciseEntry(ExerciseEntry entry) {
        List<ExerciseEntry> entries = getAllExerciseEntries();
        entries.add(entry);
        saveExerciseEntries(entries);
    }

    public static List<ExerciseEntry> getAllExerciseEntries() {
        List<ExerciseEntry> entries = new ArrayList<>();
        File file = new File(EXERCISE_CSV_FILE_PATH);
        if (!file.exists()) return entries;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens.length >= 5) {
                    LocalDate date = LocalDate.parse(tokens[0]);
                    String name = tokens[1];
                    int setNumber = Integer.parseInt(tokens[2]);
                    int reps = Integer.parseInt(tokens[3]);
                    double weightLoad = Double.parseDouble(tokens[4]);
                    String notes = tokens.length >= 6 ? tokens[5] : ""; //in case of no notes
                    entries.add(new ExerciseEntry(date, name, setNumber, reps, weightLoad, notes));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static void saveExerciseEntries(List<ExerciseEntry> entries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXERCISE_CSV_FILE_PATH))) {
            writer.write("Date,ExerciseName,SetNumber,Reps,WeightLoad,Notes\n");
            for (ExerciseEntry entry : entries) {
                writer.write(entry.getDate() + "," +
                        entry.getExerciseName() + "," +
                        entry.getSetNumber() + "," +
                        entry.getReps() + "," +
                        entry.getWeightLoad() + "," +
                        escapeCSV(entry.getNotes()) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //to ensure that fields with commas, quotes, or newlines are correctly quoted
    private static String escapeCSV(String input) {
        if (input == null) return "";
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            input = input.replace("\"", "\"\"");
            return "\"" + input + "\"";
        }
        return input;
    }
}