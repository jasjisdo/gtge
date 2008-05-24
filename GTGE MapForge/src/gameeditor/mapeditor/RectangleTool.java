package gameeditor.mapeditor;
import java.awt.*;
public class RectangleTool {
	Rectangle rect = new Rectangle();
	int[][] rectLayer;
	private int tilex=0,tiley=0;
	public void findRect(Point p1,Point p2){
		if(p1==null || p2==null)
			return;
		int x = Math.min(p2.x, p1.x);
		int y = Math.min(p2.y, p1.y);
		int w = Math.abs(p2.x - p1.x) + 1;
		int h = Math.abs(p2.y - p1.y) + 1;
		rect.setFrame(x,y,w,h);
	}
	public void drawCurrentRect(Graphics2D g,MapPane pane, MapImagePane mImgPane){
		g.setColor(Color.BLACK);
		tilex=0;
		tiley=0;
		g.drawRect(rect.x*32,rect.y*32, rect.width*32,rect.height*32);
		for(int i = 0;i<rect.height;i++){
			for(int j = 0;j<rect.width;j++){
				g.drawImage(pane.mapImg[mImgPane.selectedTiles[tiley][tilex]],(rect.x+j)*32,(rect.y+i)*32,null);
				tilex=tilex+1<mImgPane.selectedTiles[0].length?tilex+1:0;
			}
			tilex=0;
			tiley=tiley+1<mImgPane.selectedTiles.length?tiley+1:0;
		}
	}
	/**
	 * Places the current rectangle values into the current layer.  (In other words,
	 * finalizes the rectangle the user was resizing.)
	 */
	public void finalizeRect(MapPane pane, MapImagePane imgPane){
		tiley=0;
		tilex=0;
		for(int i = 0;i<rect.height;i++){
			for(int j = 0;j<rect.width;j++){
				pane.currentLayer[rect.y+i][rect.x+j]=imgPane.selectedTiles[tiley][tilex];
				tilex=tilex+1<imgPane.selectedTiles[0].length?tilex+1:0;
			}
			tilex=0;
			tiley=tiley+1<imgPane.selectedTiles.length?tiley+1:0;
		}
		pane.rectStart=null;
		rect.setFrame(0,0,0,0);
	}
}