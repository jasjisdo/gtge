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
package com.golden.gamedev.object;

// JFC
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;

/**
 * <code>Background</code> is the area where every sprites lived.
 * <p>
 * 
 * A background has a view area/view port/clipping area, that is the area which
 * is seen to the player. By default the view area of a newly created background
 * is as big as game dimension. For example if the game is created as big as 640
 * x 480 dimension, the background view area would be also 640 x 480, thus
 * occupy all the game view. To set the background view area, use
 * {@linkplain #setClip(int, int, int, int) setClip(x, y, width, height)}.
 * <p>
 * 
 * If the background size is larger than view area, means there is area not
 * viewable, use {@linkplain #move(double, double) move(dx, dy)} or
 * {@linkplain #setLocation(double, double)} to move around.
 * <p>
 * 
 * To set a sprite to be the center of the background, use
 * {@linkplain #setToCenter(Sprite)}.
 * 
 * @see com.golden.gamedev.object.background
 */
public class Background implements Serializable {
	
	/** ********************** SCREEN DIMENSION VAR ***************************** */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2401822583090769966L;
	
	/**
	 * Screen resolution dimension. Used to determine background clipping area.
	 * <br>
	 * Default value: 640x480.
	 * 
	 * @see #setClip(int, int, int, int)
	 */
	public static Dimension screen = new Dimension(640, 480);
	
	/** ********************** BACKGROUND PROPERTIES **************************** */
	
	/**
	 * Background <code>x</code> coordinate.
	 * 
	 * @see #setLocation(double, double)
	 */
	protected double x;
	
	/**
	 * Background <code>y</code> coordinate.
	 * 
	 * @see #setLocation(double, double)
	 */
	protected double y;
	
	private int width, height; // dimension
	        
	private final Rectangle clip; // view port (screen clipping)
	
	/** ************************************************************************* */
	/** *************************** SINGLETON *********************************** */
	/** ************************************************************************* */
	
	private static Background backgr;
	
	/**
	 * Returns the default background used by every newly created sprite.
	 */
	public static Background getDefaultBackground() {
		if (Background.backgr == null) {
			Background.backgr = new Background();
		}
		
		return Background.backgr;
	}
	
	/** ************************************************************************* */
	/** **************************** CONSTRUCTOR ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Creates new Background with specified size, and default clipping area (as
	 * large as screen size).
	 * <p>
	 * 
	 * Clipping area is the viewport of the background, anything outside
	 * viewport area will not be rendered.
	 * 
	 * @param w background width
	 * @param h background height
	 */
	public Background(int w, int h) {
		this.x = this.y = 0;
		this.width = w;
		this.height = h;
		
		this.clip = new Rectangle(0, 0, Background.screen.width,
		        Background.screen.height);
	}
	
	/**
	 * Creates new Background, with size and clipping area as large as screen
	 * size.
	 * <p>
	 * 
	 * Clipping area is the view port of the background, anything outside
	 * clipping area will not be rendered.
	 */
	public Background() {
		this(Background.screen.width, Background.screen.height);
	}
	
	/** ************************************************************************* */
	/** ************************ BACKGROUND POSITION **************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns the <code>x</code> coordinate of this background.
	 */
	public double getX() {
		return this.x;
	}
	
	/**
	 * Returns the <code>y</code> coordinate of this background.
	 */
	public double getY() {
		return this.y;
	}
	
	/**
	 * Returns the width of this background.
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of this background.
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Sets the size of this background.
	 */
	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
		
