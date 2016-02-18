

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

import javax.swing.border.*;

import java.awt.event.*;

public class MainPanel {
	
	private BufferedImage CImage;
	private JLabel imgLabel;
	private JPanel mainP;
	private JFrame f;
	private int h, w;
	public int drawH, drawW;
	int[][] drawn;
	Color bgC = Color.black;
	Graphics2D g;
	ArrayList<Point> dr = new ArrayList<Point>(); //List of all drawn points
	
	MainPanel(int w, int h) {
		this.h = h;
		this.w = w;
	}
	public void addListener() {
		keylist listener = new keylist();
		mainP.addKeyListener(listener);
		mainP.setFocusable(true);
	}
	public void createF() {
		CImage = new BufferedImage(w+200, h+200, BufferedImage.TYPE_INT_RGB);
		drawH = CImage.getHeight();
		drawW = CImage.getWidth();
		mainP = new JPanel();
		mainP.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.weighty = 1.0;
		c.weightx = 1.0;
		imgLabel = new JLabel(new ImageIcon(CImage));
		imgLabel.setBorder(new EmptyBorder(0,0,0,0));
		mainP.add(imgLabel, c);
		mainP.setLocation(0, 0);
		
		drawn = new int[CImage.getWidth()][CImage.getHeight()];
		
		addListener();
		createGraphics();
		clear();
		flush();
		f = new JFrame("Space");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(mainP);
		f.pack();
		f.setSize(new Dimension(w+6,h+28));
		f.setVisible(true);
	}
	public void clear() {
		g.setColor(bgC);
		//for(int i = 0; i<drawn.length; i++) {
		//	for(int j = 0; j<drawn[i].length; j++) {
		//		if(drawn[i][j]!=0) {
		//			if(drawn[i][j]!=0) {
		//				g.drawLine(i, j, i, j);
		//				drawn[i][j] = 0;
		//				SpaceRun.change = true;
		//			}
		//		}
		//	}
		//}
		for(int i = 0; i<dr.size(); i++) {
			int x = dr.get(i).x;
			int y = dr.get(i).y;
			drawn[x][y] = 0;
			g.drawLine(x, y, x, y);
			SpaceRun.change = true;
		}
		dr = new ArrayList<Point>();
	}
	public void plot(int x, int y, int r, Color c) {
		g.setColor(c);
		//System.out.println(x + " "+  y + " " + c);
		if(drawn[x][y] < r) {
			SpaceRun.change = true;
			g.drawLine(x, y, x, y);
			dr.add(new Point(x,y));
		}
		drawn[x][y] = r;
	}
	public void drawCircle(int x, int y, int r, Color c) { //Currently unused
		g.setColor(c);
		g.fillOval(x-r/2, y-r/2, r, r);
	}
	public void createGraphics() {
		g = this.CImage.createGraphics();
	}
	public void flush() {
		imgLabel.repaint();
		g.dispose();
	}
	public Dimension getSize() {
		return new Dimension(CImage.getWidth(), CImage.getHeight());
	}
	public class keylist implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key==KeyEvent.VK_PLUS) {
				SpaceRun.zoom = true;
			}
			if (key==KeyEvent.VK_MINUS) {
				SpaceRun.zoomOut = true;
			}
			if (key==KeyEvent.VK_LEFT) {
				SpaceRun.left = true;
			}
			if (key==KeyEvent.VK_RIGHT) {
				SpaceRun.right = true;
			}
			if (key==KeyEvent.VK_UP) {
				SpaceRun.up = true;
			}
			if (key==KeyEvent.VK_DOWN) {
				SpaceRun.down = true;
			}
			if (key==KeyEvent.VK_SPACE) {
				SpaceRun.clear = true;
			}
			if (key==KeyEvent.VK_I) {
				SpaceRun.incSpeed = true;
			}
			if (key==KeyEvent.VK_K) {
				SpaceRun.decSpeed = true;
			}
			if (key==KeyEvent.VK_D) {
				SpaceRun.yRight = true;
			}
			if (key==KeyEvent.VK_A) {
				SpaceRun.yLeft = true;
			}
			if (key==KeyEvent.VK_W) {
				SpaceRun.pUp = true;
			}
			if (key==KeyEvent.VK_S) {
				SpaceRun.pDown = true;
			}
			if (key==KeyEvent.VK_E) {
				SpaceRun.rRight = true;
			}
			if (key==KeyEvent.VK_Q) {
				SpaceRun.rLeft = true;
			}
			if (key==KeyEvent.VK_PAGE_UP) {
				SpaceRun.out = true;
			}
			if (key==KeyEvent.VK_PAGE_DOWN) {
				SpaceRun.in = true;
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			if (key==KeyEvent.VK_PLUS) {
				SpaceRun.zoom = false;
			}
			if (key==KeyEvent.VK_MINUS) {
				SpaceRun.zoomOut = false;
			}
			if (key==KeyEvent.VK_LEFT) {
				SpaceRun.left = false;

			}
			if (key==KeyEvent.VK_RIGHT) {
				SpaceRun.right = false;
			}
			if (key==KeyEvent.VK_UP) {
				SpaceRun.up = false;
			}
			if (key==KeyEvent.VK_DOWN) {
				SpaceRun.down = false;
			}
			if (key==KeyEvent.VK_SPACE) {
				SpaceRun.clear = false;
			}
			if (key==KeyEvent.VK_I) {
				SpaceRun.incSpeed = false;
			}
			if (key==KeyEvent.VK_K) {
				SpaceRun.decSpeed = false;
			}
			if (key==KeyEvent.VK_D) {
				SpaceRun.yRight = false;
			}
			if (key==KeyEvent.VK_A) {
				SpaceRun.yLeft = false;
			}
			if (key==KeyEvent.VK_W) {
				SpaceRun.pUp = false;
			}
			if (key==KeyEvent.VK_S) {
				SpaceRun.pDown = false;
			}
			if (key==KeyEvent.VK_E) {
				SpaceRun.rRight = false;
			}
			if (key==KeyEvent.VK_Q) {
				SpaceRun.rLeft = false;
			}
			if (key==KeyEvent.VK_PAGE_UP) {
				SpaceRun.out = false;
			}
			if (key==KeyEvent.VK_PAGE_DOWN) {
				SpaceRun.in = false;
			}
		}
	}
}
