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
package com.golden.gamedev.funbox;

// JFC
import java.awt.event.KeyEvent;

// GTGE
import com.golden.gamedev.engine.BaseInput;


/**
 * <code>KeyCapture</code> is an utility to capture key sequence combination,
 * usually used to turn on cheat mode. <p>
 *
 * Example how-to-use <code>KeyCapture</code> class :
 * (print out a text whenever the user type 'HELLO' in right sequence)
 * <pre>
 *    BaseInput input;
 *    String key = "HELLO";
 *
 *    KeyCapture keycap = new KeyCapture(input, key, 1000) {
 *       public void keyCaptured() {
 *          // typing "HELLO" will print "hello world" to console
 *          System.out.println("hello world");
 *       }
 *    };
 *
 *    public void update(long elapsedTime) {
 *       keycap.update(elapsedTime);
 *    }
 * </pre>
 */
public abstract class KeyCapture {


	/**
	 * <code>BaseInput</code> associated with this key capture.
	 */
	public BaseInput 	input;

	/**
	 * Turn on this DEBUG variable to debug player key input by print it to
	 * console (<code>System.out.println(...)</code>).
	 */
	public boolean 		DEBUG = false;


 /******************************* KEY SEQUENCE *******************************/

	private int[]   key;			// the key sequence
	private String  keyString;		// the key sequence in String

	private int[]   modifiers;		// key modifiers that must always be pressed
									// when typing the key sequence


 /************************** KEYCAPTURE PROPERTIES ***************************/

	private int     currentKey;		// current key to type

	private int     delay;			// delay time in ms
	private long    currentTick;

	private boolean active = true;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new <code>KeyCapture</code> with specified input, key, delay,
	 * and listener.
	 *
	 * @param input		<code>BaseInput</code> associated with this key capture
	 * @param key		key code array (from <code>java.awt.event.KeyEvent</code>
	 *                  class) to be captured
	 * @param delay		delay for each key input in milliseconds
	 */
    public KeyCapture(BaseInput input, int[] key, int delay) {
		this.input = input;
		this.delay = delay;
		this.key = key;

		// convert to string
		StringBuffer buff = new StringBuffer();
		for (int i=0;i < key.length;i++) {
			buff.append(KeyEvent.getKeyText(key[i]));
		}
		keyString = buff.toString();
    }

	/**
	 * Creates new <code>KeyCapture</code> with specified input, key sequence in
	 * string, delay, and listener. The string will be parsed internally before
	 * used.
	 *
	 * @param input			<code>BaseInput</code> associated with this key capture
	 * @param keyString		string to be captured
	 * @param delay			delay for each key input in milliseconds
	 */
    public KeyCapture(BaseInput input, String keyString, int delay) {
		this.input = input;
		this.delay = delay;
		this.key = parseString(keyString);

		// convert to string
		StringBuffer buff = new StringBuffer();
		for (int i=0;i < key.length;i++) {
			buff.append(KeyEvent.getKeyText(key[i]));
		}
		this.keyString = buff.toString();
	}


 /****************************************************************************/
 /*************************** THE KEY SEQUENCE *******************************/
 /****************************************************************************/

	/**
	 * Returns the key sequence key code that will be captured.
	 *
	 * @see java.awt.KeyEvent#VK_1
	 */
	public int[] getKeySequence() {
		return key;
	}

	/**
	 * Returns the key sequence in string.
	 */
	public String getKeyString() {
		return keyString;
	}

	/**
	 * Sets new key sequence key code.
	 *
	 * @param key	key sequence code
	 * @see java.awt.KeyEvent#VK_1
	 */
	public void setKeySequence(int[] key) {
		this.key = key;
		StringBuffer buff = new StringBuffer();
		for (int i=0;i < key.length;i++) {
			buff.append(KeyEvent.getKeyText(key[i]));
		}

		keyString = buff.toString();
	}

