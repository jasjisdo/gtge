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
package com.golden.gamedev.engine;

/**
 * <code>BaseInput</code> interface provides all needed functions for polling
 * keyboard and mouse input.
 * <p>
 * 
 * Common methods of how-to-use <code>BaseInput</code>:
 * 
 * <pre>
 *    public class InputEngine implements BaseInput {
 *       .....
 *       public static void main(String[] args) {
 *          BaseInput engine = new InputEngine(...);
 *          engine.refresh();
 *          // game loop
 *          long elapsedTime = 0;
 *          while (true) {
 *             // update input
 *             engine.update(elapsedTime);
 *             // poll input
 *             engine.isKeyXXX(KeyEvent.XXX);
 *             engine.isMouseXXX(MouseEvent.XXX);
 *          }
 *          // clean up input engine
 *          engine.cleanup();
 *       }
 *    }
 * </pre>
 */
public interface BaseInput {
	
	/** ************************* INPUT CONSTANTS ******************************* */
	
	/**
	 * Indicates no mouse button is being pressed.
	 */
	public static final int NO_BUTTON = Integer.MIN_VALUE;
	
	/**
	 * Indicates no key is being pressed.
	 */
	public static final int NO_KEY = Integer.MIN_VALUE;
	
	/** ************************************************************************* */
	/** ************************** UPDATE FUNCTION ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Updates input engine, this method need to be called in tight loop.
	 * @param elapsedTime The time elapsed since the last update.
	 */
	public void update(long elapsedTime);
	
	/**
	 * Refresh all input actions to empty (clear input actions).
	 */
	public void refresh();
	
	/**
	 * Releases any system resources hooked by this input engine.
	 */
	public void cleanup();
	
	/** ************************************************************************* */
	/** ************************** MOUSE MOTION EVENT *************************** */
	/** ************************************************************************* */
	
	/**
	 * Move the mouse to <code>x</code>, <code>y</code> screen coordinate.
	 * 
	 * @param x the <code>x</code>-coordinate of the new mouse location
	 * @param y the <code>y</code>-coordinate of the new mouse location
	 */
	public void mouseMove(int x, int y);
	
	/**
	 * Returns true, if the mouse pointer is in input component area.
	 * @return If the mouse is over the input component.
	 */
	public boolean isMouseExists();
	
	/**
	 * Returns the mouse <code>x</code>-coordinate.
	 * @return The x location.
	 */
	public int getMouseX();
	
	/**
	 * Returns the mouse <code>y</code>-coordinate.
	 * @return The y location.
	 */
	public int getMouseY();
	
	/**
	 * Returns the delta of mouse <code>x</code>-coordinate.
	 * @return The x movement.
	 */
	public int getMouseDX();
	
	/**
	 * Returns the delta of mouse <code>y</code>-coordinate.
	 * @return The y movement.
	 */
	public int getMouseDY();
	
	/** ************************************************************************* */
	/** **************************** MOUSE EVENT ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns mouse button released or {@link #NO_BUTTON} if no button is being
	 * released.
	 * @return The released mouse button.
	 * @see java.awt.event.MouseEvent#BUTTON1
	 * @see java.awt.event.MouseEvent#BUTTON2
	 * @see java.awt.event.MouseEvent#BUTTON3
	 */
	public int getMouseReleased();
	
	/**
	 * Returns true if the specified button is being released.
	 * 
	 * @param button the mouse button to be checked
	 * @return true, if the button is released.
	 * @see java.awt.event.MouseEvent#BUTTON1
	 * @see java.awt.event.MouseEvent#BUTTON2
	 * @see java.awt.event.MouseEvent#BUTTON3
	 */
	public boolean isMouseReleased(int button);
	
