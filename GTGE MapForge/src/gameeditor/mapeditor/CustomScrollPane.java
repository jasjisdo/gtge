package gameeditor.mapeditor;
import javax.swing.*;

import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.*;

import com.golden.gamedev.object.background.abstraction.AbstractTileBackground;
/**
 * An easily customizable JScrollPane for a TileMapComponent.  Allows the map component
 * to be displayed easily.  This class isn't actually implemented in the mapeditor package
 * itself, but could be rather easily.  Works by dynamically adjusting the AbstractTileMap's
 * clip whenever the JScrollPane is resized.
 * @author William Morrison
 *
 */
public class CustomScrollPane extends JScrollPane implements  AdjustmentListener{
	TileMapComponent tileMap;
	int horizonShift;
	int verticalShift;
	public CustomScrollPane(){
		this(null);
	}
	/**
	 * Creates a new CustomScrollPane.
	 */
	public CustomScrollPane(TileMapComponent tmComp){
		super(tmComp);
		setMap(tmComp);
		getHorizontalScrollBar().addAdjustmentListener(this);
		getVerticalScrollBar().addAdjustmentListener(this);
		setAutoscrolls(true);
	}
	public void setMap(TileMapComponent tmComp){
		this.tileMap=tmComp;
		getHorizontalScrollBar().setBlockIncrement(
				horizonShift=tileMap.map.getTileWidth());
		getVerticalScrollBar().setBlockIncrement(
				verticalShift=tileMap.map.getTileHeight());
	}
	public void setHorizontalBlockIncrement(int inc){
		getHorizontalScrollBar().setBlockIncrement(
				horizonShift=inc);
	}
	public void setVerticalBlockIncrement(int inc){
		getVerticalScrollBar().setBlockIncrement(
				verticalShift=inc);
	}
	public int getHorizontalBlockIncrement(){
		return horizonShift;
	}
	public int getVerticalBlockIncrement(){
		return verticalShift;
	}
	/**
	 * Overidden method from interface AdjustmentListener.
	 * 
	 * This method works by first calculating the current viewport
	 * from horizontal and vertical bar values.  This gives us our 
	 * upper left coordinate.  Next the viewport width is calculated 
	 * ensuring its less than or equal to mapWidth.  If it isn't, viewport
	 * width is equal to the actual viewport width.  Height for the viewport
	 * is calculated in the same way.  
	 * 
	 *  All this ensures our JScrollPane's bars are in proportion to what the map's displaying.
	 *  Next we set the center of the viewport as the center of our map so that when
	 *  calling map.render, only visible tiles are rendered.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e){
		Rectangle seeRect = getVisibleRect();
		JScrollBar horizBar = getHorizontalScrollBar();
		JScrollBar vertBar = getVerticalScrollBar();
		int x = horizBar.getValue();
		int y = vertBar.getValue();
		int w = seeRect.width;
		int h = seeRect.height;
		AbstractTileBackground map = tileMap.map;
		w = w + x + horizonShift > map.getWidth() ? map.getWidth() - x : w;
		h = h + y + verticalShift > map.getHeight() ? map.getHeight() - y : h;
		Rectangle rect = new Rectangle(x, y, w, h);
		map.setClip(x, y, w, h);
		w=tileMap.map.getTileWidth();
		h=tileMap.map.getTileHeight();
		map.setToCenter((int) rect.getCenterX() - w/2,
				(int) rect.getCenterY() - h/2, w,h);
	}
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		} else {
			currentPosition = visibleRect.y;
		}
		int inc = orientation == SwingConstants.HORIZONTAL?tileMap.map.getTileWidth():
			tileMap.map.getTileHeight();
		// Return the number of pixels between currentPosition
		// and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition - (currentPosition / inc) *inc;
			return (newPosition == 0) ? inc : newPosition;
		} else {
			return ((currentPosition / inc) + 1) * inc - currentPosition;
		}
	}
}
