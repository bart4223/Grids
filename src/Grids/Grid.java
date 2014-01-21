package Grids;

import Uniwork.Graphics.Point2D;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;

public class Grid {

    protected int FGridDistance;
    protected Color FGridColor;
    protected Stage FStage;
    protected GridStageController FStageController;
    protected ArrayList<Layer> FLayers;

    protected void UpdateStage() {
        FStageController.RenderScene(false);
    }

    protected void CreateStage(){
        FStage = new Stage();
        FXMLLoader lXMLLoader = new FXMLLoader(getClass().getResource("GridStage.fxml"));
        try {
            lXMLLoader.load();
            FStageController = (GridStageController)lXMLLoader.getController();
            FStageController.Grid = this;
            Parent lRoot = lXMLLoader.getRoot();
            FStage.setTitle("Grid");
            FStage.setScene(new Scene(lRoot, 500, 500, Color.WHITE));
            FStage.setResizable(false);
            FStageController.Initialize();
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    public Grid() {
        this(10,Color.DARKGRAY);
    }

    public Grid(int aGridDistance, Color aColor) {
        FLayers = new ArrayList<Layer>();
        FGridDistance = aGridDistance;
        FGridColor = aColor;

    }

    public void Initialize() {
        CreateStage();
    }

    public void Finalize() {
        CloseStage();
    }

    public void ShowStage(){
        FStage.show();
    }

    public void CloseStage(){
        FStage.close();
    }

    public Stage getStage() {
        return FStage;
    }

    public int getGridDistance() {
        return FGridDistance;
    }

    public Color getGridColor() {
        return FGridColor;
    }

    public Layer AddLayer(String aName, String aDescription, Color aColor) {
        Layer Layer = new Layer(aName, aDescription, aColor);
        FLayers.add(Layer);
        UpdateStage();
        return Layer;
    }

    public Point2D AddLayerPoint(String aLayerName, double aX, double aY) {
        Layer Layer = getLayer(aLayerName);
        Point2D Point = Layer.AddPoint(aX,aY);
        UpdateStage();
        return Point;
    }

    public Layer getLayer(String aName) {
        Iterator lItr = FLayers.iterator();
        while(lItr.hasNext())  {
            Layer Layer = (Layer)lItr.next();
            if (Layer.GetName().equals(aName)) {
                return Layer;
            }
        }
        return null;
    }
}
