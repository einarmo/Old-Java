package space;

import java.awt.Toolkit;
import java.awt.Dimension;

public class SpaceRun {
	public static boolean zoom, zoomOut, left, right, up, down, changed, clear, incSpeed, decSpeed = false;
	public static int selItem = 0;
	static double rel, scrX, scrY;
	public static boolean[] calc;
	private int numticks;
	Object[] OB;
	MainPanel m;
	InfoPanel inf;
	private double maxMass, maxDist;
	SpaceRun(int ticks, Object[] OB) {
		rel = 1;
		scrX = 0;
		scrY = 0;
		this.OB = OB;
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
		
		this.m = new MainPanel(wi,he);
		
		m.createF();
		//setIdealZoom();
		this.inf = new InfoPanel(350,he,wi+5,0, OB);
		for(int i = 0; i<numticks; i++) {
			for(int k = 0; k < OB.length; k++) {
				OB[k].calc(OB);
			}
			for(int k = 0; k < OB.length; k++) {
				OB[k].trav();
			}
			if(i%Space.update == 0) {
				drawObj(i);
			}
			if(i%50000 == 0) {
				calcMinDist();
			}
		}
		
	}
	public void drawObj(int i) {
		m.createGraphics();
		perfChange();
		focus();
		if(i%10 == 0) {
			if(changed) {
				m.clear();
				changed = false;
			}
		}
		if(i%2000 == 0) {
			inf.setInfoString(OB);
		}
		for(int j = 0; j < OB.length; j++) {
			m.plot(convX(OB[j].P.x), convY(OB[j].P.y), OB[j].c);
		}
		m.flush();
		
	}
	public void perfChange() {
		if(zoom) {
			rel = rel-rel/300*(((double)Space.update)/3);
			changed = true;
		}
		if(zoomOut) {
			rel = rel+rel/300*(((double)Space.update)/3);
			changed = true;
		}
		if(left) {
			scrX = scrX + 0.3*rel*(((double)Space.update)/3);
			changed = true;
		}
		if(right) {
			scrX = scrX - 0.3*rel*(((double)Space.update)/3);
			changed = true;
		}
		if(up) {
			scrY = scrY + 0.3*rel*(((double)Space.update)/3);
			changed = true;
		}
		if(down) {
			scrY = scrY - 0.3*rel* (((double)Space.update)/3);
			changed = true;
		}
		if(decSpeed) {
			Space.calcmod -= Space.calcmod*0.00001;
		}
		if(incSpeed) {
			Space.calcmod += Space.calcmod*0.00001;
		}
		if(clear) {
			changed = true;
		}
	}
	public void focus() {
		if(selItem != 0) {
			scrX = -OB[selItem-1].P.x;
			scrY = -OB[selItem-1].P.y;
		}
	}
	public int convX(double x) {
		double mX = (x+scrX)/rel + m.getSize().width/2;
		int retX = (int) mX;
		return retX;
	}
	public int convY(double y) {
		double mY = (y+scrY)/rel + m.getSize().height/2;
		int retY = (int) mY;
		return retY;
	}
	public void removeObject(int index) {
		Object[] tmpOB = new Object[OB.length-1];
		int v = 0;
		for(int i = 0; i<OB.length; i++) {
			if(i!=index) {
				tmpOB[v] = OB[i];
				v++;
			}
		}
		inf.removeCBEntry(index);
		if(selItem > 0 && selItem>index) {
			selItem--;
		}
		this.OB = tmpOB.clone();
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
	public void getMaxMass() {
		maxMass = 0;
		for(int i = 0; i<OB.length; i++) {
			if(maxMass<OB[i].mass) {
				maxMass = OB[i].mass;
			}
		}
	}
	public void setIdealZoom() {
		int maxX = Integer.MAX_VALUE, maxY = Integer.MAX_VALUE;
		int minX = Integer.MIN_VALUE, minY = Integer.MIN_VALUE;
		for(int i = 0; i<OB.length; i++) {
			if (OB[i].P.x>minX) {
				minX = (int) OB[i].P.x;
			}
			if (OB[i].P.y>minY) {
				minY = (int) OB[i].P.y;
			}
			if (OB[i].P.x<maxX) {
				maxX = (int) OB[i].P.x;
			}
			if (OB[i].P.y<maxY) {
				maxY = (int) OB[i].P.y;
			}
		}
		double[] relL = new double[4];
		relL[0] = (maxY+10+scrY)/(m.getSize().height/2);
		relL[1] = (minY+10+scrY)/(-m.getSize().height/2);
		relL[2] = (maxX+10+scrX)/(m.getSize().width/2);
		relL[3] = (minX+10+scrX)/(-m.getSize().width/2);
		
		double newRel = Double.NEGATIVE_INFINITY;
		for(int i = 0; i<4; i++) {
			if (relL[i]>newRel) {
				newRel = relL[i];
			}
		}
		rel = newRel;
		
		SpaceRun.scrX = (minX + maxX)/2;
		SpaceRun.scrY = (minY + maxY)/2;
		System.out.println(scrX + " " + scrY);
	}
}
