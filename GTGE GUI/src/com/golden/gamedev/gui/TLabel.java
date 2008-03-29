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

import com.golden.gamedev.gui.toolkit.TComponent;

public class TLabel extends TComponent {
	
	private String text;
	
	public TLabel(String text, int x, int y, int w, int h) {
		super(x, y, w, h);
		
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String st) {
		this.text = st;
		this.createUI();
	}
	
	/**
	 * This Component UI Name is <b>Label</b>.
	 */
	public String UIName() {
		return "Label";
	}
	
}
