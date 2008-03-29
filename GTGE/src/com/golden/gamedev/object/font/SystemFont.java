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
package com.golden.gamedev.object.font;

// JFC
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.util.ImageUtil;

/**
 * <code>SystemFont</code> is standard AWT Font wrapped in game font interface
 * to be able to draw AWT Font in alignment and other technique.
 */
public class SystemFont implements GameFont {
	
	private final Font font;
	private final FontMetrics fm;
	
	private int height;
	
	private int gap; // gap = awt font base line
	// text is drawn based on this base line
	private Color color;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>SystemFont</code> with specified AWT Font, and color.
	 * 
	 * @param font AWT Font that used to draw this game font
	 * @param color the color to draw the text, or null if the color should
	 *        follow the graphics context active color
	 */
	public SystemFont(Font font, Color color) {
		this.font = font;
		this.color = color;
		
		// dummy graphics only to get system font metrics
		Graphics2D g = ImageUtil.createImage(1, 1).createGraphics();
		
		this.fm = g.getFontMetrics(font);
		this.height = this.fm.getMaxAscent() + this.fm.getMaxDescent()
		        + this.fm.getLeading();
		this.gap = this.height - this.fm.getDescent();
		
		g.dispose();
	}
	
	/**
	 * Creates new <code>SystemFont</code> with specified AWT Font, and the
	 * color is following graphics context active color.
	 * 
	 * @param font AWT Font that used to draw this game font
	 */
	public SystemFont(Font font) {
		this(font, null);
	}
	
	/** ************************************************************************* */
	/** **************************** TEXT DRAWING ******************************* */
	/** ************************************************************************* */
	
	public int drawString(Graphics2D g, String s, int x, int y) {
		if (g.getFont() != this.font) {
			g.setFont(this.font);
		}
		if (this.color != null) {
			g.setColor(this.color);
		}
		
		g.drawString(s, x, y + this.gap);
		
		return x + this.getWidth(s);
	}
	
	public int drawString(Graphics2D g, String s, int alignment, int x, int y, int width) {
		if (alignment == GameFont.LEFT) {
			return this.drawString(g, s, x, y);
			
		}
		else if (alignment == GameFont.CENTER) {
			return this.drawString(g, s, x + (width / 2)
			        - (this.getWidth(s) / 2), y);
			
		}
		else if (alignment == GameFont.RIGHT) {
			return this.drawString(g, s, x + width - this.getWidth(s), y);
			
		}
		else if (alignment == GameFont.JUSTIFY) {
			// calculate left width
			int mod = width - this.getWidth(s);
			if (mod <= 0) {
				// no width left, use standard draw string
				return this.drawString(g, s, x, y);
			}
			
			String st; // the next string to be drawn
			int len = s.length();
			int space = 0; // hold total space; hold space width in pixel
			int curpos = 0; // current string position
			int endpos = 0; // end string relative to curpos (end with ' ')
			
			// count total space
			while (curpos < len) {
				if (s.charAt(curpos++) == ' ') {
					space++;
				}
			}
			
			if (space > 0) {
				// width left plus with total space
				mod += space * this.getWidth(' ');
				
				// space width (in pixel) = width left / total space
				space = mod / space;
			}
			
			curpos = endpos = 0;
			while (curpos < len) {
				endpos = s.indexOf(' ', curpos); // find space
				if (endpos == -1) {
					endpos = len; // no space, draw all string directly
				}
				st = s.substring(curpos, endpos);
				
				this.drawString(g, st, x, y);
				
				x += this.getWidth(st) + space; // increase x-coordinate
				curpos = endpos + 1;
			}
			
			return x;
		}
		
		return 0;
	}
	
	public int drawText(Graphics2D g, String text, int alignment, int x, int y, int width, int vspace, int firstIndent) {
		boolean firstLine = true;
		
		int curpos, startpos, endpos, len = text.length();
		
		int posx = firstIndent;
		curpos = startpos = endpos = 0;
		char curChr;
		while (curpos < len) {
			curChr = text.charAt(curpos++);
			posx += this.getWidth(curChr);
			if (curChr - ' ' == 0) { // space
				endpos = curpos - 1;
			}
			
			if (posx >= width) {
				if (firstLine) {
					// draw first line with indentation
					this.drawString(g, text.substring(startpos, endpos),
					        alignment, (alignment == GameFont.RIGHT) ? x : x
					                + firstIndent, y, width - firstIndent);
					firstLine = false;
					
				}
				else {
					this.drawString(g, text.substring(startpos, endpos),
					        alignment, x, y, width);
					
				}
				
				y += this.getHeight() + vspace;
				
				posx = 0;
				startpos = curpos = endpos + 1;
			}
		}
		
		if (firstLine) {
			// only one line, draw with indent
			this.drawString(g, text.substring(startpos, curpos), alignment,
			        (alignment == GameFont.RIGHT) ? x : x + firstIndent, y,
			        width - firstIndent);
			
		}
		else if (posx != 0) {
			// drawing the last line
			this.drawString(g, text.substring(startpos, curpos), alignment,
			// (alignment == RIGHT) ? RIGHT : LEFT,
			        x, y, width);
			
		}
		
		return y + this.getHeight();
	}
	
	/** ************************************************************************* */
	/** ************************** FONT GET METHODS ***************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns the AWT Font used to draw this <code>SystemFont</code>.
	 */
	public Font getFont() {
		return this.font;
	}
	
	/**
	 * Returns the font metrics used to measure this <code>SystemFont</code>.
	 */
	public FontMetrics getFontMetrics() {
		return this.fm;
	}
	
	/**
	 * Returns the color of this font, or null if the font is drawn following
	 * the graphics context active color.
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Sets the color of this font, or null to draw the font following the
	 * graphics context active color.
	 */
	public void setColor(Color c) {
		this.color = c;
	}
	
	public int getWidth(String st) {
		return this.fm.stringWidth(st);
	}
	
	public int getWidth(char c) {
		return this.fm.charWidth(c);
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public String toString() {
		return super.toString() + " " + "[basefont=" + this.font + ", color="
		        + this.color + "]";
	}
	
	public boolean isAvailable(char c) {
		return true;
	}
	
}
