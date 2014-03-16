package Grids;

import Uniwork.Graphics.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

public class GridStageController implements Initializable {

    public enum ToolMode{Select, Point, Line, Circle, Ellipse};

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
    private Canvas Layer0;

    @FXML
    private Canvas Layer1;

    @FXML
    private ComboBox cbGridSize;

    @FXML
    private ComboBox cbLayers;

    protected GraphicsContext gc0;
    protected GraphicsContext gc1;

    protected Integer FUpdateCount;
    protected ToolMode FToolMode;

    protected GeometryObject2D FCurrentGO;

    protected ContextMenu cmLayer0;
    protected DropShadow dsContextMenu;

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

    protected void RenderLayer0() {
        Integer index = 0;
        Color color;
        gc0.clearRect(0,0,Layer0.getWidth(),Layer0.getHeight());
        for(int i = 0; i <= Layer0.getWidth(); i = i + Grid.getGridDistance()) {
            gc0.beginPath();
            gc0.moveTo(i, 0);
            gc0.lineTo(i, Layer0.getHeight());
            if (index%2 == 0)
                color = Grid.getGridColor().darker();
            else
                color = Grid.getGridColor();
            gc0.setStroke(color);
            gc0.setLineWidth(1);
            gc0.stroke();
            gc0.closePath();
            index = index + 1;
        }
        index = 0;
        for(int i = 0; i < Layer0.getHeight(); i = i + Grid.getGridDistance()) {
            gc0.beginPath();
            gc0.moveTo(0, i);
            gc0.lineTo(Layer0.getWidth(), i);
            if (index%2 == 0)
                color = Grid.getGridColor().darker();
            else
                color = Grid.getGridColor();
            gc0.setStroke(color);
            gc0.setLineWidth(1);
            gc0.stroke();
            gc0.closePath();
            index = index + 1;
        }
    }

    protected void RenderLayer1() {
        gc1.clearRect(0,0,Layer1.getWidth(),Layer1.getHeight());
        ArrayList<Layer> Layers = Grid.getLayers();
        for (Layer Layer : Layers) {
            for (GeometryObject2D Object : Layer.getObjects()) {
                drawGeometryObject(gc1, Layer, Object);
            }
        }
    }

    protected void drawGeometryObject(GraphicsContext aGC, Layer aLayer, GeometryObject2D aObject) {
        aGC.setFill(aLayer.getObjectColor());
        if (aObject instanceof Point2D) {
            Point2D Point = (Point2D)aObject;
            drawGridPixel(aGC, Point.getXAsInt(), Point.getYAsInt(), aLayer.isObjectSelected(aObject));
        } else if (aObject instanceof Line2D) {
            Line2D Line = (Line2D)aObject;
            drawLineBresenham(aGC, Line.getA().getXAsInt(), Line.getA().getYAsInt(), Line.getB().getXAsInt(), Line.getB().getYAsInt(), aLayer.isObjectSelected(aObject));
        } else if (aObject instanceof Circle) {
            Circle Circle = (Circle)aObject;
            drawCircleBresenham(aGC, Circle.getMiddlePoint().getXAsInt(), Circle.getMiddlePoint().getYAsInt(), Circle.getRadiusAsInt(), aLayer.isObjectSelected(aObject));
        } else if (aObject instanceof Ellipse) {
            Ellipse Ellipse = (Ellipse)aObject;
            drawEllipseBresenham(aGC, Ellipse.getMiddlePoint().getXAsInt(), Ellipse.getMiddlePoint().getYAsInt(), Ellipse.getRadiusXAsInt(), Ellipse.getRadiusYAsInt(), aLayer.isObjectSelected(aObject));
        }
    }

    protected void drawLineBresenham(GraphicsContext aGC, int aX0, int aY0, int aX1, int aY1, Boolean aSelected) {
        {
            int dx =  abs(aX1-aX0), sx = aX0<aX1 ? 1 : -1;
            int dy = -abs(aY1-aY0), sy = aY0<aY1 ? 1 : -1;
            int err = dx+dy, e2;
            for(;;){  /* loop */
                drawGridPixel(aGC, aX0, aY0, aSelected);
                if (aX0==aX1 && aY0==aY1)
                    break;
                e2 = 2*err;
                if (e2 > dy) { err += dy; aX0 += sx; }
                if (e2 < dx) { err += dx; aY0 += sy; }
            }
        }
    }

