package Grids;

import Uniwork.Base.ObjectSerializer;
import Uniwork.Graphics.GeometryObject2D;
import java.util.ArrayList;

public class GridSerializer extends ObjectSerializer{

    public GridSerializer(String aFilename, Grid aGrid)  throws Exception {
        super(aFilename, aGrid);
    }

    @Override
    protected void DoTransform() {
        XMLLayer XMLLayer;
        ArrayList<XMLLayer> XMLLayers;
        ArrayList<GeometryObject2D> XMLGeoObjects;
        XMLGrid XMLGrid = new XMLGrid();
        setXMLObject(XMLGrid);
        Grid lGrid = (Grid)FObject;
        XMLGrid.setGridDistance(lGrid.getGridDistance());
        XMLGrid.setGridColor(lGrid.getGridColor().toString());
        XMLGrid.setCurrentLayer(lGrid.getCurrentLayer().getName());
        XMLLayers = new ArrayList<XMLLayer>();
        XMLGrid.setLayers(XMLLayers);
        for (Layer layer : lGrid.getLayers()) {
            XMLLayer = new XMLLayer();
            XMLLayers.add(XMLLayer);
            XMLLayer.setName(layer.getName());
            XMLLayer.setDescription(layer.getDescription());
            XMLLayer.setObjectColor(layer.getObjectColor().toString());
            XMLLayer.setZOrder(layer.getZOrder());
            XMLGeoObjects = new ArrayList<GeometryObject2D>();
            XMLLayer.setGeometryObjects(XMLGeoObjects);
            for (GeometryObject2D GeoObject : layer.getObjects()) {
                XMLGeoObjects.add(GeoObject);
            }
        }
    }
}
