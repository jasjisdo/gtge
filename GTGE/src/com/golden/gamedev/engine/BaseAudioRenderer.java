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

// JFC
import java.net.URL;

/**
 * A simple abstraction for playing audio sound.
 * <p>
 * 
 * <code>BaseAudioRenderer</code> <b>must</b> have one empty constructor.
 * <br>
 * For example :
 * 
 * <pre>
 * public class MP3AudioRenderer extends BaseAudioRenderer {
 * 	
 * 	public MP3Renderer() { // you should provide an empty constructor
 * 		// init the class here
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * 
 * The empty constructor is used by <code>BaseAudio</code> to create a new
 * renderer instance to play new sound using <code>Class.newInstance()</code>.
 */
public abstract class BaseAudioRenderer {
	
	/** ********************** RENDERER STATUS CONSTANTS ************************ */
	
	/**
	 * Audio renderer status indicates that the audio is currently playing.
	 */
	public static final int PLAYING = 1;
	
	/**
	 * Audio renderer status indicates that the audio is currently stopped.
	 */
	public static final int STOPPED = 2;
	
	/**
	 * Audio renderer status indicates that the audio has finished played.
	 */
	public static final int END_OF_SOUND = 3;
	
	/**
	 * Audio renderer status indicates that the audio is failed to play.
	 */
	public static final int ERROR = 4;
	
	/** ************************* RENDERER VARIABLES **************************** */
	
	private URL audiofile;
	
	private boolean loop;
	
	/**
	 * The audio renderer status.
	 * <p>
	 * 
	 * Use this to manage renderer's {@link #END_OF_SOUND} status when the audio
	 * has finished played or {@link #ERROR} status if the audio is failed to
	 * play in {@link #playSound(URL)} method.
	 */
	protected int status;
	
	/**
	 * The audio sound volume.
	 */
	protected float volume;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new audio renderer.
	 * 
	 * @see #play(URL)
	 * @see #setLoop(boolean)
	 */
	public BaseAudioRenderer() {
		this.volume = 1.0f;
		this.status = BaseAudioRenderer.STOPPED;
	}
	
	/** ************************************************************************* */
	/** ******************* MAIN ABSTRACT FUNCTION ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Plays sound with specified audio file.
	 * @param audiofile The audio file to play.
	 */
	protected abstract void playSound(URL audiofile);
	
	/**
	 * Replays last played sound.
	 * @param audiofile The audio file to replay.
	 */
	protected abstract void replaySound(URL audiofile);
	
	/**
	 * Stops any playing sound.
	 */
	protected abstract void stopSound();
	
	/**
	 * Sets audio sound volume.
	 * @param volume The new volume. The volume can be a value between 0.0f (min
	 *        volume) and 1.0f (max volume).
	 */
	protected void setSoundVolume(float volume) {
	}
	
	/** ************************************************************************* */
	/** ********************** AUDIO PLAYBACK FUNCTION ************************** */
	/** ************************************************************************* */
	
	/**
	 * Stops currently played audio and begins playback of specified audio file.
	 * 
	 * @param audiofile the audio file to be played by this renderer.
	 */
	public void play(URL audiofile) {
		this.status = BaseAudioRenderer.PLAYING;
		if (this.audiofile == audiofile) {
			this.replaySound(audiofile);
			return;
		}
		
		this.audiofile = audiofile;
		this.playSound(audiofile);
	}
	
	/**
	 * Restarts last or currently played audio.
	 */
	public void play() {
		this.status = BaseAudioRenderer.PLAYING;
		this.replaySound(this.audiofile);
	}
	
	/**
	 * Stops currently played audio.
	 */
	public void stop() {
		if (this.audiofile != null && this.status == BaseAudioRenderer.PLAYING) {
			this.status = BaseAudioRenderer.STOPPED;
			this.stopSound();
		}
	}
	
	/**
	 * Sets whether the sound should be playing continuously until stop method
	 * is called or not.
	 * <p>
	 * 
	 * Note: {@linkplain com.golden.gamedev.engine.BaseAudio the sound manager}
	 * is the one that taking care the audio loop.
	 * 
	 * @param loop true, the audio will be playing continously
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	/**
	 * Returns whether the audio is playing continuosly or not.
	 * @return If the audio is played continuosly.
	 */
	public boolean isLoop() {
		return this.loop;
	}
	
	/** ************************************************************************* */
	/** ************************ AUDIO VOLUME SETTINGS ************************** */
	/** ************************************************************************* */
	
	/**
	 * Sets audio volume.
	 * @param volume The new volume. The volume can be a value between 0.0f (min
	 *        volume) and 1.0f (max volume).
	 */
	public void setVolume(float volume) {
		if (volume < 0.0f) {
			volume = 0.0f;
		}
		if (volume > 1.0f) {
			volume = 1.0f;
		}
		
		if (this.volume != volume && this.isVolumeSupported()) {
			this.volume = volume;
			
			this.setSoundVolume(volume);
		}
	}
	
	/**
	 * Returns audio volume.
	 * @return The volume. The value can lay between 0.0f and 1.0f
	 * @see #setVolume(float)
	 */
	public float getVolume() {
		return this.volume;
	}
	
	/**
	 * Returns whether setting audio volume is supported or not.
	 * @return If setting the volume is supported.
	 */
	public boolean isVolumeSupported() {
		return true;
	}
	
	/** ************************************************************************* */
	/** ************************* AUDIO PROPERTIES ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns the audio resource URL associated with this audio renderer.
	 * @return The URL of the audio resource associated.
	 */
	public URL getAudioFile() {
		return this.audiofile;
	}
	
	/**
	 * Returns the audio renderer status.
	 * @return The status.
	 * @see #PLAYING
	 * @see #STOPPED
	 * @see #END_OF_SOUND
	 * @see #ERROR
	 */
	public int getStatus() {
		return this.status;
	}
	
	/**
	 * Returns true, if this audio renderer is available to use or false if this
	 * renderer is not available to use (failed to initialized).
	 * @return If the renderer is available for use or not.
	 */
	public abstract boolean isAvailable();
	
	// /**
	// * Closes the media stream, release any system resource
	// * hooked by this renderer.
	// */
	// public void cleanup();
	
	// /**
	// * Creates another empty idle renderer based on this renderer.
	// */
	// public abstract BaseAudioRenderer createRenderer();
	
}
