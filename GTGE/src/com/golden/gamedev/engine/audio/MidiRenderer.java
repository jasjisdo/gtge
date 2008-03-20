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
import java.io.*;
import java.net.*;
import javax.sound.midi.*;

// GTGE
import com.golden.gamedev.engine.BaseAudioRenderer;


/**
 * Play midi sound (*.mid). <p>
 *
 * Note: Midi sound use soundbank that not delivered in JRE, only JDK can play
 * midi sound properly. <br>
 * In order to play midi sound properly in JRE you must explicitly install
 * soundbank. <br>
 * Download soundbank from java sun website
 * (<a href="http://java.sun.com/products/java-media/sound/soundbanks.html">
 * http://java.sun.com/products/java-media/sound/soundbanks.html</a>)
 * and refer to the manual how to install it.
 */
public class MidiRenderer extends BaseAudioRenderer implements MetaEventListener {


 /*************************** MIDI CONSTANTS *********************************/

	// end of song event
	private static final int MIDI_EOT_MESSAGE = 47;

	// volume
	private static final int GAIN_CONTROLLER = 7;


 /***************************** MIDI SEQUENCER *******************************/

	private Sequencer 	sequencer;


 /****************************************************************************/
 /********************** VALIDATING MIDI SEQUENCER ***************************/
 /****************************************************************************/

	private static boolean available;
	private static boolean volumeSupported;

	private static final int UNINITIALIZED = 0;
	private static final int INITIALIZING  = 1;
	private static final int INITIALIZED   = 2;

	private static int rendererStatus = UNINITIALIZED;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new midi audio renderer.
	 */
	public MidiRenderer() {
		if (rendererStatus == UNINITIALIZED) {
			rendererStatus = INITIALIZING;

			Thread thread = new Thread() {
			    public final void run() {
					try {
						Sequencer sequencer = MidiSystem.getSequencer();
						sequencer.open();
						volumeSupported = (sequencer instanceof Synthesizer);
						sequencer.close();

						available = true;
					} catch (Throwable e) {
						System.err.println("WARNING: Midi audio playback is not available!");
						available = false;
					}

					rendererStatus = INITIALIZED;
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}


	public boolean isAvailable() {
	    if (rendererStatus != INITIALIZED) {
		    int i = 0;
			while (rendererStatus != INITIALIZED && i++ < 50) {
			    try { Thread.sleep(50L);
				} catch (InterruptedException e) { }
			}
			if (rendererStatus != INITIALIZED) {
				rendererStatus = INITIALIZED;
				available = false;
			}
		}

		return available;
	}


 /****************************************************************************/
 /************************ AUDIO PLAYBACK FUNCTION ***************************/
 /****************************************************************************/

    protected void playSound(URL audiofile) {
	    try {
			if (sequencer == null) {
				sequencer = MidiSystem.getSequencer();
				if (!sequencer.isOpen()) sequencer.open();
			}

			Sequence seq = MidiSystem.getSequence(getAudioFile());
			sequencer.setSequence(seq);
			sequencer.start();
			sequencer.addMetaEventListener(MidiRenderer.this);

			// the volume of newly loaded audio is always 1.0f
			if (volume != 1.0f) {
				setSoundVolume(volume);
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = ERROR;
		}
	}

    protected void replaySound(URL audiofile) {
	    sequencer.start();
	    sequencer.addMetaEventListener(this);
    }

    protected void stopSound() {
		sequencer.stop();
		sequencer.setMicrosecondPosition(0);
		sequencer.removeMetaEventListener(this);
    }


 /****************************************************************************/
 /************************* MIDI EVENT LISTENER ******************************/
 /****************************************************************************/

	/**
	 * Notified when the sound has finished playing.
	 */
	public void meta(MetaMessage msg) {
		if (msg.getType() == MIDI_EOT_MESSAGE) {
			status = END_OF_SOUND;
			sequencer.setMicrosecondPosition(0);
			sequencer.removeMetaEventListener(this);
		}
	}


 /****************************************************************************/
 /************************** AUDIO VOLUME SETTINGS ***************************/
 /****************************************************************************/

    protected void setSoundVolume(float volume) {
		if (sequencer == null) {
			return;
		}

		MidiChannel[] channels = ((Synthesizer) sequencer).getChannels();
		for (int i=0;i < channels.length;i++) {
			channels[i].controlChange(GAIN_CONTROLLER, (int) (volume * 127));
		}
    }

	public boolean isVolumeSupported() {
		return volumeSupported;
	}

}