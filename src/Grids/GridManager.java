package Grids;

import Uniwork.Base.NGObjectDeserializer;
import Uniwork.Base.NGObjectXMLDeserializerFile;
import Uniwork.Base.NGObjectSerializer;
import Uniwork.Base.NGObjectXMLSerializerFile;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class GridManager {

    protected ArrayList<Grid> FGrids;
    protected Properties FConfiguration;
    protected int FImageFileSizeX;
    protected int FImageFileSizeY;

    protected void writeLog(String aText) {
        for (Grid grid : FGrids) {
            grid.writeLog(aText);
        }
    }

    protected void LoadConfiguration() {
        try {
            InputStream is = new FileInputStream("resources/config.grd");
            FConfiguration.load(is);
            FImageFileSizeX = Integer.parseInt(FConfiguration.getProperty("ImageFileSizeX"));
            FImageFileSizeY = Integer.parseInt(FConfiguration.getProperty("ImageFileSizeY"));
        }
        catch ( Exception e) {
            writeLog(e.getMessage());
        }
    }

    public GridManager() {
        FGrids = new ArrayList<Grid>();
        FConfiguration = new Properties();
        FImageFileSizeX = 8;
        FImageFileSizeY = 8;
    }

    public void Initialize() {
        for (Grid grid : FGrids) {
            grid.Initialize();
        }
        writeLog("Welcome to Grids have fun...");
        LoadConfiguration();
    }

    public void Finalize() {
        for (Grid grid : FGrids) {
            grid.Finalize();
        }
    }

    public Grid addGrid(int aSize, Color aColor) {
        Grid grid = new Grid(this, aSize, aColor);
        FGrids.add(grid);
        return grid;
    }

    public void ShowStages() {
        for (Grid grid : FGrids) {
            grid.ShowStage();
        }
    }

    public void saveGridAsXML(Grid aGrid) {
        try
        {
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            fileChooser.setTitle("Save as XML");
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

    public void saveGridImageAsPNG(Grid aGrid) {
        try
        {
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            fileChooser.setTitle("Save as PNG");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            File chosenFile = fileChooser.showSaveDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                WritableImage wim = new WritableImage(FImageFileSizeX, FImageFileSizeY);
                try {
                    SnapshotParameters parameter = new SnapshotParameters();
                    parameter.setFill(Color.TRANSPARENT);
                    aGrid.getStageController().getObjectLayer().snapshot(parameter, wim);
                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", new File(chosenFile.getPath()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }            }
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
            fileChooser.setTitle("Load from XML");
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
