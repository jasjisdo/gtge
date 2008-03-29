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

// GAGE
import com.dnsalias.java.timer.AdvancedTimer;

import com.golden.gamedev.engine.BaseTimer;

/**
 * High-Resolution Timer builds based on Genuine Advantage Gaming Engine (GAGE)
 * Timer, <br>
 * GAGE Timer is available to download at <a href="http://java.dnsalias.com/"
 * target="_blank">http://java.dnsalias.com/</a>.
 * <p>
 * 
 * Make sure the downloaded library is included into your game classpath before
 * using this timer.
 * <p>
 * 
 * <b>Note: GTGE is not associated in any way with GAGE Timer, this class is
 * only interfacing GAGE Timer to be used in GTGE.</b>
 * <p>
 * 
 * How-to-use <code>GageTimer</code> in GTGE Frame Work :
 * 
 * <pre>
 * public class YourGame extends Game {
 * 	
 * 	protected void initEngine() {
 * 		// initialize timer engine
 * 		bsTimer = new GageTimer();
 * 		super.initEngine();
 * 	}
 * }
 * </pre>
 * 
 * @see <a href="http://java.dnsalias.com/" target="_blank">GAGE official site</a>
 */
public class GageTimer implements BaseTimer {
	
	private final AdvancedTimer timer;
	
	private boolean running;
	
	private int fps = 100;
	
	private long sleepTime;
	private long lastTicks; // last sleep time
	private long resolution;
	
	private long startTime;
	
	private FPSCounter fpsCounter;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates a new instance of <code>GageTimer</code>.
	 * 
	 * @see #startTimer()
	 */
	public GageTimer() {
		this.timer = new AdvancedTimer();
		this.fpsCounter = new FPSCounter();
	}
	
	/** ************************************************************************* */
	/** ************************** START/STOP TIMER ***************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void startTimer() {
		if (this.running) {
			this.stopTimer();
		}
		this.running = true;
		
		this.timer.start();
		
		this.resolution = AdvancedTimer.getTicksPerSecond() / 1000;
		this.sleepTime = AdvancedTimer.getTicksPerSecond() / this.fps;
		this.lastTicks = 0;
		this.startTime = this.getTime();
		
		this.fpsCounter.refresh();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void stopTimer() {
		if (this.running) {
			this.timer.stop();
			this.running = false;
		}
	}
	
	/** ************************************************************************* */
	/** ************************ MAIN-METHOD: SLEEP() *************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public long sleep() {
		this.timer.sleepUntil(this.lastTicks + this.sleepTime);
		this.lastTicks += this.sleepTime;
		
		this.fpsCounter.calculateFPS();
		
		long endTime = this.getTime();
		long elapsedTime = endTime - this.startTime;
		this.startTime = endTime;
		
		return elapsedTime;
	}
	
	/** ************************************************************************* */
	/** *************************** OTHER FUNCTIONS ***************************** */
	/** ************************************************************************* */
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isRunning() {
		return this.running;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getCurrentFPS() {
		return this.fpsCounter.getCurrentFPS();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getFPS() {
		return this.fps;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void setFPS(int fps) {
		if (this.fps == fps) {
			return;
		}
		this.fps = fps;
		
		if (this.running) {
			this.startTimer();
		}
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public long getTime() {
		return this.timer.getClockTicks() / this.resolution;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public void refresh() {
		this.startTime = this.getTime();
	}
	
}
