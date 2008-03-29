/*
 * BaseClient.java
 *
 * Created on May 6, 2007, 10:38 AM
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

package com.golden.gamedev.engine;

// JFC
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.golden.gamedev.engine.network.NetworkConfig;
import com.golden.gamedev.engine.network.NetworkException;
import com.golden.gamedev.engine.network.NetworkPacket;
import com.golden.gamedev.engine.network.NetworkUtil;
import com.golden.gamedev.engine.network.PacketManager;
import com.golden.gamedev.engine.network.packet.NetworkPing;
import com.golden.gamedev.engine.network.packet.NetworkRawPacket;

/**
 * 
 * @author Paulus Tuerah
 */
public abstract class BaseClient {
	
	private final NetworkPacket[] nullPacket = new NetworkPacket[0];
	private final NetworkPacket[] receivedPackets1 = new NetworkPacket[1];
	private final NetworkPacket[] receivedPackets2 = new NetworkPacket[2];
	
	/**
	 * Default time out when waiting for packet is 10 secs.
	 */
	public static int defaultWaitTimeOut = 10000;
	
	/**
	 * Default update time when waiting for packet is 0.1 sec.
	 */
	public static int defaultUpdateTime = 100;
	
	protected BaseServer server;
	
	private boolean hasClientID = false;
	private short clientID = -1;
	private String groupName = null;
	private Object info;
	
	private NetworkPacket[] receivedPackets = this.nullPacket;
	
	protected long lastPing = System.currentTimeMillis(); // last time we ping
															// the server/client
	protected long lastReceivedPing; // last time we got ping
	
	private int pingTime = NetworkConfig.getDefaultPingTime();
	private List listPackets = new ArrayList();
	
	private boolean connected = true;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/** Creates a new instance of BaseClient */
	public BaseClient() {
	}
	
	/**
	 * The local client created on the server to manage the remote client.
	 */
	protected BaseClient(BaseServer server) {
		this.server = server;
	}
	
	public void update(long elapsedTime) throws IOException {
		this.clearConsumedPacket();
		
		if (this.pingTime != -1) {
			if (System.currentTimeMillis() - this.lastPing > this.pingTime) {
				this.lastPing = System.currentTimeMillis();
				this.ping();
			}
		}
	}
	
	public void sendPacket(NetworkPacket packet) throws IOException {
		if (this.server == null && packet.isSendSender()) {
			packet.setSender(this);
		}
		
		if (NetworkConfig.DEBUG && packet != NetworkPing.getInstance()) {
			System.out.println("SEND packet to "
			        + ((this.server == null) ? "Server" : this
			                .getCompleteDetail()));
			System.out.println(packet);
			System.out.println();
		}
		
		PacketManager packetManager = NetworkConfig.getPacketManager();
		
		byte[] data = packetManager.pack(packet);
		
		this.sendPacket(data);
		
		// check for packet compression, uncomment these lines
		// if (packet != NetworkPing.getInstance()) {
		// String name = packet.getClass().getName();
		// if (name.lastIndexOf(".") != -1) name =
		// name.substring(name.lastIndexOf(".") + 1);
		//			
		// String packetID = (packet.getID() != NetworkPacket.NULL_ID) ?
		// String.valueOf(packet.getID()) + " " : "";
		//			
		// System.out.println(name + " " + packetID + "Compressed: " +
		// packet.isCompressed() + " = " + data.length + " (sent)");
		// packet.setCompressed(!packet.isCompressed());
		// System.out.println(name + " " + packetID + "Compressed: " +
		// packet.isCompressed() + " = " + packetManager.pack(packet).length);
		// System.out.println();
		// }
	}
	
	public void sendRawPacket(byte[] data) throws IOException {
		if (NetworkConfig.getPacketManager() != null) {
			throw new RuntimeException(
			        "The PacketManager is exists.\n"
			                + "Configure NetworkConfiguration.setPacketManager(null) to send a raw packet.");
		}
		
		this.sendPacket(data);
	}
	
	public void ping() throws IOException {
		if (NetworkConfig.getPacketManager() == null) {
			this.sendPacket(NetworkPing.ping);
			
		}
		else {
			this.sendPacket(NetworkPing.getInstance());
		}
	}
	
	protected abstract void sendPacket(byte[] data) throws IOException;
	
	protected abstract void disconnectImpl() throws IOException;
	
	public void disconnect() throws IOException {
		// remove from the server first
		if (this.connected) {
			this.connected = false;
			
			if (this.server != null) {
				this.server.removeClient(this);
			}
		}
		
		this.disconnectImpl();
	}
	
	public void silentDisconnect() {
		try {
			this.disconnect();
		}
		catch (IOException ex) {
		}
	}
	
	public abstract boolean isConnected();
	
