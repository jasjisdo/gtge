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
import java.awt.event.MouseEvent;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.gui.TPane;
import com.golden.gamedev.gui.theme.basic.BasicTheme;

public class FrameWork {
	
	// /////// NULL SINGLETON /////////
	public static final FrameWork NULL_FRAME = new FrameWork();
	
	// protected TInputEvent inputEvent = new TInputEvent();
	protected final BaseInput bsInput;
	
	private TContainer contentPane;
	private TToolTip tooltip;
	private TComponent modal;
	
	private TComponent hoverComponent;
	private TComponent selectedComponent;
	private TComponent[] clickComponent = new TComponent[3];
	
	private UITheme theme;
	
	public FrameWork(BaseInput input, int width, int height) {
		this.contentPane = new TPane(0, 0, width, height);
		this.bsInput = input;
		this.theme = new BasicTheme();
		
		this.tooltip = new TToolTip();
		this.contentPane.add(this.tooltip);
		
		this.setFrameWork(this.contentPane);
	}
	
	// /////// null frame work /////////
	private FrameWork() {
		this.contentPane = new TPane(0, 0, 1, 1);
		this.bsInput = null;
		this.theme = new UITheme();
		
		this.setFrameWork(this.contentPane);
	}
	
	public void add(TComponent comp) {
		this.contentPane.add(comp);
		this.processMouseMotionEvent();
	}
	
	public int remove(TComponent comp) {
		int removed = this.removeComponent(this.contentPane, comp);
		if (removed != -1) {
			this.processMouseMotionEvent();
		}
		
		return removed;
	}
	
	private int removeComponent(TContainer container, TComponent comp) {
		int removed = container.remove(comp);
		TComponent[] components = container.getComponents();
		int i = 0;
		while (removed == -1 && i < components.length - 1) {
			if (components[i].isContainer()) {
				removed = this
				        .removeComponent((TContainer) components[i], comp);
			}
			i++;
		}
		
		return removed;
	}
	
	public void update() {
		if (!this.contentPane.isVisible()) {
			return;
		}
		
		this.processEvents();
		
		// update all components!
		this.contentPane.update();
	}
	
	public void render(Graphics2D g) {
		this.contentPane.render(g);
	}
	
	// /////// events /////////
	private void processEvents() {
		// /////// mouse motion event /////////
		this.processMouseMotionEvent();
		
		// /////// mouse event /////////
		if (this.hoverComponent != null && this.hoverComponent.isEnabled()) {
			this.processMouseEvent();
		}
		
		// /////// key event /////////
		if (this.selectedComponent != null
		        && this.selectedComponent.isEnabled()) {
			this.processKeyEvent();
		}
	}
	
	// /////// mouse motion event /////////
	private void processMouseMotionEvent() {
		if ((this.hoverComponent != null && this.hoverComponent.isEnabled())
		        && (this.bsInput.isMouseDown(MouseEvent.BUTTON1)
		                || this.bsInput.isMouseDown(MouseEvent.BUTTON2) || this.bsInput
		                .isMouseDown(MouseEvent.BUTTON3))) {
			
			if (this.bsInput.getMouseDX() != 0
			        || this.bsInput.getMouseDY() != 0) {
				this.hoverComponent.processMouseDragged();
			}
			
		}
		else {
			// find component at current mouse coordinates
			TComponent comp = this.findComponent(this.bsInput.getMouseX(),
			        this.bsInput.getMouseY());
			
			if (comp != null) {
				if (this.bsInput.getMouseDX() != 0
				        || this.bsInput.getMouseDY() != 0) {
					comp.processMouseMoved();
					this.tooltip.dismiss = 0; // refresh tooltip, so the
												// tooltip will
					// always visible if the mouse keep moving
				}
				
				if (this.hoverComponent == null) {
					this.tooltip.setToolTipComponent(comp);
					comp.processMouseEntered();
					
				}
				else if (comp != this.hoverComponent) {
					this.tooltip.setToolTipComponent(comp);
					this.hoverComponent.processMouseExited();
					comp.processMouseEntered();
				}
				
			}
			else { // no hover component right now
				this.tooltip.setToolTipComponent(null);
				if (this.hoverComponent != null) {
					this.hoverComponent.processMouseExited();
				}
			}
			
			// set component as the new hover component
			this.hoverComponent = comp;
		}
	}
	
