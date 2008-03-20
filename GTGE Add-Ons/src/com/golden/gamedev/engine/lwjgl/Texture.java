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
package com.golden.gamedev.engine.lwjgl;

// LWJGL
import org.lwjgl.opengl.GL11;


/**
 * A texture to be bound within OpenGL. This object is responsible for
 * keeping track of a given OpenGL texture and for calculating the
 * texturing mapping coordinates of the full image.
 *
 * Since textures need to be powers of 2 the actual texture may be
 * considerably bigged that the source image and hence the texture
 * mapping coordinates need to be adjusted to matchup drawing the
 * sprite against the texture.
 *
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class Texture {


	private int		target;			// the GL target type
	private int		textureID;		// the GL texture ID

	private int		width;			// the width of the image
	private int		height;			// the height of the image

	private int		texWidth;		// the width of the texture
	private int		texHeight;		// the height of the texture

	private float	widthRatio;		// the ratio of
									// image width and texture width
	private float	heightRatio;	// the ratio of
									// image height and texture height


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates a new texture.
	 *
	 * @param target 	the GL target
	 * @param textureID the GL texture ID
	 */
	public Texture(int target, int textureID) {
		this.target = target;
		this.textureID = textureID;
	}


 /****************************************************************************/
 /************************ BIND TEXTURE TO OPENGL ****************************/
 /****************************************************************************/

	/**
	 * Bind the specified GL context to a texture.
	 *
	 * @param gl 	the GL context to bind to
	 */
	public void bind() {
		GL11.glBindTexture(target, textureID);
	}


 /****************************************************************************/
 /******************** SET IMAGE, TEXTURE WIDTH / HEIGHT *********************/
 /****************************************************************************/

	/**
	 * Sets the height of the image.
	 *
	 * @param height 	the height of the image
	 */
	public void setHeight(int height) {
		this.height = height;
		setHeight();
	}

	/**
	 * Sets the width of the image.
	 *
	 * @param width 	the width of the image
	 */
	public void setWidth(int width) {
		this.width = width;
		setWidth();
	}


	/**
	 * Set the height of this texture
	 *
	 * @param texHeight The height of the texture
	 */
	public void setTextureHeight(int texHeight) {
		this.texHeight = texHeight;
		setHeight();
	}

	/**
	 * Set the width of this texture
	 *
	 * @param texWidth The width of the texture
	 */
	public void setTextureWidth(int texWidth) {
		this.texWidth = texWidth;
		setWidth();
	}


 /****************************************************************************/
 /************** GET IMAGE, TEXTURE, RATIO WIDTH / HEIGHT ********************/
 /****************************************************************************/

	/**
	 * Returns the height of the original image.
	 */
	public int getImageHeight() {
		return height;
	}

	/**
	 * Returns the width of the original image.
	 */
	public int getImageWidth() {
		return width;
	}


	/**
	 * Returns the width of the texture.
	 */
	public int getTextureWidth() {
		return texWidth;
	}

	/**
	 * Returns the height of the texture.
	 */
	public int getTextureHeight() {
		return texHeight;
	}


	/**
	 * Returns the height of the physical texture.
	 */
	public float getHeight() {
		return heightRatio;
	}

	/**
	 * Returns the width of the physical texture.
	 */
	public float getWidth() {
		return widthRatio;
	}



	/**
	 * Set the height of the texture. This will update the
	 * ratio also.
	 */
	private void setHeight() {
		if (texHeight != 0) {
			heightRatio = ((float) height) / texHeight;
		}
	}

	/**
	 * Set the width of the texture. This will update the
	 * ratio also.
	 */
	private void setWidth() {
		if (texWidth != 0) {
			widthRatio = ((float) width) / texWidth;
		}
	}

}