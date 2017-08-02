package Grids;

import Uniwork.Misc.NGLogEntry;
import Uniwork.Graphics.*;
import Uniwork.Visuals.NGCommonDialogs;
import Uniwork.Visuals.NGDisplayView;
import Uniwork.Visuals.NGGrid2DDisplayController;
import Uniwork.Visuals.NGLabeledGrid2DDisplayController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ResourceBundle;

public class GridStageController implements Initializable {

    public enum ToolMode{Select, Point, Line, Circle, Ellipse, Quadrat, Rectangle};

    @FXML
    private Button btnSaveGrid;

    @FXML
    private Button btnLoadGrid;

    @FXML
    private ToggleButton btnSelect;

    @FXML
    private ToggleButton btnPoint;

    @FXML
    private ToggleButton btnLine;

    @FXML
    private ToggleButton btnCircle;

    @FXML
    private ToggleButton btnEllipse;

    @FXML
    private ToggleButton btnQuadrat;

    @FXML
    private ToggleButton btnRectangle;

    @FXML
    private Button btnPaintGrid;

    @FXML
    private Canvas Layer0;

    @FXML
    private Canvas Layer1;

    @FXML
    private AnchorPane CompleteLayer;

    @FXML
    private ComboBox cbGridSize;

    @FXML
    private ComboBox cbLayers;

    @FXML
    private TextArea Log;

    @FXML
    private ColorPicker cpObjectColor;

    protected GraphicsContext gc1;

    protected Integer FUpdateCount;
    protected ToolMode FToolMode;

    protected NGGeometryObject2D FCurrentGO;
    protected NGGeometryObject2D FCurrentGOPoint;
    protected Boolean FCurrentGOPointRemoved;

    protected ContextMenu cmLayer0;
    protected ContextMenu cmbtnPaintGrid;
    protected ContextMenu cmbtnLoadGrid;
    protected ContextMenu cmbtnSaveGrid;
    protected DropShadow dsContextMenu;

    protected final Button hlpbutton;

    protected NGGrid2DDisplayController FGDC;
    protected NGLabeledGrid2DDisplayController FLGDC;

    protected GridLayerDisplayManager FGLDM;
    protected NGDisplayView FView;

    protected Tooltip FCoordinatesTooltip;

    protected MouseEvent FLastMouseEvent;

    protected Boolean FShowMegaPixel;

    @FXML
    protected void handlecbGridSize(ActionEvent Event){
        if (InUpdate()) return;
        if (Event.getEventType().equals(ActionEvent.ACTION)) {
            Integer i = Integer.parseInt(cbGridSize.getValue().toString());
            Grid.setGridDistance(i);
        }
    }

    @FXML
    protected void handlecbLayers(ActionEvent Event){
        if (InUpdate()) return;
        if (Event.getEventType().equals(ActionEvent.ACTION)) {
            if (cbLayers.getValue() != null) {
                GridLayer layer = Grid.setCurrentLayer(cbLayers.getValue().toString());
                cbLayers.setTooltip(new Tooltip(layer.getDescription()));
            }
        }
    }
    
    @FXML
    protected void handleSaveGrid(){
        cmbtnSaveGrid.show(btnSaveGrid, Side.BOTTOM, 0, 0);
    }

    @FXML
    protected void handlePaintGrid(){
        cmbtnPaintGrid.show(btnPaintGrid, Side.BOTTOM, 0, 0);
    }

    @FXML
    protected void handleLoadGrid(){
        cmbtnLoadGrid.show(btnLoadGrid, Side.BOTTOM, 0, 0);
    }

    @FXML
    protected void handleSelect(){
        setToolMode(ToolMode.Select);
    }

    @FXML
    protected void handlePoint(){
        setToolMode(ToolMode.Point);
    }

    @FXML
    protected void handleLine(){
        setToolMode(ToolMode.Line);
    }

    @FXML
    protected void handleCircle(){
        setToolMode(ToolMode.Circle);
    }

