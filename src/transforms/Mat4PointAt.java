package transforms;

public class Mat4PointAt extends Mat4Identity {


    public Mat4PointAt(Vec3D position, Vec3D target, Vec3D up){
        // Calculate new forward direction
        Vec3D new_forward = target.sub(position);
        new_forward.normSelf();

        // Calculate new up direction
        Vec3D a = new_forward.mul(up.dotProduct(new_forward));
        Vec3D new_up = up.sub(a);
        new_up.normSelf();

        Vec3D new_right = new_up.crossProduct(new_forward);
        new_right.normSelf();

        mat[0][0] = new_right.getX();   mat[0][1] = new_right.getY();   mat[0][2] = new_right.getZ();   mat[0][3] = 0;
        mat[1][0] = new_up.getX();      mat[1][1] = new_up.getY();      mat[1][2] = new_up.getZ();      mat[1][3] = 0;
        mat[2][0] = new_forward.getZ(); mat[2][1] = new_forward.getZ(); mat[2][2] = new_forward.getZ(); mat[2][3] = 0;
        mat[3][0] = position.getX();    mat[3][1] = position.getY();    mat[3][2] = position.getZ();    mat[3][3] = 1;
    }
}
