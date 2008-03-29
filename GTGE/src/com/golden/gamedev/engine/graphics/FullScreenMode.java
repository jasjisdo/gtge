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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.golden.gamedev.engine.BaseGraphics;
import com.golden.gamedev.util.ImageUtil;

/**
 * Graphics engine for Full Screen Exclusive Environment (FSEM).
 * <p>
 * 
 * See {@link com.golden.gamedev.engine.BaseGraphics} for how to use graphics
 * engine separated from Golden T Game Engine (GTGE) Frame Work.
 */
public class FullScreenMode implements BaseGraphics, Comparator {
	
	/** ************************ HARDWARE DEVICE ******************************** */
	
	/**
	 * The graphics device that constructs this graphics engine.
	 */
	public static final GraphicsDevice DEVICE = GraphicsEnvironment
	        .getLocalGraphicsEnvironment().getDefaultScreenDevice();
	
	/**
	 * The graphics configuration that constructs this graphics engine.
	 */
	public static final GraphicsConfiguration CONFIG = FullScreenMode.DEVICE
	        .getDefaultConfiguration();
	
	/** *************************** AWT COMPONENT ******************************* */
	
	private Frame frame;
	
	private Dimension size;
	
	/** *************************** BACK BUFFER ********************************* */
	
	private VolatileImage offscreen; // backbuffer image
	
	private BufferStrategy strategy;
	
	// current graphics context
	private Graphics2D currentGraphics;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new instance of Full Screen Graphics Engine with specified size,
	 * and whether want to use bufferstrategy or volatile image.
	 * @param d The resolution of fullscreen mode.
	 * @param bufferstrategy If a buffer stratagy shall be used.
	 */
	public FullScreenMode(Dimension d, boolean bufferstrategy) {
		this.size = d;
		
		// checking for FSEM hardware support
		if (!FullScreenMode.DEVICE.isFullScreenSupported()) {
			throw new RuntimeException(
			        "Full Screen Exclusive Mode is not supported");
		}
		
		// sets the game frame
		this.frame = new Frame("Golden T Game Engine", FullScreenMode.CONFIG);
		
		try {
			// set frame icon
			this.frame.setIconImage(ImageUtil.getImage(FullScreenMode.class
			        .getResource("Icon.png")));
		}
		catch (Exception e) {
		}
		
		this.frame.addWindowListener(WindowExitListener.getInstance());
		this.frame.setResizable(false); // non resizable frame
		this.frame.setIgnoreRepaint(true); // turn off all paint events
		// since we doing active rendering
		this.frame.setLayout(null);
		this.frame.setUndecorated(true); // no menu bar, borders, etc
		this.frame.dispose();
		
		// enter fullscreen exclusive mode
		FullScreenMode.DEVICE.setFullScreenWindow(this.frame);
		
		// check whether changing display mode is supported or not
		if (!FullScreenMode.DEVICE.isDisplayChangeSupported()) {
			FullScreenMode.DEVICE.setFullScreenWindow(null);
			this.frame.dispose();
			throw new RuntimeException("Changing Display Mode is not supported");
		}
		
		DisplayMode bestDisplay = this.getBestDisplay(this.size);
		if (bestDisplay == null) {
			FullScreenMode.DEVICE.setFullScreenWindow(null);
			this.frame.dispose();
			throw new RuntimeException("Changing Display Mode to "
			        + this.size.width + "x" + this.size.height
			        + " is not supported");
		}
		
		// change screen display mode
		FullScreenMode.DEVICE.setDisplayMode(bestDisplay);
		
		// sleep for a while, let awt do her job
		try {
			Thread.sleep(1000L);
		}
		catch (InterruptedException e) {
		}
		
		// create backbuffer
		if (bufferstrategy) {
			bufferstrategy = this.createBufferStrategy();
		}
		
		if (!bufferstrategy) {
			this.createBackBuffer();
		}
		
		this.frame.requestFocus();
	}
	
	/** ************************************************************************* */
	/** ************************ GRAPHICS FUNCTION ****************************** */
	/** ************************************************************************* */
	
	private boolean createBufferStrategy() {
		// create bufferstrategy
		boolean bufferCreated;
		int num = 0;
		do {
			bufferCreated = true;
			try {
				// create bufferstrategy
				this.frame.createBufferStrategy(2);
			}
			catch (Exception e) {
				// unable to create bufferstrategy!
				bufferCreated = false;
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException excp) {
				}
			}
			
			if (num++ > 5) {
				break;
			}
		} while (!bufferCreated);
		
		if (!bufferCreated) {
			System.err.println("BufferStrategy is not available!");
			return false;
		}
		
		// wait until bufferstrategy successfully setup
		while (this.strategy == null) {
			try {
				this.strategy = this.frame.getBufferStrategy();
			}
			catch (Exception e) {
			}
		}
		
		// wait until backbuffer successfully setup
		Graphics2D gfx = null;
		while (gfx == null) {
			// this process will throw an exception
			// if the backbuffer has not been created yet
			try {
				gfx = this.getBackBuffer();
			}
			catch (Exception e) {
			}
		}
		
