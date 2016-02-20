
import java.awt.*;
public class Object {
	
	private double G;
	public int par, num;
	public Vector3D P, V, A, RelSpd;
	public double mass, size;
	public Color c;
	public boolean fix, remove;
	
	
	Object(int num, Vector3D P, Vector3D V, double mass, Color c, int par) {
		System.out.println(par);
		this.num = num;
		this.P = P;
		this.par = par;
		this.V = V;
		this.mass = mass;
		this.size = 1.12*mass;
		this.c = c;
		this.A = new Vector3D(0.0, 0.0, 0.0);
		this.G = Space.G;
		this.fix = true;
		this.remove = false;
	}
	
	public void calc(Object[] OB) {
		for (int i = num+1; i < OB.length; i++) {
			Vector3D dist = Vector3D.add(OB[i].P, Vector3D.mult(-1, P));
			double size = dist.size();
			Vector3D force = Vector3D.mult(OB[i].mass*mass*G*1/(size*size), Vector3D.mult(1/size, dist));
			OB[i].A.add(Vector3D.mult(-1/OB[i].mass, force), 1);
			A.add(Vector3D.mult(1/mass, force), 1);
		}
	}
	public double[] returnVal() {
		double[] dA = {P.x,P.y, V.x, V.y, mass};
		return dA;
	}
	public void trav() {
		V.add(A, Space.calcmod);
		P.add(V, Space.calcmod);
		A = new Vector3D(0.0, 0.0, 0.0);
	}
	public void checkForChildren(Object[] OB) {
		for(int i = 0; i<OB.length; i++) {
			if(OB[i].par == num) {
				fix = false;
			}
		}
	}
	public void calcChildren(Object[] OB) {
		double totalMass = 0;
		totalMass+=mass;
		Vector3D totalMomentum = new Vector3D(0, 0, 0);
		for(int i = 0; i<OB.length; i++) {
			if(OB[i].par-1 == num) {
				if(OB[i].fix == false) {
					OB[i].calcChildren(OB);
				}
				totalMass+=OB[i].mass;
				totalMomentum.add(Vector3D.dist(V, OB[i].V), OB[i].mass);
			}
		}
		Vector3D diffSpd = Vector3D.mult(1/totalMass, totalMomentum);
		for(int i = 0; i<OB.length; i++) {
			if(OB[i].par-1 == num) {
				OB[i].V.add(diffSpd, 1);
			}
		}
		V.add(diffSpd, 1);
		fix = true;
		System.out.println("CALC: " + num);
		System.out.println(diffSpd.x + " " + diffSpd.y + " " + diffSpd.z);
	}
	public void modspd(double spdx, double spdy, double spdz) {
		V.addVal(spdx, spdy, spdz);
	}
	public void printInfo() {
		System.out.println("X: " + P.x + " Y: " + P.y + " Xv: " + V.x + " YV: " + V.y + " M: "+ mass + 
				" PARENT: " + par + " num: " + num);
	}
}