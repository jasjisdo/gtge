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
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Arrays;

// GTGE
import com.golden.gamedev.util.ImageUtil;
import com.golden.gamedev.engine.BaseGraphics;


/**
 * Graphics engine for Full Screen Exclusive Environment (FSEM). <p>
 *
 * See {@link com.golden.gamedev.engine.BaseGraphics} for how to use graphics
 * engine separated from Golden T Game Engine (GTGE) Frame Work.
 */
public class FullScreenMode implements BaseGraphics, Comparator {


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

	private Frame			frame;

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
	 * Creates new instance of Full Screen Graphics Engine with specified
	 * size, and whether want to use bufferstrategy or volatile image.
	 */
    public FullScreenMode(Dimension d, boolean bufferstrategy) {
        this.size = d;

		// checking for FSEM hardware support
		if (!DEVICE.isFullScreenSupported()) {
			throw new RuntimeException("Full Screen Exclusive Mode is not supported");
	    }

        // sets the game frame
		frame = new Frame("Golden T Game Engine", CONFIG);

		try {
			// set frame icon
			frame.setIconImage(ImageUtil.getImage(FullScreenMode.class.getResource("Icon.png")));
		} catch (Exception e) { }

		frame.addWindowListener(WindowExitListener.getInstance());
		frame.setResizable(false);		// non resizable frame
	    frame.setIgnoreRepaint(true);	// turn off all paint events
										// since we doing active rendering
		frame.setLayout(null);
        frame.setUndecorated(true);		// no menu bar, borders, etc
		frame.dispose();


		// enter fullscreen exclusive mode
		DEVICE.setFullScreenWindow(frame);


		// check whether changing display mode is supported or not
		if (!DEVICE.isDisplayChangeSupported()) {
			DEVICE.setFullScreenWindow(null);
			frame.dispose();
			throw new RuntimeException("Changing Display Mode is not supported");
		}

		DisplayMode bestDisplay = getBestDisplay(size);
		if (bestDisplay == null) {
			DEVICE.setFullScreenWindow(null);
			frame.dispose();
			throw new RuntimeException("Changing Display Mode to " +
									   size.width + "x" + size.height +
									   " is not supported");
		}

		// change screen display mode
		DEVICE.setDisplayMode(bestDisplay);


		// sleep for a while, let awt do her job
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) { }


		// create backbuffer
        if (bufferstrategy) {
	        bufferstrategy = createBufferStrategy();
		}

		if (!bufferstrategy) {
			createBackBuffer();
		}


		frame.requestFocus();
	}


 /****************************************************************************/
 /************************** GRAPHICS FUNCTION *******************************/
 /****************************************************************************/

	private boolean createBufferStrategy() {
		// create bufferstrategy
		boolean bufferCreated;
		int num = 0;
		do {
			bufferCreated = true;
			try {
		        // create bufferstrategy
				frame.createBufferStrategy(2);
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
			try {
				strategy = frame.getBufferStrategy();
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
			// but not validate with current graphics context
            offscreen.flush();

			// clear old backbuffer
            offscreen = null;
        }

        offscreen = CONFIG.createCompatibleVolatileImage(size.width, size.height);
    }

	public Graphics2D getBackBuffer() {
	    if (currentGraphics == null) {
			// graphics context is not created yet,
			// or have been dispose by calling flip()

	        if (strategy == null) {
				// using volatile image
				// let see if the volatile image is still validate or not
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
        	frame.getGraphics().drawImage(offscreen, 0, 0, null);

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
			Thread.sleep(500L);
		} catch (InterruptedException e) { }


		try {
			// exit fullscreen mode
//			DEVICE.setFullScreenWindow(null);

            Thread.sleep(200L);

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
		return frame;
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
		return "Full Screen Mode [" + getSize().width + "x" + getSize().height + "]" +
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


 /****************************************************************************/
 /*********************** FIND THE BEST DISPLAY MODE *************************/
 /****************************************************************************/

	private DisplayMode getBestDisplay(Dimension size) {
		// get display mode for width x height x 32 with the optimum HZ
		DisplayMode mode[] = DEVICE.getDisplayModes();

		ArrayList modeList = new ArrayList();
		for (int i=0;i < mode.length;i++) {
			if (mode[i].getWidth() == size.width &&
				mode[i].getHeight() == size.height) {
				modeList.add(mode[i]);
			}
		}

		if (modeList.size() == 0) {
			// request display mode for 'size' is not found!
			return null;
		}

		DisplayMode[] match = (DisplayMode[]) modeList.toArray(new DisplayMode[0]);
		Arrays.sort(match, this);

		return match[0];
	}

	/**
	 * Sorts display mode, display mode in the first stack will be used by this
	 * graphics engine. The <code>o1</code> and <code>o2</code> are instance of
	 * <code>java.awt.DisplayMode</code>. <p>
	 *
	 * In this comparator, the first stack (the one that this graphics engine
	 * will be used) will be display mode that has the biggest bits per pixel
	 * (bpp) and has the biggest but limited to 75Hz frequency (refresh rate).
	 */
	public int compare(Object o1, Object o2) {
		DisplayMode mode1 = (DisplayMode) o1;
		DisplayMode mode2 = (DisplayMode) o2;

		int removed1 = (mode1.getRefreshRate() > 75) ? 5000 * mode1.getRefreshRate() : 0;
		int removed2 = (mode2.getRefreshRate() > 75) ? 5000 * mode2.getRefreshRate() : 0;

		return ((mode2.getBitDepth() - mode1.getBitDepth()) * 1000) +
				(mode2.getRefreshRate() - mode1.getRefreshRate()) -
				(removed2 - removed1);
	}


}