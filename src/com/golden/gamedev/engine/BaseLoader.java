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
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

// GTGE
import com.golden.gamedev.engine.BaseIO;
import com.golden.gamedev.util.ImageUtil;


/**
 * Class for loading and masking images, and also behave as storage of the
 * loaded images. <p>
 *
 * Supported image format: png (*.png), gif (*.gif), and jpeg (*.jpg). <p>
 *
 * <code>BaseLoader</code> class is using functions from
 * {@link com.golden.gamedev.util.ImageUtil} class for loading and masking
 * images in convenient way. <p>
 *
 * This class is using {@link BaseIO} to get the external resources.
 *
 * @see com.golden.gamedev.util.ImageUtil
 */
public class BaseLoader {


 /**************************** LOADER PROPERTIES *****************************/

    // Base IO to get external resources
	private BaseIO 	base;

    // masking color
	private Color 	maskColor;


 /****************************** IMAGE STORAGE *******************************/

	// store single image
	private Map		imageBank;

	// store multiple images
	private Map		imagesBank;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Constructs new <code>BaseLoader</code> with specified I/O loader, and
	 * masking color. <p>
	 *
	 * Masking color is the color of the images that will be converted to
	 * transparent.
	 *
	 * @param base		I/O resource loader
	 * @param maskColor	the mask color
	 */
    public BaseLoader(BaseIO base, Color maskColor) {
        this.base = base;
        this.maskColor = maskColor;

        imageBank = new HashMap(5);
        imagesBank = new HashMap(30);
    }


 /****************************************************************************/
 /************************* INSERTION OPERATION ******************************/
 /****************************************************************************/

	/**
	 * Loads and returns an image from the file location. If useMask is set
	 * to true, then the default masking colour will be used. Images that have
	 * been previously loaded will return immediately from the image cache.
	 *
	 * @param imagefile The image filename to be loaded
	 * @param useMask If true, then the image is loaded using the default transparent
	 * color
	 * @return Requested image.
	 */
    public BufferedImage getImage(String imagefile, boolean useMask) {
		BufferedImage image = (BufferedImage) imageBank.get(imagefile);

		if (image == null) {
			URL url = base.getURL(imagefile);

			image = (useMask) ?
				ImageUtil.getImage(url, maskColor) :
				ImageUtil.getImage(url);

			imageBank.put(imagefile, image);
		}

        return image;
    }

    /**
     * Loads and returns an image with specified file using masking color.
     * Image that have been loaded before will return immediately from cache.
     *
     * @param imagefile	the image filename to be loaded
     * @return Requested image.
     *
     * @see #getImage(String, boolean)
     */
    public BufferedImage getImage(String imagefile) {
        return getImage(imagefile, true);
    }

    /**
     * Loads and returns image strip with specified file and whether using
     * masking color or not. Images that have been loaded before will return
     * immediately from cache.
     *
     * @param imagefile the image filename to be loaded
     * @param col 		image strip column
     * @param row 		image strip row
     * @param useMask 	true, the image is using transparent color
     * @return Requested image.
     */
    public BufferedImage[] getImages(String imagefile, int col, int row,
									 boolean useMask) {
        BufferedImage[] image = (BufferedImage[]) imagesBank.get(imagefile);

        if (image == null) {
	        URL url = base.getURL(imagefile);

			image = (useMask) ?
				ImageUtil.getImages(url, col, row, maskColor) :
				ImageUtil.getImages(url, col, row);

            imagesBank.put(imagefile, image);
        }

        return image;
    }

    /**
     * Loads and returns image strip with specified file using  masking color.
     * Images that have been loaded before will return immediately from cache.
     *
     * @param imagefile the image filename to be loaded
     * @param col 		image strip column
     * @param row 		image strip row
     * @return Requested image.
     *
     * @see #getImages(String, int, int, boolean)
     */
    public BufferedImage[] getImages(String imagefile, int col, int row) {
        return getImages(imagefile, col, row, true);
    }


