package Grids;

import Uniwork.Graphics.Circle;
import Uniwork.Graphics.GeometryObject2D;
import Uniwork.Graphics.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Layer implements Comparable<Layer> {

    protected String FName;
    protected String FDescription;
    protected ArrayList<GeometryObject2D> FObjects;
    protected Color FObjectColor;
    protected List FEventListeners;
    protected Integer FZOrder;

    protected synchronized void RaiseAddObjectEvent(GeometryObject2D aObject) {
        LayerAddObjectEvent lEvent = new LayerAddObjectEvent(this);
        lEvent.LayerName = FName;
        lEvent.Object = aObject;
        for (Object FEventListener : FEventListeners) {
            ((LayerEventListener)FEventListener).handleAddObject(lEvent);
        }
    }

    protected synchronized void RaiseRemoveObjectEvent(GeometryObject2D aObject) {
        LayerRemoveObjectEvent lEvent = new LayerRemoveObjectEvent(this);
        lEvent.LayerName = FName;
        lEvent.Object = aObject;
        for (Object FEventListener : FEventListeners) {
            ((LayerEventListener)FEventListener).handleRemoveObject(lEvent);
        }
    }

    public Layer(String aName, String aDescription, Color aColor) {
        FEventListeners= new ArrayList();
        FObjects = new ArrayList<GeometryObject2D>();
        FName = aName;
        FDescription = aDescription;
        FObjectColor = aColor;
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

    public void addObject(GeometryObject2D aObject) {
        FObjects.add(aObject);
        RaiseAddObjectEvent(aObject);
    }

    public Point2D addPoint(int aX, int aY) {
        Point2D Point = getPointInLayer(aX, aY);
        if (Point == null ) {
            Point = new Point2D(aX, aY);
            addObject(Point);
        }
        return Point;
    }

    public Circle addCircle(int aX, int aY, int aRadius) {
        Circle Circle = new Circle(aX, aY, aRadius);
        addObject(Circle);
        return Circle;
    }

    public void removeObject(GeometryObject2D aObject) {
        FObjects.remove(aObject);
        RaiseRemoveObjectEvent(aObject);
    }

    public void removeObjects() {
        while (FObjects.size() > 0) {
            removeObject(FObjects.get(0));
        }
    }

    public Point2D getPointInLayer(int aX, int aY) {
        for (GeometryObject2D Object : FObjects)
            if (Object instanceof Point2D) {
                Point2D Point = (Point2D)Object;
                if (Point.getXAsInt() == aX && Point.getYAsInt() == aY) {
                    return Point;
                }
            }
        return null;
    }

    public void setName(String aName) {
        FName = aName;
    }

    public String getName() {
        return FName;
    }

    public void setDescription(String aDescription) {
        FDescription = aDescription;
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

    public Color getObjectColor() {
        return FObjectColor;
    }

    public ArrayList<GeometryObject2D> getObjects() {
        return FObjects;
    }

    public synchronized void addEventListener(LayerEventListener aListener)  {
        FEventListeners.add(aListener);
    }

    public synchronized void removeEventListener(LayerEventListener aListener)   {
        FEventListeners.remove(aListener);
    }

}
