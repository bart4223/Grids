package Grids;

import Uniwork.Base.ObjectDeserializer;
import Uniwork.Graphics.GeometryObject2D;
import javafx.scene.paint.Color;

public class GridDeserializer extends ObjectDeserializer{

    public GridDeserializer(String aFilename, Grid aGrid) throws Exception {
        super(aFilename, aGrid);
    }

    @Override
    protected void DoTransform() {
        Layer layer;
        XMLGrid XMLGrid = (XMLGrid)FXMLObject;
        Grid lGrid = (Grid)FObject;
        lGrid.removeAllLayers();
        lGrid.setGridDistance(XMLGrid.getGridDistance());
        lGrid.setGridColor(Color.valueOf(XMLGrid.getGridColor()));
        for (XMLLayer XMLLayer : XMLGrid.getLayers()) {
            layer = lGrid.addLayer(XMLLayer.getName(), XMLLayer.getDescription(), Color.valueOf(XMLLayer.getObjectColor()));
            layer.setZOrder(XMLLayer.getZOrder());
            for (GeometryObject2D GeoObject : XMLLayer.getGeometryObjects()) {
                layer.addObject(GeoObject);
            }
        }
        lGrid.setCurrentLayer(XMLGrid.getCurrentLayer());
    }

}
