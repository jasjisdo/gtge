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
package com.golden.gamedev.engine.input;

// JFC
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.engine.input.KeyTyped;


/**
 * Input engine using AWT Component as the input listener. <p>
 *
 * See {@link com.golden.gamedev.engine.BaseInput} for how to use input
 * engine separated from Golden T Game Engine (GTGE) Frame Work.
 */
public class AWTInput implements BaseInput {


 /****************************** AWT COMPONENT *******************************/

    private Component 	component;

	private InputListener listener; 	// the real AWT Listener for
										// keyboard, mouse, and mouse motion
										// for simplifying class documentation


 /************************** MOUSE MOTION EVENT ******************************/

    private int     	mouseX, mouseY;
    private int			lastMouseX, lastMouseY;
    private int			mouseDX, mouseDY;
    private boolean 	mouseExists;
    private boolean		mouseVisible;


 /****************************** MOUSE EVENT *********************************/

    private boolean[] 	mouseDown;
	private int[] 		mousePressed;
	private int[] 		mouseReleased;
	private int			pressedMouse;	// total pressed mouse
	private int			releasedMouse;


 /**************************** KEYBOARD EVENT ********************************/

    private boolean[] 	keyDown;
	int[] 				keyPressed;		// use package modifier since these will
	int[]				keyReleased;	// be used internally by EnhancedAWTInput
	int					pressedKey;
	int					releasedKey;

	private KeyTyped	keyTyped;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new {@link AWTInput} from specified component.
	 * @param comp The component to create a {@link AWTInput} for.
	 */
    public AWTInput(Component comp) {
        component = comp;
        component.requestFocus();

        // request all input to send to our listener
		listener = createInputListener();
        component.addKeyListener(listener);
        component.addMouseListener(listener);
        component.addMouseMotionListener(listener);
        component.addFocusListener(listener);

        // init variables
        // key event
        keyDown = new boolean[255];
        keyPressed = keyReleased = new int[20];
        pressedKey = releasedKey = 0;
        keyTyped = new KeyTyped(this);

		// mouse event
        mouseExists = true;
        mouseVisible = true;
        mouseDown = new boolean[4];
        mousePressed = mouseReleased = new int[4];
        pressedMouse = releasedMouse = 0;

		// mouse motion event
        mouseX = mouseY = lastMouseX = lastMouseY = mouseDX = mouseDY = 0;

        try {
	        // centering mouse position
			GraphicsDevice device = GraphicsEnvironment.
									getLocalGraphicsEnvironment().
									getDefaultScreenDevice();
	        DisplayMode mode = device.getDisplayMode();

	        mouseX = lastMouseX = (mode.getWidth()/2) - 10; // 10 -> cursor size
			mouseY = lastMouseY = (mode.getHeight()/2) - 10;
			(new Robot()).mouseMove(mouseX, mouseY);
		} catch (Throwable e) {
			// error centering mouse position in initialization, just ignore it
		}

		// to disable the awt component transfer VK_TAB key event
        comp.setFocusTraversalKeysEnabled(false);

//		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//		kfm.setDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
//		kfm.setDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
    }

    /**
     * Creates the default {@link InputListener} of this AWT Input Component.
     * @return The default {@link InputListener}.
     */
	protected InputListener createInputListener() {
		return new InputListener();
	}


 /****************************************************************************/
 /**************************** UPDATE FUNCTION *******************************/
 /****************************************************************************/

    public void update(long elapsedTime) {
		// key typed event
		keyTyped.update(elapsedTime);

		// mouse event
		pressedMouse = releasedMouse = 0;

	    // mouse motion event
		mouseDX = mouseX - lastMouseX;
		mouseDY = mouseY - lastMouseY;
		lastMouseX = mouseX;
		lastMouseY = mouseY;

		// key event
		pressedKey = releasedKey = 0;
    }

    public void refresh() {
	    // clear key typed event
   		keyTyped.refresh();

		// clear mouse event
	    for (int i=0;i < mouseDown.length;i++) {
			mouseDown[i] = false;
		}
		pressedMouse = releasedMouse = 0;

	    // clear mouse motion event
		mouseDX = mouseDY = 0;

		// clear key event
	    for (int i=0;i < keyDown.length;i++) {
			keyDown[i] = false;
		}
		pressedKey = releasedKey = 0;
    }

    public void cleanup() {
    try {
		// remove the listener
        component.removeKeyListener(listener);
        component.removeMouseListener(listener);
        component.removeMouseMotionListener(listener);
        component.removeFocusListener(listener);
    } catch(Exception e) {
		e.printStackTrace();
	} }


 /****************************************************************************/
 /**************************** MOUSE MOTION EVENT ****************************/
 /****************************************************************************/

	public void mouseMove(int x, int y) {
		try {
			new Robot().mouseMove(x, y);
		} catch (Exception e) {
			System.err.println("WARNING: Can't move the mouse pointer to " +
							   x + ", " + y);
		}
	}

	public boolean isMouseExists() {
		return mouseExists;
	}

	public int getMouseX() { return mouseX; }
	public int getMouseY() { return mouseY; }
	public int getMouseDX() { return mouseDX; }
	public int getMouseDY() { return mouseDY; }

	public void setMouseVisible(boolean visible) {
		if (mouseVisible == visible) return;

		mouseVisible = visible;

        if (!visible) {
			Toolkit t = Toolkit.getDefaultToolkit();
	        Dimension d = t.getBestCursorSize(1, 1); // to avoid scaling operation
			if (d.width == 0 || d.height == 0) {
				// width and height cursor can not be 0
				d.width = d.height = 1;
			}

			// create null cursor image
	        BufferedImage nullImg = new BufferedImage(d.width, d.height,
													  BufferedImage.TYPE_INT_ARGB);

	        Cursor c = t.createCustomCursor(nullImg, new Point(0,0), "null");
			component.setCursor(c);

		} else {
			component.setCursor(Cursor.getDefaultCursor());
		}
	}

