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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

import com.golden.gamedev.engine.BaseAudioRenderer;

/**
 * Play Ogg sound (*.ogg) using JOrbis library, <br>
 * JOrbis library is available to download at <a
 * href="http://www.jcraft.com/jorbis/" target="_blank">
 * http://www.jcraft.com/jorbis/</a>.
 * <p>
 * 
 * Make sure the downloaded library is included into your game classpath before
 * using this audio renderer.
 * <p>
 * 
 * <b>Note: GTGE is not associated in any way with JOrbis, this class is only
 * interfacing JOrbis to be used in GTGE. <br>
 * This class is created and has been tested to be working properly using
 * <em>JOrbis 0.0.14</em>.</b>
 * <p>
 * 
 * How-to-use <code>JOrbisRenderer</code> in GTGE Frame Work :
 * 
 * <pre>
 * public class YourGame extends Game {
 * 	
 * 	protected void initEngine() {
 * 		super.initEngine();
 * 		// set sound effect to use ogg
 * 		bsSound.setSampleRenderer(new JOrbisRenderer());
 * 		// set music to use ogg
 * 		bsMusic.setSampleRenderer(new JOrbisRenderer());
 * 	}
 * }
 * </pre>
 */
public class JOrbisOggRenderer extends BaseAudioRenderer {
	
	private static final int BUFSIZE = 4096 * 2;
	
	private static int convsize = JOrbisOggRenderer.BUFSIZE * 2;
	private static byte[] convbuffer = new byte[JOrbisOggRenderer.convsize];
	
	private SyncState oy;
	private StreamState os;
	private Page og;
	private Packet op;
	private Info vi;
	private Comment vc;
	private DspState vd;
	private Block vb;
	
	private byte[] buffer;
	private int bytes;
	
	private int format;
	private int rate;
	private int channels;
	private SourceDataLine outputLine;
	
	private int frameSizeInBytes;
	private int bufferLengthInBytes;
	
