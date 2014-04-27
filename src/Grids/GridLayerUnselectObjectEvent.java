package Grids;

import Uniwork.Graphics.NGGeometryObject2D;

public class GridLayerUnselectObjectEvent extends java.util.EventObject {

    public String LayerName;
    public NGGeometryObject2D Object;

    public GridLayerUnselectObjectEvent(Object source) {
        super(source);
    }
}
