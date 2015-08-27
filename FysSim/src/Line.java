public class Line {
	public Vector p1, p2;
	public double size;
	Polygon pf;
	public double dist; // Distance from p1 to the center of mass.
	Line(Vector p1, Vector p2, Polygon p) {
		this.p1 = p1;
		this.p2 = p2;
		this.pf = p;
		this.size = Math.sqrt(Math.pow(p2.x-p1.x,2)+Math.pow(p2.y-p1.y, 2));
	}
	Line(Vector p1, Vector p2) {
		this.p1 = p1;
		this.p2 = p2;
		this.size = Math.sqrt(Math.pow(p2.x-p1.x,2)+Math.pow(p2.y-p1.y, 2));
	}
	public void setDist(Polygon p) {
		dist = p.pyt(p1.x-p.C.x, p1.y-p.C.y);
	}
	public Vector[] checkContact(Polygon p) {
		if(p.isCloseToLine(this)) {
			for(int i = 0; i<p.points.length; i++) {
				
			}
			return null;
		} else {
			return null;
		}
	}
}