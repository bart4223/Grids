package Grids;

import Uniwork.Graphics.Point2D;

public class LayerAddPointEvent extends java.util.EventObject {

    public String LayerName;
    public Point2D Point;

    public LayerAddPointEvent(Object source) {
        super(source);
    }

}
