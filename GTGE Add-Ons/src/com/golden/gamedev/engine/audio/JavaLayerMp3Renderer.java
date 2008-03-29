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
package com.golden.gamedev.engine.audio;

// JFC
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

import com.golden.gamedev.engine.BaseAudioRenderer;

/**
 * Play MP3 sound (*.mp3) using JavaLayer library, <br>
 * JavaLayer library is available to download at <a
 * href="http://www.javazoom.net/javalayer/javalayer.html" target="_blank">
 * http://www.javazoom.net/javalayer/javalayer.html</a>.
 * <p>
 * 
 * Make sure the downloaded library is included into your game classpath before
 * using this audio renderer.
 * <p>
 * 
 * <b>Note: GTGE is not associated in any way with JavaLayer, this class is only
 * interfacing JavaLayer to be used in GTGE. <br>
 * This class is created and has been tested to be working properly using
 * <em>JavaLayer 1.0</em>.</b>
 * <p>
 * 
 * How-to-use <code>JavaLayerMp3Renderer</code> in GTGE Frame Work :
 * 
 * <pre>
 * public class YourGame extends Game {
 * 	
 * 	protected void initEngine() {
 * 		super.initEngine();
 * 		// set sound effect to use mp3
 * 		bsSound.setSampleRenderer(new JavaLayerMp3Renderer());
 * 		// set music to use mp3
 * 		bsMusic.setSampleRenderer(new JavaLayerMp3Renderer());
 * 	}
 * }
 * </pre>
 */
public class JavaLayerMp3Renderer extends BaseAudioRenderer {
	
	/** **************************** MP3 PLAYER ********************************* */
	
	private Player player;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates a new instance of <code>JavaLayerMp3Renderer</code>.
	 */
	public JavaLayerMp3Renderer() {
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isAvailable() {
		return true;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	protected void playSound(final URL audiofile) {
		Thread thread = new Thread() {
			
			public void run() {
				try {
					JavaLayerMp3Renderer.this.player = new Player(
					        new BufferedInputStream(audiofile.openStream()),
					        FactoryRegistry.systemRegistry()
					                .createAudioDevice());
					
					JavaLayerMp3Renderer.this.player.play();
				}
				catch (IOException e) {
					JavaLayerMp3Renderer.this.status = BaseAudioRenderer.ERROR;
					System.err.println("Can not load audiofile (" + audiofile
					        + ": " + e);
				}
				catch (JavaLayerException e) {
					JavaLayerMp3Renderer.this.status = BaseAudioRenderer.ERROR;
					System.err.println("Problem playing audio: " + e);
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	protected void replaySound(URL audiofile) {
		this.playSound(audiofile);
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	protected void stopSound() {
		if (this.player != null) {
			this.player.close();
			this.player = null;
		}
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public int getStatus() {
		if (this.player != null) {
			// return EOS if the sound has been completed played
			return (this.player.isComplete()) ? BaseAudioRenderer.END_OF_SOUND
			        : super.getStatus();
		}
		
		return super.getStatus();
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isVolumeSupported() {
		return false;
	}
	
	/**
	 * Testing the MP3 Player.
	 */
	public static void main(String args[]) {
		BaseAudioRenderer mp3 = new JavaLayerMp3Renderer();
		
		String music1 = "file:///d:/golden t studios/trash/sound/MUSIC1.MP3";
		String music2 = "file:///d:/golden t studios/trash/sound/music2.mp3";
		
		if (args != null) {
			if (args.length >= 1) {
				music1 = music2 = args[0];
			}
			if (args.length >= 2) {
				music1 = args[1];
			}
		}
		
		// test first song
		try {
			mp3.play(new URL(music1));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// hear some music
		int update = 0;
		while (mp3.getStatus() == BaseAudioRenderer.PLAYING) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
			}
			
			System.out.println(update++);
			
			if (update > 4) {
				mp3.stop();
			}
		}
		
		// first song stopped
		System.out.println("end-song");
		
		// test second song
		try {
			mp3.play(new URL(music2));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// wait until complete
		while (mp3.getStatus() == BaseAudioRenderer.PLAYING) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
			}
			
			System.out.println(update++);
		}
		
		System.out.println("end-song 2");
	}
	
}
