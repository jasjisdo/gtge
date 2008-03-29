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
 * Sprite that its images is taken from another sprite (the pattern).
 * <p>
 * 
 * <code>PatternSprite</code> is used to make a number of sprite that share
 * same images and have same animation sequence. <br>
 * A sprite that not created in a same time will have a different animation
 * sequence (the new sprite will start with the first frame animation, and the
 * old one perhaps at the last animation). This kind of sprite will assure that
 * <code>PatternSprite</code> that share the same pattern will animated in the
 * same sequence.
 * <p>
 * 
 * Note: Don't forget to update the pattern sprite in order to keep the pattern
 * animate.
 */
public class PatternSprite extends Sprite {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1246202800220501766L;
	private Sprite pattern;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>PatternSprite</code> with specified pattern and
	 * coordinate.
	 */
	public PatternSprite(Sprite pattern, double x, double y) {
		super(pattern.getImage(), x, y);
		
		this.pattern = pattern;
	}
	
	/**
	 * Creates new <code>PatternSprite</code> with specified pattern.
	 */
	public PatternSprite(Sprite pattern) {
		super(pattern.getImage(), 0, 0);
		
		this.pattern = pattern;
	}
	
	/** ************************************************************************* */
	/** ************************* RENDER THE PATTERN **************************** */
	/** ************************************************************************* */
	
	public void render(Graphics2D g, int x, int y) {
		g.drawImage(this.pattern.getImage(), x, y, null);
	}
	
	/** ************************************************************************* */
	/** **************************** THE PATTERN ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns the pattern sprite associates with this sprite.
	 */
	public Sprite getPattern() {
		return this.pattern;
	}
	
	/**
	 * Sets the pattern of this sprite.
	 */
	public void setPattern(Sprite pattern) {
		this.pattern = pattern;
	}
	
}
