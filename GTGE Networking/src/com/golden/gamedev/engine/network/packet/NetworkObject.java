/*
 * NetworkObject.java
 *
 * Created on May 10, 2007, 9:53 PM
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

// JFC
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.golden.gamedev.engine.network.NetworkConfig;
import com.golden.gamedev.engine.network.NetworkPacket;

/**
 * 
 * @author Paulus Tuerah
 */
public final class NetworkObject extends NetworkPacket {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5501520858235039272L;
	
	public static final Class INTEGER_TYPE = Integer.TYPE,
	        DOUBLE_TYPE = Double.TYPE, BOOLEAN_TYPE = Boolean.TYPE,
	        FLOAT_TYPE = Float.TYPE, LONG_TYPE = Long.TYPE,
	        SHORT_TYPE = Short.TYPE, BYTE_TYPE = Byte.TYPE,
	        CHARACTER_TYPE = Character.TYPE;
	
	private Class type;
	
	private int intValue;
	private double doubleValue;
	private boolean booleanValue;
	private float floatValue;
	private long longValue;
	private short shortValue;
	private byte byteValue;
	private char charValue;
	private String stringValue;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/** Creates a new instance of NetworkObject */
	public NetworkObject(short id) {
		this.setID(id);
		this.type = null; // send id only
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, Class type) {
		this.setID(id);
		this.type = type;
		
		this.setCompressed(type.equals(NetworkPacket.STRING_TYPE));
	}
	
	public NetworkObject(short id, int intValue) {
		this.setID(id);
		this.intValue = intValue;
		this.type = NetworkObject.INTEGER_TYPE;
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, double doubleValue) {
		this.setID(id);
		this.doubleValue = doubleValue;
		this.type = NetworkObject.DOUBLE_TYPE;
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, boolean booleanValue) {
		this.setID(id);
		this.booleanValue = booleanValue;
		this.type = NetworkObject.BOOLEAN_TYPE;
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, float floatValue) {
		this.setID(id);
		this.floatValue = floatValue;
		this.type = NetworkObject.FLOAT_TYPE;
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, long longValue) {
		this.setID(id);
		this.longValue = longValue;
		this.type = NetworkObject.LONG_TYPE;
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, short shortValue) {
		this.setID(id);
		this.shortValue = shortValue;
		this.type = NetworkObject.SHORT_TYPE;
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, byte byteValue) {
		this.setID(id);
		this.byteValue = byteValue;
		this.type = NetworkObject.BYTE_TYPE;
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, char charValue) {
		this.setID(id);
		this.charValue = charValue;
		this.type = NetworkObject.INTEGER_TYPE;
		
		this.setCompressed(false);
	}
	
	public NetworkObject(short id, String stringValue) {
		this.setID(id);
		this.stringValue = stringValue;
		this.type = String.class;
		
		this.setCompressed(true);
	}
	
	public void read(DataInputStream input) throws IOException {
		// read the packet data
		if (this.type == null) {
			return; // send id only
		}
		
		if (this.type == NetworkObject.INTEGER_TYPE) {
			this.intValue = input.readInt();
			
		}
		else if (this.type == NetworkObject.DOUBLE_TYPE) {
			this.doubleValue = input.readDouble();
			
		}
		else if (this.type == NetworkObject.BOOLEAN_TYPE) {
			this.booleanValue = input.readBoolean();
			
		}
		else if (this.type == NetworkObject.FLOAT_TYPE) {
			this.floatValue = input.readFloat();
			
		}
		else if (this.type == NetworkObject.LONG_TYPE) {
			this.longValue = input.readLong();
			
		}
		else if (this.type == NetworkObject.SHORT_TYPE) {
			this.shortValue = input.readShort();
			
		}
		else if (this.type == NetworkObject.BYTE_TYPE) {
			this.byteValue = input.readByte();
			
		}
		else if (this.type == NetworkObject.CHARACTER_TYPE) {
			this.charValue = input.readChar();
			
		}
		else {
			this.stringValue = input.readUTF();
		}
	}
	
