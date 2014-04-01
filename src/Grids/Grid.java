package Grids;

import Uniwork.Graphics.Point2D;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
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
    protected Random FGenerator;
    protected GridManager FGridManager;

    protected void UpdateStage(Boolean aUpdateControls, String aLayerName) {
        if (aUpdateControls) {
            FStageController.UpdateControls();
        }
        FStageController.RenderScene(aLayerName.equals(""));
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
            Scene Scene = new Scene(lRoot, 800, 800, Color.WHITE);
            FStage.setScene(Scene);
            FStage.setResizable(false);
            Scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    handleKeyPressed(keyEvent);
                }
            });
            FStageController.Initialize();
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    protected void RenameLayers() {
        int i = 1;
        for (Layer Layer : FLayers) {
            Layer.setName("LAYER" + i);
            Layer.setDescription("Layer " + i);
            i++;
        }
    }

    protected void handleKeyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case BACK_SPACE:
                FCurrentLayer.removeSelectedObjects();
        }

    }

    protected int getRandomValue(int aMax) {
        return FGenerator.nextInt(aMax);
    }

    protected int getMaxZOrder() {
        int lresult = 0;
        for (Layer Layer : FLayers) {
            if (Layer.getZOrder() > lresult) {
                lresult = Layer.getZOrder();
            }
        }
        return lresult;
    }

    public Grid(GridManager aGridManager, int aGridDistance, Color aColor) {
        FLayers = new ArrayList<Layer>();
        FGenerator = new Random();
        FGridDistance = aGridDistance;
        FGridColor = aColor;
        FCurrentLayer = null;
        FGridManager = aGridManager;
    }

    public void Initialize() {
        CreateStage();
        addLayer("LAYER1", "Layer 1", Color.rgb(getRandomValue(255), getRandomValue(255), getRandomValue(255)));
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

    public void setGridColor(Color aValue) {
        FGridColor = aValue;
        UpdateStage(false, "");
    }

    public Color getGridColor() {
        return FGridColor;
    }

    public Layer addLayer(String aName, String aDescription, Color aColor) {
        Layer Layer = new Layer(aName, aDescription, aColor);
        int lZOrder = getMaxZOrder() + 1;
        Layer.setZOrder(lZOrder);
        Layer.addEventListener(this);
        FLayers.add(Layer);
        setCurrentLayer(Layer);
        return Layer;
    }

    public Layer addLayer() {
        return(addLayer("LAYER" + (FLayers.size() + 1), "Layer " + (FLayers.size() + 1), Color.rgb(getRandomValue(255), getRandomValue(255), getRandomValue(255))));
    }

    public void removeLayer(Layer layer, boolean force) {
        if (!force && FLayers.size() < 2) return;
        layer.removeEventListener(this);
        FLayers.remove(layer);
        if (FLayers.size() > 0) {
            RenameLayers();
            setCurrentLayer(FLayers.get(0));
        }
        else {
            FCurrentLayer = null;
        }
    }

    public void removeCurrentLayer() {
        removeLayer(getCurrentLayer(), false);
    }

    public void removeAllLayers() {
        while (FLayers.size() > 0) {
            removeLayer(FLayers.get(0), true);
        }
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
            setCurrentLayer(getLayer(aName));
        }
        return FCurrentLayer;
    }

    public void setCurrentLayer(Layer aLayer) {
        FCurrentLayer = aLayer;
        int lZOrder = getMaxZOrder();
        if (FCurrentLayer.getZOrder() < lZOrder) {
            FCurrentLayer.setZOrder(lZOrder + 1);
        }
        UpdateStage(true, "");
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

    public void Save() {
        FGridManager.saveGrid(this);
    }

    public void Load() {
        FGridManager.loadGrid(this);
        UpdateStage(true, "");
    }

    public GridManager getManager() {
        return FGridManager;
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
