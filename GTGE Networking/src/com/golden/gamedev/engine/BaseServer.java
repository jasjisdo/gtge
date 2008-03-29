/*
 * BaseServer.java
 *
 * Created on May 6, 2007, 10:32 AM
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.golden.gamedev.engine.network.NetworkConfig;
import com.golden.gamedev.engine.network.NetworkPacket;
import com.golden.gamedev.engine.network.NetworkUtil;

/**
 * 
 * @author Paulus Tuerah
 */
public abstract class BaseServer {
	
	private final BaseClient[] nullClient = new BaseClient[0];
	private final BaseClient[] connectingClients1 = new BaseClient[1];
	private final BaseClient[] connectingClients2 = new BaseClient[2];
	private final BaseClient[] disconnectedClients1 = new BaseClient[1];
	private final BaseClient[] disconnectedClients2 = new BaseClient[2];
	private final BaseClient[] clientReceivedPackets1 = new BaseClient[1];
	private final BaseClient[] clientReceivedPackets2 = new BaseClient[2];
	
	private final String[] nullString = new String[0];
	
	private BaseClient[] connectingClients = this.nullClient;
	private BaseClient[] clients = this.nullClient;
	private BaseClient[] disconnectedClients = this.nullClient;
	
	private BaseClient[] receivedPacketClients = this.nullClient;
	private List listReceivedPacketClients = new ArrayList();
	
	private Map clientGroups = new HashMap();
	
	private short uniqueClientID = 0;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/** Creates a new instance of BaseServer */
	public BaseServer() {
	}
	
	public void update(long elapsedTime) throws IOException {
		// clear all connecting and disconnected clients
		if (this.connectingClients.length > 0) {
			for (int i = 0; i < this.connectingClients.length; i++) {
				if (this.connectingClients[i].isConnected()) {
					this.addClient(this.connectingClients[i]);
				}
			}
			
			this.connectingClients = this.nullClient;
		}
		
		if (this.disconnectedClients.length > 0) {
			this.disconnectedClients = this.nullClient;
		}
		
		// clear all consumed received packet client
		if (this.receivedPacketClients.length > 0) {
			for (int i = 0; i < this.receivedPacketClients.length; i++) {
				this.receivedPacketClients[i].clearConsumedPacket();
				
				if (this.receivedPacketClients[i].getReceivedPackets().length > 0) {
					this.listReceivedPacketClients
					        .add(this.receivedPacketClients[i]);
				}
			}
			
			if (this.listReceivedPacketClients.size() == 0) {
				this.receivedPacketClients = this.nullClient;
				
			}
			else {
				if (this.listReceivedPacketClients.size() == 1) {
					this.receivedPacketClients = (BaseClient[]) this.listReceivedPacketClients
					        .toArray(this.clientReceivedPackets1);
					
				}
				else if (this.listReceivedPacketClients.size() == 2) {
					this.receivedPacketClients = (BaseClient[]) this.listReceivedPacketClients
					        .toArray(this.clientReceivedPackets2);
					
				}
				else {
					this.receivedPacketClients = (BaseClient[]) this.listReceivedPacketClients
					        .toArray(this.nullClient);
				}
				
				this.listReceivedPacketClients.clear();
			}
		}
	}
	
	protected void broadcastPacket(byte[] packet) {
		for (int i = 0; i < this.clients.length; i++) {
			try {
				this.clients[i].sendPacket(packet);
			}
			catch (IOException ex) {
				this.removeClient(this.clients[i]);
			}
		}
	}
	
	protected void broadcastPacket(byte[] packet, BaseClient except) {
		for (int i = 0; i < this.clients.length; i++) {
			if (this.clients[i] == except) {
				continue;
			}
			
			try {
				this.clients[i].sendPacket(packet);
			}
			catch (IOException ex) {
				this.removeClient(this.clients[i]);
			}
		}
	}
	
	protected void broadcastPacket(byte[] packet, String group) {
		BaseClient[] clients = this.getClients(group);
		
		for (int i = 0; i < clients.length; i++) {
			try {
				clients[i].sendPacket(packet);
				
			}
			catch (IOException ex) {
				ex.printStackTrace();
				this.removeClient(clients[i]);
			}
		}
	}
	
