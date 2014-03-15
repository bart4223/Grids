package Grids;

import Uniwork.Graphics.GeometryObject2D;

public class LayerSelectObjectEvent extends java.util.EventObject {

    public String LayerName;
    public GeometryObject2D Object;

    public LayerSelectObjectEvent(Object source) {
        super(source);
    }
}
