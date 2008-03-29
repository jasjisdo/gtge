/*
 * NetworkRawPacket.java
 *
 * Created on May 11, 2007, 12:52 AM
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

package com.golden.gamedev.engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.golden.gamedev.engine.network.NetworkPacket;

/**
 * 
 * @author Paulus Tuerah
 */
public final class NetworkRawPacket extends NetworkPacket {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8720475364718926217L;
	
	public static short ID = Short.MIN_VALUE + 150; // the id for raw packet
	
	private byte[] packet;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates a new instance of NetworkRawPacket
	 */
	public NetworkRawPacket(byte[] packet) {
		this.packet = packet;
	}
	
	public short getID() {
		return NetworkRawPacket.ID;
	}
	
	public void setID(short id) {
		throw new UnsupportedOperationException(
		        "To change NetworkRawPacket ID, use NetworkRawPacket.ID = new_id instead.");
	}
	
	public byte[] getPacket() {
		return this.packet;
	}
	
	public void setPacket(byte[] packet) {
		this.packet = packet;
	}
	
	public void read(DataInputStream input) throws IOException {
		this.packet = new byte[input.available()];
		input.read(this.packet, 0, this.packet.length);
	}
	
	public void write(DataOutputStream output) throws IOException {
		output.write(this.packet, 0, this.packet.length);
	}
	
}
