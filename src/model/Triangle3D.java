package model;

import transforms.Vec2D;
import transforms.Vec3D;

import java.awt.Color;

public class Triangle3D {
    public Vec3D a, b, c, norm;
    public  Color color;
    public Vec2D t1, t2, t3;

    public Triangle3D(){
        this.a = new Vec3D(0,0,0);
        this.b = new Vec3D(0,0,0);
        this.c = new Vec3D(0,0,0);
        this.color = new Color(0xFFFFFF);
    }

    public Triangle3D(Vec3D a, Vec3D b, Vec3D c, Vec2D t1, Vec2D t2, Vec2D t3){
        this.a = a; this.b = b; this.c = c;
        this.t1 = t1; this.t2 = t2; this.t3 = t3;
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

    public void setNorm(Vec3D norm){
        this.norm = norm;
    }

    public Vec3D calculateNorm(){
        Vec3D line1 = new Vec3D();
        Vec3D line2 = new Vec3D();
        Vec3D norm = new Vec3D();

        line1.setX(this.b.getX() -  this.a.getX());
        line1.setY(this.b.getY() -  this.a.getY());
        line1.setZ(this.b.getZ() -  this.a.getZ());

        line2.setX(this.c.getX() -  this.a.getX());
        line2.setY(this.c.getY() -  this.a.getY());
        line2.setZ(this.c.getZ() -  this.a.getZ());

        line1.normSelf();
        line2.normSelf();
        norm = line1.crossProduct(line2);
        norm.normSelf();

        return norm;
    }

    public boolean is_textured(){
        return (!(this.t1 == null) || !(this.t2 == null) || !(this.t3 == null));
    }
}
