package Grids;

import Uniwork.Base.NGObjectSerializer;
import Uniwork.Graphics.NGGeometryObject2D;

import java.util.ArrayList;

public class GridSerializer extends NGObjectSerializer {

    public GridSerializer(String aFilename, Grid aGrid)  throws Exception {
        super(aFilename, aGrid);
    }

    @Override
    protected void DoTransform() {
        XMLLayer XMLLayer;
        ArrayList<XMLLayer> XMLLayers;
        ArrayList<NGGeometryObject2D> XMLGeoObjects;
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
            XMLGeoObjects = new ArrayList<NGGeometryObject2D>();
            XMLLayer.setGeometryObjects(XMLGeoObjects);
            for (NGGeometryObject2D GeoObject : layer.getObjects()) {
                XMLGeoObjects.add(GeoObject);
            }
        }
    }
}
