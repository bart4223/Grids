package Grids;

import Uniwork.Graphics.Point2D;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class GridStageController implements Initializable {

    @FXML
    private Button btnAddLayer;

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
    protected Random FGenerator;
    protected Integer FUpdateCount;

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
     protected void handleAddLayer(){
        Grid.addLayer("LAYER" + (Grid.getLayers().size() + 1), "Layer " + (Grid.getLayers().size() + 1), Color.rgb(getRandomValue(),getRandomValue(),getRandomValue()));
    }

    @FXML
    protected void handleClearLayer(){
        Grid.clearCurrentLayer();
    }

    @FXML
    protected void handleRemoveLayer(){
        Grid.removeCurrentLayer();
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
        int PX;
        int PY;
        gc1.clearRect(0,0,Layer1.getWidth(),Layer1.getHeight());
        ArrayList<Layer> Layers = Grid.getLayers();
        for (Layer Layer : Layers) {
            gc1.setFill(Layer.getPointColor());
            for (Point2D Point : Layer.getPoints()) {
                PX = (Point.getXAsInt() - 1) * Grid.getGridDistance();
                PY = (Point.getYAsInt() - 1) * Grid.getGridDistance();
                gc1.fillRect(PX, PY, Grid.getGridDistance(), Grid.getGridDistance());
            }
        }
    }

    protected void HandleMouseClicked(MouseEvent t) {
        if (t.getButton() == MouseButton.PRIMARY ) {
            Layer Layer = Grid.getCurrentLayer();
            Point2D gridPoint = Grid.CoordinatesToGridCoordinates(new Point2D(t.getX(), t.getY()));
            Point2D layerPoint = Layer.getPointInLayer(gridPoint.getXAsInt(), gridPoint.getYAsInt());
            if (layerPoint == null) {
                Layer.addPoint(gridPoint.getXAsInt(), gridPoint.getYAsInt());
            }
            else {
                Layer.deletePoint(layerPoint);
            }
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

    protected int getRandomValue() {
        return FGenerator.nextInt(255);
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

    public Grid Grid;

    public GridStageController() {
        FUpdateCount = 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc0 = Layer0.getGraphicsContext2D();
        gc1 = Layer1.getGraphicsContext2D();
        FGenerator = new Random();
        cbGridSize.getItems().add(1);
        cbGridSize.getItems().add(2);
        for( int i = 5; i <= 20; i = i + 5 )
        {
            cbGridSize.getItems().add(i);
        }
    }

    public void Initialize() {
        RenderLayer0();
        RenderLayer1();
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
