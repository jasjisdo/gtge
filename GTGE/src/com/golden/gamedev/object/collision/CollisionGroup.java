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

// GTGE
import com.golden.gamedev.object.CollisionManager;
import com.golden.gamedev.object.PlayField;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;

/**
 * Basic collision check, with calculation of some collision events, such as
 * collision side, and sprite return position.
 * <p>
 * 
 * Suitable for collision that only need to know the side of the collision, but
 * not need precise sprite return position.
 * <p>
 * 
 * For example collision between projectile and enemy in platform game, the
 * implementation need collision side to determine to which side the enemy
 * should fall.
 * <p>
 * 
 * To get more precise sprite position after collision use
 * {@link PreciseCollisionGroup} instead.
 * 
 * @see PlayField#addCollisionGroup(SpriteGroup, SpriteGroup, CollisionManager)
 */
public abstract class CollisionGroup extends BasicCollisionGroup {
	
	/** ********************* COLLISION SIDE CONSTANTS ************************** */
	
	/**
	 * Indicates the collision side of the collided sprites is the left of first
	 * sprite againts the right of other sprite.
	 */
	public static final int LEFT_RIGHT_COLLISION = 1;
	
	/**
	 * Indicates the collision side of the collided sprites is the right of
	 * first sprite againts the left of other sprite.
	 */
	public static final int RIGHT_LEFT_COLLISION = 2;
	
	/**
	 * Indicates the collision side of the collided sprites is the top of first
	 * sprite againts the bottom of other sprite.
	 */
	public static final int TOP_BOTTOM_COLLISION = 4;
	
	/**
	 * Indicates the collision side of the collided sprites is the bottom of
	 * first sprite againts the top of other sprite.
	 */
	public static final int BOTTOM_TOP_COLLISION = 8;
	
	/** ************************* COLLISION EVENTS ****************************** */
	
	/**
	 * The sprite from group 1 in current collision.
	 */
	protected Sprite sprite1;
	
	/**
	 * The sprite from group 2 in current collision.
	 */
	protected Sprite sprite2;
	
	/**
	 * The collision side of current collision.
	 */
	protected int collisionSide;
	
	/**
	 * The <code>x</code> return coordinate of sprite in group 1 in current
	 * collision.
	 */
	protected double collisionX1;
	
	/**
	 * The <code>y</code> return coordinate of sprite in group 1 in current
	 * collision.
	 */
	protected double collisionY1;
	
	/**
	 * The <code>x</code> return coordinate of sprite in group 2 in current
	 * collision.
	 */
	protected double collisionX2;
	
	/**
	 * The <code>y</code> return coordinate of sprite in group 2 in current
	 * collision.
	 */
	protected double collisionY2;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>CollisionGroup</code>.
	 */
	public CollisionGroup() {
	}
	
