import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class EntityField {
	public FieldButton b;
	public MainPanel m;
	public int[] c;
	public ArrayList<EntityField> con;
	public int selected;
	public Vector pos;
	public Color bgc, bc;
	public String text = "";
	public String suf = "";
	public ArrayList<String> tag = new ArrayList<String>();
	public int selNum = -1;
	EntityField(Vector pos, MainPanel m) {
		this.m = m;
		this.pos = pos;
		this.b = new FieldButton(0, this);
		this.c = new int[8];
		this.con = new ArrayList<EntityField>();
		this.bgc = OrgKjemi.bgc;
		this.bc = Color.black;
		for(int i = 0; i<8; i++) {
			c[i] = 0;
		}
		b.addKeyListener(new keyList());
	}
	public boolean isAdjecent(Vector p) {
		if ((Math.abs(p.x-pos.x)==1 || p.x-pos.x == 0) && (Math.abs(p.y-pos.y)==1 || p.y-pos.y == 0)) {
			return true;
		} else {
			return false;
		}
	}
	public void setSub(String first) {
		//Sets the tile to a "first" atom with hydrogen bonds and connects it to the first selected tile.
		if(OrgKjemi.slots[m.bList[m.sel1.x][m.sel1.y].selNum]-m.bList[m.sel1.x][m.sel1.y].getConnections()>m.selB&&( 
				OrgKjemi.slots[m.selO+1]>m.selB||first.equals("C")) && 
				(m.bList[m.sel1.x][m.sel1.y].text.startsWith("C")||first.equals("C"))) {
			if(con.size()==0) {
				text = first + "H";
				if(first != "C") {
					suf = Integer.toString(OrgKjemi.slots[m.selO+1]-(m.selB+1));
				} else {
					suf = Integer.toString(OrgKjemi.slots[0]-(m.selB+1));
				}
				if(suf.equals("1")) {
					suf = "";
				}
				if(suf.equals("0")) {
					suf = "";
					text = first;
				}
				m.bList[m.sel1.x][m.sel1.y].addConnect(pos);
				addConnect(m.sel1);
				if(first.equals("C")) {
					selNum = 0;
				} else {
					selNum = m.selO+1;
				}

			}
		} else {
			m.textL.setText("No room for more bonds!");
		}
	}
	public void clearNeighbors() {
		for(int i = 0; i<8; i++) {
			c[i] = 0;
		}
		con.clear();
	}
	public int getConnections() {
		int ret = 0;
		for(int i = 0; i<8; i++) {
			ret += c[i];
		}
		return ret;
	}
	public void checkTag(EntityField ignore) {
		int hydroxy = 0;
		int oxyd = 0;
		int oxy = 0;
		boolean ignored = false;
		tag.clear();
		for(int i = 0; i<con.size(); i++) {
			if(con.get(i).text.equals("OH")) {
				hydroxy++;
			}
			if(con.get(i).text.equals("O")) {
				if(c[getSide(con.get(i).pos, pos)] == 2) {
					oxyd++;
				} else {
					if(ignore != con.get(i)) {
						oxy++;
					} else {
						ignored = true;
					}
				}
			}
			if(con.get(i).text.equals("Cl")) {
				tag.add("klor");
			}
			if(con.get(i).text.equals("Br")) {
				tag.add("brom");
			}
			if(con.get(i).text.equals("NH")) {
				if(c[getSide(con.get(i).pos, pos)] == 2) {
					tag.add("imin");
				} else if (c[getSide(con.get(i).pos, pos)] == 1 && con.get(i).getConnections()==1) {
					tag.add("amin");
				} else if(con.size()-getOthers().length == 1){
					tag.add("amino");
				}
			}
			if(con.get(i).text.equals("N")) {
				if(c[getSide(con.get(i).pos, pos)]==3) {
					tag.add("cyan");
				} else if (c[getSide(con.get(i).pos, pos)] == 2 && con.get(i).getConnections() == 3) {
					tag.add("imino");
				} else if (c[getSide(con.get(i).pos, pos)] == 1 && con.get(i).getConnections() > 1) {
					tag.add("amino");
				}
			} 
		}
		if(hydroxy>0) {
			if(oxyd>0) {
				tag.add("syre");
				for(int i = 1; i<hydroxy; i++) {
					tag.add("hydroxy");
				}
			} else {
				for(int i = 0; i<hydroxy; i++) {
					tag.add("hydroxy");
				}
			}
		} else if(oxyd > 0) {
			if(oxy>0) {
				tag.add("ester");
				for(int i = 1; i<oxyd; i++) {
					if(isEndpoint()) {
						tag.add("aldehyd");
					} else {
						tag.add("keton");
					}
				}
			} else {
				for(int i = 0; i<oxyd; i++) {
					if(isEndpoint() && !ignored) {
						tag.add("aldehyd");
					} else {
						tag.add("keton");
					}
				}
			}
		}
		if(oxy>0 && oxyd == 0) {
			for(int i = 0; i<oxy; i++) {
				tag.add("eter");
			}
		}
		
	}
	public boolean isEndpoint() {
		//Checks if the tile is an endpoint.
		int cnt = 0;
		for(int i = 0; i<con.size(); i++) {
			if((con.get(i).text.equals("CH") || con.get(i).text.equals("C"))&&(text.equals("CH")||text.equals("C"))) {
				cnt++;
			}
		}
		if(cnt == 1 || (cnt == 0 && (text.equals("CH")||text.equals("C")))) {
			return true;
		} else {
			return false;
		}
	}
	public void addItem() {
		//Adds an item that is not a carbon atom but one of the other possible items.
		if(OrgKjemi.slots[m.bList[m.sel1.x][m.sel1.y].selNum]-m.bList[m.sel1.x][m.sel1.y].getConnections()>m.selB && 
				OrgKjemi.slots[m.selO+1]>m.selB) {
			if(con.size()==0) {
				selNum = m.selO+1;
				text = OrgKjemi.others[selNum-1];
				suf = "";
				m.bList[m.sel1.x][m.sel1.y].addConnect(pos);
				addConnect(m.sel1);
			} 
		b.repaint();
		}else {
			m.textL.setText("No room for more bonds!");
		}
	}
	public int getBranches() { //Returns the number of branches
		int initCH = -2;
		int count = 0;
		for(int i = 0; i<con.size(); i++) {
			if(con.get(i).text.equals("CH") || (con.get(i).text.equals("C"))) {
				initCH++;
			} else {
				count++;
			}
		}
		if(initCH < 1) {
			return count;

		} else {
			return count+initCH;
		}
	}
	public EntityField[] returnBranches(EntityField pr, EntityField next) {
		//Returns a list of branches that spring out of this one but which are not pr or next.
		int num = 0;
		for(int i = 0; i<con.size(); i++) {
			if(con.get(i) != pr && con.get(i) != next && (con.get(i).text.equals("CH") || con.get(i).text.equals("C"))) {
				num++;
			}
		}
		EntityField[] ret = new EntityField[num];
		int cnt = 0;
		for(int i = 0; i<con.size(); i++) {
			if(con.get(i) != pr && con.get(i) != next && (con.get(i).text.equals("CH") || con.get(i).text.equals("C"))) {
				ret[cnt] = con.get(i);
				cnt++;
			}
		}
		return ret;
	}
	public int[] getOthers() {
		//Returns a list of other substances connected to this tile.
		int num = 0;
		for(int i = 0; i<con.size(); i++) {
			if(!(con.get(i).text.equals("CH")||con.get(i).text.equals("C"))) {
				num++;
			}
		}
		int[] ret = new int[num];
		int cnt = 0;
		for(int i = 0; i<con.size(); i++) {
			for(int j = 0; j<OrgKjemi.others.length; j++) {
				if(con.get(i).text.equals(OrgKjemi.others[j])) {
					ret[cnt] = j;
					cnt++;
				}
			}
		}
		return ret;
	}
	public void removeConnection(EntityField par) {
		//Removes a connection to the field par
		int side = getSide(par.pos, pos);
		if(getConnections()>1) {
			if(suf.equals("")) {
				if(text.length() == 1) {
					text += "H";
					if(c[side] > 1) {
						suf = Integer.toString(c[side]);
					}
				} else {
					suf = Integer.toString(c[side] + 1);
				}
			} else {
				suf = Integer.toString(c[side]+Integer.valueOf(suf));
			}
		}
		c[side] = 0;
		for(int i = 0; i<con.size(); i++) {
			if(con.get(i) == par) {
				con.remove(i);
			}
		}
		if(con.size() == 0) {
			suf = "4";
		}
		b.repaint();
	}
	public void permRemove() {
		if (con.size()==1 && (con.get(0).text.startsWith("C") || con.get(0).con.size()>1)) {
			con.get(0).removeConnection(this);
			con.clear();
			clearNeighbors();
			text = "";
			suf = "";
			b.repaint();
			selNum = -1;
		} else {
			m.textL.setText("You can only remove endpieces!");
		}
	}
	public void addConnect(Vector p1) {
		con.add(m.bList[p1.x][p1.y]);
		c[getSide(p1, pos)] = m.selB+1;
		int numH = -1;
		if(selNum>0) {
			numH = OrgKjemi.slots[selNum]-getConnections();
		} else if(selNum == 0) {
			numH = OrgKjemi.slots[0]-getConnections();
		}
		if (numH>1) {
			suf = Integer.toString(numH);
		} else if (numH == 1){
			suf = "";
		} else if (numH == 0&&OrgKjemi.slots[selNum]>1){
			suf = "";
			text = String.valueOf(text.charAt(0));
		}
		b.repaint();
	}
	public int getSide(Vector p1, Vector p2) {
		//Identifies which side of the Vector p2 the Vector p1 is on, the numbers
		//go in clockwise order with 0 being to the left.
		int reX = p1.x-p2.x;
		int reY = p1.y-p2.y;
		int ret = 0;
		if(reX==-1) {
			if (reY == 1) {
				ret = 7;
			} else if (reY == 0) {
				ret = 0;
			} else if (reY == -1) {
				ret = 1;
			}
		} else if (reX == 0) {
			if (reY == 1) {
				ret = 6;
			} else if (reY == -1) {
				ret = 2;
			}
		} else if (reX == 1) {
			if (reY == 1) {
				ret = 5;
			} else if (reY == 0) {
				ret = 4;
			} else if (reY == -1) {
				ret = 3;
			}
		}
		return ret;
	}
	public class keyList implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
			if(arg0.getKeyCode() == KeyEvent.VK_SHIFT) {
				m.shiftPressed = true;
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			if(arg0.getKeyCode() == KeyEvent.VK_SHIFT) {
				m.shiftPressed = false;
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0) {			
		}

	}
}