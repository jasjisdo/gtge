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
		
	public boolean			fBool;
	public boolean[]		fBoolArr;
	public byte				fByte;
	public byte[]			fByteArr;
	public char				fChar;
	public char[]			fCharArr;
	public double			fDoub;
	public double[]			fDoubArr;
	public float			fFloat;
	public float[]			fFloatArr;
	public int				fInt;
	public int[]			fIntArr;
	public long				fLong;
	public long[]			fLongArr;
	public short			fShort;
	public short[]			fShortArr;
	public String			fString;
	public String[]			fStringArr;

	public NetworkMessage	fPacket;	// may have nested object packet
	public NetworkMessage[]	fPacketArr;	// object packet must be subclass of NetworkPacket
	
	
	public AllFieldTypePacket() {
		// populate data
		
		fBool		= true;
		fBoolArr	= new boolean[] { fBool, true, false, true };
		fByte		= 100;
		fByteArr	= new byte[] { fByte, 1, -2, 3, -4, 5 };
		fChar		= 'x';
		fCharArr	= new char[] { fChar, 'a', 'b', 'c' };
		fDoub		= -0.12345678;
		fDoubArr	= new double[] { fDoub, 1.0, -2.0, 3.0, -4.0, 5.0 };
		fFloat		= 9.8765f;
		fFloatArr	= new float[] { fFloat, -5.0f, 5.1f, -5.2f };
		fInt		= 4321;
		fIntArr		= new int[] { fInt, 33, -44, 44, -55, 55, -55};
		fLong		= 3145687927810L;
		fLongArr	= new long[] { fLong, -10, -200, -3000, -40000, 0 };
		fShort		= 12345;
		fShortArr	= new short[] { fShort, 7, -3, 6, -4, 5 };
		fString		= "This packet is for testing correctness of writing/reading packet";
		fStringArr	= new String[] {fString, "got it?", "if true it's good", ";   else bad " };

		fPacket		= new NetworkMessage("Hello World");
		fPacketArr	= new NetworkMessage[] { fPacket, new NetworkMessage("Hello Again"), new NetworkMessage("Bye.") };
	}

}
