import java.util.ArrayList;


public class CalcLongest {
	ArrayList<ArrayList<ArrayList<EntityField>>> points;
	CalcLongest() {
	}
	public ArrayList<EntityField> getMain(EntityField[][] bList) { //Returns the main branch from the field grid
		ArrayList<EntityField> endPoints = new ArrayList<EntityField>();
		for(int i = 0; i<bList.length; i++) {
			for(int j = 0; j<bList[0].length; j++) {
				if (bList[i][j].isEndpoint()) {
					endPoints.add(bList[i][j]);
				}
			}
		}
		this.points= new ArrayList<ArrayList<ArrayList<EntityField>>>();
		ArrayList<EntityField> mainBranch;
		if(endPoints.size() != 1) {
			for(int i = 0; i<endPoints.size(); i++) {
				ArrayList<ArrayList<EntityField>> paths = new ArrayList<ArrayList<EntityField>>();
				ArrayList<EntityField> newList = new ArrayList<EntityField>();
				newList.add(endPoints.get(i));
				paths.add(calcBranch(paths, newList, newList.get(0).con.get(0), endPoints.get(i)));
				points.add(paths);
			}
			mainBranch = getMainBranch();
		} else {
			mainBranch = new ArrayList<EntityField>();
			mainBranch.add(endPoints.get(0));
		}
		return mainBranch;
	}
	public ArrayList<EntityField> getBranch(EntityField begin, EntityField last, boolean other) { //Returns a branch where entry number 0 is last and entry 
		//number 1 is begin
		//This finds all possible branches and begins the getBestB method for getting the best Branch. 
		ArrayList<ArrayList<EntityField>> paths = new ArrayList<ArrayList<EntityField>>();
		ArrayList<EntityField> newList = new ArrayList<EntityField>();
		if(!other) {
			newList.add(last);
		}
		newList.add(begin);
		paths.add(calcBranch(paths, newList, begin, last));

		return getBestB(paths);
	}
	public ArrayList<EntityField> getMainBranch() {
		ArrayList<ArrayList<EntityField>> paths = new ArrayList<ArrayList<EntityField>>();
		for(int i = 0; i<points.size(); i++) {
			for(int j = 0; j<points.get(i).size(); j++) {
				paths.add(points.get(i).get(j));
			}
		}
		return getBestB(paths);
	}
	public ArrayList<EntityField> getBestB(ArrayList<ArrayList<EntityField>> paths) {
		//This method identifies the best branch out of the longest branches according to the rules of nomenclature in organic chemistry.
		//First it identifies the type and finds the branches with the most entries of the corresponding tag.
		int mainType = 0;
		String tmpType = "";
		ArrayList<ArrayList<EntityField>> signPaths = new ArrayList<ArrayList<EntityField>>();
		for(int i = 0; i<paths.size(); i++) {
			boolean added = false;
			if(mainType == 0) {
				if(!added) {
					signPaths.add(paths.get(i));
					added = true;
				}
			}
			for(int j = 0; j<paths.get(i).size(); j++) {
				paths.get(i).get(j).checkTag(paths.get(i).get(j));
				for(int v = 0; v<paths.get(i).get(j).tag.size(); v++) {
					for(int w = 0; w<OrgKjemi.types.length-2; w++) {
						if(paths.get(i).get(j).tag.get(v).equals(OrgKjemi.types[w])) {
							if(mainType<w+1) {
								mainType = w+1;
								tmpType = OrgKjemi.types[w];
								signPaths.clear();
								signPaths.add(paths.get(i));
								added = true;
							} else if (mainType == w+1) {
								if(!added) {
									signPaths.add(paths.get(i));
									added = true;
								}
							}
						} 
					}
				}
			}
		}

		int[] oPoints = new int[signPaths.size()];
		for(int i = 0; i<signPaths.size(); i++) {
			int points = 0;
			for(int j = 0; j<signPaths.get(i).size(); j++) {
				for(int v = 0; v<signPaths.get(i).get(j).tag.size(); v++) {
					if(signPaths.get(i).get(j).tag.get(v).equals(tmpType)) {
						points ++;
					}
				}
			}
			oPoints[i] = points;
		}
		int highestVal = 0;
		for(int i = 0; i<oPoints.length; i++) {
			if(highestVal<oPoints[i]) {
				highestVal = oPoints[i];
			}
		}
		ArrayList<ArrayList<EntityField>> bestPaths = new ArrayList<ArrayList<EntityField>>();
		for(int i = 0; i<signPaths.size(); i++) {
			if(oPoints[i] == highestVal) {
				bestPaths.add(signPaths.get(i));
			}
		}
		//Secondly it creates an array of integers that represent how ideal the branch is in regards to double and triple bindings.
		int[] mP = new int[bestPaths.size()];
		for(int i = 0; i<bestPaths.size(); i++) {
			int multiPoints = 0;
			for(int j = 0; j<bestPaths.get(i).size(); j++) {
				if(bestPaths.get(i).size()-1 != j || (j == 0 && bestPaths.get(i).size()>1)) { 
					if (bestPaths.get(i).get(j).c[bestPaths.get(i).get(j).getSide(bestPaths.get(i).get(j+1).pos, bestPaths.get(i).get(j).pos)]==2) {
						multiPoints += (bestPaths.get(i).size()-j)*1000; 
					} else if (bestPaths.get(i).get(j).c[bestPaths.get(i).get(j).getSide(bestPaths.get(i).get(j+1).pos, bestPaths.get(i).get(j).pos)]==3) {
						multiPoints += (bestPaths.get(i).size()-j)*1001;
					}
				}
			}
			mP[i] = multiPoints;
		}
		//The next two loops identify the ideal paths in regards to double and triple bindings.
		ArrayList<ArrayList<EntityField>> mostCon = new ArrayList<ArrayList<EntityField>>();
		highestVal = 0;
		for(int i = 0; i<mP.length; i++) {
			if(mP[i]>highestVal) {
				highestVal = mP[i];
			}
		}
		
		for(int i = 0; i<bestPaths.size(); i++) {
			if(mP[i] == highestVal) {
				mostCon.add(bestPaths.get(i));
			}
		}
		//Next it identifies the longest of the remaining branches
		highestVal = -1;
		for(int i = 0; i<mostCon.size(); i++) {
			if(highestVal<mostCon.get(i).size()) {
				highestVal = mostCon.get(i).size();
			}
		}
		ArrayList<ArrayList<EntityField>> longest = new ArrayList<ArrayList<EntityField>>();
		for(int i = 0; i<mostCon.size(); i++) {
			if(mostCon.get(i).size() == highestVal) {
				longest.add(mostCon.get(i));
			}
		}
		oPoints = new int[longest.size()];
		for(int i = 0; i<longest.size(); i++) {
			int points = 0;
			for(int j = 0; j<longest.get(i).size(); j++) {
				for(int v = 0; v<longest.get(i).get(j).tag.size(); v++) {
					if(longest.get(i).get(j).tag.get(v).equals(tmpType)) {
						points += longest.get(i).size()-j;
					}
				}
			}
			oPoints[i] = points;
		}
		highestVal = 0;
		for(int i = 0; i<oPoints.length; i++) {
			if(highestVal<oPoints[i]) {
				highestVal = oPoints[i];
			}
		}
		ArrayList<ArrayList<EntityField>> bestSignPaths = new ArrayList<ArrayList<EntityField>>();
		for(int i = 0; i<longest.size(); i++) {
			if(oPoints[i] == highestVal) {
				bestSignPaths.add(longest.get(i));
			}
		}
		//The next three loops identify which loops are ideal in regards to branch position, in the future, this might use branch size.
		int[] pathP = new int[bestSignPaths.size()];
		for(int i = 0; i<bestSignPaths.size(); i++) {
			int multiPoints = 0;
			for(int j = 0; j<bestSignPaths.get(i).size(); j++) {
				multiPoints += bestSignPaths.get(i).get(j).getBranches()*(bestSignPaths.get(i).size()-j);
			}
			pathP[i] = multiPoints;
		}
		ArrayList<ArrayList<EntityField>> bestCand = new ArrayList<ArrayList<EntityField>>();
		highestVal = -1;
		for(int i = 0; i<pathP.length; i++) {
			if(pathP[i]>highestVal) {
				highestVal = pathP[i];
			}
		}

		for(int i = 0; i<bestSignPaths.size(); i++) {
			if(pathP[i] == highestVal) {
				bestCand.add(bestSignPaths.get(i));
			}
		}
		ArrayList<EntityField> finalBranch = bestCand.get(0);
		
		return finalBranch;
	}
	public ArrayList<EntityField> calcBranch(ArrayList<ArrayList<EntityField>> paths, 
			ArrayList<EntityField> init, EntityField start, EntityField last) {
		//this is the main method for calculating the longest branch. It creates an array of paths which all originate in the point "start"
		//It is recursive and calls itself if the path it is following splits.
		while(true) {
			EntityField[] next = returnNext(init.get(init.size()-1), last);
			if(next.length>0) {
				for(int i = 1; i<next.length; i++) {
					if((next[i].text.equals("CH")||next[i].text.equals("C"))) {
						@SuppressWarnings("unchecked")
						ArrayList<EntityField> temp = (ArrayList<EntityField>)init.clone();
						temp.add(next[i]);
						paths.add(calcBranch(paths, temp, next[i], init.get(init.size()-1)));
					}
				}
			} else {
				return init;
			}
			last = init.get(init.size()-1);
			init.add(next[0]);
		}
	}
	public EntityField[] returnNext(EntityField cur, EntityField last) {
		//Returns possible next EntityFields that are connected to the field cur and are not the field last
		int numRet = 0;
		for(int i = 0; i<cur.con.size(); i++) {
			if(cur.con.get(i) != last && (cur.con.get(i).text.equals("CH") || cur.con.get(i).text.equals("C"))) {
				numRet++;
			}
		}
		EntityField[] next = new EntityField[numRet];
		int cnt = 0;
		for(int i = 0; i<cur.con.size(); i++) {
			if(cur.con.get(i) != last && (cur.con.get(i).text.equals("CH") || cur.con.get(i).text.equals("C"))) {
				next[cnt] = cur.con.get(i);
				cnt++;
			}
		}
		return next;
	}
}
