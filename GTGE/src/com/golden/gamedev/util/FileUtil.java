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
package com.golden.gamedev.util;

// JFC
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

/**
 * Utility class provides functions for processing <code>java.io.File</code>
 * object, such as renaming file extension, reading and writing text file, etc.
 */
public class FileUtil {
	
	private FileUtil() {
	}
	
	/** ************************************************************************* */
	/** ******************** READING/WRITING TEXT FILE ************************** */
	/** ************************************************************************* */
	
	/**
	 * Writes an array of String to specified text file.
	 * 
	 * @param text an array of string to write to the file
	 * @param file a text file to be written the text to
	 * @return true, if the text successfuly write to the file.
	 */
	public static boolean fileWrite(String[] text, File file) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			PrintWriter writeOut = new PrintWriter(out);
			
			// writing text to file
			for (int i = 0; i < text.length; i++) {
				writeOut.println(text[i]);
			}
			
			writeOut.close();
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Read an array of String from specified text file. Returns a
	 * {@linkplain Utility#compactStrings(String[]) compact string}. Each
	 * returned string represent each line in the file.
	 * 
	 * @param file file to be read
	 * @return An array of string read from the file, or null if the file cannot
	 *         be read.
	 */
	public static String[] fileRead(File file) {
		try {
			FileReader in = new FileReader(file);
			BufferedReader readIn = new BufferedReader(in);
			
			ArrayList list = new ArrayList(50);
			String data;
			
			// reading text
			while ((data = readIn.readLine()) != null) {
				list.add(data);
			}
			
			readIn.close();
			
			return Utility.compactStrings((String[]) list
			        .toArray(new String[0]));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Read an array of String from specified stream. Returns a
	 * {@linkplain Utility#compactStrings(String[]) compact string}. Each
	 * returned string represent each line in the file.
	 * 
	 * @param stream input stream to be read
	 * @return An array of string read from the stream, or null if the stream
	 *         cannot be read.
	 */
	public static String[] fileRead(InputStream stream) {
		try {
			InputStreamReader in = new InputStreamReader(stream);
			BufferedReader readIn = new BufferedReader(in);
			
			ArrayList list = new ArrayList(50);
			String data;
			
			// reading text
			while ((data = readIn.readLine()) != null) {
				list.add(data);
			}
			
			readIn.close();
			return Utility.compactStrings((String[]) list
			        .toArray(new String[0]));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Read an array of String from specified URL. Returns a
	 * {@linkplain Utility#compactStrings(String[]) compact string}. Each
	 * returned string represent each line in the file.
	 * 
	 * @param url url to be read
	 * @return An array of string read from the stream, or null if the url
	 *         cannot be read.
	 */
	public static String[] fileRead(URL url) {
		try {
			return FileUtil.fileRead(url.openStream());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** ************************************************************************* */
	/** ************************ FILE PROCESSING ******************************** */
	/** ************************************************************************* */
	
	/**
	 * Sets file extension.
	 * <p>
	 * 
	 * For example: <br>
	 * Renaming <code>"paul.dat"</code> to <code>"paul.bin"</code>:
	 * 
	 * <pre>
	 * File f = new File(&quot;paul.dat&quot;);
	 * File newFile = setExtension(f, &quot;bin&quot;);
	 * </pre>
	 * 
	 * @param f file that it's extension to be renamed
	 * @param ext the new file extension
	 * @return The file with new extension.
	 */
	public static File setExtension(File f, String ext) {
		String s = f.getAbsolutePath();
		int i = s.lastIndexOf('.');
		if (i < 0) {
			// file f doesn't have extension
			return new File(s + "." + ext);
			
		}
		else {
			return new File(s.substring(0, i) + "." + ext);
		}
	}
	
	/**
	 * Returns extension of a file. <br>
	 * A file with name <code>"paul.dat"</code> will return <code>"dat"</code>.
	 * 
	 * @param f file to get it's extension
	 * @return The file extension.
	 */
	public static String getExtension(File f) {
		return FileUtil.getExtension(f.getName());
	}
	
	/**
	 * Returns extension of a string. <br>
	 * A string with <code>"paul.dat"</code> will return <code>"dat"</code>.
	 * 
	 * @param st string to get its extension
	 * @return The file extension.
	 */
	public static String getExtension(String st) {
		int index = FileUtil.getIndex(st);
		String s = (index <= 0) ? st : st.substring(index + 1);
		String ext = "";
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			// check is file extension exists
			ext = s.substring(i + 1);
		}
		
		return ext;
	}
	
	/**
	 * Returns name of a file without its extension. <br>
	 * A file with name <code>"paul.dat"</code> will return
	 * <code>"paul"</code>.
	 * 
	 * @param f file to get it's name
	 * @return The file name.
	 */
	public static String getName(File f) {
		return FileUtil.getName(f.getName());
	}
	
	/**
	 * Returns name of a string without its extension. <br>
	 * A string <code>"paul.dat"</code> will return <code>"paul"</code>.
	 * 
	 * @param st string to get its name
	 * @return The file name.
	 */
	public static String getName(String st) {
		int index = FileUtil.getIndex(st);
		String s = (index <= 0) ? st : st.substring(index + 1);
		String name = s;
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length()) {
			name = s.substring(0, i);
		}
		
		return name;
	}
	
	/**
	 * Returns the path of specified file. <br>
	 * A file with path <code>"c:\src\res\paul.dat"</code> will return
	 * <code>"c:\src\res\"</code>.
	 * 
	 * @param f file to get its path
	 * @return The file path.
	 */
	public static String getPath(File f) {
		try {
			return FileUtil.getPath(f.getCanonicalPath());
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns path of a string. <br>
	 * A string <code>"c:\src\res\paul.dat"</code> will return
	 * <code>"c:\src\res\"</code>.
	 * 
	 * @param st string to get its path
	 * @return The file path.
	 */
	public static String getPath(String st) {
		String path = "";
		int index = FileUtil.getIndex(st);
		if (index > 0) {
			path = st.substring(0, index + 1);
		}
		
		return path;
	}
	
	/**
	 * Returns path and name of a file without its extension. <br>
	 * A file with path <code>"c:\src\res\paul.dat"</code> will return
	 * <code>"c:\src\res\paul"</code>.
	 * 
	 * @param f file to get its pathname
	 * @return The file pathname.
	 */
	public static String getPathName(File f) {
		try {
			return FileUtil.getPathName(f.getCanonicalPath());
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns path and name of a string without its extension. <br>
	 * A string <code>"c:\src\res\paul.dat"</code> will return
	 * <code>"c:\src\res\paul"</code>.
	 * 
	 * @param st string to get its pathname
	 * @return The file pathname.
	 */
	public static String getPathName(String st) {
		String path = "";
		int index = FileUtil.getIndex(st);
		if (index > 0) {
			path = st.substring(0, index + 1);
		}
		else {
			index = 0;
		}
		
		String s = (index <= 0) ? st : st.substring(index + 1);
		String name = s;
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length()) {
			name = s.substring(0, i);
		}
		
		return path + name;
	}
	
	private static int getIndex(String st) {
		int index = st.lastIndexOf('/');
		if (index < 0) {
			index = st.lastIndexOf(File.separatorChar);
		}
		
		return index;
	}
	
}
