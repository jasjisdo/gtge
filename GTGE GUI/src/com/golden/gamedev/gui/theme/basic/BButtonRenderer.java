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
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.TButton;
import com.golden.gamedev.gui.toolkit.GraphicsUtil;
import com.golden.gamedev.gui.toolkit.TComponent;
import com.golden.gamedev.gui.toolkit.UIConstants;
import com.golden.gamedev.gui.toolkit.UIRenderer;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;

public class BButtonRenderer extends UIRenderer {
	
	public BButtonRenderer() {
		this.put("Background Color", Color.LIGHT_GRAY);
		this.put("Background Over Color", Color.CYAN);
		this.put("Background Pressed Color", Color.LIGHT_GRAY);
		this.put("Background Border Color", Color.BLACK);
		this.put("Background Disabled Color", Color.GRAY);
		
		this.put("Text Color", Color.BLACK);
		this.put("Text Over Color", Color.BLACK);
		this.put("Text Pressed Color", Color.BLACK);
		this.put("Text Disabled Color", Color.BLACK);
		
		GameFont font = new SystemFont(new Font("Dialog", Font.BOLD, 15));
		this.put("Text Font", font);
		this.put("Text Over Font", font);
		this.put("Text Pressed Font", font);
		this.put("Text Disabled Font", font);
		
		this.put("Text Horizontal Alignment Integer", UIConstants.CENTER);
		this.put("Text Vertical Alignment Integer", UIConstants.CENTER);
		this.put("Text Insets", new Insets(5, 5, 5, 5));
		this.put("Text Vertical Space Integer", new Integer(1));
	}
	
	public String UIName() {
		return "Button";
	}
	
	public String[] UIDescription() {
		return new String[] {
		        "Button", "Button Over", "Button Pressed", "Button Disabled"
		};
	}
	
	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = GraphicsUtil.createImage(4, w, h,
		        Transparency.OPAQUE);
		
		String[] color = new String[] {
		        "Background Color", "Background Over Color",
		        "Background Pressed Color", "Background Disabled Color"
		};
		
		Color borderColor = (Color) this.get("Background Border Color",
		        component);
		for (int i = 0; i < 4; i++) {
			Graphics2D g = ui[i].createGraphics();
			g.setColor((Color) this.get(color[i], component));
			switch (i) {
				case 0:
					g.fill3DRect(0, 0, w - 1, h - 1, true);
					break;
				case 1:
					g.fillRect(0, 0, w - 1, h - 1);
					break;
				case 2:
					g.fill3DRect(0, 0, w - 1, h - 1, false);
					break;
				case 3:
					g.fill3DRect(0, 0, w - 1, h - 1, true);
					break;
			}
			if (borderColor != null) {
				g.setColor(borderColor);
				g.drawRect(0, 0, w - 1, h - 1);
			}
			g.dispose();
		}
		
		return ui;
	}
	
	public void processUI(TComponent component, BufferedImage[] ui) {
		TButton button = (TButton) component;
		
		String[] color = new String[] {
		        "Text Color", "Text Over Color", "Text Pressed Color",
		        "Text Disabled Color"
		};
		String[] font = new String[] {
		        "Text Font", "Text Over Font", "Text Pressed Font",
		        "Text Disabled Font"
		};
		
		String[] document = GraphicsUtil.parseString(button.getText());
		for (int i = 0; i < 4; i++) {
			Graphics2D g = ui[i].createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			        RenderingHints.VALUE_ANTIALIAS_ON);
			GraphicsUtil.drawString(g, document, button.getWidth(), button
			        .getHeight(), (GameFont) this.get(font[i], component),
			        (Color) this.get(color[i], component), (Integer) this.get(
			                "Text Horizontal Alignment Integer", component),
			        (Integer) this.get("Text Vertical Alignment Integer",
			                component), (Insets) this.get("Text Insets",
			                component), (Integer) this.get(
			                "Text Vertical Space Integer", component));
			g.dispose();
		}
	}
	
	public void renderUI(Graphics2D g, int x, int y, TComponent component, BufferedImage[] ui) {
		TButton button = (TButton) component;
		
		if (!button.isEnabled()) {
			g.drawImage(ui[3], x, y, null);
		}
		else if (button.isMousePressed()) {
			g.drawImage(ui[2], x, y, null);
		}
		else if (button.isMouseOver()) {
			g.drawImage(ui[1], x, y, null);
		}
		else {
			g.drawImage(ui[0], x, y, null);
		}
	}
	
}
