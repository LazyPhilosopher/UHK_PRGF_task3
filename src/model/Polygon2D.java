package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon2D {
    private final List<Point> _vertices;

    /**
     * Main class constructor method.
     * @param vertices list of polygon edge points.
     */
    public Polygon2D(List<Point> vertices){
        this._vertices = vertices;
    }

    // Polygon vertices getter method.
    public List<Point>getVertices(){
        return _vertices;
    }

    public void removePoint(Point point){
        this._vertices.remove(point);
    }
}
