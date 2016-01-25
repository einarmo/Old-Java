
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Dialog implements ActionListener {
	
	JButton b1, b2;
	JLabel textLabel;
	JFrame f;
	JPanel m;
	JFrame frame;
	RawFile[] rawfiles;
	int mode;
	
	Dialog(String text, RawFile[] rawfiles, JFrame frame) {
		textLabel = new JLabel("<html><div style=\"text-align: center;\">" + text + "</html>");
		this.frame = frame;
		this.rawfiles = rawfiles;
		setup();
		mode = 0;
	}
	Dialog(String text, RawFile[] rawfiles) {
		textLabel = new JLabel("<html><div style=\"text-align: center;\">" + text + "</html>");
		this.rawfiles = rawfiles;
		setup();
		mode = 1;
	}
	public void launchSim() {
		synchronized(frame) {
			frame.notify();
		}
	}
	public void setup() {

		
		m = new JPanel();
		m.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.CENTER;
		c.ipady = 10;
		m.add(textLabel, c);
		
		b1 = new JButton("Yes");
		b1.setActionCommand("yes");
		b1.addActionListener(this);
		c.gridy = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 1;
		m.add(b1, c);
		
		b2 = new JButton("No");
		b2.setActionCommand("no");
		b2.addActionListener(this);
		c.gridx = 1;
		m.add(b2, c);
		
		f = new JFrame();
		f.add(m);
		f.pack();
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
		f.setLocation(600, 300);
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("yes")) {
			for(int i = 0; i<rawfiles.length; i++) {
				rawfiles[i].writeToFile();
			}
			f.dispose();
			if (mode == 0) {
				launchSim();
			}
		}
		else if (e.getActionCommand().equals("no")) {
			f.dispose();
			if (mode == 0) {
				launchSim();
			}
		}
	}
		
}
