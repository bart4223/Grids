package Grids;

import Uniwork.Appl.NGApplication;
import Uniwork.Appl.NGCustomStageItem;
import Uniwork.Appl.NGToolboxManager;
import Uniwork.Base.*;
import Uniwork.Graphics.NGColorOctree;
import Uniwork.Graphics.NGSerializeGeometryObjectList;
import Uniwork.UI.NGUIImageModificationToolboxContext;
import Uniwork.UI.NGUIImageModificationToolboxItem;
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

public class GridManager extends NGComponentManager {

    protected CopyOnWriteArrayList<Grid> FGrids;
    protected Properties FConfiguration;
    protected int FImageFileSizeX;
    protected int FImageFileSizeY;
    protected int FMegaGridPixelSize;
    protected int FColorQuantizeFactor;
    protected double FGridMaxWidth;
    protected double FGridMaxHeight;
    protected int FGridMaxDistance;
    protected int FGridCount;
    protected Stage FPrimaryStage;
    protected NGToolboxManager FToolboxManager;
    protected GridRunScriptContext FCurrentRunScriptContext;

    protected void writeLog(String aText) {
        for (Grid grid : FGrids) {
            grid.writeLog(aText);
        }
    }

    protected void LoadConfiguration() {
        try {
            InputStream is = new FileInputStream(NGApplication.Application.getConfigurationFilename());
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
            FColorQuantizeFactor = Integer.parseInt(FConfiguration.getProperty("ColorQuantizeFactor"));
        }
        catch (Exception e) {
            writeLog(e.getMessage());
        }
    }

    protected void DoShutdown() {
        Platform.exit();
    }

    @Override
    protected void DoInitialize() {
        LoadConfiguration();
        for (int i = 0; i < FGridCount; i++ ) {
            Grid grid = addGrid(20, Color.LIGHTGRAY);
            registerComponent(grid);
        }
        super.DoInitialize();
        writeLog("Welcome to Grids have fun...");
    }

    @Override
    protected void DoAfterInitialize() {
        super.DoAfterInitialize();
        registerObjectRequests();
    }

    protected void registerObjectRequests() {
        NGObjectRequestMethod orm;
        registerObjectRequest("LoadImageWithCQ", "ScriptLoadImageWithQC", "Load an image from file with color quantization.");
        registerObjectRequest("ShowMegaPixel", "ScriptShowMegaPixel", "Show the mega pixel grid.");
        registerObjectRequest("HideMegaPixel", "ScriptHideMegaPixel", "Show the mega pixel grid.");
        orm = registerObjectRequest("SetGridDistance", "ScriptSetGridDistance", "Set the grid distance.");
        orm.addParam("Value", NGObjectRequestParameter.ParamKind.Integer);
        registerObjectRequest("ShowGrid", "ScriptShowGrid", "Show the grid.");
        registerObjectRequest("HideGrid", "ScriptHideGrid", "Hide the grid.");
        orm = registerObjectRequest("SetCurrentGrid", "ScriptSetCurrentGrid", "Set the current grid for scripting.");
        orm.addParam("Index", NGObjectRequestParameter.ParamKind.Integer);
    }

    protected NGObjectRequestMethod registerObjectRequest(String aMethod, String aObjectMethod, String aDescription) {
        return NGApplication.Application.registerObjectRequest("Grid", this, aMethod, aObjectMethod, aDescription);
    }

    protected Grid getGridForScriptTarget() {
        if (FCurrentRunScriptContext != null) {
            return FCurrentRunScriptContext.getGrid();
        }
        writeError("No current grid as script target available.");
        return null;
    }

    @Override
    protected void DoFinalize() {
        for (Grid grid : FGrids) {
            grid.Finalize();
        }
        FToolboxManager.Finalize();
        super.DoFinalize();
    }

