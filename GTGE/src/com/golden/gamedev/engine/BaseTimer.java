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
 * <code>BaseTimer</code> interface is an interface for running a loop
 * constantly in a requested frame per second.
 * <p>
 * 
 * Common methods of how-to-use <code>BaseTimer</code>:
 * 
 * <pre>
 *    public class TimerEngine implements BaseTimer {
 *       .....
 *       public static void main(String[] args) {
 *          BaseTimer engine = new TimerEngine(...);
 *          // set the target frame-per-second
 *          engine.setFPS(50); // 50 fps
 *          // start the timer!!
 *          engine.startTimer();
 *          // game loop
 *          while (true) {
 *             // sleep to achieve the target frame-per-second
 *             long elapsedTime = engine.sleep();
 *          }
 *          // stop the timer
 *          engine.stopTimer();
 *       }
 *    }
 * </pre>
 */
public interface BaseTimer {
	
	/** ************************************************************************* */
	/** ******************** START/STOP TIMER OPERATION ************************* */
	/** ************************************************************************* */
	
	/**
	 * Starts the timer, please set appropriate frame per second first before
	 * calling this method.
	 * 
	 * @see #getCurrentFPS()
	 * @see #getFPS()
	 */
	public void startTimer();
	
	/**
	 * Stops this timer.
	 */
	public void stopTimer();
	
	/** ************************************************************************* */
	/** ********************** MAIN FUNCTION: SLEEP() *************************** */
	/** ************************************************************************* */
	
	/**
	 * Sleeps for awhile to achieve requested frame per second and returns the
	 * elapsed time since last sleep.
	 * <p>
	 * 
	 * To call this method, timer must be in {@linkplain #isRunning() running}
	 * state.
	 * 
	 * @return Elapsed time since last sleep.
	 * @see #startTimer()
	 * @see #getFPS()
	 */
	public long sleep();
	
	/**
	 * Returns timer current time in milliseconds.
	 * @return The current time.
	 */
	public long getTime();
	
	/**
	 * Refreshs timer elapsed time.
	 */
	public void refresh();
	
	/** ************************************************************************* */
	/** ****************************** TIMER FPS ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns whether the timer is currently running or not.
	 * <p>
	 * 
	 * Timer is running when {@link #startTimer()} is called.
	 * @return If the timer is running.
	 * @see #startTimer()
	 */
	public boolean isRunning();
	
	/**
	 * Returns timer <b>current</b> frame per second.
	 * <p>
	 * 
	 * Current frame per second is the actual frame per second the player
	 * machine could achieve.
	 * <p>
	 * 
	 * Because of one and many things (ie: the incapability of the player
	 * machine), the current frame per second can be differ from the
	 * {@linkplain #getFPS() requested frame per second}.
	 * 
	 * @return Timer current frame per second.
	 * @see #getFPS()
	 * @see #startTimer()
	 */
	public int getCurrentFPS();
	
	/**
	 * Returns the <b>requested</b> frame per second.
	 * <p>
	 * 
	 * Requested frame per second is the target frame per second to achieve (the
	 * number set in {@link #setFPS(int)} method).
	 * <p>
	 * 
	 * Because of one and many things (ie: the incapability of the player
	 * machine), a high requested frame per second may not always be achieved.
	 * <p>
	 * 
	 * To get the actual fps see {@link #getCurrentFPS()}.
	 * 
	 * @return Requested frame per second.
	 * @see #getCurrentFPS()
	 * @see #setFPS(int)
	 */
	public int getFPS();
	
	/**
	 * Sets this timer target frame per second to specified frame per second.
	 * <p>
	 * 
	 * This timer is ordered to run as fast as this frame per second, but the
	 * actual fps achieved is depending of the player machine ability.
	 * 
	 * @param fps requested frame per second
	 * @see #getCurrentFPS()
	 */
	public void setFPS(int fps);
	
}
