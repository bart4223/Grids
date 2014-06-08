package Grids;

import Uniwork.Graphics.NGGeometryObject2D;
import Uniwork.Visuals.NGGeometryObject2DDisplayManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

public class GridLayerDisplayManager extends NGGeometryObject2DDisplayManager {

    protected ArrayList<GridLayer> FLayers;

    protected void PrepareGridLayers() {
        FLayers.clear();
        for (GridLayer Layer : Layers) {
            FLayers.add(Layer);
        }
        Collections.sort(FLayers);
    }

    protected void ClearCanvas() {
        if (FBackgroundColor.equals(Color.TRANSPARENT)) {
            FGC.clearRect(0, 0, getWidth(), getHeight());
        }
        else {
            FGC.setFill(FBackgroundColor);
            FGC.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected void InternalRender() {
        ClearCanvas();
        PrepareGridLayers();
        for (GridLayer Layer : FLayers) {
            for (NGGeometryObject2D Object : Layer.getObjects()) {
                setImageName(Layer.getImageName());
                GeometryObject = Object;
                GeometryObjectColor = Layer.getObjectColor();
                Selected = Layer.isObjectSelected(Object);
                super.InternalRender();
            }
        }
    }

    public GridLayerDisplayManager(Canvas aCanvas) {
        this(aCanvas, "");
    }

    public GridLayerDisplayManager(Canvas aCanvas, String aName) {
        super(aCanvas, aName);
        FLayers = new ArrayList<GridLayer>();
        Layers = null;
    }

    public ArrayList<GridLayer> Layers;

}
