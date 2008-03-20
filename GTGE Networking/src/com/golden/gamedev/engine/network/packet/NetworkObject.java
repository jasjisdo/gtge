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

// GTGE
import com.golden.gamedev.engine.network.*;
import java.util.HashMap;


/**
 *
 * @author Paulus Tuerah
 */
public final class NetworkObject extends NetworkPacket {

	public static final Class INTEGER_TYPE		= Integer.TYPE, 
							  DOUBLE_TYPE		= Double.TYPE,
							  BOOLEAN_TYPE		= Boolean.TYPE,
							  FLOAT_TYPE		= Float.TYPE,
							  LONG_TYPE			= Long.TYPE,
							  SHORT_TYPE		= Short.TYPE,
							  BYTE_TYPE			= Byte.TYPE,
							  CHARACTER_TYPE	= Character.TYPE;
	

	private Class		type;

	private int			intValue;
	private double		doubleValue;
	private boolean		booleanValue;
	private float		floatValue;
	private long		longValue;
	private short		shortValue;
	private byte		byteValue;
	private char		charValue;
	private String		stringValue;

	
 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/
	
	/** Creates a new instance of NetworkObject */
	public NetworkObject(short id) {
		this.setID(id);
		this.type			= null; // send id only
		
		setCompressed(false);
	}
	public NetworkObject(short id, Class type) {
		this.setID(id);
		this.type			= type;
		
		setCompressed(type.equals(STRING_TYPE));
	}
	
	public NetworkObject(short id, int intValue) {
		this.setID(id);
		this.intValue		= intValue;
		this.type			= INTEGER_TYPE;
		
		setCompressed(false);
	}
	public NetworkObject(short id, double doubleValue) {
		this.setID(id);
		this.doubleValue	= doubleValue;
		this.type			= DOUBLE_TYPE;
		
		setCompressed(false);
	}
	public NetworkObject(short id, boolean booleanValue) {
		this.setID(id);
		this.booleanValue	= booleanValue;
		this.type			= BOOLEAN_TYPE;
		
		setCompressed(false);
	}
	public NetworkObject(short id, float floatValue) {
		this.setID(id);
		this.floatValue		= floatValue;
		this.type			= FLOAT_TYPE;
		
		setCompressed(false);
	}
	public NetworkObject(short id, long longValue) {
		this.setID(id);
		this.longValue		= longValue;
		this.type			= LONG_TYPE;
		
		setCompressed(false);
	}
	public NetworkObject(short id, short shortValue) {
		this.setID(id);
		this.shortValue		= shortValue;
		this.type			= SHORT_TYPE;
	
		setCompressed(false);
	}
	public NetworkObject(short id, byte byteValue) {
		this.setID(id);
		this.byteValue		= byteValue;
		this.type			= BYTE_TYPE;
		
		setCompressed(false);
	}
	public NetworkObject(short id, char charValue) {
		this.setID(id);
		this.charValue		= charValue;
		this.type			= INTEGER_TYPE;
		
		setCompressed(false);
	}
	public NetworkObject(short id, String stringValue) {
		this.setID(id);
		this.stringValue	= stringValue;
		this.type			= String.class;
		
		setCompressed(true);
	}
	
	
	
	public void read(DataInputStream input) throws IOException {
		// read the packet data
		if (type == null) return;	// send id only
		
		
		if (type == INTEGER_TYPE) {
			intValue		= input.readInt();

		} else if (type == DOUBLE_TYPE) {
			doubleValue		= input.readDouble();

		} else if (type == BOOLEAN_TYPE) {
			booleanValue	= input.readBoolean();

		} else if (type == FLOAT_TYPE) {
			floatValue		= input.readFloat();

		} else if (type == LONG_TYPE) {
			longValue		= input.readLong();

		} else if (type == SHORT_TYPE) {
			shortValue		= input.readShort();

		} else if (type == BYTE_TYPE) {
			byteValue		= input.readByte();

		} else if (type == CHARACTER_TYPE) {
			charValue		= input.readChar();

		} else {
			stringValue		= input.readUTF();
		}			
	}
	
	public void write(DataOutputStream output) throws IOException {
		// write the packet data
		if (type == null) return;	// send id only
		
		
		if (type == INTEGER_TYPE) {
			output.writeInt(intValue);

		} else if (type == DOUBLE_TYPE) {
			output.writeDouble(doubleValue);

		} else if (type == BOOLEAN_TYPE) {
			output.writeBoolean(booleanValue);

		} else if (type == FLOAT_TYPE) {
			output.writeFloat(floatValue);

		} else if (type == LONG_TYPE) {
			output.writeLong(longValue);

		} else if (type == SHORT_TYPE) {
			output.writeShort(shortValue);

		} else if (type == BYTE_TYPE) {
			output.writeByte(byteValue);

		} else if (type == CHARACTER_TYPE) {
			output.writeChar(charValue);

		} else {
			output.writeUTF(stringValue);
		}			
	}

	
	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	
	public int getInt() {
		if (type != INTEGER_TYPE) {
			throw new RuntimeException("Attempt to get integer from " + type + " class");
		}
		
		return intValue;
	}

