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
 * <code>PlayField</code> class is the game arena where all the game objects
 * are put on. This class manages all objects in the game, such as sprite,
 * background, sprite group, and collision check.
 * <p>
 * 
 * <code>PlayField</code> simplify sprite updating and rendering. <br>
 * By calling {@link #update(long)} all sprites within this playfield will be
 * updated and collision will be check. <br>
 * By calling {@link #render(Graphics2D)} all sprites will be rendered to the
 * screen.
 * 
 * @see com.golden.gamedev.object.SpriteGroup
 * @see com.golden.gamedev.object.CollisionManager
 */
public class PlayField {
	
	/** ********************** PLAYFIELD PROPERTIES ***************************** */
	
	private SpriteGroup[] groups;
	private Background background;
	private CollisionManager[] collisions;
	
	/** ************************** SORT RENDERING ******************************* */
	
	private Sprite[] cacheSprite;
	private Comparator comparator;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Constructs new <code>PlayField</code> with specified background.
	 */
	public PlayField(Background background) {
		this.background = background;
		
		// preserve one group for the extra group
		SpriteGroup extra = new SpriteGroup("Extra Group");
		extra.setBackground(background);
		
		this.groups = new SpriteGroup[1];
		this.groups[0] = extra;
		
		this.collisions = new CollisionManager[0];
		this.cacheSprite = new Sprite[0];
	}
	
	/**
	 * Constructs new <code>PlayField</code> with
	 * {@link Background#getDefaultBackground() default background}.
	 */
	public PlayField() {
		this(Background.getDefaultBackground());
	}
	
	/** ************************************************************************* */
	/** ********************* SPRITE GROUP OPERATION **************************** */
	/** ************************************************************************* */
	
	/**
	 * Inserts a sprite (extra sprite) directly into playfield, for example
	 * animation, explosion, etc.
	 * <p>
	 * 
	 * This method is a convenient way to add sprites directly into screen
	 * without have to creates new {@link SpriteGroup}.
	 * <p>
	 * 
	 * The sprite is inserted to 'extra group' and all sprites on extra group
	 * will always on top of other sprites.
	 */
	public void add(Sprite extra) {
		this.groups[this.groups.length - 1].add(extra);
	}
	
	/**
	 * Inserts new <code>SpriteGroup</code> into this playfield. This method
	 * returned object reference of the inserted group.
	 * <p>
	 * 
	 * The returned group used to reduce code and simplicity. <br>
	 * For example :
	 * 
	 * <pre>
	 * Playfield playfield = new Playfield();
	 * SpriteGroup PLAYER = playfield.addGroup(new SpriteGroup(&quot;Player&quot;));
	 * </pre>
	 * 
	 * If there is no returned reference, we must set the sprite group and add
	 * it manually into playfield :
	 * 
	 * <pre>
	 * SpriteGroup PLAYER = new SpriteGroup(&quot;Player&quot;);
	 * Playfield playfield = new Playfield();
	 * playfield.addGroup(PLAYER);
	 * </pre>
	 * 
	 * @param group sprite group to be inserted into this playfield
	 * @return Reference of the inserted sprite group.
	 */
	public SpriteGroup addGroup(SpriteGroup group) {
		// extra group always at behind!
		SpriteGroup extra = this.groups[this.groups.length - 1];
		this.groups = (SpriteGroup[]) Utility.cut(this.groups,
		        this.groups.length - 1);
		
		this.groups = (SpriteGroup[]) Utility.expand(this.groups, 2);
		group.setBackground(this.background);
		this.groups[this.groups.length - 2] = group;
		this.groups[this.groups.length - 1] = extra; // move extra group to
		// the last row
		
		return group;
	}
	
	/**
	 * Removes specified sprite group from this playfield.
	 * 
	 * @param group sprite group to be removed from this playfield
	 * @return true, if the sprite group is successfuly removed.
	 */
	public boolean removeGroup(SpriteGroup group) {
		// last group is exclusive for extra group
		// it can't be removed!
		for (int i = 0; i < this.groups.length - 1; i++) {
			if (this.groups[i] == group) {
				this.groups = (SpriteGroup[]) Utility.cut(this.groups, i);
				
				// sprite group has been removed
				// therefore, any collision group registered
				// with it should be removed too!
				CollisionManager collisionGroup = this.getCollisionGroup(group);
				if (collisionGroup != null) {
					do {
						this.removeCollisionGroup(collisionGroup);
						collisionGroup = this.getCollisionGroup(group);
					} while (collisionGroup != null);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns sprite group with specified name associated with this playfield.
	 */
	public SpriteGroup getGroup(String name) {
		for (int i = 0; i < this.groups.length; i++) {
			if (this.groups[i].getName().equals(name)) {
				return this.groups[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns all sprite group associated with this playfield.
	 */
	public SpriteGroup[] getGroups() {
		return this.groups;
	}
	
	/**
	 * Returns this playfield extra sprite group.
	 * <p>
	 * 
	 * Extra sprite group is preserve group that always in front of other
	 * groups, usually used to hold game animation such as explosion.
	 * <p>
	 * 
	 * This group also exists for convenient way to add sprite into playfield
	 * without creating sprite group.
	 * 
	 * @see #add(Sprite)
	 */
	public SpriteGroup getExtraGroup() {
		return this.groups[this.groups.length - 1];
	}
	
	/**
	 * Clears all sprites in this playfield and makes this playfield empty.
	 * <p>
	 * 
	 * This method iterates all groups in this playfield and remove all sprites
	 * inside it by calling
	 * {@link com.golden.gamedev.object.SpriteGroup#clear()}
	 */
	public void clearPlayField() {
		for (int i = 0; i < this.groups.length; i++) {
			this.groups[i].clear();
		}
	}
	
	/** ************************************************************************* */
	/** ****************** COLLISION GROUP OPERATION **************************** */
	/** ************************************************************************* */
	
	/**
	 * Associates specified collision group to this playfield.
	 */
	public void addCollisionGroup(SpriteGroup group1, SpriteGroup group2, CollisionManager collisionGroup) {
		// ensure group1 and group2 is not registered yet
		if (this.getCollisionGroup(group1, group2) != null) {
			System.err.println("WARNING: " + group1.getName() + " <-> "
			        + group2.getName() + " already have a CollisionManager");
			System.err.println("CollisionGroup insertions operation continued");
		}
		
		this.collisions = (CollisionManager[]) Utility.expand(this.collisions,
		        1);
		collisionGroup.setCollisionGroup(group1, group2);
		this.collisions[this.collisions.length - 1] = collisionGroup;
	}
	
	/**
	 * Removes specified collision group from this playfield.
	 * 
	 * @return true, if the collision group is successfully removed.
	 */
	public boolean removeCollisionGroup(CollisionManager collisionGroup) {
		for (int i = 0; i < this.collisions.length; i++) {
			if (this.collisions[i] == collisionGroup) {
				this.collisions = (CollisionManager[]) Utility.cut(
				        this.collisions, i);
				return true; // successfully removed
			}
		}
		
		return false;
	}
	
	/**
	 * Returns associated collision group that checking collision of
	 * <code>group1</code> and <code>group2</code>, or null if requested
	 * collision group can not be found.
	 * 
	 * @param group1 the first group of the collision group to be find
	 * @param group2 the second group of the collision group to be find
	 * @return CollisionGroup that checks group1 and group2 for collision, or
	 *         null if no collision group can be found.
	 */
	public CollisionManager getCollisionGroup(SpriteGroup group1, SpriteGroup group2) {
		for (int i = 0; i < this.collisions.length; i++) {
			if (this.collisions[i].getGroup1() == group1
			        && this.collisions[i].getGroup2() == group2) {
				return this.collisions[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns any collision group associated with specified sprite group.
	 */
	public CollisionManager getCollisionGroup(SpriteGroup group) {
		for (int i = 0; i < this.collisions.length; i++) {
			if (this.collisions[i].getGroup1() == group
			        || this.collisions[i].getGroup2() == group) {
				return this.collisions[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns all collision group associated with this playfield.
	 */
	public CollisionManager[] getCollisionGroups() {
		return this.collisions;
	}
	
	/** ************************************************************************* */
	/** *********************** UPDATE PLAYFIELD ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Updates sprites, background, and check for collisions.
	 */
	public void update(long elapsedTime) {
		this.updateSpriteGroups(elapsedTime);
		this.updateBackground(elapsedTime);
		
		this.checkCollisions();
	}
	
	/**
	 * Updates playfield background.
	 */
	protected void updateBackground(long elapsedTime) {
		this.background.update(elapsedTime);
	}
	
	/**
	 * Updates sprites in sprite groups on this playfield.
	 */
	protected void updateSpriteGroups(long elapsedTime) {
		for (int i = 0; i < this.groups.length; i++) {
			if (this.groups[i].isActive()) {
				this.groups[i].update(elapsedTime);
			}
		}
	}
	
	/**
	 * Checks for collision event.
	 */
	protected void checkCollisions() {
		for (int i = 0; i < this.collisions.length; i++) {
			if (this.collisions[i].isActive()) {
				this.collisions[i].checkCollision();
			}
		}
	}
	
	/** ************************************************************************* */
	/** *********************** RENDER PLAYFIELD ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Renders background, and sprite groups (with/without
	 * {@linkplain #setComparator(Comparator) comparator}).
	 */
	public void render(Graphics2D g) {
		this.renderBackground(g);
		
		if (this.comparator == null) {
			this.renderSpriteGroups(g);
			
		}
		else {
			this.renderSpriteGroups(g, this.comparator);
		}
	}
	
	/**
	 * Renders background to specified graphics context.
	 */
	protected void renderBackground(Graphics2D g) {
		this.background.render(g);
	}
	
	/**
	 * Renders sprite groups to specified graphics context.
	 */
	protected void renderSpriteGroups(Graphics2D g) {
		for (int i = 0; i < this.groups.length; i++) {
			if (this.groups[i].isActive()) {
				this.groups[i].render(g);
			}
		}
	}
	
	/**
	 * Renders all sprites using specified comparator. Only
	 * {@linkplain Sprite#isActive() active} and
	 * {@linkplain Sprite#isOnScreen() on screen} sprite is sorted and rendered.
	 * <p>
	 * 
	 * Sprites that rendered within this method is stored in cache, therefore if
	 * this method is called ONLY ONCE and then
	 * {@linkplain #setComparator(Comparator) comparator} is set to null, it is
	 * better to manually {@linkplain #clearCache() clear the cache} after it or
	 * the cache will hold the sprites forever and makes the sprite can't be
	 * disposed.
	 */
	protected void renderSpriteGroups(Graphics2D g, Comparator c) {
		int num = 0, len = this.cacheSprite.length;
		
		if (len == 0) {
			// sprite cache initialization
			this.cacheSprite = new Sprite[100];
			len = this.cacheSprite.length;
		}
		
		for (int i = 0; i < this.groups.length; i++) {
			if (!this.groups[i].isActive()) {
				continue;
			}
			
			Sprite[] member = this.groups[i].getSprites();
			int size = this.groups[i].getSize();
			
			for (int j = 0; j < size; j++) {
				if (member[j].isActive() && // only active and onscreen sprite
				        member[j].isOnScreen()) { // is sorted and rendered
				
					if (num >= len) {
						// expand sprite storage
						this.cacheSprite = (Sprite[]) Utility.expand(
						        this.cacheSprite, 20);
						len = this.cacheSprite.length;
					}
					
					this.cacheSprite[num++] = member[j];
				}
			}
		}
		
		// sort all active and onscreen sprites
		Arrays.sort(this.cacheSprite, 0, num, c);
		
		for (int i = 0; i < num; i++) {
			this.cacheSprite[i].render(g);
		}
	}
	
	/**
	 * Clears cache sprite.
	 * <p>
	 * 
	 * Cache sprite is used to keep sprites in sorted order (if this playfield
	 * used an external comparator). <br>
	 * When {@link #renderSpriteGroups(Graphics2D, Comparator)} called, all
	 * sprites are stored in cache and then sorted, Therefore after rendering
	 * done, the cache still remain in memory and wait to be cached again on
	 * next sort rendering.
	 * <p>
	 * 
	 * This method simply clears the cache sprite. Call this method when sort
	 * rendering is not needed anymore.
	 * 
	 * @see #renderSpriteGroups(Graphics2D, Comparator)
	 */
	public void clearCache() {
		this.cacheSprite = null;
		this.cacheSprite = new Sprite[0];
	}
	
	/** ************************************************************************* */
	/** ************************ OTHER FUNCTIONS ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns background associated with this playfield.
	 */
	public Background getBackground() {
		return this.background;
	}
	
	/**
	 * Associates specified background to this playfield.
	 */
	public void setBackground(Background backgr) {
		this.background = backgr;
		if (this.background == null) {
			this.background = Background.getDefaultBackground();
		}
		
		// force all sprites to use same background
		for (int i = 0; i < this.groups.length; i++) {
			this.groups[i].setBackground(backgr);
		}
	}
	
	/**
	 * Returns playfield comparator, comparator is used for sorting the sprites
	 * before rendering.
	 */
	public Comparator getComparator() {
		return this.comparator;
	}
	
	/**
	 * Sets playfield comparator, comparator is used for sorting the sprites
	 * before rendering. Specify null comparator for unsort order (the first
	 * sprite in the array will be rendered at the back of other sprite).
	 * <p>
	 * 
	 * The comparator is used in
	 * {@link java.util.Arrays#sort(java.lang.Object[], int, int, java.util.Comparator)}
	 * from the java.lang package to sort the sprites, for more information
	 * about how to make comparator, please read java.util.Comparator and
	 * java.util.Arrays#sort().
	 * 
	 * Example of sorting sprites based on y-axis :
	 * 
	 * <pre>
	 *    PlayField playfield;
	 *    playfield.setComparator(
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
	
}
