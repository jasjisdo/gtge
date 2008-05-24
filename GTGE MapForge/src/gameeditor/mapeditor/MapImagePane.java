package gameeditor.mapeditor;
//JRE
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

//GTGE
import com.golden.gamedev.util.ImageUtil;
public class MapImagePane extends JLabel implements MouseListener,MouseMotionListener{
	/**
	 * This class handles drawing the current map's image.
	 * Multiple tile selection is done via determineSelectedTiles()
	 * for those curious few.
	 * 
	 * @author William Morrison
	 */
	int r, c;
	private BufferedImage mapImg;
	int tileX=-1, tileY=-1;
	MapPane mapPane;
	Point dragStart=null,dragEnd=null;
	Rectangle dragRect=new Rectangle(),oldDragRect=new Rectangle();
	int[][] selectedTiles;
	static Stroke bigStroke,smallStroke,oldStroke;
	static{
		bigStroke=new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
		smallStroke=new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
		oldStroke=new BasicStroke(1f);
	}
	public MapImagePane(MapPane mp){
		mapPane = mp;
		setImage(mapPane.fullMapImg);
		addMouseListener(this);
		addMouseMotionListener(this);
		oldDragRect=new Rectangle(0,0,0,0);
		selectedTiles=new int[][]{{0},{0}};
	}
	public void setSelectedTiles(int[][] tiles){
		int x=tiles[0][0]%c;
		int y=tiles[0][0]/c;
		dragRect.setFrame(x,y, tiles[0].length, tiles.length);
		selectedTiles=tiles;
	}
	public int getTileX(){
		return tileX;
	}
	public int getTileY(){
		return tileY;
	}
	public void setImage(BufferedImage img){
		this.mapImg=img;
		c=img.getWidth()/32;
		r=img.getHeight()/32;
		setPreferredSize(new Dimension(c*32,r*32));
		tileX=0;tileY=0;
		repaint();
	}
	public void paintComponent(Graphics g){
		//draw the image
		g.drawImage(mapImg,0,0,null);
		//bound the selected tiles with specified stroke
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.BLACK);
		if(dragRect.equals(oldDragRect))
			return;
		g2d.setStroke(bigStroke);
		g2d.drawRoundRect(dragRect.x*32,dragRect.y*32,dragRect.width*32-1,dragRect.height*32-1,10,10);
		g2d.setColor(Color.WHITE);
		g2d.setStroke(smallStroke);
		g2d.drawRoundRect(dragRect.x*32,dragRect.y*32,dragRect.width*32-1, dragRect.height*32-1,10,10);
		g2d.setStroke(oldStroke);
	}
	public void determineSelectedTiles(){
		int x=dragRect.x;
		int y=dragRect.y;
		int w=dragRect.width;
		int h=dragRect.height;
		selectedTiles=new int[h][w];
		for(int i = y;i<y+h;i++){
			for(int j = x;j<x+w;j++)
				selectedTiles[i-y][j-x]=i*c+j;	
		}
	}
	public Point getPoint(Point p){
		int x = p.x/32;
		int y = p.y/32;
		x=x>=c?c-1:x;
		y=y>=r?r-1:y;
		x=x<0?0:x;
		y=y<0?0:y;
		return new Point(x,y);
	}
	public void mouseMoved(MouseEvent e){}
	public void setDragRect(Point p1,Point p2){
		int x = Math.min(p2.x,p1.x);
		int y = Math.min(p2.y,p1.y);
		int w=Math.abs(p2.x-p1.x)+1;
		int h=Math.abs(p2.y-p1.y)+1;
		dragRect.setFrame(x,y,w,h);
	}
	public void mouseDragged(MouseEvent e){
		dragEnd=getPoint(e.getPoint());
		int x = Math.min(dragEnd.x, dragStart.x);
		int y = Math.min(dragEnd.y,dragStart.y);
		int w=Math.abs(dragEnd.x-dragStart.x)+1;
		int h=Math.abs(dragEnd.y-dragStart.y)+1;
		dragRect=new Rectangle(x,y,w,h);
		repaint();
	}
	public void mouseClicked(MouseEvent e){
		Point p = e.getPoint();
		int oldx=tileX;
		int oldy=tileY;
		tileX = p.x/32;
		tileY = p.y/32;
		if(oldx==tileX && oldy==tileY)
			return;
		if(tileX>=c)
			tileX=c-1;
		if(tileY>=r)
			tileY=r-1;
		mapPane.setSelectedTiles(new int[][]{{tileY*c+tileX}});
		repaint();
	}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){
		dragStart=getPoint(e.getPoint());
		dragEnd=getPoint(e.getPoint());
		setDragRect(dragStart,dragEnd);
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
}