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
import java.util.Arrays;
import java.util.Comparator;

import com.golden.gamedev.util.Utility;

/**
 * Group of sprites with common behaviour, for example PLAYER_GROUP,
 * ENEMY_GROUP, etc. This class maintain a growable sprite list (array of
 * sprites). Each time a sprite is added into this group, this group
 * automatically adjust the size of its sprites array.
 * <p>
 * 
 * <code>SpriteGroup</code> is used to store a list of sprites and also manage
 * the sprites updating, rendering, and collision check.
 * <p>
 * 
 * For example how to create and use sprite group :
 * 
 * <pre>
 * SpriteGroup ENEMY_GROUP;
 * 
 * public void initResources() {
 * 	// creates the enemy sprites
 * 	Sprite asteroid = new Sprite();
 * 	Sprite asteroid2 = new Sprite();
 * 	// creates and add the sprites into enemy group
 * 	ENEMY_GROUP = new SpriteGroup(&quot;Enemy&quot;);
 * 	ENEMY_GROUP.add(asteroid);
 * 	ENEMY_GROUP.add(asteroid2);
 * }
 * 
 * public void update(long elapsedTime) {
 * 	// update all enemies at once
 * 	ENEMY_GROUP.update(elapsedTime);
 * }
 * 
 * public void render(Graphics2D g) {
 * 	// render all enemies at once to the screen
 * 	ENEMY_GROUP.render(g);
 * }
 * </pre>
 * 
 * @see com.golden.gamedev.object.PlayField
 * @see com.golden.gamedev.object.collision.CollisionGroup
 */
public class SpriteGroup {
	
	/** *********************** GROUP SPRITE FACTOR ***************************** */
	
	// total 'empty' sprite (NULL sprite, allocation only)
	// reduce the cost of array enlargement operation
	private int expandFactor = 20;
	
	// removes inactive sprites every 15 seconds
	private Timer scanFrequence = new Timer(15000);
	
	/** ************************ GROUP PROPERTIES ******************************* */
	
	private String name; // group name (for identifier only)
	private boolean active = true;
	
	private Background background;
	
	private Comparator comparator; // comparator for sorting sprite
	
	/** ****************** SPRITES THAT BELONG TO THIS GROUP ******************** */
	
	private Sprite[] sprites; // member of this group
	private int size; // all non-null sprites (active + inactive)
	
	/** ************************************************************************* */
	/** ************************** CONSTRUCTOR ********************************** */
	/** ************************************************************************* */
	
	/**
	 * Creates a new sprite group, with specified name. Name is used for group
	 * identifier only.
	 */
	public SpriteGroup(String name) {
		this.name = name;
		this.background = Background.getDefaultBackground();
		
		this.sprites = new Sprite[this.expandFactor];
	}
	
	/** ************************************************************************* */
	/** ************************* INSERTION OPERATION *************************** */
	/** ************************************************************************* */
	
	/**
	 * Inserts sprite at the bottom (last index) of this group.
	 * <p>
	 * 
	 * Sprite at the last index (index = {@linkplain #getSize() size}-1) is
	 * rendered on top of other sprites (last rendered).
	 * 
	 * @see #add(int, Sprite)
	 */
	public void add(Sprite member) {
		this.sprites[this.size] = member;
		member.setBackground(this.background);
		
		if (++this.size >= this.sprites.length) {
			// time to enlarge sprite storage
			this.sprites = (Sprite[]) Utility.expand(this.sprites,
			        this.expandFactor);
		}
	}
	
	/**
	 * Inserts sprite at specified index, range from [0 -
	 * {@linkplain #getSize() size}].
	 * <p>
	 * 
	 * Sprite at the first index (index = 0) is at the back of other sprites
	 * (first rendered). <br>
	 * Sprite at the last index (index = {@linkplain #getSize() size}-1) is
	 * rendered on top of other sprites (last rendered).
	 */
	public void add(int index, Sprite member) {
		if (index > this.size) {
			index = this.size;
		}
		
		if (index == this.size) {
			this.add(member);
			
		}
		else {
			// shift sprites by one at specified index
			System.arraycopy(this.sprites, index, this.sprites, index + 1,
			        this.size - index);
			// for (int i=size-1;i >= index;i--) {
			// sprites[i+1] = sprites[i];
			// }
			this.sprites[index] = member;
			member.setBackground(this.background);
			
			if (++this.size >= this.sprites.length) {
				// time to enlarge sprite storage
				this.sprites = (Sprite[]) Utility.expand(this.sprites,
				        this.expandFactor);
			}
		}
	}
	
