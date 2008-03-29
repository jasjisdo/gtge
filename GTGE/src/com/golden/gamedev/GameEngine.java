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
import java.awt.Graphics2D;

/**
 * Extending <code>Game</code> class functionality to be able to handle
 * multiple game screen in order to separate game logic into separated entities.
 * For example: manage intro screen, title screen, menu screen, and main game
 * screen as separated entity.
 * <p>
 * 
 * Each game entity is subclass of {@link GameObject} class (in above example:
 * intro, title, main game screen are subclass of <code>GameObject</code>
 * class). <code>GameObject</code> class is equally with <code>Game</code>
 * class except it works under <code>GameEngine</code> frame work. Thus you
 * can create the game entities as <code>Game</code> class first, run and test
 * it like usual, and then rename it to <code>GameObject</code> in order to
 * attach it into <code>GameEngine</code> frame work.
 * <p>
 * 
 * The first game to be played is <code>GameObject</code> which use ID = 0.
 * <p>
 * 
 * <code>GameEngine</code> class also can be used to store global variables
 * that can be accessed within all game object entities.
 * <p>
 * 
 * Example how-to-use <code>GameEngine</code> class:
 * 
 * <pre>
 * import com.golden.gamedev.*;
 * 
 * public class YourGame extends GameEngine {
 * 	
 * 	public GameObject getGame(int GameID) {
 *       switch (GameID) {
 *          case 0: // GameID = 0 is always the first to play
 *             return new IntroMenu(this);
 *          case 1: return new ......
 *          case 2: return new ......
 *       }
 *       return null;
 *    }
 * 	
 * 	public static void main(String[] args) {
 * 		// GameEngine class creation is equal with Game class creation
 * 		GameLoader game = new GameLoader();
 * 		game.setup(new YourGame(), new Dimension(640, 480), false);
 * 		game.start();
 * 	}
 * }
 * 
 * public class IntroMenu extends GameObject { // change Game to GameObject
 * 
 * 	public GameObject(GameEngine parent) {
 * 		super(parent);
 * 	}
 * 	
 * 	public void update(long elapsedTime) {
 *       if (....) { // change screen
 *          // GameObject with ID = 1 will be played next
 *          parent.nextGameID = 1;
 *          finish();
 *       }
 *    }
 * }
 * </pre>
 * 
 * @see com.golden.gamedev.GameObject
 */
public abstract class GameEngine extends Game {
	
	/** **************************** CURRENT GAME ******************************* */
	
	private GameObject currentGame;
	private int currentGameID;
	
	/** ***************************** NEXT GAME ********************************* */
	
	/**
	 * GameObject to be played next, null to exit game.
	 * 
	 * @see #nextGameID
	 */
	public GameObject nextGame;
	
	/**
	 * Game ID to be played next, -1 to exit game.
	 * 
	 * @see #nextGame
	 */
	public int nextGameID = 0;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>GameEngine</code>, the first game to be played is
	 * GameObject with ID = 0 (zero), please <b>see note</b> below.
	 * <p>
	 * 
	 * Note: <b>Do not</b> make any overloading constructors. All that belong
	 * to constructor (this method) should be put in {@link #initResources()}
	 * method. Leave this method empty!
	 */
	public GameEngine() {
	}
	
	/** ************************************************************************* */
	/** ************************ GAME LOOP THREAD ******************************* */
	/** ************************************************************************* */
	
	void startGameLoop() {
		// start the timer
		this.bsTimer.startTimer();
		
		while (this.isRunning()) {
			// refresh global game state
			this.bsInput.refresh();
			this.refresh();
			
			// validate game to be played next
			if (this.nextGameID == -1 && this.nextGame == null) {
				// next game is not provided, game ended
				this.finish();
				break;
			}
			
			// get the game object to be played next
			this.currentGameID = this.nextGameID;
			this.currentGame = (this.nextGame != null) ? this.nextGame : this
			        .getGame(this.nextGameID);
			
			if (this.currentGame == null) {
				// game is not available, exit the game
				System.err.println("ERROR: GameObject with ID = "
				        + this.currentGameID + " is not available!!");
				this.finish();
				break;
			}
			
			// clear next game, to avoid this current game played forever
			if (this.nextGame == this.currentGame) {
				this.nextGame = null;
			}
			if (this.nextGameID == this.currentGameID) {
				this.nextGameID = -1;
			}
			
			// running the game
			// in here there's other game loop,
			// loop ended when the game finished, calling GameObject.finish()
			this.currentGame.start();
		}
		
		// dispose everything
		this.bsTimer.stopTimer();
		this.bsSound.stopAll();
		this.bsMusic.stopAll();
		
		if (this.isFinish()) {
			this.bsGraphics.cleanup();
			this.notifyExit();
		}
	}
	
	/** ************************************************************************* */
	/** ************************** GAME OPERATION ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Initialization of global game resources.
	 * <p>
	 * 
	 * The implementation of this method provided by the <code>GameEngine</code>
	 * class does nothing.
	 */
	public void initResources() {
	}
	
	/**
	 * Global game update.
	 * <p>
	 * 
	 * The implementation of this method provided by the <code>GameEngine</code>
	 * class does nothing.
	 * 
	 * @param elapsedTime time elapsed since last update
	 */
	public void update(long elapsedTime) {
	}
	
	/**
	 * Global game render.
	 * <p>
	 * 
	 * The implementation of this method provided by the <code>GameEngine</code>
	 * class does nothing.
	 * 
	 * @param g graphics backbuffer
	 */
	public void render(Graphics2D g) {
	}
	
	/**
	 * Refresh game global variables, called right before playing next game
	 * object.
	 * <p>
	 * 
	 * The implementation of this method provided by the <code>GameEngine</code>
	 * class does nothing.
	 */
	public void refresh() {
	}
	
	/** ************************************************************************* */
	/** ************************ GETTING GAME OBJECT **************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns <code>GameObject</code> with specific ID, the returned
	 * GameObject will be the game to be played next.
	 * 
	 * @param GameID the id of the GameObject
	 * @return GameObject to be played next.
	 * @see #nextGame
	 */
	public abstract GameObject getGame(int GameID);
	
	/**
	 * Returns currently playing <code>GameObject</code> entity.
	 */
	public GameObject getCurrentGame() {
		return this.currentGame;
	}
	
	/**
	 * Returns the ID of currently playing <code>GameObject</code> entity.
	 */
	public int getCurrentGameID() {
		return this.currentGameID;
	}
	
}
