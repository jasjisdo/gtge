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
package com.golden.gamedev.engine.jogl;

// JFC
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.WeakHashMap;

import net.java.games.jogl.GL;
import net.java.games.jogl.util.BufferUtils;

import com.golden.gamedev.engine.graphics.NullGraphics;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.AdvanceBitmapFont;
import com.golden.gamedev.util.FontUtil;
import com.golden.gamedev.util.ImageUtil;
import com.golden.gamedev.util.Utility;

/**
 * Fake Graphics2D for OpenGL JOGL.
 */
public class JOGLGraphics extends NullGraphics {
	
	// used to convert from a AffineTransform to a OpenGL matrix
	private static double affineMatrix[] = new double[3 * 3];
	
	// OpenGL matrix
	private static FloatBuffer glMatrix = BufferUtils.newFloatBuffer(16);
	
	// display dimension
	private static IntBuffer display = BufferUtils.newIntBuffer(16);
	
	private GL GL11;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	public JOGLGraphics(GL gl) {
		this.GL11 = gl;
		
		this.textureLoader = new TextureLoader(gl);
		
		this.fontMap = new WeakHashMap();
	}
	
	/**
	 * Returns OpenGL context.
	 */
	public GL getGL() {
		return this.GL11;
	}
	
	/**
	 * Returns texture loader used to load textures by this OpenGL renderer.
	 */
	public TextureLoader getTextureLoader() {
		return this.textureLoader;
	}
	
	/** ************************************************************************* */
	/** ************************* CLASS VARIABLES ******************************* */
	/** ************************************************************************* */
	
	private static final Rectangle NULL_RECTANGLE = new Rectangle();
	
	private TextureLoader textureLoader;
	
	private Color color = Color.BLACK;
	private Color background = Color.BLACK;
	
	private Composite composite;
	
	private Font font;
	private WeakHashMap fontMap;
	private Graphics2D g;
	private int gap;
	
	private Rectangle clipArea;
	
	/** ************************************************************************* */
	/** ************************** OPENGL RENDERING ***************************** */
	/** ************************************************************************* */
	
	/** ************************************************************************* */
	/** ************************* IMAGE RENDERING ******************************* */
	/** ************************************************************************* */
	
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		this.startPainting();
		
		// store the current model matrix
		this.GL11.glPushMatrix();
		
		// bind to the appropriate texture for this sprite
		Texture texture = this.textureLoader.getTexture((BufferedImage) img);
		texture.bind(this.GL11);
		
		// translate to the right location and prepare to draw
		this.GL11.glTranslatef(x, y, 0);
		
		// draw a quad textured to match the sprite
		this.GL11.glBegin(GL.GL_QUADS);
		this.GL11.glTexCoord2f(0, 0);
		this.GL11.glVertex2f(0, 0);
		
		this.GL11.glTexCoord2f(0, texture.getHeight());
		this.GL11.glVertex2f(0, texture.getImageHeight());
		
		this.GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
		this.GL11.glVertex2f(texture.getImageWidth(), texture.getImageHeight());
		
		this.GL11.glTexCoord2f(texture.getWidth(), 0);
		this.GL11.glVertex2f(texture.getImageWidth(), 0);
		this.GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		this.GL11.glPopMatrix();
		
		this.endPainting();
		