		// revalidate position againts new size
		this.setLocation(this.x, this.y);
	}
	
	/**
	 * Sets background location to specified coordinate. The location is bounded
	 * to background boundary (0, 0, Background.getWidth(),
	 * Background.getHeight()).
	 * 
	 * @param xb the <code>x</code> coordinate of the background
	 * @param yb the <code>y</code> coordinate of the background
	 */
	public void setLocation(double xb, double yb) {
		// check background bounds
		if (xb > this.width - this.clip.width) {
			xb = this.width - this.clip.width; // right bound
		}
		if (yb > this.height - this.clip.height) {
			yb = this.height - this.clip.height; // bottom bound
		}
		if (xb < 0) {
			xb = 0; // left bound
		}
		if (yb < 0) {
			yb = 0; // top bound
		}
		
		this.x = xb;
		this.y = yb;
	}
	
	/**
	 * Moves background location by specified pixels.
	 */
	public void move(double dx, double dy) {
		this.setLocation(this.x + dx, this.y + dy);
	}
	
	/**
	 * Sets specified rectangle as the center of the background's viewport.
	 */
	public void setToCenter(int x, int y, int w, int h) {
		this.setLocation(x + (w / 2) - (this.clip.width / 2), y + (h / 2)
		        - (this.clip.height / 2));
	}
	
	/**
	 * Sets specified sprite as the center of the background's viewport.
	 */
	public void setToCenter(Sprite centered) {
		this.setToCenter((int) centered.getX(), (int) centered.getY(), centered
		        .getWidth(), centered.getHeight());
	}
	
	/** ************************************************************************* */
	/** ************************ BACKGROUND VIEWPORT **************************** */
	/** ************************************************************************* */
	
	/**
	 * Sets background clipping area (viewport). Clipping area is the viewport
	 * of the background, anything outside viewport area will not be rendered.
	 * <p>
	 * 
	 * By default background viewport is as large as screen size.
	 * 
	 * @see #screen
	 */
	public void setClip(int x, int y, int width, int height) {
		this.clip.setBounds(x, y, width, height);
	}
	
	/**
	 * Sets background clipping area (viewport). Clipping area is the viewport
	 * of the background, anything outside viewport area will not be rendered.
	 * <p>
	 * 
	 * By default background viewport is as large as screen size.
	 * 
	 * @see #screen
	 */
	public void setClip(Rectangle r) {
		this.clip.setBounds(r);
	}
	
	/**
	 * Returns background screen viewport (clipping area), anything outside
	 * viewport area will not be rendered.
	 */
	public Rectangle getClip() {
		return this.clip;
	}
	
	/** ************************************************************************* */
	/** ******************************* UPDATE ********************************** */
	/** ************************************************************************* */
	
	/**
	 * Updates this background, this method is usually used to create background
	 * animation or other special effect on the background.
	 * <p>
	 * 
	 * The implementation of this method provided by the <code>Background</code>
	 * class does nothing.
	 */
	public void update(long elapsedTime) {
	}
	
	/** ************************************************************************* */
	/** ********************** RENDER TO GRAPHICS CONTEXT *********************** */
	/** ************************************************************************* */
	
	/**
	 * Renders background to specified graphics context.
	 * 
	 * @param g graphics context
	 */
	public void render(Graphics2D g) {
		this.render(g, (int) this.x, (int) this.y, this.clip.x, this.clip.y,
		        (this.width < this.clip.width) ? this.width : this.clip.width,
		        (this.height < this.clip.height) ? this.height
		                : this.clip.height);
	}
	
	/**
	 * Renders background from specified position and clipping area to specified
	 * graphics context.
	 * <p>
	 * 
	 * This method to simplify background subclass rendering, the subclass only
	 * need to render the background from specified x, y coordinate with
	 * specified clipping area.
	 * <p>
	 * 
	 * For example: <br>
	 * 
	 * <pre>
	 * Background backgr;
	 * Graphics2D g;
	 * backgr.render(g, 100, 100, 5, 10, 100, 200);
	 * </pre>
	 * 
	 * Means the background must render itself from background coordinate 100,
	 * 100 to specified graphics context, starting from 5, 10 screen pixel as
	 * large as 100 x 200 dimension.
	 * 
	 * @param g graphics context
	 * @param xbg background x-coordinate
	 * @param ybg background y-coordinate
	 * @param x screen start x clipping
	 * @param y screen start y clipping
	 * @param w clipping width
	 * @param h clipping height
	 */
	public void render(Graphics2D g, int xbg, int ybg, int x, int y, int w, int h) {
	}
	
}