    @FXML
    protected void handleEllipse(){
        setToolMode(ToolMode.Ellipse);
    }

    @FXML
    protected void handleQuadrat(){
        setToolMode(ToolMode.Quadrat);
    }

    @FXML
    protected void handleRectangle(){
        setToolMode(ToolMode.Rectangle);
    }

    @FXML
    protected void handleNew(){
        if (NGCommonDialogs.showConfirmDialog(Grid.getStage(), "New grid", "Do you want really a new grid?") == NGCommonDialogs.Response.Yes) {
            Grid.New();
        }
    }

    @FXML
    protected void handleAddLayer(){
        Grid.addLayer();
    }

    @FXML
    protected void handleClearLayer(){
        Grid.clearCurrentLayer();
    }

    @FXML
    protected void handleRemoveLayer(){
        Grid.removeCurrentLayer();
    }

    @FXML
    protected void handleObjectColor() {
        Grid.getCurrentLayer().setObjectColor(cpObjectColor.getValue());
        RenderLayer1();
    }

    @FXML
    protected void handleQuit(){
        Grid.Shutdown();
    }

    protected void setToolMode(ToolMode aToolMode) {
        FToolMode = aToolMode;
    }

    protected void RenderLayer1() {
        FGLDM.setPixelSize(Grid.getGridDistance());
        FGLDM.Layers = Grid.getLayers();
        FGLDM.Render();
    }

    protected void BeforeRenderLayer0() {
        if (FShowMegaPixel) {
            FLGDC.clearLabels();
            double x = 0 - FLGDC.getViewPositionX();
            double y = 0 - FLGDC.getViewPositionY();
            Integer indexX;
            Integer indexY = 0;
            for(double yy = y; yy < y + Layer0.getHeight(); yy = yy + Grid.getMegaGridPixleSize()) {
                indexX = 0;
                for(double xx = x; xx <= x + Layer0.getWidth(); xx = xx + Grid.getMegaGridPixleSize()) {
                    NGRegion2D region = new NGRegion2D(xx, yy, xx + Grid.getMegaGridPixleSize(), yy + Grid.getMegaGridPixleSize());
                    GridLayer MaxLayer = Grid.getLayerWithMaxPoints(region);
                    if (MaxLayer != null) {
                        FLGDC.addLabel(String.format("%d.%d", indexY, indexX), String.format("%d%s", MaxLayer.getID(), MaxLayer.getObjectColor().invert().toString()));
                    }
                    indexX = indexX + 1;
                }
                indexY = indexY + 1;
            }
        }
    }

    protected void RenderLayer0() {
        FGDC.GridColor = Grid.getGridColor();
        FGDC.GridDistance = Grid.getGridDistance();
        FGDC.DrawGrid = Grid.getDrawGrid();
        FGDC.Render();
        FLGDC.GridColor = Grid.getGridColor().darker().darker().darker();
        FLGDC.GridDistance = Grid.getGridDistance() * Grid.getMegaGridPixleSize();
        FLGDC.DrawGrid = FShowMegaPixel;
        FLGDC.Render();
    }

    protected NGPoint2D CoordinatesToGridCoordinates(NGPoint2D aPoint) {
        aPoint.setX(aPoint.getX() + FView.getPositionX());
        aPoint.setY(aPoint.getY() + FView.getPositionY());
        return Grid.CoordinatesToGridCoordinates(aPoint);
    }

    protected void HandleMouseMoved(MouseEvent t) {
        FLastMouseEvent = t;
        if (t.isAltDown()) {
            ShowInfoTooltip();
        }
        else {
            HideInfoTooltip();
        }
    }

    protected void HandleMouseExited(MouseEvent t) {
        FLastMouseEvent = null;
        HideInfoTooltip();
    }

