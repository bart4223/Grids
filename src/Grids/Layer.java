package Grids;

import Uniwork.Graphics.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Layer implements Comparable<Layer> {

    protected String FName;
    protected String FDescription;
    protected ArrayList<Point2D> FPoints;
    protected Color FPointColor;
    protected List FEventListeners;
    protected Integer FZOrder;

    protected synchronized void RaiseAddPointEvent(Point2D aPoint) {
        LayerAddPointEvent lEvent = new LayerAddPointEvent(this);
        lEvent.LayerName = FName;
        lEvent.Point = aPoint;
        for (Object FEventListener : FEventListeners) {
            ((LayerEventListener)FEventListener).handleAddPoint(lEvent);
        }
    }

    protected synchronized void RaiseDeletePointEvent(Point2D aPoint) {
        LayerAddPointEvent lEvent = new LayerAddPointEvent(this);
        lEvent.LayerName = FName;
        lEvent.Point = aPoint;
        for (Object FEventListener : FEventListeners) {
            ((LayerEventListener)FEventListener).handleAddPoint(lEvent);
        }
    }

    public Layer(String aName, String aDescription, Color aColor) {
        FEventListeners= new ArrayList();
        FPoints = new ArrayList<Point2D>();
        FName = aName;
        FDescription = aDescription;
        FPointColor = aColor;
        FZOrder = 0;
    }

    @Override
    public String toString() {
        return getName() + "-" + getDescription() + "-" + getZOrder();
    }

    @Override
    public int compareTo(Layer o) {
        Integer myZOrder = getZOrder();
        Integer oZOrder = o.getZOrder();
        return myZOrder.compareTo(oZOrder);
    }

    public void addPoint(Point2D aPoint) {
        FPoints.add(aPoint);
        RaiseAddPointEvent(aPoint);
    }

    public Point2D addPoint(int aX, int aY) {
        Point2D Point = getPointInLayer(aX, aY);
        if (Point == null ) {
            Point = new Point2D(aX, aY);
            addPoint(Point);
        }
        return Point;
    }

    public void deletePoint(Point2D aPoint) {
        FPoints.remove(aPoint);
        RaiseDeletePointEvent(aPoint);
    }

    public Point2D getPointInLayer(int aX, int aY) {
        for (Point2D Point : FPoints)
            if (Point.getXAsInt() == aX && Point.getYAsInt() == aY) {
                return Point;
            }
        return null;
    }

    public String getName() {
        return FName;
    }

    public String getDescription() {
        return FDescription;
    }

    public Integer getZOrder() {
        return FZOrder;
    }

    public void setZOrder(Integer aValue) {
        FZOrder = aValue;
    }

    public Color getPointColor() {
        return FPointColor;
    }

    public ArrayList<Point2D> getPoints() {
        return FPoints;
    }

    public synchronized void addEventListener(LayerEventListener aListener)  {
        FEventListeners.add(aListener);
    }

    public synchronized void removeEventListener(LayerEventListener aListener)   {
        FEventListeners.remove(aListener);
    }

}
