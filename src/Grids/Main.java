package Grids;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    protected Grid FGrid;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FGrid = new Grid(20,Color.DARKGREY);
        FGrid.Initialize();
        primaryStage = FGrid.getStage();
        Layer Layer = FGrid.AddLayer("LAYER01","", Color.BLACK);
        FGrid.AddLayerPoint("LAYER01",5.0,5.0);
        FGrid.AddLayerPoint("LAYER01",6.0,3.0);
        FGrid.AddLayerPoint("LAYER01",8.0,4.0);
        FGrid.AddLayerPoint("LAYER01",20.0,20.0);
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
