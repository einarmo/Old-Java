
import java.io.*;
import java.util.Scanner;
public class Space {
	Object[] OB;
	public static double G, calcmod;
	public static boolean calcorb;
	public static int numticks, update;
	public static String objPath;
	public static void main(String[] args) {
		new Space();
	}
	Space() {
		genConf();
		readConf("config");
		newInit();
		new EditPanel(800, 400, this);
	}
	public void readConf(String config) {
		Scanner conf = null;
		try {
			conf = new Scanner(new File(config));
		} catch (FileNotFoundException e) {}
		conf.useDelimiter("\n");
		String[] confstr = new String[6];
		int h = 0;
		while (conf.hasNext()) {
			confstr[h] = conf.next();
			h++;
		}
		
		String[] firststr = new String[confstr.length];
		for(int v = 0; v<confstr.length; v++) {
			String[] temp = confstr[v].split(" +");
			firststr[v] = temp[0];
		}
		conf.close();
		
		Space.calcmod = Double.valueOf(firststr[0]);
		Space.calcorb = Boolean.valueOf(firststr[1]);
		Space.update = Integer.valueOf(firststr[2]);
		Space.numticks = Integer.parseInt(firststr[3]);
		Space.G = Double.valueOf(firststr[4]);
		Space.objPath = firststr[5];
	}
	public void newInit() {
		if(!objPath.equals(".")) {
			File dir = new File(objPath);
			if(!dir.exists()) {
				dir.mkdir();
			}
		}
		File f = new File(objPath+"ObjDefault");
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				System.out.println("Failed to create default file");
				System.out.println(e.getMessage());
			}
		}

	}
	public void genConf() {
		File conf = new File("config");
		if(!conf.exists()) {
			try {
				conf.createNewFile();
			} catch (IOException e) {
			}
			PrintWriter file = null;
			try {
				file = new PrintWriter("config");
			} catch (FileNotFoundException e) {}
			file.println("0.5 double calcmod : this variable modifies all movement on the screen. A lower number will make the program run slower.");
			file.println("true boolean calcorb : Calculate circular orbits.");
			file.println("1 integer update : number of ticks between each graphical update. Should be increased if calcmod is lowered.");
			file.println("100000000 integer numticks : number of ticks to be calculated.");
			file.println("0.667384 double G : the universal gravitational constant G in this program.");
			file.println("./objects/ String objPath : the path to the object files.");
			file.close();
		}
	}
	public void launch(Object[] OB) {
		System.out.println("SIZE: " + OB.length);
		new SpaceRun(Space.numticks, OB);
	}
	public static void printInfo(Object[] OB) {
		for(int j = 0; j<OB.length; j++) {
			double[] dA = OB[j].returnVal();
			System.out.println(OB[j].num+1);
			for(int i = 0; i<dA.length; i++) {
				System.out.println(dA[i]);
			}
			System.out.println(OB[j].par);
			System.out.println(" ");
		}
	}
}
