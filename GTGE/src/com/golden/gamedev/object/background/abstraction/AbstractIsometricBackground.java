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

// GTGE
import com.golden.gamedev.object.Background;


/**
 * The base abstract class to create isometric background, the subclass need to
 * perform the background tile rendering.
 */
public abstract class AbstractIsometricBackground extends Background {


	private int tileWidth, baseTileHeight;
	private int halfTileWidth, halfTileHeight;	// half of the iso tile size
												// the tile incremental position
												// is using this value
	private int offsetTileHeight;	// tile height offset from base tile height
	private int startX, startY;		// starting top x, y coordinate

    private int tileX, tileY;

	private int horiz, vert;		// total horizontal, vertical tiles

	private Point point1 = new Point();	// return value for getTileAt(...)
	private Point point2 = new Point(); // return value for getCoordinateAt(...)


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new <code>AbstractIsometricBackground</code> as big as
	 * <code>horiz</code>, <code>vert</code> tiles, where each tile is as big as
	 * <code>tileWidth</code>, <code>tileHeight</code> with specified tile
	 * height offset, and starting y coordinate.
	 *
	 * @param horiz				total horizontal tiles
	 * @param vert				total vertical tiles
	 * @param tileWidth			the width of the iso tile
	 * @param tileHeight		the height of the iso tile
	 * @param offsetTileHeight	the tile height offset from the base tile height
	 * @param startY			starting y coordinate to draw this background
	 */
	public AbstractIsometricBackground(int horiz, int vert,
								  	   int tileWidth, int tileHeight,
									   int offsetTileHeight, int startY) {
		super((horiz+vert) * (tileWidth/2),
			  ((horiz+vert) * ((tileHeight-offsetTileHeight)/2)) + startY);

		this.tileWidth = tileWidth;
		this.baseTileHeight = tileHeight - offsetTileHeight;
		this.offsetTileHeight = offsetTileHeight;

		this.halfTileWidth = tileWidth / 2;
		this.halfTileHeight = baseTileHeight / 2;

		this.horiz = horiz;
		this.vert = vert;

		this.startX = (vert-1) * halfTileWidth;	// starting x depends on vertical tiles
		this.startY = startY;

		tileX = tileY = 0;
	}

	/**
	 * Creates new <code>AbstractIsometricBackground</code> as big as
	 * <code>horiz</code>, <code>vert</code> tiles, where each tile is as big as
	 * <code>tileWidth</code>, <code>tileHeight</code>.
	 *
	 * @param horiz				total horizontal tiles
	 * @param vert				total vertical tiles
	 * @param tileWidth			the width of the iso tile
	 * @param tileHeight		the height of the iso tile
	 */
	public AbstractIsometricBackground(int horiz, int vert,
									   int tileWidth, int tileHeight) {
		this(horiz,vert,tileWidth,tileHeight,0,0);
	}


 /****************************************************************************/
 /************************** RENDER BACKGROUND *******************************/
 /****************************************************************************/

	public void render(Graphics2D g, int xbg, int ybg,
					   int x, int y, int w, int h) {
		int x0 = x - xbg + startX,				// start x, y
			y0 = y - ybg + startY - offsetTileHeight;
		int x1 = 0,			// x, y coordinate counter
			y1 = 0;
		int x2 = x + w,		// right boundary
			y2 = y + h; 	// bottom boundary
		// - offsetY;

		int xTile = -1;
		int yTile = -1;
		int tileXTemp = tileX;	// temporary to hold tileX var
								// since we need to modified its value

//		int counter = 0, count = 0; // for debugging only

		int skip = 0;
		while (true) {
			y1 = y0;
			yTile++;

			x1 = x0;
			xTile = --tileXTemp;
			// can't be lower than tileX = 0
			if (xTile < -1) xTile = -1;

			// adjust x, y for the next tile based on tile x
			x1 += (xTile+1) * halfTileWidth;
			y1 += (xTile+1) * halfTileHeight;

			if (x1 + tileWidth <= x) {
				// the drawing is out of view area (too left)
				// adjust the position

				// calculate how many tiles must be skipped
				skip = ((x-(x1+tileWidth)) / halfTileWidth) + 1;

				xTile += skip;
				x1 += skip * halfTileWidth;
				y1 += skip * halfTileHeight;
			}

//			if (x1 >= x2 || y1 >= y2 || xTile >= horiz-1) ++count;
			while (true) {
				if (x1 >= x2 || y1 >= y2 || xTile >= horiz-1) break;

				xTile++;
				if (x1+tileWidth > x) {
					renderTile(g, xTile, yTile, x1, y1);
//					Point p = getCoordAt(xTile, yTile);
//					p.x += x - xbg;
//					p.y += y - ybg - offsetY;
////					if (x1 == point2.x && y1 == point2.y)
////					System.out.println(xTile+","+yTile+"->"+x1+","+y1+"  ==  "+p.x+","+p.y+"     !?"+x+","+y);
//					renderTile(g, xTile, yTile, p.x, p.y);
				} // else ++counter;

				// increment x, y for the next tile
				x1 += halfTileWidth;
				y1 += halfTileHeight;
			}

			if (yTile >= vert-1) break;

			// adjust start x, y for the next tile
			x0 -= halfTileWidth;
			y0 += halfTileHeight;
		}

//		g.setColor(java.awt.Color.BLACK);
//		g.setStroke(stroke);
//		g.drawRect(x,y,w,h);

//		if (counter > 0) System.out.println(counter);
//		if (count > 0) System.out.println(count);
    }
//    Stroke stroke = new BasicStroke(3.0f);

	/**
	 * Renders tile at <code>tileX</code>, <code>tileY</code> position to
	 * specified <code>x</code>, <code>y</code> coordinate.
	 */
	public abstract void renderTile(Graphics2D g,
					 	   			int tileX, int tileY,
									int x, int y);


