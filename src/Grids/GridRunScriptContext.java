package Grids;

import Uniwork.Base.NGObject;

public class GridRunScriptContext extends NGObject {

    protected Grid FGrid;

    public GridRunScriptContext(Grid aGrid) {
        super();
        FGrid = aGrid;
    }

    public Grid getGrid() {
        return FGrid;
    }

}
