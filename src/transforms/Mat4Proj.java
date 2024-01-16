package transforms;

public class Mat4Proj extends Mat4Identity {
    private final double near = 0.1;
    private double far = 1000.0;
    private double field_of_view;
    private double aspect_ratio = (double) 640 / (double) 480;
    private double rad_field_of_view;

    public Mat4Proj(double deg_field_of_view){
        this.field_of_view = deg_field_of_view;
        this.rad_field_of_view = 1.0 / Math.tan(this.field_of_view * 0.5f / 180.0 * Math.PI);
        mat[0][0] = aspect_ratio * rad_field_of_view;
        mat[1][1] = rad_field_of_view;
        mat[2][2] = far / (far - near);
        mat[2][3] = (-far*near) / (far - near);
        mat[3][2] = 1.0;
        mat[3][3] = 0.0;
    }



    public double getFieldOfView(){return this.field_of_view;}
}