		return true;
	}
	
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		this.startPainting();
		
		this.GL11.glPushMatrix();
		
		Texture texture = this.textureLoader.getTexture((BufferedImage) img);
		texture.bind(this.GL11);
		
		this.GL11.glTranslatef(x, y, 0);
		
		this.GL11.glBegin(GL.GL_QUADS);
		this.GL11.glTexCoord2f(0, 0);
		this.GL11.glVertex2f(0, 0);
		
		this.GL11.glTexCoord2f(0, texture.getHeight());
		this.GL11.glVertex2f(0, height);
		
		this.GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
		this.GL11.glVertex2f(width, height);
		
		this.GL11.glTexCoord2f(texture.getWidth(), 0);
		this.GL11.glVertex2f(width, 0);
		this.GL11.glEnd();
		
		this.GL11.glPopMatrix();
		
		this.endPainting();
		
		return true;
	}
	
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		this.startPainting();
		
		Texture texture = this.textureLoader.getTexture((BufferedImage) img);
		texture.bind(this.GL11);
		
		float tx0 = ((float) sx1 / texture.getTextureWidth());
		float tx1 = ((float) sx2 / texture.getTextureWidth());
		float ty0 = ((float) sy1 / texture.getTextureHeight());
		float ty1 = ((float) sy2 / texture.getTextureHeight());
		
		this.GL11.glBegin(GL.GL_QUADS);
		this.GL11.glTexCoord2f(tx0, ty0);
		this.GL11.glVertex2f(dx1, dy1);
		
		this.GL11.glTexCoord2f(tx1, ty0);
		this.GL11.glVertex2f(dx2, dy1);
		
		this.GL11.glTexCoord2f(tx1, ty1);
		this.GL11.glVertex2f(dx2, dy2);
		
		this.GL11.glTexCoord2f(tx0, ty1);
		this.GL11.glVertex2f(dx1, dy2);
		this.GL11.glEnd();
		
		this.endPainting();
		
		return true;
	}
	
	public boolean drawImage(Image img, AffineTransform transform, ImageObserver obs) {
		transform.getMatrix(JOGLGraphics.affineMatrix);
		
		JOGLGraphics.glMatrix.rewind();
		JOGLGraphics.glMatrix.put((float) JOGLGraphics.affineMatrix[0]).put(
		        (float) JOGLGraphics.affineMatrix[1]).put(0).put(0);
		JOGLGraphics.glMatrix.put((float) JOGLGraphics.affineMatrix[2]).put(
		        (float) JOGLGraphics.affineMatrix[3]).put(0).put(0);
		JOGLGraphics.glMatrix.put(0).put(0).put(1).put(0);
		JOGLGraphics.glMatrix.put((float) JOGLGraphics.affineMatrix[4]).put(
		        (float) JOGLGraphics.affineMatrix[5]).put(0).put(1);
		JOGLGraphics.glMatrix.rewind();
		
		this.GL11.glPushMatrix();
		this.GL11.glMultMatrixf(JOGLGraphics.glMatrix.array());
		
		this.drawImage(img, 0, 0, null);
		
		this.GL11.glPopMatrix();
		
		return true;
	}
	
	/** ************************************************************************* */
	/** ************************** TEXT RENDERING ******************************* */
	/** ************************************************************************* */
	
	public void drawString(String str, int x, int y) {
		GameFont font = this.getGameFont();
		
		font.drawString(this, str, x, y + this.gap);
	}
	
	/** ************************************************************************* */
	/** ************************ PRIMITIVE RENDERING **************************** */
	/** ************************************************************************* */
	
	public void fillRect(int x, int y, int width, int height) {
		this.drawRect(x, y, width, height, GL.GL_QUADS, this.color);
	}
	
	public void drawRect(int x, int y, int width, int height) {
		this.drawRect(x, y, width, height, GL.GL_LINE_LOOP, this.color);
	}
	
	public void clearRect(int x, int y, int width, int height) {
		this.drawRect(x, y, width, height, GL.GL_QUADS, this.background);
	}
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		this.GL11.glDisable(GL.GL_TEXTURE_2D);
		
		this.GL11
		        .glColor4f((float) this.color.getRed() / 255f,
		                (float) this.color.getGreen() / 255f,
		                (float) this.color.getBlue() / 255f, (float) this.color
		                        .getAlpha() / 255f);
		this.GL11.glLineWidth(1.0f);
		
		this.GL11.glBegin(GL.GL_LINES);
		this.GL11.glVertex2f(x1, y1);
		this.GL11.glVertex2f(x2, y2);
		this.GL11.glEnd();
		
		this.GL11.glColor3f(1.0f, 1.0f, 1.0f);
		
		this.GL11.glEnable(GL.GL_TEXTURE_2D);
	}
	
	/** ************************************************************************* */
	/** ************************ GET / SET OPERATION **************************** */
	/** ************************************************************************* */
	
	public void setColor(Color c) {
		this.color = c;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setBackground(Color color) {
		this.background = color;
	}
	
	public Color getBackground() {
		return this.background;
	}
	
	public void setComposite(Composite comp) {
		this.composite = comp;
	}
	
	public Composite getComposite() {
		return this.composite;
	}
	
	public Font getFont() {
		if (this.font == null) {
			this.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		
		return this.font;
	}
	
	public void setFont(Font font) {
		if (font != null) {
			this.font = font;
			
			FontMetrics fm = this.getFontMetrics(font);
			this.gap = fm.getDescent() - fm.getMaxAscent() - fm.getMaxDescent()
			        - fm.getLeading();
		}
	}
	
	public FontMetrics getFontMetrics() {
		return this.getFontMetrics(this.getFont());
	}
	
	public FontMetrics getFontMetrics(Font f) {
		if (this.g == null) {
			// dummy graphics only to get system font metrics
			this.g = ImageUtil.createImage(1, 1).createGraphics();
		}
		
		return this.g.getFontMetrics(f);
	}
	
	public void dispose() {
	}
	
	/** ************************************************************************* */
	/** *********************** GRAPHICS OPERATION ****************************** */
	/** ************************************************************************* */
	
	public void clipRect(int x, int y, int width, int height) {
		this.setClip(x, y, width, height);
	}
	
	public void setClip(int x, int y, int width, int height) {
		this.GL11.glGetIntegerv(GL.GL_VIEWPORT, JOGLGraphics.display.array());
		
		this.GL11.glEnable(GL.GL_SCISSOR_TEST);
		this.GL11.glScissor(x, JOGLGraphics.display.get(3) - y - height, width,
		        height);
		
		if (this.clipArea == null) {
			this.clipArea = JOGLGraphics.NULL_RECTANGLE;
		}
		
		this.clipArea.setBounds(x, y, width, height);
	}
	
	public Shape getClip() {
		return this.clipArea;
	}
	
	public void setClip(Shape clip) {
		this.clipArea = (Rectangle) clip;
		
		if (this.clipArea == null) {
			this.GL11.glDisable(GL.GL_SCISSOR_TEST);
			
		}
		else {
			this.setClip(this.clipArea.x, this.clipArea.y, this.clipArea.width,
			        this.clipArea.height);
		}
	}
	
	/** ************************************************************************* */
	/** ************************** PRIVATE METHODS ****************************** */
	/** ************************************************************************* */
	
	private void startPainting() {
		if (this.composite != null) {
			try {
				this.GL11.glColor4f(1.0f, 1.0f, 1.0f,
				        ((AlphaComposite) this.composite).getAlpha());
			}
			catch (ClassCastException e) {
			}
		}
	}
	
	private void endPainting() {
		if (this.composite != null) {
			this.GL11.glColor3f(1.0f, 1.0f, 1.0f);
		}
	}
	
	private void drawRect(int x, int y, int width, int height, int type, Color col) {
		this.GL11.glDisable(GL.GL_TEXTURE_2D);
		
		this.GL11.glColor4f((float) col.getRed() / 255f,
		        (float) col.getGreen() / 255f, (float) col.getBlue() / 255f,
		        (float) col.getAlpha() / 255f);
		this.GL11.glLineWidth(1.0f);
		
		this.GL11.glBegin(type);
		this.GL11.glVertex2f(x, y);
		this.GL11.glVertex2f(x + width, y);
		this.GL11.glVertex2f(x + width, y + height);
		this.GL11.glVertex2f(x, y + height);
		this.GL11.glEnd();
		
		this.GL11.glColor3f(1.0f, 1.0f, 1.0f);
		
		this.GL11.glEnable(GL.GL_TEXTURE_2D);
	}
	
	private GameFont getGameFont() {
		Font f = this.getFont();
		GameFont gameFont = (GameFont) this.fontMap.get(f);
		
		if (gameFont == null) {
			BufferedImage bitmap = FontUtil.createBitmapFont(f, Color.BLACK);
			
			int delimiter = bitmap.getRGB(0, 0); // pixel <0,0> : delimiter
			int[] width = new int[100]; // assumption : 100 letter
			int ctr = 0;
			int last = 0; // last width point
			
			for (int i = 1; i < bitmap.getWidth(); i++) {
				if (bitmap.getRGB(i, 0) == delimiter) {
					// found delimiter
					width[ctr++] = i - last;
					last = i;
					
					if (ctr >= width.length) {
						width = (int[]) Utility.expand(width, 50);
					}
				}
			}
			
			// create bitmap font
			BufferedImage[] imagefont = new BufferedImage[ctr];
			int height = bitmap.getHeight() - 1;
			int w = 0;
			for (int i = 0; i < imagefont.length; i++) {
				imagefont[i] = bitmap.getSubimage(w, 1, width[i], height);
				
				w += width[i];
			}
			
			gameFont = new AdvanceBitmapFont(imagefont);
			
			this.fontMap.put(f, gameFont);
		}
		
		return gameFont;
	}
	
}