	/**
	 * Parse String <code>st</code> into key sequence. <p>
	 *
	 * For example:
	 * <pre>
	 *     String key = "HYPERSPEED";
	 *     keyCapture.setKeySequence(key);
	 * </pre>
	 *
	 * @param st <code>String</code> to be parsed into key sequence
	 * @see #setKeySequence(int[])
	 * @throws RuntimeException if the <code>String st</code> can not be parsed.
	 */
	public void setKeySequence(String st) {
		setKeySequence(parseString(st));
	}

	private int[] parseString(String st) {
		int[] seq = new int[st.length()];
		st = st.toUpperCase();
		for (int i=0;i < st.length();i++) {
			switch (st.charAt(i)) {
				case 'A': seq[i] = KeyEvent.VK_A; break;
				case 'B': seq[i] = KeyEvent.VK_B; break;
				case 'C': seq[i] = KeyEvent.VK_C; break;
				case 'D': seq[i] = KeyEvent.VK_D; break;
				case 'E': seq[i] = KeyEvent.VK_E; break;
				case 'F': seq[i] = KeyEvent.VK_F; break;
				case 'G': seq[i] = KeyEvent.VK_G; break;
				case 'H': seq[i] = KeyEvent.VK_H; break;
				case 'I': seq[i] = KeyEvent.VK_I; break;
				case 'J': seq[i] = KeyEvent.VK_J; break;
				case 'K': seq[i] = KeyEvent.VK_K; break;
				case 'L': seq[i] = KeyEvent.VK_L; break;
				case 'M': seq[i] = KeyEvent.VK_M; break;
				case 'N': seq[i] = KeyEvent.VK_N; break;
				case 'O': seq[i] = KeyEvent.VK_O; break;
				case 'P': seq[i] = KeyEvent.VK_P; break;
				case 'Q': seq[i] = KeyEvent.VK_Q; break;
				case 'R': seq[i] = KeyEvent.VK_R; break;
				case 'S': seq[i] = KeyEvent.VK_S; break;
				case 'T': seq[i] = KeyEvent.VK_T; break;
				case 'U': seq[i] = KeyEvent.VK_U; break;
				case 'V': seq[i] = KeyEvent.VK_V; break;
				case 'W': seq[i] = KeyEvent.VK_W; break;
				case 'X': seq[i] = KeyEvent.VK_X; break;
				case 'Y': seq[i] = KeyEvent.VK_Y; break;
				case 'Z': seq[i] = KeyEvent.VK_Z; break;
				case ' ': seq[i] = KeyEvent.VK_SPACE; break;
				case '0': seq[i] = KeyEvent.VK_NUMPAD0; break;
				case '1': seq[i] = KeyEvent.VK_NUMPAD1; break;
				case '2': seq[i] = KeyEvent.VK_NUMPAD2; break;
				case '3': seq[i] = KeyEvent.VK_NUMPAD3; break;
				case '4': seq[i] = KeyEvent.VK_NUMPAD4; break;
				case '5': seq[i] = KeyEvent.VK_NUMPAD5; break;
				case '6': seq[i] = KeyEvent.VK_NUMPAD6; break;
				case '7': seq[i] = KeyEvent.VK_NUMPAD7; break;
				case '8': seq[i] = KeyEvent.VK_NUMPAD8; break;
				case '9': seq[i] = KeyEvent.VK_NUMPAD9; break;
				default: throw new RuntimeException(
					"Can't parse String st at " + i + " -> " + st.charAt(i) +
					"\nUse setKeySequence(int[]) instead");
			}
		}

		return seq;
	}

	/**
	 * The key modifiers associated with this key capture. Key modifiers is the
	 * key that must be always pressed while typing the key sequence combination.
	 *
	 * @see #setModifiers(int)
	 * @see #setModifiers(int[])
	 */
	public int[] getModifiers() {
		return modifiers;
	}

	/**
	 * Sets key modifiers of this key capture. Key modifiers is the key that
	 * must be always pressed while typing the key sequence combination.
	 *
	 * @param i	the key modifiers
	 * @see #getModifiers()
	 */
	public void setModifiers(int[] i) {
		modifiers = i;
	}

