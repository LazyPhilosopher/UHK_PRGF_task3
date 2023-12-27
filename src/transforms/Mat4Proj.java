package transforms;

public class Mat4Proj extends Mat4Identity {
    private final double near = 0.1;
    private double far = 1000.0;
    private double field_fo_view = 160.0;
    private double aspect_ratio = (double) 640 / (double) 480;
    private double rad_field_of_view = 1.0 / Math.tan(field_fo_view * 0.5f / 180.0 * Math.PI);

    public Mat4Proj(){
        mat[0][0] = aspect_ratio * rad_field_of_view;
        mat[1][1] = rad_field_of_view;
        mat[2][2] = far / (far - near);
        mat[2][3] = (-far*near) / (far - near);
        mat[3][2] = 1.0;
        mat[3][3] = 0.0;
    }

    public Vec3D MultiplyVector(Vec3D i){
        Vec3D o = new Vec3D();
        o.setX(i.getX() * mat[0][0] + i.getY() * mat[1][0] + i.getZ() * mat[2][0] + mat[3][0]);
        o.setY(i.getX() * mat[0][1] + i.getY() * mat[1][1] + i.getZ() * mat[2][1] + mat[3][1]);
        o.setZ(i.getX() * mat[0][2] + i.getY() * mat[1][2] + i.getZ() * mat[2][2] + mat[3][2]);
        double w = i.getX() * mat[0][3] + i.getY() * mat[1][3] + i.getZ() * mat[2][3] + mat[3][3];

        if (w != 0.0){
            o.setX(o.getX()/w);
            o.setY(o.getY()/w);
            o.setZ(o.getZ()/w);
        }
        return o;
    }
}
