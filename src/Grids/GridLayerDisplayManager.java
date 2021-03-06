package Grids;

import Uniwork.Graphics.NGGeometryObject2D;
import Uniwork.Visuals.NGGeometryObject2DDisplayManager;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class GridLayerDisplayManager extends NGGeometryObject2DDisplayManager {

    protected CopyOnWriteArrayList<GridLayer> FLayers;

    protected void PrepareGridLayers() {
        FLayers.clear();
        for (GridLayer Layer : Layers) {
            FLayers.add(Layer);
        }
        Collections.sort(FLayers);
    }

    protected void ClearCanvas() {
        if (FBackgroundColor.equals(Color.TRANSPARENT)) {
            FGC.clearRect(0, 0, getViewWidth(), getViewHeight());
        }
        else {
            FGC.setFill(FBackgroundColor);
            FGC.fillRect(0, 0, getViewWidth(), getViewHeight());
        }
    }

    @Override
    protected void InternalRender() {
        ClearCanvas();
        PrepareGridLayers();
        for (GridLayer Layer : FLayers) {
            if (Layer.getObjects().size() > 0) {
                for (NGGeometryObject2D Object : Layer.getObjects()) {
                    setImageName(Layer.getImageName());
                    GeometryObject = Object;
                    GeometryObjectColor = Layer.getObjectColor();
                    Selected = Layer.isObjectSelected(Object);
                    super.InternalRender();
                }
            }
        }
    }

    public GridLayerDisplayManager(Canvas aCanvas) {
        this(aCanvas, "");
    }

    public GridLayerDisplayManager(Canvas aCanvas, String aName) {
        super(aCanvas, aName);
        FLayers = new CopyOnWriteArrayList<GridLayer>();
        Layers = null;
    }

    public CopyOnWriteArrayList<GridLayer> Layers;

}
