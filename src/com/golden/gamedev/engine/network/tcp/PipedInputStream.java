/*
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
import java.io.InputStream;

/**
 *
 * @author Kevin
 */
public class PipedInputStream extends InputStream {
	
	private int[]	data;
	private int		write;
	private int		read;
	private int		available;
	
	public PipedInputStream(PipedOutputStream pout, int size) throws IOException {
		data = new int[size];
		pout.connect(this);
	}
	
	
	public synchronized int available() throws IOException {
		return available;
	}

	public void close() throws IOException {
	}

	public synchronized int read() throws IOException {
		if (available <= 0) {
			return -1;
		}
		if (read == write) {
			return -1;
		}
		
		available--;
		int value = data[read];
		read++;
		if (read >= data.length) {
			read = 0;
		}
		return value;
	}

	public synchronized int read(byte[] ret, int off, int len) throws IOException {
		if (available <= 0) {
			return -1;
		}
		
		int count = 0;
		for (int i=off;i<off+len;i++) {
			int b = read();
			if (b == -1) {
				break;
			}
			count++;
			ret[i] = (byte) b;
		}

		return count;
	}

	protected synchronized void receive(int b) throws IOException {
		if (b < 0) {
			b = 256 + b;
		}
		data[write] = b;
		write++;
		if (write >= data.length) {
			write = 0;
		}
		if (write == read) {
			throw new IOException("Buffer overflow in NewPipedInputStream");
		}
		
		available++;
	}

	public int read(byte[] b) throws IOException {
		return read(b,0,b.length);
	}

}
