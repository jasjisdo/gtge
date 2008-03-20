/*
 * TCPServer.java
 *
 * Created on May 6, 2007, 6:12 PM
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

package com.golden.gamedev.engine.network.tcp;

// JFC
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

// GTGE
import com.golden.gamedev.engine.BaseServer;

/**
 *
 * @author Paulus Tuerah
 */
public class TCPServer extends BaseServer {
	
	private ServerSocketChannel server;
	
	private Selector			clientReader;
	private Selector			packetReader;
	
	
 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/
	
	/** Creates a new instance of TCPServer */
	public TCPServer(int port) throws IOException {
		this(new InetSocketAddress(port));
	}
	
	public TCPServer(SocketAddress host) throws IOException {
		server = ServerSocketChannel.open();
		server.socket().bind(host);
		server.configureBlocking(false);
		
		// register for event: accept new client connection
		clientReader	= Selector.open();
		server.register(clientReader, SelectionKey.OP_ACCEPT);
		
		// prepare for event: packet from client
		packetReader	= Selector.open();
	}

	
	public void update(long elapsedTime) throws IOException {
		super.update(elapsedTime);
		
		if (clientReader.selectNow() > 0) {
			// client connected
			Iterator clientIterator = clientReader.selectedKeys().iterator();
			while (clientIterator.hasNext()) {
				SelectionKey key = (SelectionKey) clientIterator.next();
				clientIterator.remove();
				
				// construct client
				SocketChannel client = server.accept();
					
				TCPClient tcpClient = new TCPClient(this, client, packetReader);
				
				// add to connection client list
				addConnectingClient(tcpClient);
			}
		}

		
		if (packetReader.selectNow() > 0) {
			// packet received
			Iterator packetIterator = packetReader.selectedKeys().iterator();
			
			while (packetIterator.hasNext()) {
				SelectionKey key = (SelectionKey) packetIterator.next();
				packetIterator.remove();

				// construct packet
				TCPClient client = (TCPClient) key.attachment();

				try {
					client.read();

				} catch (IOException ex) {
//					ex.printStackTrace();
					key.cancel();
					removeClient(client);
				}
			}
		}		
	}
	
//	public void blockingUpdate(long elapsedTime) throws IOException {
//		super.update(elapsedTime);
//		
//		// this select() blocks until there is activity on one of 
//		// the registered channels
//		selector.select();
//		
//		// get a java.util.Set containing the SelectionKey objects for
//		// all channels that are ready for I/O
//		Set keys = selector.selectedKeys();
//		
//		// use a java.util.Iterator to loop through the selected keys
//		Iterator i=keys.iterator();
//		while (i.hasNext()) {
//			SelectionKey key = (SelectionKey) i.next();
//			i.remove();  // remove the key from the set of selected keys
//			
//			// check whether this key is the SelectionKey we got when
//			// we registered the ServerSocketChannel
//			if (key == acceptKey) {
//				// activity on the ServerSocketChannel means a client
//				// is trying to connect to the server.
//				if (key.isAcceptable()) {
//					// accept the client connection
//					SocketChannel client = server.accept();
//
//					// construct the client
//					TCPClient tcpClient = new TCPClient(this, client, selector);
//					
//					// add to connection client list
//					addConnectingClient(tcpClient);
//				}
//				
//			} else {  
//				// otherwise, there must be activity on a client channel
//				// double-check that there is data to read
//				if (!key.isReadable()) continue;
//				
//				// get the client channel that has data to read
//				TCPClient client = (TCPClient) key.attachment();
//				
//				// construct packet
//				try {
//					client.read();
//					
//				} catch (IOException ex) {
//					key.cancel();
////					ex.printStackTrace();
//					removeClient(client);
//				}
//			}
//		}
//	}

	protected void disconnectImpl() throws IOException {
		server.close();
	}

	
	public String getDetail() {
		return server.socket().getLocalSocketAddress().toString();
	}
	
}
