import javax.swing.JButton;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class TileButton extends JButton implements MouseListener{
	private static final long serialVersionUID = 1L;
	public boolean selected;
	private Dimension size = new Dimension(MainGraphics.tileSize,MainGraphics.tileSize);
	private boolean mouseEntered;
	private Tile p;
	public BufferedImage rawImage = null;

	public TileButton(Tile p) {
		super("");
		this.addMouseListener(this);
		this.p = p;
		setOpaque(false);
	}
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		Graphics2D antiAlias = (Graphics2D)g;
		antiAlias.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.gray);
		g.fillRect(0, 0, getWidth(), getHeight());
		//rawImage = p.g.graphics.get(p.terrain.graphicsNum);
		if(rawImage != null) {
			g.drawImage((Image) resizeImage(rawImage, getWidth(), getHeight()), 0, 0, null);
		}
	
	}
	public void setImage(BufferedImage rawImage) {
		this.rawImage = rawImage;
	}
	public void clearSelection() {
		selected = false;
		repaint();
	}
	public void paintBorder(Graphics g) {
	}
	public Dimension getPreferredSize()
	{
		return size;
	}
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		selected = true;
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		mouseEntered = true;
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		mouseEntered = false;
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		if (mouseEntered) {
			selected = true;
			repaint();
		}
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	public static BufferedImage resizeImage(Image originalImage, int height, int width) {
		int imageType = BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaled = new BufferedImage(width, height, imageType);
		Graphics2D g = scaled.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.blue);
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
		return scaled;
	}
}
