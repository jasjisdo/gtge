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
package com.golden.gamedev.object.collision;

// JFC
import java.awt.Rectangle;

import com.golden.gamedev.object.Background;
import com.golden.gamedev.object.CollisionManager;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;

/**
 * Checks collision for specified boundary.
 */
public abstract class CollisionBounds extends CollisionManager {
	
	/** ********************* COLLISION SIDE CONSTANTS ************************** */
	
	/**
	 * Indicates the sprite is collided at its left.
	 */
	public static final int LEFT_COLLISION = 1;
	
	/**
	 * Indicates the sprite is collided at its right.
	 */
	public static final int RIGHT_COLLISION = 2;
	
	/**
	 * Indicates the sprite is collided at its top.
	 */
	public static final int TOP_COLLISION = 4;
	
	/**
	 * Indicates the sprite is collided at its bottom.
	 */
	public static final int BOTTOM_COLLISION = 8;
	
	/** ************************ COLLISION PROPERTIES *************************** */
	
	private static final SpriteGroup DUMMY = new SpriteGroup("Dummy");
	
	private final Rectangle boundary = new Rectangle();
	
	private Sprite sprite1;
	private int collisionSide;
	private int collisionX1, collisionY1;
	
	// sprite bounding box
	/**
	 * Default sprite bounding box used in {@link #getCollisionShape1(Sprite)}.
	 */
	protected final CollisionRect rect1 = new CollisionRect();
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>CollisionBounds</code> with specified boundary.
	 */
	public CollisionBounds(int x, int y, int width, int height) {
		this.boundary.setBounds(x, y, width, height);
	}
	
	/**
	 * Creates new <code>CollisionBounds</code> with specified background as
	 * the boundary.
	 */
	public CollisionBounds(Background backgr) {
		this.boundary.setBounds(0, 0, backgr.getWidth(), backgr.getHeight());
	}
	
	public void setCollisionGroup(SpriteGroup group1, SpriteGroup group2) {
		super.setCollisionGroup(group1, CollisionBounds.DUMMY);
	}
	
	/** ************************************************************************* */
	/** ****************** MAIN-METHOD: CHECKING COLLISION ********************** */
	/** ************************************************************************* */
	
	public void checkCollision() {
		SpriteGroup group1 = this.getGroup1();
		if (!group1.isActive()) {
			// the group is not active, no need to check collision
			return;
		}
		
		Sprite[] member1 = group1.getSprites();
		int size1 = group1.getSize();
		
		CollisionShape shape1;
		
		// sprite 1 collision rectangle -> rect1
		for (int i = 0; i < size1; i++) {
			this.sprite1 = member1[i];
			
			if (!this.sprite1.isActive()
			        || (shape1 = this.getCollisionShape1(this.sprite1)) == null) {
				// sprite do not want collision check
				continue;
			}
			
			this.collisionSide = 0;
			this.collisionX1 = (int) this.sprite1.getX();
			this.collisionY1 = (int) this.sprite1.getY();
			
			if (shape1.getX() < this.boundary.x) {
				this.collisionX1 = this.boundary.x;
				this.collisionSide |= CollisionBounds.LEFT_COLLISION;
			}
			if (shape1.getY() < this.boundary.y) {
				this.collisionY1 = this.boundary.y;
				this.collisionSide |= CollisionBounds.TOP_COLLISION;
			}
			if (shape1.getX() + shape1.getWidth() > this.boundary.x
			        + this.boundary.width) {
				this.collisionX1 = this.boundary.x + this.boundary.width
				        - shape1.getWidth();
				this.collisionSide |= CollisionBounds.RIGHT_COLLISION;
			}
			if (shape1.getY() + shape1.getHeight() > this.boundary.y
			        + this.boundary.height) {
				this.collisionY1 = this.boundary.y + this.boundary.height
				        - shape1.getHeight();
				this.collisionSide |= CollisionBounds.BOTTOM_COLLISION;
			}
			
			// fire collision event
			if (this.collisionSide != 0) {
				this.collided(this.sprite1);
			}
		}
	}
	
	/**
	 * Reverts the sprite position before the collision occured.
	 */
	public void revertPosition1() {
		this.sprite1.forceX(this.collisionX1);
		this.sprite1.forceY(this.collisionY1);
	}
	
	/**
	 * Sets specified <code>Sprite</code> collision rectangle (sprite bounding
	 * box) into <code>rect</code>.
	 * <p>
	 * In this implementation, the sprite bounding box is as large as
	 * <code>Sprite</code> dimension :
	 * 
	 * <pre>
	 * public boolean getCollisionRect1(Sprite s1, CollisionRect rect) {
	 * 	rect.setBounds(s1.getX(), s1.getY(), s1.getWidth(), s1.getHeight());
	 * 	return rect;
	 * }
	 * </pre>
	 * 
	 * @return false, to skip collision check.
	 * @see CollisionRect#intersects(CollisionShape)
	 */
	public CollisionShape getCollisionShape1(Sprite s1) {
		this.rect1.setBounds(s1.getX(), s1.getY(), s1.getWidth(), s1
		        .getHeight());
		
		return this.rect1;
	}
	
	/**
	 * Returns true, the sprite is collide at it <code>side</code> side.
	 */
	public boolean isCollisionSide(int side) {
		return (this.collisionSide & side) != 0;
	}
	
	/**
	 * Sets the collision boundary, the sprite is bounded to this boundary.
	 */
	public void setBoundary(int x, int y, int width, int height) {
		this.boundary.setBounds(x, y, width, height);
	}
	
	/**
	 * Returns the boundary of the sprites.
	 */
	public Rectangle getBoundary() {
		return this.boundary;
	}
	
	/**
	 * Sprite <code>sprite</code> hit collision boundary, perform collided
	 * implementation.
	 */
	public abstract void collided(Sprite sprite);
	
}
