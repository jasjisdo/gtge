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
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.*;

// JORBIS
import com.jcraft.jogg.*;
import com.jcraft.jorbis.*;

// GTGE
import com.golden.gamedev.engine.BaseAudioRenderer;


/**
 * Play Ogg sound (*.ogg) using JOrbis library, <br>
 * JOrbis library is available to download at
 * <a href="http://www.jcraft.com/jorbis/" target="_blank">
 * http://www.jcraft.com/jorbis/</a>. <p>
 *
 * Make sure the downloaded library is included into your game classpath
 * before using this audio renderer. <p>
 *
 * <b>Note: GTGE is not associated in any way with JOrbis,
 * this class is only interfacing JOrbis to be used in GTGE. <br>
 * This class is created and has been tested to be working properly
 * using <em>JOrbis 0.0.14</em>.</b> <p>
 *
 * How-to-use <code>JOrbisRenderer</code> in GTGE Frame Work :
 * <pre>
 *    public class YourGame extends Game {
 *
 *       protected void initEngine() {
 *          super.initEngine();
 *
 *          // set sound effect to use ogg
 *          bsSound.setSampleRenderer(new JOrbisRenderer());
 *
 *          // set music to use ogg
 *          bsMusic.setSampleRenderer(new JOrbisRenderer());
 *       }
 *
 *    }
 * </pre>
 */
public class JOrbisOggRenderer extends BaseAudioRenderer {

	private static final int BUFSIZE = 4096*2;

	private static int convsize = BUFSIZE*2;
	private static byte[] convbuffer = new byte[convsize];

	private SyncState 	oy;
	private StreamState os;
	private Page 		og;
	private Packet 		op;
	private Info 		vi;
	private Comment 	vc;
	private DspState 	vd;
	private Block 		vb;

	private byte[] 		buffer;
	private int 		bytes;

	private int 		format;
	private int 		rate;
	private int 		channels;
	private SourceDataLine outputLine;

	private int 		frameSizeInBytes;
	private int 		bufferLengthInBytes;

	private OggPlayer	player;
	private InputStream bitStream;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates a new instance of <code>JOrbisOggRenderer</code>.
	 */
	public JOrbisOggRenderer() {
	}

    /**
     * <i>Please refer to super class method documentation.</i>
     */
	public boolean isAvailable() { return true; }

