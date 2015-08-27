
public class Tile {
	MainGraphics g;
	Vector pos;
	TileButton b;
	public boolean continent = false;
	Terrain terrain;
	int contNum = 0;
	public double countMod = 0.0;
	Tile(MainGraphics g, Vector pos) {
		this.g = g;
		this.pos = pos;
		this.b = new TileButton(this);
	}
	public void setTerrain(Terrain ter) {
		this.terrain = ter;
		if(ter.graphicsNum != -1) {
			b.setImage(g.graphics.get(ter.graphicsNum));
		}
	}
}
