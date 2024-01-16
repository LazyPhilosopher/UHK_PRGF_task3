package transforms;

public class Mat4PointAt extends Mat4Identity {

    private Vec3D left_vec;
    private Vec3D right_vec;
    private Vec3D front_vec;
    private Vec3D rear_vec;
    private Vec3D down_vec;
    private Vec3D up_vec;


    public Mat4PointAt(Vec3D position, Vec3D target, Vec3D down){
        // Calculate new forward direction
        this.front_vec = target.sub(position);
        this.front_vec.normSelf();
        this.rear_vec = this.front_vec.mul(-1);

        this.down_vec = down;
        this.down_vec.normSelf();

        // Calculate new up direction
        Vec3D a = this.front_vec.mul(down.dotProduct(this.front_vec));
        this.down_vec = this.down_vec.sub(a);
        this.down_vec.normSelf();
        this.up_vec = this.down_vec.mul(-1);

        this.left_vec = this.down_vec.crossProduct(this.front_vec);
        this.left_vec.normSelf();
        this.right_vec = this.left_vec.mul(-1);

        mat[0][0] = this.left_vec.getX();  mat[0][1] = this.left_vec.getY();      mat[0][2] = this.left_vec.getZ();  mat[0][3] = 0;
        mat[1][0] = this.down_vec.getX();     mat[1][1] = this.down_vec.getY();         mat[1][2] = this.down_vec.getZ();     mat[1][3] = 0;
        mat[2][0] = this.front_vec.getX();  mat[2][1] = this.front_vec.getY();      mat[2][2] = this.front_vec.getZ();  mat[2][3] = 0;
        mat[3][0] = position.getX();        mat[3][1] = position.getY();            mat[3][2] = position.getZ();        mat[3][3] = 1;
    }

    public Vec3D getLeftVector(){
        return this.left_vec;
    }

    public Vec3D getRightVector(){
        return this.right_vec;
    }

    public Vec3D getDownVector(){
        return this.down_vec;
    }

    public Vec3D getUpVector(){
        return this.up_vec;
    }

    public Vec3D getRearVector(){
        return this.rear_vec;
    }

    public Vec3D getFrontVector(){
        return this.front_vec;
    }


}
