/*
 * DataObfuscation.java
 *
 * Created on May 23, 2007, 3:34 PM
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

package com.golden.gamedev.engine.network.manipulator;

import com.golden.gamedev.engine.network.DataManipulator;

/**
 * 
 * @author Paulus Tuerah
 */
public class DataObfuscation implements DataManipulator {
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates a new instance of DataObfuscation
	 */
	public DataObfuscation() {
	}
	
	public byte[] manipulate(byte[] data) throws Exception {
		// reverse byte array
		byte tmp;
		int half = data.length / 2;
		
		for (int i = 0; i < half; i++) {
			tmp = data[i];
			
			data[i] = data[data.length - i - 1];
			data[data.length - i - 1] = tmp;
		}
		
		return data;
	}
	
	public byte[] demanipulate(byte[] data) throws Exception {
		return this.manipulate(data);
	}
	
}
