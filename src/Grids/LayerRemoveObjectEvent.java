package Grids;

import Uniwork.Graphics.NGGeometryObject2D;

public class LayerRemoveObjectEvent extends java.util.EventObject {

    public String LayerName;
    public NGGeometryObject2D Object;

    public LayerRemoveObjectEvent(Object source) {
        super(source);
    }
}
