package gameeditor.mapeditor;
//JRE
import javax.swing.*;
import java.awt.Transparency;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.*;

//GTGE
import com.golden.gamedev.util.ImageUtil;
public class MapToolBar extends JToolBar implements ActionListener{
	final HashSet <String>drawTools= new HashSet<String>(6);
	HashMap<String,Cursor> cursorMap = new HashMap<String,Cursor>(4);
	HashMap<String,ButtonGroup> buttonMap = new HashMap<String,ButtonGroup>();
	public static final String PENCIL="Pencil", FILL="Flood Fill",CUT = "Cut",
		START="Start",ERASE="Erase",EVENTS="Events",SPRAY="Spray",RECTANGLE="Rectangle",
		LINE="Line";
	protected String drawTool = PENCIL;
	final String[] tools = new String[]{FILL,SPRAY,RECTANGLE,LINE,PENCIL,ERASE,CUT,EVENTS};
	MapPane map;
	ButtonGroup layers;
	BufferedImage startImg,eventImg;
	int layerIndex=1;
	public MapToolBar(MapPane mp){
		super("Map Tools");
		this.map=mp;
		layers=new ButtonGroup();
		for(int i = 0;i<tools.length;i++)
			drawTools.add(tools[i]);
		setOrientation(JToolBar.VERTICAL);
		initToolBar();
	}
	public void initToolBar(){
		//this.remove
		BufferedImage[] imgs=null;
		try{
			/*In Jar: "gameeditor/Images/ToggleButtons.PNG"*/
			/*Outside jar: ToggleButtons.PNG*/
			imgs = ImageUtil.splitImages(ImageIO.read(getClass().getClassLoader().
					getResource("gameeditor/Images/ToggleButtons.PNG")), 12,1);
		}catch(IOException e){
			System.out.println("Problem loading MapToolBar images.");
			e.printStackTrace();
		}
		startImg = ImageUtil.resize(imgs[11],32, 32);
		eventImg=ImageUtil.resize(imgs[10], 32, 32);
		String[] texts = new String[]{"Layer 1", "Click to edit layer 1","Layer 2","Click to edit layer 2",
				"Layer 3", "Click to edit layer 3",FILL,"Click to flood map with tiles",SPRAY,"Click & spray tiles randomly",
				RECTANGLE,"Click to draw rectangles of tiles",LINE,"Click to draw line of tiles",PENCIL,"Click to draw tiles",
				ERASE,"Click to erase tiles",CUT,"Click to cut/copy/paste tiles",EVENTS,"Click to place events"};
		ButtonGroup drawTools = new ButtonGroup();
		//Initialize layer buttons
		for(int i = 0;i<3;i++){
			JToggleButton b = createButton(imgs[i],texts[i*2],texts[i*2+1]);
			layers.add(b);
			add(b);
		}
		addSeparator();
		//set first selected.
		layers.getElements().nextElement().setSelected(true);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Point hotspot = new Point(0,0);
		Point btmHotSpot = new Point(0,31);
		
		//Create cursor map
		cursorMap.put(FILL,tk.createCustomCursor(imgs[3],btmHotSpot,"Flood Fill"));
		cursorMap.put(SPRAY,tk.createCustomCursor(imgs[4],hotspot,SPRAY));
		cursorMap.put(RECTANGLE,tk.createCustomCursor(imgs[5], hotspot, RECTANGLE));
		cursorMap.put(LINE,new Cursor(Cursor.CROSSHAIR_CURSOR));
		cursorMap.put(PENCIL,tk.createCustomCursor(imgs[7],btmHotSpot,"Pencil"));
		cursorMap.put(ERASE,tk.createCustomCursor(imgs[8],btmHotSpot,"Erase"));
		cursorMap.put(CUT,tk.createCustomCursor(imgs[9],hotspot,"Cut"));
		cursorMap.put(EVENTS,tk.createCustomCursor(imgs[10],hotspot,"Events"));
		map.setCursor(cursorMap.get(PENCIL));
		/*create other tools, mostly drawing, 1 is eventing tool
		 */
		for(int i  =3;i<11;i++){
			JToggleButton b = createButton(imgs[i],texts[i*2],texts[i*2+1]);
			drawTools.add(b);
			add(b);
		}
		Enumeration<AbstractButton> enumer = drawTools.getElements();
		for(int i = 0;i<4;i++)
			enumer.nextElement();
		enumer.nextElement().setSelected(true);
		addSeparator();
	}
	public String getDrawTool(){
		return drawTool;
	}
	public JToggleButton createButton(BufferedImage img,String actCmd,String tip){
		JToggleButton b = new JToggleButton(new ImageIcon(img));
		b.setActionCommand(actCmd);
		b.setToolTipText(tip);
		b.addActionListener(this);
		return b;
	}
	public void actionPerformed(ActionEvent e){
		String s = e.getActionCommand();
		if(drawTools.contains(s)){
			//set draw tool for other class references
			drawTool=s;
			//set cursor
			map.setCursor(cursorMap.get(s));
			if(s.equals(CUT)){	
				map.renderMap();
				map.cutRect=new Rectangle();
			}
		}
		else{
			if(s.equals("Layer 1")){
				map.currentLayer=map.lay1;
				layerIndex=1;
			}
			if(s.equals("Layer 2")){
				map.currentLayer=map.lay2;
				layerIndex=2;
			}
			if(s.equals("Layer 3")){
				map.currentLayer=map.lay3;
				layerIndex=3;
			}
			map.statusLbl.setLayer(s);
			map.repaint();
		}
	}
}