	private OggPlayer player;
	private InputStream bitStream;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates a new instance of <code>JOrbisOggRenderer</code>.
	 */
	public JOrbisOggRenderer() {
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
	protected void playSound(URL audiofile) {
		try {
			this.bitStream = audiofile.openStream();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		this.player = new OggPlayer();
		this.player.setDaemon(true);
		this.player.start();
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
			try {
				this.outputLine.drain();
				this.outputLine.stop();
				this.outputLine.close();
				if (this.bitStream != null) {
					this.bitStream.close();
				}
			}
			catch (Exception e) {
			}
		}
		
		this.player = null;
	}
	
	/**
	 * <i>Please refer to super class method documentation.</i>
	 */
	public boolean isVolumeSupported() {
		return false;
	}
	
	private SourceDataLine getOutputLine(int channels, int rate) {
		if (this.outputLine != null || this.rate != rate
		        || this.channels != channels) {
			if (this.outputLine != null) {
				this.outputLine.drain();
				this.outputLine.stop();
				this.outputLine.close();
			}
			
			this.init_audio(channels, rate);
			this.outputLine.start();
		}
		return this.outputLine;
	}
	
	private void init_audio(int channels, int rate) {
		try {
			AudioFormat audioFormat = new AudioFormat((float) rate, 16,
			        channels, true, // PCM_Signed
			        false // littleEndian
			);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
			        audioFormat, AudioSystem.NOT_SPECIFIED);
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line " + info + " not supported.");
				return;
			}
			
			try {
				this.outputLine = (SourceDataLine) AudioSystem.getLine(info);
				// outputLine.addLineListener(this);
				this.outputLine.open(audioFormat);
			}
			catch (LineUnavailableException e) {
				System.out.println("Unable to open the sourceDataLine: " + e);
				return;
			}
			catch (IllegalArgumentException e) {
				System.out.println("Illegal Argument: " + e);
				return;
			}
			
			this.frameSizeInBytes = audioFormat.getFrameSize();
			int bufferLengthInFrames = this.outputLine.getBufferSize()
			        / this.frameSizeInBytes / 2;
			this.bufferLengthInBytes = bufferLengthInFrames
			        * this.frameSizeInBytes;
			
			this.rate = rate;
			this.channels = channels;
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void init_jorbis() {
		this.oy = new SyncState();
		this.os = new StreamState();
		this.og = new Page();
		this.op = new Packet();
		
		this.vi = new Info();
		this.vc = new Comment();
		this.vd = new DspState();
		this.vb = new Block(this.vd);
		
		this.buffer = null;
		this.bytes = 0;
		
		this.oy.init();
	}
	
	private class OggPlayer extends Thread {
		
		public final void run() {
			JOrbisOggRenderer.this.init_jorbis();
			
			loop: while (true) {
				int eos = 0;
				
				int index = JOrbisOggRenderer.this.oy
				        .buffer(JOrbisOggRenderer.BUFSIZE);
				JOrbisOggRenderer.this.buffer = JOrbisOggRenderer.this.oy.data;
				try {
					JOrbisOggRenderer.this.bytes = JOrbisOggRenderer.this.bitStream
					        .read(JOrbisOggRenderer.this.buffer, index,
					                JOrbisOggRenderer.BUFSIZE);
				}
				catch (Exception e) {
					JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
					System.err.println(e);
					return;
				}
				JOrbisOggRenderer.this.oy.wrote(JOrbisOggRenderer.this.bytes);
				
				if (JOrbisOggRenderer.this.oy
				        .pageout(JOrbisOggRenderer.this.og) != 1) {
					if (JOrbisOggRenderer.this.bytes < JOrbisOggRenderer.BUFSIZE) {
						break;
					}
					JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
					System.err
					        .println("Input does not appear to be an Ogg bitstream.");
					return;
				}
				
				JOrbisOggRenderer.this.os.init(JOrbisOggRenderer.this.og
				        .serialno());
				JOrbisOggRenderer.this.os.reset();
				
				JOrbisOggRenderer.this.vi.init();
				JOrbisOggRenderer.this.vc.init();
				
				if (JOrbisOggRenderer.this.os.pagein(JOrbisOggRenderer.this.og) < 0) {
					// error; stream version mismatch perhaps
					JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
					System.err
					        .println("Error reading first page of Ogg bitstream data.");
					return;
				}
				
				if (JOrbisOggRenderer.this.os
				        .packetout(JOrbisOggRenderer.this.op) != 1) {
					// no page? must not be vorbis
					JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
					System.err.println("Error reading initial header packet.");
					break;
					// return;
				}
				
				if (JOrbisOggRenderer.this.vi.synthesis_headerin(
				        JOrbisOggRenderer.this.vc, JOrbisOggRenderer.this.op) < 0) {
					// error case; not a vorbis header
					JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
					System.err
					        .println("This Ogg bitstream does not contain Vorbis audio data.");
					return;
				}
				
				int i = 0;
				
				while (i < 2) {
					while (i < 2) {
						int result = JOrbisOggRenderer.this.oy
						        .pageout(JOrbisOggRenderer.this.og);
						if (result == 0) {
							break; // Need more data
						}
						if (result == 1) {
							JOrbisOggRenderer.this.os
							        .pagein(JOrbisOggRenderer.this.og);
							while (i < 2) {
								result = JOrbisOggRenderer.this.os
								        .packetout(JOrbisOggRenderer.this.op);
								if (result == 0) {
									break;
								}
								if (result == -1) {
									JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
									System.err
									        .println("Corrupt secondary header.  Exiting.");
									// return;
									break loop;
								}
								JOrbisOggRenderer.this.vi.synthesis_headerin(
								        JOrbisOggRenderer.this.vc,
								        JOrbisOggRenderer.this.op);
								i++;
							}
						}
					}
					
					index = JOrbisOggRenderer.this.oy
					        .buffer(JOrbisOggRenderer.BUFSIZE);
					JOrbisOggRenderer.this.buffer = JOrbisOggRenderer.this.oy.data;
					try {
						JOrbisOggRenderer.this.bytes = JOrbisOggRenderer.this.bitStream
						        .read(JOrbisOggRenderer.this.buffer, index,
						                JOrbisOggRenderer.BUFSIZE);
					}
					catch (Exception e) {
						JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
						System.err.println(e);
						return;
					}
					
					if (JOrbisOggRenderer.this.bytes == 0 && i < 2) {
						JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
						System.err
						        .println("End of file before finding all Vorbis headers!");
						return;
					}
					
					JOrbisOggRenderer.this.oy
					        .wrote(JOrbisOggRenderer.this.bytes);
				}
				
				JOrbisOggRenderer.convsize = JOrbisOggRenderer.BUFSIZE
				        / JOrbisOggRenderer.this.vi.channels;
				
				JOrbisOggRenderer.this.vd
				        .synthesis_init(JOrbisOggRenderer.this.vi);
				JOrbisOggRenderer.this.vb.init(JOrbisOggRenderer.this.vd);
				
				double[][][] _pcm = new double[1][][];
				float[][][] _pcmf = new float[1][][];
				int[] _index = new int[JOrbisOggRenderer.this.vi.channels];
				
				JOrbisOggRenderer.this.getOutputLine(
				        JOrbisOggRenderer.this.vi.channels,
				        JOrbisOggRenderer.this.vi.rate);
				
				while (eos == 0) {
					while (eos == 0) {
						
						if (JOrbisOggRenderer.this.status != BaseAudioRenderer.PLAYING) {
							try {
								// outputLine.drain();
								// outputLine.stop();
								// outputLine.close();
								JOrbisOggRenderer.this.bitStream.close();
							}
							catch (Exception e) {
								System.err.println(e);
							}
							
							return;
						}
						
						int result = JOrbisOggRenderer.this.oy
						        .pageout(JOrbisOggRenderer.this.og);
						if (result == 0) {
							break; // need more data
						}
						if (result == -1) { // missing or corrupt data at this
							// page position
							// System.err.println("Corrupt or missing data in
							// bitstream; continuing...");
						}
						else {
							JOrbisOggRenderer.this.os
							        .pagein(JOrbisOggRenderer.this.og);
							while (true) {
								result = JOrbisOggRenderer.this.os
								        .packetout(JOrbisOggRenderer.this.op);
								if (result == 0) {
									break; // need more data
								}
								if (result == -1) { // missing or corrupt data
									// at this page position
									// no reason to complain; already complained
									// above
								}
								else {
									// we have a packet. Decode it
									int samples;
									if (JOrbisOggRenderer.this.vb
									        .synthesis(JOrbisOggRenderer.this.op) == 0) { // test
										// for
										// success!
										JOrbisOggRenderer.this.vd
										        .synthesis_blockin(JOrbisOggRenderer.this.vb);
									}
									while ((samples = JOrbisOggRenderer.this.vd
									        .synthesis_pcmout(_pcmf, _index)) > 0) {
										double[][] pcm = _pcm[0];
										float[][] pcmf = _pcmf[0];
										boolean clipflag = false;
										int bout = (samples < JOrbisOggRenderer.convsize ? samples
										        : JOrbisOggRenderer.convsize);
										
										// convert doubles to 16 bit signed ints
										// (host order) and
										// interleave
										for (i = 0; i < JOrbisOggRenderer.this.vi.channels; i++) {
											int ptr = i * 2;
											// int ptr=i;
											int mono = _index[i];
											for (int j = 0; j < bout; j++) {
												int val = (int) (pcmf[i][mono
												        + j] * 32767.);
												if (val > 32767) {
													val = 32767;
													clipflag = true;
												}
												if (val < -32768) {
													val = -32768;
													clipflag = true;
												}
												if (val < 0) {
													val = val | 0x8000;
												}
												JOrbisOggRenderer.convbuffer[ptr] = (byte) (val);
												JOrbisOggRenderer.convbuffer[ptr + 1] = (byte) (val >>> 8);
												ptr += 2 * (JOrbisOggRenderer.this.vi.channels);
											}
										}
										JOrbisOggRenderer.this.outputLine
										        .write(
										                JOrbisOggRenderer.convbuffer,
										                0,
										                2
										                        * JOrbisOggRenderer.this.vi.channels
										                        * bout);
										JOrbisOggRenderer.this.vd
										        .synthesis_read(bout);
									}
								}
							}
							if (JOrbisOggRenderer.this.og.eos() != 0) {
								eos = 1;
							}
						}
					}
					
					if (eos == 0) {
						index = JOrbisOggRenderer.this.oy
						        .buffer(JOrbisOggRenderer.BUFSIZE);
						JOrbisOggRenderer.this.buffer = JOrbisOggRenderer.this.oy.data;
						try {
							JOrbisOggRenderer.this.bytes = JOrbisOggRenderer.this.bitStream
							        .read(JOrbisOggRenderer.this.buffer, index,
							                JOrbisOggRenderer.BUFSIZE);
						}
						catch (Exception e) {
							JOrbisOggRenderer.this.status = BaseAudioRenderer.ERROR;
							System.err.println(e);
							return;
						}
						if (JOrbisOggRenderer.this.bytes == -1) {
							break;
						}
						JOrbisOggRenderer.this.oy
						        .wrote(JOrbisOggRenderer.this.bytes);
						if (JOrbisOggRenderer.this.bytes == 0) {
							eos = 1;
						}
					}
				}
				
				JOrbisOggRenderer.this.os.clear();
				JOrbisOggRenderer.this.vb.clear();
				JOrbisOggRenderer.this.vd.clear();
				JOrbisOggRenderer.this.vi.clear();
			}
			
			JOrbisOggRenderer.this.oy.clear();
			
			try {
				if (JOrbisOggRenderer.this.bitStream != null) {
					JOrbisOggRenderer.this.bitStream.close();
				}
			}
			catch (Exception e) {
			}
			
			JOrbisOggRenderer.this.status = BaseAudioRenderer.END_OF_SOUND;
		}
	}
	
	// protected void finalize() throws Throwable {
	// System.out.println("finalization = "+this);
	// super.finalize();
	// }
	
	/**
	 * Testing the OGG Player.
	 */
	public static void main(String args[]) {
		BaseAudioRenderer ogg = new JOrbisOggRenderer();
		
		String music1 = "file:///d:/golden t studios/trash/sound/MUSIC2.ogg";
		
		if (args != null) {
			if (args.length >= 1) {
				music1 = args[0];
			}
		}
		
		// test first song
		try {
			ogg.play(new URL(music1));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// hear some music
		int update = 0;
		while (ogg.getStatus() == BaseAudioRenderer.PLAYING) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
			}
			
			System.out.println(update++);
			
			if (update > 6) {
				ogg.stop();
			}
		}
		
		// first song stopped
		System.out.println("end-song");
		
		// try {
		// ogg.play(new URL("file:///d:/golden t studios/trash/songtitle.ogg"));
		// } catch (Exception e) { e.printStackTrace(); }
		
		// play it again
		ogg.play();
		
		// wait until complete
		while (ogg.getStatus() == BaseAudioRenderer.PLAYING) {
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
