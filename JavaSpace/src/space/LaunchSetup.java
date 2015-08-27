package space;
import javax.swing.*;

import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;

public class LaunchSetup {
	JPanel main;
	JScrollPane fileScroll, displScroll, addScroll;
	JList<String> fileList, addList;
	DefaultListModel<String> fileM, addM;
	JLabel infodisp, xoff, yoff;
	JButton lb, ab;
	JTextField xfield, yfield;
	JFrame frame;
	RawFile[] raws;
	ArrayList<RawFile> launchFiles;
	int fsel, asel;
	JFrame syncF;
	LaunchSetup(RawFile[] raws, int initsel, JFrame syncF) {
		this.raws = raws;
		this.syncF = syncF;
		main = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Lists
		infodisp = new JLabel();
		infodisp.setText(genInfoString(raws[initsel]));
		displScroll = new JScrollPane(infodisp);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		displScroll.setPreferredSize(new Dimension(100, 400));
		main.add(displScroll, c);
		
		fileM = new DefaultListModel<String>();
		fileList = new JList<String>(fileM);
		setFileList();

		fileList.setSelectedIndex(0);
		fileScroll = new JScrollPane(fileList);
		fileScroll.setPreferredSize(new Dimension(200, 400));
		c.gridx = 3;
		c.weightx = 0.0;
		main.add(fileScroll, c);
		
		launchFiles = new ArrayList<RawFile>();
		addM = new DefaultListModel<String>();
		setLaunchList();
		addList = new JList<String>(addM);
		fileList.addListSelectionListener(new fileListener());
		addList.addListSelectionListener(new fileListener());
		addScroll = new JScrollPane(addList);
		addScroll.setPreferredSize(new Dimension(200, 400));
		c.gridx = 6;
		main.add(addScroll, c);
		
		//Buttons
		c.gridwidth = 2;
		c.gridheight = 2;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 1;
		ab = new JButton("  Add ");
		ab.setToolTipText("Add object to launch queue");
		ab.setActionCommand("add");
		ab.addActionListener(new buttonListener());
		main.add(ab,c);
		
		c.gridwidth = 1;
		c.gridx = 5;
		lb = new JButton("Launch");
		lb.setToolTipText("Launch with current setup");
		lb.setActionCommand("launch");
		lb.addActionListener(new buttonListener());
		main.add(lb,c);
		
		//Labels and fields
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 6;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		xoff = new JLabel("offset x");
		main.add(xoff, c);
		
		c.gridx = 7;
		yoff = new JLabel("offset y");
		main.add(yoff, c);
		
		c.gridx = 6;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		xfield = new JTextField(3);
		xfield.setText("0");
		main.add(xfield, c);
		
		c.gridx = 7;
		yfield = new JTextField(3);
		yfield.setText("0");
		main.add(yfield, c);
		
		//other
		frame = new JFrame("Edit Launch");
		frame.add(main);
		frame.pack();
		frame.setLocation(100,100);
		load();
		frame.setVisible(true);
	}
	public void setFileList() {
		for(int i = 0; i<raws.length; i++) {
			fileM.addElement(raws[i].name());
		}
	}
	public void load() {
		String[] line = EditPanel.loadSetup();
		for(int i = 0; i<line.length; i++) {
			String[] inf = line[i].split(" ");
			for(int j = 0; j<raws.length;j++) {
				if (raws[j].rawname.equals(inf[0])) {
					System.out.println(j);
					RawFile tmpraw = raws[j].cloneFile();
					tmpraw.setOffset(new Vector(Double.valueOf(inf[1]), Double.valueOf(inf[2])));
					launchFiles.add(tmpraw);
					setLaunchList();
				}
			}
		}
	}
	public void setLaunchList() {
		addM.clear();
		for (int i = 0; i<launchFiles.size(); i++) {
			addM.addElement(launchFiles.get(i).name());
		}
	}
	public String genInfoString(RawFile rawfile) {
		String finstr = "";
		for(int i = 0; i<rawfile.getLength(); i++) {
			String[] info = rawfile.returnInfo(i);
			String linestr = "";
			linestr = (linestr + "object: " + (i+1) + "<br>x: "+(info[0]) + "<br>y:" + info[1]);
			linestr = (linestr + "<br>xv: " + info[2] + "<br>yv: " + info[3] + "<br>mass: " + info[4]);
			if (info[8].equals("0")) {
				linestr = (linestr + "<br>parent: " + "none" + "<br><br>");
			}
			else {
				linestr = (linestr + "<br>parent: " + (Integer.valueOf(info[8])) + "<br><br>");
			}
			if (info[9].equals("0")) {
				linestr = ("<strike>" + linestr + "</strike>");
			}
			finstr = (finstr + linestr);
		}
		finstr = ("<html>" + finstr + "</html>");
		return finstr;
	}
	public class fileListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (e.getSource().equals(fileList)) {
					fsel = fileList.getSelectedIndex();
					if (fsel != -1) {
						infodisp.setText(genInfoString(raws[fsel]));
						addList.clearSelection();
						ab.setText("Add");
						ab.setToolTipText("Add object to launch queue");
						ab.setActionCommand("add");
					}
				}
				else {
					asel = addList.getSelectedIndex();
					if (asel != -1) {
						fileList.clearSelection();
						ab.setText("Remove");
						ab.setToolTipText("Remove object from launch queue");
						ab.setActionCommand("remove");
					}
				}
			}
		}
	}
	public void writeSetup() {
		File f = new File("last");
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
			}
		}
		PrintWriter file = null;
		try {
			file = new PrintWriter("last", "UTF-8");
		} catch (FileNotFoundException e) {
		
		} catch (UnsupportedEncodingException s) {
			
		}
		for(int i = 0; i<launchFiles.size(); i++) {
			RawFile fil = launchFiles.get(i);
			System.out.println(fil.rawname);
			file.println(fil.rawname + " " + fil.offset.x + " " + fil.offset.y);
		}
		file.close();
	}
	public void cLaunch() {
		int totalL = 0;
		for(int i = 0; i<launchFiles.size(); i++) {
			totalL = totalL + launchFiles.get(i).getRealLength();
		}
		Object[] objects = new Object[totalL];
		int step = 0;
		for(int i = 0; i<launchFiles.size(); i++) {
			Object[] tmp = launchFiles.get(i).createObjects();
			int cnt = 0;
			for(int j = 0; j<tmp.length; j++) {
				cnt++;
				if(tmp[j].par != 0) {
					tmp[j].par = tmp[j].par+step;
				}
				tmp[j].num = tmp[j].num+step;
				objects[j+step] = tmp[j];
			}
			step = (step+cnt);
		}
		EditPanel.objects = objects;
		writeSetup();
		synchronized(syncF) {
			syncF.notify();
		}
	}
	public class buttonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals("remove")) {
				launchFiles.remove(asel);
				int svsel = asel;
				setLaunchList();
				if(svsel > 0) {
					addList.setSelectedIndex(svsel-1);
				} else if (svsel == 0 && launchFiles.size() > 0) {
					addList.setSelectedIndex(0);
				}
			}
			else if (command.equals("add")) {
				RawFile tmpraw = raws[fsel].cloneFile();
				tmpraw.setOffset(new Vector(Integer.valueOf(xfield.getText()), Integer.valueOf(yfield.getText())));
				launchFiles.add(tmpraw);
				setLaunchList();
			}
			else if (command.equals("launch")) {
				cLaunch();
				frame.dispose();
			}
		}
	}
}
