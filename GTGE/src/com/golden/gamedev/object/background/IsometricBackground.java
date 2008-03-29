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
package com.golden.gamedev.object.background;

// JFC
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.golden.gamedev.object.background.abstraction.AbstractIsometricBackground;

/**
 * The basic isometric background, creates a one layer isometric background.
 * <p>
 * 
 * <code>IsometricBackground</code> takes up two parameter, the first one is a
 * two dimensional array of integer (int[][] tiles) that makes up the background
 * tiling, and the second one is the tiling image array (BufferedImage[]
 * tileImages).
 * <p>
 * 
 * This isometric background is the basic subclass of
 * <code>AbstractIsometricBackground</code> that overrides
 * <code>renderTile</code> method to draw one layer isometric background :
 * 
 * <pre>
 *    public void render(Graphics2D g, int tileX, int tileY, int x, int y) {
 *       //
 * <code>
 * tiles
 * </code>
 *  is the two dimensional background tiling
 *       int tile = tiles[tileX][tileY];
 *       if (tile &gt;= 0) {
 *          //
 * <code>
 * tileImages
 * </code>
 *  is the tiling images
 *          g.drawImage(tileImages[tile], x, y, null);
 *       }
 *    }
 * </pre>
 * 
 * To create multiple layer, simply subclass
 * <code>AbstractIsometricBackground</code> and override the
 * <code>renderTile</code> method to render the tile multiple times.
 * <p>
 * 
 * Isometric background usage example :
 * 
 * <pre>
 * IsometricBackground background;
 * BufferedImage[] tileImages;
 * int[][] tiles = new int[40][30]; // 40 x 30 tiling
 * // fill tiles with random value
 * for (int i = 0; i &lt; tiles.length; i++)
 * 	for (int j = 0; j &lt; tiles[0].length; j++)
 * 		tiles[i][j] = getRandom(0, tileImages.length - 1);
 * // create the background
 * background = new IsometricBackground(tileImages, tiles);
 * </pre>
 * 
 * @see com.golden.gamedev.object.background.abstraction.AbstractIsometricBackground
 */
public class IsometricBackground extends AbstractIsometricBackground {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2383969026366897057L;
	
	private transient BufferedImage[] tileImages;
	
	private int[][] tiles;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>IsometricBackground</code> with specified tile
	 * images, array of tiles, offset of tile height, and starting y coordinate.
	 * <p>
	 * 
	 * The array of tiles that makes up the isometric background tiling,
	 * tiles[0][0] = 2 means the tileImages[2] will be drawn on tile 0, 0
	 * coordinate on the map.
	 * <p>
	 * 
	 * The <code>offsetTileHeight</code> is used to determine the tile image
	 * base tile height, for example the real height of the tile image is 128
	 * but the base height of the tile image is 32, therefore set the
	 * <code>offsetTileHeight</code> to 96 (128-32).
	 * <p>
	 * 
	 * The <code>startY</code> is where the isometric background y coordinate
	 * is starting to render, set this <code>startY</code> value greater than
	 * zero if this background need to be drawn down a bit.
	 * 
	 * @param tileImages an array of images for the tile
	 * @param tiles a two dimensional array that makes up the background
	 * @param offsetTileHeight the tile height offset from the base tile height
	 * @param startY starting y coordinate to draw this background
	 */
	public IsometricBackground(BufferedImage[] tileImages, int[][] tiles,
	        int offsetTileHeight, int startY) {
		super(tiles.length, tiles[0].length, tileImages[0].getWidth(),
		        tileImages[0].getHeight(), offsetTileHeight, startY);
		
		this.tileImages = tileImages;
		this.tiles = tiles;
	}
	
	/**
	 * Creates new <code>IsometricBackground</code> with specified tile images
	 * and array of tiles.
	 * <p>
	 * 
	 * The array of tiles that makes up the isometric background tiling,
	 * tiles[0][0] = 2 means the tileImages[2] will be drawn on tile 0, 0
	 * coordinate on the map.
	 * <p>
	 * 
	 * @param tileImages an array of images for the tile
	 * @param tiles a two dimensional array that makes up the background
	 */
	public IsometricBackground(BufferedImage[] tileImages, int[][] tiles) {
		this(tileImages, tiles, 0, 0);
	}
	
	/**
	 * Creates new <code>IsometricBackground</code> with specified tile images
	 * as big as <code>horiz</code> and </code>vert</code> tiles.
	 * <p>
	 * 
	 * Generates isometric tile background with tile as big as horiz and vert
	 * (tiles = new int[horiz][vert]) and using the first image of the tile
	 * images (tileImages[0]) for all the tiles.
	 * 
	 * @param tileImages an array of images for the tile
	 * @param horiz total horizontal tiles
	 * @param vert total vertical tiles
	 */
	public IsometricBackground(BufferedImage[] tileImages, int horiz, int vert) {
		this(tileImages, new int[horiz][vert]);
	}
	
	/** ************************************************************************* */
	/** ************************ RENDER BACKGROUND ****************************** */
	/** ************************************************************************* */
	
	public void renderTile(Graphics2D g, int tileX, int tileY, int x, int y) {
		int tile = this.tiles[tileX][tileY];
		
		if (tile >= 0) {
			g.drawImage(this.tileImages[tile], x, y, null);
			// g.drawString(tileX+","+tileY, x, y-5);
		}
	}
	
	/** ************************************************************************* */
	/** ************************* BACKGROUND TILE ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Return the isometric background tile images.
	 */
	public BufferedImage[] getTileImages() {
		return this.tileImages;
	}
	
	/**
	 * Sets the isometric background tile images.
	 */
	public void setTileImages(BufferedImage[] tileImages, int offsetTileHeight) {
		this.tileImages = tileImages;
		
		this.setTileSize(tileImages[0].getWidth(), tileImages[0].getHeight(),
		        offsetTileHeight);
	}
	
	/**
	 * Returns the isometric background tiling.
	 */
	public int[][] getTiles() {
		return this.tiles;
	}
	
	/**
	 * Sets the isometric background tiling.
	 * <p>
	 * 
	 * This array of tiles that makes up the background, tiles[0][0] = 2 means
	 * the tileImages[2] will be drawn on tile 0, 0 coordinate on the map.
	 * 
	 * @see #setTileImages(BufferedImage[], int)
	 */
	public void setTiles(int[][] tiles) {
		this.tiles = tiles;
		
		super.setSize(tiles.length, tiles[0].length);
	}
	
	public void setSize(int horiz, int vert) {
		if (horiz != this.tiles.length || vert != this.tiles[0].length) {
			// enlarge/shrink old tiles
			int[][] old = this.tiles;
			
			this.tiles = new int[horiz][vert];
			
			int minx = Math.min(this.tiles.length, old.length), miny = Math
			        .min(this.tiles[0].length, old[0].length);
			for (int j = 0; j < miny; j++) {
				for (int i = 0; i < minx; i++) {
					this.tiles[i][j] = old[i][j];
				}
			}
		}
		
		super.setSize(horiz, vert);
	}
	
}
