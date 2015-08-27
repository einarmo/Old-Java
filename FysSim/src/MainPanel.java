
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.border.*;

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
	public void createF() {
		CImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
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
		g.fillOval(x, y, r/2, r/2);
	}
	public void drawLine(int x1, int y1, int x2, int y2, Color c) {
		g.setColor(c);
		g.drawLine(x1, y1, x2, y2);
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
}