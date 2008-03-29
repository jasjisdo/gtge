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
package com.golden.gamedev.object.sprite;

// JFC
import java.awt.Graphics2D;

import com.golden.gamedev.object.Sprite;

/**
 * <code>InvisibleSprite</code> is sprite that has no graphical image,
 * generally used in collision to make invisible block.
 */
public class InvisibleSprite extends Sprite {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5126744876218108841L;
	
	/**
	 * Creates new <code>InvisibleSprite</code> at specified position and
	 * specified size.
	 */
	public InvisibleSprite(double x, double y, int width, int height) {
		super(x, y);
		
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Renders nothing, the implementation of this method is blank.
	 */
	public void render(Graphics2D g) {
	}
	
	/**
	 * Renders nothing, the implementation of this method is blank.
	 */
	public void render(Graphics2D g, int x, int y) {
	}
	
}
