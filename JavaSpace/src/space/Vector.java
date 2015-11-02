package space;

public class Vector {
	public double x, y;
	Vector(double aX, double aY) {
		this.x = aX;
		this.y = aY;
	}
	public void addVal(double nX, double nY) {
		this.x = x + nX;
		this.y = y + nY;
	}
	public void setVal(double nX, double nY) {
		this.x = nX;
		this.y = nY;
	}
	public static Vector add(Vector a, Vector b) {
		return new Vector(a.x+b.x, a.y+b.y);
	}
	public void add(Vector V, double mod) {
		this.x = x + V.x*mod;
		this.y = y + V.y*mod;
	}
	public static Vector mult(double mt, Vector a) {
		return new Vector(mt*a.x, mt*a.y);
	}
	public static Vector product(Vector a, Vector b) {
		Vector ret = new Vector(a.x*b.x, a.y*b.y);
		return ret;
	}
	public double size() {
		return Math.sqrt(x*x+y*y);
	}

}
