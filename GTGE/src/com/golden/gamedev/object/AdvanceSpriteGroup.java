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
import java.awt.Insets;
import java.util.Comparator;

/**
 * Subclass of <code>SpriteGroup</code> that designed to update and render
 * visible on the screen sprites only.
 * <p>
 * 
 * In standard sprite group, all registered sprites in the group is updated,
 * rendered, and check for collision in every game loop. If the game has many
 * sprites and many of them are not visible, it is not efficient to update,
 * render, and check for collision all of the sprites.
 * <p>
 * 
 * <code>AdvanceSpriteGroup</code> is designed to optimize the sprite updating
 * and rendering by updating and rendering sprites that only visible on screen,
 * sprites that outside the game view area are not checked.
 * <p>
 * 
 * The main operation is storing sprites that
 * {@linkplain com.golden.gamedev.object.Sprite#isOnScreen() visible on screen}
 * into an inner sprite group, and the inner sprite group that will be update,
 * render, and check for collision.
 */
public class AdvanceSpriteGroup extends SpriteGroup {
	
	/**
	 * Inner sprite group that hold on screen sprites (inside view area sprites)
	 * of this group.
	 */
	protected SpriteGroup ONSCREEN_GROUP;
	
	private Insets offset;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>AdvanceSpriteGroup</code> with specified name, and
	 * specified screen offset on each side.
	 */
	public AdvanceSpriteGroup(String name, int topOffset, int leftOffset,
	        int bottomOffset, int rightOffset) {
		super(name);
		
		this.offset = new Insets(topOffset, leftOffset, bottomOffset,
		        rightOffset);
		
		this.ONSCREEN_GROUP = new SpriteGroup(name + " #ONSCREEN");
		this.ONSCREEN_GROUP.setExpandFactor(50);
		// this group has done the scanning
		// on_screen_group no need to do the scanning anymore
		this.ONSCREEN_GROUP.getScanFrequence().setActive(false);
	}
	
	/**
	 * Creates new <code>AdvanceSpriteGroup</code> with specified name, and
	 * specified screen offset.
	 */
	public AdvanceSpriteGroup(String name, int screenOffset) {
		this(name, screenOffset, screenOffset, screenOffset, screenOffset);
	}
	
	/**
	 * Creates new <code>AdvanceSpriteGroup</code> with specified name without
	 * screen offset (0, 0, 0, 0).
	 */
	public AdvanceSpriteGroup(String name) {
		this(name, 0);
	}
	
	/** ************************************************************************* */
	/** ************************* UPDATE THIS GROUP ***************************** */
	/** ************************************************************************* */
	
	public void update(long elapsedTime) {
		// clear previous on screen sprites
		this.ONSCREEN_GROUP.clear();
		
		// scanning on screen sprites
		Sprite[] s = this.getGroupSprites();
		int size = this.getGroupSize();
		
		for (int i = 0; i < size; i++) {
			if (s[i].isActive()
			        && s[i].isOnScreen(this.offset.left, this.offset.top,
			                this.offset.right, this.offset.bottom)) {
				this.ONSCREEN_GROUP.add(s[i]);
			}
		}
		
		// update only on screen sprites
		this.ONSCREEN_GROUP.update(elapsedTime);
		
		// schedule to scan inactive sprite
		// since we override update(), we must schedule this manually
		// or inactive sprites in this group will never be thrown !!
		if (this.getScanFrequence().action(elapsedTime)) {
			this.removeInactiveSprites();
		}
	}
	
	/** ************************************************************************* */
	/** ******************** RENDER TO GRAPHICS CONTEXT ************************* */
	/** ************************************************************************* */
	
	public void render(Graphics2D g) {
		// render only on screen sprites
		this.ONSCREEN_GROUP.render(g);
	}
	
	/** ************************************************************************* */
	/** ************************** GROUP PROPERTIES ***************************** */
	/** ************************************************************************* */
	
	public void setBackground(Background backgr) {
		super.setBackground(backgr);
		
		this.ONSCREEN_GROUP.setBackground(backgr);
	}
	
	public void setComparator(Comparator c) {
		super.setComparator(c);
		
		this.ONSCREEN_GROUP.setComparator(c);
	}
	
	/**
	 * Returns screen offset of this group. Sprites that outside of screen
	 * bounds that still in this offset still categorized as on screen sprites.
	 */
	public Insets getScreenOffset() {
		return this.offset;
	}
	
	/** ************************************************************************* */
	/** ************************* REMOVAL OPERATION ***************************** */
	/** ************************************************************************* */
	
	public Sprite remove(int index) {
		Sprite s = super.remove(index);
		
		if (s != null) {
			this.ONSCREEN_GROUP.remove(s);
		}
		
		return s;
	}
	
	public boolean remove(Sprite s) {
		this.ONSCREEN_GROUP.remove(s);
		
		return super.remove(s);
	}
	
	public void clear() {
		super.clear();
		
		this.ONSCREEN_GROUP.clear();
	}
	
	public void reset() {
		super.reset();
		
		this.ONSCREEN_GROUP.reset();
	}
	
	/** ************************************************************************* */
	/** ************************* ON-SCREEN SPRITES ***************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns all <i>on-screen sprites</i> (active, inactive, and also <b>null</b>
	 * sprite) in this group.
	 * 
	 * @see #getSize()
	 */
	public Sprite[] getSprites() {
		return this.ONSCREEN_GROUP.getSprites();
	}
	
	/**
	 * Returns total <b>non-null</b> <i>on-screen sprites</i> (active +
	 * inactive sprites) in this group.
	 */
	public int getSize() {
		return this.ONSCREEN_GROUP.getSize();
	}
	
	/** ************************************************************************* */
	/** *************************** SPRITES GETTER ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns all sprites (active, inactive, and also <b>null</b> sprite) in
	 * this group.
	 * 
	 * @see #getGroupSize()
	 */
	public Sprite[] getGroupSprites() {
		return super.getSprites();
	}
	
	/**
	 * Returns total <b>non-null</b> sprites (active + inactive) in this group.
	 */
	public int getGroupSize() {
		return super.getSize();
	}
	
}
