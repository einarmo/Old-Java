package space;
import java.awt.*;
public class Object {
	
	private double G;
	public int par, num;
	public Vector P, V, A;
	public double mass, ec, size;
	public Color c;
	public boolean fix, remove;
	
	
	Object(int num, Vector P, Vector V, double mass, Color c, int par, double ec) {
		this.num = num;
		this.P = P;
		this.par = par;
		this.V = V;
		this.mass = mass;
		this.size = 1.12*mass;
		this.ec = ec;
		this.c = c;
		this.A = new Vector(0.0, 0.0);
		this.G = Space.G;
		this.fix = true;
		this.remove = false;
	}
	
	public void calc(Object[] OB) {
		for (int i = num+1; i < OB.length; i++) {
			Vector dist = Vector.add(OB[i].P, Vector.mult(-1, P));
			double size = dist.size();
			Vector force = Vector.mult(OB[i].mass*mass*G*1/(size*size), Vector.mult(1/size, dist));
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
			if(!SpaceRun.calc[par-1]){
				OB[par-1].calcOrbit(OB);
			}
			double u = Space.G*(OB[par-1].mass+mass);
			Vector dist = Vector.dist(OB[par-1].P, P);
			double a = dist.size()/(1-this.ec);
			Vector unit = Vector.mult(1/dist.size(), dist);
			Vector rotU = new Vector(-unit.y, unit.x);
			V.add(Vector.add(Vector.mult(Math.sqrt((1+this.ec)*u/((1-this.ec)*a)), rotU), OB[par-1].V), 1);
			
			//V.addVal(dA[2] + Math.sqrt((G*(mass+dA[4]))/dist)*-mody, dA[3] + Math.sqrt((G*(mass+dA[4]))/dist)*modx);
			
			
			
			
			SpaceRun.calc[num] = true;
			OB[par-1].notfixed();
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
					Vector moment = new Vector((OB[i].V.x-V.x)*OB[i].mass, (OB[i].V.y-V.y)*OB[i].mass);
					
					V.add(Vector.mult(-1/(2*mass), moment), 1);
					OB[i].V.add(Vector.mult(-1/(2*mass), moment), 1);
					
				}
				else {
					Vector moment = new Vector((OB[i].V.x-V.x)*OB[i].mass, (OB[i].V.y-V.y)*OB[i].mass);
					
					V.add(Vector.mult(-1/(2*mass), moment),1);
					OB[i].V.add(Vector.mult(-1/(2*mass), moment), 1);
					//OB[i].calcChildren(OB);
				}
			}
		}
		fix = true;
	}
}