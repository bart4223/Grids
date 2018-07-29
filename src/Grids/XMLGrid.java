package Grids;

import Uniwork.Base.NGObject;

import java.util.ArrayList;

public class XMLGrid extends NGObject{
    public XMLGrid() { }
    protected int GridDistance = 0;
    protected String GridColor = "";
    protected String CurrentLayer = "";
    protected ArrayList<XMLLayer> Layers;
    public void setGridDistance(int value) { GridDistance = value;}
    public int getGridDistance() { return GridDistance; }
    public void setGridColor(String value) { GridColor = value;}
    public String getGridColor() { return GridColor; }
    public void setCurrentLayer(String value) { CurrentLayer = value;}
    public String getCurrentLayer() { return CurrentLayer; }
    public void setLayers(ArrayList<XMLLayer> value) { Layers = value;}
    public ArrayList<XMLLayer> getLayers() { return Layers; }
}
