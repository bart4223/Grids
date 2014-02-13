package Grids;

import Uniwork.Graphics.GeometryObject2D;

public class LayerAddObjectEvent extends java.util.EventObject {

    public String LayerName;
    public GeometryObject2D Object;

    public LayerAddObjectEvent(Object source) {
        super(source);
    }

}
