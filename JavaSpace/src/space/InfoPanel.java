package space;
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
		double[] dB = OB[num].returnVal();
		String exp = ("<b>Object " + (num+1) + "</b><br><b>x:</b>" + Math.round(dB[0]));
		exp = (exp + "</b><br><b>y:</b>" + Math.round(dB[1]));
		exp = (exp + "</b><br><b>speed x:</b>" + Math.rint(dB[2]*1000)/1000);
		exp = (exp + "</b><br><b>speed y:</b>" + Math.rint(dB[3]*1000)/1000);
		exp = (exp + "</b><br><b>mass:</b>" + dB[4]);
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
		finTxt = (finTxt + Math.round(SpaceRun.scrX) +"<br>" + Math.round(SpaceRun.scrY) +
				"<br>" + Math.round(SpaceRun.rel) + "<br>" + Space.calcmod);
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
		
		}
	}
}