	/**
	 * Removes sprite at specified index from this group.
	 * <p>
	 * 
	 * This method has a big performance hit, <b>avoid</b> using this method in
	 * tight-loop (main-loop). <br>
	 * The standard way to remove a sprite from its group is by setting sprite
	 * active state to false
	 * {@link com.golden.gamedev.object.Sprite#setActive(boolean) Sprite.setActive(false)}.
	 * <p>
	 * 
	 * SpriteGroup is designed to remove any inactive sprites automatically
	 * after a period, use directly sprite removal method only for specific
	 * purpose (if you really know what you are doing).
	 * 
	 * @see com.golden.gamedev.object.Sprite#setActive(boolean)
	 * @see #getScanFrequence()
	 */
	public Sprite remove(int index) {
		Sprite removedSprite = this.sprites[index];
		
		int numMoved = this.size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(this.sprites, index + 1, this.sprites, index,
			        numMoved);
		}
		this.sprites[--this.size] = null;
		
		return removedSprite;
	}
	
	/**
	 * Removes specified sprite from this group.
	 * <p>
	 * 
	 * This method has a big performance hit, <b>avoid</b> using this method in
	 * tight-loop (main-loop). <br>
	 * The standard way to remove a sprite from its group is by setting sprite
	 * active state to false
	 * {@link com.golden.gamedev.object.Sprite#setActive(boolean) Sprite.setActive(false)}.
	 * <p>
	 * 
	 * SpriteGroup is designed to remove any inactive sprites automatically
	 * after a period, use directly sprite removal method only for specific
	 * purpose (if you really know what you are doing).
	 * 
	 * @return true, if specified sprite is successfuly removed from the group,
	 *         or false if the sprite is not belong to this group.
	 * @see com.golden.gamedev.object.Sprite#setActive(boolean)
	 * @see #getScanFrequence()
	 */
	public boolean remove(Sprite s) {
		for (int i = 0; i < this.size; i++) {
			if (this.sprites[i] == s) {
				this.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes all members from this group, thus makes this group empty.
	 * <p>
	 * 
	 * For example: <br>
	 * Destroying all enemies when player got a bomb.
	 * 
	 * <pre>
	 * ENEMY_GROUP.clear();
	 * </pre>
	 * 
	 * <p>
	 * 
	 * This method simply set group size to nil. The sprites reference is
	 * actually removed when {@link #removeInactiveSprites()} is scheduled.
	 * <p>
	 * 
	 * To remove all sprites and also its reference immediately, use
	 * {@link #reset()} instead.
	 * 
	 * @see #reset()
	 */
	public void clear() {
		this.size = 0;
	}
	
	/**
	 * Removes all group members, same with {@link #clear()}, except this
	 * method also removes sprite memory reference immediately.
	 * <p>
	 * 
	 * Use this method if only the size of the removed sprites is taking too big
	 * memory and you need to reclaim the used memory immediately.
	 * 
	 * @see #clear()
	 */
	public void reset() {
		this.sprites = null;
		this.sprites = new Sprite[this.expandFactor];
		this.size = 0;
	}
	
	/** ************************************************************************* */
	/** ************************* UPDATE THIS GROUP ***************************** */
	/** ************************************************************************* */
	
	/**
	 * Updates all active sprites in this group, and check the schedule for
	 * removing inactive sprites.
	 * 
	 * @see #getScanFrequence()
	 */
	public void update(long elapsedTime) {
		for (int i = 0; i < this.size; i++) {
			if (this.sprites[i].isActive()) {
				this.sprites[i].update(elapsedTime);
			}
		}
		
		if (this.scanFrequence.action(elapsedTime)) {
			// remove all inactive sprites
			this.removeInactiveSprites();
		}
	}
	
	/**
	 * Throws any inactive sprites from this group, this method won't remove
	 * immutable sprites, to remove all inactive sprites even though the
	 * inactive sprites are immutable use {@link #removeImmutableSprites()}.
	 * <p>
	 * 
	 * This method is automatically called every time
	 * {@linkplain #getScanFrequence() timer for scanning inactive sprite} is
	 * scheduled.
	 * 
	 * @see #getScanFrequence()
	 * @see #removeImmutableSprites()
	 * @see com.golden.gamedev.object.Sprite#setImmutable(boolean)
	 */
	public void removeInactiveSprites() {
		this.removeSprites(false);
	}
	
	/**
	 * Throws all inactive sprites from this group even the sprite is
	 * {@link com.golden.gamedev.object.Sprite#setImmutable(boolean) immutable
	 * sprite}.
	 * 
	 * @see #getInactiveSprite()
	 * @see #removeInactiveSprites()
	 * @see com.golden.gamedev.object.Sprite#setImmutable(boolean)
	 */
	public void removeImmutableSprites() {
		this.removeSprites(true);
	}
	
	private void removeSprites(boolean removeImmutable) {
		int i = 0;
		int removed = 0;
		while (i < this.size) {
			// check for inactive sprite in range
			if (removeImmutable == false) {
				// do not remove immutable sprites
				while (i + removed < this.size
				        && (this.sprites[i + removed].isActive() == false && this.sprites[i
				                + removed].isImmutable() == false)) {
					removed++;
				}
				
			}
			else {
				// remove all inactive sprites include immutable ones
				while (i + removed < this.size
				        && this.sprites[i + removed].isActive() == false) {
					removed++;
				}
			}
			
			if (removed > 0) {
				this.removeRange(i, i + removed);
				removed = 0;
			}
			
			i++;
		}
		
		if (this.sprites.length > this.size + (this.expandFactor * 2)) {
			// shrink sprite array
			Sprite[] dest = new Sprite[this.size + this.expandFactor];
			System.arraycopy(this.sprites, 0, dest, 0, this.size);
			this.sprites = dest;
		}
	}
	
	private void removeRange(int fromIndex, int toIndex) {
		int numMoved = this.size - toIndex;
		System.arraycopy(this.sprites, toIndex, this.sprites, fromIndex,
		        numMoved);
		
		// let gc do its work
		int newSize = this.size - (toIndex - fromIndex);
		while (this.size != newSize) {
			this.sprites[--this.size] = null;
		}
	}
	
	/** ************************************************************************* */
	/** ******************** RENDER TO GRAPHICS CONTEXT ************************* */
	/** ************************************************************************* */
	
	/**
	 * Renders all active sprites in this group. If this group is associated
	 * with a comparator, the group sprites is sort against the comparator first
	 * before rendered.
	 * 
	 * @see #setComparator(Comparator)
	 */
	public void render(Graphics2D g) {
		if (this.comparator != null) {
			// sort sprite before render
			this.sort(this.comparator);
		}
		
		for (int i = 0; i < this.size; i++) {
			if (this.sprites[i].isActive()) {
				// renders only active sprite
				this.sprites[i].render(g);
			}
		}
	}
	
	/**
	 * Sorts all sprites in this group with specified comparator.
	 * <p>
	 * 
	 * This method only sort the sprites once called, use
	 * {@link #setComparator(Comparator)} instead to sort the sprites on each
	 * update.
	 * 
	 * @see #setComparator(Comparator)
	 */
	public void sort(Comparator c) {
		Arrays.sort(this.sprites, 0, this.size, c);
	}
	
	/** ************************************************************************* */
	/** ************************** GROUP PROPERTIES ***************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns the name of this group. Name is used for group identifier only.
	 * 
	 * @see #setName(String)
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name of this group. Name is used for group identifier only.
	 * 
	 * @see #getName()
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the background of this group.
	 * 
	 * @see #setBackground(Background)
	 */
	public Background getBackground() {
		return this.background;
	}
	
	/**
	 * Associates specified background with this sprite group, the background
	 * will be used by all sprites in this group.
	 * 
	 * @see #getBackground()
	 */
	public void setBackground(Background backgr) {
		this.background = backgr;
		if (this.background == null) {
			this.background = Background.getDefaultBackground();
		}
		
		// force all sprites to use a same background
		for (int i = 0; i < this.size; i++) {
			this.sprites[i].setBackground(this.background);
		}
	}
	
	/**
	 * Returns active state of this group.
	 * 
	 * @see #setActive(boolean)
	 */
	public boolean isActive() {
		return this.active;
	}
	
	/**
	 * Sets active state of this group, inactive group won't be updated,
	 * rendered, and won't be checked for collision.
	 * <p>
	 * 
	 * @see #isActive()
	 */
	public void setActive(boolean b) {
		this.active = b;
	}
	
	/**
	 * Returns comparator used for sorting sprites in this group.
	 * 
	 * @see #setComparator(Comparator)
	 */
	public Comparator getComparator() {
		return this.comparator;
	}
	
	/**
	 * Sets comparator used for sorting sprites in this group. Specify null
	 * comparator for unsort order (the first sprite in the array will be
	 * rendered at the back of other sprite).
	 * <p>
	 * 
	 * The comparator is used by
	 * {@link java.util.Arrays#sort(java.lang.Object[], int, int, java.util.Comparator)}
	 * to sort the sprites before rendering. For more information about how to
	 * make comparator, please read java.util.Comparator and
	 * java.util.Arrays#sort().
	 * 
	 * Example of sorting sprites based on y-axis :
	 * 
	 * <pre>
	 *    SpriteGroup ENEMY_GROUP;
	 *    ENEMY_GROUP.setComparator(
	 *       new Comparator() {
	 *          public int compare(Object o1, Object o2) {
	 *             Sprite s1 = (Sprite) o1,
	 *                    s2 = (Sprite) o2;
	 *             return (s1.getY() - s2.getY());
	 *          }
	 *       }
	 *    };
	 * </pre>
	 * 
	 * @param c the sprite comparator, null for unsort order
	 * @see java.util.Comparator
	 * @see java.util.Arrays#sort(java.lang.Object[], int, int,
	 *      java.util.Comparator)
	 */
	public void setComparator(Comparator c) {
		this.comparator = c;
	}
	
	/** ************************************************************************* */
	/** *************************** SPRITES GETTER ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns the first active sprite found in this group, or null if there is
	 * no active sprite.
	 * <p>
	 * 
	 * This method usually used to check whether this group still have alive
	 * member or not. <br>
	 * Note: alive group has different meaning from
	 * {@linkplain #setActive(boolean) active} group, inactive group is not
	 * updated and rendered even there are many active sprites in the group, but
	 * dead group means there are no active sprites in the group.
	 * <p>
	 * 
	 * For example :
	 * 
	 * <pre>
	 * SpriteGroup ENEMY_GROUP;
	 * if (ENEMY_GROUP.getActiveSprite() == null) {
	 * 	// no active enemy, advance to next level
	 * 	gameState = WIN;
	 * }
	 * </pre>
	 * 
	 * @return The first found active sprite, or null if there is no active
	 *         sprite in this group.
	 * @see com.golden.gamedev.object.Sprite#setActive(boolean)
	 */
	public Sprite getActiveSprite() {
		for (int i = 0; i < this.size; i++) {
			if (this.sprites[i].isActive()) {
				return this.sprites[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the first inactive sprite found in this group (the returned
	 * sprite is automatically set to active), or null if there is no inactive
	 * sprite, please see
	 * {@link com.golden.gamedev.object.Sprite#setImmutable(boolean)} for tag
	 * method of this method.
	 * <p>
	 * 
	 * This method is used for optimization, to reuse inactive sprite instead of
	 * making new sprite.
	 * <p>
	 * 
	 * For example :
	 * 
	 * <pre>
	 *    SpriteGroup PROJECTILE_GROUP;
	 *    Sprite projectile = PROJECTILE_GROUP.getInactiveSprite();
	 *    if (projectile == null) {
	 *       // create new projectile if there is no inactive projectile
	 *       projectile = new Sprite(...);
	 *       projectile.setImmutable(true);
	 *       PROJECTILE_GROUP.add(projectile);
	 *    }
	 *    // set projectile location and other stuff
	 *    projectile.setLocation(....);
	 * </pre>
	 * 
	 * <p>
	 * 
	 * This method is only a convenient way to return the first found inactive
	 * sprite. To filter the inactive sprite, simply find and then filter the
	 * inactive sprite like this :
	 * 
	 * <pre>
	 *    SpriteGroup A_GROUP;
	 *    Sprite inactiveSprite = null;
	 *    Sprite[] sprites = A_GROUP.getSprites();
	 *    int size = A_GROUP.getSize();
	 *    for (int i=0;i &lt; size;i++) {
	 *       if (!sprites[i].isActive()) {
	 *          // do the filter
	 *          // for example, we want only reuse sprite that has ID = 100
	 *          if (sprites[i].getID() == 100) {
	 *             inactiveSprite = sprites[i];
	 *             // activate sprite
	 *             inactiveSprite.setActive(true);
	 *             break;
	 *          }
	 *       }
	 *    }
	 *    if (inactiveSprite == null) {
	 *       // no inactive sprite found like the criteria (ID = 100)
	 *       // create new sprite
	 *    }
	 *    // reuse the inactive sprite
	 *    inactiveSprite.setLocation(...);
	 * </pre>
	 * 
	 * 
	 * @return The first found inactive sprite, or null if there is no inactive
	 *         sprite in this group.
	 * @see com.golden.gamedev.object.Sprite#setImmutable(boolean)
	 */
	public Sprite getInactiveSprite() {
		for (int i = 0; i < this.size; i++) {
			if (this.sprites[i].isActive() == false) {
				this.sprites[i].setActive(true);
				return this.sprites[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns all sprites (active, inactive, and also <b>null</b> sprite) in
	 * this group.
	 * <p>
	 * 
	 * How to iterate all sprites :
	 * 
	 * <pre>
	 * SpriteGroup GROUP;
	 * Sprite[] sprites = GROUP.getSprites();
	 * int size = GROUP.getSize();
	 * // iterate the sprite one by one
	 * for (int i = 0; i &lt; size; i++) {
	 * 	// remember the sprite array consists inactive sprites too
	 * 	// you need to check sprite active state before process the sprite
	 * 	if (sprites[i].isActive()) {
	 * 		// now, what do you want with this active sprite?
	 * 		// move it to (0, 0)
	 * 		sprites[i].setLocation(0, 0);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @see #getSize()
	 */
	public Sprite[] getSprites() {
		return this.sprites;
	}
	
	/**
	 * Returns total active and inactive sprites (<b>non-null</b> sprites) in
	 * this group.
	 * 
	 * @see #getSprites()
	 */
	public int getSize() {
		return this.size;
	}
	
	/** ************************************************************************* */
	/** ************************ GROUP FACTOR METHODS *************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns allocation size for empty sprite (null sprite).
	 * 
	 * @see #setExpandFactor(int)
	 */
	public int getExpandFactor() {
		return this.expandFactor;
	}
	
	/**
	 * Sets allocation size for empty sprite (null sprite). This factor is used
	 * only for optimization (reduce the cost of array enlargement operation).
	 * <p>
	 * 
	 * The process : <br>
	 * If there is a new member insertion to the group and the group sprite
	 * array has been full, the array is expanded as large as this factor.
	 * 
	 * For example: <br>
	 * Expand factor is 20 (the default). <br>
	 * {@linkplain #getSize() The group size} is 100. <br>
	 * {@linkplain #getSprites() The group member} is also 100.
	 * <p>
	 * 
	 * If new member is added into this group, the group size is automatically
	 * grow to 120 (100+20). <br>
	 * The new sprite added is at index 101 and the rest is empty sprite (null
	 * sprite).
	 * <p>
	 * 
	 * <b>Note: use large expand factor if there are many sprites inserted into
	 * this group in a period.</b>
	 * 
	 * @see #getExpandFactor()
	 */
	public void setExpandFactor(int factor) {
		this.expandFactor = factor;
	}
	
	/**
	 * Schedule timer for {@linkplain #removeInactiveSprites() removing inactive
	 * sprites}.
	 * <p>
	 * 
	 * For example to set this group to scan inactive sprite every 30 seconds
	 * (the default is 15 seconds) :
	 * 
	 * <pre>
	 * SpriteGroup PLAYER_GROUP;
	 * PLAYER_GROUP.getScanFrequence().setDelay(30000); // 30 x 1000 ms
	 * </pre>
	 * 
	 * @see #removeInactiveSprites()
	 */
	public Timer getScanFrequence() {
		return this.scanFrequence;
	}
	
	public String toString() {
		return super.toString() + " " + "[name=" + this.name + ", active="
		        + this.getSize() + ", total=" + this.sprites.length
		        + ", member=" + this.getActiveSprite() + ", background="
		        + this.background + "]";
	}
	
}
