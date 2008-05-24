package gameeditor.mapeditor;

public class FloodFillPoint {
	int tileX, tileY, x, y;
	public FloodFillPoint(int x, int y, int tileX, int tileY) {
		this.x = x;
		this.y = y;
		this.tileX = tileX;
		this.tileY = tileY;
	}
	// For debugging
	public String toString() {
		return "FloodFillPoint: x = " + x + " y = " + y + " tileX = "
				+ tileX + " tileY = " + tileY;
	}
}
