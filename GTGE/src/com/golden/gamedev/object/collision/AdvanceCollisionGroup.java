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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;
import com.golden.gamedev.util.Utility;

/**
 * Accurates collision check that able to check collision between one sprite
 * with many sprites (one to many collision check).
 * <p>
 * 
 * This collision check is the most advance among all other collision check, it
 * checking is the collision really occured or not.
 * <p>
 * 
 * For example: <br>
 * A sprite is checked its collision with three other sprites, if the checking
 * only based on the sprite position, the sprite is collided with the three
 * other sprites. But if the sprite is collided with the first sprite and stop,
 * the sprite actually only collide with the first, because the second and the
 * third is right behind the first sprite.
 * <p>
 * 
 * This collision group is suitable for collision that using physics heavily,
 * like in platformer game, where a sprite could collide with many blocks at one
 * time.
 */
public abstract class AdvanceCollisionGroup extends PreciseCollisionGroup
        implements Comparator {
	
	private final Map storage = new HashMap();
	
	private Sprite source;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Constructs new <code>AdvanceCollisionGroup</code>.
	 */
	public AdvanceCollisionGroup() {
	}
	
	/** ************************************************************************* */
	/** ****************** MAIN-METHOD: CHECKING COLLISION ********************** */
	/** ************************************************************************* */
	
	public void checkCollision() {
		// clear previous collision event
		this.storage.clear();
		
		// the usual collision check
		SpriteGroup group1 = this.getGroup1(), group2 = this.getGroup2();
		if (!group1.isActive() || !group2.isActive()) {
			// one of the group is not active
			return;
		}
		
		Sprite[] member1 = group1.getSprites(), // members group one
		member2 = group2.getSprites();
		int size1 = group1.getSize(), // total non-null members
		size2 = group2.getSize();
		
		Sprite sprite1, sprite2; // sprite reference
		CollisionShape shape1, shape2; // sprite collision rect
		
		// sprite 1, 2 collision rectangle -> rect1, rect2
		for (int i = 0; i < size1; i++) {
			sprite1 = member1[i];
			
			if (!sprite1.isActive()
			        || (shape1 = this.getCollisionShape1(sprite1)) == null) {
				// sprite do not want collision check
				continue;
			}
			
			for (int j = 0; j < size2; j++) {
				sprite2 = member2[j];
				
				if (!sprite2.isActive()
				        || // !sprite1.isActive() ||
				        sprite1 == sprite2
				        || (shape2 = this.getCollisionShape2(sprite2)) == null) {
					// sprite do not want collision check
					continue;
				}
				
				if (this.isCollide(sprite1, sprite2, shape1, shape2)) {
					// collects all collided sprites event into
					// our storage object
					
					// store sprites collided with sprite1
					Sprite[] other = (Sprite[]) this.storage.get(sprite1);
					
					other = (Sprite[]) Utility.expand(other, 1, true,
					        Sprite.class);
					other[other.length - 1] = sprite2;
					
					this.storage.put(sprite1, other);
				}
			}
		}
		
		// now it's time to check the actual collision
		Iterator key = this.storage.keySet().iterator();
		
		while (key.hasNext()) {
			Sprite s1 = (Sprite) key.next();
			Sprite[] s2 = (Sprite[]) this.storage.get(s1);
			
			if (s2.length == 1) {
				// sprite s1 collide with only 'one' other sprite
				// so there should be no problem
				
				// need to reset revert positions- this is left at last
				// test,
				this.isCollide(s1, s2[0], this.getCollisionShape1(s1), this
				        .getCollisionShape2(s2[0]));
				
				// fire collision event
				this.collided(s1, s2[0]);
				
				// continue to the next sprite
				continue;
			}
			
			// sort all collided sprites
			this.source = s1;
			if (this.sort(this.source)) {
				Arrays.sort(s2, this);
			}
			
			// in here the collision event is really fired to the listener
			for (int i = 0; i < s2.length; i++) {
				if (!s1.isActive()) {
					break;
				}
				
				// System.out.println("Start "+s2.length);
				
				if (s2[i].isActive()) {
					
					// System.out.print(s2[i]+"
					// "+s2[i].getX()+","+s2[i].getY()+"--");
					
					if (this.isCollide(s1, s2[i], this.getCollisionShape1(s1),
					        this.getCollisionShape2(s2[i]))) {
						// fire collision event
						// System.out.print("Yes");
						this.collided(s1, s2[i]);
					}
					// System.out.println();
				}
				// System.out.println("End\n");
			}
		}
	}
	
	/**
	 * Determines all sprites that collided with Sprite <code>source</code>
	 * should be sorted or not before checking the actual collision.
	 * <p>
	 * 
	 * By default this method return true in order to sort the collided sprites.
	 * 
	 * @return true, collided sprites will be sorted before checking the actual
	 *         collision.
	 * @see #compare(Object, Object)
	 */
	protected boolean sort(Sprite source) {
		return true;
	}
	
	/**
	 * Sorts two sprites (<code>o1</code> and <code>o2</code>) that
	 * collided with {@linkplain #getSourceSprite() the object sprite} to
	 * determine which one should be checked first.
	 * <p>
	 * 
	 * By default when the {@linkplain #getSourceSprite() object sprite} is
	 * falling ({@linkplain Sprite#getVerticalSpeed() vertical speed} >= 0),
	 * all collided sprites are sorted by greater y at bottom, otherwise it sort
	 * by greater y at top.
	 */
	public int compare(Object o1, Object o2) {
		Sprite s1 = (Sprite) o1, s2 = (Sprite) o2;
		
		if (this.source.getHorizontalSpeed() != 0 && s1.getX() != s2.getX()) {
			// source not stationary and s1 x the same as s2 x
			return (this.source.getHorizontalSpeed() >= 0) ? // if source
			// heading right
			(int) Math.floor(s1.getX() - s2.getX())
			        : // most lefterly
			        (int) Math.floor(s2.getX() - s1.getX());// most righterly
		}
		
		// // sort by sprite y position
		// return (source.getVerticalSpeed() >= 0) ?
		// (int) Math.ceil(s1.getY() - s2.getY()) :
		// (int) Math.ceil(s2.getY() - s1.getY());
		
		// sort by sprite y position
		return (this.source.getVerticalSpeed() >= 0) ? (int) Math.floor(s1
		        .getY()
		        - s2.getY()) : (int) Math.floor(s2.getY() - s1.getY());
		
	}
	
	/**
	 * Returns the source sprite to be checked at the moment.
	 * <p>
	 * 
	 * Source sprite is the sprite from group 1 that will be checked its actual
	 * collision with sprite from group 2.
	 */
	protected Sprite getSourceSprite() {
		return this.source;
	}
	
	/**
	 * Returns collided sprites storage.
	 * <p>
	 * 
	 * Mapping a sprite with its collided sprites.
	 */
	public Map getStorage() {
		return this.storage;
	}
	
}
