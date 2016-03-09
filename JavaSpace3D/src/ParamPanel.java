import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class ParamPanel {
	GridBagConstraints c;
	JPanel mainP;
	JButton b1, b2;
	JRadioButton b3;
	String[] labelstrings = {"Eccentricity", "Semimajor Axis in AU", "Inclination", "Long. of AN", 
			"Arg. of Per.", "True Anomaly", "Earth masses", "Parent num", "Color in Hex"};
	JLabel[] textLabels = new JLabel[labelstrings.length];
	JTextField[] fields = new JTextField[textLabels.length];
	public int numEntry;
	RawFile file;
	EditPanel p;
	JFrame f;
	ParamPanel(int numEntry, RawFile file, EditPanel p) {
		mainP = new JPanel(new GridBagLayout());
		this.p = p;
		this.file = file;
		c = new GridBagConstraints();
		this.numEntry = numEntry;
		c.weighty = 0.0;
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.ipadx = 10;
		for(int i = 0; i<textLabels.length; i++) {
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTH;
			c.gridx = i;
			c.gridy = 2;
			fields[i] = new JTextField("0.0");
			PlainDocument doc = (PlainDocument)fields[i].getDocument();
			doc.setDocumentFilter(new DocFilter());
			mainP.add(fields[i], c);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.SOUTH;
			textLabels[i] = new JLabel(labelstrings[i]);
			c.gridy = 1;
			mainP.add(textLabels[i], c);
		}
		fields[1].setText("1.0");
		fields[6].setText("1.0");
		fields[7].setText("0");
		fields[textLabels.length-1].setText("#ffffff");
		b3 = new JRadioButton("AU");
		b3.setSelected(true);
		b3.addActionListener(new radioListener());
		b1 = new JButton("Add");
		b1.setActionCommand("add");
		b1.addActionListener(new ButtonControl());
		b2 = new JButton("Finish");
		b2.setActionCommand("finish");
		b2.addActionListener(new ButtonControl());
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 3;
		c.gridx = 0;
		mainP.add(b1, c);
		c.gridx = 2;
		mainP.add(b2, c);
		c.gridx = 4;
		mainP.add(b3, c);
		f = new JFrame("New Object from Parameter");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.add(mainP);
		f.pack();
		f.setVisible(true);
	}
	public void create() {
		String[] infoList = new String[fields.length];
		for(int i = 0; i<fields.length; i++) {
			infoList[i] = fields[i].getText();
		}
		double inc = Double.valueOf(infoList[2])/360.0*2*Math.PI;
		double LongAN = Double.valueOf(infoList[3])/360.0*2*Math.PI;
		double arP = Double.valueOf(infoList[4])/360.0*2*Math.PI;
		double mass = 0.003*Double.valueOf(infoList[6]);
		double trueAN = Double.valueOf(infoList[5])/360.0*2*Math.PI;
		
		int parentNum = Integer.valueOf(infoList[7]);
		String[] pInfo = file.returnInfo(parentNum);
		Vector3D pANNORM = new Vector3D(Math.sin(LongAN), 0, Math.cos(LongAN));
		Vector3D pC = new Vector3D(Math.cos(Double.valueOf(inc))/(Math.sqrt(1.0+
				Math.tan(Double.valueOf(LongAN))*Math.tan(Double.valueOf(LongAN)))), 
				Math.sin(Double.valueOf(inc)), -Math.tan(Double.valueOf(LongAN))*
				Math.cos(Double.valueOf(inc))/(Math.sqrt(1.0+
				Math.tan(Double.valueOf(LongAN))*Math.tan(Double.valueOf(LongAN)))));
		if(Math.cos(LongAN)<0) {
			pC = Vector3D.mult(-1.0, pC);
		}
		//double vTrue = arP/360.0*Math.PI*2+trueAN/360.0*Math.PI*2;
		Vector3D k = Vector3D.cross(pANNORM, pC);
		Vector3D perNORM = Vector3D.add(Vector3D.add(Vector3D.mult(Math.cos(arP), pANNORM), 
				Vector3D.mult(Math.sin(arP), Vector3D.cross(k, pANNORM))),
				Vector3D.mult((1-Math.cos(arP))*Vector3D.product(k, pANNORM), 
				k));
		Vector3D minNORM = Vector3D.cross(perNORM, Vector3D.cross(pC, pANNORM));
		double sMa;
		if(b3.isSelected()) {
			sMa = Double.valueOf(infoList[1])*1000;
		} else {
			sMa = Double.valueOf(infoList[1])*1000/(1.496*Math.pow(10, 8));
		}
		double smia = sMa*Math.sqrt(1.0-Double.valueOf(infoList[0])*Double.valueOf(infoList[0]));
		Vector3D trueRelPos = Vector3D.add(Vector3D.mult(sMa*Math.cos(Double.valueOf(trueAN)), perNORM),
				Vector3D.mult(smia*Math.sin(Double.valueOf(trueAN)), minNORM));
		Vector3D centPos = Vector3D.mult(-sMa*Double.valueOf(infoList[0]), perNORM);
		Vector3D fakeRelPos = Vector3D.add(centPos, trueRelPos);
		Vector3D velNORM = Vector3D.add(Vector3D.mult(-1.0*sMa*Math.sin(Double.valueOf(trueAN)), perNORM),
				Vector3D.mult(smia*Math.cos(Double.valueOf(trueAN)), minNORM)).normalize();
		double spd = Math.sqrt(Space.G*(Double.valueOf(pInfo[6])+mass)*(2/(fakeRelPos.size())-1/sMa));
		System.out.println(spd);
		System.out.println(sMa);
		System.out.println(smia);
		System.out.println(fakeRelPos.x + " " + fakeRelPos.y + " " + fakeRelPos.z);
		System.out.println(perNORM.x + " " + perNORM.y + " " + perNORM.z);
		System.out.println(trueAN + " " + Math.cos(trueAN));
		System.out.println(trueRelPos.x + " " + trueRelPos.y + " " + trueRelPos.z);
		System.out.println(centPos.x + " " + centPos.y + " " + centPos.z);
		System.out.println(pC.x + " " + pC.y + " " + pC.z);
		System.out.println(k.x + " " + k.y + " " + k.z);
		Vector3D vel =  Vector3D.mult(spd, velNORM);
		Color c = Color.decode(infoList[8]);
		file.addEntry();
		file.setEntry(numEntry, Double.toString(fakeRelPos.x+Double.valueOf(pInfo[0])) + " "
				+ Double.toString(fakeRelPos.y+Double.valueOf(pInfo[1])) + " "
				+ Double.toString(fakeRelPos.z+Double.valueOf(pInfo[2])) + " "
				+ Double.toString(vel.x+Double.valueOf(pInfo[3])) + " "
				+ Double.toString(vel.y+Double.valueOf(pInfo[4])) + " "
				+ Double.toString(vel.z+Double.valueOf(pInfo[5])) + " "
				+ Double.toString(mass) + " "
				+ Integer.toString(c.getRed()) + " "
				+ Integer.toString(c.getGreen()) + " "
				+ Integer.toString(c.getBlue()) + " "
				+ Integer.toString(parentNum+1) + " "
				+ "1");
		p.saved = false;
		p.setObjDisplay();
		numEntry++;
	}
	
	
	
	
	class ButtonControl implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals("add")) {
				create();
			} else if(command.equals("finish"))  {
				f.dispose();
			}			
		}
		
	}
	class radioListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton button = (JRadioButton) e.getSource();
			if(button.isSelected()) {
				textLabels[1].setText("Semimajor Axis in AU");
			} else {
				textLabels[1].setText("Semimajor Axis in km");
			}
			
		}
		
	}
	class DocFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);
			for(int i = 0; i<fields.length; i++) {
				if(fields[i].getDocument().equals(doc)) {
					if(i == 0) {
						if(isDouble(sb.toString())&&Double.valueOf(sb.toString())<1&&Double.valueOf(sb.toString())>=0) {
							super.insertString(fb, offset, string, attr);
						}
					} else if(i==7) {
						if(isInt(sb.toString())&&Integer.parseInt(sb.toString())<numEntry
								&&Integer.parseInt(sb.toString())>=0) {
							super.insertString(fb, offset, string, attr);
						}
					} else if(i<=6) {
						if(isDouble(sb.toString())) {
							super.insertString(fb, offset, string, attr);
						}
					} else if(i==8) {
						super.insertString(fb, offset, string, attr);
					}
				}
			}
		}
		private boolean isInt(String text) {
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		private boolean isDouble(String text) {
			try {
				Double.parseDouble(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		@Override
		public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) 
				throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, string);
			for(int i = 0; i<fields.length; i++) {
				if(fields[i].getDocument().equals(doc)) {
					if(i == 0) {
						if(isDouble(sb.toString())&&Double.valueOf(sb.toString())<1&&Double.valueOf(sb.toString())>=0) {
							super.replace(fb, offset, length, string, attr);
						}
					} else if(i==7) {
						if(isInt(sb.toString())&&Integer.parseInt(sb.toString())<numEntry
								&&Integer.parseInt(sb.toString())>=0) {
							super.replace(fb, offset, length, string, attr);
						}
					} else if(i<=6) {
						if(isDouble(sb.toString())) {
							super.replace(fb, offset, length, string, attr);
						}
					} else if(i==8) {
						super.replace(fb, offset, length, string, attr);
					}
				}
			}
		}
		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.delete(offset, offset + length);
			for(int i = 0; i<fields.length; i++) {
				if(fields[i].getDocument().equals(doc)) {
					if(i == 0) {
						if(isDouble(sb.toString())&&Double.valueOf(sb.toString())<1&&Double.valueOf(sb.toString())>=0) {
							super.remove(fb, offset, length);
						}
					} else if(i==7) {
						if(isInt(sb.toString())&&Integer.parseInt(sb.toString())<numEntry
								&&Integer.parseInt(sb.toString())>=0) {
							super.remove(fb, offset, length);
						}
					} else if(i<=6) {
						if(isDouble(sb.toString())) {
							super.remove(fb, offset, length);
						}
					} else if(i==8) {
						super.remove(fb, offset, length);
					}
				}
			}
			if(sb.length()==0) {
				super.remove(fb, offset, length);
			}
		}
	}
}
