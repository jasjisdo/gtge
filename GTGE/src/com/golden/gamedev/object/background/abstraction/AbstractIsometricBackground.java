/*
 * Copyright (c) 2008 Golden T Studios.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.golden.gamedev.object.background.abstraction;

// JFC
import java.awt.Graphics2D;
import java.awt.Point;

import com.golden.gamedev.object.Background;

/**
 * The base abstract class to create isometric background, the subclass need to
 * perform the background tile rendering.
 */
public abstract class AbstractIsometricBackground extends Background {
	
	private int tileWidth, baseTileHeight;
	private int halfTileWidth, halfTileHeight; // half of the iso tile size
	// the tile incremental position
	// is using this value
	private int offsetTileHeight; // tile height offset from base tile height
	private int startX, startY; // starting top x, y coordinate
	        
	private int tileX, tileY;
	
	private int horiz, vert; // total horizontal, vertical tiles
	        
	private Point point1 = new Point(); // return value for getTileAt(...)
	private Point point2 = new Point(); // return value for getCoordinateAt(...)
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>AbstractIsometricBackground</code> as big as
	 * <code>horiz</code>, <code>vert</code> tiles, where each tile is as
	 * big as <code>tileWidth</code>, <code>tileHeight</code> with
	 * specified tile height offset, and starting y coordinate.
	 * 
	 * @param horiz total horizontal tiles
	 * @param vert total vertical tiles
	 * @param tileWidth the width of the iso tile
	 * @param tileHeight the height of the iso tile
	 * @param offsetTileHeight the tile height offset from the base tile height
	 * @param startY starting y coordinate to draw this background
	 */
	public AbstractIsometricBackground(int horiz, int vert, int tileWidth,
	        int tileHeight, int offsetTileHeight, int startY) {
		super((horiz + vert) * (tileWidth / 2),
		        ((horiz + vert) * ((tileHeight - offsetTileHeight) / 2))
		                + startY);
		
		this.tileWidth = tileWidth;
		this.baseTileHeight = tileHeight - offsetTileHeight;
		this.offsetTileHeight = offsetTileHeight;
		
		this.halfTileWidth = tileWidth / 2;
		this.halfTileHeight = this.baseTileHeight / 2;
		
		this.horiz = horiz;
		this.vert = vert;
		
		this.startX = (vert - 1) * this.halfTileWidth; // starting x depends on
		// vertical tiles
		this.startY = startY;
		
		this.tileX = this.tileY = 0;
	}
	
	/**
	 * Creates new <code>AbstractIsometricBackground</code> as big as
	 * <code>horiz</code>, <code>vert</code> tiles, where each tile is as
	 * big as <code>tileWidth</code>, <code>tileHeight</code>.
	 * 
	 * @param horiz total horizontal tiles
	 * @param vert total vertical tiles
	 * @param tileWidth the width of the iso tile
	 * @param tileHeight the height of the iso tile
	 */
	public AbstractIsometricBackground(int horiz, int vert, int tileWidth,
	        int tileHeight) {
		this(horiz, vert, tileWidth, tileHeight, 0, 0);
	}
	
	/** ************************************************************************* */
	/** ************************ RENDER BACKGROUND ****************************** */
	/** ************************************************************************* */
	
