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
        FGrid.AddLayer("LAYER01","", Color.BLACK);
        FGrid.AddLayer("LAYER02","", Color.BLUE);
        FGrid.SetCurrentLayer("LAYER01");
        FGrid.DrawCircle(12,12,8);
        FGrid.SetCurrentLayer("LAYER02");
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
