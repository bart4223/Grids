package Grids;

import Uniwork.Misc.NGLogEvent;
import Uniwork.Misc.NGLogEventListener;
import Uniwork.Misc.NGLogManager;
import Uniwork.Base.NGObject;
import Uniwork.Graphics.NGGeometryObject2D;
import Uniwork.Graphics.NGPoint2D;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Random;

public class Grid extends NGObject implements GridLayerEventListener, NGLogEventListener {

    protected int FUpdateCount;
    protected Integer FGridDistance;
    protected Color FGridColor;
    protected Stage FStage;
    protected GridStageController FStageController;
    protected ArrayList<GridLayer> FLayers;
    protected GridLayer FCurrentLayer;
    protected Random FGenerator;
    protected GridManager FGridManager;
    protected NGLogManager FLogManager;

    protected void UpdateStage(Boolean aUpdateControls, String aLayerName) {
        if (InUpdate()) return;
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
        for (GridLayer Layer : FLayers) {
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
        for (GridLayer Layer : FLayers) {
            if (Layer.getZOrder() > lresult) {
                lresult = Layer.getZOrder();
            }
        }
        return lresult;
    }

    protected void BeginUpdate() {
        FUpdateCount = FUpdateCount + 1;
    }

    protected void EndUpdate() {
        FUpdateCount = FUpdateCount - 1;
        if (!InUpdate()) {
            UpdateStage(true,"");
        }
    }

    protected Boolean InUpdate() {
        return FUpdateCount > 0;
    }

    @Override
    protected void DoAssignFrom(Object aObject) {
        GridLayer layer;
        XMLGrid XMLGrid = (XMLGrid)aObject;
        removeAllLayers();
        setGridDistance(XMLGrid.getGridDistance());
        setGridColor(Color.valueOf(XMLGrid.getGridColor()));
        for (XMLLayer XMLLayer : XMLGrid.getLayers()) {
            layer = addLayer(XMLLayer.getName(), XMLLayer.getDescription(), Color.valueOf(XMLLayer.getObjectColor()));
            layer.setZOrder(XMLLayer.getZOrder());
            for (NGGeometryObject2D GeoObject : XMLLayer.getGeometryObjects()) {
                layer.addObject(GeoObject);
            }
        }
        setCurrentLayer(XMLGrid.getCurrentLayer());
    }

    @Override
    protected void DoAssignTo(Object aObject) {
        XMLLayer XMLLayer;
        ArrayList<XMLLayer> XMLLayers;
        ArrayList<NGGeometryObject2D> XMLGeoObjects;
        XMLGrid XMLGrid = (XMLGrid)aObject;
        XMLGrid.setGridDistance(getGridDistance());
        XMLGrid.setGridColor(getGridColor().toString());
        XMLGrid.setCurrentLayer(getCurrentLayer().getName());
        XMLLayers = new ArrayList<XMLLayer>();
        XMLGrid.setLayers(XMLLayers);
        for (GridLayer layer : getLayers()) {
            XMLLayer = new XMLLayer();
            XMLLayers.add(XMLLayer);
            XMLLayer.setName(layer.getName());
            XMLLayer.setDescription(layer.getDescription());
            XMLLayer.setObjectColor(layer.getObjectColor().toString());
            XMLLayer.setZOrder(layer.getZOrder());
            XMLGeoObjects = new ArrayList<NGGeometryObject2D>();
            XMLLayer.setGeometryObjects(XMLGeoObjects);
            for (NGGeometryObject2D GeoObject : layer.getObjects()) {
                XMLGeoObjects.add(GeoObject);
            }
        }
    }

    public Grid(GridManager aGridManager, int aGridDistance, Color aColor) {
        FLayers = new ArrayList<GridLayer>();
        FGenerator = new Random();
        FGridDistance = aGridDistance;
        FGridColor = aColor;
        FCurrentLayer = null;
        FGridManager = aGridManager;
        FLogManager = new NGLogManager();
        FUpdateCount = 0;
    }

    public void Initialize() {
        CreateStage();
        FLogManager.addEventListener(this);
        addLayer("LAYER1", "Layer 1", Color.rgb(getRandomValue(255), getRandomValue(255), getRandomValue(255)));
    }

    public void Finalize() {
        FLogManager.removeEventListener(this);
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

    public GridLayer addLayer(String aName, String aDescription, Color aColor) {
        GridLayer Layer = new GridLayer(aName, aDescription, aColor);
        int lZOrder = getMaxZOrder() + 1;
        Layer.setZOrder(lZOrder);
        Layer.addEventListener(this);
        FLayers.add(Layer);
        setCurrentLayer(Layer);
        return Layer;
    }

    public GridLayer addLayer() {
        return(addLayer("LAYER" + (FLayers.size() + 1), "Layer " + (FLayers.size() + 1), Color.rgb(getRandomValue(255), getRandomValue(255), getRandomValue(255))));
    }

    public void removeLayer(GridLayer layer, boolean force) {
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

    public void clearLayer(GridLayer aLayer) {
        aLayer.removeObjects();
    }

    public void clearLayer(String aName) {
        GridLayer aLayer = getLayer(aName);
        clearLayer(aLayer);
    }

    public void clearCurrentLayer() {
        GridLayer aLayer = getCurrentLayer();
        clearLayer(aLayer);
    }

    public GridLayer getLayer(String aName) {
        for (GridLayer Layer : FLayers) {
            if (Layer.getName().equals(aName)) {
                return Layer;
            }
        }
        return null;
    }

    public GridLayer setCurrentLayer(String aName) {
        if (FCurrentLayer == null || !FCurrentLayer.getName().equals(aName)) {
            setCurrentLayer(getLayer(aName));
        }
        return FCurrentLayer;
    }

    public void setCurrentLayer(GridLayer aLayer) {
        FCurrentLayer = aLayer;
        int lZOrder = getMaxZOrder();
        if (FCurrentLayer.getZOrder() < lZOrder) {
            FCurrentLayer.setZOrder(lZOrder + 1);
        }
        UpdateStage(true, "");
    }

    public ArrayList<GridLayer> getLayers() {
        return FLayers;
    }

    public GridLayer getCurrentLayer() {
        return FCurrentLayer;
    }

    public NGPoint2D CoordinatesToGridCoordinates(NGPoint2D aPoint) {
        return new NGPoint2D((int)(aPoint.getX() / getGridDistance()), (int)(aPoint.getY() / getGridDistance()));
    }

    public void Save() {
        BeginUpdate();
        try {
            FGridManager.saveGrid(this);
            EndUpdate();
        }
        catch (Exception e) {
            EndUpdate();
        }
    }

    public void Load() {
        BeginUpdate();
        try {
            FGridManager.loadGrid(this);
            EndUpdate();
        }
        catch (Exception e) {
            EndUpdate();
        }
    }

    public GridManager getManager() {
        return FGridManager;
    }

    public void writeLog(String aText) {
        FLogManager.writeLog(aText);
    }

    public void clearLog() {
        FLogManager.clearLog();
    }

    public NGLogManager getLogManager() {
        return FLogManager;
    }

    @Override
    public void handleAddObject(GridLayerAddObjectEvent e) {
        UpdateStage(false, e.LayerName);
    }

    @Override
    public void handleRemoveObject(GridLayerRemoveObjectEvent e) {
        UpdateStage(false, e.LayerName);
    }

    @Override
    public void handleSelectObject(GridLayerSelectObjectEvent e) {
        UpdateStage(false, e.LayerName);
    }

    @Override
    public void handleUnselectObject(GridLayerUnselectObjectEvent e) {
        UpdateStage(false, e.LayerName);
    }

    @Override
    public void handleAddLog(NGLogEvent e) {
        FStageController.DisplayLogEntry(e.LogEntry);
    }

    @Override
    public void handleClearLog() {
        FStageController.ClearLog();
    }

}