	/**
	 * Performs collision check between Sprite <code>s1</code> and Sprite
	 * <code>s2</code>, and returns true if the sprites (<code>shape1</code>,
	 * <code>shape2</code>) is collided.
	 * <p>
	 * 
	 * This method is responsible to take care all collision events, such as the
	 * {@linkplain #sprite1 collided sprite} (in this case <code>s1</code> and
	 * <code>s2</code>), the {@linkplain #collisionSide collision side},
	 * position where collision actually occured ({@linkplain #collisionX1 collisionX1},
	 * {@linkplain #collisionY1 collisionY1}).
	 * <p>
	 * 
	 * The collision information is used to return the collided sprite using
	 * {@link #revertPosition1()} and {@link #revertPosition2()} method.
	 * Therefore if the given information is wrong, those two methods will
	 * behave unpredictable.
	 * 
	 * Note: this method do not check active state of the sprites.
	 * <p>
	 * 
	 * @param s1 sprite from group 1
	 * @param s2 sprite from group 2
	 * @param shape1 bounding box of sprite 1
	 * @param shape2 bounding box of sprite 2
	 * @return true, if the sprites is collided one another.
	 * @see #collisionSide
	 * @see #sprite1
	 * @see #collisionX1
	 * @see #collisionY1
	 * @see #revertPosition1()
	 */
	public boolean isCollide(Sprite s1, Sprite s2, CollisionShape shape1, CollisionShape shape2) {
		if (super.isCollide(s1, s2, shape1, shape2)) {
			// collide!!
			this.sprite1 = s1;
			this.sprite2 = s2;
			this.collisionSide = 0;
			
			// set position before collision occured
			// @see revertPositionXX()
			this.collisionX1 = s1.getOldX();
			this.collisionY1 = s1.getOldY();
			
			this.collisionX2 = s2.getOldX();
			this.collisionY2 = s2.getOldY();
			
			final double speedX1 = s1.getX() - this.collisionX1, speedY1 = s1
			        .getY()
			        - this.collisionY1, speedX2 = s2.getX() - this.collisionX2, speedY2 = s2
			        .getY()
			        - this.collisionY2;
			
			// find collision side
			// using collision intersection rectangle
			
			// store old collision position
			double oldx1 = shape1.getX(), oldy1 = shape1.getY(), oldx2 = shape2
			        .getX(), oldy2 = shape2.getY();
			
			// to ensure the position not overlapping
			// we set position back to half (1/2 speed)
			shape1.move(-(speedX1 / 2), -(speedY1 / 2));
			shape2.move(-(speedX2 / 2), -(speedY2 / 2));
			
			if (!shape1.intersects(shape2)) {
				// the ensurement failed!!
				shape1.setLocation(oldx1, oldy1);
				shape2.setLocation(oldx2, oldy2);
				
				// try to set position back to quarter (1/4 speed)
				shape1.move(-(speedX1 / 4), -(speedY1 / 4));
				shape2.move(-(speedX2 / 4), -(speedY2 / 4));
				
				if (!shape1.intersects(shape2)) {
					shape1.setLocation(oldx1, oldy1);
					shape2.setLocation(oldx2, oldy2);
				}
			}
			
			// calculate the intersection rectangle
			CollisionRect iRect = CollisionManager.getIntersectionRect(shape1
			        .getX(), shape1.getY(), shape1.getWidth(), shape1
			        .getHeight(), shape2.getX(), shape2.getY(), shape2
			        .getWidth(), shape2.getHeight());
			
			// calculate collision side using the intersection rect
			if (iRect.width <= iRect.height) {
				// less width, means horizontal collision
				this.collisionSide = (shape1.getX() < shape2.getX()) ? CollisionGroup.RIGHT_LEFT_COLLISION
				        : // sprite 1 is at
				        // left -> its right
				        // is colliding
				        CollisionGroup.LEFT_RIGHT_COLLISION;
				
			}
			else { // otherwise vertical collision
				this.collisionSide = (shape1.getY() < shape2.getY()) ? CollisionGroup.BOTTOM_TOP_COLLISION
				        : // sprite 1 is at
				        // top -> its bottom
				        // is colliding
				        CollisionGroup.TOP_BOTTOM_COLLISION;
			}
			
			return true;
		}
		
		return false;
	}
	
	/** ************************************************************************* */
	/** ************************ COLLISION EVENTS ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Reverts sprite 1 position before the collision occured.
	 */
	public void revertPosition1() {
		if ((this.collisionSide & CollisionGroup.LEFT_RIGHT_COLLISION) != 0
		        || (this.collisionSide & CollisionGroup.RIGHT_LEFT_COLLISION) != 0) {
			this.sprite1.forceX(this.collisionX1);
		}
		if ((this.collisionSide & CollisionGroup.TOP_BOTTOM_COLLISION) != 0
		        || (this.collisionSide & CollisionGroup.BOTTOM_TOP_COLLISION) != 0) {
			this.sprite1.forceY(this.collisionY1);
		}
	}
	
	/**
	 * Reverts sprite 2 position before the collision occured.
	 */
	public void revertPosition2() {
		if ((this.collisionSide & CollisionGroup.LEFT_RIGHT_COLLISION) != 0
		        || (this.collisionSide & CollisionGroup.RIGHT_LEFT_COLLISION) != 0) {
			this.sprite2.forceX(this.collisionX2);
		}
		if ((this.collisionSide & CollisionGroup.TOP_BOTTOM_COLLISION) != 0
		        || (this.collisionSide & CollisionGroup.BOTTOM_TOP_COLLISION) != 0) {
			this.sprite2.forceY(this.collisionY2);
		}
	}
	
	/**
	 * Returns the sprite of current collision from group 1.
	 */
	public Sprite getSprite1() {
		return this.sprite1;
	}
	
	/**
	 * Returns the sprite of current collision from group 2.
	 */
	public Sprite getSprite2() {
		return this.sprite2;
	}
	
	/**
	 * Returns collision side of current collision.
	 */
	public int getCollisionSide() {
		return this.collisionSide;
	}
	
	/**
	 * Prints collision side of current collision to console (for debugging).
	 */
	public void printCollisionSide() {
		String side = "Collision Side ->";
		
		if ((this.collisionSide & CollisionGroup.LEFT_RIGHT_COLLISION) != 0) {
			side += " Left<->Right";
		}
		if ((this.collisionSide & CollisionGroup.RIGHT_LEFT_COLLISION) != 0) {
			side += " Right<->Left";
		}
		if ((this.collisionSide & CollisionGroup.TOP_BOTTOM_COLLISION) != 0) {
			side += " Top<->Bottom";
		}
		if ((this.collisionSide & CollisionGroup.BOTTOM_TOP_COLLISION) != 0) {
			side += " Bottom<->Top";
		}
		
		System.out.println(side);
	}
	
}
