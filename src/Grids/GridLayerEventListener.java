package Grids;

public interface GridLayerEventListener {

    public void handleAddObject(GridLayerAddObjectEvent e);

    public void handleRemoveObject(GridLayerRemoveObjectEvent e);

    public void handleSelectObject(GridLayerSelectObjectEvent e);

    public void handleUnselectObject(GridLayerUnselectObjectEvent e);

}
