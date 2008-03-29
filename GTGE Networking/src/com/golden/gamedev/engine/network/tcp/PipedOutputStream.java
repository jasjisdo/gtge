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
import java.io.OutputStream;

/**
 * 
 * @author Kevin
 */
public class PipedOutputStream extends OutputStream {
	
	private PipedInputStream input;
	
	public PipedOutputStream() {
		
	}
	
	public void connect(PipedInputStream input) {
		this.input = input;
	}
	
	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		this.input.receive(b);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = off; i < off + len; i++) {
			this.write(b[i]);
		}
	}
	
	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}
	
}