	public boolean isMouseVisible() {
		return mouseVisible;
	}


 /****************************************************************************/
 /****************************** MOUSE EVENT *********************************/
 /****************************************************************************/

	public int getMousePressed() {
		return (pressedMouse > 0) ? mousePressed[0] : NO_BUTTON;
	}

	public boolean isMousePressed(int button) {
		for (int i=0;i < pressedMouse;i++) {
			if (mousePressed[i] == button) {
				return true;
			}
		}

		return false;
	}

	public int getMouseReleased() {
		return (releasedMouse > 0) ? mouseReleased[0] : NO_BUTTON;
	}

	public boolean isMouseReleased(int button) {
		for (int i=0;i < releasedMouse;i++) {
			if (mouseReleased[i] == button) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns boolean (on/off) mapping of pressed mouse buttons.
	 * @return The mapping of pressed mouse buttons.
	 */
	public boolean[] getMouseDown() {
		return mouseDown;
	}

    public boolean isMouseDown(int button) {
		return mouseDown[button];
	}


 /****************************************************************************/
 /******************************* KEY EVENT **********************************/
 /****************************************************************************/

	public int getKeyPressed() {
		return (pressedKey > 0) ? keyPressed[0] : NO_KEY;
	}

	public boolean isKeyPressed(int keyCode) {
		for (int i=0;i < pressedKey;i++) {
			if (keyPressed[i] == keyCode) {
				return true;
			}
		}

		return false;
	}

	public int getKeyReleased() {
		return (releasedKey > 0) ? keyReleased[0] : NO_KEY;
	}

	public boolean isKeyReleased(int keyCode) {
		for (int i=0;i < releasedKey;i++) {
			if (keyReleased[i] == keyCode) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns boolean (on/off) mapping of currently pressed keys.
	 * @return The mapping of currently pressed keys
	 */
	public boolean[] getKeyDown() {
		return keyDown;
	}

    public boolean isKeyDown(int keyCode) {
		return keyDown[keyCode & 0xFF];
	}


 /****************************************************************************/
 /************************** KEY TYPED EVENT *********************************/
 /****************************************************************************/

	public int getKeyTyped() {
		return keyTyped.getKeyTyped();
	}

	public boolean isKeyTyped(int keyCode) {
		return keyTyped.isKeyTyped(keyCode);
	}


	public long getRepeatDelay() {
		return keyTyped.getRepeatDelay();
	}

	public void setRepeatDelay(long delay) {
		keyTyped.setRepeatDelay(delay);
	}

	public long getRepeatRate() {
		return keyTyped.getRepeatRate();
	}

	public void setRepeatRate(long rate) {
		keyTyped.setRepeatRate(rate);
	}


    /**
     * Returns the AWT Component used by this input engine.
     * @return The {@link Component} used by the input engine.
     */
    public Component getComponent() {
		return component;
	}


 //////////////////////////////////////////////////////////////////////////////
 /************************* AWT INPUT LISTENER *******************************/
 //////////////////////////////////////////////////////////////////////////////

	/**
	 * The real class that listening the AWT Input Event. <p>
	 *
	 * Separated from the parent class to simplify documentation and for
	 * expandable input function.
	 */
	protected class InputListener implements KeyListener, MouseListener,
								   			 MouseMotionListener,
											 FocusListener {

		////////// KeyListener /////////////
		public void keyPressed(KeyEvent e) {
			// we must check is the key is being pressed or not
			// since this event is repetitively called when a key is pressed
			if (!keyDown[e.getKeyCode() & 0xFF]) {
				keyDown[e.getKeyCode() & 0xFF] = true;

				keyPressed[pressedKey] = e.getKeyCode();
				pressedKey++;
			}

			// make sure the key isn't processed for anything else
			// for example ALT key won't open frame menu
			e.consume();
		}

		public void keyReleased(KeyEvent e) {
			keyDown[e.getKeyCode() & 0xFF] = false;

			keyReleased[releasedKey] = e.getKeyCode();
			releasedKey++;

			// make sure the key isn't processed for anything else
			e.consume();
		}

		public void keyTyped(KeyEvent e) {
			// make sure the key isn't processed for anything else
			e.consume();
		}


		////////// MouseListener ////////////
		public void mouseClicked(MouseEvent e) { }
		public void mouseEntered(MouseEvent e) { mouseExists = true; }
		public void mouseExited(MouseEvent e) {
			mouseExists = false;

			for (int i=0;i < 4;i++) {
				mouseDown[i] = false;
			}
		}

		public void mousePressed(MouseEvent e) {
			mouseDown[e.getButton()] = true;

			mousePressed[pressedMouse] = e.getButton();
			pressedMouse++;
		}

		public void mouseReleased(MouseEvent e) {
			mouseDown[e.getButton()] = false;

			mouseReleased[releasedMouse] = e.getButton();
			releasedMouse++;
		}

		/////////// MouseMotionListener ///////////
		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}

		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}


		//////////// FocusListener ////////////
		public void focusGained(FocusEvent e) { }
		public void focusLost(FocusEvent e) { refresh(); }

	}

 //////////////////////////////////////////////////////////////////////////////
 /********************** END OF AWT INPUT LISTENER ***************************/
 //////////////////////////////////////////////////////////////////////////////

}