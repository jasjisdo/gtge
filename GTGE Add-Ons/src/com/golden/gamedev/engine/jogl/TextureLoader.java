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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

import net.java.games.jogl.GL;

/**
 * A utility class to load textures for JOGL. This source is based on a texture
 * that can be found in the Java Gaming (www.javagaming.org) Wiki. It has been
 * simplified slightly for explicit 2D graphics use.
 * 
 * OpenGL uses a particular image format. Since the images that are loaded from
 * disk may not match this format this loader introduces a intermediate image
 * which the source image is copied into. In turn, this image is used as source
 * for the OpenGL texture.
 * 
 * @author Kevin Glass
 */
public class TextureLoader {
	
	// the table of textures that have been loaded in this loader
	private WeakHashMap table = new WeakHashMap();
	
	// the GL context used to load textures
	private GL gl;
	
	// the color model including alpha for the GL image
	private ColorModel glAlphaColorModel;
	
	// the color model for the GL image
	private ColorModel glColorModel;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Create a new texture loader based on the game panel.
	 * 
	 * @param gl The GL content in which the textures should be loaded
	 */
	public TextureLoader(GL gl) {
		this.gl = gl;
		
		this.glAlphaColorModel = new ComponentColorModel(ColorSpace
		        .getInstance(ColorSpace.CS_sRGB), new int[] {
		        8, 8, 8, 8
		}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
		
		this.glColorModel = new ComponentColorModel(ColorSpace
		        .getInstance(ColorSpace.CS_sRGB), new int[] {
		        8, 8, 8, 0
		}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	}
	
	// create a new texture ID
	private int createTextureID() {
		int[] tmp = new int[1];
		this.gl.glGenTextures(1, tmp);
		return tmp[0];
	}
	
	/**
	 * Loads a texture.
	 * 
	 * @param resourceName The location of the resource to load
	 * @return The loaded texture.
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(BufferedImage image) {
		Texture texture = (Texture) this.table.get(image);
		
		if (texture == null) {
			texture = this.getTexture(image, GL.GL_TEXTURE_2D, // target
			        GL.GL_RGBA, // dst pixel format
			        GL.GL_LINEAR, // min filter (unused)
			        GL.GL_LINEAR);
			
			this.table.put(image, texture);
		}
		
		return texture;
	}
	
	/**
	 * Loads a texture into OpenGL from a image reference on disk.
	 * 
	 * @param resourceName The location of the resource to load
	 * @param target The GL target to load the texture against
	 * @param dstPixelFormat The pixel format of the screen
	 * @param minFilter The minimising filter
	 * @param magFilter The magnification filter
	 * @return The loaded texture.
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(BufferedImage image, int target, int dstPixelFormat, int minFilter, int magFilter) {
		int srcPixelFormat = 0;
		
		// create the texture ID for this texture
		int textureID = this.createTextureID();
		Texture texture = new Texture(target, textureID);
		
		// bind this texture
		this.gl.glBindTexture(target, textureID);
		
		texture.setWidth(image.getWidth());
		texture.setHeight(image.getHeight());
		
		srcPixelFormat = (image.getColorModel().hasAlpha()) ? GL.GL_RGBA
		        : GL.GL_RGB;
		
		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = this.convertImageData(image, texture);
		
		if (target == GL.GL_TEXTURE_2D) {
			this.gl
			        .glTexParameteri(target, GL.GL_TEXTURE_MIN_FILTER,
			                minFilter);
			this.gl
			        .glTexParameteri(target, GL.GL_TEXTURE_MAG_FILTER,
			                magFilter);
		}
		
		// produce a texture from the byte buffer
		this.gl.glTexImage2D(target, 0, dstPixelFormat, this.get2Fold(image
		        .getWidth()), this.get2Fold(image.getHeight()), 0,
		        srcPixelFormat, GL.GL_UNSIGNED_BYTE, textureBuffer);
		
		return texture;
	}
	
	/**
	 * Returns the closest greater power of 2 to the fold number.
	 * 
	 * @param fold The target number
	 * @return The power of 2.
	 */
	private int get2Fold(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}
	
	/**
	 * Convert the buffered image to a texture
	 * 
	 * @param bufferedImage The image to convert to a texture
	 * @param texture The texture to store the data into
	 * @return A buffer containing the data
	 */
	private ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture) {
		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;
		
		int texWidth = 2;
		int texHeight = 2;
		
		// find the closest power of 2 for the width and height
		// of the produced texture
		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}
		
		texture.setTextureHeight(texHeight);
		texture.setTextureWidth(texWidth);
		
		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
			        texWidth, texHeight, 4, null);
			texImage = new BufferedImage(this.glAlphaColorModel, raster, false,
			        new Hashtable());
		}
		else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
			        texWidth, texHeight, 3, null);
			texImage = new BufferedImage(this.glColorModel, raster, false,
			        new Hashtable());
		}
		
		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);
		
		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
		        .getData();
		
		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		
		return imageBuffer;
	}
	
	/**
	 * Load a given resource as a buffered image
	 * 
	 * @param ref The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException Indicates a failure to find a resource
	 */
	private BufferedImage loadImage(String ref) throws IOException {
		URL url = TextureLoader.class.getClassLoader().getResource(ref);
		
		if (url == null) {
			throw new IOException("Cannot find: " + ref);
		}
		
		BufferedImage bufferedImage = ImageIO.read(new BufferedInputStream(this
		        .getClass().getClassLoader().getResourceAsStream(ref)));
		
		return bufferedImage;
	}
	
}
