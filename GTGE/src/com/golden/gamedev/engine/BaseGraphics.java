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
package com.golden.gamedev.engine;

// JFC
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * <code>BaseGraphics</code> interface provides all needed graphics function
 * for drawing unto screen.
 * <p>
 * 
 * Common methods of how-to-use <code>BaseGraphics</code>:
 * 
 * <pre>
 *    public class GraphicsEngine implements BaseGraphics {
 *       .....
 *       public static void main(String[] args) {
 *          BaseGraphics engine = new GraphicsEngine(...);
 *          // game loop
 *          while (true) {
 *             do {
 *                // get backbuffer
 *                Graphics2D g = engine.getBackBuffer();
 *                // draw to backbuffer
 *                g.drawImage(...);
 *                // flip to screen
 *             } while (engine.flip() == false);
 *          }
 *          // dispose graphics engine
 *          engine.cleanup();
 *       }
 *    }
 * </pre>
 */
public interface BaseGraphics {
	
	/**
	 * Returns backbuffer where the rendering perform.
	 * @return The {@link Graphics2D} context used to render on the backbuffer.
	 */
	public Graphics2D getBackBuffer();
	
	/**
	 * Flips backbuffer to the screen (primary surface). Since most graphics
	 * engine backbuffer is VolatileImage type, thus the flipping data could be
	 * lost and need to be restored. Therefore, if this method return false,
	 * backbuffer need to be rerendered.
	 * <p>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * do {
	 * 	Graphics2D g = BaseGraphics.getBackBuffer();
	 * 	//.... do graphics operation
	 * } while (BaseGraphics.flip() == false);
	 * </pre>
	 * 
	 * See {@link java.awt.image.VolatileImage} for detail information.
	 * 
	 * @return true, if the flipping is successfully proceed.
	 * @see #getBackBuffer()
	 */
	public boolean flip();
	
	/**
	 * Releases any system graphics resources and do finalization.
	 */
	public void cleanup();
	
	/**
	 * Returns graphics engine dimension.
	 * @return The size.
	 */
	public Dimension getSize();
	
	/**
	 * Returns the component where the rendering perform.
	 * @return The component that is rendered on.
	 */
	public Component getComponent();
	
	/**
	 * Returns graphics engine description, for example: fullscreen, windowed,
	 * applet, fullscreen with bufferstrategy, etc.
	 * @return The engine description.
	 */
	public String getGraphicsDescription();
	
	/**
	 * Sets graphics engine window title.
	 * <p>
	 * 
	 * Note: Not all graphics engine support for setting window title, for
	 * example applet graphics engine.
	 * @param title The new window title.
	 */
	public void setWindowTitle(String title);
	
	/**
	 * Returns graphics engine window title or <code>String</code> "" if
	 * setting window title is not supported.
	 * @return The window title.
	 */
	public String getWindowTitle();
	
	/**
	 * Sets graphics engine window icon image.
	 * <p>
	 * 
	 * Note: Not all graphics engine support for setting window icon, for
	 * example applet graphics engine.
	 * 
	 * @param icon The new window icon image.
	 * @see com.golden.gamedev.util.ImageUtil#getImage(java.net.URL)
	 */
	public void setWindowIcon(Image icon);
	
	/**
	 * Returns graphics engine window icon image or <code>null</code> if
	 * setting window icon image is not supported.
	 * @return The window icon.
	 */
	public Image getWindowIcon();
	
}
