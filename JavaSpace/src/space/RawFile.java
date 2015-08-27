package space;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class RawFile {
	public String name, rawname;
	File file;
	String[] lines;
	int[] lastP;
	public Vector offset;
	RawFile(File file) {
		this.rawname = file.getName();
		this.name = rawname.substring(3);
		this.file = file;
		this.offset = new Vector(0,0);
		
		readInfo();
		lastP = new int[lines.length];
		for(int i = 0; i<lines.length; i++) {
			lastP[i] = -1;
		}
	}
	public void readInfo() {
		Scanner file = null;
		try {
			file = new Scanner(new File(Space.objPath + rawname));
		} catch (FileNotFoundException e) {}
		file.useDelimiter("\n");
		String[] templines = new String[100];
		int i = 0;
		while (file.hasNext()) {
			templines[i] = file.next();
			i++;
		}
		lines = new String[i];
		for (int v = 0; v<i; v++) {
			lines[v] = templines[v];
			lines[v] = lines[v].replaceAll("\n", "");
		}
		file.close();
	}
	public String[] returnInfo(int num) {
		String[] info = lines[num].split(" ");
		return info;
	}
	public void writeToFile() {
		PrintWriter file = null;
		try {
			file = new PrintWriter(Space.objPath + rawname, "UTF-8");
		} catch (FileNotFoundException e) {
		} catch (UnsupportedEncodingException s) {
		}
		for(int i = 0; i<lines.length; i++) {
			file.print(lines[i]+"\n");
		}
		file.close();
	}
	public void appendLine(String line) {
		ArrayList<String> readF = new ArrayList<String>();
		Scanner inFile = null;
		try {
			inFile = new Scanner(new File(Space.objPath + rawname));
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		}
		inFile.useDelimiter("\n");
		while(inFile.hasNext()) {
			readF.add(inFile.next());
		}
		inFile.close();
		
		PrintWriter file = null;
		try {
			file = new PrintWriter(Space.objPath + rawname, "UTF-8");
		} catch (FileNotFoundException e) {
		} catch (UnsupportedEncodingException s) {}
		
		for(int i = 0; i<readF.size(); i++) {
			file.print(readF.get(i)+"\n");
		}
		file.print(line+"\n");
		file.close();
	}
	public void modEntry(String line, int num) {
		lines[num] = line;
	}
	public String name() {
		return name;
	}
	public int getLength() {
		return lines.length;
	}
	public void setOffset(Vector offset) {
		this.offset = offset;
	}
	public boolean isenabled(int entry) {
		String[] info = returnInfo(entry);
		boolean ret;
		if (Integer.valueOf(info[9].trim()) == 1) {
			ret = true;
		}
		else {
			ret = false;
		}
		return ret;
	}
	public String mergeToLine(String[] words) {
		String finstr = "";
		for(int i = 0; i<words.length-1; i++) {
			finstr = (finstr + words[i] + " ");
		}
		finstr = (finstr + words[words.length-1]);
		return finstr;
	}
	public RawFile cloneFile() {
		RawFile rawf = new RawFile(file);
		return rawf;
	}
	public void editEntry(int entry, int index, String replace) {
		String[] info = returnInfo(entry);
		info[index] = replace;
		lines[entry] = mergeToLine(info);
	}
	public void setLP(int newVal, int index) {
		lastP[index] = newVal;
	}
	public void removePerm(int entry) {
		String[] tmpLines = new String[lines.length-1];
		int v = 0;
		for(int i = 0; i<lines.length; i++) {
			if(i != entry) {
				tmpLines[v] = lines[i];
				v++;
			}
		}
		lines = new String[tmpLines.length];
		for(int i = 0; i<lines.length; i++) {
			lines[i] = tmpLines[i];
		}
	}
	public void addEntry() {
		String[] tmpLines = new String[lines.length+1];
		int[] tmpInt = new int[lastP.length+1];
		for (int i = 0; i<lines.length; i++) {
			tmpLines[i] = lines[i];
			tmpInt[i] = lastP[i];
		}
		tmpInt[lastP.length] = (-1);
		tmpLines[lines.length] = ("0 0 0 0 1 0 0 0 0 1");
		lastP = new int[tmpInt.length];
		lines = new String[tmpLines.length];
		for (int i = 0; i<tmpLines.length; i++) {
			lines[i] = tmpLines[i];
			lastP[i] = tmpInt[i];
		}
	}
	public int getLP(int index) {
		return lastP[index];
	}
	public int getRealLength() {
		int j = 0;
		for(int i = 0; i<lines.length; i++) {
			String[] info = this.returnInfo(i);
			if(Integer.valueOf(info[9].trim()) == 1) {
				j++;
			}
		}
		return j;
	}
	public Object[] createObjects() {
		int j = getRealLength();
		
		Object[] OB = new Object[j];
		int v = 0;
		for(int i = 0; i<lines.length; i++) {
			
			String[] info = this.returnInfo(i);
			int validNum = Integer.valueOf(info[9].trim());
			if(validNum == 1) {
				Double[] num = new Double[info.length];
				for(int n = 0; n< info.length; n++) {
					num[n] = Double.valueOf(info[n]);
				}
				Vector pos = new Vector(num[0], num[1]);
				pos.addVal(offset.x, offset.y);
				Color c = new Color(num[5].intValue(), num[6].intValue(), num[7].intValue());
				OB[v] = new Object(v, pos, new Vector(num[2], num[3]), num[4], c, num[8].intValue());
				v++;
			}
		}
		return OB;
	}
}
