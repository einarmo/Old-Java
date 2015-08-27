import java.util.ArrayList;

public class Branch {
	ArrayList<Branch> Branches = new ArrayList<Branch>();
	int parPos = -1;
	ArrayList<EntityField> mainB;
	int[][] others;
	EntityField conPoint;
	boolean largeName = false;
	int type = 0;
	public int[] branchCnt;
	public String name;
	public String bindStr, mainStr, signStr, oPre, sPre;
	public ArrayList<String> branchStr;
	public String[] listPoints;
	public ArrayList<String> finBranchStr = new ArrayList<String>();
	public int bindDifNum = 1; //Number of different bindings
	public boolean main; //If this is a main branch there are some different rules to be followed
	boolean ignoreN = false; //If this is set to true, the method to generate names will check for the ignore field to ignore. 
	public EntityField ignore;
	public EntityField signConnection;
	boolean mainGroup;
	public boolean multiBranch = false;
	Branch(EntityField con, EntityField first, int parPos, boolean main) {
		//Initiates the branch using specific commands for wether or not it is a main branch
		//Starts the getName process, this is recursive.
		this.parPos = parPos;
		this.main = main;
		CalcLongest cal = new CalcLongest();
		mainB = cal.getBranch(first, con, false);
		conPoint = mainB.get(0);
		mainB.remove(0);
		getName();
	}
	Branch(ArrayList<EntityField> mainBranch) {
		this.mainB = mainBranch;
		this.main = true;
		getName();
	}
	Branch(EntityField first, EntityField ignore) {
		this.main = false;
		this.ignoreN = true;
		this.ignore = ignore;
		this.conPoint = ignore;
		CalcLongest cal = new CalcLongest();
		mainB = cal.getBranch(first, ignore, true);         
		getName();
	}
	Branch(EntityField point, int parPos, EntityField conPoint, boolean mainGroup) {
		this.signConnection = point;
		this.conPoint = conPoint;
		this.parPos = parPos;
		this.mainGroup = mainGroup;
		this.multiBranch = true;
	}
	public void getSignificantGroup() {
		//Add all significant groups here:
		//This method finds which significant group dominates the molecule
		for(int i = 0; i<mainB.size(); i++) {
			if(ignoreN) {
				mainB.get(i).checkTag(ignore);
			} else {
				mainB.get(i).checkTag(mainB.get(i));
			}
			for(int j = 0; j<mainB.get(i).tag.size(); j++) {
				for(int v = 0; v<OrgKjemi.types.length-2; v++) {
					if(mainB.get(i).tag.get(j).equals(OrgKjemi.types[v])) {
						if(type<v+1) {
							type = v+1;
						}
					}
				}
			}
		}
	}
	public void getBranches() {
		sPre = "";
		//gets a list of all branches connected to the main branch.
		for(int i = 0; i<mainB.size(); i++) {
			EntityField[] branchI;
			if(i == 0) {
				if(main) {
					if(mainB.size()!=1) {
						branchI = mainB.get(i).returnBranches(mainB.get(i), mainB.get(i+1));
					} else {
						branchI = mainB.get(i).returnBranches(mainB.get(i), mainB.get(i));
					}
				} else {
					if(mainB.size()!=1) {
						branchI = mainB.get(i).returnBranches(conPoint, mainB.get(i+1));
					} else {
						branchI = mainB.get(i).returnBranches(conPoint, mainB.get(i));
					}
				}
			} else if(i==mainB.size()-1) {
				branchI = mainB.get(i).returnBranches(mainB.get(i-1), mainB.get(i));
			} else {
				branchI = mainB.get(i).returnBranches(mainB.get(i-1), mainB.get(i+1));
			}
			for(int j = 0; j<branchI.length; j++) {
				Branches.add(new Branch(mainB.get(i), branchI[j], i, false));
			}
			boolean subsAdd = false;
			for(int j = 0; j<mainB.get(i).con.size(); j++) {
				if((mainB.get(i).con.get(j).text.startsWith("N") || mainB.get(i).con.get(j).text.equals("O")) && mainB.get(i).con.get(j).getConnections() > 
				mainB.get(i).c[mainB.get(i).getSide(mainB.get(i).con.get(j).pos, mainB.get(i).pos)]) {
					if(!ignoreN || ignore != mainB.get(i).con.get(j)) {
						if((type != 3 && type != 4 && type != 9 && type != 10) || i != 0 || !main || subsAdd) { 
							Branches.add(new Branch(mainB.get(i).con.get(j), i, mainB.get(i), false));
						} else if ((type == 3 || type == 4) && i == 0){
							Branch b = new Branch(mainB.get(i).con.get(j), i, mainB.get(i), true);
							sPre += b.retName(true);
							subsAdd = true;
						} else if (type == 10 && i == 0) {
							Branch b = new Branch(mainB.get(i).con.get(j), i, mainB.get(i), true);
							sPre += b.retName(true) + "oxy";
							subsAdd = true;
						} else if (type == 9 && i == 0) {
							Branch b = new Branch(mainB.get(i).con.get(j), i, mainB.get(i), true);
							sPre += b.retName(true);
							subsAdd = true;
						}
					}
				}
			}
		}
		if(Branches.size()>0) {
			branchStr = new ArrayList<String>();
			if(!main) {
				largeName = true;
			}
		}
	}
	public void getName() {
		//creates the name components of this substance.
		//First it adds all branches
		getSignificantGroup();
		getBranches();
		for(int i = 0; i<Branches.size(); i++) {
			branchStr.add(Branches.get(i).retName(false));
			if (Branches.get(i).largeName) {
				branchStr.set(i, "(" + branchStr.get(i) + ")");
			}
		}

		if(Branches.size()>0) {
			finBranchStr.add(branchStr.get(0));
		}
		for(int i = 1; i<Branches.size(); i++) {
			boolean found = true;
			for(int j = 0; j<finBranchStr.size(); j++) {
				if(branchStr.get(i).equals(finBranchStr.get(j))) {
					found = false;
				}
			}
			if(found) {
				finBranchStr.add(branchStr.get(i));
			}
		}
		branchCnt = new int[finBranchStr.size()];
		for(int i = 0; i<branchCnt.length; i++) {
			branchCnt[i] = 0;
		}
		listPoints = new String[finBranchStr.size()];
		for(int i = 0; i<finBranchStr.size(); i++) {
			boolean hasAdded = false;
			listPoints[i] = "";
			for(int j = 0; j<Branches.size(); j++) {
				if(finBranchStr.get(i).equals(Branches.get(j).name) || finBranchStr.get(i).equals("("+Branches.get(j).name+")")) {
					if(hasAdded) {
						listPoints[i] = listPoints[i]+", ";
					}
					branchCnt[i]++;
					listPoints[i] = listPoints[i] + (Branches.get(j).parPos+1);
					hasAdded = true;
				}
			}
			listPoints[i] = listPoints[i] + "-";
		}
		for(int i = 0; i<finBranchStr.size(); i++) {
			finBranchStr.set(i, listPoints[i] + returnNumPrefix(branchCnt[i])+finBranchStr.get(i));
		}
		if(finBranchStr.size()>0&&!sPre.equals("")) {
			finBranchStr.set(0, "-" + finBranchStr.get(0));
		}


		//Next adds suffixes for double and triple bindings.
		mainStr = returnPrefix(mainB.size()); //This creates the main branch name
		ArrayList<Integer> bindingsD = getBindLocation(2);
		ArrayList<Integer> bindingsT = getBindLocation(3);
		bindStr = "";
		if(bindingsD.size() == 0 && bindingsT.size() == 0) {
			bindStr = "an";
		} else {
			largeName = true;
			if(mainB.size()>0) {
				if(bindingsD.size()>0) {
					bindStr = "-";
					for(int i = 0; i<bindingsD.size(); i++) {
						if(i!=0) {
							bindStr += ", ";
						}
						bindStr += bindingsD.get(i);
					}
					bindStr += "-"+returnNumPrefix(bindingsD.size())+ "en";
				}
				if(bindingsT.size()>0) {
					bindStr += "-";
					for(int i = 0; i<bindingsT.size(); i++) {
						if(i!=0) {
							bindStr += ", ";
						}
						bindStr += bindingsT.get(i);
					}
					bindStr += "-"+returnNumPrefix(bindingsT.size())+"yn";
				}
			} else {
				if(bindingsD.size()==1) {
					bindStr = "en";
				} else if(bindingsT.size()==1) {
					bindStr = "yn";
				}
			}
		}
		oPre = "";
		//Adds prefixes related to other types of bindings.
		if(!main) {
		}
		for(int i = 0; i<OrgKjemi.types.length; i++) {
			if((i!=type-1 || (!main&&(type-1 != 5 && type-1 != 6)))&&(i+1!=3&&i+1!=4&&i+1!=9&&i+1!=10) || type>11) {
				ArrayList<Integer> locs = getOLocation(OrgKjemi.types[i]);
				if(locs.size()>0) {
					largeName = true;
					boolean add = false;
					if(mainB.size()>1) {
						for(int v = 0; v<locs.size(); v++) {
							if(locs.get(v)>1) {
								add = true;
							}
						}
						if(add) {
							for(int j = 0; j<locs.size(); j++) {
								if(j!=0) {
									oPre += ", ";
								}
								oPre += locs.get(j);
							}
						}
					}
					if(mainB.size()>1 && add) {
					oPre+="-" + returnNumPrefix(locs.size()) + OrgKjemi.othersName[i];
					oPre = oPre + "-";
					} else {
						oPre += returnNumPrefix(locs.size()) + OrgKjemi.othersName[i];
					}
				}
			}
		}
		if(!oPre.equals("")&&!sPre.equals("")) {
			oPre = "-" + oPre;
		}
		//Creates the string for significant groups
		signStr = "";
		if(type>0 && (main || (type-1 == 5 || type-1 == 6)) && type <12) {
			ArrayList<Integer> locs = getOLocation(OrgKjemi.types[type-1]);
			if(locs.size()>0) {
				boolean add = false;
				for(int v = 0; v<locs.size(); v++) {
					if(locs.get(v)>1) {
						add = true;
					}
				}
				if(add&&type!=9&&type!=10 && type != 3 && type != 4) {
					for(int j = 0; j<locs.size(); j++) {
						if(j!=0) {
							signStr += ", ";
						}
						signStr += locs.get(j);
					}
				}
				if(mainB.size()>1 && add && type != 9 && type != 10 && type != 3 && type != 4) {
					signStr+="-"+returnNumPrefix(locs.size()) + OrgKjemi.signSuf[type-1];
					signStr = "-" + signStr;
				} else if(type == 9 || type == 10||type==3||type==4){
					signStr = (OrgKjemi.signSuf[type-1]);
				} else {
					signStr = returnNumPrefix(locs.size()) + OrgKjemi.signSuf[type-1];
				}
			}
		}		

	}
	public String retName(boolean yl) {
		//Builds the name and returns it, this requires the name to have been created or it goes all nullpointerexception
		String retStr = "";
		if(finBranchStr.size()>0) {
			largeName = true;
		}
		if(!multiBranch) {

			if(!oPre.equals("")) {
				retStr += oPre;
			}
			for(int i = 0; i<finBranchStr.size(); i++) {
				retStr = retStr+finBranchStr.get(i) + "-";
			}
			if(sPre != "" && largeName && main) {
				retStr = "(" + retStr + ")";
			}
			retStr = retStr+mainStr;
			if(!main && !yl) {
				retStr = retStr + "yl";
			}
			if(!bindStr.equals("an") || main) {
				retStr = retStr+bindStr;
			}

			retStr += signStr;
			if(!sPre.equals("")) {
				retStr = sPre + retStr;
			}
			name = retStr;
		} else {
			if(signConnection.text.startsWith("N")) {
				retStr = getExtName(signConnection, conPoint, 2);
				if(signConnection.c[signConnection.getSide(conPoint.pos, signConnection.pos)] == 2 && !mainGroup) {
					retStr += "imin";
				} else if (!mainGroup){
					retStr += "amin";
				}
			} else if (conPoint.tag.get(0).equals("ester")){
				retStr = getExtName(signConnection, conPoint, 0);
			} else if(!yl){
				retStr = getExtName(signConnection, conPoint, 1);
			} else {
				retStr = getExtName(signConnection, conPoint, 2);
			}
			name = retStr;
		}
		return retStr;
	}
	public String returnPrefix(int num) {
		//Returns prefixes specific to long chains of hydrocarbons
		String prefix = "";
		if(num<99) {
			if(num<11) {
				prefix = OrgKjemi.prefixC[num-1];
			} else if (num<14) {
				prefix = OrgKjemi.greekPref[num-10] + "dek";
			} else if (num<20) {
				prefix = OrgKjemi.prefixO[num-11] + "dek";
			} else if (num == 20) {
				prefix = "eicos";
			} else if (num<24) {
				prefix = OrgKjemi.greekPref[num-20] + OrgKjemi.greekBig[0];
			} else if (num<30) {
				prefix = OrgKjemi.prefixO[num-21] + OrgKjemi.greekBig[0];
			} else {
				if(num%10<4) {
					prefix = OrgKjemi.greekPref[num%10] + OrgKjemi.prefixO[(int)num/10] + "cont";
				} else {
					prefix = OrgKjemi.prefixO[num%10] + OrgKjemi.prefixO[(int)num/10] + "cont";
				}
			}
		} else {
			prefix = "(" + num + ")";
		}
		return prefix;
	}
	public String returnNumPrefix(int num) {
		//Returns prefixes specific to significant groups and others
		String prefix = "";
		if(num<99) {
			if(num<11) {
				prefix = OrgKjemi.prefixO[num-1];
			} else if (num<14) {
				prefix = OrgKjemi.greekPref[num-10] + "dek";
			} else if (num<20) {
				prefix = OrgKjemi.prefixO[num-11] + "dek";
			} else if (num == 20) {
				prefix = "eicos";
			} else if (num<24) {
				prefix = OrgKjemi.greekPref[num-20] + OrgKjemi.greekBig[0];
			} else if (num<30) {
				prefix = OrgKjemi.prefixO[num-21] + OrgKjemi.greekBig[0];
			} else {
				if(num%10<4) {
					prefix = OrgKjemi.greekPref[num%10] + OrgKjemi.prefixO[(int)num/10] + "cont";
				} else {
					prefix = OrgKjemi.prefixO[num%10] + OrgKjemi.prefixO[(int)num/10] + "cont";
				}
			}
		} else {
			prefix = "(" + num + ")";
		}
		return prefix;
	}
	public ArrayList<Integer> getBindLocation(int type) {
		//Returns the location of all double and triple bindings.
		ArrayList<Integer> locs = new ArrayList<Integer>();
		if (mainB.size() == 1 && !main) {
			if(mainB.get(0).c[mainB.get(0).getSide(conPoint.pos, mainB.get(0).pos)] == type) {
				locs.add(1);
			}
		}
		for(int i = 0; i<mainB.size()-1; i++) {
			for(int j = 0; j<mainB.get(i).con.size(); j++) {
				if(i == 0 && mainB.get(i).con.get(j) == conPoint) {
					if(mainB.get(i).c[mainB.get(i).getSide(conPoint.pos, mainB.get(i).pos)] == type) {
						locs.add(1);
					}
				}
				if(mainB.get(i).con.get(j) == mainB.get(i+1)) {

					if(mainB.get(i).c[mainB.get(i).getSide(mainB.get(i+1).pos, mainB.get(i).pos)] == type) {
						if(main) {
							locs.add(i+1);
						} else {
							locs.add(i+2);
						}
					}
				}
			}
		}
		return locs;
	}
	public ArrayList<Integer> getOLocation(String type) {
		//Returns the location of the type with the string "type"
		ArrayList<Integer> locs = new ArrayList<Integer>();
		for(int i = 0; i<mainB.size(); i++) {
			for(int j = 0; j<mainB.get(i).tag.size(); j++) {
				if(mainB.get(i).tag.get(j).equals(type)) {
					locs.add(i+1);
				}
			}
		}
		return locs;
	}
	public String getExtName(EntityField extC, EntityField con, int type) {
		String retstr = "";
		if(extC.con.size()>1) {
			String[] names = new String[extC.con.size()-1];
			int p = 0;
			for(int i = 0; i<extC.con.size(); i++) {
				if(extC.con.get(i) != con) {
					Branch b = new Branch(extC.con.get(i), extC);
					if(type == 1) {
						names[p] = b.retName(true)+"oxy";
						if(b.largeName){
							names[p] = "(" + names[p]+")";
						}
					} else if(type == 0){
						
						if(!mainGroup) {
							names[p] = b.retName(true);
					
							names[p] += "oxykarbonyl";
							if(b.largeName){
								names[p] = "(" + names[p]+")";
							}
						} else {
							names[p] = b.retName(true);
							if(b.largeName){
								names[p] = "(" + names[p]+")";
							}
						}
					} else {
						names[p] = b.retName(false);
						if(b.largeName){
							names[p] = "(" + names[p]+")";
						}
					}
					p++;
				}
			}
			ArrayList<String> finNames = new ArrayList<String>();
			ArrayList<Integer> finCnt = new ArrayList<Integer>();
			finNames.add(names[0]);
			finCnt.add(1);
			for(int i = 1; i<names.length; i++) {
				boolean added = false;
				for(int j = 0; j<finNames.size(); j++) {
					if(names[i].equals(finNames.get(j))&&!added) {
						finCnt.set(j, finCnt.get(j)+1);
						added = true;
					} else if (!added){
						finCnt.add(1);
						finNames.add(names[i]);
						added = true;
					}
				}
			}
			for(int i = 0; i<finNames.size(); i++) {
				retstr += returnNumPrefix(finCnt.get(i)) + finNames.get(i);
			}
		}
		return retstr;
	}
}
