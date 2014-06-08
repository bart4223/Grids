package Grids;

import Uniwork.Base.NGObject;
import Uniwork.Graphics.NGGeometryObject2D;

import java.util.ArrayList;

public class XMLLayer extends NGObject{
    public XMLLayer() { }
    protected String Name = "";
    protected String Description;
    protected String ObjectColor = "";
    protected int ZOrder = 0;
    protected ArrayList<NGGeometryObject2D> GeometryObjects;
    protected String Imagename = "";
    public void setName(String value) { Name = value;}
    public String getName() { return Name; }
    public void setDescription(String value) { Description = value;}
    public String getDescription() { return Description; }
    public void setZOrder(int value) { ZOrder = value;}
    public int getZOrder() { return ZOrder; }
    public void setObjectColor(String value) { ObjectColor = value;}
    public String getObjectColor() { return ObjectColor; }
    public void setGeometryObjects(ArrayList<NGGeometryObject2D> value) { GeometryObjects = value;}
    public ArrayList<NGGeometryObject2D> getGeometryObjects() { return GeometryObjects; }
    public void setImagename(String value) { Imagename = value;}
    public String getImagename() { return Imagename; }
}
