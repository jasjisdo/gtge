/*
 * PacketManager.java
 *
 * Created on May 10, 2007, 1:04 PM
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

package com.golden.gamedev.engine.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.golden.gamedev.engine.network.manipulator.DataEncryption;
import com.golden.gamedev.engine.network.manipulator.DataObfuscation;
import com.golden.gamedev.engine.network.packet.NetworkMessage;
import com.golden.gamedev.engine.network.packet.NetworkPing;

/**
 * 
 * @author Paulus Tuerah
 */
public class PacketManager {
	
	public int BUFFER_SIZE = 2048; // bigger than any packable
	
	private short[] registeredID = new short[0]; // registered packet id
	private Object[] registeredObject = new Object[0]; // registered packet
														// object (NetworkPacket
														// class or object)
	
	// for security, we encrypt and obfuscate the packet
	private DataManipulator[] manipulator = new DataManipulator[] {
	        new DataEncryption(), new DataObfuscation()
	};
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates a new instance of PacketManager
	 */
	public PacketManager() {
	}
	
	public void registerPacket(short id, NetworkPacket packet) {
		// if (NetworkConfig.DEBUG) {
		// System.out.println("Register ID " + id + " for packet " + packet);
		// }
		
		if (packet.getID() == NetworkPacket.NULL_ID) {
			throw new RuntimeException("Packet ID " + packet + " must be set "
			        + "either by passing NetworkPacket.setID(short) or "
			        + "by overriding NetworkPacket.getID() method");
		}
		
		for (int i = 0; i < this.registeredID.length; i++) {
			if (this.registeredID[i] == id) {
				throw new RuntimeException("ID " + id
				        + " is already registered with "
				        + this.registeredObject[i]);
			}
		}
		
		this.registeredID = (short[]) NetworkUtil.expand(this.registeredID, 1);
		this.registeredObject = (Object[]) NetworkUtil.expand(
		        this.registeredObject, 1);
		
		int len = this.registeredID.length - 1;
		this.registeredID[len] = id;
		this.registeredObject[len] = packet;
	}
	
	public void registerPacket(short id, Class packetClass) {
		// if (NetworkConfig.DEBUG) {
		// String className = packetClass.getName();
		// int index = className.lastIndexOf(".");
		// if (index != -1) {
		// className = className.substring(index + 1);
		// }
		//
		// System.out.println("Register ID " + id + " for packet class " +
		// className);
		// }
		
		if (!NetworkPacket.class.isAssignableFrom(packetClass)) {
			throw new RuntimeException("Unable to register packet class "
			        + packetClass + ", not subclass of NetworkPacket class.");
		}
		
		for (int i = 0; i < this.registeredID.length; i++) {
			if (this.registeredID[i] == id) {
				throw new RuntimeException("ID " + id
				        + " is already registered with "
				        + this.registeredObject[i]);
			}
		}
		
		this.registeredID = (short[]) NetworkUtil.expand(this.registeredID, 1);
		this.registeredObject = (Object[]) NetworkUtil.expand(
		        this.registeredObject, 1);
		
		int len = this.registeredID.length - 1;
		this.registeredID[len] = id;
		this.registeredObject[len] = packetClass;
	}
	
	public synchronized byte[] pack(NetworkPacket packet) throws IOException {
		ByteArrayOutputStream baout = new ByteArrayOutputStream(); // the byte
																	// array
																	// stream
		DataOutputStream output = new DataOutputStream(baout);
		
		// write the packet id
		output.writeShort(this.getPacketID(packet));
		
		// compress the packet
		if (packet.isCompressed()) {
			output = this.compress(output);
		}
		
		// write the packet data
		packet.writePacket(output);
		
		output.close();
		
		byte[] data = baout.toByteArray();
		
		// manipulate packet for security
		if (this.manipulator != null) {
			try {
				for (int i = 0; i < this.manipulator.length; i++) {
					data = this.manipulator[i].manipulate(data);
				}
				
			}
			catch (Exception ex) {
				ex.printStackTrace();
				throw new IOException(ex.getMessage());
			}
		}
		
		return data;
	}
	
