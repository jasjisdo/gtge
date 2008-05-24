package gameeditor.mapeditor;
import java.util.ArrayList;
import java.util.Stack;
public class FloodFillTool {
	/**
	 * This class implements a flood fill algorithm on an array.
	 * The algorithm has been optimized, though extensive review proved
	 * an earlier algorithm I had to be simplier and just as efficient- I
	 * didn't have time to correct the code.
	 * 
	 * The "fill" function returns an array list for an undo feature I was working on.
	 * 
	 * @author William Morrison
	 */
	MapEditorGUI editor;
	public FloodFillTool(MapEditorGUI editor){
		this.editor=editor;
	}
	public ArrayList<FloodFillPoint> fill(int x, int y, int prevTile) {
		FloodFillPoint p = new FloodFillPoint(x, y, 0, 0);
		Stack<FloodFillPoint> stack = new Stack<FloodFillPoint>();
		ArrayList<FloodFillPoint> aryList=new ArrayList<FloodFillPoint>();
		stack.push(p);
		int tileX = 0;
		int tileY = 0;
		int[][] tiles = editor.mapImagePane.selectedTiles;
		int[][] currentLayer = editor.mapPane.currentLayer;
		boolean hasLeft,hasRight,hasTop,hasBottom;
		int xR, xL, yD, yU;
		while (!stack.isEmpty()) {
			p = stack.pop();
			aryList.add(new FloodFillPoint(p.x,p.y,prevTile,-1));
			tileX = p.tileX;
			tileY = p.tileY;
			int selected;
			currentLayer[p.y][p.x] = selected=tiles[tileY][tileX];
			hasLeft = p.x > 0 && currentLayer[p.y][p.x-1] == prevTile;
			hasRight = p.x < currentLayer[0].length-1 && currentLayer[p.y][p.x+1] == prevTile;
			hasTop = p.y > 0 && currentLayer[p.y-1][p.x] == prevTile;
			hasBottom = p.y < currentLayer.length-1 && currentLayer[p.y+1][p.x] == prevTile;
			xR = tileX + 1 < tiles[0].length ? tileX + 1 : 0;
			xL = tileX - 1 >= 0 ? tileX - 1 : tiles[0].length - 1;
			yD = tileY + 1 < tiles.length ? tileY + 1 : 0;
			yU = tileY - 1 >= 0 ? tileY - 1 : tiles.length - 1;
			if (hasLeft) {
				currentLayer[p.y][p.x-1] = selected;
				if (hasRight) {
					currentLayer[p.y][p.x+1] = selected;
					if (hasTop) {
						currentLayer[p.y-1][p.x] = selected;
						if (hasBottom) {
							currentLayer[p.y+1][p.x] = selected;
							stack.push(new FloodFillPoint(p.x-1, p.y,xL,tileY));
							stack.push(new FloodFillPoint(p.x+1, p.y,xR,tileY));
							stack.push(new FloodFillPoint(p.x, p.y-1,tileX,yU));
							stack.push(new FloodFillPoint(p.x, p.y+1,tileX,yD));
						} else {
							stack.push(new FloodFillPoint(p.x-1, p.y,xL,tileY));
							stack.push(new FloodFillPoint(p.x+1, p.y,xR,tileY));
							stack.push(new FloodFillPoint(p.x, p.y-1,tileX,yU));
						}
					} else {
						if (hasBottom) {
							currentLayer[p.y+1][p.x] = selected;
							stack.push(new FloodFillPoint(p.x-1, p.y,xL,tileY));
							stack.push(new FloodFillPoint(p.x+1, p.y,xR,tileY));
							stack.push(new FloodFillPoint(p.x, p.y+1,tileX,yD));
						} else {
							stack.push(new FloodFillPoint(p.x-1, p.y,xL,tileY));
							stack.push(new FloodFillPoint(p.x+1, p.y,xR,tileY));
						}
					}
				} else {
					if (hasTop) {
						currentLayer[p.y-1][p.x] = selected;
						if (hasBottom) {
							currentLayer[p.y+1][p.x] = selected;
							stack.push(new FloodFillPoint(p.x-1, p.y,xL,tileY));
							stack.push(new FloodFillPoint(p.x, p.y-1,tileX,yU));
							stack.push(new FloodFillPoint(p.x, p.y+1,tileX,yD));
						} else {
							stack.push(new FloodFillPoint(p.x-1, p.y,xL,tileY));
							stack.push(new FloodFillPoint(p.x, p.y-1,tileX,yU));
						}
					} else {
						if (hasBottom) {
							currentLayer[p.y+1][p.x] = selected;
							stack.push(new FloodFillPoint(p.x-1, p.y,xL,tileY));
							stack.push(new FloodFillPoint(p.x, p.y+1,tileX,yD));
						} else {
							stack.push(new FloodFillPoint(p.x-1, p.y,xL,tileY));
						}
					}
				}
			} else {
				if (hasRight) {
					currentLayer[p.y][p.x+1] = selected;
					if (hasTop) {
						currentLayer[p.y-1][p.x] = selected;
						if (hasBottom) {
							currentLayer[p.y+1][p.x] = selected;
							stack.push(new FloodFillPoint(p.x+1, p.y,xR,tileY));
							stack.push(new FloodFillPoint(p.x, p.y-1,tileX,yU));
							stack.push(new FloodFillPoint(p.x, p.y+1,tileX,yD));
						} else {
							stack.push(new FloodFillPoint(p.x+1, p.y,xR,tileY));
							stack.push(new FloodFillPoint(p.x, p.y-1,tileX,yU));
						}
					} else {
						if (hasBottom) {
							currentLayer[p.y+1][p.x] = selected;
							stack.push(new FloodFillPoint(p.x+1, p.y,xR,tileY));
							stack.push(new FloodFillPoint(p.x, p.y+1,tileX,yD));
						} else {
							stack.push(new FloodFillPoint(p.x+1, p.y,xR,tileY));
						}
					}
				} else {
					if (hasTop) {
						currentLayer[p.y-1][p.x] = selected;
						if (hasBottom) {
							currentLayer[p.y+1][p.x] = selected;
							stack.push(new FloodFillPoint(p.x, p.y-1,tileX,yU));
							stack.push(new FloodFillPoint(p.x, p.y+1,tileX,yD));
						} else {
							stack.push(new FloodFillPoint(p.x, p.y-1,tileX,yU));
						}
					} else {
						if (hasBottom) {
							currentLayer[p.y+1][p.x] = selected;
							stack.push(new FloodFillPoint(p.x, p.y+1,tileX,yD));
						}
					}
				}
			}
		}
		return aryList;
	}
}
