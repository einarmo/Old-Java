import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.imageio.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainGraphics implements KeyListener {
	public static int currentDim;
	public Vector size = new Vector(100,100);
	public Vector initSize = new Vector (600, 600);
	JFrame frame;
	public static JPanel mainPanel;
	int tickDelay = 10;
	public static int scrollSpeed = 2;
	ArrayList<BufferedImage> graphics;
	Terrain[] terrain;
	public static boolean up, down, left, right = false;
	Tile[][] realGrid = new Tile[size.x][size.y];
	public static int tileSize = 40;
	boolean changed;
	public ArrayList<ArrayList<Tile>> mainGrid;
	public static JViewport view;
	public static Point point = new Point(tileSize*2, 0);
	public GridBagConstraints c;
	MainGraphics() {
		mainGrid = new ArrayList<ArrayList<Tile>>();
		graphics = new ArrayList<BufferedImage>();
		
		terrain = createTerrain();
		for(int i = 0; i<terrain.length; i++) {
			terrain[i].printOut();
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("resources/graphics/" + terrain[i].graphicsName));
				graphics.add(img);
				terrain[i].graphicsNum = graphics.size()-1;
			} catch (IOException e) {
				terrain[i].graphicsNum = -1;
			}
		}
		for(int i = 0; i<realGrid.length; i++) {
			for(int j = 0; j<realGrid[i].length; j++) {
				realGrid[i][j] = new Tile(this, new Vector(i,j));
			}
		}
		genTerrain();
		//TEMP
		for(int i = 0; i<realGrid.length; i++) {
			for(int j = 0; j<realGrid[i].length; j++) {
				if(realGrid[i][j].continent) {
					realGrid[i][j].setTerrain(terrain[0]);
				} else {
					realGrid[i][j].setTerrain(terrain[3]);
				}
			}
		}
		
		
		frame = new JFrame("testF");
		mainPanel = new JPanel();
		
		
		
		defMainGrid();
		c = new GridBagConstraints();
		mainPanel.setLayout(new GridBagLayout());
		for(int i = 0; i<mainGrid.size(); i++) {
			for(int j = 0; j<mainGrid.get(i).size(); j++) {
				c.gridx = j;
				c.gridy = i;
				mainPanel.add(mainGrid.get(i).get(j).b, c);
				//mainPanel.add(realGrid[i][j].b, c);
			}
		}

		//mainPanel.add(new JButton("test"));
		mainPanel.setBackground(Color.BLUE);

		view = new JViewport();
		view.setOpaque(false);
		view.setPreferredSize(new Dimension(initSize.x, initSize.y));
		view.setView(mainPanel);
		view.addKeyListener(this);
		view.setFocusable(true);
		view.setFocusTraversalPolicyProvider(true);
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MoveDispatcher());
		
		frame.add(view);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setViewPosition(point);
		ActionListener timerTask = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changed = false;
				if(MainGraphics.up&&!MainGraphics.down) {
					if(MainGraphics.point.y>scrollSpeed) {
						MainGraphics.point.y-=scrollSpeed;
					} else {
						MainGraphics.point.y = 0;
					}
					MainGraphics.view.setViewPosition(MainGraphics.point);
					changed = true;

				}
				if(MainGraphics.down&&!MainGraphics.up) {
					if(MainGraphics.point.y+view.getHeight()+scrollSpeed<MainGraphics.mainPanel.getHeight()) {
						MainGraphics.point.y+=scrollSpeed;
					} else {
						MainGraphics.point.y = MainGraphics.mainPanel.getHeight()-view.getHeight();
					}
					MainGraphics.view.setViewPosition(MainGraphics.point);
					changed = true;

				}
				if(MainGraphics.left&&!MainGraphics.right) {
					if(MainGraphics.point.x>scrollSpeed) {
						MainGraphics.point.x-=scrollSpeed;
					} else {
						MainGraphics.point.x = 0;
					}
					MainGraphics.view.setViewPosition(MainGraphics.point);
					changed = true;
				}
				if(MainGraphics.right&&!MainGraphics.left) {
					if(MainGraphics.point.x+view.getWidth()+scrollSpeed<MainGraphics.mainPanel.getWidth()) {
						MainGraphics.point.x+=scrollSpeed;
					} else {
						MainGraphics.point.x = MainGraphics.mainPanel.getWidth()-view.getWidth();
					}
					MainGraphics.view.setViewPosition(MainGraphics.point);
					changed = true;
				}
				if(changed) {
					if(point.x-view.getSize().width<=tileSize) {
						System.out.println("hei");
						for(int i = 0; i<mainGrid.get(0).size(); i++) {
							mainGrid.get(0).remove(i);
						}
						mainPanel.removeAll();
						for(int i = 0; i<mainGrid.size(); i++) {
							for(int j = 0; j<mainGrid.get(i).size(); j++) {
								c.gridx = i;
								c.gridy = j;
								mainPanel.add(mainGrid.get(i).get(j).b, c);
								//mainPanel.add(realGrid[i][j].b, c);
							}
						}
					}
				}
			}	
		};
		Timer timer = new Timer(tickDelay, timerTask);
		timer.start();
	}
	void defMainGrid() {
		Vector pCords = new Vector(point.x/tileSize, point.y/tileSize);
		for(int i = 0; i<initSize.y/tileSize+2; i++) {
			mainGrid.add(new ArrayList<Tile>());
			for(int j = 0; j<initSize.x/tileSize+4; j++) {
				//Vector nCords = new Vector((pCords.x-2+j)%size.x, (pCords.y-2+i)%size.y);
				Vector nCords = new Vector((pCords.x-2+j)%size.x, (pCords.y+i));
				if(nCords.x<0) {
					nCords.x += size.x;
				}
				mainGrid.get(i).add(realGrid[nCords.x][nCords.y]);
			}
		}
	}
	void createContinent(Vector sP, int contNum, double sizeMod, double stretchX, double stretchY) {
		//This is for the "pangea" style generator, other generators could be made in a similar fashion
		int tileCount = 0;
		int desiredTiles = (int) (sizeMod*(size.x*size.y*(Math.random()*0.01+0.2)));
		//sP = new Vector((int) Math.rint(Math.random()*(size.x-size.x*0.1)), (int) Math.rint(Math.random()*(size.y-size.y*0.1)));
		//sP.x += size.x*0.05;
		//sP.y += size.y*0.05;
		ArrayList<Tile> continentTiles = new ArrayList<Tile>();
		realGrid[sP.x][sP.y].continent = true;
		realGrid[sP.x][sP.y].contNum = contNum;
		continentTiles.add(realGrid[sP.x][sP.y]);
		tileCount++;
		//ArrayList<Tile> neigh = getAdjecent(realGrid[sP.x][sP.y]);
		//for(int i = 0; i<neigh.size(); i++) {
		//	continentTiles.add(neigh.get(i));
		//	neigh.get(i).continent = true;
		//	neigh.get(i).contNum = contNum;
		//	tileCount++;
		//}
		int slowLimit = (int) (desiredTiles*0.2);
		double probChange = 0;
		while (desiredTiles-tileCount>0) {
			double oldProbChange = probChange;

			ArrayList<Tile> tempContinentTiles = new ArrayList<Tile>();
			for(int i = 0; i<continentTiles.size(); i++) {
				double prob = probChange + (desiredTiles-tileCount-slowLimit)/((double)0.5*(desiredTiles));
				//System.out.println(prob + (" ") + desiredTiles + (" ") + tileCount + (" ") + slowLimit);
				ArrayList<Tile> tmpNeigh = getAdjecent(continentTiles.get(i));
				for(int j = 0; j<tmpNeigh.size(); j++) {
					double totalMod = 1.0;
					if(tmpNeigh.get(j).pos.y-continentTiles.get(i).pos.y != 0) {
						totalMod = totalMod*stretchY;
					}
					if(tmpNeigh.get(j).pos.x-continentTiles.get(i).pos.x != 0) {
						totalMod = totalMod*stretchX;
					}
					if(desiredTiles-tileCount<=0) {
						break;
					}
					if(tmpNeigh.get(j).continent == false && Math.random()<totalMod*(prob-tmpNeigh.get(j).countMod)) {
						tmpNeigh.get(j).continent = true;
						tmpNeigh.get(j).contNum = contNum;
						tileCount++;
						tempContinentTiles.add(tmpNeigh.get(j));
					} else if (tmpNeigh.get(j).continent == false) {
						tmpNeigh.get(j).countMod += 0.4;
					}
				}
			}
			//probChange = new Double(0);
			if (tempContinentTiles.size()>0) {
				continentTiles = tempContinentTiles;
			} else {
				for(int i = 0; i<realGrid.length; i++) {
					for(int j = 0; j<realGrid[i].length; j++) {
						realGrid[i][j].countMod = 0;
					}
				}
				probChange = oldProbChange + 0.1;
			}
			if(probChange>10 || tempContinentTiles.size()<4) {
				for(int i = 0; i<realGrid.length; i++) {
					for(int j = 0; j<realGrid[i].length; j++) {
						if(realGrid[i][j].continent == true && realGrid[i][j].contNum == contNum) {
							continentTiles.add(realGrid[i][j]);
							Collections.shuffle(continentTiles);
						}
					}
				}
			}
		}
	}
	void createTiles() {
		for(int i = 0; i<size.x; i++) {
			for(int j = 0; j<size.y; i++) {
				realGrid[i][j] = new Tile(this, new Vector(i,j));
			}
		}
	}
	void genTerrain() {
		createContinent(new Vector(80, 30), 1, 0.3, 1.0, 1.0);
		createContinent(new Vector(80, 50), 2, 0.3, 1.0, 1.0);
		createContinent(new Vector(25, 30), 3, 0.3, 1.0, 1.0);
		createContinent(new Vector(20, 50), 4, 0.3, 1.0, 1.0);
		createContinent(new Vector(50, 40), 5, 0.01, 1.0, 1.0);
		for(int i = 0; i<10; i++) {
			realGrid = smoothLoop(realGrid);
		}
	}
	public Tile[][] smoothLoop(Tile[][] grid) {
		for(int i = 0; i<grid.length; i++) {
			for(int j = 0; j<grid[i].length; j++) {
				ArrayList<Tile> neigh = getAdjecent(grid[i][j]);
				int contC = 0;
				for(int v = 0; v<neigh.size(); v++) {
					if (neigh.get(v).continent) {
						contC++;
					}
				}
				if((contC == 1||contC==2||contC==0) && grid[i][j].continent) {
					grid[i][j].continent = false;
				} else if ((contC == 8||contC==7) && grid[i][j].continent == false) {
					grid[i][j].continent = true;
				}
			}
		}
		return grid;
	}
	public ArrayList<Tile> getAdjecent(Tile t) {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for(int i = -1; i<2; i++) {
			for(int j = -1; j<2; j++) {
				if(!(i==0&&j==0)) {
					try {
						tiles.add(realGrid[(t.pos.x+i)%size.x][t.pos.y+j]);
					} catch (IndexOutOfBoundsException e) {
					}
				}
			}
		}
		return tiles;
	}
	Terrain[] createTerrain() {
		Scanner inFile = null;
		try {
			inFile = new Scanner(new File("resources/terrain"));
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		}
		inFile.useDelimiter("\n");
		ArrayList<String> strings = new ArrayList<String>();
		while(inFile.hasNext()) {
			strings.add(inFile.next());
		}
		inFile.close();
		String[] finalStr = new String[strings.size()];
		for(int i = 0; i<strings.size(); i++) {
			String[] tmp = strings.get(i).split(" ");
			tmp[0] = tmp[0].replaceAll("\\s", "");
			finalStr[i] = tmp[0];
			System.out.println(tmp[0]);
		}
		Terrain[] terrainTypes = new Terrain[strings.size() / 9];
		for(int i = 0; i<(finalStr.length/9); i++) {
			terrainTypes[i] = new Terrain(finalStr[9*i+1], finalStr[9*i], Integer.parseInt(finalStr[9*i+2]), 
					Boolean.parseBoolean(finalStr[9*i+3]), Boolean.parseBoolean(finalStr[9*i+4]), 
					new Yield(Integer.parseInt(finalStr[9*i+5]), Integer.parseInt(finalStr[9*i+6]), Integer.parseInt(finalStr[9*i+7]), 
							Integer.parseInt(finalStr[9*i+6])));
		}
		return terrainTypes;
	}
	private class MoveDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {
					MainGraphics.up = true;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					MainGraphics.down = true;
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					MainGraphics.right = true;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					MainGraphics.left = true;
				} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
					MainGraphics.scrollSpeed = 10;
				}
				
			} else if (e.getID() == KeyEvent.KEY_RELEASED) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {
					MainGraphics.up = false;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					MainGraphics.down = false;
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					MainGraphics.right = false;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					MainGraphics.left = false;
				} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
					MainGraphics.scrollSpeed = 2;
				}
			} else if (e.getID() == KeyEvent.KEY_TYPED) {
			}
			return false;
		}
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
}
