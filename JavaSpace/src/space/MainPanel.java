package space;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;

import javax.swing.border.*;

import java.awt.event.*;

public class MainPanel {
	
	private BufferedImage CImage;
	private JLabel imgLabel;
	private JPanel mainP;
	private JFrame f;
	private int h, w;
	Color bgC = Color.black;
	Graphics2D g;
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
		CImage = new BufferedImage(2*w, 2*h, BufferedImage.TYPE_INT_RGB);
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
		g.fillRect(0, 0, CImage.getWidth(), CImage.getHeight());
	}
	public void plot(int x, int y, Color c) {
		g.setColor(c);
		g.drawLine(x, y, x, y);
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
		}
	}
}