	protected void broadcastPacket(byte[] packet, String group, BaseClient except) {
		BaseClient[] clients = this.getClients(group);
		
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] == except) {
				continue;
			}
			
			try {
				clients[i].sendPacket(packet);
				
			}
			catch (IOException ex) {
				this.removeClient(clients[i]);
			}
		}
	}
	
	protected void broadcastPacketExcept(byte[] packet, String group) {
		BaseClient[] clients = this.getClients(group);
		
		for (int i = 0; i < clients.length; i++) {
			try {
				clients[i].sendPacket(packet);
				
			}
			catch (IOException ex) {
				this.removeClient(clients[i]);
			}
		}
	}
	
	protected void broadcastPacketExcept(byte[] packet, String group, BaseClient except) {
		BaseClient[] clients = this.getClients(group);
		
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] == except) {
				continue;
			}
			
			try {
				clients[i].sendPacket(packet);
				
			}
			catch (IOException ex) {
				this.removeClient(clients[i]);
			}
		}
	}
	
	public byte[] pack(NetworkPacket packet) throws IOException {
		return NetworkConfig.getPacketManager().pack(packet);
	}
	
	public void broadcastPacket(NetworkPacket packet) throws IOException {
		this.broadcastPacket(NetworkConfig.getPacketManager().pack(packet));
	}
	
	public void broadcastPacket(NetworkPacket packet, BaseClient except)
	        throws IOException {
		this.broadcastPacket(NetworkConfig.getPacketManager().pack(packet),
		        except);
	}
	
	public void broadcastPacket(NetworkPacket packet, String group)
	        throws IOException {
		this.broadcastPacket(NetworkConfig.getPacketManager().pack(packet),
		        group);
	}
	
	public void broadcastPacket(NetworkPacket packet, String group, BaseClient except)
	        throws IOException {
		this.broadcastPacket(NetworkConfig.getPacketManager().pack(packet),
		        group, except);
	}
	
	public void broadcastPacketExcept(NetworkPacket packet, String exceptGroup)
	        throws IOException {
		this.broadcastPacketExcept(NetworkConfig.getPacketManager()
		        .pack(packet), exceptGroup);
	}
	
	public void broadcastPacketExcept(NetworkPacket packet, String exceptGroup, BaseClient except)
	        throws IOException {
		this.broadcastPacketExcept(NetworkConfig.getPacketManager()
		        .pack(packet), exceptGroup, except);
	}
	
	private void checkNullPacketManager() {
		if (NetworkConfig.getPacketManager() != null) {
			throw new RuntimeException(
			        "The PacketManager is exists, can not send raw packet.\n"
			                + "Call NetworkConfiguration.setPacketManager(null) before sending raw packet.");
		}
	}
	
	public void broadcastRawPacket(byte[] packet) {
		this.checkNullPacketManager();
		this.broadcastPacket(packet);
	}
	
	public void broadcastRawPacket(byte[] packet, BaseClient except) {
		this.checkNullPacketManager();
		this.broadcastPacket(packet, except);
	}
	
	public void broadcastRawPacket(byte[] packet, String group) {
		this.checkNullPacketManager();
		this.broadcastPacket(packet, group);
	}
	
	public void broadcastRawPacket(byte[] packet, String group, BaseClient except) {
		this.checkNullPacketManager();
		this.broadcastPacket(packet, group, except);
	}
	
	public void broadcastRawPacketExcept(byte[] packet, String exceptGroup) {
		this.checkNullPacketManager();
		this.broadcastPacketExcept(packet, exceptGroup);
	}
	
	public void broadcastRawPacketExcept(byte[] packet, String exceptGroup, BaseClient except) {
		this.checkNullPacketManager();
		this.broadcastPacketExcept(packet, exceptGroup, except);
	}
	
	protected abstract void disconnectImpl() throws IOException;
	
	public void disconnect() throws IOException {
		for (int i = 0; i < this.clients.length; i++) {
			this.clients[i].disconnect();
		}
		
		this.disconnectImpl();
	}
	
	protected void addClient(BaseClient client) {
		// if (clients.contains(client)) {
		// throw new RuntimeException("Client " + client.getDetail() + " is
		// already exists");
		// }
		
		this.clients = (BaseClient[]) NetworkUtil.expand(this.clients, 1);
		this.clients[this.clients.length - 1] = client;
	}
	
	public boolean removeClient(BaseClient client) {
		boolean exists = false;
		
		// make sure this client connection must be closed before removal
		client.silentDisconnect();
		
		// remove from connecting list
		for (int i = 0; i < this.connectingClients.length; i++) {
			if (this.connectingClients[i] == client) {
				this.connectingClients = (BaseClient[]) NetworkUtil.cut(
				        this.connectingClients, i);
				exists = true;
				break;
			}
		}
		
		// remove from client list
		if (!exists) {
			for (int i = 0; i < this.clients.length; i++) {
				if (this.clients[i] == client) {
					this.clients = (BaseClient[]) NetworkUtil.cut(this.clients,
					        i);
					exists = true;
					break;
				}
			}
		}
		
		if (exists) {
			if (NetworkConfig.DEBUG) {
				System.out.println("========================");
				System.out.println("DISCONNECTED: " + client.getClientID()
				        + " - " + client.getCompleteDetail());
				System.out.println("========================");
			}
			
			// remove from group list
			// client.setGroupName(null);
			String group = client.getGroupName();
			if (group != null) {
				BaseClient[] groups = (BaseClient[]) this.clientGroups
				        .get(group);
				
				if (groups != null) {
					// remove from old group
					for (int i = 0; i < groups.length; i++) {
						if (groups[i] == client) {
							this.clientGroups.put(group, NetworkUtil.cut(
							        groups, i));
							break;
						}
					}
				}
			}
			
			// remove from received packet list
			for (int i = 0; i < this.receivedPacketClients.length; i++) {
				if (this.receivedPacketClients[i] == client) {
					this.receivedPacketClients = (BaseClient[]) NetworkUtil
					        .cut(this.receivedPacketClients, i);
					break;
				}
			}
			
			if (this.disconnectedClients == this.nullClient) {
				this.disconnectedClients = this.disconnectedClients1;
				this.disconnectedClients[0] = client;
				
			}
			else if (this.disconnectedClients == this.disconnectedClients1) {
				this.disconnectedClients = this.disconnectedClients2;
				this.disconnectedClients[0] = this.disconnectedClients1[0];
				this.disconnectedClients[1] = client;
				
			}
			else {
				this.disconnectedClients = (BaseClient[]) NetworkUtil.expand(
				        this.disconnectedClients, 1);
				this.disconnectedClients[this.disconnectedClients.length - 1] = client;
			}
		}
		
		return exists;
	}
	
	public void removeDisconnectedClient(BaseClient client) {
		for (int i = 0; i < this.disconnectedClients.length; i++) {
			if (this.disconnectedClients[i] == client) {
				this.disconnectedClients = (BaseClient[]) NetworkUtil.cut(
				        this.disconnectedClients, i);
				break;
			}
		}
	}
	
	protected void changeGroup(BaseClient client, String fromGroup, String toGroup) {
		if (fromGroup != null) {
			BaseClient[] groups = (BaseClient[]) this.clientGroups
			        .get(fromGroup);
			
			if (groups != null) {
				// remove from old group
				for (int i = 0; i < groups.length; i++) {
					if (groups[i] == client) {
						this.clientGroups.put(fromGroup, NetworkUtil.cut(
						        groups, i));
						break;
					}
				}
			}
		}
		
		if (toGroup != null) {
			// add to new group
			BaseClient[] groups = (BaseClient[]) this.clientGroups.get(toGroup);
			
			if (groups != null && groups.length > 0) {
				groups = (BaseClient[]) NetworkUtil.expand(groups, 1);
				groups[groups.length - 1] = client;
				
			}
			else {
				groups = new BaseClient[1];
				groups[0] = client;
			}
			
			this.clientGroups.put(toGroup, groups);
		}
	}
	
	protected void addConnectingClient(BaseClient client) throws IOException {
		short clientID = this.getUniqueClientID();
		client.setClientID(clientID);
		client.sendPacket(String.valueOf(clientID).getBytes());
		
		if (this.connectingClients == this.nullClient) {
			this.connectingClients = this.connectingClients1;
			this.connectingClients[0] = client;
			
		}
		else if (this.connectingClients == this.connectingClients1) {
			this.connectingClients = this.connectingClients2;
			this.connectingClients[0] = this.connectingClients1[0];
			this.connectingClients[1] = client;
			
		}
		else {
			this.connectingClients = (BaseClient[]) NetworkUtil.expand(
			        this.connectingClients, 1);
			this.connectingClients[this.connectingClients.length - 1] = client;
		}
	}
	
	protected void addDisconnectedClient(BaseClient client) {
		this.removeClient(client);
	}
	
	protected void addReceivedPacketClient(BaseClient client) {
		for (int i = 0; i < this.receivedPacketClients.length; i++) {
			if (this.receivedPacketClients[i] == client) {
				// already exists
				return;
			}
		}
		
		if (this.receivedPacketClients == this.nullClient) {
			this.receivedPacketClients = this.clientReceivedPackets1;
			this.receivedPacketClients[0] = client;
			
		}
		else if (this.receivedPacketClients == this.clientReceivedPackets1) {
			this.receivedPacketClients = this.clientReceivedPackets2;
			this.receivedPacketClients[0] = this.clientReceivedPackets1[0];
			this.receivedPacketClients[1] = client;
			
		}
		else {
			this.receivedPacketClients = (BaseClient[]) NetworkUtil.expand(
			        this.receivedPacketClients, 1);
			this.receivedPacketClients[this.receivedPacketClients.length - 1] = client;
		}
	}
	
	public void clearReceivedPacket() {
		for (int i = 0; i < this.receivedPacketClients.length; i++) {
			this.receivedPacketClients[i].clearReceivedPacket();
		}
		
		this.receivedPacketClients = this.nullClient;
	}
	
	public abstract String getDetail();
	
	/** ************************************************************************* */
	/** **************************** BEANS METHODS ****************************** */
	/** ************************************************************************* */
	
	protected synchronized short getUniqueClientID() {
		short id = 0;
		boolean duplicate = false;
		
		do {
			this.uniqueClientID++;
			if (this.uniqueClientID >= Short.MAX_VALUE) {
				this.uniqueClientID = (!duplicate) ? 0 : Short.MIN_VALUE;
			}
			
			id = this.uniqueClientID;
			
			// check for duplicate
			duplicate = false;
			
			for (int i = 0; i < this.clients.length; i++) {
				if (this.clients[i].getClientID() == id) {
					duplicate = true;
					break;
				}
			}
			
		} while (duplicate);
		
		return id;
	}
	
	public BaseClient[] getConnectingClients() {
		return this.connectingClients;
	}
	
	public BaseClient[] getClients() {
		return this.clients;
	}
	
	public BaseClient[] getClients(String groupName) {
		BaseClient[] clients = (BaseClient[]) this.clientGroups.get(groupName);
		
		return (clients != null) ? clients : this.nullClient;
	}
	
	public BaseClient[] getDisconnectedClients() {
		return this.disconnectedClients;
	}
	
	public String[] getClientGroups() {
		return (String[]) this.clientGroups.keySet().toArray(this.nullString);
	}
	
	public String[] getClientGroups(String[] except) {
		ArrayList list = new ArrayList(this.clientGroups.keySet());
		for (int i = 0; i < except.length; i++) {
			list.remove(except[i]);
		}
		
		return (String[]) list.toArray(new String[0]);
	}
	
	public void clearClientGroups() {
		this.clientGroups.clear();
		
		for (int i = 0; i < this.clients.length; i++) {
			this.clients[i].clearGroupName();
		}
	}
	
	public BaseClient[] getReceivedPacketClients() {
		return this.receivedPacketClients;
	}
	
}
