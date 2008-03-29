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
package com.golden.gamedev.engine.lwjgl;

// JFC
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.golden.gamedev.engine.BaseGraphics;
import com.golden.gamedev.engine.graphics.WindowExitListener;

/**
 * Graphics engine for OpenGL LWJGL Environment, <br>
 * LWJGL is available to download at <a href="http://lwjgl.org/"
 * target="_blank">http://lwjgl.org/</a>.
 * <p>
 * 
 * Make sure the downloaded library is included into your game classpath before
 * using this graphics engine.
 * <p>
 * 
 * <b>Note: GTGE is not associated in any way with LWJGL, this class is only
 * interfacing LWJGL to be used in GTGE. <br>
 * This class is created and has been tested to be working properly using
 * <em>LWJGL v0.95</em>.</b>
 * <p>
 * 
 * Use {@link com.golden.gamedev.OpenGLGameLoader} to load the game in OpenGL
 * LWJGL graphics engine environment.
 * 
 * @see com.golden.gamedev.OpenGLGameLoader
 * @see <a href="http://lwjgl.org/" target="_blank">LWJGL official site</a>
 */
public class LWJGLMode implements BaseGraphics, Comparator {
	
	/** ****************************** PROPERTIES ******************************* */
	
	private Dimension size;
	private boolean fullscreen;
	private boolean vsync;
	
	/** *************************** WINDOW LISTENER ***************************** */
	
	private WindowListener windowListener;
	
	/** *************************** BACK BUFFER ********************************* */
	
	private Graphics2D currentGraphics;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Constructs FullScreen or Windowed OpenGL Display with specified size, and
	 * whether want to turn on vsync or not.
	 */
	public LWJGLMode(Dimension d, boolean fullscreen, boolean vsync) {
		this.size = d;
		this.fullscreen = fullscreen;
		this.vsync = vsync;
		
		this.initGraphics();
		this.initGL();
		
		// init fake Graphics2D (LWJGL Graphics)
		this.currentGraphics = new LWJGLGraphics();
		
		// init fake WindowListener
		this.windowListener = WindowExitListener.getInstance();
	}
	
	/**
	 * Constructs FullScreen or Windowed OpenGL Display with specified size, and
	 * by default vsync is turned on.
	 */
	public LWJGLMode(Dimension d, boolean fullscreen) {
		this(d, fullscreen, true);
	}
	
