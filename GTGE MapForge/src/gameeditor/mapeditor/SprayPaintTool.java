package gameeditor.mapeditor;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Point;
import com.golden.gamedev.util.Utility;
/**
 * This class uses a javax.swing Timer to randomly select tiles
 * and tile locations for a spray paint effect similar to MS Paint's.
 * 
 * Spray size, and specific tile selection are supported as well.  
 * Tiles can be selected singularly from the collective selected tiles,
 * or the selected tiles themselves can all be painted at the random
 * locations.
 * 
 * @author William Morrison
 */
public class SprayPaintTool implements ActionListener{
	MapPane mapPane;
	MapImagePane mapImgPane;
	Timer timer;
	private int spraySize=7;
	private boolean selectFromTiles;
	private static Point randomLocation=new Point();
	int delay=100;
	public SprayPaintTool(MapPane mp,MapImagePane imgp){
		mapPane=mp;
		mapImgPane=imgp;
		timer = new Timer(delay,this);
		timer.addActionListener(this);
	}
	private int getRandomTile(int[][] tiles){
		int x = Utility.getRandom(0,tiles[0].length-1);
		int y = Utility.getRandom(0,tiles.length-1);
		return tiles[y][x];
	}
	private Point getRandomLocation(){
		int[][] lay = mapPane.currentLayer;
		int mx=mapPane.mouseX;
		int my = mapPane.mouseY;
		int x = mx-spraySize>=0?mx-spraySize:0;
		int y = my-spraySize>=0?my-spraySize:0;
		int w=mx+spraySize<lay[0].length?x+spraySize:lay[0].length-1;
		int h=my+spraySize<lay.length?y+spraySize:lay.length-1;
		x=Utility.getRandom(x,w);
		y=Utility.getRandom(y,h);
		randomLocation.setLocation(x,y);
		return randomLocation;
	}
	/**
	 * Sets this SprayPaintTool's spray size (ie. the area to be considered when selecting
	 * random spray paint locations to paint to.)
	 * @param size
	 */
	public void setSpraySize(int size){
		spraySize=size;
	}
	/**
	 * Specifies exactly how tiles are painted. Selected tiles are painted
	 * randomly either singularly, or all together.
	 */
	public void setMultipleTileSpray(boolean b){
		selectFromTiles=b;
	}
	public void actionPerformed(ActionEvent e){
		if(selectFromTiles){
			Point p = getRandomLocation();
			mapPane.currentLayer[p.y][p.x]=getRandomTile(mapImgPane.selectedTiles);
		}
		else{
			mapPane.paintTiles(getRandomLocation());
		}
		mapPane.repaint();
	}
}