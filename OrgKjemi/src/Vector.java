public class Vector {
	public int x, y;
	Vector(int aX, int aY) {
		this.x = aX;
		this.y = aY;
	}
	public void add(Vector V) {
		this.x = x + V.x;
		this.y = y + V.y;
	}
	public void set(Vector V) {
		this.x = V.x;
		this.y = V.y;
	}
	public Vector clone() {
		return new Vector(x, y);
	}
}
