/*
 * TestServer.java
 *
 * Created on May 24, 2007, 2:10 PM
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

package test.packet;

// JFC
import java.io.IOException;

import com.golden.gamedev.engine.BaseClient;
import com.golden.gamedev.engine.BaseServer;
import com.golden.gamedev.engine.network.NetworkConfig;
import com.golden.gamedev.engine.network.NetworkPacket;
import com.golden.gamedev.engine.network.packet.NetworkMessage;
import com.golden.gamedev.engine.network.tcp.TCPServer;

/**
 * 
 * @author Paulus Tuerah
 */
public class TestServer {
	
	public static void main(String[] args) {
		// the server port
		int port = 1234;
		
		// we need to register our custom packet first
		NetworkConfig.registerPacket((short) 1, AllFieldTypePacket.class);
		NetworkConfig.DEBUG = true;
		
		try {
			BaseServer bsServer = new TCPServer(port); // create the server
			
			System.out.println("Waiting for clients...");
			
			// network loop
			while (true) {
				bsServer.update(100); // the main work
				
				// iterate all received packets
				BaseClient[] clients = bsServer.getReceivedPacketClients();
				for (int i = 0; i < clients.length; i++) {
					NetworkPacket[] packets = clients[i].getReceivedPackets();
					
					// simply send back message OK
					clients[i]
					        .sendPacket(new NetworkMessage("Server said OK."));
				}
				
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			
		}
		catch (IOException ex) {
			System.err.println("IO Exception");
			ex.printStackTrace();
			System.exit(-1);
		}
	}
	
}
