package Grids;

import Uniwork.Graphics.NGGeometryObject2D;

public class LayerUnselectObjectEvent extends java.util.EventObject {

    public String LayerName;
    public NGGeometryObject2D Object;

    public LayerUnselectObjectEvent(Object source) {
        super(source);
    }
}
