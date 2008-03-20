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
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.object.AnimatedSprite;


/**
 * <code>AdvanceSprite</code> class is animated sprite that has status and
 * direction attributes, that way the animation is fully controlled by its
 * status and direction. <p>
 *
 * This class can be used directly, however this class meant to be subclassed.
 * The method that control the sprite animation and the one that need to be
 * override is {@link #animationChanged(int, int, int, int)
 * animationChanged(int oldStat, int oldDir, int status, int direction)}. <p>
 *
 * The <code>animationChanged</code> is the method that taking care the sprite
 * animation, everytime sprite {@link #setStatus(int) status} or
 * {@link #setDirection(int) direction} is being changed, the
 * <code>animationChanged</code> is called automatically, and this sprite need
 * to adjust its animation based on the new status and direction.
 *
 * @see #setStatus(int)
 * @see #setDirection(int)
 * @see #animationChanged(int, int, int, int)
 */
public class AdvanceSprite extends AnimatedSprite {


	private int[]	animationFrame;

	private int		status = -1;
	private int		direction = -1;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new <code>AdvanceSprite</code> with specified images and location.
	 *
	 * @param image	sprite images
	 * @param x		sprite x-coordinate
	 * @param y		sprite y-coordinate
	 */
	public AdvanceSprite(BufferedImage[] image, double x, double y) {
		super(image,x,y);
	}

    /**
     * Creates new <code>AdvanceSprite</code> with specified images
     * and located at (0, 0). <p>
     *
     * @see #setLocation(double, double)
     */
    public AdvanceSprite(BufferedImage[] image) {
	    super(image);
	}

	/**
	 * Creates new <code>AdvanceSprite</code> with specified location.
	 *
	 * @param x		sprite x-coordinate
	 * @param y		sprite y-coordinate
	 */
	public AdvanceSprite(double x, double y) {
		super(x,y);
	}

	/**
	 * Creates new <code>AdvanceSprite</code> with null image and located at (0, 0). <p>
     *
     * The sprite images must be set before rendering.
     *
     * @see #setImages(BufferedImage[])
     * @see #setLocation(double, double)
	 */
	public AdvanceSprite() {
		super();
	}


 /****************************************************************************/
 /************************** SETTING ANIMATION *******************************/
 /****************************************************************************/

	/**
	 * Sets sprite animation frame to specified animation array. The sprite will
	 * be animated according to the animation array. <p>
	 *
	 * Use this if only the animation is not following the standard image frames,
	 * for example if the sprite walking the animation is 2-3-4-2-1, then set
	 * the animation frame to : setAnimationFrame(new int[] { 2, 3, 4, 2, 1 });
	 *
	 * @see #setAnimationFrame(int, int)
	 */
	public void setAnimationFrame(int[] animation) {
		if (animationFrame != animation) {
			animationFrame = animation;

			setAnimationFrame(0,
							  (animationFrame != null) ?
							  (animationFrame.length-1) :
							  (getImages().length-1));
		}
	}

	/**
	 * Returns sprite animation frame or null if the sprite use standard
	 * animation frame.
	 */
	public int[] getAnimationFrame() {
		return animationFrame;
	}


 /****************************************************************************/
 /*************************** IMAGE OPERATION ********************************/
 /****************************************************************************/

    public BufferedImage getImage() {
		return (animationFrame == null) ?
			super.getImage() :
			getImage(animationFrame[getFrame()]);
	}

    public void setImages(BufferedImage[] image) {
		super.setImages(image);

		if (animationFrame != null) {
			setAnimationFrame(0, animationFrame.length-1);
		}
	}


 /****************************************************************************/
 /******************** NOTIFY STATUS / DIRECTION CHANGED *********************/
 /****************************************************************************/

	/**
	 * The sprite status and/or direction is changed, set appropriate sprite
	 * animation. <p>
	 *
	 * This method is responsible to set the sprite animation. By default this
	 * method do nothing.
	 *
	 * @see #setAnimationFrame(int, int)
	 * @see #setAnimationFrame(int[])
	 * @see #setImages(BufferedImage[])
	 * @see #setAnimate(boolean)
	 * @see #setLoopAnim(boolean)
	 */
	protected void animationChanged(int oldStat, int oldDir,
									int status, int direction) {
	}


 /****************************************************************************/
 /************************* STATUS / DIRECTION *******************************/
 /****************************************************************************/

	/**
	 * Sets new sprite direction. Direction is the direction which the sprite is
	 * facing, like left, right, up, down, each direction has its own animation
	 * based on its status. <p>
	 *
	 * If the sprite direction is changed (current direction != new direction),
	 * {@link #animationChanged(int, int, int, int)} will be notified.
	 *
	 * @see #animationChanged(int, int, int, int)
	 * @see #setStatus(int)
	 */
	public void setDirection(int dir) {
		if (direction != dir) {
			int oldDir = direction;
			direction = dir;

			animationChanged(status, oldDir, status, direction);
		}
	}

	/**
	 * Returns current sprite direction.
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Sets new sprite status. Status is the sprite status no matter in which
	 * directional the sprite is facing, like walking status, standing status,
	 * jumping status. <p>
	 *
	 * If the sprite status is changed (current status != new status),
	 * {@link #animationChanged(int, int, int, int)} will be notified.
	 *
	 * @see #animationChanged(int, int, int, int)
	 * @see #setDirection(int)
	 */
	public void setStatus(int stat) {
		if (status != stat) {
			int oldStat = status;
			status = stat;

			animationChanged(oldStat, direction, status, direction);
		}
	}

	/**
	 * Returns current sprite status.
	 */
	public int getStatus() {
		return status;
	}


	/**
	 * Sets sprite animation to specified status and direction. This method
	 * to simplify setting sprite status and direction at once.
	 *
	 * @param stat	the new sprite status
	 * @param dir	the new sprite direction
	 * @see #animationChanged(int, int, int, int)
	 */
	public void setAnimation(int stat, int dir) {
		if (status != stat || direction != dir) {
			int oldStat = status;
			int oldDir = direction;

			status = stat;
			direction = dir;

			animationChanged(oldStat, oldDir, stat, dir);
		}
	}


 /****************************************************************************/
 /**************************** RENDER SPRITE *********************************/
 /****************************************************************************/

	public void render(Graphics2D g, int xs, int ys) {
		if (animationFrame != null) {
			g.drawImage(getImage(animationFrame[getFrame()]), xs, ys, null);

		} else {
			super.render(g, xs, ys);
		}
	}

}