	public void write(DataOutputStream output) throws IOException {
		// write the packet data
		if (this.type == null) {
			return; // send id only
		}
		
		if (this.type == NetworkObject.INTEGER_TYPE) {
			output.writeInt(this.intValue);
			
		}
		else if (this.type == NetworkObject.DOUBLE_TYPE) {
			output.writeDouble(this.doubleValue);
			
		}
		else if (this.type == NetworkObject.BOOLEAN_TYPE) {
			output.writeBoolean(this.booleanValue);
			
		}
		else if (this.type == NetworkObject.FLOAT_TYPE) {
			output.writeFloat(this.floatValue);
			
		}
		else if (this.type == NetworkObject.LONG_TYPE) {
			output.writeLong(this.longValue);
			
		}
		else if (this.type == NetworkObject.SHORT_TYPE) {
			output.writeShort(this.shortValue);
			
		}
		else if (this.type == NetworkObject.BYTE_TYPE) {
			output.writeByte(this.byteValue);
			
		}
		else if (this.type == NetworkObject.CHARACTER_TYPE) {
			output.writeChar(this.charValue);
			
		}
		else {
			output.writeUTF(this.stringValue);
		}
	}
	
	public Class getType() {
		return this.type;
	}
	
	public void setType(Class type) {
		this.type = type;
	}
	
	public int getInt() {
		if (this.type != NetworkObject.INTEGER_TYPE) {
			throw new RuntimeException("Attempt to get integer from "
			        + this.type + " class");
		}
		
		return this.intValue;
	}
	
	public void setInt(int intValue) {
		if (this.type != NetworkObject.INTEGER_TYPE) {
			throw new RuntimeException("Attempt to set integer from "
			        + this.type + " class");
		}
		
		this.intValue = intValue;
	}
	
	public double getDouble() {
		if (this.type != NetworkObject.INTEGER_TYPE) {
			throw new RuntimeException("Attempt to get double from "
			        + this.type + " class");
		}
		
		return this.doubleValue;
	}
	
	public void setDouble(double doubleValue) {
		if (this.type != NetworkObject.DOUBLE_TYPE) {
			throw new RuntimeException("Attempt to set double from "
			        + this.type + " class");
		}
		
		this.doubleValue = doubleValue;
	}
	
	public boolean getBoolean() {
		if (this.type != NetworkObject.BOOLEAN_TYPE) {
			throw new RuntimeException("Attempt to get boolean from "
			        + this.type + " class");
		}
		
		return this.booleanValue;
	}
	
	public void setBoolean(boolean booleanValue) {
		if (this.type != NetworkObject.BOOLEAN_TYPE) {
			throw new RuntimeException("Attempt to set boolean from "
			        + this.type + " class");
		}
		
		this.booleanValue = booleanValue;
	}
	
	public float getFloat() {
		if (this.type != NetworkObject.FLOAT_TYPE) {
			throw new RuntimeException("Attempt to get float from " + this.type
			        + " class");
		}
		
		return this.floatValue;
	}
	
	public void setFloat(float floatValue) {
		if (this.type != NetworkObject.FLOAT_TYPE) {
			throw new RuntimeException("Attempt to set float from " + this.type
			        + " class");
		}
		
		this.floatValue = floatValue;
	}
	
	public long getLong() {
		if (this.type != NetworkObject.LONG_TYPE) {
			throw new RuntimeException("Attempt to get long from " + this.type
			        + " class");
		}
		
		return this.longValue;
	}
	
	public void setLong(long longValue) {
		if (this.type != NetworkObject.LONG_TYPE) {
			throw new RuntimeException("Attempt to set long from " + this.type
			        + " class");
		}
		
		this.longValue = longValue;
	}
	
	public short getShort() {
		if (this.type != NetworkObject.SHORT_TYPE) {
			throw new RuntimeException("Attempt to get short from " + this.type
			        + " class");
		}
		
		return this.shortValue;
	}
	
