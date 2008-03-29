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

import com.golden.gamedev.gui.toolkit.GraphicsUtil;
import com.golden.gamedev.gui.toolkit.TComponent;
import com.golden.gamedev.gui.toolkit.TToolTip;
import com.golden.gamedev.gui.toolkit.UIConstants;
import com.golden.gamedev.gui.toolkit.UIRenderer;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;

public class BToolTipRenderer extends UIRenderer {
	
	public BToolTipRenderer() {
		this.put("Background Color", new Color(255, 255, 231));
		this.put("Background Border Color", Color.BLACK);
		
		this.put("Text Color", Color.BLACK);
		
		this.put("Text Font", new SystemFont(
		        new Font("Verdana", Font.PLAIN, 11)));
		
		this.put("Text Insets", new Insets(3, 4, 4, 4));
		this.put("Text Vertical Space Integer", new Integer(1));
	}
	
	public String UIName() {
		return "ToolTip";
	}
	
	public String[] UIDescription() {
		return new String[] {
			"ToolTip"
		};
	}
	
	public BufferedImage[] createUI(TComponent component, int w, int h) {
		TComponent tooltip = ((TToolTip) component).getToolTipComponent();
		if (tooltip == null) {
			return null;
		}
		
		String tipText = tooltip.getToolTipText();
		String[] document = GraphicsUtil.parseString(tipText);
		
		GameFont font = (GameFont) this.get("Text Font", component);
		Insets inset = (Insets) this.get("Text Insets", component);
		int space = ((Integer) this.get("Text Vertical Space Integer",
		        component)).intValue();
		int width = 0;
		for (int i = 0; i < document.length; i++) {
			w = font.getWidth(document[i]) + inset.left + inset.right;
			if (w > width) {
				width = w;
			}
		}
		int height = (document.length * (font.getHeight() + space)) - space
		        + inset.top + inset.bottom;
		
		BufferedImage[] ui = GraphicsUtil.createImage(1, width, height,
		        Transparency.OPAQUE);
		Graphics2D g = ui[0].createGraphics();
		
		g.setColor((Color) this.get("Background Color", component));
		g.fillRect(1, 1, width - 2, height - 2);
		Color borderColor = (Color) this.get("Background Border Color",
		        component);
		if (borderColor != null) {
			g.setColor(borderColor);
			g.drawRect(0, 0, width - 1, height - 1);
		}
		
		g.dispose();
		
		return ui;
	}
	
	public void processUI(TComponent component, BufferedImage[] ui) {
		TComponent tooltip = ((TToolTip) component).getToolTipComponent();
		if (tooltip == null) {
			return;
		}
		
		String tipText = tooltip.getToolTipText();
		
		Graphics2D g = ui[0].createGraphics();
		GraphicsUtil.drawString(g, GraphicsUtil.parseString(tipText), ui[0]
		        .getWidth(), ui[0].getHeight(), (GameFont) this.get(
		        "Text Font", component), (Color) this.get("Text Color",
		        component), UIConstants.LEFT, UIConstants.TOP, (Insets) this
		        .get("Text Insets", component), (Integer) this.get(
		        "Text Vertical Space Integer", component));
		g.dispose();
	}
	
	public void renderUI(Graphics2D g, int x, int y, TComponent component, BufferedImage[] ui) {
		g.drawImage(ui[0], x, y, null);
	}
	
}