    protected void drawCircleBresenham(GraphicsContext aGC, int aX, int aY, int aRadius, Boolean aSelected) {
        int f = 1 - aRadius;
        int ddF_x = 0;
        int ddF_y = -2 * aRadius;
        int x = 0;
        int y = aRadius;
        drawGridPixel(aGC, aX, aY + aRadius, aSelected);
        drawGridPixel(aGC, aX, aY - aRadius, aSelected);
        drawGridPixel(aGC, aX + aRadius, aY, aSelected);
        drawGridPixel(aGC, aX - aRadius, aY, aSelected);
        while(x < y)
        {
            if(f >= 0)
            {
                y--;
                ddF_y += 2;
                f += ddF_y;
            }
            x++;
            ddF_x += 2;
            f += ddF_x + 1;
            drawGridPixel(aGC, aX + x, aY + y, aSelected);
            drawGridPixel(aGC, aX - x, aY + y, aSelected);
            drawGridPixel(aGC, aX + x, aY - y, aSelected);
            drawGridPixel(aGC, aX - x, aY - y, aSelected);
            drawGridPixel(aGC, aX + y, aY + x, aSelected);
            drawGridPixel(aGC, aX - y, aY + x, aSelected);
            drawGridPixel(aGC, aX + y, aY - x, aSelected);
            drawGridPixel(aGC, aX - y, aY - x, aSelected);
        }
        if (aSelected) {
            drawGridPixel(aGC, aX, aY, aSelected);
        }
    }

    protected void drawEllipseBresenham(GraphicsContext aGC, int aX, int aY, int aRadiusX, int aRadiusY, Boolean aSelected) {
        int dx = 0, dy = aRadiusY; /* im I. Quadranten von links oben nach rechts unten */
        long a2 = aRadiusX*aRadiusX, b2 = aRadiusY*aRadiusY;
        long err = b2-(2*aRadiusY-1)*a2, e2; /* Fehler im 1. Schritt */
        do {
            drawGridPixel(aGC, aX + dx, aY + dy, aSelected); /* I. Quadrant */
            drawGridPixel(aGC, aX - dx, aY + dy, aSelected); /* II. Quadrant */
            drawGridPixel(aGC, aX - dx, aY - dy, aSelected); /* III. Quadrant */
            drawGridPixel(aGC, aX + dx, aY - dy, aSelected); /* IV. Quadrant */

            e2 = 2*err;
            if (e2 <  (2*dx+1)*b2) { dx++; err += (2*dx+1)*b2; }
            if (e2 > -(2*dy-1)*a2) { dy--; err -= (2*dy-1)*a2; }
        } while (dy > 0);
        dx--;
        while (dx++ < aRadiusX) { /* fehlerhafter Abbruch bei flachen Ellipsen (b=1) */
            drawGridPixel(aGC, aX + dx, aY, aSelected); /* -> Spitze der Ellipse vollenden */
            drawGridPixel(aGC, aX - dx, aY, aSelected);
        }
    }

    protected void drawGridPixel(GraphicsContext aGC, int aX, int aY, Boolean aSelected) {
        int PX = (aX - 1) * Grid.getGridDistance();
        int PY = (aY - 1) * Grid.getGridDistance();
        aGC.fillRect(PX, PY, Grid.getGridDistance(), Grid.getGridDistance());
        if (aSelected) {
            drawGridPixelFrame(aGC, aX, aY);
        }
    }

    protected void drawGridPixelFrame(GraphicsContext aGC, int aX, int aY) {
        int PX = (aX - 1) * Grid.getGridDistance();
        int PY = (aY - 1) * Grid.getGridDistance();
        aGC.setLineWidth(2);
        aGC.setStroke(Color.MAGENTA);
        aGC.strokeRect(PX, PY, Grid.getGridDistance(), Grid.getGridDistance());
    }

