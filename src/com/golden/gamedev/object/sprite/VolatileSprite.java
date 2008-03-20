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
import java.awt.image.BufferedImage;


/**
 * One time animation sprite, the sprite is animated once, and then
 * disappeared, suitable for explosion type sprite.
 */
public class VolatileSprite extends AdvanceSprite {


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new <code>VolatileSprite</code>.
	 */
	public VolatileSprite(BufferedImage[] image, double x, double y) {
		super(image,x,y);

		setAnimate(true);
	}


 /****************************************************************************/
 /*************************** UPDATE SPRITE **********************************/
 /****************************************************************************/

	public void update(long elapsedTime) {
		super.update(elapsedTime);

		if (!isAnimate()) {
			setActive(false);
		}
	}

}