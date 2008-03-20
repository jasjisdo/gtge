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
package com.golden.gamedev.engine.graphics;

// JFC
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.applet.Applet;

// GTGE
import com.golden.gamedev.engine.BaseGraphics;


/**
 * Graphics engine for Applet Environment. <p>
 *
 * See {@link com.golden.gamedev.engine.BaseGraphics} for how to use graphics
 * engine separated from Golden T Game Engine (GTGE) Frame Work.
 */
public class AppletMode extends Applet implements BaseGraphics {


 /************************** HARDWARE DEVICE *********************************/

	/**
	 * The graphics device that constructs this graphics engine.
	 */
	public static final GraphicsDevice DEVICE =
			GraphicsEnvironment.getLocalGraphicsEnvironment().
			getDefaultScreenDevice();

	/**
	 * The graphics configuration that constructs this graphics engine.
	 */
	public static final GraphicsConfiguration CONFIG =
			DEVICE.getDefaultConfiguration();


 /***************************** AWT COMPONENT ********************************/

    private Canvas          canvas;


 /***************************** BACK BUFFER **********************************/

	private BufferStrategy  strategy;

    private Graphics2D      currentGraphics;	// current graphics context


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new instance of Applet Graphics Engine,
	 * please <b>see note</b> below. <p>
	 *
	 * <b>Note:</b> <br>
	 * <b>Do not</b> make any overloading constructors. <br>
	 * The engine is initialized in {@link #start()} method,
	 * therefore do not use any graphics function before
	 * the <code>start()</code> method is being called by the browser.
	 *
	 * @see #start()
	 */
	public AppletMode() {
	}

	/**
	 * Initializes this applet graphics engine, do not attempt to use any
	 * graphics function before this method is automatically called by the
	 * browser.
	 */
    public void start() {
        if (strategy == null) {
			setIgnoreRepaint(true);
			setLayout(null);

			canvas = new Canvas(CONFIG);
			canvas.setIgnoreRepaint(true);
            canvas.setSize(getSize());

			add(canvas);
			canvas.setLocation(0, 0);

            // using buffer strategy as backbuffer
			boolean bufferCreated;
			int num = 0;
			do {
				bufferCreated = true;
				try {
			        // create bufferstrategy
					canvas.createBufferStrategy(2);
				} catch (ClassCastException e) {
					// unable to create bufferstrategy!
					bufferCreated = false;
					try { Thread.sleep(200);
					} catch (InterruptedException excp) { }

					if (num++ > 5) throw e;
				}
			} while (!bufferCreated);

	        // wait until bufferstrategy successfully setup
	        while (strategy == null) {
				try {
					strategy = canvas.getBufferStrategy();
				} catch (Exception e) { }
			}

			// wait until backbuffer successfully setup
			Graphics2D gfx = null;
			while (gfx == null) {
				// this process will throw an exception
				// if the backbuffer has not been created yet
				try {
					gfx = getBackBuffer();
				} catch (Exception e) { }
			}
        }

        canvas.requestFocus();
    }


 /****************************************************************************/
 /************************** GRAPHICS FUNCTION *******************************/
 /****************************************************************************/

	public Graphics2D getBackBuffer() {
	    if (currentGraphics == null) {
			// graphics context is not created yet,
			// or have been dispose by calling flip()

			currentGraphics = (Graphics2D) strategy.getDrawGraphics();
	    }

	    return currentGraphics;
	}

    public boolean flip() {
	    // disposing current graphics context
        currentGraphics.dispose();
        currentGraphics = null;

        // show to screen
        try {
			strategy.show();

	        // sync the display on some systems.
	        // (on linux, this fixes event queue problems)
        	Toolkit.getDefaultToolkit().sync();

			return (!strategy.contentsLost());

		} catch (NullPointerException e) {
			return true;
		}
     }


 /****************************************************************************/
 /********************* DISPOSING GRAPHICS ENGINE ****************************/
 /****************************************************************************/

    public void cleanup() {
	    // do nothing
	}

    /**
     * Override <code>Applet.destroy()</code> to release any graphics resource
     * by calling {@linkplain #cleanup()}.
     */
	public void destroy() {
		cleanup();
	}


 /****************************************************************************/
 /***************************** PROPERTIES ***********************************/
 /****************************************************************************/

	public Dimension getSize() {
		return super.getSize();
	}

	public Component getComponent() {
		return canvas;
	}

	public String getGraphicsDescription() {
		return "Applet Mode [" + getSize().width + "x" + getSize().height +
			   "] with BufferStrategy";
	}

	public void setWindowTitle(String st) {
	}

	public String getWindowTitle() {
		return "";
	}

	public void setWindowIcon(Image icon) {
	}

	public Image getWindowIcon() {
		return null;
	}

}