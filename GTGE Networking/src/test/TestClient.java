/*
 * TestClient.java
 *
 * Created on May 6, 2007, 10:09 PM
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

import com.golden.gamedev.engine.network.NetworkPacket;
import com.golden.gamedev.engine.network.packet.NetworkMessage;
import com.golden.gamedev.engine.network.tcp.TCPClient;

/**
 * 
 * @author Paulus Tuerah
 */
public class TestClient {
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	public static void main(String[] args) {
		// connect to server
		TCPClient client = null;
		
		try {
			client = new TCPClient("localhost", 11137); // connect to
														// localhost:11137
			
		}
		catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("Client initialization failed.\nCaused by:\n"
			        + ex.getMessage());
			System.exit(-1);
		}
		
		System.out.println("Client Detail: " + client.getDetail());
		
		// network loop
		int i = 0;
		while (true) {
			// update client
			try {
				client.update(100);
				
			}
			catch (IOException ex) {
				ex.printStackTrace();
				System.err.println("Client disconnected.\nCaused by:\n"
				        + ex.getMessage());
				System.exit(-1);
			}
			
			// handle received messages
			NetworkPacket[] packets = client.getReceivedPackets();
			for (int j = 0; j < packets.length; j++) {
				NetworkMessage packet = (NetworkMessage) packets[j];
				
				System.out.println(client.getRemoteDetail() + ": "
				        + packet.getMessage());
			}
			
			// send message
			// every 20 tick
			if (i++ % 20 == 0 && i < 100) {
				try {
					System.out.println("sending packet to server");
					client.sendPacket(new NetworkMessage(
					        "Client said Yes or No"));
					
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
				
				// disconnect from server
				// after 200 tick
			}
			else if (i > 200) {
				try {
					client.disconnect();
					
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
				
				break; // out from network loop
			}
			
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException ex) {
			}
		}
	}
}
