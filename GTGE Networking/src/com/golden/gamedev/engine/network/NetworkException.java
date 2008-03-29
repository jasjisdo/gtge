/*
 * NetworkException.java
 *
 * Created on May 9, 2007, 3:55 AM
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

package com.golden.gamedev.engine.network;

/**
 * 
 * @author Paulus Tuerah
 */
public class NetworkException extends java.io.IOException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4996531508477520066L;
	
	/**
	 * Creates a new instance of <code>NetworkException</code> without detail
	 * message.
	 */
	public NetworkException() {
	}
	
	/**
	 * Constructs an instance of <code>NetworkException</code> with the
	 * specified detail message.
	 * @param msg the detail message.
	 */
	public NetworkException(String msg) {
		super(msg);
	}
}
