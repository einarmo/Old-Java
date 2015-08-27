import java.awt.image.BufferedImage;


public class Terrain {
	public BufferedImage graphics;
	public String name;
	public int cost;
	public boolean build, land;
	public Yield baseYield;
	public String graphicsName, baseType;
	public int graphicsNum;
	Terrain(String graphicsName, String name, int cost, boolean build, boolean land, Yield baseYield) {
		this.graphicsName = graphicsName;
		this.name = name;
		this.cost = cost;
		this.build = build;
		this.land = land;
		this.baseYield = baseYield;
	}
	public void printOut() {
		System.out.println(graphicsName + " " + name + " " + cost + " " + build + " " + land);
	}
}
