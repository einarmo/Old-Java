import javax.swing.JButton;

import java.awt.*;
import java.awt.event.*;

public class FieldButton extends JButton implements MouseListener {
	private static final long serialVersionUID = 1L;
	public int selected;
	private Dimension size = new Dimension(80,40);
	private boolean mouseEntered;
	private EntityField p;
	
	
	public FieldButton(int selected, EntityField p) {
		super("");
		this.selected = selected;
		this.addMouseListener(this);
		this.p = p;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D antiAlias = (Graphics2D)g;
        antiAlias.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (selected==0) {
        	g.setColor(p.bgc);
        	g.fillRect(0, 0, getWidth(), getHeight());
        } else if (selected == 1) {
        	g.setColor(Color.decode("#CDD7F7"));
        	g.fillRect(0, 0, getWidth(), getHeight());
        } else if (selected == 2) {
        	g.setColor(Color.decode("#B8C1DE"));
        	g.fillRect(0, 0, getWidth(), getHeight());
        	
        }
        g.setColor(Color.black);
        g.setFont(getFont("TimesRoman", Font.PLAIN, this.getSize().height*5/8));
        int xp = g.getFontMetrics().stringWidth(p.text)/2;
        int yp = this.getSize().height-g.getFontMetrics().getHeight()/4;
        g.drawString(p.text, this.getSize().width/2-xp-this.getSize().width/20, yp-this.getSize().height/10);
        g.setFont(getFont("TimesRoman", Font.PLAIN, this.getSize().height/2));
        g.drawString(p.suf, this.getSize().width/2+xp-5, yp);
        g.setColor(p.bc);
        addLines(g);
	}
	public void clearSelection() {
		selected = 0;
		repaint();
	}
	public Font getFont(String name, int style, int height) {
		int size = height;
		Font tfont = new Font(name, style, size);
		int tmph = getFontMetrics(tfont).getHeight();
		
		Boolean up = true;
		if (tmph > size) {
			up = false;
		}
		while (true) {
			Font font = new Font(name, style, size);
			int testHeight = getFontMetrics(font).getHeight()/2;
			if (testHeight < height && up) {
				size++;
				up = true;
			} else if (testHeight > height && !up) {
				size--;
				up = false;
			} else {
				return font;
			}
		}
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
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		mouseEntered = true;
		p.m.mouseC.set(p.pos);
	}
	public void expandTo() {
		if(p.m.mousePressed && p.isAdjecent(p.m.mousePressC) && p.m.bList[p.m.mousePressC.x][p.m.mousePressC.y].selNum != -1) {
			p.m.updateSelection2(p.pos);
			p.m.updateSelection1(p.m.mousePressC);
			selected = 2;
			p.m.bList[p.m.mousePressC.x][p.m.mousePressC.y].b.selected = 1;
			p.m.bList[p.m.mousePressC.x][p.m.mousePressC.y].b.repaint();
			p.m.checkExpansion();
			p.setSub("C");
			MainPanel.mainBranch = new Branch(p.m.cal.getMain(p.m.bList));
			p.m.nameL.setText(MainPanel.addTrivial(MainPanel.mainBranch.retName(false)));
			p.m.paintLongest();
		}
		repaint();
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		mouseEntered = false;
		repaint();
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		p.m.mousePressed = true;
		p.m.mousePressC.set(p.pos);

	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(p.m.shiftPressed && mouseEntered) {
			p.permRemove();			
			MainPanel.mainBranch = new Branch(p.m.cal.getMain(p.m.bList));
			p.m.nameL.setText(MainPanel.addTrivial(MainPanel.mainBranch.retName(false)));
			p.m.paintLongest();
			repaint();
		} else if(mouseEntered) {
			if(arg0.getButton() == MouseEvent.BUTTON1) {
				if(selected==0) {
					p.m.updateSelection1(p.pos);
					selected = 1;
					p.m.updateSelection2(new Vector(-1, -1));
				} else if (selected == 1){
					p.m.sel1.x = -1;
					selected = 0;
					p.m.updateSelection2(new Vector(-1, -1));
				} else if (selected == 2){
					p.m.updateSelection1(p.pos);
					p.m.updateSelection2(new Vector(-1, -1));
					selected = 1;
				}
			
			} else if(arg0.getButton() == MouseEvent.BUTTON3) {
				if(selected == 0 && p.isAdjecent(p.m.sel1)) {
					p.m.updateSelection2(p.pos);
					selected = 2;
				} else if (selected == 2) {
					p.m.sel2.x = -1;
					selected = 0;
				}
			}
			this.repaint();
		} else {
			p.m.bList[p.m.mouseC.x][p.m.mouseC.y].b.expandTo();
		}
		p.m.mousePressed=false;
		p.m.mousePressC.x = -1;
	}
	public Graphics addLines(Graphics g) {
		//Adds lines to the graphics object based on which directions have connections.
		int width = this.getSize().width;
		int height = this.getSize().height;
		for(int i = 0; i<p.c.length; i++) {
			Polygon po;
			if(p.c[i] == 1) {
				if(i == 0) {
					int[] yc = {height*9/20, height*11/20, height*11/20, height*9/20};
					int[] xc = {0, 0, width*1/10, width*1/10};
					po = new Polygon(xc, yc, 4);
					g.fillPolygon(po);
				} else if (i == 1) {
					int[] yc = {-height*1/20, height*5/20, height*6/20, 0};
					int[] xc = {0, width*2/10, width*3/20, -width*1/20};
					po = new Polygon(xc, yc, 4);
					g.fillPolygon(po);
				} else if (i == 2) {
					int[] yc = {0, 0, height*2/10, height*2/10};
					int[] xc = {width*19/40, width*21/40, width*21/40, width*19/40};
					po = new Polygon(xc, yc, 4);
					g.fillPolygon(po);
				} else if (i == 3) {
					int[] yc = {-height*1/20, 0, height*6/20, height*5/20};
					int[] xc = {width, width*21/20, width*17/20, width*16/20};
					po = new Polygon(xc, yc, 4);
					g.fillPolygon(po);
				} else if (i == 4) {
					int[] yc = {height*9/20, height*11/20, height*11/20, height*9/20};
					int[] xc = {width, width, width*9/10, width*9/10};
					po = new Polygon(xc, yc, 4);
					g.fillPolygon(po);
				} else if (i == 5) {
					int[] yc = {height, height*21/20, height*15/20, height*14/20};
					int[] xc = {width*21/20, width, width*16/20, width*17/20};
					po = new Polygon(xc, yc, 4);
					g.fillPolygon(po);
				} else if (i == 6) {
					int[] yc = {height, height, height*8/10, height*8/10};
					int[] xc = {width*21/40, width*19/40, width*19/40, width*21/40};
					po = new Polygon(xc, yc, 4);
					g.fillPolygon(po);
				} else if (i == 7) {
					int[] yc = {height*21/20, height, height*14/20, height*15/20};
					int[] xc = {0, -width*1/20, width*3/20, width*2/10};
					po = new Polygon(xc, yc, 4);
					g.fillPolygon(po);
				}
			}
			if(p.c[i] == 2 || p.c[i] == 3) {
				if(i == 0) {
					g.drawLine(0, height*18/40, width/10, height*18/40);
					g.drawLine(0, height*22/40, width/10, height*22/40);
				} else if(i == 1) {
					g.drawLine(width/40, 0, width*8/40, height*10/40);
					g.drawLine(0, height/40, width*7/40, height*11/40);
				} else if(i == 2) {
					g.drawLine(width*19/40, 0, width*19/40, height*2/10);
					g.drawLine(width*21/40, 0, width*21/40, height*2/10);
				} else if(i == 3) {
					g.drawLine(width*39/40, 0, width*4/5, height*5/20);
					g.drawLine(width, height/40, width*33/40, height*11/40);
				} else if(i == 4) {
					g.drawLine(width, height*18/40, width*9/10, height*18/40);
					g.drawLine(width, height*22/40, width*9/10, height*22/40);
				} else if(i == 5) {
					g.drawLine(width*39/40, height, width*4/5, height*15/20);
					g.drawLine(width, height*39/40, width*33/40, height*29/40);
				} else if(i == 6) {
					g.drawLine(width*19/40, height, width*19/40, height*8/10);
					g.drawLine(width*21/40, height, width*21/40, height*8/10);
				} else if(i == 7) {
					g.drawLine(width/40, height, width/5, height*15/20);
					g.drawLine(0, height*39/40, width*7/40, height*29/40);
				}
			}
			if(p.c[i] == 3) {
				if(i == 0) {
					g.drawLine(0, height*1/2, width/10, height*1/2);
				} else if(i == 1) {
					g.drawLine(0, 0, width*15/80, height*21/80);
				} else if(i == 2) {
					g.drawLine(width/2, 0, width/2, height/5);
				} else if(i == 3) {
					g.drawLine(width, 0, width*65/80, height*21/80);
				} else if(i == 4) {
					g.drawLine(width, height*1/2, width*9/10, height*1/2);
				} else if(i == 5) {
					g.drawLine(width, height, width*65/80, height*59/80);
				} else if(i == 6) {
					g.drawLine(width/2, height, width/2, height*4/5);
				} else if(i == 7) {
					g.drawLine(0, height, width*15/80, height*59/80);
				}
			}
		}
		return g;
	}
}
