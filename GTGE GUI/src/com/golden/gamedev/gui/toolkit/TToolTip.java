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

import java.awt.Point;

public class TToolTip extends TComponent {
	
	private int initialDelay = 60, // default initial,
	        dismissDelay = 250, // dismiss, and
	        reshowDelay = 30; // reshow delay
	        
	protected int initial, dismiss, reshow, dismissTime;
	
	private TComponent tooltip;
	private String tipText = ""; // to avoid same ui creation
	private boolean tooltipChanged; // if tooltip text is changed,
	// must refresh this tooltip
	// in order to show the new tooltip text
	
	private final Point spacing = new Point(0, 22);
	
	public TToolTip() {
		super(0, 0, 0, 0);
		
		this.setLayer(Integer.MAX_VALUE - 100); // will always be on top!!! :)
		this.setVisible(false);
	}
	
	public void update() {
		if (this.isVisible()) {
			if (this.tooltip != null && !this.tooltipChanged) {
				if (++this.dismiss >= this.dismissTime) {
					// dismiss the tooltip
					this.setToolTipComponent(null);
					this.setVisible(false);
					this.reshow = 0;
				}
				
			}
			else {
				// simply hide the tooltip
				this.setVisible(false);
			}
			
		}
		else {
			if (this.reshow > 0) {
				this.reshow--;
			}
			
			if (this.tooltip != null
			        && (this.reshow > 0 || ++this.initial >= this.initialDelay)) {
				// show the tooltip
				this.show(this.bsInput.getMouseX(), this.bsInput.getMouseY());
			}
		}
	}
	
	public void show(int x, int y) {
		if (this.tooltip == null) {
			return;
		}
		this.setVisible(true);
		
		this.initial = 0;
		this.dismiss = 0;
		this.reshow = this.reshowDelay;
		this.tooltipChanged = false;
		
		if (this.ui == null
		        || !this.tooltip.getToolTipText().equals(this.tipText)) {
			this.createUI();
			
			this.tipText = this.tooltip.getToolTipText();
			String[] componentTipText = GraphicsUtil.parseString(this.tipText);
			this.dismissTime = (this.dismissDelay * componentTipText.length);
		}
		
		// show tooltip, based on mouse coordinate
		x += this.spacing.x;
		y += this.spacing.y;
		
		// to avoid tooltip exceed frame bounds
		if (x + this.ui[0].getWidth() + 20 > this.frame.getWidth()) {
			x -= this.ui[0].getWidth() + this.spacing.x;
		}
		if (y + this.ui[0].getHeight() + 20 > this.frame.getHeight()) {
			y -= this.ui[0].getHeight() + this.spacing.y + 8;
		}
		
		this.setLocation(x, y);
	}
	
	// /////// member fields /////////
	public TComponent getToolTipComponent() {
		return this.tooltip;
	}
	
	public void setToolTipComponent(TComponent tooltip) {
		if (tooltip != null) {
			if (tooltip.getToolTipParent() != null) {
				tooltip = tooltip.getToolTipParent();
			}
			
			if (tooltip.getToolTipText() == null) {
				tooltip = null;
			}
		}
		if (this.tooltip == tooltip) {
			return;
		}
		
		this.tooltip = tooltip;
		this.tooltipChanged = true;
		
		if (!this.isVisible()) {
			// if tooltip not yet visible and tooltip is changed
			// start over the tooltip counter visibility
			this.initial = 0;
		}
	}
	
	public int getInitialDelay() {
		return this.initialDelay;
	}
	
	public void setInitialDelay(int i) {
		this.initialDelay = i;
	}
	
	public int getDismissDelay() {
		return this.dismissDelay;
	}
	
	public void setDismissDelay(int i) {
		this.dismissDelay = i;
	}
	
	public int getReshowDelay() {
		return this.reshowDelay;
	}
	
	public void setReshowDelay(int i) {
		this.reshowDelay = i;
	}
	
	public Point getSpacing() {
		return this.spacing;
	}
	
	public void setSpacing(int x, int y) {
		this.spacing.x = x;
		this.spacing.y = y;
	}
	
	/**
	 * This Component UI Name is <b>ToolTip</b>.
	 */
	public String UIName() {
		return "ToolTip";
	}
	
}
