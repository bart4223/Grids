package Grids;

import Uniwork.Graphics.NGGeometryObject2D;

public class LayerAddObjectEvent extends java.util.EventObject {

    public String LayerName;
    public NGGeometryObject2D Object;

    public LayerAddObjectEvent(Object source) {
        super(source);
    }

}
