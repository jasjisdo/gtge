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

// GTGE
import com.golden.gamedev.util.Utility;

/**
 * Audio manager that manages playing, stopping, looping of multiple audio
 * sounds (<code>BaseAudioRenderer</code>s).
 * <p>
 * 
 * Audio manager takes up a single base renderer parameter. The base is used to
 * create new instance of <code>BaseAudioRenderer</code> to play new audio
 * sound.
 * <p>
 * 
 * Audio manager also take care any idle renderer and looping audio renderer.
 * <p>
 * 
 * This class is using {@link BaseIO} to get the external resources.
 */
public class BaseAudio implements Runnable {
	
	/** *************************** AUDIO POLICY ******************************** */
	
	/**
	 * Audio clip with a same name only can be played once at a time. The audio
	 * clip is continued if the clip is currently playing.
	 * <p>
	 * 
	 * To force the clip to replay, set the audio policy to
	 * {@link #SINGLE_REPLAY} instead.
	 * 
	 * @see #setAudioPolicy(int)
	 * @see #play(String, int)
	 */
	public static final int SINGLE = 0;
	
	/**
	 * Multiple audio clips can be played at the same time (simultaneous). <br>
	 * Note: when using {@link #setExclusive(boolean) exclusive mode} (only
	 * <b>one</b> audio clip can be played at a time), <code>MULTIPLE</code>
	 * policy is obsolete, and automatically changed into {@link #SINGLE}.
	 * 
	 * @see #setAudioPolicy(int)
	 * @see #play(String, int)
	 */
	public static final int MULTIPLE = 1;
	
	/**
	 * Same as {@link #SINGLE} policy except the audio clip is force to replay.
	 * 
	 * @see #setAudioPolicy(int)
	 * @see #play(String, int)
	 */
	public static final int SINGLE_REPLAY = 2;
	
	private int audioPolicy = BaseAudio.MULTIPLE; // default audio policy
	
	private int maxSimultaneous; // max simultaneous audio sound
	// played at a time
	
	/** ************************** AUDIO RENDERER ******************************* */
	
	private BaseAudioRenderer baseRenderer;
	
	private BaseAudioRenderer[] renderer;
	
	private String[] rendererFile; // store the filename of
	// the rendered audio
	
	private String lastAudioFile; // last played audio
	
	/** ************************* MANAGER PROPERTIES **************************** */
	
	private BaseIO base;
	
	private boolean exclusive; // only one clip can be played at a time
	
	private boolean loop; // ALL clips are played continously or not
	
	private float volume;
	
	private boolean active;
	
	private int buffer; // total audio renderer instances before
	
	// attempting to replace idle renderer
	
	/** ************************************************************************* */
	/** ************************** CONSTRUCTOR ********************************** */
	/** ************************************************************************* */
	
	/**
	 * Creates new audio manager with specified renderer as the base renderer of
	 * all audio sounds created by this audio manager.
	 * <p>
	 * 
	 * @param base the BaseIO to get audio resources
	 * @param baseRenderer the base renderer of this audio manager
	 */
	public BaseAudio(BaseIO base, BaseAudioRenderer baseRenderer) {
		this.base = base;
		this.baseRenderer = baseRenderer;
		
		this.active = baseRenderer.isAvailable();
		this.volume = 1.0f;
		this.buffer = 10;
		this.maxSimultaneous = 6;
		
		this.renderer = new BaseAudioRenderer[0];
		this.rendererFile = new String[0];
		
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * Thread implementation for managing audio renderers looping.
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(100L);
			}
			catch (InterruptedException e) {
			}
			
