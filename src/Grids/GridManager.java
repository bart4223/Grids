package Grids;

import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
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
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            File chosenFile = fileChooser.showSaveDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                GridSerializer Serializer = new GridSerializer(chosenFile.getPath(), aGrid);
                Serializer.Serialize();
                aGrid.writeLog("Saving ok...");
            }
            else {
                aGrid.writeLog("Saving aborted...");
            }
        }
        catch (Exception e) {
            aGrid.writeLog(e.getMessage());
        }
    }

    public void loadGrid(Grid aGrid) {
        try
        {
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            File chosenFile = fileChooser.showOpenDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                GridDeserializer Deserializer = new GridDeserializer(chosenFile.getPath(), aGrid);
                Deserializer.Deserialize();
                aGrid.writeLog("Loading ok...");
            }
            else {
                aGrid.writeLog("Loading aborted...");
            }
        }
        catch (Exception e) {
            aGrid.writeLog(e.getMessage());
        }
    }

}
