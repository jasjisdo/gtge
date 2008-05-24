package gameeditor.mapeditor;

import java.io.*;
import javax.swing.filechooser.FileFilter;
import java.util.HashSet;
import java.util.Iterator;
public class GenericFilter extends FileFilter {
	HashSet<String> ext = new HashSet<String>();
	public GenericFilter(String s){
		ext.add(s);
	}
	public GenericFilter(String[] s){
		setExtensions(s);
	}
	public GenericFilter(HashSet<String> hash){
		ext=hash;
	}
	public void addExtension(String s){
		ext.add(s);
	}
	public void setExtensions(String[] s){
		ext.clear();
		for(int i = 0;i<s.length;i++)
			ext.add(s[i]);
	}
	public boolean accept(File f) {
		return ext.contains(getExtension(f));
	}
	protected String getExtension(File f){
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1)
			ext = s.substring(i+1).toLowerCase();
		return ext;
	}
	public String getDescription() {
		StringBuffer strBuff = new StringBuffer();
		Iterator<String> it = ext.iterator();
		strBuff.append("(");
		while(it.hasNext())
			strBuff.append("*."+it.next()+";");
		return strBuff.toString()+")";
	}
}