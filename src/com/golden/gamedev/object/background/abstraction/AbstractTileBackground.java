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
 * The base abstract class to create tiling background, the subclass need to
 * perform the background tile rendering.
 *
 * @see #renderTile(Graphics2D, int, int, int, int)
 */
public abstract class AbstractTileBackground extends Background {


 /*************************** TILE PROPERTIES ********************************/

	private int tileWidth, tileHeight;

    private int tileX, tileY;       // background <x, y> in tiles
    private int offsetX, offsetY;	// offset/exceed tiles for smooth scrolling

	private int horiz, vert;

	private Point point = new Point();	// return value for getTileAt(...)


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new <code>AbstractTileBackground</code> as big as
	 * <code>horiz</code>, <code>vert</code> tiles, where each tile is as big as
	 * <code>tileWidth</code>, <code>tileHeight</code>. <p>
	 *
	 * The actual dimension of the background is : <br>
	 * width = horiz*tileWidth, height = vert*tileHeight.
	 */
	public AbstractTileBackground(int horiz, int vert,
								  int tileWidth, int tileHeight) {
		super(horiz*tileWidth, vert*tileHeight);

		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		this.horiz = horiz;
		this.vert = vert;

		tileX = tileY = 0;
		offsetX = offsetY = 0;
	}


 /****************************************************************************/
 /************************** RENDER BACKGROUND *******************************/
 /****************************************************************************/

	public void render(Graphics2D g, int xbg, int ybg,
					   int x, int y, int w, int h) {
		int x1 = 0, 		// coordinate counter
			y1 = 0;
		int x2 = x + w,		// right boundary
			y2 = y + h;		// bottom boundary

		int xTile = 0;		// tile counter
		int yTile = tileY;

		for (y1=y-offsetY;y1 < y2;y1+=tileHeight) {
			xTile = tileX;
			for (x1=x-offsetX;x1 < x2;x1+=tileWidth) {
				renderTile(g, xTile, yTile, x1, y1);
				xTile++;
			}
			yTile++;
		}
	}

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
        tileX = x / tileWidth;
		tileY = y / tileHeight;

		offsetX = x % tileWidth;
		offsetY = y % tileHeight;
    }

    /**
     * Sets the background location to specified tile.
     */
	public void setTileLocation(int xs, int ys) {
	    setLocation(xs*tileWidth, ys*tileHeight);
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
	 * Returns background tile position of specified coordinate or null if
	 * the coordinate is out of background viewport/boundary. <p>
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
	 *             int posX = (tileAt.x - bg.getTileX()) * bg.getTileWidth(),
	 *                 posY = (tileAt.y - bg.getTileY()) * bg.getTileHeight();
	 *
	 *             g.setColor(Color.WHITE);
	 *             g.drawRect(posX - bg.getOffsetX() + bg.getClip().x,
	 *                        posY - bg.getOffsetY() + bg.getClip().y,
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

		point.x = (int) (getX() + screenX - getClip().x) / tileWidth;
		point.y = (int) (getY() + screenY - getClip().y) / tileHeight;

		return point;
	}

	/**
	 * Returns current background tile offset-x. <p>
	 *
	 * This offset value that makes smooth scrolling on the background tile.
	 */
    public int getOffsetX() {
		return offsetX;
	}

	/**
	 * Returns current background tile offset-y. <p>
	 *
	 * This offset value that makes smooth scrolling on the background tile.
	 */
	public int getOffsetY() {
		return offsetY;
	}


 /****************************************************************************/
 /*************************** TILE PROPERTIES ********************************/
 /****************************************************************************/

	/**
	 * Returns the width of the tile.
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * Returns the height of the tile.
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * Sets the background tile size.
	 */
	protected void setTileSize(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		super.setSize(horiz*getTileWidth(), vert*getTileHeight());
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

	/**
	 * Sets the size of this background (in tiles).
	 *
	 * @param horiz	total background horizontal tiles
	 * @param vert	total background vertical tiles
	 */
	public void setSize(int horiz, int vert) {
		this.horiz = horiz;
		this.vert = vert;

		super.setSize(horiz*getTileWidth(), vert*getTileHeight());
	}

}