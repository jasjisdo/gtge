package gameeditor.util;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.util.HashMap;
import java.awt.Frame;
public class FrameUtil {
	static HashMap<String,String> LAFNameHash = new HashMap<String,String>();
	private static String[] lafNames;
	static{
		LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		lafNames=new String[lafInfo.length];
		for(int i = 0;i<lafInfo.length;i++){
			LAFNameHash.put(lafNames[i]=lafInfo[i].getName(),lafInfo[i].getClassName());
		}
	}
	public static void setNativeLAF() {
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch(Exception e) {
	      System.out.println("Error setting native LAF: " + e);
	    }
	}
	public static String getLAFClassName(String lafName){
		return LAFNameHash.get(lafName);
	}
	public static void setLAFByName(String lafName){
		if(!LAFNameHash.containsValue(lafName)){
			System.out.println("Could not find look and feel "+lafName+".");
			return;
		}
		try{
			UIManager.setLookAndFeel(lafName);
		}catch(Exception e){
			System.out.println("Failed to set look and feel "+lafName+".");
			System.out.println("Details:\n"+e);
			e.printStackTrace();
		}
	}
	public static JMenu getSkinMenu(Frame f){
		JMenu menu = new JMenu("Set skin");
		ButtonGroup b = new ButtonGroup();
		JCheckBoxMenuItem item;
		GUISkinListener skinListener = new GUISkinListener(f);
		for(int i = 0;i<lafNames.length;i++){
			item = new JCheckBoxMenuItem(lafNames[i]);
			b.add(item);
			item.addActionListener(skinListener);
			menu.add(item);
		}
		return menu;
	}
	public static String[] getLAFNames(){
		return lafNames;
	}
}
