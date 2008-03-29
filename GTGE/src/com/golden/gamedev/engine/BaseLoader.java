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
package com.golden.gamedev.engine;

// JFC
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.golden.gamedev.util.ImageUtil;

/**
 * Class for loading and masking images, and also behave as storage of the
 * loaded images.
 * <p>
 * 
 * Supported image format: png (*.png), gif (*.gif), and jpeg (*.jpg).
 * <p>
 * 
 * <code>BaseLoader</code> class is using functions from
 * {@link com.golden.gamedev.util.ImageUtil} class for loading and masking
 * images in convenient way.
 * <p>
 * 
 * This class is using {@link BaseIO} to get the external resources.
 * 
 * @see com.golden.gamedev.util.ImageUtil
 */
public class BaseLoader {
	
	/** ************************** LOADER PROPERTIES **************************** */
	
	// Base IO to get external resources
	private BaseIO base;
	
	// masking color
	private Color maskColor;
	
	/** **************************** IMAGE STORAGE ****************************** */
	
	// store single image
	private Map imageBank;
	
	// store multiple images
	private Map imagesBank;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Constructs new <code>BaseLoader</code> with specified I/O loader, and
	 * masking color.
	 * <p>
	 * 
	 * Masking color is the color of the images that will be converted to
	 * transparent.
	 * 
	 * @param base I/O resource loader
	 * @param maskColor the mask color
	 */
	public BaseLoader(BaseIO base, Color maskColor) {
		this.base = base;
		this.maskColor = maskColor;
		
		this.imageBank = new HashMap(5);
		this.imagesBank = new HashMap(30);
	}
	
	/** ************************************************************************* */
	/** *********************** INSERTION OPERATION ***************************** */
	/** ************************************************************************* */
	
	/**
	 * Loads and returns an image from the file location. If useMask is set to
	 * true, then the default masking colour will be used. Images that have been
	 * previously loaded will return immediately from the image cache.
	 * 
	 * @param imagefile The image filename to be loaded
	 * @param useMask If true, then the image is loaded using the default
	 *        transparent color
	 * @return Requested image.
	 */
	public BufferedImage getImage(String imagefile, boolean useMask) {
		BufferedImage image = (BufferedImage) this.imageBank.get(imagefile);
		
		if (image == null) {
			URL url = this.base.getURL(imagefile);
			
			image = (useMask) ? ImageUtil.getImage(url, this.maskColor)
			        : ImageUtil.getImage(url);
			
			this.imageBank.put(imagefile, image);
		}
		
		return image;
	}
	
	/**
	 * Loads and returns an image with specified file using masking color. Image
	 * that have been loaded before will return immediately from cache.
	 * 
	 * @param imagefile the image filename to be loaded
	 * @return Requested image.
	 * 
	 * @see #getImage(String, boolean)
	 */
	public BufferedImage getImage(String imagefile) {
		return this.getImage(imagefile, true);
	}
	
	/**
	 * Loads and returns image strip with specified file and whether using
	 * masking color or not. Images that have been loaded before will return
	 * immediately from cache.
	 * 
	 * @param imagefile the image filename to be loaded
	 * @param col image strip column
	 * @param row image strip row
	 * @param useMask true, the image is using transparent color
	 * @return Requested image.
	 */
	public BufferedImage[] getImages(String imagefile, int col, int row, boolean useMask) {
		BufferedImage[] image = (BufferedImage[]) this.imagesBank
		        .get(imagefile);
		
		if (image == null) {
			URL url = this.base.getURL(imagefile);
			
			image = (useMask) ? ImageUtil.getImages(url, col, row,
			        this.maskColor) : ImageUtil.getImages(url, col, row);
			
			this.imagesBank.put(imagefile, image);
		}
		
		return image;
	}
	
	/**
	 * Loads and returns image strip with specified file using masking color.
	 * Images that have been loaded before will return immediately from cache.
	 * 
	 * @param imagefile the image filename to be loaded
	 * @param col image strip column
	 * @param row image strip row
	 * @return Requested image.
	 * 
	 * @see #getImages(String, int, int, boolean)
	 */
	public BufferedImage[] getImages(String imagefile, int col, int row) {
		return this.getImages(imagefile, col, row, true);
	}
	
