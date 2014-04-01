package Grids;

import javafx.scene.paint.Color;
import java.util.ArrayList;

public class GridManager {

    protected ArrayList<Grid> FGrids;

    public GridManager() {
        FGrids = new ArrayList<Grid>();
    }

    public void Initialize() {

    }

    public void Finalize() {
        for (Grid grid : FGrids) {
            grid.Finalize();
        }
    }

    public Grid addGrid(int aSize, Color aColor) {
        Grid grid = new Grid(this, aSize, aColor);
        FGrids.add(grid);
        grid.Initialize();
        return grid;
    }

    public void ShowStages() {
        for (Grid grid : FGrids) {
            grid.ShowStage();
        }
    }

    public void saveGrid(Grid aGrid) {
        try
        {
            GridSerializer Serializer = new GridSerializer("/Users/Nils/Desktop/grid.xml", aGrid);
            Serializer.Serialize();
            System.out.println("Saving ok...");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadGrid(Grid aGrid) {
        try
        {
            GridDeserializer Deserializer = new GridDeserializer("/Users/Nils/Desktop/grid.xml", aGrid);
            Deserializer.Deserialize();
            System.out.println("Loading ok...");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