	/**
	 * Sets key modifiers of this key capture. Key modifiers is the key that
	 * must be always pressed while typing the key sequence combination.
	 *
	 * @param i the key modifier
	 * @see #getModifiers()
	 */
	public void setModifiers(int i) {
		setModifiers(new int[] {i});
	}


 /****************************************************************************/
 /****************************** MAIN-METHOD *********************************/
 /****************************************************************************/

	/**
	 * Received key captured event when the
	 * {@linkplain #getKeySequence() key sequence combination} is successfully
	 * captured.
	 */
	public abstract void keyCaptured();

	/**
	 * Refreshs captured key sequence.
	 */
	public void refresh() {
		currentKey = 0;
		currentTick = 0;
	}

    /**
     * Updates key capture, this method need to be called in tight loop.
     */
	public void update(long elapsedTime) {
		if (!active) {
			return;
		}

		currentTick += elapsedTime;

		if ((currentTick > delay) && (currentKey > 0)) {
			// run out of time
			printDebugWrong("FAILED: Run out of time, delay time=" + delay + "ms");
			refresh();
		}

		if (modifiers != null) {
			for (int i=0;i < modifiers.length;i++) {
				if (!input.isKeyDown(modifiers[i])) {
					// modifiers is not pressed!!
					if (currentKey > 0) {
						printDebugWrong("FAILED: Modifiers key <" +
								   		KeyEvent.getKeyText(modifiers[i]) +
								   		"> not pressed");
						refresh();
					}

					// no need further process
					return;
				}
			}
		}

		if (input.getKeyPressed() != BaseInput.NO_BUTTON) {
			if (input.isKeyPressed(key[currentKey])) {
				currentTick = 0;	// refresh tick counter

				if (++currentKey > key.length-1) {
					// key captured !!!
					printDebugRight("SUCCESS: Key Captured");

					// send event to the listener
					keyCaptured();

					refresh();
				}

			} else if (currentKey > 0) {
				// failed, wrong button pressed
				printDebugWrong("FAILED: Wrong key sequence=" +
						   		KeyEvent.getKeyText(input.getKeyPressed()));
				refresh();

				// try again as the key has been reset
				// (checking the first key sequence again key[0])
				if (input.isKeyPressed(key[currentKey])) {
					if (++currentKey > key.length-1) {
						// key captured !!!
						printDebugRight("SUCCESS: Key Captured");

						// send event to the listener
						keyCaptured();

						refresh();
					}
				}
			}
		}
    }


 /****************************************************************************/
 /*********************** KEY CAPTURE PROPERTIES *****************************/
 /****************************************************************************/

	/**
	 * Returns this key capture is active or not.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets active state of this key capture.
	 */
	public void setActive(boolean b) {
		active = b;
		refresh();
	}

	/**
	 * Returns the delay time each key typing allowed in milliseconds.
	 */
	public int getDelayTime() {
		return delay;
	}

	/**
	 * Sets the delay time each key typing allowed in milliseconds.
	 */
	public void setDelayTime(int i) {
		delay = i;
	}


 /****************************************************************************/
 /******************************* DEBUGGER ***********************************/
 /****************************************************************************/

	private void printDebugRight(String st) {
		if (DEBUG && currentKey > 0) {
			System.out.println(this);
			System.out.println(st);
		}
	}

	private void printDebugWrong(String st) {
		if (DEBUG && currentKey > 0) {
			System.out.println(this);
			System.out.print(st + ", ");

			StringBuffer buff = new StringBuffer();
			buff.append("Current sequence=");
			for (int i=0;i < currentKey;i++) {
				buff.append(KeyEvent.getKeyText(key[i]));
			}
			System.out.println(buff.toString());
		}
	}

	public String toString() {
		// print modifiers key
		StringBuffer modifierBuff = null;
		if (modifiers != null) {
			modifierBuff = new StringBuffer();
			for (int i=0;i < modifiers.length;i++) {
				modifierBuff.append(modifiers[i]);
			}
		}

		return super.toString() + " " +
			"[keysequence=" + keyString +
			", modifiers=" + modifierBuff +
			", delay=" + delay + "]";
	}

}