		return true;
	}
	
	private void createBackBuffer() {
		if (this.offscreen != null) {
			// backbuffer is already created,
			// but not validate with current graphics context
			this.offscreen.flush();
			
			// clear old backbuffer
			this.offscreen = null;
		}
		
		this.offscreen = FullScreenMode.CONFIG.createCompatibleVolatileImage(
		        this.size.width, this.size.height);
	}
	
	public Graphics2D getBackBuffer() {
		if (this.currentGraphics == null) {
			// graphics context is not created yet,
			// or have been dispose by calling flip()
			
			if (this.strategy == null) {
				// using volatile image
				// let see if the volatile image is still validate or not
				if (this.offscreen.validate(FullScreenMode.CONFIG) == VolatileImage.IMAGE_INCOMPATIBLE) {
					// volatile image is not valid
					this.createBackBuffer();
				}
				
				this.currentGraphics = this.offscreen.createGraphics();
				
			}
			else {
				// using buffer strategy
				this.currentGraphics = (Graphics2D) this.strategy
				        .getDrawGraphics();
			}
		}
		
		return this.currentGraphics;
	}
	
	public boolean flip() {
		// disposing current graphics context
		this.currentGraphics.dispose();
		this.currentGraphics = null;
		
		// show to screen
		if (this.strategy == null) {
			this.frame.getGraphics().drawImage(this.offscreen, 0, 0, null);
			
			// sync the display on some systems.
			// (on linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
			
			return (!this.offscreen.contentsLost());
			
		}
		else {
			this.strategy.show();
			
			// sync the display on some systems.
			// (on linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
			
			return (!this.strategy.contentsLost());
		}
	}
	
	/** ************************************************************************* */
	/** ******************* DISPOSING GRAPHICS ENGINE *************************** */
	/** ************************************************************************* */
	
	public void cleanup() {
		try {
			Thread.sleep(500L);
		}
		catch (InterruptedException e) {
		}
		
		try {
			// exit fullscreen mode
			// DEVICE.setFullScreenWindow(null);
			
			Thread.sleep(200L);
			
			// dispose the frame
			if (this.frame != null) {
				this.frame.dispose();
			}
		}
		catch (Exception e) {
			System.err.println("ERROR: Shutting down graphics context " + e);
			System.exit(-1);
		}
	}
	
	/** ************************************************************************* */
	/** *************************** PROPERTIES ********************************** */
	/** ************************************************************************* */
	
	public Dimension getSize() {
		return this.size;
	}
	
	public Component getComponent() {
		return this.frame;
	}
	
	/**
	 * Returns the top level frame where this graphics engine is being put on.
	 * @return The top level frame.
	 */
	public Frame getFrame() {
		return this.frame;
	}
	
	/**
	 * Returns whether this graphics engine is using buffer strategy or volatile
	 * image.
	 * @return If a buffer strategy or a volatile image is used.
	 */
	public boolean isBufferStrategy() {
		return (this.strategy != null);
	}
	
	public String getGraphicsDescription() {
		return "Full Screen Mode [" + this.getSize().width + "x"
		        + this.getSize().height + "]"
		        + ((this.strategy != null) ? " with BufferStrategy" : "");
	}
	
	public void setWindowTitle(String st) {
		this.frame.setTitle(st);
	}
	
	public String getWindowTitle() {
		return this.frame.getTitle();
	}
	
	public void setWindowIcon(Image icon) {
		try {
			this.frame.setIconImage(icon);
		}
		catch (Exception e) {
		}
	}
	
	public Image getWindowIcon() {
		return this.frame.getIconImage();
	}
	
	/** ************************************************************************* */
	/** ********************* FIND THE BEST DISPLAY MODE ************************ */
	/** ************************************************************************* */
	
	private DisplayMode getBestDisplay(Dimension size) {
		// get display mode for width x height x 32 with the optimum HZ
		DisplayMode mode[] = FullScreenMode.DEVICE.getDisplayModes();
		
		ArrayList modeList = new ArrayList();
		for (int i = 0; i < mode.length; i++) {
			if (mode[i].getWidth() == size.width
			        && mode[i].getHeight() == size.height) {
				modeList.add(mode[i]);
			}
		}
		
		if (modeList.size() == 0) {
			// request display mode for 'size' is not found!
			return null;
		}
		
		DisplayMode[] match = (DisplayMode[]) modeList
		        .toArray(new DisplayMode[0]);
		Arrays.sort(match, this);
		
		return match[0];
	}
	
	/**
	 * Sorts display mode, display mode in the first stack will be used by this
	 * graphics engine. The <code>o1</code> and <code>o2</code> are instance
	 * of <code>java.awt.DisplayMode</code>.
	 * <p>
	 * 
	 * In this comparator, the first stack (the one that this graphics engine
	 * will be used) will be display mode that has the biggest bits per pixel
	 * (bpp) and has the biggest but limited to 75Hz frequency (refresh rate).
	 */
	public int compare(Object o1, Object o2) {
		DisplayMode mode1 = (DisplayMode) o1;
		DisplayMode mode2 = (DisplayMode) o2;
		
		int removed1 = (mode1.getRefreshRate() > 75) ? 5000 * mode1
		        .getRefreshRate() : 0;
		int removed2 = (mode2.getRefreshRate() > 75) ? 5000 * mode2
		        .getRefreshRate() : 0;
		
		return ((mode2.getBitDepth() - mode1.getBitDepth()) * 1000)
		        + (mode2.getRefreshRate() - mode1.getRefreshRate())
		        - (removed2 - removed1);
	}
	
}
