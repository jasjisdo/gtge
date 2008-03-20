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

// GTGE
import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.Timer;


/**
 * <code>KeyTyped</code> class is a class to simulate key typing. Key typed
 * is a key that pressed for some time and the key event is fired following
 * {@link #getRepeatDelay() initial repeat delay} and
 * {@link #getRepeatRate() repeat rate delay}.
 *
 * @see #update(long)
 * @see #getKeyTyped()
 */
public class KeyTyped {


	private BaseInput	bsInput;

	private Timer 		repeatDelayTimer;	// timer for starting repeat key
	private Timer 		repeatRateTimer;	// timer for repeating repeat key

	private int 		key;				// store last pressed key
	private int 		keyTyped;			// currently typed key


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Constructs new <code>KeyTyped</code> using following input engine, and
	 * specified initial repeat delay and repeat rate delay.
	 */
	public KeyTyped(BaseInput bsInput, int repeatDelay, int repeatRate) {
		this.bsInput = bsInput;

		repeatDelayTimer = new Timer(repeatDelay);
		repeatRateTimer = new Timer(repeatRate);

		repeatDelayTimer.setActive(false);

		key = keyTyped = BaseInput.NO_KEY;
	}

	/**
	 * Constructs new <code>KeyTyped</code> with 450 ms repeat delay and 40 ms
	 * repeat rate.
	 */
	public KeyTyped(BaseInput bsInput) {
		this(bsInput, 450, 40);
	}


 /****************************************************************************/
 /*************************** UPDATE KEY TYPED *******************************/
 /****************************************************************************/

    /**
     * Updates key typing.
     */
	public void update(long elapsedTime) {
		keyTyped = bsInput.getKeyPressed();

		if (keyTyped != BaseInput.NO_KEY) {
			// save key code for repeat key implementation
			key = keyTyped;
			repeatDelayTimer.setActive(true);

		} else {
			// check whether repeat key has been released or not
			if (bsInput.getKeyReleased() == key) {
				// repeat key has been released
				key = BaseInput.NO_KEY;
				repeatDelayTimer.setActive(false);

			} else if (key != BaseInput.NO_KEY) {
				// check for first time repeatness
				if (repeatDelayTimer.isActive()) {
   				 	// first time delay
					if (repeatDelayTimer.action(elapsedTime)) {
						repeatDelayTimer.setActive(false);
						repeatRateTimer.refresh();
						keyTyped = key;
					}

				} else {
					// repeat key
					if (repeatRateTimer.action(elapsedTime)) {
						keyTyped = key;
					}
				}
			}
		}
	}

	/**
	 * Refresh and clears key typing input.
	 */
	public void refresh() {
		repeatDelayTimer.refresh();
		repeatRateTimer.refresh();

		repeatDelayTimer.setActive(false);

		key = keyTyped = BaseInput.NO_KEY;
	}


 /****************************************************************************/
 /*************************** GETTING KEY TYPED ******************************/
 /****************************************************************************/

	/**
	 * Returns key typed or {@link BaseInput#NO_KEY} if no key is being typed.
	 *
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public int getKeyTyped() {
		return keyTyped;
	}

	/**
	 * Returns true if the specified key is being typed.
	 *
	 * @see java.awt.event.KeyEvent#VK_1
	 */
	public boolean isKeyTyped(int keyCode) {
		return (keyTyped == keyCode);
	}


 /****************************************************************************/
 /*************************** REPEAT RATE DELAY ******************************/
 /****************************************************************************/

	/**
	 * Returns the key typed initial delay.
	 *
	 * @see #getKeyTyped()
	 */
	public long getRepeatDelay() {
		return repeatDelayTimer.getDelay();
	}

	/**
	 * Sets the key typed initial delay.
	 *
	 * @see #getKeyTyped()
	 */
	public void setRepeatDelay(long delay) {
		repeatDelayTimer.setDelay(delay);
	}


	/**
	 * Returns the key typed repeat rate delay.
	 *
	 * @see #getKeyTyped()
	 */
	public long getRepeatRate() {
		return repeatRateTimer.getDelay();
	}

	/**
	 * Sets the key typed repeat rate delay.
	 *
	 * @see #getKeyTyped()
	 */
	public void setRepeatRate(long rate) {
		repeatRateTimer.setDelay(rate);
	}

}