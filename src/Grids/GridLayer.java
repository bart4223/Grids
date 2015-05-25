package Grids;

import Uniwork.Graphics.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class GridLayer implements Comparable<GridLayer> {

    protected String FName;
    protected String FDescription;
    protected ArrayList<NGGeometryObject2D> FObjects;
    protected ArrayList<NGGeometryObject2D> FSelectedObjects;
    protected Color FObjectColor;
    protected List FEventListeners;
    protected Integer FZOrder;
    protected String FImageName;

    protected synchronized void RaiseAddObjectEvent(NGGeometryObject2D aObject) {
        GridLayerAddObjectEvent lEvent = new GridLayerAddObjectEvent(this);
        lEvent.LayerName = FName;
        lEvent.Object = aObject;
        for (Object FEventListener : FEventListeners) {
            ((GridLayerEventListener)FEventListener).handleAddObject(lEvent);
        }
    }

    protected synchronized void RaiseRemoveObjectEvent(NGGeometryObject2D aObject) {
        GridLayerRemoveObjectEvent lEvent = new GridLayerRemoveObjectEvent(this);
        lEvent.LayerName = FName;
        lEvent.Object = aObject;
        for (Object FEventListener : FEventListeners) {
            ((GridLayerEventListener)FEventListener).handleRemoveObject(lEvent);
        }
    }

    protected synchronized void RaiseSelectObjectEvent(NGGeometryObject2D aObject) {
        GridLayerRemoveObjectEvent lEvent = new GridLayerRemoveObjectEvent(this);
        lEvent.LayerName = FName;
        lEvent.Object = aObject;
        for (Object FEventListener : FEventListeners) {
            ((GridLayerEventListener)FEventListener).handleRemoveObject(lEvent);
        }
    }

    protected synchronized void RaiseUnselectObjectEvent(NGGeometryObject2D aObject) {
        GridLayerRemoveObjectEvent lEvent = new GridLayerRemoveObjectEvent(this);
        lEvent.LayerName = FName;
        lEvent.Object = aObject;
        for (Object FEventListener : FEventListeners) {
            ((GridLayerEventListener)FEventListener).handleRemoveObject(lEvent);
        }
    }

    public GridLayer(String aName, String aDescription, Color aColor) {
        FEventListeners= new ArrayList();
        FObjects = new ArrayList<NGGeometryObject2D>();
        FSelectedObjects = new ArrayList<NGGeometryObject2D>();
        FName = aName;
        FDescription = aDescription;
        FObjectColor = aColor;
        FZOrder = 0;
        FImageName = "";
    }

    @Override
    public String toString() {
        return getName() + "-" + getDescription() + "-" + getZOrder();
    }

    @Override
    public int compareTo(GridLayer o) {
        Integer myZOrder = getZOrder();
        Integer oZOrder = o.getZOrder();
        return myZOrder.compareTo(oZOrder);
    }

    public void addObject(NGGeometryObject2D aObject) {
        FObjects.add(aObject);
        RaiseAddObjectEvent(aObject);
    }

    public NGPoint2D addPoint(int aX, int aY) {
        NGPoint2D Point = null;
        NGGeometryObject2D layerObject = getObjectInLayer(aX, aY);
        if (!(layerObject instanceof NGPoint2D)) {
            Point = new NGPoint2D(aX, aY);
            addObject(Point);
        }
        return Point;
    }

    public NGLine2D addLine(int aAX, int aAY, int aBX, int aBY) {
        NGLine2D Line = new NGLine2D(aAX, aAY, aBX, aBY);
        addObject(Line);
        return Line;
    }

    public NGCircle addCircle(int aX, int aY, int aRadius) {
        NGCircle Circle = new NGCircle(aX, aY, aRadius);
        addObject(Circle);
        return Circle;
    }

    public NGEllipse addEllipse(int aX, int aY, int aRadiusX, int aRadiusY) {
        NGEllipse Ellipse = new NGEllipse(aX, aY, aRadiusX, aRadiusY);
        addObject(Ellipse);
        return Ellipse;
    }

    public NGQuadrat addQuadrat(int aX, int aY, int aA) {
        NGQuadrat Quadrat = new NGQuadrat(aA, aX, aY);
        addObject(Quadrat);
        return Quadrat;
    }

    public NGRectangle addRectangle(int aX, int aY, int aA, int aB) {
        NGRectangle Rectangle = new NGRectangle(aA, aB, aX, aY);
        addObject(Rectangle);
        return Rectangle;
    }

    public void removeObject(NGGeometryObject2D aObject) {
        unselectObject(aObject);
        FObjects.remove(aObject);
        RaiseRemoveObjectEvent(aObject);
    }

    public void removeObjects() {
        while (FObjects.size() > 0) {
            removeObject(FObjects.get(0));
        }
    }

    public NGGeometryObject2D getObjectInLayer(int aX, int aY) {
        for (NGGeometryObject2D Object : FObjects)
            if (Object instanceof NGPoint2D) {
                NGPoint2D Point = (NGPoint2D)Object;
                if (Point.getXAsInt() == aX && Point.getYAsInt() == aY) {
                    return Point;
                }
            }
            else if (Object instanceof NGEllipse) {
                NGEllipse Ellipse = (NGEllipse)Object;
                if (Ellipse.getMiddlePoint().getXAsInt() == aX && Ellipse.getMiddlePoint().getYAsInt() == aY) {
                    return Ellipse;
                }
            }
            else if (Object instanceof NGRectangle) {
                NGRectangle Rectangle = (NGRectangle)Object;
                if (Rectangle.getMiddlePoint().getXAsInt() == aX && Rectangle.getMiddlePoint().getYAsInt() == aY) {
                    return Rectangle;
                }
            }
            else if (Object instanceof NGLine2D) {
                NGLine2D Line = (NGLine2D)Object;
                if ((Line.getA().getXAsInt() == aX && Line.getA().getYAsInt() == aY) || ((Line.getB().getXAsInt() == aX && Line.getB().getYAsInt() == aY))) {
                    return Line;
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

    public void setObjectColor(Color aValue) {
        FObjectColor = aValue;
    }

    public void setImageName(String aImageName) {
        FImageName = aImageName;
    }

    public String getImageName() {
        return FImageName;
    }

    public ArrayList<NGGeometryObject2D> getObjects() {
        return FObjects;
    }

    public ArrayList<NGGeometryObject2D> getSelected() {
        return FSelectedObjects;
    }

    public void selectObject(NGGeometryObject2D aObject) {
        for (NGGeometryObject2D Object : FObjects)
            if (Object.equals(aObject))  {
                FSelectedObjects.add(aObject);
                RaiseSelectObjectEvent(aObject);
                return;
            }
    }

    public void unselectObject(NGGeometryObject2D aObject) {
        for (NGGeometryObject2D Object : FSelectedObjects)
            if (Object.equals(aObject))  {
                FSelectedObjects.remove(aObject);
                RaiseUnselectObjectEvent(aObject);
                return;
            }
    }

    public boolean isObjectSelected(NGGeometryObject2D aObject) {
        for (NGGeometryObject2D Object : FSelectedObjects)
            if (Object.equals(aObject))  {
                return true;
            }
        return false;
    }

    public boolean toggleObjectSelected(NGGeometryObject2D aObject) {
        if (isObjectSelected(aObject)) {
            unselectObject(aObject);
        }
        else {
            selectObject(aObject);
        }
        return isObjectSelected(aObject);
    }

    public void SelectAllObjects() {
        for (NGGeometryObject2D obj : FObjects) {
            selectObject(obj);
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

    public synchronized void addEventListener(GridLayerEventListener aListener)  {
        FEventListeners.add(aListener);
    }

    public synchronized void removeEventListener(GridLayerEventListener aListener)   {
        FEventListeners.remove(aListener);
    }

}
