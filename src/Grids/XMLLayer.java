package Grids;

import Uniwork.Base.NGObject;
import Uniwork.Graphics.NGGeometryObject2D;

import java.util.concurrent.CopyOnWriteArrayList;

public class XMLLayer extends NGObject{
    public XMLLayer() { }
    protected String Name = "";
    protected String Description;
    protected String ObjectColor = "";
    protected int ZOrder = 0;
    protected CopyOnWriteArrayList<NGGeometryObject2D> GeometryObjects;
    protected String Imagename = "";
    public void setName(String value) { Name = value;}
    public String getName() { return Name; }
    public void setDescription(String value) { Description = value;}
    public String getDescription() { return Description; }
    public void setZOrder(int value) { ZOrder = value;}
    public int getZOrder() { return ZOrder; }
    public void setObjectColor(String value) { ObjectColor = value;}
    public String getObjectColor() { return ObjectColor; }
    public void setGeometryObjects(CopyOnWriteArrayList<NGGeometryObject2D> value) { GeometryObjects = value;}
    public CopyOnWriteArrayList<NGGeometryObject2D> getGeometryObjects() { return GeometryObjects; }
    public void setImagename(String value) { Imagename = value;}
    public String getImagename() { return Imagename; }
}
