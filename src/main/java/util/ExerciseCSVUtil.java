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
                    entries.add(new ExerciseEntry(date, name, setNumber, reps, weightLoad));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private static void saveExerciseEntries(List<ExerciseEntry> entries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXERCISE_CSV_FILE_PATH))) {
            writer.write("Date,ExerciseName,SetNumber,Reps,WeightLoad\n");
            for (ExerciseEntry entry : entries) {
                writer.write(entry.getDate() + "," +
                        entry.getExerciseName() + "," +
                        entry.getSetNumber() + "," +
                        entry.getReps() + "," +
                        entry.getWeightLoad() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

