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

// LWJGL
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

// GTGE
import com.golden.gamedev.engine.BaseTimer;
import com.golden.gamedev.engine.timer.FPSCounter;


/**
 * Timer engine used in LWJGL graphics environment, <br>
 * LWJGL is available to download at
 * <a href="http://lwjgl.org/" target="_blank">http://lwjgl.org/</a>. <p>
 *
 * Make sure the downloaded library is included into your game classpath
 * before using this timer engine. <p>
 *
 * <b>Note: GTGE is not associated in any way with LWJGL,
 * this class is only interfacing LWJGL to be used in GTGE. <br>
 * This class is created and has been tested to be working properly
 * using <em>LWJGL v0.95</em>.</b>
 */
public class LWJGLTimer implements BaseTimer {


	private int			fps = 50;
	private boolean		running;
	private long		startTime;

	private long		resolution;

	private FPSCounter 	fpsCounter;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Constructs new <code>LWJGLTimer</code>.
	 */
	public LWJGLTimer() {
        resolution = Sys.getTimerResolution() / 1000;

		fpsCounter = new FPSCounter();
	}


 /****************************************************************************/
 /**************************** START/STOP TIMER ******************************/
 /****************************************************************************/

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public void startTimer() {
		if (!running) {
			running = true;

		    startTime = getTime();
		}
    }

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public void stopTimer() {
		if (running) {
			running = false;
		}
    }


 /****************************************************************************/
 /************************** MAIN-METHOD: SLEEP() ****************************/
 /****************************************************************************/

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public long sleep() {
	    // cap to specified fps
		Display.sync(fps);

	    // count fps
		fpsCounter.calculateFPS();

		// get elapsed time
		long endTime = getTime();
		long elapsedTime = endTime - startTime;
		startTime = endTime;

        return elapsedTime;
    }


 /****************************************************************************/
 /***************************** OTHER FUNCTIONS ******************************/
 /****************************************************************************/

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public long getTime() {
		return Sys.getTime() / resolution;
    }

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public void refresh() {
	    startTime = getTime();
    }

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public boolean isRunning() {
		return running;
    }

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public int getCurrentFPS() {
		return fpsCounter.getCurrentFPS();
    }

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public int getFPS() {
	    return fps;
    }

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    public void setFPS(int fps) {
		this.fps = fps;
    }

}