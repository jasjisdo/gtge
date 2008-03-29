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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.golden.gamedev.engine.BaseGraphics;
import com.golden.gamedev.engine.graphics.AppletMode;
import com.golden.gamedev.engine.graphics.FullScreenMode;
import com.golden.gamedev.engine.graphics.WindowExitListener;
import com.golden.gamedev.engine.graphics.WindowedMode;
import com.golden.gamedev.funbox.ErrorNotificationDialog;

/**
 * <code>GameLoader</code> class is the class that manages <code>Game</code>
 * class initialization.
 * <p>
 * 
 * <code>GameLoader</code> handles <code>Game</code> class
 * {@linkplain com.golden.gamedev.engine.graphics graphics engine}
 * initialization, validating {@linkplain #MINIMUM_VERSION user Java version},
 * and also catch any unexpected runtime error in <code>Applet</code> mode.
 * <p>
 * 
 * Example of how-to-use <code>GameLoader</code> in application mode :
 * 
 * <pre>
 * public class YourGame extends Game {
 * 	
 * 	public static void main(String[] args) {
 *          &lt;b&gt;GameLoader&lt;/b&gt; game = new GameLoader();
 *          // init game with fullscreen mode, 640x480 screen resolution
 *          game.setup(&lt;b&gt;new YourGame()&lt;/b&gt;, new Dimension(640,480), true);
 *          game.start();
 *       }
 * }
 * </pre>
 * 
 * <p>
 * 
 * In applet mode, <code>GameLoader</code> class need to be subclassed and
 * override {@link #createAppletGame()} method to return the actual game :
 * 
 * <pre>
 * public class YourGameApplet extends GameLoader {
 * 	
 * 	protected Game createAppletGame() {
 * 		return new YourGame();
 * 	}
 * }
 * </pre>
 * 
 * Then point the applet tag code to that <code>GameLoader</code> subclass :
 * <br>
 * (html page)
 * 
 * <pre>
 *    &lt;html&gt;
 *    &lt;head&gt;
 *    &lt;/head&gt;
 *    &lt;body&gt;
 *    &lt;applet code=&quot;&lt;b&gt;gamepackage.YourGameApplet.class&lt;/b&gt;&quot;
 *            archive=&quot;&lt;b&gt;yourgamearchive.jar,golden_x_x_x.jar&lt;/b&gt;&quot;
 *            width=640 height=480&gt;
 *    &lt;/applet&gt;
 *    &lt;/body&gt;
 *    &lt;/html&gt;
 * </pre>
 * 
 * @see #setup(Game, Dimension, boolean, boolean)
 * @see #start()
 * @see #createAppletGame()
 */
public class GameLoader extends AppletMode implements WindowListener, Runnable {
	
	/** ************************* VERSION VALIDATOR ***************************** */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7164725885243400217L;
	
	/**
	 * The installed Java Virtual Machine (JVM) version on player machine.
	 */
	public static final String JAVA_VERSION;
	
	static {
		String ver = "1.4.1";
		
		try {
			ver = System.getProperty("java.version");
		}
		catch (Exception e) {
		}
		
		JAVA_VERSION = ver;
	}
	
	/**
	 * The minimum Java version that the game known can be run properly. The
	 * default is 1.4.
	 * <p>
	 * 
	 * How to change the minimum Java version :
	 * 
	 * <pre>
	 *    GameLoader game = new GameLoader();
	 *    game.MINIMUM_VERSION = &quot;1.4.2&quot;;
	 *    game.setup(....);
	 *    game.start();
	 * </pre>
	 * 
	 * In applet mode, minimum version is specified in applet param 'MINIMUM'
	 * tag. <br>
	 * For example:
	 * 
	 * <pre>
	 *    &lt;PARAM NAME=&quot;MINIMUM&quot; VALUE=&quot;1.4.2&quot;&gt;
	 * </pre>
	 */
	public String MINIMUM_VERSION = "1.4";
	
	private boolean VALID_JAVA_VERSION = true;
	
	/** *************************** INFO MESSAGES ******************************* */
	
	private String[] INFO_MSG = new String[] {
		"Loading Game, please wait a moment"
	};
	