	// /////// mouse event /////////
	private void processMouseEvent() {
		int pressed = this.bsInput.getMousePressed(), released = this.bsInput
		        .getMouseReleased();
		
		if (pressed != BaseInput.NO_BUTTON) {
			this.tooltip.setToolTipComponent(null);
			this.tooltip.reshow = 0;
			this.tooltip.initial = 0;
			this.hoverComponent.processMousePressed();
			
			this.clickComponent[pressed - 1] = this.hoverComponent;
			
			// if mouse button 1 pressed,
			// sets hover component as selected component
			if (this.hoverComponent.isFocusable()) {
				if (pressed == MouseEvent.BUTTON1
				        && this.hoverComponent != this.selectedComponent) {
					this.selectComponent(this.hoverComponent);
				}
			}
		}
		
		if (released != BaseInput.NO_BUTTON) {
			this.hoverComponent.processMouseReleased();
			
			// mouse pressed == mouse released
			// process mouse click
			if (this.clickComponent[released - 1] == this.hoverComponent) {
				this.hoverComponent.processMouseClicked();
			}
		}
	}
	
	// /////// key event /////////
	private void processKeyEvent() {
		if (this.bsInput.getKeyPressed() != BaseInput.NO_KEY) {
			this.selectedComponent.keyPressed();
		}
		
		if (this.bsInput.getKeyReleased() != BaseInput.NO_KEY
		        && this.selectedComponent != null) {
			this.selectedComponent.processKeyReleased();
		}
	}
	
	// /////// member methods /////////
	private TComponent findComponent(int x, int y) {
		if (this.modal != null && !this.modal.isContainer()) {
			// when there's a modal, and the modal not container
			// return immediately
			return null;
		}
		
		// set the top container that hold all child components
		// contentpane or modal
		TContainer panel = (this.modal == null) ? this.contentPane
		        : ((TContainer) this.modal);
		TComponent comp = panel.findComponent(x, y);
		
		return comp;
		// return (comp == panel) ? null : comp;
		
		// return contentPane.findComponent(x, y);
	}
	
	public void clearFocus() {
		this.deselectComponent();
	}
	
	void deselectComponent() {
		if (this.selectedComponent == null) {
			return; // there's no selected component, nothing to proceed
		}
		
		this.selectedComponent.setSelected(false);
		this.selectedComponent = null;
	}
	
	boolean selectComponent(TComponent comp) {
		if (!comp.isVisible() || // can not select invisible,
		        !comp.isFocusable() || // unfocusable, and disable component
		        !comp.isEnabled()) {
			return false;
		}
		
		// clear last selected component
		this.deselectComponent();
		
		// select component
		comp.setSelected(true);
		this.selectedComponent = comp;
		
		return true;
	}
	
	void setFrameWork(TComponent comp) {
		if (comp.isContainer()) {
			TComponent[] child = ((TContainer) comp).getComponents();
			for (int i = 0; i < child.length; i++) {
				this.setFrameWork(child[i]);
			}
		}
		
		comp.setFrameWork(this);
	}
	
	void setComponentStat(TComponent comp, boolean active) {
		if (this == FrameWork.NULL_FRAME) {
			return;
		}
		
		if (active == false) { // component is set to non-active
			if (this.hoverComponent == comp) { // check for new hover component
				this.processMouseMotionEvent();
			}
			
			if (this.selectedComponent == comp) {
				this.deselectComponent();
			}
			
			for (int i = 0; i < this.clickComponent.length; i++) {
				// check for clicked component
				if (this.clickComponent[i] == comp) {
					this.clickComponent[i] = null;
					break;
				}
			}
			
			// set to non-modal
			if (this.modal == comp) {
				this.modal = null;
			}
			
		}
		else {
			// check is this component is new hover component
			this.processMouseMotionEvent();
		}
		
		if (comp.isContainer()) {
			TComponent[] components = ((TContainer) comp).getComponents();
			int size = ((TContainer) comp).getComponentCount();
			for (int i = 0; i < size; i++) {
				this.setComponentStat(components[i], active);
			}
		}
	}
	
