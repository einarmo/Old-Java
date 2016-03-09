

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
	public static Vector3D angle = new Vector3D(1.0/2*Math.PI, 0.0, 0.0); //Pitch, yaw, roll
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
	SpaceRun(int ticks, Object[] OB) { //Initiate the main process
		SpaceRun.OB = OB;
		this.numticks = ticks;
		for(int i = 0; i<OB.length; i++) {
			OB[i].checkForChildren(OB);
		}
		for(int i = 0; i<OB.length; i++) {
			if(!OB[i].fix) {
				OB[i].calcChildren(OB);
			}
		}
		
		calc = new boolean[OB.length];
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
		TimerThread timer = new TimerThread();
		new Thread(timer).start();
		doSim();
	}
	public void doSim() { //Perform calculation tick

		for(int i = 0; i<numticks; i++) {
			for(int k = 0; k < OB.length; k++) {
				OB[k].calc(OB);
			}
			for(int k = 0; k < OB.length; k++) {
				OB[k].trav();
			}
			if(i%Space.update==0) {
				drawObj(i);
				perfChange();
			}
			
			if(i%50000 == 0) {
				calcMinDist();
			}
		}
	}
	public void drawObj(int i) { //Perform draw tick

		

		if(i%(200*Space.update) == 0) {
			inf.setInfoString(OB);
		}
		for(int j = 0; j < OB.length; j++) {
			int rad = 1;
			Vector3D d = Vector3D.dist(OB[j].P, cPos);
			Vector3D conv = conv(OB[j].P, d);
			int x = convX(conv);
			int y = convY(conv);
			if(conv.z<0&&x>0&&y>0&&x<m.drawW&&y<m.drawH) {
				m.plot(x, y, rad, OB[j].c);
			}
		}
		updateCamera();
		if(doDraw) {
			m.flush();
			change = false;
			
			m.createGraphics();
			if(achanged) {
				updateConstants();

			}
			perfChange();
			if(changed) {
				m.clear();
				changed = false;
			}
			doDraw = false;
		}
		
	}
	public void updateCamera() { //Update camera position to match the current angle of the camera and the focus
		double size = Math.sqrt(1+Math.sin(angle.x)*Math.sin(angle.x));
		cPos.z = zoomR*Math.cos(-angle.x)*Math.cos(angle.y)/size + focusPos.z;
		cPos.y = zoomR*Math.sin(-angle.x)/size + focusPos.y;
		cPos.x = zoomR*Math.cos(-angle.x)*Math.sin(angle.y)/size + focusPos.x;
	}
	public void updateConstants() { //Update the sine and cosine constants
		currentSin.x = Math.sin(angle.x);
		currentSin.y = Math.sin(angle.y);
		currentSin.z = Math.sin(angle.z);
		currentCos.x = Math.cos(angle.x);
		currentCos.y = Math.cos(angle.y);
		currentCos.z = Math.cos(angle.z);
	}
	public void perfChange() { //Perform changes to constants based on keypresses
		if(decSpeed) {
			Space.calcmod -= Space.calcmod*0.001;
		}
		if(incSpeed) {
			Space.calcmod += Space.calcmod*0.001;
		}
		if(clear) {
			changed = true;
		}
		if(!fMode) {
			if(zoom) {
				cPos.z+=1.0;
				changed = true;
			}
			if(zoomOut) {
				cPos.z-=1.0;
				changed = true;
			}
			if(left) {
				cPos.x+=1.0;
				changed = true;
			}
			if(right) {
				cPos.x-=1.0;
				changed = true;
			}
			if(up) {
				cPos.y+=1.0;
				changed = true;
			}
			if(down) {
				cPos.y-=1.0;
				changed = true;
			}

			if(yRight) {
				achanged = true;
				changed = true;
				angle.y+=0.001;
			}
			if(yLeft) {
				achanged = true;
				changed = true;
				angle.y-=0.001;
			}
			if(pUp) {
				achanged = true;
				changed = true;
				angle.x+=0.001;
			}
			if(pDown) {
				achanged = true;
				changed = true;
				angle.x-=0.001;
			}
			if(rRight) {
				achanged = true;
				changed = true;
				angle.z+=0.001;
			}
			if(rLeft) {
				achanged = true;
				changed = true;
				angle.z+=0.001;
			}
		} else {
			if (selItem == 0) {
				if(in) {
					focusPos.z+=1.0;
					changed = true;
				}
				if(out) {
					focusPos.z-=1.0;
					changed = true;
				}
				if(left) {
					focusPos.x+=1.0;
					changed = true;
				}
				if(right) {
					focusPos.x-=1.0;
					changed = true;
				}
				if(up) {
					focusPos.y+=1.0;
					changed = true;
				}
				if(down) {
					focusPos.y-=1.0;
					changed = true;
				}
				
			}
			if(zoom) {
				changed = true;
				zoomR -= 1.0;
			}
			if(zoomOut) {
				changed = true;
				zoomR += 1.0;
			}
			if(yRight) {
				achanged = true;
				changed = true;
				angle.y+=0.001;
			}
			if(yLeft) {
				achanged = true;
				changed = true;
				angle.y-=0.001;
			}
			if(pUp) {
				achanged = true;
				changed = true;
				angle.x+=0.001;
			}
			if(pDown) {
				achanged = true;
				changed = true;
				angle.x-=0.001;
			}
			//if(rRight) {
				//achanged = true;
				//changed = true;
				//angle.z+=0.01/Space.update;
			//}
			//if(rLeft) {
				//achanged = true;
				//changed = true;
				//angle.z-=0.01/Space.update;
			//}
			
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
	public void calcMinDist() { //Calculates the deletion distance
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
	public void lookAt(Vector3D p) { //Changes the camera to look at the specified object
		Vector3D dist = Vector3D.dist(p, cPos);
		angle.x = Math.atan2(dist.y,Math.sqrt(dist.z*dist.z+dist.x*dist.x));
		angle.y = -Math.atan2(dist.x, -dist.z);
		//angle.z = -Math.atan2( Math.cos(angle.x), Math.sin(angle.x) * Math.sin(angle.y) );
		achanged = true;
		//System.out.println(dist.z);
	}
	public void getMaxMass() { //Returns the highest mass.
		maxMass = 0;
		for(int i = 0; i<OB.length; i++) {
			if(maxMass<OB[i].mass) {
				maxMass = OB[i].mass;
			}
		}
	}
}
