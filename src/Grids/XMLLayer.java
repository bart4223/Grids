package Grids;

import Uniwork.Graphics.GeometryObject2D;

import java.util.ArrayList;

public class XMLLayer {
    public XMLLayer() { }
    protected String Name = "";
    protected String Description;
    protected String ObjectColor = "";
    protected int ZOrder = 0;
    protected ArrayList<GeometryObject2D> GeometryObjects;
    public void setName(String value) { Name = value;}
    public String getName() { return Name; }
    public void setDescription(String value) { Description = value;}
    public String getDescription() { return Description; }
    public void setZOrder(int value) { ZOrder = value;}
    public int getZOrder() { return ZOrder; }
    public void setObjectColor(String value) { ObjectColor = value;}
    public String getObjectColor() { return ObjectColor; }
    public void setGeometryObjects(ArrayList<GeometryObject2D> value) { GeometryObjects = value;}
    public ArrayList<GeometryObject2D> getGeometryObjects() { return GeometryObjects; }
}
