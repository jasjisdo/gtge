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

import com.golden.gamedev.engine.BaseInput;

/**
 * Input engine using AWT Component as the input listener.
 * <p>
 * 
 * See {@link com.golden.gamedev.engine.BaseInput} for how to use input engine
 * separated from Golden T Game Engine (GTGE) Frame Work.
 */
public class AWTInput implements BaseInput {
	
	/** **************************** AWT COMPONENT ****************************** */
	
	private Component component;
	
	private InputListener listener; // the real AWT Listener for
	// keyboard, mouse, and mouse motion
	// for simplifying class documentation
	
	/** ************************ MOUSE MOTION EVENT ***************************** */
	
	private int mouseX, mouseY;
	private int lastMouseX, lastMouseY;
	private int mouseDX, mouseDY;
	private boolean mouseExists;
	private boolean mouseVisible;
	
	/** **************************** MOUSE EVENT ******************************** */
	
	private boolean[] mouseDown;
	private int[] mousePressed;
	private int[] mouseReleased;
	private int pressedMouse; // total pressed mouse
	private int releasedMouse;
	
	/** ************************** KEYBOARD EVENT ******************************* */
	
	private boolean[] keyDown;
	int[] keyPressed; // use package modifier since these will
	int[] keyReleased; // be used internally by
	// EnhancedAWTInput
	int pressedKey;
	int releasedKey;
	
	private KeyTyped keyTyped;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new {@link AWTInput} from specified component.
	 * @param comp The component to create a {@link AWTInput} for.
	 */
	public AWTInput(Component comp) {
		this.component = comp;
		this.component.requestFocus();
		
		// request all input to send to our listener
		this.listener = this.createInputListener();
		this.component.addKeyListener(this.listener);
		this.component.addMouseListener(this.listener);
		this.component.addMouseMotionListener(this.listener);
		this.component.addFocusListener(this.listener);
		
		// init variables
		// key event
		this.keyDown = new boolean[255];
		this.keyPressed = this.keyReleased = new int[20];
		this.pressedKey = this.releasedKey = 0;
		this.keyTyped = new KeyTyped(this);
		
		// mouse event
		this.mouseExists = true;
		this.mouseVisible = true;
		this.mouseDown = new boolean[4];
		this.mousePressed = this.mouseReleased = new int[4];
		this.pressedMouse = this.releasedMouse = 0;
		
		// mouse motion event
		this.mouseX = this.mouseY = this.lastMouseX = this.lastMouseY = this.mouseDX = this.mouseDY = 0;
		
		try {
			// centering mouse position
			GraphicsDevice device = GraphicsEnvironment
			        .getLocalGraphicsEnvironment().getDefaultScreenDevice();
			DisplayMode mode = device.getDisplayMode();
			
			this.mouseX = this.lastMouseX = (mode.getWidth() / 2) - 10; // 10 ->
			// cursor
			// size
			this.mouseY = this.lastMouseY = (mode.getHeight() / 2) - 10;
			(new Robot()).mouseMove(this.mouseX, this.mouseY);
		}
		catch (Throwable e) {
			// error centering mouse position in initialization, just ignore it
		}
		
		// to disable the awt component transfer VK_TAB key event
		comp.setFocusTraversalKeysEnabled(false);
		
		// KeyboardFocusManager kfm =
		// KeyboardFocusManager.getCurrentKeyboardFocusManager();
		// kfm.setDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
		// Collections.EMPTY_SET);
		// kfm.setDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
		// Collections.EMPTY_SET);
	}
	
	/**
	 * Creates the default {@link InputListener} of this AWT Input Component.
	 * @return The default {@link InputListener}.
	 */
	protected InputListener createInputListener() {
		return new InputListener();
	}
	
	/** ************************************************************************* */
	/** ************************** UPDATE FUNCTION ****************************** */
	/** ************************************************************************* */
	
	public void update(long elapsedTime) {
		// key typed event
		this.keyTyped.update(elapsedTime);
		
		// mouse event
		this.pressedMouse = this.releasedMouse = 0;
		
		// mouse motion event
		this.mouseDX = this.mouseX - this.lastMouseX;
		this.mouseDY = this.mouseY - this.lastMouseY;
		this.lastMouseX = this.mouseX;
		this.lastMouseY = this.mouseY;
		
		// key event
		this.pressedKey = this.releasedKey = 0;
	}
	
