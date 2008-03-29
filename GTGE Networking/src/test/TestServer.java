/*
 * TestServer.java
 *
 * Created on May 6, 2007, 9:13 PM
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

package test;

// JFC
import java.io.IOException;

import com.golden.gamedev.engine.BaseClient;
import com.golden.gamedev.engine.BaseServer;
import com.golden.gamedev.engine.network.NetworkPacket;
import com.golden.gamedev.engine.network.packet.NetworkMessage;
import com.golden.gamedev.engine.network.tcp.TCPServer;

/**
 * 
 * @author Paulus Tuerah
 */
public class TestServer {
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	private static void handleNetwork(BaseServer server) {
		// handle connecting clients
		BaseClient[] clients = server.getConnectingClients(); // all
																// connecting
																// clients
		if (clients.length > 0) {
			for (int i = 0; i < clients.length; i++) {
				System.out.println("New Client Connected: "
				        + clients[i].getRemoteDetail());
				
				try {
					clients[i].sendPacket(new NetworkMessage("Hello World"));
					
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		
		// handle disconnected clients
		clients = server.getDisconnectedClients(); // all disconnected clients
		if (clients.length > 0) {
			System.out.println("DISCONNECTED CLIENT = " + clients.length);
		}
		
		for (int i = 0; i < clients.length; i++) {
			System.out.println("Client Disconnected: "
			        + clients[i].getRemoteDetail());
		}
		
		// handle new packets
		clients = server.getReceivedPacketClients(); // all clients which
														// received packets
		for (int i = 0; i < clients.length; i++) {
			NetworkPacket[] packets = clients[i].getReceivedPackets(); // get
																		// the
																		// packets
			
			for (int j = 0; j < packets.length; j++) {
				NetworkMessage packet = (NetworkMessage) packets[j];
				
				System.out.println(clients[i].getRemoteDetail() + ": "
				        + packet.getMessage());
				
				try {
					clients[i].sendPacket(new NetworkMessage("Server said OK"));
					
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		// construct server
		TCPServer server = null;
		
		try {
			server = new TCPServer(11137); // connect to port 11137
			
		}
		catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("Server initialization failed.\nCaused by:\n"
			        + ex.getMessage());
			System.exit(-1);
		}
		
		System.out.println("Server Detail: " + server.getDetail());
		
		// network loop
		while (true) {
			try {
				server.update(100);
				
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			
			TestServer.handleNetwork(server);
			
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		
		// try {
		// server.close();
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
	}
	
}
