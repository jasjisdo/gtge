package gameeditor.mapeditor;
//JRE
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//GTGE
import com.golden.gamedev.object.background.abstraction.*;

/**
 * Used to display an AbstractTileBackground within a JLabel.  For best effect,
 * this component should be placed in a JScrollPane.  This component is draggable,
 * and automatically draws a square where the user is current pointing on the map.
 * 
 * @author William Morrison
 */
public class TileMapComponent extends JLabel implements MouseMotionListener,Scrollable{
	protected AbstractTileBackground map;
	private int mouseX,mouseY;
	private static Point tileAt=new Point();
	/**
	 * Amount this label is shifted on the horizontal axis when dragged.
	 */
	protected int dragXInc;
	/**
	 * Amount this label is shifted on the vertical axis dragged.
	 */
	protected int dragYInc;
	public TileMapComponent(AbstractTileBackground map){
		setMap(map);
		mouseX=mouseY=-100;
		setPreferredSize(new Dimension(map.getWidth(),map.getHeight()));
		addMouseMotionListener(this);
		setAutoscrolls(true);
	}
	public void setMap(AbstractTileBackground map){
		this.map=map;
		dragXInc=map.getTileWidth();
		dragYInc=map.getTileHeight();
	}
	/**
	 * Sets the amount this label is shifted on the X-Axis when dragged.
	 * @param inc new horizontal shift amount
	 */
	public void setHorizontalDrag(int inc){
		dragXInc=inc;
	}
	/**
	 * Sets the amount this label is shifted on the Y-Axis when dragged.
	 * @param inc new vertical shift amount
	 */
	public void setVerticalDrag(int inc){
		dragYInc=inc;
	}
	/**
	 * Returns the current horizontal shift amount for when this label is dragged.
	 * @return current horizontal shift amount
	 */
	public int getHorizontalDrag(){
		return dragXInc;
	}
	/**
	 * Returns the current vertical shift amount for when this label is dragged.
	 * @return current vertical shift amount
	 */
	public int getVerticalDrag(){
		return dragYInc;
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return orientation==SwingConstants.HORIZONTAL?visibleRect.width-map.getTileWidth():
			visibleRect.height-map.getTileHeight();
	}
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// Get the current position.
		int currentPosition = orientation==SwingConstants.HORIZONTAL?
				visibleRect.x:visibleRect.y;
		int inc = orientation==SwingConstants.HORIZONTAL?
				dragXInc:dragYInc;
		// Return the number of pixels between currentPosition
		// and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition - (currentPosition/inc) *inc;
			return (newPosition == 0) ? inc : newPosition;
		} else {
			return ((currentPosition / inc) + 1) * inc - currentPosition;
		}
	}
	/**
	 * Convenience method for drawing the cursor's position on map.
	 * Equivalent to calling drawTileRect(p.x,p.y,g)
	 * @param p some point
	 * @param g graphics object rendering rectangle
	 */
	public void drawTileRect(Point p,Graphics2D g){
		drawTileRect(p.x,p.y,g);
	}
	/**
	 * Draws a simple rectangle with dimension equal to the
	 * current tile width and height.  This outlines the current tile.
	 * @param x upper left location of rectangle
	 * @param y upper left y location of rectangle
	 * @param g graphics object rendering rectangle
	 */
	public void drawTileRect(int x, int y,Graphics2D g){
		g.fillRect(x,y, map.getTileWidth()-1,map.getTileHeight()-1);
	}
	/**
	 * Returns the current tile relative to some point.
	 * Equivalent to calling tileAt(p.x,p.y)
	 * @param p some point
	 * @return a point location of the current tile
	 */
	public Point tileAt(Point p){
		return tileAt(p.x,p.y);
	}
	/**
	 * Returns the current tile x and y locations relative to an x and y location.
	 * @param x x location
	 * @param y y location
	 * @return the x and y location of the current tile
	 */
	public Point tileAt(double x, double y) {
		Rectangle clip =map.getClip();
		if (x < clip.x || x > clip.x + clip.width
				|| y < clip.y || y > clip.y + clip.height)
			return null;
		tileAt.x = (int) (map.getX() + x - clip.x) / map.getTileWidth();
		tileAt.y = (int) (map.getY() + y - clip.y) / map.getTileHeight();
		return tileAt;
	}
	public void mouseMoved(MouseEvent e){
		Point p=null;
		if((p=tileAt(e.getPoint()))!=null){
			if(mouseX!=p.x || mouseY!=p.y){
				mouseX=p.x;
				mouseY=p.y;
				repaint();
			}
		}
	}
	public void mouseDragged(MouseEvent e){	
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
	}
	public void paintComponent(Graphics g){
		Graphics2D g2d= (Graphics2D)g;
		map.render(g2d);
		g2d.setColor(Color.RED);
		drawTileRect(mouseX*32,mouseY*32,g2d);
	}
}