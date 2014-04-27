package Grids;

import Uniwork.Graphics.NGGeometryObject2D;

public class LayerSelectObjectEvent extends java.util.EventObject {

    public String LayerName;
    public NGGeometryObject2D Object;

    public LayerSelectObjectEvent(Object source) {
        super(source);
    }
}
