package Grids;

import Uniwork.Graphics.Point2D;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Grid implements LayerEventListener {

    protected Integer FGridDistance;
    protected Color FGridColor;
    protected Stage FStage;
    protected GridStageController FStageController;
    protected ArrayList<Layer> FLayers;
    protected Layer FCurrentLayer;
    protected Integer FMaxZOrder;
    protected Random FGenerator;

    protected void UpdateStage(Boolean aUpdateControls, String aLayerName) {
        if (aUpdateControls) {
            FStageController.UpdateControls();
        }
        FStageController.RenderScene(aLayerName.equals(""));
    }

    protected void SortLayers() {
        Collections.sort(FLayers);
    }

    protected void RenameLayers() {
        int i = 1;
        for (Layer Layer : FLayers) {
            Layer.setName("LAYER" + i);
            Layer.setDescription("Layer " + i);
            i++;
        }
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
            FStage.setScene(new Scene(lRoot, 600, 600, Color.WHITE));
            FStage.setResizable(false);
            FStageController.Initialize();
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    protected int getRandomValue(int aMax) {
        return FGenerator.nextInt(aMax);
    }

    public Grid(int aGridDistance, Color aColor) {
        FLayers = new ArrayList<Layer>();
        FGenerator = new Random();
        FGridDistance = aGridDistance;
        FGridColor = aColor;
        FCurrentLayer = null;
        FMaxZOrder = 0;
    }

    public void Initialize() {
        CreateStage();
        addLayer("LAYER1", "Layer 1", Color.rgb(getRandomValue(255),getRandomValue(255),getRandomValue(255)));
        setCurrentLayer("LAYER1");
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

    public Layer addLayer() {
        return(addLayer("LAYER" + (FLayers.size() + 1), "Layer " + (FLayers.size() + 1), Color.rgb(getRandomValue(255),getRandomValue(255),getRandomValue(255))));
    }

    public void removeLayer(Layer layer) {
        if (FLayers.size() < 2) return;
        layer.removeEventListener(this);
        FLayers.remove(layer);
        RenameLayers();
        UpdateStage(true, "");
    }

    public void removeCurrentLayer() {
        removeLayer(getCurrentLayer());
    }

    public void clearLayer(Layer aLayer) {
        aLayer.removeObjects();
    }

    public void clearLayer(String aName) {
        Layer aLayer = getLayer(aName);
        clearLayer(aLayer);
    }

    public void clearCurrentLayer() {
        Layer aLayer = getCurrentLayer();
        clearLayer(aLayer);
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

    public Point2D CoordinatesToGridCoordinates(Point2D aPoint) {
        return new Point2D((int)(aPoint.getX() / getGridDistance()) + 1, (int)(aPoint.getY() / getGridDistance()) + 1);
    }

    @Override
    public void handleAddObject(LayerAddObjectEvent e) {
        UpdateStage(false, e.LayerName);
    }

    @Override
    public void handleRemoveObject(LayerRemoveObjectEvent e) {
        UpdateStage(false, e.LayerName);
    }

    @Override
    public void handleSelectObject(LayerSelectObjectEvent e) {
        UpdateStage(false, e.LayerName);
    }

    @Override
    public void handleUnselectObject(LayerUnselectObjectEvent e) {
        UpdateStage(false, e.LayerName);
    }
}
