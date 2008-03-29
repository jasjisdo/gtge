/*
 * NetworkPacket.java
 *
 * Created on May 6, 2007, 12:12 PM
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.golden.gamedev.engine.BaseClient;
import com.golden.gamedev.engine.network.packet.NetworkObject;

/**
 * 
 * @author Paulus Tuerah
 */
public abstract class NetworkPacket implements Serializable, Cloneable {
	
	public static final Class STRING_TYPE = String.class;
	public static final Class NETWORK_PACKET_TYPE = NetworkPacket.class;
	
	/**
	 * Indicates the default <code>NetworkPacket</code> without an ID. Every
	 * <code>NetworkPacket</code> without an ID must be registered using
	 * {@link NetworkConfig#registerPacket(short, Class)}.
	 */
	public static short NULL_ID = Short.MIN_VALUE;
	private static short UNIQUE_CODE = 0;
	
	protected static synchronized short getUniqueCode() {
		if (NetworkPacket.UNIQUE_CODE >= Short.MAX_VALUE) {
			NetworkPacket.UNIQUE_CODE = 0;
		}
		
		return ++NetworkPacket.UNIQUE_CODE;
	}
	
	private short id = NetworkPacket.NULL_ID;
	private short code;
	private short sender;
	
	private boolean consumed = NetworkConfig.isAutoConsumed();
	private boolean compressed = NetworkConfig.isDefaultCompressed();
	private int expiredTime = NetworkConfig.getDefaultExpiredTime();
	
	private boolean sendCode = NetworkConfig.isDefaultSendCode();
	private boolean sendSender = NetworkConfig.isDefaultSendSender();
	
	private long receivedTime;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/** Creates a new instance of NetworkPacket */
	public NetworkPacket() {
	}
	
	protected final void readPacket(DataInputStream input) throws IOException {
		this.receivedTime = System.currentTimeMillis();
		
		// read the packet code and packet sender first
		if (this.sendCode) {
			this.code = input.readShort();
		}
		if (this.sendSender) {
			this.sender = input.readShort();
		}
		
		this.read(input);
	}
	
	protected final void writePacket(DataOutputStream output)
	        throws IOException {
		// write the packet code and packet sender first
		if (this.sendCode) {
			output.writeShort(this.code);
		}
		if (this.sendSender) {
			output.writeShort(this.sender);
		}
		
		this.write(output);
	}
	
