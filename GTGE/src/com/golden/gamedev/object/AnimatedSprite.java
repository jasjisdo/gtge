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
package com.golden.gamedev.object;

// JFC
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * <code>AnimatedSprite</code> class is sprite that use several images and can
 * be animated.
 * <p>
 * 
 * To animate animated sprite, simply use
 * {@linkplain #setAnimate(boolean) setAnimate(true)}, and to loop the
 * animation use {@linkplain #setLoopAnim(boolean) setLoopAnim(true)}. <br>
 * To control animation speed, adjust the sprite
 * {@linkplain #getAnimationTimer() animation timer} delay. <br>
 * To control animation frame sequence, use
 * {@link #setAnimationFrame(int, int) setAnimationFrame(int startframe, int endframe)}.
 * <p>
 * 
 * Animated sprite usage example :
 * 
 * <pre>
 * AnimatedSprite sprite;
 * BufferedImage[] spriteImage;
 * // create the sprite
 * sprite = new AnimatedSprite(spriteImage, 0, 0);
 * // set animation speed 100 milliseconds for each frame
 * sprite.getAnimationTimer().setDelay(100);
 * // set animation frame starting from the first image to the third image
 * sprite.setAnimationFrame(0, 2);
 * // animate the sprite, and perform continous animation
 * sprite.setAnimate(true);
 * sprite.setLoopAnim(true);
 * </pre>
 * 
 * <code>AnimatedSprite</code> is only to make a simple animated sprite, there
 * is no property to control sprite animation behaviour. For more control to
 * determine sprite animation based on sprite status and direction, use
 * {@link com.golden.gamedev.object.sprite.AdvanceSprite}.
 * 
 * @see com.golden.gamedev.object.sprite.AdvanceSprite
 */
public class AnimatedSprite extends Sprite {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3921494149259881742L;
	/** ************************** SPRITE ANIMATION ***************************** */
	
	// sprite images
	private transient BufferedImage[] image;
	private int frame;
	private int startFrame;
	private int finishFrame;
	
	// true, sprite is animated
	private boolean animate;
	
	// true, the animation is continously (looping)
	private boolean loopAnim;
	
	private Timer animationTimer;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>AnimatedSprite</code> with specified images and
	 * location.
	 * 
	 * @param image sprite images
	 * @param x sprite x-coordinate
	 * @param y sprite y-coordinate
	 */
	public AnimatedSprite(BufferedImage[] image, double x, double y) {
		super(x, y);
		
		// set sprite images
		if (image != null) {
			this.image = image;
			this.width = image[0].getWidth();
			this.height = image[0].getHeight();
			
			this.frame = this.startFrame = 0;
			this.finishFrame = image.length - 1;
		}
		
		// default animation speed = 60 ms
		this.animationTimer = new Timer(60);
	}
	
	/**
	 * Creates new <code>AnimatedSprite</code> with specified images and
	 * located at (0, 0).
	 * <p>
	 * 
	 * @see #setLocation(double, double)
	 */
	public AnimatedSprite(BufferedImage[] image) {
		this(image, 0, 0);
	}
	
	/**
	 * Creates new <code>AnimatedSprite</code> with null image to specified
	 * location.
	 * <p>
	 * 
	 * The sprite images must be set before rendering.
	 * 
	 * @param x sprite x-coordinate
	 * @param y sprite y-coordinate
	 */
	public AnimatedSprite(double x, double y) {
		this(null, x, y);
	}
	
	/**
	 * Creates new <code>AnimatedSprite</code> with null image and located at
	 * (0, 0).
	 * <p>
	 * 
	 * The sprite images must be set before rendering.
	 * 
	 * @see #setImages(BufferedImage[])
	 * @see #setLocation(double, double)
	 */
	public AnimatedSprite() {
		this(0, 0);
	}
	
	/** ************************************************************************* */
	/** ************************* IMAGE OPERATION ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Sets sprite animation images.
	 */
	public void setImages(BufferedImage[] image) {
		if (this.image == image) {
			return;
		}
		
		// nullify previous image
		this.image = null;
		
		// reset frame
		this.frame = 0;
		this.startFrame = 0;
		this.animationTimer.refresh();
		
		if (image == null || image[0] == null) {
			// no images
			this.width = this.height = this.finishFrame = 0;
			
		}
		else {
			this.image = image;
			this.width = image[0].getWidth();
			this.height = image[0].getHeight();
			this.finishFrame = image.length - 1;
		}
	}
	
	/**
	 * This method is obsolete on animated sprite, since animated sprite always
	 * use several images for its animation.
	 * 
	 * @see #setImages(BufferedImage[])
	 */
	public void setImage(BufferedImage image) {
		throw new RuntimeException("Animated Sprite need an array of images, "
		        + "use setImages(BufferedImage[]) instead!");
	}
	
	/**
	 * Returns sprite animation images.
	 */
	public BufferedImage[] getImages() {
		return this.image;
	}
	
	/**
	 * Returns image of specified frame.
	 */
	public BufferedImage getImage(int i) {
		return this.image[i];
	}
	
	/**
	 * Returns image of current frame.
	 */
	public BufferedImage getImage() {
		return this.image[this.frame];
	}
	
	/** ************************************************************************* */
	/** ************************** ANIMATION FRAME ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Sets current frame animation.
	 */
	public void setFrame(int i) {
		this.frame = i + this.startFrame;
		
		if (this.frame > this.finishFrame) {
			this.frame = this.startFrame;
			
		}
		else if (this.frame < this.startFrame) {
			this.frame = this.finishFrame;
		}
	}
	
	/**
	 * Returns current frame animation.
	 */
	public int getFrame() {
		return this.frame;
	}
	
	/**
	 * Sets sprite animation sequence, starting from <code>start</code> frame
	 * and ended at <code>finish</code> frame. The sprite current frame is
	 * automatically set to <code>start</code> frame.
	 * <p>
	 * 
	 * The first images is <code>frame 0</code> and the last images is
	 * <code>frame {@linkplain #getImages()}.length-1</code>.
	 * <p>
	 * 
	 * For example to make the sprite animated from the first image to the third
	 * image :
	 * 
	 * <pre>
	 * AnimatedSprite sprite;
	 * // animate from first image to third image
	 * sprite.setAnimationFrame(0, 2);
	 * sprite.setAnimate(true);
	 * </pre>
	 * 
	 * Note: If the <code>start</code> and <code>finish</code> frame is same
	 * with current animation frame, no action is taken.
	 */
	public void setAnimationFrame(int start, int finish) {
		if (start == this.startFrame && finish == this.finishFrame) {
			return;
		}
		
		this.animationTimer.refresh();
		
		this.startFrame = start;
		this.finishFrame = finish;
		
		this.frame = this.startFrame;
	}
	
	/**
	 * Returns the start animation frame of this frame.
	 * 
	 * @see #setAnimationFrame(int, int)
	 */
	public int getStartAnimationFrame() {
		return this.startFrame;
	}
	
	/**
	 * Returns the finish animation frame of this sprite.
	 * 
	 * @see #setAnimationFrame(int, int)
	 */
	public int getFinishAnimationFrame() {
		return this.finishFrame;
	}
	
	/** ************************************************************************* */
	/** *************************** UPDATE SPRITE ******************************* */
	/** ************************************************************************* */
	
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		if (this.animate && this.animationTimer.action(elapsedTime)) {
			this.updateAnimation();
		}
	}
	
	/**
	 * Updates sprite animation.
	 * <p>
	 * 
	 * The animation frame is increased by one, and if the animation frame is
	 * exceeded {@linkplain #getFinishAnimationFrame() total animation frame},
	 * the animation frame is back to the {@linkplain #getStartAnimationFrame()
	 * first animation frame}, and stop the animation if the animation is not
	 * set to {@linkplain #isLoopAnim() loop continously}.
	 */
	protected void updateAnimation() {
		if (++this.frame > this.finishFrame) {
			this.frame = this.startFrame;
			
			if (!this.loopAnim) {
				this.animate = false;
			}
		}
	}
	
	/** ************************************************************************* */
	/** ************************** RENDER SPRITE ******************************** */
	/** ************************************************************************* */
	
	public void render(Graphics2D g, int xs, int ys) {
		g.drawImage(this.image[this.frame], xs, ys, null);
	}
	
	/** ************************************************************************* */
	/** ************************** SPRITE PROPERTIES **************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns whether this sprite is currently animating or not.
	 */
	public boolean isAnimate() {
		return this.animate;
	}
	
	/**
	 * Sets true to animate this sprite.
	 */
	public void setAnimate(boolean b) {
		this.animate = b;
	}
	
	/**
	 * Returns whether this sprite animation is looping continously or not.
	 */
	public boolean isLoopAnim() {
		return this.loopAnim;
	}
	
	/**
	 * Sets true to animate this sprite continously.
	 */
	public void setLoopAnim(boolean b) {
		this.loopAnim = b;
	}
	
	/**
	 * Sets sprite animation timer.
	 */
	public void setAnimationTimer(Timer t) {
		this.animationTimer = t;
	}
	
	/**
	 * Returns sprite animation timer.
	 */
	public Timer getAnimationTimer() {
		return this.animationTimer;
	}
	
}