	/** ************************* LOADER PROPERTIES ***************************** */
	
	/**
	 * Graphics engine loaded by this <code>GameLoader</code>.
	 */
	protected BaseGraphics gfx;
	
	/**
	 * The game (subclass of <code>Game</code> class) initialized by this
	 * <code>GameLoader</code>.
	 */
	protected Game game;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Constructs new <code>GameLoader</code>.
	 * 
	 * @see #setup(Game, Dimension, boolean, boolean)
	 * @see #start()
	 */
	public GameLoader() {
	}
	
	/** ************************************************************************* */
	/** ************************ START / STOP GAME ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Starts the game that have been loaded by this loader.
	 * 
	 * @see #setup(Game, Dimension, boolean, boolean)
	 */
	public void start() {
		if (!this.VALID_JAVA_VERSION) {
			// java version is not valid!
			return;
		}
		
		if (this.gfx != null) {
			// graphics has been initialized
			// time to start the game
			if (this.game != null) {
				this.game.start();
			}
			
			return;
		}
		
		try {
			// getParameter() will fail if this is not in applet environment
			String param = this.getParameter("MINIMUM_VERSION");
			
			if (param != null) {
				// developer has defined a custom minimum Java version
				this.MINIMUM_VERSION = param;
			}
			
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null,
			        "ERROR: GameLoader.setup(Game, Dimension, Fullscreen); need to be called\n"
			                + "before calling GameLoader.start();",
			        "Game Initialization", JOptionPane.ERROR_MESSAGE);
			
			System.exit(0);
		}
		
		// ********** if above method successfully passed ***********//
		// ********** this must be in applet environment! ***********//
		
		// java version validation
		if (!this.validJavaVersion()) {
			// not valid java version!!
			this.INFO_MSG = new String[] {
			        "Sorry, this game requires Java " + this.MINIMUM_VERSION
			                + "++",
			        "Your machine only has Java " + GameLoader.JAVA_VERSION
			                + " installed", "",
			        "Install the latest Java Runtime Edition (JRE)",
			        "from http://www.java.com"
			};
			
			return;
		}
		
