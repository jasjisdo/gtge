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
package com.golden.gamedev.object.background;

// JFC
import java.awt.Color;
import java.awt.Graphics2D;

import com.golden.gamedev.object.Background;

/**
 * The very basic background type that only fill the background view port with a
 * single color.
 * <p>
 * 
 * This type of background use a fixed memory size. Memory used by small size
 * color background (e.g: 1 x 1) with an extremely large size color background
 * (e.g: 100,000,000 x 100,000,000) is equal.
 */
public class ColorBackground extends Background {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3668701023849676983L;
	private Color color;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>ColorBackground</code> with specified size.
	 */
	public ColorBackground(Color bgColor, int w, int h) {
		super(w, h);
		
		this.color = bgColor;
	}
	
	/**
	 * Creates new <code>ColorBackground</code> as large as screen dimension.
	 */
	public ColorBackground(Color bgColor) {
		this.color = bgColor;
	}
	
	/** ************************************************************************* */
	/** ************************** BGCOLOR GET / SET **************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns this background color.
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Sets the background color.
	 */
	public void setColor(Color bgColor) {
		this.color = bgColor;
	}
	
	/** ************************************************************************* */
	/** ************************ RENDER BACKGROUND ****************************** */
	/** ************************************************************************* */
	
	public void render(Graphics2D g, int xbg, int ybg, int x, int y, int w, int h) {
		g.setColor(this.color);
		g.fillRect(x, y, w, h);
	}
	
}
