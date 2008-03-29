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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.golden.gamedev.object.Background;

/**
 * Background that use a single image as the background.
 */
public class ImageBackground extends Background {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4083512078848542717L;
	private transient BufferedImage image;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>ImageBackground</code> with specified image and
	 * background size.
	 */
	public ImageBackground(BufferedImage image, int w, int h) {
		super(w, h);
		
		this.image = image;
	}
	
	/**
	 * Creates new <code>ImageBackground</code> with specified image and the
	 * background size is as large as the image.
	 */
	public ImageBackground(BufferedImage image) {
		super(image.getWidth(), image.getHeight());
		
		this.image = image;
	}
	
	/** ************************************************************************* */
	/** ************************ IMAGE GET / SET ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns this background image.
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * Sets this background image, and the size of this background is set to the
	 * image size.
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
		
		this.setSize(image.getWidth(), image.getHeight());
	}
	
	/** ************************************************************************* */
	/** ************************ RENDER BACKGROUND ****************************** */
	/** ************************************************************************* */
	
	public void render(Graphics2D g, int xbg, int ybg, int x, int y, int w, int h) {
		g.drawImage(this.image, x, y, x + w, y + h, // destination (screen area)
		        xbg, ybg, xbg + w, ybg + h, // source (image area)
		        null);
	}
	
}
