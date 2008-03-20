/*
 * TCPClient.java
 *
 * Created on May 6, 2007, 9:19 PM
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

// GTGE
import com.golden.gamedev.engine.BaseClient;
import java.io.DataInputStream;
import java.nio.ByteOrder;

/**
 *
 * @author Paulus Tuerah
 */
public class TCPClient extends BaseClient {
	
	private static final int		BUFFER_SIZE = 10240;

	private SocketChannel			client;
	private Selector				packetReader;
	
	private ByteBuffer				readBuffer;
	private ByteBuffer				writeBuffer;

	// packet stream reader
	private DataInputStream			input;
	private PipedOutputStream	storage;
	private boolean					waitingForLength = true;
	private int						length;	

	
 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/
	
	/** Creates a new instance of TCPClient */
	public TCPClient(String host, int port) throws IOException {
		this(new InetSocketAddress(host, port));
	}

	public TCPClient(SocketAddress host) throws IOException {
		this.client				= SocketChannel.open();
		this.packetReader		= Selector.open();

		client.connect(host);
		
		init();
	}
	
	protected TCPClient(TCPServer server, SocketChannel client, Selector packetReader) throws IOException {
		super(server);
		
		this.client			= client;
		this.packetReader	= packetReader;
		
		init();
	}
	
	private void init() throws IOException {
		storage		= new PipedOutputStream();
		input		= new DataInputStream(new PipedInputStream(storage, BUFFER_SIZE*5));

		// allocate unpack/write network packet buffer
		readBuffer	= ByteBuffer.allocate(BUFFER_SIZE);
		writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		
		readBuffer.order(ByteOrder.BIG_ENDIAN);
		writeBuffer.order(ByteOrder.BIG_ENDIAN);
		
		client.configureBlocking(false);
		client.socket().setTcpNoDelay(true);
		
		// register packet reader
		// if this client is client on server then packetReader 
		// is grabbed from the server and shared among all other clients
		// (one packetReader used by all clients)
		client.register(packetReader, SelectionKey.OP_READ, this);
	}
	
	
	// this is used only if this is a real client
	// client on server is managed by the server itself
	public void update(long elapsedTime) throws IOException {
		super.update(elapsedTime);
		
		if (packetReader.selectNow() > 0) {
			// packet received
			Iterator packetIterator = packetReader.selectedKeys().iterator();
			
			while (packetIterator.hasNext()) {
				SelectionKey key = (SelectionKey) packetIterator.next();
				packetIterator.remove();
				
				read();
			}
		}
	}
	
	protected synchronized void read() throws IOException {
		readBuffer.clear();							// clear previous buffer
		
		int bytesRead = client.read(readBuffer);	// unpack into unpack buffer
		if (bytesRead < 0) {
			throw new IOException("Reached end of stream");
			
		} else if (bytesRead == 0) {
			return;
		}


		// write to storage (DataInputStream input field storage)
		storage.write(readBuffer.array(), 0, bytesRead);
		
		
		// unpack the packet
		while (input.available() > 0) { 
			// unpack the byte length first
			if (waitingForLength) {
				if (input.available() > 2) {
					length				= input.readShort();
					waitingForLength	= false;
					
				} else {
					// the length has not fully read
					break;
				}
				
			// then construct the packet
			} else {
				if (input.available() >= length) {
					// store the content to data
					byte[] data = new byte[length];
					input.readFully(data); 
			
					// add to received packet
					addReceivedPacket(data);
					
					waitingForLength = true;
					
				} else {
					// the content has not fully read
					break;
				}
			}
		}
	}

	protected synchronized void sendPacket(byte[] data) throws IOException {
		writeBuffer.clear();						// clear previous data
		
		writeBuffer.putShort((short) data.length);	// send the byte length first
		writeBuffer.put(data);						// and then send the data
		
		writeBuffer.rewind();
		writeBuffer.limit(data.length + 2);			// limit the byte to data size plus 2 (data length)
		
		client.write(writeBuffer);					// send to network

		writeBuffer.limit(2048);					// refresh the limit
	}
	
	
	protected void disconnectImpl() throws IOException {
		client.close();
	}

	public boolean isConnected() {
		return client.isConnected();
	}
	
	
	public String getDetail() {
		return String.valueOf(client.socket().getLocalSocketAddress());
	}

	public String getRemoteDetail() {
		return client.socket().getRemoteSocketAddress().toString();
	}
	
}