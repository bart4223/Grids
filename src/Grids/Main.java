package Grids;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    protected Grid FGrid;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FGrid = new Grid(20,Color.LIGHTGRAY);
        FGrid.Initialize();
        primaryStage = FGrid.getStage();
        FGrid.addLayer("LAYER1","Layer1", Color.BLACK);
        FGrid.setCurrentLayer("LAYER1");
        FGrid.drawCircle(12,12,8);
        FGrid.ShowStage();
    }

    @Override
    public void stop() throws Exception {
        FGrid.Finalize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