	public void refresh() {
		// clear key typed event
		this.keyTyped.refresh();
		
		// clear mouse event
		for (int i = 0; i < this.mouseDown.length; i++) {
			this.mouseDown[i] = false;
		}
		this.pressedMouse = this.releasedMouse = 0;
		
		// clear mouse motion event
		this.mouseDX = this.mouseDY = 0;
		
		// clear key event
		for (int i = 0; i < this.keyDown.length; i++) {
			this.keyDown[i] = false;
		}
		this.pressedKey = this.releasedKey = 0;
	}
	
	public void cleanup() {
		try {
			// remove the listener
			this.component.removeKeyListener(this.listener);
			this.component.removeMouseListener(this.listener);
			this.component.removeMouseMotionListener(this.listener);
			this.component.removeFocusListener(this.listener);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** ************************************************************************* */
	/** ************************** MOUSE MOTION EVENT *************************** */
	/** ************************************************************************* */
	
	public void mouseMove(int x, int y) {
		try {
			new Robot().mouseMove(x, y);
		}
		catch (Exception e) {
			System.err.println("WARNING: Can't move the mouse pointer to " + x
			        + ", " + y);
		}
	}
	
	public boolean isMouseExists() {
		return this.mouseExists;
	}
	
	public int getMouseX() {
		return this.mouseX;
	}
	
	public int getMouseY() {
		return this.mouseY;
	}
	
	public int getMouseDX() {
		return this.mouseDX;
	}
	
	public int getMouseDY() {
		return this.mouseDY;
	}
	
	public void setMouseVisible(boolean visible) {
		if (this.mouseVisible == visible) {
			return;
		}
		
		this.mouseVisible = visible;
		
		if (!visible) {
			Toolkit t = Toolkit.getDefaultToolkit();
			Dimension d = t.getBestCursorSize(1, 1); // to avoid scaling
			// operation
			if (d.width == 0 || d.height == 0) {
				// width and height cursor can not be 0
				d.width = d.height = 1;
			}
			
			// create null cursor image
			BufferedImage nullImg = new BufferedImage(d.width, d.height,
			        BufferedImage.TYPE_INT_ARGB);
			
			Cursor c = t.createCustomCursor(nullImg, new Point(0, 0), "null");
			this.component.setCursor(c);
			
		}
		else {
			this.component.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	public boolean isMouseVisible() {
		return this.mouseVisible;
	}
	
	/** ************************************************************************* */
	/** **************************** MOUSE EVENT ******************************** */
	/** ************************************************************************* */
	
	public int getMousePressed() {
		return (this.pressedMouse > 0) ? this.mousePressed[0]
		        : BaseInput.NO_BUTTON;
	}
	
	public boolean isMousePressed(int button) {
		for (int i = 0; i < this.pressedMouse; i++) {
			if (this.mousePressed[i] == button) {
				return true;
			}
		}
		
		return false;
	}
	
	public int getMouseReleased() {
		return (this.releasedMouse > 0) ? this.mouseReleased[0]
		        : BaseInput.NO_BUTTON;
	}
	
	public boolean isMouseReleased(int button) {
		for (int i = 0; i < this.releasedMouse; i++) {
			if (this.mouseReleased[i] == button) {
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
		return this.mouseDown;
	}
	
	public boolean isMouseDown(int button) {
		return this.mouseDown[button];
	}
	
	/** ************************************************************************* */
	/** ***************************** KEY EVENT ********************************* */
	/** ************************************************************************* */
	
	public int getKeyPressed() {
		return (this.pressedKey > 0) ? this.keyPressed[0] : BaseInput.NO_KEY;
	}
	
	public boolean isKeyPressed(int keyCode) {
		for (int i = 0; i < this.pressedKey; i++) {
			if (this.keyPressed[i] == keyCode) {
				return true;
			}
		}
		
		return false;
	}
	
	public int getKeyReleased() {
		return (this.releasedKey > 0) ? this.keyReleased[0] : BaseInput.NO_KEY;
	}
	
	public boolean isKeyReleased(int keyCode) {
		for (int i = 0; i < this.releasedKey; i++) {
			if (this.keyReleased[i] == keyCode) {
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
		return this.keyDown;
	}
	
	public boolean isKeyDown(int keyCode) {
		return this.keyDown[keyCode & 0xFF];
	}
	
	/** ************************************************************************* */
	/** ************************ KEY TYPED EVENT ******************************** */
	/** ************************************************************************* */
	
	public int getKeyTyped() {
		return this.keyTyped.getKeyTyped();
	}
	
	public boolean isKeyTyped(int keyCode) {
		return this.keyTyped.isKeyTyped(keyCode);
	}
	
	public long getRepeatDelay() {
		return this.keyTyped.getRepeatDelay();
	}
	
	public void setRepeatDelay(long delay) {
		this.keyTyped.setRepeatDelay(delay);
	}
	
	public long getRepeatRate() {
		return this.keyTyped.getRepeatRate();
	}
	
	public void setRepeatRate(long rate) {
		this.keyTyped.setRepeatRate(rate);
	}
	
	/**
	 * Returns the AWT Component used by this input engine.
	 * @return The {@link Component} used by the input engine.
	 */
	public Component getComponent() {
		return this.component;
	}
	
	// ////////////////////////////////////////////////////////////////////////////
	/** *********************** AWT INPUT LISTENER ****************************** */
	// ////////////////////////////////////////////////////////////////////////////
	/**
	 * The real class that listening the AWT Input Event.
	 * <p>
	 * 
	 * Separated from the parent class to simplify documentation and for
	 * expandable input function.
	 */
	protected class InputListener implements KeyListener, MouseListener,
	        MouseMotionListener, FocusListener {
		
		// //////// KeyListener /////////////
		public void keyPressed(KeyEvent e) {
			// we must check is the key is being pressed or not
			// since this event is repetitively called when a key is pressed
			if (!AWTInput.this.keyDown[e.getKeyCode() & 0xFF]) {
				AWTInput.this.keyDown[e.getKeyCode() & 0xFF] = true;
				
				AWTInput.this.keyPressed[AWTInput.this.pressedKey] = e
				        .getKeyCode();
				AWTInput.this.pressedKey++;
			}
			
			// make sure the key isn't processed for anything else
			// for example ALT key won't open frame menu
			e.consume();
		}
		
		public void keyReleased(KeyEvent e) {
			AWTInput.this.keyDown[e.getKeyCode() & 0xFF] = false;
			
			AWTInput.this.keyReleased[AWTInput.this.releasedKey] = e
			        .getKeyCode();
			AWTInput.this.releasedKey++;
			
			// make sure the key isn't processed for anything else
			e.consume();
		}
		
		public void keyTyped(KeyEvent e) {
			// make sure the key isn't processed for anything else
			e.consume();
		}
		
		// //////// MouseListener ////////////
		public void mouseClicked(MouseEvent e) {
		}
		
		public void mouseEntered(MouseEvent e) {
			AWTInput.this.mouseExists = true;
		}
		
		public void mouseExited(MouseEvent e) {
			AWTInput.this.mouseExists = false;
			
			for (int i = 0; i < 4; i++) {
				AWTInput.this.mouseDown[i] = false;
			}
		}
		
		public void mousePressed(MouseEvent e) {
			AWTInput.this.mouseDown[e.getButton()] = true;
			
			AWTInput.this.mousePressed[AWTInput.this.pressedMouse] = e
			        .getButton();
			AWTInput.this.pressedMouse++;
		}
		
		public void mouseReleased(MouseEvent e) {
			AWTInput.this.mouseDown[e.getButton()] = false;
			
			AWTInput.this.mouseReleased[AWTInput.this.releasedMouse] = e
			        .getButton();
			AWTInput.this.releasedMouse++;
		}
		
		// ///////// MouseMotionListener ///////////
		public void mouseDragged(MouseEvent e) {
			AWTInput.this.mouseX = e.getX();
			AWTInput.this.mouseY = e.getY();
		}
		
		public void mouseMoved(MouseEvent e) {
			AWTInput.this.mouseX = e.getX();
			AWTInput.this.mouseY = e.getY();
		}
		
		// ////////// FocusListener ////////////
		public void focusGained(FocusEvent e) {
		}
		
		public void focusLost(FocusEvent e) {
			AWTInput.this.refresh();
		}
		
	}
	
	// ////////////////////////////////////////////////////////////////////////////
	/** ******************** END OF AWT INPUT LISTENER ************************** */
	// ////////////////////////////////////////////////////////////////////////////
}
