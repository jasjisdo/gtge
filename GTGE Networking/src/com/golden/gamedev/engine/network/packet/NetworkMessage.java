/*
 * NetworkMessage.java
 *
 * Created on May 10, 2007, 12:04 PM
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

import com.golden.gamedev.engine.network.NetworkConfig;
import com.golden.gamedev.engine.network.NetworkPacket;

/**
 * 
 * @author Paulus Tuerah
 */
public final class NetworkMessage extends NetworkPacket {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5676154023701486957L;
	
	public static short ID = -255; // the id for network message
	
	private String message;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/** Creates a new instance of NetworkMessage */
	public NetworkMessage() {
	}
	
	public NetworkMessage(String message) {
		this.message = message;
	}
	
	public short getID() {
		return NetworkMessage.ID;
	}
	
	public void setID(short id) {
		throw new UnsupportedOperationException(
		        "To change NetworkMessage ID, use NetworkMessage.ID = new_id instead. "
		                + "Or use NetworkObject to send String message with unique ID.");
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void read(DataInputStream input) throws IOException {
		// read the packet data
		this.message = input.readUTF();
	}
	
	public void write(DataOutputStream output) throws IOException {
		// write the packet data
		output.writeUTF(this.message);
	}
	
	public String toString() {
		if (!NetworkConfig.DEBUG) {
			return super.toString();
		}
		
		return "NetworkMessage: " + this.message;
	}
	
}
