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

    public void shift_X(double shift){
        this.a.setX(this.a.getX() + shift);
        this.b.setX(this.b.getX() + shift);
        this.c.setX(this.c.getX() + shift);
    }

    public void shift_Y(double shift){
        this.a.setY(this.a.getY() + shift);
        this.b.setY(this.b.getY() + shift);
        this.c.setY(this.c.getY() + shift);
    }

    public void shift_Z(double shift){
        this.a.setZ(this.a.getZ() + shift);
        this.b.setZ(this.b.getZ() + shift);
        this.c.setZ(this.c.getZ() + shift);
    }

    public void shift_XYZ(double scaleX, double scaleY, double scaleZ){
        shift_X(scaleX);
        shift_Y(scaleY);
        shift_Z(scaleZ);
    }

}
