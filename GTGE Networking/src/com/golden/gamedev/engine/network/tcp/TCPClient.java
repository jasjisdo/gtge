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

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.golden.gamedev.engine.BaseClient;

/**
 * 
 * @author Paulus Tuerah
 */
public class TCPClient extends BaseClient {
	
	private static final int BUFFER_SIZE = 10240;
	
	private SocketChannel client;
	private Selector packetReader;
	
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	
	// packet stream reader
	private DataInputStream input;
	private PipedOutputStream storage;
	private boolean waitingForLength = true;
	private int length;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/** Creates a new instance of TCPClient */
	public TCPClient(String host, int port) throws IOException {
		this(new InetSocketAddress(host, port));
	}
	
	public TCPClient(SocketAddress host) throws IOException {
		this.client = SocketChannel.open();
		this.packetReader = Selector.open();
		
		this.client.connect(host);
		
		this.init();
	}
	
	protected TCPClient(TCPServer server, SocketChannel client,
	        Selector packetReader) throws IOException {
		super(server);
		
		this.client = client;
		this.packetReader = packetReader;
		
		this.init();
	}
	
	private void init() throws IOException {
		this.storage = new PipedOutputStream();
		this.input = new DataInputStream(new PipedInputStream(this.storage,
		        TCPClient.BUFFER_SIZE * 5));
		
		// allocate unpack/write network packet buffer
		this.readBuffer = ByteBuffer.allocate(TCPClient.BUFFER_SIZE);
		this.writeBuffer = ByteBuffer.allocate(TCPClient.BUFFER_SIZE);
		
		this.readBuffer.order(ByteOrder.BIG_ENDIAN);
		this.writeBuffer.order(ByteOrder.BIG_ENDIAN);
		
		this.client.configureBlocking(false);
		this.client.socket().setTcpNoDelay(true);
		
		// register packet reader
		// if this client is client on server then packetReader
		// is grabbed from the server and shared among all other clients
		// (one packetReader used by all clients)
		this.client.register(this.packetReader, SelectionKey.OP_READ, this);
	}
	
	// this is used only if this is a real client
	// client on server is managed by the server itself
	public void update(long elapsedTime) throws IOException {
		super.update(elapsedTime);
		
		if (this.packetReader.selectNow() > 0) {
			// packet received
			Iterator packetIterator = this.packetReader.selectedKeys()
			        .iterator();
			
			while (packetIterator.hasNext()) {
				SelectionKey key = (SelectionKey) packetIterator.next();
				packetIterator.remove();
				
				this.read();
			}
		}
	}
	
	protected synchronized void read() throws IOException {
		this.readBuffer.clear(); // clear previous buffer
		
		int bytesRead = this.client.read(this.readBuffer); // unpack into
															// unpack buffer
		if (bytesRead < 0) {
			throw new IOException("Reached end of stream");
			
		}
		else if (bytesRead == 0) {
			return;
		}
		
		// write to storage (DataInputStream input field storage)
		this.storage.write(this.readBuffer.array(), 0, bytesRead);
		
		// unpack the packet
		while (this.input.available() > 0) {
			// unpack the byte length first
			if (this.waitingForLength) {
				if (this.input.available() > 2) {
					this.length = this.input.readShort();
					this.waitingForLength = false;
					
				}
				else {
					// the length has not fully read
					break;
				}
				
				// then construct the packet
			}
			else {
				if (this.input.available() >= this.length) {
					// store the content to data
					byte[] data = new byte[this.length];
					this.input.readFully(data);
					
					// add to received packet
					this.addReceivedPacket(data);
					
					this.waitingForLength = true;
					
				}
				else {
					// the content has not fully read
					break;
				}
			}
		}
	}
	
	protected synchronized void sendPacket(byte[] data) throws IOException {
		this.writeBuffer.clear(); // clear previous data
		
		this.writeBuffer.putShort((short) data.length); // send the byte length
														// first
		this.writeBuffer.put(data); // and then send the data
		
		this.writeBuffer.rewind();
		this.writeBuffer.limit(data.length + 2); // limit the byte to data
													// size plus 2 (data length)
		
		this.client.write(this.writeBuffer); // send to network
		
		this.writeBuffer.limit(2048); // refresh the limit
	}
	
	protected void disconnectImpl() throws IOException {
		this.client.close();
	}
	
	public boolean isConnected() {
		return this.client.isConnected();
	}
	
	public String getDetail() {
		return String.valueOf(this.client.socket().getLocalSocketAddress());
	}
	
	public String getRemoteDetail() {
		return this.client.socket().getRemoteSocketAddress().toString();
	}
	
}
