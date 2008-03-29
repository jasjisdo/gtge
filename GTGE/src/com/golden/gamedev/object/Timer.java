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
package com.golden.gamedev.object;

import java.io.Serializable;

/**
 * Class to manage timing in GTGE Frame Work to create game independent of frame
 * rate. <code>Timer</code> is usually used to create sprite behaviour, such
 * as used in sprite animation.
 * <p>
 * 
 * Example how to use timer in conjunction with sprite in order to make the
 * sprite do an action every 1 second :
 * 
 * <pre>
 * public class DummySprite extends Sprite {
 *     // 1000 ms = 1 sec
 *     Timer timer = new Timer(1000);
 *     public void update(long elapsedTime) {
 *       if (timer.action(elapsedTime)) {
 *         // do an action!! this always called every 1 second
 *       }
 *     }
 *   }
 * }
 * </pre>
 */
public class Timer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5183805739820889052L;
	/** ************************* TIMER VARIABLES ******************************* */
	
	private boolean active = true;
	private long delay; // action delay
	private long currentTick;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>Timer</code> with specified delay time in
	 * milliseconds.
	 * 
	 * @param delay delay time in milliseconds.
	 */
	public Timer(int delay) {
		this.delay = delay;
	}
	
	/** ************************************************************************* */
	/** *************************** MAIN-FUNCTION ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Returns true, if the timer delay time has been elapsed, thus the action
	 * need to be performed.
	 * 
	 * @param elapsedTime time elapsed since last update
	 */
	public boolean action(long elapsedTime) {
		if (this.active) {
			this.currentTick += elapsedTime;
			if (this.currentTick >= this.delay) {
				// time elapsed!
				
				// currentTick = 0;
				// synch the current tick to make the next tick accurate
				this.currentTick -= this.delay;
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Refreshs the timer counter (current tick).
	 */
	public void refresh() {
		this.currentTick = 0;
	}
	
	/**
	 * Makes this timer state equals with other timer, this include active
	 * state, delay time, and timer current tick.
	 */
	public void setEquals(Timer other) {
		this.active = other.active;
		this.delay = other.delay;
		this.currentTick = other.currentTick;
	}
	
	/** ************************************************************************* */
	/** ************************ TIMER VARIABLES ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns active state of this timer, inactive timer won't do any action.
	 */
	public boolean isActive() {
		return this.active;
	}
	
	/**
	 * Sets active state of this timer, inactive timer won't do any action.
	 */
	public void setActive(boolean b) {
		this.active = b;
		this.refresh();
	}
	
	/**
	 * Returns timer delay time in milliseconds.
	 */
	public long getDelay() {
		return this.delay;
	}
	
	/**
	 * Sets timer delay time in milliseconds.
	 */
	public void setDelay(long i) {
		this.delay = i;
		this.refresh();
	}
	
	/**
	 * Returns timer current tick.
	 * <p>
	 * 
	 * If current tick is exceeded timer {@linkplain #getDelay() delay time},
	 * the {@linkplain #action(long) action(elapsedTime)} method will return
	 * true.
	 */
	public long getCurrentTick() {
		return this.currentTick;
	}
	
	/**
	 * Sets timer current tick.
	 * <p>
	 * 
	 * If current tick is exceeded timer {@linkplain #getDelay() delay time},
	 * the {@linkplain #action(long) action(elapsedTime)} method will return
	 * true.
	 */
	public void setCurrentTick(long tick) {
		this.currentTick = tick;
	}
	
}
