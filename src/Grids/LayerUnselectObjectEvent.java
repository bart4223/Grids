package Grids;

import Uniwork.Graphics.GeometryObject2D;

public class LayerUnselectObjectEvent extends java.util.EventObject {

    public String LayerName;
    public GeometryObject2D Object;

    public LayerUnselectObjectEvent(Object source) {
        super(source);
    }
}
