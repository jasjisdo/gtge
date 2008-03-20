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
	
	private final BaseClient[]	nullClient				= new BaseClient[0];
	private final BaseClient[]	connectingClients1		= new BaseClient[1];
	private final BaseClient[]	connectingClients2		= new BaseClient[2];
	private final BaseClient[]	disconnectedClients1	= new BaseClient[1];
	private final BaseClient[]	disconnectedClients2	= new BaseClient[2];
	private final BaseClient[]	clientReceivedPackets1	= new BaseClient[1];
	private final BaseClient[]	clientReceivedPackets2	= new BaseClient[2];
	
	private final String[]		nullString				= new String[0];

	
	private BaseClient[]	connectingClients			= nullClient;
	private BaseClient[]	clients						= nullClient;
	private BaseClient[]	disconnectedClients			= nullClient;
	
	private BaseClient[]	receivedPacketClients		= nullClient;
	private List			listReceivedPacketClients	= new ArrayList();

	private Map				clientGroups				= new HashMap();
		
	private short			uniqueClientID				= 0;
	
	
 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/
	
	/** Creates a new instance of BaseServer */
	public BaseServer() {
	}

	
	public void update(long elapsedTime) throws IOException {
		// clear all connecting and disconnected clients
		if (connectingClients.length > 0) {
			for (int i=0;i < connectingClients.length;i++) {
				if (connectingClients[i].isConnected()) {
					addClient(connectingClients[i]);				
				}
			}
			
			connectingClients = nullClient;
		}

		if (disconnectedClients.length > 0) {
			disconnectedClients = nullClient;
		}
		
		
		// clear all consumed received packet client
		if (receivedPacketClients.length > 0) {
			for (int i=0;i < receivedPacketClients.length;i++) {
				receivedPacketClients[i].clearConsumedPacket();
				
				if (receivedPacketClients[i].getReceivedPackets().length > 0) {
					listReceivedPacketClients.add(receivedPacketClients[i]);
				}
			}
			
			if (listReceivedPacketClients.size() == 0) {
				receivedPacketClients = nullClient;
				
			} else {
				if (listReceivedPacketClients.size() == 1) {
					receivedPacketClients = (BaseClient[]) listReceivedPacketClients.toArray(clientReceivedPackets1);
					
				} else if (listReceivedPacketClients.size() == 2) {
					receivedPacketClients = (BaseClient[]) listReceivedPacketClients.toArray(clientReceivedPackets2);
					
				} else {
					receivedPacketClients = (BaseClient[]) listReceivedPacketClients.toArray(nullClient);
				}
				
				listReceivedPacketClients.clear();
			}
		}
	}
	
	protected void broadcastPacket(byte[] packet) {
		for (int i=0;i < clients.length;i++) {
			try {
				clients[i].sendPacket(packet);
			} catch (IOException ex) {
				removeClient(clients[i]);
			}
		}
	}
	protected void broadcastPacket(byte[] packet, BaseClient except) {
		for (int i=0;i < clients.length;i++) {
			if (clients[i] == except) continue;

			try {
				clients[i].sendPacket(packet);
			} catch (IOException ex) {
				removeClient(clients[i]);
			}
		}
	}
	protected void broadcastPacket(byte[] packet, String group) {
		BaseClient[] clients = getClients(group);

		for (int i=0;i < clients.length;i++) {
			try {
				clients[i].sendPacket(packet);

			} catch (IOException ex) {
				ex.printStackTrace();
				removeClient(clients[i]);
			}
		}
	}
	protected void broadcastPacket(byte[] packet, String group, BaseClient except) {
		BaseClient[] clients = getClients(group);

		for (int i=0;i < clients.length;i++) {
			if (clients[i] == except) continue;

			try {
				clients[i].sendPacket(packet);
				
			} catch (IOException ex) {
				removeClient(clients[i]);
			}
		}			
	}
	protected void broadcastPacketExcept(byte[] packet, String group) {
		BaseClient[] clients = getClients(group);

		for (int i=0;i < clients.length;i++) {
			try {
				clients[i].sendPacket(packet);

			} catch (IOException ex) {
				removeClient(clients[i]);
			}
		}
	}
	protected void broadcastPacketExcept(byte[] packet, String group, BaseClient except) {
		BaseClient[] clients = getClients(group);

		for (int i=0;i < clients.length;i++) {
			if (clients[i] == except) continue;

			try {
				clients[i].sendPacket(packet);

			} catch (IOException ex) {
				removeClient(clients[i]);
			}
		}			
	}

	public byte[] pack(NetworkPacket packet) throws IOException {
		return NetworkConfig.getPacketManager().pack(packet);
	}
	
	public void broadcastPacket(NetworkPacket packet) throws IOException {
		broadcastPacket(NetworkConfig.getPacketManager().pack(packet));
	}
	public void broadcastPacket(NetworkPacket packet, BaseClient except) throws IOException {
		broadcastPacket(NetworkConfig.getPacketManager().pack(packet), except);
	}
	public void broadcastPacket(NetworkPacket packet, String group) throws IOException {
		broadcastPacket(NetworkConfig.getPacketManager().pack(packet), group);
	}
	public void broadcastPacket(NetworkPacket packet, String group, BaseClient except) throws IOException {
		broadcastPacket(NetworkConfig.getPacketManager().pack(packet), group, except);
	}
	public void broadcastPacketExcept(NetworkPacket packet, String exceptGroup) throws IOException {
		broadcastPacketExcept(NetworkConfig.getPacketManager().pack(packet), exceptGroup);
	}
	public void broadcastPacketExcept(NetworkPacket packet, String exceptGroup, BaseClient except) throws IOException {
		broadcastPacketExcept(NetworkConfig.getPacketManager().pack(packet), exceptGroup, except);
	}
	
	private void checkNullPacketManager() {
		if (NetworkConfig.getPacketManager() != null) {
			throw new RuntimeException(
				"The PacketManager is exists, can not send raw packet.\n" +
				"Call NetworkConfiguration.setPacketManager(null) before sending raw packet.");
		}
	}
	public void broadcastRawPacket(byte[] packet) {
		checkNullPacketManager();
		broadcastPacket(packet);
	}
	public void broadcastRawPacket(byte[] packet, BaseClient except) {
		checkNullPacketManager();
		broadcastPacket(packet, except);
	}
	public void broadcastRawPacket(byte[] packet, String group) {
		checkNullPacketManager();
		broadcastPacket(packet, group);
	}
	public void broadcastRawPacket(byte[] packet, String group, BaseClient except) {
		checkNullPacketManager();
		broadcastPacket(packet, group, except);
	}
	public void broadcastRawPacketExcept(byte[] packet, String exceptGroup) {
		checkNullPacketManager();
		broadcastPacketExcept(packet, exceptGroup);
	}
	public void broadcastRawPacketExcept(byte[] packet, String exceptGroup, BaseClient except) {
		checkNullPacketManager();
		broadcastPacketExcept(packet, exceptGroup, except);
	}
	
	protected abstract void disconnectImpl() throws IOException;
	
	public void disconnect() throws IOException {
		for (int i=0;i < clients.length;i++) {
			clients[i].disconnect();
		}

		disconnectImpl();
	}
	
	
	protected void addClient(BaseClient client) {
//		if (clients.contains(client)) {
//			throw new RuntimeException("Client " + client.getDetail() + " is already exists");
//		}

		clients = (BaseClient[]) NetworkUtil.expand(clients, 1);
		clients[clients.length-1] = client;
	}
	
	public boolean removeClient(BaseClient client) {
		boolean exists = false;

		// make sure this client connection must be closed before removal
		client.silentDisconnect();
			
		
		// remove from connecting list
		for (int i=0;i < connectingClients.length;i++) {
			if (connectingClients[i] == client) {
				connectingClients = (BaseClient[]) NetworkUtil.cut(connectingClients, i);
				exists = true;
				break;
			}
		}
		
		// remove from client list
		if (!exists) {
			for (int i=0;i < clients.length;i++) {
				if (clients[i] == client) {
					clients = (BaseClient[]) NetworkUtil.cut(clients, i);
					exists = true;
					break;
				}
			}
		}
				
		if (exists) {
			if (NetworkConfig.DEBUG) {
				System.out.println("========================");
				System.out.println("DISCONNECTED: " + client.getClientID() + " - " + client.getCompleteDetail());
				System.out.println("========================");
			}
			
			// remove from group list
//			client.setGroupName(null);
			String group = client.getGroupName();
			if (group != null) {
				BaseClient[] groups = (BaseClient[]) clientGroups.get(group);
				
				if (groups != null) {
					// remove from old group
					for (int i=0;i < groups.length;i++) {
						if (groups[i] == client) {
							clientGroups.put(group, NetworkUtil.cut(groups, i));
							break;
						}
					}
				}
			}
			
			// remove from received packet list
			for (int i=0;i < receivedPacketClients.length;i++) {
				if (receivedPacketClients[i] == client) {
					receivedPacketClients = (BaseClient[]) NetworkUtil.cut(receivedPacketClients, i);
					break;
				}
			}
			

			if (disconnectedClients == nullClient) {
				disconnectedClients		= disconnectedClients1;
				disconnectedClients[0]	= client;

			} else if (disconnectedClients == disconnectedClients1) {
				disconnectedClients		= disconnectedClients2;
				disconnectedClients[0]	= disconnectedClients1[0];
				disconnectedClients[1]	= client;

			} else {
				disconnectedClients = (BaseClient[]) NetworkUtil.expand(disconnectedClients, 1);
				disconnectedClients[disconnectedClients.length-1] = client;
			}
		}
		
		return exists;
	}

	public void removeDisconnectedClient(BaseClient client) {
		for (int i=0;i < disconnectedClients.length;i++) {
			if (disconnectedClients[i] == client) {
				disconnectedClients = (BaseClient[]) NetworkUtil.cut(disconnectedClients, i);
				break;
			}
		}
	}
	
	protected void changeGroup(BaseClient client, String fromGroup, String toGroup) {
		if (fromGroup != null) {
			BaseClient[] groups = (BaseClient[]) clientGroups.get(fromGroup);
			
			if (groups != null) {
				// remove from old group
				for (int i=0;i < groups.length;i++) {
					if (groups[i] == client) {
						clientGroups.put(fromGroup, NetworkUtil.cut(groups, i));
						break;
					}
				}
			}
		}
		
		if (toGroup != null) {
			// add to new group
			BaseClient[] groups = (BaseClient[]) clientGroups.get(toGroup);
			
			if (groups != null && groups.length > 0) {
				groups = (BaseClient[]) NetworkUtil.expand(groups, 1);
				groups[groups.length-1] = client;

			} else {
				groups = new BaseClient[1];
				groups[0] = client;
			}

			clientGroups.put(toGroup, groups);
		}
	}

	
	protected void addConnectingClient(BaseClient client) throws IOException {
		short clientID = getUniqueClientID();
		client.setClientID(clientID);
		client.sendPacket(String.valueOf(clientID).getBytes());
		
		if (connectingClients == nullClient) {
			connectingClients		= connectingClients1;
			connectingClients[0]	= client;
			
		} else if (connectingClients == connectingClients1) {
			connectingClients		= connectingClients2;
			connectingClients[0]	= connectingClients1[0];
			connectingClients[1]	= client;
			
		} else {
			connectingClients = (BaseClient[]) NetworkUtil.expand(connectingClients, 1);
			connectingClients[connectingClients.length-1] = client;
		}
	}

	protected void addDisconnectedClient(BaseClient client) {
		removeClient(client);
	}
	
	
	protected void addReceivedPacketClient(BaseClient client) {
		for (int i=0;i < receivedPacketClients.length;i++) {
			if (receivedPacketClients[i] == client) {
				// already exists
				return;
			}
		}
		
		if (receivedPacketClients == nullClient) {
			receivedPacketClients		= clientReceivedPackets1;
			receivedPacketClients[0]	= client;
			
		} else if (receivedPacketClients == clientReceivedPackets1) {
			receivedPacketClients		= clientReceivedPackets2;
			receivedPacketClients[0]	= clientReceivedPackets1[0];
			receivedPacketClients[1]	= client;
			
		} else {
			receivedPacketClients = (BaseClient[]) NetworkUtil.expand(receivedPacketClients, 1);
			receivedPacketClients[receivedPacketClients.length-1] = client;
		}
	}
	
	public void clearReceivedPacket() {
		for (int i=0;i < receivedPacketClients.length;i++) {
			receivedPacketClients[i].clearReceivedPacket();
		}
		
		receivedPacketClients = nullClient;
	}
	

	public abstract String getDetail();

	
 /****************************************************************************/
 /****************************** BEANS METHODS *******************************/
 /****************************************************************************/

	protected synchronized short getUniqueClientID() {
		short id = 0;
		boolean duplicate = false;
		
		do {
			uniqueClientID++;
			if (uniqueClientID >= Short.MAX_VALUE) {
				uniqueClientID = (!duplicate) ? 0 : Short.MIN_VALUE; 
			}

			id = uniqueClientID;

			
			// check for duplicate
			duplicate = false;
			
			for (int i=0;i < clients.length;i++) {
				if (clients[i].getClientID() == id) {
					duplicate = true;
					break;
				}
			}
			
		} while (duplicate);
		
		return id;
	}


	public BaseClient[] getConnectingClients() {
		return connectingClients;
	}

	public BaseClient[] getClients() {
		return clients;
	}

	public BaseClient[] getClients(String groupName) {
		BaseClient[] clients = (BaseClient[]) clientGroups.get(groupName);
		
		return (clients != null) ? clients : nullClient;
	}

	public BaseClient[] getDisconnectedClients() {
		return disconnectedClients;
	}
	
	public String[] getClientGroups() {
		return (String[]) clientGroups.keySet().toArray(nullString);
	}
	
	public String[] getClientGroups(String[] except) {
		ArrayList list = new ArrayList(clientGroups.keySet());
		for (int i=0;i < except.length;i++) {
			list.remove(except[i]);
		}
		
		return (String[]) list.toArray(new String[0]);
	}

	public void clearClientGroups() {
		clientGroups.clear();
		
		for (int i=0;i < clients.length;i++) {
			clients[i].clearGroupName();
		}
	}
	
	
	public BaseClient[] getReceivedPacketClients() {
		return receivedPacketClients;
	}
	
}
