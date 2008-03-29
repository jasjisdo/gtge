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
import java.awt.image.BufferedImage;

/**
 * Game font that use images for the letter, each images can have different
 * width but must have same height.
 * <p>
 * 
 * <code>AdvanceBitmapFont</code> takes up two parameters, the array of images
 * font and the sequence of the images, for example if the images font array
 * sequence is ordered as follow: "ABCDEFGHIJKLMNOPQRSTUVWXYZ", specify the
 * parameter letter sequence as is.
 * <p>
 * 
 * If the images font have same width, use the standard
 * {@link com.golden.gamedev.object.font.BitmapFont}.
 */
public class AdvanceBitmapFont extends BitmapFont {
	
	private int[] w;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Creates new <code>AdvanceBitmapFont</code> with specified images font
	 * and letter sequence.
	 * 
	 * @param imagefont the images font, all images must have same height
	 * @param letterSequence the order sequence of the images font
	 */
	public AdvanceBitmapFont(BufferedImage[] imagefont, String letterSequence) {
		super(imagefont, letterSequence);
		
		this.w = new int[imagefont.length];
		
		for (int i = 0; i < imagefont.length; i++) {
			this.w[i] = imagefont[i].getWidth();
		}
	}
	
	/**
	 * Creates new <code>AdvanceBitmapFont</code> with specified images font
	 * and default letter sequence :
	 * 
	 * <pre>
	 *         ! &quot; # $ % &amp; ' ( ) * + , - . / 0 1 2 3
	 *       4 5 6 7 8 9 : ; &lt; = &gt; ? @ A B C D E F G
	 *       H I J K L M N O P Q R S T U V W X Y Z [
	 *       \ ] &circ; _ ` a b c d e f g h i j k l m n o
	 *       p q r s t u v w x y z { | } &tilde;
	 * </pre>
	 * 
	 * @param imagefont the images font, all images must have same height
	 */
	public AdvanceBitmapFont(BufferedImage[] imagefont) {
		this(imagefont, " !\"#$%&'()*+,-./0123" + "456789:;<=>?@ABCDEFG"
		        + "HIJKLMNOPQRSTUVWXYZ[" + "\\]^_`abcdefghijklmno"
		        + "pqrstuvwxyz{|}~");
	}
	
	public void setImageFont(BufferedImage[] imagefont, String letterSequence) {
		super.setImageFont(imagefont, letterSequence);
		
		this.w = new int[imagefont.length];
		
		for (int i = 0; i < imagefont.length; i++) {
			this.w[i] = imagefont[i].getWidth();
		}
	}
	
	public int getWidth(char c) {
		return this.w[this.charIndex[(int) c]];
	}
	
	public int getWidth(String st) {
		int len = st.length();
		int width = 0;
		
		for (int i = 0; i < len; i++) {
			width += this.getWidth(st.charAt(i));
		}
		
		return width;
	}
	
}
