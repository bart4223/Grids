package Grids;

import Uniwork.Appl.NGApplicationProtocol;
import Uniwork.Base.NGComponent;
import Uniwork.Base.NGSerializePropertyItem;
import Uniwork.Graphics.*;
import Uniwork.Misc.NGImageList;
import Uniwork.Misc.NGLogEvent;
import Uniwork.Misc.NGLogEventListener;
import Uniwork.Misc.NGLogManager;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Grid extends NGComponent implements GridLayerEventListener, NGLogEventListener {

    protected int FUpdateCount;
    protected Integer FGridDistance;
    protected Boolean FDrawGrid;
    protected Color FGridColor;
    protected Stage FStage;
    protected GridStageController FStageController;
    protected CopyOnWriteArrayList<GridLayer> FLayers;
    protected GridLayer FCurrentLayer;
    protected Random FGenerator;
    protected GridManager FGridManager;
    protected GridAnalyzer FGridAnalyzer;

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
            Scene Scene = new Scene(lRoot, 1000, 1000, Color.WHITE);
            FStage.setScene(Scene);
            FStage.setResizable(false);
            Scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    handleKeyPressed(keyEvent);
                }
            });
            Scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    handleKeyReleased(keyEvent);
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
            case ALT:
                FStageController.ShowInfoTooltip();
        }
    }

    protected void handleKeyReleased(KeyEvent e) {
        switch (e.getCode()) {
            case ALT:
                FStageController.HideInfoTooltip();
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
    protected void DoInitialize() {
        super.DoInitialize();
        CreateStage();
        FLogManager.addEventListener(this);
        addLayer("LAYER1", "Layer 1", Color.rgb(getRandomValue(255), getRandomValue(255), getRandomValue(255)), FLayers.size());
    }

    @Override
    protected void DoFinalize() {
        FLogManager.removeEventListener(this);
        CloseStage();
        super.DoFinalize();
    }

    @Override
    protected void DoAssignFrom(Object aObject) {
        GridLayer layer;
        XMLGrid XMLGrid = (XMLGrid)aObject;
        New(false);
        setGridDistance(XMLGrid.getGridDistance());
        setGridColor(Color.valueOf(XMLGrid.getGridColor()));
        for (XMLLayer XMLLayer : XMLGrid.getLayers()) {
            layer = addLayer(XMLLayer.getName(), XMLLayer.getDescription(), Color.valueOf(XMLLayer.getObjectColor()), FLayers.size());
            layer.setZOrder(XMLLayer.getZOrder());
            if (XMLLayer.getImagename().length() > 0) {
                layer.setImageName(XMLLayer.getImagename());
            }
            for (NGGeometryObject2D GeoObject : XMLLayer.getGeometryObjects()) {
                layer.addObject(GeoObject);
            }
        }
        setCurrentLayer(XMLGrid.getCurrentLayer());
    }

    @Override
    protected void DoAssignTo(Object aObject) {
        if (aObject instanceof XMLGrid) {
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
                XMLLayer.setImagename(layer.getImageName());
                for (NGGeometryObject2D GeoObject : layer.getObjects()) {
                    XMLGeoObjects.add(GeoObject);
                }
            }
        }
        else if (aObject instanceof NGSerializeGeometryObjectList) {
            Integer count = 0;
            NGSerializeGeometryObjectList SGOL = (NGSerializeGeometryObjectList)aObject;
            SGOL.setSGOS(new CopyOnWriteArrayList<NGSerializeGeometryObjectItem>());
            for (GridLayer layer : getLayers()) {
                for (NGGeometryObject2D go : layer.getObjects()) {
                    NGSerializeGeometryObjectItem sgoi = new NGSerializeGeometryObjectItem();
                    go.setID(layer.getName());
                    sgoi.setGO(go);
                    sgoi.setProps(new CopyOnWriteArrayList<NGSerializePropertyItem>());
                    NGSerializePropertyItem prop = new NGSerializePropertyItem();
                    prop.setName("NAME");
                    prop.setValue(layer.getName());
                    sgoi.getProps().add(prop);
                    prop = new NGSerializePropertyItem();
                    prop.setName("COLOR");
                    prop.setValue(layer.getObjectColor().toString());
                    sgoi.getProps().add(prop);
                    SGOL.getSGOS().add(sgoi);
                    count ++;
                }
            }
            writeLog(String.format("%d geometry object(s) exported...", count));
        }
    }

    public Grid(GridManager aGridManager, int aGridDistance, Color aColor) {
        super(aGridManager);
        FLayers = new CopyOnWriteArrayList<GridLayer>();
        FGenerator = new Random();
        FGridDistance = aGridDistance;
        FDrawGrid = true;
        FGridColor = aColor;
        FCurrentLayer = null;
        FGridManager = aGridManager;
        FLogManager = new NGLogManager();
        FUpdateCount = 0;
        FGridAnalyzer = new GridAnalyzer(this);
    }

    public void Shutdown() {
        FGridManager.Shutdown();
    }

    public void ShowStage(){
        FStage.show();
    }

    public void CloseStage(){
        FStageController.Finalize();
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

    public Integer getMegaGridPixelSize() {
        return FGridManager.getMegaGridPixelSize();
    }

    public void setGridColor(Color aValue) {
        FGridColor = aValue;
        UpdateStage(false, "");
    }

    public Boolean getDrawGrid() {
        return FDrawGrid;
    }

    public void setDrawGrid(Boolean aDrawGrid) {
        FDrawGrid = aDrawGrid;
        UpdateStage(true, "");
    }

    public Color getGridColor() {
        return FGridColor;
    }

    public GridLayer getLayerWithMaxPoints(NGRegion2D aRegion) {
        GridLayer res = null;
        Integer max = 0;
        for (GridLayer layer : FLayers) {
            Integer count = layer.getPointsInRegion(aRegion);
            if (count > max) {
                max = count;
                res = layer;
            }
        }
        return res;
    }

    public GridLayer addLayer(String aName, String aDescription, Color aColor, Integer aID) {
        GridLayer Layer = new GridLayer(aName, aDescription, aColor, aID);
        int lZOrder = getMaxZOrder() + 1;
        Layer.setZOrder(lZOrder);
        Layer.addEventListener(this);
        FLayers.add(Layer);
        setCurrentLayer(Layer);
        return Layer;
    }

    public GridLayer addLayer() {
        return(addLayer("LAYER" + (FLayers.size() + 1), "Layer " + (FLayers.size() + 1), Color.rgb(getRandomValue(255), getRandomValue(255), getRandomValue(255)), FLayers.size()));
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

    public void New() {
        New(true);
    }

    public void New(Boolean aOneLayer) {
        removeAllLayers();
        NGImageList.resetGlobal();
        if (aOneLayer) {
            addLayer();
        }
        writeLog("New grid...");
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

    public GridLayer getLayer(Color aColor) {
        for (GridLayer Layer : FLayers) {
            if (Layer.getObjectColor().equals(aColor)) {
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

    public CopyOnWriteArrayList<GridLayer> getLayers() {
        return FLayers;
    }

    public Iterator<GridLayer> getLayerIterator() {
        return FLayers.iterator();
    }

    public GridLayer getCurrentLayer() {
        return FCurrentLayer;
    }

    public void setCurrentLayerWithObject(int aX, int aY) {
        for (GridLayer layer : FLayers) {
            NGGeometryObject2D object = layer.getObjectInLayer(aX, aY);
            if (object != null) {
                setCurrentLayer(layer);
                return;
            }
        }
    }

    public Integer getLayerCount() {
        return FLayers.size();
    }

    public NGPoint2D CoordinatesToGridCoordinates(NGPoint2D aPoint) {
        return new NGPoint2D((int)(aPoint.getX() / getGridDistance()), (int)(aPoint.getY() / getGridDistance()));
    }

    public void RunScript(String aScript) {
        BeginUpdate();
        try {
            FGridManager.RunScript(this, aScript);
            EndUpdate();
        }
        catch (Exception e) {
            writeError(e.getMessage());
            EndUpdate();
        }
    }

    public void SaveAsGDF() {
        BeginUpdate();
        try {
            FGridManager.saveGridAsGDF(this);
            EndUpdate();
        }
        catch (Exception e) {
            writeError(e.getMessage());
            EndUpdate();
        }
    }

    public void SaveAsGOF() {
        BeginUpdate();
        try {
            FGridManager.saveGridAsGOF(this);
            EndUpdate();
        }
        catch (Exception e) {
            writeError(e.getMessage());
            EndUpdate();
        }
    }

    public void SaveAsPNG(Boolean aComplete) {
        BeginUpdate();
        try {
            FGridManager.saveGridImageAsPNG(this, aComplete);
            EndUpdate();
        }
        catch (Exception e) {
            writeError(e.getMessage());
            EndUpdate();
        }
    }

    public void LoadFromGDF() {
        BeginUpdate();
        try {
            FGridManager.loadGridFromGDF(this);
            EndUpdate();
        }
        catch (Exception e) {
            writeError(e.getMessage());
            EndUpdate();
        }
    }

    public void LoadFromImageWithCQ() {
        BeginUpdate();
        try {
            FGridManager.loadGridFromImageWithQC(this);
            EndUpdate();
        }
        catch (Exception e) {
            writeError(e.getMessage());
            EndUpdate();
        }
    }

    public void LoadFromImage() {
        BeginUpdate();
        try {
            FGridManager.loadGridFromImage(this);
            EndUpdate();
        }
        catch (Exception e) {
            writeError(e.getMessage());
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

    public GridStageController getStageController() {
        return FStageController;
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

    public void ShowMegaPixel() {
        FStageController.ShowMegaPixel();
    }

    public void HideMegaPixel() {
        FStageController.HideMegaPixel();
    }

    public void ShowGrid() {
        setDrawGrid(true);
    }

    public void HideGrid() {
        setDrawGrid(false);
    }

    public NGApplicationProtocol Analyze() {
        return FGridAnalyzer.Analyze();
    }

}
