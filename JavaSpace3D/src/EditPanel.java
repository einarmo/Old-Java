
import javax.swing.*;

import java.awt.*;
import javax.swing.border.*;

import java.io.*;
import java.util.Scanner;

import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.event.*;

public class EditPanel {
	JList<String> objlist, filelist;
	int selnum;
	DefaultListModel<String> listM, listF;
	RawFile[] rawfiles;
	int objselection, fileselection;
	JTextField[] fields;
	JButton b1,b2,b3,b4,b5,b6;
	boolean saved;
	JFrame frame;
	Space s;
	JTextField nameField;
	JRadioButton calcB;
	public static Object[] objects;
	EditPanel t;
	
	//Primary class used for editing objects as well as adding new objects to setup.
	EditPanel(int width, int height, Space s) {
		saved = true;
		t = this;
		//Creates the various lists and panels necessary for the display of objects and files.
		genRawFiles(Space.objPath);
		setObjDisplay();
		this.objselection = -1;
		listM = new DefaultListModel<String>();
		this.objlist = new JList<String>(listM);
		objlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		objlist.setLayoutOrientation(JList.VERTICAL);
		objlist.setVisibleRowCount(-1);
		ListSelectionModel objlistselmod = objlist.getSelectionModel();
		objlistselmod.addListSelectionListener(new selobjlist());

		JScrollPane mainScroll = new JScrollPane(objlist);
		mainScroll.setBorder(new EmptyBorder(0,0,0,0));
		mainScroll.setPreferredSize(new Dimension(width, height/2));

		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridwidth = 12;
		pane.add(mainScroll, c);

		listF = new DefaultListModel<String>();
		setFileDisplay();
		this.filelist = new JList<String>(listF);
		filelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filelist.setLayoutOrientation(JList.VERTICAL);
		filelist.setVisibleRowCount(-1);
		ListSelectionModel filelistselmod = filelist.getSelectionModel();
		filelistselmod.addListSelectionListener(new selfilelist());

		JScrollPane fileScroll = new JScrollPane(filelist);
		fileScroll.setBorder(new EmptyBorder(0,0,0,0));
		fileScroll.setPreferredSize(new Dimension(200, height/2));
		c.gridx = 12;
		c.gridwidth = 2;
		pane.add(fileScroll, c);

		//Initiates and sets up all the buttons
		b1 = new JButton("Toggle on");
		b1.setToolTipText("Toggle selected object on or off");
		b1.setActionCommand("toggle");
		b1.addActionListener(new buttonControl());
		b2 = new JButton("Save Changes");
		b2.setToolTipText("Save all changes to file");
		b2.setActionCommand("save");
		b2.addActionListener(new buttonControl());
		b3 = new JButton("Delete");
		b3.setToolTipText("Delete selected object");
		b3.setActionCommand("delete");
		b3.addActionListener(new buttonControl());
		b4 = new JButton("Launch");
		b4.setToolTipText("Launch the program using the last launch profile");
		b4.setActionCommand("launch");
		b4.addActionListener(new buttonControl());
		b5 = new JButton("Edit Launch");
		b5.setToolTipText("Change launch profile, or make a new one");
		b5.setActionCommand("elaunch");
		b5.addActionListener(new buttonControl());
		b6 = new JButton("Create from parameters");
		b6.setToolTipText("Create new object from orbital parameters, opens a seperate menu");
		b6.setActionCommand("nParam");
		b6.addActionListener(new buttonControl());
		calcB = new JRadioButton("Draw size");

		//Creates the text fields and associated labels and adds them to the GUI.
		String[] labelstrings = {"x-pos", "y-pos", "z-pos", "x-vel", "y-vel", "z-vel", "mass", "red", "green", "blue", "parent"};
		fields = new JTextField[labelstrings.length];
		JLabel[] label = new JLabel[labelstrings.length];
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.0;
		for(int i = 0; i<labelstrings.length; i++) {
			fields[i] = new JTextField(4);
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridy = 2;
			c.gridx = i;
			fields[i].getDocument().addDocumentListener(new fieldList());
			PlainDocument doc = (PlainDocument) fields[i].getDocument();
			doc.setDocumentFilter(new DocFilter());
			pane.add(fields[i], c);

			label[i] = new JLabel(labelstrings[i]);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.SOUTH;
			c.gridy = 1;
			pane.add(label[i], c);
		}
		
		for(int k = 0; k<fields.length; k++) {
			if(k<5) {
				fields[k].setText("0.0");
			} else {
				fields[k].setText("0");
			}
		}
		
		//Adds all the buttons as well as the field for adding names.
		nameField = new JTextField(4);
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.gridy = 3;
		
		c.gridx = 0;
		pane.add(calcB, c);
		c.gridx = 1;
		pane.add(b3, c);

		c.gridx = 3;
		pane.add(b1, c);

		c.gridx = 5;
		pane.add(b2, c);

		c.gridx = 7;
		pane.add(b3, c);

		c.gridwidth = 1;
		c.gridx = 9;
		pane.add(b4, c);

		c.gridx = 10;
		pane.add(b5, c);

		c.gridy = 2;
		c.gridx = 12;
		pane.add(nameField, c);
		
		c.gridy = 3;
		pane.add(b6, c);
		JLabel nameLabel = new JLabel("Filename");
		c.gridy = 1;
		pane.add(nameLabel, c);

		//Initiates the main frame
		frame = new JFrame("Edit");
		frame.add(pane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		//Waits for a call to end before closing the frame and launching the main program.
		synchronized(frame) {
			try {
				frame.wait();

			} catch (InterruptedException e) {
			}
		}
		SpaceRun.sizeC = calcB.isSelected();
		frame.dispose();
		s.launch(objects);
	}
	public String createString(int num,int dispnum, RawFile file) { //Creates the filelist display string from rawfile info
		String finstr = "";
		String[] info = file.returnInfo(num);
		finstr = (finstr + "Object_" + (dispnum+1));
		finstr = (finstr + ": x: " + info[0]);
		finstr = (finstr + ", y: " + info[1]);
		finstr = (finstr + ", z: " + info[2]);
		finstr = (finstr + ", xv: " + info[3]);
		finstr = (finstr + ", yv: " + info[4]);
		finstr = (finstr + ", zv: " + info[5]);
		finstr = (finstr + ", mass: " + info[6]);
		finstr = (finstr + ", colourRed: " + info[7]);
		finstr = (finstr + ", colourGreen: " + info[8]);
		finstr = (finstr + ", colourBlue: " + info[9]);
		if(info[10].equals("0")) {
			finstr = (finstr + ", parent: none");
		}
		else {
			finstr = (finstr + ", parent: " + info[10]);	
		}
		if(info[11].equals("0")) {
			finstr = ("<html><strike>" + finstr + "</strike></html>");
		}
		return finstr;
	}

	public void setFileDisplay() { //Resets the filedisplay and adds all files
		listF.clear();

		for (int i = 0; i<rawfiles.length; i++) {
			listF.addElement(rawfiles[i].name);
		}
		listF.addElement("New entry...");
	}
	public void handleRemove(int removed) { //Handles removal of objects
		RawFile tempfile = rawfiles[fileselection]; 
		int pos = getActualPosition(removed)-1;
		String[] mInfo = tempfile.returnInfo(pos);
		for (int i = 0; i<tempfile.getLength(); i++) {
			String[] info = tempfile.returnInfo(i);
			if (Integer.valueOf(info[10]) == pos + 1) {
				tempfile.setLP(Integer.valueOf(info[10]), i);
				tempfile.editEntry(i, 10, "0");
				
			}
			else if (Integer.valueOf(info[10]) > pos+1) {
				tempfile.editEntry(i, 10, Integer.toString(Integer.valueOf(info[10])-1));
			}
			if(Integer.valueOf(mInfo[10])==pos) {
				Vector3D pdV = Vector3D.mult(-Double.valueOf(mInfo[6])/Double.valueOf(info[6]), new Vector3D(
						Double.valueOf(mInfo[3]), Double.valueOf(mInfo[4]), Double.valueOf(mInfo[5])));
				tempfile.editEntry(i, 3, Double.toString(Double.valueOf(info[3])-pdV.x));
				tempfile.editEntry(i, 4, Double.toString(Double.valueOf(info[4])-pdV.y));
				tempfile.editEntry(i, 5, Double.toString(Double.valueOf(info[5])-pdV.z));
			}
			if(Integer.valueOf(info[10])<-1) {
				tempfile.editEntry(i, 10, Integer.toString(-1));
			}
		}
	}
	public void handleEnable(int enabled) { //Handles reenabling of objects
		int pos = getActualPosition(enabled);
		RawFile tempfile = rawfiles[fileselection];
		for (int i = 0; i<tempfile.getLength(); i++) {
			String[] info = tempfile.returnInfo(i);
			if (Integer.valueOf(getActualPosition(tempfile.getLP(i))-1) == pos) {
				tempfile.editEntry(i, 10, Integer.toString(pos));
				tempfile.setLP(-1, i);
			}
			else if (Integer.valueOf(info[10]) >= pos) {
				tempfile.editEntry(i, 10, Integer.toString(Integer.valueOf(info[8])+1));
			}
			if(Integer.valueOf(info[10])<-1) {
				tempfile.editEntry(i, 10, Integer.toString(-1));
			}
		}
		
	}
	public int getActualPosition(int entry) { //Gets real position while taking disabled objects into account
		int v = 0;
		for (int i = 0; i<entry; i++) {
			if (rawfiles[fileselection].isenabled(i)) {
				v++;
			}
		}
		v++;
		return v;
	}
	public void addFile(String name) { //Adds a new file
		RawFile[] tmpFile = new RawFile[rawfiles.length+1];
		for(int i = 0; i<rawfiles.length; i++) {
			tmpFile[i] = rawfiles[i];
		}
		File f = new File(Space.objPath + "Obj" + name);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
			}
			tmpFile[rawfiles.length] = new RawFile(f);
			rawfiles = new RawFile[tmpFile.length];
			for(int i = 0; i<tmpFile.length; i++) {
				rawfiles[i] = tmpFile[i];
			}
		}
	}
	public void genRawFiles(String path) { //Generates rawfile classes from file
		File f = new File(path);
		File[] files = f.listFiles();
		int k = 0;
		File[] verfiles = new File[100];
		for (int i = 0; i<files.length; i++) {
			String nm = files[i].getName();
			if(nm.startsWith("Obj")) {
				verfiles[k] = files[i];
				k++;
			}
		}
		rawfiles = new RawFile[k];
		for(int i = 0; i<k; i++) { 
			rawfiles[i] = new RawFile(verfiles[i]);
		}
	}

	public void setObjDisplay() { //Sets up the object display list
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listM.clear();
				int clength = rawfiles[fileselection].getLength();
				int tmp = 0;
				for(int i = 0; i<clength; i++) {
					listM.addElement(createString(i, tmp, rawfiles[fileselection]));
					if(rawfiles[fileselection].isenabled(i)) {
						tmp++;
					}
				}
				listM.addElement("new entry...");

			}
		});
	}
	public static String[] loadSetup() { //Loads relevant filenames
		Scanner inFile = null;
		try {
			inFile = new Scanner(new File("last"));
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		}
		inFile.useDelimiter("\n");
		String[] temp = new String[100];
		int j = 0;
		while(inFile.hasNext()) {
			temp[j] = inFile.next();
			j++;
		}
		String[] ret = new String[j];
		for(int i = 0; i<j; i++) {
			ret[i] = temp[i];
		}
		inFile.close();
		return ret;
	}
	public void updateObjDisplay() { //Updates the object display from rawfiles
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int clength = rawfiles[fileselection].getLength();
				int tmp = 0;
				for(int i = 0; i<clength; i++) {
					listM.set(i, createString(i, tmp, rawfiles[fileselection]));
					if(rawfiles[fileselection].isenabled(i)) {
						tmp++;
					}
				}
				listM.set(clength, "new entry...");
			}
		});
	}
	public class selobjlist implements ListSelectionListener { //Object selection listener
		public void valueChanged(ListSelectionEvent e) {
			if (!(e.getValueIsAdjusting())) {
				if(fileselection != rawfiles.length) {
					if(objlist.getSelectedIndex() == -1 || objlist.getSelectedIndex() == rawfiles[fileselection].getLength()) {
						objselection = objlist.getSelectedIndex();
						for(int k = 0; k<fields.length; k++) {
							fields[k].setText("0");
						}
						if(objselection==rawfiles[fileselection].getLength()) {

							b1.setText("New Entry");
							b1.setActionCommand("newentry");
							b1.setToolTipText("Create a new object");
						}
					}
					else {
						b1.setText("Toggle on");
						b1.setActionCommand("toggle");
						b1.setToolTipText("Toggle selected object on or off");
						objselection = objlist.getSelectedIndex();
						for(int k = 0; k<fields.length; k++) {
							String[] info = rawfiles[fileselection].returnInfo(objselection);
							if(k>7) {
								if(k!=11) {
									fields[k].setText(info[k]);
								} else {
									fields[k].setText(info[k+1]);
								}
							} else if ((info[k].equals("0") && k<7)){
								fields[k].setText("0.0");
							} else {
								fields[k].setText(info[k]);
							}
						}
					}
				}
			}
		}
	}
	public class selfilelist implements ListSelectionListener { //File selection listener
		public void valueChanged(ListSelectionEvent e) {
			if (!(e.getValueIsAdjusting())) {
				if(filelist.getSelectedIndex() == -1 || filelist.getSelectedIndex() == rawfiles.length+1) {
					objselection = -1;
				}
				else {
					fileselection = filelist.getSelectedIndex();
					if(fileselection == rawfiles.length) {
						b1.setText("New File");
						b1.setActionCommand("newfile");
						b1.setToolTipText("Create a new file with the entered name");
						objselection = -1;
						listM.clear();
					}
					else {
						b1.setText("Toggle on");
						b1.setActionCommand("toggle");
						b1.setToolTipText("Toggle selected object on or off");
						objselection = -1;
						setObjDisplay();
					}
				}
			}
		}
	}
	 class buttonControl implements ActionListener { //button listener
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ((command.equals("toggle"))&&objselection != -1&&fileselection != rawfiles.length) {
				if (objselection != rawfiles[fileselection].getLength()) {
					saved = false;
					if (rawfiles[fileselection].isenabled(objselection)) {
						handleRemove(objselection);
						rawfiles[fileselection].editEntry(objselection, 11, "0");
					}
					else {
						rawfiles[fileselection].editEntry(objselection, 11, "1");
						handleEnable(objselection);
					}
					updateObjDisplay();
				}
			}
			else if (command.equals("save")) {
				saved = true;
				for(int i = 0; i<rawfiles.length; i++) {
					rawfiles[i].writeToFile();
				}
			}
			else if (command.equals("delete")) {
				handleRemove(objselection);
				rawfiles[fileselection].removePerm(objselection);
				setObjDisplay();
				saved = false;
			}
			else if (command.equals("launch")) {
				if(fileselection != -1 && fileselection != rawfiles.length) {
					objects = rawfiles[fileselection].createObjects();
					if(!saved) {
						new Dialog("Save before launching?", rawfiles, frame);
					}
					else {
						synchronized(frame) {
							frame.notify();
						}
					}
				}
			}
			else if (command.equals("elaunch")) {
				if(fileselection != -1 && fileselection != rawfiles.length) {
					//new LaunchSetup(rawfiles, fileselection, frame);
					if(!saved) {
						new Dialog("Save files?", rawfiles);
					}
				}
			}
			else if (command.equals("newfile")) {
				String name = nameField.getText();
				if(name.length() > 1) {
					addFile(name);
					setFileDisplay();
					fileselection = rawfiles.length-1;
				}
			}
			else if (command.equals("newentry")) {
				rawfiles[fileselection].addEntry();
				objselection = rawfiles[fileselection].getLength()-1;
				for(int i = 0; i<fields.length; i++) {
					if(i!=11) {
					rawfiles[fileselection].editEntry(objselection, i, fields[i].getText());
					} else {
						rawfiles[fileselection].editEntry(objselection, i+1, fields[i].getText());
					}
				}
				setObjDisplay();
				saved = false;
				objlist.setSelectedIndex(objselection);
			}
			else if (command.equals("nParam")) {
				new ParamPanel(rawfiles[fileselection].getLength(), rawfiles[fileselection], t);
			}
		}
	}
	public class fieldList implements DocumentListener { //Document listener for actively modifying the object list
		public void insertUpdate(DocumentEvent e) {
			Document source = e.getDocument();
			if(rawfiles.length != fileselection) {
				if(rawfiles[fileselection].getLength()!=objselection) {
					for(int w = 0; w<fields.length; w++) {
						if (source.equals(fields[w].getDocument())&& objselection != -1) {
							saved = false;
							if(w == 11) {
								rawfiles[fileselection].editEntry(objselection, w+1, fields[w].getText());
							} else {
								rawfiles[fileselection].editEntry(objselection, w, fields[w].getText());
							}
						}
					}
					updateObjDisplay();
				}
			}
		}
		public void removeUpdate(DocumentEvent e) {
			Document source = e.getDocument();
			if(rawfiles.length != fileselection) {
				if(rawfiles[fileselection].getLength()!=objselection) {
					for(int w = 0; w<fields.length; w++) {
						if (source.equals(fields[w].getDocument())&& objselection != -1) {
							saved = false;
							if(w == 11) {
								rawfiles[fileselection].editEntry(objselection, w+1, fields[w].getText());
							} else {
								rawfiles[fileselection].editEntry(objselection, w, fields[w].getText());
							}
						}
					}
					updateObjDisplay();
				}
			}
		}
		public void changedUpdate(DocumentEvent e) {
		}
	}
	class DocFilter extends DocumentFilter { //Filters the textfields
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);
			for(int i = 0; i<fields.length; i++) {
				if(fields[i].getDocument().equals(doc)) {
					if(i<=6) {
						if(isInt(string) || string.equals("-") && (offset == 0)) {
							super.insertString(fb, offset, string, attr);
						}
					}else if(i<10 && i>6) {
						if(isInt(string)) {
							if ((Integer.valueOf(sb.toString()) < 256) && (Integer.valueOf(sb.toString()) > -1)) {
								super.insertString(fb, offset, string, attr);
							}
						}
					} else if(i==10) {
						System.out.println(sb.toString() + rawfiles[fileselection].getLength());

						if(isInt(string)) {
							if((Integer.valueOf(sb.toString()) < (rawfiles[fileselection].getLength()+1)) 
									&& (Integer.valueOf(sb.toString())> -1)) {
								super.insertString(fb, offset, string, attr);
							}
						}
					} else if(i==11) {
						if(isDouble(string)) {
							if(Double.valueOf(sb.toString()) < 1 && Double.valueOf(sb.toString())>=0) {
								super.insertString(fb, offset, string, attr);
							}
						}
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
					if(i<=6) {
						if(isDouble(sb.toString())) {
							super.replace(fb, offset, length, string, attr);
						}
					}else if(i<10 && i>6) {
						if(isInt(sb.toString())) {
							if ((Integer.valueOf(sb.toString()) < 256) && (Integer.valueOf(sb.toString()) > -1)) {
								super.replace(fb, offset, length, string, attr);
							}
						}
					}else if(i==10) {
						if(isInt(sb.toString())) {
								if ((Integer.valueOf(sb.toString()) < rawfiles[fileselection].getLength()+1) 
										&& (Integer.valueOf(sb.toString())> -1)) {
								super.replace(fb, offset, length, string, attr);
							}
						}
					}else if(i==11) {
						if(isDouble(sb.toString())) {
							if(Double.valueOf(sb.toString()) < 1 && Double.valueOf(sb.toString())>=0) {
								super.replace(fb, offset, length, string, attr);
							}
						}
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
					if(i<=6) {
						if (isDouble(sb.toString())) {
							super.remove(fb, offset, length);
						} 
					} else if(i>6 && i<10) {
						if (isInt(sb.toString())) {
							if(Integer.parseInt(sb.toString()) > -1 && Integer.parseInt(sb.toString()) < 256) {
								super.remove(fb, offset, length);
							}
						}
					} else if(i == 10) {
						if (isInt(sb.toString())) {
							if(Integer.parseInt(sb.toString()) > -1 && Integer.parseInt(sb.toString()) < 
									rawfiles[fileselection].getLength()) {
								super.remove(fb, offset, length);
							}
						}
					}else if(i==11) {
						if(isDouble(sb.toString())) {
							if(Double.valueOf(sb.toString()) < 1 && Double.valueOf(sb.toString())>=0) {
								super.remove(fb, offset, length);
							}
						}
					}
				}
			}
			if(sb.length() == 0) {
				super.remove(fb, 0, 1);
			}
		}
	}
}
