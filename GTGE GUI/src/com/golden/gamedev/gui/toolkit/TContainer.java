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
package com.golden.gamedev.gui.toolkit;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Arrays;
import java.util.Comparator;

import com.golden.gamedev.util.Utility;

public abstract class TContainer extends TComponent {
	
	private static final Comparator DEFAULT_COMPARATOR = new Comparator() {
		
		public int compare(Object o1, Object o2) {
			return ((TComponent) o2).getLayer() - ((TComponent) o1).getLayer();
		}
	};
	private Comparator comparator = TContainer.DEFAULT_COMPARATOR;
	
	private TComponent[] childs = new TComponent[0];
	private int childCount = 0;
	
	private Shape oldClip; // to revert old graphics clip area
	// used only if elastic = true
	private boolean elastic; // true, rendering out of container bounds
									// is clipped
	
	private TComponent latestInserted = null;
	
	public TContainer(int x, int y, int w, int h) {
		super(x, y, w, h);
		
		this.setFocusable(false);
	}
	
	public boolean isContainer() {
		return true;
	}
	
	/** Appends the specified component to the end of this container. */
	public void add(TComponent comp) {
		if (comp.getContainer() != null) {
			throw new IllegalStateException(comp
			        + " already reside in another container!!!");
		}
		
		comp.setContainer(this);
		
		this.childs = (TComponent[]) Utility.expand(this.childs, 1, false);
		this.childs[0] = comp;
		this.childCount++;
		
		this.frame.setFrameWork(comp); // set child component frame work
		
		this.sortComponents();
		this.latestInserted = comp;
	}
	
	/**
	 * Adds the specified component to this container at the given position.
	 */
	public void add(TComponent comp, int index) {
		if (comp.getContainer() != null) {
			throw new IllegalStateException(comp
			        + " already reside in another container!!!");
		}
		
		comp.setContainer(this);
		
		TComponent[] newChilds = new TComponent[this.childs.length + 1];
		this.childCount++;
		int ctr = 0;
		for (int i = 0; i < this.childCount; i++) {
			if (i != index) {
				newChilds[i] = this.childs[ctr];
				ctr++;
			}
		}
		this.childs = newChilds;
		this.childs[index] = comp;
		
		this.frame.setFrameWork(comp); // set child component frame work
		
		this.sortComponents();
		this.latestInserted = comp;
	}
	
	/** Removes the specified component from this container. */
	public int remove(TComponent comp) {
		for (int i = 0; i < this.childCount; i++) {
			if (this.childs[i] == comp) {
				this.remove(i);
				return i;
			}
		}
		
		return -1;
	}
	
	/** Removes the specified indexth component from this container. */
	public TComponent remove(int index) {
		TComponent comp = this.childs[index];
		
		this.frame.setComponentStat(comp, false);
		comp.setContainer(null);
		
		this.childs = (TComponent[]) Utility.cut(this.childs, index);
		this.childCount--;
		
		return comp;
	}
	
	/** Clears the container (removes all container child components). */
	public void clear() {
		this.frame.clearComponentsStat(this.childs);
		for (int i = 0; i < this.childCount; i++) {
			this.childs[i].setContainer(null);
		}
		
		this.childCount = 0;
		this.childs = new TComponent[0];
	}
	
	public void replace(TComponent oldComp, TComponent newComp) {
		int index = this.remove(oldComp);
		this.add(newComp, index);
	}
	
	public void update() {
		if (!this.isVisible()) {
			return;
		}
		
		super.update();
		
		for (int i = 0; i < this.childCount; i++) {
			this.childs[i].update();
		}
	}
	
	protected void validatePosition() {
		super.validatePosition();
		
		for (int i = 0; i < this.childCount; i++) {
			this.childs[i].validatePosition();
		}
		
		if (!this.elastic) {
			for (int i = 0; i < this.childCount; i++) {
				if (this.childs[i].getX() > this.getWidth()
				        || this.childs[i].getY() > this.getHeight()
				        || this.childs[i].getX() + this.childs[i].getWidth() < 0
				        || this.childs[i].getY() + this.childs[i].getHeight() < 0) {
					this.elastic = true;
					break;
				}
			}
		}
	}
	
	protected void validateSize() {
		super.validateSize();
		
		for (int i = 0; i < this.childCount; i++) {
			this.childs[i].validateSize();
		}
	}
	
