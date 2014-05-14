package Grids;

import Uniwork.Misc.NGLogEntry;
import Uniwork.Graphics.*;
import Uniwork.Visuals.NGGrid2DDisplayController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ResourceBundle;

public class GridStageController implements Initializable {

    public enum ToolMode{Select, Point, Line, Circle, Ellipse, Quadrat, Rectangle};

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
    private ToggleButton btnPaintGrid;

    @FXML
    private Canvas Layer0;

    @FXML
    private Canvas Layer1;

    @FXML
    private ComboBox cbGridSize;

    @FXML
    private ComboBox cbLayers;

    @FXML
    private TextArea Log;

    protected GraphicsContext gc1;

    protected Integer FUpdateCount;
    protected ToolMode FToolMode;

    protected NGGeometryObject2D FCurrentGO;
    protected NGGeometryObject2D FCurrentGOPoint;

    protected ContextMenu cmLayer0;
    protected DropShadow dsContextMenu;

    protected boolean FDrawGrid;

    protected NGGrid2DDisplayController FGDC;
    protected GridLayerDisplayManager FGLDM;

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
                Grid.setCurrentLayer(cbLayers.getValue().toString());
            }
        }
    }

    @FXML
    protected void handlePaintGrid(){
        FDrawGrid = btnPaintGrid.isSelected();
        RenderScene(true);
    }

    @FXML
    protected void handleSaveGrid(){
        Grid.Save();
    }

    @FXML
    protected void handleLoadGrid(){
        Grid.Load();
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

    protected void setToolMode(ToolMode aToolMode) {
        FToolMode = aToolMode;
    }

    protected void RenderLayer1() {
        FGLDM.setPixelSize(Grid.getGridDistance());
        FGLDM.Layers = Grid.getLayers();
        FGLDM.Render();
    }

    protected void RenderLayer0() {
        FGDC.GridColor = Grid.getGridColor();
        FGDC.GridDistance = Grid.getGridDistance();
        FGDC.DrawGrid = FDrawGrid;
        FGDC.Render();
    }

    protected void HandleMousePressed(MouseEvent t) {
        switch (t.getButton()) {
            case PRIMARY:
                NGGeometryObject2D layerObject;
                GridLayer Layer = Grid.getCurrentLayer();
                NGPoint2D gridPoint = Grid.CoordinatesToGridCoordinates(new NGPoint2D(t.getX(), t.getY()));
                switch (FToolMode) {
                    case Select:
                        layerObject = Layer.getObjectInLayer(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                        if (layerObject != null) {
                            if (Layer.toggleObjectSelected(layerObject)) {
                                FCurrentGO = layerObject;
                            }
                        }
                        break;
                    case Point:
                        layerObject = Layer.getObjectInLayer(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                        if (layerObject == null || !(layerObject instanceof NGPoint2D)) {
                            FCurrentGO = Layer.addPoint(gridPoint.getXAsInt(), gridPoint.getYAsInt());
                        }
                        else {
                            Layer.removeObject(layerObject);
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
                break;
        }
    }

    protected void HandleMouseDragged(MouseEvent t) {
        int lXDist;
        int lYDist;
        if (FCurrentGO != null) {
            NGPoint2D gridPoint = Grid.CoordinatesToGridCoordinates(new NGPoint2D(t.getX(), t.getY()));
            switch (FToolMode) {
                case Select:
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
                            Line.setB(gridPoint.getX()+dx, gridPoint.getY()+dy);
                        }
                        else {
                            dx = Line.getA().getX() - Line.getB().getX();
                            dy = Line.getA().getY() - Line.getB().getY();
                            Line.setB(gridPoint.getX(), gridPoint.getY());
                            Line.setA(gridPoint.getX()+dx, gridPoint.getY()+dy);
                        }
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
                        Quadrat.setA(2*lXDist);
                    else
                        Quadrat.setA(2*lYDist);
                case Rectangle:
                    NGRectangle Rectangle = (NGRectangle)FCurrentGO;
                    lXDist = Math.abs(Rectangle.getMiddlePoint().getXAsInt() - gridPoint.getXAsInt());
                    lYDist = Math.abs(Rectangle.getMiddlePoint().getYAsInt() - gridPoint.getYAsInt());
                    Rectangle.setA(2*lXDist);
                    Rectangle.setB(2*lYDist);
            }
            RenderLayer1();
        }
    }

    protected void HandleMouseClicked(MouseEvent t) {
        switch (t.getButton()) {
            case SECONDARY:
                cmLayer0.show(Layer0, t.getScreenX(), t.getScreenY());
                break;
        }
    }

    protected void UpdatecbLayers() {
        cbLayers.getItems().clear();
        for (GridLayer Layer : Grid.getLayers()) {
            cbLayers.getItems().add(Layer.getName());
        }
        if (Grid.getCurrentLayer() != null) {
            cbLayers.getSelectionModel().select(Grid.getCurrentLayer().getName());
        }
    }

    protected void UpdatecbGridSize() {
        cbGridSize.getSelectionModel().select(Grid.getGridDistance().toString());
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

    public Grid Grid;

    public GridStageController() {
        FUpdateCount = 0;
        FToolMode = ToolMode.Select;
        FDrawGrid = true;
        FCurrentGO = null;
        FCurrentGOPoint = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Line line;
        EventHandler<MouseEvent> click;
        gc1 = Layer1.getGraphicsContext2D();
        cbGridSize.getItems().add(1);
        cbGridSize.getItems().add(2);
        for( int i = 5; i <= 20; i = i + 5 ) {
            cbGridSize.getItems().add(i);
        }
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
                Grid.getCurrentLayer().removeSelectedObjects();
            }};
        cmLayer0.getItems().add(getMenuItemForLine("Remove Selected Object(s)", line, click));
        // Cancel
        line = new Line(60, 50, 150, 90);
        click = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }};
        cmLayer0.getItems().add(getMenuItemForLine("Cancel", line, click));
        btnPaintGrid.setSelected(FDrawGrid);
        FGDC = new NGGrid2DDisplayController(Layer0);
        FGLDM = new GridLayerDisplayManager(Layer1);
    }

    public void Initialize() {
        FGDC.Initialize();
        FGLDM.Initialize();
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
    }

    public void RenderScene(Boolean aComplete) {
        if (aComplete) {
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
        EndUpdateControls();
    }

}
