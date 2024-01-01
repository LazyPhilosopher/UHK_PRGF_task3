package transforms;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * 3D vector over real numbers (final double-precision), equivalent to 3D affine
 * point, immutable
 * 
 * @author PGRF FIM UHK 
 * @version 2016
 */

public class Vec3D {
	private double x;
	private double y;
	private double z;

	/**
	 * Creates a zero vector
	 */
	public Vec3D() {
		x = y = z = 0.0f;
	}

	/**
	 * Creates a vector with the given coordinates
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param z
	 *            z coordinate
	 */
	public Vec3D(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a vector with all coordinates set to the given value
	 *
	 * @param value
	 *            coordinate
	 */
	public Vec3D(final double value) {
		this.x = value;
		this.y = value;
		this.z = value;
	}

	/**
	 * Creates a vector by cloning the given one
	 *
	 * @param v
	 *            vector to be cloned
	 */
	public Vec3D(final Vec3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	/**
	 * Creates a vector by ignoring the fourth homogeneous coordinate w of the
	 * given homogeneous 3D point
	 *
	 * @param point
	 *            homogeneous 3D point whose x,y,z will be cloned
	 */
	public Vec3D(Point3D point) {
		x = point.getX();
		y = point.getY();
		z = point.getZ();
	}

	/**
	 * Creates a vector by extracting coordinates from the given array of
	 * doubles
	 *
	 * @param array
	 *            double array of size 3 (asserted)
	 */
	public Vec3D(final double[] array) {
		assert(array.length >= 3);
		x = array[0];
		y = array[1];
		z = array[2];
	}

	/**
	 * Returns the x coordinate
	 *
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate
	 *
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the z coordinate
	 *
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Adds _x to the x coordinate
	 */
	public void addX(double _x) {this.x += _x;}

	/**
	 * Adds _y the y coordinate
	 */
	public void addY(double _y) {this.y += _y;}

	/**
	 * Adds _z to the z coordinate
	 */
	public void addZ(double _z) {this.z += _z;}

	/**
	 * Set the x coordinate
	 */
	public void setX(double _x) {this.x = _x;}

	/**
	 * Set the y coordinate
	 */
	public void setY(double _y) {this.y = _y;}

	/**
	 * Set the z coordinate
	 */
	public void setZ(double _z) {this.z = _z;}

	/**
	 * Returns a clone of this vector with the x coordinate replaced by the
	 * given value
	 *
	 * @param x
	 *            x coordinate
	 * @return new Vec3D instance
	 */
	public Vec3D withX(double x) {
		return new Vec3D(x, this.getY(), this.getZ());
	}

	/**
	 * Returns a clone of this vector with the y coordinate replaced by the
	 * given value
	 *
	 * @param y
	 *            y coordinate
	 * @return new Vec3D instance
	 */
	public Vec3D withY(double y) {
		return new Vec3D(this.getX(), y, this.getZ());
	}

	/**
	 * Returns a clone of this vector with the z coordinate replaced by the
	 * given value
	 *
	 * @param z
	 *            z coordinate
	 * @return new Vec3D instance
	 */
	public Vec3D withZ(double z) {
		return new Vec3D(this.getX(), this.getY(), z);
	}

	/**
	 * Returns 2D vector with this x and y coordinates, i.e. an orthogonal
	 * projection of this as an affine point into the xy plane
	 *
	 * @return new Vec2D instance
	 */
	public Vec2D ignoreZ() {
		return new Vec2D(x, y);
	}

	/**
	 * Returns the result of vector addition of the given vector
	 *
	 * @param v
	 *            vector to add
	 * @return new Vec3D instance
	 */
	public Vec3D add(final Vec3D v) {
		return new Vec3D(x + v.x, y + v.y, z + v.z);
	}

	/**
	 * Returns the result of vector subtraction of the given vector
	 *
	 * @param v
	 *            vector to subtract
	 * @return new Vec3D instance
	 */
	public Vec3D sub(final Vec3D v) {
		return new Vec3D(x - v.x, y - v.y, z - v.z);
	}

	/**
	 * Returns the result of scalar multiplication
	 *
	 * @param d
	 *            scalar value of type double
	 * @return new Vec3D instance
	 */
	public Vec3D mul(final double d) {
		return new Vec3D(x * d, y * d, z * d);
	}

	/**
	 * Returns the result of multiplication by the given 3x3 matrix thus
	 * applying the transformation contained within
	 *
	 * @param m
	 *            3x3 matrix
	 * @return new Vec3D instance
	 */
	public Vec3D mul(final Mat3 m) {
		return new Vec3D(
				m.mat[0][0] * x + m.mat[1][0] * y + m.mat[2][0] * z,
				m.mat[0][1] * x + m.mat[1][1] * y + m.mat[2][1] * z,
				m.mat[0][2] * x + m.mat[1][2] * y + m.mat[2][2] * z);
	}

	/**
	 * Returns the result of applying the given quaternion to this vector
	 *
	 * @param q
	 *            quaternion
	 * @return new Vec3D instance
	 */
	public Vec3D mul(final Quat q) {
		//final Quat p = q.mulR(new Quat(0, x, y, z)).mulR(q.inverse());
		//return new Vec3D(p.i, p.j, p.k);
		final Vec3D t = q.getIJK().mul(2).cross(this);
		return this.add(t.mul(q.getR())).add(q.getIJK().cross(t));
	}

	/**
	 * Returns the result of element-wise multiplication with the given vector
	 *
	 * @param v
	 *            3D vector
	 * @return new Vec3D instance
	 */
	public Vec3D mul(final Vec3D v) {
		return new Vec3D(x * v.x, y * v.y, z * v.z);
	}

	/**
	 * Returns the result of dot-product with the given vector
	 *
	 * @param v
	 *            3D vector
	 * @return double-precision floating point value
	 */
	public double dot(final Vec3D v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * Returns the result of cross-product with the given vector, i.e. a vector
	 * perpendicular to both this and the given vector, the direction is
	 * right-handed
	 *
	 * @param v
	 *            3D vector
	 * @return new Vec3D instance
	 */
	public Vec3D cross(final Vec3D v) {
		return new Vec3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y
				* v.x);
	}

	/**
	 * Returns a collinear unit vector (by dividing all vector components by
	 * vector length) if possible (nonzero length), empty Optional otherwise
	 *
	 * @return new Optional<Vec3D> instance
	 */
	public Optional<Vec3D> normalized() {
		final double len = length();
		if (len == 0.0)
			return Optional.empty();
		return Optional.of(new Vec3D(x / len, y / len, z / len));
	}

	/**
	 * Returns the vector opposite to this vector
	 *
	 * @return new Vec2D instance
	 */
	public Vec3D opposite() {
		return new Vec3D(-x, -y, -z);
	}

	/**
	 * Returns the length of this vector
	 *
	 * @return double-precision floating point value
	 */
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Compares this object against the specified object.
	 *
	 * @param obj
	 *            the object to compare with.
	 * @return {@code true} if the objects are the same; {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj != null) && (obj instanceof Vec3D)
				&& (new Double(((Vec3D) obj).getX()).equals(getX()))
				&& (new Double(((Vec3D) obj).getY()).equals(getY()))
				&& (new Double(((Vec3D) obj).getZ()).equals(getZ()));
	}

	/**
     * Returns a hash code value for the object. 
     * 
     * @return  a hash code value for this object.
     */
    @Override
	public int hashCode(){
		return Objects.hash(this.getX(), this.getY(), this.getZ());
	}

	/**
	 * Compares this Vec3D against the specified Vec3D with epsilon.
	 *
	 * @param vec
	 *            the vector to compare with.
	 * @param epsilon
	 *            the maximum epsilon between actual and specified value for
	 *            which both numbers are still considered equal
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Vec3D vec, double epsilon) {
		return (this == vec) || (vec != null)
				&& Compare.eEquals(getX(), vec.getX(), epsilon)
				&& Compare.eEquals(getY(), vec.getY(), epsilon)
				&& Compare.eEquals(getZ(), vec.getZ(), epsilon);
	}

	/**
	 * Compares this Vec3D against the specified Vec3D with epsilon.
	 *
	 * @param vec
	 *            the vector to compare with.
	 * @return {@code true} if the objects are considered equal; {@code false}
	 *         otherwise.
	 */
	public boolean eEquals(Vec3D vec) {
		return eEquals(vec, Compare.EPSILON);
	}

	/**
	 * Returns String representation of this vector
	 *
	 * @return comma separated floating-point values in brackets
	 */
	@Override
	public String toString() {
		return toString("%4.1f");
	}

	/**
	 * Returns String representation of this vector with coordinates formated
	 * according to the given format, see
	 * {@link String#format(String, Object...)}
	 *
	 * @param format
	 *            String format applied to each coordinate
	 * @return comma separated floating-point values in brackets
	 */
	public String toString(String format) {
		return String.format(Locale.US, "(" + format + "," + format + "," + format + ")",
				x, y, z);
	}


	public void normSelf(){
		if (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)){
			if(Double.isInfinite(x)){
				if (x > 0){
					x = 1;
				} else {
					x=-1;
				}
			} else {
				x = 0;
			}

			if(Double.isInfinite(y)){
				if (y > 0){
					y = 1;
				} else {
					y=-1;
				}
			} else {
				y = 0;
			}

			if(Double.isInfinite(z)){
				if (z > 0){
					z = 1;
				} else {
					z=-1;
				}
			} else {
				z = 0;
			}
		}

		if (Double.isNaN(x)){
			x = 0;
		}
		if (Double.isNaN(y)){
			y = 0;
		}
		if (Double.isNaN(z)){
			z = 0;
		}

		double l = Math.sqrt(this.getX()*this.getX() + this.getY()*this.getY() + this.getZ()*this.getZ());
		// Check for zero, infinity, or NaN values
		if (l == 0 || Double.isInfinite(l) || Double.isNaN(l)) {
			// Handle the edge case, e.g., by setting to a default normalized vector or zero vector
			if(x>0){
				x = 1;
			}else{
				x = -1;
			}

			if(y>0){
				y = 1;
			}else{
				y = -1;
			}

			if(z>0){
				z = 1;
			}else{
				z = -1;
			}

			l = Math.sqrt(this.getX()*this.getX() + this.getY()*this.getY() + this.getZ()*this.getZ());
		}
		// Normal normalization process
		x /= l;
		y /= l;
		z /= l;
	}

	public Vec3D crossProduct(Vec3D a){
		Vec3D out = new Vec3D();
		out.setX(this.getY() * a.getZ() - this.getZ() * a.getY());
		out.setY(this.getZ() * a.getX() - this.getX() * a.getZ());
		out.setZ(this.getX() * a.getY() - this.getY() * a.getX());
		return out;
	}

	public double dotProduct(Vec3D a){
		return  a.getX() * this.getX() +
				a.getY() * this.getY() +
				a.getZ() * this.getZ();
	}
}
