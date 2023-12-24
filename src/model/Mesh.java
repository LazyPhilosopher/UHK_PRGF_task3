package model;

import java.util.ArrayList;

public class Mesh {
    public ArrayList<Triangle3D> polygons;

    public Mesh(ArrayList<Triangle3D> _polygons){
        this.polygons = _polygons;
    }
}