    protected void HandleMousePressed(MouseEvent t) {
        if (t.getButton() == MouseButton.PRIMARY ) {
            GeometryObject2D layerObject;
            Layer Layer = Grid.getCurrentLayer();
            Point2D gridPoint = Grid.CoordinatesToGridCoordinates(new Point2D(t.getX(), t.getY()));
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
                    if (layerObject == null || !(layerObject instanceof Point2D)) {
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
            }
        }
    }

    protected void HandleMouseReleased(MouseEvent t) {
        if (t.getButton() == MouseButton.PRIMARY && FCurrentGO != null) {
            FCurrentGO = null;
        }
    }

    protected void HandleMouseDragged(MouseEvent t) {
        int lXDist;
        int lYDist;
        if (FCurrentGO != null) {
            Point2D gridPoint = Grid.CoordinatesToGridCoordinates(new Point2D(t.getX(), t.getY()));
            switch (FToolMode) {
                case Select:
                    if (FCurrentGO instanceof Point2D) {
                        Point2D Point = (Point2D)FCurrentGO;
                        Point.setX(gridPoint.getX());
                        Point.setY(gridPoint.getY());
                    } else if (FCurrentGO instanceof Circle) {
                        Circle Circle = (Circle)FCurrentGO;
                        Circle.setMiddlePoint(gridPoint.getX(), gridPoint.getY());
                    }
                    break;
                case Line:
                    Line2D Line = (Line2D)FCurrentGO;
                    Line.getB().setX(gridPoint.getX());
                    Line.getB().setY(gridPoint.getY());
                    break;
                case Circle:
                    Circle Circle = (Circle)FCurrentGO;
                    lXDist = Math.abs(Circle.getMiddlePoint().getXAsInt() - gridPoint.getXAsInt());
                    lYDist = Math.abs(Circle.getMiddlePoint().getYAsInt() - gridPoint.getYAsInt());
                    if (lXDist > lYDist)
                        Circle.setRadius(lXDist);
                    else
                        Circle.setRadius(lYDist);
                    break;
                case Ellipse:
                    Ellipse Ellipse = (Ellipse)FCurrentGO;
                    lXDist = Math.abs(Ellipse.getMiddlePoint().getXAsInt() - gridPoint.getXAsInt());
                    lYDist = Math.abs(Ellipse.getMiddlePoint().getYAsInt() - gridPoint.getYAsInt());
                    Ellipse.setRadiusX(lXDist);
                    Ellipse.setRadiusY(lYDist);
                    break;
            }
            RenderLayer1();
        }
    }

    protected void HandleMouseClicked(MouseEvent t) {
        if (t.getButton() == MouseButton.SECONDARY) {
            cmLayer0.show(Layer0, t.getScreenX(), t.getScreenY());
        }
    }

    protected void UpdatecbLayers() {
        Layer selLayer = null;
        cbLayers.getItems().clear();
        ArrayList<Layer> Layers;
        Layers = Grid.getLayers();
        for (Layer Layer : Layers) {
            selLayer = Layer;
            cbLayers.getItems().add(Layer.getName());
        }
        if (selLayer != null) {
            cbLayers.getSelectionModel().select(selLayer.getName());
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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Line line;
        EventHandler<MouseEvent> click;
        gc0 = Layer0.getGraphicsContext2D();
        gc1 = Layer1.getGraphicsContext2D();
        cbGridSize.getItems().add(1);
        cbGridSize.getItems().add(2);
        for( int i = 5; i <= 20; i = i + 5 )
        {
            cbGridSize.getItems().add(i);
        }
        ToggleGroup group = new ToggleGroup();
        btnSelect.setToggleGroup(group);
        btnPoint.setToggleGroup(group);
        btnLine.setToggleGroup(group);
        btnCircle.setToggleGroup(group);
        btnEllipse.setToggleGroup(group);
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
    }

    public void Initialize() {
        RenderLayer0();
        RenderLayer1();
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

    public void UpdateControls() {
        if (InUpdate()) return;
        BeginUpdateControls();
        UpdatecbLayers();
        UpdatecbGridSize();
        EndUpdateControls();
    }

}
