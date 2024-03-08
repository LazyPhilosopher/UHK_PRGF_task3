package model;

import transforms.Vec2D;

import java.awt.*;
import java.util.List;

public class Polygon2D {
    private final List<Vec2D> _vertices;
    Color _color;

    /**
     * Main class constructor method.
     * @param vertices list of polygon edge points.
     */
    public Polygon2D(List<Vec2D> vertices, Color color){
        this._vertices = vertices;
        this._color = color;
    }

    public void setColor(Color color){
        this._color = color;
    }

    public Color getColor(){
        return this._color;
    }

    // Polygon vertices getter method.
    public List<Vec2D>getVertices(){
        return _vertices;
    }
}
