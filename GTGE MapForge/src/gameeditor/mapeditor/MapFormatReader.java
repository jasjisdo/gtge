package gameeditor.mapeditor;
//JRE
import javax.swing.JOptionPane;
import java.util.*;
import java.io.*;
import java.awt.Point;
import java.util.Scanner;

//GTGE
import com.golden.gamedev.util.ImageUtil;
public class MapFormatReader {
	Vector<String> format = new Vector<String>();
	static final String[] mapSpecs= new String[]{"Start Location","Map Image","Map Music",
		"Layer 1 Data","Layer 2 Data","Layer 3 Data","Delineator:"};
	static HashSet<String>hash;
	static{ 
		hash = new HashSet<String>();
		for(int i = 0;i<mapSpecs.length;i++)
			hash.add(mapSpecs[i]);
	}
	/**
	 * Return map data formated in the specified format
	 */
	public static String read(Vector<String> format,MapEditorGUI editor){
		String command="";
		StringBuffer strBuff = new StringBuffer();
		for(int i = 0;i<format.size();i++){
			command=format.get(i);
			String spacer="";
			if(command.length()>11)
				spacer = command.substring(0,11);
			if(hash.contains(command) || hash.contains(spacer)){
			if(command.equals("Start Location")){
				if(editor.mapPane.startLocation!=null)
					strBuff.append(editor.mapPane.startLocation.x+" "+editor.mapPane.startLocation.y);
			}else if(command.equals("Map Image")){
				strBuff.append(editor.mapPane.imgPath);
			
			}else if(command.equals("Map Music")){
				strBuff.append(editor.mapPane.mscPath);
		
			}else if(command.equals("Layer 1 Data")){
				strBuff.append(getLayer(editor.mapPane.lay1));
				//checkNextCommand(format,i+1,strBuff);
			}else if(command.equals("Layer 2 Data")){
				strBuff.append(getLayer(editor.mapPane.lay2));
				//checkNextCommand(format,i+1,strBuff);
			}else if(command.equals("Layer 3 Data")){
				strBuff.append(getLayer(editor.mapPane.lay3));
				//checkNextCommand(format,i+1,strBuff);
			}else if(command.equals("Map Dimensions in Tiles")){
				strBuff.append(editor.mapPane.lay1[0].length+" "+editor.mapPane.lay1.length);
			}else{
				strBuff.append(command.substring(11));
			}
			strBuff.append("\n");
		}
		}
		return strBuff.toString();
	}
	/**
	 * Returns whether or not a string is a valid map format argument.
	 * Used in loaded previously saved map formats to determine validity
	 * of data. A map format argument is valid if its total value equals
	 * any of the known map format arguments, or if its first 11 letters
	 * are "Delineator:"
	 */
	public static boolean isValidFormatArg(String arg){
		String s = "";
		if(arg.length()>11)
			s=arg.substring(0,11);
		return hash.contains(s) || hash.contains(arg);
	}
	/**
	 * Initializes map variables using a specified format.
	 */
	public static void openWithFormat(Vector<String> format,MapEditorGUI editor,File f){
		Vector<String> problems = new Vector<String>();
		int line=0;
		boolean lay1=false,lay2=false,lay3=false;
		try{
			Scanner scan = new Scanner(f);
			for(int i = 0;i<format.size();i++){
			String s = format.get(i);
			System.out.println(s);
			if(s.equals("Start Location")){
				String ss= scan.nextLine();
				if(ss.equals(""))
					problems.add("Starting point undefined.");
				else{
					StringTokenizer token = new StringTokenizer(ss);
					int x = Integer.parseInt(token.nextToken());
					int y = Integer.parseInt(token.nextToken());
					editor.mapPane.startLocation = new Point(x,y);
				}
			}else if(s.equals("Map Image")){
				editor.mapPane.setMapImage(ImageUtil.getImage(editor.getClass().getResource(scan.nextLine())));
			}else if(s.equals("Map Music")){
				editor.mapPane.mscPath=scan.nextLine();
				if(editor.mapPane.mscPath.equals(""))
					problems.add("Map music undefined.");
			}else if(s.equals("Layer 1 Data")){
				editor.mapPane.lay1=fillLayer(scan);
				lay1=true;
			}else if(s.equals("Layer 2 Data")){
				editor.mapPane.lay2=fillLayer(scan);
				lay2=true;
			}else if(s.equals("Layer 3 Data")){
				editor.mapPane.lay3=fillLayer(scan);
				lay3=true;
			}else if(s.equals("Map Dimension in tiles")){
				editor.mapPane.setMapSize(scan.nextInt(),scan.nextInt());
			}else
				if(scan.hasNextLine()){
					System.out.println("Ignore "+scan.nextLine());
				}
		}
		}catch(Exception e){
			JOptionPane.showMessageDialog(editor,"Irresolvable exception opening map "+f.getAbsolutePath()+
					"\nFile's format might differ from the current map loading format."+
					"\nDetails: "+e,
					"Map Format Exception", JOptionPane.WARNING_MESSAGE);
		}
		if(!lay1)
			clearLayer(editor.mapPane.lay1);
		if(!lay2)
			clearLayer(editor.mapPane.lay2);
		if(!lay3)
			clearLayer(editor.mapPane.lay3);
		fitMap(editor.mapPane);
		if(problems.size()>0){
			StringBuffer buff = new StringBuffer();
			for(int i = 0;i<problems.size();i++)
				buff.append(problems.get(i)+"\n");
			JOptionPane.showMessageDialog(editor,buff.toString(),
					f.getName()+" details",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	/*
	 * Called after all variables are initialized.
	 * Map is fit to the appropriate size. We assume the map size has changed.
	 */
	private static void fitMap(MapPane mapPane){
		mapPane.setMapSize(mapPane.lay1[0].length,mapPane.lay1.length);
	}
	/*
	 * Set all indicies of a specified layer to 0.
	 * Used when a map doesn't specify data for a layer.
	 */
	protected static void clearLayer(int[][] lay){
		for(int i = 0;i<lay.length;i++)
			for(int j = 0;j<lay[0].length;j++)
				lay[i][j]=0;
	}
	/*
	 * Returns a 2 dimensional integer filled with data from subsequent
	 * lines in the map file.  Processing ends when the scanner encounters
	 * data that isn't numerical.
	 */
	protected static int[][] fillLayer(Scanner scan){
		StringTokenizer token = new StringTokenizer(scan.nextLine());
		int width = token.countTokens();
		ArrayList<Integer[]> list = new ArrayList<Integer[]>();
		Integer[] layer = new Integer[width];
		int i = -1;
		while(token.hasMoreTokens()){
			layer[++i]=Integer.parseInt(token.nextToken());
			if(!token.hasMoreTokens()){
				if(!scan.hasNextLine())
					break;
				token=new StringTokenizer(scan.nextLine());
				list.add(layer);
				if(token.countTokens()!=width){
					break;
				}
				layer = new Integer[width];
				i=-1;
			}
		}
		int[][] copyLayer = new int[list.size()][width];
		for(i = 0;i<copyLayer.length;i++)
			for(int j = 0;j<copyLayer[0].length;j++)
				copyLayer[i][j]=list.get(i)[j];
		return copyLayer;
	}
	/*
	 * Used when processing map data.  
	 * Returns a string representation of a layer.
	 */
	protected static String getLayer(int[][] layer){
		StringBuffer strBuff = new StringBuffer();
		for(int i = 0;i<layer.length;i++){
			for(int j = 0;j<layer[0].length;j++)
				strBuff.append(layer[i][j]+" ");
			if(i!=layer.length-1)
				strBuff.append("\n");
		}
		return strBuff.toString()+"\n";
	}
}