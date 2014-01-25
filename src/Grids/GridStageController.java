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

    @FXML
    protected void handlecbGridSize(ActionEvent Event){
        if (Event.getEventType().equals(ActionEvent.ACTION)) {
            Integer i = Integer.parseInt(cbGridSize.getValue().toString());
            Grid.setGridDistance(i);
        }
    }

    @FXML
    protected void handlecbLayers(ActionEvent Event){
        if (Event.getEventType().equals(ActionEvent.ACTION)) {
            if (cbLayers.getValue() != null) {
                Grid.setCurrentLayer(cbLayers.getValue().toString());
            }
        }
    }

    @FXML
    protected void handleAddLayer(){
        Grid.addLayer("LAYER" + (Grid.getLayers().size() + 1), "Layer " + Grid.getLayers().size(), Color.rgb(getRandomValue(),getRandomValue(),getRandomValue()));
    }

    protected void RenderLayer0() {
        gc0.clearRect(0,0,Layer0.getWidth(),Layer0.getHeight());
        for(int i = 0; i <= Layer0.getWidth(); i = i + Grid.getGridDistance()) {
            gc0.beginPath();
            gc0.moveTo(i, 0);
            gc0.lineTo(i, Layer0.getHeight());
            gc0.setStroke(Grid.getGridColor());
            gc0.setLineWidth(1);
            gc0.stroke();
            gc0.closePath();
        }
        for(int i = 0; i < Layer0.getHeight(); i = i + Grid.getGridDistance()) {
            gc0.beginPath();
            gc0.moveTo(0, i);
            gc0.lineTo(Layer0.getWidth(), i);
            gc0.setStroke(Grid.getGridColor());
            gc0.setLineWidth(1);
            gc0.stroke();
            gc0.closePath();
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
            int PX = (int)(t.getX() / Grid.getGridDistance()) + 1;
            int PY = (int)(t.getY() / Grid.getGridDistance()) + 1;
            Point2D Point = Layer.getPointInLayer(PX, PY);
            if (Point == null) {
                Layer.addPoint(PX, PY);
            }
            else {
                Layer.deletePoint(Point);
            }
        }
    }

    protected void UpdatecbLayers() {
        cbLayers.getItems().clear();
        ArrayList<Layer> Layers = Grid.getLayers();
        for (Layer Layer : Layers) {
            cbLayers.getItems().add(Layer.getName());
            if (Grid.getCurrentLayer() != null && Layer.getName().equals(Grid.getCurrentLayer().getName())) {
                cbLayers.getSelectionModel().select(Grid.getCurrentLayer().getName());
            }
        }
    }

    protected void UpdatecbGridSize() {
        cbGridSize.getSelectionModel().select(Grid.getGridDistance().toString());
    }

    protected int getRandomValue() {
        return FGenerator.nextInt(255);
    }

    public Grid Grid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc0 = Layer0.getGraphicsContext2D();
        gc1 = Layer1.getGraphicsContext2D();
        FGenerator = new Random();
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
        UpdatecbLayers();
        UpdatecbGridSize();
    }

}