	public void render(Graphics2D g, int xbg, int ybg, int x, int y, int w, int h) {
		int x0 = x - xbg + this.startX, // start x, y
		y0 = y - ybg + this.startY - this.offsetTileHeight;
		int x1 = 0, // x, y coordinate counter
		y1 = 0;
		int x2 = x + w, // right boundary
		y2 = y + h; // bottom boundary
		// - offsetY;
		
		int xTile = -1;
		int yTile = -1;
		int tileXTemp = this.tileX; // temporary to hold tileX var
		// since we need to modified its value
		
		// int counter = 0, count = 0; // for debugging only
		
		int skip = 0;
		while (true) {
			y1 = y0;
			yTile++;
			
			x1 = x0;
			xTile = --tileXTemp;
			// can't be lower than tileX = 0
			if (xTile < -1) {
				xTile = -1;
			}
			
			// adjust x, y for the next tile based on tile x
			x1 += (xTile + 1) * this.halfTileWidth;
			y1 += (xTile + 1) * this.halfTileHeight;
			
			if (x1 + this.tileWidth <= x) {
				// the drawing is out of view area (too left)
				// adjust the position
				
				// calculate how many tiles must be skipped
				skip = ((x - (x1 + this.tileWidth)) / this.halfTileWidth) + 1;
				
				xTile += skip;
				x1 += skip * this.halfTileWidth;
				y1 += skip * this.halfTileHeight;
			}
			
			// if (x1 >= x2 || y1 >= y2 || xTile >= horiz-1) ++count;
			while (true) {
				if (x1 >= x2 || y1 >= y2 || xTile >= this.horiz - 1) {
					break;
				}
				
				xTile++;
				if (x1 + this.tileWidth > x) {
					this.renderTile(g, xTile, yTile, x1, y1);
					// Point p = getCoordAt(xTile, yTile);
					// p.x += x - xbg;
					// p.y += y - ybg - offsetY;
					// // if (x1 == point2.x && y1 == point2.y)
					// // System.out.println(xTile+","+yTile+"->"+x1+","+y1+" ==
					// "+p.x+","+p.y+" !?"+x+","+y);
					// renderTile(g, xTile, yTile, p.x, p.y);
				} // else ++counter;
				
				// increment x, y for the next tile
				x1 += this.halfTileWidth;
				y1 += this.halfTileHeight;
			}
			
			if (yTile >= this.vert - 1) {
				break;
			}
			
			// adjust start x, y for the next tile
			x0 -= this.halfTileWidth;
			y0 += this.halfTileHeight;
		}
		
		// g.setColor(java.awt.Color.BLACK);
		// g.setStroke(stroke);
		// g.drawRect(x,y,w,h);
		
		// if (counter > 0) System.out.println(counter);
		// if (count > 0) System.out.println(count);
	}
	
	// Stroke stroke = new BasicStroke(3.0f);
	
	/**
	 * Renders tile at <code>tileX</code>, <code>tileY</code> position to
	 * specified <code>x</code>, <code>y</code> coordinate.
	 * @param g The {@link Graphics2D} context used for rendering.
	 * @param tileX The tile x location.
	 * @param tileY The tile y location.
	 * @param x The x location.
	 * @param y The y location.
	 */
	public abstract void renderTile(Graphics2D g, int tileX, int tileY, int x, int y);
	
	/** ************************************************************************* */
	/** ************************ BACKGROUND POSITION **************************** */
	/** ************************************************************************* */
	
	/**
	 * @see com.golden.gamedev.object.Background#setLocation(double, double)
	 */
	public void setLocation(double xb, double yb) {
		int oldx = (int) this.getX(), oldy = (int) this.getY();
		
		super.setLocation(xb, yb);
		
		int x = (int) this.getX(), y = (int) this.getY();
		if (x == oldx && y == oldy) {
			// position is not changed
			return;
		}
		
		// convert <x, y> into tiles
		this.tileX = (y - this.startY) / this.halfTileHeight;
		if (--this.tileX < 0) {
			this.tileX = 0;
		}
		
		this.tileY = x / this.halfTileWidth;
		
		// System.out.println(tileX+" "+tileY);
	}
	
	/**
	 * Sets the background location to specified tile.
	 * @param xs
	 * @param ys
	 */
	public void setTileLocation(int xs, int ys) {
		Point p = this.getCoordinateAt(xs, ys);
		
		this.setLocation(p.x, p.y);
	}
	
	/**
	 * Returns current tile-x position.
	 * @return The tile x location.
	 */
	public int getTileX() {
		return this.tileX;
	}
	
	/**
	 * Returns current tile-y position.
	 * @return The tile y location.
	 */
	public int getTileY() {
		return this.tileY;
	}
	
