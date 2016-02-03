public class Vector3D {
	public double x, y, z;
	Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	Vector3D(Vector3D a, Vector3D b) {
		this.x = b.x-a.x;
		this.y = b.y-a.y;
		this.z = b.z-a.z;
	}
	public void addVal(double nX, double nY, double nZ) {
		this.x = x + nX;
		this.y = y + nY;
		this.z = z + nZ;
	}
	public void setVal(double nX, double nY, double nZ) {
		this.x = nX;
		this.y = nY;
		this.z = nZ;
	}
	public static Vector3D dist(Vector3D a, Vector3D b) {
		return new Vector3D(a.x-b.x, a.y-b.y, a.z-b.z);
	}
	public static Vector3D add(Vector3D a, Vector3D b) {
		return new Vector3D(a.x+b.x, a.y+b.y, a.z+b.z);
	}
	public void add(Vector3D V, double mod) {
		this.x = x + V.x*mod;
		this.y = y + V.y*mod;
		this.z = z + V.z*mod;
	}
	public static Vector3D mult(double mt, Vector3D a) {
		return new Vector3D(mt*a.x, mt*a.y, mt*a.z);
	}
	public static double product(Vector3D a, Vector3D b) {
		double ret = a.x*b.x+a.y*b.y+a.z*b.z;
		return ret;
	}
	public double size() {
		return Math.sqrt(x*x+y*y+z*z);
	}
	public Vector3D normalize() {
		double s = this.size();
		return Vector3D.mult(1.0/s, this);
	}
	public static Vector3D cross(Vector3D a, Vector3D b) {
		return new Vector3D(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
	}
	public Vector3D clone() {
		return new Vector3D(x, y, z);
	}
}