		try {
			if (this.game == null) {
				// time to initialize applet game
				this.game = this.createAppletGame();
				
				if (this.game == null) {
					// game failed to initialized
					JOptionPane
					        .showMessageDialog(
					                null,
					                "FATAL ERROR: Game main-class is not specified!\n"
					                        + "Please subclass GameLoader class and override\n"
					                        + "createAppletGame() method to return your game main class.\n\n"
					                        + "For example :\n"
					                        + "public class YourGameApplet extends GameLoader {\n"
					                        + "   protected Game createAppletGame() {\n"
					                        + "      return new YourGame();\n"
					                        + "   }\n" + "}",
					                "Game Initialization",
					                JOptionPane.ERROR_MESSAGE);
					
					this.VALID_JAVA_VERSION = false;
					return;
				}
				
				this.gfx = this;
				this.game.bsGraphics = this.gfx;
			}
			
			// initialization of applet mode graphics engine
			super.start();
			
			// start the game
			new Thread(this).start();
			
		}
		catch (Throwable e) {
			this.INFO_MSG = new String[] {
			        "UNRECOVERABLE ERROR", "PLEASE CONTACT THE GAME AUTHOR"
			};
			
			this.removeAll();
			
			// draw the error messages in standard paint() method
			this.setIgnoreRepaint(false);
			
			// show the exception dialog to the user
			new ErrorNotificationDialog(e, this, this.getClass().getName(),
			        null);
		}
	}
	
	/**
	 * Stops the game from running, to resume the game call {@link #start()}
	 * again, please <b>see note</b> below.
	 * <p>
	 * 
	 * By default this method is only called by browser in Applet Game.
	 * <p>
	 * 
	 * Browser calls this method automatically when the web page contain the
	 * game is been replaced by another page and just before the applet is going
	 * to be destroyed. <br>
	 * The <code>start()</code> method is also automatically called by the
	 * browser each time the applet is being revisited.
	 */
	public void stop() {
		if (this.game != null) {
			this.game.stop();
		}
	}
	
	/**
	 * In applet environment the game have to be started in a thread, this is
	 * the thread implementation that actually launch the applet game.
	 */
	public final void run() {
		if (this.game != null) {
			this.game.start();
		}
	}
	
	/**
	 * To play game in applet environment, <code>GameLoader</code> class need
	 * to be subclassed and this method need to be override to return the actual
	 * game object.
	 * <p>
	 * 
	 * For example :
	 * 
	 * <pre>
	 * public class YourGameApplet extends GameLoader {
	 * 	
	 * 	protected Game createAppletGame() {
	 * 		return new YourGame();
	 * 	}
	 * }
	 * </pre>
	 */
	protected Game createAppletGame() {
		try {
			String className = this.getParameter("GAME");
			// System.out.println(className);
			
			if (className != null) {
				if (className.endsWith(".class")) {
					// omit '.class'
					className = className.substring(0, className.length() - 6);
				}
				
				Class mainClass = Class.forName(className);
				
				return (Game) mainClass.newInstance();
			}
		}
		catch (Throwable e) {
		}
		
		return null;
	}
	
	/** ************************************************************************* */
	/** ***************************** GAME SETUP ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Initializes graphics engine with specified size, mode, bufferstrategy,
	 * and associates it with specified <code>Game</code> object.
	 */
	public void setup(Game game, Dimension d, boolean fullscreen, boolean bufferstrategy) {
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
			
			// time to create the graphics engine
			if (fullscreen) {
				// fullscreen mode
				FullScreenMode mode = null;
				try {
					mode = new FullScreenMode(d, bufferstrategy);
					mode.getFrame().removeWindowListener(
					        WindowExitListener.getInstance());
					mode.getFrame().addWindowListener(this);
					
					this.gfx = mode;
				}
				catch (Throwable e) {
					e.printStackTrace();
					
					JOptionPane.showMessageDialog(null,
					        "ERROR: Entering FullScreen Mode\n" + "Caused by: "
					                + e.toString(),
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
				// windowed mode
				WindowedMode mode = new WindowedMode(d, bufferstrategy);
				mode.getFrame().removeWindowListener(
				        WindowExitListener.getInstance());
				mode.getFrame().addWindowListener(this);
				
				this.gfx = mode;
			}
			
			this.game = game;
			this.game.bsGraphics = this.gfx;
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			
			JOptionPane
			        .showMessageDialog(
			                null,
			                "Fatal Error: Failed to initialize game environment!\n"
			                        + "Caused by:\n"
			                        + "       "
			                        + e
			                        + "\n"
			                        + "Please send above exception to the Game Author.\n",
			                "Game Initialization", JOptionPane.ERROR_MESSAGE);
			
			System.exit(-1);
		}
	}
	
	/**
	 * Initializes graphics engine with specified size, mode, using
	 * bufferstrategy by default, and associates it with specified
	 * <code>Game</code> object.
	 */
	public void setup(Game game, Dimension d, boolean fullscreen) {
		this.setup(game, d, fullscreen, true);
	}
	
	/**
	 * Returns the game associated with this game loader or null if this game
	 * loader has not loaded any game.
	 */
	public Game getGame() {
		return this.game;
	}
	
	/** ************************************************************************* */
	/** *************************** INFO NOTIFIER ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Draw essentials informations in applet game, for example : draw info when
	 * waiting the game to show up, or when the game is throwing an exception.
	 */
	public void paint(Graphics g) {
		// background
		int width = this.getSize().width, height = this.getSize().height;
		
		g.setColor(new Color(255, 0, 0));
		g.fillRect(0, 0, width, height);
		
		// draw the error messages
		g.setFont(new Font("Monospaced", Font.PLAIN, 16));
		FontMetrics fm = g.getFontMetrics();
		
		int y = (height / 2)
		        - ((fm.getHeight() + 10) * (this.INFO_MSG.length / 2));
		g.setColor(new Color(0, 0, 0));
		
		try {
			// for smoooth text :)
			((Graphics2D) g).setRenderingHint(
			        RenderingHints.KEY_TEXT_ANTIALIASING,
			        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		catch (Exception e) {
		}
		
		for (int i = 0; i < this.INFO_MSG.length; i++) {
			g.drawString(this.INFO_MSG[i], (width / 2)
			        - (fm.stringWidth(this.INFO_MSG[i]) / 2), y);
			
			y += fm.getHeight() + 10;
		}
	}
	
	/** ************************************************************************* */
	/** ******************** VALIDATING JAVA VERSION **************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns whether the Java version is passed the minimum Java version
	 * requirement or not.
	 * <p>
	 * 
	 * Called this method before load the game to be sure the user machine has
	 * the minimum Java version to play the game.
	 * 
	 * @see #MINIMUM_VERSION
	 */
	protected boolean validJavaVersion() {
		this.VALID_JAVA_VERSION = this.isValidVersion();
		
		return this.VALID_JAVA_VERSION;
	}
	
	private boolean isValidVersion() {
		try {
			StringTokenizer versionToken = new StringTokenizer(
			        GameLoader.JAVA_VERSION, "."), minimumToken = new StringTokenizer(
			        this.MINIMUM_VERSION, ".");
			int total = (versionToken.countTokens() > minimumToken
			        .countTokens()) ? versionToken.countTokens() : minimumToken
			        .countTokens();
			String version = "", minimum = "";
			
			String parsed = "";
			for (int i = 0; i < total; i++) {
				char inc;
				try {
					inc = versionToken.nextToken().charAt(0);
				}
				catch (Exception e) {
					inc = '0';
				}
				version += inc;
				
				try {
					inc = minimumToken.nextToken().charAt(0);
					if (parsed.length() > 0) {
						parsed += ".";
					}
					parsed += inc;
				}
				catch (Exception e) {
					inc = '0';
				}
				minimum += inc;
			}
			
			int ver = Integer.parseInt(version), min = Integer
			        .parseInt(minimum);
			this.MINIMUM_VERSION = parsed;
			
			return (ver >= min);
			
		}
		catch (Exception e) {
			System.err.println("WARNING: MINIMUM_VERSION ["
			        + this.MINIMUM_VERSION + "] and/or " + "JAVA_VERSION ["
			        + GameLoader.JAVA_VERSION + "] value is not valid!");
			
			return true;
		}
	}
	
	public String getGraphicsDescription() {
		try {
			return (this.gfx != null) ? this.gfx.getGraphicsDescription()
			        : super.getGraphicsDescription();
			
		}
		catch (Exception e) {
			return (this.gfx != null) ? this.gfx.getClass().toString() : this
			        .getClass().toString();
		}
	}
	
	/** ************************************************************************* */
	/** ************************* WINDOW LISTENER ******************************* */
	/** ************************************************************************* */
	
	/**
	 * If the user pressing frame close button while playing in
	 * <code>WindowedMode</code> or <code>FullScreenMode</code>, this
	 * method will receive the closing event.
	 * <p>
	 * 
	 * In this implementation, pressing frame close button will instantly close
	 * any playing game by calling the game {@linkplain Game#finish() finish()}
	 * method.
	 * <p>
	 * 
	 * To avoid the game closed when frame close button is pressed, simply
	 * override this method or remove this listener from the graphics engine :
	 * 
	 * <pre>
	 *    GameLoader game = new GameLoader();
	 *    game.setup(...);
	 *    BaseGraphics gfx = game.getGame().bsGraphics;
	 *    // only fullscreen and window mode the game is using frame
	 *    if (gfx instanceof FullScreenMode) {
	 *       // remove this listener
	 *       ((FullScreenMode) gfx).getFrame().removeWindowListener(game);
	 *    }
	 *    if (gfx instanceof WindowedMode) {
	 *       // remove this listener
	 *       ((WindowedMode) gfx).getFrame().removeWindowListener(game);
	 *    }
	 *    game.start();
	 * </pre>
	 */
	public void windowClosing(WindowEvent e) {
		if (this.game != null) {
			this.game.finish();
		}
	}
	
	/** Do nothing. */
	public void windowOpened(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowClosed(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowIconified(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowDeiconified(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowActivated(WindowEvent e) {
	}
	
	/** Do nothing. */
	public void windowDeactivated(WindowEvent e) {
	}
	
}
