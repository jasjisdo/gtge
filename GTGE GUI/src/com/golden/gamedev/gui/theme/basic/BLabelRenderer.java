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
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.TLabel;
import com.golden.gamedev.gui.toolkit.GraphicsUtil;
import com.golden.gamedev.gui.toolkit.TComponent;
import com.golden.gamedev.gui.toolkit.UIConstants;
import com.golden.gamedev.gui.toolkit.UIRenderer;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;
import com.golden.gamedev.util.ImageUtil;

public class BLabelRenderer extends UIRenderer {
	
	public BLabelRenderer() {
		this.put("Background Color", null);
		this.put("Background Border Color", null);
		this.put("Background Disabled Color", null);
		
		this.put("Text Color", Color.BLACK);
		this.put("Text Disabled Color", Color.GRAY);
		
		GameFont font = new SystemFont(new Font("Verdana", Font.PLAIN, 12));
		this.put("Text Font", font);
		this.put("Text Disabled Font", font);
		
		this.put("Text Horizontal Alignment Integer", UIConstants.LEFT);
		this.put("Text Vertical Alignment Integer", UIConstants.CENTER);
		this.put("Text Insets", new Insets(0, 0, 0, 0));
		this.put("Text Vertical Space Integer", new Integer(1));
	}
	
	public String UIName() {
		return "Label";
	}
	
	public String[] UIDescription() {
		return new String[] {
		        "Label", "Label Disabled"
		};
	}
	
	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = new BufferedImage[2];
		
		String[] bgColor = new String[] {
		        "Background Color", "Background Disabled Color"
		};
		
		for (int i = 0; i < ui.length; i++) {
			Color background = (Color) this.get(bgColor[i], component);
			
			// set ui image transparency based on background color
			ui[i] = (background != null) ? ImageUtil.createImage(w, h,
			        Transparency.OPAQUE) : ImageUtil.createImage(w, h,
			        Transparency.BITMASK);
			
			Graphics2D g = ui[i].createGraphics();
			
			// fill background
			if (background != null) {
				g.setColor(background);
				g.fillRect(0, 0, w, h);
			}
			
			// draw border
			Color border = (Color) this.get("Background Border Color",
			        component);
			if (border != null) {
				g.setColor(border);
				g.drawRect(0, 0, w - 1, h - 1);
			}
			
			g.dispose();
		}
		
		return ui;
	}
	
	public void processUI(TComponent component, BufferedImage[] ui) {
		TLabel label = (TLabel) component;
		
		String[] color = new String[] {
		        "Text Color", "Text Disabled Color"
		};
		String[] font = new String[] {
		        "Text Font", "Text Disabled Font"
		};
		
		String[] document = GraphicsUtil.parseString(label.getText());
		for (int i = 0; i < 2; i++) {
			Graphics2D g = ui[i].createGraphics();
			GraphicsUtil.drawString(g, document, label.getWidth(), label
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
		TLabel label = (TLabel) component;
		
		if (!label.isEnabled()) {
			g.drawImage(ui[1], x, y, null);
		}
		else {
			g.drawImage(ui[0], x, y, null);
		}
	}
	
}
