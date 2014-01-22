package Grids;

import Uniwork.Graphics.Point2D;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class GridStageController implements Initializable {

    @FXML
    private Canvas Layer0;

    @FXML
    private Canvas Layer1;

    protected GraphicsContext gc0;

    protected GraphicsContext gc1;

    protected void RenderLayer0() {
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
        ArrayList<Layer> Layers = Grid.getLayers();
        Iterator lILayer = Layers.iterator();
        while(lILayer.hasNext())  {
            Layer Layer = (Layer)lILayer.next();
            //System.out.println(Layer.toString());
            gc1.setFill(Layer.GetPointColor());
            Iterator lItr = Layer.GetPoints().iterator();
            while(lItr.hasNext())  {
                Point2D Point = (Point2D)lItr.next();
                PX = (Point.getXAsInt() - 1) * Grid.getGridDistance();
                PY = (Point.getYAsInt() - 1) * Grid.getGridDistance();
                gc1.fillRect(PX, PY, Grid.getGridDistance(), Grid.getGridDistance());
            }
        }
    }

    public Grid Grid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc0 = Layer0.getGraphicsContext2D();
        gc1 = Layer1.getGraphicsContext2D();
    }

    protected void Initialize() {
        RenderLayer0();
        RenderLayer1();
    }

    public void RenderScene(Boolean aComplete) {
        if (aComplete) {
            RenderLayer0();
        }
        RenderLayer1();
    }

}