	public void setShort(short shortValue) {
		if (this.type != NetworkObject.SHORT_TYPE) {
			throw new RuntimeException("Attempt to set short from " + this.type
			        + " class");
		}
		
		this.shortValue = shortValue;
	}
	
	public byte getByte() {
		if (this.type != NetworkObject.BYTE_TYPE) {
			throw new RuntimeException("Attempt to get byte from " + this.type
			        + " class");
		}
		
		return this.byteValue;
	}
	
	public void setByte(byte byteValue) {
		if (this.type != NetworkObject.BYTE_TYPE) {
			throw new RuntimeException("Attempt to set byte from " + this.type
			        + " class");
		}
		
		this.byteValue = byteValue;
	}
	
	public char getChar() {
		if (this.type != NetworkObject.CHARACTER_TYPE) {
			throw new RuntimeException("Attempt to get char from " + this.type
			        + " class");
		}
		
		return this.charValue;
	}
	
	public void setChar(char charValue) {
		if (this.type != NetworkObject.CHARACTER_TYPE) {
			throw new RuntimeException("Attempt to set char from " + this.type
			        + " class");
		}
		
		this.charValue = charValue;
	}
	
	public String getString() {
		if (this.type == null || this.type.isPrimitive()) {
			throw new RuntimeException("Attempt to get String from "
			        + this.type + " class");
		}
		
		return this.stringValue;
	}
	
	public void setString(String stringValue) {
		if (this.type == null || this.type.isPrimitive()) {
			throw new RuntimeException("Attempt to set String from "
			        + this.type + " class");
		}
		
		this.stringValue = stringValue;
	}
	
	private static HashMap map;
	
	/**
	 * Set this <code>NetworkObject</code> packet description to describe what
	 * this packet for when <code>NetworkConfig.DEBUG</code> is set to true.
	 */
	public NetworkPacket setDescription(String description) {
		if (NetworkConfig.DEBUG) {
			if (NetworkObject.map == null) {
				NetworkObject.map = new HashMap();
			}
			
			NetworkObject.map.put(new Short(this.getID()), description);
		}
		
		return this;
	}
	
	public String toString() {
		if (!NetworkConfig.DEBUG) {
			return super.toString();
		}
		
		StringBuffer buff = new StringBuffer();
		
		buff.append("NetworkObject ");
		if (NetworkObject.map != null) {
			String description = (String) NetworkObject.map.get(new Short(this
			        .getID()));
			
			if (description != null) {
				buff.append(description).append(" ");
			}
		}
		if (this.isSendSender()) {
			buff.append("Sender ID ").append(this.getSender()).append(" ");
		}
		buff.append("ID ").append(this.getID());
		if (this.isSendCode()) {
			buff.append(" (code=").append(this.getCode()).append(")");
		}
		buff.append(": ");
		
		if (this.type == null) {
			buff.append("send ID only");
		}
		else if (this.type == NetworkObject.INTEGER_TYPE) {
			buff.append("int = " + this.intValue);
		}
		else if (this.type == NetworkObject.DOUBLE_TYPE) {
			buff.append("double = " + this.doubleValue);
		}
		else if (this.type == NetworkObject.BOOLEAN_TYPE) {
			buff.append("boolean = " + this.booleanValue);
		}
		else if (this.type == NetworkObject.FLOAT_TYPE) {
			buff.append("float = " + this.floatValue);
		}
		else if (this.type == NetworkObject.LONG_TYPE) {
			buff.append("long = " + this.longValue);
		}
		else if (this.type == NetworkObject.SHORT_TYPE) {
			buff.append("short = " + this.shortValue);
		}
		else if (this.type == NetworkObject.BYTE_TYPE) {
			buff.append("byte = " + this.byteValue);
		}
		else if (this.type == NetworkObject.CHARACTER_TYPE) {
			buff.append("char = " + this.charValue);
		}
		else {
			buff.append("String = " + this.stringValue);
		}
		
		return buff.toString();
	}
	
}
