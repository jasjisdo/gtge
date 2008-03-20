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
import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.applet.*;

// GTGE
import com.golden.gamedev.engine.*;
import com.golden.gamedev.engine.timer.*;
import com.golden.gamedev.engine.audio.*;
import com.golden.gamedev.engine.input.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.util.*;
import com.golden.gamedev.funbox.*;


/**
 * <code>Game</code> class is <b>Golden T Game Engine (GTGE) core class</b>
 * that initializes all GTGE game engines, wrap the engines up, and setup the
 * basic game frame work to be play on. <p>
 *
 * Every game is a subclass of <code>Game</code> class. And every subclass of
 * <code>Game</code> class have to do three things :
 * <ul>
 *    <li>{@linkplain #initResources() initialize game variables}</li>
 *    <li>{@linkplain #update(long) update the game variables}</li>
 *    <li>{@linkplain #render(Graphics2D) render the game to the screen}</li>
 * </ul>
 * <p>
 *
 * How-to-subclass <code>Game</code> class to create a new game : <br>
 * (this is the basic skeleton of every game)
 * <pre>
 *    import java.awt.*;
 *    import com.golden.gamedev.*;
 *
 *    public class YourGame extends Game {
 *
 *       public void initResources() {
 *          // initialize game variables
 *       }
 *
 *       public void update(long elapsedTime) {
 *          // update the game variables
 *       }
 *
 *       public void render(Graphics2D g) {
 *          // render the game to the screen
 *       }
 *
 *    }
 * </pre>
 * <p>
 *
 * And to launch/init the game use {@link com.golden.gamedev.GameLoader} class :
 * <pre>
 *    import java.awt.*;
 *    import com.golden.gamedev.*;
 *
 *    public class YourGame extends Game {
 *
 *       public void initResources() { }
 *       public void update(long elapsedTime) { }
 *       public void render(Graphics2D g) { }
 *
 *       public static void main(String[] args) {
 *          <b>GameLoader game = new GameLoader();</b>
 *
 *          // init the game with fullscreen mode, 640x480 screen resolution
 *          game.setup(<b>new YourGame()</b>, new Dimension(640,480), true);
 *
 *          game.start();
 *       }
 *
 *    }
 * </pre>
 * <p>
 *
 * There are two main tasks of <code>Game</code> class that we need to know : <br>
 * <ul>
 * <li>Game class initializes all GTGE game engines and keep the engines
 * reference (named as bsGraphics, bsInput, bsIO, etc). <br>
 * Therefore to change the default engine, do it within Game class game engine
 * initialization in {@link #initEngine()} method.</li>
 * <li>The Game class then provides a direct call to the engines commonly used
 * functions, in other word, wrapping the game engines inside the class. <br>
 * The purpose of this wrapping is to make the game coding more convenient,
 * easier, and simple. <br>
 * Therefore you can call the engines functions directly if you like to.</li>
 * </ul>
 * <p>
 *
 * @see com.golden.gamedev.GameLoader
 * @see #initEngine()
 */
public abstract class Game {


	/**
	 * Current GTGE version.
	 */
	public static final String GTGE_VERSION = "0.2.4";

	private static final int DEFAULT_FPS = 100;


 /******************************* GAME ENGINES *******************************/

    /** Graphics engine. */
    public BaseGraphics 	bsGraphics;
	/** I/O file engine. */
	public BaseIO        	bsIO;
    /** Image loader engine. */
	public BaseLoader    	bsLoader;
    /** Input engine. */
	public BaseInput     	bsInput;
    /** Timer engine. */
	public BaseTimer     	bsTimer;
	/** Audio engine for music. */
    public BaseAudio		bsMusic;
    /** Audio engine for sound. */
	public BaseAudio		bsSound;

	/** Font manager. */
	public GameFontManager	fontManager;


 /****************************** GAME VARIABLES ******************************/

	private boolean		running;	// true, indicates the game is currently
									// running/playing
	private boolean 	finish;		// true, indicates the game has been ended
									// an ended game can't be played anymore

	/**
	 * Indicates whether this game is finished and ready to distribute or still
	 * in development stage. <p>
	 *
	 * A distributed game (distribute = true) will catch any uncatch/unexpected
	 * game exception and send the error to {@link #notifyError(Throwable)}
	 * method. <p>
	 *
	 * When your game is completed and it is time to distribute the game to the
	 * world, set this distribute value to true in class initialization :
	 * <pre>
	 *    public class YourGame extends Game {
	 *
	 *       // class initialization, put it here
	 *       <b>{ distribute = true; }</b>
	 *
	 *       // do not put it in initResources() method or other place!
	 *       public void initResources() { }
	 *       public void update(long elapsedTime) { }
	 *       public void render(Graphics2D g) { }
	 *
	 *    }
	 * </pre>
	 *
	 * @see #notifyError(Throwable)
	 */
	protected boolean 	distribute;

