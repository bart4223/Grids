package Grids;

import Uniwork.Graphics.NGGeometryObject2D;

public class GridLayerRemoveObjectEvent extends java.util.EventObject {

    public String LayerName;
    public NGGeometryObject2D Object;

    public GridLayerRemoveObjectEvent(Object source) {
        super(source);
    }
}
