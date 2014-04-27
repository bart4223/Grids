package Grids;

import Uniwork.Graphics.NGGeometryObject2D;

public class GridLayerSelectObjectEvent extends java.util.EventObject {

    public String LayerName;
    public NGGeometryObject2D Object;

    public GridLayerSelectObjectEvent(Object source) {
        super(source);
    }
}
