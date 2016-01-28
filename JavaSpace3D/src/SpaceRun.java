

import java.awt.Toolkit;
import java.awt.Dimension;

public class SpaceRun {
	public static boolean in, out, zoom, zoomOut, left, right, up, down, changed, clear, incSpeed, decSpeed
	, pUp, pDown, yRight, yLeft, rRight, rLeft = false;
	public static int selItem = 0;
	public static boolean fMode = true;
	public boolean achanged = false;
	public static boolean sizeC = false;
	public static boolean[] calc;
	public static boolean change, doDraw;
	//Vector3D angle = new Vector3D(Math.PI/2, 0.0, 0.0); 
	public static Vector3D angle = new Vector3D(0.0, 0.0, 0.0); //Pitch, yaw, roll
	//Vector3D cPos = new Vector3D(0, 1000, 0);
	public static Vector3D cPos = new Vector3D(0, 0, 1000);
	public static Vector3D focusPos = new Vector3D(0, 0, 0);
	Vector3D uPos;
	Vector3D relC = new Vector3D(0.0, 0.0, 1000);
	public double zoomR = 1000;
	private int numticks;
	public static Object[] OB;
	MainPanel m;
	InfoPanel inf;
	private double maxMass, maxDist;
	Vector3D currentSin = new Vector3D(0, 0, 0); //Current sine value of the camera, to save processing power
	Vector3D currentCos = new Vector3D(0, 0, 0); 
	SpaceRun(int ticks, Object[] OB) {
		SpaceRun.OB = OB;
		this.numticks = ticks;
		
		calc = new boolean[OB.length];
		if(Space.calcorb){
			for(int h = 0; h<OB.length; h++) {
				OB[h].calcOrbit(OB);
			}
			for(int h = 0; h<OB.length; h++) {
				OB[h].calcChildren(OB);
			}
		}
		getMaxMass();
		maxDist = maxMass*10000;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int wi = (int) screenSize.getWidth()-360;
		int he = (int) screenSize.getHeight()-70;
		
		this.m = new MainPanel(wi, he);
		m.createF();
		m.clear();
		//m.createF();
		//setIdealZoom();
		this.inf = new InfoPanel(350,he,wi+5,0, OB);
		uPos = new Vector3D(-(wi+200)/2, -(he+200)/2, 500);
		updateConstants();
		doSim();
	}
	public void doSim() {
		TimerThread timer = new TimerThread();
		new Thread(timer).start();
		for(int i = 0; i<numticks; i++) {
			for(int k = 0; k < OB.length; k++) {
				OB[k].calc(OB);
			}
			for(int k = 0; k < OB.length; k++) {
				OB[k].trav();
			}
			if(i%Space.update==0) {
				drawObj(i);
			}
			perfChange();
			if(i%50000 == 0) {
				calcMinDist();
			}
		}
	}
	public void drawObj(int i) {
		m.createGraphics();
		updateCamera();
		if(i%100 == 0) {
			if(changed) {
				m.clear();
				
				changed = false;
				
			}
		}
		if(achanged) {
			if(fMode) {
				
			}
			updateConstants();
		}
		if(i%2000*Space.update == 0) {
			inf.setInfoString(OB);
		}
		for(int j = 0; j < OB.length; j++) {
			//int rad = (int)Math.ceil(OB[j].size/rel);
			int rad = 1;
			Vector3D d = Vector3D.dist(OB[j].P, cPos);
			Vector3D conv = conv(OB[j].P, d);
			int x = convX(conv);
			int y = convY(conv);
			//System.out.println(x + " " + y);
			//System.out.println(conv.x + " " + conv.y + " " + conv.z);
			//if(x > 0 && x<500 && y> 0 && y<500)
			if(conv.z<0&&x>0&&y>0&&x<m.drawW&&y<m.drawH) {
				m.plot(x, y, rad, OB[j].c);
			}
		}
		if(doDraw) {
			m.flush();
			change = false;
		}
		
	}
	public void updateCamera() {
		double size = Math.sqrt(1+Math.sin(angle.x)*Math.sin(angle.x));
		cPos.z = zoomR*Math.cos(-angle.x)*Math.cos(angle.y)/size + focusPos.z;
		cPos.y = zoomR*Math.sin(-angle.x)/size + focusPos.y;
		cPos.x = zoomR*Math.cos(-angle.x)*Math.sin(angle.y)/size + focusPos.x;
	}
	public void updateConstants() {
		currentSin.x = Math.sin(angle.x);
		currentSin.y = Math.sin(angle.y);
		currentSin.z = Math.sin(angle.z);
		currentCos.x = Math.cos(angle.x);
		currentCos.y = Math.cos(angle.y);
		currentCos.z = Math.cos(angle.z);
	}
	public void perfChange() {
		if(decSpeed) {
			Space.calcmod -= Space.calcmod*0.00001/Space.update;
		}
		if(incSpeed) {
			Space.calcmod += Space.calcmod*0.00001/Space.update;
		}
		if(clear) {
			changed = true;
		}
		if(!fMode) {
			if(zoom) {
				cPos.z+=1.0/Space.update;
				changed = true;
			}
			if(zoomOut) {
				cPos.z-=1.0/Space.update;
				changed = true;
			}
			if(left) {
				cPos.x+=1.0/Space.update;
				changed = true;
			}
			if(right) {
				cPos.x-=1.0/Space.update;
				changed = true;
			}
			if(up) {
				cPos.y+=1.0/Space.update;
				changed = true;
			}
			if(down) {
				cPos.y-=1.0/Space.update;
				changed = true;
			}

			if(yRight) {
				achanged = true;
				changed = true;
				angle.y+=0.001/Space.update;
			}
			if(yLeft) {
				achanged = true;
				changed = true;
				angle.y-=0.001/Space.update;
			}
			if(pUp) {
				achanged = true;
				changed = true;
				angle.x+=0.001/Space.update;
			}
			if(pDown) {
				achanged = true;
				changed = true;
				angle.x-=0.001/Space.update;
			}
			if(rRight) {
				achanged = true;
				changed = true;
				angle.z+=0.001/Space.update;
			}
			if(rLeft) {
				achanged = true;
				changed = true;
				angle.z+=0.001/Space.update;
			}
		} else {
			if (selItem == 0) {
				if(in) {
					focusPos.z+=1.0/Space.update;
					changed = true;
				}
				if(out) {
					focusPos.z-=1.0/Space.update;
					changed = true;
				}
				if(left) {
					focusPos.x+=1.0/Space.update;
					changed = true;
				}
				if(right) {
					focusPos.x-=1.0/Space.update;
					changed = true;
				}
				if(up) {
					focusPos.y+=1.0/Space.update;
					changed = true;
				}
				if(down) {
					focusPos.y-=1.0/Space.update;
					changed = true;
				}
				
			}
			if(zoom) {
				changed = true;
				zoomR -= 1.0/Space.update;
			}
			if(zoomOut) {
				changed = true;
				zoomR += 1.0/Space.update;
			}
			if(yRight) {
				achanged = true;
				changed = true;
				angle.y+=0.001/Space.update;
			}
			if(yLeft) {
				achanged = true;
				changed = true;
				angle.y-=0.001/Space.update;
			}
			if(pUp) {
				achanged = true;
				changed = true;
				angle.x+=0.001/Space.update;
			}
			if(pDown) {
				achanged = true;
				changed = true;
				angle.x-=0.001/Space.update;
			}
			if(rRight) {
				achanged = true;
				changed = true;
				angle.z+=0.001/Space.update;
			}
			if(rLeft) {
				achanged = true;
				changed = true;
				angle.z-=0.001/Space.update;
			}
			
		}
	}
	public void focus() {
		if(fMode) {
			//lookAt(focusPos);
		}
	}
	public Vector3D conv(Vector3D point, Vector3D d) {
		Vector3D fin = new Vector3D(currentCos.y*(currentSin.z*d.y + currentCos.z*d.x)-currentSin.y*d.z,
				currentSin.x*(currentCos.y*d.z + currentSin.y*(currentSin.z*d.y+currentCos.z*d.x))
				+currentCos.x*(currentCos.z*d.y-currentSin.z*d.x),
				currentCos.x*(currentCos.y*d.z + currentSin.y*(currentSin.z*d.y+currentCos.z*d.x))
				-currentSin.x*(currentCos.z*d.y-currentSin.z*d.x));
		return fin;
	}
	public int convX(Vector3D conv) { //Makes the final conversion from relative 3D 
		//coordinates to a 2D projection
		return (int) (uPos.z/conv.z*conv.x - uPos.x);
	}
	public int convY(Vector3D conv) {
		//System.out.println("CY " + uPos.z + " " + conv.z + " " 
		//+ conv.y + " " + uPos.y+ " " + uPos.z/conv.z*conv.y);
		return (int) (uPos.z/conv.z*conv.y - uPos.y);
	}
	public void removeObject(int index) { //Removes object cleanly (ish)
		Object[] tmpOB = new Object[OB.length-1];
		int v = 0;
		for(int i = 0; i<OB.length; i++) {
			if(i!=index) {
				tmpOB[v] = OB[i];
				v++;
			}
		}
		//inf.removeCBEntry(index);
		if(selItem > 0 && selItem>index) {
			selItem--;
		}
		SpaceRun.OB = tmpOB.clone();
	}
	public void calcMinDist() {
		for(int i = 0; i<OB.length; i++) {
			double mindist = Double.POSITIVE_INFINITY;
			for (int j = 0; j<OB.length; j++) {
				if(i != j) {
					double dist = Math.sqrt(Math.pow(OB[i].P.x-OB[j].P.x, 2) + 
							(Math.pow(OB[j].P.y-OB[i].P.y, 2)));
					if (dist<mindist) {
						mindist = dist;
					}
				}
			}
			if (mindist > maxDist) {
				System.out.println(mindist + " " + maxDist + " " + i + " " + OB[i].P.x);
				OB[i].remove = true;
			}
		}
		int sub = 0;
		for(int i = 0; i<OB.length; i++) {
			if(OB[i-sub].remove) {
				sub++;
				System.out.println("REMOVED: " + i);
				removeObject(i);
			}
		}
	}
	public void lookAt(Vector3D p) {
		Vector3D dist = Vector3D.dist(p, cPos);
		angle.x = Math.atan2(dist.y,Math.sqrt(dist.z*dist.z+dist.x*dist.x));
		angle.y = -Math.atan2(dist.x, -dist.z);
		//angle.z = -Math.atan2( Math.cos(angle.x), Math.sin(angle.x) * Math.sin(angle.y) );
		achanged = true;
		//System.out.println(dist.z);
	}
	public void getMaxMass() {
		maxMass = 0;
		for(int i = 0; i<OB.length; i++) {
			if(maxMass<OB[i].mass) {
				maxMass = OB[i].mass;
			}
		}
	}
}