	public void setInt(int intValue) {
		if (type != INTEGER_TYPE) {
			throw new RuntimeException("Attempt to set integer from " + type + " class");
		}

		this.intValue = intValue;
	}

	public double getDouble() {
		if (type != INTEGER_TYPE) {
			throw new RuntimeException("Attempt to get double from " + type + " class");
		}
		
		return doubleValue;
	}

	public void setDouble(double doubleValue) {
		if (type != DOUBLE_TYPE) {
			throw new RuntimeException("Attempt to set double from " + type + " class");
		}

		this.doubleValue = doubleValue;
	}

	public boolean getBoolean() {
		if (type != BOOLEAN_TYPE) {
			throw new RuntimeException("Attempt to get boolean from " + type + " class");
		}
		
		return booleanValue;
	}

	public void setBoolean(boolean booleanValue) {
		if (type != BOOLEAN_TYPE) {
			throw new RuntimeException("Attempt to set boolean from " + type + " class");
		}

		this.booleanValue = booleanValue;
	}

	public float getFloat() {
		if (type != FLOAT_TYPE) {
			throw new RuntimeException("Attempt to get float from " + type + " class");
		}
		
		return floatValue;
	}

	public void setFloat(float floatValue) {
		if (type != FLOAT_TYPE) {
			throw new RuntimeException("Attempt to set float from " + type + " class");
		}

		this.floatValue = floatValue;
	}
	
	public long getLong() {
		if (type != LONG_TYPE) {
			throw new RuntimeException("Attempt to get long from " + type + " class");
		}
		
		return longValue;
	}

	public void setLong(long longValue) {
		if (type != LONG_TYPE) {
			throw new RuntimeException("Attempt to set long from " + type + " class");
		}

		this.longValue = longValue;
	}

	public short getShort() {
		if (type != SHORT_TYPE) {
			throw new RuntimeException("Attempt to get short from " + type + " class");
		}
		
		return shortValue;
	}

	public void setShort(short shortValue) {
		if (type != SHORT_TYPE) {
			throw new RuntimeException("Attempt to set short from " + type + " class");
		}

		this.shortValue = shortValue;
	}

	public byte getByte() {
		if (type != BYTE_TYPE) {
			throw new RuntimeException("Attempt to get byte from " + type + " class");
		}
		
		return byteValue;
	}

	public void setByte(byte byteValue) {
		if (type != BYTE_TYPE) {
			throw new RuntimeException("Attempt to set byte from " + type + " class");
		}

		this.byteValue = byteValue;
	}

	public char getChar() {
		if (type != CHARACTER_TYPE) {
			throw new RuntimeException("Attempt to get char from " + type + " class");
		}
		
		return charValue;
	}

	public void setChar(char charValue) {
		if (type != CHARACTER_TYPE) {
			throw new RuntimeException("Attempt to set char from " + type + " class");
		}

		this.charValue = charValue;
	}

	public String getString() {
		if (type == null || type.isPrimitive()) {
			throw new RuntimeException("Attempt to get String from " + type + " class");
		}
		
		return stringValue;
	}

	public void setString(String stringValue) {
		if (type == null || type.isPrimitive()) {
			throw new RuntimeException("Attempt to set String from " + type + " class");
		}

		this.stringValue = stringValue;
	}


	private static HashMap	map;
	
	/**
	 * Set this <code>NetworkObject</code> packet description to describe what 
	 * this packet for when <code>NetworkConfig.DEBUG</code> is set to true.
	 */
	public NetworkPacket setDescription(String description) {
		if (NetworkConfig.DEBUG) {
			if (map == null) map = new HashMap();

			map.put(new Short(this.getID()), description);
		}
		
		return this;
	}
	
	
	public String toString() {
		if (!NetworkConfig.DEBUG) return super.toString();
		
		StringBuffer buff = new StringBuffer();
		
		buff.append("NetworkObject ");
		if (map != null) {
			String description = (String) map.get(new Short(this.getID()));
			
			if (description != null) buff.append(description).append(" ");
		}
		if (isSendSender()) buff.append("Sender ID ").append(getSender()).append(" ");
		buff.append("ID ").append(getID());
		if (isSendCode()) buff.append(" (code=").append(getCode()).append(")");
		buff.append(": ");
		
		if (type == null)					buff.append("send ID only");
		else if (type == INTEGER_TYPE)		buff.append("int = "		+ intValue);
		else if (type == DOUBLE_TYPE)		buff.append("double = "		+ doubleValue);
		else if (type == BOOLEAN_TYPE)		buff.append("boolean = "	+ booleanValue);
		else if (type == FLOAT_TYPE)		buff.append("float = "		+ floatValue);
		else if (type == LONG_TYPE)			buff.append("long = "		+ longValue);
		else if (type == SHORT_TYPE)		buff.append("short = "		+ shortValue);
		else if (type == BYTE_TYPE)			buff.append("byte = "		+ byteValue);
		else if (type == CHARACTER_TYPE)	buff.append("char = "		+ charValue);
		else buff.append("String = " + stringValue);
		
		return buff.toString();
	}


}
