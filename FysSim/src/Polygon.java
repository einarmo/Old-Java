
public class Polygon {
	public Vector[] points;
	double mass, rot, rotV, rotA;
	public Line[] lines;
	public Vector P, rP, C, K, cC, V, A; //rP is the initial center of mass relative to the lines.
	//C is the centroid of the polygon
	//K is the current total position
	//cC is the current center of rotation.
	//V is the current velocity vector of the centroid
	int area, max;
	Polygon(Vector[] points, double mass, Vector P) { //creates a polygon from an abstract set of lines.
		//line 0 should start in the point (0,0)
		//This program will not work correctly with self intersecting polygons
		this.mass = mass; //mass of the object as a whole, mathematically concentrated in the center of mass P.
		this.points = points; //List of points that make up the polygon.
		this.P = P; //Position of the center of mass in the main graphics field, this is NOT relative to the lines at all.
		this.rot = 0; //rotation in rad relative to the contact point. 
		//If the contact point changes, this will have to be recalculated.
		this.rotV = 0; //Speed of rotation in rad/s relative to the contact point. 
		calcTotalArea();
		calcCentroid();
		this.rotA = 0;
		this.V = new Vector(0,0);
		this.A = new Vector(0,0);
		this.K = new Vector(0,0);
		K.add(P);
		cC = C;
		calcMaxDist();
		for(int i = 0; i<points.length; i++) {
			points[i].setDist(this);
		}
	}
	public void calcCentroid() {
		int xpos = 0;
		int ypos = 0;
		int xp1,xp2,yp1,yp2;
		for(int i = 0; i<points.length; i++) {
			if(i == points.length-1) {
				xp1 = points[i].x;
				xp2 = points[0].x;
				yp1 = points[i].y;
				yp2 = points[0].y;
			} else{
				xp1 = points[i].x;
				xp2 = points[i+1].x;
				yp1 = points[i].y;
				yp2 = points[i+1].y;
			}
			xpos = xpos +(xp1+xp2)*(xp1*yp2-xp2*yp1);
			ypos = ypos +(yp1+yp2)*(xp1*yp2-xp2*yp1);
		}
		xpos = xpos/(6*area);
		ypos = ypos/(6*area);
		C = new Vector(xpos, ypos);
	}
	public void calcTotalArea() {
		area= 0;
		int xp1,xp2,yp1,yp2;
		for(int i = 0; i<points.length; i++) {
			if(i == points.length-1) {
				xp1 = points[i].x;
				xp2 = points[0].x;
				yp1 = points[i].y;
				yp2 = points[0].y;
			} else{
				xp1 = points[i].x;
				xp2 = points[i+1].x;
				yp1 = points[i].y;
				yp2 = points[i+1].y;
			}
			area = area + (xp1*yp2-xp2*yp1);
		}
		area = (int) area/2;
	}
	public void doContactCalc() {
		double sinAng = ((P.y-K.y)/pyt(P.x-K.x, P.y-K.y));
		double change = (FysSim.grav/pyt(P.x-K.x, P.y-K.y))*sinAng;
		rotV = rotV + change;
		rot = rot + rotV;
	}
	public double pyt(int x, int y) {
		return (Math.sqrt(Math.pow(y, 2)+Math.pow(x, 2)));
	}
	public Vector convPoint(int pointNum) { //Converts the first point in the line from absolute to relative coordinates.
		Vector tmp = points[pointNum].clone();
		tmp.x = (int) Math.round(K.x + ((points[pointNum].x-cC.x)*Math.cos(rot)-(points[pointNum].y-cC.y)*Math.sin(rot)));
		tmp.y = (int) Math.round(K.y + ((points[pointNum].x-cC.x)*Math.sin(rot)+(points[pointNum].y-cC.y)*Math.cos(rot)));
		return tmp;
	}
	public Vector convCentroid() {
		Vector tmp = C.clone();
		tmp.x = (int) Math.round(K.x + ((C.x-cC.x)*Math.cos(rot)-(C.y-cC.y)*Math.sin(rot)));
		tmp.y = (int) Math.round(K.y + ((C.x-cC.x)*Math.sin(rot)+(C.y-cC.y)*Math.cos(rot)));
		return tmp;
	}
	public void perfTickChanges() {
		V.add(A);
		K.add(V);
		rotV+=rotA;
		rot+=rotV;
	}
	public void changePerspective(Vector newCenter) {
		Vector[] tmpPoints = new Vector[points.length];
		for(int i = 0; i<points.length; i++) {
			tmpPoints[i] = convPoint(i);
		}
		Vector tmpCentroid = convCentroid();
		C = tmpCentroid;
		for(int i = 0; i<points.length; i++) {
			points[i] = tmpPoints[i];
		}
		cC = newCenter.clone();
		K = newCenter.clone();
		if(points.length>1) {
			Vector ref = new Vector(0,0);
			for(int i = 0; i<points.length; i++) {
				if(points[i].x != newCenter.x || points[i].y != newCenter.y) {
					ref = points[i];
					break;
				}
			}
			int dist = (int) Math.sqrt(Math.pow(ref.x-newCenter.x,2)+Math.pow(ref.y-newCenter.y, 2));
			rot = Math.asin((ref.x-newCenter.x)/dist);
		} 
	}
	public void modRot(double rad) {
		rot = rot+rad;
	}
	public void defLines() {
		lines = new Line[points.length];
		for(int i = 0; i<points.length; i++) {
			lines[i] = new Line(points[i], points[(i+1)%points.length], this);
		}
	}
	void calcMaxDist() {
		max = 0;
		for(int i = 0; i<points.length; i++) {
			if(max<C.dist(points[i])) {
				max = (int) C.dist(points[i]);
			}
		}
		System.out.println(max);
	}
	public boolean isCloseToLine(Line l) {
		double Adist = (Math.abs((l.p2.y-l.p1.y)*C.x-(l.p2.x-l.p1.x)*C.y+l.p2.x*
				l.p1.y-l.p2.y*l.p1.x)/Math.sqrt(Math.pow(l.p2.y-l.p1.y,2)+Math.pow(l.p2.x-l.p1.x, 2)));
		if(Adist<(max-V.dist)) {
			return true;
		} else {
			return false;
		}
	}
}
