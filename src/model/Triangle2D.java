package model;

import transforms.Vec2D;

public class Triangle2D {
    public double a_x, a_y,b_x,b_y,c_x,c_y;

    public Triangle2D(double _a_x, double _a_y, double _b_x, double _b_y, double _c_x, double _c_y){
        this.a_x = _a_x;
        this.a_y = _a_y;
        this.b_x = _b_x;
        this.b_y = _b_y;
        this.c_x = _c_x;
        this.c_y = _c_y;
    }

    public Triangle2D(Vec2D a, Vec2D b, Vec2D c){
        this.a_x = a.getX();
        this.a_y = a.getY();
        this.b_x = b.getX();
        this.b_y = b.getY();;
        this.c_x = c.getX();
        this.c_y = c.getY();;
    }

    public Triangle2D(Triangle3D triangle3D){
        this.a_x = triangle3D.a.getX();
        this.a_y = triangle3D.a.getY();
        this.b_x = triangle3D.b.getX();
        this.b_y = triangle3D.b.getY();
        this.c_x = triangle3D.c.getX();
        this.c_y = triangle3D.c.getY();
    }

    public void shift_X(double shift){
        this.a_x += shift;
        this.b_x += shift;
        this.c_x += shift;
    }

    public void shift_Y(double shift){
        this.a_y += shift;
        this.b_y += shift;
        this.c_y += shift;
    }

    public void shift_XY(double shiftX, double shiftY){
        shift_X(shiftX);
        shift_Y(shiftY);
    }

    public void mul_X(double scale){
        this.a_x *= scale;
        this.b_x *= scale;
        this.c_x *= scale;
    }

    public void mul_Y(double scale){
        this.a_y *= scale;
        this.b_y *= scale;
        this.c_y *= scale;
    }

    public void mul_XY(double scaleX, double scaleY){
        mul_X(scaleX);
        mul_Y(scaleY);
    }
}
