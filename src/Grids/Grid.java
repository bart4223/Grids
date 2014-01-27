package Grids;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.*;
import static java.lang.Thread.sleep;

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

    protected void drawCircleBresenham(int aX, int aY, int aRadius) {
        if (FCurrentLayer == null) return;
        int f = 1 - aRadius;
        int ddF_x = 0;
        int ddF_y = -2 * aRadius;
        int x = 0;
        int y = aRadius;
        FCurrentLayer.addPoint(aX, aY + aRadius);
        FCurrentLayer.addPoint(aX, aY - aRadius);
        FCurrentLayer.addPoint(aX + aRadius, aY);
        FCurrentLayer.addPoint(aX - aRadius, aY);
        while(x < y)
        {
            if(f >= 0)
            {
                y--;
                ddF_y += 2;
                f += ddF_y;
            }
            x++;
            ddF_x += 2;
            f += ddF_x + 1;
            FCurrentLayer.addPoint(aX + x, aY + y);
            FCurrentLayer.addPoint(aX - x, aY + y);
            FCurrentLayer.addPoint(aX + x, aY - y);
            FCurrentLayer.addPoint(aX - x, aY - y);
            FCurrentLayer.addPoint(aX + y, aY + x);
            FCurrentLayer.addPoint(aX - y, aY + x);
            FCurrentLayer.addPoint(aX + y, aY - x);
            FCurrentLayer.addPoint(aX - y, aY - x);
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

    public void drawCircle(int aX, int aY, int aRadius) {
        drawCircleBresenham(aX, aY, aRadius);
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