	public NetworkPacket waitForPacket(boolean update, long updateTime, int waitTimeOut)
	        throws NetworkException, IOException {
		long startTime = System.currentTimeMillis();
		
		while (true) {
			if (update) {
				this.update(updateTime);
			}
			
			if (this.receivedPackets.length > 0) {
				// packet arrived!
				return this.receivedPackets[0];
			}
			
			if (waitTimeOut != -1) {
				if (System.currentTimeMillis() - startTime > waitTimeOut) {
					// time out reached
					throw new NetworkException("Packet time out " + waitTimeOut
					        + "ms");
				}
			}
			
			try {
				Thread.sleep(updateTime);
			}
			catch (InterruptedException ex) {
			}
		}
	}
	
	public NetworkPacket waitForPacket(boolean update) throws NetworkException,
	        IOException {
		// retrieve packet every 0.1 sec
		// time out 20 secs
		return this.waitForPacket(update, BaseClient.defaultUpdateTime,
		        BaseClient.defaultWaitTimeOut);
	}
	
	public NetworkPacket waitForPacket(boolean update, short id, long updateTime, int waitTimeOut)
	        throws NetworkException, IOException {
		long startTime = System.currentTimeMillis();
		
		while (true) {
			if (update) {
				this.update(updateTime);
			}
			
			NetworkPacket packet = this.getReceivedPacket(id);
			if (packet != null) {
				// packet arrived!
				return packet;
			}
			
			if (waitTimeOut != -1) {
				if (System.currentTimeMillis() - startTime > waitTimeOut) {
					// time out reached
					throw new NetworkException("Packet time out " + waitTimeOut
					        + "ms");
				}
			}
			
			try {
				Thread.sleep(updateTime);
			}
			catch (InterruptedException ex) {
			}
		}
	}
	
	public NetworkPacket waitForPacket(boolean update, short id)
	        throws NetworkException, IOException {
		// retrieve packet every 0.1 sec
		// time out 20 secs
		return this.waitForPacket(update, id, BaseClient.defaultUpdateTime,
		        BaseClient.defaultWaitTimeOut);
	}
	
	public NetworkPacket waitForPacketCode(boolean update, short code, long updateTime, int waitTimeOut)
	        throws NetworkException, IOException {
		long startTime = System.currentTimeMillis();
		
		while (true) {
			if (update) {
				this.update(updateTime);
			}
			
			NetworkPacket packet = this.getReceivedPacketCode(code);
			if (packet != null) {
				// packet arrived!
				packet.consume(); // since this unique packet, the packet must
									// be the right packet
				// we simply consume it, to make everything much more easy
				
				return packet;
			}
			
			if (waitTimeOut != -1) {
				if (System.currentTimeMillis() - startTime > waitTimeOut) {
					// time out reached
					throw new NetworkException("Packet time out " + waitTimeOut
					        + "ms");
				}
			}
			
			try {
				Thread.sleep(updateTime);
			}
			catch (InterruptedException ex) {
			}
		}
	}
	
	public NetworkPacket waitForPacketCode(boolean update, short code)
	        throws NetworkException, IOException {
		// retrieve packet every 0.1 sec
		// time out 20 secs
		return this.waitForPacketCode(update, code,
		        BaseClient.defaultUpdateTime, BaseClient.defaultWaitTimeOut);
	}
	
	public boolean isReceivedPacket() {
		return (this.receivedPackets.length > 0);
	}
	
	public NetworkPacket getReceivedPacket() {
		return (this.receivedPackets.length > 0) ? this.receivedPackets[0]
		        : null;
	}
	
	public NetworkPacket getReceivedPacket(short id) {
		for (int i = 0; i < this.receivedPackets.length; i++) {
			if (this.receivedPackets[i].getID() == id) {
				return this.receivedPackets[i];
			}
		}
		
		return null;
	}
	
	public NetworkPacket getReceivedPacketCode(short code) {
		for (int i = 0; i < this.receivedPackets.length; i++) {
			if (this.receivedPackets[i].isSendCode()
			        && this.receivedPackets[i].getCode() == code) {
				return this.receivedPackets[i];
			}
		}
		
		return null;
	}
	
	public NetworkPacket[] getReceivedPackets() {
		return this.receivedPackets;
	}
	
