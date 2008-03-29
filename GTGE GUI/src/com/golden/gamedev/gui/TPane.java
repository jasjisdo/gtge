/*
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
package com.golden.gamedev.gui;

import com.golden.gamedev.gui.toolkit.TContainer;

/**
 * TPane is simply a transparent container for grouping components.
 */
public class TPane extends TContainer {
	
	public TPane(int x, int y, int w, int h) {
		super(x, y, w, h);
		
		// turn on custom rendering
		this.customRendering = true;
	}
	
	/**
	 * This Component UI Name is <b>Pane</b>.
	 */
	public String UIName() {
		return "Pane";
	}
	
}
