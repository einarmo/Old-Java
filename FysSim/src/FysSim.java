import java.awt.Color;
import javax.swing.SwingUtilities;
public class FysSim {
	MainPanel m;
	Polygon p;
	public static Line[] solids = {new Line(new Vector(0, 590), new Vector(800, 590))};
	public static void main(String[] args) {
		new FysSim();
	}
	
	public static double grav = 9.804;
	FysSim() {
		//Initiate Config
		//read from files
		//Start simulation
		Vector[] points = {new Vector(0, 0), new Vector(100, 0), new Vector(100, 400), new Vector(0, 400), new Vector(50, 200)};
		this.m = new MainPanel(800, 600);
		m.createF();
		this.p = new Polygon(points, 10, new Vector(400,400));
		p.cC = p.points[0];
		p.rotA = 0.0001;
		int time = 10;
		for(int j = 0; j<2000; j++) {
			p.perfTickChanges();
			if(j == 800) {
				p.changePerspective(p.convCentroid());
			}
			try {
				Thread.sleep(time);
			} catch(InterruptedException e) {
			}
			drawStuff();
		}


	}
	public void drawStuff() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				m.createGraphics();
				m.clear();
				for(int i = 0; i<p.points.length; i++) {
					m.drawLine(p.convPoint(i).x, p.convPoint(i).y, 
							p.convPoint((i+1) % p.points.length).x, p.convPoint((i+1) % p.points.length).y, Color.BLUE);
				}
				m.drawCircle(p.K.x, p.K.y, 5, Color.GREEN);
				m.drawCircle(p.convCentroid().x, p.convCentroid().y, 5, Color.RED);
				for(int i = 0; i<solids.length; i++) {
					m.drawLine(solids[i].p1.x, solids[i].p1.y, solids[i].p2.x, solids[i].p2.y, Color.WHITE);
				}
				p.perfTickChanges();
				m.flush();
			}
		});
	}
}