    /**
     * <i>Please refer to super class method documentation.</i>
     */
	protected void playSound(URL audiofile) {
		try {
			bitStream = audiofile.openStream();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		player = new OggPlayer();
		player.setDaemon(true);
		player.start();
	}

    /**
     * <i>Please refer to super class method documentation.</i>
     */
    protected void replaySound(URL audiofile) {
		playSound(audiofile);
    }

    /**
     * <i>Please refer to super class method documentation.</i>
     */
	protected void stopSound() {
		if (player != null) {
			try {
				outputLine.drain();
				outputLine.stop();
				outputLine.close();
				if (bitStream != null) {
					bitStream.close();
				}
			} catch (Exception e) { }
		}

		player = null;
	}

    /**
     * <i>Please refer to super class method documentation.</i>
     */
	public boolean isVolumeSupported() {
		return false;
	}

	private SourceDataLine getOutputLine(int channels, int rate){
		if (outputLine	!= null ||
			this.rate	!= rate ||
			this.channels != channels) {
			if (outputLine != null) {
				outputLine.drain();
				outputLine.stop();
				outputLine.close();
			}

			init_audio(channels, rate);
			outputLine.start();
		}
    	return outputLine;
	}

	private void init_audio(int channels, int rate) {
		try {
			AudioFormat audioFormat = new AudioFormat((float) rate,
		  											  16,
													  channels,
													  true,  // PCM_Signed
													  false  // littleEndian
													  );
			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
												   audioFormat,
												   AudioSystem.NOT_SPECIFIED);
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line " + info + " not supported.");
				return;
			}

			try {
				outputLine = (SourceDataLine) AudioSystem.getLine(info);
				// outputLine.addLineListener(this);
				outputLine.open(audioFormat);
			} catch (LineUnavailableException e) {
				System.out.println("Unable to open the sourceDataLine: " + e);
				return;
			} catch (IllegalArgumentException e) {
				System.out.println("Illegal Argument: " + e);
				return;
			}

			frameSizeInBytes = audioFormat.getFrameSize();
			int bufferLengthInFrames = outputLine.getBufferSize() /
									   frameSizeInBytes / 2;
			bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;

			this.rate = rate;
			this.channels = channels;
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	private void init_jorbis(){
		oy=new SyncState();
		os=new StreamState();
		og=new Page();
		op=new Packet();

		vi=new Info();
		vc=new Comment();
		vd=new DspState();
		vb=new Block(vd);

		buffer=null;
		bytes=0;

		oy.init();
	}

	private class OggPlayer extends Thread {
		public final void run() {
			init_jorbis();

			loop:
			while (true) {
				int eos = 0;

				int index = oy.buffer(BUFSIZE);
				buffer = oy.data;
				try {
					bytes = bitStream.read(buffer, index, BUFSIZE);
				} catch(Exception e) {
					status = ERROR;
					System.err.println(e);
					return;
				}
				oy.wrote(bytes);

				if (oy.pageout(og) != 1) {
					if (bytes < BUFSIZE) {
						break;
					}
					status = ERROR;
					System.err.println("Input does not appear to be an Ogg bitstream.");
					return;
				}

				os.init(og.serialno());
				os.reset();

				vi.init();
				vc.init();

				if (os.pagein(og) < 0) {
					// error; stream version mismatch perhaps
					status = ERROR;
					System.err.println("Error reading first page of Ogg bitstream data.");
					return;
				}

				if (os.packetout(op) != 1) {
					// no page? must not be vorbis
					status = ERROR;
					System.err.println("Error reading initial header packet.");
					break;
					// return;
				}

				if (vi.synthesis_headerin(vc, op) < 0) {
					// error case; not a vorbis header
					status = ERROR;
					System.err.println("This Ogg bitstream does not contain Vorbis audio data.");
					return;
				}

				int i = 0;

				while (i < 2) {
				while (i < 2) {
					int result = oy.pageout(og);
					if (result == 0) break; // Need more data
					if (result == 1) {
						os.pagein(og);
						while (i < 2) {
							result = os.packetout(op);
							if (result == 0) break;
							if (result == -1) {
								status = ERROR;
								System.err.println("Corrupt secondary header.  Exiting.");
								//return;
								break loop;
							}
							vi.synthesis_headerin(vc, op);
							i++;
						}
					}
				}

				index = oy.buffer(BUFSIZE);
				buffer = oy.data;
				try {
					bytes = bitStream.read(buffer, index, BUFSIZE);
				} catch(Exception e) {
					status = ERROR;
					System.err.println(e);
					return;
				}

				if (bytes == 0 && i < 2) {
					status = ERROR;
					System.err.println("End of file before finding all Vorbis headers!");
					return;
				}

				oy.wrote(bytes);
				}

				convsize = BUFSIZE / vi.channels;

				vd.synthesis_init(vi);
				vb.init(vd);

				double[][][] _pcm=new double[1][][];
				float[][][] _pcmf=new float[1][][];
				int[] _index=new int[vi.channels];

				getOutputLine(vi.channels, vi.rate);

				while (eos == 0) {
				while (eos == 0) {

					if (status != PLAYING) {
						try {
							//outputLine.drain();
							//outputLine.stop();
							//outputLine.close();
							bitStream.close();
						} catch (Exception e) {
							System.err.println(e);
						}

						return;
					}

					int result = oy.pageout(og);
					if (result == 0) break; // need more data
					if (result == -1) { // missing or corrupt data at this page position
						// System.err.println("Corrupt or missing data in bitstream; continuing...");
					} else {
						os.pagein(og);
						while (true) {
							result = os.packetout(op);
							if (result == 0) break; // need more data
							if (result == -1) { // missing or corrupt data at this page position
								// no reason to complain; already complained above
							} else {
								// we have a packet.  Decode it
								int samples;
								if (vb.synthesis(op) == 0) { // test for success!
									vd.synthesis_blockin(vb);
								}
								while((samples = vd.synthesis_pcmout(_pcmf, _index)) > 0) {
									double[][] pcm=_pcm[0];
									float[][] pcmf=_pcmf[0];
									boolean clipflag=false;
									int bout = (samples < convsize ? samples : convsize);

									// convert doubles to 16 bit signed ints (host order) and
									// interleave
									for (i=0;i < vi.channels;i++) {
										int ptr=i*2;
										//int ptr=i;
										int mono=_index[i];
										for(int j=0;j < bout;j++) {
											int val=(int)(pcmf[i][mono+j]*32767.);
											if (val > 32767) {
												val = 32767;
												clipflag=true;
											}
											if (val < -32768) {
												val = -32768;
												clipflag = true;
											}
											if (val < 0) val = val|0x8000;
											convbuffer[ptr] = (byte) (val);
											convbuffer[ptr+1] = (byte) (val>>>8);
											ptr += 2 * (vi.channels);
										}
									}
									outputLine.write(convbuffer, 0, 2*vi.channels*bout);
									vd.synthesis_read(bout);
								}
							}
						}
						if (og.eos() != 0) eos = 1;
					}
					}

					if (eos == 0) {
						index = oy.buffer(BUFSIZE);
						buffer = oy.data;
						try {
							bytes = bitStream.read(buffer, index, BUFSIZE);
						} catch(Exception e) {
							status = ERROR;
							System.err.println(e);
							return;
						}
						if (bytes == -1) {
							break;
						}
						oy.wrote(bytes);
						if (bytes == 0) eos = 1;
					}
	      		}

				os.clear();
				vb.clear();
				vd.clear();
				vi.clear();
			}

	    	oy.clear();

			try {
				if (bitStream != null) {
					bitStream.close();
				}
			} catch(Exception e) { }

			status = END_OF_SOUND;
		}
	}

//	protected void finalize() throws Throwable {
//		System.out.println("finalization = "+this);
//		super.finalize();
//	}


	/**
	 * Testing the OGG Player.
	 */
	public static void main(String args[]) {
		BaseAudioRenderer ogg = new JOrbisOggRenderer();

		String music1 = "file:///d:/golden t studios/trash/sound/MUSIC2.ogg";

		if (args != null) {
			if (args.length >= 1) music1 = args[0];
		}

		// test first song
		try {
			ogg.play(new URL(music1));
		} catch (Exception e) { e.printStackTrace(); }

		// hear some music
		int update = 0;
		while (ogg.getStatus() == ogg.PLAYING) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { }

			System.out.println(update++);

			if (update > 6) {
				ogg.stop();
			}
		}

		// first song stopped
		System.out.println("end-song");

//		try {
//			ogg.play(new URL("file:///d:/golden t studios/trash/songtitle.ogg"));
//		} catch (Exception e) { e.printStackTrace(); }

		// play it again
		ogg.play();

		// wait until complete
		while (ogg.getStatus() == ogg.PLAYING) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { }

			System.out.println(update++);
		}

		System.out.println("end-song 2");
	}

}