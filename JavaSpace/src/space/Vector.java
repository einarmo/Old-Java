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
	public void add(Vector V) {
		this.x = x + V.x*Space.calcmod;
		this.y = y + V.y*Space.calcmod;
	}

}
