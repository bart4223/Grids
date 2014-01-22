package Grids;

import Uniwork.Graphics.Point2D;

public class LayerDeletePointEvent extends java.util.EventObject {

    public String LayerName;
    public Point2D Point;

    public LayerDeletePointEvent(Object source) {
        super(source);
    }
}
