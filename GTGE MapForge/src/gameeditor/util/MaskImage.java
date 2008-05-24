package gameeditor.util;
import com.pixelface.util.*;
import com.golden.gamedev.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
public class MaskImage {
	public MaskImage(String path,Color mask,String name){
		BufferedImage img=com.pixelface.util.ImageUtil.getImage(path,mask);
		try{
			com.golden.gamedev.util.ImageUtil.saveImage(img,new File(name));
		}catch(Exception e){
			System.out.println(e);
		}
	}
	public static void main(String[] a){
		String path="C:/Documents and Settings/William Morrison/Desktop/";
		new MaskImage(path+"ToggleButtons.PNG",
				new Color(255,0,255),path+"img.PNG");
	}
}
