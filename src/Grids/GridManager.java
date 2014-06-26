package Grids;

import Uniwork.Base.NGObjectDeserializer;
import Uniwork.Base.NGObjectXMLDeserializerFile;
import Uniwork.Base.NGObjectSerializer;
import Uniwork.Base.NGObjectXMLSerializerFile;
import Uniwork.Graphics.NGSerializeGeometryObjectItem;
import Uniwork.Graphics.NGSerializeGeometryObjectList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    protected double FGridMaxWidth;
    protected double FGridMaxHeight;
    protected int FGridMaxDistance;
    protected int FGridCount;

    protected void writeLog(String aText) {
        for (Grid grid : FGrids) {
            grid.writeLog(aText);
        }
    }

    protected void LoadConfiguration() {
        try {
            InputStream is = new FileInputStream("resources/config.gcf");
            FConfiguration.load(is);
            FImageFileSizeX = Integer.parseInt(FConfiguration.getProperty("ImageFileSizeX"));
            FImageFileSizeY = Integer.parseInt(FConfiguration.getProperty("ImageFileSizeY"));
            FGridCount = Integer.parseInt(FConfiguration.getProperty("GridCount"));
            if (FGridCount < 1) {
                FGridCount = 1;
            }
            if (FGridCount > 10) {
                FGridCount = 10;
            }
            FGridMaxWidth = Integer.parseInt(FConfiguration.getProperty("GridMaxWidth"));
            FGridMaxHeight = Integer.parseInt(FConfiguration.getProperty("GridMaxHeight"));
            FGridMaxDistance = Integer.parseInt(FConfiguration.getProperty("GridMaxDistance"));
        }
        catch (Exception e) {
            writeLog(e.getMessage());
        }
    }

    public GridManager() {
        FGrids = new ArrayList<Grid>();
        FConfiguration = new Properties();
        FImageFileSizeX = 8;
        FImageFileSizeY = 8;
        FGridMaxWidth = 1024;
        FGridMaxHeight = 1024;
        FGridMaxDistance = 10;
        FGridCount = 1;
    }

    public void Initialize() {
        LoadConfiguration();
        for (int i = 0; i < FGridCount; i++ ) {
            Grid grid = addGrid(20, Color.LIGHTGRAY);
            grid.Initialize();
        }
        writeLog("Welcome to Grids have fun...");
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

    public void saveGridAsGDF(Grid aGrid) {
        try
        {
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            fileChooser.setTitle("Save as GDF");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GDF files (*.gdf)", "*.gdf");
            fileChooser.getExtensionFilters().add(extFilter);
            File chosenFile = fileChooser.showSaveDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                NGObjectSerializer Serializer = new NGObjectXMLSerializerFile(aGrid, XMLGrid.class, chosenFile.getPath());
                Serializer.setLogManager(aGrid.getLogManager());
                Serializer.serializeObject();
            }
            else {
                aGrid.writeLog("Saving as GDF aborted...");
            }
        }
        catch (Exception e) {
            aGrid.writeLog(e.getMessage());
        }
    }

    public void saveGridAsGOF(Grid aGrid) {
        try
        {
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            fileChooser.setTitle("Save as GOF");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GOF files (*.gof)", "*.gof");
            fileChooser.getExtensionFilters().add(extFilter);
            File chosenFile = fileChooser.showSaveDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                NGObjectSerializer Serializer = new NGObjectXMLSerializerFile(aGrid, NGSerializeGeometryObjectList.class, chosenFile.getPath());
                Serializer.setLogManager(aGrid.getLogManager());
                Serializer.serializeObject();
            }
            else {
                aGrid.writeLog("Saving as GOF aborted...");
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
                    aGrid.writeLog(String.format("PNG saved in %s.",chosenFile.getPath()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }            }
            else {
                aGrid.writeLog("Saving as PNG aborted...");
            }
        }
        catch (Exception e) {
            aGrid.writeLog(e.getMessage());
        }
    }

    public void loadGridFromGDF(Grid aGrid) {
        try
        {
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            fileChooser.setTitle("Load from GDF");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GDF files (*.gdf)", "*.gdf");
            fileChooser.getExtensionFilters().add(extFilter);
            File chosenFile = fileChooser.showOpenDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                NGObjectDeserializer Deserializer = new NGObjectXMLDeserializerFile(aGrid, chosenFile.getPath());
                Deserializer.setLogManager(aGrid.getLogManager());
                Deserializer.deserializeObject();
            }
            else {
                aGrid.writeLog("Loading as GDF aborted...");
            }
        }
        catch (Exception e) {
            aGrid.writeLog(e.getMessage());
        }
    }

    public double getGridMaxWidth() {
        return FGridMaxWidth;
    }

    public double getGridMaxHeight() {
        return FGridMaxHeight;
    }

    public int getGridMaxDistance() {
        return FGridMaxDistance;
    }

    public Stage getPrimaryStage() {
        return FGrids.get(0).getStage();
    }

}
