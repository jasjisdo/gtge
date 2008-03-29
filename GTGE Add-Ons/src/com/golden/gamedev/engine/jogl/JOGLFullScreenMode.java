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
package com.golden.gamedev.engine.jogl;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import net.java.games.jogl.GLCanvas;
import net.java.games.jogl.GLCapabilities;
import net.java.games.jogl.GLDrawableFactory;

import com.golden.gamedev.engine.BaseGraphics;
import com.golden.gamedev.engine.graphics.WindowExitListener;
import com.golden.gamedev.util.ImageUtil;

/**
 * Graphics engine for OpenGL JOGL Full Screen Exclusive Environment (FSEM),
 * <br>
 * JOGL is available to download at <a href="https://jogl.dev.java.net/"
 * target="_blank">https://jogl.dev.java.net/</a>.
 * <p>
 * 
 * Make sure the downloaded library is included into your game classpath before
 * using this graphics engine.
 * <p>
 * 
 * <b>Note: GTGE is not associated in any way with JOGL, this class is only
 * interfacing JOGL to be used in GTGE. <br>
 * This class is created and has been tested to be working properly using
 * <em>JOGL v1.1b-08</em>.</b>
 * <p>
 * 
 * Use {@link com.golden.gamedev.OpenGLGameLoader} to load the game in OpenGL
 * JOGL graphics engine environment.
 * 
 * @see com.golden.gamedev.OpenGLGameLoader
 * @see <a href="https://jogl.dev.java.net/" target="_blank">JOGL official site</a>
 */
public class JOGLFullScreenMode implements BaseGraphics, Comparator {
	
	/** ************************ HARDWARE DEVICE ******************************** */
	
	/**
	 * Graphics device that constructs this graphics engine.
	 */
	public static final GraphicsDevice DEVICE = GraphicsEnvironment
	        .getLocalGraphicsEnvironment().getDefaultScreenDevice();
	
	/**
	 * Graphics configuration that creates this graphics engine.
	 */
	public static final GraphicsConfiguration CONFIG = JOGLFullScreenMode.DEVICE
	        .getDefaultConfiguration();
	
	/** *************************** AWT COMPONENT ******************************* */
	
	private Frame frame;
	private Dimension size;
	
	/** *************************** JOGL COMPONENT ****************************** */
	
	private GLCanvas canvas;
	
	/** *************************** JOGL RENDERER ******************************* */
	
	private JOGLRenderer renderer;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new instance of Windowed Graphics Engine with specified size, and
	 * bufferstrategy.
	 */
	public JOGLFullScreenMode(Dimension d, boolean vsync) {
		this.size = d;
		
		// checking for FSEM hardware support
		if (!JOGLFullScreenMode.DEVICE.isFullScreenSupported()) {
			throw new RuntimeException(
			        "Full Screen Exclusive Mode is not supported");
		}
		
		// setting up the game frame
		this.frame = new Frame("Golden T Game Engine",
		        JOGLFullScreenMode.CONFIG);
		
		try {
			// set frame icon
			this.frame.setIconImage(ImageUtil.getImage(WindowExitListener.class
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
		JOGLFullScreenMode.DEVICE.setFullScreenWindow(this.frame);
		
		// check whether changing display mode is supported or not
		if (!JOGLFullScreenMode.DEVICE.isDisplayChangeSupported()) {
			JOGLFullScreenMode.DEVICE.setFullScreenWindow(null);
			this.frame.dispose();
			throw new RuntimeException("Changing Display Mode is not supported");
		}
		
		// find the best display mode
		DisplayMode bestDisplay = this.getBestDisplay(this.size);
		if (bestDisplay == null) {
			JOGLFullScreenMode.DEVICE.setFullScreenWindow(null);
			this.frame.dispose();
			throw new RuntimeException("Changing Display Mode to "
			        + this.size.width + "x" + this.size.height
			        + " is not supported");
		}
		
		// change screen display mode
		JOGLFullScreenMode.DEVICE.setDisplayMode(bestDisplay);
		
		// finish setup fullscreen mode
		
		// time to create OpenGL canvas
		// create the JOGL event listener and renderer
		this.renderer = new JOGLRenderer(vsync);
		
		this.canvas = GLDrawableFactory.getFactory().createGLCanvas(
		        new GLCapabilities());
		this.canvas.addGLEventListener(this.renderer);
		this.canvas.setNoAutoRedrawMode(true);
		
		this.canvas.setFocusable(true);
		this.canvas.setSize(this.size);
		
		// add to the frame
		this.frame.add(this.canvas);
		
		try {
			Thread.sleep(200L);
		}
		catch (InterruptedException e) {
		}
		
		this.canvas.setRenderingThread(Thread.currentThread());
		this.canvas.display();
		
		try {
			Thread.sleep(500L);
		}
		catch (InterruptedException e) {
		}
	}
	
	/** ************************************************************************* */
	/** ************************ GRAPHICS FUNCTION ****************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public Graphics2D getBackBuffer() {
		return this.renderer.getRenderer();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean flip() {
		this.canvas.display();
		
		return true;
	}
	
	/** ************************************************************************* */
	/** ******************* DISPOSING GRAPHICS ENGINE *************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void cleanup() {
		try {
			Thread.sleep(500L);
		}
		catch (InterruptedException e) {
		}
		
		try {
			// exit fullscreen mode
			// device.setFullScreenWindow(null);
			
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
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public Dimension getSize() {
		return this.size;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public Component getComponent() {
		return this.canvas;
	}
	
	/**
	 * Returns the top-level frame of this graphics engine.
	 */
	public Frame getFrame() {
		return this.frame;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public String getGraphicsDescription() {
		return "JOGL FullScreen Mode [" + this.getSize().width + "x"
		        + this.getSize().height + "]"
		        + ((this.isVSync()) ? " with VSync" : "");
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void setWindowTitle(String st) {
		this.frame.setTitle(st);
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public String getWindowTitle() {
		return this.frame.getTitle();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void setWindowIcon(Image icon) {
		try {
			this.frame.setIconImage(icon);
		}
		catch (Exception e) {
		}
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public Image getWindowIcon() {
		return this.frame.getIconImage();
	}
	
	/**
	 * Returns whether this graphics engine is vsync to display refresh rate or
	 * not.
	 */
	public boolean isVSync() {
		return (this.renderer != null) ? this.renderer.isVSync() : false;
	}
	
	/**
	 * Returns JOGL event listener and renderer.
	 */
	public JOGLRenderer getRenderer() {
		return this.renderer;
	}
	
	/** ************************************************************************* */
	/** ********************* FIND THE BEST DISPLAY MODE ************************ */
	/** ************************************************************************* */
	
	private DisplayMode getBestDisplay(Dimension size) {
		// get display mode for width x height x 32 with the optimum HZ
		DisplayMode mode[] = JOGLFullScreenMode.DEVICE.getDisplayModes();
		
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
	 * Sorts display mode, display mode in the first stack will be used by the
	 * game. The <code>o1</code> and <code>o2</code> are instance of
	 * java.awt.DisplayMode.
	 * <p>
	 * 
	 * In this comparator, the first stack (the one that the game will use)
	 * would be display mode that has the biggest bits per pixel (bpp) and has
	 * the biggest but limited to 75Hz frequency (refresh rate).
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
