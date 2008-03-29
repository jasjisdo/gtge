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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.util.ImageUtil;

public abstract class TComponent {
	
	protected FrameWork frame = FrameWork.NULL_FRAME;
	protected BaseInput bsInput;
	private TContainer parent; // parent container
	
	// /////// rendering variables /////////
	private UIRenderer renderer;
	protected BufferedImage[] ui;
	private BufferedImage[] externalUI;
	private boolean processUI;
	protected final Map UIResource = new java.util.HashMap();
	
	/**
	 * true, component is taken all the responsibility of its own rendering.
	 * 
	 * @see #renderCustomUI(Graphics2D,int,int,int,int).
	 */
	public boolean customRendering;
	
	// /////// member variables /////////
	private int x, y, width, height; // position, size
	private int screenX, screenY; // screen position
	private int layer = 0; // default layer, the lowest layer
	private String tooltip; // tooltip text
	private TComponent tooltipParent; // tooltip parent
	
	// ///// flags ////////
	private boolean visible = true;
	private boolean enabled = true;
	
	private boolean focusable = true; // only focusable component can be
										// selected
	private boolean selected = false;
	
	public TComponent(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		
		if (this.width == 0) {
			this.width = 1;
		}
		if (this.height == 0) {
			this.height = 1;
		}
	}
	
	/**
	 * Returns true if this component is a container. If component is a
	 * container, some method will also check its child components. Reduce the
	 * use of instanceof, because instanceof is a 'heavy' method.
	 */
	public boolean isContainer() {
		return false;
	}
	
	// /////// main methods /////////
	/** Updates component state. */
	public void update() {
	}
	
