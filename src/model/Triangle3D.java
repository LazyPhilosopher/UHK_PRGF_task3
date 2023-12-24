package model;

import transforms.Vec3D;

import java.awt.Color;

public class Triangle3D {
    public Vec3D a, b, c;
    public  Color color;

    public Triangle3D(){
        this.a = new Vec3D(0,0,0);
        this.b = new Vec3D(0,0,0);
        this.c = new Vec3D(0,0,0);
        this.color = new Color(0xFFFFFF);
    }

    public Triangle3D(Vec3D _a, Vec3D _b, Vec3D _c, Color _color){
        this.a = _a;
        this.b = _b;
        this.c = _c;
        this.color = _color;
    }
}
