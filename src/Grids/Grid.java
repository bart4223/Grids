package Grids;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.*;

public class Grid implements LayerEventListener {

    protected Integer FGridDistance;
    protected Color FGridColor;
    protected Stage FStage;
    protected GridStageController FStageController;
    protected ArrayList<Layer> FLayers;
    protected Layer FCurrentLayer;
    protected Integer FMaxZOrder;

    protected void UpdateStage(Boolean aUpdateControls, String aLayerName) {
        //LogLayers();
        if (aUpdateControls) {
            FStageController.UpdateControls();
        }
        FStageController.RenderScene(aLayerName.equals(""));
    }

    protected void SortLayers() {
        Collections.sort(FLayers);
    }

    protected void LogLayers() {
        System.out.print("Begin Layer Log"+"\n");
        for (Layer Layer : FLayers) {
            System.out.print(Layer.toString()+"\n");
        }
        System.out.print("End Layer Log"+"\n");
    }

    protected void CreateStage(){
        FStage = new Stage();
        FXMLLoader lXMLLoader;
        lXMLLoader = new FXMLLoader(getClass().getResource("GridStage.fxml"));
        try {
            lXMLLoader.load();
            FStageController = lXMLLoader.getController();
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

    public Grid(int aGridDistance, Color aColor) {
        FLayers = new ArrayList<Layer>();
        FGridDistance = aGridDistance;
        FGridColor = aColor;
        FCurrentLayer = null;
        FMaxZOrder = 0;
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

    public void setGridDistance(Integer aValue) {
        FGridDistance = aValue;
        UpdateStage(false, "");
    }

    public Integer getGridDistance() {
        return FGridDistance;
    }

    public Color getGridColor() {
        return FGridColor;
    }

    public Layer addLayer(String aName, String aDescription, Color aColor) {
        Layer Layer = new Layer(aName, aDescription, aColor);
        FMaxZOrder = FMaxZOrder + 1;
        Layer.setZOrder(FMaxZOrder);
        Layer.addEventListener(this);
        FLayers.add(Layer);
        SortLayers();
        UpdateStage(true, "");
        return Layer;
    }

    public Layer getLayer(String aName) {
        for (Layer Layer : FLayers) {
            if (Layer.getName().equals(aName)) {
                return Layer;
            }
        }
        return null;
    }

    public Layer setCurrentLayer(String aName) {
        if (FCurrentLayer == null || !FCurrentLayer.getName().equals(aName)) {
            FCurrentLayer = getLayer(aName);
            //System.out.println("New CurrentLayer: " + FCurrentLayer.getName());
            if (FCurrentLayer.getZOrder() < FMaxZOrder) {
                FMaxZOrder = FMaxZOrder + 1;
                FCurrentLayer.setZOrder(FMaxZOrder);
                SortLayers();
                UpdateStage(false, "");
            }
        }
        return FCurrentLayer;
    }

    public ArrayList<Layer> getLayers() {
        return FLayers;
    }

    public Layer getCurrentLayer() {
        return FCurrentLayer;
    }

    public void drawCircle(double aX, double aY, double aRadius) {
        double PX;
        double PY;
        for (int i = 0; i < 360; i = i + 1) {
            PX = cos(i*2*PI/360) * aRadius;
            PY = sin(i*2*PI/360) * aRadius;
            FCurrentLayer.addPoint(PX + aX, PY + aY);
        }
    }

    @Override
    public void handleAddPoint(LayerAddPointEvent e) {
        UpdateStage(false, e.LayerName);
    }

    @Override
    public void handleDeletePoint(LayerDeletePointEvent e) {
        UpdateStage(false, e.LayerName);
    }
}
