package transforms;

/**
 * A 4x4 matrix of right-handed rotation about z-axis
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */
public class Mat4RotZ extends Mat4Identity {

	/**
	 * Creates a 4x4 transformation matrix equivalent to right-handed rotation
	 * about z-axis
	 * 
	 * @param alpha
	 *            rotation angle in radians
	 */
	public Mat4RotZ(final double alpha) {
		mat[0][0] = Math.cos(alpha);
		mat[1][1] = Math.cos(alpha);
		mat[1][0] = -Math.sin(alpha);
		mat[0][1] = Math.sin(alpha);
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