	private void initGL() {
		// init GL
		// enable textures since we're going to use these for our sprites
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// disable the OpenGL depth test since we're rendering 2D graphics
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GL11.glOrtho(0, this.size.width, this.size.height, 0, -1, 1);
		
		// enable transparency
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void initGraphics() {
		// setting display mode using util class in LWJGL library to
		// and that means 105 KB addition, not a wise choice! :-)
		// DisplayMode[] dm = org.lwjgl.util.Display.getAvailableDisplayModes(
		// size.width, size.height,
		// -1, -1, -1, -1, 60, 60);
		//
		// try {
		// org.lwjgl.util.Display.setDisplayMode(dm, new String[] {
		// "width=" + size.width,
		// "height=" + size.height,
		// "freq=" + 60,
		// "bpp=" + Display.getDisplayMode().getBitsPerPixel()
		// } );
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		//
		// Sys.alert("Graphics Engine Initialization",
		// "Unable to set desired display " +
		// "mode ("+size.width+"x"+size.height+"x"+16+").");
		//
		// System.exit(-1);
		// }
		
		try {
			DisplayMode best = (this.fullscreen) ? this
			        .getBestDisplay(this.size) : new DisplayMode(
			        this.size.width, this.size.height);
			if (best == null) {
				throw new Exception();
			}
			
			Display.setDisplayMode(best);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			
			throw new RuntimeException("LWJGL Error: "
			        + "Unable to set desired display " + "mode ("
			        + this.size.width + "x" + this.size.height + "x" + 16 + ")");
		}
		
		try {
			Display.setTitle("Golden T Game Engine");
			Display.setFullscreen(this.fullscreen);
			Display.setVSyncEnabled(this.vsync);
			Display.create();
		}
		catch (Exception e) {
			e.printStackTrace();
			
			throw new RuntimeException(
			        "LWJGL Error: Unable to initialize display");
		}
	}
	
	/** ************************************************************************* */
	/** ************************* GRAPHICS FUNCTION ***************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public Graphics2D getBackBuffer() {
		return this.currentGraphics;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean flip() {
		if (Display.isCloseRequested()) {
			this.windowListener.windowClosing(null);
			
		}
		else if (Display.isActive()) {
			Display.update();
			
		}
		else {
			if (Display.isVisible() || Display.isDirty()) {
				Display.update();
			}
		}
		
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
			// destroy the display
			Display.destroy();
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
		return null;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public String getGraphicsDescription() {
		return "OpenGL LWJGL "
		        + ((this.fullscreen) ? "Full Screen" : "Windowed") + " Mode ["
		        + this.getSize().width + "x" + this.getSize().height + "]"
		        + ((this.vsync) ? " with VSync" : "");
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void setWindowTitle(String st) {
		Display.setTitle(st);
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public String getWindowTitle() {
		return Display.getTitle();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void setWindowIcon(Image icon) {
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public Image getWindowIcon() {
		return null;
	}
	
	/** ************************************************************************* */
	/** ************************** SPECIFIC FUNCTION **************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns whether this graphics engine is in fullscreen mode or not.
	 */
	public boolean isFullScreen() {
		return this.fullscreen;
	}
	
	/**
	 * Sets fullscreen mode of this graphics engine.
	 * 
	 * @param b true, to change the mode to fullscreen mode
	 * @return whether the operation is succeed or not.
	 */
	public boolean setFullScreen(boolean b) {
		if (this.fullscreen != b) {
			try {
				Display.setFullscreen(b);
			}
			catch (LWJGLException e) {
				return false;
			}
			
			this.fullscreen = b;
		}
		
		return true;
	}
	
	/**
	 * Returns whether this graphics engine is vsync to display mode refresh
	 * rate or not.
	 */
	public boolean isVSync() {
		return this.vsync;
	}
	
	/**
	 * Sets vsync mode of this graphics engine.
	 */
	public void setVSync(boolean b) {
		if (this.vsync != b) {
			Display.setVSyncEnabled(b);
			this.vsync = b;
		}
	}
	
	/** ************************************************************************* */
	/** ************************ WINDOW LISTENER ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Sets fake window listener used by this graphics engine to listen for
	 * window closing.
	 */
	public void setWindowListener(WindowListener l) {
		this.windowListener = l;
	}
	
	/**
	 * Returns fake window listener used by this graphics engine to listen for
	 * window closing.
	 */
	public WindowListener getWindowListener() {
		return this.windowListener;
	}
	
	/** ************************************************************************* */
	/** ********************* FIND THE BEST DISPLAY MODE ************************ */
	/** ************************************************************************* */
	
	private DisplayMode getBestDisplay(Dimension size) {
		try {
			// get display mode for width x height x 32 with the optimum HZ
			DisplayMode[] mode = Display.getAvailableDisplayModes();
			
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
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Sorts display mode, display mode in the first stack will be used by the
	 * game. The <code>o1</code> and <code>o2</code> are instance of
	 * org.lwjgl.opengl.DisplayMode.
	 * <p>
	 * 
	 * In this comparator, the first stack (the one that the game will use)
	 * would be display mode that has the biggest bits per pixel (bpp) and has
	 * the biggest but limited to 75Hz frequency (refresh rate).
	 */
	public int compare(Object o1, Object o2) {
		DisplayMode mode1 = (DisplayMode) o1;
		DisplayMode mode2 = (DisplayMode) o2;
		
		int removed1 = (mode1.getFrequency() > 75) ? 5000 * mode1
		        .getFrequency() : 0;
		int removed2 = (mode2.getFrequency() > 75) ? 5000 * mode2
		        .getFrequency() : 0;
		
		return ((mode2.getBitsPerPixel() - mode1.getBitsPerPixel()) * 1000)
		        + (mode2.getFrequency() - mode1.getFrequency())
		        - (removed2 - removed1);
	}
	
}
