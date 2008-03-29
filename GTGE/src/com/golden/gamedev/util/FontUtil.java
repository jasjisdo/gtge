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
package com.golden.gamedev.util;

// JFC
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Utility class for manipulating <code>java.awt.Font</code> object.
 */
public class FontUtil {
	
	private FontUtil() {
	}
	
	/**
	 * Creates java.awt.Font from specified True Type Font URL (*.ttf).
	 * 
	 * @see com.golden.gamedev.engine.BaseIO#getURL(String)
	 * @see com.golden.gamedev.object.GameFontManager#getFont(Font)
	 */
	public static Font createTrueTypeFont(URL url, int style, float size) {
		Font f = null;
		
		try {
			f = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
		}
		catch (IOException e) {
			System.err.println("ERROR: " + url
			        + " is not found or can not be read");
			f = new Font("Verdana", 0, 0);
		}
		catch (FontFormatException e) {
			System.err.println("ERROR: " + url
			        + " is not a valid true type font");
			f = new Font("Verdana", 0, 0);
		}
		
		return f.deriveFont(style, size);
	}
	
	/**
	 * Creates bitmap font from specified java.awt.Font that can be used by
	 * {@link com.golden.gamedev.object.font.AdvanceBitmapFont}. <br>
	 * The bitmap is created using standard <i>Bitmap Font Writer</i>, created
	 * by Stefan Pettersson (http://www.stefan-pettersson.nu).
	 * <p>
	 * 
	 * @return Font images using standard Bitmap Font Writer technique.
	 * 
	 * @see com.golden.gamedev.object.font.AdvanceBitmapFont
	 * @see com.golden.gamedev.object.GameFontManager#getFont(BufferedImage)
	 */
	public static BufferedImage createBitmapFont(Font f, Color col) {
		Graphics2D g = ImageUtil.createImage(1, 1).createGraphics();
		FontMetrics fm = g.getFontMetrics(f);
		g.dispose();
		
		byte[] bytes = new byte[95];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (32 + i);
		}
		String st = new String(bytes); // all letters
		
		int w = fm.stringWidth(st), // image width
		h = fm.getHeight(), // and height
		len = st.length(), // total letter count
		x = 0, // draw letter to <x, y>
		y = h - fm.getDescent() + 1;
		
		Color delim = Color.GREEN; // delimiter at <0, 0>
		
		// to avoid the color same like font color
		if (delim.equals(col)) {
			delim = Color.YELLOW;
		}
		
		// draw all letters to the bitmap
		BufferedImage bitmap = ImageUtil
		        .createImage(w, h, Transparency.BITMASK);
		g = bitmap.createGraphics();
		g.setFont(f);
		
		for (int i = 0; i < len; i++) {
			char c = st.charAt(i);
			
			// draw delimiter
			g.setColor(delim);
			g.drawLine(x, 0, x, 0);
			
			// draw letter
			g.setColor(col);
			g.drawString(String.valueOf(c), x, y);
			x += fm.charWidth(c);
		}
		
		g.dispose();
		
		return bitmap;
	}
	
}
