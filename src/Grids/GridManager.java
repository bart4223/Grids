package Grids;

import Uniwork.Base.NGObjectDeserializer;
import Uniwork.Base.NGObjectXMLDeserializerFile;
import Uniwork.Base.NGObjectSerializer;
import Uniwork.Base.NGObjectXMLSerializerFile;
import Uniwork.Graphics.NGColorOctree;
import Uniwork.Graphics.NGSerializeGeometryObjectList;
import Uniwork.Visuals.NGCommonDialogs;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GridManager {

    protected CopyOnWriteArrayList<Grid> FGrids;
    protected Properties FConfiguration;
    protected int FImageFileSizeX;
    protected int FImageFileSizeY;
    protected int FMegaGridPixelSize;
    protected double FGridMaxWidth;
    protected double FGridMaxHeight;
    protected int FGridMaxDistance;
    protected int FGridCount;
    protected Stage FPrimaryStage;

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
            FMegaGridPixelSize = Integer.parseInt(FConfiguration.getProperty("MegaGridPixelSize"));
        }
        catch (Exception e) {
            writeLog(e.getMessage());
        }
    }

    protected void DoShutdown() {
        Platform.exit();
    }

    public GridManager(Stage aStage) {
        FPrimaryStage = aStage;
        FGrids = new CopyOnWriteArrayList<Grid>();
        FConfiguration = new Properties();
        FImageFileSizeX = 8;
        FImageFileSizeY = 8;
        FGridMaxWidth = 1024;
        FGridMaxHeight = 1024;
        FGridMaxDistance = 10;
        FGridCount = 1;
        FMegaGridPixelSize = 10;
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

    public void Shutdown() {
        if (NGCommonDialogs.showConfirmDialog(FPrimaryStage, "Quit", "Do you really want to leave grids?") == NGCommonDialogs.Response.Yes)
            DoShutdown();
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

    public void saveGridImageAsPNG(Grid aGrid, Boolean aComplete) {
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
                    if (aComplete) {
                        aGrid.getStageController().getCompleteLayer().snapshot(parameter, wim);
                    } else {
                        aGrid.getStageController().getObjectLayer().snapshot(parameter, wim);
                    }
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

    public void loadGridFromImageWithQC(Grid aGrid) {
        try {
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            fileChooser.setTitle("Load from Image with Color Quantization");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png,*.jpg,*.bmp)", "*.png", "*.jpg", "*.bmp");
            fileChooser.getExtensionFilters().add(extFilter);
            File chosenFile = fileChooser.showOpenDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                NGColorOctree ot = new NGColorOctree();
                aGrid.New(false);
                BufferedImage bufferedImage = ImageIO.read(chosenFile);
                // Build Octree
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    for (int x = 0; x < bufferedImage.getWidth(); x++) {
                        int color = bufferedImage.getRGB(x, y);
                        int  red   = (color & 0x00ff0000) >> 16;
                        int  green = (color & 0x0000ff00) >> 8;
                        int  blue  =  color & 0x000000ff;
                        ot.addColor(red, green, blue);
                    }
                }
                ot.BuildPalette();
                aGrid.writeLog(String.format("Image %s with %dx%d Pixels with %d colors loaded.", chosenFile.getName(), bufferedImage.getWidth(), bufferedImage.getHeight(), ot.getPaletteCount()));
                aGrid.writeLog(String.format("Octree color Quantization start...", ot.getPaletteCount()));
                ot.Quantize(42 * 3);
                ot.RebuildPalette();
                aGrid.writeLog(String.format("...Octree color Quantization to %d colors.", ot.getPaletteCount()));
                // Build Grid
                aGrid.writeLog("Build Grid start...");
                Map<Integer, Color> colors = new TreeMap<Integer, Color>();
                Integer layercount = aGrid.getLayerCount();
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    for (int x = 0; x < bufferedImage.getWidth(); x++) {
                        int color = bufferedImage.getRGB(x, y);
                        int  red   = (color & 0x00ff0000) >> 16;
                        int  green = (color & 0x0000ff00) >> 8;
                        int  blue  =  color & 0x000000ff;
                        Integer key = red * 65536 + green * 256 + blue * 255;
                        Color c = colors.get(key);
                        if (c == null) {
                            c = ot.getNodeColorFromColor(Color.rgb(red, green, blue));
                            colors.put(key, c);
                        }
                        if (!c.equals(Color.TRANSPARENT)) {
                            GridLayer layer = aGrid.getLayer(c);
                            if (layer == null) {
                                layercount++;
                                layer = aGrid.addLayer(String.format("LAYER%d", layercount), String.format("Layer %d", layercount), c, layercount);
                            }
                            layer.addPoint(x, y);
                        }
                    }
                }
                aGrid.writeLog("Build Grid finished.");
                aGrid.setGridDistance(1);
                aGrid.setDrawGrid(false);
            }
        } catch (Exception e) {
            aGrid.writeLog(e.getMessage());
        }
    }

    public void loadGridFromImage(Grid aGrid) {
        try
        {
            FileChooser fileChooser = new FileChooser();
            String userDirectoryString = System.getProperty("user.home");
            File userDirectory = new File(userDirectoryString);
            fileChooser.setInitialDirectory(userDirectory);
            fileChooser.setTitle("Load from Image");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png,*.jpg,*.bmp)", "*.png", "*.jpg", "*.bmp");
            fileChooser.getExtensionFilters().add(extFilter);
            File chosenFile = fileChooser.showOpenDialog(aGrid.getStage().getOwner());
            if (chosenFile != null) {
                aGrid.New(false);
                BufferedImage bufferedImage = ImageIO.read(chosenFile);
                aGrid.writeLog(String.format("Image %s with %dx%d Pixels loaded.", chosenFile.getName(), bufferedImage.getWidth(), bufferedImage.getHeight()));
                WritableImage img =  new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
                SwingFXUtils.toFXImage(bufferedImage, img);
                // Build Grid
                aGrid.writeLog("Build Grid start...");
                Integer layercount = aGrid.getLayerCount();
                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        Color color = img.getPixelReader().getColor(x, y);
                        if (!color.equals(Color.TRANSPARENT)) {
                            GridLayer layer = aGrid.getLayer(color);
                            if (layer == null) {
                                layercount++;
                                layer = aGrid.addLayer(String.format("LAYER%d", layercount), String.format("Layer %d", layercount), color, layercount);
                            }
                            layer.addPoint(x, y);
                        }
                    }
                }
                aGrid.writeLog("Build Grid finished.");
                aGrid.setGridDistance(1);
                aGrid.setDrawGrid(false);
            }
            else {
                aGrid.writeLog("Loading from Image aborted...");
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

    public int getMegaGridPixelSize() {
        return FMegaGridPixelSize;
    }

    public Stage getPrimaryStage() {
        return FGrids.get(0).getStage();
    }

}
