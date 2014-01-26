package Grids;

import Uniwork.Graphics.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.round;

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

    protected String getIDFromPoints(double aX, double aY) {
        Integer x = (int)round(aX);
        Integer y = (int)round(aY);
        return x.toString() + y.toString();

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
        aPoint.setID(getIDFromPoints(aPoint.getX(),aPoint.getY()));
        FPoints.add(aPoint);
        RaiseAddPointEvent(aPoint);
    }

    public Point2D addPoint(double aX, double aY) {
        Point2D Point = getPointInLayer(aX, aY);
        if (Point == null ) {
            Point = new Point2D(aX, aY);
            addPoint(Point);
            //System.out.println(Point.getID());
        }
        return Point;
    }

    public void deletePoint(Point2D aPoint) {
        FPoints.remove(aPoint);
        RaiseDeletePointEvent(aPoint);
    }

    public Point2D getPointInLayer(double aX, double aY) {
        String id = getIDFromPoints(aX, aY);
        for (Point2D Point : FPoints)
            if (Point.getID().equals(id)) {
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
