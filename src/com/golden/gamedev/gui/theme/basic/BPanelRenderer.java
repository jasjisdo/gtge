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

import java.awt.*;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.toolkit.*;

public class BPanelRenderer extends UIRenderer {

	public BPanelRenderer() {
		put("Background Color", new Color(231,227,231));
		put("Background Border Color", Color.BLACK);
		put("Background Disabled Color", Color.DARK_GRAY);
	}

	public String UIName() { return "Panel"; }
	public String[] UIDescription() {
		return new String[] { "Panel", "Panel Disabled" };
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = GraphicsUtil.createImage(2, w, h, Transparency.OPAQUE);

		String[] color = new String[] {
			"Background Color", "Background Disabled Color"
		};

		Color borderColor = (Color) get("Background Border Color", component);
		for (int i=0;i < ui.length;i++) {
			Graphics2D g = ui[i].createGraphics();

			g.setColor((Color) get(color[i], component));
			if (borderColor == null) {
				g.fill3DRect(0, 0, w, h, true);
			} else { // borderColor != null
				g.fill3DRect(0, 0, w-1, h-1, true);
				g.setColor(borderColor);
				g.drawRect(0, 0, w-1, h-1);
			}

			g.dispose();
		}

		return ui;
	}
	public void processUI(TComponent component, BufferedImage[] ui) {
	}
	public void renderUI(Graphics2D g, int x, int y,
						 TComponent component, BufferedImage[] ui) {
		if (!component.isEnabled()) {
			g.drawImage(ui[1], x, y, null);

		} else {
			g.drawImage(ui[0], x, y, null);
		}
	}

}