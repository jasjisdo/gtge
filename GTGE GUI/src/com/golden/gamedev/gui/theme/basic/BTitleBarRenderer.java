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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.TFloatPanel;
import com.golden.gamedev.gui.toolkit.GraphicsUtil;
import com.golden.gamedev.gui.toolkit.TComponent;
import com.golden.gamedev.gui.toolkit.UIConstants;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;

public class BTitleBarRenderer extends BPanelRenderer {
	
	public BTitleBarRenderer() {
		this.put("Background Color", new Color(204, 204, 255));
		
		this.put("Text Color", Color.BLACK);
		this.put("Text Disabled Color", Color.GRAY);
		
		GameFont font = new SystemFont(new Font("DIALOG", Font.BOLD, 14));
		this.put("Text Font", font);
		this.put("Text Disabled Font", font);
		
		this.put("Text Vertical Alignment Integer", UIConstants.CENTER);
		this.put("Text Insets", new Insets(2, 6, 2, 0));
	}
	
	public String UIName() {
		return "TitleBar";
	}
	
	public String[] UIDescription() {
		return new String[] {
		        "TitleBar", "TitleBar Disabled"
		};
	}
	
	public void processUI(TComponent component, BufferedImage[] ui) {
		TFloatPanel.TTitleBar titleBar = (TFloatPanel.TTitleBar) component;
		
		String[] color = new String[] {
		        "Text Color", "Text Disabled Color"
		};
		String[] font = new String[] {
		        "Text Font", "Text Disabled Font"
		};
		
		String[] document = GraphicsUtil.parseString(titleBar.getTitle());
		for (int i = 0; i < ui.length; i++) {
			Graphics2D g = ui[i].createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			        RenderingHints.VALUE_ANTIALIAS_ON);
			GraphicsUtil.drawString(g, document, titleBar.getWidth(), titleBar
			        .getHeight(), (GameFont) this.get(font[i], component),
			        (Color) this.get(color[i], component), UIConstants.LEFT,
			        (Integer) this.get("Text Vertical Alignment Integer",
			                component), (Insets) this.get("Text Insets",
			                component), null);
			g.dispose();
		}
	}
	
}
