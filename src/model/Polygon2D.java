package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon2D {
    private final List<Point> _vertices;
    Color _color;

    /**
     * Main class constructor method.
     * @param vertices list of polygon edge points.
     */
    public Polygon2D(List<Point> vertices, Color color){
        this._vertices = vertices;
        this._color = color;
    }

    // Polygon vertices getter method.
    public List<Point>getVertices(){
        return _vertices;
    }

    public void removePoint(Point point){
        this._vertices.remove(point);
    }
}
