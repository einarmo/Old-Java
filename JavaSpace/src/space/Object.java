package space;
import java.awt.*;
public class Object {
	
	private double G;
	public int par, num;
	public Vector P, V;
	public double mass;
	public Color c;
	public boolean fix, remove;
	
	
	Object(int num, Vector P, Vector V, double mass, Color c, int par) {
		this.num = num;
		this.P = P;
		this.par = par;
		this.V = V;
		this.mass = mass;
		this.c = c;
		this.G = Space.G;
		this.fix = true;
		this.remove = false;
	}
	
	public void calc(Object[] OB) {
		Vector A = new Vector(0.0,0.0);
		for (int i = 0; i < OB.length; i++) {
			if (i != num) {
				double[] dA = OB[i].returnVal();
				double radius = Math.sqrt(Math.pow(dA[0]-P.x,2)+(Math.pow(dA[1]-P.y,2)));
				
				double accelraw = ((G*dA[4])/(radius*radius));
				double moddif = accelraw/radius;
				A.addVal((dA[0]-P.x)*moddif, (dA[1]-P.y)*moddif);
				//System.out.println(moddif);
			}
		}
		V.add(A);

	}
	public double[] returnVal() {
		double[] dA = {P.x,P.y, V.x, V.y, mass};
		return dA;
	}
	public void trav() {
		P.add(V);
	}
	public void notfixed() {
		fix = false;
	}
	public void modspd(double spdx, double spdy) {
		V.addVal(spdx, spdy);
	}
	public void printInfo() {
		System.out.println("X: " + P.x + " Y: " + P.y + " Xv: " + V.x + " YV: " + V.y + " M: "+ mass + 
				" PARENT: " + par + " num: " + num);
	}
	public void calcOrbit(Object OB[]) {
		if(par != 0 && !SpaceRun.calc[num])  {
			if(SpaceRun.calc[par-1]) {
				double[] dA = OB[par-1].returnVal();
				double xa = dA[0]-P.x;
				double ya = dA[1]-P.y;
				double dist = Math.sqrt(Math.pow(xa,2)+Math.pow(ya,2));
				double modx = xa/dist;
				double mody = ya/dist;
				V.setVal(dA[2] + Math.sqrt((G*(mass+dA[4]))/dist)*-mody, dA[3] + Math.sqrt((G*(mass+dA[4]))/dist)*modx);
				SpaceRun.calc[num] = true;
				OB[par-1].notfixed();
			}
			else {
				OB[par-1].calcOrbit(OB);
				double[] dA = OB[par-1].returnVal();
				double xa = dA[0]-P.x;
				double ya = dA[1]-P.y;
				double dist = Math.sqrt(Math.pow(xa,2)+Math.pow(ya,2));
				double modx = xa/dist;
				double mody = ya/dist;
				V.setVal(dA[2] + Math.sqrt((G*(mass+dA[4]))/dist)*-mody, dA[3] + Math.sqrt((G*(mass+dA[4]))/dist)*modx);
				SpaceRun.calc[num] = true;
				OB[par-1].notfixed();

			}
		}
		else {
			SpaceRun.calc[num] = true;
		}
	}
	public void calcChildren(Object[] OB) {
		for (int i = 0; i<OB.length; i++) {
			if ((OB[i].par-1==num)&&!fix) {
				if (!OB[i].fix) {
					OB[i].calcChildren(OB);
					double[] dA = OB[i].returnVal();
					double xa = dA[0]-P.x;
					double ya = dA[1]-P.y;
					double relspdx = dA[2]-V.x;
					double relspdy = dA[3]-V.y;
					double dist = Math.sqrt(Math.pow(xa,2)+Math.pow(ya,2));
					double modx = xa/dist;
					double mody = ya/dist;
					V.addVal((dA[4]*relspdx)/mass*-mody,(dA[4]*relspdy)/mass*modx);
					OB[i].modspd((dA[4]*relspdx)/mass*-mody,(dA[4]*relspdy)/mass*modx);				
				}
				else {
					double[] dA = OB[i].returnVal();
					double xa = dA[0]-P.x;
					double ya = dA[1]-P.y;
					double relspdx = dA[2]-V.x;
					double relspdy = dA[3]-V.y;
					double dist = Math.sqrt(Math.pow(xa,2)+Math.pow(ya,2));
					double modx = xa/dist;
					double mody = ya/dist;
					V.addVal((dA[4]*relspdx)/mass*-mody,(dA[4]*relspdy)/mass*modx);
					OB[i].modspd((dA[4]*relspdx)/mass*-mody,(dA[4]*relspdy)/mass*modx);
				}
			}
		}
		fix = true;
	}
}