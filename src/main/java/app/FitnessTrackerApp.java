package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FitnessTrackerApp extends Application {
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Weight Calories Tracker App");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}