 /****************************************************************************/
 /************************** BACKGROUND POSITION *****************************/
 /****************************************************************************/

    public void setLocation(double xb, double yb) {
		int oldx = (int) getX(), oldy = (int) getY();

		super.setLocation(xb, yb);

		int x = (int) getX(), y = (int) getY();
        if (x == oldx && y == oldy) {
	        // position is not changed
			return;
		}

		// convert <x, y> into tiles
		tileX = (y-startY) / halfTileHeight;
		if (--tileX < 0) tileX = 0;

		tileY = x / halfTileWidth;

//		System.out.println(tileX+" "+tileY);
    }

    /**
     * Sets the background location to specified tile.
     */
	public void setTileLocation(int xs, int ys) {
		Point p = getCoordinateAt(xs, ys);

	    setLocation(p.x, p.y);
	}

	/**
	 * Returns current tile-x position.
	 */
	public int getTileX() {
		return tileX;
	}

	/**
	 * Returns current tile-y position.
	 */
	public int getTileY() {
		return tileY;
	}

	/**
	 * Returns iso tile position of specified coordinate or null if the
	 * coordinate is out of background viewport/boundary.
	 *
	 * Used to detect mouse position on this background, for example : <br>
	 * Drawing rectangle at mouse cursor position
	 * <pre>
	 *    public class YourGame extends Game {
	 *
	 *       AbstractTileBackground bg;
	 *
	 *       public void render(Graphics2D g) {
	 *          Point tileAt = bg.getTileAt(getMouseX(), getMouseY());
	 *
	 *          if (tileAt != null) {
	 *             // mouse cursor is in background area
	 *             // draw pointed tile
	 *
	 *             // convert tile to coordinate
	 *             Point coordAt = bg.getCoordinateAt(tileAt.x, tileAt.y);
	 *
	 *             g.setColor(Color.WHITE);
	 *             g.drawRect(coordAt.x - (int) bg.getX() + bg.getClip().x,
	 *                        coordAt.y - (int) bg.getY() + bg.getClip().y,
	 *                        bg.getTileWidth(), bg.getTileHeight());
	 *          }
	 *       }
	 *
	 *    }
	 * </pre>
	 */
	public Point getTileAt(double screenX, double screenY) {
		if (screenX < getClip().x || screenX > getClip().x + getClip().width ||
			screenY < getClip().y || screenY > getClip().y + getClip().height) {
			// out of background view port
			return null;
		}

		screenX += getX() - getClip().x;
		screenY += getY() - getClip().y - startY;
		screenX -= vert * halfTileWidth;
		point1.x = (int) ((screenY / baseTileHeight) + (screenX / tileWidth));
		point1.y = (int) ((screenY / baseTileHeight) - (screenX / tileWidth));

		if (point1.x < 0 || point1.x > horiz-1 ||
			point1.y < 0 || point1.y > vert-1) {
			return null;
		}

		return point1;
	}

	/**
	 * Returns screen coordinate of specified tile position. Can be used in
	 * conjunction with {@link #getTileAt(double, double)} to get tile
	 * coordinate at specified coordinate.
	 */
	public Point getCoordinateAt(int tileX, int tileY) {
		point2.x = startX + ((tileX-tileY) * halfTileWidth);
		point2.y = ((tileY+tileX) * halfTileHeight) + startY;

		return point2;
	}


 /****************************************************************************/
 /*************************** TILE PROPERTIES ********************************/
 /****************************************************************************/

	/**
	 * Returns the width of the iso tile.
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * Returns the base height of the iso tile. <p>
	 *
	 * This is the base height of the isometric tile, the actual image tile is
	 * <code>getTileHeight()</code> + <code>getOffsetTileHeight()</code>.
	 *
	 * @see #getOffsetTileHeight()
	 */
	public int getTileHeight() {
		return baseTileHeight;
	}

	/**
	 * Returns the tile height offset from the base tile height.
	 */
	public int getOffsetTileHeight() {
		return offsetTileHeight;
	}

	/**
	 * Sets the size of the iso tile.
	 *
	 * @param tileWidth			the width of the iso tile
	 * @param tileHeight		the height of the iso tile
	 * @param offsetTileHeight	the tile height offset from the base tile height
	 */
	protected void setTileSize(int tileWidth, int tileHeight, int offsetTileHeight) {
		this.tileWidth  = tileWidth;
		this.baseTileHeight = tileHeight - offsetTileHeight;
		this.offsetTileHeight = offsetTileHeight;

		halfTileWidth  = tileWidth / 2;
		halfTileHeight = baseTileHeight / 2;

		super.setSize((horiz+vert) * halfTileWidth,
			  		  ((horiz+vert) * halfTileHeight) + startY);
	}

	/**
	 * Returns starting y coordinate where the isometric background start
	 * rendered.
	 */
	public int getStartY() {
		return startY;
	}

	/**
	 * Sets starting y coordinate where the isometric background start
	 * rendered.
	 */
	public void setStartY(int startY) {
		this.startY = startY;

		super.setSize((horiz+vert) * halfTileWidth,
			  		  ((horiz+vert) * halfTileHeight) + startY);
	}

	/**
	 * Returns background total horizontal tiles.
	 */
	public int getTotalHorizontalTiles() {
		return horiz;
	}

	/**
	 * Returns background total vertical tiles.
	 */
	public int getTotalVerticalTiles() {
		return vert;
	}

	public void setSize(int horiz, int vert) {
		this.horiz = horiz;
		this.vert = vert;

		super.setSize((horiz+vert) * halfTileWidth,
			  		  ((horiz+vert) * halfTileHeight) + startY);
	}

}