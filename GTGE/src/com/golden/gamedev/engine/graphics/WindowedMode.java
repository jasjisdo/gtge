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
import java.awt.image.VolatileImage;

// GTGE
import com.golden.gamedev.util.ImageUtil;
import com.golden.gamedev.engine.BaseGraphics;


/**
 * Graphics engine for Windowed Environment. <p>
 *
 * See {@link com.golden.gamedev.engine.BaseGraphics} for how to use graphics
 * engine separated from Golden T Game Engine (GTGE) Frame Work.
 */
public class WindowedMode implements BaseGraphics {


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

	private Frame			frame;		// top frame where the canvas is put
    private Canvas          canvas;

	private Dimension       size;


 /***************************** BACK BUFFER **********************************/

    private VolatileImage   offscreen;	// backbuffer image

	private BufferStrategy  strategy;

	// current graphics context
    private Graphics2D      currentGraphics;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new instance of Windowed Graphics Engine with specified size,
	 * and whether want to use bufferstrategy or volatile image.
	 */
    public WindowedMode(Dimension d, boolean bufferstrategy) {
	    this.size = d;

        // sets game frame
		frame = new Frame("Golden T Game Engine", CONFIG);

		try {
			// set frame icon
			frame.setIconImage(ImageUtil.getImage(WindowedMode.class.getResource("Icon.png")));
		} catch (Exception e) { }

		frame.addWindowListener(WindowExitListener.getInstance());
		frame.setResizable(false);		// non resizable frame
	    frame.setIgnoreRepaint(true);	// turn off all paint events
										// since we doing active rendering

		// the active component where the game drawn
		canvas = new Canvas(CONFIG);
		canvas.setIgnoreRepaint(true);
		canvas.setSize(size);

		// frame title bar and border (frame insets) makes
		// game screen smaller than requested size
		// we must enlarge the frame by it's insets size
		frame.setVisible(true);
        Insets inset = frame.getInsets();
        frame.setVisible(false);
        frame.setSize(size.width + inset.left + inset.right,
					  size.height + inset.top + inset.bottom);
        frame.add(canvas);
        frame.pack();
		frame.setLayout(null);
        frame.setLocationRelativeTo(null); // centering game frame
        if (frame.getX() < 0) frame.setLocation(0, frame.getY());
        if (frame.getY() < 0) frame.setLocation(frame.getX(), 0);
        frame.setVisible(true);


		// create backbuffer
        if (bufferstrategy) {
	        bufferstrategy = createBufferStrategy();
		}

		if (!bufferstrategy) {
			createBackBuffer();
		}


		canvas.requestFocus();
    }



 /****************************************************************************/
 /************************** GRAPHICS FUNCTION *******************************/
 /****************************************************************************/

	private boolean createBufferStrategy() {
		boolean bufferCreated;
		int num = 0;
		do {
			bufferCreated = true;
			try {
		        // create bufferstrategy
				canvas.createBufferStrategy(2);
			} catch (Exception e) {
				// unable to create bufferstrategy!
				bufferCreated = false;
				try { Thread.sleep(200);
				} catch (InterruptedException excp) { }
			}

			if (num++ > 5) break;
		} while (!bufferCreated);

		if (!bufferCreated) {
			System.err.println("BufferStrategy is not available!");
			return false;
		}


        // wait until bufferstrategy successfully setup
        while (strategy == null) {
			try { strategy = canvas.getBufferStrategy();
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

		return true;
	}

	private void createBackBuffer() {
        if (offscreen != null) {
            // backbuffer is already created,
			// but not validate with current graphics configuration
            offscreen.flush();

            // clear old backbuffer
            offscreen = null;
        }

        offscreen = CONFIG.createCompatibleVolatileImage(size.width, size.height);
    }

	public Graphics2D getBackBuffer() {
	    if (currentGraphics == null) {
			// graphics context is not created yet,
			// or have been disposed by calling flip()

	        if (strategy == null) {
				// using volatile image
	            if (offscreen.validate(CONFIG) == VolatileImage.IMAGE_INCOMPATIBLE) {
		            // volatile image is not valid
					createBackBuffer();
				}
	            currentGraphics = offscreen.createGraphics();

			} else {
				// using buffer strategy
				currentGraphics = (Graphics2D) strategy.getDrawGraphics();
	        }
	    }

		return currentGraphics;
	}

    public boolean flip() {
	    // disposing current graphics context
        currentGraphics.dispose();
        currentGraphics = null;

        // show to screen
		if (strategy == null) {
        	canvas.getGraphics().drawImage(offscreen, 0, 0, null);

	        // sync the display on some systems.
	        // (on linux, this fixes event queue problems)
        	Toolkit.getDefaultToolkit().sync();

        	return (!offscreen.contentsLost());

		} else {
			strategy.show();

			// sync the display on some systems.
	        // (on linux, this fixes event queue problems)
        	Toolkit.getDefaultToolkit().sync();

			return (!strategy.contentsLost());
		}
   }

 /****************************************************************************/
 /********************* DISPOSING GRAPHICS ENGINE ****************************/
 /****************************************************************************/

	public void cleanup() {
		try {
			Thread.sleep(200L);
		} catch (InterruptedException e) { }


		try {
			// dispose the frame
			if (frame != null) {
				frame.dispose();
			}
		} catch(Exception e) {
			System.err.println("ERROR: Shutting down graphics context " + e);
			System.exit(-1);
		}
	}


 /****************************************************************************/
 /***************************** PROPERTIES ***********************************/
 /****************************************************************************/

	public Dimension getSize() {
		return size;
	}

	public Component getComponent() {
		return canvas;
	}

	/**
	 * Returns the top level frame where this graphics engine is being put on.
	 */
	public Frame getFrame() {
		return frame;
	}

	/**
	 * Returns whether this graphics engine is using buffer strategy or volatile
	 * image.
	 */
	public boolean isBufferStrategy() {
		return (strategy != null);
	}

	public String getGraphicsDescription() {
		return "Windowed Mode [" + getSize().width + "x" + getSize().height + "]" +
			   ((strategy != null) ? " with BufferStrategy" : "");
	}

	public void setWindowTitle(String st) {
		frame.setTitle(st);
	}

	public String getWindowTitle() {
		return frame.getTitle();
	}

	public void setWindowIcon(Image icon) {
		try {
			frame.setIconImage(icon);
		} catch (Exception e) { }
	}

	public Image getWindowIcon() {
		return frame.getIconImage();
	}

}