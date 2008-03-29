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

import com.golden.gamedev.object.Background;

/**
 * <code>ParallaxBackground</code> class is a background composed by several
 * backgrounds.
 * <p>
 * 
 * This class automatically handles displaying and scrolling of the stacked
 * backgrounds. The backgrounds are normalized to the size and position of the
 * largest background in the stack. This way, the largest coordinate system is
 * presented, and all backgrounds move together at a smooth rate.
 * <p>
 * 
 * The backgrounds is rendered from the first background on the stack to the
 * last background on the stack, in other word the first background on the stack
 * will be at the back of other backgrounds.
 * <p>
 * 
 * Parallax background usage example : <br>
 * 
 * <pre>
 * ParallaxBackground background;
 * Background bg1, bg2, bg3;
 * background = new ParallaxBackground(new Background[] {
 *         bg1, bg2, bg3
 * });
 * // bg1 is at the back of bg2 and bg2 is at the back of bg3
 * </pre>
 */
public class ParallaxBackground extends Background {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8282072812030049762L;
	private Background[] stack;
	private int total;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>ParallaxBackground</code>.
	 */
	public ParallaxBackground(Background[] stack) {
		this.stack = stack;
		this.total = stack.length;
		
		this.normalizedView();
	}
	
	private void normalizedView() {
		// find the largest one!
		for (int i = 0; i < this.total; i++) {
			if (this.stack[i].getWidth() > this.getWidth()) {
				this.setSize(this.stack[i].getWidth(), this.getHeight());
			}
			
			if (this.stack[i].getHeight() > this.getHeight()) {
				this.setSize(this.getWidth(), this.stack[i].getHeight());
			}
		}
	}
	
	public void setLocation(double xb, double yb) {
		super.setLocation(xb, yb);
		
		for (int i = 0; i < this.total; i++) {
			this.stack[i].setLocation(this.getX()
			        * (this.stack[i].getWidth() - this.getClip().width)
			        / (this.getWidth() - this.getClip().width), this.getY()
			        * (this.stack[i].getHeight() - this.getClip().height)
			        / (this.getHeight() - this.getClip().height));
		}
	}
	
	/** ************************************************************************* */
	/** **************** UPDATE AND RENDER STACKED BACKGROUND ******************* */
	/** ************************************************************************* */
	
	public void update(long elapsedTime) {
		for (int i = 0; i < this.total; i++) {
			this.stack[i].update(elapsedTime);
		}
	}
	
	public void render(Graphics2D g) {
		for (int i = 0; i < this.total; i++) {
			this.stack[i].render(g);
		}
	}
	
	/**
	 * Returns the stacked parallax backgrounds.
	 */
	public Background[] getParallaxBackground() {
		return this.stack;
	}
	
	/**
	 * Sets parallax background stacked backgrounds.
	 */
	public void setParallaxBackground(Background[] stack) {
		this.stack = stack;
		this.total = stack.length;
		
		this.normalizedView();
	}
	
}
