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
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Class to get external resources object, such as <code>java.io.File</code>,
 * <code>java.io.InputStream</code>, and <code>java.net.URL</code>.
 * <p>
 * 
 * There are four types mode of how <code>BaseIO</code> getting the external
 * resources object : <br>
 * <ul>
 * <li>{@link #CLASS_URL}</li>
 * <li>{@link #WORKING_DIRECTORY}</li>
 * <li>{@link #SYSTEM_LOADER}, and</li>
 * <li>{@link #CLASS_LOADER}</li>
 * </ul>
 * <p>
 * 
 * By default <code>BaseIO</code> class is using <code>CLASS_URL</code>.
 */
public class BaseIO {
	
	/** ************************* IO MODE CONSTANTS ***************************** */
	
	/**
	 * IO mode constant for class url.
	 */
	public static final int CLASS_URL = 1;
	
	/**
	 * IO mode constant for working directory.
	 */
	public static final int WORKING_DIRECTORY = 2;
	
	/**
	 * IO mode constant for class loader.
	 */
	public static final int CLASS_LOADER = 3;
	
	/**
	 * IO mode constant for system loader.
	 */
	public static final int SYSTEM_LOADER = 4;
	
	/** ************************* BASE CLASS LOADER ***************************** */
	
	private Class base;
	private ClassLoader loader;
	private int mode;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Construct new <code>BaseIO</code> with specified class as the base
	 * loader, and specified IO mode (one of {@link #CLASS_URL},
	 * {@link #WORKING_DIRECTORY}, {@link #CLASS_LOADER}, or
	 * {@link #SYSTEM_LOADER}).
	 * 
	 * @param base the base class loader
	 * @param mode one of IO mode constants
	 * @see #CLASS_URL
	 * @see #WORKING_DIRECTORY
	 * @see #CLASS_LOADER
	 * @see #SYSTEM_LOADER
	 */
	public BaseIO(Class base, int mode) {
		this.base = base;
		this.loader = base.getClassLoader();
		this.mode = mode;
	}
	
	/**
	 * Construct new <code>BaseIO</code> with specified class as the base
	 * loader using {@link #CLASS_URL} mode as the default.
	 * 
	 * @param base the base class loader
	 */
	public BaseIO(Class base) {
		this(base, BaseIO.CLASS_URL);
	}
	
	/** ************************************************************************* */
	/** ***************************** INPUT URL ********************************* */
	/** ************************************************************************* */
	
	/**
	 * Returns URL from specified path with specified mode.
	 * @param path The path to get the URL from.
	 * @param mode The mode to retrieve the URL.
	 * @return The {@link URL}.
	 * @see #CLASS_LOADER
	 * @see #CLASS_URL
	 * @see #SYSTEM_LOADER
	 * @see #WORKING_DIRECTORY
	 */
	public URL getURL(String path, int mode) {
		URL url = null;
		
		try {
			switch (mode) {
				case CLASS_URL:
					url = this.base.getResource(path);
					break;
				
				case WORKING_DIRECTORY:
					File f = new File(path);
					if (f.exists()) {
						url = f.toURL();
					}
					break;
				
				case CLASS_LOADER:
					url = this.loader.getResource(path);
					break;
				
				case SYSTEM_LOADER:
					url = ClassLoader.getSystemResource(path);
					break;
			}
		}
		catch (Exception e) {
		}
		
		if (url == null) {
			throw new RuntimeException(this.getException(path, mode, "getURL"));
		}
		
		return url;
	}
	
	/**
	 * Returns URL from specified path with this {@link BaseIO} default mode.
	 * @param path The path to retrieve the URL from.
	 * @return The {@link URL} of the given path.
	 * @see #getMode()
	 */
	public URL getURL(String path) {
		URL url = null;
		
		try {
			url = this.getURL(path, this.mode);
		}
		catch (Exception e) {
		}
		
		if (url == null) {
			// smart resource locater
			int smart = 0;
			while (url == null
			        && !this.getModeString(++smart).equals("[UNKNOWN-MODE]")) {
				try {
					url = this.getURL(path, smart);
				}
				catch (Exception e) {
				}
			}
			
			if (url == null) {
				throw new RuntimeException(this.getException(path, this.mode,
				        "getURL"));
			}
			
			this.mode = smart;
		}
		
		return url;
	}
	
	/** ************************************************************************* */
	/** **************************** INPUT STREAM ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Returns {@link InputStream} from specified path with specified mode.
	 * @param path The path to retrieve an {@link InputStream} from.
	 * @param mode The mode to use for retrieving the {@link InputStream}.
	 * @return The {@link InputStream}.
	 * @see #CLASS_LOADER
	 * @see #CLASS_URL
	 * @see #SYSTEM_LOADER
	 * @see #WORKING_DIRECTORY
	 */
	public InputStream getStream(String path, int mode) {
		InputStream stream = null;
		
		try {
			switch (mode) {
				case CLASS_URL:
					stream = this.base.getResourceAsStream(path);
					break;
				
				case WORKING_DIRECTORY:
					stream = new File(path).toURL().openStream();
					break;
				
				case CLASS_LOADER:
					stream = this.loader.getResourceAsStream(path);
					break;
				
				case SYSTEM_LOADER:
					stream = ClassLoader.getSystemResourceAsStream(path);
					break;
			}
		}
		catch (Exception e) {
		}
		
		if (stream == null) {
			throw new RuntimeException(this.getException(path, mode,
			        "getStream"));
		}
		
		return stream;
	}
	
	/**
	 * Returns input stream from specified path with this <code>BaseIO</code>
	 * default mode.
	 * @param path The path to retrieve an {@link InputStream} from.
	 * @return The {@link InputStream}.
	 * @see #getMode()
	 */
	public InputStream getStream(String path) {
		InputStream stream = null;
		
		try {
			stream = this.getStream(path, this.mode);
		}
		catch (Exception e) {
		}
		
		if (stream == null) {
			// smart resource locater
			int smart = 0;
			while (stream == null
			        && !this.getModeString(++smart).equals("[UNKNOWN-MODE]")) {
				try {
					stream = this.getStream(path, smart);
				}
				catch (Exception e) {
				}
			}
			
			if (stream == null) {
				throw new RuntimeException(this.getException(path, this.mode,
				        "getStream"));
			}
			
			this.mode = smart;
		}
		
		return stream;
	}
	
	/** ************************************************************************* */
	/** ***************************** INPUT FILE ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Return file from specified path with specified mode.
	 * @param path The path to retrieve an {@link File} from.
	 * @param mode The mode to use for retrieving the {@link File}.
	 * @return The {@link File}.
	 * @see #CLASS_LOADER
	 * @see #CLASS_URL
	 * @see #SYSTEM_LOADER
	 * @see #WORKING_DIRECTORY
	 */
	public File getFile(String path, int mode) {
		File file = null;
		
		try {
			switch (mode) {
				case CLASS_URL:
					file = new File(this.base.getResource(path).getFile()
					        .replaceAll("%20", " "));
					break;
				
				case WORKING_DIRECTORY:
					file = new File(path);
					break;
				
				case CLASS_LOADER:
					file = new File(this.loader.getResource(path).getFile()
					        .replaceAll("%20", " "));
					break;
				
				case SYSTEM_LOADER:
					file = new File(ClassLoader.getSystemResource(path)
					        .getFile().replaceAll("%20", " "));
					break;
			}
		}
		catch (Exception e) {
		}
		
		if (file == null) {
			throw new RuntimeException(this.getException(path, mode, "getFile"));
		}
		
		return file;
	}
	
	/**
	 * Returns file from specified path with this <code>BaseIO</code> default
	 * mode.
	 * <p>
	 * 
	 * File object usually used only for writing to disk.
	 * <p>
	 * 
	 * <b>Caution:</b> always try to avoid using <code>java.io.File</code>
	 * object (this method), because <code>java.io.File</code> is system
	 * dependent and not working inside jar file, use <code>java.net.URL</code>
	 * OR <code>java.io.InputStream</code> instead.
	 * <p>
	 * @param path The path to retrieve an {@link File} from.
	 * @return The {@link File}.
	 * @see #getURL(String)
	 * @see #getStream(String)
	 * @see #setFile(String)
	 * @see #getMode()
	 */
	public File getFile(String path) {
		File file = null;
		
		try {
			file = this.getFile(path, this.mode);
		}
		catch (Exception e) {
		}
		
		if (file == null) {
			// smart resource locater
			int smart = 0;
			while (file == null
			        && !this.getModeString(++smart).equals("[UNKNOWN-MODE]")) {
				try {
					file = this.getFile(path, smart);
				}
				catch (Exception e) {
				}
			}
			
			if (file == null) {
				throw new RuntimeException(this.getException(path, this.mode,
				        "getFile"));
			}
			
			this.mode = smart;
		}
		
		return file;
	}
	
	/** ************************************************************************* */
	/** *************************** OUTPUT FILE ********************************* */
	/** ************************************************************************* */
	
	/**
	 * Returns file on specified path with specified mode for processing.
	 * @param path The path to retrieve a {@link File} from.
	 * @param mode The mode to use for retrieving the {@link File}.
	 * @return The {@link File}.
	 * @see #CLASS_LOADER
	 * @see #CLASS_URL
	 * @see #SYSTEM_LOADER
	 * @see #WORKING_DIRECTORY
	 */
	public File setFile(String path, int mode) {
		File file = null;
		
		try {
			switch (mode) {
				case CLASS_URL:
					file = new File(this.base.getResource("").getFile()
					        .replaceAll("%20", " ")
					        + File.separator + path);
					break;
				
				case WORKING_DIRECTORY:
					file = new File(path);
					break;
				
				case CLASS_LOADER:
					file = new File(this.loader.getResource("").getFile()
					        .replaceAll("%20", " ")
					        + File.separator + path);
					break;
				
				case SYSTEM_LOADER:
					file = new File(ClassLoader.getSystemResource("").getFile()
					        .replaceAll("%20", " ")
					        + File.separator + path);
					break;
			}
		}
		catch (Exception e) {
		}
		
		if (file == null) {
			throw new RuntimeException(this.getException(path, mode, "setFile"));
		}
		
		return file;
	}
	
	/**
	 * Returns file on specified path with this <code>BaseIO</code> default
	 * mode for processing.
	 * @param path The path to retrieve an {@link File} from.
	 * @return The {@link File}.
	 */
	public File setFile(String path) {
		File file = null;
		
		try {
			file = this.setFile(path, this.mode);
		}
		catch (Exception e) {
		}
		
		if (file == null) {
			// smart resource locater
			int smart = 0;
			while (file == null
			        && !this.getModeString(++smart).equals("[UNKNOWN-MODE]")) {
				try {
					file = this.setFile(path, smart);
				}
				catch (Exception e) {
				}
			}
			
			if (file == null) {
				throw new RuntimeException(this.getException(path, this.mode,
				        "setFile"));
			}
			
			this.mode = smart;
		}
		
		return file;
	}
	
	/** ************************************************************************* */
	/** *********************** IO MODE CONSTANTS ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Returns the root path of this {@link BaseIO} if using specified mode. The
	 * root path is the root where all the resources will be taken from.
	 * <p>
	 * 
	 * For example : <br>
	 * The root path = "c:\games\spaceinvader" <br>
	 * The resource name = "images\background.png" <br>
	 * The resource then will be taken from = <br>
	 * "c:\games\spaceinvader\images\background.png"
	 * @param mode The mode to retrieve root path for.
	 * @return The root path of the given mode.
	 */
	public String getRootPath(int mode) {
		switch (mode) {
			case CLASS_URL:
				return this.base.getResource("").toString();
				
			case WORKING_DIRECTORY:
				return System.getProperty("user.dir") + File.separator;
				
			case CLASS_LOADER:
				return this.loader.getResource("").toString();
				
			case SYSTEM_LOADER:
				return ClassLoader.getSystemResource("").toString();
		}
		
		return "[UNKNOWN-MODE]";
	}
	
	/**
	 * Returns the official statement of specified IO mode, or
	 * <code>[UNKNOWN-MODE]</code> if the IO mode is undefined.
	 * @param mode The mode to get a string representation for.
	 * @return The {@link String} representation fo the given mode.
	 * @see #getMode()
	 */
	public String getModeString(int mode) {
		switch (mode) {
			case CLASS_URL:
				return "Class-URL";
			case WORKING_DIRECTORY:
				return "Working-Directory";
			case CLASS_LOADER:
				return "Class-Loader";
			case SYSTEM_LOADER:
				return "System-Loader";
		}
		
		return "[UNKNOWN-MODE]";
	}
	
	/**
	 * Returns the default IO mode used for getting the resources.
	 * @return The default IO mode.
	 * @see #setMode(int)
	 * @see #getModeString(int)
	 */
	public int getMode() {
		return this.mode;
	}
	
	/**
	 * Sets the default IO mode used for getting the resources.
	 * @param mode The new default io mode.
	 * @see #getMode()
	 * @see #CLASS_URL
	 * @see #WORKING_DIRECTORY
	 * @see #CLASS_LOADER
	 * @see #SYSTEM_LOADER
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	/**
	 * Returns exception string used whenever resource can not be found.
	 * @param path The path that was retrived.
	 * @param mode The io mode used during the exception occured.
	 * @param method The method the exception occured in.
	 * @return The exception description string.
	 */
	protected String getException(String path, int mode, String method) {
		return "Resource not found (" + this + "): " + this.getRootPath(mode)
		        + path;
	}
	
	/** ************************************************************************* */
	/** *********************** BASE CLASS LOADER ******************************* */
	/** ************************************************************************* */
	
	/**
	 * Sets the base class where the resources will be taken from.
	 * @param base The base {@link Class}.
	 * @see #getBase()
	 */
	public void setBase(Class base) {
		this.base = base;
		this.loader = base.getClassLoader();
	}
	
	/**
	 * Returns the base class where the resources will be taken from.
	 * @return The base {@link Class}.
	 * @see #setBase(Class)
	 */
	public Class getBase() {
		return this.base;
	}
	
	/**
	 * Returns the class loader associated with this {@link BaseIO}.
	 * @return The {@link ClassLoader}.
	 * @see #setBase(Class)
	 */
	public ClassLoader getLoader() {
		return this.loader;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + " " + "[mode="
		        + this.getModeString(this.mode) + ", baseClass=" + this.base
		        + ", classLoader=" + this.loader + "]";
	}
	
}
