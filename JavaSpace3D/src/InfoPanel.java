
import javax.swing.*;

import java.awt.event.*;

import javax.swing.border.*;

import java.awt.*;
public class InfoPanel {
	private JLabel tLab;
	private JScrollPane scroll;
	private JFrame f;
	private JPanel main;
	private GridBagConstraints c;
	private JComboBox<String> cbox;
	public int width, height, xpos,ypos;
	InfoPanel(int width, int height, int xpos, int ypos, Object[] OB) {
		this.width = width;
		this.height = height;
		this.xpos = xpos;
		this.ypos = ypos;

		this.tLab = new JLabel();
		tLab.setBorder(new EmptyBorder(0,0,0,0));
		tLab.setVerticalAlignment(JLabel.TOP);
		this.scroll = new JScrollPane(tLab);
		
		this.main = new JPanel();
		main.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.ipadx = 40;
		c.fill = GridBagConstraints.BOTH;
		main.add(scroll, c);
		
		setComboBox(OB);
		
		this.f = new JFrame("Info");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.add(main);
		f.setLocation(xpos+4, ypos);
		f.pack();
		f.setSize(new Dimension(width+6,height+28));
		f.setVisible(true);
		
		
	}
	public String generateInfoString(Object[] OB, int num) {
		String exp = ("<b>Object " + (num+1) + "</b><br><b>x:</b>" + Math.round(OB[num].P.x));
		exp = (exp + "</b><br><b>y:</b>" + Math.round(OB[num].P.y));
		exp = (exp + "</b><br><b>z:</b>" + Math.round(OB[num].P.z));
		exp = (exp + "</b><br><b>speed x:</b>" + Math.rint(OB[num].V.x*1000)/1000);
		exp = (exp + "</b><br><b>speed y:</b>" + Math.rint(OB[num].V.y*1000)/1000);
		exp = (exp + "</b><br><b>speed z:</b>" + Math.rint(OB[num].V.z*1000)/1000);
		exp = (exp + "</b><br><b>mass:</b>" + OB[num].mass);
		return exp;
	}
	public void setComboBox(Object[] OB) {
		String[] str = new String[OB.length+1];
		System.out.println(str.length);
		str[0] = "Object Focus";
		for(int v = 0; v<OB.length; v++) {
			str[v+1] = ("Object " + (Integer.toString(v+1)));
		}
		cbox = new JComboBox<String>(str);
		c.gridx = 1;
		c.ipadx = 0;
		c.weightx = 0.2;
		c.insets = new Insets(20, 0, 0, 0);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		cboxlist alist = new cboxlist();
		cbox.addActionListener(alist);
		main.add(cbox, c);



	}
	public void removeCBEntry(int index) {
		cbox.removeItemAt(index);
	}
	public void setInfoString(Object[] OB) {
		String finTxt = "";
		for(int v = 0; v<OB.length; v++) {
			finTxt = (finTxt + generateInfoString(OB,v) + "<br><br>");
		}
		finTxt = (finTxt + SpaceRun.angle.x + "<br>");
		finTxt = (finTxt + SpaceRun.angle.y + "<br>");
		finTxt = (finTxt + SpaceRun.angle.z + "<br>");
		finTxt = (finTxt + SpaceRun.cPos.x + "<br>");
		finTxt = (finTxt + SpaceRun.cPos.y + "<br>");
		finTxt = (finTxt + SpaceRun.cPos.z + "<br>");
		finTxt = (finTxt + Space.calcmod + "<br>");
		finTxt = ("<html>" + finTxt + "</html>");
		tLab.setText(finTxt);
	}
	
	public class cboxlist implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>)e.getSource();
			SpaceRun.changed = true;
			SpaceRun.selItem = cb.getSelectedIndex();
			if(cb.getSelectedIndex()>0) {
				SpaceRun.focusPos = SpaceRun.OB[cb.getSelectedIndex()-1].P;
			} else {
				SpaceRun.focusPos = SpaceRun.focusPos.clone();
			}
		}
	}
}