	/** ************************************************************************* */
	/** ************************ REMOVAL OPERATION ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Removes specified image from cache.
	 * @param image The image to remove from cache.
	 * @return If removing the image from cache worked.
	 */
	public boolean removeImage(BufferedImage image) {
		Iterator it = this.imageBank.values().iterator();
		
		while (it.hasNext()) {
			if (it.next() == image) {
				it.remove();
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes specified images from cache.
	 * @param images The images to remove from cache.
	 * @return If removing the images from cache worked.
	 */
	public boolean removeImages(BufferedImage[] images) {
		Iterator it = this.imagesBank.values().iterator();
		
		while (it.hasNext()) {
			if (it.next() == images) {
				it.remove();
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes image with specified image filename from cache.
	 * @param imagefile The file name of the image to remove.
	 * @return The removed image.
	 */
	public BufferedImage removeImage(String imagefile) {
		return (BufferedImage) this.imageBank.remove(imagefile);
	}
	
	/**
	 * Removes images with specified image filename from cache.
	 * @param imagefile The file name of the image to remove.
	 * @return The removed images.
	 */
	public BufferedImage[] removeImages(String imagefile) {
		return (BufferedImage[]) this.imagesBank.remove(imagefile);
	}
	
	/**
	 * Clear all cached images.
	 */
	public void clearCache() {
		this.imageBank.clear();
		this.imagesBank.clear();
	}
	
	/** ************************************************************************* */
	/** ************************* CUSTOM OPERATION ****************************** */
	/** ************************************************************************* */
	
	/**
	 * Stores image into cache with specified key.
	 * @param key The key used to store the image.
	 * @param image The image to store.
	 */
	public void storeImage(String key, BufferedImage image) {
		if (this.imageBank.get(key) != null) {
			throw new ArrayStoreException("Key -> " + key + " is bounded to "
			        + this.imageBank.get(key));
		}
		
		this.imageBank.put(key, image);
	}
	
	/**
	 * Stores images into cache with specified key.
	 * @param key The key used to store the images.
	 * @param images The images to store.
	 */
	public void storeImages(String key, BufferedImage[] images) {
		if (this.imagesBank.get(key) != null) {
			throw new ArrayStoreException("Key -> " + key + " is bounded to "
			        + this.imagesBank.get(key));
		}
		
		this.imagesBank.put(key, images);
	}
	
	/**
	 * Returns cache image with specified key.
	 * @param key The key of the image wanted.
	 * @return The image with the given key or <code>null</code>.
	 */
	public BufferedImage getStoredImage(String key) {
		return (BufferedImage) this.imageBank.get(key);
	}
	
	/**
	 * Returns cache images with specified key.
	 * @param key The key of the images wanted.
	 * @return The images with the given key.
	 */
	public BufferedImage[] getStoredImages(String key) {
		return (BufferedImage[]) this.imagesBank.get(key);
	}
	
	/** ************************************************************************* */
	/** ********************** BASE LOADER PROPERTIES *************************** */
	/** ************************************************************************* */
	
	/**
	 * Returns {@link BaseIO} associated with this image loader.
	 * @return The {@link BaseIO} used by the loader.
	 * @see #setBaseIO(BaseIO)
	 */
	public BaseIO getBaseIO() {
		return this.base;
	}
	
	/**
	 * Sets {@link BaseIO} where the image resources is loaded from.
	 * @param base The new {@link BaseIO} used by the loader.
	 */
	public void setBaseIO(BaseIO base) {
		this.base = base;
	}
	
	/**
	 * Returns image loader masking color.
	 * @return The masking color.
	 * @see #setMaskColor(Color)
	 */
	public Color getMaskColor() {
		return this.maskColor;
	}
	
	/**
	 * Sets image loader masking color.
	 * <p>
	 * 
	 * Masking color is the color of the images that will be converted to
	 * transparent.
	 * @param c The new masking color.
	 * @see #getMaskColor()
	 */
	public void setMaskColor(Color c) {
		this.maskColor = c;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer imageKey = new StringBuffer(), imagesKey = new StringBuffer();
		
		Iterator imageIt = this.imageBank.keySet().iterator(), imagesIt = this.imagesBank
		        .keySet().iterator();
		
		imageKey.append("\"");
		while (imageIt.hasNext()) {
			imageKey.append(imageIt.next());
			
			if (imageIt.hasNext()) {
				imageKey.append(",");
			}
		}
		imageKey.append("\"");
		
		imagesKey.append("\"");
		while (imagesIt.hasNext()) {
			String key = (String) imagesIt.next();
			BufferedImage[] image = (BufferedImage[]) this.imagesBank.get(key);
			int len = (image == null) ? -1 : image.length;
			imagesKey.append(key).append("(").append(len).append(")");
			
			if (imagesIt.hasNext()) {
				imagesKey.append(",");
			}
		}
		imagesKey.append("\"");
		
		return super.toString() + " " + "[maskColor=" + this.maskColor
		        + ", BaseIO=" + this.base + ", imageLoaded=" + imageKey
		        + ", imagesLoaded=" + imagesKey + "]";
	}
	
}
