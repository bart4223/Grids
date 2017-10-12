package Grids;

import Uniwork.Appl.NGApplicationProtocol;
import Uniwork.Base.NGObject;
import Uniwork.Graphics.NGRegion2D;

import java.util.ArrayList;
import java.util.Iterator;

public class GridAnalyzer extends NGObject {

    protected class LayerAnalyzeItem {

        protected GridLayer FGridLayer;
        protected Integer FCount;

        public LayerAnalyzeItem(GridLayer aGridLayer) {
            FGridLayer = aGridLayer;
            FCount = 0;
        }

        public void addCount() {
            FCount = FCount + 1;
        }

        public Integer getCount() {
            return FCount;
        }

        public GridLayer getLayer() {
            return FGridLayer;
        }

    }

    protected Grid FGrid;
    protected ArrayList<LayerAnalyzeItem> FLayers;

    protected void AnalyzeLayers(NGApplicationProtocol aProtocol) {
        FLayers.clear();
        Iterator<GridLayer> itr = FGrid.getLayerIterator();
        while (itr.hasNext()) {
            GridLayer layer = itr.next();
            FLayers.add(new LayerAnalyzeItem(layer));
        }
        for(double y = 0; y <= getGrid().getStageController().getLayerHeight(); y = y + FGrid.getMegaGridPixelSize()) {
            for(double x = 0; x <= getGrid().getStageController().getLayerWidth(); x = x + FGrid.getMegaGridPixelSize()) {
                NGRegion2D region = new NGRegion2D(x, y, x + FGrid.getMegaGridPixelSize(), y + FGrid.getMegaGridPixelSize());
                GridLayer MaxLayer = FGrid.getLayerWithMaxPoints(region);
                for (LayerAnalyzeItem item: FLayers) {
                    if (item.getLayer().equals(MaxLayer)) {
                        item.addCount();
                        break;
                    }
                }
            }
        }
    }

    public GridAnalyzer(Grid aGrid) {
        super();
        FGrid = aGrid;
        FLayers = new ArrayList<LayerAnalyzeItem>();
    }

    public NGApplicationProtocol Analyze() {
        NGApplicationProtocol res = NGApplicationProtocol.Open();
        res.writeInfo("Common");
        res.writeInfo(String.format("Layer count=%d", FGrid.getLayerCount()));
        res.writeInfo(String.format("Grid distance=%d", FGrid.getGridDistance()));
        if (FGrid.getStageController().IsShowMegaPixel()) {
            res.writeInfo("MegaPixel");
            res.writeInfo(String.format("Pixel Size=%d", FGrid.getMegaGridPixelSize()));
            AnalyzeLayers(res);
            Integer TotalCount = 0;
            for (LayerAnalyzeItem item: FLayers) {
                res.writeInfo(String.format("Layer %s", item.getLayer().getName()));
                res.writeInfo(String.format("Count=%d", item.getCount()));
                TotalCount = TotalCount + item.getCount();
            }
            res.writeInfo(String.format("TotalCount=%d", TotalCount));
        }
        res.Close();
        return res;
    }

    public Grid getGrid() {
        return FGrid;
    }

}
