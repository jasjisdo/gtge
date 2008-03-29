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
package com.golden.gamedev;

// JFC
import java.awt.Dimension;
import java.lang.reflect.Constructor;

import javax.swing.JOptionPane;

import com.golden.gamedev.engine.graphics.WindowExitListener;
import com.golden.gamedev.engine.jogl.JOGLFullScreenMode;
import com.golden.gamedev.engine.jogl.JOGLWindowedMode;
import com.golden.gamedev.engine.lwjgl.LWJGLInput;
import com.golden.gamedev.engine.lwjgl.LWJGLMode;
import com.golden.gamedev.engine.lwjgl.LWJGLTimer;

/**
 * Extending <code>GameLoader</code> class for loading game with the support
 * of OpenGL renderer using LWJGL or JOGL.
 * <p>
 * 
 * You must download LWJGL and/or JOGL library from its official site to use
 * this class. <br>
 * Download LWJGL library if you want to use OpenGL via LWJGL. <br>
 * Download JOGL library if you want to use OpenGL via JOGL. <br>
 * Or download both libraries if you want your game can be running in both mode.
 * <p>
 * 
 * LWJGL is available to download at <a href="http://lwjgl.org/"
 * target="_blank">http://lwjgl.org/</a>. <br>
 * JOGL is available to download at <a href="https://jogl.dev.java.net/"
 * target="_blank">https://jogl.dev.java.net/</a>.
 * <p>
 * 
 * Make sure the downloaded library is included into your game classpath before
 * using this class.
 * <p>
 * 
 * <b>Note: GTGE is not associated in any way with LWJGL and JOGL, this class is
 * only interfacing LWJGL and JOGL to be used in GTGE. <br>
 * This class is created and has been tested to be working properly using
 * <em>LWJGL v0.95</em> and <em>JOGL v1.1b-08</em>.</b>
 * <p>
 * 
 * Example how-to-use <code>OpenGLGameLoader</code> :
 * 
 * <pre>
 * public class YourGame extends Game {
 * 	
 * 	public static void main(String[] args) {
 *          &lt;b&gt;OpenGLGameLoader&lt;/b&gt; game = new OpenGLGameLoader();
 *          // init game with OpenGL LWJGL fullscreen mode
 *          // 640x480 screen resolution
 *          game.setupLWJGL(&lt;b&gt;new YourGame()&lt;/b&gt;, new Dimension(640,480), true);
 *          // init game with OpenGL JOGL fullscreen mode
 *          // 640x480 screen resolution
 *          //game.setupJOGL(&lt;b&gt;new YourGame()&lt;/b&gt;, new Dimension(640,480), true);
 *          // init game with Java2D fullscreen mode
 *          // 640x480 screen resolution
 *          //game.setup(&lt;b&gt;new YourGame()&lt;/b&gt;, new Dimension(640,480), true);
 *          // pick the graphics engine you like
 *          // in this example we pick OpenGL via LWJGL
 *          game.start();
 *       }
 * }
 * </pre>
 * 
 * @see <a href="http://lwjgl.org/" target="_blank">LWJGL official site</a>
 * @see <a href="https://jogl.dev.java.net/" target="_blank">JOGL official site</a>
 */
public class OpenGLGameLoader extends GameLoader {
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4328052696862787392L;
	
	/**
	 * Constructs new <code>OpenGLGameLoader</code>.
	 * 
	 * @see #setupLWJGL(Game, Dimension, boolean, boolean)
	 * @see #setupJOGL(Game, Dimension, boolean, boolean)
	 */
	public OpenGLGameLoader() {
	}
	
	/** ************************************************************************* */
	/** ****************** SETUP GAME IN OPENGL VIA LWJGL *********************** */
	/** ************************************************************************* */
	
