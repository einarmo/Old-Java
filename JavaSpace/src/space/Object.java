package space;
import java.awt.*;
public class Object {
	
	private double G;
	public int par, num;
	public Vector P, V, A;
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
		this.A = new Vector(0.0, 0.0);
		this.G = Space.G;
		this.fix = true;
		this.remove = false;
	}
	
	public void calc(Object[] OB) {
		for (int i = num+1; i < OB.length; i++) {
			Vector dist = Vector.add(OB[i].P, Vector.mult(-1, P));
			Vector force = Vector.mult(OB[i].mass*mass*G*1/(dist.size()*dist.size()), Vector.mult(1/dist.size(), dist));
			OB[i].A.add(Vector.mult(-1/OB[i].mass, force), 1);
			A.add(Vector.mult(1/mass, force), 1);
		}
	}
	public double[] returnVal() {
		double[] dA = {P.x,P.y, V.x, V.y, mass};
		return dA;
	}
	public void trav() {
		V.add(A, Space.calcmod);
		P.add(V, Space.calcmod);
		A = new Vector(0.0, 0.0);
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
				V.addVal(dA[2] + Math.sqrt((G*(mass+dA[4]))/dist)*-mody, dA[3] + Math.sqrt((G*(mass+dA[4]))/dist)*modx);
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
				V.addVal(dA[2] + Math.sqrt((G*(mass+dA[4]))/dist)*-mody, dA[3] + Math.sqrt((G*(mass+dA[4]))/dist)*modx);
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