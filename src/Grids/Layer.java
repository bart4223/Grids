package Grids;

import Uniwork.Graphics.Circle;
import Uniwork.Graphics.GeometryObject;
import Uniwork.Graphics.GeometryObject2D;
import Uniwork.Graphics.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Layer implements Comparable<Layer> {

    protected String FName;
    protected String FDescription;
    protected ArrayList<GeometryObject2D> FObjects;
    protected ArrayList<GeometryObject2D> FSelectedObjects;
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

    protected synchronized void RaiseSelectObjectEvent(GeometryObject2D aObject) {
        LayerRemoveObjectEvent lEvent = new LayerRemoveObjectEvent(this);
        lEvent.LayerName = FName;
        lEvent.Object = aObject;
        for (Object FEventListener : FEventListeners) {
            ((LayerEventListener)FEventListener).handleRemoveObject(lEvent);
        }
    }

    protected synchronized void RaiseUnselectObjectEvent(GeometryObject2D aObject) {
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
        FSelectedObjects = new ArrayList<GeometryObject2D>();
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
        Point2D Point = null;
        GeometryObject2D layerObject = getObjectInLayer(aX, aY);
        if (!(layerObject instanceof Point2D)) {
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
        unselectObject(aObject);
        FObjects.remove(aObject);
        RaiseRemoveObjectEvent(aObject);
    }

    public void removeObjects() {
        while (FObjects.size() > 0) {
            removeObject(FObjects.get(0));
        }
    }

    public GeometryObject2D getObjectInLayer(int aX, int aY) {
        for (GeometryObject2D Object : FObjects)
            if (Object instanceof Point2D) {
                Point2D Point = (Point2D)Object;
                if (Point.getXAsInt() == aX && Point.getYAsInt() == aY) {
                    return Point;
                }
            }
            else if (Object instanceof Circle) {
                Circle Circle = (Circle)Object;
                if (Circle.getMiddlePoint().getXAsInt() == aX && Circle.getMiddlePoint().getYAsInt() == aY) {
                    return Circle;
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

    public ArrayList<GeometryObject2D> getSelected() {
        return FSelectedObjects;
    }

    public void selectObject(GeometryObject2D aObject) {
        for (GeometryObject2D Object : FObjects)
            if (Object.equals(aObject))  {
                FSelectedObjects.add(aObject);
                RaiseSelectObjectEvent(aObject);
                return;
            }
    }

    public void unselectObject(GeometryObject2D aObject) {
        for (GeometryObject2D Object : FSelectedObjects)
            if (Object.equals(aObject))  {
                FSelectedObjects.remove(aObject);
                RaiseUnselectObjectEvent(aObject);
                return;
            }
    }

    public boolean isObjectSelected(GeometryObject2D aObject) {
        for (GeometryObject2D Object : FSelectedObjects)
            if (Object.equals(aObject))  {
                return true;
            }
        return false;
    }

    public void toggleObjectSelected(GeometryObject2D aObject) {
        if (isObjectSelected(aObject)) {
            unselectObject(aObject);
        }
        else {
            selectObject(aObject);
        }
    }

    public void clearSelectedObjects() {
        while (FSelectedObjects.size() > 0) {
            unselectObject(FSelectedObjects.get(0));
        }
    }

    public void removeSelectedObjects() {
        while (FSelectedObjects.size() > 0) {
            removeObject(FSelectedObjects.get(0));
        }
    }

    public synchronized void addEventListener(LayerEventListener aListener)  {
        FEventListeners.add(aListener);
    }

    public synchronized void removeEventListener(LayerEventListener aListener)   {
        FEventListeners.remove(aListener);
    }

}
