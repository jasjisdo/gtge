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
package com.golden.gamedev.gui.theme.basic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.TFloatPanel;
import com.golden.gamedev.gui.toolkit.GraphicsUtil;
import com.golden.gamedev.gui.toolkit.TComponent;

public class BTitleBarButtonRenderer extends BButtonRenderer {
	
	public BTitleBarButtonRenderer() {
		this.put("Background Color", new Color(204, 204, 255));
		this.put("Background Border Color", new Color(102, 102, 153));
		
		this.put("Icon Close Image", GraphicsUtil.loadImage(this.getClass(),
		        "icons/close.png", Transparency.BITMASK));
		this.put("Icon Minimize Image", GraphicsUtil.loadImage(this.getClass(),
		        "icons/minimize.png", Transparency.BITMASK));
		this.put("Icon Restore Image", GraphicsUtil.loadImage(this.getClass(),
		        "icons/restore.png", Transparency.BITMASK));
	}
	
	public String UIName() {
		return "TitleBarButton";
	}
	
	public void processUI(TComponent component, BufferedImage[] ui) {
		TFloatPanel.TTitleBar.TTitleBarButton button = (TFloatPanel.TTitleBar.TTitleBarButton) component;
		
		for (int i = 0; i < ui.length; i++) {
			Graphics2D g = ui[i].createGraphics();
			int w = ui[i].getWidth(), h = ui[i].getHeight();
			
			BufferedImage img;
			switch (button.getAction()) {
				case TFloatPanel.TTitleBar.TTitleBarButton.CLOSE_BUTTON:
					img = (BufferedImage) this.get("Icon Close Image",
					        component);
					g.drawImage(img, ((w - img.getWidth()) / 2) + 1, ((h - img
					        .getHeight()) / 2) + 1, null);
					break;
				case TFloatPanel.TTitleBar.TTitleBarButton.ICONIFIED_BUTTON:
					TFloatPanel pane = (TFloatPanel) button.getContainer()
					        .getContainer();
					img = (BufferedImage) this.get(
					        (pane.isIcon() == true) ? "Icon Restore Image"
					                : "Icon Minimize Image", component);
					g.drawImage(img, ((w - img.getWidth()) / 2) + 1, ((h - img
					        .getHeight()) / 2) + 1, null);
					break;
			}
		}
	}
	
}