	/** Renders component to specified graphics context. */
	public void render(Graphics2D g) {
		if (!this.visible) {
			return;
		}
		
		try {
			if (this.customRendering) {
				this.renderCustomUI(g, this.screenX, this.screenY, this.width,
				        this.height);
				
			}
			else {
				this.renderer.renderUI(g, this.screenX, this.screenY, this,
				        this.ui);
				
			}
		}
		catch (Exception e) {
			System.out.println("error rendering = " + this);
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * When component {@link #customRendering} is set to true, component
	 * rendering is done within this method. The implementation of this method
	 * provided by TComponent class does nothing.
	 */
	protected void renderCustomUI(Graphics2D g, int x, int y, int w, int h) {
	}
	
	/**
	 * Tests whether this component bounds intersects with specified point,
	 * where the point are defined as screen coordinate.
	 */
	public boolean intersects(int x1, int y1) {
		return (this.visible)
		        && (x1 >= this.screenX && x1 <= this.screenX + this.width
		                && y1 >= this.screenY && y1 <= this.screenY
		                + this.height);
	}
	
	/**
	 * Tests whether this component intersects with specified component.
	 */
	public boolean intersects(TComponent comp) {
		return (this.visible)
		        && (comp.isVisible())
		        && (this.screenX + this.width >= comp.screenX
		                && this.screenX <= comp.screenX + comp.width
		                && this.screenY + this.height >= comp.screenY && this.screenY <= comp.screenY
		                + comp.height);
	}
	
	/**
	 * Destroys this component.
	 */
	public void dispose() {
		this.frame.setComponentStat(this, false);
		if (this.parent != null) {
			this.parent.remove(this);
		}
		
		this.frame = FrameWork.NULL_FRAME;
		this.bsInput = null;
		this.parent = null;
		
		this.ui = null;
		this.renderer = null;
		
		this.selected = false;
	}
	
	// /////// flags /////////
	/** Returns true if this component is visible on screen. */
	public boolean isVisible() {
		return this.visible;
	}
	
	/** Shows or hides this component. */
	public void setVisible(boolean b) {
		if (this.visible == b) {
			return; // no visibility state changed, no need further process
		}
		
		this.visible = b;
		this.frame.setComponentStat(this, this.visible);
	}
	
	/**
	 * Returns true if this component is enabled. An enabled component can
	 * receive user input.
	 */
	public boolean isEnabled() {
		return (this.parent == null) ? this.enabled
		        : (this.enabled && this.parent.isEnabled());
	}
	
	/**
	 * Enables or disables this component. An enabled component can receive user
	 * input.
	 */
	public void setEnabled(boolean b) {
		if (this.enabled == b) {
			return; // no enabled state changed, no need further process
		}
		
		this.enabled = b;
		this.frame.setComponentStat(this, this.enabled);
	}
	
	/** Returns true if this component is selected (the focus owner). */
	public boolean isSelected() {
		return this.selected;
	}
	
	final void setSelected(boolean b) {
		this.selected = b;
	}
	
	/**
	 * Requests that this component get the input focus.
	 * 
	 * @return false if the request is guaranteed to fail
	 */
	public boolean requestFocus() {
		return this.frame.selectComponent(this);
	}
	
	/**
	 * Transfers the focus to the next component, as though this component were
	 * the focus owner.
	 */
	public void transferFocus() {
		if (this.isSelected() && this.parent != null) {
			this.parent.transferFocus(this);
		}
	}
	
	/**
	 * Transfers the focus to the previous component, as though this component
	 * were the focus owner.
	 */
	public void transferFocusBackward() {
		if (this.isSelected() && this.parent != null) {
			this.parent.transferFocusBackward(this);
		}
	}
	
	/** Returns whether this component can be focused (selected). */
	public boolean isFocusable() {
		return this.focusable;
	}
	
	/** Sets the focusable state of this component to the specified value. */
	public void setFocusable(boolean b) {
		this.focusable = b;
	}
	
	/** Returns the parent container of this component. */
	public TContainer getContainer() {
		return this.parent;
	}
	
	final void setContainer(TContainer container) {
		this.parent = container;
		
		this.validatePosition();
	}
	
	final void setFrameWork(FrameWork frame) {
		if (this.frame == frame) {
			return;
		}
		
		this.frame = frame;
		this.bsInput = frame.bsInput;
		
		if (this.renderer == null) {
			this.renderer = frame.getTheme().getUIRenderer(this.UIName());
		}
		
		this.createUI();
	}
	
	// /////// member variables /////////
	public void setBounds(int x, int y, int width, int height) {
		if (this.x != x || this.y != y) {
			this.x = x;
			this.y = y;
			this.validatePosition();
		}
		
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			if (width == 0) {
				width = 1;
			}
			if (height == 0) {
				height = 1;
			}
			
			this.createUI();
			this.validateSize();
		}
	}
	
	public void setLocation(int x, int y) {
		if (this.x != x || this.y != y) {
			this.x = x;
			this.y = y;
			
			this.validatePosition();
		}
	}
	
	public void move(int dx, int dy) {
		if (dx != 0 || dy != 0) {
			this.x += dx;
			this.y += dy;
			this.validatePosition();
		}
	}
	
	public void setSize(int w, int h) {
		if (this.width != w || this.height != h) {
			this.width = w;
			this.height = h;
			if (this.width == 0) {
				this.width = 1;
			}
			if (this.height == 0) {
				this.height = 1;
			}
			
			this.createUI();
			this.validateSize();
		}
	}
	
	protected void validateSize() {
	}
	
	protected void validatePosition() {
		this.screenX = (this.parent == null) ? this.x : this.x
		        + this.parent.getScreenX();
		this.screenY = (this.parent == null) ? this.y : this.y
		        + this.parent.getScreenY();
	}
	
	/** Returns component x-axis coordinate. */
	public int getX() {
		return this.x;
	}
	
	/** Returns component y-axis coordinate. */
	public int getY() {
		return this.y;
	}
	
	/** Returns component screen x-axis coordinate. */
	public int getScreenX() {
		return this.screenX;
	}
	
	/** Returns component screen y-axis coordinate. */
	public int getScreenY() {
		return this.screenY;
	}
	
	/** Returns the width of this component. */
	public int getWidth() {
		return this.width;
	}
	
	/** Returns the height of this component. */
	public int getHeight() {
		return this.height;
	}
	
	public int getLayer() {
		return this.layer;
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	/** Returns component tooltip string. */
	public String getToolTipText() {
		return this.tooltip;
	}
	
	/**
	 * Registers the text as this component tool tip. The text displays when the
	 * cursor lingers over the component.
	 */
	public void setToolTipText(String text) {
		this.tooltip = text;
	}
	
	public TComponent getToolTipParent() {
		return this.tooltipParent;
	}
	
	public void setToolTipParent(TComponent tipParent) {
		this.tooltipParent = tipParent;
	}
	
	// /////// events /////////
	// /////// key event /////////
	protected void processKeyPressed() {
	}
	
	protected void processKeyReleased() {
	}
	
	// /////// mouse event /////////
	protected void processMousePressed() {
	}
	
	protected void processMouseReleased() {
	}
	
	protected void processMouseClicked() {
	}
	
	// /////// mouse motion event /////////
	protected void processMouseMoved() {
	}
	
	protected void processMouseDragged() {
	}
	
	protected void processMouseEntered() {
	}
	
	protected void processMouseExited() {
	}
	
	// /////// custom events /////////
	void keyPressed() {
		this.checkFocusKey();
		this.processKeyPressed();
	}
	
	protected void checkFocusKey() {
		if (this.bsInput.getKeyPressed() == KeyEvent.VK_TAB) {
			if (this.bsInput.isKeyDown(KeyEvent.VK_SHIFT) == false) {
				this.transferFocus();
			}
			else {
				this.transferFocusBackward();
			}
		}
	}
	
	// void mouseDragged() {
	// if (dragable && bsInput.isMouseDown(MouseEvent.BUTTON1)) {
	// // move dragable component
	// if (parent != null) {
	// parent.sendToFront(this);
	// }
	// move(bsInput.getMouseDX(), bsInput.getMouseDY());
	// }
	//
	// processMouseDragged();
	// }
	
	// protected void finalize() throws Throwable {
	// System.out.println("finalizing "+this);
	// super.finalize();
	// }
	
	public String toString() {
		return super.toString() + " " + "[UIName=" + this.UIName()
		        + ", bounds=" + this.x + "," + this.y + "," + this.width + ","
		        + this.height + "]";
	}
	
	// /////// various ui rendering function /////////
	public BufferedImage[] getExternalUI() {
		return this.externalUI;
	}
	
	public void setExternalUI(BufferedImage[] externalUI, boolean processUI) {
		if (externalUI != null) {
			this.width = externalUI[0].getWidth();
			this.height = externalUI[0].getHeight();
		}
		
		this.externalUI = externalUI;
		this.processUI = processUI;
		
		this.createUI();
	}
	
	/** Returns customized UI Resource used by this component. */
	public Map UIResource() {
		return this.UIResource;
	}
	
	/** Returns the UI Renderer object that renders this component. */
	public UIRenderer getUIRenderer() {
		return this.renderer;
	}
	
	/** Sets the UI Renderer object that renders this component. */
	public void setUIRenderer(UIRenderer renderer) {
		this.renderer = renderer;
		
		this.createUI();
	}
	
	/**
	 * Component UI Name, UI Factory used this name to poll this component UI
	 * Renderer.
	 * 
	 * @return Component UI Name
	 */
	public abstract String UIName();
	
	/**
	 * Creates component UI, this method adjust the UI creation depends on
	 * component rendering technique.
	 * <p>
	 * 
	 * There are three rendering technique :
	 * 
	 * First, if this component is responsible of its own rendering ({@link #customRendering} =
	 * true), the UI creation will be handled by
	 * {@link #createCustomUI(int, int)} and processed by
	 * {@link #processCustomUI()}.
	 * <p>
	 * 
	 * If not then it will checking the possibility of using
	 * {@linkplain #getExternalUI() external UI}.
	 * <p>
	 * 
	 * If all fail then this component will use its default
	 * {@linkplain #getUIRenderer() Component UI Renderer}.
	 * 
	 * @see #customRendering
	 * @see #createCustomUI(int, int)
	 * @see #setExternalUI(BufferedImage[], boolean)
	 */
	protected void createUI() {
		if (this.frame == FrameWork.NULL_FRAME) {
			return;
		}
		
		if (this.customRendering) {
			this.createCustomUI(this.width, this.height);
			this.processCustomUI();
			
		}
		else if (this.externalUI != null
		        && this.renderer.UIDescription().length == this.externalUI.length) {
			// using external ui
			// validate size
			if (this.width != this.externalUI[0].getWidth()
			        || this.height != this.externalUI[0].getHeight()) {
				System.err
				        .print(this
				                + "\n"
				                + "Illegal Operation! "
				                + "Can not change component size when using external UI\n"
				                + "size (" + this.width + "," + this.height
				                + ") -> ");
				this.width = this.externalUI[0].getWidth();
				this.height = this.externalUI[0].getHeight();
				System.err.println("(" + this.width + "," + this.height + ")");
			}
			
			if (!this.processUI) {
				this.ui = this.externalUI;
				
			}
			else {
				// external ui need further process
				// clone and process the ui
				this.ui = new BufferedImage[this.externalUI.length];
				for (int i = 0; i < this.ui.length; i++) {
					this.ui[i] = ImageUtil.createImage(this.externalUI[i]
					        .getWidth(), this.externalUI[i].getHeight(),
					        this.externalUI[i].getColorModel()
					                .getTransparency());
					Graphics2D g = this.ui[i].createGraphics();
					g.setComposite(AlphaComposite.Src);
					g.drawImage(this.externalUI[i], 0, 0, null);
					g.dispose();
				}
				
				this.processExternalUI();
			}
			
		}
		else {
			// using ui renderer
			this.createRenderedUI(this.width, this.height);
			this.processRenderedUI();
		}
	}
	
	/**
	 * Creates component UI by component
	 * {@linkplain #getUIRenderer() UI Renderer}.
	 */
	protected void createRenderedUI(int w, int h) {
		this.ui = this.renderer.createUI(this, w, h);
	}
	
	/**
	 * Process component UI by component
	 * {@linkplain #getUIRenderer() UI Renderer}.
	 */
	protected void processRenderedUI() {
		this.renderer.processUI(this, this.ui);
	}
	
	/**
	 * When component {@link #customRendering} is set to true, component UI is
	 * created in this method.
	 * <p>
	 * The implementation of this method provided by TComponent class does
	 * nothing.
	 */
	protected void createCustomUI(int w, int h) {
	}
	
	/**
	 * Process component UI when component {@link #customRendering} is set to
	 * true.
	 * <p>
	 * The implementation of this method provided by TComponent class does
	 * nothing.
	 */
	protected void processCustomUI() {
	}
	
	/**
	 * External UI that needed further process is processed within this method,
	 * by default external UI is processed by component
	 * {@linkplain #getUIRenderer() UI Renderer}.
	 */
	protected void processExternalUI() {
		this.renderer.processUI(this, this.ui);
	}
	
	public void validateUI() {
		this.createUI();
	}
	
	public void printUIResource() {
		if (this.renderer != null) {
			this.renderer.printUIResource(this);
			
		}
		else {
			String[] temp = new String[this.UIResource.size()];
			String[] keys = (String[]) this.UIResource.keySet().toArray(temp);
			
			System.out.println(this.UIName() + " Component UI Resource");
			System.out.println("UI Renderer has not been set!!");
			System.out.println("Before print component UI Resource, "
			        + "please insert this component "
			        + "into the FrameWork to set its UI Renderer");
			System.out.println("=============================================");
			for (int i = 0; i < keys.length; i++) {
				System.out.println(keys[i] + " -> "
				        + this.UIResource.get(keys[i]));
			}
			System.out.println("=============================================");
			System.out.println();
		}
	}
	
}
