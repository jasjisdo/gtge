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
import java.awt.image.*;

import com.golden.gamedev.gui.TFloatPanel;
import com.golden.gamedev.gui.toolkit.*;

import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;

public class BTitleBarRenderer extends BPanelRenderer {

	public BTitleBarRenderer() {
		put("Background Color", new Color(204,204,255));

		put("Text Color", Color.BLACK);
		put("Text Disabled Color", Color.GRAY);

		GameFont font = new SystemFont(new Font("DIALOG", Font.BOLD, 14));
		put("Text Font", font);
		put("Text Disabled Font", font);

		put("Text Vertical Alignment Integer", UIConstants.CENTER);
		put("Text Insets", new Insets(2, 6, 2, 0));
	}

	public String UIName() { return "TitleBar"; }
	public String[] UIDescription() {
		return new String[] {
			"TitleBar", "TitleBar Disabled"
		};
	}

	public void processUI(TComponent component, BufferedImage[] ui) {
		TFloatPanel.TTitleBar titleBar = (TFloatPanel.TTitleBar) component;

		String[] color = new String[] { "Text Color", "Text Disabled Color" };
		String[] font = new String[] { "Text Font", "Text Disabled Font" };

		String[] document = GraphicsUtil.parseString(titleBar.getTitle());
		for (int i=0;i < ui.length;i++) {
			Graphics2D g = ui[i].createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							   RenderingHints.VALUE_ANTIALIAS_ON);
			GraphicsUtil.drawString(g, document,
									titleBar.getWidth(), titleBar.getHeight(),
									(GameFont) get(font[i], component),
									(Color) get(color[i], component),
									UIConstants.LEFT,
									(Integer) get("Text Vertical Alignment Integer", component),
									(Insets) get("Text Insets", component),
									null);
			g.dispose();
		}
	}

}