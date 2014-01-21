package Grids;

import Uniwork.Graphics.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Layer {

    protected String FName;
    protected String FDescription;
    protected ArrayList<Point2D> FPoints;
    protected Color FPointColor;

    public Layer() {
        this("", "", Color.BLACK);
    }

    public Layer(String aName, String aDescription, Color aColor) {
        FPoints = new ArrayList<Point2D>();
        FName = aName;
        FDescription = aDescription;
        FPointColor = aColor;
    }

    public void AddPoint(Point2D aPoint) {
        FPoints.add(aPoint);
    }

    public Point2D AddPoint(double aX, double aY) {
        Point2D Point = new Point2D(aX, aY);
        AddPoint(Point);
        return Point;
    }

    public String GetName() {
        return FName;
    }

    public Color GetPointColor() {
        return FPointColor;
    }

    public ArrayList<Point2D> GetPoints() {
        return FPoints;
    }

}
