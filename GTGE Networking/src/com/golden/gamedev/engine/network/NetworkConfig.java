/*
 * NetworkConfig.java
 *
 * Created on May 10, 2007, 12:02 PM
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

import com.golden.gamedev.engine.network.packet.NetworkObject;

/**
 * Static class to configure commonly used network configuration.
 *
 * @author Paulus Tuerah
 */
public final class NetworkConfig {

	public static boolean			DEBUG				= false;


	private static PacketManager	packetManager		= new PacketManager();

	private static boolean			autoConsumed		= true;

	private static boolean			defaultCompressed	= true;		// packet default compressed
	private static int				defaultPingTime		= 6000;		// default ping server-client 6 secs
	private static boolean			defaultSendCode		= false;	// default not send code (used on multi-threading)
	private static boolean			defaultSendSender	= false;	//
	private static int				defaultExpiredTime	= 10000;


	// private constructor
	private NetworkConfig() { }


	public static PacketManager getPacketManager() {
		return packetManager;
	}

	public static void setPacketManager(PacketManager manager) {
		packetManager = manager;
	}


	public static void registerPacket(short id, Class packetClass) {
		packetManager.registerPacket(id, packetClass);
	}

	public static void registerPacket(short id, NetworkPacket packet) {
		packetManager.registerPacket(id, packet);
	}

	public static void registerPacket(short id, Class type, String description) {
		packetManager.registerPacket(id, new NetworkObject(id, type).setDescription(description));
	}

	public static void registerPacket(short id, String description) {
		packetManager.registerPacket(id, new NetworkObject(id).setDescription(description));
	}


 /****************************************************************************/
 /****************************** BEANS METHODS *******************************/
 /****************************************************************************/

	public static boolean isDefaultCompressed() {
		return defaultCompressed;
	}

	public static void setDefaultCompressed(boolean compressed) {
		defaultCompressed = compressed;
	}


	public static int getDefaultPingTime() {
		return defaultPingTime;
	}

	public static void setDefaultPingTime(int aDefaultPingTime) {
		defaultPingTime = aDefaultPingTime;
	}


	public static boolean isDefaultSendCode() {
		return defaultSendCode;
	}

	public static void setDefaultSendCode(boolean aDefaultSendCode) {
		defaultSendCode = aDefaultSendCode;
	}


	public static boolean isDefaultSendSender() {
		return defaultSendSender;
	}

	public static void setDefaultSendSender(boolean aDefaultSendSender) {
		defaultSendSender = aDefaultSendSender;
	}


	public static boolean isAutoConsumed() {
		return autoConsumed;
	}

	public static void setAutoConsumed(boolean aAutoConsumed) {
		autoConsumed = aAutoConsumed;
	}


	public static int getDefaultExpiredTime() {
		return defaultExpiredTime;
	}

	public static void setDefaultExpiredTime(int aDefaultExpiredTime) {
		defaultExpiredTime = aDefaultExpiredTime;
	}

}
