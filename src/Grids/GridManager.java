package Grids;

import Uniwork.Base.NGObjectDeserializer;
import Uniwork.Base.NGObjectXMLDeserializerFile;
import Uniwork.Base.NGObjectSerializer;
import Uniwork.Base.NGObjectXMLSerializerFile;
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
        grid.writeLog("Welcome to Grids have fun...");
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
            fileChooser.setTitle("Save as");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
            fileChooser.getExtensionFilters().add(extFilter);
            File chosenFile = fileChooser.showSaveDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                NGObjectSerializer Serializer = new NGObjectXMLSerializerFile(aGrid, XMLGrid.class, chosenFile.getPath());
                Serializer.setLogManager(aGrid.getLogManager());
                Serializer.serializeObject();
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
            fileChooser.setTitle("Load from");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
            fileChooser.getExtensionFilters().add(extFilter);
            File chosenFile = fileChooser.showOpenDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                NGObjectDeserializer Deserializer = new NGObjectXMLDeserializerFile(aGrid, chosenFile.getPath());
                Deserializer.setLogManager(aGrid.getLogManager());
                Deserializer.deserializeObject();
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
