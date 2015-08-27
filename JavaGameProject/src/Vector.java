public class Vector {
	public int x, y;
	public double dist;
	Vector(int aX, int aY) {
		this.x = aX;
		this.y = aY;
	}
	public void add(Vector V) {
		this.x = x + V.x;
		this.y = y + V.y;
	}
	public Vector clone() {
		return new Vector(x, y);
	}
	public double dist(Vector t) {
		return Math.sqrt(Math.pow(x-t.x,2)+Math.pow(y-t.y, 2));
	}
}
