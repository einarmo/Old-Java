import javax.swing.JFrame;

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.*;
public class MainPanel {
	public EntityField[][] bList;
	public Vector size, sel1, sel2;
	private JPanel main;
	private JScrollPane mscr;
	private JButton b1, b2, b3, b4;
	private JComboBox<String> Ocbox, Bcbox;
	public JLabel textL;
	public JTextField nameL;
	public int selO, selB;
	public boolean mousePressed = false;
	public boolean shiftPressed = false;
	public Vector mousePressC = new Vector(-1,-1);
	public Vector mouseC = new Vector(-1, -1);
	public static Branch mainBranch;
	CalcLongest cal;
	private GridBagConstraints c;
	MainPanel() {
		JFrame frame = new JFrame("Organisk Kjemi Navnsetter");
		size = new Vector(5,5);
		main = new JPanel(new GridBagLayout());
		GridBagConstraints c1 = new GridBagConstraints();
		c1.weightx = 1.0;
		c1.weighty = 1.0;
		c1.fill = GridBagConstraints.BOTH;
		mscr = new JScrollPane(main);
		this.bList = new EntityField[size.x][size.y];
		for(int i = 0; i<size.x; i++) {
			for (int j = 0; j<size.y; j++) {
				bList[i][j] = new EntityField(new Vector(i, j), this);
			}
		}
		for(int i = 0; i<size.x; i++) {
			for (int j = 0; j<size.y; j++) {
				c1.gridx = i;
				c1.gridy = j;
				main.add(bList[i][j].b, c1);
			}
		}
		sel2 = new Vector(-1,-1);
		sel1 = new Vector(-1,-1);
		frame.setLayout(new GridBagLayout());
		this.c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 6;
		c.weighty = 1.0;
		c.weightx = 1.0;
		frame.add(mscr, c);
		
		b1 = new JButton("add CH");
		b1.setToolTipText("Add a carbon atom with hydrogen atoms");
		b1.addActionListener(new buttonControl());
		b1.setActionCommand("addC");
		b2 = new JButton("add O");
		b2.addActionListener(new buttonControl());
		b2.setActionCommand("addO");
		b3 = new JButton("add binding");
		b3.addActionListener(new buttonControl());
		b3.setActionCommand("addB");
		b4 = new JButton("Remove");
		b4.addActionListener(new buttonControl());
		b4.setActionCommand("remove");

		Ocbox = new JComboBox<String>(OrgKjemi.others);
		Ocbox.addActionListener(new cboxListO());
		Bcbox = new JComboBox<String>(OrgKjemi.bindings);
		Bcbox.addActionListener(new cboxListB());
		textL = new JLabel("An Einar Omang production");
		nameL = new JTextField("metan");
		nameL.setEditable(false);
		JPanel menuP = new JPanel(new GridLayout());
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.gridwidth = 1;
		c.gridy = 1;
		menuP.add(b3);
		c.gridx = 1;
		menuP.add(b1);
		c.gridx = 2;
		menuP.add(Bcbox);
		c.gridx = 3;
		menuP.add(b2);
		c.ipadx = 0;
		c.gridx = 4;
		menuP.add(Ocbox);
		c.gridx = 5;
		menuP.add(b4);
		c.gridx = 0;
		c.gridwidth = 6;
		c.gridy = 1;
		frame.add(menuP, c);
		c.gridy = 2;
		bList[2][2].text = "CH";
		bList[2][2].suf = Integer.toString(OrgKjemi.slots[0]);
		bList[2][2].selNum = 0;
		frame.add(nameL, c);
		c.gridy = 3;
		frame.add(textL, c);
		c.gridwidth = 1;
		c.weighty = 1.0;
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		this.cal = new CalcLongest();
	}
	public void updateSelection1(Vector newSel) {
		if (!(sel1.x==-1)) {
			bList[sel1.x][sel1.y].b.clearSelection();
		}
		sel1.set(newSel);
	}
	public void updateSelection2(Vector newSel) {
		if (!(sel2.x==-1)) {
			bList[sel2.x][sel2.y].b.clearSelection();
		}
		sel2.set(newSel);
	}
	public void checkExpansion() {
		if (sel2.x == 0) {
			expand(0);
		}
		if (sel2.y == 0) {
			expand(1);
		}
		if (sel2.x == bList.length-1) {
			expand(2);
		} 
		if (sel2.y == bList[0].length-1) {
			expand(3);
		}
	}
	public void resetBG() {
		for(int i = 0; i<bList.length; i++) {
			for (int j = 0; j<bList[0].length; j++) {
				bList[i][j].bgc = OrgKjemi.bgc;
				bList[i][j].b.repaint();
				bList[i][j].bc = Color.black;
			}
		}
	}
	public void paintLongest() {
		resetBG();
		for(int i = 0; i<mainBranch.mainB.size(); i++) {
			mainBranch.mainB.get(i).bc = Color.blue;
			mainBranch.mainB.get(i).b.repaint();
		}
		mainBranch.mainB.get(0).bc = Color.red;
	}
	public void expand(int state) {
		EntityField[][] copy;
		if (state == 0 || state == 2) {
			size.x++;
			copy = new EntityField[size.x][size.y];
			if (state == 0) {
				for(int i = 0; i<size.y; i++) {
					copy[0][i] = new EntityField(new Vector(0, i), this);
				}
				for(int i = 0; i<size.x-1; i++) {
					for(int j = 0; j<size.y; j++) {
						copy[i+1][j] = bList[i][j];
						bList[i][j].pos.x++;
					}
				}
				sel1.x++;
				sel2.x++;
			} else if (state == 2) {
				for(int i = 0; i<size.y; i++) {
					copy[size.x-1][i] = new EntityField(new Vector(size.x-1, i), this);
				}
				for(int i = 0; i<size.x-1; i++) {
					for(int j = 0; j<size.y; j++) {
						copy[i][j] = bList[i][j];
					}
				}
			}
			bList = copy.clone();
			main.removeAll();
			for(int i = 0; i< size.x; i++) {
				for(int j = 0; j<size.y; j++) {
					c.gridx = i;
					c.gridy = j;
					main.add(bList[i][j].b, c);
				}
			}
			main.repaint();
			main.revalidate();
			mscr.repaint();
		} else if (state == 1 || state == 3) {
			size.y++;
			copy = new EntityField[size.x][size.y];
			if (state == 1) {
				for(int i = 0; i<size.x; i++) {
					copy[i][0] = new EntityField(new Vector(i, 0), this);
				}
				for(int i = 0; i<size.x; i++) {
					for(int j = 0; j<size.y-1; j++) {
						copy[i][j+1] = bList[i][j];
						bList[i][j].pos.y++;
					}
				}
				sel1.y++;
				sel2.y++;
			} else if (state == 3) {
				for(int i = 0; i<size.x; i++) {
					copy[i][size.y-1] = new EntityField(new Vector(i, size.y-1), this);
				}
				for(int i = 0; i<size.x; i++) {
					for(int j = 0; j<size.y-1; j++) {
						copy[i][j] = bList[i][j];
						
					}
				}
			}
			bList = copy;
			main.removeAll();
			for(int i = 0; i< size.x; i++) {
				for(int j = 0; j<size.y; j++) {
					c.gridx = i;
					c.gridy = j;
					main.add(bList[i][j].b, c);
				}
			}
			main.repaint();
			main.revalidate();
			mscr.repaint();
		}
	}
	public static String addTrivial(String name) {
		for(int i = 0; i<OrgKjemi.baseName.length; i++) {
			if(OrgKjemi.baseName[i].equals(name)) {
				name = name + " / " + OrgKjemi.commonName[i];
			}
		}
		return name;
	}
	public class buttonControl implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String com = e.getActionCommand();
			if (com.equals("addC")) {
				if(sel2.x != -1 && bList[sel1.x][sel1.y].selNum != -1) {
					bList[sel2.x][sel2.y].setSub("C");
					bList[sel2.x][sel2.y].b.repaint();
					checkExpansion();
					mainBranch = new Branch(cal.getMain(bList));
					nameL.setText(addTrivial(mainBranch.retName(false)));
					paintLongest();
				}
			} else if (com.equals("addO")) {
				if(sel2.x != -1 && bList[sel1.x][sel1.y].selNum != -1) {
					if(OrgKjemi.slots[selO+1]>1){
						bList[sel2.x][sel2.y].setSub(OrgKjemi.others[selO]);
					} else {
						bList[sel2.x][sel2.y].addItem();
					}
					checkExpansion();
					mainBranch = new Branch(cal.getMain(bList));
					nameL.setText(addTrivial(mainBranch.retName(false)));
					paintLongest();
				}
			} else if (com.equals("addB")) {
				
			} else if (com.equals("remove")) {
				if(sel1.x != -1) {
					bList[sel1.x][sel1.y].permRemove();
					mainBranch = new Branch(cal.getMain(bList));
					nameL.setText(addTrivial(mainBranch.retName(false)));
					paintLongest();
				}
			}
		}
	}
	public class cboxListO implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>)e.getSource();
			selO = cb.getSelectedIndex();
			b2.setText("add " + cb.getItemAt(selO));
		}
		
	}
	public class cboxListB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>)e.getSource();
			selB = cb.getSelectedIndex();
		}
	}
}
