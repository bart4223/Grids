package Grids;

public interface LayerEventListener {

    public void handleAddObject(LayerAddObjectEvent e);

    public void handleRemoveObject(LayerRemoveObjectEvent e);

    public void handleSelectObject(LayerSelectObjectEvent e);

    public void handleUnselectObject(LayerUnselectObjectEvent e);

}
