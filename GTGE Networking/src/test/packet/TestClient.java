/*
 * TestClient.java
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
import com.golden.gamedev.engine.network.NetworkConfig;
import com.golden.gamedev.engine.network.tcp.TCPClient;

/**
 * 
 * @author Paulus Tuerah
 */
public class TestClient {
	
	public static void main(String[] args) {
		// the server host and port
		String host = "localhost";
		int port = 1234;
		
		// we need to register our custom packet first
		NetworkConfig.registerPacket((short) 1, AllFieldTypePacket.class);
		NetworkConfig.DEBUG = true;
		
		try {
			BaseClient bsClient = new TCPClient(host, port); // create client
																// and connect
																// to server
			
			int sendCounter = 0;
			
			System.out.println("Start the Client...");
			
			// network loop
			while (true) {
				bsClient.update(100); // the main work
				
				// send packet to server
				// every 50 tick
				if (++sendCounter % 50 == 0) {
					bsClient.sendPacket(new AllFieldTypePacket());
				}
				
				// disconnect from server
				// after 200 tick
				if (sendCounter > 200) {
					bsClient.silentDisconnect();
					break;
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