	/**
	 * Initializes OpenGL LWJGL graphics engine with specified size, mode, and
	 * associates it with specified <code>Game</code> object.
	 */
	public void setupLWJGL(Game game, Dimension d, boolean fullscreen, boolean vsync) {
		try {
			// validate java version first
			if (!this.validJavaVersion()) {
				// not valid java version!!
				JOptionPane
				        .showMessageDialog(
				                null,
				                "Sorry, this game requires Java "
				                        + this.MINIMUM_VERSION
				                        + "++ installed\n"
				                        + "Your machine only has Java "
				                        + GameLoader.JAVA_VERSION
				                        + " installed\n\n"
				                        + "Please install the latest Java Runtime Edition (JRE)\n"
				                        + "from http://www.java.com",
				                "Game Initialization",
				                JOptionPane.ERROR_MESSAGE);
				
				// don't bother to continue
				System.exit(-1);
			}
			
			// time to create the OpenGL Graphics Engine
			LWJGLMode mode = new LWJGLMode(d, fullscreen, vsync);
			mode.setWindowListener(this);
			
			this.gfx = mode;
			
			this.game = game;
			this.game.bsGraphics = this.gfx;
			this.game.bsInput = new LWJGLInput();
			this.game.bsTimer = new LWJGLTimer();
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null,
			        "Your machine does not support OpenGL via LWJGL!\n"
			                + "Caused by: " + e.toString() + "\n"
			                + "Fail back to Java2D Graphics Engine.",
			        "Game Initialization", JOptionPane.ERROR_MESSAGE);
			
			if (this.gfx != null) {
				this.gfx.cleanup();
			}
			
			this.setup(game, d, fullscreen, false);
		}
	}
	
	/**
	 * Initializes OpenGL LWJGL graphics engine with specified size, mode, using
	 * vsync by default, and associates it with specified <code>Game</code>
	 * object.
	 */
	public void setupLWJGL(Game game, Dimension d, boolean fullscreen) {
		this.setupLWJGL(game, d, fullscreen, true);
	}
	
	/** ************************************************************************* */
	/** ******************* SETUP GAME IN OPENGL VIA JOGL *********************** */
	/** ************************************************************************* */
	
	/**
	 * Initializes OpenGL JOGL graphics engine with specified size, mode, and
	 * associates it with specified <code>Game</code> object.
	 */
	public void setupJOGL(Game game, Dimension d, boolean fullscreen, boolean vsync) {
		boolean orig = fullscreen;
		try {
			// validate java version first
			if (!this.validJavaVersion()) {
				// not valid java version!!
				JOptionPane
				        .showMessageDialog(
				                null,
				                "Sorry, this game requires Java "
				                        + this.MINIMUM_VERSION
				                        + "++ installed\n"
				                        + "Your machine only has Java "
				                        + GameLoader.JAVA_VERSION
				                        + " installed\n\n"
				                        + "Please install the latest Java Runtime Edition (JRE)\n"
				                        + "from http://www.java.com",
				                "Game Initialization",
				                JOptionPane.ERROR_MESSAGE);
				
				// don't bother to continue
				System.exit(-1);
			}
			
			// time to create the OpenGL Graphics Engine
			
			if (fullscreen) {
				// fullscreen mode
				JOGLFullScreenMode mode = null;
				try {
					// using reflection to load the class, this is a work around
					// to
					// avoid
					// JOGL static initialization exception when JOGL library is
					// not
					// included in the bundle, when the game is not using JOGL
					// graphics engine
					Class joglClass = Class
					        .forName("com.golden.gamedev.engine.jogl.JOGLFullScreenMode");
					Constructor joglConstructor = joglClass
					        .getConstructor(new Class[] {
					                Dimension.class, boolean.class
					        });
					
					mode = (JOGLFullScreenMode) joglConstructor
					        .newInstance(new Object[] {
					                d, new Boolean(vsync)
					        });
					mode.getFrame().removeWindowListener(
					        WindowExitListener.getInstance());
					mode.getFrame().addWindowListener(this);
					
					this.gfx = mode;
				}
				catch (Throwable e) {
					// the first exception is
					// the exception because of class creation via reflection
					// we need to know what is the actual exception!
					if (e.getCause() != null) {
						e = e.getCause();
					}
					
					e.printStackTrace();
					
					JOptionPane.showMessageDialog(null,
					        "ERROR: Entering JOGL FullScreen Mode\n"
					                + "Caused by: " + e.toString(),
					        "Graphics Engine Initialization",
					        JOptionPane.ERROR_MESSAGE);
					// fail-safe
					fullscreen = false;
					
					if (mode != null) {
						mode.cleanup();
					}
				}
			}
			
			if (!fullscreen) {
				// using reflection to load the class, this is a work around to
				// avoid
				// JOGL static initialization exception when JOGL library is not
				// included in the bundle, when the game is not using JOGL
				// graphics
				// engine
				Class joglClass = Class
				        .forName("com.golden.gamedev.engine.jogl.JOGLWindowedMode");
				Constructor joglConstructor = joglClass
				        .getConstructor(new Class[] {
				                Dimension.class, boolean.class
				        });
				
				JOGLWindowedMode mode = (JOGLWindowedMode) joglConstructor
				        .newInstance(new Object[] {
				                d, new Boolean(vsync)
				        });
				mode.getFrame().removeWindowListener(
				        WindowExitListener.getInstance());
				mode.getFrame().addWindowListener(this);
				
				this.gfx = mode;
			}
			
			this.game = game;
			this.game.bsGraphics = this.gfx;
			
		}
		catch (Throwable e) {
			// the first exception is
			// the exception because of class creation via reflection
			// we need to know what is the actual exception!
			if (e.getCause() != null) {
				e = e.getCause();
			}
			
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null,
			        "Your machine does not support OpenGL via JOGL!\n"
			                + "Caused by: " + e.toString() + "\n"
			                + "Fail back to Java2D Graphics Engine.",
			        "Game Initialization", JOptionPane.ERROR_MESSAGE);
			
			if (this.gfx != null) {
				this.gfx.cleanup();
			}
			
			this.setup(game, d, orig, false);
		}
	}
	
	/**
	 * Initializes OpenGL JOGL graphics engine with specified size, mode, using
	 * vsync by default, and associates it with specified <code>Game</code>
	 * object.
	 */
	public void setupJOGL(Game game, Dimension d, boolean fullscreen) {
		this.setupJOGL(game, d, fullscreen, true);
	}
	
}
