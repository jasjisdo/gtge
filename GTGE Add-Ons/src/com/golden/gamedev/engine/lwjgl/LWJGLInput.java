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
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.golden.gamedev.engine.BaseInput;

/**
 * Input engine used in LWJGL graphics enviroment, <br>
 * LWJGL is available to download at <a href="http://lwjgl.org/"
 * target="_blank">http://lwjgl.org/</a>.
 * <p>
 * 
 * Make sure the downloaded library is included into your game classpath before
 * using this input engine.
 * <p>
 * 
 * <b>Note: GTGE is not associated in any way with LWJGL, this class is only
 * interfacing LWJGL to be used in GTGE. <br>
 * This class is created and has been tested to be working properly using
 * <em>LWJGL v0.95</em>.</b>
 */
public class LWJGLInput implements BaseInput {
	
	/** ************************ MOUSE MOTION EVENT ***************************** */
	
	private int mouseX, mouseY, lastMouseX, lastMouseY;
	private int mouseDX, mouseDY;
	
	private boolean mouseExists;
	
	private Cursor cursor;
	private boolean mouseVisible = true;
	
	/** **************************** MOUSE EVENT ******************************** */
	
	private int mousePressed;
	private int mouseReleased;
	
	/** ************************** KEYBOARD EVENT ******************************* */
	
	private int keyPressed;
	private int keyReleased;
	
	/** *********************** AWT <-> LWJGL KEY MAPPING *********************** */
	
	/**
	 * AWT to LWJGL key constants conversion.
	 */
	protected static final int[] LWJGL_KEY_CONVERSION;
	
	/**
	 * LWJGL to AWT key constants conversion.
	 */
	protected static final int[] AWT_KEY_CONVERSION;
	