    public GridManager(Stage aStage) {
        super();
        FToolboxManager = new NGToolboxManager(this, "ToolboxManager");
        registerComponent(FToolboxManager);
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
        FColorQuantizeFactor = 10;
        FToolboxManager.registerItemClass("Image.Manipulation", "Uniwork.UI.NGUIImageModificationToolboxItem");
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

    public void RunScript(Grid aGrid, String aScript) {
        try {
            FCurrentRunScriptContext = new GridRunScriptContext(aGrid);
            NGApplication.Application.RunScript(aScript);
        }   finally {

            FCurrentRunScriptContext = null;
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
                NGUIImageModificationToolboxItem dialogIM = (NGUIImageModificationToolboxItem)FToolboxManager.CreateToolbox("Image.Manipulation", "Image.Manipulation", new NGUIImageModificationToolboxContext(chosenFile.getPath()));
                dialogIM.setLogManager(aGrid.getLogManager());
                dialogIM.setCQColorCount(getColorQuantizeFactor());
                if (dialogIM.ShowModal() == NGCustomStageItem.NGDialogResult.OK) {
                    NGColorOctree ot = new NGColorOctree();
                    aGrid.New(false);
                    BufferedImage bufferedImage = ImageIO.read(chosenFile);
                    // Build Octree
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        for (int x = 0; x < bufferedImage.getWidth(); x++) {
                            int color = bufferedImage.getRGB(x, y);
                            int red = (color & 0x00ff0000) >> 16;
                            int green = (color & 0x0000ff00) >> 8;
                            int blue = color & 0x000000ff;
                            ot.addColor(red, green, blue);
                        }
                    }
                    ot.BuildPalette();
                    aGrid.writeLog(String.format("Image %s with %dx%d Pixels with %d colors loaded.", chosenFile.getName(), bufferedImage.getWidth(), bufferedImage.getHeight(), ot.getPaletteCount()));
                    aGrid.writeLog(String.format("Octree color Quantization start...", ot.getPaletteCount()));
                    ot.Quantize(dialogIM.getCQColorCount());
                    ot.RebuildPalette();
                    aGrid.writeLog(String.format("...Octree color Quantization to %d colors.", ot.getPaletteCount()));
                    // Build Grid
                    aGrid.writeLog("Build Grid start...");
                    Map<Integer, Color> colors = new TreeMap<Integer, Color>();
                    Integer layercount = aGrid.getLayerCount();
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        for (int x = 0; x < bufferedImage.getWidth(); x++) {
                            int color = bufferedImage.getRGB(x, y);
                            int red = (color & 0x00ff0000) >> 16;
                            int green = (color & 0x0000ff00) >> 8;
                            int blue = color & 0x000000ff;
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

    public int getColorQuantizeFactor() {
        return FColorQuantizeFactor;
    }

    public Stage getPrimaryStage() {
        return FGrids.get(0).getStage();
    }

    public void ScriptLoadImageWithQC() {
        Grid grid = getGridForScriptTarget();
        if (grid != null) {
            loadGridFromImageWithQC(grid);
        }
    }

    public void ScriptShowMegaPixel() {
        Grid grid = getGridForScriptTarget();
        if (grid != null) {
            grid.ShowMegaPixel();
        }
    }

    public void ScriptHideMegaPixel() {
        Grid grid = getGridForScriptTarget();
        if (grid != null) {
            grid.HideMegaPixel();
        }
    }

    public void ScriptSetGridDistance(Integer aValue) {
        Grid grid = getGridForScriptTarget();
        if (grid != null) {
            grid.setGridDistance(aValue);
        }
    }

    public void ScriptShowGrid() {
        Grid grid = getGridForScriptTarget();
        if (grid != null) {
            grid.ShowGrid();
        }
    }

    public void ScriptHideGrid() {
        Grid grid = getGridForScriptTarget();
        if (grid != null) {
            grid.HideGrid();
        }
    }

    public void ScriptSetCurrentGrid(Integer aIndex) {
        if (aIndex >= 0 && aIndex < FGrids.size()) {
            FCurrentRunScriptContext = new GridRunScriptContext(FGrids.get(aIndex));
        } else {
            writeError("Invalid grid index.");
        }
    }

}
