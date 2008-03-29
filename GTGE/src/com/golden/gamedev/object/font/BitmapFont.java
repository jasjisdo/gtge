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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.golden.gamedev.object.GameFont;

/**
 * Game font that use images for the letter, all the images <b>must</b> have
 * equal dimension (same width and height).
 * <p>
 * 
 * <code>BitmapFont</code> takes up two parameters, the array of images font
 * and the sequence of the images, for example if the images font array sequence
 * is ordered as follow: "ABCDEFGHIJKLMNOPQRSTUVWXYZ", specify the parameter
 * letter sequence as is.
 * <p>
 * 
 * If the images font has different width, use
 * {@link com.golden.gamedev.object.font.AdvanceBitmapFont} instead.
 */
public class BitmapFont implements GameFont {
	
	/** *********************** BITMAP FONT PROPERTIES ************************** */
	
	private BufferedImage[] imagefont;
	
	private int w, h; // font width and height
	        
	protected int[] charIndex = new int[256];
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>BitmapFont</code> with specified images font and
	 * letter sequence.
	 * 
	 * @param imagefont the images font, all images must have same size
	 * @param letterSequence the order sequence of the images font
	 */
	public BitmapFont(BufferedImage[] imagefont, String letterSequence) {
		this.imagefont = imagefont;
		
		this.w = imagefont[0].getWidth();
		this.h = imagefont[0].getHeight();
		
		for (int i = 0; i < this.charIndex.length; i++) {
			this.charIndex[i] = -1;
		}
		
		int totalLetter = letterSequence.length();
		for (int i = 0; i < totalLetter; i++) {
			this.charIndex[(int) letterSequence.charAt(i)] = i;
		}
	}
	
	/**
	 * Creates new <code>BitmapFont</code> with specified images font and
	 * default letter sequence :
	 * 
	 * <pre>
	 *         ! &quot; # $ % &amp; ' ( ) * + , - . / 0 1 2 3
	 *       4 5 6 7 8 9 : ; &lt; = &gt; ? @ A B C D E F G
	 *       H I J K L M N O P Q R S T U V W X Y Z [
	 *       \ ] &circ; _ ' a b c d e f g h i j k l m n o
	 *       p q r s t u v w x y z { | } &tilde;
	 * </pre>
	 * 
	 * @param imagefont the images font, all images must have same size
	 */
	public BitmapFont(BufferedImage[] imagefont) {
		this(imagefont, " !\"#$%&'()*+,-./0123" + "456789:;<=>?@ABCDEFG"
		        + "HIJKLMNOPQRSTUVWXYZ[" + "\\]^_`abcdefghijklmno"
		        + "pqrstuvwxyz{|}~");
	}
	
	/** ************************************************************************* */
	/** **************************** TEXT DRAWING ******************************* */
	/** ************************************************************************* */
	
	public int drawString(Graphics2D g, String s, int x, int y) {
		int len = s.length();
		int index = 0;
		char letter;
		
		for (int i = 0; i < len; i++) {
			letter = s.charAt(i);
			index = this.charIndex[(int) letter];
			
			if (index != -1) {
				try {
					g.drawImage(this.imagefont[index], x, y, null);
					x += this.getWidth(letter);
				}
				catch (Exception e) {
					throw new RuntimeException(this
					        + "\nunable to draw letter '" + s.charAt(i) + "'["
					        + (i + 1) + "] in " + "\"" + s + "\"! charIndex = "
					        + index + "\n" + "caused by : " + e);
				}
				
			}
			else {
				throw new RuntimeException(this + "\nunable to draw letter ["
				        + s.charAt(i) + "] (" + (i + 1) + ") in " + "[" + s
				        + "] text! ");
			}
		}
		
		return x;
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
				mod += this.w * space;
				
				// space width (in pixel) = width left / total space
				space = mod / space;
			}
			// =============================================================
			
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
			// only one line
			this.drawString(g, text.substring(startpos, curpos), alignment,
			        (alignment == GameFont.RIGHT) ? x : x + firstIndent, y,
			        width - firstIndent);
			
		}
		else if (posx != 0) {
			// draw last line
			this.drawString(g, text.substring(startpos, curpos), alignment,
			// (alignment == RIGHT) ? RIGHT : LEFT,
			        x, y, width);
			
		}
		
		return y + this.getHeight();
	}
	
	/** ************************************************************************* */
	/** ************************** FONT PROPERTIES ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns the images font used to draw this bitmap font.
	 */
	public BufferedImage[] getImageFont() {
		return this.imagefont;
	}
	
	/**
	 * Sets images font to draw this bitmap font.
	 */
	public void setImageFont(BufferedImage[] imagefont, String letterSequence) {
		this.imagefont = imagefont;
		
		this.w = imagefont[0].getWidth();
		this.h = imagefont[0].getHeight();
		
		for (int i = 0; i < this.charIndex.length; i++) {
			this.charIndex[i] = -1;
		}
		
		int totalLetter = letterSequence.length();
		for (int i = 0; i < totalLetter; i++) {
			this.charIndex[(int) letterSequence.charAt(i)] = i;
		}
	}
	
	public int getWidth(char c) {
		return this.w;
	}
	
	public int getWidth(String st) {
		return st.length() * this.w;
	}
	
	public int getHeight() {
		return this.h;
	}
	
	public String toString() {
		return super.toString() + " " + "[totalChar=" + this.imagefont.length
		        + ", width=" + this.w + ", height=" + this.h + "]";
	}
	
	public boolean isAvailable(char c) {
		try {
			return (this.charIndex[(int) c] != -1);
		}
		catch (Exception e) {
			return false;
		}
	}
	
}
