package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.NutritionDashboardView;
import view.WeightLiftingDashboardView;

public class GUI extends Application {
    @Override
    public void start(Stage stage) {
        NutritionDashboardView dashboardView = new NutritionDashboardView();
        WeightLiftingDashboardView exerciseDashboardView = new WeightLiftingDashboardView();

        // StackPane to hold both Nutrition and Exercise Dashboards
        StackPane viewContainer = new StackPane(dashboardView, exerciseDashboardView);
        dashboardView.setVisible(true);
        exerciseDashboardView.setVisible(false);

        // ComboBox for toggling between Nutrition and Exercise Dashboards
        ComboBox<String> viewSelector = new ComboBox<>();
        viewSelector.getItems().addAll("Nutrition Dashboard", "Exercise Dashboard");
        viewSelector.setValue("Nutrition Dashboard");

        viewSelector.setOnAction(e -> {
            String selected = viewSelector.getValue();
            dashboardView.setVisible("Nutrition Dashboard".equals(selected));
            exerciseDashboardView.setVisible("Exercise Dashboard".equals(selected));
        });

        // Layout wrapper
        BorderPane root = new BorderPane();
        root.setTop(viewSelector);
        root.setCenter(viewContainer);

        Scene scene = new Scene(root, 1200, 900);
        stage.setScene(scene);
        stage.setTitle("Nicolas's Fitness Tracker");
        stage.show();
    }
}