	public void render(Graphics2D g) {
		if (!this.isVisible()) {
			return;
		}
		
		super.render(g);
		
		if (this.elastic) {
			// elastic container means:
			// child component could be outside container bounds
			// so we must set graphics clip
			// to clip components that rendered out of container bounds
			this.oldClip = g.getClip(); // save old clip
			g.clipRect(this.getScreenX(), this.getScreenY(), this.getWidth(),
			        this.getHeight());
		}
		
		this.renderComponents(g);
		
		if (this.elastic) {
			// revert back graphics clip
			g.setClip(this.oldClip);
		}
	}
	
	protected void renderComponents(Graphics2D g) {
		for (int i = this.childCount - 1; i >= 0; i--) {
			this.childs[i].render(g);
		}
	}
	
	public void sendToFront(TComponent comp) {
		if (this.childCount <= 1 || this.childs[0] == comp) {
			return;
		}
		
		for (int i = 0; i < this.childCount; i++) {
			if (this.childs[i] == comp) {
				// algorithm:
				// - remove from old position
				// - send to front
				// - then sort
				this.childs = (TComponent[]) Utility.cut(this.childs, i);
				this.childs = (TComponent[]) Utility.expand(this.childs, 1,
				        false);
				this.childs[0] = comp;
				this.sortComponents();
				break;
			}
		}
	}
	
	public void sendToBack(TComponent comp) {
		if (this.childCount <= 1 || this.childs[this.childCount - 1] == comp) {
			return;
		}
		
		for (int i = 0; i < this.childCount; i++) {
			if (this.childs[i] == comp) {
				// algorithm:
				// - remove from old position
				// - send to back
				// - then sort
				this.childs = (TComponent[]) Utility.cut(this.childs, i);
				this.childs = (TComponent[]) Utility.expand(this.childs, 1,
				        true);
				this.childs[this.childCount - 1] = comp;
				this.sortComponents();
				break;
			}
		}
	}
	
	public void sortComponents() {
		Arrays.sort(this.childs, this.comparator);
	}
	
	/** Transfers the specified child component focus forward. */
	protected void transferFocus(TComponent component) {
		for (int i = 0; i < this.childCount; i++) {
			if (component == this.childs[i]) {
				int j = i;
				do {
					if (--i < 0) {
						i = this.childCount - 1;
					}
					if (i == j) {
						return;
					}
				} while (!this.childs[i].requestFocus());
				
				break;
			}
		}
	}
	
	/** Transfers the specified child component focus backward. */
	protected void transferFocusBackward(TComponent component) {
		for (int i = 0; i < this.childCount; i++) {
			if (component == this.childs[i]) {
				int j = i;
				do {
					if (++i >= this.childCount) {
						i = 0;
					}
					if (i == j) {
						return;
					}
				} while (!this.childs[i].requestFocus());
				
				break;
			}
		}
	}
	
	/**
	 * Returns true whether this container is being selected. If any of this
	 * container childs is being selected, this container is marked as selected
	 * too.
	 */
	public boolean isSelected() {
		if (!super.isSelected()) {
			for (int i = 0; i < this.childCount; i++) {
				if (this.childs[i].isSelected()) {
					// child component is being selected
					// mark this container as selected too
					return true;
				}
			}
			return false;
			
		}
		else {
			return true;
		}
	}
	
	public boolean isElastic() {
		return this.elastic;
	}
	
	public void setElastic(boolean b) {
		this.elastic = b;
	}
	
	public Comparator getComparator() {
		return this.comparator;
	}
	
	public void setComparator(Comparator c) {
		if (c == null) {
			throw new NullPointerException("Comparator can not null");
		}
		
		this.comparator = c;
		this.sortComponents();
	}
	
	/**
	 * Finds visible child component in specified screen coordinate. The
	 * top-most child component is returned in the case of overlap child
	 * components.
	 * <p>
	 * If the founded child component is also a container, this method will
	 * continue searching for the deepest nested child component.
	 */
	public TComponent findComponent(int x1, int y1) {
		if (!this.intersects(x1, y1)) {
			return null;
		}
		
		for (int i = 0; i < this.childCount; i++) {
			if (this.childs[i].intersects(x1, y1)) {
				// recursive find component
				TComponent comp = (this.childs[i].isContainer() == false) ? this.childs[i]
				        : ((TContainer) this.childs[i]).findComponent(x1, y1);
				return comp;
			}
		}
		
		return this;
	}
	
	/** Returns the number of components in this container. */
	public int getComponentCount() {
		return this.childCount;
	}
	
	/** Returns all child components in this container. */
	public TComponent[] getComponents() {
		return this.childs;
	}
	
	/** Returns the latest inserted component into this container. */
	public TComponent get() {
		return this.latestInserted;
	}
	
}
