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
 * Basic collision check, only check whether a collision occured or not.
 * <p>
 * 
 * This class does not gather any information from the collision. To get more
 * information from the collision, such as collision side, use
 * {@link CollisionGroup} instead.
 * <p>
 * 
 * This type of collision check is the best to use for hit and destroy
 * collision.
 * <p>
 * 
 * For example: collision between projectile and enemy ships
 * 
 * <pre>
 * Playfield playfield;
 * SpriteGroup PROJECTILE, ENEMY;
 * playfield.addCollisionGroup(PROJECTILE, ENEMY, new BasicCollisionGroup() {
 * 	
 * 	public void collided(Sprite s1, Sprite s2) {
 * 		// after enemy collided with projectile,
 * 		// the enemy explode (set to non-active)
 * 		s2.setActive(false);
 * 	}
 * });
 * </pre>
 * 
 * @see PlayField#addCollisionGroup(SpriteGroup, SpriteGroup, CollisionManager)
 */
public abstract class BasicCollisionGroup extends CollisionManager {
	
	// sprite bounding box
	/**
	 * Default collision shape used as every sprites in group 1 bounding box.
	 */
	protected final CollisionRect rect1 = new CollisionRect();
	
	/**
	 * Default collision shape used as every sprites in group 2 bounding box.
	 */
	protected final CollisionRect rect2 = new CollisionRect();
	
	/**
	 * Indicates whether this collision detection should use pixel-perfect
	 * precision or not.
	 * <p>
	 * 
	 * The usual way to turn on this variable is :
	 * 
	 * <pre>
	 * class ThisThatCollision extends BasicCollisionGroup {
	 * 	
	 * 	// class initialization
	 * 	{
	 * 		pixelPerfectCollision = true;
	 * 	}
	 * }
	 * </pre>
	 */
	public boolean pixelPerfectCollision;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>BasicCollisionGroup</code>.
	 */
	public BasicCollisionGroup() {
	}
	
	/**
	 * Returns collision shape (bounding box) of specified sprite from group 1.
	 * <p>
	 * 
	 * In this implementation, the sprite bounding box is set as large as
	 * <code>Sprite</code> dimension:
	 * 
	 * <pre>
	 * public CollisionShape getCollisionRect1(Sprite s1) {
	 * 	rect1.setBounds(s1.getX(), s1.getY(), s1.getWidth(), s1.getHeight());
	 * 	return rect1;
	 * }
	 * </pre>
	 * 
	 * @param s1 the sprite from group 1 to be check its collision
	 * @return The collision shape of the sprite, or null to skip collision
	 *         check.
	 * 
	 * @see #rect1
	 * @see #getCollisionShape2(Sprite)
	 * @see CollisionShape#intersects(CollisionShape)
	 */
	public CollisionShape getCollisionShape1(Sprite s1) {
		this.rect1.setBounds(s1.getX(), s1.getY(), s1.getWidth(), s1
		        .getHeight());
		
		return this.rect1;
	}
	
	/**
	 * Returns collision shape (bounding box) of specified sprite from group 2.
	 * <p>
	 * 
	 * In this implementation, the sprite bounding box is set as large as
	 * <code>Sprite</code> dimension:
	 * 
	 * <pre>
	 * public CollisionShape getCollisionRect2(Sprite s2) {
	 * 	rect2.setBounds(s2.getX(), s2.getY(), s2.getWidth(), s2.getHeight());
	 * 	return rect2;
	 * }
	 * </pre>
	 * 
	 * @param s2 the sprite from group 2 to be check its collision
	 * @return The collision shape of the sprite, or null to skip collision
	 *         check.
	 * 
	 * @see #rect2
	 * @see #getCollisionShape1(Sprite)
	 * @see CollisionRect#intersects(CollisionShape)
	 */
	public CollisionShape getCollisionShape2(Sprite s2) {
		this.rect2.setBounds(s2.getX(), s2.getY(), s2.getWidth(), s2
		        .getHeight());
		
		return this.rect2;
	}
	
	/** ************************************************************************* */
	/** ****************** MAIN-METHOD: CHECKING COLLISION ********************** */
	/** ************************************************************************* */
	
	public void checkCollision() {
		SpriteGroup group1 = this.getGroup1(), group2 = this.getGroup2();
		if (!group1.isActive() || !group2.isActive()) {
			// one of the group is not active
			return;
		}
		
		Sprite[] member1 = group1.getSprites(), // group one members
		member2 = group2.getSprites();
		int size1 = group1.getSize(), // size of non-null members
		size2 = group2.getSize();
		
		Sprite sprite1, sprite2; // sprite reference
		CollisionShape shape1, shape2; // sprite collision rect
		
		for (int i = 0; i < size1; i++) {
			sprite1 = member1[i];
			
			if (!sprite1.isActive()
			        || (shape1 = this.getCollisionShape1(sprite1)) == null) {
				// sprite do not want collision check
				continue;
			}
			
			for (int j = 0; j < size2; j++) {
				sprite2 = member2[j];
				
				if (!sprite2.isActive() || sprite1 == sprite2
				        || (shape2 = this.getCollisionShape2(sprite2)) == null) {
					// sprite do not want collision check
					continue;
				}
				
				if (this.isCollide(sprite1, sprite2, shape1, shape2)) {
					// fire collision event
					this.collided(sprite1, sprite2);
					
					// size1 = group1.getSize();
					// size2 = group2.getSize();
					
					if (!sprite1.isActive()
					        || (shape1 = this.getCollisionShape1(sprite1)) == null) {
						// collided sprite has been dead
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Performs collision check between Sprite <code>s1</code> and Sprite
	 * <code>s2</code>, and returns true if the sprites (<code>shape1</code>,
	 * <code>shape2</code>) is collided.
	 * <p>
	 * 
	 * Note: this method do not check active state of the sprites.
	 * 
	 * @param s1 sprite from group 1
	 * @param s2 sprite from group 2
	 * @param shape1 bounding box of sprite 1
	 * @param shape2 bounding box of sprite 2
	 * @return true, if the sprites is collided one another.
	 */
	public boolean isCollide(Sprite s1, Sprite s2, CollisionShape shape1, CollisionShape shape2) {
		if (!this.pixelPerfectCollision) {
			return (shape1.intersects(shape2));
			
		}
		else {
			if (shape1.intersects(shape2)) {
				return CollisionManager.isPixelCollide(s1.getX(), s1.getY(), s1
				        .getImage(), s2.getX(), s2.getY(), s2.getImage());
			}
			
			return false;
		}
	}
	
	/**
	 * Notified when <code>sprite1</code> from group 1 collided with
	 * <code>sprite2</code> from group 2.
	 * 
	 * @param s1 sprite from group 1
	 * @param s2 sprite from group 2
	 */
	public abstract void collided(Sprite s1, Sprite s2);
	
}