 /****************************************************************************/
 /************************** REMOVAL OPERATION *******************************/
 /****************************************************************************/

	/**
	 * Removes specified image from cache.
	 */
	public boolean removeImage(BufferedImage image) {
		Iterator it = imageBank.values().iterator();

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
	 */
	public boolean removeImages(BufferedImage[] images) {
		Iterator it = imagesBank.values().iterator();

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
	 */
	public BufferedImage removeImage(String imagefile) {
		return (BufferedImage) imageBank.remove(imagefile);
	}

	/**
	 * Removes images with specified image filename from cache.
	 */
	public BufferedImage[] removeImages(String imagefile) {
		return (BufferedImage[]) imagesBank.remove(imagefile);
	}

	/**
	 * Clear all cached images.
	 */
	public void clearCache() {
		imageBank.clear();
		imagesBank.clear();
	}


 /****************************************************************************/
 /*************************** CUSTOM OPERATION *******************************/
 /****************************************************************************/

	/**
	 * Stores image into cache with specified key.
	 */
	public void storeImage(String key, BufferedImage image) {
		if (imageBank.get(key) != null) {
			throw new ArrayStoreException("Key -> " + key + " is bounded to " +
										  imageBank.get(key));
		}

		imageBank.put(key, image);
	}

	/**
	 * Stores images into cache with specified key.
	 */
	public void storeImages(String key, BufferedImage[] images) {
		if (imagesBank.get(key) != null) {
			throw new ArrayStoreException("Key -> " + key + " is bounded to " +
										  imagesBank.get(key));
		}

		imagesBank.put(key, images);
	}

	/**
	 * Returns cache image with specified key.
	 */
	public BufferedImage getStoredImage(String key) {
		return (BufferedImage) imageBank.get(key);
	}

	/**
	 * Returns cache images with specified key.
	 */
	public BufferedImage[] getStoredImages(String key) {
		return (BufferedImage[]) imagesBank.get(key);
	}


 /****************************************************************************/
 /************************ BASE LOADER PROPERTIES ****************************/
 /****************************************************************************/

	/**
	 * Returns <code>BaseIO</code> associated with this image loader.
	 *
	 * @see #setBaseIO(BaseIO)
	 */
	public BaseIO getBaseIO() {
		return base;
	}

	/**
	 * Sets <code>BaseIO</code> where the image resources is loaded from.
	 */
	public void setBaseIO(BaseIO base) {
		this.base = base;
	}

    /**
     * Returns image loader masking color.
     *
     * @see #setMaskColor(Color)
     */
	public Color getMaskColor() {
		return maskColor;
	}

	/**
	 * Sets image loader masking color. <p>
	 *
	 * Masking color is the color of the images that will be converted to
	 * transparent.
	 *
	 * @see #getMaskColor()
	 */
	public void setMaskColor(Color c) {
		maskColor = c;
	}

	public String toString() {
		StringBuffer imageKey = new StringBuffer(),
					 imagesKey = new StringBuffer();

		Iterator imageIt = imageBank.keySet().iterator(),
				 imagesIt = imagesBank.keySet().iterator();

		imageKey.append("\"");
		while (imageIt.hasNext()) {
			imageKey.append(imageIt.next());

			if (imageIt.hasNext()) imageKey.append(",");
		}
		imageKey.append("\"");

		imagesKey.append("\"");
		while (imagesIt.hasNext()) {
			String key = (String) imagesIt.next();
			BufferedImage[] image = (BufferedImage[]) imagesBank.get(key);
			int len = (image == null) ? -1 : image.length;
			imagesKey.append(key).append("(").append(len).append(")");

			if (imagesIt.hasNext()) imagesKey.append(",");
		}
		imagesKey.append("\"");

		return super.toString() + " " +
			"[maskColor=" + maskColor +
			", BaseIO=" + base +
			", imageLoaded=" + imageKey +
			", imagesLoaded=" + imagesKey + "]";
	}

}