    protected void HandleMousePressed(MouseEvent t) {
        switch (t.getButton()) {
            case PRIMARY:
                NGGeometryObject2D layerObject;
                GridLayer Layer = Grid.getCurrentLayer();
                NGPoint2D gridPoint = CoordinatesToGridCoordinates(new NGPoint2D(t.getX(), t.getY()));
                switch (FToolMode) {
                    case Select:
                        layerObject = Layer.getObjectInLayer(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                        if (layerObject != null) {
                            if (Layer.toggleObjectSelected(layerObject)) {
                                FCurrentGO = layerObject;
                            }
                        } else {
                            Grid.setCurrentLayerWithObject(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                        }
                        break;
                    case Point:
                        layerObject = Layer.getObjectInLayer(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                        if (layerObject == null || !(layerObject instanceof NGPoint2D)) {
                            FCurrentGO = Layer.addPoint(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                        }
                        else {
                            Layer.removeObject(layerObject);
                            FCurrentGOPointRemoved = true;
                        }
                        break;
                    case Line:
                        FCurrentGO = Layer.addLine(gridPoint.getXAsInt(), gridPoint.getYAsInt(), gridPoint.getXAsInt(), gridPoint.getYAsInt());
                        break;
                    case Circle:
                        FCurrentGO = Layer.addCircle(gridPoint.getXAsInt(), gridPoint.getYAsInt(), 0);
                        break;
                    case Ellipse:
                        FCurrentGO = Layer.addEllipse(gridPoint.getXAsInt(), gridPoint.getYAsInt(), 0, 0);
                        break;
                    case Quadrat:
                        FCurrentGO = Layer.addQuadrat(gridPoint.getXAsInt(), gridPoint.getYAsInt(), 0);
                        break;
                    case Rectangle:
                        FCurrentGO = Layer.addRectangle(gridPoint.getXAsInt(), gridPoint.getYAsInt(), 0, 0);
                        break;
                }
                break;
        }
    }

    protected void HandleMouseReleased(MouseEvent t) {
        switch (t.getButton()) {
            case PRIMARY:
                FCurrentGO = null;
                FCurrentGOPoint = null;
                FCurrentGOPointRemoved = false;
                break;
        }
    }

    protected void HandleMouseDragged(MouseEvent t) {
        FLastMouseEvent = t;
        int lXDist;
        int lYDist;
        NGGeometryObject2D layerObject;
        GridLayer Layer = Grid.getCurrentLayer();
        NGPoint2D gridPoint = CoordinatesToGridCoordinates(new NGPoint2D(t.getX(), t.getY()));
        switch (FToolMode) {
            case Select:
                if (FLastMouseEvent.isAltDown()) {
                    if (FCurrentGO instanceof NGPoint2D) {
                        NGPoint2D Point = (NGPoint2D) FCurrentGO;
                        double dX = Point.getX() - gridPoint.getX();
                        double dY = Point.getY() - gridPoint.getY();
                        Point.setX(gridPoint.getX());
                        Point.setY(gridPoint.getY());
                        for (NGGeometryObject2D obj : Layer.getSelected()) {
                            if ((obj != FCurrentGO) && (obj instanceof NGPoint2D)) {
                                Point = (NGPoint2D)obj;
                                Point.setX(Point.getX() - dX);
                                Point.setY(Point.getY() - dY);
                            }
                        }
                    }
                }
                else {
                    if (FCurrentGO instanceof NGPoint2D) {
                        NGPoint2D Point = (NGPoint2D)FCurrentGO;
                        Point.setX(gridPoint.getX());
                        Point.setY(gridPoint.getY());
                    } else if (FCurrentGO instanceof NGEllipse) {
                        NGEllipse Ellipse = (NGEllipse)FCurrentGO;
                        Ellipse.setMiddlePoint(gridPoint.getX(), gridPoint.getY());
                    } else if (FCurrentGO instanceof NGRectangle) {
                        NGRectangle Rectangle = (NGRectangle)FCurrentGO;
                        Rectangle.setMiddlePoint(gridPoint.getX(), gridPoint.getY());
                    } else if (FCurrentGO instanceof NGLine2D) {
                        NGLine2D Line = (NGLine2D) FCurrentGO;
                        double dx;
                        double dy;
                        if (FCurrentGOPoint == null) {
                            if (Line.getA().getX() == gridPoint.getX() && Line.getA().getY() == gridPoint.getY()) {
                                FCurrentGOPoint = Line.getA();
                            }
                            else {
                                FCurrentGOPoint = Line.getB();
                            }
                        }
                        if (FCurrentGOPoint.equals(Line.getA())) {
                            dx = Line.getB().getX() - Line.getA().getX();
                            dy = Line.getB().getY() - Line.getA().getY();
                            Line.setA(gridPoint.getX(), gridPoint.getY());
                            Line.setB(gridPoint.getX() + dx, gridPoint.getY() + dy);
                        }
                        else {
                            dx = Line.getA().getX() - Line.getB().getX();
                            dy = Line.getA().getY() - Line.getB().getY();
                            Line.setB(gridPoint.getX(), gridPoint.getY());
                            Line.setA(gridPoint.getX() + dx, gridPoint.getY() + dy);
                        }
                    }
                }
                break;
            case Point:
                layerObject = Layer.getObjectInLayer(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                if (!FCurrentGOPointRemoved) {
                    if (layerObject == null || !(layerObject instanceof NGPoint2D)) {
                        FCurrentGO = Layer.addPoint(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                    }
                }
                else if (layerObject != null) {
                    Layer.removeObject(layerObject);
                }
                break;
            case Line:
                NGLine2D Line = (NGLine2D)FCurrentGO;
                Line.getB().setX(gridPoint.getX());
                Line.getB().setY(gridPoint.getY());
                break;
            case Circle:
                NGCircle Circle = (NGCircle)FCurrentGO;
                lXDist = Math.abs(Circle.getMiddlePoint().getXAsInt() - gridPoint.getXAsInt());
                lYDist = Math.abs(Circle.getMiddlePoint().getYAsInt() - gridPoint.getYAsInt());
                if (lXDist > lYDist)
                    Circle.setRadius(lXDist);
                else
                    Circle.setRadius(lYDist);
                break;
            case Ellipse:
                NGEllipse Ellipse = (NGEllipse)FCurrentGO;
                lXDist = Math.abs(Ellipse.getMiddlePoint().getXAsInt() - gridPoint.getXAsInt());
                lYDist = Math.abs(Ellipse.getMiddlePoint().getYAsInt() - gridPoint.getYAsInt());
                Ellipse.setRadiusX(lXDist);
                Ellipse.setRadiusY(lYDist);
                break;
            case Quadrat:
                NGQuadrat Quadrat = (NGQuadrat)FCurrentGO;
                lXDist = Math.abs(Quadrat.getMiddlePoint().getXAsInt() - gridPoint.getXAsInt());
                lYDist = Math.abs(Quadrat.getMiddlePoint().getYAsInt() - gridPoint.getYAsInt());
                if (lXDist > lYDist)
                    Quadrat.setA(2 * lXDist);
                else
                    Quadrat.setA(2 * lYDist);
            case Rectangle:
                NGRectangle Rectangle = (NGRectangle)FCurrentGO;
                lXDist = Math.abs(Rectangle.getMiddlePoint().getXAsInt() - gridPoint.getXAsInt());
                lYDist = Math.abs(Rectangle.getMiddlePoint().getYAsInt() - gridPoint.getYAsInt());
                Rectangle.setA(2*lXDist);
                Rectangle.setB(2*lYDist);
        }
        RenderLayer1();
        if (t.isAltDown()) {
            ShowInfoTooltip();
        }
        else {
            HideInfoTooltip();
        }
    }

    protected void HandleMouseClicked(MouseEvent t) {
        switch (t.getButton()) {
            case SECONDARY:
                cmLayer0.show(Layer0, t.getScreenX(), t.getScreenY());
                break;
        }
    }

    protected void HandleMouseScrolled(ScrollEvent t) {
        double x = FView.getPositionX() - t.getDeltaX();
        if (x < 0) {
            x = 0;
        }
        double xmax = Grid.getManager().getGridMaxWidth() - FView.getWidth();
        if (x > xmax) {
            x = xmax;
        }
        double y = FView.getPositionY() - t.getDeltaY();
        if (y < 0) {
            y = 0;
        }
        double ymax = Grid.getManager().getGridMaxHeight() - FView.getHeight();
        if (y > ymax) {
            y = ymax;
        }
        FView.setPosition(x, y);
        RenderScene(true);
    }

    protected void HandleZoom(ZoomEvent t) {
        double z = Grid.getGridDistance() * t.getTotalZoomFactor();
        if (z < 0) {
            z = 0;
        }
        double maxz = Grid.getManager().getGridMaxDistance();
        if (z > maxz) {
            z = maxz;
        }
        Grid.setGridDistance((int)z);
        cbGridSize.setValue((int)z);
    }

    protected void UpdatecbLayers() {
        cbLayers.getItems().clear();
        for (GridLayer Layer : Grid.getLayers()) {
            cbLayers.getItems().add(Layer.getName());
        }
        if (Grid.getCurrentLayer() != null) {
            // workaround start
            cbLayers.valueProperty().set(null);
            // workaround end
            cbLayers.getSelectionModel().select(Grid.getCurrentLayer().getName());
        }
    }

    protected void UpdatecbGridSize() {
        cbGridSize.getSelectionModel().select(Grid.getGridDistance().toString());
    }

    protected void UpdatecpObjectColor() {
        cpObjectColor.setValue(Grid.getCurrentLayer().getObjectColor());
        // workaround start
        cpObjectColor.fireEvent(new ActionEvent(hlpbutton, cpObjectColor));
        // workaround end
    }

    protected void BeginUpdateControls() {
        FUpdateCount = FUpdateCount + 1;
    }

    protected void EndUpdateControls() {
        FUpdateCount = FUpdateCount - 1;
    }

    protected Boolean InUpdate() {
        return FUpdateCount > 1;
    }

    protected MenuItem getMenuItemForLine(String menuName, final Line line, EventHandler click) {
        Label menuLabel = new Label(menuName);
        menuLabel.setStyle("-fx-padding: 5 10 5 10");
        MenuItem mi = new MenuItem();
        mi.setGraphic(menuLabel);
        line.setStroke(Color.BLUE);
        menuLabel.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                line.setStroke(Color.RED);
                line.setEffect(dsContextMenu);
            }
        });
        menuLabel.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                line.setStroke(Color.BLUE);
                line.setEffect(null);
            }
        });
        menuLabel.addEventHandler(MouseEvent.MOUSE_RELEASED, click);
        return mi;
    }

