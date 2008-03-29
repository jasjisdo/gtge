/*
 * AllFieldTypePacket.java
 *
 * Created on May 24, 2007, 2:05 PM
 *
 *
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

package test.packet;

import com.golden.gamedev.engine.network.NetworkPacket;
import com.golden.gamedev.engine.network.packet.NetworkMessage;

/**
 * 
 * @author Paulus Tuerah
 */
public class AllFieldTypePacket extends NetworkPacket {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6450490075996075771L;
	public boolean fBool;
	public boolean[] fBoolArr;
	public byte fByte;
	public byte[] fByteArr;
	public char fChar;
	public char[] fCharArr;
	public double fDoub;
	public double[] fDoubArr;
	public float fFloat;
	public float[] fFloatArr;
	public int fInt;
	public int[] fIntArr;
	public long fLong;
	public long[] fLongArr;
	public short fShort;
	public short[] fShortArr;
	public String fString;
	public String[] fStringArr;
	
	public NetworkMessage fPacket; // may have nested object packet
	public NetworkMessage[] fPacketArr; // object packet must be subclass of
										// NetworkPacket
	
	public AllFieldTypePacket() {
		// populate data
		
		this.fBool = true;
		this.fBoolArr = new boolean[] {
		        this.fBool, true, false, true
		};
		this.fByte = 100;
		this.fByteArr = new byte[] {
		        this.fByte, 1, -2, 3, -4, 5
		};
		this.fChar = 'x';
		this.fCharArr = new char[] {
		        this.fChar, 'a', 'b', 'c'
		};
		this.fDoub = -0.12345678;
		this.fDoubArr = new double[] {
		        this.fDoub, 1.0, -2.0, 3.0, -4.0, 5.0
		};
		this.fFloat = 9.8765f;
		this.fFloatArr = new float[] {
		        this.fFloat, -5.0f, 5.1f, -5.2f
		};
		this.fInt = 4321;
		this.fIntArr = new int[] {
		        this.fInt, 33, -44, 44, -55, 55, -55
		};
		this.fLong = 3145687927810L;
		this.fLongArr = new long[] {
		        this.fLong, -10, -200, -3000, -40000, 0
		};
		this.fShort = 12345;
		this.fShortArr = new short[] {
		        this.fShort, 7, -3, 6, -4, 5
		};
		this.fString = "This packet is for testing correctness of writing/reading packet";
		this.fStringArr = new String[] {
		        this.fString, "got it?", "if true it's good", ";   else bad "
		};
		
		this.fPacket = new NetworkMessage("Hello World");
		this.fPacketArr = new NetworkMessage[] {
		        this.fPacket, new NetworkMessage("Hello Again"),
		        new NetworkMessage("Bye.")
		};
	}
	
}
