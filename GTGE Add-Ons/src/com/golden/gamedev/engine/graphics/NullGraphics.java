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
package com.golden.gamedev.engine.graphics;

// JFC
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * A fake Graphics2D class for OpenGL architecture.
 * <p>
 * 
 * The new graphics engine, in this case OpenGL architecture, should override
 * all essential functions.
 */
public abstract class NullGraphics extends Graphics2D {
	
	/** ************************************************************************* */
	/** ************************* IMAGE RENDERING ******************************* */
	/** ************************************************************************* */
	
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}
	
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}
	
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}
	
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}
	
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}
	
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		throw new UnsupportedOperationException();
	}
	
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		throw new UnsupportedOperationException();
	}
	
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		throw new UnsupportedOperationException();
	}
	
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		throw new UnsupportedOperationException();
	}
	
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		throw new UnsupportedOperationException();
	}
	
	/** ************************************************************************* */
	/** ************************** TEXT RENDERING ******************************* */
	/** ************************************************************************* */
	
	public void drawString(String str, int x, int y) {
		throw new UnsupportedOperationException();
	}
	
	public void drawString(String s, float x, float y) {
		throw new UnsupportedOperationException();
	}
	
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		throw new UnsupportedOperationException();
	}
	
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		throw new UnsupportedOperationException();
	}
	
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		throw new UnsupportedOperationException();
	}
	
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		throw new UnsupportedOperationException();
	}
	
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		throw new UnsupportedOperationException();
	}
	
	/** ************************************************************************* */
	/** ************************ PRIMITIVE RENDERING **************************** */
	/** ************************************************************************* */
	
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		throw new UnsupportedOperationException();
	}
	
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		throw new UnsupportedOperationException();
	}
	
	public void draw(Shape s) {
		throw new UnsupportedOperationException();
	}
	
	public void fill(Shape s) {
		throw new UnsupportedOperationException();
	}
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		throw new UnsupportedOperationException();
	}
	
	public void drawRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public void fillRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public void clearRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		throw new UnsupportedOperationException();
	}
	
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		throw new UnsupportedOperationException();
	}
	
	public void drawOval(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public void fillOval(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		throw new UnsupportedOperationException();
	}
	
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		throw new UnsupportedOperationException();
	}
	
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		throw new UnsupportedOperationException();
	}
	
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		throw new UnsupportedOperationException();
	}
	
	public void drawPolygon(Polygon p) {
		throw new UnsupportedOperationException();
	}
	
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		throw new UnsupportedOperationException();
	}
	
	public void fillPolygon(Polygon p) {
		throw new UnsupportedOperationException();
	}
	
	/** ************************************************************************* */
	/** ************************ GET / SET OPERATION **************************** */
	/** ************************************************************************* */
	
	public void setComposite(Composite comp) {
		throw new UnsupportedOperationException();
	}
	
	public Composite getComposite() {
		throw new UnsupportedOperationException();
	}
	
	public void setBackground(Color color) {
		throw new UnsupportedOperationException();
	}
	
	public Color getBackground() {
		throw new UnsupportedOperationException();
	}
	
	public Color getColor() {
		throw new UnsupportedOperationException();
	}
	
	public void setColor(Color c) {
		throw new UnsupportedOperationException();
	}
	
	public Font getFont() {
		throw new UnsupportedOperationException();
	}
	
	public void setFont(Font font) {
		throw new UnsupportedOperationException();
	}
	
	public FontMetrics getFontMetrics() {
		throw new UnsupportedOperationException();
	}
	
	public FontMetrics getFontMetrics(Font f) {
		throw new UnsupportedOperationException();
	}
	
	public FontRenderContext getFontRenderContext() {
		throw new UnsupportedOperationException();
	}
	
	public void setPaint(Paint paint) {
		throw new UnsupportedOperationException();
	}
	
	public Paint getPaint() {
		throw new UnsupportedOperationException();
	}
	
	public void setPaintMode() {
		throw new UnsupportedOperationException();
	}
	
	public void setStroke(Stroke s) {
		throw new UnsupportedOperationException();
	}
	
	public Stroke getStroke() {
		throw new UnsupportedOperationException();
	}
	
	public void setRenderingHint(Key hintKey, Object hintValue) {
		throw new UnsupportedOperationException();
	}
	
	public Object getRenderingHint(Key hintKey) {
		throw new UnsupportedOperationException();
	}
	
	public void setRenderingHints(Map hints) {
		throw new UnsupportedOperationException();
	}
	
	public RenderingHints getRenderingHints() {
		throw new UnsupportedOperationException();
	}
	
	public void addRenderingHints(Map hints) {
		throw new UnsupportedOperationException();
	}
	
	public void setXORMode(Color c1) {
		throw new UnsupportedOperationException();
	}
	
	public GraphicsConfiguration getDeviceConfiguration() {
		throw new UnsupportedOperationException();
	}
	
	/** ************************************************************************* */
	/** *********************** GRAPHICS OPERATION ****************************** */
	/** ************************************************************************* */
	
	public void translate(int x, int y) {
		throw new UnsupportedOperationException();
	}
	
	public void translate(double tx, double ty) {
		throw new UnsupportedOperationException();
	}
	
	public void rotate(double theta) {
		throw new UnsupportedOperationException();
	}
	
	public void rotate(double theta, double x, double y) {
		throw new UnsupportedOperationException();
	}
	
	public void scale(double sx, double sy) {
		throw new UnsupportedOperationException();
	}
	
	public void shear(double shx, double shy) {
		throw new UnsupportedOperationException();
	}
	
	public void transform(AffineTransform Tx) {
		throw new UnsupportedOperationException();
	}
	
	public void setTransform(AffineTransform Tx) {
		throw new UnsupportedOperationException();
	}
	
	public AffineTransform getTransform() {
		throw new UnsupportedOperationException();
	}
	
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		throw new UnsupportedOperationException();
	}
	
	public void clip(Shape s) {
		throw new UnsupportedOperationException();
	}
	
	public Graphics create() {
		throw new UnsupportedOperationException();
	}
	
	public Graphics create(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public Rectangle getClipBounds() {
		throw new UnsupportedOperationException();
	}
	
	public void clipRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public void setClip(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public Shape getClip() {
		throw new UnsupportedOperationException();
	}
	
	public void setClip(Shape clip) {
		throw new UnsupportedOperationException();
	}
	
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		throw new UnsupportedOperationException();
	}
	
	public void dispose() {
		throw new UnsupportedOperationException();
	}
	
	public boolean hitClip(int x, int y, int width, int height) {
		throw new UnsupportedOperationException();
	}
	
	public Rectangle getClipBounds(Rectangle r) {
		throw new UnsupportedOperationException();
	}
	
}