	/**
	 * Returns iso tile position of specified coordinate or null if the
	 * coordinate is out of background viewport/boundary.
	 * 
	 * Used to detect mouse position on this background, for example : <br>
	 * Drawing rectangle at mouse cursor position
	 * 
	 * <pre>
	 * public class YourGame extends Game {
	 * 	
	 * 	AbstractTileBackground bg;
	 * 	
	 * 	public void render(Graphics2D g) {
	 * 		Point tileAt = bg.getTileAt(getMouseX(), getMouseY());
	 * 		if (tileAt != null) {
	 * 			// mouse cursor is in background area
	 * 			// draw pointed tile
	 * 			// convert tile to coordinate
	 * 			Point coordAt = bg.getCoordinateAt(tileAt.x, tileAt.y);
	 * 			g.setColor(Color.WHITE);
	 * 			g.drawRect(coordAt.x - (int) bg.getX() + bg.getClip().x, coordAt.y
	 * 			        - (int) bg.getY() + bg.getClip().y, bg.getTileWidth(), bg
	 * 			        .getTileHeight());
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param screenX The x location on screen.
	 * @param screenY The y location on screen.
	 * @return The tile at the given location.
	 */
	public Point getTileAt(double screenX, double screenY) {
		if (screenX < this.getClip().x
		        || screenX > this.getClip().x + this.getClip().width
		        || screenY < this.getClip().y
		        || screenY > this.getClip().y + this.getClip().height) {
			// out of background view port
			return null;
		}
		
		screenX += this.getX() - this.getClip().x;
		screenY += this.getY() - this.getClip().y - this.startY;
		screenX -= this.vert * this.halfTileWidth;
		this.point1.x = (int) ((screenY / this.baseTileHeight) + (screenX / this.tileWidth));
		this.point1.y = (int) ((screenY / this.baseTileHeight) - (screenX / this.tileWidth));
		
		if (this.point1.x < 0 || this.point1.x > this.horiz - 1
		        || this.point1.y < 0 || this.point1.y > this.vert - 1) {
			return null;
		}
		
		return this.point1;
	}
	
	/**
	 * Returns screen coordinate of specified tile position. Can be used in
	 * conjunction with {@link #getTileAt(double, double)} to get tile
	 * coordinate at specified coordinate.
	 */
	public Point getCoordinateAt(int tileX, int tileY) {
		this.point2.x = this.startX + ((tileX - tileY) * this.halfTileWidth);
		this.point2.y = ((tileY + tileX) * this.halfTileHeight) + this.startY;
		
		return this.point2;
	}
	
	/** ************************************************************************* */
	/** ************************* TILE PROPERTIES ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Returns the width of the iso tile.
	 */
	public int getTileWidth() {
		return this.tileWidth;
	}
	
	/**
	 * Returns the base height of the iso tile.
	 * <p>
	 * 
	 * This is the base height of the isometric tile, the actual image tile is
	 * <code>getTileHeight()</code> + <code>getOffsetTileHeight()</code>.
	 * 
	 * @see #getOffsetTileHeight()
	 */
	public int getTileHeight() {
		return this.baseTileHeight;
	}
	
	/**
	 * Returns the tile height offset from the base tile height.
	 */
	public int getOffsetTileHeight() {
		return this.offsetTileHeight;
	}
	
	/**
	 * Sets the size of the iso tile.
	 * 
	 * @param tileWidth the width of the iso tile
	 * @param tileHeight the height of the iso tile
	 * @param offsetTileHeight the tile height offset from the base tile height
	 */
	protected void setTileSize(int tileWidth, int tileHeight, int offsetTileHeight) {
		this.tileWidth = tileWidth;
		this.baseTileHeight = tileHeight - offsetTileHeight;
		this.offsetTileHeight = offsetTileHeight;
		
		this.halfTileWidth = tileWidth / 2;
		this.halfTileHeight = this.baseTileHeight / 2;
		
		super.setSize((this.horiz + this.vert) * this.halfTileWidth,
		        ((this.horiz + this.vert) * this.halfTileHeight) + this.startY);
	}
	
	/**
	 * Returns starting y coordinate where the isometric background start
	 * rendered.
	 */
	public int getStartY() {
		return this.startY;
	}
	
	/**
	 * Sets starting y coordinate where the isometric background start rendered.
	 */
	public void setStartY(int startY) {
		this.startY = startY;
		
		super.setSize((this.horiz + this.vert) * this.halfTileWidth,
		        ((this.horiz + this.vert) * this.halfTileHeight) + startY);
	}
	
	/**
	 * Returns background total horizontal tiles.
	 */
	public int getTotalHorizontalTiles() {
		return this.horiz;
	}
	
	/**
	 * Returns background total vertical tiles.
	 */
	public int getTotalVerticalTiles() {
		return this.vert;
	}
	
	public void setSize(int horiz, int vert) {
		this.horiz = horiz;
		this.vert = vert;
		
		super.setSize((horiz + vert) * this.halfTileWidth,
		        ((horiz + vert) * this.halfTileHeight) + this.startY);
	}
	
}
