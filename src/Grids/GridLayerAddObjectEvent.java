package Grids;

import Uniwork.Graphics.NGGeometryObject2D;

public class GridLayerAddObjectEvent extends java.util.EventObject {

    public String LayerName;
    public NGGeometryObject2D Object;

    public GridLayerAddObjectEvent(Object source) {
        super(source);
    }

}