	void clearComponentsStat(TComponent[] comp) {
		if (this == FrameWork.NULL_FRAME) {
			return;
		}
		
		boolean checkMouseMotion = false;
		for (int i = 0; i < comp.length; i++) {
			if (this.hoverComponent == comp[i]) {
				checkMouseMotion = true;
			}
			
			if (this.selectedComponent == comp[i]) {
				this.deselectComponent();
			}
			
			for (int j = 0; j < this.clickComponent.length; j++) {
				// check for clicked component
				if (this.clickComponent[j] == comp[i]) {
					this.clickComponent[j] = null;
					break;
				}
			}
		}
		
		if (checkMouseMotion) {
			this.processMouseMotionEvent();
		}
	}
	
	public void validateUI() {
		this.validateContainer(this.contentPane);
	}
	
	final void validateContainer(TContainer container) {
		if (container.UIResource().size() > 0) {
			container.createUI();
		}
		
		TComponent[] components = container.getComponents();
		int size = container.getComponentCount();
		for (int i = 0; i < size; i++) {
			if (components[i].UIResource().size() > 0) {
				components[i].createUI();
			}
			if (components[i].isContainer()) {
				this.validateContainer((TContainer) components[i]);
			}
		}
	}
	
	// /////// member variables /////////
	public int getWidth() {
		return this.contentPane.getWidth();
	}
	
	public int getHeight() {
		return this.contentPane.getHeight();
	}
	
	public void setSize(int w, int h) {
		this.contentPane.setSize(w, h);
	}
	
	public TContainer getContentPane() {
		return this.contentPane;
	}
	
	public void setContentPane(TContainer pane) {
		pane.setBounds(0, 0, this.getWidth(), this.getHeight());
		
		this.contentPane = pane;
		this.setFrameWork(this.contentPane);
	}
	
	public TComponent getHoverComponent() {
		return this.hoverComponent;
	}
	
	public TComponent getSelectedComponent() {
		return this.selectedComponent;
	}
	
	public TComponent getModal() {
		return this.modal;
	}
	
	public void setModal(TComponent comp) {
		if (comp != null && !comp.isVisible()) {
			throw new RuntimeException(
			        "Can't set invisible component as modal component!");
		}
		
		this.modal = comp;
	}
	
	/**
	 * Returns the latest inserted component into this frame work.
	 */
	public TComponent get() {
		return this.contentPane.get();
	}
	
	public UITheme getTheme() {
		return this.theme;
	}
	
	public void installTheme(UITheme newTheme) {
		UIRenderer[] ui = this.theme.getInstalledUI();
		for (int i = 0; i < ui.length; i++) {
			if (newTheme.getUITheme(ui[i].UIName()) == null || ui[i].immutable) {
				// new theme doesn't have UI Renderer for specified UIName
				// or old theme use immutable renderers for specified UIName
				// therefore install from old theme
				newTheme.installUI(ui[i]);
			}
		}
		
		this.theme = newTheme;
		
		this.installTheme(this.contentPane);
	}
	
	private void installTheme(TComponent comp) {
		comp.setUIRenderer(this.theme.getUIRenderer(comp.UIName()));
		if (comp.isContainer()) {
			TComponent[] childs = ((TContainer) comp).getComponents();
			for (int i = 0; i < childs.length; i++) {
				this.installTheme(childs[i]);
			}
		}
	}
	
	public TToolTip getToolTip() {
		return this.tooltip;
	}
	
	public void setToolTip(TToolTip tip) {
		this.contentPane.replace(this.tooltip, tip);
		this.tooltip = tip;
	}
	
	protected void finalize() throws Throwable {
		System.out.println("Finalization Frame Work = " + this);
		super.finalize();
	}
	
	public String toString() {
		if (this == FrameWork.NULL_FRAME) {
			return "NULL FRAME WORK";
		}
		return super.toString() + " " + "[width=" + this.getWidth()
		        + ", height=" + this.getHeight() + "]";
	}
	
}
