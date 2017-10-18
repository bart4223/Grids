package Grids;

import Uniwork.Appl.NGApplicationProtocol;
import Uniwork.Base.NGObject;
import Uniwork.Graphics.NGRegion2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class GridAnalyzer extends NGObject {

    protected class LayerAnalyzeItem {

        protected GridLayer FGridLayer;
        protected Integer FCount;
        protected Double FBrightness;
        protected Double FSaturation;
        protected Double FHue;

        public LayerAnalyzeItem(GridLayer aGridLayer) {
            FGridLayer = aGridLayer;
            FCount = 0;
            FHue = aGridLayer.getObjectColor().getHue();
            FSaturation = aGridLayer.getObjectColor().getSaturation();
            FBrightness = aGridLayer.getObjectColor().getBrightness();
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

        public Double getBrightness() {
            return FBrightness;
        }

        public Double getSaturation() {
            return FSaturation;
        }

        public Double getHue() {
            return FHue;
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
        Collections.sort(FLayers, new Comparator<LayerAnalyzeItem>() {
            @Override
            public int compare(LayerAnalyzeItem s1, LayerAnalyzeItem s2) {
                Double v1 = s1.getBrightness();
                Double v2 = s2.getBrightness();
                if (v1 < v2) {
                    return 1;
                } if (v1 > v2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
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
