/*
 * NetworkPing.java
 *
 * Created on May 11, 2007, 12:18 PM
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
public final class NetworkPing extends NetworkPacket {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8978606059231699856L;
	
	public static short ID = -25; // the id for ping packet
	
	public static final byte[] ping = new byte[0];
	
	private static final NetworkPing instance = new NetworkPing();
	
	public static NetworkPing getInstance() {
		return NetworkPing.instance;
	}
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	// private constructor
	private NetworkPing() {
	}
	
	public short getID() {
		return NetworkPing.ID;
	}
	
	public static void setID(short id) {
		ID = id;
	}
	
	public boolean isCompressed() {
		return false;
	}
	
	public void read(DataInputStream input) throws IOException {
	}
	
	public void write(DataOutputStream output) throws IOException {
	}
	
}