    protected void resetView() {
        FView.setPosition(0, 0);
        RenderScene(true);
    }

    public Grid Grid;

    public GridStageController() {
        FUpdateCount = 0;
        FToolMode = ToolMode.Select;
        FCurrentGO = null;
        FCurrentGOPointRemoved = false;
        hlpbutton = new Button("Press me.");
        FCoordinatesTooltip = new Tooltip("Mouse Position 0,0");
        FCoordinatesTooltip.setHideOnEscape(true);
        FShowMegaPixel = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Line line;
        EventHandler<MouseEvent> click;
        gc1 = Layer1.getGraphicsContext2D();
        ToggleGroup group = new ToggleGroup();
        btnSelect.setToggleGroup(group);
        btnPoint.setToggleGroup(group);
        btnLine.setToggleGroup(group);
        btnCircle.setToggleGroup(group);
        btnEllipse.setToggleGroup(group);
        btnQuadrat.setToggleGroup(group);
        btnRectangle.setToggleGroup(group);
        btnSelect.setSelected(true);
        dsContextMenu = new DropShadow();
        // Contextmenu Layer 0
        cmLayer0 = new ContextMenu();
        // Clear Selection
        line = new Line(60, 10, 150, 10);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.getCurrentLayer().clearSelectedObjects();
            }};
        cmLayer0.getItems().add(getMenuItemForLine("Clear Selection", line, click));
        // Clear Selection
        line = new Line(60, 30, 150, 50);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.getCurrentLayer().SelectAllObjects();
            }};
        cmLayer0.getItems().add(getMenuItemForLine("Select All", line, click));
        // Clear Selected Objects
        line = new Line(60, 50, 150, 90);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.getCurrentLayer().removeSelectedObjects();
            }};
        cmLayer0.getItems().add(getMenuItemForLine("Remove Selected Object(s)", line, click));
        // Reset View
        line = new Line(60, 70, 150, 130);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resetView();
            }};
        cmLayer0.getItems().add(getMenuItemForLine("Reset View", line, click));
        // Cancel
        line = new Line(60, 90, 150, 170);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }};
        cmLayer0.getItems().add(getMenuItemForLine("Cancel", line, click));
        // Contextmenu btnSaveGrid
        cmbtnSaveGrid = new ContextMenu();
        // Save as GDF
        line = new Line(60, 10, 150, 10);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.SaveAsGDF();
            }};
        cmbtnSaveGrid.getItems().add(getMenuItemForLine("as GDF", line, click));
        // Save as GOF
        line = new Line(60, 30, 150, 50);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.SaveAsGOF();
            }};
        cmbtnSaveGrid.getItems().add(getMenuItemForLine("as GOF", line, click));
        // Save as PNG
        line = new Line(60, 50, 150, 90);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.SaveAsPNG(false);
            }};
        cmbtnSaveGrid.getItems().add(getMenuItemForLine("as PNG", line, click));
        // Save as PNG with Grid
        line = new Line(90, 50, 150, 90);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.SaveAsPNG(true);
            }};
        cmbtnSaveGrid.getItems().add(getMenuItemForLine("as PNG with Grid", line, click));
        // Contextmenu btnLoadGrid
        cmbtnLoadGrid = new ContextMenu();
        // Load from GDF
        line = new Line(60, 10, 150, 10);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.LoadFromGDF();
            }};
        cmbtnLoadGrid.getItems().add(getMenuItemForLine("from GDF", line, click));
        // Load from Image
        line = new Line(60, 30, 150, 10);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.LoadFromImage();
            }};
        cmbtnLoadGrid.getItems().add(getMenuItemForLine("from Image", line, click));
        // Load from Image with CQ
        line = new Line(60, 50, 150, 10);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.LoadFromImageWithCQ();
            }};
        cmbtnLoadGrid.getItems().add(getMenuItemForLine("from Image with CQ", line, click));
        // Contextmenu btnPaintGrid aka Show
        cmbtnPaintGrid = new ContextMenu();
        // Default Grid
        line = new Line(60, 10, 150, 10);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Grid.setDrawGrid(!Grid.getDrawGrid());
                RenderScene(true);
            }};
        cmbtnPaintGrid.getItems().add(getMenuItemForLine("Default Grid", line, click));
        // Mega Pixel Grid
        line = new Line(60, 30, 150, 10);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FShowMegaPixel = !FShowMegaPixel;
                RenderScene(true);
            }};
        cmbtnPaintGrid.getItems().add(getMenuItemForLine("Mega Pixel Grid", line, click));
        FView = new NGDisplayView(Layer0.getWidth(), Layer0.getHeight());
        FGDC = new NGGrid2DDisplayController(Layer0);
        FGDC.setView(FView);
        FLGDC = new NGLabeledGrid2DDisplayController(Layer0);
        FLGDC.ClearRect = false;
        FLGDC.setView(FView);
        FGLDM = new GridLayerDisplayManager(Layer1);
        FGLDM.setView(FView);
        FGLDM.setBackgroundColor(Color.TRANSPARENT);
    }

    public void Initialize() {
        for( int i = 1; i <= Grid.getManager().getGridMaxDistance(); i ++ ) {
            cbGridSize.getItems().add(i);
        }
        FGDC.Initialize();
        FGDC.GridWidth = Grid.getManager().getGridMaxWidth();
        FGDC.GridHeight = Grid.getManager().getGridMaxHeight();
        FLGDC.Initialize();
        FLGDC.GridWidth = Grid.getManager().getGridMaxWidth();
        FLGDC.GridHeight = Grid.getManager().getGridMaxHeight();
        FGLDM.Initialize();
        Layer0.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        HandleMouseExited(t);
                    }
                });
        Layer0.addEventHandler(MouseEvent.MOUSE_MOVED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        HandleMouseMoved(t);
                    }
                });
        Layer0.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        HandleMousePressed(t);
                    }
                });
        Layer0.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        HandleMouseReleased(t);
                    }
                });
        Layer0.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        HandleMouseDragged(t);
                    }
                });
        Layer0.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        HandleMouseClicked(t);
                    }
                });
        Layer0.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent t) {
                HandleMouseScrolled(t);
            }
        });
        Layer0.setOnZoom(new EventHandler<ZoomEvent>() {
            @Override
            public void handle(ZoomEvent t) {
                HandleZoom(t);
            }
        });
    }

    public void Finalize() {
        HideInfoTooltip();
    }

    public void RenderScene(Boolean aComplete) {
        if (aComplete) {
            BeforeRenderLayer0();
            RenderLayer0();
        }
        RenderLayer1();
    }

    public void DisplayLogEntry(NGLogEntry aLogEntry) {
        String lStr = Log.getText();
        if (lStr.length() == 0) {
            lStr = aLogEntry.GetFullAsString();
        }
        else {
            lStr = aLogEntry.GetFullAsString() + "\n" + lStr;
        }
        Log.setText(lStr);
    }

    public void ClearLog() {
        Log.clear();
    }

    public void UpdateControls() {
        if (InUpdate()) return;
        BeginUpdateControls();
        UpdatecbLayers();
        UpdatecbGridSize();
        UpdatecpObjectColor();
        EndUpdateControls();
    }

    public Canvas getObjectLayer() {
        return Layer1;
    }

    public AnchorPane getCompleteLayer() {
        return CompleteLayer;
    }

    public void HideInfoTooltip() {
        FCoordinatesTooltip.hide();
    }

    public void ShowInfoTooltip() {
        if (FLastMouseEvent != null) {
            Node node = (Node)FLastMouseEvent.getSource();
            int x = (int)FLastMouseEvent.getX() / Grid.getGridDistance() + 1;
            int y = (int)FLastMouseEvent.getY() / Grid.getGridDistance() + 1;
            if (FCurrentGO == null) {
                FCoordinatesTooltip.setText(String.format("Mouse Position %d,%d", x, y));
            }
            else {
                if (FCurrentGO instanceof NGCircle) {
                    NGCircle circle = (NGCircle)FCurrentGO;
                    FCoordinatesTooltip.setText(String.format("Mouse Position %d,%d\nCircle:\n-MiddlePoint %d,%d\n-Radius %d", x, y, (int)circle.getMiddlePointX(), (int)circle.getMiddlePointY(), circle.getRadiusAsInt()));
                } else if (FCurrentGO instanceof NGEllipse) {
                    NGEllipse ellipse = (NGEllipse)FCurrentGO;
                    FCoordinatesTooltip.setText(String.format("Mouse Position %d,%d\nEllipse:\n-MiddlePoint %d,%d\n-Radius X %d\n-Radius Y %d", x, y, (int)ellipse.getMiddlePointX(), (int)ellipse.getMiddlePointY(), ellipse.getRadiusXAsInt(), ellipse.getRadiusYAsInt()));
                }
            }
            FCoordinatesTooltip.show(node, Grid.getStage().getX() + FLastMouseEvent.getSceneX() + 10, Grid.getStage().getY() + FLastMouseEvent.getSceneY() + 10);
        }
    }

}