	public void read(DataInputStream input) throws IOException {
		// System.out.println("READING " + this.getClass());
		
		Field[] fields = this.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			int modifiers = fields[i].getModifiers();
			
			// field with final or static or transient modifiers is not saved
			if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)
			        || Modifier.isTransient(modifiers)) {
				continue;
			}
			
			// read field
			// System.out.println("read " + fields[i].getName());
			Class fieldClass = fields[i].getType();
			
			try {
				// primitive data type
				if (fieldClass.isPrimitive()) {
					if (fieldClass == Integer.TYPE) {
						fields[i].setInt(this, input.readInt());
						
					}
					else if (fieldClass == Double.TYPE) {
						fields[i].setDouble(this, input.readDouble());
						
					}
					else if (fieldClass == Boolean.TYPE) {
						fields[i].setBoolean(this, input.readBoolean());
						
					}
					else if (fieldClass == Float.TYPE) {
						fields[i].setFloat(this, input.readFloat());
						
					}
					else if (fieldClass == Long.TYPE) {
						fields[i].setLong(this, input.readLong());
						
					}
					else if (fieldClass == Short.TYPE) {
						fields[i].setShort(this, input.readShort());
						
					}
					else if (fieldClass == Byte.TYPE) {
						fields[i].setByte(this, input.readByte());
						
					}
					else if (fieldClass == Character.TYPE) {
						fields[i].setChar(this, input.readChar());
					}
					
					// array data type
				}
				else if (fieldClass.isArray()) {
					short length = input.readShort();
					fieldClass = fieldClass.getComponentType();
					
					Object arr = Array.newInstance(fieldClass, length);
					
					// array of primitive
					if (fieldClass.isPrimitive()) {
						if (fieldClass == Integer.TYPE) {
							for (int j = 0; j < length; j++) {
								Array.setInt(arr, j, input.readInt());
							}
							
						}
						else if (fieldClass == Double.TYPE) {
							for (int j = 0; j < length; j++) {
								Array.setDouble(arr, j, input.readDouble());
							}
							
						}
						else if (fieldClass == Boolean.TYPE) {
							for (int j = 0; j < length; j++) {
								Array.setBoolean(arr, j, input.readBoolean());
							}
							
						}
						else if (fieldClass == Float.TYPE) {
							for (int j = 0; j < length; j++) {
								Array.setFloat(arr, j, input.readFloat());
							}
							
						}
						else if (fieldClass == Long.TYPE) {
							for (int j = 0; j < length; j++) {
								Array.setLong(arr, j, input.readLong());
							}
							
						}
						else if (fieldClass == Short.TYPE) {
							for (int j = 0; j < length; j++) {
								Array.setShort(arr, j, input.readShort());
							}
							
						}
						else if (fieldClass == Byte.TYPE) {
							for (int j = 0; j < length; j++) {
								Array.setByte(arr, j, input.readByte());
							}
							
						}
						else if (fieldClass == Character.TYPE) {
							for (int j = 0; j < length; j++) {
								Array.setChar(arr, j, input.readChar());
							}
						}
						
						// array inner packet, recursively read
					}
					else if (NetworkPacket.NETWORK_PACKET_TYPE
					        .isAssignableFrom(fieldClass)) {
						// System.out.println("READING [ARRAY] INNER PACKET");
						for (int j = 0; j < length; j++) {
							NetworkPacket innerPacket = (NetworkPacket) fieldClass
							        .newInstance();
							innerPacket.read(input);
							
							Array.set(arr, j, innerPacket);
						}
						
						// array of string
					}
					else {
						for (int j = 0; j < length; j++) {
							Array.set(arr, j, input.readUTF());
						}
					}
					
					fields[i].set(this, arr);
					
					// inner packet, recursively read
				}
				else if (NetworkPacket.NETWORK_PACKET_TYPE
				        .isAssignableFrom(fieldClass)) {
					// System.out.println("READING INNER PACKET");
					NetworkPacket innerPacket = (NetworkPacket) fieldClass
					        .newInstance();
					innerPacket.read(input);
					
					fields[i].set(this, innerPacket);
					
					// string type
				}
				else {
					fields[i].set(this, input.readUTF());
				}
				
			}
			catch (Exception ex) {
				// ex.printStackTrace();
				throw new IOException(ex.getMessage());
			}
		}
	}
	
	public void write(DataOutputStream output) throws IOException {
		// System.out.println("WRITING " + this.getClass());
		
		Field[] fields = this.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			int modifiers = fields[i].getModifiers();
			
			// field with final or static or transient modifiers is not saved
			if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)
			        || Modifier.isTransient(modifiers)) {
				continue;
			}
			
			// write field
			// System.out.println("write " + fields[i].getName());
			Class fieldClass = fields[i].getType();
			
			try {
				// primitive data type
				if (fieldClass.isPrimitive()) {
					if (fieldClass == Integer.TYPE) {
						output.writeInt(fields[i].getInt(this));
						
					}
					else if (fieldClass == Double.TYPE) {
						output.writeDouble(fields[i].getDouble(this));
						
					}
					else if (fieldClass == Boolean.TYPE) {
						output.writeBoolean(fields[i].getBoolean(this));
						
					}
					else if (fieldClass == Float.TYPE) {
						output.writeFloat(fields[i].getFloat(this));
						
					}
					else if (fieldClass == Long.TYPE) {
						output.writeLong(fields[i].getLong(this));
						
					}
					else if (fieldClass == Short.TYPE) {
						output.writeShort(fields[i].getShort(this));
						
					}
					else if (fieldClass == Byte.TYPE) {
						output.writeByte(fields[i].getByte(this));
						
					}
					else if (fieldClass == Character.TYPE) {
						output.writeChar(fields[i].getChar(this));
					}
					
					// array data type
				}
				else if (fieldClass.isArray()) {
					Object arr = fields[i].get(this);
					short length = (short) Array.getLength(arr);
					output.writeShort(length);
					
					fieldClass = fieldClass.getComponentType();
					
					// array of primitive
					if (fieldClass.isPrimitive()) {
						if (fieldClass == Integer.TYPE) {
							for (int j = 0; j < length; j++) {
								output.writeInt(Array.getInt(arr, j));
							}
							
						}
						else if (fieldClass == Double.TYPE) {
							for (int j = 0; j < length; j++) {
								output.writeDouble(Array.getDouble(arr, j));
							}
							
						}
						else if (fieldClass == Boolean.TYPE) {
							for (int j = 0; j < length; j++) {
								output.writeBoolean(Array.getBoolean(arr, j));
							}
							
						}
						else if (fieldClass == Float.TYPE) {
							for (int j = 0; j < length; j++) {
								output.writeFloat(Array.getFloat(arr, j));
							}
							
						}
						else if (fieldClass == Long.TYPE) {
							for (int j = 0; j < length; j++) {
								output.writeLong(Array.getLong(arr, j));
							}
							
						}
						else if (fieldClass == Short.TYPE) {
							for (int j = 0; j < length; j++) {
								output.writeShort(Array.getShort(arr, j));
							}
							
						}
						else if (fieldClass == Byte.TYPE) {
							for (int j = 0; j < length; j++) {
								output.writeByte(Array.getByte(arr, j));
							}
							
						}
						else if (fieldClass == Character.TYPE) {
							for (int j = 0; j < length; j++) {
								output.writeChar(Array.getChar(arr, j));
							}
						}
						
						// array inner packet, recursively write
					}
					else if (NetworkPacket.NETWORK_PACKET_TYPE
					        .isAssignableFrom(fieldClass)) {
						// System.out.println("WRITING ARRAY INNER PACKET");
						if (fieldClass
						        .equals(NetworkPacket.NETWORK_PACKET_TYPE)) {
							throw new UnsupportedOperationException(
							        "Field array "
							                + fields[i].getName()
							                + " must be subclass of NetworkPacket, "
							                + "can not use the NetworkPacket class directly.");
						}
						
						for (int j = 0; j < length; j++) {
							NetworkPacket innerPacket = (NetworkPacket) Array
							        .get(arr, j);
							innerPacket.write(output);
						}
						
						// array of string
					}
					else if (NetworkPacket.STRING_TYPE
					        .isAssignableFrom(fieldClass)) {
						for (int j = 0; j < length; j++) {
							output.writeUTF((String) Array.get(arr, j));
						}
						
						// invalid array type
					}
					else {
						throw new UnsupportedOperationException(
						        "Field array "
						                + fields[i].getName()
						                + " must be primitive, "
						                + "or String class, or subclass of NetworkPacket class.");
					}
					
					fields[i].set(this, arr);
					
					// inner packet, recursively write
				}
				else if (NetworkPacket.NETWORK_PACKET_TYPE
				        .isAssignableFrom(fieldClass)) {
					// System.out.println("WRITING INNER PACKET");
					if (fieldClass.equals(NetworkPacket.NETWORK_PACKET_TYPE)) {
						throw new UnsupportedOperationException(
						        "Field "
						                + fields[i].getName()
						                + " must be subclass of NetworkPacket, "
						                + "can not use the NetworkPacket class directly.");
					}
					
					NetworkPacket innerPacket = (NetworkPacket) fields[i]
					        .get(this);
					innerPacket.write(output);
					
					// string data type
				}
				else if (NetworkPacket.STRING_TYPE.isAssignableFrom(fieldClass)) {
					output.writeUTF((String) fields[i].get(this));
					
					// invalid type
				}
				else {
					throw new UnsupportedOperationException(
					        "Field "
					                + fields[i].getName()
					                + " must be primitive, "
					                + "or String class, or subclass of NetworkPacket class.");
				}
				
			}
			catch (Exception ex) {
				throw new IOException(ex.getMessage());
			}
		}
	}
	
	public void consume() {
		this.consumed = true;
	}
	
	public boolean isExpired() {
		if (this.expiredTime == -1) {
			// never expired packet
			return false;
		}
		
		return ((System.currentTimeMillis() - this.receivedTime) > this.expiredTime);
	}
	
	/**
	 * Returns this packet ID.
	 * <p>
	 * By default <code>NetworkPacket</code> return {@link #NULL_ID}, the
	 * developer need to register their NetworkPacket class id by using
	 * {@link NetworkConfig#registerPacket(short, Class)}.
	 */
	public short getID() {
		return this.id;
	}
	
	protected void setID(short id) {
		this.id = id;
	}
	
	/**
	 * To send packet unique code, the application need to set send code flag to
	 * true, by using {@link NetworkPacket#setSendCode(boolean)}.
	 * 
	 * @return the packet unique code to be sent
	 */
	public short generateCode() {
		this.setCode(NetworkPacket.getUniqueCode());
		
		return this.code;
	}
	
	public short getCode() {
		if (!this.sendCode) {
			throw new RuntimeException(
			        "In order to get packet code, packet send code must be set to true.");
		}
		
		return this.code;
	}
	
	public void setCode(NetworkPacket retrievedPacketCode) {
		this.setCode(retrievedPacketCode.code);
	}
	
	/**
	 * @see #generateCode()
	 */
	protected void setCode(short code) {
		this.code = code;
		
		if (!this.sendCode) {
			if (!this.getClass().equals(NetworkObject.class)) {
				throw new RuntimeException(
				        "In order to set packet code, packet send code must be set to true.");
				
			}
			else {
				this.sendCode = true;
			}
		}
	}
	
	public short getSender() {
		if (!this.sendSender) {
			throw new RuntimeException(
			        "In order to get packet sender, packet send sender must be set to true.");
		}
		
		return this.sender;
	}
	
	public NetworkPacket setSender(BaseClient sender) {
		this.setSender(sender.getClientID());
		
		return this;
	}
	
	protected void setSender(short sender) {
		this.sender = sender;
		
		this.sendSender = true;
	}
	
	/** ************************************************************************* */
	/** **************************** BEANS METHODS ****************************** */
	/** ************************************************************************* */
	
	public boolean isConsumed() {
		return this.consumed;
	}
	
	public NetworkPacket setConsumed(boolean consumed) {
		this.consumed = consumed;
		
		return this;
	}
	
	public boolean isCompressed() {
		return this.compressed;
	}
	
	public NetworkPacket setCompressed(boolean compressed) {
		this.compressed = compressed;
		
		return this;
	}
	
	public int getExpiredTime() {
		return this.expiredTime;
	}
	
	public NetworkPacket setExpiredTime(int expiredTime) {
		this.expiredTime = expiredTime;
		
		return this;
	}
	
	public boolean isSendCode() {
		return this.sendCode;
	}
	
	/**
	 * If send code is set to true, this packet will send its unique code.
	 * 
	 * @see #generateCode()
	 */
	public NetworkPacket setSendCode(boolean sendCode) {
		this.sendCode = sendCode;
		
		return this;
	}
	
	public boolean isSendSender() {
		return this.sendSender;
	}
	
	public NetworkPacket setSendSender(boolean sendSender) {
		this.sendSender = sendSender;
		
		return this;
	}
	
	/**
	 * NetworkPacket is cloneable, used by
	 * {@link PacketManager#registerPacket(short, NetworkPacket)} to clone the
	 * packet.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	protected static boolean useComma; // for array field formatting
	
	public String toString() {
		if (!NetworkConfig.DEBUG) {
			return super.toString();
		}
		
		StringBuffer buff = new StringBuffer();
		
		String packetClass = this.getClass().getName();
		if (packetClass.lastIndexOf('.') != -1) {
			packetClass = packetClass
			        .substring(packetClass.lastIndexOf('.') + 1);
		}
		buff.append("Packet");
		if (this.sendSender) {
			buff.append(" Sender ID ").append(this.sender);
		}
		buff.append(": ").append(packetClass);
		if (this.sendCode) {
			buff.append(" (code=").append(this.code).append(")");
		}
		buff.append((NetworkPacket.useComma) ? " -> " : "\n");
		
		Field[] fields = this.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			int modifiers = fields[i].getModifiers();
			
			// field with private or final or static or transient modifiers is
			// not saved
			if (Modifier.isPrivate(modifiers) || Modifier.isFinal(modifiers)
			        || Modifier.isStatic(modifiers)
			        || Modifier.isTransient(modifiers)) {
				continue;
			}
			
			try {
				Class cls = fields[i].getType();
				if (!cls.isArray()) {
					String fieldClass = cls.getName();
					if (fieldClass.lastIndexOf('.') != -1) {
						fieldClass = fieldClass.substring(fieldClass
						        .lastIndexOf('.') + 1);
					}
					if (!NetworkPacket.useComma) {
						buff.append("> ");
					}
					buff.append(fieldClass).append(" ").append(
					        fields[i].getName()).append(" = ").append(
					        fields[i].get(this));
					buff.append((NetworkPacket.useComma) ? ", " : "\n");
					
				}
				else {
					cls = cls.getComponentType();
					
					String fieldClass = cls.getName();
					if (fieldClass.lastIndexOf('.') != -1) {
						fieldClass = fieldClass.substring(fieldClass
						        .lastIndexOf('.') + 1);
					}
					if (!NetworkPacket.useComma) {
						buff.append("> ");
					}
					buff.append(fieldClass).append("[] ").append(
					        fields[i].getName()).append(" = ");
					
					Object arr = fields[i].get(this);
					if (arr == null) {
						buff.append("null");
						
					}
					else {
						buff.append("[ ");
						
						int len = Array.getLength(arr);
						for (int j = 0; j < len; j++) {
							NetworkPacket.useComma = true;
							
							buff.append(Array.get(arr, j));
							if (j < len - 1) {
								buff.append("; ");
							}
						}
						NetworkPacket.useComma = false;
						
						buff.append(" ]");
					}
					
					buff.append((NetworkPacket.useComma) ? ", " : "\n");
				}
				
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		buff.append("End-Packet");
		
		NetworkPacket.useComma = false;
		
		return buff.toString();
	}
	
}
