package Grids;

import Uniwork.Graphics.GeometryObject2D;

public class LayerRemoveObjectEvent extends java.util.EventObject {

    public String LayerName;
    public GeometryObject2D Object;

    public LayerRemoveObjectEvent(Object source) {
        super(source);
    }
}
