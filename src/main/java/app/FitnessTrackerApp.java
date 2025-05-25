package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.DashboardView;

public class FitnessTrackerApp extends Application {
    @Override
    public void start(Stage stage) {
        DashboardView dashboard = new DashboardView();

        Scene scene = new Scene(dashboard, 1200, 900);
        stage.setScene(scene);
        stage.setTitle("Nicolas's Fitness Tracker");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}