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
package com.golden.gamedev.engine.timer;

// GTGE
import com.golden.gamedev.engine.BaseTimer;


/**
 * The standard timer used in Golden T Game Engine (GTGE) Frame Work. <p>
 *
 * This timer is using System.currentTimeMillis() and support for time drift
 * calculation. <p>
 *
 * See {@link com.golden.gamedev.engine.BaseTimer} for how to use timer
 * engine separated from Golden T Game Engine (GTGE) Frame Work.
 */
public class SystemTimer implements BaseTimer {


 /************************** TIMER FPS VARIABLES *****************************/

	// timer variables
    private int	    fps = 50;		// requested FPS
    private long    msDelay;		// requested sleep time

	private long 	start, end,		// start, end time
					timeDiff, 		// time difference between end-start
					sleepTime;		// real sleep time

    private long 	overSleepTime;	// hold drift on Thread.sleep() method


 /****************************** OTHER VARIABLES *****************************/

	private boolean		running;
	private FPSCounter	fpsCounter;


 /****************************************************************************/
 /**************************** CONSTRUCTOR ***********************************/
 /****************************************************************************/

	/**
	 * Constructs new <code>SystemTimer</code>.
	 */
	public SystemTimer() {
		fpsCounter = new FPSCounter();
	}


 /****************************************************************************/
 /********************** START/STOP TIMER OPERATION **************************/
 /****************************************************************************/

	public void startTimer() {
		if (running) {
			stopTimer();
		}
		running = true;

		msDelay = 1000/fps;
		refresh();

		fpsCounter.refresh();
	}

	public void stopTimer() {
		running = false;
	}


 /****************************************************************************/
 /************************ MAIN FUNCTION: SLEEP() ****************************/
 /****************************************************************************/

	public long sleep() {
		end = System.currentTimeMillis();

		timeDiff = end-start;
		sleepTime = (msDelay-timeDiff) - overSleepTime;

		if (sleepTime > 0) {
			// some time left in this cycle
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) { }

			overSleepTime = (System.currentTimeMillis() - end) - sleepTime;

		} else { // sleepTime <= 0;
			// give another thread a chance to run
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) { }
//			Thread.yield();

			overSleepTime = 0;
		}

        fpsCounter.calculateFPS();

		long end = System.currentTimeMillis();
		long elapsedTime = end - start;
		start = end;

        return elapsedTime;
	}


 /****************************************************************************/
 /*************************** TIMER PROPERTIES *******************************/
 /****************************************************************************/

	public boolean isRunning() {
		return running;
	}

	public int getCurrentFPS() {
		return fpsCounter.getCurrentFPS();
	}

	public int getFPS() {
		return fps;
	}

	public void setFPS(int fps) {
		if (this.fps == fps) return;
		this.fps = fps;

		if (running) {
			startTimer();
		}
	}

	public long getTime() {
		return System.currentTimeMillis();
	}

	public void refresh() {
		start = System.currentTimeMillis();
		overSleepTime = 0;
	}

}