	/**
	 * Conversion LWJGL <-> AWT keycode. Taken from Hybrid Input Abstraction
	 * Layer (HIAL) http://input.jtank.net/
	 */
	static {
		
		// AWT -> LWJGL conversion
		// used for keypressed and keyreleased
		// mapping Keyboard.KEY_ -> KeyEvent.VK_
		LWJGL_KEY_CONVERSION = new int[Keyboard.KEYBOARD_SIZE];
		
		// loops through all of the registered keys in KeyEvent
		Field[] keys = KeyEvent.class.getFields();
		for (int i = 0; i < keys.length; i++) {
			
			try {
				// Converts the KeyEvent constant name to the LWJGL constant
				// name
				String field = "KEY_" + keys[i].getName().substring(3);
				Field lwjglKey = Keyboard.class.getField(field);
				
				// print key mapping
				// System.out.println(field + " " + lwjglKey.getInt(null) + "="
				// + keys[i].getInt(null));
				
				// Sets LWJGL index to be the KeyCode value
				LWJGLInput.LWJGL_KEY_CONVERSION[lwjglKey.getInt(null)] = keys[i]
				        .getInt(null);
				
			}
			catch (Exception e) {
			}
		}
		
		try {
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_BACK] = KeyEvent.VK_BACK_SPACE;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_LBRACKET] = KeyEvent.VK_BRACELEFT;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_RBRACKET] = KeyEvent.VK_BRACERIGHT;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_APOSTROPHE] = KeyEvent.VK_QUOTE;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_GRAVE] = KeyEvent.VK_BACK_QUOTE;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_BACKSLASH] = KeyEvent.VK_BACK_SLASH;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_CAPITAL] = KeyEvent.VK_CAPS_LOCK;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_NUMLOCK] = KeyEvent.VK_NUM_LOCK;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_SCROLL] = KeyEvent.VK_SCROLL_LOCK;
			
			// two to one buttons mapping
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_RETURN] = KeyEvent.VK_ENTER;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_NUMPADENTER] = KeyEvent.VK_ENTER;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_LCONTROL] = KeyEvent.VK_CONTROL;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_RCONTROL] = KeyEvent.VK_CONTROL;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_LSHIFT] = KeyEvent.VK_SHIFT;
			LWJGLInput.LWJGL_KEY_CONVERSION[Keyboard.KEY_RSHIFT] = KeyEvent.VK_SHIFT;
		}
		catch (Exception e) {
		}
		
		// LWJGL -> AWT conversion
		// used for keydown
		// mapping KeyEvent.VK_ -> Keyboard.KEY_
		AWT_KEY_CONVERSION = new int[Keyboard.KEYBOARD_SIZE];
		try {
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_BACK_SPACE] = Keyboard.KEY_BACK;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_BRACELEFT] = Keyboard.KEY_LBRACKET;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_BRACERIGHT] = Keyboard.KEY_RBRACKET;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_ENTER] = Keyboard.KEY_RETURN;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_QUOTE] = Keyboard.KEY_APOSTROPHE;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_BACK_QUOTE] = Keyboard.KEY_GRAVE;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_BACK_SLASH] = Keyboard.KEY_BACKSLASH;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_CAPS_LOCK] = Keyboard.KEY_CAPITAL;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_NUM_LOCK] = Keyboard.KEY_NUMLOCK;
			LWJGLInput.AWT_KEY_CONVERSION[KeyEvent.VK_SCROLL_LOCK] = Keyboard.KEY_SCROLL;
		}
		catch (Exception e) {
		}
		
		// loops through all of the registered keys in Keyboard
		keys = Keyboard.class.getFields();
		for (int i = 0; i < keys.length; i++) {
			
			try {
				// LWJGL constant -> AWT constant
				String field = "VK_" + keys[i].getName().substring(4);
				Field awtKey = KeyEvent.class.getField(field);
				
				// print key mapping
				// System.out.println(field + " " + awtKey.getInt(null) + "=" +
				// keys[i].getInt(null));
				
				// Sets AWT index to be the LWJGL value
				LWJGLInput.AWT_KEY_CONVERSION[awtKey.getInt(null)] = keys[i]
				        .getInt(null);
				
			}
			catch (Exception e) {
				// print out unbound key!
				// try {
				// if (LWJGL_KEY_CONVERSION[keys[i].getInt(null)] == 0)
				// System.out.println("not match -> "+keys[i].getName());
				// } catch (Exception e2) { }
			}
		}
		
	}
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Constructs new <code>LWJGLInput</code>.
	 */
	public LWJGLInput() {
		this.mousePressed = this.mouseReleased = BaseInput.NO_BUTTON;
		this.keyPressed = this.keyReleased = BaseInput.NO_KEY;
		
		this.mouseX = this.lastMouseX = Mouse.getX();
		this.mouseY = this.lastMouseX = Display.getDisplayMode().getHeight()
		        - Mouse.getY();
	}
	
	/** ************************************************************************* */
	/** ************************** UPDATE FUNCTION ****************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void update(long elapsedTime) {
		Keyboard.poll();
		Mouse.poll();
		
		this.mouseX = Mouse.getX();
		this.mouseY = Display.getDisplayMode().getHeight() - Mouse.getY();
		// in OpenGL, y-axis is reversed, coordinate 0, 0 is at BOTTOM left
		
		this.mouseDX = this.mouseX - this.lastMouseX;
		this.mouseDY = this.mouseY - this.lastMouseY;
		
		this.lastMouseX = this.mouseX;
		this.lastMouseY = this.mouseY;
		
		// clear last event
		this.mousePressed = this.mouseReleased = BaseInput.NO_BUTTON;
		this.keyPressed = this.keyReleased = BaseInput.NO_KEY;
		
		// polling keyboard event
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				this.keyPressed = LWJGLInput.convertToAWT(Keyboard
				        .getEventKey());
			}
			else {
				this.keyReleased = LWJGLInput.convertToAWT(Keyboard
				        .getEventKey());
			}
		}
		
		// polling mouse event
		while (Mouse.next()) {
			if (Mouse.getEventButton() != -1) {
				if (Mouse.getEventButtonState()) {
					this.mousePressed = Mouse.getEventButton()
					        + MouseEvent.BUTTON1;
				}
				else {
					this.mouseReleased = Mouse.getEventButton()
					        + MouseEvent.BUTTON1;
				}
			}
		}
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void refresh() {
		// clear mouse event
		this.mousePressed = this.mouseReleased = BaseInput.NO_BUTTON;
		this.mouseDX = this.mouseDY = 0;
		
		// clear keyboard event
		this.keyPressed = this.keyReleased = BaseInput.NO_KEY;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void cleanup() {
	}
	
	/** ************************************************************************* */
	/** ************************** MOUSE MOTION EVENT *************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void mouseMove(int x, int y) {
		try {
			new Robot().mouseMove(x, y);
		}
		catch (Exception e) {
			System.err.println("WARNING: Can't move the mouse pointer to " + x
			        + ", " + y);
		}
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isMouseExists() {
		return true;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getMouseX() {
		return Mouse.getX();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getMouseY() {
		return Display.getDisplayMode().getHeight() - Mouse.getY();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getMouseDX() {
		return this.mouseDX;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getMouseDY() {
		return this.mouseDY;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void setMouseVisible(boolean visible) {
		if (visible == this.mouseVisible) {
			return;
		}
		
		this.mouseVisible = visible;
		
		try {
			// to ensure setting native cursor is available
			// we hide the native cursor whatever it request to visible or not
			this.hideNativeCursor();
			
			if (visible) {
				Mouse.setNativeCursor(null);
			}
		}
		catch (Exception e) {
			// failed to set native cursor to transparent
			// time to grab the mouse
			Mouse.setGrabbed(!visible);
		}
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isMouseVisible() {
		return this.mouseVisible;
	}
	
	/** ************************************************************************* */
	/** **************************** MOUSE EVENT ******************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getMouseReleased() {
		return this.mouseReleased;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isMouseReleased(int button) {
		return (this.mouseReleased == button);
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getMousePressed() {
		return this.mousePressed;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isMousePressed(int button) {
		return (this.mousePressed == button);
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isMouseDown(int button) {
		return Mouse.isButtonDown(button - MouseEvent.BUTTON1);
	}
	
	/** ************************************************************************* */
	/** ***************************** KEY EVENT ********************************* */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getKeyReleased() {
		return this.keyReleased;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isKeyReleased(int keyCode) {
		return (this.keyReleased == keyCode);
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getKeyPressed() {
		return this.keyPressed;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isKeyPressed(int keyCode) {
		return (this.keyPressed == keyCode);
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isKeyDown(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_CONTROL:
				return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
				        || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
				
			case KeyEvent.VK_SHIFT:
				return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
				        || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
				
			case KeyEvent.VK_ENTER:
				return Keyboard.isKeyDown(Keyboard.KEY_RETURN)
				        || Keyboard.isKeyDown(Keyboard.KEY_NUMPADENTER);
		}
		
		return Keyboard.isKeyDown(LWJGLInput.convertToLWJGL(keyCode));
	}
	
	/** ************************************************************************* */
	/** ****************** LWJGL <-> AWT SPECIFIC FUNCTION ********************** */
	/** ************************************************************************* */
	
	/**
	 * Conversion LWJGL key code to AWT key code. This is an O(1) complexity
	 * operation.
	 * 
	 * @param lwjglKeyCode the LWJGL key code to convert
	 * @return The AWT key code for the given LWJGL key code.
	 */
	public static int convertToAWT(int lwjglKeyCode) {
		try {
			return LWJGLInput.LWJGL_KEY_CONVERSION[lwjglKeyCode];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("ERROR: Invalid LWJGL KeyCode " + lwjglKeyCode);
			return -1;
		}
	}
	
	/**
	 * Conversion AWT key code to LWJGL key code. This is an O(1) complexity
	 * operation.
	 * 
	 * @param awtKeyCode the AWT key code to convert
	 * @return The LWJGL key code for the given AWT key code.
	 */
	public static int convertToLWJGL(int awtKeyCode) {
		try {
			return LWJGLInput.AWT_KEY_CONVERSION[awtKeyCode];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("ERROR: Invalid AWT KeyCode " + awtKeyCode);
			return -1;
		}
	}
	
	private void hideNativeCursor() throws Exception {
		if ((Cursor.getCapabilities() & Cursor.CURSOR_ONE_BIT_TRANSPARENCY) != 0) {
			if (this.cursor == null) {
				int minSize = Cursor.getMinCursorSize();
				this.cursor = this.createCursor(new int[minSize * minSize],
				        minSize, minSize, 0, 0);
			}
			
			Mouse.setNativeCursor(this.cursor);
			
		}
		else {
			throw new RuntimeException();
		}
	}
	
	private Cursor createCursor(int data[], int w, int h, int x, int y)
	        throws Exception {
		ByteBuffer scratch = ByteBuffer.allocateDirect(4 * data.length);
		for (int i = 0; i < data.length; i++) {
			if ((data[i] >>> 24) > 0) {
				scratch.put((byte) 0xff);
				scratch.put((byte) ((data[i] >> 16) & 0xff));
				scratch.put((byte) ((data[i] >> 8) & 0xff));
				scratch.put((byte) ((data[i]) & 0xff));
				
			}
			else {
				scratch.putInt(0);
			}
		}
		
		scratch.rewind();
		
		return new Cursor(w, h, x, y, 1, scratch.asIntBuffer(), null);
	}
	
}
