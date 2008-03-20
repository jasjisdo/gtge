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
	private int initialDelay = 60, 		// default initial,
				dismissDelay = 250,		// dismiss, and
				reshowDelay = 30;		// reshow delay

	protected int initial, dismiss, reshow, dismissTime;

	private TComponent 	tooltip;
	private String 		tipText = "";	// to avoid same ui creation
	private boolean		tooltipChanged;	// if tooltip text is changed,
										// must refresh this tooltip
										// in order to show the new tooltip text

	private final Point	spacing = new Point(0, 22);

	public TToolTip() {
		super(0,0,0,0);

		setLayer(Integer.MAX_VALUE-100); // will always be on top!!! :)
		setVisible(false);
	}

	public void update() {
		if (isVisible()) {
			if (tooltip != null && !tooltipChanged) {
				if (++dismiss >= dismissTime) {
					// dismiss the tooltip
					setToolTipComponent(null);
					setVisible(false);
					reshow = 0;
				}

			} else {
				// simply hide the tooltip
				setVisible(false);
			}

		} else {
			if (reshow > 0) reshow--;

			if (tooltip != null &&
				(reshow > 0 || ++initial >= initialDelay)) {
				// show the tooltip
				show(bsInput.getMouseX(), bsInput.getMouseY());
			}
		}
	}
	public void show(int x, int y) {
		if (tooltip == null) return;
		setVisible(true);

		initial = 0;
		dismiss = 0;
		reshow = reshowDelay;
		tooltipChanged = false;

		if (ui == null || !tooltip.getToolTipText().equals(tipText)) {
			createUI();

			tipText = tooltip.getToolTipText();
			String[] componentTipText = GraphicsUtil.parseString(tipText);
			dismissTime = (dismissDelay * componentTipText.length);
		}

		// show tooltip, based on mouse coordinate
		x += spacing.x; y += spacing.y;

		// to avoid tooltip exceed frame bounds
		if (x + ui[0].getWidth() + 20 > frame.getWidth()) {
			x -= ui[0].getWidth() + spacing.x;
		}
		if (y + ui[0].getHeight() + 20 > frame.getHeight()) {
			y -= ui[0].getHeight() + spacing.y + 8;
		}

		setLocation(x, y);
	}

	///////// member fields /////////
	public TComponent getToolTipComponent() { return tooltip; }
	public void setToolTipComponent(TComponent tooltip) {
		if (tooltip != null) {
   		 	if (tooltip.getToolTipParent() != null) {
	   		 	tooltip = tooltip.getToolTipParent();
			}

			if (tooltip.getToolTipText() == null) {
				tooltip = null;
			}
		}
		if (this.tooltip == tooltip) { return; }

		this.tooltip = tooltip;
		tooltipChanged = true;

		if (!isVisible()) {
			// if tooltip not yet visible and tooltip is changed
			// start over the tooltip counter visibility
			initial = 0;
		}
	}

	public int getInitialDelay() { return initialDelay; }
	public void setInitialDelay(int i) { initialDelay = i; }

	public int getDismissDelay() { return dismissDelay; }
	public void setDismissDelay(int i) { dismissDelay = i; }

	public int getReshowDelay() { return reshowDelay; }
	public void setReshowDelay(int i) { reshowDelay = i; }

	public Point getSpacing() { return spacing; }
	public void setSpacing(int x, int y) { spacing.x = x; spacing.y = y; }

	/**
	 * This Component UI Name is <b>ToolTip</b>.
	 */
	public String UIName() { return "ToolTip"; }

}