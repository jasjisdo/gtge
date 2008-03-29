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
	
	public static boolean DEBUG = false;
	
	private static PacketManager packetManager = new PacketManager();
	
	private static boolean autoConsumed = true;
	
	private static boolean defaultCompressed = true; // packet default
														// compressed
	private static int defaultPingTime = 6000; // default ping server-client 6
												// secs
	private static boolean defaultSendCode = false; // default not send code
													// (used on multi-threading)
	private static boolean defaultSendSender = false; //
	private static int defaultExpiredTime = 10000;
	
	// private constructor
	private NetworkConfig() {
	}
	
	public static PacketManager getPacketManager() {
		return NetworkConfig.packetManager;
	}
	
	public static void setPacketManager(PacketManager manager) {
		NetworkConfig.packetManager = manager;
	}
	
	public static void registerPacket(short id, Class packetClass) {
		NetworkConfig.packetManager.registerPacket(id, packetClass);
	}
	
	public static void registerPacket(short id, NetworkPacket packet) {
		NetworkConfig.packetManager.registerPacket(id, packet);
	}
	
	public static void registerPacket(short id, Class type, String description) {
		NetworkConfig.packetManager.registerPacket(id, new NetworkObject(id,
		        type).setDescription(description));
	}
	
	public static void registerPacket(short id, String description) {
		NetworkConfig.packetManager.registerPacket(id, new NetworkObject(id)
		        .setDescription(description));
	}
	
	/** ************************************************************************* */
	/** **************************** BEANS METHODS ****************************** */
	/** ************************************************************************* */
	
	public static boolean isDefaultCompressed() {
		return NetworkConfig.defaultCompressed;
	}
	
	public static void setDefaultCompressed(boolean compressed) {
		NetworkConfig.defaultCompressed = compressed;
	}
	
	public static int getDefaultPingTime() {
		return NetworkConfig.defaultPingTime;
	}
	
	public static void setDefaultPingTime(int aDefaultPingTime) {
		NetworkConfig.defaultPingTime = aDefaultPingTime;
	}
	
	public static boolean isDefaultSendCode() {
		return NetworkConfig.defaultSendCode;
	}
	
	public static void setDefaultSendCode(boolean aDefaultSendCode) {
		NetworkConfig.defaultSendCode = aDefaultSendCode;
	}
	
	public static boolean isDefaultSendSender() {
		return NetworkConfig.defaultSendSender;
	}
	
	public static void setDefaultSendSender(boolean aDefaultSendSender) {
		NetworkConfig.defaultSendSender = aDefaultSendSender;
	}
	
	public static boolean isAutoConsumed() {
		return NetworkConfig.autoConsumed;
	}
	
	public static void setAutoConsumed(boolean aAutoConsumed) {
		NetworkConfig.autoConsumed = aAutoConsumed;
	}
	
	public static int getDefaultExpiredTime() {
		return NetworkConfig.defaultExpiredTime;
	}
	
	public static void setDefaultExpiredTime(int aDefaultExpiredTime) {
		NetworkConfig.defaultExpiredTime = aDefaultExpiredTime;
	}
	
}