	/**
	 * Returns mouse button pressed or {@link #NO_BUTTON} if no button is being
	 * pressed.
	 * @return The pressed mouse button.
	 * @see java.awt.event.MouseEvent#BUTTON1
	 * @see java.awt.event.MouseEvent#BUTTON2
	 * @see java.awt.event.MouseEvent#BUTTON3
	 */
	public int getMousePressed();
	
	/**
	 * Returns true if the specified button is being pressed.
	 * 
	 * @param button the mouse button to be checked
	 * @return true, if the button is pressed.
	 * @see java.awt.event.MouseEvent#BUTTON1
	 * @see java.awt.event.MouseEvent#BUTTON2
	 * @see java.awt.event.MouseEvent#BUTTON3
	 */
	public boolean isMousePressed(int button);
	
	/**
	 * Returns true if the specified button is being pressed.
	 * 
	 * @param button the mouse button to be checked
	 * @return true, if the button is being pressed.
	 * @see java.awt.event.MouseEvent#BUTTON1
	 * @see java.awt.event.MouseEvent#BUTTON2
	 * @see java.awt.event.MouseEvent#BUTTON3
	 */
	public boolean isMouseDown(int button);
	
	/**
	 * Sets mouse pointer visible status.
	 * 
	 * @param visible true, show mouse pointer
	 */
	public void setMouseVisible(boolean visible);
	
	/**
	 * Returns mouse pointer visible status.
	 * @return If the mouse is visible.
	 */
	public boolean isMouseVisible();
	
	/** ************************************************************************* */
	/** ***************************** KEY EVENT ********************************* */
	/** ************************************************************************* */
	
	/**
	 * Returns key released or {@link #NO_KEY} if no key is released.
	 * @return The key code of the released key.
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public int getKeyReleased();
	
	/**
	 * Returns true if the specified key is released.
	 * 
	 * @param keyCode the key to be checked
	 * @return true, if the key is released.
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public boolean isKeyReleased(int keyCode);
	
	/**
	 * Returns key pressed or {@link #NO_KEY} if no key is pressed.
	 * @return The key code of the pressed key.
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public int getKeyPressed();
	
	/**
	 * Returns true if the specified key is pressed.
	 * 
	 * @param keyCode the key to be checked
	 * @return true, if the key is pressed.
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public boolean isKeyPressed(int keyCode);
	
	/**
	 * Returns true if the specified key is being pressed.
	 * 
	 * @param keyCode the key to be checked
	 * @return true, if the key is being pressed.
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public boolean isKeyDown(int keyCode);
	
	/**
	 * Returns key typed or {@link #NO_KEY} if no key is being typed. Key typed
	 * is key event that simulate key typing, key that fired following
	 * {@link #getRepeatDelay() initial repeat delay} and
	 * {@link #getRepeatRate() repeat rate delay}.
	 * @return The key code of the key typed.
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public int getKeyTyped();
	
	/**
	 * Returns true if the specified key is being typed. Key typed is key event
	 * that simulate key typing, key that fired following
	 * {@link #getRepeatDelay() initial repeat delay} and
	 * {@link #getRepeatRate() repeat rate delay}.
	 * @param keyCode The key code of the key to check.
	 * @return If the key with the given key code is typed.
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public boolean isKeyTyped(int keyCode);
	
	/**
	 * Returns the delay for repeating key typed.
	 * @return The repeat delay.
	 * @see #getKeyTyped()
	 */
	public long getRepeatDelay();
	
	/**
	 * Sets the initial delay for repeating key typed.
	 * @param delay The new repeat delay.
	 * @see #getKeyTyped()
	 */
	public void setRepeatDelay(long delay);
	
	/**
	 * Returns the repeat rate delay for repeating key typed.
	 * @return The repeat rate.
	 * @see #getKeyTyped()
	 */
	public long getRepeatRate();
	
	/**
	 * Sets the repeat rate delay for repeating key typed.
	 * @param rate The new repeat rate.
	 * @see #getKeyTyped()
	 */
	public void setRepeatRate(long rate);
	
}