			for (int i = 0; i < this.renderer.length; i++) {
				if (this.renderer[i].isLoop()
				        && this.renderer[i].getStatus() == BaseAudioRenderer.END_OF_SOUND) {
					this.renderer[i].play();
				}
			}
		}
	}
	
	/** ************************************************************************* */
	/** ********************** PLAYING AUDIO OPERATION ************************** */
	/** ************************************************************************* */
	
	/**
	 * Plays audio clip with {@link #getAudioPolicy() default policy}.
	 * @param audiofile Name of the audio file to play.
	 * @return Slot which the audio is played.
	 * @see #getAudioRenderer(int)
	 */
	public int play(String audiofile) {
		return this.play(audiofile, this.audioPolicy);
	}
	
	/**
	 * Plays an audio clip based on specified policy ({@link #SINGLE},
	 * {@link #MULTIPLE}, {@link #SINGLE_REPLAY}).
	 * @param audiofile The audio file to play.
	 * @param policy The policy to use for playing.
	 * @return Slot which the audio is played.
	 * @see #getAudioRenderer(int)
	 */
	public int play(String audiofile, int policy) {
		this.lastAudioFile = audiofile;
		
		if (!this.active) {
			return -1;
		}
		
		// -2 means attempt to replace idle renderer
		// since total renderer has exceed buffer size
		int emptyslot = (this.renderer.length <= this.buffer) ? -1 : -2;
		
		// to count simultaneous playing sound
		int playedSound = 0;
		
		for (int i = 0; i < this.renderer.length; i++) {
			if (this.rendererFile[i].equals(audiofile)) {
				if (this.renderer[i].getStatus() == BaseAudioRenderer.PLAYING) {
					playedSound++;
				}
				
				if (policy == BaseAudio.MULTIPLE && !this.exclusive) {
					if (this.renderer[i].getStatus() != BaseAudioRenderer.PLAYING) {
						this.renderer[i].setVolume(this.volume);
						this.renderer[i].play();
						return i;
					}
					
				}
				else if (policy == BaseAudio.SINGLE_REPLAY) {
					// replay the sound
					if (this.exclusive) {
						this.stopAll();
					}
					else {
						this.renderer[i].stop();
					}
					
					this.renderer[i].setVolume(this.volume);
					this.renderer[i].play();
					
					return i;
					
				}
				else {
					// single policy no replay OR
					// multiple policy and exclusive mode
					if (this.exclusive) {
						// stop all except this audio renderer
						this.stopAll(this.renderer[i]);
					}
					
					if (this.renderer[i].getStatus() != BaseAudioRenderer.PLAYING) {
						this.renderer[i].setVolume(this.volume);
						this.renderer[i].play();
					}
					
					return i;
				}
			}
			
			// replace this idle slot
			if (emptyslot == -2
			        && this.renderer[i].getStatus() != BaseAudioRenderer.PLAYING) {
				emptyslot = i;
			}
		}
		
		// ////// attempt to play sound in new slot ////////
		
		// check for simultaneous sounds
		if (playedSound >= this.maxSimultaneous) {
			// too many simultaneous sounds!
			return -1;
		}
		
		if (emptyslot < 0) {
			// no empty slot, expand the renderer array
			this.renderer = (BaseAudioRenderer[]) Utility.expand(this.renderer,
			        1);
			this.rendererFile = (String[]) Utility.expand(this.rendererFile, 1);
			emptyslot = this.renderer.length - 1;
		}
		
		if (this.renderer[emptyslot] == null) {
			// create new renderer in the empty slot
			this.renderer[emptyslot] = this.createRenderer();
			this.renderer[emptyslot].setLoop(this.loop);
		}
		
		if (this.exclusive) {
			// in exclusive mode, only one clip can be played at a time
			this.stopAll();
		}
		else {
			// to be sure the renderer is not playing
			this.stop(emptyslot);
		}
		
		this.renderer[emptyslot].setVolume(this.volume);
		this.renderer[emptyslot].play(this.base.getURL(audiofile));
		this.rendererFile[emptyslot] = audiofile;
		
		return emptyslot;
	}
	
	/**
	 * Stops audio playback in specified slot.
	 * @param slot The slot to be stopped.
	 */
	public void stop(int slot) {
		if (this.renderer[slot].getStatus() == BaseAudioRenderer.PLAYING) {
			this.renderer[slot].stop();
		}
	}
	
	/**
	 * Stops audio playback with specified name.
	 * @param audiofile The audio file to stop.
	 */
	public void stop(String audiofile) {
		BaseAudioRenderer audio = this.getAudioRenderer(audiofile);
		
		if (audio != null) {
			audio.stop();
		}
	}
	
	/**
	 * Stops all played audio playbacks in this audio manager.
	 */
	public void stopAll() {
		int count = this.renderer.length;
		for (int i = 0; i < count; i++) {
			this.stop(i);
		}
	}
	
	/**
	 * Stops all played audio playbacks in this audio manager except specified
	 * renderer.
	 * @param except The playback that shall not be stopped.
	 * @see #getAudioRenderer(String)
	 * @see #getAudioRenderer(int)
	 */
	public void stopAll(BaseAudioRenderer except) {
		int count = this.renderer.length;
		for (int i = 0; i < count; i++) {
			if (this.renderer[i] != except) {
				this.stop(i);
			}
		}
	}
	
	/** ************************************************************************* */
	/** ********************* LOADED RENDERER TRACKER *************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns audio renderer in specified slot.
	 * @param slot The slot of the audio renderer to return.
	 * @return The audio renderer of the given slot.
	 */
	public BaseAudioRenderer getAudioRenderer(int slot) {
		return this.renderer[slot];
	}
	
	/**
	 * Returns audio renderer with specified audio file or null if not found.
	 * @param audiofile The audio file name of the renderer to return.
	 * @return The audion renderer of the given audio file.
	 */
	public BaseAudioRenderer getAudioRenderer(String audiofile) {
		int count = this.renderer.length;
		for (int i = 0; i < count; i++) {
			// find renderer with specified audio file
			if (this.rendererFile[i].equals(audiofile)) {
				return this.renderer[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the last played audio file.
	 * <p>
	 * 
	 * This method is used for example when audio manager is set to active state
	 * from inactive state, if the game wish to play the last played audio, call
	 * {@link #play(String) play(getLastAudioFile())}.
	 * @return The last played audio file.
	 * @see #play(String)
	 */
	public String getLastAudioFile() {
		return this.lastAudioFile;
	}
	
	/**
	 * Returns all audio renderers (playing and idle renderer) associated with
	 * this audio manager.
	 * @return All associated renderers.
	 * @see #getCountRenderers()
	 */
	public BaseAudioRenderer[] getRenderers() {
		return this.renderer;
	}
	
	/**
	 * Returns total audio renderer created within this audio manager.
	 * @return The number of associated renderers.
	 * @see #getRenderers()
	 */
	public int getCountRenderers() {
		return this.renderer.length;
	}
	
	/** ************************************************************************* */
	/** ******************** SETTINGS AUDIO VOLUME ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns audio manager volume.
	 * @return The volume.
	 * @see #setVolume(float)
	 */
	public float getVolume() {
		return this.volume;
	}
	
	/**
	 * Sets audio manager volume range in [0.0f - 1.0f].
	 * <p>
	 * 
	 * If setting volume of {@linkplain #getBaseRenderer() base renderer} is not
	 * supported, this method will return immediately.
	 * @param volume The new volume.
	 * @see #getVolume()
	 */
	public void setVolume(float volume) {
		if (volume < 0.0f) {
			volume = 0.0f;
		}
		if (volume > 1.0f) {
			volume = 1.0f;
		}
		
		if (this.baseRenderer.isVolumeSupported() == false
		        || this.volume == volume) {
			return;
		}
		
		this.volume = volume;
		
		int count = this.renderer.length;
		for (int i = 0; i < count; i++) {
			this.renderer[i].setVolume(volume);
		}
	}
	
	/**
	 * Returns whether setting audio volume is supported or not.
	 * @return If changing the volume is supported.
	 */
	public boolean isVolumeSupported() {
		return this.baseRenderer.isVolumeSupported();
	}
	
	/** ************************************************************************* */
	/** ************************ MANAGER PROPERTIES ***************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns the default audio policy used by this audio manager to play audio
	 * sound when no audio policy is specified.
	 * @return The default audio policy.
	 * @see #play(String)
	 */
	public int getAudioPolicy() {
		return this.audioPolicy;
	}
	
	/**
	 * Sets the default audio policy used by this audio manager to play audio
	 * sound when no audio policy is specified.
	 * 
	 * @param i the default audio policy, one of {@link #SINGLE},
	 *        {@link #MULTIPLE}, {@link #SINGLE_REPLAY}
	 * @see #play(String)
	 */
	public void setAudioPolicy(int i) {
		this.audioPolicy = i;
	}
	
	/**
	 * Returns maximum simultaneous same audio sound can be played at a time.
	 * @return The maximum amount of sounds that can be played the same time.
	 */
	public int getMaxSimultaneous() {
		return this.maxSimultaneous;
	}
	
	/**
	 * Sets maximum simultaneous same audio sound can be played at a time.
	 * @param i The maximum of amount of sounds that can be played the same
	 *        time.
	 */
	public void setMaxSimultaneous(int i) {
		this.maxSimultaneous = i;
	}
	
	/**
	 * Returns true, if only one clip is allowed to play at a time.
	 * @return IF only one clip can be played at a time.
	 * @see #setExclusive(boolean)
	 */
	public boolean isExclusive() {
		return this.exclusive;
	}
	
	/**
	 * Sets whether only one clip is allowed to play at a time or not.
	 * 
	 * @param exclusive true, only one clip is allowed to play at a time
	 * @see #isExclusive()
	 */
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
		
		if (exclusive) {
			this.stopAll();
		}
	}
	
	/**
	 * Returns total renderer allowed to create before audio manager attempt to
	 * replace idle renderer.
	 * @return The renderer buffer size.
	 * @see #setBuffer(int)
	 */
	public int getBuffer() {
		return this.buffer;
	}
	
	/**
	 * Sets total renderer allowed to create before audio manager attempt to
	 * replace idle renderer.
	 * @param i The new renderer buffer size.
	 * @see #getBuffer()
	 */
	public void setBuffer(int i) {
		this.buffer = i;
	}
	
	/**
	 * Returns true, if all the audio sounds are played continously.
	 * @return If all sounds are played continously.
	 * @see #setLoop(boolean)
	 */
	public boolean isLoop() {
		return this.loop;
	}
	
	/**
	 * Sets whether all the audio sounds should be played continously or not.
	 * @param loop If all sounds shall be played continously.
	 * @see #isLoop()
	 */
	public void setLoop(boolean loop) {
		if (this.loop == loop) {
			return;
		}
		
		this.loop = loop;
		
		int count = this.renderer.length;
		for (int i = 0; i < count; i++) {
			this.renderer[i].setLoop(loop);
		}
	}
	
	/**
	 * Returns {@link BaseIO} from where this audio manager is getting all audio
	 * sound resources.
	 * @return The {@link BaseIO} used to retrieve audio resources.
	 * @see #setBaseIO(BaseIO)
	 */
	public BaseIO getBaseIO() {
		return this.base;
	}
	
	/**
	 * Sets {@link BaseIO} from where this audio manager is getting all audio
	 * sound resources.
	 * @param base The {@link BaseIO} to use for getting audio resources.
	 * @see #getBaseIO()
	 */
	public void setBaseIO(BaseIO base) {
		this.base = base;
	}
	
	/**
	 * Returns true, if this audio manager is fully functional.
	 * @return If the audion manager is fully functional.
	 * @see #setActive(boolean)
	 */
	public boolean isActive() {
		return this.active;
	}
	
	/**
	 * Turn on/off this audio manager.
	 * <p>
	 * 
	 * Note: {@linkplain #isAvailable() unavailable} audio manager can't be
	 * switch to on.
	 * 
	 * @param b true, turn on this audio manager
	 * @see #isActive()
	 * @see #isAvailable()
	 */
	public void setActive(boolean b) {
		this.active = (this.isAvailable()) ? b : false;
		
		if (!this.active) {
			this.stopAll();
		}
	}
	
	/**
	 * Returns whether this audio manager is available to use or not.
	 * <p>
	 * 
	 * Unavailable audio manager is caused by
	 * {@link BaseAudioRenderer#isAvailable() unavailable}
	 * {@link #getBaseRenderer() base renderer}.
	 * @return If the manager is available.
	 */
	public boolean isAvailable() {
		return this.baseRenderer.isAvailable();
	}
	
	/** ************************************************************************* */
	/** *************************** BASE RENDERER ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Returns the base renderer of this audio manager.
	 * @return The base renderer.
	 * @see #setBaseRenderer(BaseAudioRenderer)
	 */
	public BaseAudioRenderer getBaseRenderer() {
		return this.baseRenderer;
	}
	
	/**
	 * Sets specified audio renderer as this audio manager base renderer.
	 * 
	 * All renderers in this audio manager are created based on this base
	 * renderer.
	 * @param renderer The base renderer used to create renderers.
	 * @see #getBaseRenderer()
	 */
	public void setBaseRenderer(BaseAudioRenderer renderer) {
		this.baseRenderer = renderer;
		
		if (this.active) {
			this.active = this.baseRenderer.isAvailable();
		}
	}
	
	/**
	 * Constructs new audio renderer to play new audio sound.
	 * <p>
	 * 
	 * The new audio renderer is created using {@link Class#forName(String)}
	 * from the {@linkplain #getBaseRenderer() base renderer} class name.
	 * @return The new created renderer.
	 * @see #getBaseRenderer()
	 */
	protected BaseAudioRenderer createRenderer() {
		try {
			return (BaseAudioRenderer) Class.forName(
			        this.baseRenderer.getClass().getName()).newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException(
			        "Unable to create new instance of audio renderer on "
			                + this
			                + " audio manager caused by: "
			                + e.getMessage()
			                + "\n"
			                + "Make sure the base renderer has one empty constructor!");
		}
	}
	
}
