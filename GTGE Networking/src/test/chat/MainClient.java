/*
 * MainClient.java
 *
 * Created on May 3, 2007, 1:07 AM
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

package test.chat;

// JFC
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.golden.gamedev.engine.network.packet.NetworkMessage;
import com.golden.gamedev.engine.network.tcp.TCPClient;

/**
 * 
 * @author Paulus Tuerah
 */
public class MainClient {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ex) {
		}
		
		// server host name
		String host = JOptionPane.showInputDialog("Server Host IP/Name :",
		        "localhost");
		if (host == null) {
			System.exit(0);
		}
		
		// server port
		String port = JOptionPane.showInputDialog("Server Port :", "11137");
		if (port == null) {
			System.exit(0);
		}
		
		int portNumber = 0;
		
		try {
			portNumber = Integer.parseInt(port);
		}
		catch (NumberFormatException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
		
		// create client and connet to server
		TCPClient client = null;
		try {
			client = new TCPClient(host, portNumber);
			
		}
		catch (IOException ex) {
			ex.printStackTrace();
			
			JOptionPane.showMessageDialog(null, "Connection Failed.\n"
			        + "Caused by:\n" + ex.getMessage(), "Connection Failed",
			        JOptionPane.ERROR_MESSAGE);
			
			System.exit(-1);
		}
		
		// our first network packet: nick name
		String nick = JOptionPane.showInputDialog("Enter Your Nick Name:");
		if (nick == null) {
			// cancel
			client.silentDisconnect();
			System.exit(0);
		}
		
		// send the client nick name
		try {
			client.sendPacket(new NetworkMessage(nick));
		}
		catch (IOException ex) {
			ex.printStackTrace();
			
			JOptionPane.showMessageDialog(null, "Connection Failed.\n"
			        + "Caused by:\n" + ex.getMessage(), "Connection Failed",
			        JOptionPane.ERROR_MESSAGE);
			
			System.exit(-1);
		}
		
		// show the ui
		new ClientGUI(client, nick).setVisible(true);
	}
	
}