	public synchronized NetworkPacket unpack(byte[] data) throws IOException {
		// demanipulate packet data first
		if (this.manipulator != null) {
			try {
				for (int i = this.manipulator.length - 1; i >= 0; i--) {
					data = this.manipulator[i].demanipulate(data);
				}
				
			}
			catch (Exception ex) {
				ex.printStackTrace();
				throw new IOException(ex.getMessage());
			}
		}
		
		ByteArrayInputStream bain = new ByteArrayInputStream(data);
		DataInputStream input = new DataInputStream(bain);
		
		// read the packet id
		short packetID = input.readShort();
		
		// create the packet
		NetworkPacket packet = null;
		
		// read the packet data
		if (packetID == NetworkPing.ID) {
			// ping packet
			packet = NetworkPing.getInstance();
			
		}
		else {
			// network packet
			packet = (packetID == NetworkMessage.ID) ? new NetworkMessage()
			        : this.createPacket(packetID);
			
			// decompress
			if (packet.isCompressed()) {
				input = this.decompress(input);
			}
			
			// construct the packet
			packet.readPacket(input);
		}
		
		input.close();
		
		return packet;
	}
	
	protected short getPacketID(NetworkPacket packet) {
		if (packet.getID() != NetworkPacket.NULL_ID) {
			// packet id is already set
			// for example on new NetworkObject(UNIQUE_ID);
			return packet.getID();
		}
		
		// packet id is not set (null id)
		// we need to get the real packet id
		// from the type of the packet class
		
		Class packetClass = packet.getClass();
		
		for (int i = 0; i < this.registeredID.length; i++) {
			if (packetClass.equals(this.registeredObject[i])) {
				return this.registeredID[i];
			}
		}
		
		throw new RuntimeException(
		        packetClass
		                + " class has not been registered.\n"
		                + "Use NetworkConfig.registerPacket(short id, Class packetClass) to register the packet id.");
	}
	
	protected NetworkPacket createPacket(short id) {
		NetworkPacket packet = null;
		
		// construct the packet based on its id
		// if it's a class, create new instance
		// if it's an object, we clone it
		for (int i = 0; i < this.registeredID.length; i++) {
			if (this.registeredID[i] == id) {
				Object type = this.registeredObject[i];
				
				if (type instanceof Class) {
					// class type
					// create new instance
					try {
						packet = (NetworkPacket) ((Class) type).newInstance();
						
					}
					catch (Exception ex) {
						throw new RuntimeException(ex);
					}
					
				}
				else {
					// NetworkPacket object, clone from the object
					try {
						packet = (NetworkPacket) ((NetworkPacket) type).clone();
						
					}
					catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				}
				
				break;
			}
		}
		
		if (packet == null) {
			throw new RuntimeException(
			        "ID "
			                + id
			                + " has not been registered.\n"
			                + "Use NetworkConfig.registerPacket(short id, Class packetClass) to register the packet id.");
		}
		
		packet.setID(id);
		
		return packet;
	}
	
	protected DataOutputStream compress(DataOutputStream output)
	        throws IOException {
		return new DataOutputStream(new GZIPOutputStream(output,
		        this.BUFFER_SIZE));
	}
	
	protected DataInputStream decompress(DataInputStream input)
	        throws IOException {
		return new DataInputStream(new GZIPInputStream(input, this.BUFFER_SIZE));
	}
	
	public short[] getRegisteredID() {
		return this.registeredID;
	}
	
	public Object[] getRegisteredObject() {
		return this.registeredObject;
	}
	
	public DataManipulator[] getDataManipulator() {
		return this.manipulator;
	}
	
	public void setDataManipulator(DataManipulator[] dataManipulator) {
		this.manipulator = dataManipulator;
	}
	
}
