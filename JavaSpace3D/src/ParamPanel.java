import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	String[] labelstrings = {"Eccentricity", "Semimajor Axis", "Inclination", "Long. of AN", "Arg. of Per.", "True Anomaly", 
			"Solar masses", "Parent num", "Color in Hex"};
	JLabel[] textLabels = new JLabel[labelstrings.length];
	JTextField[] fields = new JTextField[textLabels.length];
	public int numEntry;
	RawFile file;
	ParamPanel(int numEntry, RawFile file) {
		mainP = new JPanel(new GridBagLayout());
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
		
		JButton b1 = new JButton("Add");
		b1.setActionCommand("add");
		JButton b2 = new JButton("Finish");
		b2.setActionCommand("finish");
		JFrame f = new JFrame("New Object from Parameter");
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
		int parentNum = Integer.valueOf(infoList[6]);
		String[] pInfo = file.returnInfo(parentNum);
		Vector3D pANNORM = new Vector3D(Math.sin(Double.valueOf(pInfo[3])), 0, Math.cos(Double.valueOf(pInfo[3])));
		Vector3D pC = new Vector3D(Math.cos(Double.valueOf(infoList[2]))/(Math.sqrt(1.0+
				Math.tan(Double.valueOf(infoList[3]))*Math.tan(Double.valueOf(infoList[3])))), 
				Math.sin(Double.valueOf(infoList[2])), Math.tan(Double.valueOf(infoList[3]))*
				Math.cos(Double.valueOf(infoList[2]))/(Math.sqrt(1.0+
				Math.tan(Double.valueOf(infoList[3]))*Math.tan(Double.valueOf(infoList[3])))));
		double argPer = Double.valueOf(infoList[4]);
		double vTrue = Double.valueOf(infoList[4])+Double.valueOf(infoList[5]);
		Vector3D posNORM = Vector3D.add(Vector3D.add(Vector3D.mult(Math.cos(vTrue), pANNORM), 
				Vector3D.mult(Math.sin(vTrue), Vector3D.cross(Vector3D.cross(pC, pANNORM), pANNORM))), 
				Vector3D.mult((1-Math.cos(vTrue))*Vector3D.product(Vector3D.cross(pC, pANNORM), pANNORM), 
				Vector3D.cross(pC, pANNORM)));
		Vector3D perNORM = Vector3D.add(Vector3D.add(Vector3D.mult(Math.cos(argPer), pANNORM), 
				Vector3D.mult(Math.sin(argPer), Vector3D.cross(Vector3D.cross(pC, pANNORM), pANNORM))), 
				Vector3D.mult((1-Math.cos(argPer))*Vector3D.product(Vector3D.cross(pC, pANNORM), pANNORM), 
				Vector3D.cross(pC, pANNORM)));
		Vector3D minNORM = Vector3D.cross(perNORM, Vector3D.cross(pC, pANNORM));
		double sMa = Double.valueOf(infoList[1]);
		double sma = sMa*Math.sqrt(1.0-Double.valueOf(infoList[0])*Double.valueOf(infoList[0]));
		Vector3D trueRelPos = Vector3D.add(Vector3D.mult(sMa*Math.cos(Double.valueOf(infoList[5])), perNORM),
				Vector3D.mult(sma*Math.sin(Double.valueOf(infoList[5])), minNORM));
		Vector3D centPos = Vector3D.mult(sMa*-Double.valueOf(infoList[0]), perNORM);
		Vector3D fakeRelPos = Vector3D.add(trueRelPos, centPos);
	}
	
	
	
	
	class ButtonControl implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals("add")) {
				
			} else if(command.equals("finish"))  {
				
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
					} else if(i<=5) {
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
					} else if(i<=5) {
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
					} else if(i<=5) {
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
