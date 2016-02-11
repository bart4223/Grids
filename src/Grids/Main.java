package Grids;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    protected GridManager FGridManager;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FGridManager = new GridManager(primaryStage);
        FGridManager.Initialize();
        FGridManager.ShowStages();
    }

    @Override
    public void stop() throws Exception {
        FGridManager.Finalize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