	GameFont			fpsFont;
	private boolean		development;	// to avoid developer hack 'distribute' value
	private boolean		initialized;	// true, indicates the game has been initialized
										// used when the game is stopped and played again
										// to avoid multiple initialization
	boolean				inFocus = true;
	private boolean		inFocusBlink;
	private boolean		pauseOnLostFocus = false;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new instance of <code>Game</code> class, please <b>see note</b>
	 * below. <p>
	 *
	 * Note: <b>Do not</b> make any overloading constructors. All that belong to
	 * constructor (this method) should be put in {@link #initResources()}
	 * method. <b>Leave this method empty and simply do not use constructor!</b>
	 *
	 * @see #initResources()
	 * @see #update(long)
	 * @see #render(Graphics2D)
	 */
	public Game() {
	}


 /****************************************************************************/
 /************************* START / STOP OPERATION ***************************/
 /****************************************************************************/

	/**
	 * Stops the game from running, and to resume the game call {@link #start()}
	 * method. This method is only holding the game, to quit the game call
	 * {@link #finish()} instead. During the holding time, no action is taken,
	 * even the game rendering, therefore this method is not suitable for making
	 * game pause event. <p>
	 *
	 * By default this stop method is only called in applet environment whenever
	 * the applet stop method is executed by the webpage.
	 *
	 * @see #start()
	 * @see #finish()
	 */
	public void stop() {
		running = false;
	}

	/**
	 * End the game and back to operating system. <p>
	 *
	 * Only call this method when the game has been finished playing. Calling
	 * this method will immediatelly makes the game to quit and the game can not
	 * be resumed/played anymore.
	 *
	 * @see #stop()
	 */
	public void finish() {
		finish = true;
		stop();
	}

	/**
	 * Returns true, if the game has been finished playing and the game is about
	 * to return back to operating system.
	 */
	public boolean isFinish() {
		return finish;
	}

	/**
	 * Returns whether the game is currently running/playing or not. Running
	 * game means the game is in game main-loop (update and render loop).
	 *
	 * @see #start()
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Starts the game main loop, this method will not return until the game is
	 * finished playing/running. To stop the game use either {@link #finish()}
	 * to quit the game or {@link #stop()} to hold the game. <p>
	 *
	 * Be sure the game {@linkplain #bsGraphics graphics engine} has been
	 * initialized (not null) before attempt to call this method.
	 *
	 * @see #finish()
	 * @see #initEngine()
	 * @see #distribute
	 * @see #notifyError(Throwable)
	 * @see #notifyExit()
	 */
	public final void start() {
		if (running || finish) return;
		running = true;


		if (initialized == false) {
			// mark distribute state
			development = !distribute;
		}

		if (development == false) {
			// the game has been distributed
			// catch any unexpected/uncaught exception!
			// the logo is shown in initialize() method
			try {
				if (initialized == false) {
					initialized = true;
   					initialize();
				}

				startGameLoop();
			} catch (Throwable e) {
				notifyError(e);
			}

		} else { // still in development
			if (initialized == false) {
				initialized = true;
				initialize();
			}

			startGameLoop();
		}
	}

	private void initialize() {
		if (bsGraphics instanceof Applet) {
			// applet game need to make sure that the applet is being focused
			// when playing the game
			// this makes the players can browse on the net while playing
			setPauseOnLostFocus(true);
		}

		// init all engines
		initEngine();

		try {
			bsGraphics.getComponent().addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					inFocus = true;
				}

				public void focusLost(FocusEvent e) {
					if (pauseOnLostFocus) {
						inFocus = false;
					}
				}
			} );
		} catch (Exception e) { }


		if (development == false) {
			// show GTGE splash screen :-)
			showLogo();
		}

		// load fps font
		try {
			URL fontURL = com.golden.gamedev.Game.class.getResource("Game.fnt");
			BufferedImage fpsImage = ImageUtil.getImage(fontURL);

			fpsFont = fontManager.getFont(fpsImage);
			fontManager.removeFont(fpsImage);	// unload the image
			fontManager.putFont("FPS Font", fpsFont);

			if (development == false) {
				// if splash screen is shown (distribute = true)
				// fps font is not used anymore
				// remove the reference!
				// however the font still exists via fontManager.getFont("FPS Font");
				fpsFont = null;
			}

		} catch (Exception e) {
			// someone is trying to hack GTGE here!
			bailOut();
		}


		// before play, clear unused memory (runs garbage collector)
		System.gc();
		System.runFinalization();

		// init resources
		initResources();
	}


 /****************************************************************************/
 /************************** GAME LOOP THREAD ********************************/
 /****************************************************************************/

	void startGameLoop() {
		// before play, runs garbage collector to clear unused memory
		System.gc();
		System.runFinalization();

		// start the timer
		bsTimer.startTimer();
		bsTimer.refresh();

		long elapsedTime = 0;
		out:
		while (true) {
			if (inFocus) {
				// update game
				update(elapsedTime);
				bsInput.update(elapsedTime); 	// update input

			} else {
				// the game is not in focus!
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
				}
			}

			do {
				if (!running) {
					// if not running, quit this game
					break out;
				}

				// graphics operation
				Graphics2D g = bsGraphics.getBackBuffer();

				render(g);  	// render game

//				if (development) {
//					// if the game is still under development
//					// draw game FPS and other stuff
//
//					fpsFont.drawString(g,
//						"FPS = " + getCurrentFPS() + "/" + getFPS(),
//						9, getHeight()-21);
//
//					fpsFont.drawString(g, "GTGE", getWidth()-65, 9);
//				}

				if (!inFocus) {
					renderLostFocus(g);
				}

			} while (bsGraphics.flip() == false);


			elapsedTime = bsTimer.sleep();

			if (elapsedTime > 100) {
				// the elapsedTime can't be lower than 100 (10 fps)
				// it's a workaround so the movement is not too jumpy
				elapsedTime = 100;
			}
        }

		// stop the timer
		bsTimer.stopTimer();
		bsSound.stopAll();
		bsMusic.stopAll();

		if (finish) {
			bsGraphics.cleanup();
			notifyExit();
		}
	}

	/**
	 * Renders information when the game is not in focused.
	 *
	 * @see #setPauseOnLostFocus(boolean)
	 */
	protected void renderLostFocus(Graphics2D g) {
		String st1 = "GAME IS NOT IN FOCUSED",
			   st2 = "CLICK HERE TO GET THE FOCUS BACK";

		g.setFont(new Font("Dialog", Font.BOLD, 15));
		FontMetrics fm = g.getFontMetrics();

		int posy = (getHeight()/2) - ((fm.getHeight()+10)*(2/2));

		int x = (getWidth()/2) - (fm.stringWidth(st2)/2)-20,
			y = posy-25,
			width = fm.stringWidth(st2)+40,
			height = fm.getHeight() + fm.getHeight()+30;

		g.setColor(Color.BLACK);
		g.fillRect(x, y, width-1, height-1);
		g.setColor(Color.RED);
		g.drawRect(x, y, width-1, height-1);

		inFocusBlink = !inFocusBlink;

		if (!inFocusBlink) {
			try {
				// for smoooth text :)
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
								   				  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			} catch (Exception e) { }

			g.setColor(Color.RED);
			g.drawString(st1, (getWidth()/2) - (fm.stringWidth(st1)/2), posy);
			posy += fm.getHeight() + 10;
			g.drawString(st2, (getWidth()/2) - (fm.stringWidth(st2)/2), posy);
		}
	}

	/**
	 * Sets whether the game is paused when the game is lost the input focus or
	 * not. By default only applet game that paused when the game is lost the
	 * focus.
	 *
	 * @see #renderLostFocus(Graphics2D)
	 */
	public void setPauseOnLostFocus(boolean b) {
		pauseOnLostFocus = b;

		if (pauseOnLostFocus == false) {
			// if not paused on lost focus, make sure the game is in focus
			inFocus = true;
		}
	}

	/**
	 * Returns whether whether the game is paused when the game is lost the
	 * input focus or not.
	 *
	 * @see #renderLostFocus(Graphics2D)
	 */
	public boolean isPauseOnLostFocus() {
		return pauseOnLostFocus;
	}


 /****************************************************************************/
 /*********************** GAME ENGINE INITIALIZATION *************************/
 /****************************************************************************/

	/**
	 * Game engines is initialized in this method. <br>
	 * Thus modifying or changing any game engines should be done within this
	 * method. <p>
	 *
	 * List of default game engines initialized in this method :
	 * <ul>
	 *   <li> Timer Engine : uses {@link com.golden.gamedev.engine.timer.SystemTimer}</li>
	 *   <li> Input Engine : uses {@link com.golden.gamedev.engine.input.AWTInput}</li>
	 *   <li> Music Engine : uses {@link com.golden.gamedev.engine.audio.MidiRenderer}</li>
	 *   <li> Sound Engine : uses {@link com.golden.gamedev.engine.audio.WaveRenderer}</li>
	 *   <li> I/O Engine   : uses {@link com.golden.gamedev.engine.BaseIO}</li>
	 *   <li> Image Engine : uses {@link com.golden.gamedev.engine.BaseLoader}</li>
	 * </ul> <p>
	 *
	 * Example how to modify or change the default game engine :
	 * <pre>
	 *    protected void initEngine() {
	 *       super.initEngine();
	 *
	 *       // change the timer engine
	 *       bsTimer = new GageTimer();
	 *
	 *       // modify the music engine base renderer
	 *       bsMusic.setBaseRenderer(new JOrbisOggRenderer());
	 *    }
	 * </pre>
	 *
	 * @see #bsGraphics
	 * @see #bsIO
	 * @see #bsLoader
	 * @see #bsInput
	 * @see #bsTimer
	 * @see #bsMusic
	 * @see #bsSound
	 * @see #fontManager
	 * @see com.golden.gamedev.engine
	 */
	protected void initEngine() {
        // game engine initilialization
    	if (bsTimer == null)  bsTimer 	= new SystemTimer(); // GageTimer(); // LoraxTimer(); //
		if (bsIO == null) 	  bsIO 		= new BaseIO(getClass());
		if (bsLoader == null) bsLoader 	= new BaseLoader(bsIO, Color.MAGENTA);
		if (bsInput == null)  bsInput 	= new AWTInput(bsGraphics.getComponent());
		if (bsMusic == null) {
			bsMusic	= new BaseAudio(bsIO, new MidiRenderer());
			bsMusic.setExclusive(true);
			bsMusic.setLoop(true);
		}
		if (bsSound == null) {
		 	bsSound	= new BaseAudio(bsIO, new WaveRenderer());
		}

		// miscellanous
		// set default fps
		bsTimer.setFPS(DEFAULT_FPS);

		// set background screen size
		Background.screen = bsGraphics.getSize();

		// creates font manager
		if (fontManager == null) {
			fontManager = new GameFontManager();
		}

		// locale = Locale.getDefault();
	}


 /****************************************************************************/
 /******************************* MAIN METHODS *******************************/
 /****************************************************************************/

    /**
     * All game resources initialization, everything that usually goes to
     * constructor should be put in here. <p>
     *
     * This method is called only once for every newly created <code>Game</code>
     * class.
     *
     * @see #getImage(String)
     * @see #getImages(String, int, int)
     * @see #playMusic(String)
     * @see #setMaskColor(Color)
     * @see com.golden.gamedev.object
     */
	public abstract void initResources();

	/**
	 * Updates game variables.
	 *
	 * @see #keyDown(int)
	 * @see #keyPressed(int)
	 */
    public abstract void update(long elapsedTime);

    /**
     * Renders game to the screen.
     *
     * @param g	backbuffer graphics context
     */
    public abstract void render(Graphics2D g);


 /****************************************************************************/
 /*********************** EXIT/ERROR NOTIFICATION ****************************/
 /****************************************************************************/

	/**
	 * Notified when the game is about to quit. By default this method is
	 * calling <code>System.exit()</code> to ensure everything is properly
	 * shut down. <p>
	 *
	 * Override this method to create a custom exit dialog, and be sure to call
	 * <code>System.exit()</code> at the end.
	 */
	protected void notifyExit() {
		if ((bsGraphics instanceof Applet) == false) {
			// non-applet game should call System.exit(0);
			try {
				System.exit(0);
			} catch (Exception e) { }

		} else {
			// applet game should display to the user
			// that the game has been ended
			final Applet applet = (Applet) bsGraphics;
			BufferedImage src = ImageUtil.createImage(getWidth(), getHeight());
			Graphics2D g = src.createGraphics();

			try {
				// fill background
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());

				// play with transparency a bit
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));

				// draw in a circle only
				Shape shape = new java.awt.geom.Ellipse2D.Float(
									getWidth()/10,getHeight()/10,
									getWidth()-(getWidth()/10*2),
									getHeight()-(getHeight()/10*2));
				g.setClip(shape);

				// draw the game unto this image
				if (this instanceof GameEngine) {
					((GameEngine) this).getCurrentGame().render(g);
				}
				render(g);

				g.dispose();
			} catch (Exception e) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.dispose();
			}

			// make it as gray
			BufferedImage converted = null;
			try {
				// technique #1
//				ColorSpace gray = ColorSpace.getInstance(ColorSpace.CS_GRAY);
//				converted = new ColorConvertOp(gray, null).filter(src, null);

				// technique #2
				BufferedImage image = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
				Graphics gfx = image.getGraphics();
				gfx.drawImage(src, 0, 0, null);
				gfx.dispose();
				converted = image;

				// technique #3
//				ImageFilter filter = new GrayFilter(true, 75);
//				ImageProducer producer = new FilteredImageSource(colorImage.getSource(), filter);
//				Image mage = this.createImage(producer);

			} catch (Throwable e) {
			}
			final BufferedImage image = (converted != null) ? converted : src;


			applet.removeAll();
			applet.setIgnoreRepaint(false);

			Canvas canvas = new Canvas() {
				public void paint(Graphics g1) {
					Graphics2D g = (Graphics2D) g1;

					// draw game image
					g.drawImage(image, 0, 0, null);

					// draw text
					g.setColor(Color.YELLOW);
					g.setFont(new Font("Verdana", Font.BOLD, 12));
					g.drawString("Game has been ended", 10, 25);
					g.drawString("Thank you for playing!", 10, 45);
					g.drawString("Visit http://www.goldenstudios.or.id/", 10, 75);
					g.drawString("For free game engine!", 10, 95);
					g.drawString("This game is developed with GTGE v" + Game.GTGE_VERSION, 10, 115);
				}
			};
            canvas.setSize(applet.getSize());
			canvas.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						applet.getAppletContext().showDocument(new URL("http://goldenstudios.or.id/"));
					} catch (Exception excp) { }
				}
			} );

			applet.add(canvas);
            applet.repaint();
            canvas.repaint();
		}
	}

	/**
	 * Notified of any unexpected or uncatch error thrown by the game when the
	 * game is ready to distribute ({@link #distribute} = true). <p>
	 *
	 * By default this method creates an
	 * {@link com.golden.gamedev.funbox.ErrorNotificationDialog} to show the
	 * error to the user. <p>
	 *
	 * Override this method to make a custom error dialog, or simply use the
	 * {@linkplain com.golden.gamedev.funbox.ErrorNotificationDialog}
	 * with your email address provided so the user can directly send the
	 * exception to your email. <p>
	 *
	 * For example:
	 * <pre>
	 *    protected void notifyError(Throwable error) {
	 *       new ErrorNotificationDialog(error, bsGraphics,
	 *                                   "Game Title v1.0",       // the game title
	 *                                   "yourmail@address.com"); // your email
	 *    }
	 * </pre>
	 *
	 * @see #distribute
	 * @see com.golden.gamedev.funbox.ErrorNotificationDialog
	 */
	protected void notifyError(Throwable error) {
		new ErrorNotificationDialog(error, bsGraphics, getClass().getName(), null);
	}

	/**
	 * Returns whether this game is ready to distribute or still in development
	 * stage.
	 *
	 * @see #distribute
	 */
	public final boolean isDistribute() {
		return (development == false);
	}


 /****************************************************************************/
 /******************************* SHOW LOGO **********************************/
 /****************************************************************************/

	/**
	 * Shows GTGE logo/splash screen, GTGE is freeware library, please
	 * support GTGE by showing this logo on your game, thank you. <p>
	 *
	 * <b><u>Keep this method intact!</u></b>
	 *
	 * @see #distribute
	 * @see #notifyError(Throwable)
	 */
	public final void showLogo() {
		hideCursor();
		SystemTimer dummyTimer = new SystemTimer();
		dummyTimer.setFPS(20);
		bsInput.refresh();

		// loading GTGE logo for splash screen
		BufferedImage logo = null;
		try {
			URL logoURL = com.golden.gamedev.Game.class.getResource("Game.dat");
			BufferedImage orig = ImageUtil.getImage(logoURL);

			logo = ImageUtil.resize(orig, getWidth(), getHeight());

			orig.flush();
			orig = null;
		} catch (Exception e) {
			bailOut();
		}

		// time to show GTGE splash screen!
		// clear background with black color
		// and wait for a second
		try {
			clearScreen(Color.BLACK);
			Thread.sleep(1000L);
		} catch (InterruptedException e) { }


		// check for focus owner
		if (!inFocus) {
			while (!inFocus) {
				// the game is not in focus!
				Graphics2D g = bsGraphics.getBackBuffer();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				renderLostFocus(g);
				bsGraphics.flip();

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) { }
			}

			bsInput.refresh();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { }
		}


		// gradually show (alpha blending)
		float alpha = 0.0f;
		dummyTimer.startTimer();
		boolean firstTime = true;
		while (alpha < 1.0f) {
			do {
				if (!running) return;
				Graphics2D g = bsGraphics.getBackBuffer();

				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				Composite old = g.getComposite();
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				g.drawImage(logo, 0, 0, null);
				g.setComposite(old);
			} while (bsGraphics.flip() == false);

			if (firstTime) {
				// workaround for OpenGL mode
				firstTime = false;
				dummyTimer.refresh();
			}

			long elapsedTime = dummyTimer.sleep();
			double increment = 0.00065 * elapsedTime;
			if (increment > 0.22) {
				increment = 0.22 + (increment/6);
			}
			alpha += increment;

			if (isSkip(elapsedTime)) {
				clearScreen(Color.BLACK);
				logo.flush(); logo = null;
				return;
			}
		}

		// show the shiny logo for 2500 ms :-)
		do {
			if (!running) return;
			Graphics2D g = bsGraphics.getBackBuffer();

			g.drawImage(logo, 0, 0, null);
		} while (bsGraphics.flip() == false);

		int i = 0;
		while (i++ < 50) { // 50 x 50 = 2500
			if (!running) return;

			try {
				Thread.sleep(50L);
			} catch (InterruptedException e) { }

			if (isSkip(50)) {
				clearScreen(Color.BLACK);
				logo.flush(); logo = null;
				return;
			}
		}

		// gradually disappeared
		alpha = 1.0f;
		dummyTimer.refresh();
		while (alpha > 0.0f) {
			do {
				if (!running) return;
				Graphics2D g = bsGraphics.getBackBuffer();

				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				Composite old = g.getComposite();
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				g.drawImage(logo, 0, 0, null);
				g.setComposite(old);
			} while (bsGraphics.flip() == false);

			long elapsedTime = dummyTimer.sleep();
			double decrement = 0.00055 * elapsedTime;
			if (decrement > 0.15) {
				decrement = 0.15 + ((decrement-0.04)/2);
			}
			alpha -= decrement;

			if (isSkip(elapsedTime)) {
				clearScreen(Color.BLACK);
				logo.flush(); logo = null;
				return;
			}
		}

		logo.flush();
		logo = null;
		dummyTimer.stopTimer();
		dummyTimer = null;

		// black wait before playing
		try {
			clearScreen(Color.BLACK);
			Thread.sleep(100L);
		} catch (InterruptedException e) { }
	}

	private boolean isSkip(long elapsedTime) {
		boolean skip = (bsInput.getKeyPressed() != BaseInput.NO_KEY ||
						bsInput.getMousePressed() != BaseInput.NO_BUTTON);
		bsInput.update(elapsedTime);

		return skip;
	}

	private void clearScreen(Color col) {
		Graphics2D g = bsGraphics.getBackBuffer();
		g.setColor(col);
		g.fillRect(0, 0, getWidth(), getHeight());
		bsGraphics.flip();

		g = bsGraphics.getBackBuffer();
		g.setColor(col);
		g.fillRect(0, 0, getWidth(), getHeight());
		bsGraphics.flip();
	}


 /****************************************************************************/
 /*************************** GTGE VALIDATION ********************************/
 /****************************************************************************/

	private void bailOut() {
		try {
			URL fontURL = com.golden.gamedev.Game.class.getResource("Game.fnt");
			BufferedImage fpsImage = ImageUtil.getImage(fontURL);

			fontManager = new GameFontManager();
			GameFont font = fontManager.getFont(fpsImage);

			// clear background with red color
			// and write cracked version!
			Graphics2D g = bsGraphics.getBackBuffer();

			g.setColor(Color.RED.darker());
			g.fillRect(0, 0, getWidth(), getHeight());
			font.drawString(g, "THIS GAME IS USING", 10, 10);
			font.drawString(g, "GTGE CRACKED VERSION!!", 10, 30);
			font.drawString(g, "PLEASE REPORT THIS GAME TO", 10, 50);
			font.drawString(g, "WWW.GOLDENSTUDIOS.OR.ID", 10, 70);
			font.drawString(g, "THANK YOU....", 10, 105);

			bsGraphics.flip();

			// wait for 8 seconds
			bsInput = new AWTInput(bsGraphics.getComponent());
			try {
				int i = 0;
				do {
					Thread.sleep(50L);
				} while (++i < 160 && isSkip(50) == false); // 160 x 50 = 800
			} catch (InterruptedException e) { }

			finish();
		} catch (Throwable e) {
//			e.printStackTrace();
		}

		System.out.println("THIS GAME IS USING GTGE CRACKED VERSION!!");
		System.out.println("PLEASE REPORT THIS GAME TO HTTP://WWW.GOLDENSTUDIOS.OR.ID/");
		System.out.println("THANK YOU....");

		System.exit(-1);
	}


 /****************************************************************************/
 /******************* BELOW THIS LINE IS ENGINES UTILIZE *********************/
 /*******************   (PASTE INTO GAME OBJECT CLASS)   *********************/
 /****************************************************************************/

 /****************************************************************************/
 /************************* ESSENTIAL GAME UTILITY ***************************/
 /****************************************************************************/
  	// -> com.golden.gamedev.util.Utility

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.util.Utility#getRandom(int, int)
	 * Utility.getRandom(int, int)}
	 */
	public int getRandom(int low, int hi) {
		return Utility.getRandom(low, hi);
	}

	// INTERNATIONALIZATION UTILITY
	//public Locale getLocale() { return locale; }
	//public void setLocale(Locale locale) { this.locale = locale; }


 /****************************************************************************/
 /*************************** GRAPHICS UTILITY *******************************/
 /****************************************************************************/
	// -> com.golden.gamedev.engine.BaseGraphics

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseGraphics#getSize()
	 * bsGraphics.getSize().width}.
	 */
	public int getWidth() {
		return bsGraphics.getSize().width;
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseGraphics#getSize()
	 * bsGraphics.getSize().height}.
	 */
	public int getHeight() {
		return bsGraphics.getSize().height;
	}

	/**
	 * Returns a new created buffered image which the current game state is
	 * rendered into it.
	 */
	public BufferedImage takeScreenShot() {
		BufferedImage screen = ImageUtil.createImage(getWidth(), getHeight(),
												 	 Transparency.OPAQUE);
		Graphics2D g = screen.createGraphics();
		render(g);
		g.dispose();

		return screen;
	}

	/**
	 * Captures current game screen into specified file.
	 *
	 * @see #takeScreenShot()
	 */
	public void takeScreenShot(File f) {
		ImageUtil.saveImage(takeScreenShot(), f);
	}


 /****************************************************************************/
 /**************************** AUDIO UTILITY *********************************/
 /****************************************************************************/
	// -> com.golden.gamedev.engine.BaseAudio

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseAudio#play(String)
	 * bsMusic.play(String)}.
	 *
	 * @see com.golden.gamedev.engine.BaseAudio#setBaseRenderer(com.golden.gamedev.engine.BaseAudioRenderer)
	 * @see com.golden.gamedev.engine.audio
	 */
	public int playMusic(String audiofile) {
		return bsMusic.play(audiofile);
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseAudio#play(String)
	 * bsSound.play(String)}.
	 *
	 * @see com.golden.gamedev.engine.BaseAudio#setBaseRenderer(com.golden.gamedev.engine.BaseAudioRenderer)
	 * @see com.golden.gamedev.engine.audio
	 */
	public int playSound(String audiofile) {
		return bsSound.play(audiofile);
	}


 /****************************************************************************/
 /**************************** TIMER UTILITY *********************************/
 /****************************************************************************/
	// -> com.golden.gamedev.engine.BaseTimer

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseTimer#setFPS(int)
	 * bsTimer.setFPS(int)}.
	 */
	public void setFPS(int fps) {
		bsTimer.setFPS(fps);
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseTimer#getCurrentFPS()
	 * bsTimer.getCurrentFPS()}.
	 */
	public int getCurrentFPS() {
		return bsTimer.getCurrentFPS();
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseTimer#getFPS()}.
	 */
	public int getFPS() {
		return bsTimer.getFPS();
	}

	/**
	 * Draws game frame-per-second (FPS) to specified location.
	 */
	public void drawFPS(Graphics2D g, int x, int y) {
		fontManager.getFont("FPS Font").drawString(g,
			"FPS = " + getCurrentFPS() + "/" + getFPS(),
			x, y);
	}


 /****************************************************************************/
 /**************************** INPUT UTILITY *********************************/
 /****************************************************************************/
	// -> com.golden.gamedev.engine.BaseInput

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseInput#getMouseX()
	 * bsInput.getMouseX()}.
	 */
	public int getMouseX() {
		return bsInput.getMouseX();
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseInput#getMouseY()
	 * bsInput.getMouseY()}.
	 */
	public int getMouseY() {
		return bsInput.getMouseY();
	}

	/**
	 * Returns whether the mouse pointer is inside specified screen boundary.
	 */
	public boolean checkPosMouse(int x1, int y1, int x2, int y2) {
		return (getMouseX() >= x1 && getMouseY() >= y1 &&
				getMouseX() <= x2 && getMouseY() <= y2);
	}

	/**
	 * Returns whether the mouse pointer is inside specified sprite boundary.
	 *
	 * @param sprite		sprite to check its intersection with mouse pointer
	 * @param pixelCheck	true, checking the sprite image with pixel precision
	 */
	public boolean checkPosMouse(Sprite sprite, boolean pixelCheck) {
		Background bg = sprite.getBackground();

		// check whether the mouse is in background clip area
		if (getMouseX() < bg.getClip().x || getMouseY() < bg.getClip().y ||
			getMouseX() > bg.getClip().x+bg.getClip().width ||
			getMouseY() > bg.getClip().y+bg.getClip().height) {
			return false;
		}

		double mosx = getMouseX() + bg.getX() - bg.getClip().x;
		double mosy = getMouseY() + bg.getY() - bg.getClip().y;

		if (pixelCheck) {
			try {
	            return ((sprite.getImage().getRGB((int) (mosx-sprite.getX()),
												  (int) (mosy-sprite.getY())) & 0xFF000000) != 0x00);
			} catch (Exception e) {
				return false;
			}

		} else {
			return (mosx >= sprite.getX() && mosy >= sprite.getY() &&
					mosx <= sprite.getX()+sprite.getWidth() &&
					mosy <= sprite.getY()+sprite.getHeight());
		}
	}

	/**
	 * Returns sprite in specified sprite group that intersected with mouse pointer,
	 * or null if no sprite intersected with mouse pointer.
	 *
	 * @param field			playfield to check its intersection with mouse pointer
	 * @param pixelCheck	true, checking the sprite image with pixel precision
	 */
	public Sprite checkPosMouse(SpriteGroup group, boolean pixelCheck) {
		Sprite[] sprites = group.getSprites();
		int size = group.getSize();

		for (int i=0;i < size;i++) {
			if (sprites[i].isActive() &&
				checkPosMouse(sprites[i], pixelCheck)) {
				return sprites[i];
			}
		}

		return null;
	}

	/**
	 * Returns sprite in specified playfield that intersected with mouse pointer,
	 * or null if no sprite intersected with mouse pointer.
	 *
	 * @param field			playfield to check its intersection with mouse pointer
	 * @param pixelCheck	true, checking the sprite image with pixel precision
	 */
	public Sprite checkPosMouse(PlayField field, boolean pixelCheck) {
		SpriteGroup[] groups = field.getGroups();
		int size = groups.length;

		for (int i=0;i < size;i++) {
			if (groups[i].isActive()) {
				Sprite s = checkPosMouse(groups[i], pixelCheck);
				if (s != null) {
					return s;
				}
			}
		}

		return null;
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseInput#isMousePressed(int)
	 * bsInput.isMousePressed(java.awt.event.MouseEvent.BUTTON1)}.
	 */
	public boolean click() {
		return bsInput.isMousePressed(MouseEvent.BUTTON1);
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseInput#isMousePressed(int)
	 * bsInput.isMousePressed(java.awt.event.MouseEvent.BUTTON3)}.
	 */
	public boolean rightClick() {
		return bsInput.isMousePressed(MouseEvent.BUTTON3);
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseInput#isKeyDown(int)
	 * bsInput.isKeyDown(int)}.
	 */
	public boolean keyDown(int keyCode) {
		return bsInput.isKeyDown(keyCode);
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseInput#isKeyPressed(int)
	 * bsInput.isKeyPressed(int)}.
	 */
	public boolean keyPressed(int keyCode) {
		return bsInput.isKeyPressed(keyCode);
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseInput#setMouseVisible(boolean)
	 * bsInput.setMouseVisible(false)}.
	 */
	public void hideCursor() {
		bsInput.setMouseVisible(false);
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseInput#setMouseVisible(boolean)
	 * bsInput.setMouseVisible(true)}.
	 */
	public void showCursor() {
		bsInput.setMouseVisible(true);
	}


 /****************************************************************************/
 /**************************** IMAGE UTILITY *********************************/
 /****************************************************************************/
    // com.golden.gamedev.engine.BaseLoader

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseLoader#setMaskColor(Color)
	 * bsLoader.setMaskColor(java.awt.Color)}.
	 */
	public void setMaskColor(Color c) {
		bsLoader.setMaskColor(c);
	}

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseLoader#getImage(String, boolean)
	 * bsLoader.getImage(String, boolean)}.
	 */
    public BufferedImage getImage(String imagefile, boolean useMask) {
        return bsLoader.getImage(imagefile, useMask);
    }

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseLoader#getImage(String)
	 * bsLoader.getImage(String)}.
	 */
    public BufferedImage getImage(String imagefile) {
        return bsLoader.getImage(imagefile);
    }

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseLoader#getImages(String, int, int, boolean)
	 * bsLoader.getImages(String, int, int, boolean)}.
	 */
    public BufferedImage[] getImages(String imagefile, int col, int row, boolean useMask) {
        return bsLoader.getImages(imagefile, col, row, useMask);
    }

	/**
	 * Effectively equivalent to the call
	 * {@linkplain com.golden.gamedev.engine.BaseLoader#getImages(String, int, int)
	 * bsLoader.getImages(String, int, int)}.
	 */
    public BufferedImage[] getImages(String imagefile, int col, int row) {
        return bsLoader.getImages(imagefile, col, row);
    }

    /**
     * Returns stripped images with specified sequence. <p>
     *
     * First the image is stripped by column and row, and then the images
     * is arranged with specified sequence order.
     * The images then stored into cache
     * ({@linkplain com.golden.gamedev.engine.BaseLoader bsLoader})
     * with key as followed: the image file + sequence + digit. <p>
     *
     * For example:
     * <pre>
     *   // we want the images sequence is as followed
     *   String sequence = "020120";
     *   BufferedImage[] image = getImages("imagestrip.png", 3, 1, true, sequence, 1);
     *
     *   // this is plain same like above code except we use 2 digits here
     *   // 2 digits is used for image strip larger than 10
     *   String sequence = "000200010200";
     *   BufferedImage[] image = getImages("imagestrip.png", 20, 1, true, sequence, 1);
     * </pre>
     * Notice that the first image is start from 0 (zero). <p>
     *
     * This is used to make custom animation (012321).
     */
	public BufferedImage[] getImages(String imagefile,
									 int col, int row, boolean useMask,
									 String sequence, int digit) {
		String mapping = imagefile + sequence + digit;
		BufferedImage[] image = bsLoader.getStoredImages(mapping);

		if (image == null) {
			BufferedImage[] src = getImages(imagefile, col, row, useMask);
			int count = sequence.length() / digit;
			image = new BufferedImage[count];
			for (int i=0;i < count;i++) {
				image[i] = src[Integer.parseInt
							  (sequence.substring(i*digit, ((i+1)*digit)))];
			}
			bsLoader.storeImages(mapping, image);
		}

		return image;
	}

	/**
	 * Same as {@linkplain #getImages(String, int, int, boolean, String, int)
	 * getImages(imagefile, col, row, useMask, sequence, digit)} with
	 * mask color is turned on by default.
	 */
    public BufferedImage[] getImages(String imagefile,
									 int col, int row,
									 String sequence, int digit) {
		return getImages(imagefile, col, row, true, sequence, digit);
	}

    /**
     * Returns stripped images with cropped sequence. <p>
     *
     * First the image is stripped by column and row, and then the images
     * is arranged with specified series sequence order.
     * The images then stored into cache
     * ({@linkplain com.golden.gamedev.engine.BaseLoader bsLoader}
     * with key as followed: start sequence + the image file + end sequence. <p>
     *
     * For example:
     * <pre>
     *   int start = 2, end = 4;
     *   BufferedImage[] image = getImages("imagestrip.png", 6, 1, true, start, end);
     * </pre>
     * Notice that the first image is start from 0 (zero).
     */
	public BufferedImage[] getImages(String imagefile,
									 int col, int row, boolean useMask,
									 int start, int end) {
		String mapping = start + imagefile + end;
		BufferedImage[] image = bsLoader.getStoredImages(mapping);

		if (image == null) {
			BufferedImage[] src = getImages(imagefile, col, row, useMask);
			int count = end - start + 1;
			image = new BufferedImage[count];
			for (int i=0;i < count;i++) {
				image[i] = src[start + i];
			}
			bsLoader.storeImages(mapping, image);
		}

		return image;
	}

 	/**
	 * Same as {@linkplain #getImages(String, int, int, int, int)
	 * getImages(imagefile, col, row, useMask, start, end)} with
	 * mask color is turned on by default.
	 */
	public BufferedImage[] getImages(String imagefile,
								    int col, int row,
								    int start, int end) {
		return getImages(imagefile, col, row, true, start, end);
	}


}