	protected void addReceivedPacket(byte[] data) throws IOException {
		if (!this.hasClientID) {
			this.setClientID(Short.parseShort(new String(data)));
			return;
		}
		
		PacketManager manager = NetworkConfig.getPacketManager();
		
		NetworkPacket packet = null;
		if (manager != null) {
			// we unpack the data using packet manager
			packet = manager.unpack(data);
			
		}
		else {
			// we wrap it inside raw packet
			if (data.length == 0) {
				packet = NetworkPing.getInstance();
				
			}
			else {
				packet = new NetworkRawPacket(data);
			}
			
		}
		
		if (NetworkConfig.DEBUG && packet != NetworkPing.getInstance()) {
			System.out.println("RECEIVED packet from "
			        + ((this.server == null) ? "Server" : this
			                .getCompleteDetail()));
			System.out.println(packet);
			System.out.println();
		}
		
		this.lastReceivedPing = System.currentTimeMillis();
		
		if (packet == NetworkPing.getInstance()) {
			// only a ping
			if (this.server != null) {
				// if this is server, we need to ping back
				this.ping();
			}
			
			return;
		}
		
		if (this.receivedPackets == this.nullPacket) {
			this.receivedPackets = this.receivedPackets1;
			this.receivedPackets[0] = packet;
			
		}
		else if (this.receivedPackets == this.receivedPackets1) {
			this.receivedPackets = this.receivedPackets2;
			this.receivedPackets[0] = this.receivedPackets1[0];
			this.receivedPackets[1] = packet;
			
		}
		else {
			this.receivedPackets = (NetworkPacket[]) NetworkUtil.expand(
			        this.receivedPackets, 1);
			this.receivedPackets[this.receivedPackets.length - 1] = packet;
		}
		
		if (this.server != null) {
			// client on server
			this.server.addReceivedPacketClient(this);
		}
	}
	
	protected void clearConsumedPacket() {
		if (this.receivedPackets.length > 0) {
			for (int i = 0; i < this.receivedPackets.length; i++) {
				if (!this.receivedPackets[i].isConsumed()
				        && !this.receivedPackets[i].isExpired()) {
					// packet not consumed and
					// still not expired will be still available for use later
					this.listPackets.add(this.receivedPackets[i]);
				}
			}
			
			if (this.listPackets.size() == 0) {
				this.receivedPackets = this.nullPacket;
				
			}
			else {
				if (this.listPackets.size() == 1) {
					this.receivedPackets = (NetworkPacket[]) this.listPackets
					        .toArray(this.receivedPackets1);
					
				}
				else if (this.listPackets.size() == 2) {
					this.receivedPackets = (NetworkPacket[]) this.listPackets
					        .toArray(this.receivedPackets2);
					
				}
				else {
					this.receivedPackets = (NetworkPacket[]) this.listPackets
					        .toArray(this.nullPacket);
				}
				
				if (NetworkConfig.DEBUG) {
					System.out.println("Not consumed packet = "
					        + this.receivedPackets.length);
					if (this.server != null) {
						System.out.println("On client "
						        + this.getCompleteDetail());
					}
					for (int i = 0; i < this.receivedPackets.length; i++) {
						System.out.println("Not consumed #" + (i + 1) + " "
						        + this.receivedPackets[i]);
					}
					System.out.println();
				}
				
				this.listPackets.clear();
			}
		}
	}
	
	public void clearReceivedPacket() {
		this.receivedPackets = this.nullPacket;
	}
	
	/** ************************************************************************* */
	/** **************************** BEANS METHODS ****************************** */
	/** ************************************************************************* */
	
	public int getPingTime() {
		return this.pingTime;
	}
	
	public void setPingTime(int pingTime) {
		this.pingTime = pingTime;
	}
	
	public short getClientID() {
		return this.clientID;
	}
	
	public void setClientID(short clientID) {
		this.clientID = clientID;
		this.hasClientID = true;
		
		if (NetworkConfig.DEBUG) {
			System.out.println("========================");
			System.out.println("CONNECTED CLIENT ID: " + clientID);
			System.out.println("========================");
		}
	}
	
	public Object getInfo() {
		return this.info;
	}
	
	public void setInfo(Object info) {
		this.info = info;
	}
	
	public boolean isGroupName(String groupName) {
		if (this.groupName == groupName) {
			return true;
		}
		if (this.groupName == null || groupName == null) {
			return false;
		}
		
		return this.groupName.equals(groupName);
	}
	
	public String getGroupName() {
		return this.groupName;
	}
	
	public void setGroupName(String groupName) {
		if (this.server != null) {
			this.server.changeGroup(this, this.groupName, groupName);
		}
		
		this.groupName = groupName;
	}
	
	void clearGroupName() {
		// set no group
		this.groupName = null;
	}
	
	public abstract String getDetail();
	
	public abstract String getRemoteDetail();
	
	public String getCompleteDetail() {
		StringBuffer buff = new StringBuffer();
		
		// id
		buff.append("Client ID ");
		if (this.hasClientID) {
			buff.append(this.clientID);
		}
		else {
			buff.append("[no-id]");
		}
		buff.append(" ");
		
		buff.append("[");
		
		// group
		if (this.server != null) {
			String groupName = this.getGroupName();
			if (groupName == null) {
				groupName = "[no-group]";
			}
			
			buff.append("group=").append(groupName).append("; ");
		}
		
		// ip detail
		buff.append(this.getRemoteDetail());
		
		// info
		if (this.getInfo() != null) {
			buff.append("; info=").append(this.getInfo());
		}
		
		buff.append("]");
		
		return buff.toString();
	}
	
	public long getLastReceivedPing() {
		return this.lastReceivedPing;
	}
	
}
