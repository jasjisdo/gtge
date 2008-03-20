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

import com.golden.gamedev.gui.TTextField;
import com.golden.gamedev.gui.toolkit.*;

public class BTextFieldRenderer extends UIRenderer {

	public BTextFieldRenderer() {
		put("Background Color", Color.WHITE);
		put("Background Disabled Color", Color.WHITE);
		put("Background Uneditable Color", new Color(204, 204, 204));

		put("Background Border Color", Color.BLACK);
		put("Background Border Disabled Color", Color.DARK_GRAY);
		put("Background Border Uneditable Color", Color.DARK_GRAY);
	}

	public String UIName() { return "TextField"; }
	public String[] UIDescription() {
		return new String[] {
			"TextField", "TextField Disabled", "TextField Not Editable"
		};
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = GraphicsUtil.createImage(3, w, h, Transparency.OPAQUE);

		String[] color = new String[] {
			"Background Color", "Background Disabled Color",
			"Background Uneditable Color"
		};
		String[] border = new String[] {
			"Background Border Color", "Background Border Disabled Color",
			"Background Border Uneditable Color"
		};

		for (int i=0;i < ui.length;i++) {
			Graphics2D g = ui[i].createGraphics();

			g.setColor((Color) get(color[i], component));
			g.fillRect(0, 0, w, h);

			g.setColor((Color) get(border[i], component));
			g.drawRect(0, 0, w-1, h-1);

			g.dispose();
		}

		return ui;
	}

	public void processUI(TComponent component, BufferedImage[] ui) {
	}
	public void renderUI(Graphics2D g, int x, int y,
						 TComponent component, BufferedImage[] ui) {
		TTextField textField = (TTextField) component;

		if (!textField.isEnabled()) {
			g.drawImage(ui[1], x, y, null);

		} else if (!textField.isEditable()) {
			g.drawImage(ui[2], x, y, null);

		} else {
			g.drawImage(ui[0], x, y, null);
		}
	}

}